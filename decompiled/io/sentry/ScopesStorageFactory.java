package io.sentry;

import io.sentry.util.LoadClass;
import io.sentry.util.Platform;
import java.lang.reflect.InvocationTargetException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ScopesStorageFactory {
   private static final String OTEL_SCOPES_STORAGE = "io.sentry.opentelemetry.OtelContextScopesStorage";

   @NotNull
   public static IScopesStorage create(@NotNull LoadClass loadClass, @NotNull ILogger logger) {
      IScopesStorage storage = createInternal(loadClass, logger);
      storage.init();
      return storage;
   }

   @NotNull
   private static IScopesStorage createInternal(@NotNull LoadClass loadClass, @NotNull ILogger logger) {
      if (Platform.isJvm() && loadClass.isClassAvailable("io.sentry.opentelemetry.OtelContextScopesStorage", logger)) {
         Class<?> otelScopesStorageClazz = loadClass.loadClass("io.sentry.opentelemetry.OtelContextScopesStorage", logger);
         if (otelScopesStorageClazz != null) {
            try {
               Object otelScopesStorage = otelScopesStorageClazz.getDeclaredConstructor().newInstance();
               if (otelScopesStorage != null && otelScopesStorage instanceof IScopesStorage) {
                  return (IScopesStorage)otelScopesStorage;
               }
            } catch (InstantiationException var4) {
            } catch (IllegalAccessException var5) {
            } catch (InvocationTargetException var6) {
            } catch (NoSuchMethodException var7) {
            }
         }
      }

      return new DefaultScopesStorage();
   }
}
