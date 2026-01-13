package io.sentry.protocol.profiling;

import io.sentry.ILogger;
import io.sentry.JsonDeserializer;
import io.sentry.JsonSerializable;
import io.sentry.JsonUnknown;
import io.sentry.ObjectReader;
import io.sentry.ObjectWriter;
import io.sentry.protocol.SentryStackFrame;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryProfile implements JsonUnknown, JsonSerializable {
   @NotNull
   private List<SentrySample> samples = new ArrayList<>();
   @NotNull
   private List<List<Integer>> stacks = new ArrayList<>();
   @NotNull
   private List<SentryStackFrame> frames = new ArrayList<>();
   @NotNull
   private Map<String, SentryThreadMetadata> threadMetadata = new HashMap<>();
   @Nullable
   private Map<String, Object> unknown;

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("samples").value(logger, this.samples);
      writer.name("stacks").value(logger, this.stacks);
      writer.name("frames").value(logger, this.frames);
      writer.name("thread_metadata").value(logger, this.threadMetadata);
      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key).value(logger, value);
         }
      }

      writer.endObject();
   }

   @NotNull
   public List<SentrySample> getSamples() {
      return this.samples;
   }

   public void setSamples(@NotNull List<SentrySample> samples) {
      this.samples = samples;
   }

   @NotNull
   public List<List<Integer>> getStacks() {
      return this.stacks;
   }

   public void setStacks(@NotNull List<List<Integer>> stacks) {
      this.stacks = stacks;
   }

   @NotNull
   public List<SentryStackFrame> getFrames() {
      return this.frames;
   }

   public void setFrames(@NotNull List<SentryStackFrame> frames) {
      this.frames = frames;
   }

   @NotNull
   public Map<String, SentryThreadMetadata> getThreadMetadata() {
      return this.threadMetadata;
   }

   public void setThreadMetadata(@NotNull Map<String, SentryThreadMetadata> threadMetadata) {
      this.threadMetadata = threadMetadata;
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

   public static final class Deserializer implements JsonDeserializer<SentryProfile> {
      @NotNull
      public SentryProfile deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         SentryProfile data = new SentryProfile();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "frames":
                  List<SentryStackFrame> jfrFrame = reader.nextListOrNull(logger, new SentryStackFrame.Deserializer());
                  if (jfrFrame != null) {
                     data.frames = jfrFrame;
                  }
                  break;
               case "samples":
                  List<SentrySample> sentrySamples = reader.nextListOrNull(logger, new SentrySample.Deserializer());
                  if (sentrySamples != null) {
                     data.samples = sentrySamples;
                  }
                  break;
               case "thread_metadata":
                  Map<String, SentryThreadMetadata> threadMetadata = reader.nextMapOrNull(logger, new SentryThreadMetadata.Deserializer());
                  if (threadMetadata != null) {
                     data.threadMetadata = threadMetadata;
                  }
                  break;
               case "stacks":
                  List<List<Integer>> jfrStacks = reader.nextOrNull(logger, new SentryProfile.NestedIntegerListDeserializer());
                  if (jfrStacks != null) {
                     data.stacks = jfrStacks;
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
      public static final String SAMPLES = "samples";
      public static final String STACKS = "stacks";
      public static final String FRAMES = "frames";
      public static final String THREAD_METADATA = "thread_metadata";
   }

   private static final class NestedIntegerListDeserializer implements JsonDeserializer<List<List<Integer>>> {
      private NestedIntegerListDeserializer() {
      }

      @NotNull
      public List<List<Integer>> deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         List<List<Integer>> result = new ArrayList<>();
         reader.beginArray();

         while (reader.hasNext()) {
            List<Integer> innerList = new ArrayList<>();
            reader.beginArray();

            while (reader.hasNext()) {
               innerList.add(reader.nextInt());
            }

            reader.endArray();
            result.add(innerList);
         }

         reader.endArray();
         return result;
      }
   }
}
