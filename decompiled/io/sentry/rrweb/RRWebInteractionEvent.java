package io.sentry.rrweb;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RRWebInteractionEvent extends RRWebIncrementalSnapshotEvent implements JsonSerializable, JsonUnknown {
   private static final int POINTER_TYPE_TOUCH = 2;
   @Nullable
   private RRWebInteractionEvent.InteractionType interactionType;
   private int id;
   private float x;
   private float y;
   private int pointerType = 2;
   private int pointerId;
   @Nullable
   private Map<String, Object> unknown;
   @Nullable
   private Map<String, Object> dataUnknown;

   public RRWebInteractionEvent() {
      super(RRWebIncrementalSnapshotEvent.IncrementalSource.MouseInteraction);
   }

   @Nullable
   public RRWebInteractionEvent.InteractionType getInteractionType() {
      return this.interactionType;
   }

   public void setInteractionType(@Nullable RRWebInteractionEvent.InteractionType type) {
      this.interactionType = type;
   }

   public int getId() {
      return this.id;
   }

   public void setId(int id) {
      this.id = id;
   }

   public float getX() {
      return this.x;
   }

   public void setX(float x) {
      this.x = x;
   }

   public float getY() {
      return this.y;
   }

   public void setY(float y) {
      this.y = y;
   }

   public int getPointerType() {
      return this.pointerType;
   }

   public void setPointerType(int pointerType) {
      this.pointerType = pointerType;
   }

   public int getPointerId() {
      return this.pointerId;
   }

   public void setPointerId(int pointerId) {
      this.pointerId = pointerId;
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
      new RRWebIncrementalSnapshotEvent.Serializer().serialize(this, writer, logger);
      writer.name("type").value(logger, this.interactionType);
      writer.name("id").value((long)this.id);
      writer.name("x").value((double)this.x);
      writer.name("y").value((double)this.y);
      writer.name("pointerType").value((long)this.pointerType);
      writer.name("pointerId").value((long)this.pointerId);
      if (this.dataUnknown != null) {
         for (String key : this.dataUnknown.keySet()) {
            Object value = this.dataUnknown.get(key);
            writer.name(key);
            writer.value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<RRWebInteractionEvent> {
      @NotNull
      public RRWebInteractionEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         Map<String, Object> unknown = null;
         RRWebInteractionEvent event = new RRWebInteractionEvent();
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

      private void deserializeData(@NotNull RRWebInteractionEvent event, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Map<String, Object> dataUnknown = null;
         RRWebIncrementalSnapshotEvent.Deserializer baseEventDeserializer = new RRWebIncrementalSnapshotEvent.Deserializer();
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "type":
                  event.interactionType = reader.nextOrNull(logger, new RRWebInteractionEvent.InteractionType.Deserializer());
                  break;
               case "id":
                  event.id = reader.nextInt();
                  break;
               case "x":
                  event.x = reader.nextFloat();
                  break;
               case "y":
                  event.y = reader.nextFloat();
                  break;
               case "pointerType":
                  event.pointerType = reader.nextInt();
                  break;
               case "pointerId":
                  event.pointerId = reader.nextInt();
                  break;
               default:
                  if (!baseEventDeserializer.deserializeValue(event, nextName, reader, logger)) {
                     if (dataUnknown == null) {
                        dataUnknown = new HashMap<>();
                     }

                     reader.nextUnknown(logger, dataUnknown, nextName);
                  }
            }
         }

         event.setDataUnknown(dataUnknown);
         reader.endObject();
      }
   }

   public static enum InteractionType implements JsonSerializable {
      MouseUp,
      MouseDown,
      Click,
      ContextMenu,
      DblClick,
      Focus,
      Blur,
      TouchStart,
      TouchMove_Departed,
      TouchEnd,
      TouchCancel;

      @Override
      public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
         writer.value((long)this.ordinal());
      }

      public static final class Deserializer implements JsonDeserializer<RRWebInteractionEvent.InteractionType> {
         @NotNull
         public RRWebInteractionEvent.InteractionType deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            return RRWebInteractionEvent.InteractionType.values()[reader.nextInt()];
         }
      }
   }

   public static final class JsonKeys {
      public static final String DATA = "data";
      public static final String TYPE = "type";
      public static final String ID = "id";
      public static final String X = "x";
      public static final String Y = "y";
      public static final String POINTER_TYPE = "pointerType";
      public static final String POINTER_ID = "pointerId";
   }
}
