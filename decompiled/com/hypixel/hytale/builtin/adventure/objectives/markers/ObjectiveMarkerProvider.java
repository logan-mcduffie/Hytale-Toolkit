package com.hypixel.hytale.builtin.adventure.objectives.markers;

import com.hypixel.hytale.builtin.adventure.objectives.Objective;
import com.hypixel.hytale.builtin.adventure.objectives.ObjectiveDataStore;
import com.hypixel.hytale.builtin.adventure.objectives.ObjectivePlugin;
import com.hypixel.hytale.builtin.adventure.objectives.task.ObjectiveTask;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import java.util.Set;
import java.util.UUID;
import javax.annotation.Nonnull;

public class ObjectiveMarkerProvider implements WorldMapManager.MarkerProvider {
   public static final ObjectiveMarkerProvider INSTANCE = new ObjectiveMarkerProvider();

   private ObjectiveMarkerProvider() {
   }

   @Override
   public void update(
      @Nonnull World world, @Nonnull GameplayConfig gameplayConfig, @Nonnull WorldMapTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ
   ) {
      Player player = tracker.getPlayer();
      Set<UUID> activeObjectiveUUIDs = player.getPlayerConfigData().getActiveObjectiveUUIDs();
      if (!activeObjectiveUUIDs.isEmpty()) {
         UUID playerUUID = player.getUuid();
         ObjectiveDataStore objectiveDataStore = ObjectivePlugin.get().getObjectiveDataStore();

         for (UUID objectiveUUID : activeObjectiveUUIDs) {
            Objective objective = objectiveDataStore.getObjective(objectiveUUID);
            if (objective != null && objective.getActivePlayerUUIDs().contains(playerUUID)) {
               ObjectiveTask[] tasks = objective.getCurrentTasks();
               if (tasks != null) {
                  for (ObjectiveTask task : tasks) {
                     for (MapMarker marker : task.getMarkers()) {
                        tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, marker);
                     }
                  }
               }
            }
         }
      }
   }
}
