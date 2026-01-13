package io.sentry;

import io.sentry.protocol.SentryId;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryReplayEvent extends SentryBaseEvent implements JsonUnknown, JsonSerializable {
   public static final long REPLAY_VIDEO_MAX_SIZE = 10485760L;
   public static final String REPLAY_EVENT_TYPE = "replay_event";
   @Nullable
   private File videoFile;
   @NotNull
   private String type;
   @NotNull
   private SentryReplayEvent.ReplayType replayType;
   @Nullable
   private SentryId replayId = new SentryId();
   private int segmentId;
   @NotNull
   private Date timestamp;
   @Nullable
   private Date replayStartTimestamp;
   @Nullable
   private List<String> urls;
   @Nullable
   private List<String> errorIds;
   @Nullable
   private List<String> traceIds;
   @Nullable
   private Map<String, Object> unknown;

   public SentryReplayEvent() {
      this.type = "replay_event";
      this.replayType = SentryReplayEvent.ReplayType.SESSION;
      this.errorIds = new ArrayList<>();
      this.traceIds = new ArrayList<>();
      this.urls = new ArrayList<>();
      this.timestamp = DateUtils.getCurrentDateTime();
   }

   @Nullable
   public File getVideoFile() {
      return this.videoFile;
   }

   public void setVideoFile(@Nullable File videoFile) {
      this.videoFile = videoFile;
   }

   @NotNull
   public String getType() {
      return this.type;
   }

   public void setType(@NotNull String type) {
      this.type = type;
   }

   @Nullable
   public SentryId getReplayId() {
      return this.replayId;
   }

   public void setReplayId(@Nullable SentryId replayId) {
      this.replayId = replayId;
   }

   public int getSegmentId() {
      return this.segmentId;
   }

   public void setSegmentId(int segmentId) {
      this.segmentId = segmentId;
   }

   @NotNull
   public Date getTimestamp() {
      return this.timestamp;
   }

   public void setTimestamp(@NotNull Date timestamp) {
      this.timestamp = timestamp;
   }

   @Nullable
   public Date getReplayStartTimestamp() {
      return this.replayStartTimestamp;
   }

   public void setReplayStartTimestamp(@Nullable Date replayStartTimestamp) {
      this.replayStartTimestamp = replayStartTimestamp;
   }

   @Nullable
   public List<String> getUrls() {
      return this.urls;
   }

   public void setUrls(@Nullable List<String> urls) {
      this.urls = urls;
   }

   @Nullable
   public List<String> getErrorIds() {
      return this.errorIds;
   }

   public void setErrorIds(@Nullable List<String> errorIds) {
      this.errorIds = errorIds;
   }

   @Nullable
   public List<String> getTraceIds() {
      return this.traceIds;
   }

   public void setTraceIds(@Nullable List<String> traceIds) {
      this.traceIds = traceIds;
   }

   @NotNull
   public SentryReplayEvent.ReplayType getReplayType() {
      return this.replayType;
   }

   public void setReplayType(@NotNull SentryReplayEvent.ReplayType replayType) {
      this.replayType = replayType;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         SentryReplayEvent that = (SentryReplayEvent)o;
         return this.segmentId == that.segmentId
            && Objects.equals(this.type, that.type)
            && this.replayType == that.replayType
            && Objects.equals(this.replayId, that.replayId)
            && Objects.equals(this.urls, that.urls)
            && Objects.equals(this.errorIds, that.errorIds)
            && Objects.equals(this.traceIds, that.traceIds);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.type, this.replayType, this.replayId, this.segmentId, this.urls, this.errorIds, this.traceIds);
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("type").value(this.type);
      writer.name("replay_type").value(logger, this.replayType);
      writer.name("segment_id").value((long)this.segmentId);
      writer.name("timestamp").value(logger, this.timestamp);
      if (this.replayId != null) {
         writer.name("replay_id").value(logger, this.replayId);
      }

      if (this.replayStartTimestamp != null) {
         writer.name("replay_start_timestamp").value(logger, this.replayStartTimestamp);
      }

      if (this.urls != null) {
         writer.name("urls").value(logger, this.urls);
      }

      if (this.errorIds != null) {
         writer.name("error_ids").value(logger, this.errorIds);
      }

      if (this.traceIds != null) {
         writer.name("trace_ids").value(logger, this.traceIds);
      }

      new SentryBaseEvent.Serializer().serialize(this, writer, logger);
      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
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

   public static final class Deserializer implements JsonDeserializer<SentryReplayEvent> {
      @NotNull
      public SentryReplayEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         SentryBaseEvent.Deserializer baseEventDeserializer = new SentryBaseEvent.Deserializer();
         SentryReplayEvent replay = new SentryReplayEvent();
         Map<String, Object> unknown = null;
         String type = null;
         SentryReplayEvent.ReplayType replayType = null;
         SentryId replayId = null;
         Integer segmentId = null;
         Date timestamp = null;
         Date replayStartTimestamp = null;
         List<String> urls = null;
         List<String> errorIds = null;
         List<String> traceIds = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "type":
                  type = reader.nextStringOrNull();
                  break;
               case "replay_type":
                  replayType = reader.nextOrNull(logger, new SentryReplayEvent.ReplayType.Deserializer());
                  break;
               case "replay_id":
                  replayId = reader.nextOrNull(logger, new SentryId.Deserializer());
                  break;
               case "segment_id":
                  segmentId = reader.nextIntegerOrNull();
                  break;
               case "timestamp":
                  timestamp = reader.nextDateOrNull(logger);
                  break;
               case "replay_start_timestamp":
                  replayStartTimestamp = reader.nextDateOrNull(logger);
                  break;
               case "urls":
                  urls = (List<String>)reader.nextObjectOrNull();
                  break;
               case "error_ids":
                  errorIds = (List<String>)reader.nextObjectOrNull();
                  break;
               case "trace_ids":
                  traceIds = (List<String>)reader.nextObjectOrNull();
                  break;
               default:
                  if (!baseEventDeserializer.deserializeValue(replay, nextName, reader, logger)) {
                     if (unknown == null) {
                        unknown = new HashMap<>();
                     }

                     reader.nextUnknown(logger, unknown, nextName);
                  }
            }
         }

         reader.endObject();
         if (type != null) {
            replay.setType(type);
         }

         if (replayType != null) {
            replay.setReplayType(replayType);
         }

         if (segmentId != null) {
            replay.setSegmentId(segmentId);
         }

         if (timestamp != null) {
            replay.setTimestamp(timestamp);
         }

         replay.setReplayId(replayId);
         replay.setReplayStartTimestamp(replayStartTimestamp);
         replay.setUrls(urls);
         replay.setErrorIds(errorIds);
         replay.setTraceIds(traceIds);
         replay.setUnknown(unknown);
         return replay;
      }
   }

   public static final class JsonKeys {
      public static final String TYPE = "type";
      public static final String REPLAY_TYPE = "replay_type";
      public static final String REPLAY_ID = "replay_id";
      public static final String SEGMENT_ID = "segment_id";
      public static final String TIMESTAMP = "timestamp";
      public static final String REPLAY_START_TIMESTAMP = "replay_start_timestamp";
      public static final String URLS = "urls";
      public static final String ERROR_IDS = "error_ids";
      public static final String TRACE_IDS = "trace_ids";
   }

   public static enum ReplayType implements JsonSerializable {
      SESSION,
      BUFFER;

      @Override
      public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
         writer.value(this.name().toLowerCase(Locale.ROOT));
      }

      public static final class Deserializer implements JsonDeserializer<SentryReplayEvent.ReplayType> {
         @NotNull
         public SentryReplayEvent.ReplayType deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
            return SentryReplayEvent.ReplayType.valueOf(reader.nextString().toUpperCase(Locale.ROOT));
         }
      }
   }
}
