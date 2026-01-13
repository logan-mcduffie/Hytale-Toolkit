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
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Deprecated
public final class HubAdapter implements IHub {
   private static final HubAdapter INSTANCE = new HubAdapter();

   private HubAdapter() {
   }

   public static HubAdapter getInstance() {
      return INSTANCE;
   }

   @Override
   public boolean isEnabled() {
      return Sentry.isEnabled();
   }

   @NotNull
   @Override
   public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint) {
      return Sentry.captureEvent(event, hint);
   }

   @NotNull
   @Override
   public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint, @NotNull ScopeCallback callback) {
      return Sentry.captureEvent(event, hint, callback);
   }

   @NotNull
   @Override
   public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level) {
      return Sentry.captureMessage(message, level);
   }

   @NotNull
   @Override
   public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level, @NotNull ScopeCallback callback) {
      return Sentry.captureMessage(message, level, callback);
   }

   @NotNull
   @Override
   public SentryId captureFeedback(@NotNull Feedback feedback) {
      return Sentry.captureFeedback(feedback);
   }

   @NotNull
   @Override
   public SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint) {
      return Sentry.captureFeedback(feedback, hint);
   }

   @NotNull
   @Override
   public SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint, @Nullable ScopeCallback callback) {
      return Sentry.captureFeedback(feedback, hint, callback);
   }

   @Internal
   @NotNull
   @Override
   public SentryId captureEnvelope(@NotNull SentryEnvelope envelope, @Nullable Hint hint) {
      return Sentry.getCurrentScopes().captureEnvelope(envelope, hint);
   }

   @NotNull
   @Override
   public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint) {
      return Sentry.captureException(throwable, hint);
   }

   @NotNull
   @Override
   public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint, @NotNull ScopeCallback callback) {
      return Sentry.captureException(throwable, hint, callback);
   }

   @Override
   public void captureUserFeedback(@NotNull UserFeedback userFeedback) {
      Sentry.captureUserFeedback(userFeedback);
   }

   @Override
   public void startSession() {
      Sentry.startSession();
   }

   @Override
   public void endSession() {
      Sentry.endSession();
   }

   @Override
   public void close(boolean isRestarting) {
      Sentry.close();
   }

   @Override
   public void close() {
      Sentry.close();
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
      Sentry.addBreadcrumb(breadcrumb, hint);
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
      this.addBreadcrumb(breadcrumb, new Hint());
   }

   @Override
   public void setLevel(@Nullable SentryLevel level) {
      Sentry.setLevel(level);
   }

   @Override
   public void setTransaction(@Nullable String transaction) {
      Sentry.setTransaction(transaction);
   }

   @Override
   public void setUser(@Nullable User user) {
      Sentry.setUser(user);
   }

   @Override
   public void setFingerprint(@NotNull List<String> fingerprint) {
      Sentry.setFingerprint(fingerprint);
   }

   @Override
   public void clearBreadcrumbs() {
      Sentry.clearBreadcrumbs();
   }

   @Override
   public void setTag(@Nullable String key, @Nullable String value) {
      Sentry.setTag(key, value);
   }

   @Override
   public void removeTag(@Nullable String key) {
      Sentry.removeTag(key);
   }

   @Override
   public void setExtra(@Nullable String key, @Nullable String value) {
      Sentry.setExtra(key, value);
   }

   @Override
   public void removeExtra(@Nullable String key) {
      Sentry.removeExtra(key);
   }

   @NotNull
   @Override
   public SentryId getLastEventId() {
      return Sentry.getLastEventId();
   }

   @NotNull
   @Override
   public ISentryLifecycleToken pushScope() {
      return Sentry.pushScope();
   }

   @NotNull
   @Override
   public ISentryLifecycleToken pushIsolationScope() {
      return Sentry.pushIsolationScope();
   }

   @Deprecated
   @Override
   public void popScope() {
      Sentry.popScope();
   }

   @Override
   public void withScope(@NotNull ScopeCallback callback) {
      Sentry.withScope(callback);
   }

   @Override
   public void withIsolationScope(@NotNull ScopeCallback callback) {
      Sentry.withIsolationScope(callback);
   }

   @Override
   public void configureScope(@Nullable ScopeType scopeType, @NotNull ScopeCallback callback) {
      Sentry.configureScope(scopeType, callback);
   }

   @Override
   public void bindClient(@NotNull ISentryClient client) {
      Sentry.bindClient(client);
   }

   @Override
   public boolean isHealthy() {
      return Sentry.isHealthy();
   }

   @Override
   public void flush(long timeoutMillis) {
      Sentry.flush(timeoutMillis);
   }

   @Deprecated
   @NotNull
   @Override
   public IHub clone() {
      return Sentry.getCurrentScopes().clone();
   }

   @NotNull
   @Override
   public IScopes forkedScopes(@NotNull String creator) {
      return Sentry.forkedScopes(creator);
   }

   @NotNull
   @Override
   public IScopes forkedCurrentScope(@NotNull String creator) {
      return Sentry.forkedCurrentScope(creator);
   }

   @NotNull
   @Override
   public IScopes forkedRootScopes(@NotNull String creator) {
      return Sentry.forkedRootScopes(creator);
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
      return Sentry.getCurrentScopes().getScope();
   }

   @Internal
   @NotNull
   @Override
   public IScope getIsolationScope() {
      return Sentry.getCurrentScopes().getIsolationScope();
   }

   @Internal
   @NotNull
   @Override
   public IScope getGlobalScope() {
      return Sentry.getGlobalScope();
   }

   @Nullable
   @Override
   public IScopes getParentScopes() {
      return Sentry.getCurrentScopes().getParentScopes();
   }

   @Override
   public boolean isAncestorOf(@Nullable IScopes otherScopes) {
      return Sentry.getCurrentScopes().isAncestorOf(otherScopes);
   }

   @NotNull
   @Override
   public SentryId captureTransaction(
      @NotNull SentryTransaction transaction, @Nullable TraceContext traceContext, @Nullable Hint hint, @Nullable ProfilingTraceData profilingTraceData
   ) {
      return Sentry.getCurrentScopes().captureTransaction(transaction, traceContext, hint, profilingTraceData);
   }

   @NotNull
   @Override
   public ITransaction startTransaction(@NotNull TransactionContext transactionContext, @NotNull TransactionOptions transactionOptions) {
      return Sentry.startTransaction(transactionContext, transactionOptions);
   }

   @Override
   public void startProfiler() {
      Sentry.startProfiler();
   }

   @Override
   public void stopProfiler() {
      Sentry.stopProfiler();
   }

   @NotNull
   @Override
   public SentryId captureProfileChunk(@NotNull ProfileChunk profilingContinuousData) {
      return Sentry.getCurrentScopes().captureProfileChunk(profilingContinuousData);
   }

   @Override
   public void setSpanContext(@NotNull Throwable throwable, @NotNull ISpan span, @NotNull String transactionName) {
      Sentry.getCurrentScopes().setSpanContext(throwable, span, transactionName);
   }

   @Nullable
   @Override
   public ISpan getSpan() {
      return Sentry.getCurrentScopes().getSpan();
   }

   @Override
   public void setActiveSpan(@Nullable ISpan span) {
      Sentry.getCurrentScopes().setActiveSpan(span);
   }

   @Internal
   @Nullable
   @Override
   public ITransaction getTransaction() {
      return Sentry.getCurrentScopes().getTransaction();
   }

   @NotNull
   @Override
   public SentryOptions getOptions() {
      return Sentry.getCurrentScopes().getOptions();
   }

   @Nullable
   @Override
   public Boolean isCrashedLastRun() {
      return Sentry.isCrashedLastRun();
   }

   @Override
   public void reportFullyDisplayed() {
      Sentry.reportFullyDisplayed();
   }

   @Nullable
   @Override
   public TransactionContext continueTrace(@Nullable String sentryTrace, @Nullable List<String> baggageHeaders) {
      return Sentry.continueTrace(sentryTrace, baggageHeaders);
   }

   @Nullable
   @Override
   public SentryTraceHeader getTraceparent() {
      return Sentry.getTraceparent();
   }

   @Nullable
   @Override
   public BaggageHeader getBaggage() {
      return Sentry.getBaggage();
   }

   @Experimental
   @NotNull
   @Override
   public SentryId captureCheckIn(@NotNull CheckIn checkIn) {
      return Sentry.captureCheckIn(checkIn);
   }

   @NotNull
   @Override
   public SentryId captureReplay(@NotNull SentryReplayEvent replay, @Nullable Hint hint) {
      return Sentry.getCurrentScopes().captureReplay(replay, hint);
   }

   @Internal
   @Nullable
   @Override
   public RateLimiter getRateLimiter() {
      return Sentry.getCurrentScopes().getRateLimiter();
   }

   @NotNull
   @Override
   public ILoggerApi logger() {
      return Sentry.getCurrentScopes().logger();
   }

   @Override
   public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
      Sentry.addFeatureFlag(flag, result);
   }
}
