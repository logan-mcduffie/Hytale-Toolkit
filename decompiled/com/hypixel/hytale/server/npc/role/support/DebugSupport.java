package com.hypixel.hytale.server.npc.role.support;

import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.instructions.Sensor;
import com.hypixel.hytale.server.npc.role.RoleDebugDisplay;
import com.hypixel.hytale.server.npc.role.RoleDebugFlags;
import com.hypixel.hytale.server.npc.role.builders.BuilderRole;
import java.util.EnumSet;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DebugSupport {
   protected final NPCEntity parent;
   @Nullable
   protected RoleDebugDisplay debugDisplay;
   protected boolean debugRoleSteering;
   protected boolean debugMotionSteering;
   protected EnumSet<RoleDebugFlags> debugFlags;
   @Nullable
   protected String displayCustomString;
   @Nullable
   protected String displayPathfinderString;
   protected boolean traceSuccess;
   protected boolean traceFail;
   protected boolean traceSensorFails;
   protected Sensor lastFailingSensor;

   public DebugSupport(NPCEntity parent, @Nonnull BuilderRole builder) {
      this.parent = parent;
      this.debugFlags = builder.getDebugFlags();
   }

   @Nullable
   public RoleDebugDisplay getDebugDisplay() {
      return this.debugDisplay;
   }

   public boolean isTraceSuccess() {
      return this.traceSuccess;
   }

   public boolean isTraceFail() {
      return this.traceFail;
   }

   public boolean isTraceSensorFails() {
      return this.traceSensorFails;
   }

   public void setLastFailingSensor(Sensor sensor) {
      this.lastFailingSensor = sensor;
   }

   public Sensor getLastFailingSensor() {
      return this.lastFailingSensor;
   }

   public boolean isDebugRoleSteering() {
      return this.debugRoleSteering;
   }

   public boolean isDebugMotionSteering() {
      return this.debugMotionSteering;
   }

   public void setDisplayCustomString(String displayCustomString) {
      this.displayCustomString = displayCustomString;
   }

   @Nullable
   public String pollDisplayCustomString() {
      String ret = this.displayCustomString;
      this.displayCustomString = null;
      return ret;
   }

   public void setDisplayPathfinderString(String displayPathfinderString) {
      this.displayPathfinderString = displayPathfinderString;
   }

   @Nullable
   public String pollDisplayPathfinderString() {
      String ret = this.displayPathfinderString;
      this.displayPathfinderString = null;
      return ret;
   }

   public EnumSet<RoleDebugFlags> getDebugFlags() {
      return this.debugFlags;
   }

   public void setDebugFlags(EnumSet<RoleDebugFlags> debugFlags) {
      this.debugFlags = debugFlags;
      this.activate();
   }

   public boolean isDebugFlagSet(RoleDebugFlags flag) {
      return this.debugFlags.contains(flag);
   }

   public boolean isAnyDebugFlagSet(@Nonnull EnumSet<RoleDebugFlags> flags) {
      for (RoleDebugFlags d : flags) {
         if (this.debugFlags.contains(d)) {
            return true;
         }
      }

      return false;
   }

   public void activate() {
      this.debugRoleSteering = this.isDebugFlagSet(RoleDebugFlags.SteeringRole);
      this.debugMotionSteering = this.isDebugFlagSet(RoleDebugFlags.MotionControllerSteer);
      this.traceFail = this.isDebugFlagSet(RoleDebugFlags.TraceFail);
      this.traceSuccess = this.isDebugFlagSet(RoleDebugFlags.TraceSuccess);
      this.traceSensorFails = this.isDebugFlagSet(RoleDebugFlags.TraceSensorFailures);
      this.debugDisplay = RoleDebugDisplay.create(this.debugFlags);
   }
}
