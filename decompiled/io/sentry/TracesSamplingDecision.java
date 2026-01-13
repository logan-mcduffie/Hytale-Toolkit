package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class TracesSamplingDecision {
   @NotNull
   private final Boolean sampled;
   @Nullable
   private final Double sampleRate;
   @Nullable
   private final Double sampleRand;
   @NotNull
   private final Boolean profileSampled;
   @Nullable
   private final Double profileSampleRate;

   public TracesSamplingDecision(@NotNull Boolean sampled) {
      this(sampled, null);
   }

   public TracesSamplingDecision(@NotNull Boolean sampled, @Nullable Double sampleRate) {
      this(sampled, sampleRate, null, false, null);
   }

   public TracesSamplingDecision(@NotNull Boolean sampled, @Nullable Double sampleRate, @Nullable Double sampleRand) {
      this(sampled, sampleRate, sampleRand, false, null);
   }

   public TracesSamplingDecision(@NotNull Boolean sampled, @Nullable Double sampleRate, @NotNull Boolean profileSampled, @Nullable Double profileSampleRate) {
      this(sampled, sampleRate, null, profileSampled, profileSampleRate);
   }

   public TracesSamplingDecision(
      @NotNull Boolean sampled, @Nullable Double sampleRate, @Nullable Double sampleRand, @NotNull Boolean profileSampled, @Nullable Double profileSampleRate
   ) {
      this.sampled = sampled;
      this.sampleRate = sampleRate;
      this.sampleRand = sampleRand;
      this.profileSampled = sampled && profileSampled;
      this.profileSampleRate = profileSampleRate;
   }

   @NotNull
   public Boolean getSampled() {
      return this.sampled;
   }

   @Nullable
   public Double getSampleRate() {
      return this.sampleRate;
   }

   @Nullable
   public Double getSampleRand() {
      return this.sampleRand;
   }

   @NotNull
   public Boolean getProfileSampled() {
      return this.profileSampled;
   }

   @Nullable
   public Double getProfileSampleRate() {
      return this.profileSampleRate;
   }
}
