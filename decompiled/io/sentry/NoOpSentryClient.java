package io.sentry;

import io.sentry.protocol.Feedback;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.transport.RateLimiter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

final class NoOpSentryClient implements ISentryClient {
   private static final NoOpSentryClient instance = new NoOpSentryClient();

   private NoOpSentryClient() {
   }

   public static NoOpSentryClient getInstance() {
      return instance;
   }

   @Override
   public boolean isEnabled() {
      return false;
   }

   @NotNull
   @Override
   public SentryId captureEvent(@NotNull SentryEvent event, @Nullable IScope scope, @Nullable Hint hint) {
      return SentryId.EMPTY_ID;
   }

   @Override
   public void close(boolean isRestarting) {
   }

   @Override
   public void close() {
   }

   @Override
   public void flush(long timeoutMillis) {
   }

   @NotNull
   @Override
   public SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint, @NotNull IScope scope) {
      return SentryId.EMPTY_ID;
   }

   @Override
   public void captureUserFeedback(@NotNull UserFeedback userFeedback) {
   }

   @Override
   public void captureSession(@NotNull Session session, @Nullable Hint hint) {
   }

   @Override
   public SentryId captureEnvelope(@NotNull SentryEnvelope envelope, @Nullable Hint hint) {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public SentryId captureTransaction(
      @NotNull SentryTransaction transaction,
      @Nullable TraceContext traceContext,
      @Nullable IScope scope,
      @Nullable Hint hint,
      @Nullable ProfilingTraceData profilingTraceData
   ) {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public SentryId captureProfileChunk(@NotNull ProfileChunk profileChunk, @Nullable IScope scope) {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public SentryId captureCheckIn(@NotNull CheckIn checkIn, @Nullable IScope scope, @Nullable Hint hint) {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public SentryId captureReplayEvent(@NotNull SentryReplayEvent event, @Nullable IScope scope, @Nullable Hint hint) {
      return SentryId.EMPTY_ID;
   }

   @Override
   public void captureLog(@NotNull SentryLogEvent logEvent, @Nullable IScope scope) {
   }

   @Internal
   @Override
   public void captureBatchedLogEvents(@NotNull SentryLogEvents logEvents) {
   }

   @Nullable
   @Override
   public RateLimiter getRateLimiter() {
      return null;
   }
}
