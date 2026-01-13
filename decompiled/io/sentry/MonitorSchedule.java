package io.sentry;

import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class MonitorSchedule implements JsonUnknown, JsonSerializable {
   @NotNull
   private String type;
   @NotNull
   private String value;
   @Nullable
   private String unit;
   @Nullable
   private Map<String, Object> unknown;

   @NotNull
   public static MonitorSchedule crontab(@NotNull String value) {
      return new MonitorSchedule(MonitorScheduleType.CRONTAB.apiName(), value, null);
   }

   @NotNull
   public static MonitorSchedule interval(@NotNull Integer value, @NotNull MonitorScheduleUnit unit) {
      return new MonitorSchedule(MonitorScheduleType.INTERVAL.apiName(), value.toString(), unit.apiName());
   }

   @Internal
   public MonitorSchedule(@NotNull String type, @NotNull String value, @Nullable String unit) {
      this.type = type;
      this.value = value;
      this.unit = unit;
   }

   @NotNull
   public String getType() {
      return this.type;
   }

   public void setType(@NotNull String type) {
      this.type = type;
   }

   @NotNull
   public String getValue() {
      return this.value;
   }

   public void setValue(@NotNull String value) {
      this.value = value;
   }

   public void setValue(@NotNull Integer value) {
      this.value = value.toString();
   }

   @Nullable
   public String getUnit() {
      return this.unit;
   }

   public void setUnit(@Nullable String unit) {
      this.unit = unit;
   }

   public void setUnit(@Nullable MonitorScheduleUnit unit) {
      this.unit = unit == null ? null : unit.apiName();
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
      writer.name("type").value(this.type);
      if (MonitorScheduleType.INTERVAL.apiName().equalsIgnoreCase(this.type)) {
         try {
            writer.name("value").value(Integer.valueOf(this.value));
         } catch (Throwable var6) {
            logger.log(SentryLevel.ERROR, "Unable to serialize monitor schedule value: %s", this.value);
         }
      } else {
         writer.name("value").value(this.value);
      }

      if (this.unit != null) {
         writer.name("unit").value(this.unit);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<MonitorSchedule> {
      @NotNull
      public MonitorSchedule deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         String type = null;
         String value = null;
         String unit = null;
         Map<String, Object> unknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "type":
                  type = reader.nextStringOrNull();
                  break;
               case "value":
                  value = reader.nextStringOrNull();
                  break;
               case "unit":
                  unit = reader.nextStringOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         if (type == null) {
            String message = "Missing required field \"type\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else if (value == null) {
            String message = "Missing required field \"value\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else {
            MonitorSchedule monitorSchedule = new MonitorSchedule(type, value, unit);
            monitorSchedule.setUnknown(unknown);
            return monitorSchedule;
         }
      }
   }

   public static final class JsonKeys {
      public static final String TYPE = "type";
      public static final String VALUE = "value";
      public static final String UNIT = "unit";
   }
}
