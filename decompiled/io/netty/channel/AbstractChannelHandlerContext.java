package io.netty.channel;

import io.netty.buffer.ByteBufAllocator;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;
import io.netty.util.Recycler;
import io.netty.util.ReferenceCountUtil;
import io.netty.util.ResourceLeakHint;
import io.netty.util.concurrent.AbstractEventExecutor;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.OrderedEventExecutor;
import io.netty.util.internal.ObjectPool;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.PromiseNotificationUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.SystemPropertyUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

abstract class AbstractChannelHandlerContext implements ChannelHandlerContext, ResourceLeakHint {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(AbstractChannelHandlerContext.class);
   volatile AbstractChannelHandlerContext next;
   volatile AbstractChannelHandlerContext prev;
   private static final AtomicIntegerFieldUpdater<AbstractChannelHandlerContext> HANDLER_STATE_UPDATER = AtomicIntegerFieldUpdater.newUpdater(
      AbstractChannelHandlerContext.class, "handlerState"
   );
   private static final int ADD_PENDING = 1;
   private static final int ADD_COMPLETE = 2;
   private static final int REMOVE_COMPLETE = 3;
   private static final int INIT = 0;
   private final DefaultChannelPipeline pipeline;
   private final String name;
   private final boolean ordered;
   private final int executionMask;
   final EventExecutor childExecutor;
   EventExecutor contextExecutor;
   private ChannelFuture succeededFuture;
   private AbstractChannelHandlerContext.Tasks invokeTasks;
   private volatile int handlerState = 0;

   AbstractChannelHandlerContext(DefaultChannelPipeline pipeline, EventExecutor executor, String name, Class<? extends ChannelHandler> handlerClass) {
      this.name = ObjectUtil.checkNotNull(name, "name");
      this.pipeline = pipeline;
      this.childExecutor = executor;
      this.executionMask = ChannelHandlerMask.mask(handlerClass);
      this.ordered = executor == null || executor instanceof OrderedEventExecutor;
   }

   @Override
   public Channel channel() {
      return this.pipeline.channel();
   }

   @Override
   public ChannelPipeline pipeline() {
      return this.pipeline;
   }

   @Override
   public ByteBufAllocator alloc() {
      return this.channel().config().getAllocator();
   }

   @Override
   public EventExecutor executor() {
      EventExecutor ex = this.contextExecutor;
      if (ex == null) {
         this.contextExecutor = ex = (EventExecutor)(this.childExecutor != null ? this.childExecutor : this.channel().eventLoop());
      }

      return ex;
   }

   @Override
   public String name() {
      return this.name;
   }

   @Override
   public ChannelHandlerContext fireChannelRegistered() {
      AbstractChannelHandlerContext next = this.findContextInbound(2);
      if (next.executor().inEventLoop()) {
         if (next.invokeHandler()) {
            try {
               ChannelHandler handler = next.handler();
               DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
               if (handler == headContext) {
                  headContext.channelRegistered(next);
               } else if (handler instanceof ChannelInboundHandlerAdapter) {
                  ((ChannelInboundHandlerAdapter)handler).channelRegistered(next);
               } else {
                  ((ChannelInboundHandler)handler).channelRegistered(next);
               }
            } catch (Throwable var4) {
               next.invokeExceptionCaught(var4);
            }
         } else {
            next.fireChannelRegistered();
         }
      } else {
         next.executor().execute(this::fireChannelRegistered);
      }

      return this;
   }

   @Override
   public ChannelHandlerContext fireChannelUnregistered() {
      AbstractChannelHandlerContext next = this.findContextInbound(4);
      if (next.executor().inEventLoop()) {
         if (next.invokeHandler()) {
            try {
               ChannelHandler handler = next.handler();
               DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
               if (handler == headContext) {
                  headContext.channelUnregistered(next);
               } else if (handler instanceof ChannelInboundHandlerAdapter) {
                  ((ChannelInboundHandlerAdapter)handler).channelUnregistered(next);
               } else {
                  ((ChannelInboundHandler)handler).channelUnregistered(next);
               }
            } catch (Throwable var4) {
               next.invokeExceptionCaught(var4);
            }
         } else {
            next.fireChannelUnregistered();
         }
      } else {
         next.executor().execute(this::fireChannelUnregistered);
      }

      return this;
   }

