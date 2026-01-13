package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.TestOnly;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class MeasurementValue implements JsonUnknown, JsonSerializable {
   public static final String KEY_APP_START_COLD = "app_start_cold";
   public static final String KEY_APP_START_WARM = "app_start_warm";
   public static final String KEY_FRAMES_TOTAL = "frames_total";
   public static final String KEY_FRAMES_SLOW = "frames_slow";
   public static final String KEY_FRAMES_FROZEN = "frames_frozen";
   public static final String KEY_FRAMES_DELAY = "frames_delay";
   public static final String KEY_TIME_TO_INITIAL_DISPLAY = "time_to_initial_display";
   public static final String KEY_TIME_TO_FULL_DISPLAY = "time_to_full_display";
   @NotNull
   private final Number value;
   @Nullable
   private final String unit;
   @Nullable
   private Map<String, Object> unknown;

   public MeasurementValue(@NotNull Number value, @Nullable String unit) {
      this.value = value;
      this.unit = unit;
   }

   @TestOnly
   public MeasurementValue(@NotNull Number value, @Nullable String unit, @Nullable Map<String, Object> unknown) {
      this.value = value;
      this.unit = unit;
      this.unknown = unknown;
   }

   @TestOnly
   @NotNull
   public Number getValue() {
      return this.value;
   }

   @Nullable
   public String getUnit() {
      return this.unit;
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
      writer.name("value").value(this.value);
      if (this.unit != null) {
         writer.name("unit").value(this.unit);
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

   public static final class Deserializer implements JsonDeserializer<MeasurementValue> {
      @NotNull
      public MeasurementValue deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         String unit = null;
         Number value = null;
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "value":
                  value = (Number)reader.nextObjectOrNull();
                  break;
               case "unit":
                  unit = reader.nextStringOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         if (value == null) {
            String message = "Missing required field \"value\"";
            Exception ex = new IllegalStateException("Missing required field \"value\"");
            logger.log(SentryLevel.ERROR, "Missing required field \"value\"", ex);
            throw ex;
         } else {
            MeasurementValue measurement = new MeasurementValue(value, unit);
            measurement.setUnknown(unknown);
            return measurement;
         }
      }
   }

   public static final class JsonKeys {
      public static final String VALUE = "value";
      public static final String UNIT = "unit";
   }
}
