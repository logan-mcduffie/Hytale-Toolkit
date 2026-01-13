package io.sentry.backpressure;

import io.sentry.IScopes;
import io.sentry.ISentryExecutorService;
import io.sentry.ISentryLifecycleToken;
import io.sentry.SentryLevel;
import io.sentry.SentryOptions;
import io.sentry.util.AutoClosableReentrantLock;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class BackpressureMonitor implements IBackpressureMonitor, Runnable {
   static final int MAX_DOWNSAMPLE_FACTOR = 10;
   private static final int INITIAL_CHECK_DELAY_IN_MS = 500;
   private static final int CHECK_INTERVAL_IN_MS = 10000;
   @NotNull
   private final SentryOptions sentryOptions;
   @NotNull
   private final IScopes scopes;
   private int downsampleFactor = 0;
   @Nullable
   private volatile Future<?> latestScheduledRun = null;
   @NotNull
   private final AutoClosableReentrantLock lock = new AutoClosableReentrantLock();

   public BackpressureMonitor(@NotNull SentryOptions sentryOptions, @NotNull IScopes scopes) {
      this.sentryOptions = sentryOptions;
      this.scopes = scopes;
   }

   @Override
   public void start() {
      this.reschedule(500);
   }

   @Override
   public void run() {
      this.checkHealth();
      this.reschedule(10000);
   }

   @Override
   public int getDownsampleFactor() {
      return this.downsampleFactor;
   }

   @Override
   public void close() {
      Future<?> currentRun = this.latestScheduledRun;
      if (currentRun != null) {
         ISentryLifecycleToken ignored = this.lock.acquire();

         try {
            currentRun.cancel(true);
         } catch (Throwable var6) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }
            }

            throw var6;
         }

         if (ignored != null) {
            ignored.close();
         }
      }
   }

   void checkHealth() {
      if (this.isHealthy()) {
         if (this.downsampleFactor > 0) {
            this.sentryOptions.getLogger().log(SentryLevel.DEBUG, "Health check positive, reverting to normal sampling.");
         }

         this.downsampleFactor = 0;
      } else if (this.downsampleFactor < 10) {
         this.downsampleFactor++;
         this.sentryOptions.getLogger().log(SentryLevel.DEBUG, "Health check negative, downsampling with a factor of %d", this.downsampleFactor);
      }
   }

   private void reschedule(int delay) {
      ISentryExecutorService executorService = this.sentryOptions.getExecutorService();
      if (!executorService.isClosed()) {
         ISentryLifecycleToken ignored = this.lock.acquire();

         try {
            try {
               this.latestScheduledRun = executorService.schedule(this, delay);
            } catch (RejectedExecutionException var7) {
               this.sentryOptions.getLogger().log(SentryLevel.WARNING, "Backpressure monitor reschedule task rejected", var7);
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
   }

   private boolean isHealthy() {
      return this.scopes.isHealthy();
   }
}
