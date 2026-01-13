package io.netty.handler.codec.quic;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelId;
import io.netty.channel.ChannelMetadata;
import io.netty.channel.ChannelOutboundBuffer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.ChannelPromise;
import io.netty.channel.DefaultChannelId;
import io.netty.channel.DefaultChannelPipeline;
import io.netty.channel.EventLoop;
import io.netty.channel.PendingWriteQueue;
import io.netty.channel.RecvByteBufAllocator;
import io.netty.channel.VoidChannelPromise;
import io.netty.channel.socket.ChannelInputShutdownEvent;
import io.netty.channel.socket.ChannelInputShutdownReadComplete;
import io.netty.channel.socket.ChannelOutputShutdownException;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.concurrent.PromiseNotifier;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.nio.channels.ClosedChannelException;
import java.util.concurrent.RejectedExecutionException;
import org.jetbrains.annotations.Nullable;

final class QuicheQuicStreamChannel extends DefaultAttributeMap implements QuicStreamChannel {
   private static final ChannelMetadata METADATA = new ChannelMetadata(false, 16);
   private static final InternalLogger LOGGER = InternalLoggerFactory.getInstance(QuicheQuicStreamChannel.class);
   private final QuicheQuicChannel parent;
   private final ChannelId id;
   private final ChannelPipeline pipeline;
   private final QuicheQuicStreamChannel.QuicStreamChannelUnsafe unsafe;
   private final ChannelPromise closePromise;
   private final PendingWriteQueue queue;
   private final QuicStreamChannelConfig config;
   private final QuicStreamAddress address;
   private boolean readable;
   private boolean readPending;
   private boolean inRecv;
   private boolean inWriteQueued;
   private boolean finReceived;
   private boolean finSent;
   private volatile boolean registered;
   private volatile boolean writable = true;
   private volatile boolean active = true;
   private volatile boolean inputShutdown;
   private volatile boolean outputShutdown;
   private volatile QuicStreamPriority priority;
   private volatile long capacity;

   QuicheQuicStreamChannel(QuicheQuicChannel parent, long streamId) {
      this.parent = parent;
      this.id = DefaultChannelId.newInstance();
      this.unsafe = new QuicheQuicStreamChannel.QuicStreamChannelUnsafe();
      this.pipeline = new DefaultChannelPipeline(this) {};
      this.config = new QuicheQuicStreamChannelConfig(this);
      this.address = new QuicStreamAddress(streamId);
      this.closePromise = this.newPromise();
      this.queue = new PendingWriteQueue(this);
      if (parent.streamType(streamId) == QuicStreamType.UNIDIRECTIONAL && parent.isStreamLocalCreated(streamId)) {
         this.inputShutdown = true;
      }
   }

   @Override
   public QuicStreamAddress localAddress() {
      return this.address;
   }

   @Override
   public QuicStreamAddress remoteAddress() {
      return this.address;
   }

   @Override
   public boolean isLocalCreated() {
      return this.parent().isStreamLocalCreated(this.streamId());
   }

   @Override
   public QuicStreamType type() {
      return this.parent().streamType(this.streamId());
   }

   @Override
   public long streamId() {
      return this.address.streamId();
   }

   @Override
   public QuicStreamPriority priority() {
      return this.priority;
   }

   @Override
   public ChannelFuture updatePriority(QuicStreamPriority priority, ChannelPromise promise) {
      if (this.eventLoop().inEventLoop()) {
         this.updatePriority0(priority, promise);
      } else {
         this.eventLoop().execute(() -> this.updatePriority0(priority, promise));
      }

      return promise;
   }

   private void updatePriority0(QuicStreamPriority priority, ChannelPromise promise) {
      assert this.eventLoop().inEventLoop();

      if (promise.setUncancellable()) {
         try {
            this.parent().streamPriority(this.streamId(), (byte)priority.urgency(), priority.isIncremental());
         } catch (Throwable var4) {
            promise.setFailure(var4);
            return;
         }

         this.priority = priority;
         promise.setSuccess();
      }
   }

