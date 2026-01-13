package io.sentry;

import io.sentry.protocol.SentryId;
import org.jetbrains.annotations.NotNull;

public final class NoOpContinuousProfiler implements IContinuousProfiler {
   private static final NoOpContinuousProfiler instance = new NoOpContinuousProfiler();

   private NoOpContinuousProfiler() {
   }

   public static NoOpContinuousProfiler getInstance() {
      return instance;
   }

   @Override
   public void stopProfiler(@NotNull ProfileLifecycle profileLifecycle) {
   }

   @Override
   public boolean isRunning() {
      return false;
   }

   @Override
   public void startProfiler(@NotNull ProfileLifecycle profileLifecycle, @NotNull TracesSampler tracesSampler) {
   }

   @Override
   public void close(boolean isTerminating) {
   }

   @Override
   public void reevaluateSampling() {
   }

   @NotNull
   @Override
   public SentryId getProfilerId() {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public SentryId getChunkId() {
      return SentryId.EMPTY_ID;
   }
}
