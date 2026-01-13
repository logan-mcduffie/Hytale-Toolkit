package io.sentry;

import io.sentry.util.SentryRandom;
import io.sentry.vendor.gson.stream.JsonToken;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.VisibleForTesting;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SentryAppStartProfilingOptions implements JsonUnknown, JsonSerializable {
   boolean profileSampled;
   @Nullable
   Double profileSampleRate;
   boolean traceSampled;
   @Nullable
   Double traceSampleRate;
   @Nullable
   String profilingTracesDirPath;
   boolean isProfilingEnabled;
   boolean isContinuousProfilingEnabled;
   int profilingTracesHz;
   boolean continuousProfileSampled;
   boolean isEnableAppStartProfiling;
   boolean isStartProfilerOnAppStart;
   @NotNull
   ProfileLifecycle profileLifecycle;
   @Nullable
   private Map<String, Object> unknown;

   @VisibleForTesting
   public SentryAppStartProfilingOptions() {
      this.traceSampled = false;
      this.traceSampleRate = null;
      this.profileSampled = false;
      this.profileSampleRate = null;
      this.continuousProfileSampled = false;
      this.profilingTracesDirPath = null;
      this.isProfilingEnabled = false;
      this.isContinuousProfilingEnabled = false;
      this.profileLifecycle = ProfileLifecycle.MANUAL;
      this.profilingTracesHz = 0;
      this.isEnableAppStartProfiling = true;
      this.isStartProfilerOnAppStart = false;
   }

   SentryAppStartProfilingOptions(@NotNull SentryOptions options, @NotNull TracesSamplingDecision samplingDecision) {
      this.traceSampled = samplingDecision.getSampled();
      this.traceSampleRate = samplingDecision.getSampleRate();
      this.profileSampled = samplingDecision.getProfileSampled();
      this.profileSampleRate = samplingDecision.getProfileSampleRate();
      this.continuousProfileSampled = options.getInternalTracesSampler().sampleSessionProfile(SentryRandom.current().nextDouble());
      this.profilingTracesDirPath = options.getProfilingTracesDirPath();
      this.isProfilingEnabled = options.isProfilingEnabled();
      this.isContinuousProfilingEnabled = options.isContinuousProfilingEnabled();
      this.profileLifecycle = options.getProfileLifecycle();
      this.profilingTracesHz = options.getProfilingTracesHz();
      this.isEnableAppStartProfiling = options.isEnableAppStartProfiling();
      this.isStartProfilerOnAppStart = options.isStartProfilerOnAppStart();
   }

   public void setProfileSampled(boolean profileSampled) {
      this.profileSampled = profileSampled;
   }

   public boolean isProfileSampled() {
      return this.profileSampled;
   }

   public void setContinuousProfileSampled(boolean continuousProfileSampled) {
      this.continuousProfileSampled = continuousProfileSampled;
   }

   public boolean isContinuousProfileSampled() {
      return this.continuousProfileSampled;
   }

   public void setProfileLifecycle(@NotNull ProfileLifecycle profileLifecycle) {
      this.profileLifecycle = profileLifecycle;
   }

   @NotNull
   public ProfileLifecycle getProfileLifecycle() {
      return this.profileLifecycle;
   }

   public void setProfileSampleRate(@Nullable Double profileSampleRate) {
      this.profileSampleRate = profileSampleRate;
   }

   @Nullable
   public Double getProfileSampleRate() {
      return this.profileSampleRate;
   }

   public void setTraceSampled(boolean traceSampled) {
      this.traceSampled = traceSampled;
   }

   public boolean isTraceSampled() {
      return this.traceSampled;
   }

   public void setTraceSampleRate(@Nullable Double traceSampleRate) {
      this.traceSampleRate = traceSampleRate;
   }

   @Nullable
   public Double getTraceSampleRate() {
      return this.traceSampleRate;
   }

   public void setProfilingTracesDirPath(@Nullable String profilingTracesDirPath) {
      this.profilingTracesDirPath = profilingTracesDirPath;
   }

   @Nullable
   public String getProfilingTracesDirPath() {
      return this.profilingTracesDirPath;
   }

   public void setProfilingEnabled(boolean profilingEnabled) {
      this.isProfilingEnabled = profilingEnabled;
   }

   public boolean isProfilingEnabled() {
      return this.isProfilingEnabled;
   }

   public void setContinuousProfilingEnabled(boolean continuousProfilingEnabled) {
      this.isContinuousProfilingEnabled = continuousProfilingEnabled;
   }

   public boolean isContinuousProfilingEnabled() {
      return this.isContinuousProfilingEnabled;
   }

   public void setProfilingTracesHz(int profilingTracesHz) {
      this.profilingTracesHz = profilingTracesHz;
   }

   public int getProfilingTracesHz() {
      return this.profilingTracesHz;
   }

   public void setEnableAppStartProfiling(boolean enableAppStartProfiling) {
      this.isEnableAppStartProfiling = enableAppStartProfiling;
   }

   public boolean isEnableAppStartProfiling() {
      return this.isEnableAppStartProfiling;
   }

   public void setStartProfilerOnAppStart(boolean startProfilerOnAppStart) {
      this.isStartProfilerOnAppStart = startProfilerOnAppStart;
   }

   public boolean isStartProfilerOnAppStart() {
      return this.isStartProfilerOnAppStart;
   }

   @Override
   public void serialize(@NotNull ObjectWriter writer, @NotNull ILogger logger) throws IOException {
      writer.beginObject();
      writer.name("profile_sampled").value(logger, this.profileSampled);
      writer.name("profile_sample_rate").value(logger, this.profileSampleRate);
      writer.name("continuous_profile_sampled").value(logger, this.continuousProfileSampled);
      writer.name("trace_sampled").value(logger, this.traceSampled);
      writer.name("trace_sample_rate").value(logger, this.traceSampleRate);
      writer.name("profiling_traces_dir_path").value(logger, this.profilingTracesDirPath);
      writer.name("is_profiling_enabled").value(logger, this.isProfilingEnabled);
      writer.name("is_continuous_profiling_enabled").value(logger, this.isContinuousProfilingEnabled);
      writer.name("profile_lifecycle").value(logger, this.profileLifecycle.name());
      writer.name("profiling_traces_hz").value(logger, this.profilingTracesHz);
      writer.name("is_enable_app_start_profiling").value(logger, this.isEnableAppStartProfiling);
      writer.name("is_start_profiler_on_app_start").value(logger, this.isStartProfilerOnAppStart);
      if (this.unknown != null) {
         for (String key : this.unknown.keySet()) {
            Object value = this.unknown.get(key);
            writer.name(key);
            writer.value(logger, value);
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

   public static final class Deserializer implements JsonDeserializer<SentryAppStartProfilingOptions> {
      @NotNull
      public SentryAppStartProfilingOptions deserialize(@NotNull ObjectReader reader, @NotNull ILogger logger) throws Exception {
         reader.beginObject();
         SentryAppStartProfilingOptions options = new SentryAppStartProfilingOptions();
         Map<String, Object> unknown = null;

         while (reader.peek() == JsonToken.NAME) {
            String nextName = reader.nextName();
            switch (nextName) {
               case "profile_sampled":
                  Boolean profileSampled = reader.nextBooleanOrNull();
                  if (profileSampled != null) {
                     options.profileSampled = profileSampled;
                  }
                  break;
               case "profile_sample_rate":
                  Double profileSampleRate = reader.nextDoubleOrNull();
                  if (profileSampleRate != null) {
                     options.profileSampleRate = profileSampleRate;
                  }
                  break;
               case "continuous_profile_sampled":
                  Boolean continuousProfileSampled = reader.nextBooleanOrNull();
                  if (continuousProfileSampled != null) {
                     options.continuousProfileSampled = continuousProfileSampled;
                  }
                  break;
               case "trace_sampled":
                  Boolean traceSampled = reader.nextBooleanOrNull();
                  if (traceSampled != null) {
                     options.traceSampled = traceSampled;
                  }
                  break;
               case "trace_sample_rate":
                  Double traceSampleRate = reader.nextDoubleOrNull();
                  if (traceSampleRate != null) {
                     options.traceSampleRate = traceSampleRate;
                  }
                  break;
               case "profiling_traces_dir_path":
                  String profilingTracesDirPath = reader.nextStringOrNull();
                  if (profilingTracesDirPath != null) {
                     options.profilingTracesDirPath = profilingTracesDirPath;
                  }
                  break;
               case "is_profiling_enabled":
                  Boolean isProfilingEnabled = reader.nextBooleanOrNull();
                  if (isProfilingEnabled != null) {
                     options.isProfilingEnabled = isProfilingEnabled;
                  }
                  break;
               case "is_continuous_profiling_enabled":
                  Boolean isContinuousProfilingEnabled = reader.nextBooleanOrNull();
                  if (isContinuousProfilingEnabled != null) {
                     options.isContinuousProfilingEnabled = isContinuousProfilingEnabled;
                  }
                  break;
               case "profile_lifecycle":
                  String profileLifecycle = reader.nextStringOrNull();
                  if (profileLifecycle != null) {
                     try {
                        options.profileLifecycle = ProfileLifecycle.valueOf(profileLifecycle);
                     } catch (IllegalArgumentException var20) {
                        logger.log(SentryLevel.ERROR, "Error when deserializing ProfileLifecycle: " + profileLifecycle);
                     }
                  }
                  break;
               case "profiling_traces_hz":
                  Integer profilingTracesHz = reader.nextIntegerOrNull();
                  if (profilingTracesHz != null) {
                     options.profilingTracesHz = profilingTracesHz;
                  }
                  break;
               case "is_enable_app_start_profiling":
                  Boolean isEnableAppStartProfiling = reader.nextBooleanOrNull();
                  if (isEnableAppStartProfiling != null) {
                     options.isEnableAppStartProfiling = isEnableAppStartProfiling;
                  }
                  break;
               case "is_start_profiler_on_app_start":
                  Boolean isStartProfilerOnAppStart = reader.nextBooleanOrNull();
                  if (isStartProfilerOnAppStart != null) {
                     options.isStartProfilerOnAppStart = isStartProfilerOnAppStart;
                  }
                  break;
               default:
                  if (unknown == null) {
                     unknown = new ConcurrentHashMap<>();
                  }

                  reader.nextUnknown(logger, unknown, nextName);
            }
         }

         options.setUnknown(unknown);
         reader.endObject();
         return options;
      }
   }

   public static final class JsonKeys {
      public static final String PROFILE_SAMPLED = "profile_sampled";
      public static final String PROFILE_SAMPLE_RATE = "profile_sample_rate";
      public static final String CONTINUOUS_PROFILE_SAMPLED = "continuous_profile_sampled";
      public static final String TRACE_SAMPLED = "trace_sampled";
      public static final String TRACE_SAMPLE_RATE = "trace_sample_rate";
      public static final String PROFILING_TRACES_DIR_PATH = "profiling_traces_dir_path";
      public static final String IS_PROFILING_ENABLED = "is_profiling_enabled";
      public static final String IS_CONTINUOUS_PROFILING_ENABLED = "is_continuous_profiling_enabled";
      public static final String PROFILE_LIFECYCLE = "profile_lifecycle";
      public static final String PROFILING_TRACES_HZ = "profiling_traces_hz";
      public static final String IS_ENABLE_APP_START_PROFILING = "is_enable_app_start_profiling";
      public static final String IS_START_PROFILER_ON_APP_START = "is_start_profiler_on_app_start";
   }
}
