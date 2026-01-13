package com.hypixel.hytale.builtin.beds.sleep.systems.world;

import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSleep;
import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSleep;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSlumber;
import com.hypixel.hytale.builtin.beds.sleep.resources.WorldSomnolence;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.time.Duration;
import java.time.Instant;
import java.util.Collection;
import java.util.Iterator;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class UpdateWorldSlumberSystem extends TickingSystem<EntityStore> {
   @Override
   public void tick(float dt, int systemIndex, @NonNullDecl Store<EntityStore> store) {
      World world = store.getExternalData().getWorld();
      WorldSomnolence worldSomnolence = store.getResource(WorldSomnolence.getResourceType());
      if (worldSomnolence.getState() instanceof WorldSlumber slumber) {
         slumber.incProgressSeconds(dt);
         boolean sleepingIsOver = slumber.getProgressSeconds() >= slumber.getIrlDurationSeconds() || isSomeoneAwake(store);
         if (sleepingIsOver) {
            worldSomnolence.setState(WorldSleep.Awake.INSTANCE);
            WorldTimeResource timeResource = store.getResource(WorldTimeResource.getResourceType());
            Instant wakeUpTime = computeWakeupTime(slumber);
            timeResource.setGameTime(wakeUpTime, world, store);
            store.forEachEntityParallel(PlayerSomnolence.getComponentType(), (index, archetypeChunk, commandBuffer) -> {
               PlayerSomnolence somnolence = archetypeChunk.getComponent(index, PlayerSomnolence.getComponentType());
               if (somnolence.getSleepState() instanceof PlayerSleep.Slumber) {
                  Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
                  commandBuffer.putComponent(ref, PlayerSomnolence.getComponentType(), PlayerSleep.MorningWakeUp.createComponent(timeResource));
               }
            });
         }
      }
   }

   private static Instant computeWakeupTime(WorldSlumber slumber) {
      float progress = slumber.getProgressSeconds() / slumber.getIrlDurationSeconds();
      long totalNanos = Duration.between(slumber.getStartInstant(), slumber.getTargetInstant()).toNanos();
      long progressNanos = (long)((float)totalNanos * progress);
      return slumber.getStartInstant().plusNanos(progressNanos);
   }

   private static boolean isSomeoneAwake(ComponentAccessor<EntityStore> store) {
      World world = store.getExternalData().getWorld();
      Collection<PlayerRef> playerRefs = world.getPlayerRefs();
      if (playerRefs.isEmpty()) {
         return false;
      } else {
         Iterator var3 = playerRefs.iterator();
         if (var3.hasNext()) {
            PlayerRef playerRef = (PlayerRef)var3.next();
            PlayerSomnolence somnolence = store.getComponent(playerRef.getReference(), PlayerSomnolence.getComponentType());
            if (somnolence == null) {
               return true;
            } else {
               PlayerSleep sleepState = somnolence.getSleepState();
               return sleepState instanceof PlayerSleep.FullyAwake;
            }
         } else {
            return false;
         }
      }
   }
}
