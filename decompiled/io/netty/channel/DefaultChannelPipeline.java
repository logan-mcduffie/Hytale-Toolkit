package io.netty.channel;

import io.netty.util.ReferenceCountUtil;
import io.netty.util.ResourceLeakDetector;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorGroup;
import io.netty.util.concurrent.FastThreadLocal;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.StringUtil;
import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.WeakHashMap;
import java.util.Map.Entry;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.atomic.AtomicReferenceFieldUpdater;

public class DefaultChannelPipeline implements ChannelPipeline {
   static final InternalLogger logger = InternalLoggerFactory.getInstance(DefaultChannelPipeline.class);
   private static final String HEAD_NAME = generateName0(DefaultChannelPipeline.HeadContext.class);
   private static final String TAIL_NAME = generateName0(DefaultChannelPipeline.TailContext.class);
   private static final FastThreadLocal<Map<Class<?>, String>> nameCaches = new FastThreadLocal<Map<Class<?>, String>>() {
      protected Map<Class<?>, String> initialValue() {
         return new WeakHashMap<>();
      }
   };
   private static final AtomicReferenceFieldUpdater<DefaultChannelPipeline, MessageSizeEstimator.Handle> ESTIMATOR = AtomicReferenceFieldUpdater.newUpdater(
      DefaultChannelPipeline.class, MessageSizeEstimator.Handle.class, "estimatorHandle"
   );
   final DefaultChannelPipeline.HeadContext head;
   final DefaultChannelPipeline.TailContext tail;
   private final Channel channel;
   private final ChannelFuture succeededFuture;
   private final VoidChannelPromise voidPromise;
   private final boolean touch = ResourceLeakDetector.isEnabled();
   private Map<EventExecutorGroup, EventExecutor> childExecutors;
   private volatile MessageSizeEstimator.Handle estimatorHandle;
   private boolean firstRegistration = true;
   private DefaultChannelPipeline.PendingHandlerCallback pendingHandlerCallbackHead;
   private boolean registered;

   protected DefaultChannelPipeline(Channel channel) {
      this.channel = ObjectUtil.checkNotNull(channel, "channel");
      this.succeededFuture = new SucceededChannelFuture(channel, null);
      this.voidPromise = new VoidChannelPromise(channel, true);
      this.tail = new DefaultChannelPipeline.TailContext(this);
      this.head = new DefaultChannelPipeline.HeadContext(this);
      this.head.next = this.tail;
      this.tail.prev = this.head;
   }

   final MessageSizeEstimator.Handle estimatorHandle() {
      MessageSizeEstimator.Handle handle = this.estimatorHandle;
      if (handle == null) {
         handle = this.channel.config().getMessageSizeEstimator().newHandle();
         if (!ESTIMATOR.compareAndSet(this, null, handle)) {
            handle = this.estimatorHandle;
         }
      }

      return handle;
   }

   final Object touch(Object msg, AbstractChannelHandlerContext next) {
      return this.touch ? ReferenceCountUtil.touch(msg, next) : msg;
   }

   private AbstractChannelHandlerContext newContext(EventExecutorGroup group, String name, ChannelHandler handler) {
      return new DefaultChannelHandlerContext(this, this.childExecutor(group), name, handler);
   }

   private EventExecutor childExecutor(EventExecutorGroup group) {
      if (group == null) {
         return null;
      } else {
         Boolean pinEventExecutor = this.channel.config().getOption(ChannelOption.SINGLE_EVENTEXECUTOR_PER_GROUP);
         if (pinEventExecutor != null && !pinEventExecutor) {
            return group.next();
         } else {
            Map<EventExecutorGroup, EventExecutor> childExecutors = this.childExecutors;
            if (childExecutors == null) {
               childExecutors = this.childExecutors = new IdentityHashMap<>(4);
            }

            EventExecutor childExecutor = childExecutors.get(group);
            if (childExecutor == null) {
               childExecutor = group.next();
               childExecutors.put(group, childExecutor);
            }

            return childExecutor;
         }
      }
   }

   @Override
   public final Channel channel() {
      return this.channel;
   }

   @Override
   public final ChannelPipeline addFirst(String name, ChannelHandler handler) {
      return this.addFirst(null, name, handler);
   }

   private ChannelPipeline internalAdd(
      EventExecutorGroup group, String name, ChannelHandler handler, String baseName, DefaultChannelPipeline.AddStrategy addStrategy
   ) {
      AbstractChannelHandlerContext newCtx;
      synchronized (this) {
         checkMultiplicity(handler);
         name = this.filterName(name, handler);
         newCtx = this.newContext(group, name, handler);
         switch (addStrategy) {
            case ADD_FIRST:
               this.addFirst0(newCtx);
               break;
            case ADD_LAST:
               this.addLast0(newCtx);
               break;
            case ADD_BEFORE:
               addBefore0(this.getContextOrDie(baseName), newCtx);
               break;
            case ADD_AFTER:
               addAfter0(this.getContextOrDie(baseName), newCtx);
               break;
            default:
               throw new IllegalArgumentException("unknown add strategy: " + addStrategy);
         }

         if (!this.registered) {
            newCtx.setAddPending();
            this.callHandlerCallbackLater(newCtx, true);
            return this;
         }

         EventExecutor executor = newCtx.executor();
         if (!executor.inEventLoop()) {
            this.callHandlerAddedInEventLoop(newCtx, executor);
            return this;
         }
      }

      this.callHandlerAdded0(newCtx);
      return this;
   }

