package io.sentry;

import io.sentry.protocol.SdkVersion;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface IOptionsObserver {
   void setRelease(@Nullable String var1);

   void setProguardUuid(@Nullable String var1);

   void setSdkVersion(@Nullable SdkVersion var1);

   void setEnvironment(@Nullable String var1);

   void setDist(@Nullable String var1);

   void setTags(@NotNull Map<String, String> var1);

   void setReplayErrorSampleRate(@Nullable Double var1);
}
