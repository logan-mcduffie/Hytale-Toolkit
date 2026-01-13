package io.sentry;

import io.sentry.featureflags.IFeatureFlagBuffer;
import io.sentry.featureflags.SpanFeatureFlagBuffer;
import io.sentry.protocol.SentryId;
import io.sentry.util.CollectionUtils;
import io.sentry.util.Objects;
import io.sentry.util.thread.IThreadChecker;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.ApiStatus.Internal;

public class SpanContext implements JsonUnknown, JsonSerializable {
   public static final String TYPE = "trace";
   public static final String DEFAULT_ORIGIN = "manual";
   @NotNull
   private final SentryId traceId;
   @NotNull
   private final SpanId spanId;
   @Nullable
   private SpanId parentSpanId;
   @Nullable
   private transient TracesSamplingDecision samplingDecision;
   @NotNull
   protected String op;
   @Nullable
   protected String description;
   @Nullable
   protected SpanStatus status;
   @NotNull
   protected Map<String, String> tags = new ConcurrentHashMap<>();
   @Nullable
   protected String origin = "manual";
   @NotNull
   protected Map<String, Object> data = new ConcurrentHashMap<>();
   @Nullable
   private Map<String, Object> unknown;
   @NotNull
   private Instrumenter instrumenter = Instrumenter.SENTRY;
   @Nullable
   protected Baggage baggage;
   @NotNull
   protected IFeatureFlagBuffer featureFlags = SpanFeatureFlagBuffer.create();
   @NotNull
   private SentryId profilerId = SentryId.EMPTY_ID;

   public SpanContext(@NotNull String operation, @Nullable TracesSamplingDecision samplingDecision) {
      this(new SentryId(), new SpanId(), operation, null, samplingDecision);
   }

   public SpanContext(@NotNull String operation) {
      this(new SentryId(), new SpanId(), operation, null, null);
   }

   public SpanContext(
      @NotNull SentryId traceId,
      @NotNull SpanId spanId,
      @NotNull String operation,
      @Nullable SpanId parentSpanId,
      @Nullable TracesSamplingDecision samplingDecision
   ) {
      this(traceId, spanId, parentSpanId, operation, null, samplingDecision, null, "manual");
   }

   @Internal
   public SpanContext(
      @NotNull SentryId traceId,
      @NotNull SpanId spanId,
      @Nullable SpanId parentSpanId,
      @NotNull String operation,
      @Nullable String description,
      @Nullable TracesSamplingDecision samplingDecision,
      @Nullable SpanStatus status,
      @Nullable String origin
   ) {
      this.traceId = Objects.requireNonNull(traceId, "traceId is required");
      this.spanId = Objects.requireNonNull(spanId, "spanId is required");
      this.op = Objects.requireNonNull(operation, "operation is required");
      this.parentSpanId = parentSpanId;
      this.description = description;
      this.status = status;
      this.origin = origin;
      this.setSamplingDecision(samplingDecision);
      IThreadChecker threadChecker = ScopesAdapter.getInstance().getOptions().getThreadChecker();
      this.data.put("thread.id", String.valueOf(threadChecker.currentThreadSystemId()));
      this.data.put("thread.name", threadChecker.getCurrentThreadName());
   }

   public SpanContext(@NotNull SpanContext spanContext) {
      this.traceId = spanContext.traceId;
      this.spanId = spanContext.spanId;
      this.parentSpanId = spanContext.parentSpanId;
      this.setSamplingDecision(spanContext.samplingDecision);
      this.op = spanContext.op;
      this.description = spanContext.description;
      this.status = spanContext.status;
      Map<String, String> copiedTags = CollectionUtils.newConcurrentHashMap(spanContext.tags);
      if (copiedTags != null) {
         this.tags = copiedTags;
      }

      Map<String, Object> copiedUnknown = CollectionUtils.newConcurrentHashMap(spanContext.unknown);
      if (copiedUnknown != null) {
         this.unknown = copiedUnknown;
      }

      this.baggage = spanContext.baggage;
      Map<String, Object> copiedData = CollectionUtils.newConcurrentHashMap(spanContext.data);
      if (copiedData != null) {
         this.data = copiedData;
      }
   }

   public void setOperation(@NotNull String operation) {
      this.op = Objects.requireNonNull(operation, "operation is required");
   }

   public void setTag(@Nullable String name, @Nullable String value) {
      if (name != null) {
         if (value == null) {
            this.tags.remove(name);
         } else {
            this.tags.put(name, value);
         }
      }
   }

   public void setDescription(@Nullable String description) {
      this.description = description;
   }

