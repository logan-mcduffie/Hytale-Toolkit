package io.sentry;

import io.sentry.protocol.Feedback;
import io.sentry.protocol.Message;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.transport.RateLimiter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface ISentryClient {
   boolean isEnabled();

   @NotNull
   SentryId captureEvent(@NotNull SentryEvent var1, @Nullable IScope var2, @Nullable Hint var3);

   void close();

   void close(boolean var1);

   void flush(long var1);

   @NotNull
   default SentryId captureEvent(@NotNull SentryEvent event) {
      return this.captureEvent(event, null, null);
   }

   @NotNull
   default SentryId captureEvent(@NotNull SentryEvent event, @Nullable IScope scope) {
      return this.captureEvent(event, scope, null);
   }

   @NotNull
   default SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint) {
      return this.captureEvent(event, null, hint);
   }

   @NotNull
   SentryId captureFeedback(@NotNull Feedback var1, @Nullable Hint var2, @NotNull IScope var3);

   @NotNull
   default SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level, @Nullable IScope scope) {
      SentryEvent event = new SentryEvent();
      Message sentryMessage = new Message();
      sentryMessage.setFormatted(message);
      event.setMessage(sentryMessage);
      event.setLevel(level);
      return this.captureEvent(event, scope);
   }

   @NotNull
   default SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level) {
      return this.captureMessage(message, level, null);
   }

   @NotNull
   default SentryId captureException(@NotNull Throwable throwable) {
      return this.captureException(throwable, null, null);
   }

   @NotNull
   default SentryId captureException(@NotNull Throwable throwable, @Nullable IScope scope, @Nullable Hint hint) {
      SentryEvent event = new SentryEvent(throwable);
      return this.captureEvent(event, scope, hint);
   }

   @NotNull
   default SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint) {
      return this.captureException(throwable, null, hint);
   }

   @NotNull
   default SentryId captureException(@NotNull Throwable throwable, @Nullable IScope scope) {
      return this.captureException(throwable, scope, null);
   }

   @NotNull
   SentryId captureReplayEvent(@NotNull SentryReplayEvent var1, @Nullable IScope var2, @Nullable Hint var3);

   void captureUserFeedback(@NotNull UserFeedback var1);

   void captureSession(@NotNull Session var1, @Nullable Hint var2);

   default void captureSession(@NotNull Session session) {
      this.captureSession(session, null);
   }

   @Nullable
   SentryId captureEnvelope(@NotNull SentryEnvelope var1, @Nullable Hint var2);

   @Nullable
   default SentryId captureEnvelope(@NotNull SentryEnvelope envelope) {
      return this.captureEnvelope(envelope, null);
   }

   @NotNull
   default SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable IScope scope, @Nullable Hint hint) {
      return this.captureTransaction(transaction, null, scope, hint);
   }

   @NotNull
   default SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable TraceContext traceContext, @Nullable IScope scope, @Nullable Hint hint) {
      return this.captureTransaction(transaction, traceContext, scope, hint, null);
   }

   @NotNull
   @Internal
   SentryId captureTransaction(
      @NotNull SentryTransaction var1, @Nullable TraceContext var2, @Nullable IScope var3, @Nullable Hint var4, @Nullable ProfilingTraceData var5
   );

   @Internal
   @NotNull
   default SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable TraceContext traceContext) {
      return this.captureTransaction(transaction, traceContext, null, null);
   }

   @NotNull
   default SentryId captureTransaction(@NotNull SentryTransaction transaction) {
      return this.captureTransaction(transaction, null, null, null);
   }

   @Internal
   @NotNull
   SentryId captureProfileChunk(@NotNull ProfileChunk var1, @Nullable IScope var2);

   @NotNull
   SentryId captureCheckIn(@NotNull CheckIn var1, @Nullable IScope var2, @Nullable Hint var3);

   void captureLog(@NotNull SentryLogEvent var1, @Nullable IScope var2);

   @Internal
   void captureBatchedLogEvents(@NotNull SentryLogEvents var1);

   @Internal
   @Nullable
   RateLimiter getRateLimiter();

   @Internal
   default boolean isHealthy() {
      return true;
   }
}
