package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryOptions;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public final class DebugMeta implements JsonUnknown, JsonSerializable {
   @Nullable
   private SdkInfo sdkInfo;
   @Nullable
   private List<DebugImage> images;
   @Nullable
   private Map<String, Object> unknown;

   @Nullable
   public List<DebugImage> getImages() {
      return this.images;
   }

   public void setImages(@Nullable List<DebugImage> images) {
      this.images = images != null ? new ArrayList<>(images) : null;
   }

   @Nullable
   public SdkInfo getSdkInfo() {
      return this.sdkInfo;
   }

   public void setSdkInfo(@Nullable SdkInfo sdkInfo) {
      this.sdkInfo = sdkInfo;
   }

   @Internal
   @Nullable
   public static DebugMeta buildDebugMeta(@Nullable DebugMeta eventDebugMeta, @NotNull SentryOptions options) {
      List<DebugImage> debugImages = new ArrayList<>();
      if (options.getProguardUuid() != null) {
         DebugImage proguardMappingImage = new DebugImage();
         proguardMappingImage.setType("proguard");
         proguardMappingImage.setUuid(options.getProguardUuid());
         debugImages.add(proguardMappingImage);
      }

      for (String bundleId : options.getBundleIds()) {
         DebugImage sourceBundleImage = new DebugImage();
         sourceBundleImage.setType("jvm");
         sourceBundleImage.setDebugId(bundleId);
         debugImages.add(sourceBundleImage);
      }

      if (!debugImages.isEmpty()) {
         DebugMeta debugMeta = eventDebugMeta;
         if (eventDebugMeta == null) {
            debugMeta = new DebugMeta();
         }

         if (debugMeta.getImages() == null) {
            debugMeta.setImages(debugImages);
         } else {
            debugMeta.getImages().addAll(debugImages);
         }

         return debugMeta;
      } else {
         return null;
      }
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
      if (this.sdkInfo != null) {
         writer.name("sdk_info").value(logger, this.sdkInfo);
      }

      if (this.images != null) {
         writer.name("images").value(logger, this.images);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<DebugMeta> {
      @NotNull
      public DebugMeta deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         DebugMeta debugMeta = new DebugMeta();
         Map<String, Object> unknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "sdk_info":
                  debugMeta.sdkInfo = reader.nextOrNull(logger, new SdkInfo.Deserializer());
                  break;
               case "images":
                  debugMeta.images = reader.nextListOrNull(logger, new DebugImage.Deserializer());
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         debugMeta.setUnknown(unknown);
         return debugMeta;
      }
   }

   public static final class JsonKeys {
      public static final String SDK_INFO = "sdk_info";
      public static final String IMAGES = "images";
   }
}
