package io.sentry;

import io.sentry.rrweb.RRWebEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface ReplayBreadcrumbConverter {
   @Nullable
   RRWebEvent convert(@NotNull Breadcrumb var1);
}
