package io.sentry.config;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class FilesystemPropertiesLoader implements PropertiesLoader {
   @NotNull
   private final String filePath;
   @NotNull
   private final ILogger logger;
   private boolean logNonExisting;

   public FilesystemPropertiesLoader(@NotNull String filePath, @NotNull ILogger logger) {
      this(filePath, logger, true);
   }

   public FilesystemPropertiesLoader(@NotNull String filePath, @NotNull ILogger logger, boolean logNonExisting) {
      this.filePath = filePath;
      this.logger = logger;
      this.logNonExisting = logNonExisting;
   }

   @Nullable
   @Override
   public Properties load() {
      try {
         File f = new File(this.filePath.trim());
         if (f.isFile() && f.canRead()) {
            InputStream is = new BufferedInputStream(new FileInputStream(f));

            Properties var4;
            try {
               Properties properties = new Properties();
               properties.load(is);
               var4 = properties;
            } catch (Throwable var6) {
               try {
                  is.close();
               } catch (Throwable var5) {
                  var6.addSuppressed(var5);
               }

               throw var6;
            }

            is.close();
            return var4;
         } else {
            if (!f.isFile()) {
               if (this.logNonExisting) {
                  this.logger.log(SentryLevel.ERROR, "Failed to load Sentry configuration since it is not a file or does not exist: %s", this.filePath);
               }
            } else if (!f.canRead()) {
               this.logger.log(SentryLevel.ERROR, "Failed to load Sentry configuration since it is not readable: %s", this.filePath);
            }

            return null;
         }
      } catch (Throwable var7) {
         this.logger.log(SentryLevel.ERROR, var7, "Failed to load Sentry configuration from file: %s", this.filePath);
         return null;
      }
   }
}
