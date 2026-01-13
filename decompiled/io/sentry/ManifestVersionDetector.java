package io.sentry;

import io.sentry.internal.ManifestVersionReader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ManifestVersionDetector implements IVersionDetector {
   @NotNull
   private final SentryOptions options;

   public ManifestVersionDetector(@NotNull SentryOptions options) {
      this.options = options;
   }

   @Override
   public boolean checkForMixedVersions() {
      ManifestVersionReader.getInstance().readManifestFiles();
      return SentryIntegrationPackageStorage.getInstance().checkForMixedVersions(this.options.getFatalLogger());
   }
}
