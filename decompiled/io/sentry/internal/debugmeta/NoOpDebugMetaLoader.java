package io.sentry.internal.debugmeta;

import java.util.List;
import java.util.Properties;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class NoOpDebugMetaLoader implements IDebugMetaLoader {
   private static final NoOpDebugMetaLoader instance = new NoOpDebugMetaLoader();

   public static NoOpDebugMetaLoader getInstance() {
      return instance;
   }

   private NoOpDebugMetaLoader() {
   }

   @Nullable
   @Override
   public List<Properties> loadDebugMeta() {
      return null;
   }
}
