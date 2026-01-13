package com.hypixel.hytale.builtin.adventure.farming.config.stages;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingStageData;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockStateFarmingStageData extends FarmingStageData {
   @Nonnull
   public static BuilderCodec<BlockStateFarmingStageData> CODEC = BuilderCodec.builder(
         BlockStateFarmingStageData.class, BlockStateFarmingStageData::new, FarmingStageData.BASE_CODEC
      )
      .append(new KeyedCodec<>("State", Codec.STRING), (stage, block) -> stage.state = block, stage -> stage.state)
      .add()
      .build();
   protected String state;

   public String getState() {
      return this.state;
   }

   @Override
   public void apply(
      ComponentAccessor<ChunkStore> commandBuffer,
      Ref<ChunkStore> sectionRef,
      Ref<ChunkStore> blockRef,
      int x,
      int y,
      int z,
      @Nullable FarmingStageData previousStage
   ) {
      super.apply(commandBuffer, sectionRef, blockRef, x, y, z, previousStage);
      ChunkSection section = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
      WorldChunk worldChunk = commandBuffer.getComponent(section.getChunkColumnReference(), WorldChunk.getComponentType());
      int origBlockId = worldChunk.getBlock(x, y, z);
      BlockType origBlockType = BlockType.getAssetMap().getAsset(origBlockId);
      BlockType blockType = origBlockType.getBlockForState(this.state);
      if (blockType != null) {
         int newType = BlockType.getAssetMap().getIndex(blockType.getId());
         if (origBlockId != newType) {
            int rotation = worldChunk.getRotationIndex(x, y, z);
            commandBuffer.getExternalData().getWorld().execute(() -> worldChunk.setBlock(x, y, z, newType, blockType, rotation, 0, 2));
         }
      }
   }

   @Nonnull
   @Override
   public String toString() {
      return "BlockStateFarmingStageData{state='" + this.state + "'} " + super.toString();
   }
}
