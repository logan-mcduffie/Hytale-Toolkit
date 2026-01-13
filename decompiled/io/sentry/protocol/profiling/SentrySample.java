package io.sentry.protocol.profiling;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentrySample implements JsonUnknown, JsonSerializable {
   private double timestamp;
   private int stackId;
   @Nullable
   private String threadId;
   @Nullable
   private Map<String, Object> unknown;

   public double getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(double timestamp) {
      this.timestamp = timestamp;
   }

   public int getStackId() {
      return this.stackId;
   }

   public void setStackId(int stackId) {
      this.stackId = stackId;
   }

   @Nullable
   public String getThreadId() {
      return this.threadId;
   }

   public void setThreadId(@Nullable String threadId) {
      this.threadId = threadId;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("timestamp").value(logger, this.doubleToBigDecimal(this.timestamp));
      writer.name("stack_id").value(logger, this.stackId);
      if (this.threadId != null) {
         writer.name("thread_id").value(logger, this.threadId);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
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

   public static final class Deserializer implements JsonDeserializer<SentrySample> {
      @NotNull
      public SentrySample deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         SentrySample data = new SentrySample();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "timestamp":
                  data.timestamp = reader.nextDouble();
                  break;
               case "stack_id":
                  data.stackId = reader.nextInt();
                  break;
               case "thread_id":
                  data.threadId = reader.nextStringOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
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
      public static final String TIMESTAMP = "timestamp";
      public static final String STACK_ID = "stack_id";
      public static final String THREAD_ID = "thread_id";
   }
}