   public void setStatus(@Nullable SpanStatus status) {
      this.status = status;
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
   @TestOnly
   public SpanId getParentSpanId() {
      return this.parentSpanId;
   }

   @NotNull
   public String getOperation() {
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
   public TracesSamplingDecision getSamplingDecision() {
      return this.samplingDecision;
   }

   @Nullable
   public Boolean getSampled() {
      return this.samplingDecision == null ? null : this.samplingDecision.getSampled();
   }

   @Nullable
   public Boolean getProfileSampled() {
      return this.samplingDecision == null ? null : this.samplingDecision.getProfileSampled();
   }

   @Internal
   public void setSampled(@Nullable Boolean sampled) {
      if (sampled == null) {
         this.setSamplingDecision(null);
      } else {
         this.setSamplingDecision(new TracesSamplingDecision(sampled));
      }
   }

   @Internal
   public void setSampled(@Nullable Boolean sampled, @Nullable Boolean profileSampled) {
      if (sampled == null) {
         this.setSamplingDecision(null);
      } else if (profileSampled == null) {
         this.setSamplingDecision(new TracesSamplingDecision(sampled));
      } else {
         this.setSamplingDecision(new TracesSamplingDecision(sampled, null, profileSampled, null));
      }
   }

   @Internal
   public void setSamplingDecision(@Nullable TracesSamplingDecision samplingDecision) {
      this.samplingDecision = samplingDecision;
      if (this.baggage != null) {
         this.baggage.setValuesFromSamplingDecision(this.samplingDecision);
      }
   }

   @Nullable
   public String getOrigin() {
      return this.origin;
   }

   public void setOrigin(@Nullable String origin) {
      this.origin = origin;
   }

   @NotNull
   public Instrumenter getInstrumenter() {
      return this.instrumenter;
   }

   public void setInstrumenter(@NotNull Instrumenter instrumenter) {
      this.instrumenter = instrumenter;
   }

   @Nullable
   public Baggage getBaggage() {
      return this.baggage;
   }

   @NotNull
   public Map<String, Object> getData() {
      return this.data;
   }

   public void setData(@Nullable String key, @Nullable Object value) {
      if (key != null) {
         if (value == null) {
            this.data.remove(key);
         } else {
            this.data.put(key, value);
         }
      }
   }

   @Internal
   public SpanContext copyForChild(@NotNull String operation, @Nullable SpanId parentSpanId, @Nullable SpanId spanId) {
      return new SpanContext(this.traceId, spanId == null ? new SpanId() : spanId, parentSpanId, operation, null, this.samplingDecision, null, "manual");
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof SpanContext)) {
         return false;
      } else {
         SpanContext that = (SpanContext)o;
         return this.traceId.equals(that.traceId)
            && this.spanId.equals(that.spanId)
            && Objects.equals(this.parentSpanId, that.parentSpanId)
            && this.op.equals(that.op)
            && Objects.equals(this.description, that.description)
            && this.getStatus() == that.getStatus();
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.traceId, this.spanId, this.parentSpanId, this.op, this.description, this.getStatus());
   }

   @Internal
   @NotNull
   public SentryId getProfilerId() {
      return this.profilerId;
   }

   @Internal
   public void setProfilerId(@NotNull SentryId profilerId) {
      this.profilerId = profilerId;
   }

   @Internal
   public void addFeatureFlag(@Nullable String flag, @Nullable Boolean result) {
      this.featureFlags.add(flag, result);
   }

   @Internal
   @NotNull
   public IFeatureFlagBuffer getFeatureFlagBuffer() {
      return this.featureFlags;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("trace_id");
      this.traceId.serialize(writer, logger);
      writer.name("span_id");
      this.spanId.serialize(writer, logger);
      if (this.parentSpanId != null) {
         writer.name("parent_span_id");
         this.parentSpanId.serialize(writer, logger);
      }

      writer.name("op").value(this.op);
      if (this.description != null) {
         writer.name("description").value(this.description);
      }

      if (this.getStatus() != null) {
         writer.name("status").value(logger, this.getStatus());
      }

      if (this.origin != null) {
         writer.name("origin").value(logger, this.origin);
      }

      if (!this.tags.isEmpty()) {
         writer.name("tags").value(logger, this.tags);
      }

      if (!this.data.isEmpty()) {
         writer.name("data").value(logger, this.data);
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

   public static final class Deserializer implements JsonDeserializer<SpanContext> {
      @NotNull
      public SpanContext deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         SentryId traceId = null;
         SpanId spanId = null;
         SpanId parentSpanId = null;
         String op = null;
         String description = null;
         SpanStatus status = null;
         String origin = null;
         Map<String, String> tags = null;
         Map<String, Object> data = null;
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
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
                  op = reader.nextString();
                  break;
               case "description":
                  description = reader.nextString();
                  break;
               case "status":
                  status = reader.nextOrNull(logger, new SpanStatus.Deserializer());
                  break;
               case "origin":
                  origin = reader.nextString();
                  break;
               case "tags":
                  tags = CollectionUtils.newConcurrentHashMap((Map<String, String>)reader.nextObjectOrNull());
                  break;
               case "data":
                  data = (Map<String, Object>)reader.nextObjectOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         if (traceId == null) {
            String message = "Missing required field \"trace_id\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else if (spanId == null) {
            String message = "Missing required field \"span_id\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else {
            if (op == null) {
               op = "";
            }

            SpanContext spanContext = new SpanContext(traceId, spanId, op, parentSpanId, null);
            spanContext.setDescription(description);
            spanContext.setStatus(status);
            spanContext.setOrigin(origin);
            if (tags != null) {
               spanContext.tags = tags;
            }

            if (data != null) {
               spanContext.data = data;
            }

            spanContext.setUnknown(unknown);
            reader.endObject();
            return spanContext;
         }
      }
   }

   public static final class JsonKeys {
      public static final String TRACE_ID = "trace_id";
      public static final String SPAN_ID = "span_id";
      public static final String PARENT_SPAN_ID = "parent_span_id";
      public static final String OP = "op";
      public static final String DESCRIPTION = "description";
      public static final String STATUS = "status";
      public static final String TAGS = "tags";
      public static final String ORIGIN = "origin";
      public static final String DATA = "data";
   }
}
