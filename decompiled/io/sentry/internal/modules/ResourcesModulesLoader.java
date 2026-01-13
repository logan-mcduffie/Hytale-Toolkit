package io.sentry.internal.modules;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.util.ClassLoaderUtils;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.TreeMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ResourcesModulesLoader extends ModulesLoader {
   @NotNull
   private final ClassLoader classLoader;

   public ResourcesModulesLoader(@NotNull ILogger logger) {
      this(logger, ResourcesModulesLoader.class.getClassLoader());
   }

   ResourcesModulesLoader(@NotNull ILogger logger, @Nullable ClassLoader classLoader) {
      super(logger);
      this.classLoader = ClassLoaderUtils.classLoaderOrDefault(classLoader);
   }

   @Override
   protected Map<String, String> loadModules() {
      Map<String, String> modules = new TreeMap<>();

      try {
         InputStream resourcesStream = this.classLoader.getResourceAsStream("sentry-external-modules.txt");

         Map var9;
         label57: {
            try {
               if (resourcesStream == null) {
                  this.logger.log(SentryLevel.INFO, "%s file was not found.", "sentry-external-modules.txt");
                  var9 = modules;
                  break label57;
               }

               var9 = this.parseStream(resourcesStream);
            } catch (Throwable var6) {
               if (resourcesStream != null) {
                  try {
                     resourcesStream.close();
                  } catch (Throwable var5) {
                     var6.addSuppressed(var5);
                  }
               }

               throw var6;
            }

            if (resourcesStream != null) {
               resourcesStream.close();
            }

            return var9;
         }

         if (resourcesStream != null) {
            resourcesStream.close();
         }

         return var9;
      } catch (SecurityException var7) {
         this.logger.log(SentryLevel.INFO, "Access to resources denied.", var7);
      } catch (IOException var8) {
         this.logger.log(SentryLevel.INFO, "Access to resources failed.", var8);
      }

      return modules;
   }
}
