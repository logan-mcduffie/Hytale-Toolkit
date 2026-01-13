package io.sentry;

import io.sentry.util.AutoClosableReentrantLock;
import java.util.concurrent.Callable;
import java.util.concurrent.CancellationException;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryExecutorService implements ISentryExecutorService {
   private static final int INITIAL_QUEUE_SIZE = 40;
   private static final int MAX_QUEUE_SIZE = 271;
   @NotNull
   private final ScheduledThreadPoolExecutor executorService;
   @NotNull
   private final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();
   @NotNull
   private final Runnable dummyRunnable = () -> {};
   @Nullable
   private final SentryOptions options;

   @TestOnly
   SentryExecutorService(@NotNull ScheduledThreadPoolExecutor executorService, @Nullable SentryOptions options) {
      this.executorService = executorService;
      this.options = options;
   }

   public SentryExecutorService(@Nullable SentryOptions options) {
      this(new ScheduledThreadPoolExecutor(1, new SentryExecutorService.SentryExecutorServiceThreadFactory()), options);
   }

   public SentryExecutorService() {
      this(new ScheduledThreadPoolExecutor(1, new SentryExecutorService.SentryExecutorServiceThreadFactory()), null);
   }

   private boolean isQueueAvailable() {
      if (this.executorService.getQueue().size() >= 271) {
         this.executorService.purge();
      }

      return this.executorService.getQueue().size() < 271;
   }

   @NotNull
   @Override
   public Future<?> submit(@NotNull Runnable runnable) throws RejectedExecutionException {
      if (this.isQueueAvailable()) {
         return this.executorService.submit(runnable);
      } else {
         if (this.options != null) {
            this.options.getLogger().log(SentryLevel.WARNING, "Task " + runnable + " rejected from " + this.executorService);
         }

         return new SentryExecutorService.CancelledFuture();
      }
   }

   @NotNull
   @Override
   public <T> Future<T> submit(@NotNull Callable<T> callable) throws RejectedExecutionException {
      if (this.isQueueAvailable()) {
         return this.executorService.submit(callable);
      } else {
         if (this.options != null) {
            this.options.getLogger().log(SentryLevel.WARNING, "Task " + callable + " rejected from " + this.executorService);
         }

         return new SentryExecutorService.CancelledFuture<>();
      }
   }

   @NotNull
   @Override
   public Future<?> schedule(@NotNull Runnable runnable, long delayMillis) throws RejectedExecutionException {
      return this.executorService.schedule(runnable, delayMillis, TimeUnit.MILLISECONDS);
   }

   @Override
   public void close(long timeoutMillis) {
      ISentryLifecycleToken ignored = this.lock.acquire();

      try {
         if (!this.executorService.isShutdown()) {
            this.executorService.shutdown();

            try {
               if (!this.executorService.awaitTermination(timeoutMillis, TimeUnit.MILLISECONDS)) {
                  this.executorService.shutdownNow();
               }
            } catch (InterruptedException var7) {
               this.executorService.shutdownNow();
               Thread.currentThread().interrupt();
            }
         }
      } catch (Throwable var8) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var6) {
               var8.addSuppressed(var6);
            }
         }

         throw var8;
      }

      if (ignored != null) {
         ignored.close();
      }
   }

   @Override
   public boolean isClosed() {
      ISentryLifecycleToken ignored = this.lock.acquire();

      boolean var2;
      try {
         var2 = this.executorService.isShutdown();
      } catch (Throwable var5) {
         if (ignored != null) {
            try {
               ignored.close();
            } catch (Throwable var4) {
               var5.addSuppressed(var4);
            }
         }

         throw var5;
      }

      if (ignored != null) {
         ignored.close();
      }

      return var2;
   }

   @Override
   public void prewarm() {
      try {
         this.executorService.submit(() -> {
            try {
               for (int i = 0; i < 40; i++) {
                  Future<?> future = this.executorService.schedule(this.dummyRunnable, 365L, TimeUnit.DAYS);
                  future.cancel(true);
               }

               this.executorService.purge();
            } catch (RejectedExecutionException var3) {
            }
         });
      } catch (RejectedExecutionException var2) {
         if (this.options != null) {
            this.options.getLogger().log(SentryLevel.WARNING, "Prewarm task rejected from " + this.executorService, var2);
         }
      }
   }

   private static final class CancelledFuture<T> implements Future<T> {
      private CancelledFuture() {
      }

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

   private static final class SentryExecutorServiceThreadFactory implements ThreadFactory {
      private int cnt;

      private SentryExecutorServiceThreadFactory() {
      }

      @NotNull
      @Override
      public Thread newThread(@NotNull Runnable r) {
         Thread ret = new Thread(r, "SentryExecutorServiceThreadFactory-" + this.cnt++);
         ret.setDaemon(true);
         return ret;
      }
   }
}
