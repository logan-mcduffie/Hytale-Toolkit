package com.hypixel.hytale.builtin.buildertools.prefabeditor;

import com.hypixel.hytale.builtin.buildertools.BuilderToolsPlugin;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import javax.annotation.Nonnull;

public class PrefabMarkerProvider implements WorldMapManager.MarkerProvider {
   public static final PrefabMarkerProvider INSTANCE = new PrefabMarkerProvider();

   @Override
   public void update(
      @Nonnull World world, @Nonnull GameplayConfig gameplayConfig, @Nonnull WorldMapTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ
   ) {
      PrefabEditSessionManager sessionManager = BuilderToolsPlugin.get().getPrefabEditSessionManager();
      Player player = tracker.getPlayer();
      PrefabEditSession session = sessionManager.getPrefabEditSession(player.getUuid());
      if (session != null && session.getWorldName().equals(world.getName())) {
         for (PrefabEditingMetadata metadata : session.getLoadedPrefabMetadata().values()) {
            MapMarker marker = PrefabEditSession.createPrefabMarker(metadata);
            tracker.trySendMarker(chunkViewRadius, playerChunkX, playerChunkZ, marker);
         }
      }
   }
}
