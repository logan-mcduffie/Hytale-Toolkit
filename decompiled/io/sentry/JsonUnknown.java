package io.sentry;

import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface JsonUnknown {
   @Nullable
   Map<String, Object> getUnknown();

   void setUnknown(@Nullable Map<String, Object> var1);
}
