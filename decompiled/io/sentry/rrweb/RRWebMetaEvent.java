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

public final class RRWebMetaEvent extends RRWebEvent implements JsonUnknown, JsonSerializable {
   @NotNull
   private String href = "";
   private int height;
   private int width;
   @Nullable
   private Map<String, Object> unknown;
   @Nullable
   private Map<String, Object> dataUnknown;

   public RRWebMetaEvent() {
      super(RRWebEventType.Meta);
   }

   @NotNull
   public String getHref() {
      return this.href;
   }

   public void setHref(@NotNull String href) {
      this.href = href;
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

   @Nullable
   public Map<String, Object> getDataUnknown() {
      return this.dataUnknown;
   }

   public void setDataUnknown(@Nullable Map<String, Object> dataUnknown) {
      this.dataUnknown = dataUnknown;
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
         RRWebMetaEvent metaEvent = (RRWebMetaEvent)o;
         return this.height == metaEvent.height && this.width == metaEvent.width && Objects.equals(this.href, metaEvent.href);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(super.hashCode(), this.href, this.height, this.width);
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      new RRWebEvent.Serializer().serialize(this, writer, logger);
      writer.name("data");
      this.serializeData(writer, logger);
      writer.endObject();
   }

   private void serializeData(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("href").value(this.href);
      writer.name("height").value((long)this.height);
      writer.name("width").value((long)this.width);
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

   public static final class Deserializer implements JsonDeserializer<RRWebMetaEvent> {
      @NotNull
      public RRWebMetaEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         Map<String, Object> unknown = null;
         RRWebMetaEvent event = new RRWebMetaEvent();
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

      private void deserializeData(@NotNull RRWebMetaEvent event, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Map<String, Object> unknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "href":
                  String href = reader.nextStringOrNull();
                  event.href = href == null ? "" : href;
                  break;
               case "height":
                  Integer height = reader.nextIntegerOrNull();
                  event.height = height == null ? 0 : height;
                  break;
               case "width":
                  Integer width = reader.nextIntegerOrNull();
                  event.width = width == null ? 0 : width;
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         event.setDataUnknown(unknown);
         reader.endObject();
      }
   }

   public static final class JsonKeys {
      public static final String DATA = "data";
      public static final String HREF = "href";
      public static final String HEIGHT = "height";
      public static final String WIDTH = "width";
   }
}
