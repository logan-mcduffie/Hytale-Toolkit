package io.sentry.util;

import io.sentry.TracesSamplingDecision;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class SampleRateUtils {
   public static boolean isValidSampleRate(@Nullable Double sampleRate) {
      return isValidRate(sampleRate, true);
   }

   public static boolean isValidTracesSampleRate(@Nullable Double tracesSampleRate) {
      return isValidTracesSampleRate(tracesSampleRate, true);
   }

   public static boolean isValidTracesSampleRate(@Nullable Double tracesSampleRate, boolean allowNull) {
      return isValidRate(tracesSampleRate, allowNull);
   }

   public static boolean isValidProfilesSampleRate(@Nullable Double profilesSampleRate) {
      return isValidRate(profilesSampleRate, true);
   }

   public static boolean isValidContinuousProfilesSampleRate(@Nullable Double profilesSampleRate) {
      return isValidRate(profilesSampleRate, true);
   }

   @NotNull
   public static Double backfilledSampleRand(@Nullable Double sampleRand, @Nullable Double sampleRate, @Nullable Boolean sampled) {
      if (sampleRand != null) {
         return sampleRand;
      } else {
         double newSampleRand = SentryRandom.current().nextDouble();
         if (sampleRate == null || sampled == null) {
            return newSampleRand;
         } else {
            return sampled ? newSampleRand * sampleRate : sampleRate + newSampleRand * (1.0 - sampleRate);
         }
      }
   }

   @NotNull
   public static TracesSamplingDecision backfilledSampleRand(@NotNull TracesSamplingDecision samplingDecision) {
      if (samplingDecision.getSampleRand() != null) {
         return samplingDecision;
      } else {
         Double sampleRand = backfilledSampleRand(null, samplingDecision.getSampleRate(), samplingDecision.getSampled());
         return new TracesSamplingDecision(
            samplingDecision.getSampled(),
            samplingDecision.getSampleRate(),
            sampleRand,
            samplingDecision.getProfileSampled(),
            samplingDecision.getProfileSampleRate()
         );
      }
   }

   private static boolean isValidRate(@Nullable Double rate, boolean allowNull) {
      return rate == null ? allowNull : !rate.isNaN() && rate >= 0.0 && rate <= 1.0;
   }
}
