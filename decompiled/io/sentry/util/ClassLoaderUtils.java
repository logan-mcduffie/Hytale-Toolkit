package io.sentry.util;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ClassLoaderUtils {
   @NotNull
   public static ClassLoader classLoaderOrDefault(@Nullable ClassLoader classLoader) {
      if (classLoader == null) {
         ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
         return contextClassLoader != null ? contextClassLoader : ClassLoader.getSystemClassLoader();
      } else {
         return classLoader;
      }
   }
}
