package io.sentry.transport;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.SentryDate;
import io.sentry.SentryDateProvider;
import io.sentry.SentryLevel;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class QueuedThreadPoolExecutor extends ThreadPoolExecutor {
   private final int maxQueueSize;
   @Nullable
   private SentryDate lastRejectTimestamp = null;
   @NotNull
   private final ILogger logger;
   @NotNull
   private final SentryDateProvider dateProvider;
   @NotNull
   private final ReusableCountLatch unfinishedTasksCount = new ReusableCountLatch();
   private static final long RECENT_THRESHOLD = DateUtils.millisToNanos(2000L);

   public QueuedThreadPoolExecutor(
      int corePoolSize,
      int maxQueueSize,
      @NotNull ThreadFactory threadFactory,
      @NotNull RejectedExecutionHandler rejectedExecutionHandler,
      @NotNull ILogger logger,
      @NotNull SentryDateProvider dateProvider
   ) {
      super(corePoolSize, corePoolSize, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>(), threadFactory, rejectedExecutionHandler);
      this.maxQueueSize = maxQueueSize;
      this.logger = logger;
      this.dateProvider = dateProvider;
   }

   @Override
   public Future<?> submit(@NotNull Runnable task) {
      if (this.isSchedulingAllowed()) {
         this.unfinishedTasksCount.increment();

         try {
            return super.submit(task);
         } catch (RejectedExecutionException var3) {
            this.unfinishedTasksCount.decrement();
            this.lastRejectTimestamp = this.dateProvider.now();
            this.logger.log(SentryLevel.WARNING, "Submit rejected by thread pool executor", var3);
            return new QueuedThreadPoolExecutor.CancelledFuture();
         }
      } else {
         this.lastRejectTimestamp = this.dateProvider.now();
         this.logger.log(SentryLevel.WARNING, "Submit cancelled");
         return new QueuedThreadPoolExecutor.CancelledFuture();
      }
   }

   @Override
   protected void afterExecute(@NotNull Runnable r, @Nullable Throwable t) {
      try {
         super.afterExecute(r, t);
      } finally {
         this.unfinishedTasksCount.decrement();
      }
   }

   void waitTillIdle(long timeoutMillis) {
      try {
         this.unfinishedTasksCount.waitTillZero(timeoutMillis, TimeUnit.MILLISECONDS);
      } catch (InterruptedException var4) {
         this.logger.log(SentryLevel.ERROR, "Failed to wait till idle", var4);
         Thread.currentThread().interrupt();
      }
   }

   public boolean isSchedulingAllowed() {
      return this.unfinishedTasksCount.getCount() < this.maxQueueSize;
   }

   public boolean didRejectRecently() {
      SentryDate lastReject = this.lastRejectTimestamp;
      if (lastReject == null) {
         return false;
      } else {
         long diff = this.dateProvider.now().diff(lastReject);
         return diff < RECENT_THRESHOLD;
      }
   }

   static final class CancelledFuture<T> implements Future<T> {
      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
         return true;
      }

      @Override
      public boolean isCancelled() {
         return true;
      }

      @Override
      public boolean isDone() {
         return true;
      }

      @Override
      public T get() {
         throw new CancellationException();
      }

      @Override
      public T get(long timeout, @NotNull TimeUnit unit) {
         throw new CancellationException();
      }
   }
}
