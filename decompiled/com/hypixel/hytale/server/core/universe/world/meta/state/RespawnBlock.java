package com.hypixel.hytale.server.core.universe.world.meta.state;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerRespawnPointData;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerWorldData;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RespawnBlock implements Component<ChunkStore> {
   public static final BuilderCodec<RespawnBlock> CODEC = BuilderCodec.builder(RespawnBlock.class, RespawnBlock::new)
      .append(
         new KeyedCodec<>("OwnerUUID", Codec.UUID_BINARY),
         (respawnBlockState, uuid) -> respawnBlockState.ownerUUID = uuid,
         respawnBlockState -> respawnBlockState.ownerUUID
      )
      .add()
      .build();
   private UUID ownerUUID;

   public static ComponentType<ChunkStore, RespawnBlock> getComponentType() {
      return BlockModule.get().getRespawnBlockComponentType();
   }

   public RespawnBlock() {
   }

   public RespawnBlock(UUID ownerUUID) {
      this.ownerUUID = ownerUUID;
   }

   public UUID getOwnerUUID() {
      return this.ownerUUID;
   }

   public void setOwnerUUID(UUID ownerUUID) {
      this.ownerUUID = ownerUUID;
   }

   @Nullable
   @Override
   public Component<ChunkStore> clone() {
      return new RespawnBlock(this.ownerUUID);
   }

   public static class OnRemove extends RefSystem<ChunkStore> {
      public static final ComponentType<ChunkStore, RespawnBlock> COMPONENT_TYPE = RespawnBlock.getComponentType();
      public static final ComponentType<ChunkStore, BlockModule.BlockStateInfo> BLOCK_INFO_TYPE = BlockModule.BlockStateInfo.getComponentType();
      public static final Query<ChunkStore> QUERY = Query.and(COMPONENT_TYPE, BLOCK_INFO_TYPE);

      @Override
      public void onEntityAdded(
         @Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         if (reason != RemoveReason.UNLOAD) {
            RespawnBlock respawnState = commandBuffer.getComponent(ref, COMPONENT_TYPE);

            assert respawnState != null;

            if (respawnState.ownerUUID != null) {
               BlockModule.BlockStateInfo blockInfo = commandBuffer.getComponent(ref, BLOCK_INFO_TYPE);

               assert blockInfo != null;

               PlayerRef playerRef = Universe.get().getPlayer(respawnState.ownerUUID);
               if (playerRef == null) {
                  HytaleLogger.getLogger().at(Level.WARNING).log("Need to load PlayerConfig to remove RespawnPoint!");
               } else {
                  Player player = playerRef.getComponent(Player.getComponentType());
                  Ref<ChunkStore> chunkRef = blockInfo.getChunkRef();
                  if (chunkRef != null && chunkRef.isValid()) {
                     PlayerWorldData playerWorldData = player.getPlayerConfigData().getPerWorldData(store.getExternalData().getWorld().getName());
                     PlayerRespawnPointData[] respawnPoints = playerWorldData.getRespawnPoints();
                     WorldChunk wc = commandBuffer.getComponent(chunkRef, WorldChunk.getComponentType());
                     Vector3i blockPosition = new Vector3i(
                        ChunkUtil.worldCoordFromLocalCoord(wc.getX(), ChunkUtil.xFromBlockInColumn(blockInfo.getIndex())),
                        ChunkUtil.yFromBlockInColumn(blockInfo.getIndex()),
                        ChunkUtil.worldCoordFromLocalCoord(wc.getZ(), ChunkUtil.zFromBlockInColumn(blockInfo.getIndex()))
                     );

                     for (int i = 0; i < respawnPoints.length; i++) {
                        PlayerRespawnPointData respawnPoint = respawnPoints[i];
                        if (respawnPoint.getBlockPosition().equals(blockPosition)) {
                           playerWorldData.setRespawnPoints(ArrayUtil.remove(respawnPoints, i));
                           return;
                        }
                     }
                  }
               }
            }
         }
      }

      @Nullable
      @Override
      public Query<ChunkStore> getQuery() {
         return QUERY;
      }
   }
}
