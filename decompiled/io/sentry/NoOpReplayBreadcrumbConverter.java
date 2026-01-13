package io.sentry;

import io.sentry.rrweb.RRWebEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class NoOpReplayBreadcrumbConverter implements ReplayBreadcrumbConverter {
   private static final NoOpReplayBreadcrumbConverter instance = new NoOpReplayBreadcrumbConverter();

   public static NoOpReplayBreadcrumbConverter getInstance() {
      return instance;
   }

   private NoOpReplayBreadcrumbConverter() {
   }

   @Nullable
   @Override
   public RRWebEvent convert(@NotNull Breadcrumb breadcrumb) {
      return null;
   }
}
