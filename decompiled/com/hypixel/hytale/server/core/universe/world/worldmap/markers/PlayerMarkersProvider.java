package com.hypixel.hytale.server.core.universe.world.worldmap.markers;

import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import javax.annotation.Nonnull;

public class PlayerMarkersProvider implements WorldMapManager.MarkerProvider {
   public static final PlayerMarkersProvider INSTANCE = new PlayerMarkersProvider();

   private PlayerMarkersProvider() {
   }

   @Override
   public void update(
      @Nonnull World world, @Nonnull GameplayConfig gameplayConfig, @Nonnull WorldMapTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ
   ) {
      Player player = tracker.getPlayer();
      PlayerWorldData perWorldData = player.getPlayerConfigData().getPerWorldData(world.getName());
      MapMarker[] worldMapMarkers = perWorldData.getWorldMapMarkers();
      if (worldMapMarkers != null) {
         for (MapMarker marker : worldMapMarkers) {
            tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, marker);
         }
      }
   }
}
