package io.netty.channel.nio;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.AbstractChannel;
import io.netty.channel.Channel;
import io.netty.channel.ChannelException;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelPromise;
import io.netty.channel.ConnectTimeoutException;
import io.netty.channel.EventLoop;
import io.netty.channel.IoEvent;
import io.netty.channel.IoEventLoop;
import io.netty.channel.IoEventLoopGroup;
import io.netty.channel.IoRegistration;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ReferenceCounted;
import io.netty.util.concurrent.Future;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.SocketAddress;
import java.nio.channels.CancelledKeyException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.ConnectionPendingException;
import java.nio.channels.SelectableChannel;
import java.nio.channels.SelectionKey;
import java.util.concurrent.TimeUnit;

public abstract class AbstractNioChannel extends AbstractChannel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractNioChannel.class);
   private final SelectableChannel ch;
   protected final int readInterestOp;
   protected final NioIoOps readOps;
   volatile IoRegistration registration;
   boolean readPending;
   private final Runnable clearReadPendingRunnable = new Runnable() {
      @Override
      public void run() {
         AbstractNioChannel.this.clearReadPending0();
      }
   };
   private ChannelPromise connectPromise;
   private Future<?> connectTimeoutFuture;
   private SocketAddress requestedRemoteAddress;

   protected AbstractNioChannel(Channel parent, SelectableChannel ch, int readOps) {
      this(parent, ch, NioIoOps.valueOf(readOps));
   }

   protected AbstractNioChannel(Channel parent, SelectableChannel ch, NioIoOps readOps) {
      super(parent);
      this.ch = ch;
      this.readInterestOp = ObjectUtil.checkNotNull(readOps, "readOps").value;
      this.readOps = readOps;

      try {
         ch.configureBlocking(false);
      } catch (IOException var7) {
         try {
            ch.close();
         } catch (IOException var6) {
            logger.warn("Failed to close a partially initialized socket.", (Throwable)var6);
         }

         throw new ChannelException("Failed to enter non-blocking mode.", var7);
      }
   }

   protected void addAndSubmit(NioIoOps addOps) {
      int interestOps = this.selectionKey().interestOps();
      if (!addOps.isIncludedIn(interestOps)) {
         try {
            this.registration().submit(NioIoOps.valueOf(interestOps).with(addOps));
         } catch (Exception var4) {
            throw new ChannelException(var4);
         }
      }
   }

   protected void removeAndSubmit(NioIoOps removeOps) {
      int interestOps = this.selectionKey().interestOps();
      if (removeOps.isIncludedIn(interestOps)) {
         try {
            this.registration().submit(NioIoOps.valueOf(interestOps).without(removeOps));
         } catch (Exception var4) {
            throw new ChannelException(var4);
         }
      }
   }

   @Override
   public boolean isOpen() {
      return this.ch.isOpen();
   }

   public AbstractNioChannel.NioUnsafe unsafe() {
      return (AbstractNioChannel.NioUnsafe)super.unsafe();
   }

   protected SelectableChannel javaChannel() {
      return this.ch;
   }

   @Deprecated
   protected SelectionKey selectionKey() {
      return this.registration().attachment();
   }

   protected IoRegistration registration() {
      assert this.registration != null;

      return this.registration;
   }

   @Deprecated
   protected boolean isReadPending() {
      return this.readPending;
   }

   @Deprecated
   protected void setReadPending(final boolean readPending) {
      if (this.isRegistered()) {
         EventLoop eventLoop = this.eventLoop();
         if (eventLoop.inEventLoop()) {
            this.setReadPending0(readPending);
         } else {
            eventLoop.execute(new Runnable() {
               @Override
               public void run() {
                  AbstractNioChannel.this.setReadPending0(readPending);
               }
            });
         }
      } else {
         this.readPending = readPending;
      }
   }

   protected final void clearReadPending() {
      if (this.isRegistered()) {
         EventLoop eventLoop = this.eventLoop();
         if (eventLoop.inEventLoop()) {
            this.clearReadPending0();
         } else {
            eventLoop.execute(this.clearReadPendingRunnable);
         }
      } else {
         this.readPending = false;
      }
   }

   private void setReadPending0(boolean readPending) {
      this.readPending = readPending;
      if (!readPending) {
         ((AbstractNioChannel.AbstractNioUnsafe)this.unsafe()).removeReadOp();
      }
   }

   private void clearReadPending0() {
      this.readPending = false;
      ((AbstractNioChannel.AbstractNioUnsafe)this.unsafe()).removeReadOp();
   }

   @Override
   protected boolean isCompatible(EventLoop loop) {
      return loop instanceof IoEventLoop && ((IoEventLoopGroup)loop).isCompatible(AbstractNioChannel.AbstractNioUnsafe.class);
   }

   @Override
   protected void doRegister(ChannelPromise promise) {
      assert this.registration == null;

      ((IoEventLoop)this.eventLoop()).register((AbstractNioChannel.AbstractNioUnsafe)this.unsafe()).addListener(f -> {
         if (f.isSuccess()) {
            this.registration = f.getNow();
            promise.setSuccess();
         } else {
            promise.setFailure(f.cause());
         }
      });
   }

   @Override
   protected void doDeregister() throws Exception {
      IoRegistration registration = this.registration;
      if (registration != null) {
         this.registration = null;
         registration.cancel();
      }
   }

   @Override
   protected void doBeginRead() throws Exception {
      IoRegistration registration = this.registration;
      if (registration != null && registration.isValid()) {
         this.readPending = true;
         this.addAndSubmit(this.readOps);
      }
   }

   protected abstract boolean doConnect(SocketAddress var1, SocketAddress var2) throws Exception;

   protected abstract void doFinishConnect() throws Exception;

   protected final ByteBuf newDirectBuffer(ByteBuf buf) {
      int readableBytes = buf.readableBytes();
      if (readableBytes == 0) {
         ReferenceCountUtil.safeRelease(buf);
         return Unpooled.EMPTY_BUFFER;
      } else {
         ByteBufAllocator alloc = this.alloc();
         if (alloc.isDirectBufferPooled()) {
            ByteBuf directBuf = alloc.directBuffer(readableBytes);
            directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
            ReferenceCountUtil.safeRelease(buf);
            return directBuf;
         } else {
            ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
            if (directBuf != null) {
               directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
               ReferenceCountUtil.safeRelease(buf);
               return directBuf;
            } else {
               return buf;
            }
         }
      }
   }

   protected final ByteBuf newDirectBuffer(ReferenceCounted holder, ByteBuf buf) {
      int readableBytes = buf.readableBytes();
      if (readableBytes == 0) {
         ReferenceCountUtil.safeRelease(holder);
         return Unpooled.EMPTY_BUFFER;
      } else {
         ByteBufAllocator alloc = this.alloc();
         if (alloc.isDirectBufferPooled()) {
            ByteBuf directBuf = alloc.directBuffer(readableBytes);
            directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
            ReferenceCountUtil.safeRelease(holder);
            return directBuf;
         } else {
            ByteBuf directBuf = ByteBufUtil.threadLocalDirectBuffer();
            if (directBuf != null) {
               directBuf.writeBytes(buf, buf.readerIndex(), readableBytes);
               ReferenceCountUtil.safeRelease(holder);
               return directBuf;
            } else {
               if (holder != buf) {
                  buf.retain();
                  ReferenceCountUtil.safeRelease(holder);
               }

               return buf;
            }
         }
      }
   }

   @Override
   protected void doClose() throws Exception {
      ChannelPromise promise = this.connectPromise;
      if (promise != null) {
         promise.tryFailure(new ClosedChannelException());
         this.connectPromise = null;
      }

      Future<?> future = this.connectTimeoutFuture;
      if (future != null) {
         future.cancel(false);
         this.connectTimeoutFuture = null;
      }
   }

   protected abstract class AbstractNioUnsafe extends AbstractChannel.AbstractUnsafe implements AbstractNioChannel.NioUnsafe, NioIoHandle {
      @Override
      public void close() {
         this.close(this.voidPromise());
      }

      @Override
      public SelectableChannel selectableChannel() {
         return this.ch();
      }

      Channel channel() {
         return AbstractNioChannel.this;
      }

      protected final void removeReadOp() {
         IoRegistration registration = AbstractNioChannel.this.registration();
         if (registration.isValid()) {
            AbstractNioChannel.this.removeAndSubmit(AbstractNioChannel.this.readOps);
         }
      }

      @Override
      public final SelectableChannel ch() {
         return AbstractNioChannel.this.javaChannel();
      }

      @Override
      public final void connect(final SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
         if (!promise.isDone() && this.ensureOpen(promise)) {
            try {
               if (AbstractNioChannel.this.connectPromise != null) {
                  throw new ConnectionPendingException();
               }

               boolean wasActive = AbstractNioChannel.this.isActive();
               if (AbstractNioChannel.this.doConnect(remoteAddress, localAddress)) {
                  this.fulfillConnectPromise(promise, wasActive);
               } else {
                  AbstractNioChannel.this.connectPromise = promise;
                  AbstractNioChannel.this.requestedRemoteAddress = remoteAddress;
                  final int connectTimeoutMillis = AbstractNioChannel.this.config().getConnectTimeoutMillis();
                  if (connectTimeoutMillis > 0) {
                     AbstractNioChannel.this.connectTimeoutFuture = AbstractNioChannel.this.eventLoop()
                        .schedule(
                           new Runnable() {
                              @Override
                              public void run() {
                                 ChannelPromise connectPromise = AbstractNioChannel.this.connectPromise;
                                 if (connectPromise != null
                                    && !connectPromise.isDone()
                                    && connectPromise.tryFailure(
                                       new ConnectTimeoutException("connection timed out after " + connectTimeoutMillis + " ms: " + remoteAddress)
                                    )) {
                                    AbstractNioUnsafe.this.close(AbstractNioUnsafe.this.voidPromise());
                                 }
                              }
                           },
                           connectTimeoutMillis,
                           TimeUnit.MILLISECONDS
                        );
                  }

                  promise.addListener(new ChannelFutureListener() {
                     public void operationComplete(ChannelFuture future) {
                        if (future.isCancelled()) {
                           if (AbstractNioChannel.this.connectTimeoutFuture != null) {
                              AbstractNioChannel.this.connectTimeoutFuture.cancel(false);
                           }

                           AbstractNioChannel.this.connectPromise = null;
                           AbstractNioUnsafe.this.close(AbstractNioUnsafe.this.voidPromise());
                        }
                     }
                  });
               }
            } catch (Throwable var6) {
               promise.tryFailure(this.annotateConnectException(var6, remoteAddress));
               this.closeIfClosed();
            }
         }
      }

      private void fulfillConnectPromise(ChannelPromise promise, boolean wasActive) {
         if (promise != null) {
            boolean active = AbstractNioChannel.this.isActive();
            boolean promiseSet = promise.trySuccess();
            if (!wasActive && active) {
               AbstractNioChannel.this.pipeline().fireChannelActive();
            }

            if (!promiseSet) {
               this.close(this.voidPromise());
            }
         }
      }

      private void fulfillConnectPromise(ChannelPromise promise, Throwable cause) {
         if (promise != null) {
            promise.tryFailure(cause);
            this.closeIfClosed();
         }
      }

      @Override
      public final void finishConnect() {
         assert AbstractNioChannel.this.eventLoop().inEventLoop();

         try {
            boolean wasActive = AbstractNioChannel.this.isActive();
            AbstractNioChannel.this.doFinishConnect();
            this.fulfillConnectPromise(AbstractNioChannel.this.connectPromise, wasActive);
         } catch (Throwable var5) {
            this.fulfillConnectPromise(
               AbstractNioChannel.this.connectPromise, this.annotateConnectException(var5, AbstractNioChannel.this.requestedRemoteAddress)
            );
         } finally {
            if (AbstractNioChannel.this.connectTimeoutFuture != null) {
               AbstractNioChannel.this.connectTimeoutFuture.cancel(false);
            }

            AbstractNioChannel.this.connectPromise = null;
         }
      }

      @Override
      protected final void flush0() {
         if (!this.isFlushPending()) {
            super.flush0();
         }
      }

      @Override
      public final void forceFlush() {
         super.flush0();
      }

      private boolean isFlushPending() {
         IoRegistration registration = AbstractNioChannel.this.registration();
         return registration.isValid() && NioIoOps.WRITE.isIncludedIn(registration.<SelectionKey>attachment().interestOps());
      }

      @Override
      public void handle(IoRegistration registration, IoEvent event) {
         try {
            NioIoEvent nioEvent = (NioIoEvent)event;
            NioIoOps nioReadyOps = nioEvent.ops();
            if (nioReadyOps.contains(NioIoOps.CONNECT)) {
               AbstractNioChannel.this.removeAndSubmit(NioIoOps.CONNECT);
               AbstractNioChannel.this.unsafe().finishConnect();
            }

            if (nioReadyOps.contains(NioIoOps.WRITE)) {
               this.forceFlush();
            }

            if (nioReadyOps.contains(NioIoOps.READ_AND_ACCEPT) || nioReadyOps.equals(NioIoOps.NONE)) {
               this.read();
            }
         } catch (CancelledKeyException var5) {
            this.close(this.voidPromise());
         }
      }
   }

   public interface NioUnsafe extends Channel.Unsafe {
      SelectableChannel ch();

      void finishConnect();

      void read();

      void forceFlush();
   }
}