   @Override
   public boolean isInputShutdown() {
      return this.inputShutdown;
   }

   @Override
   public ChannelFuture shutdownOutput(ChannelPromise promise) {
      if (this.eventLoop().inEventLoop()) {
         this.shutdownOutput0(promise);
      } else {
         this.eventLoop().execute(() -> this.shutdownOutput0(promise));
      }

      return promise;
   }

   private void shutdownOutput0(ChannelPromise promise) {
      assert this.eventLoop().inEventLoop();

      if (promise.setUncancellable()) {
         this.outputShutdown = true;
         this.unsafe.writeWithoutCheckChannelState(QuicStreamFrame.EMPTY_FIN, promise);
         this.unsafe.flush();
      }
   }

   @Override
   public ChannelFuture shutdownInput(int error, ChannelPromise promise) {
      if (this.eventLoop().inEventLoop()) {
         this.shutdownInput0(error, promise);
      } else {
         this.eventLoop().execute(() -> this.shutdownInput0(error, promise));
      }

      return promise;
   }

   @Override
   public ChannelFuture shutdownOutput(int error, ChannelPromise promise) {
      if (this.eventLoop().inEventLoop()) {
         this.shutdownOutput0(error, promise);
      } else {
         this.eventLoop().execute(() -> this.shutdownOutput0(error, promise));
      }

      return promise;
   }

   public QuicheQuicChannel parent() {
      return this.parent;
   }

   private void shutdownInput0(int err, ChannelPromise channelPromise) {
      assert this.eventLoop().inEventLoop();

      if (channelPromise.setUncancellable()) {
         this.inputShutdown = true;
         this.parent().streamShutdown(this.streamId(), true, false, err, channelPromise);
         this.closeIfDone();
      }
   }

   @Override
   public boolean isOutputShutdown() {
      return this.outputShutdown;
   }

   private void shutdownOutput0(int error, ChannelPromise channelPromise) {
      assert this.eventLoop().inEventLoop();

      if (channelPromise.setUncancellable()) {
         this.parent().streamShutdown(this.streamId(), false, true, error, channelPromise);
         this.outputShutdown = true;
         this.closeIfDone();
      }
   }

   @Override
   public boolean isShutdown() {
      return this.outputShutdown && this.inputShutdown;
   }

   @Override
   public ChannelFuture shutdown(ChannelPromise channelPromise) {
      if (this.eventLoop().inEventLoop()) {
         this.shutdown0(channelPromise);
      } else {
         this.eventLoop().execute(() -> this.shutdown0(channelPromise));
      }

      return channelPromise;
   }

   private void shutdown0(ChannelPromise promise) {
      assert this.eventLoop().inEventLoop();

      if (promise.setUncancellable()) {
         this.inputShutdown = true;
         this.outputShutdown = true;
         this.unsafe.writeWithoutCheckChannelState(QuicStreamFrame.EMPTY_FIN, this.unsafe.voidPromise());
         this.unsafe.flush();
         this.parent().streamShutdown(this.streamId(), true, false, 0, promise);
         this.closeIfDone();
      }
   }

   @Override
   public ChannelFuture shutdown(int error, ChannelPromise promise) {
      if (this.eventLoop().inEventLoop()) {
         this.shutdown0(error, promise);
      } else {
         this.eventLoop().execute(() -> this.shutdown0(error, promise));
      }

      return promise;
   }

   private void shutdown0(int error, ChannelPromise channelPromise) {
      assert this.eventLoop().inEventLoop();

      if (channelPromise.setUncancellable()) {
         this.inputShutdown = true;
         this.outputShutdown = true;
         this.parent().streamShutdown(this.streamId(), true, true, error, channelPromise);
         this.closeIfDone();
      }
   }

