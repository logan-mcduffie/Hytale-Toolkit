package io.sentry.rrweb;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.util.CollectionUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RRWebBreadcrumbEvent extends RRWebEvent implements JsonUnknown, JsonSerializable {
   public static final String EVENT_TAG = "breadcrumb";
   @NotNull
   private String tag = "breadcrumb";
   private double breadcrumbTimestamp;
   @Nullable
   private String breadcrumbType;
   @Nullable
   private String category;
   @Nullable
   private String message;
   @Nullable
   private SentryLevel level;
   @Nullable
   private Map<String, Object> data;
   @Nullable
   private Map<String, Object> unknown;
   @Nullable
   private Map<String, Object> payloadUnknown;
   @Nullable
   private Map<String, Object> dataUnknown;

   public RRWebBreadcrumbEvent() {
      super(RRWebEventType.Custom);
   }

   @NotNull
   public String getTag() {
      return this.tag;
   }

   public void setTag(@NotNull String tag) {
      this.tag = tag;
   }

   public double getBreadcrumbTimestamp() {
      return this.breadcrumbTimestamp;
   }

   public void setBreadcrumbTimestamp(double breadcrumbTimestamp) {
      this.breadcrumbTimestamp = breadcrumbTimestamp;
   }

   @Nullable
   public String getBreadcrumbType() {
      return this.breadcrumbType;
   }

   public void setBreadcrumbType(@Nullable String breadcrumbType) {
      this.breadcrumbType = breadcrumbType;
   }

   @Nullable
   public String getCategory() {
      return this.category;
   }

   public void setCategory(@Nullable String category) {
      this.category = category;
   }

   @Nullable
   public String getMessage() {
      return this.message;
   }

   public void setMessage(@Nullable String message) {
      this.message = message;
   }

   @Nullable
   public SentryLevel getLevel() {
      return this.level;
   }

   public void setLevel(@Nullable SentryLevel level) {
      this.level = level;
   }

   @Nullable
   public Map<String, Object> getData() {
      return this.data;
   }

   public void setData(@Nullable Map<String, Object> data) {
      this.data = data == null ? null : new ConcurrentHashMap<>(data);
   }

   @Nullable
   public Map<String, Object> getPayloadUnknown() {
      return this.payloadUnknown;
   }

   public void setPayloadUnknown(@Nullable Map<String, Object> payloadUnknown) {
      this.payloadUnknown = payloadUnknown;
   }

   @Nullable
   public Map<String, Object> getDataUnknown() {
      return this.dataUnknown;
   }

   public void setDataUnknown(@Nullable Map<String, Object> dataUnknown) {
      this.dataUnknown = dataUnknown;
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
      new RRWebEvent.Serializer().serialize(this, writer, logger);
      writer.name("data");
      this.serializeData(writer, logger);
      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key);
            writer.value(logger, value);
         }
      }

      writer.endObject();
   }

   private void serializeData(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("tag").value(this.tag);
      writer.name("payload");
      this.serializePayload(writer, logger);
      if (this.dataUnknown != null) {
         for (String key : this.dataUnknown.keySet()) {
            Object value = this.dataUnknown.get(key);
            writer.name(key);
            writer.value(logger, value);
         }
      }

      writer.endObject();
   }

   private void serializePayload(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.breadcrumbType != null) {
         writer.name("type").value(this.breadcrumbType);
      }

      writer.name("timestamp").value(logger, BigDecimal.valueOf(this.breadcrumbTimestamp));
      if (this.category != null) {
         writer.name("category").value(this.category);
      }

      if (this.message != null) {
         writer.name("message").value(this.message);
      }

      if (this.level != null) {
         writer.name("level").value(logger, this.level);
      }

      if (this.data != null) {
         writer.name("data").value(logger, this.data);
      }

      if (this.payloadUnknown != null) {
         for (String key : this.payloadUnknown.keySet()) {
            Object value = this.payloadUnknown.get(key);
            writer.name(key);
            writer.value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<RRWebBreadcrumbEvent> {
      @NotNull
      public RRWebBreadcrumbEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         Map<String, Object> unknown = null;
         RRWebBreadcrumbEvent event = new RRWebBreadcrumbEvent();
         RRWebEvent.Deserializer baseEventDeserializer = new RRWebEvent.Deserializer();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "data":
                  this.deserializeData(event, reader, logger);
                  break;
               default:
                  if (!baseEventDeserializer.deserializeValue(event, nextName, reader, logger)) {
                     if (unknown == null) {
                        unknown = new HashMap<>();
                     }

                     reader.nextUnknown(logger, unknown, nextName);
                  }
            }
         }

         event.setUnknown(unknown);
         reader.endObject();
         return event;
      }

      private void deserializeData(@NotNull RRWebBreadcrumbEvent event, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Map<String, Object> dataUnknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "tag":
                  String tag = reader.nextStringOrNull();
                  event.tag = tag == null ? "" : tag;
                  break;
               case "payload":
                  this.deserializePayload(event, reader, logger);
                  break;
               default:
                  if (dataUnknown == null) {
                     dataUnknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, dataUnknown, nextName);
            }
         }

         event.setDataUnknown(dataUnknown);
         reader.endObject();
      }

      private void deserializePayload(@NotNull RRWebBreadcrumbEvent event, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Map<String, Object> payloadUnknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "type":
                  event.breadcrumbType = reader.nextStringOrNull();
                  break;
               case "timestamp":
                  event.breadcrumbTimestamp = reader.nextDouble();
                  break;
               case "category":
                  event.category = reader.nextStringOrNull();
                  break;
               case "message":
                  event.message = reader.nextStringOrNull();
                  break;
               case "level":
                  try {
                     event.level = new SentryLevel.Deserializer().deserialize(reader, logger);
                  } catch (Exception var9) {
                     logger.log(SentryLevel.DEBUG, var9, "Error when deserializing SentryLevel");
                  }
                  break;
               case "data":
                  Map<String, Object> deserializedData = CollectionUtils.newConcurrentHashMap((Map<String, Object>)reader.nextObjectOrNull());
                  if (deserializedData != null) {
                     event.data = deserializedData;
                  }
                  break;
               default:
                  if (payloadUnknown == null) {
                     payloadUnknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, payloadUnknown, nextName);
            }
         }

         event.setPayloadUnknown(payloadUnknown);
         reader.endObject();
      }
   }

   public static final class JsonKeys {
      public static final String DATA = "data";
      public static final String PAYLOAD = "payload";
      public static final String TIMESTAMP = "timestamp";
      public static final String TYPE = "type";
      public static final String CATEGORY = "category";
      public static final String MESSAGE = "message";
      public static final String LEVEL = "level";
   }
}
