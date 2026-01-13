package io.sentry.internal.modules;

import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IModulesLoader {
   @Nullable
   Map<String, String> getOrLoadModules();
}
