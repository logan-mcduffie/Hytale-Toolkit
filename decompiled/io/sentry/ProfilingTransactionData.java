package io.sentry;

import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ProfilingTransactionData implements JsonUnknown, JsonSerializable {
   @NotNull
   private String id;
   @NotNull
   private String traceId;
   @NotNull
   private String name;
   @NotNull
   private Long relativeStartNs;
   @Nullable
   private Long relativeEndNs;
   @NotNull
   private Long relativeStartCpuMs;
   @Nullable
   private Long relativeEndCpuMs;
   @Nullable
   private Map<String, Object> unknown;

   public ProfilingTransactionData() {
      this(NoOpTransaction.getInstance(), 0L, 0L);
   }

   public ProfilingTransactionData(@NotNull ITransaction transaction, @NotNull Long startNs, @NotNull Long startCpuMs) {
      this.id = transaction.getEventId().toString();
      this.traceId = transaction.getSpanContext().getTraceId().toString();
      this.name = transaction.getName().isEmpty() ? "unknown" : transaction.getName();
      this.relativeStartNs = startNs;
      this.relativeStartCpuMs = startCpuMs;
   }

   public void notifyFinish(@NotNull Long endNs, @NotNull Long profileStartNs, @NotNull Long endCpuMs, @NotNull Long profileStartCpuMs) {
      if (this.relativeEndNs == null) {
         this.relativeEndNs = endNs - profileStartNs;
         this.relativeStartNs = this.relativeStartNs - profileStartNs;
         this.relativeEndCpuMs = endCpuMs - profileStartCpuMs;
         this.relativeStartCpuMs = this.relativeStartCpuMs - profileStartCpuMs;
      }
   }

   @NotNull
   public String getId() {
      return this.id;
   }

   @NotNull
   public String getTraceId() {
      return this.traceId;
   }

   @NotNull
   public String getName() {
      return this.name;
   }

   @NotNull
   public Long getRelativeStartNs() {
      return this.relativeStartNs;
   }

   @Nullable
   public Long getRelativeEndNs() {
      return this.relativeEndNs;
   }

   @Nullable
   public Long getRelativeEndCpuMs() {
      return this.relativeEndCpuMs;
   }

   @NotNull
   public Long getRelativeStartCpuMs() {
      return this.relativeStartCpuMs;
   }

   public void setId(@NotNull String id) {
      this.id = id;
   }

   public void setTraceId(@NotNull String traceId) {
      this.traceId = traceId;
   }

   public void setName(@NotNull String name) {
      this.name = name;
   }

   public void setRelativeStartNs(@NotNull Long relativeStartNs) {
      this.relativeStartNs = relativeStartNs;
   }

   public void setRelativeEndNs(@Nullable Long relativeEndNs) {
      this.relativeEndNs = relativeEndNs;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ProfilingTransactionData that = (ProfilingTransactionData)o;
         return this.id.equals(that.id)
            && this.traceId.equals(that.traceId)
            && this.name.equals(that.name)
            && this.relativeStartNs.equals(that.relativeStartNs)
            && this.relativeStartCpuMs.equals(that.relativeStartCpuMs)
            && Objects.equals(this.relativeEndCpuMs, that.relativeEndCpuMs)
            && Objects.equals(this.relativeEndNs, that.relativeEndNs)
            && Objects.equals(this.unknown, that.unknown);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.id, this.traceId, this.name, this.relativeStartNs, this.relativeEndNs, this.relativeStartCpuMs, this.relativeEndCpuMs, this.unknown
      );
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("id").value(logger, this.id);
      writer.name("trace_id").value(logger, this.traceId);
      writer.name("name").value(logger, this.name);
      writer.name("relative_start_ns").value(logger, this.relativeStartNs);
      writer.name("relative_end_ns").value(logger, this.relativeEndNs);
      writer.name("relative_cpu_start_ms").value(logger, this.relativeStartCpuMs);
      writer.name("relative_cpu_end_ms").value(logger, this.relativeEndCpuMs);
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

   public static final class Deserializer implements JsonDeserializer<ProfilingTransactionData> {
      @NotNull
      public ProfilingTransactionData deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         ProfilingTransactionData data = new ProfilingTransactionData();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "id":
                  String id = reader.nextStringOrNull();
                  if (id != null) {
                     data.id = id;
                  }
                  break;
               case "trace_id":
                  String traceId = reader.nextStringOrNull();
                  if (traceId != null) {
                     data.traceId = traceId;
                  }
                  break;
               case "name":
                  String name = reader.nextStringOrNull();
                  if (name != null) {
                     data.name = name;
                  }
                  break;
               case "relative_start_ns":
                  Long startNs = reader.nextLongOrNull();
                  if (startNs != null) {
                     data.relativeStartNs = startNs;
                  }
                  break;
               case "relative_end_ns":
                  Long endNs = reader.nextLongOrNull();
                  if (endNs != null) {
                     data.relativeEndNs = endNs;
                  }
                  break;
               case "relative_cpu_start_ms":
                  Long startCpuMs = reader.nextLongOrNull();
                  if (startCpuMs != null) {
                     data.relativeStartCpuMs = startCpuMs;
                  }
                  break;
               case "relative_cpu_end_ms":
                  Long endCpuMs = reader.nextLongOrNull();
                  if (endCpuMs != null) {
                     data.relativeEndCpuMs = endCpuMs;
                  }
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         data.setUnknown(unknown);
         reader.endObject();
         return data;
      }
   }

   public static final class JsonKeys {
      public static final String ID = "id";
      public static final String TRACE_ID = "trace_id";
      public static final String NAME = "name";
      public static final String START_NS = "relative_start_ns";
      public static final String END_NS = "relative_end_ns";
      public static final String START_CPU_MS = "relative_cpu_start_ms";
      public static final String END_CPU_MS = "relative_cpu_end_ms";
   }
}
