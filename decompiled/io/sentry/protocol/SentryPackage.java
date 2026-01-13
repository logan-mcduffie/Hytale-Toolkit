package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryPackage implements JsonUnknown, JsonSerializable {
   @NotNull
   private String name;
   @NotNull
   private String version;
   @Nullable
   private Map<String, Object> unknown;

   public SentryPackage(@NotNull String name, @NotNull String version) {
      this.name = Objects.requireNonNull(name, "name is required.");
      this.version = Objects.requireNonNull(version, "version is required.");
   }

   @NotNull
   public String getName() {
      return this.name;
   }

   public void setName(@NotNull String name) {
      this.name = Objects.requireNonNull(name, "name is required.");
   }

   @NotNull
   public String getVersion() {
      return this.version;
   }

   public void setVersion(@NotNull String version) {
      this.version = Objects.requireNonNull(version, "version is required.");
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SentryPackage that = (SentryPackage)o;
         return java.util.Objects.equals(this.name, that.name) && java.util.Objects.equals(this.version, that.version);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return java.util.Objects.hash(this.name, this.version);
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
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("name").value(this.name);
      writer.name("version").value(this.version);
      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<SentryPackage> {
      @NotNull
      public SentryPackage deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         String name = null;
         String version = null;
         Map<String, Object> unknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "name":
                  name = reader.nextString();
                  break;
               case "version":
                  version = reader.nextString();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         if (name == null) {
            String message = "Missing required field \"name\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else if (version == null) {
            String message = "Missing required field \"version\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else {
            SentryPackage sentryPackage = new SentryPackage(name, version);
            sentryPackage.setUnknown(unknown);
            return sentryPackage;
         }
      }
   }

   public static final class JsonKeys {
      public static final String NAME = "name";
      public static final String VERSION = "version";
   }
}
