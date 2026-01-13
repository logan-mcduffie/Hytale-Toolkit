package io.sentry;

import io.sentry.featureflags.FeatureFlagBuffer;
import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.internal.eventprocessor.EventProcessorAndOrder;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.FeatureFlags;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.util.EventProcessorUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class CombinedScopeView implements IScope {
   private final IScope globalScope;
   private final IScope isolationScope;
   private final IScope scope;

   public CombinedScopeView(@NotNull IScope globalScope, @NotNull IScope isolationScope, @NotNull IScope scope) {
      this.globalScope = globalScope;
      this.isolationScope = isolationScope;
      this.scope = scope;
   }

   @Nullable
   @Override
   public SentryLevel getLevel() {
      SentryLevel current = this.scope.getLevel();
      if (current != null) {
         return current;
      } else {
         SentryLevel isolation = this.isolationScope.getLevel();
         return isolation != null ? isolation : this.globalScope.getLevel();
      }
   }

   @Override
   public void setLevel(@Nullable SentryLevel level) {
      this.getDefaultWriteScope().setLevel(level);
   }

   @Nullable
   @Override
   public String getTransactionName() {
      String current = this.scope.getTransactionName();
      if (current != null) {
         return current;
      } else {
         String isolation = this.isolationScope.getTransactionName();
         return isolation != null ? isolation : this.globalScope.getTransactionName();
      }
   }

   @Override
   public void setTransaction(@NotNull String transaction) {
      this.getDefaultWriteScope().setTransaction(transaction);
   }

   @Nullable
   @Override
   public ISpan getSpan() {
      ISpan current = this.scope.getSpan();
      if (current != null) {
         return current;
      } else {
         ISpan isolation = this.isolationScope.getSpan();
         return isolation != null ? isolation : this.globalScope.getSpan();
      }
   }

   @Override
   public void setActiveSpan(@Nullable ISpan span) {
      this.scope.setActiveSpan(span);
   }

   @Override
   public void setTransaction(@Nullable ITransaction transaction) {
      this.getDefaultWriteScope().setTransaction(transaction);
   }

   @Nullable
   @Override
   public User getUser() {
      User current = this.scope.getUser();
      if (current != null) {
         return current;
      } else {
         User isolation = this.isolationScope.getUser();
         return isolation != null ? isolation : this.globalScope.getUser();
      }
   }

   @Override
   public void setUser(@Nullable User user) {
      this.getDefaultWriteScope().setUser(user);
   }

   @Nullable
   @Override
   public String getScreen() {
      String current = this.scope.getScreen();
      if (current != null) {
         return current;
      } else {
         String isolation = this.isolationScope.getScreen();
         return isolation != null ? isolation : this.globalScope.getScreen();
      }
   }

   @Override
   public void setScreen(@Nullable String screen) {
      this.getDefaultWriteScope().setScreen(screen);
   }

   @Nullable
   @Override
   public Request getRequest() {
      Request current = this.scope.getRequest();
      if (current != null) {
         return current;
      } else {
         Request isolation = this.isolationScope.getRequest();
         return isolation != null ? isolation : this.globalScope.getRequest();
      }
   }

   @Override
   public void setRequest(@Nullable Request request) {
      this.getDefaultWriteScope().setRequest(request);
   }

   @NotNull
   @Override
   public List<String> getFingerprint() {
      List<String> current = this.scope.getFingerprint();
      if (!current.isEmpty()) {
         return current;
      } else {
         List<String> isolation = this.isolationScope.getFingerprint();
         return !isolation.isEmpty() ? isolation : this.globalScope.getFingerprint();
      }
   }

   @Override
   public void setFingerprint(@NotNull List<String> fingerprint) {
      this.getDefaultWriteScope().setFingerprint(fingerprint);
   }

   @NotNull
   @Override
   public Queue<Breadcrumb> getBreadcrumbs() {
      List<Breadcrumb> allBreadcrumbs = new ArrayList<>();
      allBreadcrumbs.addAll(this.globalScope.getBreadcrumbs());
      allBreadcrumbs.addAll(this.isolationScope.getBreadcrumbs());
      allBreadcrumbs.addAll(this.scope.getBreadcrumbs());
      Collections.sort(allBreadcrumbs);
      Queue<Breadcrumb> breadcrumbs = Scope.createBreadcrumbsList(this.scope.getOptions().getMaxBreadcrumbs());
      breadcrumbs.addAll(allBreadcrumbs);
      return breadcrumbs;
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
      this.getDefaultWriteScope().addBreadcrumb(breadcrumb, hint);
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
      this.getDefaultWriteScope().addBreadcrumb(breadcrumb);
   }

   @Override
   public void clearBreadcrumbs() {
      this.getDefaultWriteScope().clearBreadcrumbs();
   }

   @Override
   public void clearTransaction() {
      this.getDefaultWriteScope().clearTransaction();
   }

   @Nullable
   @Override
   public ITransaction getTransaction() {
      ITransaction current = this.scope.getTransaction();
      if (current != null) {
         return current;
      } else {
         ITransaction isolation = this.isolationScope.getTransaction();
         return isolation != null ? isolation : this.globalScope.getTransaction();
      }
   }

   @Override
   public void clear() {
      this.getDefaultWriteScope().clear();
   }

   @NotNull
   @Override
   public Map<String, String> getTags() {
      Map<String, String> allTags = new ConcurrentHashMap<>();
      allTags.putAll(this.globalScope.getTags());
      allTags.putAll(this.isolationScope.getTags());
      allTags.putAll(this.scope.getTags());
      return allTags;
   }

   @Override
   public void setTag(@Nullable String key, @Nullable String value) {
      this.getDefaultWriteScope().setTag(key, value);
   }

   @Override
   public void removeTag(@Nullable String key) {
      this.getDefaultWriteScope().removeTag(key);
   }

   @NotNull
   @Override
   public Map<String, Object> getExtras() {
      Map<String, Object> allTags = new ConcurrentHashMap<>();
      allTags.putAll(this.globalScope.getExtras());
      allTags.putAll(this.isolationScope.getExtras());
      allTags.putAll(this.scope.getExtras());
      return allTags;
   }

   @Override
   public void setExtra(@Nullable String key, @Nullable String value) {
      this.getDefaultWriteScope().setExtra(key, value);
   }

   @Override
   public void removeExtra(@Nullable String key) {
      this.getDefaultWriteScope().removeExtra(key);
   }

   @NotNull
   @Override
   public Contexts getContexts() {
      return new CombinedContextsView(
         this.globalScope.getContexts(), this.isolationScope.getContexts(), this.scope.getContexts(), this.getOptions().getDefaultScopeType()
      );
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Object value) {
      this.getDefaultWriteScope().setContexts(key, value);
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Boolean value) {
      this.getDefaultWriteScope().setContexts(key, value);
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable String value) {
      this.getDefaultWriteScope().setContexts(key, value);
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Number value) {
      this.getDefaultWriteScope().setContexts(key, value);
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Collection<?> value) {
      this.getDefaultWriteScope().setContexts(key, value);
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Object[] value) {
      this.getDefaultWriteScope().setContexts(key, value);
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Character value) {
      this.getDefaultWriteScope().setContexts(key, value);
   }

   @Override
   public void removeContexts(@Nullable String key) {
      this.getDefaultWriteScope().removeContexts(key);
   }

   @NotNull
   private IScope getDefaultWriteScope() {
      return this.getSpecificScope(null);
   }

   IScope getSpecificScope(@Nullable ScopeType scopeType) {
      if (scopeType != null) {
         switch (scopeType) {
            case CURRENT:
               return this.scope;
            case ISOLATION:
               return this.isolationScope;
            case GLOBAL:
               return this.globalScope;
            case COMBINED:
               return this;
         }
      }

      switch (this.getOptions().getDefaultScopeType()) {
         case CURRENT:
            return this.scope;
         case ISOLATION:
            return this.isolationScope;
         case GLOBAL:
            return this.globalScope;
         default:
            return this.scope;
      }
   }

   @NotNull
   @Override
   public List<Attachment> getAttachments() {
      List<Attachment> allAttachments = new CopyOnWriteArrayList<>();
      allAttachments.addAll(this.globalScope.getAttachments());
      allAttachments.addAll(this.isolationScope.getAttachments());
      allAttachments.addAll(this.scope.getAttachments());
      return allAttachments;
   }

   @Override
   public void addAttachment(@NotNull Attachment attachment) {
      this.getDefaultWriteScope().addAttachment(attachment);
   }

   @Override
   public void clearAttachments() {
      this.getDefaultWriteScope().clearAttachments();
   }

   @NotNull
   @Override
   public List<EventProcessorAndOrder> getEventProcessorsWithOrder() {
      List<EventProcessorAndOrder> allEventProcessors = new CopyOnWriteArrayList<>();
      allEventProcessors.addAll(this.globalScope.getEventProcessorsWithOrder());
      allEventProcessors.addAll(this.isolationScope.getEventProcessorsWithOrder());
      allEventProcessors.addAll(this.scope.getEventProcessorsWithOrder());
      Collections.sort(allEventProcessors);
      return allEventProcessors;
   }

   @NotNull
   @Override
   public List<EventProcessor> getEventProcessors() {
      return EventProcessorUtils.unwrap(this.getEventProcessorsWithOrder());
   }

   @Override
   public void addEventProcessor(@NotNull EventProcessor eventProcessor) {
      this.getDefaultWriteScope().addEventProcessor(eventProcessor);
   }

   @Nullable
   @Override
   public Session withSession(Scope.@NotNull IWithSession sessionCallback) {
      return this.getDefaultWriteScope().withSession(sessionCallback);
   }

   @Nullable
   @Override
   public Scope.SessionPair startSession() {
      return this.getDefaultWriteScope().startSession();
   }

   @Nullable
   @Override
   public Session endSession() {
      return this.getDefaultWriteScope().endSession();
   }

   @Override
   public void withTransaction(Scope.@NotNull IWithTransaction callback) {
      this.getDefaultWriteScope().withTransaction(callback);
   }

   @NotNull
   @Override
   public SentryOptions getOptions() {
      return this.globalScope.getOptions();
   }

   @Nullable
   @Override
   public Session getSession() {
      Session current = this.scope.getSession();
      if (current != null) {
         return current;
      } else {
         Session isolation = this.isolationScope.getSession();
         return isolation != null ? isolation : this.globalScope.getSession();
      }
   }

   @Override
   public void clearSession() {
      this.getDefaultWriteScope().clearSession();
   }

   @Override
   public void setPropagationContext(@NotNull PropagationContext propagationContext) {
      this.getDefaultWriteScope().setPropagationContext(propagationContext);
   }

   @Internal
   @NotNull
   @Override
   public PropagationContext getPropagationContext() {
      return this.getDefaultWriteScope().getPropagationContext();
   }

   @NotNull
   @Override
   public PropagationContext withPropagationContext(Scope.@NotNull IWithPropagationContext callback) {
      return this.getDefaultWriteScope().withPropagationContext(callback);
   }

   @NotNull
   @Override
   public IScope clone() {
      return new CombinedScopeView(this.globalScope, this.isolationScope.clone(), this.scope.clone());
   }

   @Override
   public void setLastEventId(@NotNull SentryId lastEventId) {
      this.globalScope.setLastEventId(lastEventId);
      this.isolationScope.setLastEventId(lastEventId);
      this.scope.setLastEventId(lastEventId);
   }

   @NotNull
   @Override
   public SentryId getLastEventId() {
      return this.globalScope.getLastEventId();
   }

   @Override
   public void bindClient(@NotNull ISentryClient client) {
      this.getDefaultWriteScope().bindClient(client);
   }

   @NotNull
   @Override
   public ISentryClient getClient() {
      ISentryClient current = this.scope.getClient();
      if (!(current instanceof NoOpSentryClient)) {
         return current;
      } else {
         ISentryClient isolation = this.isolationScope.getClient();
         return !(isolation instanceof NoOpSentryClient) ? isolation : this.globalScope.getClient();
      }
   }

   @Override
   public void assignTraceContext(@NotNull SentryEvent event) {
      this.globalScope.assignTraceContext(event);
   }

   @Override
   public void setSpanContext(@NotNull Throwable throwable, @NotNull ISpan span, @NotNull String transactionName) {
      this.globalScope.setSpanContext(throwable, span, transactionName);
   }

   @Internal
   @Override
   public void replaceOptions(@NotNull SentryOptions options) {
      this.globalScope.replaceOptions(options);
   }

   @NotNull
   @Override
   public SentryId getReplayId() {
      SentryId current = this.scope.getReplayId();
      if (!SentryId.EMPTY_ID.equals(current)) {
         return current;
      } else {
         SentryId isolation = this.isolationScope.getReplayId();
         return !SentryId.EMPTY_ID.equals(isolation) ? isolation : this.globalScope.getReplayId();
      }
   }

   @Override
   public void setReplayId(@NotNull SentryId replayId) {
      this.getDefaultWriteScope().setReplayId(replayId);
   }

   @Override
   public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
      this.getDefaultWriteScope().addFeatureFlag(flag, result);
      ISpan span = this.getSpan();
      if (span != null) {
         span.addFeatureFlag(flag, result);
      }
   }

   @Nullable
   @Override
   public FeatureFlags getFeatureFlags() {
      return this.getFeatureFlagBuffer().getFeatureFlags();
   }

   @NotNull
   @Override
   public IFeatureFlagBuffer getFeatureFlagBuffer() {
      return FeatureFlagBuffer.merged(
         this.getOptions(), this.globalScope.getFeatureFlagBuffer(), this.isolationScope.getFeatureFlagBuffer(), this.scope.getFeatureFlagBuffer()
      );
   }
}
