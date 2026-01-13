package io.sentry;

import io.sentry.profilemeasurements.ProfileMeasurement;
import io.sentry.protocol.DebugMeta;
import io.sentry.protocol.SdkVersion;
import io.sentry.protocol.SentryId;
import io.sentry.protocol.profiling.SentryProfile;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ProfileChunk implements JsonUnknown, JsonSerializable {
   public static final String PLATFORM_ANDROID = "android";
   public static final String PLATFORM_JAVA = "java";
   @Nullable
   private DebugMeta debugMeta;
   @NotNull
   private SentryId profilerId;
   @NotNull
   private SentryId chunkId;
   @Nullable
   private SdkVersion clientSdk;
   @NotNull
   private final Map<String, ProfileMeasurement> measurements;
   @NotNull
   private String platform;
   @NotNull
   private String release;
   @Nullable
   private String environment;
   @NotNull
   private String version;
   private double timestamp;
   @NotNull
   private final File traceFile;
   @Nullable
   private String sampledProfile = null;
   @Nullable
   private SentryProfile sentryProfile;
   @Nullable
   private Map<String, Object> unknown;

   public ProfileChunk() {
      this(SentryId.EMPTY_ID, SentryId.EMPTY_ID, new File("dummy"), new HashMap<>(), 0.0, "android", SentryOptions.empty());
   }

   public ProfileChunk(
      @NotNull SentryId profilerId,
      @NotNull SentryId chunkId,
      @NotNull File traceFile,
      @NotNull Map<String, ProfileMeasurement> measurements,
      @NotNull Double timestamp,
      @NotNull String platform,
      @NotNull SentryOptions options
   ) {
      this.profilerId = profilerId;
      this.chunkId = chunkId;
      this.traceFile = traceFile;
      this.measurements = measurements;
      this.debugMeta = null;
      this.clientSdk = options.getSdkVersion();
      this.release = options.getRelease() != null ? options.getRelease() : "";
      this.environment = options.getEnvironment();
      this.platform = platform;
      this.version = "2";
      this.timestamp = timestamp;
   }

   @NotNull
   public Map<String, ProfileMeasurement> getMeasurements() {
      return this.measurements;
   }

   @Nullable
   public DebugMeta getDebugMeta() {
      return this.debugMeta;
   }

   public void setDebugMeta(@Nullable DebugMeta debugMeta) {
      this.debugMeta = debugMeta;
   }

   @Nullable
   public SdkVersion getClientSdk() {
      return this.clientSdk;
   }

   @NotNull
   public SentryId getChunkId() {
      return this.chunkId;
   }

   @Nullable
   public String getEnvironment() {
      return this.environment;
   }

   @NotNull
   public String getPlatform() {
      return this.platform;
   }

   @NotNull
   public SentryId getProfilerId() {
      return this.profilerId;
   }

   @NotNull
   public String getRelease() {
      return this.release;
   }

   @Nullable
   public String getSampledProfile() {
      return this.sampledProfile;
   }

   public void setSampledProfile(@Nullable String sampledProfile) {
      this.sampledProfile = sampledProfile;
   }

   @NotNull
   public File getTraceFile() {
      return this.traceFile;
   }

   public double getTimestamp() {
      return this.timestamp;
   }

   @NotNull
   public String getVersion() {
      return this.version;
   }

   @Nullable
   public SentryProfile getSentryProfile() {
      return this.sentryProfile;
   }

   public void setSentryProfile(@Nullable SentryProfile sentryProfile) {
      this.sentryProfile = sentryProfile;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof ProfileChunk)) {
         return false;
      } else {
         ProfileChunk that = (ProfileChunk)o;
         return Objects.equals(this.debugMeta, that.debugMeta)
            && Objects.equals(this.profilerId, that.profilerId)
            && Objects.equals(this.chunkId, that.chunkId)
            && Objects.equals(this.clientSdk, that.clientSdk)
            && Objects.equals(this.measurements, that.measurements)
            && Objects.equals(this.platform, that.platform)
            && Objects.equals(this.release, that.release)
            && Objects.equals(this.environment, that.environment)
            && Objects.equals(this.version, that.version)
            && Objects.equals(this.sampledProfile, that.sampledProfile)
            && Objects.equals(this.unknown, that.unknown)
            && Objects.equals(this.sentryProfile, that.sentryProfile);
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(
         this.debugMeta,
         this.profilerId,
         this.chunkId,
         this.clientSdk,
         this.measurements,
         this.platform,
         this.release,
         this.environment,
         this.version,
         this.sampledProfile,
         this.sentryProfile,
         this.unknown
      );
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      if (this.debugMeta != null) {
         writer.name("debug_meta").value(logger, this.debugMeta);
      }

      writer.name("profiler_id").value(logger, this.profilerId);
      writer.name("chunk_id").value(logger, this.chunkId);
      if (this.clientSdk != null) {
         writer.name("client_sdk").value(logger, this.clientSdk);
      }

      if (!this.measurements.isEmpty()) {
         String prevIndent = writer.getIndent();
         writer.setIndent("");
         writer.name("measurements").value(logger, this.measurements);
         writer.setIndent(prevIndent);
      }

      writer.name("platform").value(logger, this.platform);
      writer.name("release").value(logger, this.release);
      if (this.environment != null) {
         writer.name("environment").value(logger, this.environment);
      }

      writer.name("version").value(logger, this.version);
      if (this.sampledProfile != null) {
         writer.name("sampled_profile").value(logger, this.sampledProfile);
      }

      writer.name("timestamp").value(logger, this.doubleToBigDecimal(this.timestamp));
      if (this.sentryProfile != null) {
         writer.name("profile").value(logger, this.sentryProfile);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   @NotNull
   private BigDecimal doubleToBigDecimal(@NotNull Double value) {
      return BigDecimal.valueOf(value).setScale(6, RoundingMode.DOWN);
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

   public static final class Builder {
      @NotNull
      private final SentryId profilerId;
      @NotNull
      private final SentryId chunkId;
      @NotNull
      private final Map<String, ProfileMeasurement> measurements;
      @NotNull
      private final File traceFile;
      private final double timestamp;
      @NotNull
      private final String platform;

      public Builder(
         @NotNull SentryId profilerId,
         @NotNull SentryId chunkId,
         @NotNull Map<String, ProfileMeasurement> measurements,
         @NotNull File traceFile,
         @NotNull SentryDate timestamp,
         @NotNull String platform
      ) {
         this.profilerId = profilerId;
         this.chunkId = chunkId;
         this.measurements = new ConcurrentHashMap<>(measurements);
         this.traceFile = traceFile;
         this.timestamp = DateUtils.nanosToSeconds(timestamp.nanoTimestamp());
         this.platform = platform;
      }

      public ProfileChunk build(SentryOptions options) {
         return new ProfileChunk(this.profilerId, this.chunkId, this.traceFile, this.measurements, this.timestamp, this.platform, options);
      }
   }

   public static final class Deserializer implements JsonDeserializer<ProfileChunk> {
      @NotNull
      public ProfileChunk deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         ProfileChunk data = new ProfileChunk();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "debug_meta":
                  DebugMeta debugMeta = reader.nextOrNull(logger, new DebugMeta.Deserializer());
                  if (debugMeta != null) {
                     data.debugMeta = debugMeta;
                  }
                  break;
               case "profiler_id":
                  SentryId profilerId = reader.nextOrNull(logger, new SentryId.Deserializer());
                  if (profilerId != null) {
                     data.profilerId = profilerId;
                  }
                  break;
               case "chunk_id":
                  SentryId chunkId = reader.nextOrNull(logger, new SentryId.Deserializer());
                  if (chunkId != null) {
                     data.chunkId = chunkId;
                  }
                  break;
               case "client_sdk":
                  SdkVersion clientSdk = reader.nextOrNull(logger, new SdkVersion.Deserializer());
                  if (clientSdk != null) {
                     data.clientSdk = clientSdk;
                  }
                  break;
               case "measurements":
                  Map<String, ProfileMeasurement> measurements = reader.nextMapOrNull(logger, new ProfileMeasurement.Deserializer());
                  if (measurements != null) {
                     data.measurements.putAll(measurements);
                  }
                  break;
               case "platform":
                  String platform = reader.nextStringOrNull();
                  if (platform != null) {
                     data.platform = platform;
                  }
                  break;
               case "release":
                  String release = reader.nextStringOrNull();
                  if (release != null) {
                     data.release = release;
                  }
                  break;
               case "environment":
                  String environment = reader.nextStringOrNull();
                  if (environment != null) {
                     data.environment = environment;
                  }
                  break;
               case "version":
                  String version = reader.nextStringOrNull();
                  if (version != null) {
                     data.version = version;
                  }
                  break;
               case "sampled_profile":
                  String sampledProfile = reader.nextStringOrNull();
                  if (sampledProfile != null) {
                     data.sampledProfile = sampledProfile;
                  }
                  break;
               case "timestamp":
                  Double timestamp = reader.nextDoubleOrNull();
                  if (timestamp != null) {
                     data.timestamp = timestamp;
                  }
                  break;
               case "profile":
                  SentryProfile sentryProfile = reader.nextOrNull(logger, new SentryProfile.Deserializer());
                  if (sentryProfile != null) {
                     data.sentryProfile = sentryProfile;
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
      public static final String DEBUG_META = "debug_meta";
      public static final String PROFILER_ID = "profiler_id";
      public static final String CHUNK_ID = "chunk_id";
      public static final String CLIENT_SDK = "client_sdk";
      public static final String MEASUREMENTS = "measurements";
      public static final String PLATFORM = "platform";
      public static final String RELEASE = "release";
      public static final String ENVIRONMENT = "environment";
      public static final String VERSION = "version";
      public static final String SAMPLED_PROFILE = "sampled_profile";
      public static final String TIMESTAMP = "timestamp";
      public static final String SENTRY_PROFILE = "profile";
   }
}
