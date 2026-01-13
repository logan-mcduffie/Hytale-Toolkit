package com.hypixel.hytale.builtin.beds.sleep.resources;

import com.hypixel.hytale.protocol.InstantData;
import com.hypixel.hytale.protocol.packets.world.SleepClock;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import java.time.Instant;

public final class WorldSlumber implements WorldSleep {
   private final Instant startInstant;
   private final Instant targetInstant;
   private final InstantData startInstantData;
   private final InstantData targetInstantData;
   private final float irlDurationSeconds;
   private float progressSeconds = 0.0F;

   public WorldSlumber(Instant startInstant, Instant targetInstant, float irlDurationSeconds) {
      this.startInstant = startInstant;
      this.targetInstant = targetInstant;
      this.startInstantData = WorldTimeResource.instantToInstantData(startInstant);
      this.targetInstantData = WorldTimeResource.instantToInstantData(targetInstant);
      this.irlDurationSeconds = irlDurationSeconds;
   }

   public Instant getStartInstant() {
      return this.startInstant;
   }

   public Instant getTargetInstant() {
      return this.targetInstant;
   }

   public InstantData getStartInstantData() {
      return this.startInstantData;
   }

   public InstantData getTargetInstantData() {
      return this.targetInstantData;
   }

   public float getProgressSeconds() {
      return this.progressSeconds;
   }

   public void incProgressSeconds(float seconds) {
      this.progressSeconds += seconds;
      this.progressSeconds = Math.min(this.progressSeconds, this.irlDurationSeconds);
   }

   public float getIrlDurationSeconds() {
      return this.irlDurationSeconds;
   }

   public SleepClock createSleepClock() {
      float progress = this.progressSeconds / this.irlDurationSeconds;
      return new SleepClock(this.startInstantData, this.targetInstantData, progress, this.irlDurationSeconds);
   }
}
