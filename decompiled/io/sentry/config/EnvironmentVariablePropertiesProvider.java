package io.sentry.config;

import io.sentry.util.StringUtils;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class EnvironmentVariablePropertiesProvider implements PropertiesProvider {
   private static final String PREFIX = "SENTRY";

   @Nullable
   @Override
   public String getProperty(@NotNull String property) {
      return StringUtils.removeSurrounding(System.getenv(this.propertyToEnvironmentVariableName(property)), "\"");
   }

   @NotNull
   @Override
   public Map<String, String> getMap(@NotNull String property) {
      String prefix = this.propertyToEnvironmentVariableName(property) + "_";
      Map<String, String> result = new ConcurrentHashMap<>();

      for (Entry<String, String> entry : System.getenv().entrySet()) {
         String key = entry.getKey();
         if (key.startsWith(prefix)) {
            String value = StringUtils.removeSurrounding(entry.getValue(), "\"");
            if (value != null) {
               result.put(key.substring(prefix.length()).toLowerCase(Locale.ROOT), value);
            }
         }
      }

      return result;
   }

   @NotNull
   private String propertyToEnvironmentVariableName(@NotNull String property) {
      return "SENTRY_" + property.replace(".", "_").replace("-", "_").toUpperCase(Locale.ROOT);
   }
}
