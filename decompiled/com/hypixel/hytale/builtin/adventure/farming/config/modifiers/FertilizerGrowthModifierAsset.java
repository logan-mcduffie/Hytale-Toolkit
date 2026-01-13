package com.hypixel.hytale.builtin.adventure.farming.config.modifiers;

import com.hypixel.hytale.builtin.adventure.farming.states.TilledSoilBlock;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.GrowthModifierAsset;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;

public class FertilizerGrowthModifierAsset extends GrowthModifierAsset {
   public static final BuilderCodec<FertilizerGrowthModifierAsset> CODEC = BuilderCodec.builder(
         FertilizerGrowthModifierAsset.class, FertilizerGrowthModifierAsset::new, ABSTRACT_CODEC
      )
      .build();

   @Override
   public double getCurrentGrowthMultiplier(
      CommandBuffer<ChunkStore> commandBuffer, Ref<ChunkStore> sectionRef, Ref<ChunkStore> blockRef, int x, int y, int z, boolean initialTick
   ) {
      ChunkSection chunkSection = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
      Ref<ChunkStore> chunk = chunkSection.getChunkColumnReference();
      BlockComponentChunk blockComponentChunk = commandBuffer.getComponent(chunk, BlockComponentChunk.getComponentType());
      Ref<ChunkStore> blockRefBelow = blockComponentChunk.getEntityReference(ChunkUtil.indexBlockInColumn(x, y - 1, z));
      if (blockRefBelow == null) {
         return 1.0;
      } else {
         TilledSoilBlock soil = commandBuffer.getComponent(blockRefBelow, TilledSoilBlock.getComponentType());
         return soil != null && soil.isFertilized() ? super.getCurrentGrowthMultiplier(commandBuffer, sectionRef, blockRef, x, y, z, initialTick) : 1.0;
      }
   }
}
