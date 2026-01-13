package com.hypixel.hytale.server.core.universe.world.worldmap.markers;

import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.asset.type.gameplay.WorldMapConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerDeathPositionData;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.util.PositionUtil;
import javax.annotation.Nonnull;

public class DeathMarkerProvider implements WorldMapManager.MarkerProvider {
   public static final DeathMarkerProvider INSTANCE = new DeathMarkerProvider();

   private DeathMarkerProvider() {
   }

   @Override
   public void update(
      @Nonnull World world, @Nonnull GameplayConfig gameplayConfig, @Nonnull WorldMapTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ
   ) {
      WorldMapConfig worldMapConfig = gameplayConfig.getWorldMapConfig();
      if (worldMapConfig.isDisplayDeathMarker()) {
         Player player = tracker.getPlayer();
         PlayerWorldData perWorldData = player.getPlayerConfigData().getPerWorldData(world.getName());

         for (PlayerDeathPositionData deathPosition : perWorldData.getDeathPositions()) {
            addDeathMarker(tracker, playerChunkX, playerChunkZ, deathPosition);
         }
      }
   }

   private static void addDeathMarker(@Nonnull WorldMapTracker tracker, int playerChunkX, int playerChunkZ, @Nonnull PlayerDeathPositionData deathPosition) {
      String markerId = deathPosition.getMarkerId();
      Transform transform = deathPosition.getTransform();
      int deathDay = deathPosition.getDay();
      tracker.trySendMarker(
         -1,
         playerChunkX,
         playerChunkZ,
         transform.getPosition(),
         transform.getRotation().getYaw(),
         markerId,
         "Death (Day " + deathDay + ")",
         transform,
         (id, name, t) -> new MapMarker(id, name, "Death.png", PositionUtil.toTransformPacket(t), null)
      );
   }
}