   private void sendFinIfNeeded() throws Exception {
      if (!this.finSent) {
         this.finSent = true;
         this.parent().streamSendFin(this.streamId());
      }
   }

   private void closeIfDone() {
      if (this.finSent && (this.finReceived || this.type() == QuicStreamType.UNIDIRECTIONAL && this.isLocalCreated())) {
         this.unsafe().close(this.unsafe().voidPromise());
      }
   }

   private void removeStreamFromParent() {
      if (!this.active && this.finReceived) {
         this.parent().streamClosed(this.streamId());
         this.inputShutdown = true;
         this.outputShutdown = true;
      }
   }

   @Override
   public QuicStreamChannel flush() {
      this.pipeline.flush();
      return this;
   }

   @Override
   public QuicStreamChannel read() {
      this.pipeline.read();
      return this;
   }

   @Override
   public QuicStreamChannelConfig config() {
      return this.config;
   }

   @Override
   public boolean isOpen() {
      return this.active;
   }

   @Override
   public boolean isActive() {
      return this.isOpen();
   }

   @Override
   public ChannelMetadata metadata() {
      return METADATA;
   }

   @Override
   public ChannelId id() {
      return this.id;
   }

   @Override
   public EventLoop eventLoop() {
      return this.parent.eventLoop();
   }

   @Override
   public boolean isRegistered() {
      return this.registered;
   }

   @Override
   public ChannelFuture closeFuture() {
      return this.closePromise;
   }

   @Override
   public boolean isWritable() {
      return this.writable;
   }

   @Override
   public long bytesBeforeUnwritable() {
      return Math.max(this.capacity, 0L);
   }

   @Override
   public long bytesBeforeWritable() {
      return this.writable ? 0L : 8L;
   }

   public QuicheQuicStreamChannel.QuicStreamChannelUnsafe unsafe() {
      return this.unsafe;
   }

   @Override
   public ChannelPipeline pipeline() {
      return this.pipeline;
   }

   @Override
   public ByteBufAllocator alloc() {
      return this.config.getAllocator();
   }

   public int compareTo(Channel o) {
      return this.id.compareTo(o.id());
   }

   @Override
   public int hashCode() {
      return this.id.hashCode();
   }

   @Override
   public boolean equals(Object o) {
      return this == o;
   }

   @Override
   public String toString() {
      return "[id: 0x" + this.id.asShortText() + ", " + this.address + "]";
   }

   boolean writable(long capacity) {
      assert this.eventLoop().inEventLoop();

      if (capacity < 0L) {
         if (capacity != Quiche.QUICHE_ERR_DONE) {
            if (!this.queue.isEmpty()) {
               if (capacity == Quiche.QUICHE_ERR_STREAM_STOPPED) {
                  this.queue.removeAndFailAll(new ChannelOutputShutdownException("STOP_SENDING frame received"));
                  return false;
               }

               this.queue.removeAndFailAll(Quiche.convertToException((int)capacity));
            } else if (capacity == Quiche.QUICHE_ERR_STREAM_STOPPED) {
               return false;
            }

            this.finSent = true;
            this.unsafe().close(this.unsafe().voidPromise());
         }

         return false;
      } else {
         this.capacity = capacity;
         boolean mayNeedWrite = this.unsafe().writeQueued();
         this.updateWritabilityIfNeeded(this.capacity > 0L);
         return mayNeedWrite;
      }
   }

   private void updateWritabilityIfNeeded(boolean newWritable) {
      if (this.writable != newWritable) {
         this.writable = newWritable;
         this.pipeline.fireChannelWritabilityChanged();
      }
   }

   void readable() {
      assert this.eventLoop().inEventLoop();

      this.readable = true;
      if (this.readPending) {
         this.unsafe().recv();
      }
   }

   final class QuicStreamChannelUnsafe implements Channel.Unsafe {
      private RecvByteBufAllocator.Handle recvHandle;
      private final ChannelPromise voidPromise = new VoidChannelPromise(QuicheQuicStreamChannel.this, false);