   @Override
   public final ChannelPipeline addFirst(EventExecutorGroup group, String name, ChannelHandler handler) {
      return this.internalAdd(group, name, handler, null, DefaultChannelPipeline.AddStrategy.ADD_FIRST);
   }

   private void addFirst0(AbstractChannelHandlerContext newCtx) {
      AbstractChannelHandlerContext nextCtx = this.head.next;
      newCtx.prev = this.head;
      newCtx.next = nextCtx;
      this.head.next = newCtx;
      nextCtx.prev = newCtx;
   }

   @Override
   public final ChannelPipeline addLast(String name, ChannelHandler handler) {
      return this.addLast(null, name, handler);
   }

   @Override
   public final ChannelPipeline addLast(EventExecutorGroup group, String name, ChannelHandler handler) {
      return this.internalAdd(group, name, handler, null, DefaultChannelPipeline.AddStrategy.ADD_LAST);
   }

   private void addLast0(AbstractChannelHandlerContext newCtx) {
      AbstractChannelHandlerContext prev = this.tail.prev;
      newCtx.prev = prev;
      newCtx.next = this.tail;
      prev.next = newCtx;
      this.tail.prev = newCtx;
   }

   @Override
   public final ChannelPipeline addBefore(String baseName, String name, ChannelHandler handler) {
      return this.addBefore(null, baseName, name, handler);
   }

   @Override
   public final ChannelPipeline addBefore(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
      return this.internalAdd(group, name, handler, baseName, DefaultChannelPipeline.AddStrategy.ADD_BEFORE);
   }

   private static void addBefore0(AbstractChannelHandlerContext ctx, AbstractChannelHandlerContext newCtx) {
      newCtx.prev = ctx.prev;
      newCtx.next = ctx;
      ctx.prev.next = newCtx;
      ctx.prev = newCtx;
   }

   private String filterName(String name, ChannelHandler handler) {
      if (name == null) {
         return this.generateName(handler);
      } else {
         this.checkDuplicateName(name);
         return name;
      }
   }

   @Override
   public final ChannelPipeline addAfter(String baseName, String name, ChannelHandler handler) {
      return this.addAfter(null, baseName, name, handler);
   }

   @Override
   public final ChannelPipeline addAfter(EventExecutorGroup group, String baseName, String name, ChannelHandler handler) {
      return this.internalAdd(group, name, handler, baseName, DefaultChannelPipeline.AddStrategy.ADD_AFTER);
   }

   private static void addAfter0(AbstractChannelHandlerContext ctx, AbstractChannelHandlerContext newCtx) {
      newCtx.prev = ctx;
      newCtx.next = ctx.next;
      ctx.next.prev = newCtx;
      ctx.next = newCtx;
   }

   public final ChannelPipeline addFirst(ChannelHandler handler) {
      return this.addFirst(null, handler);
   }

   @Override
   public final ChannelPipeline addFirst(ChannelHandler... handlers) {
      return this.addFirst(null, handlers);
   }

   @Override
   public final ChannelPipeline addFirst(EventExecutorGroup executor, ChannelHandler... handlers) {
      ObjectUtil.checkNotNull(handlers, "handlers");
      if (handlers.length != 0 && handlers[0] != null) {
         int size = 1;

         while (size < handlers.length && handlers[size] != null) {
            size++;
         }

         for (int i = size - 1; i >= 0; i--) {
            ChannelHandler h = handlers[i];
            this.addFirst(executor, null, h);
         }

         return this;
      } else {
         return this;
      }
   }

   public final ChannelPipeline addLast(ChannelHandler handler) {
      return this.addLast(null, handler);
   }

   @Override
   public final ChannelPipeline addLast(ChannelHandler... handlers) {
      return this.addLast(null, handlers);
   }

   @Override
   public final ChannelPipeline addLast(EventExecutorGroup executor, ChannelHandler... handlers) {
      ObjectUtil.checkNotNull(handlers, "handlers");

      for (ChannelHandler h : handlers) {
         if (h == null) {
            break;
         }

         this.addLast(executor, null, h);
      }

      return this;
   }

   private String generateName(ChannelHandler handler) {
      Map<Class<?>, String> cache = nameCaches.get();
      Class<?> handlerType = handler.getClass();
      String name = cache.get(handlerType);
      if (name == null) {
         name = generateName0(handlerType);
         cache.put(handlerType, name);
      }

      if (this.context0(name) != null) {
         String baseName = name.substring(0, name.length() - 1);
         int i = 1;

         while (true) {
            String newName = baseName + i;
            if (this.context0(newName) == null) {
               name = newName;
               break;
            }

            i++;
         }
      }

      return name;
   }

