package io.sentry.cache;

import io.sentry.IOptionsObserver;
import io.sentry.JsonDeserializer;
import io.sentry.SentryOptions;
import io.sentry.protocol.SdkVersion;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class PersistingOptionsObserver implements IOptionsObserver {
   public static final String OPTIONS_CACHE = ".options-cache";
   public static final String RELEASE_FILENAME = "release.json";
   public static final String PROGUARD_UUID_FILENAME = "proguard-uuid.json";
   public static final String SDK_VERSION_FILENAME = "sdk-version.json";
   public static final String ENVIRONMENT_FILENAME = "environment.json";
   public static final String DIST_FILENAME = "dist.json";
   public static final String TAGS_FILENAME = "tags.json";
   public static final String REPLAY_ERROR_SAMPLE_RATE_FILENAME = "replay-error-sample-rate.json";
   @NotNull
   private final SentryOptions options;

   public PersistingOptionsObserver(@NotNull SentryOptions options) {
      this.options = options;
   }

   @Override
   public void setRelease(@Nullable String release) {
      if (release == null) {
         this.delete("release.json");
      } else {
         this.store(release, "release.json");
      }
   }

   @Override
   public void setProguardUuid(@Nullable String proguardUuid) {
      if (proguardUuid == null) {
         this.delete("proguard-uuid.json");
      } else {
         this.store(proguardUuid, "proguard-uuid.json");
      }
   }

   @Override
   public void setSdkVersion(@Nullable SdkVersion sdkVersion) {
      if (sdkVersion == null) {
         this.delete("sdk-version.json");
      } else {
         this.store(sdkVersion, "sdk-version.json");
      }
   }

   @Override
   public void setDist(@Nullable String dist) {
      if (dist == null) {
         this.delete("dist.json");
      } else {
         this.store(dist, "dist.json");
      }
   }

   @Override
   public void setEnvironment(@Nullable String environment) {
      if (environment == null) {
         this.delete("environment.json");
      } else {
         this.store(environment, "environment.json");
      }
   }

   @Override
   public void setTags(@NotNull Map<String, String> tags) {
      this.store(tags, "tags.json");
   }

   @Override
   public void setReplayErrorSampleRate(@Nullable Double replayErrorSampleRate) {
      if (replayErrorSampleRate == null) {
         this.delete("replay-error-sample-rate.json");
      } else {
         this.store(replayErrorSampleRate.toString(), "replay-error-sample-rate.json");
      }
   }

   private <T> void store(@NotNull T entity, @NotNull String fileName) {
      CacheUtils.store(this.options, entity, ".options-cache", fileName);
   }

   private void delete(@NotNull String fileName) {
      CacheUtils.delete(this.options, ".options-cache", fileName);
   }

   @Nullable
   public static <T> T read(@NotNull SentryOptions options, @NotNull String fileName, @NotNull Class<T> clazz) {
      return read(options, fileName, clazz, null);
   }

   @Nullable
   public static <T, R> T read(
      @NotNull SentryOptions options, @NotNull String fileName, @NotNull Class<T> clazz, @Nullable JsonDeserializer<R> elementDeserializer
   ) {
      return CacheUtils.read(options, ".options-cache", fileName, clazz, elementDeserializer);
   }
}
