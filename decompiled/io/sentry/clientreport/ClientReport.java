package io.sentry.clientreport;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ClientReport implements JsonUnknown, JsonSerializable {
   @NotNull
   private final Date timestamp;
   @NotNull
   private final List<DiscardedEvent> discardedEvents;
   @Nullable
   private Map<String, Object> unknown;

   public ClientReport(@NotNull Date timestamp, @NotNull List<DiscardedEvent> discardedEvents) {
      this.timestamp = timestamp;
      this.discardedEvents = discardedEvents;
   }

   @NotNull
   public Date getTimestamp() {
      return this.timestamp;
   }

   @NotNull
   public List<DiscardedEvent> getDiscardedEvents() {
      return this.discardedEvents;
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
      writer.name("timestamp").value(DateUtils.getTimestamp(this.timestamp));
      writer.name("discarded_events").value(logger, this.discardedEvents);
      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<ClientReport> {
      @NotNull
      public ClientReport deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Date timestamp = null;
         List<DiscardedEvent> discardedEvents = new ArrayList<>();
         Map<String, Object> unknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "timestamp":
                  timestamp = reader.nextDateOrNull(logger);
                  break;
               case "discarded_events":
                  List<DiscardedEvent> deserializedDiscardedEvents = reader.nextListOrNull(logger, new DiscardedEvent.Deserializer());
                  discardedEvents.addAll(deserializedDiscardedEvents);
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         if (timestamp == null) {
            throw this.missingRequiredFieldException("timestamp", logger);
         } else if (discardedEvents.isEmpty()) {
            throw this.missingRequiredFieldException("discarded_events", logger);
         } else {
            ClientReport clientReport = new ClientReport(timestamp, discardedEvents);
            clientReport.setUnknown(unknown);
            return clientReport;
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
      public static final String TIMESTAMP = "timestamp";
      public static final String DISCARDED_EVENTS = "discarded_events";
   }
}
