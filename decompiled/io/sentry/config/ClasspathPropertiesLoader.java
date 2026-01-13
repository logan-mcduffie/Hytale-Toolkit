package io.sentry.config;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.util.ClassLoaderUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class ClasspathPropertiesLoader implements PropertiesLoader {
   @NotNull
   private final String fileName;
   @NotNull
   private final ClassLoader classLoader;
   @NotNull
   private final ILogger logger;

   public ClasspathPropertiesLoader(@NotNull String fileName, @Nullable ClassLoader classLoader, @NotNull ILogger logger) {
      this.fileName = fileName;
      this.classLoader = ClassLoaderUtils.classLoaderOrDefault(classLoader);
      this.logger = logger;
   }

   public ClasspathPropertiesLoader(@NotNull ILogger logger) {
      this("sentry.properties", ClasspathPropertiesLoader.class.getClassLoader(), logger);
   }

   @Nullable
   @Override
   public Properties load() {
      try {
         InputStream inputStream = this.classLoader.getResourceAsStream(this.fileName);

         Properties var4;
         label61: {
            try {
               if (inputStream != null) {
                  BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                  try {
                     Properties properties = new Properties();
                     properties.load(bufferedInputStream);
                     var4 = properties;
                  } catch (Throwable var7) {
                     try {
                        bufferedInputStream.close();
                     } catch (Throwable var6) {
                        var7.addSuppressed(var6);
                     }

                     throw var7;
                  }

                  bufferedInputStream.close();
                  break label61;
               }
            } catch (Throwable var8) {
               if (inputStream != null) {
                  try {
                     inputStream.close();
                  } catch (Throwable var5) {
                     var8.addSuppressed(var5);
                  }
               }

               throw var8;
            }

            if (inputStream != null) {
               inputStream.close();
            }

            return null;
         }

         if (inputStream != null) {
            inputStream.close();
         }

         return var4;
      } catch (IOException var9) {
         this.logger.log(SentryLevel.ERROR, var9, "Failed to load Sentry configuration from classpath resource: %s", this.fileName);
         return null;
      }
   }
}
