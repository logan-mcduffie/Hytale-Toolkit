package io.sentry.featureflags;

import io.sentry.protocol.FeatureFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class NoOpFeatureFlagBuffer implements IFeatureFlagBuffer {
   private static final NoOpFeatureFlagBuffer instance = new NoOpFeatureFlagBuffer();

   public static NoOpFeatureFlagBuffer getInstance() {
      return instance;
   }

   @Override
   public void add(@Nullable String flag, @Nullable Boolean result) {
   }

   @Nullable
   @Override
   public FeatureFlags getFeatureFlags() {
      return null;
   }

   @NotNull
   @Override
   public IFeatureFlagBuffer clone() {
      return instance;
   }
}
