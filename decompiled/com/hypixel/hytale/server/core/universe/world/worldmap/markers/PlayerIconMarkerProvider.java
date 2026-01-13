package com.hypixel.hytale.server.core.universe.world.worldmap.markers;

import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.asset.type.gameplay.WorldMapConfig;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.WorldMapTracker;
import com.hypixel.hytale.server.core.universe.world.worldmap.WorldMapManager;
import com.hypixel.hytale.server.core.util.PositionUtil;
import java.util.function.Predicate;
import javax.annotation.Nonnull;

public class PlayerIconMarkerProvider implements WorldMapManager.MarkerProvider {
   public static final PlayerIconMarkerProvider INSTANCE = new PlayerIconMarkerProvider();

   private PlayerIconMarkerProvider() {
   }

   @Override
   public void update(
      @Nonnull World world, @Nonnull GameplayConfig gameplayConfig, @Nonnull WorldMapTracker tracker, int chunkViewRadius, int playerChunkX, int playerChunkZ
   ) {
      WorldMapConfig worldMapConfig = gameplayConfig.getWorldMapConfig();
      if (worldMapConfig.isDisplayPlayers()) {
         if (tracker.shouldUpdatePlayerMarkers()) {
            Player player = tracker.getPlayer();
            int chunkViewRadiusSq = chunkViewRadius * chunkViewRadius;
            Predicate<PlayerRef> playerMapFilter = tracker.getPlayerMapFilter();

            for (PlayerRef otherPlayer : world.getPlayerRefs()) {
               if (!otherPlayer.getUuid().equals(player.getUuid())) {
                  Transform otherPlayerTransform = otherPlayer.getTransform();
                  Vector3d otherPos = otherPlayerTransform.getPosition();
                  int otherChunkX = (int)otherPos.x >> 5;
                  int otherChunkZ = (int)otherPos.z >> 5;
                  int chunkDiffX = otherChunkX - playerChunkX;
                  int chunkDiffZ = otherChunkZ - playerChunkZ;
                  int chunkDistSq = chunkDiffX * chunkDiffX + chunkDiffZ * chunkDiffZ;
                  if (chunkDistSq <= chunkViewRadiusSq && (playerMapFilter == null || !playerMapFilter.test(otherPlayer))) {
                     tracker.trySendMarker(
                        chunkViewRadius,
                        playerChunkX,
                        playerChunkZ,
                        otherPos,
                        otherPlayer.getHeadRotation().getYaw(),
                        "Player-" + otherPlayer.getUuid(),
                        "Player: " + otherPlayer.getUsername(),
                        otherPlayer,
                        (id, name, op) -> new MapMarker(id, name, "Player.png", PositionUtil.toTransformPacket(op.getTransform()), null)
                     );
                  }
               }
            }

            tracker.resetPlayerMarkersUpdateTimer();
         }
      }
   }
}
