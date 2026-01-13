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

@Deprecated
public final class HubScopesWrapper implements IHub {
   @NotNull
   private final IScopes scopes;

   public HubScopesWrapper(@NotNull IScopes scopes) {
      this.scopes = scopes;
   }

   @NotNull
   public IScopes getScopes() {
      return this.scopes;
   }

   @Override
   public boolean isEnabled() {
      return this.scopes.isEnabled();
   }

   @NotNull
   @Override
   public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint) {
      return this.scopes.captureEvent(event, hint);
   }

   @NotNull
   @Override
   public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint, @NotNull ScopeCallback callback) {
      return this.scopes.captureEvent(event, hint, callback);
   }

   @NotNull
   @Override
   public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level) {
      return this.scopes.captureMessage(message, level);
   }

   @NotNull
   @Override
   public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level, @NotNull ScopeCallback callback) {
      return this.scopes.captureMessage(message, level, callback);
   }

   @NotNull
   @Override
   public SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint, @Nullable ScopeCallback callback) {
      return this.scopes.captureFeedback(feedback, hint, callback);
   }

   @NotNull
   @Override
   public SentryId captureEnvelope(@NotNull SentryEnvelope envelope, @Nullable Hint hint) {
      return this.scopes.captureEnvelope(envelope, hint);
   }

   @NotNull
   @Override
   public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint) {
      return this.scopes.captureException(throwable, hint);
   }

   @NotNull
   @Override
   public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint, @NotNull ScopeCallback callback) {
      return this.scopes.captureException(throwable, hint, callback);
   }

   @Override
   public void captureUserFeedback(@NotNull UserFeedback userFeedback) {
      this.scopes.captureUserFeedback(userFeedback);
   }

   @Override
   public void startSession() {
      this.scopes.startSession();
   }

   @Override
   public void endSession() {
      this.scopes.endSession();
   }

   @Override
   public void close() {
      this.scopes.close();
   }

   @Override
   public void close(boolean isRestarting) {
      this.scopes.close(isRestarting);
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
      this.scopes.addBreadcrumb(breadcrumb, hint);
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
      this.scopes.addBreadcrumb(breadcrumb);
   }

   @Override
   public void setLevel(@Nullable SentryLevel level) {
      this.scopes.setLevel(level);
   }

   @Override
   public void setTransaction(@Nullable String transaction) {
      this.scopes.setTransaction(transaction);
   }

   @Override
   public void setUser(@Nullable User user) {
      this.scopes.setUser(user);
   }

   @Override
   public void setFingerprint(@NotNull List<String> fingerprint) {
      this.scopes.setFingerprint(fingerprint);
   }

   @Override
   public void clearBreadcrumbs() {
      this.scopes.clearBreadcrumbs();
   }

   @Override
   public void setTag(@Nullable String key, @Nullable String value) {
      this.scopes.setTag(key, value);
   }

   @Override
   public void removeTag(@Nullable String key) {
      this.scopes.removeTag(key);
   }

   @Override
   public void setExtra(@Nullable String key, @Nullable String value) {
      this.scopes.setExtra(key, value);
   }

   @Override
   public void removeExtra(@Nullable String key) {
      this.scopes.removeExtra(key);
   }

   @NotNull
   @Override
   public SentryId getLastEventId() {
      return this.scopes.getLastEventId();
   }

   @NotNull
   @Override
   public ISentryLifecycleToken pushScope() {
      return this.scopes.pushScope();
   }

   @NotNull
   @Override
   public ISentryLifecycleToken pushIsolationScope() {
      return this.scopes.pushIsolationScope();
   }

   @Deprecated
   @Override
   public void popScope() {
      this.scopes.popScope();
   }

   @Override
   public void withScope(@NotNull ScopeCallback callback) {
      this.scopes.withScope(callback);
   }

   @Override
   public void withIsolationScope(@NotNull ScopeCallback callback) {
      this.scopes.withIsolationScope(callback);
   }

   @Override
   public void configureScope(@Nullable ScopeType scopeType, @NotNull ScopeCallback callback) {
      this.scopes.configureScope(scopeType, callback);
   }

   @Override
   public void bindClient(@NotNull ISentryClient client) {
      this.scopes.bindClient(client);
   }

   @Override
   public boolean isHealthy() {
      return this.scopes.isHealthy();
   }

   @Override
   public void flush(long timeoutMillis) {
      this.scopes.flush(timeoutMillis);
   }

   @Deprecated
   @NotNull
   @Override
   public IHub clone() {
      return this.scopes.clone();
   }

   @NotNull
   @Override
   public IScopes forkedScopes(@NotNull String creator) {
      return this.scopes.forkedScopes(creator);
   }

   @NotNull
   @Override
   public IScopes forkedCurrentScope(@NotNull String creator) {
      return this.scopes.forkedCurrentScope(creator);
   }

   @NotNull
   @Override
   public IScopes forkedRootScopes(@NotNull String creator) {
      return Sentry.forkedRootScopes(creator);
   }

   @NotNull
   @Override
   public ISentryLifecycleToken makeCurrent() {
      return this.scopes.makeCurrent();
   }

   @Internal
   @NotNull
   @Override
   public IScope getScope() {
      return this.scopes.getScope();
   }

   @Internal
   @NotNull
   @Override
   public IScope getIsolationScope() {
      return this.scopes.getIsolationScope();
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
      return this.scopes.getParentScopes();
   }

   @Override
   public boolean isAncestorOf(@Nullable IScopes otherScopes) {
      return this.scopes.isAncestorOf(otherScopes);
   }

   @Internal
   @NotNull
   @Override
   public SentryId captureTransaction(
      @NotNull SentryTransaction transaction, @Nullable TraceContext traceContext, @Nullable Hint hint, @Nullable ProfilingTraceData profilingTraceData
   ) {
      return this.scopes.captureTransaction(transaction, traceContext, hint, profilingTraceData);
   }

   @NotNull
   @Override
   public SentryId captureProfileChunk(@NotNull ProfileChunk profileChunk) {
      return this.scopes.captureProfileChunk(profileChunk);
   }

   @NotNull
   @Override
   public ITransaction startTransaction(@NotNull TransactionContext transactionContext, @NotNull TransactionOptions transactionOptions) {
      return this.scopes.startTransaction(transactionContext, transactionOptions);
   }

   @Override
   public void startProfiler() {
      this.scopes.startProfiler();
   }

   @Override
   public void stopProfiler() {
      this.scopes.stopProfiler();
   }

   @Internal
   @Override
   public void setSpanContext(@NotNull Throwable throwable, @NotNull ISpan span, @NotNull String transactionName) {
      this.scopes.setSpanContext(throwable, span, transactionName);
   }

   @Nullable
   @Override
   public ISpan getSpan() {
      return this.scopes.getSpan();
   }

   @Override
   public void setActiveSpan(@Nullable ISpan span) {
      this.scopes.setActiveSpan(span);
   }

   @Internal
   @Nullable
   @Override
   public ITransaction getTransaction() {
      return this.scopes.getTransaction();
   }

   @NotNull
   @Override
   public SentryOptions getOptions() {
      return this.scopes.getOptions();
   }

   @Nullable
   @Override
   public Boolean isCrashedLastRun() {
      return this.scopes.isCrashedLastRun();
   }

   @Override
   public void reportFullyDisplayed() {
      this.scopes.reportFullyDisplayed();
   }

   @Nullable
   @Override
   public TransactionContext continueTrace(@Nullable String sentryTrace, @Nullable List<String> baggageHeaders) {
      return this.scopes.continueTrace(sentryTrace, baggageHeaders);
   }

   @Nullable
   @Override
   public SentryTraceHeader getTraceparent() {
      return this.scopes.getTraceparent();
   }

   @Nullable
   @Override
   public BaggageHeader getBaggage() {
      return this.scopes.getBaggage();
   }

   @NotNull
   @Override
   public SentryId captureCheckIn(@NotNull CheckIn checkIn) {
      return this.scopes.captureCheckIn(checkIn);
   }

   @Internal
   @Nullable
   @Override
   public RateLimiter getRateLimiter() {
      return this.scopes.getRateLimiter();
   }

   @NotNull
   @Override
   public SentryId captureReplay(@NotNull SentryReplayEvent replay, @Nullable Hint hint) {
      return this.scopes.captureReplay(replay, hint);
   }

   @NotNull
   @Override
   public ILoggerApi logger() {
      return this.scopes.logger();
   }

   @Override
   public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
      this.scopes.addFeatureFlag(flag, result);
   }
}
