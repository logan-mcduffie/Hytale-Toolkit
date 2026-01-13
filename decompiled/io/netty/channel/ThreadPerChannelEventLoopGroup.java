package io.netty.channel;

import io.netty.util.concurrent.AbstractEventExecutorGroup;
import io.netty.util.concurrent.DefaultPromise;
import io.netty.util.concurrent.DefaultThreadFactory;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.FutureListener;
import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;
import io.netty.util.concurrent.ThreadPerTaskExecutor;
import io.netty.util.internal.EmptyArrays;
import io.netty.util.internal.ObjectUtil;
import io.netty.util.internal.ReadOnlyIterator;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;

@Deprecated
public class ThreadPerChannelEventLoopGroup extends AbstractEventExecutorGroup implements EventLoopGroup {
   private final Object[] childArgs;
   private final int maxChannels;
   final Executor executor;
   final Set<EventLoop> activeChildren = ConcurrentHashMap.newKeySet();
   final Queue<EventLoop> idleChildren = new ConcurrentLinkedQueue<>();
   private final ChannelException tooManyChannels;
   private volatile boolean shuttingDown;
   private final Promise<?> terminationFuture = new DefaultPromise(GlobalEventExecutor.INSTANCE);
   private final FutureListener<Object> childTerminationListener = new FutureListener<Object>() {
      @Override
      public void operationComplete(Future<Object> future) throws Exception {
         if (ThreadPerChannelEventLoopGroup.this.isTerminated()) {
            ThreadPerChannelEventLoopGroup.this.terminationFuture.trySuccess(null);
         }
      }
   };

   protected ThreadPerChannelEventLoopGroup() {
      this(0);
   }

   protected ThreadPerChannelEventLoopGroup(int maxChannels) {
      this(maxChannels, (ThreadFactory)null);
   }

   protected ThreadPerChannelEventLoopGroup(int maxChannels, ThreadFactory threadFactory, Object... args) {
      this(maxChannels, threadFactory == null ? null : new ThreadPerTaskExecutor(threadFactory), args);
   }

   protected ThreadPerChannelEventLoopGroup(int maxChannels, Executor executor, Object... args) {
      ObjectUtil.checkPositiveOrZero(maxChannels, "maxChannels");
      if (executor == null) {
         executor = new ThreadPerTaskExecutor(new DefaultThreadFactory(this.getClass()));
      }

      if (args == null) {
         this.childArgs = EmptyArrays.EMPTY_OBJECTS;
      } else {
         this.childArgs = (Object[])args.clone();
      }

      this.maxChannels = maxChannels;
      this.executor = executor;
      this.tooManyChannels = ChannelException.newStatic("too many channels (max: " + maxChannels + ')', ThreadPerChannelEventLoopGroup.class, "nextChild()");
   }

   protected EventLoop newChild(Object... args) throws Exception {
      return new ThreadPerChannelEventLoop(this);
   }

   @Override
   public Iterator<EventExecutor> iterator() {
      return new ReadOnlyIterator<>(this.activeChildren.iterator());
   }

   @Override
   public EventLoop next() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
      this.shuttingDown = true;

      for (EventLoop l : this.activeChildren) {
         l.shutdownGracefully(quietPeriod, timeout, unit);
      }

      for (EventLoop l : this.idleChildren) {
         l.shutdownGracefully(quietPeriod, timeout, unit);
      }

      if (this.isTerminated()) {
         this.terminationFuture.trySuccess(null);
      }

      return this.terminationFuture();
   }

   @Override
   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   @Deprecated
   @Override
   public void shutdown() {
      this.shuttingDown = true;

      for (EventLoop l : this.activeChildren) {
         l.shutdown();
      }

      for (EventLoop l : this.idleChildren) {
         l.shutdown();
      }

      if (this.isTerminated()) {
         this.terminationFuture.trySuccess(null);
      }
   }

   @Override
   public boolean isShuttingDown() {
      for (EventLoop l : this.activeChildren) {
         if (!l.isShuttingDown()) {
            return false;
         }
      }

      for (EventLoop lx : this.idleChildren) {
         if (!lx.isShuttingDown()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean isShutdown() {
      for (EventLoop l : this.activeChildren) {
         if (!l.isShutdown()) {
            return false;
         }
      }

      for (EventLoop lx : this.idleChildren) {
         if (!lx.isShutdown()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean isTerminated() {
      for (EventLoop l : this.activeChildren) {
         if (!l.isTerminated()) {
            return false;
         }
      }

      for (EventLoop lx : this.idleChildren) {
         if (!lx.isTerminated()) {
            return false;
         }
      }

      return true;
   }

   @Override
   public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      long deadline = System.nanoTime() + unit.toNanos(timeout);

      long timeLeft;
      for (EventLoop l : this.activeChildren) {
         do {
            timeLeft = deadline - System.nanoTime();
            if (timeLeft <= 0L) {
               return this.isTerminated();
            }
         } while (!l.awaitTermination(timeLeft, TimeUnit.NANOSECONDS));
      }

      for (EventLoop l : this.idleChildren) {
         do {
            timeLeft = deadline - System.nanoTime();
            if (timeLeft <= 0L) {
               return this.isTerminated();
            }
         } while (!l.awaitTermination(timeLeft, TimeUnit.NANOSECONDS));
      }

      return this.isTerminated();
   }

   @Override
   public ChannelFuture register(Channel channel) {
      ObjectUtil.checkNotNull(channel, "channel");

      try {
         EventLoop l = this.nextChild();
         return l.register(new DefaultChannelPromise(channel, l));
      } catch (Throwable var3) {
         return new FailedChannelFuture(channel, GlobalEventExecutor.INSTANCE, var3);
      }
   }

   @Override
   public ChannelFuture register(ChannelPromise promise) {
      try {
         return this.nextChild().register(promise);
      } catch (Throwable var3) {
         promise.setFailure(var3);
         return promise;
      }
   }

   @Deprecated
   @Override
   public ChannelFuture register(Channel channel, ChannelPromise promise) {
      ObjectUtil.checkNotNull(channel, "channel");

      try {
         return this.nextChild().register(channel, promise);
      } catch (Throwable var4) {
         promise.setFailure(var4);
         return promise;
      }
   }

   private EventLoop nextChild() throws Exception {
      if (this.shuttingDown) {
         throw new RejectedExecutionException("shutting down");
      } else {
         EventLoop loop = this.idleChildren.poll();
         if (loop == null) {
            if (this.maxChannels > 0 && this.activeChildren.size() >= this.maxChannels) {
               throw this.tooManyChannels;
            }

            loop = this.newChild(this.childArgs);
            loop.terminationFuture().addListener(this.childTerminationListener);
         }

         this.activeChildren.add(loop);
         return loop;
      }
   }
}