      @Override
      public void connect(SocketAddress remote, SocketAddress local, ChannelPromise promise) {
         assert QuicheQuicStreamChannel.this.eventLoop().inEventLoop();

         promise.setFailure(new UnsupportedOperationException());
      }

      @Override
      public RecvByteBufAllocator.Handle recvBufAllocHandle() {
         if (this.recvHandle == null) {
            this.recvHandle = QuicheQuicStreamChannel.this.config.<RecvByteBufAllocator>getRecvByteBufAllocator().newHandle();
         }

         return this.recvHandle;
      }

      @Override
      public SocketAddress localAddress() {
         return QuicheQuicStreamChannel.this.address;
      }

      @Override
      public SocketAddress remoteAddress() {
         return QuicheQuicStreamChannel.this.address;
      }

      @Override
      public void register(EventLoop eventLoop, ChannelPromise promise) {
         assert eventLoop.inEventLoop();

         if (promise.setUncancellable()) {
            if (QuicheQuicStreamChannel.this.registered) {
               promise.setFailure(new IllegalStateException());
            } else if (eventLoop != QuicheQuicStreamChannel.this.parent.eventLoop()) {
               promise.setFailure(new IllegalArgumentException());
            } else {
               QuicheQuicStreamChannel.this.registered = true;
               promise.setSuccess();
               QuicheQuicStreamChannel.this.pipeline.fireChannelRegistered();
               QuicheQuicStreamChannel.this.pipeline.fireChannelActive();
            }
         }
      }

      @Override
      public void bind(SocketAddress localAddress, ChannelPromise promise) {
         assert QuicheQuicStreamChannel.this.eventLoop().inEventLoop();

         if (promise.setUncancellable()) {
            promise.setFailure(new UnsupportedOperationException());
         }
      }

      @Override
      public void disconnect(ChannelPromise promise) {
         assert QuicheQuicStreamChannel.this.eventLoop().inEventLoop();

         this.close(promise);
      }

      @Override
      public void close(ChannelPromise promise) {
         this.close(null, promise);
      }

      void close(@Nullable ClosedChannelException writeFailCause, ChannelPromise promise) {
         assert QuicheQuicStreamChannel.this.eventLoop().inEventLoop();

         if (promise.setUncancellable()) {
            if (QuicheQuicStreamChannel.this.active && !QuicheQuicStreamChannel.this.closePromise.isDone()) {
               QuicheQuicStreamChannel.this.active = false;

               try {
                  QuicheQuicStreamChannel.this.sendFinIfNeeded();
               } catch (Exception var7) {
               } finally {
                  if (!QuicheQuicStreamChannel.this.queue.isEmpty()) {
                     if (writeFailCause == null) {
                        writeFailCause = new ClosedChannelException();
                     }

                     QuicheQuicStreamChannel.this.queue.removeAndFailAll(writeFailCause);
                  }

                  promise.trySuccess();
                  QuicheQuicStreamChannel.this.closePromise.trySuccess();
                  if (QuicheQuicStreamChannel.this.type() == QuicStreamType.UNIDIRECTIONAL && QuicheQuicStreamChannel.this.isLocalCreated()) {
                     QuicheQuicStreamChannel.this.inputShutdown = true;
                     QuicheQuicStreamChannel.this.outputShutdown = true;
                     QuicheQuicStreamChannel.this.parent().streamClosed(QuicheQuicStreamChannel.this.streamId());
                  } else {
                     QuicheQuicStreamChannel.this.removeStreamFromParent();
                  }
               }

               if (QuicheQuicStreamChannel.this.inWriteQueued) {
                  this.invokeLater(() -> this.deregister(this.voidPromise(), true));
               } else {
                  this.deregister(this.voidPromise(), true);
               }
            } else if (!promise.isVoid()) {
               QuicheQuicStreamChannel.this.closePromise.addListener(new PromiseNotifier<>(promise));
            }
         }
      }

