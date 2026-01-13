package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.util.CollectionUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Spring implements JsonUnknown, JsonSerializable {
   public static final String TYPE = "spring";
   @Nullable
   private String[] activeProfiles;
   @Nullable
   private Map<String, @NotNull Object> unknown;

   public Spring() {
   }

   public Spring(@NotNull Spring spring) {
      this.activeProfiles = spring.activeProfiles;
      this.unknown = CollectionUtils.newConcurrentHashMap(spring.unknown);
   }

   @Nullable
   public String[] getActiveProfiles() {
      return this.activeProfiles;
   }

   public void setActiveProfiles(@Nullable String[] activeProfiles) {
      this.activeProfiles = activeProfiles;
   }

   @Nullable
   @Override
   public Map<String, Object> getUnknown() {
      return this.unknown;
   }

   @Override
   public void setUnknown(@Nullable Map<String, Object> unknown) {
      this.unknown = unknown;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         Spring spring = (Spring)o;
         return Arrays.equals((Object[])this.activeProfiles, (Object[])spring.activeProfiles);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Arrays.hashCode((Object[])this.activeProfiles);
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.activeProfiles != null) {
         writer.name("active_profiles").value(logger, this.activeProfiles);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key);
            writer.value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<Spring> {
      @NotNull
      public Spring deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         Spring spring = new Spring();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "active_profiles":
                  List<?> activeProfilesList = (List<?>)reader.nextObjectOrNull();
                  if (activeProfilesList != null) {
                     Object[] activeProfiles = new String[activeProfilesList.size()];
                     activeProfilesList.toArray(activeProfiles);
                     spring.activeProfiles = (String[])activeProfiles;
                  }
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         spring.setUnknown(unknown);
         reader.endObject();
         return spring;
      }
   }

   public static final class JsonKeys {
      public static final String ACTIVE_PROFILES = "active_profiles";
   }
}
