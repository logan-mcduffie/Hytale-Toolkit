package io.sentry;

import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryLogEventAttributeValue implements JsonUnknown, JsonSerializable {
   @NotNull
   private String type;
   @Nullable
   private Object value;
   @Nullable
   private Map<String, Object> unknown;

   public SentryLogEventAttributeValue(@NotNull String type, @Nullable Object value) {
      this.type = type;
      if (value != null && type.equals("string")) {
         this.value = value.toString();
      } else {
         this.value = value;
      }
   }

   public SentryLogEventAttributeValue(@NotNull SentryAttributeType type, @Nullable Object value) {
      this(type.apiName(), value);
   }

   @NotNull
   public String getType() {
      return this.type;
   }

   @Nullable
   public Object getValue() {
      return this.value;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("type").value(logger, this.type);
      writer.name("value").value(logger, this.value);
      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
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

   public static final class Deserializer implements JsonDeserializer<SentryLogEventAttributeValue> {
      @NotNull
      public SentryLogEventAttributeValue deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Map<String, Object> unknown = null;
         String type = null;
         Object value = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "type":
                  type = reader.nextStringOrNull();
                  break;
               case "value":
                  value = reader.nextObjectOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         if (type == null) {
            String message = "Missing required field \"type\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else {
            SentryLogEventAttributeValue logEvent = new SentryLogEventAttributeValue(type, value);
            logEvent.setUnknown(unknown);
            return logEvent;
         }
      }
   }

   public static final class JsonKeys {
      public static final String TYPE = "type";
      public static final String VALUE = "value";
   }
}
