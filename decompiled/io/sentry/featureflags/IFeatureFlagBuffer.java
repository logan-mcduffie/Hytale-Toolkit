package io.sentry.featureflags;

import io.sentry.protocol.FeatureFlags;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IFeatureFlagBuffer {
   void add(@Nullable String var1, @Nullable Boolean var2);

   @Nullable
   FeatureFlags getFeatureFlags();

   @NotNull
   IFeatureFlagBuffer clone();
}