   private static String generateName0(Class<?> handlerType) {
      return StringUtil.simpleClassName(handlerType) + "#0";
   }

   @Override
   public final ChannelPipeline remove(ChannelHandler handler) {
      this.remove(this.getContextOrDie(handler));
      return this;
   }

   @Override
   public final ChannelHandler remove(String name) {
      return this.remove(this.getContextOrDie(name)).handler();
   }

   @Override
   public final <T extends ChannelHandler> T remove(Class<T> handlerType) {
      return (T)this.remove(this.getContextOrDie(handlerType)).handler();
   }

   public final <T extends ChannelHandler> T removeIfExists(String name) {
      return this.removeIfExists(this.context(name));
   }

   public final <T extends ChannelHandler> T removeIfExists(Class<T> handlerType) {
      return this.removeIfExists(this.context(handlerType));
   }

   public final <T extends ChannelHandler> T removeIfExists(ChannelHandler handler) {
      return this.removeIfExists(this.context(handler));
   }

   private <T extends ChannelHandler> T removeIfExists(ChannelHandlerContext ctx) {
      return (T)(ctx == null ? null : this.remove((AbstractChannelHandlerContext)ctx).handler());
   }

   private AbstractChannelHandlerContext remove(final AbstractChannelHandlerContext ctx) {
      assert ctx != this.head && ctx != this.tail;

      synchronized (this) {
         this.atomicRemoveFromHandlerList(ctx);
         if (!this.registered) {
            this.callHandlerCallbackLater(ctx, false);
            return ctx;
         }

         EventExecutor executor = ctx.executor();
         if (!executor.inEventLoop()) {
            executor.execute(new Runnable() {
               @Override
               public void run() {
                  DefaultChannelPipeline.this.callHandlerRemoved0(ctx);
               }
            });
            return ctx;
         }
      }

      this.callHandlerRemoved0(ctx);
      return ctx;
   }

   private synchronized void atomicRemoveFromHandlerList(AbstractChannelHandlerContext ctx) {
      AbstractChannelHandlerContext prev = ctx.prev;
      AbstractChannelHandlerContext next = ctx.next;
      prev.next = next;
      next.prev = prev;
   }

   @Override
   public final ChannelHandler removeFirst() {
      if (this.head.next == this.tail) {
         throw new NoSuchElementException();
      } else {
         return this.remove(this.head.next).handler();
      }
   }

   @Override
   public final ChannelHandler removeLast() {
      if (this.head.next == this.tail) {
         throw new NoSuchElementException();
      } else {
         return this.remove(this.tail.prev).handler();
      }
   }

   @Override
   public final ChannelPipeline replace(ChannelHandler oldHandler, String newName, ChannelHandler newHandler) {
      this.replace(this.getContextOrDie(oldHandler), newName, newHandler);
      return this;
   }

   @Override
   public final ChannelHandler replace(String oldName, String newName, ChannelHandler newHandler) {
      return this.replace(this.getContextOrDie(oldName), newName, newHandler);
   }

   @Override
   public final <T extends ChannelHandler> T replace(Class<T> oldHandlerType, String newName, ChannelHandler newHandler) {
      return (T)this.replace(this.getContextOrDie(oldHandlerType), newName, newHandler);
   }

   private ChannelHandler replace(final AbstractChannelHandlerContext ctx, String newName, ChannelHandler newHandler) {
      assert ctx != this.head && ctx != this.tail;

      final AbstractChannelHandlerContext newCtx;
      synchronized (this) {
         checkMultiplicity(newHandler);
         if (newName == null) {
            newName = this.generateName(newHandler);
         } else {
            boolean sameName = ctx.name().equals(newName);
            if (!sameName) {
               this.checkDuplicateName(newName);
            }
         }

         newCtx = this.newContext(ctx.childExecutor, newName, newHandler);
         replace0(ctx, newCtx);
         if (!this.registered) {
            this.callHandlerCallbackLater(newCtx, true);
            this.callHandlerCallbackLater(ctx, false);
            return ctx.handler();
         }

         EventExecutor executor = ctx.executor();
         if (!executor.inEventLoop()) {
            executor.execute(new Runnable() {
               @Override
               public void run() {
                  DefaultChannelPipeline.this.callHandlerAdded0(newCtx);
                  DefaultChannelPipeline.this.callHandlerRemoved0(ctx);
               }
            });
            return ctx.handler();
         }
      }

      this.callHandlerAdded0(newCtx);
      this.callHandlerRemoved0(ctx);
      return ctx.handler();
   }

   private static void replace0(AbstractChannelHandlerContext oldCtx, AbstractChannelHandlerContext newCtx) {
      AbstractChannelHandlerContext prev = oldCtx.prev;
      AbstractChannelHandlerContext next = oldCtx.next;
      newCtx.prev = prev;
      newCtx.next = next;
      prev.next = newCtx;
      next.prev = newCtx;
      oldCtx.prev = newCtx;
      oldCtx.next = newCtx;
   }

