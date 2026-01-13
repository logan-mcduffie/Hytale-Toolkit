package com.hypixel.hytale.builtin.adventure.farming.config.stages;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.map.BlockTypeAssetMap;
import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlock;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.Validators;
import com.hypixel.hytale.common.map.IWeightedElement;
import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.util.MathUtil;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.BlockMaterial;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.Rotation;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.RotationTuple;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingStageData;
import com.hypixel.hytale.server.core.codec.WeightedMapCodec;
import com.hypixel.hytale.server.core.prefab.PrefabRotation;
import com.hypixel.hytale.server.core.prefab.PrefabStore;
import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferCall;
import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabBufferUtil;
import com.hypixel.hytale.server.core.prefab.selection.buffer.impl.IPrefabBuffer;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.accessor.LocalCachedChunkAccessor;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.util.PrefabUtil;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabFarmingStageData extends FarmingStageData {
   public static final float MIN_VOLUME_PREFAB = 125.0F;
   public static final float MAX_VOLUME_PREFAB = 1000.0F;
   public static final float MIN_BROKEN_PARTICLE_RATE = 0.25F;
   public static final float MAX_BROKEN_PARTICLE_RATE = 0.75F;
   private static final String[] EMPTY_REPLACE_MASK = new String[0];
   @Nonnull
   public static BuilderCodec<PrefabFarmingStageData> CODEC = BuilderCodec.builder(
         PrefabFarmingStageData.class, PrefabFarmingStageData::new, FarmingStageData.BASE_CODEC
      )
      .append(
         new KeyedCodec<>("Prefabs", new WeightedMapCodec<>(PrefabFarmingStageData.PrefabStage.CODEC, PrefabFarmingStageData.PrefabStage.EMPTY_ARRAY)),
         (stage, prefabStages) -> stage.prefabStages = prefabStages,
         stage -> stage.prefabStages
      )
      .add()
      .append(
         new KeyedCodec<>("ReplaceMaskTags", new ArrayCodec<>(Codec.STRING, String[]::new)),
         (stage, replaceMask) -> stage.replaceMaskTags = replaceMask,
         stage -> stage.replaceMaskTags
      )
      .add()
      .afterDecode(PrefabFarmingStageData::processConfig)
      .build();
   protected IWeightedMap<PrefabFarmingStageData.PrefabStage> prefabStages;
   private String[] replaceMaskTags = EMPTY_REPLACE_MASK;
   private int[] replaceMaskTagIndices;

   private static double computeParticlesRate(@Nonnull IPrefabBuffer prefab) {
      double xLength = prefab.getMaxX() - prefab.getMinX();
      double yLength = prefab.getMaxY() - prefab.getMinY();
      double zLength = prefab.getMaxZ() - prefab.getMinZ();
      double volume = xLength * yLength * zLength;
      double ratio = -5.7142857E-4F;
      double rate = (volume - 125.0) * ratio;
      return MathUtil.clamp(rate + 0.75, 0.25, 0.75);
   }

   private static boolean isPrefabBlockIntact(
      LocalCachedChunkAccessor chunkAccessor,
      int worldX,
      int worldY,
      int worldZ,
      int blockX,
      int blockY,
      int blockZ,
      int blockId,
      int rotation,
      PrefabRotation prefabRotation
   ) {
      int globalX = prefabRotation.getX(blockX, blockZ) + worldX;
      int globalY = blockY + worldY;
      int globalZ = prefabRotation.getZ(blockX, blockZ) + worldZ;
      BlockType block = BlockType.getAssetMap().getAsset(blockId);
      if (block.getMaterial() == BlockMaterial.Empty) {
         return true;
      } else {
         WorldChunk chunk = chunkAccessor.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(globalX, globalZ));
         if (chunk == null) {
            return false;
         } else {
            int worldBlockId = chunk.getBlock(globalX, globalY, globalZ);
            if (worldBlockId != blockId) {
               return false;
            } else {
               int expectedRotation = prefabRotation.getRotation(rotation);
               int worldRotation = chunk.getRotationIndex(globalX, globalY, globalZ);
               return worldRotation == expectedRotation;
            }
         }
      }
   }

   private static boolean isPrefabIntact(
      IPrefabBuffer prefabBuffer, LocalCachedChunkAccessor chunkAccessor, int worldX, int worldY, int worldZ, PrefabRotation prefabRotation, FastRandom random
   ) {
      return prefabBuffer.forEachRaw(
         IPrefabBuffer.iterateAllColumns(),
         (blockX, blockY, blockZ, blockId, chance, holder, supportValue, rotation, filler, t) -> isPrefabBlockIntact(
            chunkAccessor, worldX, worldY, worldZ, blockX, blockY, blockZ, blockId, rotation, prefabRotation
         ),
         (fluidX, fluidY, fluidZ, fluidId, level, o) -> true,
         null,
         new PrefabBufferCall(random, prefabRotation)
      );
   }

   public IWeightedMap<PrefabFarmingStageData.PrefabStage> getPrefabStages() {
      return this.prefabStages;
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
      FarmingBlock farming = commandBuffer.getComponent(blockRef, FarmingBlock.getComponentType());
      IPrefabBuffer prefabBuffer = this.getCachedPrefab(x, y, z, farming.getGeneration());
      BlockSection blockSection = commandBuffer.getComponent(sectionRef, BlockSection.getComponentType());
      int randomRotation = HashUtil.randomInt(x, y, z, Rotation.VALUES.length);
      RotationTuple yaw = RotationTuple.of(Rotation.VALUES[randomRotation], Rotation.None);
      World world = commandBuffer.getExternalData().getWorld();
      ChunkSection section = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
      int worldX = ChunkUtil.worldCoordFromLocalCoord(section.getX(), x);
      int worldY = ChunkUtil.worldCoordFromLocalCoord(section.getY(), y);
      int worldZ = ChunkUtil.worldCoordFromLocalCoord(section.getZ(), z);
      if (farming.getPreviousBlockType() == null) {
         farming.setPreviousBlockType(BlockType.getAssetMap().getAsset(blockSection.get(x, y, z)).getId());
      }

      double xLength = prefabBuffer.getMaxX() - prefabBuffer.getMinX();
      double zLength = prefabBuffer.getMaxZ() - prefabBuffer.getMinZ();
      int prefabRadius = (int)MathUtil.fastFloor(0.5 * Math.sqrt(xLength * xLength + zLength * zLength));
      LocalCachedChunkAccessor chunkAccessor = LocalCachedChunkAccessor.atWorldCoords(world, x, z, prefabRadius);
      FastRandom random = new FastRandom();
      PrefabRotation prefabRotation = PrefabRotation.fromRotation(yaw.yaw());
      BlockTypeAssetMap<String, BlockType> blockTypeMap = BlockType.getAssetMap();
      if (previousStage instanceof PrefabFarmingStageData oldPrefab) {
         IPrefabBuffer oldPrefabBuffer = oldPrefab.getCachedPrefab(worldX, worldY, worldZ, farming.getGeneration() - 1);
         double brokenParticlesRate = computeParticlesRate(prefabBuffer);
         world.execute(
            () -> {
               boolean isIntact = isPrefabIntact(oldPrefabBuffer, chunkAccessor, worldX, worldY, worldZ, prefabRotation, random);
               if (isIntact) {
                  boolean isUnobstructed = prefabBuffer.compare(
                     (px, py, pz, blockId, stateWrapper, chance, rotation, filler, secondBlockId, secondStateWrapper, secondChance, secondRotation, secondFiller, prefabBufferCall) -> {
                        int bx = worldX + px;
                        int by = worldY + py;
                        int bz = worldZ + pz;
                        if ((secondBlockId == 0 || secondBlockId == Integer.MIN_VALUE) && blockId != 0 && blockId != Integer.MIN_VALUE) {
                           WorldChunk nonTickingChunk = chunkAccessor.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(bx, bz));
                           int worldBlock = nonTickingChunk.getBlock(bx, by, bz);
                           return !this.doesBlockObstruct(blockId, worldBlock);
                        } else {
                           return true;
                        }
                     },
                     new PrefabBufferCall(random, prefabRotation),
                     oldPrefabBuffer
                  );
                  if (isUnobstructed) {
                     prefabBuffer.compare(
                        (px, py, pz, blockId, stateWrapper, chance, rotation, filler, secondBlockId, secondStateWrapper, secondChance, secondRotation, secondFiller, prefabBufferCall) -> {
                           int bx = worldX + px;
                           int by = worldY + py;
                           int bz = worldZ + pz;
                           WorldChunk nonTickingChunk = chunkAccessor.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(bx, bz));
                           int updatedSetBlockSettings = 2;
                           if (random.nextDouble() > brokenParticlesRate) {
                              updatedSetBlockSettings |= 4;
                           }

                           if (blockId != 0 && blockId != Integer.MIN_VALUE) {
                              BlockType block = blockTypeMap.getAsset(blockId);
                              if (filler != 0) {
                                 return true;
                              }

                              int worldBlock = nonTickingChunk.getBlock(bx, by, bz);
                              if ((secondBlockId == 0 || secondBlockId == Integer.MIN_VALUE) && !this.canReplace(worldBlock, blockTypeMap)) {
                                 return true;
                              }

                              nonTickingChunk.setBlock(bx, by, bz, blockId, block, rotation, filler, updatedSetBlockSettings);
                              if (stateWrapper != null) {
                                 nonTickingChunk.setState(bx, by, bz, stateWrapper.clone());
                              }
                           } else if (secondBlockId != 0 && secondBlockId != Integer.MIN_VALUE) {
                              nonTickingChunk.breakBlock(bx, by, bz, updatedSetBlockSettings);
                           }

                           return true;
                        },
                        new PrefabBufferCall(random, prefabRotation),
                        oldPrefabBuffer
                     );
                  }
               }
            }
         );
      } else {
         super.apply(commandBuffer, sectionRef, blockRef, x, y, z, previousStage);
         world.execute(
            () -> {
               boolean isUnObstructed = prefabBuffer.forEachRaw(
                  IPrefabBuffer.iterateAllColumns(), (blockX, blockY, blockZ, blockId, chance, holder, supportValue, rotation, filler, t) -> {
                     int bx = worldX + prefabRotation.getX(blockX, blockZ);
                     int by = worldY + blockY;
                     int bz = worldZ + prefabRotation.getX(blockZ, blockX);
                     if (blockId != 0 && blockId != Integer.MIN_VALUE) {
                        int worldBlock = chunkAccessor.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(bx, bz)).getBlock(bx, by, bz);
                        return !this.doesBlockObstruct(blockId, worldBlock);
                     } else {
                        return true;
                     }
                  }, (fluidX, fluidY, fluidZ, fluidId, level, o) -> true, null, new PrefabBufferCall(random, prefabRotation)
               );
               if (isUnObstructed) {
                  prefabBuffer.forEach(
                     IPrefabBuffer.iterateAllColumns(), (blockX, blockY, blockZ, blockId, holder, supportValue, rotation, filler, t, fluidId, fluidLevel) -> {
                        int bx = worldX + blockX;
                        int by = worldY + blockY;
                        int bz = worldZ + blockZ;
                        WorldChunk nonTickingChunk = chunkAccessor.getNonTickingChunk(ChunkUtil.indexChunkFromBlock(bx, bz));
                        int updatedSetBlockSettings = 2;
                        if (blockId != 0 && blockId != Integer.MIN_VALUE) {
                           BlockType block = blockTypeMap.getAsset(blockId);
                           if (filler != 0) {
                              return;
                           }

                           int worldBlock = nonTickingChunk.getBlock(bx, by, bz);
                           if (!this.canReplace(worldBlock, blockTypeMap)) {
                              return;
                           }

                           nonTickingChunk.setBlock(bx, by, bz, blockId, block, rotation, filler, updatedSetBlockSettings);
                           if (holder != null) {
                              nonTickingChunk.setState(bx, by, bz, holder.clone());
                           }
                        }
                     }, null, null, new PrefabBufferCall(random, prefabRotation)
                  );
               }
            }
         );
      }
   }

   private boolean doesBlockObstruct(int blockId, int worldBlockId) {
      BlockTypeAssetMap<String, BlockType> assetMap = BlockType.getAssetMap();
      BlockType blockType = assetMap.getAsset(blockId);
      return blockType != null && blockType.getMaterial() != BlockMaterial.Empty ? !this.canReplace(worldBlockId, assetMap) : false;
   }

   private boolean canReplace(int worldBlockId, BlockTypeAssetMap<String, BlockType> assetMap) {
      BlockType worldBlockType = assetMap.getAsset(worldBlockId);
      if (worldBlockType != null && worldBlockType.getMaterial() != BlockMaterial.Empty) {
         for (int tagIndex : this.replaceMaskTagIndices) {
            if (assetMap.getIndexesForTag(tagIndex).contains(worldBlockId)) {
               return true;
            }
         }

         return false;
      } else {
         return true;
      }
   }

   @Override
   public void remove(ComponentAccessor<ChunkStore> commandBuffer, Ref<ChunkStore> sectionRef, Ref<ChunkStore> blockRef, int x, int y, int z) {
      super.remove(commandBuffer, sectionRef, blockRef, x, y, z);
      ChunkSection section = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
      int worldX = ChunkUtil.worldCoordFromLocalCoord(section.getX(), x);
      int worldY = ChunkUtil.worldCoordFromLocalCoord(section.getY(), y);
      int worldZ = ChunkUtil.worldCoordFromLocalCoord(section.getZ(), z);
      FarmingBlock farming = commandBuffer.getComponent(blockRef, FarmingBlock.getComponentType());
      IPrefabBuffer prefab = this.getCachedPrefab(worldX, worldY, worldZ, farming.getGeneration() - 1);
      RotationTuple rotation = commandBuffer.getComponent(sectionRef, BlockSection.getComponentType()).getRotation(x, y, z);
      double rate = computeParticlesRate(prefab);
      World world = commandBuffer.getExternalData().getWorld();
      world.execute(() -> PrefabUtil.remove(prefab, world, new Vector3i(worldX, worldY, worldZ), rotation.yaw(), true, new FastRandom(), 2, rate));
   }

   @Nonnull
   private IPrefabBuffer getCachedPrefab(int x, int y, int z, int generation) {
      return PrefabBufferUtil.getCached(this.prefabStages.get(HashUtil.random(x, y, z, generation)).getResolvedPath());
   }

   private void processConfig() {
      this.replaceMaskTagIndices = new int[this.replaceMaskTags.length];

      for (int i = 0; i < this.replaceMaskTags.length; i++) {
         this.replaceMaskTagIndices[i] = AssetRegistry.getOrCreateTagIndex(this.replaceMaskTags[i]);
      }
   }

   @Override
   public String toString() {
      return "PrefabFarmingStageData{replaceMaskTags=" + Arrays.toString((Object[])this.replaceMaskTags) + ", prefabStages=" + this.prefabStages + "}";
   }

   public static class PrefabStage implements IWeightedElement {
      public static final PrefabFarmingStageData.PrefabStage[] EMPTY_ARRAY = new PrefabFarmingStageData.PrefabStage[0];
      @Nonnull
      public static Codec<PrefabFarmingStageData.PrefabStage> CODEC = BuilderCodec.builder(
            PrefabFarmingStageData.PrefabStage.class, PrefabFarmingStageData.PrefabStage::new
         )
         .append(new KeyedCodec<>("Weight", Codec.INTEGER), (prefabStage, integer) -> prefabStage.weight = integer, prefabStage -> prefabStage.weight)
         .addValidator(Validators.greaterThanOrEqual(1))
         .add()
         .<String>append(new KeyedCodec<>("Path", Codec.STRING), (prefabStage, s) -> prefabStage.path = s, prefabStage -> prefabStage.path)
         .addValidator(Validators.nonNull())
         .add()
         .build();
      protected int weight = 1;
      protected String path;

      @Override
      public double getWeight() {
         return this.weight;
      }

      @Nonnull
      public Path getResolvedPath() {
         for (AssetPack pack : AssetModule.get().getAssetPacks()) {
            Path assetPath = pack.getRoot().resolve("Server").resolve("Prefabs").resolve(this.path);
            if (Files.exists(assetPath)) {
               return assetPath;
            }
         }

         return PrefabStore.get().getAssetPrefabsPath().resolve(this.path);
      }

      @Nonnull
      @Override
      public String toString() {
         return "PrefabStage{weight=" + this.weight + ", path='" + this.path + "'}";
      }
   }
}
