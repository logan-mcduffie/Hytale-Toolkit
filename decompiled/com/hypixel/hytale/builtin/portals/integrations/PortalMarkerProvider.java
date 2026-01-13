package com.hypixel.hytale.builtin.portals.integrations;

import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.server.core.universe.world.spawn.IndividualSpawnProvider;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.util.PositionUtil;

public class PortalMarkerProvider implements WorldMapManager.MarkerProvider {
   public static final PortalMarkerProvider INSTANCE = new PortalMarkerProvider();

   @Override
   public void update(World world, GameplayConfig gameplayConfig, WorldMapTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
      addSpawn(world, tracker, chunkViewRadius, playerChunkX, playerChunkZ);
   }

   private static void addSpawn(World world, WorldMapTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ) {
      if (world.getWorldConfig().getSpawnProvider() instanceof IndividualSpawnProvider individualSpawnProvider) {
         Transform spawnPoint = individualSpawnProvider.getFirstSpawnPoint();
         if (spawnPoint != null) {
            tracker.trySendMarker(
               chunkViewRadius,
               playerChunkX,
               playerChunkZ,
               spawnPoint.getPosition(),
               spawnPoint.getRotation().getYaw(),
               "Portal",
               "Fragment Exit",
               spawnPoint,
               (id, name, sp) -> new MapMarker(id, name, "Portal.png", PositionUtil.toTransformPacket(sp), null)
            );
         }
      }
   }
}