   private static void checkMultiplicity(ChannelHandler handler) {
      if (handler instanceof ChannelHandlerAdapter) {
         ChannelHandlerAdapter h = (ChannelHandlerAdapter)handler;
         if (!h.isSharable() && h.added) {
            throw new ChannelPipelineException(h.getClass().getName() + " is not a @Sharable handler, so can't be added or removed multiple times.");
         }

         h.added = true;
      }
   }

   private void callHandlerAdded0(AbstractChannelHandlerContext ctx) {
      try {
         ctx.callHandlerAdded();
      } catch (Throwable var6) {
         boolean removed = false;

         try {
            this.atomicRemoveFromHandlerList(ctx);
            ctx.callHandlerRemoved();
            removed = true;
         } catch (Throwable var5) {
            if (logger.isWarnEnabled()) {
               logger.warn("Failed to remove a handler: " + ctx.name(), var5);
            }
         }

         if (removed) {
            this.fireExceptionCaught(
               new ChannelPipelineException(ctx.handler().getClass().getName() + ".handlerAdded() has thrown an exception; removed.", var6)
            );
         } else {
            this.fireExceptionCaught(
               new ChannelPipelineException(ctx.handler().getClass().getName() + ".handlerAdded() has thrown an exception; also failed to remove.", var6)
            );
         }
      }
   }

   private void callHandlerRemoved0(AbstractChannelHandlerContext ctx) {
      try {
         ctx.callHandlerRemoved();
      } catch (Throwable var3) {
         this.fireExceptionCaught(new ChannelPipelineException(ctx.handler().getClass().getName() + ".handlerRemoved() has thrown an exception.", var3));
      }
   }

   final void invokeHandlerAddedIfNeeded() {
      assert this.channel.eventLoop().inEventLoop();

      if (this.firstRegistration) {
         this.firstRegistration = false;
         this.callHandlerAddedForAllHandlers();
      }
   }

   @Override
   public final ChannelHandler first() {
      ChannelHandlerContext first = this.firstContext();
      return first == null ? null : first.handler();
   }

   @Override
   public final ChannelHandlerContext firstContext() {
      AbstractChannelHandlerContext first = this.head.next;
      return first == this.tail ? null : this.head.next;
   }

   @Override
   public final ChannelHandler last() {
      AbstractChannelHandlerContext last = this.tail.prev;
      return last == this.head ? null : last.handler();
   }

   @Override
   public final ChannelHandlerContext lastContext() {
      AbstractChannelHandlerContext last = this.tail.prev;
      return last == this.head ? null : last;
   }

   @Override
   public final ChannelHandler get(String name) {
      ChannelHandlerContext ctx = this.context(name);
      return ctx == null ? null : ctx.handler();
   }

   @Override
   public final <T extends ChannelHandler> T get(Class<T> handlerType) {
      ChannelHandlerContext ctx = this.context(handlerType);
      return (T)(ctx == null ? null : ctx.handler());
   }

   @Override
   public final ChannelHandlerContext context(String name) {
      return this.context0(ObjectUtil.checkNotNull(name, "name"));
   }

   @Override
   public final ChannelHandlerContext context(ChannelHandler handler) {
      ObjectUtil.checkNotNull(handler, "handler");

      for (AbstractChannelHandlerContext ctx = this.head.next; ctx != null; ctx = ctx.next) {
         if (ctx.handler() == handler) {
            return ctx;
         }
      }

      return null;
   }

   @Override
   public final ChannelHandlerContext context(Class<? extends ChannelHandler> handlerType) {
      ObjectUtil.checkNotNull(handlerType, "handlerType");

      for (AbstractChannelHandlerContext ctx = this.head.next; ctx != null; ctx = ctx.next) {
         if (handlerType.isAssignableFrom(ctx.handler().getClass())) {
            return ctx;
         }
      }

      return null;
   }

   @Override
   public final List<String> names() {
      List<String> list = new ArrayList<>();

      for (AbstractChannelHandlerContext ctx = this.head.next; ctx != null; ctx = ctx.next) {
         list.add(ctx.name());
      }

      return list;
   }

   @Override
   public final Map<String, ChannelHandler> toMap() {
      Map<String, ChannelHandler> map = new LinkedHashMap<>();

      for (AbstractChannelHandlerContext ctx = this.head.next; ctx != this.tail; ctx = ctx.next) {
         map.put(ctx.name(), ctx.handler());
      }

      return map;
   }

   @Override
   public final Iterator<Entry<String, ChannelHandler>> iterator() {
      return this.toMap().entrySet().iterator();
   }

   @Override
   public final String toString() {
      StringBuilder buf = new StringBuilder().append(StringUtil.simpleClassName(this)).append('{');
      AbstractChannelHandlerContext ctx = this.head.next;

      while (ctx != this.tail) {
         buf.append('(').append(ctx.name()).append(" = ").append(ctx.handler().getClass().getName()).append(')');
         ctx = ctx.next;
         if (ctx == this.tail) {
            break;
         }

         buf.append(", ");
      }

      buf.append('}');
      return buf.toString();
   }

