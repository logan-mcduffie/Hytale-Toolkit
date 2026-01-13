package io.sentry;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;
import org.jetbrains.annotations.NotNull;

final class NoOpSentryExecutorService implements ISentryExecutorService {
   private static final NoOpSentryExecutorService instance = new NoOpSentryExecutorService();

   private NoOpSentryExecutorService() {
   }

   @NotNull
   public static ISentryExecutorService getInstance() {
      return instance;
   }

   @NotNull
   @Override
   public Future<?> submit(@NotNull Runnable runnable) {
      return new FutureTask(() -> null);
   }

   @NotNull
   @Override
   public <T> Future<T> submit(@NotNull Callable<T> callable) {
      return new FutureTask<>(() -> null);
   }

   @NotNull
   @Override
   public Future<?> schedule(@NotNull Runnable runnable, long delayMillis) {
      return new FutureTask(() -> null);
   }

   @Override
   public void close(long timeoutMillis) {
   }

   @Override
   public boolean isClosed() {
      return false;
   }

   @Override
   public void prewarm() {
   }
}
