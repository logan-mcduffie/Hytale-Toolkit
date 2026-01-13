package io.sentry.internal.debugmeta;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.util.ClassLoaderUtils;
import io.sentry.util.DebugMetaPropertiesApplier;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ResourcesDebugMetaLoader implements IDebugMetaLoader {
   @NotNull
   private final ILogger logger;
   @NotNull
   private final ClassLoader classLoader;

   public ResourcesDebugMetaLoader(@NotNull ILogger logger) {
      this(logger, ResourcesDebugMetaLoader.class.getClassLoader());
   }

   ResourcesDebugMetaLoader(@NotNull ILogger logger, @Nullable ClassLoader classLoader) {
      this.logger = logger;
      this.classLoader = ClassLoaderUtils.classLoaderOrDefault(classLoader);
   }

   @Nullable
   @Override
   public List<Properties> loadDebugMeta() {
      List<Properties> debugPropertyList = new ArrayList<>();

      try {
         Enumeration<URL> resourceUrls = this.classLoader.getResources(DebugMetaPropertiesApplier.DEBUG_META_PROPERTIES_FILENAME);

         while (resourceUrls.hasMoreElements()) {
            URL currentUrl = resourceUrls.nextElement();

            try {
               InputStream is = currentUrl.openStream();

               try {
                  Properties currentProperties = new Properties();
                  currentProperties.load(is);
                  debugPropertyList.add(currentProperties);
                  this.logger.log(SentryLevel.INFO, "Debug Meta Data Properties loaded from %s", currentUrl);
               } catch (Throwable var8) {
                  if (is != null) {
                     try {
                        is.close();
                     } catch (Throwable var7) {
                        var8.addSuppressed(var7);
                     }
                  }

                  throw var8;
               }

               if (is != null) {
                  is.close();
               }
            } catch (RuntimeException var9) {
               this.logger.log(SentryLevel.ERROR, var9, "%s file is malformed.", currentUrl);
            }
         }
      } catch (IOException var10) {
         this.logger.log(SentryLevel.ERROR, var10, "Failed to load %s", DebugMetaPropertiesApplier.DEBUG_META_PROPERTIES_FILENAME);
      }

      if (debugPropertyList.isEmpty()) {
         this.logger.log(SentryLevel.INFO, "No %s file was found.", DebugMetaPropertiesApplier.DEBUG_META_PROPERTIES_FILENAME);
         return null;
      } else {
         return debugPropertyList;
      }
   }
}