   @Override
   public final ChannelPipeline fireChannelRegistered() {
      if (this.head.executor().inEventLoop()) {
         if (this.head.invokeHandler()) {
            this.head.channelRegistered(this.head);
         } else {
            this.head.fireChannelRegistered();
         }
      } else {
         this.head.executor().execute(this::fireChannelRegistered);
      }

      return this;
   }

   @Override
   public final ChannelPipeline fireChannelUnregistered() {
      if (this.head.executor().inEventLoop()) {
         if (this.head.invokeHandler()) {
            this.head.channelUnregistered(this.head);
         } else {
            this.head.fireChannelUnregistered();
         }
      } else {
         this.head.executor().execute(this::fireChannelUnregistered);
      }

      return this;
   }

   private synchronized void destroy() {
      this.destroyUp(this.head.next, false);
   }

   private void destroyUp(AbstractChannelHandlerContext ctx, boolean inEventLoop) {
      Thread currentThread = Thread.currentThread();
      AbstractChannelHandlerContext tail = this.tail;

      while (true) {
         if (ctx == tail) {
            this.destroyDown(currentThread, tail.prev, inEventLoop);
            break;
         }

         EventExecutor executor = ctx.executor();
         if (!inEventLoop && !executor.inEventLoop(currentThread)) {
            final AbstractChannelHandlerContext finalCtx = ctx;
            executor.execute(new Runnable() {
               @Override
               public void run() {
                  DefaultChannelPipeline.this.destroyUp(finalCtx, true);
               }
            });
            break;
         }

         ctx = ctx.next;
         inEventLoop = false;
      }
   }

   private void destroyDown(Thread currentThread, AbstractChannelHandlerContext ctx, boolean inEventLoop) {
      AbstractChannelHandlerContext head = this.head;

      while (ctx != head) {
         EventExecutor executor = ctx.executor();
         if (!inEventLoop && !executor.inEventLoop(currentThread)) {
            final AbstractChannelHandlerContext finalCtx = ctx;
            executor.execute(new Runnable() {
               @Override
               public void run() {
                  DefaultChannelPipeline.this.destroyDown(Thread.currentThread(), finalCtx, true);
               }
            });
            break;
         }

         this.atomicRemoveFromHandlerList(ctx);
         this.callHandlerRemoved0(ctx);
         ctx = ctx.prev;
         inEventLoop = false;
      }
   }

   @Override
   public final ChannelPipeline fireChannelActive() {
      if (this.head.executor().inEventLoop()) {
         if (this.head.invokeHandler()) {
            this.head.channelActive(this.head);
         } else {
            this.head.fireChannelActive();
         }
      } else {
         this.head.executor().execute(this::fireChannelActive);
      }

      return this;
   }

   @Override
   public final ChannelPipeline fireChannelInactive() {
      if (this.head.executor().inEventLoop()) {
         if (this.head.invokeHandler()) {
            this.head.channelInactive(this.head);
         } else {
            this.head.fireChannelInactive();
         }
      } else {
         this.head.executor().execute(this::fireChannelInactive);
      }

      return this;
   }

   @Override
   public final ChannelPipeline fireExceptionCaught(Throwable cause) {
      if (this.head.executor().inEventLoop()) {
         if (this.head.invokeHandler()) {
            this.head.exceptionCaught(this.head, cause);
         } else {
            this.head.fireExceptionCaught(cause);
         }
      } else {
         this.head.executor().execute(() -> this.fireExceptionCaught(cause));
      }

      return this;
   }

   @Override
   public final ChannelPipeline fireUserEventTriggered(Object event) {
      if (this.head.executor().inEventLoop()) {
         if (this.head.invokeHandler()) {
            this.head.userEventTriggered(this.head, event);
         } else {
            this.head.fireUserEventTriggered(event);
         }
      } else {
         this.head.executor().execute(() -> this.fireUserEventTriggered(event));
      }

      return this;
   }

   @Override
   public final ChannelPipeline fireChannelRead(Object msg) {
      if (this.head.executor().inEventLoop()) {
         if (this.head.invokeHandler()) {
            this.head.channelRead(this.head, msg);
         } else {
            this.head.fireChannelRead(msg);
         }
      } else {
         this.head.executor().execute(() -> this.fireChannelRead(msg));
      }

      return this;
   }

   @Override
   public final ChannelPipeline fireChannelReadComplete() {
      if (this.head.executor().inEventLoop()) {
         if (this.head.invokeHandler()) {
            this.head.channelReadComplete(this.head);
         } else {
            this.head.fireChannelReadComplete();
         }
      } else {
         this.head.executor().execute(this::fireChannelReadComplete);
      }

      return this;
   }

   @Override
   public final ChannelPipeline fireChannelWritabilityChanged() {
      if (this.head.executor().inEventLoop()) {
         if (this.head.invokeHandler()) {
            this.head.channelWritabilityChanged(this.head);
         } else {
            this.head.fireChannelWritabilityChanged();
         }
      } else {
         this.head.executor().execute(this::fireChannelWritabilityChanged);
      }

      return this;
   }

