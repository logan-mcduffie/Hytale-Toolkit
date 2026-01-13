package io.sentry.util;

import io.sentry.SentryIntegrationPackageStorage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class IntegrationUtils {
   public static void addIntegrationToSdkVersion(@NotNull String name) {
      SentryIntegrationPackageStorage.getInstance().addIntegration(name);
   }
}
