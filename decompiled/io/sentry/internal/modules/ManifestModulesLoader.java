package io.sentry.internal.modules;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import io.sentry.util.ClassLoaderUtils;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Experimental;
import org.jetbrains.annotations.ApiStatus.Internal;

@Experimental
@Internal
public final class ManifestModulesLoader extends ModulesLoader {
   private final Pattern URL_LIB_PATTERN = Pattern.compile(".*/(.+)!/META-INF/MANIFEST.MF");
   private final Pattern NAME_AND_VERSION = Pattern.compile("(.*?)-(\\d+\\.\\d+.*).jar");
   private final ClassLoader classLoader;

   public ManifestModulesLoader(@NotNull ILogger logger) {
      this(ManifestModulesLoader.class.getClassLoader(), logger);
   }

   ManifestModulesLoader(@Nullable ClassLoader classLoader, @NotNull ILogger logger) {
      super(logger);
      this.classLoader = ClassLoaderUtils.classLoaderOrDefault(classLoader);
   }

   @Override
   protected Map<String, String> loadModules() {
      Map<String, String> modules = new HashMap<>();

      for (ManifestModulesLoader.Module module : this.detectModulesViaManifestFiles()) {
         modules.put(module.name, module.version);
      }

      return modules;
   }

   @NotNull
   private List<ManifestModulesLoader.Module> detectModulesViaManifestFiles() {
      List<ManifestModulesLoader.Module> modules = new ArrayList<>();

      try {
         Enumeration<URL> manifestUrls = this.classLoader.getResources("META-INF/MANIFEST.MF");

         while (manifestUrls.hasMoreElements()) {
            URL manifestUrl = manifestUrls.nextElement();
            String originalName = this.extractDependencyNameFromUrl(manifestUrl);
            ManifestModulesLoader.Module module = this.convertOriginalNameToModule(originalName);
            if (module != null) {
               modules.add(module);
            }
         }
      } catch (Throwable var6) {
         this.logger.log(SentryLevel.ERROR, "Unable to detect modules via manifest files.", var6);
      }

      return modules;
   }

   @Nullable
   private ManifestModulesLoader.Module convertOriginalNameToModule(@Nullable String originalName) {
      if (originalName == null) {
         return null;
      } else {
         Matcher matcher = this.NAME_AND_VERSION.matcher(originalName);
         if (matcher.matches() && matcher.groupCount() == 2) {
            String moduleName = matcher.group(1);
            String moduleVersion = matcher.group(2);
            return new ManifestModulesLoader.Module(moduleName, moduleVersion);
         } else {
            return null;
         }
      }
   }

   @Nullable
   private String extractDependencyNameFromUrl(@NotNull URL url) {
      String urlString = url.toString();
      Matcher matcher = this.URL_LIB_PATTERN.matcher(urlString);
      return matcher.matches() && matcher.groupCount() == 1 ? matcher.group(1) : null;
   }

   private static final class Module {
      @NotNull
      private final String name;
      @NotNull
      private final String version;

      public Module(@NotNull String name, @NotNull String version) {
         this.name = name;
         this.version = version;
      }
   }
}
