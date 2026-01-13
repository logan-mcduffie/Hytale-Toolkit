package io.sentry;

import io.sentry.logger.ILoggerApi;
import io.sentry.protocol.Feedback;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.User;
import io.sentry.transport.RateLimiter;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface IScopes {
   boolean isEnabled();

   @NotNull
   SentryId captureEvent(@NotNull SentryEvent var1, @Nullable Hint var2);

   @NotNull
   default SentryId captureEvent(@NotNull SentryEvent event) {
      return this.captureEvent(event, new Hint());
   }

   @NotNull
   default SentryId captureEvent(@NotNull SentryEvent event, @NotNull ScopeCallback callback) {
      return this.captureEvent(event, new Hint(), callback);
   }

   @NotNull
   SentryId captureEvent(@NotNull SentryEvent var1, @Nullable Hint var2, @NotNull ScopeCallback var3);

   @NotNull
   default SentryId captureMessage(@NotNull String message) {
      return this.captureMessage(message, SentryLevel.INFO);
   }

   @NotNull
   SentryId captureMessage(@NotNull String var1, @NotNull SentryLevel var2);

   @NotNull
   SentryId captureMessage(@NotNull String var1, @NotNull SentryLevel var2, @NotNull ScopeCallback var3);

   @NotNull
   default SentryId captureMessage(@NotNull String message, @NotNull ScopeCallback callback) {
      return this.captureMessage(message, SentryLevel.INFO, callback);
   }

   @NotNull
   default SentryId captureFeedback(@NotNull Feedback feedback) {
      return this.captureFeedback(feedback, null);
   }

   @NotNull
   default SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint) {
      return this.captureFeedback(feedback, hint, null);
   }

   @NotNull
   SentryId captureFeedback(@NotNull Feedback var1, @Nullable Hint var2, @Nullable ScopeCallback var3);

   @NotNull
   SentryId captureEnvelope(@NotNull SentryEnvelope var1, @Nullable Hint var2);

   @NotNull
   default SentryId captureEnvelope(@NotNull SentryEnvelope envelope) {
      return this.captureEnvelope(envelope, new Hint());
   }

   @NotNull
   SentryId captureException(@NotNull Throwable var1, @Nullable Hint var2);

   @NotNull
   default SentryId captureException(@NotNull Throwable throwable) {
      return this.captureException(throwable, new Hint());
   }

   @NotNull
   default SentryId captureException(@NotNull Throwable throwable, @NotNull ScopeCallback callback) {
      return this.captureException(throwable, new Hint(), callback);
   }

   @NotNull
   SentryId captureException(@NotNull Throwable var1, @Nullable Hint var2, @NotNull ScopeCallback var3);

   void captureUserFeedback(@NotNull UserFeedback var1);

   void startSession();

   void endSession();

   void close();

   void close(boolean var1);

   void addBreadcrumb(@NotNull Breadcrumb var1, @Nullable Hint var2);

   void addBreadcrumb(@NotNull Breadcrumb var1);

   default void addBreadcrumb(@NotNull String message) {
      this.addBreadcrumb(new Breadcrumb(message));
   }

   default void addBreadcrumb(@NotNull String message, @NotNull String category) {
      Breadcrumb breadcrumb = new Breadcrumb(message);
      breadcrumb.setCategory(category);
      this.addBreadcrumb(breadcrumb);
   }

   void setLevel(@Nullable SentryLevel var1);

   void setTransaction(@Nullable String var1);

   void setUser(@Nullable User var1);

   void setFingerprint(@NotNull List<String> var1);

   void clearBreadcrumbs();

   void setTag(@Nullable String var1, @Nullable String var2);

   void removeTag(@Nullable String var1);

   void setExtra(@Nullable String var1, @Nullable String var2);

   void removeExtra(@Nullable String var1);

   @NotNull
   SentryId getLastEventId();

   @NotNull
   ISentryLifecycleToken pushScope();

   @NotNull
   ISentryLifecycleToken pushIsolationScope();

   @Deprecated
   void popScope();

   void withScope(@NotNull ScopeCallback var1);

   void withIsolationScope(@NotNull ScopeCallback var1);

   default void configureScope(@NotNull ScopeCallback callback) {
      this.configureScope(null, callback);
   }

   void configureScope(@Nullable ScopeType var1, @NotNull ScopeCallback var2);

   void bindClient(@NotNull ISentryClient var1);

   boolean isHealthy();

   void flush(long var1);

   @Deprecated
   @NotNull
   IHub clone();

   @NotNull
   IScopes forkedScopes(@NotNull String var1);

   @NotNull
   IScopes forkedCurrentScope(@NotNull String var1);

   @NotNull
   IScopes forkedRootScopes(@NotNull String var1);

   @NotNull
   ISentryLifecycleToken makeCurrent();

   @Internal
   @NotNull
   IScope getScope();

   @Internal
   @NotNull
   IScope getIsolationScope();

   @Internal
   @NotNull
   IScope getGlobalScope();

   @Internal
   @Nullable
   IScopes getParentScopes();

   @Internal
   boolean isAncestorOf(@Nullable IScopes var1);

   @Internal
   @NotNull
   SentryId captureTransaction(@NotNull SentryTransaction var1, @Nullable TraceContext var2, @Nullable Hint var3, @Nullable ProfilingTraceData var4);

   @Internal
   @NotNull
   default SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable TraceContext traceContext, @Nullable Hint hint) {
      return this.captureTransaction(transaction, traceContext, hint, null);
   }

   @Internal
   @NotNull
   default SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable Hint hint) {
      return this.captureTransaction(transaction, null, hint);
   }

   @Internal
   @NotNull
   default SentryId captureTransaction(@NotNull SentryTransaction transaction, @Nullable TraceContext traceContext) {
      return this.captureTransaction(transaction, traceContext, null);
   }

   @Internal
   @NotNull
   SentryId captureProfileChunk(@NotNull ProfileChunk var1);

   @NotNull
   default ITransaction startTransaction(@NotNull TransactionContext transactionContexts) {
      return this.startTransaction(transactionContexts, new TransactionOptions());
   }

   @NotNull
   default ITransaction startTransaction(@NotNull String name, @NotNull String operation) {
      return this.startTransaction(name, operation, new TransactionOptions());
   }

   @NotNull
   default ITransaction startTransaction(@NotNull String name, @NotNull String operation, @NotNull TransactionOptions transactionOptions) {
      return this.startTransaction(new TransactionContext(name, operation), transactionOptions);
   }

   @NotNull
   ITransaction startTransaction(@NotNull TransactionContext var1, @NotNull TransactionOptions var2);

   void startProfiler();

   void stopProfiler();

   @Internal
   void setSpanContext(@NotNull Throwable var1, @NotNull ISpan var2, @NotNull String var3);

   @Nullable
   ISpan getSpan();

   @Internal
   void setActiveSpan(@Nullable ISpan var1);

   @Internal
   @Nullable
   ITransaction getTransaction();

   @NotNull
   SentryOptions getOptions();

   @Nullable
   Boolean isCrashedLastRun();

   void reportFullyDisplayed();

   @Nullable
   TransactionContext continueTrace(@Nullable String var1, @Nullable List<String> var2);

   @Nullable
   SentryTraceHeader getTraceparent();

   @Nullable
   BaggageHeader getBaggage();

   @NotNull
   SentryId captureCheckIn(@NotNull CheckIn var1);

   @Internal
   @Nullable
   RateLimiter getRateLimiter();

   default boolean isNoOp() {
      return false;
   }

   @NotNull
   SentryId captureReplay(@NotNull SentryReplayEvent var1, @Nullable Hint var2);

   @NotNull
   ILoggerApi logger();

   void addFeatureFlag(@Nullable String var1, @Nullable Boolean var2);
}
