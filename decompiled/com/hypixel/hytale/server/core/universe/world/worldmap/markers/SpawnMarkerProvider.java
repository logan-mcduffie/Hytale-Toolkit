package com.hypixel.hytale.server.core.universe.world.worldmap.markers;

import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.asset.type.gameplay.WorldMapConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.util.PositionUtil;
import javax.annotation.Nonnull;

public class SpawnMarkerProvider implements WorldMapManager.MarkerProvider {
   public static final SpawnMarkerProvider INSTANCE = new SpawnMarkerProvider();

   private SpawnMarkerProvider() {
   }

   @Override
   public void update(
      @Nonnull World world, @Nonnull GameplayConfig gameplayConfig, @Nonnull WorldMapTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ
   ) {
      WorldMapConfig worldMapConfig = gameplayConfig.getWorldMapConfig();
      if (worldMapConfig.isDisplaySpawn()) {
         Player player = tracker.getPlayer();
         Transform spawnPoint = world.getWorldConfig().getSpawnProvider().getSpawnPoint(player);
         if (spawnPoint != null) {
            Vector3d spawnPosition = spawnPoint.getPosition();
            tracker.trySendMarker(
               chunkViewRadius,
               playerChunkX,
               playerChunkZ,
               spawnPosition,
               spawnPoint.getRotation().getYaw(),
               "Spawn",
               "Spawn",
               spawnPosition,
               (id, name, pos) -> new MapMarker(id, name, "Spawn.png", PositionUtil.toTransformPacket(new Transform(pos)), null)
            );
         }
      }
   }
}
