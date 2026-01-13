package io.netty.channel.local;

import io.netty.buffer.ByteBuf;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelConfig;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelConfig;
import io.netty.channel.EventLoop;
import io.netty.channel.IoEvent;
import io.netty.channel.IoEventLoop;
import io.netty.channel.IoRegistration;
import io.netty.channel.PreferHeapByteBufAllocator;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.SingleThreadEventLoop;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.SingleThreadEventExecutor;
import io.netty.util.internal.InternalThreadLocalMap;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.ConnectException;
import java.net.SocketAddress;
import java.nio.channels.AlreadyConnectedException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.NotYetConnectedException;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class LocalChannel extends AbstractChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(LocalChannel.class);
   private static final AtomicReferenceFieldUpdater<LocalChannel, Future> FINISH_READ_FUTURE_UPDATER = AtomicReferenceFieldUpdater.newUpdater(
      LocalChannel.class, Future.class, "finishReadFuture"
   );
   private static final ChannelMetadata METADATA = new ChannelMetadata(false);
   private static final int MAX_READER_STACK_DEPTH = 8;
   private final ChannelConfig config = new DefaultChannelConfig(this);
   final Queue<Object> inboundBuffer = PlatformDependent.newSpscQueue();
   private final Runnable readTask = new Runnable() {
      @Override
      public void run() {
         if (!LocalChannel.this.inboundBuffer.isEmpty()) {
            LocalChannel.this.readInbound();
         }
      }
   };
   private final Runnable shutdownHook = new Runnable() {
      @Override
      public void run() {
         LocalChannel.this.unsafe().close(LocalChannel.this.unsafe().voidPromise());
      }
   };
   private final Runnable finishReadTask = new Runnable() {
      @Override
      public void run() {
         LocalChannel.this.finishPeerRead0(LocalChannel.this);
      }
   };
   private IoRegistration registration;
   private volatile LocalChannel.State state;
   private volatile LocalChannel peer;
   private volatile LocalAddress localAddress;
   private volatile LocalAddress remoteAddress;
   private volatile ChannelPromise connectPromise;
   private volatile boolean readInProgress;
   private volatile boolean writeInProgress;
   private volatile Future<?> finishReadFuture;

   public LocalChannel() {
      super(null);
      this.config().setAllocator(new PreferHeapByteBufAllocator(this.config.getAllocator()));
   }

   protected LocalChannel(LocalServerChannel parent, LocalChannel peer) {
      super(parent);
      this.config().setAllocator(new PreferHeapByteBufAllocator(this.config.getAllocator()));
      this.peer = peer;
      this.localAddress = parent.localAddress();
      this.remoteAddress = peer.localAddress();
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   @Override
   public ChannelConfig config() {
      return this.config;
   }

   public LocalServerChannel parent() {
      return (LocalServerChannel)super.parent();
   }

   public LocalAddress localAddress() {
      return (LocalAddress)super.localAddress();
   }

   public LocalAddress remoteAddress() {
      return (LocalAddress)super.remoteAddress();
   }

   @Override
   public boolean isOpen() {
      return this.state != LocalChannel.State.CLOSED;
   }

   @Override
   public boolean isActive() {
      return this.state == LocalChannel.State.CONNECTED;
   }

   @Override
   protected AbstractChannel.AbstractUnsafe newUnsafe() {
      return new LocalChannel.LocalUnsafe();
   }

   @Override
   protected boolean isCompatible(EventLoop loop) {
      return loop instanceof SingleThreadEventLoop || loop instanceof IoEventLoop && ((IoEventLoop)loop).isCompatible(LocalChannel.LocalUnsafe.class);
   }

   @Override
   protected SocketAddress localAddress0() {
      return this.localAddress;
   }

   @Override
   protected SocketAddress remoteAddress0() {
      return this.remoteAddress;
   }

   @Override
   protected void doRegister(ChannelPromise promise) {
      EventLoop loop = this.eventLoop();
      if (loop instanceof IoEventLoop) {
         assert this.registration == null;

         ((IoEventLoop)loop).register((LocalChannel.LocalUnsafe)this.unsafe()).addListener(f -> {
            if (f.isSuccess()) {
               this.registration = f.getNow();
               promise.setSuccess();
            } else {
               promise.setFailure(f.cause());
            }
         });
      } else {
         try {
            ((LocalChannel.LocalUnsafe)this.unsafe()).registered();
         } catch (Throwable var4) {
            promise.setFailure(var4);
         }

         promise.setSuccess();
      }
   }

   @Override
   protected void doDeregister() throws Exception {
      EventLoop loop = this.eventLoop();
      if (loop instanceof IoEventLoop) {
         IoRegistration registration = this.registration;
         if (registration != null) {
            this.registration = null;
            registration.cancel();
         }
      } else {
         ((LocalChannel.LocalUnsafe)this.unsafe()).unregistered();
      }
   }

   @Override
   protected void doBind(SocketAddress localAddress) throws Exception {
      this.localAddress = LocalChannelRegistry.register(this, this.localAddress, localAddress);
      this.state = LocalChannel.State.BOUND;
   }

   @Override
   protected void doDisconnect() throws Exception {
      this.doClose();
   }

   @Override
   protected void doClose() throws Exception {
      final LocalChannel peer = this.peer;
      LocalChannel.State oldState = this.state;

      try {
         if (oldState != LocalChannel.State.CLOSED) {
            if (this.localAddress != null) {
               if (this.parent() == null) {
                  LocalChannelRegistry.unregister(this.localAddress);
               }

               this.localAddress = null;
            }

            this.state = LocalChannel.State.CLOSED;
            if (this.writeInProgress && peer != null) {
               this.finishPeerRead(peer);
            }

            ChannelPromise promise = this.connectPromise;
            if (promise != null) {
               promise.tryFailure(new ClosedChannelException());
               this.connectPromise = null;
            }
         }

         if (peer != null) {
            this.peer = null;
            EventLoop peerEventLoop = peer.eventLoop();
            final boolean peerIsActive = peer.isActive();

            try {
               peerEventLoop.execute(new Runnable() {
                  @Override
                  public void run() {
                     peer.tryClose(peerIsActive);
                  }
               });
            } catch (Throwable var9) {
               logger.warn("Releasing Inbound Queues for channels {}-{} because exception occurred!", this, peer, var9);
               if (peerEventLoop.inEventLoop()) {
                  peer.releaseInboundBuffers();
               } else {
                  peer.close();
               }

               PlatformDependent.throwException(var9);
            }
         }
      } finally {
         if (oldState != null && oldState != LocalChannel.State.CLOSED) {
            this.releaseInboundBuffers();
         }
      }
   }

   private void tryClose(boolean isActive) {
      if (isActive) {
         this.unsafe().close(this.unsafe().voidPromise());
      } else {
         this.releaseInboundBuffers();
      }
   }

   private void readInbound() {
      RecvByteBufAllocator.Handle handle = this.unsafe().recvBufAllocHandle();
      handle.reset(this.config());
      ChannelPipeline pipeline = this.pipeline();

      do {
         Object received = this.inboundBuffer.poll();
         if (received == null) {
            break;
         }

         if (received instanceof ByteBuf && this.inboundBuffer.peek() instanceof ByteBuf) {
            ByteBuf msg = (ByteBuf)received;
            ByteBuf output = handle.allocate(this.alloc());
            if (msg.readableBytes() < output.writableBytes()) {
               output.writeBytes(msg, msg.readerIndex(), msg.readableBytes());
               msg.release();

               while ((received = this.inboundBuffer.peek()) instanceof ByteBuf && ((ByteBuf)received).readableBytes() < output.writableBytes()) {
                  this.inboundBuffer.poll();
                  msg = (ByteBuf)received;
                  output.writeBytes(msg, msg.readerIndex(), msg.readableBytes());
                  msg.release();
               }

               handle.lastBytesRead(output.readableBytes());
               received = output;
            } else {
               handle.lastBytesRead(output.capacity());
               output.release();
            }
         }

         handle.incMessagesRead(1);
         pipeline.fireChannelRead(received);
      } while (handle.continueReading());

      handle.readComplete();
      pipeline.fireChannelReadComplete();
   }

   @Override
   protected void doBeginRead() throws Exception {
      if (!this.readInProgress) {
         Queue<Object> inboundBuffer = this.inboundBuffer;
         if (inboundBuffer.isEmpty()) {
            this.readInProgress = true;
         } else {
            InternalThreadLocalMap threadLocals = InternalThreadLocalMap.get();
            int stackDepth = threadLocals.localChannelReaderStackDepth();
            if (stackDepth < 8) {
               threadLocals.setLocalChannelReaderStackDepth(stackDepth + 1);

               try {
                  this.readInbound();
               } finally {
                  threadLocals.setLocalChannelReaderStackDepth(stackDepth);
               }
            } else {
               try {
                  this.eventLoop().execute(this.readTask);
               } catch (Throwable var7) {
                  logger.warn("Closing Local channels {}-{} because exception occurred!", this, this.peer, var7);
                  this.close();
                  this.peer.close();
                  PlatformDependent.throwException(var7);
               }
            }
         }
      }
   }

   @Override
   protected void doWrite(ChannelOutboundBuffer in) throws Exception {
      switch (this.state) {
         case OPEN:
         case BOUND:
            throw new NotYetConnectedException();
         case CLOSED:
            throw new ClosedChannelException();
         case CONNECTED:
         default:
            LocalChannel peer = this.peer;
            this.writeInProgress = true;

            try {
               ClosedChannelException exception = null;

               while (true) {
                  Object msg = in.current();
                  if (msg == null) {
                     break;
                  }

                  try {
                     if (peer.state == LocalChannel.State.CONNECTED) {
                        peer.inboundBuffer.add(ReferenceCountUtil.retain(msg));
                        in.remove();
                     } else {
                        if (exception == null) {
                           exception = new ClosedChannelException();
                        }

                        in.remove(exception);
                     }
                  } catch (Throwable var9) {
                     in.remove(var9);
                  }
               }
            } finally {
               this.writeInProgress = false;
            }

            this.finishPeerRead(peer);
      }
   }

   private void finishPeerRead(LocalChannel peer) {
      if (peer.eventLoop() == this.eventLoop() && !peer.writeInProgress) {
         this.finishPeerRead0(peer);
      } else {
         this.runFinishPeerReadTask(peer);
      }
   }

   private void runFinishTask0() {
      if (this.writeInProgress) {
         this.finishReadFuture = this.eventLoop().submit(this.finishReadTask);
      } else {
         this.eventLoop().execute(this.finishReadTask);
      }
   }

   private void runFinishPeerReadTask(LocalChannel peer) {
      try {
         peer.runFinishTask0();
      } catch (Throwable var3) {
         logger.warn("Closing Local channels {}-{} because exception occurred!", this, peer, var3);
         this.close();
         peer.close();
         PlatformDependent.throwException(var3);
      }
   }

   private void releaseInboundBuffers() {
      assert this.eventLoop() == null || this.eventLoop().inEventLoop();

      this.readInProgress = false;
      Queue<Object> inboundBuffer = this.inboundBuffer;

      Object msg;
      while ((msg = inboundBuffer.poll()) != null) {
         ReferenceCountUtil.release(msg);
      }
   }

   private void finishPeerRead0(LocalChannel peer) {
      Future<?> peerFinishReadFuture = peer.finishReadFuture;
      if (peerFinishReadFuture != null) {
         if (!peerFinishReadFuture.isDone()) {
            this.runFinishPeerReadTask(peer);
            return;
         }

         FINISH_READ_FUTURE_UPDATER.compareAndSet(peer, peerFinishReadFuture, null);
      }

      if (peer.readInProgress && !peer.inboundBuffer.isEmpty()) {
         peer.readInProgress = false;
         peer.readInbound();
      }
   }

   private class LocalUnsafe extends AbstractChannel.AbstractUnsafe implements LocalIoHandle {
      private LocalUnsafe() {
      }

      @Override
      public void close() {
         this.close(this.voidPromise());
      }

      @Override
      public void handle(IoRegistration registration, IoEvent event) {
      }

      @Override
      public void registered() {
         if (LocalChannel.this.peer != null && LocalChannel.this.parent() != null) {
            final LocalChannel peer = LocalChannel.this.peer;
            LocalChannel.this.state = LocalChannel.State.CONNECTED;
            peer.remoteAddress = LocalChannel.this.parent() == null ? null : LocalChannel.this.parent().localAddress();
            peer.state = LocalChannel.State.CONNECTED;
            peer.eventLoop().execute(new Runnable() {
               @Override
               public void run() {
                  ChannelPromise promise = peer.connectPromise;
                  if (promise != null && promise.trySuccess()) {
                     peer.pipeline().fireChannelActive();
                  }
               }
            });
         }

         ((SingleThreadEventExecutor)LocalChannel.this.eventLoop()).addShutdownHook(LocalChannel.this.shutdownHook);
      }

      @Override
      public void unregistered() {
         ((SingleThreadEventExecutor)LocalChannel.this.eventLoop()).removeShutdownHook(LocalChannel.this.shutdownHook);
      }

      @Override
      public void closeNow() {
         this.close(this.voidPromise());
      }

      @Override
      public void connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
         if (promise.setUncancellable() && this.ensureOpen(promise)) {
            if (LocalChannel.this.state == LocalChannel.State.CONNECTED) {
               Exception cause = new AlreadyConnectedException();
               this.safeSetFailure(promise, cause);
            } else if (LocalChannel.this.connectPromise != null) {
               throw new ConnectionPendingException();
            } else {
               LocalChannel.this.connectPromise = promise;
               if (LocalChannel.this.state != LocalChannel.State.BOUND && localAddress == null) {
                  localAddress = new LocalAddress(LocalChannel.this);
               }

               if (localAddress != null) {
                  try {
                     LocalChannel.this.doBind(localAddress);
                  } catch (Throwable var6) {
                     this.safeSetFailure(promise, var6);
                     this.close(this.voidPromise());
                     return;
                  }
               }

               Channel boundChannel = LocalChannelRegistry.get(remoteAddress);
               if (!(boundChannel instanceof LocalServerChannel)) {
                  Exception cause = new ConnectException("connection refused: " + remoteAddress);
                  this.safeSetFailure(promise, cause);
                  this.close(this.voidPromise());
               } else {
                  LocalServerChannel serverChannel = (LocalServerChannel)boundChannel;
                  LocalChannel.this.peer = serverChannel.serve(LocalChannel.this);
               }
            }
         }
      }
   }

   private static enum State {
      OPEN,
      BOUND,
      CONNECTED,
      CLOSED;
   }
}
