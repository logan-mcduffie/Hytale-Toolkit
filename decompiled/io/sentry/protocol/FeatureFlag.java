package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.SentryLevel;
import io.sentry.util.Objects;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class FeatureFlag implements JsonUnknown, JsonSerializable {
   @NotNull
   public static final String DATA_PREFIX = "flag.evaluation.";
   @NotNull
   private String flag;
   private boolean result;
   @Nullable
   private Map<String, @NotNull Object> unknown;

   public FeatureFlag(@NotNull String flag, boolean result) {
      this.flag = flag;
      this.result = result;
   }

   @NotNull
   public String getFlag() {
      return this.flag;
   }

   public void setFlag(@NotNull String flag) {
      this.flag = flag;
   }

   @NotNull
   public Boolean getResult() {
      return this.result;
   }

   public void setResult(@NotNull Boolean result) {
      this.result = result;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (o != null && this.getClass() == o.getClass()) {
         FeatureFlag otherFlag = (FeatureFlag)o;
         return Objects.equals(this.flag, otherFlag.flag) && Objects.equals(this.result, otherFlag.result);
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.flag, this.result);
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
      writer.name("flag").value(this.flag);
      writer.name("result").value(this.result);
      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<FeatureFlag> {
      @NotNull
      public FeatureFlag deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         String flag = null;
         Boolean result = null;
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "flag":
                  flag = reader.nextStringOrNull();
                  break;
               case "result":
                  result = reader.nextBooleanOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         if (flag == null) {
            String message = "Missing required field \"flag\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else if (result == null) {
            String message = "Missing required field \"result\"";
            Exception exception = new IllegalStateException(message);
            logger.log(SentryLevel.ERROR, message, exception);
            throw exception;
         } else {
            FeatureFlag app = new FeatureFlag(flag, result);
            app.setUnknown(unknown);
            reader.endObject();
            return app;
         }
      }
   }

   public static final class JsonKeys {
      public static final String FLAG = "flag";
      public static final String RESULT = "result";
   }
}
