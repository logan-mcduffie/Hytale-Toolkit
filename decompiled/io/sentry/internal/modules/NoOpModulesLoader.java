package io.sentry.internal.modules;

import java.util.Map;
import org.jetbrains.annotations.Nullable;

public final class NoOpModulesLoader implements IModulesLoader {
   private static final NoOpModulesLoader instance = new NoOpModulesLoader();

   public static NoOpModulesLoader getInstance() {
      return instance;
   }

   private NoOpModulesLoader() {
   }

   @Nullable
   @Override
   public Map<String, String> getOrLoadModules() {
      return null;
   }
}