      private void deregister(ChannelPromise promise, boolean fireChannelInactive) {
         assert QuicheQuicStreamChannel.this.eventLoop().inEventLoop();

         if (promise.setUncancellable()) {
            if (!QuicheQuicStreamChannel.this.registered) {
               promise.trySuccess();
            } else {
               this.invokeLater(() -> {
                  if (fireChannelInactive) {
                     QuicheQuicStreamChannel.this.pipeline.fireChannelInactive();
                  }

                  if (QuicheQuicStreamChannel.this.registered) {
                     QuicheQuicStreamChannel.this.registered = false;
                     QuicheQuicStreamChannel.this.pipeline.fireChannelUnregistered();
                  }

                  promise.setSuccess();
               });
            }
         }
      }

      private void invokeLater(Runnable task) {
         try {
            QuicheQuicStreamChannel.this.eventLoop().execute(task);
         } catch (RejectedExecutionException var3) {
            QuicheQuicStreamChannel.LOGGER.warn("Can't invoke task later as EventLoop rejected it", (Throwable)var3);
         }
      }

      @Override
      public void closeForcibly() {
         assert QuicheQuicStreamChannel.this.eventLoop().inEventLoop();

         this.close(QuicheQuicStreamChannel.this.unsafe().voidPromise());
      }

      @Override
      public void deregister(ChannelPromise promise) {
         assert QuicheQuicStreamChannel.this.eventLoop().inEventLoop();

         this.deregister(promise, false);
      }

      @Override
      public void beginRead() {
         assert QuicheQuicStreamChannel.this.eventLoop().inEventLoop();

         QuicheQuicStreamChannel.this.readPending = true;
         if (QuicheQuicStreamChannel.this.readable) {
            QuicheQuicStreamChannel.this.unsafe().recv();
            QuicheQuicStreamChannel.this.parent().connectionSendAndFlush();
         }
      }

      private void closeIfNeeded(boolean wasFinSent) {
         if (!wasFinSent
            && QuicheQuicStreamChannel.this.finSent
            && (QuicheQuicStreamChannel.this.type() == QuicStreamType.UNIDIRECTIONAL || QuicheQuicStreamChannel.this.finReceived)) {
            this.close(this.voidPromise());
         }
      }

      boolean writeQueued() {
         assert QuicheQuicStreamChannel.this.eventLoop().inEventLoop();

         boolean wasFinSent = QuicheQuicStreamChannel.this.finSent;
         QuicheQuicStreamChannel.this.inWriteQueued = true;

         boolean written;
         try {
            if (!QuicheQuicStreamChannel.this.queue.isEmpty()) {
               written = false;

               while (true) {
                  Object msg = QuicheQuicStreamChannel.this.queue.current();
                  if (msg == null) {
                     break;
                  }

                  try {
                     int res = this.write0(msg);
                     if (res == 1) {
                        QuicheQuicStreamChannel.this.queue.remove().setSuccess();
                        written = true;
                     } else {
                        if (res == 0 || res == Quiche.QUICHE_ERR_DONE) {
                           break;
                        }

                        if (res == Quiche.QUICHE_ERR_STREAM_STOPPED) {
                           QuicheQuicStreamChannel.this.queue.removeAndFailAll(new ChannelOutputShutdownException("STOP_SENDING frame received"));
                           break;
                        }

                        QuicheQuicStreamChannel.this.queue.remove().setFailure(Quiche.convertToException(res));
                     }
                  } catch (Exception var8) {
                     QuicheQuicStreamChannel.this.queue.remove().setFailure(var8);
                  }
               }

               if (written) {
                  QuicheQuicStreamChannel.this.updateWritabilityIfNeeded(true);
               }

               return written;
            }

            written = false;
         } finally {
            this.closeIfNeeded(wasFinSent);
            QuicheQuicStreamChannel.this.inWriteQueued = false;
         }

         return written;
      }

