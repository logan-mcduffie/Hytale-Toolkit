package io.sentry.protocol;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryBaseEvent;
import io.sentry.SentryTracer;
import io.sentry.Span;
import io.sentry.SpanContext;
import io.sentry.SpanStatus;
import io.sentry.TracesSamplingDecision;
import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class SentryTransaction extends SentryBaseEvent implements JsonUnknown, JsonSerializable {
   @Nullable
   private String transaction;
   @NotNull
   private Double startTimestamp;
   @Nullable
   private Double timestamp;
   @NotNull
   private final List<SentrySpan> spans = new ArrayList<>();
   @NotNull
   private final String type = "transaction";
   @NotNull
   private final Map<String, MeasurementValue> measurements = new HashMap<>();
   @NotNull
   private TransactionInfo transactionInfo;
   @Nullable
   private Map<String, Object> unknown;

   public SentryTransaction(@NotNull SentryTracer sentryTracer) {
      super(sentryTracer.getEventId());
      Objects.requireNonNull(sentryTracer, "sentryTracer is required");
      this.startTimestamp = DateUtils.nanosToSeconds(sentryTracer.getStartDate().nanoTimestamp());
      this.timestamp = DateUtils.nanosToSeconds(sentryTracer.getStartDate().laterDateNanosTimestampByDiff(sentryTracer.getFinishDate()));
      this.transaction = sentryTracer.getName();

      for (Span span : sentryTracer.getChildren()) {
         if (Boolean.TRUE.equals(span.isSampled())) {
            this.spans.add(new SentrySpan(span));
         }
      }

      Contexts contexts = this.getContexts();
      contexts.putAll(sentryTracer.getContexts());
      SpanContext tracerContext = sentryTracer.getSpanContext();
      Map<String, Object> data = sentryTracer.getData();
      SpanContext tracerContextToSend = new SpanContext(
         tracerContext.getTraceId(),
         tracerContext.getSpanId(),
         tracerContext.getParentSpanId(),
         tracerContext.getOperation(),
         tracerContext.getDescription(),
         tracerContext.getSamplingDecision(),
         tracerContext.getStatus(),
         tracerContext.getOrigin()
      );

      for (Entry<String, String> tag : tracerContext.getTags().entrySet()) {
         this.setTag(tag.getKey(), tag.getValue());
      }

      if (data != null) {
         for (Entry<String, Object> tag : data.entrySet()) {
            tracerContextToSend.setData(tag.getKey(), tag.getValue());
         }
      }

      IFeatureFlagBuffer featureFlagBuffer = tracerContext.getFeatureFlagBuffer();
      FeatureFlags featureFlags = featureFlagBuffer.getFeatureFlags();
      if (featureFlags != null) {
         for (FeatureFlag featureFlag : featureFlags.getValues()) {
            tracerContextToSend.setData("flag.evaluation." + featureFlag.getFlag(), featureFlag.getResult());
         }
      }

      contexts.setTrace(tracerContextToSend);
      this.transactionInfo = new TransactionInfo(sentryTracer.getTransactionNameSource().apiName());
   }

   @Internal
   public SentryTransaction(
      @Nullable String transaction,
      @NotNull Double startTimestamp,
      @Nullable Double timestamp,
      @NotNull List<SentrySpan> spans,
      @NotNull Map<String, MeasurementValue> measurements,
      @NotNull TransactionInfo transactionInfo
   ) {
      this.transaction = transaction;
      this.startTimestamp = startTimestamp;
      this.timestamp = timestamp;
      this.spans.addAll(spans);
      this.measurements.putAll(measurements);

      for (SentrySpan span : spans) {
         this.measurements.putAll(span.getMeasurements());
      }

      this.transactionInfo = transactionInfo;
   }

   @NotNull
   public List<SentrySpan> getSpans() {
      return this.spans;
   }

   public boolean isFinished() {
      return this.timestamp != null;
   }

   @Nullable
   public String getTransaction() {
      return this.transaction;
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
   public String getType() {
      return "transaction";
   }

   @Nullable
   public SpanStatus getStatus() {
      SpanContext trace = this.getContexts().getTrace();
      return trace != null ? trace.getStatus() : null;
   }

   public boolean isSampled() {
      TracesSamplingDecision samplingDecsion = this.getSamplingDecision();
      return samplingDecsion == null ? false : samplingDecsion.getSampled();
   }

   @Nullable
   public TracesSamplingDecision getSamplingDecision() {
      SpanContext trace = this.getContexts().getTrace();
      return trace == null ? null : trace.getSamplingDecision();
   }

   @NotNull
   public Map<String, MeasurementValue> getMeasurements() {
      return this.measurements;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.transaction != null) {
         writer.name("transaction").value(this.transaction);
      }

      writer.name("start_timestamp").value(logger, DateUtils.doubleToBigDecimal(this.startTimestamp));
      if (this.timestamp != null) {
         writer.name("timestamp").value(logger, DateUtils.doubleToBigDecimal(this.timestamp));
      }

      if (!this.spans.isEmpty()) {
         writer.name("spans").value(logger, this.spans);
      }

      writer.name("type").value("transaction");
      if (!this.measurements.isEmpty()) {
         writer.name("measurements").value(logger, this.measurements);
      }

      writer.name("transaction_info").value(logger, this.transactionInfo);
      new SentryBaseEvent.Serializer().serialize(this, writer, logger);
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

   public static final class Deserializer implements JsonDeserializer<SentryTransaction> {
      @NotNull
      public SentryTransaction deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         SentryTransaction transaction = new SentryTransaction(
            "", 0.0, null, new ArrayList<>(), new HashMap<>(), new TransactionInfo(TransactionNameSource.CUSTOM.apiName())
         );
         Map<String, Object> unknown = null;
         SentryBaseEvent.Deserializer baseEventDeserializer = new SentryBaseEvent.Deserializer();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "transaction":
                  transaction.transaction = reader.nextStringOrNull();
                  break;
               case "start_timestamp":
                  try {
                     Double deserializedStartTimestamp = reader.nextDoubleOrNull();
                     if (deserializedStartTimestamp != null) {
                        transaction.startTimestamp = deserializedStartTimestamp;
                     }
                  } catch (NumberFormatException var12) {
                     Date date = reader.nextDateOrNull(logger);
                     if (date != null) {
                        transaction.startTimestamp = DateUtils.dateToSeconds(date);
                     }
                  }
                  break;
               case "timestamp":
                  try {
                     Double deserializedTimestamp = reader.nextDoubleOrNull();
                     if (deserializedTimestamp != null) {
                        transaction.timestamp = deserializedTimestamp;
                     }
                  } catch (NumberFormatException var11) {
                     Date date = reader.nextDateOrNull(logger);
                     if (date != null) {
                        transaction.timestamp = DateUtils.dateToSeconds(date);
                     }
                  }
                  break;
               case "spans":
                  List<SentrySpan> deserializedSpans = reader.nextListOrNull(logger, new SentrySpan.Deserializer());
                  if (deserializedSpans != null) {
                     transaction.spans.addAll(deserializedSpans);
                  }
                  break;
               case "type":
                  reader.nextString();
                  break;
               case "measurements":
                  Map<String, MeasurementValue> deserializedMeasurements = reader.nextMapOrNull(logger, new MeasurementValue.Deserializer());
                  if (deserializedMeasurements != null) {
                     transaction.measurements.putAll(deserializedMeasurements);
                  }
                  break;
               case "transaction_info":
                  transaction.transactionInfo = new TransactionInfo.Deserializer().deserialize(reader, logger);
                  break;
               default:
                  if (!baseEventDeserializer.deserializeValue(transaction, nextName, reader, logger)) {
                     if (unknown == null) {
                        unknown = new ConcurrentHashMap<>();
                     }

                     reader.nextUnknown(logger, unknown, nextName);
                  }
            }
         }

         transaction.setUnknown(unknown);
         reader.endObject();
         return transaction;
      }
   }

   public static final class JsonKeys {
      public static final String TRANSACTION = "transaction";
      public static final String START_TIMESTAMP = "start_timestamp";
      public static final String TIMESTAMP = "timestamp";
      public static final String SPANS = "spans";
      public static final String TYPE = "type";
      public static final String MEASUREMENTS = "measurements";
      public static final String TRANSACTION_INFO = "transaction_info";
   }
}
