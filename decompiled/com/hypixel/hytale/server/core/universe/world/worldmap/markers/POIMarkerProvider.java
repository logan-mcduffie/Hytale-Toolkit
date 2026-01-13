package com.hypixel.hytale.server.core.universe.world.worldmap.markers;

import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import java.util.Map;
import javax.annotation.Nonnull;

public class POIMarkerProvider implements WorldMapManager.MarkerProvider {
   public static final POIMarkerProvider INSTANCE = new POIMarkerProvider();

   private POIMarkerProvider() {
   }

   @Override
   public void update(
      @Nonnull World world, @Nonnull GameplayConfig gameplayConfig, @Nonnull WorldMapTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ
   ) {
      Map<String, MapMarker> globalMarkers = world.getWorldMapManager().getPointsOfInterest();
      if (!globalMarkers.isEmpty()) {
         for (MapMarker marker : globalMarkers.values()) {
            tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, marker);
         }
      }
   }
}
