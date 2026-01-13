package io.sentry.util;

import io.sentry.SentryAttribute;
import io.sentry.SentryAttributes;
import io.sentry.SentryEvent;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class LoggerPropertiesUtil {
   @Internal
   public static void applyPropertiesToEvent(
      @NotNull SentryEvent event, @NotNull List<String> targetKeys, @NotNull Map<String, String> properties, @NotNull String contextName
   ) {
      if (!targetKeys.isEmpty() && !properties.isEmpty()) {
         for (String key : targetKeys) {
            String value = properties.remove(key);
            if (value != null) {
               event.setTag(key, value);
            }
         }
      }

      if (!properties.isEmpty()) {
         event.getContexts().put(contextName, properties);
      }
   }

   public static void applyPropertiesToEvent(@NotNull SentryEvent event, @NotNull List<String> targetKeys, @NotNull Map<String, String> properties) {
      applyPropertiesToEvent(event, targetKeys, properties, "MDC");
   }

   @Internal
   public static void applyPropertiesToAttributes(
      @NotNull SentryAttributes attributes, @NotNull List<String> targetKeys, @NotNull Map<String, String> properties
   ) {
      if (!targetKeys.isEmpty() && !properties.isEmpty()) {
         for (String key : targetKeys) {
            String value = properties.get(key);
            if (value != null) {
               attributes.add(SentryAttribute.stringAttribute("mdc." + key, value));
            }
         }
      }
   }
}
