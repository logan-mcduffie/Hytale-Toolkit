package io.sentry;

import io.sentry.protocol.SentryId;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IContinuousProfiler {
   boolean isRunning();

   void startProfiler(@NotNull ProfileLifecycle var1, @NotNull TracesSampler var2);

   void stopProfiler(@NotNull ProfileLifecycle var1);

   void close(boolean var1);

   void reevaluateSampling();

   @NotNull
   SentryId getProfilerId();

   @NotNull
   SentryId getChunkId();
}
