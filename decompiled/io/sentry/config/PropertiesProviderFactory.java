package io.sentry.config;

import io.sentry.ILogger;
import io.sentry.SystemOutLogger;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class PropertiesProviderFactory {
   @NotNull
   public static PropertiesProvider create() {
      ILogger logger = new SystemOutLogger();
      List<PropertiesProvider> providers = new ArrayList<>();
      providers.add(new SystemPropertyPropertiesProvider());
      providers.add(new EnvironmentVariablePropertiesProvider());
      String systemPropertyLocation = System.getProperty("sentry.properties.file");
      if (systemPropertyLocation != null) {
         Properties properties = new FilesystemPropertiesLoader(systemPropertyLocation, logger).load();
         if (properties != null) {
            providers.add(new SimplePropertiesProvider(properties));
         }
      }

      String environmentVariablesLocation = System.getenv("SENTRY_PROPERTIES_FILE");
      if (environmentVariablesLocation != null) {
         Properties properties = new FilesystemPropertiesLoader(environmentVariablesLocation, logger).load();
         if (properties != null) {
            providers.add(new SimplePropertiesProvider(properties));
         }
      }

      Properties properties = new ClasspathPropertiesLoader(logger).load();
      if (properties != null) {
         providers.add(new SimplePropertiesProvider(properties));
      }

      Properties runDirectoryProperties = new FilesystemPropertiesLoader("sentry.properties", logger, false).load();
      if (runDirectoryProperties != null) {
         providers.add(new SimplePropertiesProvider(runDirectoryProperties));
      }

      return new CompositePropertiesProvider(providers);
   }
}