   @Override
   public ChannelHandlerContext fireChannelActive() {
      AbstractChannelHandlerContext next = this.findContextInbound(8);
      if (next.executor().inEventLoop()) {
         if (next.invokeHandler()) {
            try {
               ChannelHandler handler = next.handler();
               DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
               if (handler == headContext) {
                  headContext.channelActive(next);
               } else if (handler instanceof ChannelInboundHandlerAdapter) {
                  ((ChannelInboundHandlerAdapter)handler).channelActive(next);
               } else {
                  ((ChannelInboundHandler)handler).channelActive(next);
               }
            } catch (Throwable var4) {
               next.invokeExceptionCaught(var4);
            }
         } else {
            next.fireChannelActive();
         }
      } else {
         next.executor().execute(this::fireChannelActive);
      }

      return this;
   }

   @Override
   public ChannelHandlerContext fireChannelInactive() {
      AbstractChannelHandlerContext next = this.findContextInbound(16);
      if (next.executor().inEventLoop()) {
         if (next.invokeHandler()) {
            try {
               ChannelHandler handler = next.handler();
               DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
               if (handler == headContext) {
                  headContext.channelInactive(next);
               } else if (handler instanceof ChannelInboundHandlerAdapter) {
                  ((ChannelInboundHandlerAdapter)handler).channelInactive(next);
               } else {
                  ((ChannelInboundHandler)handler).channelInactive(next);
               }
            } catch (Throwable var4) {
               next.invokeExceptionCaught(var4);
            }
         } else {
            next.fireChannelInactive();
         }
      } else {
         next.executor().execute(this::fireChannelInactive);
      }

      return this;
   }

   @Override
   public ChannelHandlerContext fireExceptionCaught(Throwable cause) {
      AbstractChannelHandlerContext next = this.findContextInbound(1);
      ObjectUtil.checkNotNull(cause, "cause");
      if (next.executor().inEventLoop()) {
         next.invokeExceptionCaught(cause);
      } else {
         try {
            next.executor().execute(() -> next.invokeExceptionCaught(cause));
         } catch (Throwable var4) {
            if (logger.isWarnEnabled()) {
               logger.warn("Failed to submit an exceptionCaught() event.", var4);
               logger.warn("The exceptionCaught() event that was failed to submit was:", cause);
            }
         }
      }

      return this;
   }

   private void invokeExceptionCaught(Throwable cause) {
      if (this.invokeHandler()) {
         try {
            this.handler().exceptionCaught(this, cause);
         } catch (Throwable var3) {
            if (logger.isDebugEnabled()) {
               logger.debug("An exception was thrown by a user handler's exceptionCaught() method while handling the following exception:", cause);
            } else if (logger.isWarnEnabled()) {
               logger.warn(
                  "An exception '{}' [enable DEBUG level for full stacktrace] was thrown by a user handler's exceptionCaught() method while handling the following exception:",
                  var3,
                  cause
               );
            }
         }
      } else {
         this.fireExceptionCaught(cause);
      }
   }

   @Override
   public ChannelHandlerContext fireUserEventTriggered(Object event) {
      ObjectUtil.checkNotNull(event, "event");
      AbstractChannelHandlerContext next = this.findContextInbound(128);
      if (next.executor().inEventLoop()) {
         if (next.invokeHandler()) {
            try {
               ChannelHandler handler = next.handler();
               DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
               if (handler == headContext) {
                  headContext.userEventTriggered(next, event);
               } else if (handler instanceof ChannelInboundHandlerAdapter) {
                  ((ChannelInboundHandlerAdapter)handler).userEventTriggered(next, event);
               } else {
                  ((ChannelInboundHandler)handler).userEventTriggered(next, event);
               }
            } catch (Throwable var5) {
               next.invokeExceptionCaught(var5);
            }
         } else {
            next.fireUserEventTriggered(event);
         }
      } else {
         next.executor().execute(() -> this.fireUserEventTriggered(event));
      }

      return this;
   }

