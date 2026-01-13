package io.sentry.profilemeasurements;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ProfileMeasurement implements JsonUnknown, JsonSerializable {
   public static final String ID_FROZEN_FRAME_RENDERS = "frozen_frame_renders";
   public static final String ID_SLOW_FRAME_RENDERS = "slow_frame_renders";
   public static final String ID_SCREEN_FRAME_RATES = "screen_frame_rates";
   public static final String ID_CPU_USAGE = "cpu_usage";
   public static final String ID_MEMORY_FOOTPRINT = "memory_footprint";
   public static final String ID_MEMORY_NATIVE_FOOTPRINT = "memory_native_footprint";
   public static final String ID_UNKNOWN = "unknown";
   public static final String UNIT_HZ = "hz";
   public static final String UNIT_NANOSECONDS = "nanosecond";
   public static final String UNIT_BYTES = "byte";
   public static final String UNIT_PERCENT = "percent";
   public static final String UNIT_UNKNOWN = "unknown";
   @Nullable
   private Map<String, Object> unknown;
   @NotNull
   private String unit;
   @NotNull
   private Collection<ProfileMeasurementValue> values;

   public ProfileMeasurement() {
      this("unknown", new ArrayList<>());
   }

   public ProfileMeasurement(@NotNull String unit, @NotNull Collection<ProfileMeasurementValue> values) {
      this.unit = unit;
      this.values = values;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ProfileMeasurement that = (ProfileMeasurement)o;
         return Objects.equals(this.unknown, that.unknown) && this.unit.equals(that.unit) && new ArrayList<>(this.values).equals(new ArrayList<>(that.values));
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.unknown, this.unit, this.values);
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("unit").value(logger, this.unit);
      writer.name("values").value(logger, this.values);
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

   @NotNull
   public String getUnit() {
      return this.unit;
   }

   @Override
   public void setUnknown(@Nullable Map<String, Object> unknown) {
      this.unknown = unknown;
   }

   public void setUnit(@NotNull String unit) {
      this.unit = unit;
   }

   @NotNull
   public Collection<ProfileMeasurementValue> getValues() {
      return this.values;
   }

   public void setValues(@NotNull Collection<ProfileMeasurementValue> values) {
      this.values = values;
   }

   public static final class Deserializer implements JsonDeserializer<ProfileMeasurement> {
      @NotNull
      public ProfileMeasurement deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         ProfileMeasurement data = new ProfileMeasurement();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "unit":
                  String unit = reader.nextStringOrNull();
                  if (unit != null) {
                     data.unit = unit;
                  }
                  break;
               case "values":
                  List<ProfileMeasurementValue> values = reader.nextListOrNull(logger, new ProfileMeasurementValue.Deserializer());
                  if (values != null) {
                     data.values = values;
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
      public static final String UNIT = "unit";
      public static final String VALUES = "values";
   }
}
