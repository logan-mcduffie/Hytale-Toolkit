package io.sentry.protocol;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.util.CollectionUtils;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class SentryStackTrace implements JsonUnknown, JsonSerializable {
   @Nullable
   private List<SentryStackFrame> frames;
   @Nullable
   private Map<String, String> registers;
   @Nullable
   private Boolean snapshot;
   @Nullable
   private Map<String, Object> unknown;

   public SentryStackTrace() {
   }

   public SentryStackTrace(@Nullable List<SentryStackFrame> frames) {
      this.frames = frames;
   }

   @Nullable
   public List<SentryStackFrame> getFrames() {
      return this.frames;
   }

   public void setFrames(@Nullable List<SentryStackFrame> frames) {
      this.frames = frames;
   }

   @Nullable
   public Map<String, String> getRegisters() {
      return this.registers;
   }

   public void setRegisters(@Nullable Map<String, String> registers) {
      this.registers = registers;
   }

   @Nullable
   public Boolean getSnapshot() {
      return this.snapshot;
   }

   public void setSnapshot(@Nullable Boolean snapshot) {
      this.snapshot = snapshot;
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
      if (this.frames != null) {
         writer.name("frames").value(logger, this.frames);
      }

      if (this.registers != null) {
         writer.name("registers").value(logger, this.registers);
      }

      if (this.snapshot != null) {
         writer.name("snapshot").value(this.snapshot);
      }

      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key);
            writer.value(logger, value);
         }
      }

      writer.endObject();
   }

   public static final class Deserializer implements JsonDeserializer<SentryStackTrace> {
      @NotNull
      public SentryStackTrace deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         SentryStackTrace sentryStackTrace = new SentryStackTrace();
         Map<String, Object> unknown = null;
         reader.beginObject();

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "frames":
                  sentryStackTrace.frames = reader.nextListOrNull(logger, new SentryStackFrame.Deserializer());
                  break;
               case "registers":
                  sentryStackTrace.registers = CollectionUtils.newConcurrentHashMap((Map<String, String>)reader.nextObjectOrNull());
                  break;
               case "snapshot":
                  sentryStackTrace.snapshot = reader.nextBooleanOrNull();
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         sentryStackTrace.setUnknown(unknown);
         reader.endObject();
         return sentryStackTrace;
      }
   }

   public static final class JsonKeys {
      public static final String FRAMES = "frames";
      public static final String REGISTERS = "registers";
      public static final String SNAPSHOT = "snapshot";
   }
}
