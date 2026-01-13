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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryRuntime implements JsonUnknown, JsonSerializable {
   public static final String TYPE = "runtime";
   @Nullable
   private String name;
   @Nullable
   private String version;
   @Nullable
   private String rawDescription;
   @Nullable
   private Map<String, @NotNull Object> unknown;

   public SentryRuntime() {
   }

   SentryRuntime(@NotNull SentryRuntime sentryRuntime) {
      this.name = sentryRuntime.name;
      this.version = sentryRuntime.version;
      this.rawDescription = sentryRuntime.rawDescription;
      this.unknown = CollectionUtils.newConcurrentHashMap(sentryRuntime.unknown);
   }

   @Nullable
   public String getName() {
      return this.name;
   }

   public void setName(@Nullable String name) {
      this.name = name;
   }

   @Nullable
   public String getVersion() {
      return this.version;
   }

   public void setVersion(@Nullable String version) {
      this.version = version;
   }

   @Nullable
   public String getRawDescription() {
      return this.rawDescription;
   }

   public void setRawDescription(@Nullable String rawDescription) {
      this.rawDescription = rawDescription;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.name != null) {
         writer.name("name").value(this.name);
      }

      if (this.version != null) {
         writer.name("version").value(this.version);
      }

      if (this.rawDescription != null) {
         writer.name("raw_description").value(this.rawDescription);
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

   @Nullable
   @Override
   public Map<String, Object> getUnknown() {
      return this.unknown;
   }

   @Override
   public void setUnknown(@Nullable Map<String, Object> unknown) {
      this.unknown = unknown;
   }

   public static final class Deserializer implements JsonDeserializer<SentryRuntime> {
      @NotNull
      public SentryRuntime deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         SentryRuntime runtime = new SentryRuntime();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "name":
                  runtime.name = reader.nextStringOrNull();
                  break;
               case "version":
                  runtime.version = reader.nextStringOrNull();
                  break;
               case "raw_description":
                  runtime.rawDescription = reader.nextStringOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         runtime.setUnknown(unknown);
         reader.endObject();
         return runtime;
      }
   }

   public static final class JsonKeys {
      public static final String NAME = "name";
      public static final String VERSION = "version";
      public static final String RAW_DESCRIPTION = "raw_description";
   }
}
