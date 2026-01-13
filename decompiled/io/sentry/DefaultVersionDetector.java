package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class DefaultVersionDetector implements IVersionDetector {
   @NotNull
   private final SentryOptions options;

   public DefaultVersionDetector(@NotNull SentryOptions options) {
      this.options = options;
   }

   @Override
   public boolean checkForMixedVersions() {
      return SentryIntegrationPackageStorage.getInstance().checkForMixedVersions(this.options.getFatalLogger());
   }
}
