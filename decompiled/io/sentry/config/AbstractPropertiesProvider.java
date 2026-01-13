package io.sentry.config;

import io.sentry.util.Objects;
import io.sentry.util.StringUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class AbstractPropertiesProvider implements PropertiesProvider {
   @NotNull
   private final String prefix;
   @NotNull
   private final Properties properties;

   protected AbstractPropertiesProvider(@NotNull String prefix, @NotNull Properties properties) {
      this.prefix = Objects.requireNonNull(prefix, "prefix is required");
      this.properties = Objects.requireNonNull(properties, "properties are required");
   }

   protected AbstractPropertiesProvider(@NotNull Properties properties) {
      this("", properties);
   }

   @Nullable
   @Override
   public String getProperty(@NotNull String property) {
      return StringUtils.removeSurrounding(this.properties.getProperty(this.prefix + property), "\"");
   }

   @NotNull
   @Override
   public Map<String, String> getMap(@NotNull String property) {
      String prefix = this.prefix + property + ".";
      Map<String, String> result = new HashMap<>();

      for (Entry<Object, Object> entry : this.properties.entrySet()) {
         if (entry.getKey() instanceof String && entry.getValue() instanceof String) {
            String key = (String)entry.getKey();
            if (key.startsWith(prefix)) {
               String value = StringUtils.removeSurrounding((String)entry.getValue(), "\"");
               result.put(key.substring(prefix.length()), value);
            }
         }
      }

      return result;
   }
}
