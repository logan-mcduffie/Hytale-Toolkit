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

public class BlockTypeFarmingStageData extends FarmingStageData {
   @Nonnull
   public static BuilderCodec<BlockTypeFarmingStageData> CODEC = BuilderCodec.builder(
         BlockTypeFarmingStageData.class, BlockTypeFarmingStageData::new, FarmingStageData.BASE_CODEC
      )
      .append(new KeyedCodec<>("Block", Codec.STRING), (stage, block) -> stage.block = block, stage -> stage.block)
      .addValidatorLate(() -> BlockType.VALIDATOR_CACHE.getValidator().late())
      .add()
      .build();
   protected String block;

   public String getBlock() {
      return this.block;
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
      int blockId = BlockType.getAssetMap().getIndex(this.block);
      if (blockId != worldChunk.getBlock(x, y, z)) {
         BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
         commandBuffer.getExternalData().getWorld().execute(() -> worldChunk.setBlock(x, y, z, blockId, blockType, worldChunk.getRotationIndex(x, y, z), 0, 2));
      }
   }

   @Nonnull
   @Override
   public String toString() {
      return "BlockTypeFarmingStageData{block=" + this.block + "} " + super.toString();
   }
}
