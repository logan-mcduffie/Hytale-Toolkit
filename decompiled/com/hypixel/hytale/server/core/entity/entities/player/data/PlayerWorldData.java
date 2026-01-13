package com.hypixel.hytale.server.core.entity.entities.player.data;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.protocol.SavedMovementStates;
import com.hypixel.hytale.protocol.packets.worldmap.MapMarker;
import com.hypixel.hytale.server.core.codec.ProtocolCodecs;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public final class PlayerWorldData {
   @Nonnull
   public static final BuilderCodec<PlayerWorldData> CODEC = BuilderCodec.builder(PlayerWorldData.class, PlayerWorldData::new)
      .append(
         new KeyedCodec<>("LastPosition", Transform.CODEC),
         (playerWorldData, lastPosition) -> playerWorldData.lastPosition = lastPosition,
         playerWorldData -> playerWorldData.lastPosition
      )
      .documentation("The last known position of the player.")
      .add()
      .<SavedMovementStates>append(
         new KeyedCodec<>("LastMovementStates", ProtocolCodecs.SAVED_MOVEMENT_STATES),
         (playerWorldData, lastMovementStates) -> playerWorldData.lastMovementStates = lastMovementStates,
         playerWorldData -> playerWorldData.lastMovementStates
      )
      .documentation("The last known movement states of the player.")
      .add()
      .<MapMarker[]>append(
         new KeyedCodec<>("WorldMapMarkers", ProtocolCodecs.MARKER_ARRAY),
         (playerConfigData, objectives) -> playerConfigData.worldMapMarkers = objectives,
         playerConfigData -> playerConfigData.worldMapMarkers
      )
      .documentation("The world map markers of the player.")
      .add()
      .<Boolean>append(
         new KeyedCodec<>("FirstSpawn", Codec.BOOLEAN),
         (playerWorldData, value) -> playerWorldData.firstSpawn = value,
         playerWorldData -> playerWorldData.firstSpawn
      )
      .documentation("Whether this is the first spawn of the player.")
      .add()
      .<PlayerRespawnPointData[]>append(
         new KeyedCodec<>("RespawnPoints", new ArrayCodec<>(PlayerRespawnPointData.CODEC, PlayerRespawnPointData[]::new)),
         (playerWorldData, respawnPointData) -> playerWorldData.respawnPoints = respawnPointData,
         playerWorldData -> playerWorldData.respawnPoints
      )
      .documentation("The respawn points of the player.")
      .add()
      .<PlayerDeathPositionData[]>append(
         new KeyedCodec<>("DeathPositions", new ArrayCodec<>(PlayerDeathPositionData.CODEC, PlayerDeathPositionData[]::new)),
         (playerWorldData, deathPositions) -> playerWorldData.deathPositions = ObjectArrayList.wrap(deathPositions),
         playerWorldData -> playerWorldData.deathPositions.toArray(PlayerDeathPositionData[]::new)
      )
      .documentation("The death positions of the player in this world.")
      .add()
      .build();
   private static final int DEATH_POSITIONS_COUNT_MAX = 5;
   private transient PlayerConfigData playerConfigData;
   private Transform lastPosition;
   private SavedMovementStates lastMovementStates;
   private MapMarker[] worldMapMarkers;
   private boolean firstSpawn = true;
   @Nullable
   private PlayerRespawnPointData[] respawnPoints;
   @Nonnull
   private List<PlayerDeathPositionData> deathPositions = new ObjectArrayList<>();

   private PlayerWorldData() {
   }

   PlayerWorldData(@Nonnull PlayerConfigData playerConfigData) {
      this.playerConfigData = playerConfigData;
   }

   public void setPlayerConfigData(@Nonnull PlayerConfigData playerConfigData) {
      this.playerConfigData = playerConfigData;
   }

   public Transform getLastPosition() {
      return this.lastPosition;
   }

   public void setLastPosition(@Nonnull Transform lastPosition) {
      this.lastPosition = lastPosition;
      this.playerConfigData.markChanged();
   }

   public SavedMovementStates getLastMovementStates() {
      return this.lastMovementStates;
   }

   public void setLastMovementStates(@Nonnull MovementStates lastMovementStates, boolean save) {
      this.setLastMovementStates_internal(lastMovementStates);
      if (save) {
         this.playerConfigData.markChanged();
      }
   }

   private void setLastMovementStates_internal(@Nonnull MovementStates lastMovementStates) {
      this.lastMovementStates = new SavedMovementStates(lastMovementStates.flying);
   }

   @Nullable
   public MapMarker[] getWorldMapMarkers() {
      return this.worldMapMarkers;
   }

   public void setWorldMapMarkers(MapMarker[] worldMapMarkers) {
      this.worldMapMarkers = worldMapMarkers;
      this.playerConfigData.markChanged();
   }

   public boolean isFirstSpawn() {
      return this.firstSpawn;
   }

   public void setFirstSpawn(boolean firstSpawn) {
      this.firstSpawn = firstSpawn;
   }

   @Nullable
   public PlayerRespawnPointData[] getRespawnPoints() {
      return this.respawnPoints;
   }

   public void setRespawnPoints(@Nonnull PlayerRespawnPointData[] respawnPoints) {
      this.respawnPoints = respawnPoints;
      this.playerConfigData.markChanged();
   }

   @Nonnull
   public List<PlayerDeathPositionData> getDeathPositions() {
      return this.deathPositions;
   }

   public void addLastDeath(@Nonnull String markerId, @Nonnull Transform transform, int deathDay) {
      this.deathPositions.add(new PlayerDeathPositionData(markerId, transform, deathDay));

      while (this.deathPositions.size() > 5) {
         this.deathPositions.removeFirst();
      }

      this.playerConfigData.markChanged();
   }

   public void removeLastDeath(@Nonnull String markerId) {
      this.deathPositions.removeIf(deathPosition -> deathPosition.getMarkerId().equalsIgnoreCase(markerId));
      this.playerConfigData.markChanged();
   }
}
