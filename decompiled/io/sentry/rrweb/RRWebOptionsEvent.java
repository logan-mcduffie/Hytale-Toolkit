package io.sentry.rrweb;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.ScreenshotStrategyType;
import io.sentry.SentryOptions;
import io.sentry.SentryReplayOptions;
import io.sentry.protocol.SdkVersion;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class RRWebOptionsEvent extends RRWebEvent implements JsonSerializable, JsonUnknown {
   public static final String EVENT_TAG = "options";
   @NotNull
   private String tag;
   @NotNull
   private Map<String, Object> optionsPayload = new HashMap<>();
   @Nullable
   private Map<String, Object> unknown;
   @Nullable
   private Map<String, Object> dataUnknown;

   public RRWebOptionsEvent() {
      super(RRWebEventType.Custom);
      this.tag = "options";
   }

   public RRWebOptionsEvent(@NotNull SentryOptions options) {
      this();
      SdkVersion sdkVersion = options.getSdkVersion();
      if (sdkVersion != null) {
         this.optionsPayload.put("nativeSdkName", sdkVersion.getName());
         this.optionsPayload.put("nativeSdkVersion", sdkVersion.getVersion());
      }

      SentryReplayOptions replayOptions = options.getSessionReplay();
      this.optionsPayload.put("errorSampleRate", replayOptions.getOnErrorSampleRate());
      this.optionsPayload.put("sessionSampleRate", replayOptions.getSessionSampleRate());
      this.optionsPayload.put("maskAllImages", replayOptions.getMaskViewClasses().contains("android.widget.ImageView"));
      this.optionsPayload.put("maskAllText", replayOptions.getMaskViewClasses().contains("android.widget.TextView"));
      this.optionsPayload.put("quality", replayOptions.getQuality().serializedName());
      this.optionsPayload.put("maskedViewClasses", replayOptions.getMaskViewClasses());
      this.optionsPayload.put("unmaskedViewClasses", replayOptions.getUnmaskViewClasses());
      String screenshotStrategy = replayOptions.getScreenshotStrategy() == ScreenshotStrategyType.PIXEL_COPY ? "pixelCopy" : "canvas";
      this.optionsPayload.put("screenshotStrategy", screenshotStrategy);
      this.optionsPayload.put("networkDetailHasUrls", !replayOptions.getNetworkDetailAllowUrls().isEmpty());
      if (!replayOptions.getNetworkDetailAllowUrls().isEmpty()) {
         this.optionsPayload.put("networkDetailAllowUrls", replayOptions.getNetworkDetailAllowUrls());
         this.optionsPayload.put("networkRequestHeaders", replayOptions.getNetworkRequestHeaders());
         this.optionsPayload.put("networkResponseHeaders", replayOptions.getNetworkResponseHeaders());
         this.optionsPayload.put("networkCaptureBodies", replayOptions.isNetworkCaptureBodies());
         if (!replayOptions.getNetworkDetailDenyUrls().isEmpty()) {
            this.optionsPayload.put("networkDetailDenyUrls", replayOptions.getNetworkDetailDenyUrls());
         }
      }
   }

   @NotNull
   public String getTag() {
      return this.tag;
   }

   public void setTag(@NotNull String tag) {
      this.tag = tag;
   }

   @NotNull
   public Map<String, Object> getOptionsPayload() {
      return this.optionsPayload;
   }

   public void setOptionsPayload(@NotNull Map<String, Object> optionsPayload) {
      this.optionsPayload = optionsPayload;
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
      if (this.optionsPayload != null) {
         for (String key : this.optionsPayload.keySet()) {
            Object value = this.optionsPayload.get(key);
            writer.name(key);
            writer.value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<RRWebOptionsEvent> {
      @NotNull
      public RRWebOptionsEvent deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         Map<String, Object> unknown = null;
         RRWebOptionsEvent event = new RRWebOptionsEvent();
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

      private void deserializeData(@NotNull RRWebOptionsEvent event, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
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

      private void deserializePayload(@NotNull RRWebOptionsEvent event, @NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         Map<String, Object> optionsPayload = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            if (optionsPayload == null) {
               optionsPayload = new HashMap<>();
            }

            reader.nextUnknown(logger, optionsPayload, nextName);
         }

         if (optionsPayload != null) {
            event.setOptionsPayload(optionsPayload);
         }

         reader.endObject();
      }
   }

   public static final class JsonKeys {
      public static final String DATA = "data";
      public static final String PAYLOAD = "payload";
   }
}
