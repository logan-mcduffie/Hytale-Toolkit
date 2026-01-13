package io.sentry;

import io.sentry.rrweb.RRWebBreadcrumbEvent;
import io.sentry.rrweb.RRWebEvent;
import io.sentry.rrweb.RRWebEventType;
import io.sentry.rrweb.RRWebIncrementalSnapshotEvent;
import io.sentry.rrweb.RRWebInteractionEvent;
import io.sentry.rrweb.RRWebInteractionMoveEvent;
import io.sentry.rrweb.RRWebMetaEvent;
import io.sentry.rrweb.RRWebSpanEvent;
import io.sentry.rrweb.RRWebVideoEvent;
import io.sentry.util.MapObjectReader;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class ReplayRecording implements JsonUnknown, JsonSerializable {
   @Nullable
   private Integer segmentId;
   @Nullable
   private List<? extends RRWebEvent> payload;
   @Nullable
   private Map<String, Object> unknown;

   @Nullable
   public Integer getSegmentId() {
      return this.segmentId;
   }

   public void setSegmentId(@Nullable Integer segmentId) {
      this.segmentId = segmentId;
   }

   @Nullable
   public List<? extends RRWebEvent> getPayload() {
      return this.payload;
   }

   public void setPayload(@Nullable List<? extends RRWebEvent> payload) {
      this.payload = payload;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         ReplayRecording that = (ReplayRecording)o;
         return Objects.equals(this.segmentId, that.segmentId) && Objects.equals(this.payload, that.payload);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.segmentId, this.payload);
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.segmentId != null) {
         writer.name("segment_id").value(this.segmentId);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
      writer.setLenient(true);
      if (this.segmentId != null) {
         writer.jsonValue("\n");
      }

      if (this.payload != null) {
         writer.value(logger, this.payload);
      }

      writer.setLenient(false);
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

   public static final class Deserializer implements JsonDeserializer<ReplayRecording> {
      @NotNull
      public ReplayRecording deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         ReplayRecording replay = new ReplayRecording();
         Map<String, Object> unknown = null;
         Integer segmentId = null;
         List<RRWebEvent> payload = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            byte event = -1;
            switch (nextName.hashCode()) {
               case 1077649831:
                  if (nextName.equals("segment_id")) {
                     event = 0;
                  }
               default:
                  switch (event) {
                     case 0:
                        segmentId = reader.nextIntegerOrNull();
                        break;
                     default:
                        if (unknown == null) {
                           unknown = new HashMap<>();
                        }

                        reader.nextUnknown(logger, unknown, nextName);
                  }
            }
         }

         reader.endObject();
         reader.setLenient(true);
         List<Object> events = (List<Object>)reader.nextObjectOrNull();
         reader.setLenient(false);
         if (events != null) {
            payload = new ArrayList<>(events.size());

            for (Object event : events) {
               if (event instanceof Map) {
                  Map<String, Object> eventMap = (Map<String, Object>)event;
                  ObjectReader mapReader = new MapObjectReader(eventMap);

                  for (Entry<String, Object> entry : eventMap.entrySet()) {
                     String key = entry.getKey();
                     Object value = entry.getValue();
                     if (key.equals("type")) {
                        RRWebEventType type = RRWebEventType.values()[(Integer)value];
                        switch (type) {
                           case IncrementalSnapshot:
                              Map<String, Object> incrementalData = (Map<String, Object>)eventMap.get("data");
                              if (incrementalData == null) {
                                 incrementalData = Collections.emptyMap();
                              }

                              Integer sourceInt = (Integer)incrementalData.get("source");
                              if (sourceInt != null) {
                                 RRWebIncrementalSnapshotEvent.IncrementalSource source = RRWebIncrementalSnapshotEvent.IncrementalSource.values()[sourceInt];
                                 switch (source) {
                                    case MouseInteraction:
                                       RRWebInteractionEvent interactionEvent = new RRWebInteractionEvent.Deserializer().deserialize(mapReader, logger);
                                       payload.add(interactionEvent);
                                       continue;
                                    case TouchMove:
                                       RRWebInteractionMoveEvent interactionMoveEvent = new RRWebInteractionMoveEvent.Deserializer()
                                          .deserialize(mapReader, logger);
                                       payload.add(interactionMoveEvent);
                                       continue;
                                    default:
                                       logger.log(SentryLevel.DEBUG, "Unsupported rrweb incremental snapshot type %s", source);
                                 }
                              }
                              break;
                           case Meta:
                              RRWebEvent metaEvent = new RRWebMetaEvent.Deserializer().deserialize(mapReader, logger);
                              payload.add(metaEvent);
                              break;
                           case Custom:
                              Map<String, Object> customData = (Map<String, Object>)eventMap.get("data");
                              if (customData == null) {
                                 customData = Collections.emptyMap();
                              }

                              String tag = (String)customData.get("tag");
                              if (tag != null) {
                                 switch (tag) {
                                    case "video":
                                       RRWebEvent videoEvent = new RRWebVideoEvent.Deserializer().deserialize(mapReader, logger);
                                       payload.add(videoEvent);
                                       continue;
                                    case "breadcrumb":
                                       RRWebEvent breadcrumbEvent = new RRWebBreadcrumbEvent.Deserializer().deserialize(mapReader, logger);
                                       payload.add(breadcrumbEvent);
                                       continue;
                                    case "performanceSpan":
                                       RRWebEvent spanEvent = new RRWebSpanEvent.Deserializer().deserialize(mapReader, logger);
                                       payload.add(spanEvent);
                                       continue;
                                    default:
                                       logger.log(SentryLevel.DEBUG, "Unsupported rrweb event type %s", type);
                                 }
                              }
                              break;
                           default:
                              logger.log(SentryLevel.DEBUG, "Unsupported rrweb event type %s", type);
                        }
                     }
                  }
               }
            }
         }

         replay.setSegmentId(segmentId);
         replay.setPayload(payload);
         replay.setUnknown(unknown);
         return replay;
      }
   }

   public static final class JsonKeys {
      public static final String SEGMENT_ID = "segment_id";
   }
}
