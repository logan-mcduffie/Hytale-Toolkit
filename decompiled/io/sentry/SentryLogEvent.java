package io.sentry;

import io.sentry.protocol.SentryId;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryLogEvent implements JsonUnknown, JsonSerializable {
   @NotNull
   private SentryId traceId;
   @NotNull
   private Double timestamp;
   @NotNull
   private String body;
   @NotNull
   private SentryLogLevel level;
   @Nullable
   private Integer severityNumber;
   @Nullable
   private Map<String, SentryLogEventAttributeValue> attributes;
   @Nullable
   private Map<String, Object> unknown;

   public SentryLogEvent(@NotNull SentryId traceId, @NotNull SentryDate timestamp, @NotNull String body, @NotNull SentryLogLevel level) {
      this(traceId, DateUtils.nanosToSeconds(timestamp.nanoTimestamp()), body, level);
   }

   public SentryLogEvent(@NotNull SentryId traceId, @NotNull Double timestamp, @NotNull String body, @NotNull SentryLogLevel level) {
      this.traceId = traceId;
      this.timestamp = timestamp;
      this.body = body;
      this.level = level;
   }

   @NotNull
   public Double getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(@NotNull Double timestamp) {
      this.timestamp = timestamp;
   }

   @NotNull
   public String getBody() {
      return this.body;
   }

   public void setBody(@NotNull String body) {
      this.body = body;
   }

   @NotNull
   public SentryLogLevel getLevel() {
      return this.level;
   }

   public void setLevel(@NotNull SentryLogLevel level) {
      this.level = level;
   }

   @Nullable
   public Map<String, SentryLogEventAttributeValue> getAttributes() {
      return this.attributes;
   }

   public void setAttributes(@Nullable Map<String, SentryLogEventAttributeValue> attributes) {
      this.attributes = attributes;
   }

   public void setAttribute(@Nullable String key, @Nullable SentryLogEventAttributeValue value) {
      if (key != null) {
         if (this.attributes == null) {
            this.attributes = new HashMap<>();
         }

         this.attributes.put(key, value);
      }
   }

   @Nullable
   public Integer getSeverityNumber() {
      return this.severityNumber;
   }

   public void setSeverityNumber(@Nullable Integer severityNumber) {
      this.severityNumber = severityNumber;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("timestamp").value(logger, DateUtils.doubleToBigDecimal(this.timestamp));
      writer.name("trace_id").value(logger, this.traceId);
      writer.name("body").value(this.body);
      writer.name("level").value(logger, this.level);
      if (this.severityNumber != null) {
         writer.name("severity_number").value(logger, this.severityNumber);
      }

      if (this.attributes != null) {
         writer.name("attributes").value(logger, this.attributes);
      }

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

   public static final class Deserializer implements JsonDeserializer<SentryLogEvent> {
      @NotNull
      public SentryLogEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Map<String, Object> unknown = null;
         SentryId traceId = null;
         Double timestamp = null;
         String body = null;
         SentryLogLevel level = null;
         Integer severityNumber = null;
         Map<String, SentryLogEventAttributeValue> attributes = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "trace_id":
                  traceId = reader.nextOrNull(logger, new SentryId.Deserializer());
                  break;
               case "timestamp":
                  timestamp = reader.nextDoubleOrNull();
                  break;
               case "body":
                  body = reader.nextStringOrNull();
                  break;
               case "level":
                  level = reader.nextOrNull(logger, new SentryLogLevel.Deserializer());
                  break;
               case "severity_number":
                  severityNumber = reader.nextIntegerOrNull();
                  break;
               case "attributes":
                  attributes = reader.nextMapOrNull(logger, new SentryLogEventAttributeValue.Deserializer());
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         if (traceId == null) {
            String message = "Missing required field \"trace_id\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else if (timestamp == null) {
            String message = "Missing required field \"timestamp\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else if (body == null) {
            String message = "Missing required field \"body\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else if (level == null) {
            String message = "Missing required field \"level\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else {
            SentryLogEvent logEvent = new SentryLogEvent(traceId, timestamp, body, level);
            logEvent.setAttributes(attributes);
            logEvent.setSeverityNumber(severityNumber);
            logEvent.setUnknown(unknown);
            return logEvent;
         }
      }
   }

   public static final class JsonKeys {
      public static final String TIMESTAMP = "timestamp";
      public static final String TRACE_ID = "trace_id";
      public static final String LEVEL = "level";
      public static final String SEVERITY_NUMBER = "severity_number";
      public static final String BODY = "body";
      public static final String ATTRIBUTES = "attributes";
   }
}
