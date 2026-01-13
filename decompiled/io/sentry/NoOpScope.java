package io.sentry;

import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.featureflags.NoOpFeatureFlagBuffer;
import io.sentry.internal.eventprocessor.EventProcessorAndOrder;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.FeatureFlags;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import io.sentry.util.LazyEvaluator;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class NoOpScope implements IScope {
   private static final NoOpScope instance = new NoOpScope();
   @NotNull
   private final LazyEvaluator<SentryOptions> emptyOptions = new LazyEvaluator<>(() -> SentryOptions.empty());

   private NoOpScope() {
   }

   public static NoOpScope getInstance() {
      return instance;
   }

   @Nullable
   @Override
   public SentryLevel getLevel() {
      return null;
   }

   @Override
   public void setLevel(@Nullable SentryLevel level) {
   }

   @Nullable
   @Override
   public String getTransactionName() {
      return null;
   }

   @Override
   public void setTransaction(@NotNull String transaction) {
   }

   @Nullable
   @Override
   public ISpan getSpan() {
      return null;
   }

   @Override
   public void setActiveSpan(@Nullable ISpan span) {
   }

   @Override
   public void setTransaction(@Nullable ITransaction transaction) {
   }

   @Nullable
   @Override
   public User getUser() {
      return null;
   }

   @Override
   public void setUser(@Nullable User user) {
   }

   @Internal
   @Nullable
   @Override
   public String getScreen() {
      return null;
   }

   @Internal
   @Override
   public void setScreen(@Nullable String screen) {
   }

   @NotNull
   @Override
   public SentryId getReplayId() {
      return SentryId.EMPTY_ID;
   }

   @Override
   public void setReplayId(@Nullable SentryId replayId) {
   }

   @Nullable
   @Override
   public Request getRequest() {
      return null;
   }

   @Override
   public void setRequest(@Nullable Request request) {
   }

   @Internal
   @NotNull
   @Override
   public List<String> getFingerprint() {
      return new ArrayList<>();
   }

   @Override
   public void setFingerprint(@NotNull List<String> fingerprint) {
   }

   @Internal
   @NotNull
   @Override
   public Queue<Breadcrumb> getBreadcrumbs() {
      return new ArrayDeque<>();
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb, @Nullable Hint hint) {
   }

   @Override
   public void addBreadcrumb(@NotNull Breadcrumb breadcrumb) {
   }

   @Override
   public void clearBreadcrumbs() {
   }

   @Override
   public void clearTransaction() {
   }

   @Nullable
   @Override
   public ITransaction getTransaction() {
      return null;
   }

   @Override
   public void clear() {
   }

   @Internal
   @NotNull
   @Override
   public Map<String, String> getTags() {
      return new HashMap<>();
   }

   @Override
   public void setTag(@Nullable String key, @Nullable String value) {
   }

   @Override
   public void removeTag(@Nullable String key) {
   }

   @Internal
   @NotNull
   @Override
   public Map<String, Object> getExtras() {
      return new HashMap<>();
   }

   @Override
   public void setExtra(@Nullable String key, @Nullable String value) {
   }

   @Override
   public void removeExtra(@Nullable String key) {
   }

   @NotNull
   @Override
   public Contexts getContexts() {
      return new Contexts();
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Object value) {
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Boolean value) {
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable String value) {
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Number value) {
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Collection<?> value) {
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Object[] value) {
   }

   @Override
   public void setContexts(@Nullable String key, @Nullable Character value) {
   }

   @Override
   public void removeContexts(@Nullable String key) {
   }

   @Internal
   @NotNull
   @Override
   public List<Attachment> getAttachments() {
      return new ArrayList<>();
   }

   @Override
   public void addAttachment(@NotNull Attachment attachment) {
   }

   @Override
   public void clearAttachments() {
   }

   @Internal
   @NotNull
   @Override
   public List<EventProcessor> getEventProcessors() {
      return new ArrayList<>();
   }

   @Internal
   @NotNull
   @Override
   public List<EventProcessorAndOrder> getEventProcessorsWithOrder() {
      return new ArrayList<>();
   }

   @Override
   public void addEventProcessor(@NotNull EventProcessor eventProcessor) {
   }

   @Internal
   @Nullable
   @Override
   public Session withSession(Scope.@NotNull IWithSession sessionCallback) {
      return null;
   }

   @Internal
   @Nullable
   @Override
   public Scope.SessionPair startSession() {
      return null;
   }

   @Internal
   @Nullable
   @Override
   public Session endSession() {
      return null;
   }

   @Internal
   @Override
   public void withTransaction(Scope.@NotNull IWithTransaction callback) {
   }

   @Internal
   @NotNull
   @Override
   public SentryOptions getOptions() {
      return this.emptyOptions.getValue();
   }

   @Internal
   @Nullable
   @Override
   public Session getSession() {
      return null;
   }

   @Internal
   @Override
   public void clearSession() {
   }

   @Internal
   @Override
   public void setPropagationContext(@NotNull PropagationContext propagationContext) {
   }

   @Internal
   @NotNull
   @Override
   public PropagationContext getPropagationContext() {
      return new PropagationContext();
   }

   @Internal
   @NotNull
   @Override
   public PropagationContext withPropagationContext(Scope.@NotNull IWithPropagationContext callback) {
      return new PropagationContext();
   }

   @Override
   public void setLastEventId(@NotNull SentryId lastEventId) {
   }

   @NotNull
   @Override
   public IScope clone() {
      return getInstance();
   }

   @NotNull
   @Override
   public SentryId getLastEventId() {
      return SentryId.EMPTY_ID;
   }

   @Override
   public void bindClient(@NotNull ISentryClient client) {
   }

   @NotNull
   @Override
   public ISentryClient getClient() {
      return NoOpSentryClient.getInstance();
   }

   @Override
   public void assignTraceContext(@NotNull SentryEvent event) {
   }

   @Override
   public void setSpanContext(@NotNull Throwable throwable, @NotNull ISpan span, @NotNull String transactionName) {
   }

   @Override
   public void replaceOptions(@NotNull SentryOptions options) {
   }

   @Override
   public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
   }

   @Nullable
   @Override
   public FeatureFlags getFeatureFlags() {
      return null;
   }

   @NotNull
   @Override
   public IFeatureFlagBuffer getFeatureFlagBuffer() {
      return NoOpFeatureFlagBuffer.getInstance();
   }
}
