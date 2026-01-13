package com.hypixel.hytale.builtin.beds.sleep.systems.world;

import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSleep;
import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSleep;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSlumber;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSomnolence;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.DelayedSystem;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class StartSlumberSystem extends DelayedSystem<EntityStore> {
   public static final Duration NODDING_OFF_DURATION = Duration.ofMillis(3200L);
   public static final Duration WAKE_UP_AUTOSLEEP_DELAY = Duration.ofHours(1L);

   public StartSlumberSystem() {
      super(0.3F);
   }

   @Override
   public void delayedTick(float dt, int systemIndex, @NonNullDecl Store<EntityStore> store) {
      this.checkIfEveryoneIsReadyToSleep(store);
   }

   private void checkIfEveryoneIsReadyToSleep(Store<EntityStore> store) {
      World world = store.getExternalData().getWorld();
      Collection<PlayerRef> playerRefs = world.getPlayerRefs();
      if (!playerRefs.isEmpty()) {
         if (!CanSleepInWorld.check(world).isNegative()) {
            float wakeUpHour = world.getGameplayConfig().getWorldConfig().getSleepConfig().getWakeUpHour();
            WorldSomnolence worldSomnolence = store.getResource(WorldSomnolence.getResourceType());
            WorldSleep worldState = worldSomnolence.getState();
            if (worldState == WorldSleep.Awake.INSTANCE) {
               if (this.isEveryoneReadyToSleep(store)) {
                  WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());
                  Instant now = timeResource.getGameTime();
                  Instant target = this.computeWakeupInstant(now, wakeUpHour);
                  float irlSeconds = computeIrlSeconds(now, target);
                  worldSomnolence.setState(new WorldSlumber(now, target, irlSeconds));
                  store.forEachEntityParallel(PlayerSomnolence.getComponentType(), (index, archetypeChunk, commandBuffer) -> {
                     Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
                     commandBuffer.putComponent(ref, PlayerSomnolence.getComponentType(), PlayerSleep.Slumber.createComponent(timeResource));
                  });
               }
            }
         }
      }
   }

   private Instant computeWakeupInstant(Instant now, float wakeUpHour) {
      LocalDateTime ldt = LocalDateTime.ofInstant(now, ZoneOffset.UTC);
      int hours = (int)wakeUpHour;
      float fractionalHour = wakeUpHour - hours;
      LocalDateTime wakeUpTime = ldt.toLocalDate().atTime(hours, (int)(fractionalHour * 60.0F));
      if (!ldt.isBefore(wakeUpTime)) {
         wakeUpTime = wakeUpTime.plusDays(1L);
      }

      return wakeUpTime.toInstant(ZoneOffset.UTC);
   }

   private static float computeIrlSeconds(Instant startInstant, Instant targetInstant) {
      long ms = Duration.between(startInstant, targetInstant).toMillis();
      long hours = TimeUnit.MILLISECONDS.toHours(ms);
      double seconds = Math.max(3.0, hours / 6.0);
      return (float)Math.ceil(seconds);
   }

   private boolean isEveryoneReadyToSleep(ComponentAccessor<EntityStore> store) {
      World world = store.getExternalData().getWorld();
      Collection<PlayerRef> playerRefs = world.getPlayerRefs();
      if (playerRefs.isEmpty()) {
         return false;
      } else {
         for (PlayerRef playerRef : playerRefs) {
            if (!isReadyToSleep(store, playerRef.getReference())) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isReadyToSleep(ComponentAccessor<EntityStore> store, Ref<EntityStore> ref) {
      PlayerSomnolence somnolence = store.getComponent(ref, PlayerSomnolence.getComponentType());
      if (somnolence == null) {
         return false;
      } else {
         PlayerSleep sleepState = somnolence.getSleepState();

         return switch (sleepState) {
            case PlayerSleep.FullyAwake fullyAwake -> false;
            case PlayerSleep.MorningWakeUp morningWakeUp -> {
               WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
               Instant readyTime = morningWakeUp.gameTimeStart().plus(WAKE_UP_AUTOSLEEP_DELAY);
               yield worldTimeResource.getGameTime().isAfter(readyTime);
            }
            case PlayerSleep.NoddingOff noddingOff -> {
               Instant sleepStart = noddingOff.realTimeStart().plus(NODDING_OFF_DURATION);
               yield Instant.now().isAfter(sleepStart);
            }
            case PlayerSleep.Slumber slumber -> true;
            default -> throw new MatchException(null, null);
         };
      }
   }
}
