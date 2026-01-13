package io.sentry.config;

import java.util.Properties;
import org.jetbrains.annotations.NotNull;

final class SimplePropertiesProvider extends AbstractPropertiesProvider {
   public SimplePropertiesProvider(@NotNull Properties properties) {
      super(properties);
   }
}