      @Override
      public void write(Object msg, ChannelPromise promise) {
         assert QuicheQuicStreamChannel.this.eventLoop().inEventLoop();

         if (!promise.setUncancellable()) {
            ReferenceCountUtil.release(msg);
         } else {
            if (!QuicheQuicStreamChannel.this.isOpen()) {
               this.queueAndFailAll(msg, promise, new ClosedChannelException());
            } else if (QuicheQuicStreamChannel.this.finSent) {
               this.queueAndFailAll(msg, promise, new ChannelOutputShutdownException("Fin was sent already"));
            } else if (!QuicheQuicStreamChannel.this.queue.isEmpty()) {
               try {
                  msg = this.filterMsg(msg);
               } catch (UnsupportedOperationException var4) {
                  ReferenceCountUtil.release(msg);
                  promise.setFailure(var4);
                  return;
               }

               ReferenceCountUtil.touch(msg);
               QuicheQuicStreamChannel.this.queue.add(msg, promise);
               this.writeQueued();
            } else {
               assert QuicheQuicStreamChannel.this.queue.isEmpty();

               this.writeWithoutCheckChannelState(msg, promise);
            }
         }
      }

      private void queueAndFailAll(Object msg, ChannelPromise promise, Throwable cause) {
         ReferenceCountUtil.touch(msg);
         QuicheQuicStreamChannel.this.queue.add(msg, promise);
         QuicheQuicStreamChannel.this.queue.removeAndFailAll(cause);
      }

      private Object filterMsg(Object msg) {
         if (msg instanceof ByteBuf) {
            ByteBuf buffer = (ByteBuf)msg;
            if (!buffer.isDirect()) {
               ByteBuf tmpBuffer = QuicheQuicStreamChannel.this.alloc().directBuffer(buffer.readableBytes());
               tmpBuffer.writeBytes(buffer, buffer.readerIndex(), buffer.readableBytes());
               buffer.release();
               return tmpBuffer;
            }
         } else {
            if (!(msg instanceof QuicStreamFrame)) {
               throw new UnsupportedOperationException("unsupported message type: " + StringUtil.simpleClassName(msg));
            }

            QuicStreamFrame frame = (QuicStreamFrame)msg;
            ByteBuf buffer = frame.content();
            if (!buffer.isDirect()) {
               ByteBuf tmpBuffer = QuicheQuicStreamChannel.this.alloc().directBuffer(buffer.readableBytes());
               tmpBuffer.writeBytes(buffer, buffer.readerIndex(), buffer.readableBytes());
               QuicStreamFrame tmpFrame = frame.replace(tmpBuffer);
               frame.release();
               return tmpFrame;
            }
         }

         return msg;
      }

      void writeWithoutCheckChannelState(Object msg, ChannelPromise promise) {
         try {
            msg = this.filterMsg(msg);
         } catch (UnsupportedOperationException var10) {
            ReferenceCountUtil.release(msg);
            promise.setFailure(var10);
         }

         boolean wasFinSent = QuicheQuicStreamChannel.this.finSent;
         boolean mayNeedWritabilityUpdate = false;

         try {
            int res = this.write0(msg);
            if (res > 0) {
               ReferenceCountUtil.release(msg);
               promise.setSuccess();
               mayNeedWritabilityUpdate = QuicheQuicStreamChannel.this.capacity == 0L;
            } else {
               if (res != 0 && res != Quiche.QUICHE_ERR_DONE) {
                  if (res == Quiche.QUICHE_ERR_STREAM_STOPPED) {
                     throw new ChannelOutputShutdownException("STOP_SENDING frame received");
                  }

                  throw Quiche.convertToException(res);
               }

               ReferenceCountUtil.touch(msg);
               QuicheQuicStreamChannel.this.queue.add(msg, promise);
               mayNeedWritabilityUpdate = true;
            }
         } catch (Exception var11) {
            ReferenceCountUtil.release(msg);
            promise.setFailure(var11);
            mayNeedWritabilityUpdate = QuicheQuicStreamChannel.this.capacity == 0L;
         } finally {
            if (mayNeedWritabilityUpdate) {
               QuicheQuicStreamChannel.this.updateWritabilityIfNeeded(false);
            }

            this.closeIfNeeded(wasFinSent);
         }
      }

