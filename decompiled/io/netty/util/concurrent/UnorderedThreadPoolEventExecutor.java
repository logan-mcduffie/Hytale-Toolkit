package io.netty.util.concurrent;

import io.netty.util.internal.logging.InternalLogger;
import io.netty.util.internal.logging.InternalLoggerFactory;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;

@Deprecated
public final class UnorderedThreadPoolEventExecutor extends ScheduledThreadPoolExecutor implements EventExecutor {
   private static final InternalLogger logger = InternalLoggerFactory.getInstance(UnorderedThreadPoolEventExecutor.class);
   private final Promise<?> terminationFuture = GlobalEventExecutor.INSTANCE.newPromise();
   private final Set<EventExecutor> executorSet = Collections.singleton(this);
   private final Set<Thread> eventLoopThreads = ConcurrentHashMap.newKeySet();

   public UnorderedThreadPoolEventExecutor(int corePoolSize) {
      this(corePoolSize, new DefaultThreadFactory(UnorderedThreadPoolEventExecutor.class));
   }

   public UnorderedThreadPoolEventExecutor(int corePoolSize, ThreadFactory threadFactory) {
      super(corePoolSize, threadFactory);
      this.setThreadFactory(new UnorderedThreadPoolEventExecutor.AccountingThreadFactory(threadFactory, this.eventLoopThreads));
   }

   public UnorderedThreadPoolEventExecutor(int corePoolSize, java.util.concurrent.RejectedExecutionHandler handler) {
      this(corePoolSize, new DefaultThreadFactory(UnorderedThreadPoolEventExecutor.class), handler);
   }

   public UnorderedThreadPoolEventExecutor(int corePoolSize, ThreadFactory threadFactory, java.util.concurrent.RejectedExecutionHandler handler) {
      super(corePoolSize, threadFactory, handler);
      this.setThreadFactory(new UnorderedThreadPoolEventExecutor.AccountingThreadFactory(threadFactory, this.eventLoopThreads));
   }

   @Override
   public EventExecutor next() {
      return this;
   }

   @Override
   public EventExecutorGroup parent() {
      return this;
   }

   @Override
   public boolean inEventLoop() {
      return this.inEventLoop(Thread.currentThread());
   }

   @Override
   public boolean inEventLoop(Thread thread) {
      return this.eventLoopThreads.contains(thread);
   }

   @Override
   public <V> Promise<V> newPromise() {
      return new DefaultPromise<>(this);
   }

   @Override
   public <V> ProgressivePromise<V> newProgressivePromise() {
      return new DefaultProgressivePromise<>(this);
   }

   @Override
   public <V> Future<V> newSucceededFuture(V result) {
      return new SucceededFuture<>(this, result);
   }

   @Override
   public <V> Future<V> newFailedFuture(Throwable cause) {
      return new FailedFuture<>(this, cause);
   }

   @Override
   public boolean isShuttingDown() {
      return this.isShutdown();
   }

   @Override
   public List<Runnable> shutdownNow() {
      List<Runnable> tasks = super.shutdownNow();
      this.terminationFuture.trySuccess(null);
      return tasks;
   }

   @Override
   public void shutdown() {
      super.shutdown();
      this.terminationFuture.trySuccess(null);
   }

   @Override
   public Future<?> shutdownGracefully() {
      return this.shutdownGracefully(2L, 15L, TimeUnit.SECONDS);
   }

   @Override
   public Future<?> shutdownGracefully(long quietPeriod, long timeout, TimeUnit unit) {
      this.shutdown();
      return this.terminationFuture();
   }

   @Override
   public Future<?> terminationFuture() {
      return this.terminationFuture;
   }

   @Override
   public Iterator<EventExecutor> iterator() {
      return this.executorSet.iterator();
   }

   @Override
   protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable, RunnableScheduledFuture<V> task) {
      return (RunnableScheduledFuture<V>)(runnable instanceof UnorderedThreadPoolEventExecutor.NonNotifyRunnable
         ? task
         : new UnorderedThreadPoolEventExecutor.RunnableScheduledFutureTask<>(this, task, false));
   }

   @Override
   protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable, RunnableScheduledFuture<V> task) {
      return new UnorderedThreadPoolEventExecutor.RunnableScheduledFutureTask<>(this, task, true);
   }

   @Override
   public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      return (ScheduledFuture<?>)super.schedule(command, delay, unit);
   }

   @Override
   public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
      return (ScheduledFuture<V>)super.<V>schedule(callable, delay, unit);
   }

   @Override
   public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay, long period, TimeUnit unit) {
      return (ScheduledFuture<?>)super.scheduleAtFixedRate(command, initialDelay, period, unit);
   }

   @Override
   public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay, long delay, TimeUnit unit) {
      return (ScheduledFuture<?>)super.scheduleWithFixedDelay(command, initialDelay, delay, unit);
   }

   @Override
   public Future<?> submit(Runnable task) {
      return (Future<?>)super.submit(task);
   }

   @Override
   public <T> Future<T> submit(Runnable task, T result) {
      return (Future<T>)super.<T>submit(task, result);
   }

   @Override
   public <T> Future<T> submit(Callable<T> task) {
      return (Future<T>)super.<T>submit(task);
   }

   @Override
   public void execute(Runnable command) {
      super.schedule(new UnorderedThreadPoolEventExecutor.NonNotifyRunnable(command), 0L, TimeUnit.NANOSECONDS);
   }

   private static final class AccountingThreadFactory implements ThreadFactory {
      private final ThreadFactory delegate;
      private final Set<Thread> threads;

      private AccountingThreadFactory(ThreadFactory delegate, Set<Thread> threads) {
         this.delegate = delegate;
         this.threads = threads;
      }

      @Override
      public Thread newThread(@NotNull Runnable r) {
         return this.delegate.newThread(() -> {
            this.threads.add(Thread.currentThread());

            try {
               r.run();
            } finally {
               this.threads.remove(Thread.currentThread());
            }
         });
      }
   }

   private static final class NonNotifyRunnable implements Runnable {
      private final Runnable task;

      NonNotifyRunnable(Runnable task) {
         this.task = task;
      }

      @Override
      public void run() {
         this.task.run();
      }
   }

   private static final class RunnableScheduledFutureTask<V> extends PromiseTask<V> implements RunnableScheduledFuture<V>, ScheduledFuture<V> {
      private final RunnableScheduledFuture<V> future;
      private final boolean wasCallable;

      RunnableScheduledFutureTask(EventExecutor executor, RunnableScheduledFuture<V> future, boolean wasCallable) {
         super(executor, future);
         this.future = future;
         this.wasCallable = wasCallable;
      }

      @Override
      V runTask() throws Throwable {
         V result = super.runTask();
         if (result != null || !this.wasCallable) {
            return result;
         } else {
            assert this.future.isDone();

            try {
               return this.future.get();
            } catch (ExecutionException var3) {
               throw var3.getCause();
            }
         }
      }

      @Override
      public void run() {
         if (!this.isPeriodic()) {
            super.run();
         } else if (!this.isDone()) {
            try {
               this.runTask();
            } catch (Throwable var2) {
               if (!this.tryFailureInternal(var2)) {
                  UnorderedThreadPoolEventExecutor.logger.warn("Failure during execution of task", var2);
               }
            }
         }
      }

      @Override
      public boolean isPeriodic() {
         return this.future.isPeriodic();
      }

      @Override
      public long getDelay(TimeUnit unit) {
         return this.future.getDelay(unit);
      }

      public int compareTo(Delayed o) {
         return this.future.compareTo(o);
      }
   }
}