   @Override
   public ChannelHandlerContext fireChannelRead(Object msg) {
      AbstractChannelHandlerContext next = this.findContextInbound(32);
      if (next.executor().inEventLoop()) {
         Object m = this.pipeline.touch(msg, next);
         if (next.invokeHandler()) {
            try {
               ChannelHandler handler = next.handler();
               DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
               if (handler == headContext) {
                  headContext.channelRead(next, m);
               } else if (handler instanceof ChannelDuplexHandler) {
                  ((ChannelDuplexHandler)handler).channelRead(next, m);
               } else {
                  ((ChannelInboundHandler)handler).channelRead(next, m);
               }
            } catch (Throwable var6) {
               next.invokeExceptionCaught(var6);
            }
         } else {
            next.fireChannelRead(m);
         }
      } else {
         next.executor().execute(() -> this.fireChannelRead(msg));
      }

      return this;
   }

   @Override
   public ChannelHandlerContext fireChannelReadComplete() {
      AbstractChannelHandlerContext next = this.findContextInbound(64);
      if (next.executor().inEventLoop()) {
         if (next.invokeHandler()) {
            try {
               ChannelHandler handler = next.handler();
               DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
               if (handler == headContext) {
                  headContext.channelReadComplete(next);
               } else if (handler instanceof ChannelDuplexHandler) {
                  ((ChannelDuplexHandler)handler).channelReadComplete(next);
               } else {
                  ((ChannelInboundHandler)handler).channelReadComplete(next);
               }
            } catch (Throwable var4) {
               next.invokeExceptionCaught(var4);
            }
         } else {
            next.fireChannelReadComplete();
         }
      } else {
         next.executor().execute(this.getInvokeTasks().invokeChannelReadCompleteTask);
      }

      return this;
   }

   @Override
   public ChannelHandlerContext fireChannelWritabilityChanged() {
      AbstractChannelHandlerContext next = this.findContextInbound(256);
      if (next.executor().inEventLoop()) {
         if (next.invokeHandler()) {
            try {
               ChannelHandler handler = next.handler();
               DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
               if (handler == headContext) {
                  headContext.channelWritabilityChanged(next);
               } else if (handler instanceof ChannelInboundHandlerAdapter) {
                  ((ChannelInboundHandlerAdapter)handler).channelWritabilityChanged(next);
               } else {
                  ((ChannelInboundHandler)handler).channelWritabilityChanged(next);
               }
            } catch (Throwable var4) {
               next.invokeExceptionCaught(var4);
            }
         } else {
            next.fireChannelWritabilityChanged();
         }
      } else {
         next.executor().execute(this.getInvokeTasks().invokeChannelWritableStateChangedTask);
      }

      return this;
   }

   @Override
   public ChannelFuture bind(SocketAddress localAddress) {
      return this.bind(localAddress, this.newPromise());
   }

   @Override
   public ChannelFuture connect(SocketAddress remoteAddress) {
      return this.connect(remoteAddress, this.newPromise());
   }

