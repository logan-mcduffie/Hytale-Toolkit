package io.sentry;

import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryLogEvents implements JsonUnknown, JsonSerializable {
   @NotNull
   private List<SentryLogEvent> items;
   @Nullable
   private Map<String, Object> unknown;

   public SentryLogEvents(@NotNull List<SentryLogEvent> items) {
      this.items = items;
   }

   @NotNull
   public List<SentryLogEvent> getItems() {
      return this.items;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("items").value(logger, this.items);
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

   public static final class Deserializer implements JsonDeserializer<SentryLogEvents> {
      @NotNull
      public SentryLogEvents deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Map<String, Object> unknown = null;
         List<SentryLogEvent> items = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "items":
                  items = reader.nextListOrNull(logger, new SentryLogEvent.Deserializer());
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         if (items == null) {
            String message = "Missing required field \"items\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else {
            SentryLogEvents logEvent = new SentryLogEvents(items);
            logEvent.setUnknown(unknown);
            return logEvent;
         }
      }
   }

   public static final class JsonKeys {
      public static final String ITEMS = "items";
   }
}
