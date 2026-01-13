package io.sentry;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryAttributes {
   @NotNull
   private final Map<String, SentryAttribute> attributes;

   private SentryAttributes(@NotNull Map<String, SentryAttribute> attributes) {
      this.attributes = attributes;
   }

   public void add(@Nullable SentryAttribute attribute) {
      if (attribute != null) {
         this.attributes.put(attribute.getName(), attribute);
      }
   }

   @NotNull
   public Map<String, SentryAttribute> getAttributes() {
      return this.attributes;
   }

   @NotNull
   public static SentryAttributes of(@Nullable SentryAttribute... attributes) {
      if (attributes == null) {
         return new SentryAttributes(new ConcurrentHashMap<>());
      } else {
         SentryAttributes sentryAttributes = new SentryAttributes(new ConcurrentHashMap<>(attributes.length));

         for (SentryAttribute attribute : attributes) {
            sentryAttributes.add(attribute);
         }

         return sentryAttributes;
      }
   }

   @NotNull
   public static SentryAttributes fromMap(@Nullable Map<String, Object> attributes) {
      if (attributes == null) {
         return new SentryAttributes(new ConcurrentHashMap<>());
      } else {
         SentryAttributes sentryAttributes = new SentryAttributes(new ConcurrentHashMap<>(attributes.size()));

         for (Entry<String, Object> attribute : attributes.entrySet()) {
            String key = attribute.getKey();
            if (key != null) {
               sentryAttributes.add(SentryAttribute.named(key, attribute.getValue()));
            }
         }

         return sentryAttributes;
      }
   }
}
