package io.sentry.rrweb;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RRWebVideoEvent extends RRWebEvent implements JsonUnknown, JsonSerializable {
   public static final String EVENT_TAG = "video";
   public static final String REPLAY_ENCODING = "h264";
   public static final String REPLAY_CONTAINER = "mp4";
   public static final String REPLAY_FRAME_RATE_TYPE_CONSTANT = "constant";
   public static final String REPLAY_FRAME_RATE_TYPE_VARIABLE = "variable";
   @NotNull
   private String tag;
   private int segmentId;
   private long size;
   private long durationMs;
   @NotNull
   private String encoding = "h264";
   @NotNull
   private String container = "mp4";
   private int height;
   private int width;
   private int frameCount;
   @NotNull
   private String frameRateType = "constant";
   private int frameRate;
   private int left;
   private int top;
   @Nullable
   private Map<String, Object> unknown;
   @Nullable
   private Map<String, Object> payloadUnknown;
   @Nullable
   private Map<String, Object> dataUnknown;

   public RRWebVideoEvent() {
      super(RRWebEventType.Custom);
      this.tag = "video";
   }

   @NotNull
   public String getTag() {
      return this.tag;
   }

   public void setTag(@NotNull String tag) {
      this.tag = tag;
   }

   public int getSegmentId() {
      return this.segmentId;
   }

   public void setSegmentId(int segmentId) {
      this.segmentId = segmentId;
   }

   public long getSize() {
      return this.size;
   }

   public void setSize(long size) {
      this.size = size;
   }

   public long getDurationMs() {
      return this.durationMs;
   }

   public void setDurationMs(long durationMs) {
      this.durationMs = durationMs;
   }

   @NotNull
   public String getEncoding() {
      return this.encoding;
   }

   public void setEncoding(@NotNull String encoding) {
      this.encoding = encoding;
   }

   @NotNull
   public String getContainer() {
      return this.container;
   }

   public void setContainer(@NotNull String container) {
      this.container = container;
   }

   public int getHeight() {
      return this.height;
   }

   public void setHeight(int height) {
      this.height = height;
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int width) {
      this.width = width;
   }

   public int getFrameCount() {
      return this.frameCount;
   }

   public void setFrameCount(int frameCount) {
      this.frameCount = frameCount;
   }

   @NotNull
   public String getFrameRateType() {
      return this.frameRateType;
   }

   public void setFrameRateType(@NotNull String frameRateType) {
      this.frameRateType = frameRateType;
   }

   public int getFrameRate() {
      return this.frameRate;
   }

   public void setFrameRate(int frameRate) {
      this.frameRate = frameRate;
   }

   public int getLeft() {
      return this.left;
   }

   public void setLeft(int left) {
      this.left = left;
   }

   public int getTop() {
      return this.top;
   }

   public void setTop(int top) {
      this.top = top;
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
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o == null || this.getClass() != o.getClass()) {
         return false;
      } else if (!super.equals(o)) {
         return false;
      } else {
         RRWebVideoEvent that = (RRWebVideoEvent)o;
         return this.segmentId == that.segmentId
            && this.size == that.size
            && this.durationMs == that.durationMs
            && this.height == that.height
            && this.width == that.width
            && this.frameCount == that.frameCount
            && this.frameRate == that.frameRate
            && this.left == that.left
            && this.top == that.top
            && Objects.equals(this.tag, that.tag)
            && Objects.equals(this.encoding, that.encoding)
            && Objects.equals(this.container, that.container)
            && Objects.equals(this.frameRateType, that.frameRateType);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         super.hashCode(),
         this.tag,
         this.segmentId,
         this.size,
         this.durationMs,
         this.encoding,
         this.container,
         this.height,
         this.width,
         this.frameCount,
         this.frameRateType,
         this.frameRate,
         this.left,
         this.top
      );
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
      writer.name("segmentId").value((long)this.segmentId);
      writer.name("size").value(this.size);
      writer.name("duration").value(this.durationMs);
      writer.name("encoding").value(this.encoding);
      writer.name("container").value(this.container);
      writer.name("height").value((long)this.height);
      writer.name("width").value((long)this.width);
      writer.name("frameCount").value((long)this.frameCount);
      writer.name("frameRate").value((long)this.frameRate);
      writer.name("frameRateType").value(this.frameRateType);
      writer.name("left").value((long)this.left);
      writer.name("top").value((long)this.top);
      if (this.payloadUnknown != null) {
         for (String key : this.payloadUnknown.keySet()) {
            Object value = this.payloadUnknown.get(key);
            writer.name(key);
            writer.value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<RRWebVideoEvent> {
      @NotNull
      public RRWebVideoEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         Map<String, Object> unknown = null;
         RRWebVideoEvent event = new RRWebVideoEvent();
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

      private void deserializeData(@NotNull RRWebVideoEvent event, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
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

      private void deserializePayload(@NotNull RRWebVideoEvent event, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Map<String, Object> payloadUnknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "segmentId":
                  event.segmentId = reader.nextInt();
                  break;
               case "size":
                  Long size = reader.nextLongOrNull();
                  event.size = size == null ? 0L : size;
                  break;
               case "duration":
                  event.durationMs = reader.nextLong();
                  break;
               case "container":
                  String container = reader.nextStringOrNull();
                  event.container = container == null ? "" : container;
                  break;
               case "encoding":
                  String encoding = reader.nextStringOrNull();
                  event.encoding = encoding == null ? "" : encoding;
                  break;
               case "height":
                  Integer height = reader.nextIntegerOrNull();
                  event.height = height == null ? 0 : height;
                  break;
               case "width":
                  Integer width = reader.nextIntegerOrNull();
                  event.width = width == null ? 0 : width;
                  break;
               case "frameCount":
                  Integer frameCount = reader.nextIntegerOrNull();
                  event.frameCount = frameCount == null ? 0 : frameCount;
                  break;
               case "frameRate":
                  Integer frameRate = reader.nextIntegerOrNull();
                  event.frameRate = frameRate == null ? 0 : frameRate;
                  break;
               case "frameRateType":
                  String frameRateType = reader.nextStringOrNull();
                  event.frameRateType = frameRateType == null ? "" : frameRateType;
                  break;
               case "left":
                  Integer left = reader.nextIntegerOrNull();
                  event.left = left == null ? 0 : left;
                  break;
               case "top":
                  Integer top = reader.nextIntegerOrNull();
                  event.top = top == null ? 0 : top;
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
      public static final String SEGMENT_ID = "segmentId";
      public static final String SIZE = "size";
      public static final String DURATION = "duration";
      public static final String ENCODING = "encoding";
      public static final String CONTAINER = "container";
      public static final String HEIGHT = "height";
      public static final String WIDTH = "width";
      public static final String FRAME_COUNT = "frameCount";
      public static final String FRAME_RATE_TYPE = "frameRateType";
      public static final String FRAME_RATE = "frameRate";
      public static final String LEFT = "left";
      public static final String TOP = "top";
   }
}
