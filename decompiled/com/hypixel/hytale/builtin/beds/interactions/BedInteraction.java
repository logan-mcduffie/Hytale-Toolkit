package com.hypixel.hytale.builtin.beds.interactions;

import com.hypixel.hytale.builtin.beds.respawn.OverrideNearbyRespawnPointPage;
import com.hypixel.hytale.builtin.beds.respawn.SelectOverrideRespawnPointPage;
import com.hypixel.hytale.builtin.beds.respawn.SetNameRespawnPointPage;
import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSleep;
import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.builtin.mounts.BlockMountAPI;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockPosition;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.asset.type.gameplay.RespawnConfig;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.data.PlayerRespawnPointData;
import com.hypixel.hytale.server.core.entity.entities.player.pages.PageManager;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.meta.state.RespawnBlock;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class BedInteraction extends SimpleBlockInteraction {
   public static final BuilderCodec<BedInteraction> CODEC = BuilderCodec.builder(BedInteraction.class, BedInteraction::new, SimpleBlockInteraction.CODEC)
      .documentation("Interact with a bed block, ostensibly to sleep in it.")
      .build();

   @Override
   protected void interactWithBlock(
      @NonNullDecl World world,
      @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
      @NonNullDecl InteractionType type,
      @NonNullDecl InteractionContext context,
      @NullableDecl ItemStack itemInHand,
      @NonNullDecl Vector3i pos,
      @NonNullDecl CooldownHandler cooldownHandler
   ) {
      Ref<EntityStore> ref = context.getEntity();
      Player player = commandBuffer.getComponent(ref, Player.getComponentType());
      if (player != null) {
         Store<EntityStore> store = commandBuffer.getStore();
         PlayerRef playerRefComponent = commandBuffer.getComponent(ref, PlayerRef.getComponentType());

         assert playerRefComponent != null;

         UUIDComponent playerUuidComponent = commandBuffer.getComponent(ref, UUIDComponent.getComponentType());

         assert playerUuidComponent != null;

         UUID playerUuid = playerUuidComponent.getUuid();
         Ref<ChunkStore> chunkReference = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(pos.x, pos.z));
         if (chunkReference != null) {
            Store<ChunkStore> chunkStore = chunkReference.getStore();
            BlockComponentChunk blockComponentChunk = chunkStore.getComponent(chunkReference, BlockComponentChunk.getComponentType());

            assert blockComponentChunk != null;

            int blockIndex = ChunkUtil.indexBlockInColumn(pos.x, pos.y, pos.z);
            Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndex);
            if (blockRef == null) {
               Holder<ChunkStore> holder = ChunkStore.REGISTRY.newHolder();
               holder.putComponent(BlockModule.BlockStateInfo.getComponentType(), new BlockModule.BlockStateInfo(blockIndex, chunkReference));
               holder.ensureComponent(RespawnBlock.getComponentType());
               blockRef = chunkStore.addEntity(holder, AddReason.SPAWN);
            }

            RespawnBlock respawnBlock = chunkStore.getComponent(blockRef, RespawnBlock.getComponentType());
            if (respawnBlock != null) {
               UUID ownerUUID = respawnBlock.getOwnerUUID();
               PageManager pageManager = player.getPageManager();
               boolean isOwner = playerUuid.equals(ownerUUID);
               if (isOwner) {
                  BlockPosition rawTarget = context.getMetaStore().getMetaObject(TARGET_BLOCK_RAW);
                  Vector3f whereWasHit = new Vector3f(rawTarget.x + 0.5F, rawTarget.y + 0.5F, rawTarget.z + 0.5F);
                  BlockMountAPI.BlockMountResult result = BlockMountAPI.mountOnBlock(ref, commandBuffer, pos, whereWasHit);
                  if (result instanceof BlockMountAPI.DidNotMount) {
                     player.sendMessage(Message.translation("server.interactions.didNotMount").param("state", result.toString()));
                  } else if (result instanceof BlockMountAPI.Mounted) {
                     commandBuffer.putComponent(ref, PlayerSomnolence.getComponentType(), PlayerSleep.NoddingOff.createComponent());
                  }
               } else if (ownerUUID != null) {
                  player.sendMessage(Message.translation("server.customUI.respawnPointClaimed"));
               } else {
                  PlayerRespawnPointData[] respawnPoints = player.getPlayerConfigData().getPerWorldData(world.getName()).getRespawnPoints();
                  RespawnConfig respawnConfig = world.getGameplayConfig().getRespawnConfig();
                  int radiusLimitRespawnPoint = respawnConfig.getRadiusLimitRespawnPoint();
                  PlayerRespawnPointData[] nearbyRespawnPoints = this.getNearbySavedRespawnPoints(pos, respawnBlock, respawnPoints, radiusLimitRespawnPoint);
                  if (nearbyRespawnPoints != null) {
                     pageManager.openCustomPage(
                        ref,
                        store,
                        new OverrideNearbyRespawnPointPage(playerRefComponent, type, pos, respawnBlock, nearbyRespawnPoints, radiusLimitRespawnPoint)
                     );
                  } else if (respawnPoints != null && respawnPoints.length >= respawnConfig.getMaxRespawnPointsPerPlayer()) {
                     pageManager.openCustomPage(ref, store, new SelectOverrideRespawnPointPage(playerRefComponent, type, pos, respawnBlock, respawnPoints));
                  } else {
                     pageManager.openCustomPage(ref, store, new SetNameRespawnPointPage(playerRefComponent, type, pos, respawnBlock));
                  }
               }
            }
         }
      }
   }

   @Override
   protected void simulateInteractWithBlock(
      @NonNullDecl InteractionType type,
      @NonNullDecl InteractionContext context,
      @NullableDecl ItemStack itemInHand,
      @NonNullDecl World world,
      @NonNullDecl Vector3i targetBlock
   ) {
   }

   @Nullable
   private PlayerRespawnPointData[] getNearbySavedRespawnPoints(
      @Nonnull Vector3i currentRespawnPointPosition,
      @Nonnull RespawnBlock respawnBlock,
      @Nullable PlayerRespawnPointData[] respawnPoints,
      int radiusLimitRespawnPoint
   ) {
      if (respawnPoints != null && respawnPoints.length != 0) {
         ObjectArrayList<PlayerRespawnPointData> nearbyRespawnPointList = new ObjectArrayList<>();

         for (int i = 0; i < respawnPoints.length; i++) {
            PlayerRespawnPointData respawnPoint = respawnPoints[i];
            Vector3i respawnPointPosition = respawnPoint.getBlockPosition();
            if (respawnPointPosition.distanceTo(currentRespawnPointPosition.x, respawnPointPosition.y, currentRespawnPointPosition.z) < radiusLimitRespawnPoint
               )
             {
               nearbyRespawnPointList.add(respawnPoint);
            }
         }

         return nearbyRespawnPointList.isEmpty() ? null : nearbyRespawnPointList.toArray(PlayerRespawnPointData[]::new);
      } else {
         return null;
      }
   }

   @NonNullDecl
   @Override
   public String toString() {
      return "BedInteraction{} " + super.toString();
   }
}
