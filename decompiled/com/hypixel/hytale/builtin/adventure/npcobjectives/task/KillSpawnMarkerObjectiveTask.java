package com.hypixel.hytale.builtin.adventure.npcobjectives.task;

import com.hypixel.hytale.builtin.adventure.npcobjectives.assets.KillSpawnMarkerObjectiveTaskAsset;
import com.hypixel.hytale.builtin.adventure.npcobjectives.resources.KillTrackerResource;
import com.hypixel.hytale.builtin.adventure.npcobjectives.transaction.KillTaskTransaction;
import com.hypixel.hytale.builtin.adventure.objectives.Objective;
import com.hypixel.hytale.builtin.adventure.objectives.ObjectivePlugin;
import com.hypixel.hytale.builtin.adventure.objectives.transaction.TransactionRecord;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.spawning.SpawningPlugin;
import com.hypixel.hytale.server.spawning.spawnmarkers.SpawnMarkerEntity;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class KillSpawnMarkerObjectiveTask extends KillObjectiveTask {
   public static final BuilderCodec<KillSpawnMarkerObjectiveTask> CODEC = BuilderCodec.builder(
         KillSpawnMarkerObjectiveTask.class, KillSpawnMarkerObjectiveTask::new, KillObjectiveTask.CODEC
      )
      .build();
   private static final ComponentType<EntityStore, SpawnMarkerEntity> SPAWN_MARKER_COMPONENT_TYPE = SpawnMarkerEntity.getComponentType();
   private static final ComponentType<EntityStore, TransformComponent> TRANSFORM_COMPONENT_TYPE = TransformComponent.getComponentType();

   public KillSpawnMarkerObjectiveTask(@Nonnull KillSpawnMarkerObjectiveTaskAsset asset, int taskSetIndex, int taskIndex) {
      super(asset, taskSetIndex, taskIndex);
   }

   protected KillSpawnMarkerObjectiveTask() {
   }

   @Nonnull
   public KillSpawnMarkerObjectiveTaskAsset getAsset() {
      return (KillSpawnMarkerObjectiveTaskAsset)super.getAsset();
   }

   @Nonnull
   @Override
   protected TransactionRecord[] setup0(@Nonnull Objective objective, @Nonnull World world, @Nonnull Store<EntityStore> store) {
      Vector3d objectivePosition = objective.getPosition(store);
      if (objectivePosition != null) {
         KillSpawnMarkerObjectiveTaskAsset asset = this.getAsset();
         ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
         SpatialResource<Ref<EntityStore>, EntityStore> spatialResource = store.getResource(SpawningPlugin.get().getSpawnMarkerSpatialResource());
         spatialResource.getSpatialStructure().collect(objectivePosition, asset.getRadius(), results);
         String[] spawnMarkerIds = asset.getSpawnMarkerIds();
         HytaleLogger logger = ObjectivePlugin.get().getLogger();

         for (Ref<EntityStore> entityReference : results) {
            SpawnMarkerEntity entitySpawnMarkerComponent = store.getComponent(entityReference, SPAWN_MARKER_COMPONENT_TYPE);

            assert entitySpawnMarkerComponent != null;

            String spawnMarkerId = entitySpawnMarkerComponent.getSpawnMarkerId();
            if (ArrayUtil.contains(spawnMarkerIds, spawnMarkerId)) {
               world.execute(() -> entitySpawnMarkerComponent.trigger(entityReference, store));
               logger.at(Level.INFO)
                  .log(
                     "Triggered SpawnMarker '"
                        + spawnMarkerId
                        + "' at position: "
                        + store.getComponent(entityReference, TRANSFORM_COMPONENT_TYPE).getPosition()
                  );
            }
         }
      }

      KillTaskTransaction transaction = new KillTaskTransaction(this, objective, store);
      store.getResource(KillTrackerResource.getResourceType()).watch(transaction);
      return new TransactionRecord[]{transaction};
   }

   @Nonnull
   @Override
   public String toString() {
      return "KillSpawnMarkerObjectiveTask{} " + super.toString();
   }
}
