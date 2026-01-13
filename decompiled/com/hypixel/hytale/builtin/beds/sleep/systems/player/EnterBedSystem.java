package com.hypixel.hytale.builtin.beds.sleep.systems.player;

import com.hypixel.hytale.builtin.beds.sleep.systems.world.CanSleepInWorld;
import com.hypixel.hytale.builtin.mounts.MountedComponent;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.protocol.BlockMountType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.gameplay.SleepConfig;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class EnterBedSystem extends RefChangeSystem<EntityStore, MountedComponent> {
   public static final Query<EntityStore> QUERY = Query.and(MountedComponent.getComponentType(), PlayerRef.getComponentType());

   @Override
   public ComponentType<EntityStore, MountedComponent> componentType() {
      return MountedComponent.getComponentType();
   }

   @Override
   public Query<EntityStore> getQuery() {
      return QUERY;
   }

   public void onComponentAdded(
      @NonNullDecl Ref<EntityStore> ref,
      @NonNullDecl MountedComponent component,
      @NonNullDecl Store<EntityStore> store,
      @NonNullDecl CommandBuffer<EntityStore> commandBuffer
   ) {
      this.check(ref, component, store);
   }

   public void onComponentSet(
      @NonNullDecl Ref<EntityStore> ref,
      @NullableDecl MountedComponent oldComponent,
      @NonNullDecl MountedComponent newComponent,
      @NonNullDecl Store<EntityStore> store,
      @NonNullDecl CommandBuffer<EntityStore> commandBuffer
   ) {
      this.check(ref, newComponent, store);
   }

   public void onComponentRemoved(
      @NonNullDecl Ref<EntityStore> ref,
      @NonNullDecl MountedComponent component,
      @NonNullDecl Store<EntityStore> store,
      @NonNullDecl CommandBuffer<EntityStore> commandBuffer
   ) {
   }

   public void check(Ref<EntityStore> ref, MountedComponent component, Store<EntityStore> store) {
      if (component.getBlockMountType() == BlockMountType.Bed) {
         this.onEnterBed(ref, store);
      }
   }

   public void onEnterBed(Ref<EntityStore> ref, Store<EntityStore> store) {
      World world = store.getExternalData().getWorld();
      CanSleepInWorld.Result canSleepResult = CanSleepInWorld.check(world);
      if (canSleepResult.isNegative()) {
         PlayerRef playerRef = store.getComponent(ref, PlayerRef.getComponentType());
         if (canSleepResult instanceof CanSleepInWorld.NotDuringSleepHoursRange(LocalDateTime msg, SleepConfig var14)) {
            LocalTime startTime = var14.getSleepStartTime();
            Duration untilSleep = var14.computeDurationUntilSleep(msg);
            Message msgx = Message.translation("server.interactions.sleep.sleepAtTheseHours")
               .param("time", formatTime(startTime))
               .param("until", formatDuration(untilSleep));
            playerRef.sendMessage(msgx.color("#F2D729"));
         } else {
            Message msg = this.getMessage(canSleepResult);
            playerRef.sendMessage(msg);
         }
      }
   }

   private Message getMessage(CanSleepInWorld.Result param1) {
      // $VF: Couldn't be decompiled
      // Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
      // java.lang.IllegalStateException: Invalid switch case set: [[const(0)], [var1_1 instanceof x], [null]] for selector of type Lcom/hypixel/hytale/builtin/beds/sleep/systems/world/CanSleepInWorld$Result;
      //   at org.jetbrains.java.decompiler.modules.decompiler.exps.SwitchHeadExprent.checkExprTypeBounds(SwitchHeadExprent.java:66)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExpr(VarTypeProcessor.java:140)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.checkTypeExprent(VarTypeProcessor.java:126)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.lambda$processVarTypes$2(VarTypeProcessor.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.flow.DirectGraph.iterateExprents(DirectGraph.java:107)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.processVarTypes(VarTypeProcessor.java:114)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarTypeProcessor.calculateVarTypes(VarTypeProcessor.java:44)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarVersionsProcessor.setVarVersions(VarVersionsProcessor.java:68)
      //   at org.jetbrains.java.decompiler.modules.decompiler.vars.VarProcessor.setVarVersions(VarProcessor.java:47)
      //   at org.jetbrains.java.decompiler.main.rels.MethodProcessor.codeToJava(MethodProcessor.java:302)
      //
      // Bytecode:
      // 00: aload 1
      // 01: dup
      // 02: invokestatic java/util/Objects.requireNonNull (Ljava/lang/Object;)Ljava/lang/Object;
      // 05: pop
      // 06: astore 3
      // 07: bipush 0
      // 08: istore 4
      // 0a: aload 3
      // 0b: iload 4
      // 0d: invokedynamic typeSwitch (Ljava/lang/Object;I)I bsm=java/lang/runtime/SwitchBootstraps.typeSwitch (Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite; args=[ null.invoke Ljava/lang/Enum$EnumDesc;, com/hypixel/hytale/builtin/beds/sleep/systems/world/CanSleepInWorld$NotDuringSleepHoursRange ]
      // 12: lookupswitch 42 2 0 26 1 31
      // 2c: ldc "server.interactions.sleep.gameTimePaused"
      // 2e: goto 3e
      // 31: aload 3
      // 32: checkcast com/hypixel/hytale/builtin/beds/sleep/systems/world/CanSleepInWorld$NotDuringSleepHoursRange
      // 35: astore 5
      // 37: ldc "server.interactions.sleep.notWithinHours"
      // 39: goto 3e
      // 3c: ldc "server.interactions.sleep.disabled"
      // 3e: astore 2
      // 3f: aload 2
      // 40: invokestatic com/hypixel/hytale/server/core/Message.translation (Ljava/lang/String;)Lcom/hypixel/hytale/server/core/Message;
      // 43: areturn
   }

   private static Message formatTime(LocalTime time) {
      int hour = time.getHour();
      int minute = time.getMinute();
      boolean isPM = hour >= 12;
      int displayHour = hour % 12;
      if (displayHour == 0) {
         displayHour = 12;
      }

      String msgKey = isPM ? "server.interactions.sleep.timePM" : "server.interactions.sleep.timeAM";
      return Message.translation(msgKey).param("h", displayHour).param("m", String.format("%02d", minute));
   }

   private static Message formatDuration(Duration duration) {
      long totalMinutes = duration.toMinutes();
      long hours = totalMinutes / 60L;
      long minutes = totalMinutes % 60L;
      String msgKey = hours > 0L ? "server.interactions.sleep.durationHours" : "server.interactions.sleep.durationMins";
      return Message.translation(msgKey).param("hours", hours).param("mins", hours == 0L ? String.valueOf(minutes) : String.format("%02d", minutes));
   }
}
