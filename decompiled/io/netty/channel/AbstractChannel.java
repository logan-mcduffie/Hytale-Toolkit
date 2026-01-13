package io.netty.channel;

import io.netty.channel.socket.ChannelOutputShutdownEvent;
import io.netty.channel.socket.ChannelOutputShutdownException;
import io.netty.util.DefaultAttributeMap;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PlatformDependent;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.io.IOException;
import java.net.ConnectException;
import java.net.InetSocketAddress;
import java.net.NoRouteToHostException;
import java.net.SocketAddress;
import java.net.SocketException;
import java.nio.channels.ClosedChannelException;
import java.nio.channels.NotYetConnectedException;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;

public abstract class AbstractChannel extends DefaultAttributeMap implements Channel {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannel.class);
   private final Channel parent;
   private final ChannelId id;
   private final Channel.Unsafe unsafe;
   private final DefaultChannelPipeline pipeline;
   private final VoidChannelPromise unsafeVoidPromise = new VoidChannelPromise(this, false);
   private final AbstractChannel.CloseFuture closeFuture = new AbstractChannel.CloseFuture(this);
   private volatile SocketAddress localAddress;
   private volatile SocketAddress remoteAddress;
   private volatile EventLoop eventLoop;
   private volatile boolean registered;
   private boolean closeInitiated;
   private Throwable initialCloseCause;
   private boolean strValActive;
   private String strVal;

   protected AbstractChannel(Channel parent) {
      this.parent = parent;
      this.id = this.newId();
      this.unsafe = this.newUnsafe();
      this.pipeline = this.newChannelPipeline();
   }

   protected AbstractChannel(Channel parent, ChannelId id) {
      this.parent = parent;
      this.id = id;
      this.unsafe = this.newUnsafe();
      this.pipeline = this.newChannelPipeline();
   }

   protected final int maxMessagesPerWrite() {
      ChannelConfig config = this.config();
      if (config instanceof DefaultChannelConfig) {
         return ((DefaultChannelConfig)config).getMaxMessagesPerWrite();
      } else {
         Integer value = config.getOption(ChannelOption.MAX_MESSAGES_PER_WRITE);
         return value == null ? Integer.MAX_VALUE : value;
      }
   }

   @Override
   public final ChannelId id() {
      return this.id;
   }

   protected ChannelId newId() {
      return DefaultChannelId.newInstance();
   }

   protected DefaultChannelPipeline newChannelPipeline() {
      return new DefaultChannelPipeline(this);
   }

   @Override
   public Channel parent() {
      return this.parent;
   }

   @Override
   public ChannelPipeline pipeline() {
      return this.pipeline;
   }

   @Override
   public EventLoop eventLoop() {
      EventLoop eventLoop = this.eventLoop;
      if (eventLoop == null) {
         throw new IllegalStateException("channel not registered to an event loop");
      } else {
         return eventLoop;
      }
   }

   @Override
   public SocketAddress localAddress() {
      SocketAddress localAddress = this.localAddress;
      if (localAddress == null) {
         try {
            this.localAddress = localAddress = this.unsafe().localAddress();
         } catch (Error var3) {
            throw var3;
         } catch (Throwable var4) {
            return null;
         }
      }

      return localAddress;
   }

   @Deprecated
   protected void invalidateLocalAddress() {
      this.localAddress = null;
   }

   @Override
   public SocketAddress remoteAddress() {
      SocketAddress remoteAddress = this.remoteAddress;
      if (remoteAddress == null) {
         try {
            this.remoteAddress = remoteAddress = this.unsafe().remoteAddress();
         } catch (Error var3) {
            throw var3;
         } catch (Throwable var4) {
            return null;
         }
      }

      return remoteAddress;
   }

   @Deprecated
   protected void invalidateRemoteAddress() {
      this.remoteAddress = null;
   }

   @Override
   public boolean isRegistered() {
      return this.registered;
   }

   @Override
   public ChannelFuture closeFuture() {
      return this.closeFuture;
   }

   @Override
   public Channel.Unsafe unsafe() {
      return this.unsafe;
   }

   protected abstract AbstractChannel.AbstractUnsafe newUnsafe();

   @Override
   public final int hashCode() {
      return this.id.hashCode();
   }

   @Override
   public final boolean equals(Object o) {
      return this == o;
   }

   public final int compareTo(Channel o) {
      return this == o ? 0 : this.id().compareTo(o.id());
   }

   @Override
   public String toString() {
      boolean active = this.isActive();
      if (this.strValActive == active && this.strVal != null) {
         return this.strVal;
      } else {
         SocketAddress remoteAddr = this.remoteAddress();
         SocketAddress localAddr = this.localAddress();
         if (remoteAddr != null) {
            StringBuilder buf = new StringBuilder(96)
               .append("[id: 0x")
               .append(this.id.asShortText())
               .append(", L:")
               .append(localAddr)
               .append(active ? " - " : " ! ")
               .append("R:")
               .append(remoteAddr)
               .append(']');
            this.strVal = buf.toString();
         } else if (localAddr != null) {
            StringBuilder buf = new StringBuilder(64).append("[id: 0x").append(this.id.asShortText()).append(", L:").append(localAddr).append(']');
            this.strVal = buf.toString();
         } else {
            StringBuilder buf = new StringBuilder(16).append("[id: 0x").append(this.id.asShortText()).append(']');
            this.strVal = buf.toString();
         }

         this.strValActive = active;
         return this.strVal;
      }
   }

   @Override
   public final ChannelPromise voidPromise() {
      return this.pipeline.voidPromise();
   }

   protected abstract boolean isCompatible(EventLoop var1);

   protected abstract SocketAddress localAddress0();

   protected abstract SocketAddress remoteAddress0();

   @Deprecated
   protected void doRegister() throws Exception {
   }

   protected void doRegister(ChannelPromise promise) {
      try {
         this.doRegister();
      } catch (Throwable var3) {
         promise.setFailure(var3);
         return;
      }

      promise.setSuccess();
   }

   protected abstract void doBind(SocketAddress var1) throws Exception;

   protected abstract void doDisconnect() throws Exception;

   protected abstract void doClose() throws Exception;

   protected void doShutdownOutput() throws Exception {
      this.doClose();
   }

   protected void doDeregister() throws Exception {
   }

   protected abstract void doBeginRead() throws Exception;

   protected abstract void doWrite(ChannelOutboundBuffer var1) throws Exception;

   protected Object filterOutboundMessage(Object msg) throws Exception {
      return msg;
   }

   protected void validateFileRegion(DefaultFileRegion region, long position) throws IOException {
      DefaultFileRegion.validate(region, position);
   }

   protected abstract class AbstractUnsafe implements Channel.Unsafe {
      private volatile ChannelOutboundBuffer outboundBuffer = new ChannelOutboundBuffer(AbstractChannel.this);
      private RecvByteBufAllocator.Handle recvHandle;
      private boolean inFlush0;
      private boolean neverRegistered = true;

      private void assertEventLoop() {
         assert !AbstractChannel.this.registered || AbstractChannel.this.eventLoop.inEventLoop();
      }

      @Override
      public RecvByteBufAllocator.Handle recvBufAllocHandle() {
         if (this.recvHandle == null) {
            this.recvHandle = AbstractChannel.this.config().<RecvByteBufAllocator>getRecvByteBufAllocator().newHandle();
         }

         return this.recvHandle;
      }

      @Override
      public final ChannelOutboundBuffer outboundBuffer() {
         return this.outboundBuffer;
      }

      @Override
      public final SocketAddress localAddress() {
         return AbstractChannel.this.localAddress0();
      }

      @Override
      public final SocketAddress remoteAddress() {
         return AbstractChannel.this.remoteAddress0();
      }

      @Override
      public final void register(EventLoop eventLoop, final ChannelPromise promise) {
         ObjectUtil.checkNotNull(eventLoop, "eventLoop");
         if (AbstractChannel.this.isRegistered()) {
            promise.setFailure(new IllegalStateException("registered to an event loop already"));
         } else if (!AbstractChannel.this.isCompatible(eventLoop)) {
            promise.setFailure(new IllegalStateException("incompatible event loop type: " + eventLoop.getClass().getName()));
         } else {
            AbstractChannel.this.eventLoop = eventLoop;
            AbstractChannelHandlerContext context = AbstractChannel.this.pipeline.tail;

            do {
               context.contextExecutor = null;
               context = context.prev;
            } while (context != null);

            if (eventLoop.inEventLoop()) {
               this.register0(promise);
            } else {
               try {
                  eventLoop.execute(new Runnable() {
                     @Override
                     public void run() {
                        AbstractUnsafe.this.register0(promise);
                     }
                  });
               } catch (Throwable var5) {
                  AbstractChannel.logger
                     .warn("Force-closing a channel whose registration task was not accepted by an event loop: {}", AbstractChannel.this, var5);
                  this.closeForcibly();
                  AbstractChannel.this.closeFuture.setClosed();
                  this.safeSetFailure(promise, var5);
               }
            }
         }
      }

      private void register0(final ChannelPromise promise) {
         if (promise.setUncancellable() && this.ensureOpen(promise)) {
            ChannelPromise registerPromise = AbstractChannel.this.newPromise();
            final boolean firstRegistration = this.neverRegistered;
            registerPromise.addListener(new ChannelFutureListener() {
               public void operationComplete(ChannelFuture future) throws Exception {
                  if (future.isSuccess()) {
                     AbstractUnsafe.this.neverRegistered = false;
                     AbstractChannel.this.registered = true;
                     AbstractChannel.this.pipeline.invokeHandlerAddedIfNeeded();
                     AbstractUnsafe.this.safeSetSuccess(promise);
                     AbstractChannel.this.pipeline.fireChannelRegistered();
                     if (AbstractChannel.this.isActive()) {
                        if (firstRegistration) {
                           AbstractChannel.this.pipeline.fireChannelActive();
                        } else if (AbstractChannel.this.config().isAutoRead()) {
                           AbstractUnsafe.this.beginRead();
                        }
                     }
                  } else {
                     AbstractUnsafe.this.closeForcibly();
                     AbstractChannel.this.closeFuture.setClosed();
                     AbstractUnsafe.this.safeSetFailure(promise, future.cause());
                  }
               }
            });
            AbstractChannel.this.doRegister(registerPromise);
         }
      }

      @Override
      public final void bind(SocketAddress localAddress, ChannelPromise promise) {
         this.assertEventLoop();
         if (promise.setUncancellable() && this.ensureOpen(promise)) {
            if (Boolean.TRUE.equals(AbstractChannel.this.config().getOption(ChannelOption.SO_BROADCAST))
               && localAddress instanceof InetSocketAddress
               && !((InetSocketAddress)localAddress).getAddress().isAnyLocalAddress()
               && !PlatformDependent.isWindows()
               && !PlatformDependent.maybeSuperUser()) {
               AbstractChannel.logger
                  .warn(
                     "A non-root user can't receive a broadcast packet if the socket is not bound to a wildcard address; binding to a non-wildcard address ("
                        + localAddress
                        + ") anyway as requested."
                  );
            }

            boolean wasActive = AbstractChannel.this.isActive();

            try {
               AbstractChannel.this.doBind(localAddress);
            } catch (Throwable var5) {
               this.safeSetFailure(promise, var5);
               this.closeIfClosed();
               return;
            }

            if (!wasActive && AbstractChannel.this.isActive()) {
               this.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     AbstractChannel.this.pipeline.fireChannelActive();
                  }
               });
            }

            this.safeSetSuccess(promise);
         }
      }

      @Override
      public final void disconnect(ChannelPromise promise) {
         this.assertEventLoop();
         if (promise.setUncancellable()) {
            boolean wasActive = AbstractChannel.this.isActive();

            try {
               AbstractChannel.this.doDisconnect();
               AbstractChannel.this.remoteAddress = null;
               AbstractChannel.this.localAddress = null;
            } catch (Throwable var4) {
               this.safeSetFailure(promise, var4);
               this.closeIfClosed();
               return;
            }

            if (wasActive && !AbstractChannel.this.isActive()) {
               this.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     AbstractChannel.this.pipeline.fireChannelInactive();
                  }
               });
            }

            this.safeSetSuccess(promise);
            this.closeIfClosed();
         }
      }

      @Override
      public void close(ChannelPromise promise) {
         this.assertEventLoop();
         ClosedChannelException closedChannelException = StacklessClosedChannelException.newInstance(AbstractChannel.class, "close(ChannelPromise)");
         this.close(promise, closedChannelException, closedChannelException);
      }

      public final void shutdownOutput(ChannelPromise promise) {
         this.assertEventLoop();
         this.shutdownOutput(promise, null);
      }

      private void shutdownOutput(ChannelPromise promise, Throwable cause) {
         if (promise.setUncancellable()) {
            ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
            if (outboundBuffer == null) {
               promise.setFailure(new ClosedChannelException());
            } else {
               this.outboundBuffer = null;
               Throwable shutdownCause = cause == null
                  ? new ChannelOutputShutdownException("Channel output shutdown")
                  : new ChannelOutputShutdownException("Channel output shutdown", cause);

               try {
                  AbstractChannel.this.doShutdownOutput();
                  promise.setSuccess();
               } catch (Throwable var9) {
                  promise.setFailure(var9);
               } finally {
                  this.closeOutboundBufferForShutdown(AbstractChannel.this.pipeline, outboundBuffer, shutdownCause);
               }
            }
         }
      }

      private void closeOutboundBufferForShutdown(ChannelPipeline pipeline, ChannelOutboundBuffer buffer, Throwable cause) {
         buffer.failFlushed(cause, false);
         buffer.close(cause, true);
         pipeline.fireUserEventTriggered(ChannelOutputShutdownEvent.INSTANCE);
      }

      protected void close(final ChannelPromise promise, final Throwable cause, final ClosedChannelException closeCause) {
         if (promise.setUncancellable()) {
            if (AbstractChannel.this.closeInitiated) {
               if (AbstractChannel.this.closeFuture.isDone()) {
                  this.safeSetSuccess(promise);
               } else if (!(promise instanceof VoidChannelPromise)) {
                  AbstractChannel.this.closeFuture.addListener(new ChannelFutureListener() {
                     public void operationComplete(ChannelFuture future) throws Exception {
                        promise.setSuccess();
                     }
                  });
               }
            } else {
               AbstractChannel.this.closeInitiated = true;
               final boolean wasActive = AbstractChannel.this.isActive();
               final ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
               this.outboundBuffer = null;
               Executor closeExecutor = this.prepareToClose();
               if (closeExecutor != null) {
                  closeExecutor.execute(new Runnable() {
                     @Override
                     public void run() {
                        try {
                           AbstractUnsafe.this.doClose0(promise);
                        } finally {
                           AbstractUnsafe.this.invokeLater(new Runnable() {
                              @Override
                              public void run() {
                                 if (outboundBuffer != null) {
                                    outboundBuffer.failFlushed(cause, false);
                                    outboundBuffer.close(closeCause);
                                 }

                                 AbstractUnsafe.this.fireChannelInactiveAndDeregister(wasActive);
                              }
                           });
                        }
                     }
                  });
               } else {
                  try {
                     this.doClose0(promise);
                  } finally {
                     if (outboundBuffer != null) {
                        outboundBuffer.failFlushed(cause, false);
                        outboundBuffer.close(closeCause);
                     }
                  }

                  if (this.inFlush0) {
                     this.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                           AbstractUnsafe.this.fireChannelInactiveAndDeregister(wasActive);
                        }
                     });
                  } else {
                     this.fireChannelInactiveAndDeregister(wasActive);
                  }
               }
            }
         }
      }

      private void doClose0(ChannelPromise promise) {
         try {
            AbstractChannel.this.doClose();
            AbstractChannel.this.closeFuture.setClosed();
            this.safeSetSuccess(promise);
         } catch (Throwable var3) {
            AbstractChannel.this.closeFuture.setClosed();
            this.safeSetFailure(promise, var3);
         }
      }

      private void fireChannelInactiveAndDeregister(boolean wasActive) {
         this.deregister(this.voidPromise(), wasActive && !AbstractChannel.this.isActive());
      }

      @Override
      public final void closeForcibly() {
         this.assertEventLoop();

         try {
            AbstractChannel.this.doClose();
         } catch (Exception var2) {
            AbstractChannel.logger.warn("Failed to close a channel.", (Throwable)var2);
         }
      }

      @Override
      public final void deregister(ChannelPromise promise) {
         this.assertEventLoop();
         this.deregister(promise, false);
      }

      private void deregister(final ChannelPromise promise, final boolean fireChannelInactive) {
         if (promise.setUncancellable()) {
            if (!AbstractChannel.this.registered) {
               this.safeSetSuccess(promise);
            } else {
               this.invokeLater(new Runnable() {
                  @Override
                  public void run() {
                     try {
                        AbstractChannel.this.doDeregister();
                     } catch (Throwable var5) {
                        AbstractChannel.logger.warn("Unexpected exception occurred while deregistering a channel.", var5);
                     } finally {
                        if (fireChannelInactive) {
                           AbstractChannel.this.pipeline.fireChannelInactive();
                        }

                        if (AbstractChannel.this.registered) {
                           AbstractChannel.this.registered = false;
                           AbstractChannel.this.pipeline.fireChannelUnregistered();
                        }

                        AbstractUnsafe.this.safeSetSuccess(promise);
                     }
                  }
               });
            }
         }
      }

      @Override
      public final void beginRead() {
         this.assertEventLoop();

         try {
            AbstractChannel.this.doBeginRead();
         } catch (Exception var2) {
            final Exception e = var2;
            this.invokeLater(new Runnable() {
               @Override
               public void run() {
                  AbstractChannel.this.pipeline.fireExceptionCaught(e);
               }
            });
            this.close(this.voidPromise());
         }
      }

      @Override
      public final void write(Object msg, ChannelPromise promise) {
         this.assertEventLoop();
         ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
         if (outboundBuffer == null) {
            try {
               ReferenceCountUtil.release(msg);
            } finally {
               this.safeSetFailure(promise, this.newClosedChannelException(AbstractChannel.this.initialCloseCause, "write(Object, ChannelPromise)"));
            }
         } else {
            int size;
            try {
               msg = AbstractChannel.this.filterOutboundMessage(msg);
               size = AbstractChannel.this.pipeline.estimatorHandle().size(msg);
               if (size < 0) {
                  size = 0;
               }
            } catch (Throwable var15) {
               try {
                  ReferenceCountUtil.release(msg);
               } finally {
                  this.safeSetFailure(promise, var15);
               }

               return;
            }

            outboundBuffer.addMessage(msg, size, promise);
         }
      }

      @Override
      public final void flush() {
         this.assertEventLoop();
         ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
         if (outboundBuffer != null) {
            outboundBuffer.addFlush();
            this.flush0();
         }
      }

      protected void flush0() {
         if (!this.inFlush0) {
            ChannelOutboundBuffer outboundBuffer = this.outboundBuffer;
            if (outboundBuffer != null && !outboundBuffer.isEmpty()) {
               this.inFlush0 = true;
               if (AbstractChannel.this.isActive()) {
                  try {
                     AbstractChannel.this.doWrite(outboundBuffer);
                  } catch (Throwable var10) {
                     this.handleWriteError(var10);
                  } finally {
                     this.inFlush0 = false;
                  }
               } else {
                  try {
                     if (!outboundBuffer.isEmpty()) {
                        if (AbstractChannel.this.isOpen()) {
                           outboundBuffer.failFlushed(new NotYetConnectedException(), true);
                        } else {
                           outboundBuffer.failFlushed(this.newClosedChannelException(AbstractChannel.this.initialCloseCause, "flush0()"), false);
                        }
                     }
                  } finally {
                     this.inFlush0 = false;
                  }
               }
            }
         }
      }

      protected final void handleWriteError(Throwable t) {
         if (t instanceof IOException && AbstractChannel.this.config().isAutoClose()) {
            AbstractChannel.this.initialCloseCause = t;
            this.close(this.voidPromise(), t, this.newClosedChannelException(t, "flush0()"));
         } else {
            try {
               this.shutdownOutput(this.voidPromise(), t);
            } catch (Throwable var3) {
               AbstractChannel.this.initialCloseCause = t;
               this.close(this.voidPromise(), var3, this.newClosedChannelException(t, "flush0()"));
            }
         }
      }

      private ClosedChannelException newClosedChannelException(Throwable cause, String method) {
         ClosedChannelException exception = StacklessClosedChannelException.newInstance(AbstractChannel.AbstractUnsafe.class, method);
         if (cause != null) {
            exception.initCause(cause);
         }

         return exception;
      }

      @Override
      public final ChannelPromise voidPromise() {
         this.assertEventLoop();
         return AbstractChannel.this.unsafeVoidPromise;
      }

      protected final boolean ensureOpen(ChannelPromise promise) {
         if (AbstractChannel.this.isOpen()) {
            return true;
         } else {
            this.safeSetFailure(promise, this.newClosedChannelException(AbstractChannel.this.initialCloseCause, "ensureOpen(ChannelPromise)"));
            return false;
         }
      }

      protected final void safeSetSuccess(ChannelPromise promise) {
         if (!(promise instanceof VoidChannelPromise) && !promise.trySuccess()) {
            AbstractChannel.logger.warn("Failed to mark a promise as success because it is done already: {}", promise);
         }
      }

      protected final void safeSetFailure(ChannelPromise promise, Throwable cause) {
         if (!(promise instanceof VoidChannelPromise) && !promise.tryFailure(cause)) {
            AbstractChannel.logger.warn("Failed to mark a promise as failure because it's done already: {}", promise, cause);
         }
      }

      protected final void closeIfClosed() {
         if (!AbstractChannel.this.isOpen()) {
            this.close(this.voidPromise());
         }
      }

      private void invokeLater(Runnable task) {
         try {
            AbstractChannel.this.eventLoop().execute(task);
         } catch (RejectedExecutionException var3) {
            AbstractChannel.logger.warn("Can't invoke task later as EventLoop rejected it", (Throwable)var3);
         }
      }

      protected final Throwable annotateConnectException(Throwable cause, SocketAddress remoteAddress) {
         if (cause instanceof ConnectException) {
            return new AbstractChannel.AnnotatedConnectException((ConnectException)cause, remoteAddress);
         } else if (cause instanceof NoRouteToHostException) {
            return new AbstractChannel.AnnotatedNoRouteToHostException((NoRouteToHostException)cause, remoteAddress);
         } else {
            return (Throwable)(cause instanceof SocketException ? new AbstractChannel.AnnotatedSocketException((SocketException)cause, remoteAddress) : cause);
         }
      }

      protected Executor prepareToClose() {
         return null;
      }
   }

   private static final class AnnotatedConnectException extends ConnectException {
      private static final long serialVersionUID = 3901958112696433556L;

      AnnotatedConnectException(ConnectException exception, SocketAddress remoteAddress) {
         super(exception.getMessage() + ": " + remoteAddress);
         this.initCause(exception);
      }

      @Override
      public Throwable fillInStackTrace() {
         return this;
      }
   }

   private static final class AnnotatedNoRouteToHostException extends NoRouteToHostException {
      private static final long serialVersionUID = -6801433937592080623L;

      AnnotatedNoRouteToHostException(NoRouteToHostException exception, SocketAddress remoteAddress) {
         super(exception.getMessage() + ": " + remoteAddress);
         this.initCause(exception);
      }

      @Override
      public Throwable fillInStackTrace() {
         return this;
      }
   }

   private static final class AnnotatedSocketException extends SocketException {
      private static final long serialVersionUID = 3896743275010454039L;

      AnnotatedSocketException(SocketException exception, SocketAddress remoteAddress) {
         super(exception.getMessage() + ": " + remoteAddress);
         this.initCause(exception);
      }

      @Override
      public Throwable fillInStackTrace() {
         return this;
      }
   }

   static final class CloseFuture extends DefaultChannelPromise {
      CloseFuture(AbstractChannel ch) {
         super(ch);
      }

      @Override
      public ChannelPromise setSuccess() {
         throw new IllegalStateException();
      }

      @Override
      public ChannelPromise setFailure(Throwable cause) {
         throw new IllegalStateException();
      }

      @Override
      public boolean trySuccess() {
         throw new IllegalStateException();
      }

      @Override
      public boolean tryFailure(Throwable cause) {
         throw new IllegalStateException();
      }

      boolean setClosed() {
         return super.trySuccess();
      }
   }
}
