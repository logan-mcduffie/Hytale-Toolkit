package io.sentry;

import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.internal.eventprocessor.EventProcessorAndOrder;
import io.sentry.protocol.Contexts;
import io.sentry.protocol.FeatureFlags;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface IScope {
   @Nullable
   SentryLevel getLevel();

   void setLevel(@Nullable SentryLevel var1);

   @Nullable
   String getTransactionName();

   void setTransaction(@NotNull String var1);

   @Nullable
   ISpan getSpan();

   @Internal
   void setActiveSpan(@Nullable ISpan var1);

   void setTransaction(@Nullable ITransaction var1);

   @Nullable
   User getUser();

   void setUser(@Nullable User var1);

   @Internal
   @Nullable
   String getScreen();

   @Internal
   void setScreen(@Nullable String var1);

   @Internal
   @NotNull
   SentryId getReplayId();

   @Internal
   void setReplayId(@NotNull SentryId var1);

   @Nullable
   Request getRequest();

   void setRequest(@Nullable Request var1);

   @Internal
   @NotNull
   List<String> getFingerprint();

   void setFingerprint(@NotNull List<String> var1);

   @Internal
   @NotNull
   Queue<Breadcrumb> getBreadcrumbs();

   void addBreadcrumb(@NotNull Breadcrumb var1, @Nullable Hint var2);

   void addBreadcrumb(@NotNull Breadcrumb var1);

   void clearBreadcrumbs();

   void clearTransaction();

   @Nullable
   ITransaction getTransaction();

   void clear();

   @Internal
   @NotNull
   Map<String, String> getTags();

   void setTag(@Nullable String var1, @Nullable String var2);

   void removeTag(@Nullable String var1);

   @Internal
   @NotNull
   Map<String, Object> getExtras();

   void setExtra(@Nullable String var1, @Nullable String var2);

   void removeExtra(@Nullable String var1);

   @NotNull
   Contexts getContexts();

   void setContexts(@Nullable String var1, @Nullable Object var2);

   void setContexts(@Nullable String var1, @Nullable Boolean var2);

   void setContexts(@Nullable String var1, @Nullable String var2);

   void setContexts(@Nullable String var1, @Nullable Number var2);

   void setContexts(@Nullable String var1, @Nullable Collection<?> var2);

   void setContexts(@Nullable String var1, @Nullable Object[] var2);

   void setContexts(@Nullable String var1, @Nullable Character var2);

   void removeContexts(@Nullable String var1);

   @NotNull
   List<Attachment> getAttachments();

   void addAttachment(@NotNull Attachment var1);

   void clearAttachments();

   @Internal
   @NotNull
   List<EventProcessor> getEventProcessors();

   @Internal
   @NotNull
   List<EventProcessorAndOrder> getEventProcessorsWithOrder();

   void addEventProcessor(@NotNull EventProcessor var1);

   @Nullable
   Session withSession(@NotNull Scope.IWithSession var1);

   @Nullable
   Scope.SessionPair startSession();

   @Nullable
   Session endSession();

   @Internal
   void withTransaction(@NotNull Scope.IWithTransaction var1);

   @NotNull
   SentryOptions getOptions();

   @Internal
   @Nullable
   Session getSession();

   @Internal
   void clearSession();

   @Internal
   void setPropagationContext(@NotNull PropagationContext var1);

   @Internal
   @NotNull
   PropagationContext getPropagationContext();

   @Internal
   @NotNull
   PropagationContext withPropagationContext(@NotNull Scope.IWithPropagationContext var1);

   @NotNull
   IScope clone();

   void setLastEventId(@NotNull SentryId var1);

   @NotNull
   SentryId getLastEventId();

   void bindClient(@NotNull ISentryClient var1);

   @NotNull
   ISentryClient getClient();

   @Internal
   void assignTraceContext(@NotNull SentryEvent var1);

   @Internal
   void setSpanContext(@NotNull Throwable var1, @NotNull ISpan var2, @NotNull String var3);

   @Internal
   void replaceOptions(@NotNull SentryOptions var1);

   void addFeatureFlag(@Nullable String var1, @Nullable Boolean var2);

   @Internal
   @Nullable
   FeatureFlags getFeatureFlags();

   @Internal
   @NotNull
   IFeatureFlagBuffer getFeatureFlagBuffer();
}
