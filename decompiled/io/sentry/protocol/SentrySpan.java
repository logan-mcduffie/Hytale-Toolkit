package io.sentry.protocol;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.Span;
import io.sentry.SpanId;
import io.sentry.SpanStatus;
import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentrySpan implements JsonUnknown, JsonSerializable {
   @NotNull
   private final Double startTimestamp;
   @Nullable
   private final Double timestamp;
   @NotNull
   private final SentryId traceId;
   @NotNull
   private final SpanId spanId;
   @Nullable
   private final SpanId parentSpanId;
   @NotNull
   private final String op;
   @Nullable
   private final String description;
   @Nullable
   private final SpanStatus status;
   @Nullable
   private final String origin;
   @NotNull
   private final Map<String, String> tags;
   @Nullable
   private Map<String, Object> data;
   @NotNull
   private final Map<String, MeasurementValue> measurements;
   @Nullable
   private Map<String, Object> unknown;

   public SentrySpan(@NotNull Span span) {
      this(span, span.getData());
   }

   @Internal
   public SentrySpan(@NotNull Span span, @Nullable Map<String, Object> data) {
      Objects.requireNonNull(span, "span is required");
      this.description = span.getDescription();
      this.op = span.getOperation();
      this.spanId = span.getSpanId();
      this.parentSpanId = span.getParentSpanId();
      this.traceId = span.getTraceId();
      this.status = span.getStatus();
      this.origin = span.getSpanContext().getOrigin();
      Map<String, String> tagsCopy = CollectionUtils.newConcurrentHashMap(span.getTags());
      this.tags = (Map<String, String>)(tagsCopy != null ? tagsCopy : new ConcurrentHashMap<>());
      Map<String, MeasurementValue> measurementsCopy = CollectionUtils.newConcurrentHashMap(span.getMeasurements());
      this.measurements = (Map<String, MeasurementValue>)(measurementsCopy != null ? measurementsCopy : new ConcurrentHashMap<>());
      this.timestamp = span.getFinishDate() == null ? null : DateUtils.nanosToSeconds(span.getStartDate().laterDateNanosTimestampByDiff(span.getFinishDate()));
      this.startTimestamp = DateUtils.nanosToSeconds(span.getStartDate().nanoTimestamp());
      this.data = data;
      IFeatureFlagBuffer featureFlagBuffer = span.getSpanContext().getFeatureFlagBuffer();
      FeatureFlags featureFlags = featureFlagBuffer.getFeatureFlags();
      if (featureFlags != null) {
         if (this.data == null) {
            this.data = new HashMap<>();
         }

         for (FeatureFlag featureFlag : featureFlags.getValues()) {
            this.data.put("flag.evaluation." + featureFlag.getFlag(), featureFlag.getResult());
         }
      }
   }

   @Internal
   public SentrySpan(
      @NotNull Double startTimestamp,
      @Nullable Double timestamp,
      @NotNull SentryId traceId,
      @NotNull SpanId spanId,
      @Nullable SpanId parentSpanId,
      @NotNull String op,
      @Nullable String description,
      @Nullable SpanStatus status,
      @Nullable String origin,
      @NotNull Map<String, String> tags,
      @NotNull Map<String, MeasurementValue> measurements,
      @Nullable Map<String, Object> data
   ) {
      this.startTimestamp = startTimestamp;
      this.timestamp = timestamp;
      this.traceId = traceId;
      this.spanId = spanId;
      this.parentSpanId = parentSpanId;
      this.op = op;
      this.description = description;
      this.status = status;
      this.origin = origin;
      this.tags = tags;
      this.measurements = measurements;
      this.data = data;
   }

   public boolean isFinished() {
      return this.timestamp != null;
   }

   @NotNull
   public Double getStartTimestamp() {
      return this.startTimestamp;
   }

   @Nullable
   public Double getTimestamp() {
      return this.timestamp;
   }

   @NotNull
   public SentryId getTraceId() {
      return this.traceId;
   }

   @NotNull
   public SpanId getSpanId() {
      return this.spanId;
   }

   @Nullable
   public SpanId getParentSpanId() {
      return this.parentSpanId;
   }

   @NotNull
   public String getOp() {
      return this.op;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   @Nullable
   public SpanStatus getStatus() {
      return this.status;
   }

   @NotNull
   public Map<String, String> getTags() {
      return this.tags;
   }

   @Nullable
   public Map<String, Object> getData() {
      return this.data;
   }

   public void setData(@Nullable Map<String, Object> data) {
      this.data = data;
   }

   @Nullable
   public String getOrigin() {
      return this.origin;
   }

   @NotNull
   public Map<String, MeasurementValue> getMeasurements() {
      return this.measurements;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("start_timestamp").value(logger, this.doubleToBigDecimal(this.startTimestamp));
      if (this.timestamp != null) {
         writer.name("timestamp").value(logger, this.doubleToBigDecimal(this.timestamp));
      }

      writer.name("trace_id").value(logger, this.traceId);
      writer.name("span_id").value(logger, this.spanId);
      if (this.parentSpanId != null) {
         writer.name("parent_span_id").value(logger, this.parentSpanId);
      }

      writer.name("op").value(this.op);
      if (this.description != null) {
         writer.name("description").value(this.description);
      }

      if (this.status != null) {
         writer.name("status").value(logger, this.status);
      }

      if (this.origin != null) {
         writer.name("origin").value(logger, this.origin);
      }

      if (!this.tags.isEmpty()) {
         writer.name("tags").value(logger, this.tags);
      }

      if (this.data != null) {
         writer.name("data").value(logger, this.data);
      }

      if (!this.measurements.isEmpty()) {
         writer.name("measurements").value(logger, this.measurements);
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

   @NotNull
   private BigDecimal doubleToBigDecimal(@NotNull Double value) {
      return BigDecimal.valueOf(value).setScale(6, RoundingMode.DOWN);
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

   public static final class Deserializer implements JsonDeserializer<SentrySpan> {
      @NotNull
      public SentrySpan deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         Double startTimestamp = null;
         Double timestamp = null;
         SentryId traceId = null;
         SpanId spanId = null;
         SpanId parentSpanId = null;
         String op = null;
         String description = null;
         SpanStatus status = null;
         String origin = null;
         Map<String, String> tags = null;
         Map<String, MeasurementValue> measurements = null;
         Map<String, Object> data = null;
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "start_timestamp":
                  try {
                     startTimestamp = reader.nextDoubleOrNull();
                  } catch (NumberFormatException var22) {
                     Date date = reader.nextDateOrNull(logger);
                     startTimestamp = date != null ? DateUtils.dateToSeconds(date) : null;
                  }
                  break;
               case "timestamp":
                  try {
                     timestamp = reader.nextDoubleOrNull();
                  } catch (NumberFormatException var21) {
                     Date date = reader.nextDateOrNull(logger);
                     timestamp = date != null ? DateUtils.dateToSeconds(date) : null;
                  }
                  break;
               case "trace_id":
                  traceId = new SentryId.Deserializer().deserialize(reader, logger);
                  break;
               case "span_id":
                  spanId = new SpanId.Deserializer().deserialize(reader, logger);
                  break;
               case "parent_span_id":
                  parentSpanId = reader.nextOrNull(logger, new SpanId.Deserializer());
                  break;
               case "op":
                  op = reader.nextStringOrNull();
                  break;
               case "description":
                  description = reader.nextStringOrNull();
                  break;
               case "status":
                  status = reader.nextOrNull(logger, new SpanStatus.Deserializer());
                  break;
               case "origin":
                  origin = reader.nextStringOrNull();
                  break;
               case "tags":
                  tags = (Map<String, String>)reader.nextObjectOrNull();
                  break;
               case "data":
                  data = (Map<String, Object>)reader.nextObjectOrNull();
                  break;
               case "measurements":
                  measurements = reader.nextMapOrNull(logger, new MeasurementValue.Deserializer());
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         if (startTimestamp == null) {
            throw this.missingRequiredFieldException("start_timestamp", logger);
         } else if (traceId == null) {
            throw this.missingRequiredFieldException("trace_id", logger);
         } else if (spanId == null) {
            throw this.missingRequiredFieldException("span_id", logger);
         } else if (op == null) {
            throw this.missingRequiredFieldException("op", logger);
         } else {
            if (tags == null) {
               tags = new HashMap<>();
            }

            if (measurements == null) {
               measurements = new HashMap<>();
            }

            SentrySpan sentrySpan = new SentrySpan(
               startTimestamp, timestamp, traceId, spanId, parentSpanId, op, description, status, origin, tags, measurements, data
            );
            sentrySpan.setUnknown(unknown);
            reader.endObject();
            return sentrySpan;
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
      public static final String START_TIMESTAMP = "start_timestamp";
      public static final String TIMESTAMP = "timestamp";
      public static final String TRACE_ID = "trace_id";
      public static final String SPAN_ID = "span_id";
      public static final String PARENT_SPAN_ID = "parent_span_id";
      public static final String OP = "op";
      public static final String DESCRIPTION = "description";
      public static final String STATUS = "status";
      public static final String ORIGIN = "origin";
      public static final String TAGS = "tags";
      public static final String MEASUREMENTS = "measurements";
      public static final String DATA = "data";
   }
}
