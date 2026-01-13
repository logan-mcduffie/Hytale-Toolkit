package io.sentry.rrweb;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.util.CollectionUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RRWebSpanEvent extends RRWebEvent implements JsonSerializable, JsonUnknown {
   public static final String EVENT_TAG = "performanceSpan";
   @NotNull
   private String tag = "performanceSpan";
   @Nullable
   private String op;
   @Nullable
   private String description;
   private double startTimestamp;
   private double endTimestamp;
   @Nullable
   private Map<String, Object> data;
   @Nullable
   private Map<String, Object> unknown;
   @Nullable
   private Map<String, Object> payloadUnknown;
   @Nullable
   private Map<String, Object> dataUnknown;

   public RRWebSpanEvent() {
      super(RRWebEventType.Custom);
   }

   @NotNull
   public String getTag() {
      return this.tag;
   }

   public void setTag(@NotNull String tag) {
      this.tag = tag;
   }

   @Nullable
   public String getOp() {
      return this.op;
   }

   public void setOp(@Nullable String op) {
      this.op = op;
   }

   @Nullable
   public String getDescription() {
      return this.description;
   }

   public void setDescription(@Nullable String description) {
      this.description = description;
   }

   public double getStartTimestamp() {
      return this.startTimestamp;
   }

   public void setStartTimestamp(double startTimestamp) {
      this.startTimestamp = startTimestamp;
   }

   public double getEndTimestamp() {
      return this.endTimestamp;
   }

   public void setEndTimestamp(double endTimestamp) {
      this.endTimestamp = endTimestamp;
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
      if (this.op != null) {
         writer.name("op").value(this.op);
      }

      if (this.description != null) {
         writer.name("description").value(this.description);
      }

      writer.name("startTimestamp").value(logger, BigDecimal.valueOf(this.startTimestamp));
      writer.name("endTimestamp").value(logger, BigDecimal.valueOf(this.endTimestamp));
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

   public static final class Deserializer implements JsonDeserializer<RRWebSpanEvent> {
      @NotNull
      public RRWebSpanEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         Map<String, Object> unknown = null;
         RRWebSpanEvent event = new RRWebSpanEvent();
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

      private void deserializeData(@NotNull RRWebSpanEvent event, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
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

      private void deserializePayload(@NotNull RRWebSpanEvent event, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Map<String, Object> payloadUnknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "op":
                  event.op = reader.nextStringOrNull();
                  break;
               case "description":
                  event.description = reader.nextStringOrNull();
                  break;
               case "startTimestamp":
                  event.startTimestamp = reader.nextDouble();
                  break;
               case "endTimestamp":
                  event.endTimestamp = reader.nextDouble();
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
      public static final String OP = "op";
      public static final String DESCRIPTION = "description";
      public static final String START_TIMESTAMP = "startTimestamp";
      public static final String END_TIMESTAMP = "endTimestamp";
   }
}