      private int write0(Object msg) throws Exception {
         if (QuicheQuicStreamChannel.this.type() == QuicStreamType.UNIDIRECTIONAL && !QuicheQuicStreamChannel.this.isLocalCreated()) {
            throw new UnsupportedOperationException("Writes on non-local created streams that are unidirectional are not supported");
         } else if (QuicheQuicStreamChannel.this.finSent) {
            throw new ChannelOutputShutdownException("Fin was sent already");
         } else {
            boolean fin;
            ByteBuf buffer;
            if (msg instanceof ByteBuf) {
               fin = false;
               buffer = (ByteBuf)msg;
            } else {
               QuicStreamFrame frame = (QuicStreamFrame)msg;
               fin = frame.hasFin();
               buffer = frame.content();
            }

            boolean readable = buffer.isReadable();
            if (!fin && !readable) {
               return 1;
            } else {
               boolean sendSomething = false;

               try {
                  while (true) {
                     int res = QuicheQuicStreamChannel.this.parent().streamSend(QuicheQuicStreamChannel.this.streamId(), buffer, fin);
                     long cap = QuicheQuicStreamChannel.this.parent.streamCapacity(QuicheQuicStreamChannel.this.streamId());
                     if (cap >= 0L) {
                        QuicheQuicStreamChannel.this.capacity = cap;
                     }

                     if (res < 0) {
                        return res;
                     }

                     if (!readable || res != 0) {
                        sendSomething = true;
                        buffer.skipBytes(res);
                        if (!buffer.isReadable()) {
                           if (fin) {
                              QuicheQuicStreamChannel.this.finSent = true;
                              QuicheQuicStreamChannel.this.outputShutdown = true;
                           }

                           return 1;
                        }
                     } else {
                        return 0;
                     }
                  }
               } finally {
                  if (sendSomething) {
                     QuicheQuicStreamChannel.this.parent.connectionSendAndFlush();
                  }
               }
            }
         }
      }

      @Override
      public void flush() {
         assert QuicheQuicStreamChannel.this.eventLoop().inEventLoop();
      }

      @Override
      public ChannelPromise voidPromise() {
         assert QuicheQuicStreamChannel.this.eventLoop().inEventLoop();

         return this.voidPromise;
      }

      @Nullable
      @Override
      public ChannelOutboundBuffer outboundBuffer() {
         return null;
      }

      private void closeOnRead(ChannelPipeline pipeline, boolean readFrames) {
         if (readFrames && QuicheQuicStreamChannel.this.finReceived && QuicheQuicStreamChannel.this.finSent) {
            this.close(this.voidPromise());
         } else if (QuicheQuicStreamChannel.this.config.isAllowHalfClosure()) {
            if (QuicheQuicStreamChannel.this.finReceived) {
               pipeline.fireUserEventTriggered(ChannelInputShutdownEvent.INSTANCE);
               pipeline.fireUserEventTriggered(ChannelInputShutdownReadComplete.INSTANCE);
               if (QuicheQuicStreamChannel.this.finSent) {
                  this.close(this.voidPromise());
               }
            }
         } else {
            this.close(this.voidPromise());
         }
      }

      private void handleReadException(
         ChannelPipeline pipeline, @Nullable ByteBuf byteBuf, Throwable cause, RecvByteBufAllocator.Handle allocHandle, boolean readFrames
      ) {
         if (byteBuf != null) {
            if (byteBuf.isReadable()) {
               pipeline.fireChannelRead(byteBuf);
            } else {
               byteBuf.release();
            }
         }

         this.readComplete(allocHandle, pipeline);
         pipeline.fireExceptionCaught(cause);
         if (QuicheQuicStreamChannel.this.finReceived) {
            this.closeOnRead(pipeline, readFrames);
         }
      }

