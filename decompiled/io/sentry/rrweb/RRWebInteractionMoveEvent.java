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
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RRWebInteractionMoveEvent extends RRWebIncrementalSnapshotEvent implements JsonSerializable, JsonUnknown {
   private int pointerId;
   @Nullable
   private List<RRWebInteractionMoveEvent.Position> positions;
   @Nullable
   private Map<String, Object> unknown;
   @Nullable
   private Map<String, Object> dataUnknown;

   public RRWebInteractionMoveEvent() {
      super(RRWebIncrementalSnapshotEvent.IncrementalSource.TouchMove);
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

   @Nullable
   public List<RRWebInteractionMoveEvent.Position> getPositions() {
      return this.positions;
   }

   public void setPositions(@Nullable List<RRWebInteractionMoveEvent.Position> positions) {
      this.positions = positions;
   }

   public int getPointerId() {
      return this.pointerId;
   }

   public void setPointerId(int pointerId) {
      this.pointerId = pointerId;
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
      if (this.positions != null && !this.positions.isEmpty()) {
         writer.name("positions").value(logger, this.positions);
      }

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

   public static final class Deserializer implements JsonDeserializer<RRWebInteractionMoveEvent> {
      @NotNull
      public RRWebInteractionMoveEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         Map<String, Object> unknown = null;
         RRWebInteractionMoveEvent event = new RRWebInteractionMoveEvent();
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

      private void deserializeData(@NotNull RRWebInteractionMoveEvent event, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Map<String, Object> dataUnknown = null;
         RRWebIncrementalSnapshotEvent.Deserializer baseEventDeserializer = new RRWebIncrementalSnapshotEvent.Deserializer();
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "positions":
                  event.positions = reader.nextListOrNull(logger, new RRWebInteractionMoveEvent.Position.Deserializer());
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

   public static final class JsonKeys {
      public static final String DATA = "data";
      public static final String POSITIONS = "positions";
      public static final String POINTER_ID = "pointerId";
   }

   public static final class Position implements JsonSerializable, JsonUnknown {
      private int id;
      private float x;
      private float y;
      private long timeOffset;
      @Nullable
      private Map<String, Object> unknown;

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

      public long getTimeOffset() {
         return this.timeOffset;
      }

      public void setTimeOffset(long timeOffset) {
         this.timeOffset = timeOffset;
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
         writer.name("id").value((long)this.id);
         writer.name("x").value((double)this.x);
         writer.name("y").value((double)this.y);
         writer.name("timeOffset").value(this.timeOffset);
         if (this.unknown != null) {
            for (String key : this.unknown.keySet()) {
               Object value = this.unknown.get(key);
               writer.name(key);
               writer.value(logger, value);
            }
         }

         writer.endObject();
      }

      public static final class Deserializer implements JsonDeserializer<RRWebInteractionMoveEvent.Position> {
         @NotNull
         public RRWebInteractionMoveEvent.Position deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            reader.beginObject();
            Map<String, Object> unknown = null;
            RRWebInteractionMoveEvent.Position position = new RRWebInteractionMoveEvent.Position();

            while (reader.peek() == JsonToken.NAME) {
               String nextName = reader.nextName();
               switch (nextName) {
                  case "id":
                     position.id = reader.nextInt();
                     break;
                  case "x":
                     position.x = reader.nextFloat();
                     break;
                  case "y":
                     position.y = reader.nextFloat();
                     break;
                  case "timeOffset":
                     position.timeOffset = reader.nextLong();
                     break;
                  default:
                     if (unknown == null) {
                        unknown = new HashMap<>();
                     }

                     reader.nextUnknown(logger, unknown, nextName);
               }
            }

            position.setUnknown(unknown);
            reader.endObject();
            return position;
         }
      }

      public static final class JsonKeys {
         public static final String ID = "id";
         public static final String X = "x";
         public static final String Y = "y";
         public static final String TIME_OFFSET = "timeOffset";
      }
   }
}
