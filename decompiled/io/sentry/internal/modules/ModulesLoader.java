package io.sentry.internal.modules;

import io.sentry.ILogger;
import io.sentry.ISentryLifecycleToken;
import io.sentry.SentryLevel;
import io.sentry.util.AutoClosableReentrantLock;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Map;
import java.util.TreeMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public abstract class ModulesLoader implements IModulesLoader {
   private static final Charset UTF_8 = Charset.forName("UTF-8");
   public static final String EXTERNAL_MODULES_FILENAME = "sentry-external-modules.txt";
   @NotNull
   protected final ILogger logger;
   @NotNull
   private final AutoClosableReentrantLock modulesLock = new AutoClosableReentrantLock();
   @Nullable
   private volatile Map<String, String> cachedModules = null;

   public ModulesLoader(@NotNull ILogger logger) {
      this.logger = logger;
   }

   @Nullable
   @Override
   public Map<String, String> getOrLoadModules() {
      if (this.cachedModules == null) {
         ISentryLifecycleToken ignored = this.modulesLock.acquire();

         try {
            if (this.cachedModules == null) {
               this.cachedModules = this.loadModules();
            }
         } catch (Throwable var5) {
            if (ignored != null) {
               try {
                  ignored.close();
               } catch (Throwable var4) {
                  var5.addSuppressed(var4);
               }
            }

            throw var5;
         }

         if (ignored != null) {
            ignored.close();
         }
      }

      return this.cachedModules;
   }

   protected abstract Map<String, String> loadModules();

   protected Map<String, String> parseStream(@NotNull InputStream stream) {
      Map<String, String> modules = new TreeMap<>();

      try {
         BufferedReader reader = new BufferedReader(new InputStreamReader(stream, UTF_8));

         try {
            for (String module = reader.readLine(); module != null; module = reader.readLine()) {
               int sep = module.lastIndexOf(58);
               String group = module.substring(0, sep);
               String version = module.substring(sep + 1);
               modules.put(group, version);
            }

            this.logger.log(SentryLevel.DEBUG, "Extracted %d modules from resources.", modules.size());
         } catch (Throwable var9) {
            try {
               reader.close();
            } catch (Throwable var8) {
               var9.addSuppressed(var8);
            }

            throw var9;
         }

         reader.close();
      } catch (IOException var10) {
         this.logger.log(SentryLevel.ERROR, "Error extracting modules.", var10);
      } catch (RuntimeException var11) {
         this.logger.log(SentryLevel.ERROR, var11, "%s file is malformed.", "sentry-external-modules.txt");
      }

      return modules;
   }
}
