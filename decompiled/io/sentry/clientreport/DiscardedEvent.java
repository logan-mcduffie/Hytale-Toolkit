package io.sentry.clientreport;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class DiscardedEvent implements JsonUnknown, JsonSerializable {
   @NotNull
   private final String reason;
   @NotNull
   private final String category;
   @NotNull
   private final Long quantity;
   @Nullable
   private Map<String, Object> unknown;

   public DiscardedEvent(@NotNull String reason, @NotNull String category, @NotNull Long quantity) {
      this.reason = reason;
      this.category = category;
      this.quantity = quantity;
   }

   @NotNull
   public String getReason() {
      return this.reason;
   }

   @NotNull
   public String getCategory() {
      return this.category;
   }

   @NotNull
   public Long getQuantity() {
      return this.quantity;
   }

   @Override
   public String toString() {
      return "DiscardedEvent{reason='" + this.reason + '\'' + ", category='" + this.category + '\'' + ", quantity=" + this.quantity + '}';
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
      writer.name("reason").value(this.reason);
      writer.name("category").value(this.category);
      writer.name("quantity").value(this.quantity);
      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<DiscardedEvent> {
      @NotNull
      public DiscardedEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         String reason = null;
         String category = null;
         Long quanity = null;
         Map<String, Object> unknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "reason":
                  reason = reader.nextStringOrNull();
                  break;
               case "category":
                  category = reader.nextStringOrNull();
                  break;
               case "quantity":
                  quanity = reader.nextLongOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         if (reason == null) {
            throw this.missingRequiredFieldException("reason", logger);
         } else if (category == null) {
            throw this.missingRequiredFieldException("category", logger);
         } else if (quanity == null) {
            throw this.missingRequiredFieldException("quantity", logger);
         } else {
            DiscardedEvent discardedEvent = new DiscardedEvent(reason, category, quanity);
            discardedEvent.setUnknown(unknown);
            return discardedEvent;
         }
      }

      private Exception missingRequiredFieldException(String field, ILogger logger) {
         String message = "Missing required field \"" + field + "\"";
         Exception exception = new IllegalStateException(message);
         logger.log(SentryLevel.ERROR, message, exception);
         return exception;
      }
   }

   public static final class JsonKeys {
      public static final String REASON = "reason";
      public static final String CATEGORY = "category";
      public static final String QUANTITY = "quantity";
   }
}