   @Override
   public ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
      return this.connect(remoteAddress, localAddress, this.newPromise());
   }

   @Override
   public ChannelFuture disconnect() {
      return this.disconnect(this.newPromise());
   }

   @Override
   public ChannelFuture close() {
      return this.close(this.newPromise());
   }

   @Override
   public ChannelFuture deregister() {
      return this.deregister(this.newPromise());
   }

   @Override
   public ChannelFuture bind(final SocketAddress localAddress, final ChannelPromise promise) {
      ObjectUtil.checkNotNull(localAddress, "localAddress");
      if (this.isNotValidPromise(promise, false)) {
         return promise;
      } else {
         final AbstractChannelHandlerContext next = this.findContextOutbound(512);
         EventExecutor executor = next.executor();
         if (executor.inEventLoop()) {
            next.invokeBind(localAddress, promise);
         } else {
            safeExecute(executor, new Runnable() {
               @Override
               public void run() {
                  next.invokeBind(localAddress, promise);
               }
            }, promise, null, false);
         }

         return promise;
      }
   }

   private void invokeBind(SocketAddress localAddress, ChannelPromise promise) {
      if (this.invokeHandler()) {
         try {
            ChannelHandler handler = this.handler();
            DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
            if (handler == headContext) {
               headContext.bind(this, localAddress, promise);
            } else if (handler instanceof ChannelDuplexHandler) {
               ((ChannelDuplexHandler)handler).bind(this, localAddress, promise);
            } else if (handler instanceof ChannelOutboundHandlerAdapter) {
               ((ChannelOutboundHandlerAdapter)handler).bind(this, localAddress, promise);
            } else {
               ((ChannelOutboundHandler)handler).bind(this, localAddress, promise);
            }
         } catch (Throwable var5) {
            notifyOutboundHandlerException(var5, promise);
         }
      } else {
         this.bind(localAddress, promise);
      }
   }

   @Override
   public ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
      return this.connect(remoteAddress, null, promise);
   }

   @Override
   public ChannelFuture connect(final SocketAddress remoteAddress, final SocketAddress localAddress, final ChannelPromise promise) {
      ObjectUtil.checkNotNull(remoteAddress, "remoteAddress");
      if (this.isNotValidPromise(promise, false)) {
         return promise;
      } else {
         final AbstractChannelHandlerContext next = this.findContextOutbound(1024);
         EventExecutor executor = next.executor();
         if (executor.inEventLoop()) {
            next.invokeConnect(remoteAddress, localAddress, promise);
         } else {
            safeExecute(executor, new Runnable() {
               @Override
               public void run() {
                  next.invokeConnect(remoteAddress, localAddress, promise);
               }
            }, promise, null, false);
         }

         return promise;
      }
   }

   private void invokeConnect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      if (this.invokeHandler()) {
         try {
            ChannelHandler handler = this.handler();
            DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
            if (handler == headContext) {
               headContext.connect(this, remoteAddress, localAddress, promise);
            } else if (handler instanceof ChannelDuplexHandler) {
               ((ChannelDuplexHandler)handler).connect(this, remoteAddress, localAddress, promise);
            } else if (handler instanceof ChannelOutboundHandlerAdapter) {
               ((ChannelOutboundHandlerAdapter)handler).connect(this, remoteAddress, localAddress, promise);
            } else {
               ((ChannelOutboundHandler)handler).connect(this, remoteAddress, localAddress, promise);
            }
         } catch (Throwable var6) {
            notifyOutboundHandlerException(var6, promise);
         }
      } else {
         this.connect(remoteAddress, localAddress, promise);
      }
   }

   @Override
   public ChannelFuture disconnect(final ChannelPromise promise) {
      if (!this.channel().metadata().hasDisconnect()) {
         return this.close(promise);
      } else if (this.isNotValidPromise(promise, false)) {
         return promise;
      } else {
         final AbstractChannelHandlerContext next = this.findContextOutbound(2048);
         EventExecutor executor = next.executor();
         if (executor.inEventLoop()) {
            next.invokeDisconnect(promise);
         } else {
            safeExecute(executor, new Runnable() {
               @Override
               public void run() {
                  next.invokeDisconnect(promise);
               }
            }, promise, null, false);
         }

         return promise;
      }
   }

   private void invokeDisconnect(ChannelPromise promise) {
      if (this.invokeHandler()) {
         try {
            ChannelHandler handler = this.handler();
            DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
            if (handler == headContext) {
               headContext.disconnect(this, promise);
            } else if (handler instanceof ChannelDuplexHandler) {
               ((ChannelDuplexHandler)handler).disconnect(this, promise);
            } else if (handler instanceof ChannelOutboundHandlerAdapter) {
               ((ChannelOutboundHandlerAdapter)handler).disconnect(this, promise);
            } else {
               ((ChannelOutboundHandler)handler).disconnect(this, promise);
            }
         } catch (Throwable var4) {
            notifyOutboundHandlerException(var4, promise);
         }
      } else {
         this.disconnect(promise);
      }
   }

   @Override
   public ChannelFuture close(final ChannelPromise promise) {
      if (this.isNotValidPromise(promise, false)) {
         return promise;
      } else {
         final AbstractChannelHandlerContext next = this.findContextOutbound(4096);
         EventExecutor executor = next.executor();
         if (executor.inEventLoop()) {
            next.invokeClose(promise);
         } else {
            safeExecute(executor, new Runnable() {
               @Override
               public void run() {
                  next.invokeClose(promise);
               }
            }, promise, null, false);
         }

         return promise;
      }
   }

   private void invokeClose(ChannelPromise promise) {
      if (this.invokeHandler()) {
         try {
            ChannelHandler handler = this.handler();
            DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
            if (handler == headContext) {
               headContext.close(this, promise);
            } else if (handler instanceof ChannelDuplexHandler) {
               ((ChannelDuplexHandler)handler).close(this, promise);
            } else if (handler instanceof ChannelOutboundHandlerAdapter) {
               ((ChannelOutboundHandlerAdapter)handler).close(this, promise);
            } else {
               ((ChannelOutboundHandler)handler).close(this, promise);
            }
         } catch (Throwable var4) {
            notifyOutboundHandlerException(var4, promise);
         }
      } else {
         this.close(promise);
      }
   }

   @Override
   public ChannelFuture deregister(final ChannelPromise promise) {
      if (this.isNotValidPromise(promise, false)) {
         return promise;
      } else {
         final AbstractChannelHandlerContext next = this.findContextOutbound(8192);
         EventExecutor executor = next.executor();
         if (executor.inEventLoop()) {
            next.invokeDeregister(promise);
         } else {
            safeExecute(executor, new Runnable() {
               @Override
               public void run() {
                  next.invokeDeregister(promise);
               }
            }, promise, null, false);
         }

         return promise;
      }
   }

   private void invokeDeregister(ChannelPromise promise) {
      if (this.invokeHandler()) {
         try {
            ChannelHandler handler = this.handler();
            DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
            if (handler == headContext) {
               headContext.deregister(this, promise);
            } else if (handler instanceof ChannelDuplexHandler) {
               ((ChannelDuplexHandler)handler).deregister(this, promise);
            } else if (handler instanceof ChannelOutboundHandlerAdapter) {
               ((ChannelOutboundHandlerAdapter)handler).deregister(this, promise);
            } else {
               ((ChannelOutboundHandler)handler).deregister(this, promise);
            }
         } catch (Throwable var4) {
            notifyOutboundHandlerException(var4, promise);
         }
      } else {
         this.deregister(promise);
      }
   }

   @Override
   public ChannelHandlerContext read() {
      AbstractChannelHandlerContext next = this.findContextOutbound(16384);
      if (next.executor().inEventLoop()) {
         if (next.invokeHandler()) {
            try {
               ChannelHandler handler = next.handler();
               DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
               if (handler == headContext) {
                  headContext.read(next);
               } else if (handler instanceof ChannelDuplexHandler) {
                  ((ChannelDuplexHandler)handler).read(next);
               } else if (handler instanceof ChannelOutboundHandlerAdapter) {
                  ((ChannelOutboundHandlerAdapter)handler).read(next);
               } else {
                  ((ChannelOutboundHandler)handler).read(next);
               }
            } catch (Throwable var4) {
               this.invokeExceptionCaught(var4);
            }
         } else {
            next.read();
         }
      } else {
         next.executor().execute(this.getInvokeTasks().invokeReadTask);
      }

      return this;
   }

   @Override
   public ChannelFuture write(Object msg) {
      ChannelPromise promise = this.newPromise();
      this.write(msg, false, promise);
      return promise;
   }

   @Override
   public ChannelFuture write(Object msg, ChannelPromise promise) {
      this.write(msg, false, promise);
      return promise;
   }

   @Override
   public ChannelHandlerContext flush() {
      AbstractChannelHandlerContext next = this.findContextOutbound(65536);
      EventExecutor executor = next.executor();
      if (executor.inEventLoop()) {
         next.invokeFlush();
      } else {
         AbstractChannelHandlerContext.Tasks tasks = next.invokeTasks;
         if (tasks == null) {
            next.invokeTasks = tasks = new AbstractChannelHandlerContext.Tasks(next);
         }

         safeExecute(executor, tasks.invokeFlushTask, this.channel().voidPromise(), null, false);
      }

      return this;
   }

   private void invokeFlush() {
      if (this.invokeHandler()) {
         this.invokeFlush0();
      } else {
         this.flush();
      }
   }

   private void invokeFlush0() {
      try {
         ChannelHandler handler = this.handler();
         DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
         if (handler == headContext) {
            headContext.flush(this);
         } else if (handler instanceof ChannelDuplexHandler) {
            ((ChannelDuplexHandler)handler).flush(this);
         } else if (handler instanceof ChannelOutboundHandlerAdapter) {
            ((ChannelOutboundHandlerAdapter)handler).flush(this);
         } else {
            ((ChannelOutboundHandler)handler).flush(this);
         }
      } catch (Throwable var3) {
         this.invokeExceptionCaught(var3);
      }
   }

   @Override
   public ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
      this.write(msg, true, promise);
      return promise;
   }

   void write(Object msg, boolean flush, ChannelPromise promise) {
      if (this.validateWrite(msg, promise)) {
         AbstractChannelHandlerContext next = this.findContextOutbound(flush ? 98304 : '耀');
         Object m = this.pipeline.touch(msg, next);
         EventExecutor executor = next.executor();
         if (executor.inEventLoop()) {
            if (next.invokeHandler()) {
               try {
                  ChannelHandler handler = next.handler();
                  DefaultChannelPipeline.HeadContext headContext = this.pipeline.head;
                  if (handler == headContext) {
                     headContext.write(next, msg, promise);
                  } else if (handler instanceof ChannelDuplexHandler) {
                     ((ChannelDuplexHandler)handler).write(next, msg, promise);
                  } else if (handler instanceof ChannelOutboundHandlerAdapter) {
                     ((ChannelOutboundHandlerAdapter)handler).write(next, msg, promise);
                  } else {
                     ((ChannelOutboundHandler)handler).write(next, msg, promise);
                  }
               } catch (Throwable var9) {
                  notifyOutboundHandlerException(var9, promise);
               }

               if (flush) {
                  next.invokeFlush0();
               }
            } else {
               next.write(msg, flush, promise);
            }
         } else {
            AbstractChannelHandlerContext.WriteTask task = AbstractChannelHandlerContext.WriteTask.newInstance(this, m, promise, flush);
            if (!safeExecute(executor, task, promise, m, !flush)) {
               task.cancel();
            }
         }
      }
   }

   private boolean validateWrite(Object msg, ChannelPromise promise) {
      ObjectUtil.checkNotNull(msg, "msg");

      try {
         if (this.isNotValidPromise(promise, true)) {
            ReferenceCountUtil.release(msg);
            return false;
         } else {
            return true;
         }
      } catch (RuntimeException var4) {
         ReferenceCountUtil.release(msg);
         throw var4;
      }
   }

   @Override
   public ChannelFuture writeAndFlush(Object msg) {
      return this.writeAndFlush(msg, this.newPromise());
   }

   private static void notifyOutboundHandlerException(Throwable cause, ChannelPromise promise) {
      PromiseNotificationUtil.tryFailure(promise, cause, promise instanceof VoidChannelPromise ? null : logger);
   }

   @Override
   public ChannelPromise newPromise() {
      return new DefaultChannelPromise(this.channel(), this.executor());
   }

   @Override
   public ChannelProgressivePromise newProgressivePromise() {
      return new DefaultChannelProgressivePromise(this.channel(), this.executor());
   }

   @Override
   public ChannelFuture newSucceededFuture() {
      ChannelFuture succeededFuture = this.succeededFuture;
      if (succeededFuture == null) {
         this.succeededFuture = succeededFuture = new SucceededChannelFuture(this.channel(), this.executor());
      }

      return succeededFuture;
   }

   @Override
   public ChannelFuture newFailedFuture(Throwable cause) {
      return new FailedChannelFuture(this.channel(), this.executor(), cause);
   }

   private boolean isNotValidPromise(ChannelPromise promise, boolean allowVoidPromise) {
      ObjectUtil.checkNotNull(promise, "promise");
      if (promise.isDone()) {
         if (promise.isCancelled()) {
            return true;
         } else {
            throw new IllegalArgumentException("promise already done: " + promise);
         }
      } else if (promise.channel() != this.channel()) {
         throw new IllegalArgumentException(String.format("promise.channel does not match: %s (expected: %s)", promise.channel(), this.channel()));
      } else if (promise.getClass() == DefaultChannelPromise.class) {
         return false;
      } else if (!allowVoidPromise && promise instanceof VoidChannelPromise) {
         throw new IllegalArgumentException(StringUtil.simpleClassName(VoidChannelPromise.class) + " not allowed for this operation");
      } else if (promise instanceof AbstractChannel.CloseFuture) {
         throw new IllegalArgumentException(StringUtil.simpleClassName(AbstractChannel.CloseFuture.class) + " not allowed in a pipeline");
      } else {
         return false;
      }
   }

   private AbstractChannelHandlerContext findContextInbound(int mask) {
      AbstractChannelHandlerContext ctx = this;
      EventExecutor currentExecutor = this.executor();

      do {
         ctx = ctx.next;
      } while (skipContext(ctx, currentExecutor, mask, 510));

      return ctx;
   }

   private AbstractChannelHandlerContext findContextOutbound(int mask) {
      AbstractChannelHandlerContext ctx = this;
      EventExecutor currentExecutor = this.executor();

      do {
         ctx = ctx.prev;
      } while (skipContext(ctx, currentExecutor, mask, 130560));

      return ctx;
   }

   private static boolean skipContext(AbstractChannelHandlerContext ctx, EventExecutor currentExecutor, int mask, int onlyMask) {
      return (ctx.executionMask & (onlyMask | mask)) == 0 || ctx.executor() == currentExecutor && (ctx.executionMask & mask) == 0;
   }

   @Override
   public ChannelPromise voidPromise() {
      return this.channel().voidPromise();
   }

   final void setRemoved() {
      this.handlerState = 3;
   }

   final boolean setAddComplete() {
      int oldState;
      do {
         oldState = this.handlerState;
         if (oldState == 3) {
            return false;
         }
      } while (!HANDLER_STATE_UPDATER.compareAndSet(this, oldState, 2));

      return true;
   }

   final void setAddPending() {
      boolean updated = HANDLER_STATE_UPDATER.compareAndSet(this, 0, 1);

      assert updated;
   }

   final void callHandlerAdded() throws Exception {
      if (this.setAddComplete()) {
         this.handler().handlerAdded(this);
      }
   }

   final void callHandlerRemoved() throws Exception {
      try {
         if (this.handlerState == 2) {
            this.handler().handlerRemoved(this);
         }
      } finally {
         this.setRemoved();
      }
   }

   boolean invokeHandler() {
      int handlerState = this.handlerState;
      return handlerState == 2 || !this.ordered && handlerState == 1;
   }

   @Override
   public boolean isRemoved() {
      return this.handlerState == 3;
   }

   @Override
   public <T> Attribute<T> attr(AttributeKey<T> key) {
      return this.channel().attr(key);
   }

   @Override
   public <T> boolean hasAttr(AttributeKey<T> key) {
      return this.channel().hasAttr(key);
   }

   private static boolean safeExecute(EventExecutor executor, Runnable runnable, ChannelPromise promise, Object msg, boolean lazy) {
      try {
         if (lazy && executor instanceof AbstractEventExecutor) {
            ((AbstractEventExecutor)executor).lazyExecute(runnable);
         } else {
            executor.execute(runnable);
         }

         return true;
      } catch (Throwable var10) {
         try {
            if (msg != null) {
               ReferenceCountUtil.release(msg);
            }
         } finally {
            promise.setFailure(var10);
         }

         return false;
      }
   }

   @Override
   public String toHintString() {
      return '\'' + this.name + "' will handle the message from this point.";
   }

   @Override
   public String toString() {
      return StringUtil.simpleClassName(ChannelHandlerContext.class) + '(' + this.name + ", " + this.channel() + ')';
   }

   AbstractChannelHandlerContext.Tasks getInvokeTasks() {
      AbstractChannelHandlerContext.Tasks tasks = this.invokeTasks;
      if (tasks == null) {
         this.invokeTasks = tasks = new AbstractChannelHandlerContext.Tasks(this);
      }

      return tasks;
   }

   static final class Tasks {
      final Runnable invokeChannelReadCompleteTask;
      private final Runnable invokeReadTask;
      private final Runnable invokeChannelWritableStateChangedTask;
      private final Runnable invokeFlushTask;

      Tasks(AbstractChannelHandlerContext ctx) {
         this.invokeChannelReadCompleteTask = ctx::fireChannelReadComplete;
         this.invokeReadTask = ctx::read;
         this.invokeChannelWritableStateChangedTask = ctx::fireChannelWritabilityChanged;
         this.invokeFlushTask = () -> ctx.invokeFlush();
      }
   }

   static final class WriteTask implements Runnable {
      private static final Recycler<AbstractChannelHandlerContext.WriteTask> RECYCLER = new Recycler<AbstractChannelHandlerContext.WriteTask>() {
         protected AbstractChannelHandlerContext.WriteTask newObject(Recycler.Handle<AbstractChannelHandlerContext.WriteTask> handle) {
            return new AbstractChannelHandlerContext.WriteTask(handle);
         }
      };
      private static final boolean ESTIMATE_TASK_SIZE_ON_SUBMIT = SystemPropertyUtil.getBoolean("io.netty.transport.estimateSizeOnSubmit", true);
      private static final int WRITE_TASK_OVERHEAD = SystemPropertyUtil.getInt("io.netty.transport.writeTaskSizeOverhead", 32);
      private final ObjectPool.Handle<AbstractChannelHandlerContext.WriteTask> handle;
      private AbstractChannelHandlerContext ctx;
      private Object msg;
      private ChannelPromise promise;
      private int size;

      static AbstractChannelHandlerContext.WriteTask newInstance(AbstractChannelHandlerContext ctx, Object msg, ChannelPromise promise, boolean flush) {
         AbstractChannelHandlerContext.WriteTask task = RECYCLER.get();
         init(task, ctx, msg, promise, flush);
         return task;
      }

      private WriteTask(ObjectPool.Handle<AbstractChannelHandlerContext.WriteTask> handle) {
         this.handle = handle;
      }

      static void init(AbstractChannelHandlerContext.WriteTask task, AbstractChannelHandlerContext ctx, Object msg, ChannelPromise promise, boolean flush) {
         task.ctx = ctx;
         task.msg = msg;
         task.promise = promise;
         if (ESTIMATE_TASK_SIZE_ON_SUBMIT) {
            task.size = ctx.pipeline.estimatorHandle().size(msg) + WRITE_TASK_OVERHEAD;
            ctx.pipeline.incrementPendingOutboundBytes(task.size);
         } else {
            task.size = 0;
         }

         if (flush) {
            task.size |= Integer.MIN_VALUE;
         }
      }

      @Override
      public void run() {
         try {
            this.decrementPendingOutboundBytes();
            this.ctx.write(this.msg, this.size < 0, this.promise);
         } finally {
            this.recycle();
         }
      }

      void cancel() {
         try {
            this.decrementPendingOutboundBytes();
         } finally {
            this.recycle();
         }
      }

      private void decrementPendingOutboundBytes() {
         if (ESTIMATE_TASK_SIZE_ON_SUBMIT) {
            this.ctx.pipeline.decrementPendingOutboundBytes(this.size & 2147483647);
         }
      }

      private void recycle() {
         this.ctx = null;
         this.msg = null;
         this.promise = null;
         this.handle.recycle(this);
      }
   }
}
