package io.sentry.internal.debugmeta;

import java.util.List;
import java.util.Properties;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IDebugMetaLoader {
   @Nullable
   List<Properties> loadDebugMeta();
}
