package io.sentry.profilemeasurements;

import io.sentry.DateUtils;
import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ProfileMeasurementValue implements JsonUnknown, JsonSerializable {
   @Nullable
   private Map<String, Object> unknown;
   private double timestamp;
   @NotNull
   private String relativeStartNs;
   private double value;

   public ProfileMeasurementValue() {
      this(0L, 0, 0L);
   }

   public ProfileMeasurementValue(@NotNull Long relativeStartNs, @NotNull Number value, long nanoTimestamp) {
      this.relativeStartNs = relativeStartNs.toString();
      this.value = value.doubleValue();
      this.timestamp = DateUtils.nanosToSeconds(nanoTimestamp);
   }

   public double getTimestamp() {
      return this.timestamp;
   }

   public double getValue() {
      return this.value;
   }

   @NotNull
   public String getRelativeStartNs() {
      return this.relativeStartNs;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ProfileMeasurementValue that = (ProfileMeasurementValue)o;
         return Objects.equals(this.unknown, that.unknown)
            && this.relativeStartNs.equals(that.relativeStartNs)
            && this.value == that.value
            && this.timestamp == that.timestamp;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.unknown, this.relativeStartNs, this.value);
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("value").value(logger, this.value);
      writer.name("elapsed_since_start_ns").value(logger, this.relativeStartNs);
      writer.name("timestamp").value(logger, this.doubleToBigDecimal(this.timestamp));
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

   public static final class Deserializer implements JsonDeserializer<ProfileMeasurementValue> {
      @NotNull
      public ProfileMeasurementValue deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         ProfileMeasurementValue data = new ProfileMeasurementValue();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "value":
                  Double value = reader.nextDoubleOrNull();
                  if (value != null) {
                     data.value = value;
                  }
                  break;
               case "elapsed_since_start_ns":
                  String startNs = reader.nextStringOrNull();
                  if (startNs != null) {
                     data.relativeStartNs = startNs;
                  }
                  break;
               case "timestamp":
                  Double timestamp;
                  try {
                     timestamp = reader.nextDoubleOrNull();
                  } catch (NumberFormatException var13) {
                     Date date = reader.nextDateOrNull(logger);
                     timestamp = date != null ? DateUtils.dateToSeconds(date) : null;
                  }

                  if (timestamp != null) {
                     data.timestamp = timestamp;
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
      public static final String VALUE = "value";
      public static final String START_NS = "elapsed_since_start_ns";
      public static final String TIMESTAMP = "timestamp";
   }
}
