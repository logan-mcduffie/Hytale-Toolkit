package com.hypixel.hytale.builtin.adventure.npcobjectives.systems;

import com.hypixel.hytale.builtin.adventure.objectives.ObjectivePlugin;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.spawning.beacons.LegacySpawnBeaconEntity;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SpawnBeaconCheckRemovalSystem extends HolderSystem<EntityStore> {
   @Nullable
   @Override
   public Query<EntityStore> getQuery() {
      return LegacySpawnBeaconEntity.getComponentType();
   }

   @Override
   public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
      LegacySpawnBeaconEntity spawnBeaconComponent = holder.getComponent(LegacySpawnBeaconEntity.getComponentType());

      assert spawnBeaconComponent != null;

      UUID objectiveUUID = spawnBeaconComponent.getObjectiveUUID();
      if (objectiveUUID != null && ObjectivePlugin.get().getObjectiveDataStore().getObjective(objectiveUUID) == null) {
         spawnBeaconComponent.remove();
      }
   }

   @Override
   public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
   }
}
