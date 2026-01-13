package com.hypixel.hytale.builtin.adventure.farming.interactions;

import com.hypixel.hytale.builtin.adventure.farming.states.CoopBlock;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.InteractionState;
import com.hypixel.hytale.protocol.InteractionType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.InteractionContext;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.CombinedItemContainer;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.interaction.interaction.CooldownHandler;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.client.SimpleBlockInteraction;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class UseCoopInteraction extends SimpleBlockInteraction {
   public static final BuilderCodec<UseCoopInteraction> CODEC = BuilderCodec.builder(
         UseCoopInteraction.class, UseCoopInteraction::new, SimpleBlockInteraction.CODEC
      )
      .build();

   @Override
   protected void interactWithBlock(
      @NonNullDecl World world,
      @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
      @NonNullDecl InteractionType type,
      @NonNullDecl InteractionContext context,
      @NullableDecl ItemStack itemInHand,
      @NonNullDecl Vector3i targetBlock,
      @NonNullDecl CooldownHandler cooldownHandler
   ) {
      int x = targetBlock.getX();
      int z = targetBlock.getZ();
      WorldChunk worldChunk = world.getChunk(ChunkUtil.indexChunkFromBlock(x, z));
      if (worldChunk == null) {
         context.getState().state = InteractionState.Failed;
      } else {
         Ref<ChunkStore> blockRef = worldChunk.getBlockComponentEntity(x, targetBlock.getY(), z);
         if (blockRef == null) {
            blockRef = BlockModule.ensureBlockEntity(worldChunk, targetBlock.x, targetBlock.y, targetBlock.z);
         }

         if (blockRef == null) {
            context.getState().state = InteractionState.Failed;
         } else {
            Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
            CoopBlock coopBlockState = chunkStore.getComponent(blockRef, CoopBlock.getComponentType());
            if (coopBlockState == null) {
               context.getState().state = InteractionState.Failed;
            } else {
               Ref<EntityStore> playerRef = context.getEntity();
               LivingEntity playerEntity = (LivingEntity)EntityUtils.getEntity(playerRef, commandBuffer);
               if (playerEntity == null) {
                  context.getState().state = InteractionState.Failed;
               } else {
                  CombinedItemContainer playerInventoryContainer = playerEntity.getInventory().getCombinedHotbarFirst();
                  if (playerInventoryContainer != null) {
                     coopBlockState.gatherProduceFromInventory(playerInventoryContainer);
                     BlockType currentBlockType = worldChunk.getBlockType(targetBlock);

                     assert currentBlockType != null;

                     worldChunk.setBlockInteractionState(targetBlock, currentBlockType, coopBlockState.hasProduce() ? "Produce_Ready" : "default");
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
}
