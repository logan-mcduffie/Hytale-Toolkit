package io.sentry.protocol;

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

public final class SentryException implements JsonUnknown, JsonSerializable {
   @Nullable
   private String type;
   @Nullable
   private String value;
   @Nullable
   private String module;
   @Nullable
   private Long threadId;
   @Nullable
   private SentryStackTrace stacktrace;
   @Nullable
   private Mechanism mechanism;
   @Nullable
   private Map<String, Object> unknown;

   @Nullable
   public String getType() {
      return this.type;
   }

   public void setType(@Nullable String type) {
      this.type = type;
   }

   @Nullable
   public String getValue() {
      return this.value;
   }

   public void setValue(@Nullable String value) {
      this.value = value;
   }

   @Nullable
   public String getModule() {
      return this.module;
   }

   public void setModule(@Nullable String module) {
      this.module = module;
   }

   @Nullable
   public Long getThreadId() {
      return this.threadId;
   }

   public void setThreadId(@Nullable Long threadId) {
      this.threadId = threadId;
   }

   @Nullable
   public SentryStackTrace getStacktrace() {
      return this.stacktrace;
   }

   public void setStacktrace(@Nullable SentryStackTrace stacktrace) {
      this.stacktrace = stacktrace;
   }

   @Nullable
   public Mechanism getMechanism() {
      return this.mechanism;
   }

   public void setMechanism(@Nullable Mechanism mechanism) {
      this.mechanism = mechanism;
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
      if (this.type != null) {
         writer.name("type").value(this.type);
      }

      if (this.value != null) {
         writer.name("value").value(this.value);
      }

      if (this.module != null) {
         writer.name("module").value(this.module);
      }

      if (this.threadId != null) {
         writer.name("thread_id").value(this.threadId);
      }

      if (this.stacktrace != null) {
         writer.name("stacktrace").value(logger, this.stacktrace);
      }

      if (this.mechanism != null) {
         writer.name("mechanism").value(logger, this.mechanism);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<SentryException> {
      @NotNull
      public SentryException deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         SentryException sentryException = new SentryException();
         Map<String, Object> unknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "type":
                  sentryException.type = reader.nextStringOrNull();
                  break;
               case "value":
                  sentryException.value = reader.nextStringOrNull();
                  break;
               case "module":
                  sentryException.module = reader.nextStringOrNull();
                  break;
               case "thread_id":
                  sentryException.threadId = reader.nextLongOrNull();
                  break;
               case "stacktrace":
                  sentryException.stacktrace = reader.nextOrNull(logger, new SentryStackTrace.Deserializer());
                  break;
               case "mechanism":
                  sentryException.mechanism = reader.nextOrNull(logger, new Mechanism.Deserializer());
                  break;
               default:
                  if (unknown == null) {
                     unknown = new HashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         reader.endObject();
         sentryException.setUnknown(unknown);
         return sentryException;
      }
   }

   public static final class JsonKeys {
      public static final String TYPE = "type";
      public static final String VALUE = "value";
      public static final String MODULE = "module";
      public static final String THREAD_ID = "thread_id";
      public static final String STACKTRACE = "stacktrace";
      public static final String MECHANISM = "mechanism";
   }
}