   @Override
   public final ChannelFuture bind(SocketAddress localAddress) {
      return this.tail.bind(localAddress);
   }

   @Override
   public final ChannelFuture connect(SocketAddress remoteAddress) {
      return this.tail.connect(remoteAddress);
   }

   @Override
   public final ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress) {
      return this.tail.connect(remoteAddress, localAddress);
   }

   @Override
   public final ChannelFuture disconnect() {
      return this.tail.disconnect();
   }

   @Override
   public final ChannelFuture close() {
      return this.tail.close();
   }

   @Override
   public final ChannelFuture deregister() {
      return this.tail.deregister();
   }

   @Override
   public final ChannelPipeline flush() {
      this.tail.flush();
      return this;
   }

   @Override
   public final ChannelFuture bind(SocketAddress localAddress, ChannelPromise promise) {
      return this.tail.bind(localAddress, promise);
   }

   @Override
   public final ChannelFuture connect(SocketAddress remoteAddress, ChannelPromise promise) {
      return this.tail.connect(remoteAddress, promise);
   }

   @Override
   public final ChannelFuture connect(SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
      return this.tail.connect(remoteAddress, localAddress, promise);
   }

   @Override
   public final ChannelFuture disconnect(ChannelPromise promise) {
      return this.tail.disconnect(promise);
   }

   @Override
   public final ChannelFuture close(ChannelPromise promise) {
      return this.tail.close(promise);
   }

   @Override
   public final ChannelFuture deregister(ChannelPromise promise) {
      return this.tail.deregister(promise);
   }

   public final ChannelPipeline read() {
      this.tail.read();
      return this;
   }

   @Override
   public final ChannelFuture write(Object msg) {
      return this.tail.write(msg);
   }

   @Override
   public final ChannelFuture write(Object msg, ChannelPromise promise) {
      return this.tail.write(msg, promise);
   }

   @Override
   public final ChannelFuture writeAndFlush(Object msg, ChannelPromise promise) {
      return this.tail.writeAndFlush(msg, promise);
   }

   @Override
   public final ChannelFuture writeAndFlush(Object msg) {
      return this.tail.writeAndFlush(msg);
   }

   @Override
   public final ChannelPromise newPromise() {
      return new DefaultChannelPromise(this.channel);
   }

   @Override
   public final ChannelProgressivePromise newProgressivePromise() {
      return new DefaultChannelProgressivePromise(this.channel);
   }

   @Override
   public final ChannelFuture newSucceededFuture() {
      return this.succeededFuture;
   }

   @Override
   public final ChannelFuture newFailedFuture(Throwable cause) {
      return new FailedChannelFuture(this.channel, null, cause);
   }

   @Override
   public final ChannelPromise voidPromise() {
      return this.voidPromise;
   }

   private void checkDuplicateName(String name) {
      if (this.context0(name) != null) {
         throw new IllegalArgumentException("Duplicate handler name: " + name);
      }
   }

   private AbstractChannelHandlerContext context0(String name) {
      for (AbstractChannelHandlerContext context = this.head.next; context != this.tail; context = context.next) {
         if (context.name().equals(name)) {
            return context;
         }
      }

      return null;
   }

   private AbstractChannelHandlerContext getContextOrDie(String name) {
      AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context(name);
      if (ctx == null) {
         throw new NoSuchElementException(name);
      } else {
         return ctx;
      }
   }

   private AbstractChannelHandlerContext getContextOrDie(ChannelHandler handler) {
      AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context(handler);
      if (ctx == null) {
         throw new NoSuchElementException(handler.getClass().getName());
      } else {
         return ctx;
      }
   }

   private AbstractChannelHandlerContext getContextOrDie(Class<? extends ChannelHandler> handlerType) {
      AbstractChannelHandlerContext ctx = (AbstractChannelHandlerContext)this.context(handlerType);
      if (ctx == null) {
         throw new NoSuchElementException(handlerType.getName());
      } else {
         return ctx;
      }
   }

   private void callHandlerAddedForAllHandlers() {
      DefaultChannelPipeline.PendingHandlerCallback pendingHandlerCallbackHead;
      synchronized (this) {
         assert !this.registered;

         this.registered = true;
         pendingHandlerCallbackHead = this.pendingHandlerCallbackHead;
         this.pendingHandlerCallbackHead = null;
      }

      for (DefaultChannelPipeline.PendingHandlerCallback task = pendingHandlerCallbackHead; task != null; task = task.next) {
         task.execute();
      }
   }

   private void callHandlerCallbackLater(AbstractChannelHandlerContext ctx, boolean added) {
      assert !this.registered;

      DefaultChannelPipeline.PendingHandlerCallback task = (DefaultChannelPipeline.PendingHandlerCallback)(added
         ? new DefaultChannelPipeline.PendingHandlerAddedTask(ctx)
         : new DefaultChannelPipeline.PendingHandlerRemovedTask(ctx));
      DefaultChannelPipeline.PendingHandlerCallback pending = this.pendingHandlerCallbackHead;
      if (pending == null) {
         this.pendingHandlerCallbackHead = task;
      } else {
         while (pending.next != null) {
            pending = pending.next;
         }

         pending.next = task;
      }
   }

   private void callHandlerAddedInEventLoop(final AbstractChannelHandlerContext newCtx, EventExecutor executor) {
      newCtx.setAddPending();
      executor.execute(new Runnable() {
         @Override
         public void run() {
            DefaultChannelPipeline.this.callHandlerAdded0(newCtx);
         }
      });
   }

   protected void onUnhandledInboundException(Throwable cause) {
      try {
         logger.warn(
            "An exceptionCaught() event was fired, and it reached at the tail of the pipeline. It usually means the last handler in the pipeline did not handle the exception.",
            cause
         );
      } finally {
         ReferenceCountUtil.release(cause);
      }
   }

   protected void onUnhandledInboundChannelActive() {
   }

   protected void onUnhandledInboundChannelInactive() {
   }

   protected void onUnhandledInboundMessage(Object msg) {
      try {
         logger.debug("Discarded inbound message {} that reached at the tail of the pipeline. Please check your pipeline configuration.", msg);
      } finally {
         ReferenceCountUtil.release(msg);
      }
   }

   protected void onUnhandledInboundMessage(ChannelHandlerContext ctx, Object msg) {
      this.onUnhandledInboundMessage(msg);
      if (logger.isDebugEnabled()) {
         logger.debug("Discarded message pipeline : {}. Channel : {}.", ctx.pipeline().names(), ctx.channel());
      }
   }

   protected void onUnhandledInboundChannelReadComplete() {
   }

   protected void onUnhandledInboundUserEventTriggered(Object evt) {
      ReferenceCountUtil.release(evt);
   }

   protected void onUnhandledChannelWritabilityChanged() {
   }

   protected void incrementPendingOutboundBytes(long size) {
      ChannelOutboundBuffer buffer = this.channel.unsafe().outboundBuffer();
      if (buffer != null) {
         buffer.incrementPendingOutboundBytes(size);
      }
   }

   protected void decrementPendingOutboundBytes(long size) {
      ChannelOutboundBuffer buffer = this.channel.unsafe().outboundBuffer();
      if (buffer != null) {
         buffer.decrementPendingOutboundBytes(size);
      }
   }

   private static enum AddStrategy {
      ADD_FIRST,
      ADD_LAST,
      ADD_BEFORE,
      ADD_AFTER;
   }

   final class HeadContext extends AbstractChannelHandlerContext implements ChannelOutboundHandler, ChannelInboundHandler {
      private final Channel.Unsafe unsafe;

      HeadContext(DefaultChannelPipeline pipeline) {
         super(pipeline, null, DefaultChannelPipeline.HEAD_NAME, DefaultChannelPipeline.HeadContext.class);
         this.unsafe = pipeline.channel().unsafe();
         this.setAddComplete();
      }

      @Override
      public ChannelHandler handler() {
         return this;
      }

      @Override
      public void handlerAdded(ChannelHandlerContext ctx) {
      }

      @Override
      public void handlerRemoved(ChannelHandlerContext ctx) {
      }

      @Override
      public void bind(ChannelHandlerContext ctx, SocketAddress localAddress, ChannelPromise promise) {
         this.unsafe.bind(localAddress, promise);
      }

      @Override
      public void connect(ChannelHandlerContext ctx, SocketAddress remoteAddress, SocketAddress localAddress, ChannelPromise promise) {
         this.unsafe.connect(remoteAddress, localAddress, promise);
      }

      @Override
      public void disconnect(ChannelHandlerContext ctx, ChannelPromise promise) {
         this.unsafe.disconnect(promise);
      }

      @Override
      public void close(ChannelHandlerContext ctx, ChannelPromise promise) {
         this.unsafe.close(promise);
      }

      @Override
      public void deregister(ChannelHandlerContext ctx, ChannelPromise promise) {
         this.unsafe.deregister(promise);
      }

      @Override
      public void read(ChannelHandlerContext ctx) {
         this.unsafe.beginRead();
      }

      @Override
      public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) {
         this.unsafe.write(msg, promise);
      }

      @Override
      public void flush(ChannelHandlerContext ctx) {
         this.unsafe.flush();
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
         ctx.fireExceptionCaught(cause);
      }

      @Override
      public void channelRegistered(ChannelHandlerContext ctx) {
         DefaultChannelPipeline.this.invokeHandlerAddedIfNeeded();
         ctx.fireChannelRegistered();
      }

      @Override
      public void channelUnregistered(ChannelHandlerContext ctx) {
         ctx.fireChannelUnregistered();
         if (!DefaultChannelPipeline.this.channel.isOpen()) {
            DefaultChannelPipeline.this.destroy();
         }
      }

      @Override
      public void channelActive(ChannelHandlerContext ctx) {
         ctx.fireChannelActive();
         this.readIfIsAutoRead();
      }

      @Override
      public void channelInactive(ChannelHandlerContext ctx) {
         ctx.fireChannelInactive();
      }

      @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) {
         ctx.fireChannelRead(msg);
      }

      @Override
      public void channelReadComplete(ChannelHandlerContext ctx) {
         ctx.fireChannelReadComplete();
         this.readIfIsAutoRead();
      }

      private void readIfIsAutoRead() {
         if (DefaultChannelPipeline.this.channel.config().isAutoRead()) {
            DefaultChannelPipeline.this.channel.read();
         }
      }

      @Override
      public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
         ctx.fireUserEventTriggered(evt);
      }

      @Override
      public void channelWritabilityChanged(ChannelHandlerContext ctx) {
         ctx.fireChannelWritabilityChanged();
      }
   }

   private final class PendingHandlerAddedTask extends DefaultChannelPipeline.PendingHandlerCallback {
      PendingHandlerAddedTask(AbstractChannelHandlerContext ctx) {
         super(ctx);
      }

      @Override
      public void run() {
         DefaultChannelPipeline.this.callHandlerAdded0(this.ctx);
      }

      @Override
      void execute() {
         EventExecutor executor = this.ctx.executor();
         if (executor.inEventLoop()) {
            DefaultChannelPipeline.this.callHandlerAdded0(this.ctx);
         } else {
            try {
               executor.execute(this);
            } catch (RejectedExecutionException var3) {
               if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                  DefaultChannelPipeline.logger
                     .warn("Can't invoke handlerAdded() as the EventExecutor {} rejected it, removing handler {}.", executor, this.ctx.name(), var3);
               }

               DefaultChannelPipeline.this.atomicRemoveFromHandlerList(this.ctx);
               this.ctx.setRemoved();
            }
         }
      }
   }

   private abstract static class PendingHandlerCallback implements Runnable {
      final AbstractChannelHandlerContext ctx;
      DefaultChannelPipeline.PendingHandlerCallback next;

      PendingHandlerCallback(AbstractChannelHandlerContext ctx) {
         this.ctx = ctx;
      }

      abstract void execute();
   }

   private final class PendingHandlerRemovedTask extends DefaultChannelPipeline.PendingHandlerCallback {
      PendingHandlerRemovedTask(AbstractChannelHandlerContext ctx) {
         super(ctx);
      }

      @Override
      public void run() {
         DefaultChannelPipeline.this.callHandlerRemoved0(this.ctx);
      }

      @Override
      void execute() {
         EventExecutor executor = this.ctx.executor();
         if (executor.inEventLoop()) {
            DefaultChannelPipeline.this.callHandlerRemoved0(this.ctx);
         } else {
            try {
               executor.execute(this);
            } catch (RejectedExecutionException var3) {
               if (DefaultChannelPipeline.logger.isWarnEnabled()) {
                  DefaultChannelPipeline.logger
                     .warn("Can't invoke handlerRemoved() as the EventExecutor {} rejected it, removing handler {}.", executor, this.ctx.name(), var3);
               }

               this.ctx.setRemoved();
            }
         }
      }
   }

   final class TailContext extends AbstractChannelHandlerContext implements ChannelInboundHandler {
      TailContext(DefaultChannelPipeline pipeline) {
         super(pipeline, null, DefaultChannelPipeline.TAIL_NAME, DefaultChannelPipeline.TailContext.class);
         this.setAddComplete();
      }

      @Override
      public ChannelHandler handler() {
         return this;
      }

      @Override
      public void channelRegistered(ChannelHandlerContext ctx) {
      }

      @Override
      public void channelUnregistered(ChannelHandlerContext ctx) {
      }

      @Override
      public void channelActive(ChannelHandlerContext ctx) {
         DefaultChannelPipeline.this.onUnhandledInboundChannelActive();
      }

      @Override
      public void channelInactive(ChannelHandlerContext ctx) {
         DefaultChannelPipeline.this.onUnhandledInboundChannelInactive();
      }

      @Override
      public void channelWritabilityChanged(ChannelHandlerContext ctx) {
         DefaultChannelPipeline.this.onUnhandledChannelWritabilityChanged();
      }

      @Override
      public void handlerAdded(ChannelHandlerContext ctx) {
      }

      @Override
      public void handlerRemoved(ChannelHandlerContext ctx) {
      }

      @Override
      public void userEventTriggered(ChannelHandlerContext ctx, Object evt) {
         DefaultChannelPipeline.this.onUnhandledInboundUserEventTriggered(evt);
      }

      @Override
      public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
         DefaultChannelPipeline.this.onUnhandledInboundException(cause);
      }

      @Override
      public void channelRead(ChannelHandlerContext ctx, Object msg) {
         DefaultChannelPipeline.this.onUnhandledInboundMessage(ctx, msg);
      }

      @Override
      public void channelReadComplete(ChannelHandlerContext ctx) {
         DefaultChannelPipeline.this.onUnhandledInboundChannelReadComplete();
      }
   }
}
