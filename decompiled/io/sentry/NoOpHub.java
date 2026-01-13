package io.sentry;

import io.sentry.logger.ILoggerApi;
import io.sentry.logger.NoOpLoggerApi;
import io.sentry.protocol.Feedback;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.User;
import io.sentry.transport.RateLimiter;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Deprecated
public final class NoOpHub implements IHub {
   private static final NoOpHub instance = new NoOpHub();
   @NotNull
   private final SentryOptions emptyOptions = SentryOptions.empty();

   private NoOpHub() {
   }

   @Deprecated
   public static NoOpHub getInstance() {
      return instance;
   }

   @Override
   public boolean isEnabled() {
      return false;
   }

   @NotNull
   @Override
   public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint) {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint, @NotNull ScopeCallback callback) {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level) {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level, @NotNull ScopeCallback callback) {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint, @Nullable ScopeCallback callback) {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public SentryId captureEnvelope(@NotNull SentryEnvelope envelope, @Nullable Hint hint) {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint) {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint, @NotNull ScopeCallback callback) {
      return SentryId.EMPTY_ID;
   }

   @Override
   public void captureUserFeedback(@NotNull UserFeedback userFeedback) {
   }

   @Override
   public void startSession() {
   }

   @Override
   public void endSession() {
   }

   @Override
   public void close() {
   }

   @Override
   public void close(boolean isRestarting) {
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
   }

   @Override
   public void setLevel(@Nullable SentryLevel level) {
   }

   @Override
   public void setTransaction(@Nullable String transaction) {
   }

   @Override
   public void setUser(@Nullable User user) {
   }

   @Override
   public void setFingerprint(@NotNull List<String> fingerprint) {
   }

   @Override
   public void clearBreadcrumbs() {
   }

   @Override
   public void setTag(@Nullable String key, @Nullable String value) {
   }

   @Override
   public void removeTag(@Nullable String key) {
   }

   @Override
   public void setExtra(@Nullable String key, @Nullable String value) {
   }

   @Override
   public void removeExtra(@Nullable String key) {
   }

   @NotNull
   @Override
   public SentryId getLastEventId() {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public ISentryLifecycleToken pushScope() {
      return NoOpScopesLifecycleToken.getInstance();
   }

   @NotNull
   @Override
   public ISentryLifecycleToken pushIsolationScope() {
      return NoOpScopesLifecycleToken.getInstance();
   }

   @Deprecated
   @Override
   public void popScope() {
   }

   @Override
   public void withScope(@NotNull ScopeCallback callback) {
      callback.run(NoOpScope.getInstance());
   }

   @Override
   public void withIsolationScope(@NotNull ScopeCallback callback) {
      callback.run(NoOpScope.getInstance());
   }

   @Override
   public void configureScope(@Nullable ScopeType scopeType, @NotNull ScopeCallback callback) {
   }

   @Override
   public void bindClient(@NotNull ISentryClient client) {
   }

   @Override
   public boolean isHealthy() {
      return true;
   }

   @Override
   public void flush(long timeoutMillis) {
   }

   @Deprecated
   @NotNull
   @Override
   public IHub clone() {
      return instance;
   }

   @NotNull
   @Override
   public IScopes forkedScopes(@NotNull String creator) {
      return NoOpScopes.getInstance();
   }

   @NotNull
   @Override
   public IScopes forkedCurrentScope(@NotNull String creator) {
      return NoOpScopes.getInstance();
   }

   @NotNull
   @Override
   public ISentryLifecycleToken makeCurrent() {
      return NoOpScopesLifecycleToken.getInstance();
   }

   @Internal
   @NotNull
   @Override
   public IScope getScope() {
      return NoOpScope.getInstance();
   }

   @Internal
   @NotNull
   @Override
   public IScope getIsolationScope() {
      return NoOpScope.getInstance();
   }

   @Internal
   @NotNull
   @Override
   public IScope getGlobalScope() {
      return NoOpScope.getInstance();
   }

   @Nullable
   @Override
   public IScopes getParentScopes() {
      return null;
   }

   @Override
   public boolean isAncestorOf(@Nullable IScopes otherScopes) {
      return false;
   }

   @NotNull
   @Override
   public IScopes forkedRootScopes(@NotNull String creator) {
      return NoOpScopes.getInstance();
   }

   @NotNull
   @Override
   public SentryId captureTransaction(
      @NotNull SentryTransaction transaction, @Nullable TraceContext traceContext, @Nullable Hint hint, @Nullable ProfilingTraceData profilingTraceData
   ) {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public SentryId captureProfileChunk(@NotNull ProfileChunk profileChunk) {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public ITransaction startTransaction(@NotNull TransactionContext transactionContext, @NotNull TransactionOptions transactionOptions) {
      return NoOpTransaction.getInstance();
   }

   @Override
   public void startProfiler() {
   }

   @Override
   public void stopProfiler() {
   }

   @Override
   public void setSpanContext(@NotNull Throwable throwable, @NotNull ISpan spanContext, @NotNull String transactionName) {
   }

   @Nullable
   @Override
   public ISpan getSpan() {
      return null;
   }

   @Override
   public void setActiveSpan(@Nullable ISpan span) {
   }

   @Nullable
   @Override
   public ITransaction getTransaction() {
      return null;
   }

   @NotNull
   @Override
   public SentryOptions getOptions() {
      return this.emptyOptions;
   }

   @Nullable
   @Override
   public Boolean isCrashedLastRun() {
      return null;
   }

   @Override
   public void reportFullyDisplayed() {
   }

   @Nullable
   @Override
   public TransactionContext continueTrace(@Nullable String sentryTrace, @Nullable List<String> baggageHeaders) {
      return null;
   }

   @Nullable
   @Override
   public SentryTraceHeader getTraceparent() {
      return null;
   }

   @Nullable
   @Override
   public BaggageHeader getBaggage() {
      return null;
   }

   @NotNull
   @Override
   public SentryId captureCheckIn(@NotNull CheckIn checkIn) {
      return SentryId.EMPTY_ID;
   }

   @NotNull
   @Override
   public SentryId captureReplay(@NotNull SentryReplayEvent replay, @Nullable Hint hint) {
      return SentryId.EMPTY_ID;
   }

   @Nullable
   @Override
   public RateLimiter getRateLimiter() {
      return null;
   }

   @Override
   public boolean isNoOp() {
      return true;
   }

   @NotNull
   @Override
   public ILoggerApi logger() {
      return NoOpLoggerApi.getInstance();
   }

   @Override
   public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
   }
}
