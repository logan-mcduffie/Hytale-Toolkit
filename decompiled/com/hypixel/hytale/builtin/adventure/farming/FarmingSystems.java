package com.hypixel.hytale.builtin.adventure.farming;

import com.hypixel.hytale.builtin.adventure.farming.component.CoopResidentComponent;
import com.hypixel.hytale.builtin.adventure.farming.config.FarmingCoopAsset;
import com.hypixel.hytale.builtin.adventure.farming.config.stages.BlockStateFarmingStageData;
import com.hypixel.hytale.builtin.adventure.farming.config.stages.BlockTypeFarmingStageData;
import com.hypixel.hytale.builtin.adventure.farming.states.CoopBlock;
import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlock;
import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlockState;
import com.hypixel.hytale.builtin.adventure.farming.states.TilledSoilBlock;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.Rangef;
import com.hypixel.hytale.server.core.asset.type.blocktick.BlockTickStrategy;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingData;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingStageData;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.ChunkColumn;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.TargetUtil;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class FarmingSystems {
   private static boolean updateSoilDecayTime(CommandBuffer<ChunkStore> commandBuffer, TilledSoilBlock soilBlock, BlockType blockType) {
      if (blockType != null && blockType.getFarming() != null && blockType.getFarming().getSoilConfig() != null) {
         FarmingData.SoilConfig soilConfig = blockType.getFarming().getSoilConfig();
         Rangef range = soilConfig.getLifetime();
         if (range == null) {
            return false;
         } else {
            double baseDuration = range.min + (range.max - range.min) * ThreadLocalRandom.current().nextDouble();
            Instant currentTime = commandBuffer.getExternalData()
               .getWorld()
               .getEntityStore()
               .getStore()
               .getResource(WorldTimeResource.getResourceType())
               .getGameTime();
            Instant endTime = currentTime.plus(Math.round(baseDuration), ChronoUnit.SECONDS);
            soilBlock.setDecayTime(endTime);
            return true;
         }
      } else {
         return false;
      }
   }

   public static class CoopResidentEntitySystem extends RefSystem<EntityStore> {
      private static final ComponentType<EntityStore, CoopResidentComponent> componentType = CoopResidentComponent.getComponentType();

      @Override
      public Query<EntityStore> getQuery() {
         return componentType;
      }

      @Override
      public void onEntityAdded(
         @NonNullDecl Ref<EntityStore> ref,
         @NonNullDecl AddReason reason,
         @NonNullDecl Store<EntityStore> store,
         @NonNullDecl CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      @Override
      public void onEntityRemove(
         @NonNullDecl Ref<EntityStore> ref,
         @NonNullDecl RemoveReason reason,
         @NonNullDecl Store<EntityStore> store,
         @NonNullDecl CommandBuffer<EntityStore> commandBuffer
      ) {
         if (reason != RemoveReason.UNLOAD) {
            UUIDComponent uuidComponent = commandBuffer.getComponent(ref, UUIDComponent.getComponentType());
            if (uuidComponent != null) {
               UUID uuid = uuidComponent.getUuid();
               CoopResidentComponent coopResidentComponent = commandBuffer.getComponent(ref, componentType);
               if (coopResidentComponent != null) {
                  Vector3i coopPosition = coopResidentComponent.getCoopLocation();
                  World world = commandBuffer.getExternalData().getWorld();
                  long chunkIndex = ChunkUtil.indexChunkFromBlock(coopPosition.x, coopPosition.z);
                  WorldChunk chunk = world.getChunkIfLoaded(chunkIndex);
                  if (chunk != null) {
                     Ref<ChunkStore> chunkReference = world.getChunkStore().getChunkReference(chunkIndex);
                     if (chunkReference != null && chunkReference.isValid()) {
                        Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
                        ChunkColumn chunkColumnComponent = chunkStore.getComponent(chunkReference, ChunkColumn.getComponentType());
                        if (chunkColumnComponent != null) {
                           BlockChunk blockChunkComponent = chunkStore.getComponent(chunkReference, BlockChunk.getComponentType());
                           if (blockChunkComponent != null) {
                              Ref<ChunkStore> sectionRef = chunkColumnComponent.getSection(ChunkUtil.chunkCoordinate(coopPosition.y));
                              if (sectionRef != null && sectionRef.isValid()) {
                                 BlockComponentChunk blockComponentChunk = chunkStore.getComponent(chunkReference, BlockComponentChunk.getComponentType());
                                 if (blockComponentChunk != null) {
                                    int blockIndexColumn = ChunkUtil.indexBlockInColumn(coopPosition.x, coopPosition.y, coopPosition.z);
                                    Ref<ChunkStore> coopEntityReference = blockComponentChunk.getEntityReference(blockIndexColumn);
                                    if (coopEntityReference != null) {
                                       CoopBlock coop = chunkStore.getComponent(coopEntityReference, CoopBlock.getComponentType());
                                       if (coop != null) {
                                          coop.handleResidentDespawn(uuid);
                                       }
                                    }
                                 }
                              }
                           }
                        }
                     }
                  }
               }
            }
         }
      }
   }

   public static class CoopResidentTicking extends EntityTickingSystem<EntityStore> {
      private static final ComponentType<EntityStore, CoopResidentComponent> componentType = CoopResidentComponent.getComponentType();

      @Override
      public Query<EntityStore> getQuery() {
         return componentType;
      }

      @Override
      public void tick(
         float dt,
         int index,
         @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
         @NonNullDecl Store<EntityStore> store,
         @NonNullDecl CommandBuffer<EntityStore> commandBuffer
      ) {
         CoopResidentComponent coopResidentComponent = archetypeChunk.getComponent(index, CoopResidentComponent.getComponentType());
         if (coopResidentComponent != null) {
            if (coopResidentComponent.getMarkedForDespawn()) {
               commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
            }
         }
      }
   }

   @Deprecated(forRemoval = true)
   public static class MigrateFarming extends BlockModule.MigrationSystem {
      @Override
      public void onEntityAdd(@Nonnull Holder<ChunkStore> holder, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store) {
         FarmingBlockState oldState = holder.getComponent(FarmingPlugin.get().getFarmingBlockStateComponentType());
         FarmingBlock farming = new FarmingBlock();
         farming.setGrowthProgress(oldState.getCurrentFarmingStageIndex());
         farming.setCurrentStageSet(oldState.getCurrentFarmingStageSetName());
         farming.setSpreadRate(oldState.getSpreadRate());
         holder.putComponent(FarmingBlock.getComponentType(), farming);
         holder.removeComponent(FarmingPlugin.get().getFarmingBlockStateComponentType());
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<ChunkStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store) {
      }

      @Nullable
      @Override
      public Query<ChunkStore> getQuery() {
         return FarmingPlugin.get().getFarmingBlockStateComponentType();
      }
   }

   public static class OnCoopAdded extends RefSystem<ChunkStore> {
      private static final Query<ChunkStore> QUERY = Query.and(BlockModule.BlockStateInfo.getComponentType(), CoopBlock.getComponentType());

      @Override
      public void onEntityAdded(
         @NonNullDecl Ref<ChunkStore> ref,
         @NonNullDecl AddReason reason,
         @NonNullDecl Store<ChunkStore> store,
         @NonNullDecl CommandBuffer<ChunkStore> commandBuffer
      ) {
         CoopBlock coopBlock = commandBuffer.getComponent(ref, CoopBlock.getComponentType());
         if (coopBlock != null) {
            WorldTimeResource worldTimeResource = commandBuffer.getExternalData()
               .getWorld()
               .getEntityStore()
               .getStore()
               .getResource(WorldTimeResource.getResourceType());
            BlockModule.BlockStateInfo info = commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());

            assert info != null;

            int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
            int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
            int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
            BlockChunk blockChunk = commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());

            assert blockChunk != null;

            BlockSection blockSection = blockChunk.getSectionAtBlockY(y);
            blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), coopBlock.getNextScheduledTick(worldTimeResource));
         }
      }

      @Override
      public void onEntityRemove(
         @NonNullDecl Ref<ChunkStore> ref,
         @NonNullDecl RemoveReason reason,
         @NonNullDecl Store<ChunkStore> store,
         @NonNullDecl CommandBuffer<ChunkStore> commandBuffer
      ) {
         if (reason != RemoveReason.UNLOAD) {
            CoopBlock coop = commandBuffer.getComponent(ref, CoopBlock.getComponentType());
            if (coop != null) {
               BlockModule.BlockStateInfo info = commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());

               assert info != null;

               Store<EntityStore> entityStore = commandBuffer.getExternalData().getWorld().getEntityStore().getStore();
               int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
               int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
               int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
               BlockChunk blockChunk = commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());

               assert blockChunk != null;

               ChunkColumn column = commandBuffer.getComponent(info.getChunkRef(), ChunkColumn.getComponentType());

               assert column != null;

               Ref<ChunkStore> sectionRef = column.getSection(ChunkUtil.chunkCoordinate(y));

               assert sectionRef != null;

               BlockSection blockSection = commandBuffer.getComponent(sectionRef, BlockSection.getComponentType());

               assert blockSection != null;

               ChunkSection chunkSection = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());

               assert chunkSection != null;

               int worldX = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getX(), x);
               int worldY = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getY(), y);
               int worldZ = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getZ(), z);
               World world = commandBuffer.getExternalData().getWorld();
               WorldTimeResource worldTimeResource = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType());
               coop.handleBlockBroken(world, worldTimeResource, entityStore, worldX, worldY, worldZ);
            }
         }
      }

      @NullableDecl
      @Override
      public Query<ChunkStore> getQuery() {
         return QUERY;
      }
   }

   public static class OnFarmBlockAdded extends RefSystem<ChunkStore> {
      private static final Query<ChunkStore> QUERY = Query.and(BlockModule.BlockStateInfo.getComponentType(), FarmingBlock.getComponentType());

      @Override
      public void onEntityAdded(
         @Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         FarmingBlock farmingBlock = commandBuffer.getComponent(ref, FarmingBlock.getComponentType());

         assert farmingBlock != null;

         BlockModule.BlockStateInfo info = commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());

         assert info != null;

         if (farmingBlock.getLastTickGameTime() == null) {
            BlockChunk blockChunk = commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());
            int blockId = blockChunk.getBlock(
               ChunkUtil.xFromBlockInColumn(info.getIndex()), ChunkUtil.yFromBlockInColumn(info.getIndex()), ChunkUtil.zFromBlockInColumn(info.getIndex())
            );
            BlockType blockType = BlockType.getAssetMap().getAsset(blockId);
            if (blockType.getFarming() == null) {
               return;
            }

            farmingBlock.setCurrentStageSet(blockType.getFarming().getStartingStageSet());
            farmingBlock.setLastTickGameTime(
               store.getExternalData().getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType()).getGameTime()
            );
            if (blockType.getFarming().getStages() != null) {
               FarmingStageData[] stages = blockType.getFarming().getStages().get(blockType.getFarming().getStartingStageSet());
               if (stages != null && stages.length > 0) {
                  boolean found = false;

                  for (int i = 0; i < stages.length; i++) {
                     FarmingStageData stage = stages[i];
                     switch (stage) {
                        case BlockTypeFarmingStageData data:
                           if (data.getBlock().equals(blockType.getId())) {
                              farmingBlock.setGrowthProgress(i);
                              found = true;
                           }
                           break;
                        case BlockStateFarmingStageData datax:
                           BlockType stateBlockType = blockType.getBlockForState(datax.getState());
                           if (stateBlockType != null && stateBlockType.getId().equals(blockType.getId())) {
                              farmingBlock.setGrowthProgress(i);
                              found = true;
                           }
                           break;
                        default:
                     }
                  }

                  if (!found) {
                     Ref<ChunkStore> sectionRef = commandBuffer.getComponent(info.getChunkRef(), ChunkColumn.getComponentType())
                        .getSection(ChunkUtil.chunkCoordinate(ChunkUtil.yFromBlockInColumn(info.getIndex())));
                     stages[0]
                        .apply(
                           commandBuffer,
                           sectionRef,
                           ref,
                           ChunkUtil.xFromBlockInColumn(info.getIndex()),
                           ChunkUtil.yFromBlockInColumn(info.getIndex()),
                           ChunkUtil.zFromBlockInColumn(info.getIndex()),
                           null
                        );
                  }
               }
            }
         }

         if (farmingBlock.getLastTickGameTime() == null) {
            farmingBlock.setLastTickGameTime(
               store.getExternalData().getWorld().getEntityStore().getStore().getResource(WorldTimeResource.getResourceType()).getGameTime()
            );
         }

         int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
         int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
         int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
         BlockComponentChunk blockComponentChunk = commandBuffer.getComponent(info.getChunkRef(), BlockComponentChunk.getComponentType());

         assert blockComponentChunk != null;

         ChunkColumn column = commandBuffer.getComponent(info.getChunkRef(), ChunkColumn.getComponentType());

         assert column != null;

         Ref<ChunkStore> section = column.getSection(ChunkUtil.chunkCoordinate(y));
         BlockSection blockSection = commandBuffer.getComponent(section, BlockSection.getComponentType());
         FarmingUtil.tickFarming(commandBuffer, blockSection, section, ref, farmingBlock, x, y, z, true);
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
      }

      @Nullable
      @Override
      public Query<ChunkStore> getQuery() {
         return QUERY;
      }
   }

   public static class OnSoilAdded extends RefSystem<ChunkStore> {
      private static final Query<ChunkStore> QUERY = Query.and(BlockModule.BlockStateInfo.getComponentType(), TilledSoilBlock.getComponentType());

      @Override
      public void onEntityAdded(
         @Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         TilledSoilBlock soil = commandBuffer.getComponent(ref, TilledSoilBlock.getComponentType());

         assert soil != null;

         BlockModule.BlockStateInfo info = commandBuffer.getComponent(ref, BlockModule.BlockStateInfo.getComponentType());

         assert info != null;

         if (!soil.isPlanted()) {
            int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
            int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
            int z = ChunkUtil.zFromBlockInColumn(info.getIndex());

            assert info.getChunkRef() != null;

            BlockChunk blockChunk = commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());

            assert blockChunk != null;

            BlockSection blockSection = blockChunk.getSectionAtBlockY(y);
            Instant decayTime = soil.getDecayTime();
            if (decayTime == null) {
               BlockType blockType = BlockType.getAssetMap().getAsset(blockSection.get(x, y, z));
               FarmingSystems.updateSoilDecayTime(commandBuffer, soil, blockType);
            }

            if (decayTime == null) {
               return;
            }

            blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), decayTime);
         }
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
      }

      @Nullable
      @Override
      public Query<ChunkStore> getQuery() {
         return QUERY;
      }
   }

   public static class Ticking extends EntityTickingSystem<ChunkStore> {
      private static final Query<ChunkStore> QUERY = Query.and(BlockSection.getComponentType(), ChunkSection.getComponentType());

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<ChunkStore> archetypeChunk,
         @Nonnull Store<ChunkStore> store,
         @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         BlockSection blocks = archetypeChunk.getComponent(index, BlockSection.getComponentType());

         assert blocks != null;

         if (blocks.getTickingBlocksCountCopy() != 0) {
            ChunkSection section = archetypeChunk.getComponent(index, ChunkSection.getComponentType());

            assert section != null;

            BlockComponentChunk blockComponentChunk = commandBuffer.getComponent(section.getChunkColumnReference(), BlockComponentChunk.getComponentType());

            assert blockComponentChunk != null;

            Ref<ChunkStore> ref = archetypeChunk.getReferenceTo(index);
            blocks.forEachTicking(
               blockComponentChunk, commandBuffer, section.getY(), (blockComponentChunk1, commandBuffer1, localX, localY, localZ, blockId) -> {
                  Ref<ChunkStore> blockRef = blockComponentChunk1.getEntityReference(ChunkUtil.indexBlockInColumn(localX, localY, localZ));
                  if (blockRef == null) {
                     return BlockTickStrategy.IGNORED;
                  } else {
                     FarmingBlock farming = commandBuffer1.getComponent(blockRef, FarmingBlock.getComponentType());
                     if (farming != null) {
                        FarmingUtil.tickFarming(commandBuffer1, blocks, ref, blockRef, farming, localX, localY, localZ, false);
                        return BlockTickStrategy.SLEEP;
                     } else {
                        TilledSoilBlock soil = commandBuffer1.getComponent(blockRef, TilledSoilBlock.getComponentType());
                        if (soil != null) {
                           tickSoil(commandBuffer1, blockComponentChunk1, blockRef, soil);
                           return BlockTickStrategy.SLEEP;
                        } else {
                           CoopBlock coop = commandBuffer1.getComponent(blockRef, CoopBlock.getComponentType());
                           if (coop != null) {
                              tickCoop(commandBuffer1, blockComponentChunk1, blockRef, coop);
                              return BlockTickStrategy.SLEEP;
                           } else {
                              return BlockTickStrategy.IGNORED;
                           }
                        }
                     }
                  }
               }
            );
         }
      }

      private static void tickSoil(
         CommandBuffer<ChunkStore> commandBuffer, BlockComponentChunk blockComponentChunk, Ref<ChunkStore> blockRef, TilledSoilBlock soilBlock
      ) {
         BlockModule.BlockStateInfo info = commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());

         assert info != null;

         int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
         int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
         int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
         if (y < 320) {
            int checkIndex = ChunkUtil.indexBlockInColumn(x, y + 1, z);
            Ref<ChunkStore> aboveBlockRef = blockComponentChunk.getEntityReference(checkIndex);
            boolean hasCrop = false;
            if (aboveBlockRef != null) {
               FarmingBlock farmingBlock = commandBuffer.getComponent(aboveBlockRef, FarmingBlock.getComponentType());
               hasCrop = farmingBlock != null;
            }

            assert info.getChunkRef() != null;

            BlockChunk blockChunk = commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());

            assert blockChunk != null;

            BlockSection blockSection = blockChunk.getSectionAtBlockY(y);
            BlockType blockType = BlockType.getAssetMap().getAsset(blockSection.get(x, y, z));
            Instant currentTime = commandBuffer.getExternalData()
               .getWorld()
               .getEntityStore()
               .getStore()
               .getResource(WorldTimeResource.getResourceType())
               .getGameTime();
            Instant decayTime = soilBlock.getDecayTime();
            if (soilBlock.isPlanted() && !hasCrop) {
               if (!FarmingSystems.updateSoilDecayTime(commandBuffer, soilBlock, blockType)) {
                  return;
               }

               if (decayTime != null) {
                  blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), decayTime);
               }
            } else if (!soilBlock.isPlanted() && !hasCrop) {
               if (decayTime == null || !decayTime.isAfter(currentTime)) {
                  assert info.getChunkRef() != null;

                  if (blockType != null && blockType.getFarming() != null && blockType.getFarming().getSoilConfig() != null) {
                     FarmingData.SoilConfig soilConfig = blockType.getFarming().getSoilConfig();
                     String targetBlock = soilConfig.getTargetBlock();
                     if (targetBlock == null) {
                        return;
                     } else {
                        int targetBlockId = BlockType.getAssetMap().getIndex(targetBlock);
                        if (targetBlockId == Integer.MIN_VALUE) {
                           return;
                        } else {
                           BlockType targetBlockType = BlockType.getAssetMap().getAsset(targetBlockId);
                           int rotation = blockSection.getRotationIndex(x, y, z);
                           WorldChunk worldChunk = commandBuffer.getComponent(info.getChunkRef(), WorldChunk.getComponentType());
                           commandBuffer.run(_store -> worldChunk.setBlock(x, y, z, targetBlockId, targetBlockType, rotation, 0, 0));
                           return;
                        }
                     }
                  } else {
                     return;
                  }
               }
            } else if (hasCrop) {
               soilBlock.setDecayTime(null);
            }

            String targetBlock = soilBlock.computeBlockType(currentTime, blockType);
            if (targetBlock != null && !targetBlock.equals(blockType.getId())) {
               WorldChunk worldChunk = commandBuffer.getComponent(info.getChunkRef(), WorldChunk.getComponentType());
               int rotation = blockSection.getRotationIndex(x, y, z);
               int targetBlockId = BlockType.getAssetMap().getIndex(targetBlock);
               BlockType targetBlockType = BlockType.getAssetMap().getAsset(targetBlockId);
               commandBuffer.run(_store -> worldChunk.setBlock(x, y, z, targetBlockId, targetBlockType, rotation, 0, 2));
            }

            soilBlock.setPlanted(hasCrop);
         }
      }

      private static void tickCoop(
         CommandBuffer<ChunkStore> commandBuffer, BlockComponentChunk blockComponentChunk, Ref<ChunkStore> blockRef, CoopBlock coopBlock
      ) {
         BlockModule.BlockStateInfo info = commandBuffer.getComponent(blockRef, BlockModule.BlockStateInfo.getComponentType());

         assert info != null;

         Store<EntityStore> store = commandBuffer.getExternalData().getWorld().getEntityStore().getStore();
         WorldTimeResource worldTimeResource = store.getResource(WorldTimeResource.getResourceType());
         FarmingCoopAsset coopAsset = coopBlock.getCoopAsset();
         if (coopAsset != null) {
            int x = ChunkUtil.xFromBlockInColumn(info.getIndex());
            int y = ChunkUtil.yFromBlockInColumn(info.getIndex());
            int z = ChunkUtil.zFromBlockInColumn(info.getIndex());
            BlockChunk blockChunk = commandBuffer.getComponent(info.getChunkRef(), BlockChunk.getComponentType());

            assert blockChunk != null;

            ChunkColumn column = commandBuffer.getComponent(info.getChunkRef(), ChunkColumn.getComponentType());

            assert column != null;

            Ref<ChunkStore> sectionRef = column.getSection(ChunkUtil.chunkCoordinate(y));

            assert sectionRef != null;

            BlockSection blockSection = commandBuffer.getComponent(sectionRef, BlockSection.getComponentType());

            assert blockSection != null;

            ChunkSection chunkSection = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());

            assert chunkSection != null;

            int worldX = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getX(), x);
            int worldY = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getY(), y);
            int worldZ = ChunkUtil.worldCoordFromLocalCoord(chunkSection.getZ(), z);
            World world = commandBuffer.getExternalData().getWorld();
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(worldX, worldZ));
            double blockRotation = chunk.getRotation(worldX, worldY, worldZ).yaw().getRadians();
            Vector3d spawnOffset = new Vector3d().assign(coopAsset.getResidentSpawnOffset()).rotateY((float)blockRotation);
            Vector3i coopLocation = new Vector3i(worldX, worldY, worldZ);
            boolean tryCapture = coopAsset.getCaptureWildNPCsInRange();
            float captureRange = coopAsset.getWildCaptureRadius();
            if (tryCapture && captureRange >= 0.0F) {
               world.execute(() -> {
                  for (Ref<EntityStore> entity : TargetUtil.getAllEntitiesInSphere(coopLocation.toVector3d(), captureRange, store)) {
                     coopBlock.tryPutWildResidentFromWild(store, entity, worldTimeResource, coopLocation);
                  }
               });
            }

            if (coopBlock.shouldResidentsBeInCoop(worldTimeResource)) {
               world.execute(() -> coopBlock.ensureNoResidentsInWorld(store));
            } else {
               world.execute(() -> {
                  coopBlock.ensureSpawnResidentsInWorld(world, store, coopLocation.toVector3d(), spawnOffset);
                  coopBlock.generateProduceToInventory(worldTimeResource);
                  Vector3i blockPos = new Vector3i(worldX, worldY, worldZ);
                  BlockType currentBlockType = world.getBlockType(blockPos);

                  assert currentBlockType != null;

                  chunk.setBlockInteractionState(blockPos, currentBlockType, coopBlock.hasProduce() ? "Produce_Ready" : "default");
               });
            }

            Instant nextTickInstant = coopBlock.getNextScheduledTick(worldTimeResource);
            if (nextTickInstant != null) {
               blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), nextTickInstant);
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
