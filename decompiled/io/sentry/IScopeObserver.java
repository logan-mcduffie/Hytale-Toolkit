package io.sentry;

import io.sentry.protocol.Contexts;
import io.sentry.protocol.Request;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.User;
import java.util.Collection;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IScopeObserver {
   void setUser(@Nullable User var1);

   void addBreadcrumb(@NotNull Breadcrumb var1);

   void setBreadcrumbs(@NotNull Collection<Breadcrumb> var1);

   void setTag(@NotNull String var1, @NotNull String var2);

   void removeTag(@NotNull String var1);

   void setTags(@NotNull Map<String, String> var1);

   void setExtra(@NotNull String var1, @NotNull String var2);

   void removeExtra(@NotNull String var1);

   void setExtras(@NotNull Map<String, Object> var1);

   void setRequest(@Nullable Request var1);

   void setFingerprint(@NotNull Collection<String> var1);

   void setLevel(@Nullable SentryLevel var1);

   void setContexts(@NotNull Contexts var1);

   void setTransaction(@Nullable String var1);

   void setTrace(@Nullable SpanContext var1, @NotNull IScope var2);

   void setReplayId(@NotNull SentryId var1);
}
