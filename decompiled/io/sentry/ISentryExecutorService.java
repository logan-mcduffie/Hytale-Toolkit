package io.sentry;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.RejectedExecutionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface ISentryExecutorService {
   @NotNull
   Future<?> submit(@NotNull Runnable var1) throws RejectedExecutionException;

   @NotNull
   <T> Future<T> submit(@NotNull Callable<T> var1) throws RejectedExecutionException;

   @NotNull
   Future<?> schedule(@NotNull Runnable var1, long var2) throws RejectedExecutionException;

   void close(long var1);

   boolean isClosed();

   void prewarm();
}
