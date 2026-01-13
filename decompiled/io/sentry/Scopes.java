package io.sentry;

import io.sentry.clientreport.DiscardReason;
import io.sentry.hints.SessionEndHint;
import io.sentry.hints.SessionStartHint;
import io.sentry.logger.ILoggerApi;
import io.sentry.logger.LoggerApi;
import io.sentry.protocol.Feedback;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.SentryTransaction;
import io.sentry.protocol.User;
import io.sentry.transport.RateLimiter;
import io.sentry.util.HintUtils;
import io.sentry.util.Objects;
import io.sentry.util.SpanUtils;
import io.sentry.util.TracingUtils;
import java.io.Closeable;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class Scopes implements IScopes {
   @NotNull
   private final IScope scope;
   @NotNull
   private final IScope isolationScope;
   @NotNull
   private final IScope globalScope;
   @Nullable
   private final Scopes parentScopes;
   @NotNull
   private final String creator;
   @NotNull
   private final CompositePerformanceCollector compositePerformanceCollector;
   @NotNull
   private final CombinedScopeView combinedScope;
   @NotNull
   private final ILoggerApi logger;

   public Scopes(@NotNull IScope scope, @NotNull IScope isolationScope, @NotNull IScope globalScope, @NotNull String creator) {
      this(scope, isolationScope, globalScope, null, creator);
   }

   private Scopes(@NotNull IScope scope, @NotNull IScope isolationScope, @NotNull IScope globalScope, @Nullable Scopes parentScopes, @NotNull String creator) {
      this.combinedScope = new CombinedScopeView(globalScope, isolationScope, scope);
      this.scope = scope;
      this.isolationScope = isolationScope;
      this.globalScope = globalScope;
      this.parentScopes = parentScopes;
      this.creator = creator;
      SentryOptions options = this.getOptions();
      validateOptions(options);
      this.compositePerformanceCollector = options.getCompositePerformanceCollector();
      this.logger = new LoggerApi(this);
   }

   @NotNull
   public String getCreator() {
      return this.creator;
   }

   @Internal
   @NotNull
   @Override
   public IScope getScope() {
      return this.scope;
   }

   @Internal
   @NotNull
   @Override
   public IScope getIsolationScope() {
      return this.isolationScope;
   }

   @Internal
   @NotNull
   @Override
   public IScope getGlobalScope() {
      return this.globalScope;
   }

   @Internal
   @Nullable
   @Override
   public IScopes getParentScopes() {
      return this.parentScopes;
   }

   @Internal
   @Override
   public boolean isAncestorOf(@Nullable IScopes otherScopes) {
      if (otherScopes == null) {
         return false;
      } else if (this == otherScopes) {
         return true;
      } else {
         return otherScopes.getParentScopes() != null ? this.isAncestorOf(otherScopes.getParentScopes()) : false;
      }
   }

   @NotNull
   @Override
   public IScopes forkedScopes(@NotNull String creator) {
      return new Scopes(this.scope.clone(), this.isolationScope.clone(), this.globalScope, this, creator);
   }

   @NotNull
   @Override
   public IScopes forkedCurrentScope(@NotNull String creator) {
      return new Scopes(this.scope.clone(), this.isolationScope, this.globalScope, this, creator);
   }

   @NotNull
   @Override
   public IScopes forkedRootScopes(@NotNull String creator) {
      return Sentry.forkedRootScopes(creator);
   }

   @Override
   public boolean isEnabled() {
      return this.getClient().isEnabled();
   }

   @NotNull
   @Override
   public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint) {
      return this.captureEventInternal(event, hint, null);
   }

   @NotNull
   @Override
   public SentryId captureEvent(@NotNull SentryEvent event, @Nullable Hint hint, @NotNull ScopeCallback callback) {
      return this.captureEventInternal(event, hint, callback);
   }

   @NotNull
   private SentryId captureEventInternal(@NotNull SentryEvent event, @Nullable Hint hint, @Nullable ScopeCallback scopeCallback) {
      SentryId sentryId = SentryId.EMPTY_ID;
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureEvent' call is a no-op.");
      } else if (event == null) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "captureEvent called with null parameter.");
      } else {
         try {
            this.assignTraceContext(event);
            IScope localScope = this.buildLocalScope(this.getCombinedScopeView(), scopeCallback);
            sentryId = this.getClient().captureEvent(event, localScope, hint);
            this.updateLastEventId(sentryId);
         } catch (Throwable var6) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing event with id: " + event.getEventId(), var6);
         }
      }

      return sentryId;
   }

   @Internal
   @NotNull
   public ISentryClient getClient() {
      return this.getCombinedScopeView().getClient();
   }

   private void assignTraceContext(@NotNull SentryEvent event) {
      this.getCombinedScopeView().assignTraceContext(event);
   }

   @NotNull
   private IScope buildLocalScope(@NotNull IScope parentScope, @Nullable ScopeCallback callback) {
      if (callback != null) {
         try {
            IScope localScope = parentScope.clone();
            callback.run(localScope);
            return localScope;
         } catch (Throwable var4) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'ScopeCallback' callback.", var4);
         }
      }

      return parentScope;
   }

   @NotNull
   @Override
   public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level) {
      return this.captureMessageInternal(message, level, null);
   }

   @NotNull
   @Override
   public SentryId captureMessage(@NotNull String message, @NotNull SentryLevel level, @NotNull ScopeCallback callback) {
      return this.captureMessageInternal(message, level, callback);
   }

   @NotNull
   private SentryId captureMessageInternal(@NotNull String message, @NotNull SentryLevel level, @Nullable ScopeCallback scopeCallback) {
      SentryId sentryId = SentryId.EMPTY_ID;
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureMessage' call is a no-op.");
      } else if (message == null) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "captureMessage called with null parameter.");
      } else {
         try {
            IScope localScope = this.buildLocalScope(this.getCombinedScopeView(), scopeCallback);
            sentryId = this.getClient().captureMessage(message, level, localScope);
         } catch (Throwable var6) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing message: " + message, var6);
         }
      }

      this.updateLastEventId(sentryId);
      return sentryId;
   }

   @NotNull
   @Override
   public SentryId captureFeedback(@NotNull Feedback feedback, @Nullable Hint hint, @Nullable ScopeCallback scopeCallback) {
      SentryId sentryId = SentryId.EMPTY_ID;
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureFeedback' call is a no-op.");
      } else if (feedback.getMessage().isEmpty()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "captureFeedback called with empty message.");
      } else {
         try {
            IScope localScope = this.buildLocalScope(this.getCombinedScopeView(), scopeCallback);
            sentryId = this.getClient().captureFeedback(feedback, hint, localScope);
         } catch (Throwable var6) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing feedback: " + feedback.getMessage(), var6);
         }
      }

      return sentryId;
   }

   @Internal
   @NotNull
   @Override
   public SentryId captureEnvelope(@NotNull SentryEnvelope envelope, @Nullable Hint hint) {
      Objects.requireNonNull(envelope, "SentryEnvelope is required.");
      SentryId sentryId = SentryId.EMPTY_ID;
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureEnvelope' call is a no-op.");
      } else {
         try {
            SentryId capturedEnvelopeId = this.getClient().captureEnvelope(envelope, hint);
            if (capturedEnvelopeId != null) {
               sentryId = capturedEnvelopeId;
            }
         } catch (Throwable var5) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing envelope.", var5);
         }
      }

      return sentryId;
   }

   @NotNull
   @Override
   public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint) {
      return this.captureExceptionInternal(throwable, hint, null);
   }

   @NotNull
   @Override
   public SentryId captureException(@NotNull Throwable throwable, @Nullable Hint hint, @NotNull ScopeCallback callback) {
      return this.captureExceptionInternal(throwable, hint, callback);
   }

   @NotNull
   private SentryId captureExceptionInternal(@NotNull Throwable throwable, @Nullable Hint hint, @Nullable ScopeCallback scopeCallback) {
      SentryId sentryId = SentryId.EMPTY_ID;
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureException' call is a no-op.");
      } else if (throwable == null) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "captureException called with null parameter.");
      } else {
         try {
            SentryEvent event = new SentryEvent(throwable);
            this.assignTraceContext(event);
            IScope localScope = this.buildLocalScope(this.getCombinedScopeView(), scopeCallback);
            sentryId = this.getClient().captureEvent(event, localScope, hint);
         } catch (Throwable var7) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing exception: " + throwable.getMessage(), var7);
         }
      }

      this.updateLastEventId(sentryId);
      return sentryId;
   }

   @Override
   public void captureUserFeedback(@NotNull UserFeedback userFeedback) {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureUserFeedback' call is a no-op.");
      } else {
         try {
            this.getClient().captureUserFeedback(userFeedback);
         } catch (Throwable var3) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing captureUserFeedback: " + userFeedback.toString(), var3);
         }
      }
   }

   @Override
   public void startSession() {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'startSession' call is a no-op.");
      } else {
         Scope.SessionPair pair = this.getCombinedScopeView().startSession();
         if (pair != null) {
            if (pair.getPrevious() != null) {
               Hint hint = HintUtils.createWithTypeCheckHint(new SessionEndHint());
               this.getClient().captureSession(pair.getPrevious(), hint);
            }

            Hint hint = HintUtils.createWithTypeCheckHint(new SessionStartHint());
            this.getClient().captureSession(pair.getCurrent(), hint);
         } else {
            this.getOptions().getLogger().log(SentryLevel.WARNING, "Session could not be started.");
         }
      }
   }

   @Override
   public void endSession() {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'endSession' call is a no-op.");
      } else {
         Session previousSession = this.getCombinedScopeView().endSession();
         if (previousSession != null) {
            Hint hint = HintUtils.createWithTypeCheckHint(new SessionEndHint());
            this.getClient().captureSession(previousSession, hint);
         }
      }
   }

   @Internal
   @NotNull
   public IScope getCombinedScopeView() {
      return this.combinedScope;
   }

   @Override
   public void close() {
      this.close(false);
   }

   @Override
   public void close(boolean isRestarting) {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'close' call is a no-op.");
      } else {
         try {
            for (Integration integration : this.getOptions().getIntegrations()) {
               if (integration instanceof Closeable) {
                  try {
                     ((Closeable)integration).close();
                  } catch (Throwable var6) {
                     this.getOptions().getLogger().log(SentryLevel.WARNING, "Failed to close the integration {}.", integration, var6);
                  }
               }
            }

            this.configureScope(scope -> scope.clear());
            this.configureScope(ScopeType.ISOLATION, scope -> scope.clear());
            this.getOptions().getBackpressureMonitor().close();
            this.getOptions().getTransactionProfiler().close();
            this.getOptions().getContinuousProfiler().close(true);
            this.getOptions().getCompositePerformanceCollector().close();
            this.getOptions().getConnectionStatusProvider().close();
            ISentryExecutorService executorService = this.getOptions().getExecutorService();
            if (isRestarting) {
               try {
                  executorService.submit(() -> executorService.close(this.getOptions().getShutdownTimeoutMillis()));
               } catch (RejectedExecutionException var5) {
                  this.getOptions()
                     .getLogger()
                     .log(SentryLevel.WARNING, "Failed to submit executor service shutdown task during restart. Shutting down synchronously.", var5);
                  executorService.close(this.getOptions().getShutdownTimeoutMillis());
               }
            } else {
               executorService.close(this.getOptions().getShutdownTimeoutMillis());
            }

            this.configureScope(ScopeType.CURRENT, scope -> scope.getClient().close(isRestarting));
            this.configureScope(ScopeType.ISOLATION, scope -> scope.getClient().close(isRestarting));
            this.configureScope(ScopeType.GLOBAL, scope -> scope.getClient().close(isRestarting));
         } catch (Throwable var7) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while closing the Scopes.", var7);
         }
      }
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'addBreadcrumb' call is a no-op.");
      } else if (breadcrumb == null) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "addBreadcrumb called with null parameter.");
      } else {
         this.getCombinedScopeView().addBreadcrumb(breadcrumb, hint);
      }
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
      this.addBreadcrumb(breadcrumb, new Hint());
   }

   @Override
   public void setLevel(@Nullable SentryLevel level) {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setLevel' call is a no-op.");
      } else {
         this.getCombinedScopeView().setLevel(level);
      }
   }

   @Override
   public void setTransaction(@Nullable String transaction) {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setTransaction' call is a no-op.");
      } else if (transaction != null) {
         this.getCombinedScopeView().setTransaction(transaction);
      } else {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Transaction cannot be null");
      }
   }

   @Override
   public void setUser(@Nullable User user) {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setUser' call is a no-op.");
      } else {
         this.getCombinedScopeView().setUser(user);
      }
   }

   @Override
   public void setFingerprint(@NotNull List<String> fingerprint) {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setFingerprint' call is a no-op.");
      } else if (fingerprint == null) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "setFingerprint called with null parameter.");
      } else {
         this.getCombinedScopeView().setFingerprint(fingerprint);
      }
   }

   @Override
   public void clearBreadcrumbs() {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'clearBreadcrumbs' call is a no-op.");
      } else {
         this.getCombinedScopeView().clearBreadcrumbs();
      }
   }

   @Override
   public void setTag(@Nullable String key, @Nullable String value) {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setTag' call is a no-op.");
      } else if (key != null && value != null) {
         this.getCombinedScopeView().setTag(key, value);
      } else {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "setTag called with null parameter.");
      }
   }

   @Override
   public void removeTag(@Nullable String key) {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'removeTag' call is a no-op.");
      } else if (key == null) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "removeTag called with null parameter.");
      } else {
         this.getCombinedScopeView().removeTag(key);
      }
   }

   @Override
   public void setExtra(@Nullable String key, @Nullable String value) {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'setExtra' call is a no-op.");
      } else if (key != null && value != null) {
         this.getCombinedScopeView().setExtra(key, value);
      } else {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "setExtra called with null parameter.");
      }
   }

   @Override
   public void removeExtra(@Nullable String key) {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'removeExtra' call is a no-op.");
      } else if (key == null) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "removeExtra called with null parameter.");
      } else {
         this.getCombinedScopeView().removeExtra(key);
      }
   }

   private void updateLastEventId(@NotNull SentryId lastEventId) {
      this.getCombinedScopeView().setLastEventId(lastEventId);
   }

   @NotNull
   @Override
   public SentryId getLastEventId() {
      return this.getCombinedScopeView().getLastEventId();
   }

   @Override
   public ISentryLifecycleToken pushScope() {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'pushScope' call is a no-op.");
         return NoOpScopesLifecycleToken.getInstance();
      } else {
         IScopes scopes = this.forkedCurrentScope("pushScope");
         return scopes.makeCurrent();
      }
   }

   @Override
   public ISentryLifecycleToken pushIsolationScope() {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'pushIsolationScope' call is a no-op.");
         return NoOpScopesLifecycleToken.getInstance();
      } else {
         IScopes scopes = this.forkedScopes("pushIsolationScope");
         return scopes.makeCurrent();
      }
   }

   @NotNull
   @Override
   public ISentryLifecycleToken makeCurrent() {
      return Sentry.setCurrentScopes(this);
   }

   @Deprecated
   @Override
   public void popScope() {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'popScope' call is a no-op.");
      } else {
         Scopes parent = this.parentScopes;
         if (parent != null) {
            parent.makeCurrent();
         }
      }
   }

   @Override
   public void withScope(@NotNull ScopeCallback callback) {
      if (!this.isEnabled()) {
         try {
            callback.run(NoOpScope.getInstance());
         } catch (Throwable var7) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'withScope' callback.", var7);
         }
      } else {
         IScopes forkedScopes = this.forkedCurrentScope("withScope");

         try {
            ISentryLifecycleToken ignored = forkedScopes.makeCurrent();

            try {
               callback.run(forkedScopes.getScope());
            } catch (Throwable var8) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var6) {
                     var8.addSuppressed(var6);
                  }
               }

               throw var8;
            }

            if (ignored != null) {
               ignored.close();
            }
         } catch (Throwable var9) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'withScope' callback.", var9);
         }
      }
   }

   @Override
   public void withIsolationScope(@NotNull ScopeCallback callback) {
      if (!this.isEnabled()) {
         try {
            callback.run(NoOpScope.getInstance());
         } catch (Throwable var7) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'withIsolationScope' callback.", var7);
         }
      } else {
         IScopes forkedScopes = this.forkedScopes("withIsolationScope");

         try {
            ISentryLifecycleToken ignored = forkedScopes.makeCurrent();

            try {
               callback.run(forkedScopes.getIsolationScope());
            } catch (Throwable var8) {
               if (ignored != null) {
                  try {
                     ignored.close();
                  } catch (Throwable var6) {
                     var8.addSuppressed(var6);
                  }
               }

               throw var8;
            }

            if (ignored != null) {
               ignored.close();
            }
         } catch (Throwable var9) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'withIsolationScope' callback.", var9);
         }
      }
   }

   @Override
   public void configureScope(@Nullable ScopeType scopeType, @NotNull ScopeCallback callback) {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'configureScope' call is a no-op.");
      } else {
         try {
            callback.run(this.combinedScope.getSpecificScope(scopeType));
         } catch (Throwable var4) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'configureScope' callback.", var4);
         }
      }
   }

   @Override
   public void bindClient(@NotNull ISentryClient client) {
      if (client != null) {
         this.getOptions().getLogger().log(SentryLevel.DEBUG, "New client bound to scope.");
         this.getCombinedScopeView().bindClient(client);
      } else {
         this.getOptions().getLogger().log(SentryLevel.DEBUG, "NoOp client bound to scope.");
         this.getCombinedScopeView().bindClient(NoOpSentryClient.getInstance());
      }
   }

   @Override
   public boolean isHealthy() {
      return this.getClient().isHealthy();
   }

   @Override
   public void flush(long timeoutMillis) {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'flush' call is a no-op.");
      } else {
         try {
            this.getClient().flush(timeoutMillis);
         } catch (Throwable var4) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error in the 'client.flush'.", var4);
         }
      }
   }

   @Deprecated
   @NotNull
   @Override
   public IHub clone() {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Disabled Scopes cloned.");
      }

      return new HubScopesWrapper(this.forkedScopes("scopes clone"));
   }

   @Internal
   @NotNull
   @Override
   public SentryId captureTransaction(
      @NotNull SentryTransaction transaction, @Nullable TraceContext traceContext, @Nullable Hint hint, @Nullable ProfilingTraceData profilingTraceData
   ) {
      Objects.requireNonNull(transaction, "transaction is required");
      SentryId sentryId = SentryId.EMPTY_ID;
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureTransaction' call is a no-op.");
      } else if (!transaction.isFinished()) {
         this.getOptions()
            .getLogger()
            .log(SentryLevel.WARNING, "Transaction: %s is not finished and this 'captureTransaction' call is a no-op.", transaction.getEventId());
      } else if (!Boolean.TRUE.equals(transaction.isSampled())) {
         this.getOptions().getLogger().log(SentryLevel.DEBUG, "Transaction %s was dropped due to sampling decision.", transaction.getEventId());
         if (this.getOptions().getBackpressureMonitor().getDownsampleFactor() > 0) {
            this.getOptions().getClientReportRecorder().recordLostEvent(DiscardReason.BACKPRESSURE, DataCategory.Transaction);
            this.getOptions().getClientReportRecorder().recordLostEvent(DiscardReason.BACKPRESSURE, DataCategory.Span, transaction.getSpans().size() + 1);
         } else {
            this.getOptions().getClientReportRecorder().recordLostEvent(DiscardReason.SAMPLE_RATE, DataCategory.Transaction);
            this.getOptions().getClientReportRecorder().recordLostEvent(DiscardReason.SAMPLE_RATE, DataCategory.Span, transaction.getSpans().size() + 1);
         }
      } else {
         try {
            sentryId = this.getClient().captureTransaction(transaction, traceContext, this.getCombinedScopeView(), hint, profilingTraceData);
         } catch (Throwable var7) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing transaction with id: " + transaction.getEventId(), var7);
         }
      }

      return sentryId;
   }

   @Internal
   @NotNull
   @Override
   public SentryId captureProfileChunk(@NotNull ProfileChunk profilingContinuousData) {
      Objects.requireNonNull(profilingContinuousData, "profilingContinuousData is required");
      SentryId sentryId = SentryId.EMPTY_ID;
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureTransaction' call is a no-op.");
      } else {
         try {
            sentryId = this.getClient().captureProfileChunk(profilingContinuousData, this.getScope());
         } catch (Throwable var4) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing profile chunk with id: " + profilingContinuousData.getChunkId(), var4);
         }
      }

      return sentryId;
   }

   @NotNull
   @Override
   public ITransaction startTransaction(@NotNull TransactionContext transactionContext, @NotNull TransactionOptions transactionOptions) {
      return this.createTransaction(transactionContext, transactionOptions);
   }

   @NotNull
   private ITransaction createTransaction(@NotNull TransactionContext transactionContext, @NotNull TransactionOptions transactionOptions) {
      Objects.requireNonNull(transactionContext, "transactionContext is required");
      transactionContext.setOrigin(transactionOptions.getOrigin());
      ITransaction transaction;
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'startTransaction' returns a no-op.");
         transaction = NoOpTransaction.getInstance();
      } else if (SpanUtils.isIgnored(this.getOptions().getIgnoredSpanOrigins(), transactionContext.getOrigin())) {
         this.getOptions()
            .getLogger()
            .log(SentryLevel.DEBUG, "Returning no-op for span origin %s as the SDK has been configured to ignore it", transactionContext.getOrigin());
         transaction = NoOpTransaction.getInstance();
      } else if (!this.getOptions().getInstrumenter().equals(transactionContext.getInstrumenter())) {
         this.getOptions()
            .getLogger()
            .log(
               SentryLevel.DEBUG,
               "Returning no-op for instrumenter %s as the SDK has been configured to use instrumenter %s",
               transactionContext.getInstrumenter(),
               this.getOptions().getInstrumenter()
            );
         transaction = NoOpTransaction.getInstance();
      } else if (!this.getOptions().isTracingEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.INFO, "Tracing is disabled and this 'startTransaction' returns a no-op.");
         transaction = NoOpTransaction.getInstance();
      } else {
         Double sampleRand = this.getSampleRand(transactionContext);
         SamplingContext samplingContext = new SamplingContext(transactionContext, transactionOptions.getCustomSamplingContext(), sampleRand, null);
         TracesSampler tracesSampler = this.getOptions().getInternalTracesSampler();
         TracesSamplingDecision samplingDecision = tracesSampler.sample(samplingContext);
         transactionContext.setSamplingDecision(samplingDecision);
         ISpanFactory maybeSpanFactory = transactionOptions.getSpanFactory();
         ISpanFactory spanFactory = maybeSpanFactory == null ? this.getOptions().getSpanFactory() : maybeSpanFactory;
         if (samplingDecision.getSampled()
            && this.getOptions().isContinuousProfilingEnabled()
            && this.getOptions().getProfileLifecycle() == ProfileLifecycle.TRACE
            && transactionContext.getProfilerId().equals(SentryId.EMPTY_ID)) {
            this.getOptions().getContinuousProfiler().startProfiler(ProfileLifecycle.TRACE, this.getOptions().getInternalTracesSampler());
         }

         transaction = spanFactory.createTransaction(transactionContext, this, transactionOptions, this.compositePerformanceCollector);
         if (samplingDecision.getSampled() && samplingDecision.getProfileSampled()) {
            ITransactionProfiler transactionProfiler = this.getOptions().getTransactionProfiler();
            if (!transactionProfiler.isRunning()) {
               transactionProfiler.start();
               transactionProfiler.bindTransaction(transaction);
            } else if (transactionOptions.isAppStartTransaction()) {
               transactionProfiler.bindTransaction(transaction);
            }
         }
      }

      if (transactionOptions.isBindToScope()) {
         transaction.makeCurrent();
      }

      return transaction;
   }

   @NotNull
   private Double getSampleRand(@NotNull TransactionContext transactionContext) {
      Baggage baggage = transactionContext.getBaggage();
      if (baggage != null) {
         Double sampleRandFromBaggageMaybe = baggage.getSampleRand();
         if (sampleRandFromBaggageMaybe != null) {
            return sampleRandFromBaggageMaybe;
         }
      }

      return this.getCombinedScopeView().getPropagationContext().getSampleRand();
   }

   @Override
   public void startProfiler() {
      if (this.getOptions().isContinuousProfilingEnabled()) {
         if (this.getOptions().getProfileLifecycle() != ProfileLifecycle.MANUAL) {
            this.getOptions()
               .getLogger()
               .log(SentryLevel.WARNING, "Profiling lifecycle is %s. Profiling cannot be started manually.", this.getOptions().getProfileLifecycle().name());
            return;
         }

         this.getOptions().getContinuousProfiler().startProfiler(ProfileLifecycle.MANUAL, this.getOptions().getInternalTracesSampler());
      } else if (this.getOptions().isProfilingEnabled()) {
         this.getOptions()
            .getLogger()
            .log(SentryLevel.WARNING, "Continuous Profiling is not enabled. Set profilesSampleRate and profilesSampler to null to enable it.");
      }
   }

   @Override
   public void stopProfiler() {
      if (this.getOptions().isContinuousProfilingEnabled()) {
         if (this.getOptions().getProfileLifecycle() != ProfileLifecycle.MANUAL) {
            this.getOptions()
               .getLogger()
               .log(SentryLevel.WARNING, "Profiling lifecycle is %s. Profiling cannot be stopped manually.", this.getOptions().getProfileLifecycle().name());
            return;
         }

         this.getOptions().getLogger().log(SentryLevel.DEBUG, "Stopped continuous Profiling.");
         this.getOptions().getContinuousProfiler().stopProfiler(ProfileLifecycle.MANUAL);
      } else {
         this.getOptions()
            .getLogger()
            .log(SentryLevel.WARNING, "Continuous Profiling is not enabled. Set profilesSampleRate and profilesSampler to null to enable it.");
      }
   }

   @Internal
   @Override
   public void setSpanContext(@NotNull Throwable throwable, @NotNull ISpan span, @NotNull String transactionName) {
      this.getCombinedScopeView().setSpanContext(throwable, span, transactionName);
   }

   @Nullable
   @Override
   public ISpan getSpan() {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'getSpan' call is a no-op.");
         return null;
      } else {
         return this.getCombinedScopeView().getSpan();
      }
   }

   @Override
   public void setActiveSpan(@Nullable ISpan span) {
      this.getCombinedScopeView().setActiveSpan(span);
   }

   @Internal
   @Nullable
   @Override
   public ITransaction getTransaction() {
      ITransaction span = null;
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'getTransaction' call is a no-op.");
      } else {
         span = this.getCombinedScopeView().getTransaction();
      }

      return span;
   }

   @NotNull
   @Override
   public SentryOptions getOptions() {
      return this.combinedScope.getOptions();
   }

   @Nullable
   @Override
   public Boolean isCrashedLastRun() {
      return SentryCrashLastRunState.getInstance().isCrashedLastRun(this.getOptions().getCacheDirPath(), !this.getOptions().isEnableAutoSessionTracking());
   }

   @Override
   public void reportFullyDisplayed() {
      if (this.getOptions().isEnableTimeToFullDisplayTracing()) {
         this.getOptions().getFullyDisplayedReporter().reportFullyDrawn();
      }
   }

   @Nullable
   @Override
   public TransactionContext continueTrace(@Nullable String sentryTrace, @Nullable List<String> baggageHeaders) {
      PropagationContext propagationContext = PropagationContext.fromHeaders(this.getOptions().getLogger(), sentryTrace, baggageHeaders);
      this.configureScope(scope -> scope.withPropagationContext(oldPropagationContext -> scope.setPropagationContext(propagationContext)));
      return this.getOptions().isTracingEnabled() ? TransactionContext.fromPropagationContext(propagationContext) : null;
   }

   @Nullable
   @Override
   public SentryTraceHeader getTraceparent() {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'getTraceparent' call is a no-op.");
      } else {
         TracingUtils.TracingHeaders headers = TracingUtils.trace(this, null, this.getSpan());
         if (headers != null) {
            return headers.getSentryTraceHeader();
         }
      }

      return null;
   }

   @Nullable
   @Override
   public BaggageHeader getBaggage() {
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'getBaggage' call is a no-op.");
      } else {
         TracingUtils.TracingHeaders headers = TracingUtils.trace(this, null, this.getSpan());
         if (headers != null) {
            return headers.getBaggageHeader();
         }
      }

      return null;
   }

   @NotNull
   @Override
   public SentryId captureCheckIn(@NotNull CheckIn checkIn) {
      SentryId sentryId = SentryId.EMPTY_ID;
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureCheckIn' call is a no-op.");
      } else {
         try {
            sentryId = this.getClient().captureCheckIn(checkIn, this.getCombinedScopeView(), null);
         } catch (Throwable var4) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing check-in for slug", var4);
         }
      }

      this.updateLastEventId(sentryId);
      return sentryId;
   }

   @NotNull
   @Override
   public SentryId captureReplay(@NotNull SentryReplayEvent replay, @Nullable Hint hint) {
      SentryId sentryId = SentryId.EMPTY_ID;
      if (!this.isEnabled()) {
         this.getOptions().getLogger().log(SentryLevel.WARNING, "Instance is disabled and this 'captureReplay' call is a no-op.");
      } else {
         try {
            sentryId = this.getClient().captureReplayEvent(replay, this.getCombinedScopeView(), hint);
         } catch (Throwable var5) {
            this.getOptions().getLogger().log(SentryLevel.ERROR, "Error while capturing replay", var5);
         }
      }

      return sentryId;
   }

   @Internal
   @Nullable
   @Override
   public RateLimiter getRateLimiter() {
      return this.getClient().getRateLimiter();
   }

   @NotNull
   @Override
   public ILoggerApi logger() {
      return this.logger;
   }

   @Override
   public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
      this.combinedScope.addFeatureFlag(flag, result);
   }

   private static void validateOptions(@NotNull SentryOptions options) {
      Objects.requireNonNull(options, "SentryOptions is required.");
      if (options.getDsn() == null || options.getDsn().isEmpty()) {
         throw new IllegalArgumentException("Scopes requires a DSN to be instantiated. Considering using the NoOpScopes if no DSN is available.");
      }
   }
}