      void recv() {
         assert QuicheQuicStreamChannel.this.eventLoop().inEventLoop();

         if (!QuicheQuicStreamChannel.this.inRecv) {
            QuicheQuicStreamChannel.this.inRecv = true;

            try {
               ChannelPipeline pipeline = QuicheQuicStreamChannel.this.pipeline();
               QuicheQuicStreamChannelConfig config = (QuicheQuicStreamChannelConfig)QuicheQuicStreamChannel.this.config();
               DirectIoByteBufAllocator allocator = config.allocator;
               RecvByteBufAllocator.Handle allocHandle = this.recvBufAllocHandle();
               boolean readFrames = config.isReadFrames();

               label126:
               while (QuicheQuicStreamChannel.this.active && QuicheQuicStreamChannel.this.readPending && QuicheQuicStreamChannel.this.readable) {
                  allocHandle.reset(config);
                  ByteBuf byteBuf = null;
                  QuicheQuicChannel parent = QuicheQuicStreamChannel.this.parent();
                  boolean readCompleteNeeded = false;
                  boolean continueReading = true;

                  try {
                     while (!QuicheQuicStreamChannel.this.finReceived && continueReading) {
                        byteBuf = allocHandle.allocate(allocator);
                        allocHandle.attemptedBytesRead(byteBuf.writableBytes());
                        QuicheQuicChannel.StreamRecvResult result = parent.streamRecv(QuicheQuicStreamChannel.this.streamId(), byteBuf);
                        switch (result) {
                           case DONE:
                              QuicheQuicStreamChannel.this.readable = false;
                              break;
                           case FIN:
                              QuicheQuicStreamChannel.this.readable = false;
                              QuicheQuicStreamChannel.this.finReceived = true;
                              QuicheQuicStreamChannel.this.inputShutdown = true;
                           case OK:
                              break;
                           default:
                              throw new Error("Unexpected StreamRecvResult: " + result);
                        }

                        allocHandle.lastBytesRead(byteBuf.readableBytes());
                        if (allocHandle.lastBytesRead() <= 0) {
                           byteBuf.release();
                           if (!QuicheQuicStreamChannel.this.finReceived || !readFrames) {
                              byteBuf = null;
                              if (readCompleteNeeded) {
                                 this.readComplete(allocHandle, pipeline);
                              }

                              if (QuicheQuicStreamChannel.this.finReceived) {
                                 QuicheQuicStreamChannel.this.readable = false;
                                 this.closeOnRead(pipeline, readFrames);
                              }
                              continue label126;
                           }

                           byteBuf = Unpooled.EMPTY_BUFFER;
                        }

                        allocHandle.incMessagesRead(1);
                        readCompleteNeeded = true;
                        QuicheQuicStreamChannel.this.readPending = false;
                        if (readFrames) {
                           pipeline.fireChannelRead(new DefaultQuicStreamFrame(byteBuf, QuicheQuicStreamChannel.this.finReceived));
                        } else {
                           pipeline.fireChannelRead(byteBuf);
                        }

                        byteBuf = null;
                        continueReading = allocHandle.continueReading();
                     }
                     break;
                  } catch (Throwable var14) {
                     QuicheQuicStreamChannel.this.readable = false;
                     this.handleReadException(pipeline, byteBuf, var14, allocHandle, readFrames);
                  }
               }
            } finally {
               QuicheQuicStreamChannel.this.inRecv = false;
               QuicheQuicStreamChannel.this.removeStreamFromParent();
            }
         }
      }

      private void readComplete(RecvByteBufAllocator.Handle allocHandle, ChannelPipeline pipeline) {
         allocHandle.readComplete();
         pipeline.fireChannelReadComplete();
      }
   }
}
