package com.hypixel.hytale.builtin.adventure.farming;

import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlock;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.util.HashUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3i;
import com.hypixel.hytale.protocol.Rangef;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.HarvestingDropType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingData;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingStageData;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.GrowthModifierAsset;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.entity.ItemUtils;
import com.hypixel.hytale.server.core.modules.block.BlockModule;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.interaction.BlockHarvestUtils;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.WorldChunk;
import com.hypixel.hytale.server.core.universe.world.chunk.section.BlockSection;
import com.hypixel.hytale.server.core.universe.world.chunk.section.ChunkSection;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.metadata.CapturedNPCMetadata;
import java.time.Instant;
import java.util.Map;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class FarmingUtil {
   private static final int MAX_SECONDS_BETWEEN_TICKS = 15;
   private static final int BETWEEN_RANDOM = 10;

   public static void tickFarming(
      CommandBuffer<ChunkStore> commandBuffer,
      BlockSection blockSection,
      Ref<ChunkStore> sectionRef,
      Ref<ChunkStore> blockRef,
      FarmingBlock farmingBlock,
      int x,
      int y,
      int z,
      boolean initialTick
   ) {
      World world = commandBuffer.getExternalData().getWorld();
      WorldTimeResource worldTimeResource = world.getEntityStore().getStore().getResource(WorldTimeResource.getResourceType());
      Instant currentTime = worldTimeResource.getGameTime();
      BlockType blockType = farmingBlock.getPreviousBlockType() != null
         ? BlockType.getAssetMap().getAsset(farmingBlock.getPreviousBlockType())
         : BlockType.getAssetMap().getAsset(blockSection.get(x, y, z));
      if (blockType != null) {
         if (blockType.getFarming() != null) {
            FarmingData farmingConfig = blockType.getFarming();
            if (farmingConfig.getStages() != null) {
               float currentProgress = farmingBlock.getGrowthProgress();
               int currentStage = (int)currentProgress;
               String currentStageSet = farmingBlock.getCurrentStageSet();
               FarmingStageData[] stages = farmingConfig.getStages().get(currentStageSet);
               if (stages != null) {
                  if (currentStage < 0) {
                     currentStage = 0;
                     currentProgress = 0.0F;
                     farmingBlock.setGrowthProgress(0.0F);
                  }

                  if (currentStage >= stages.length) {
                     commandBuffer.removeEntity(blockRef, RemoveReason.REMOVE);
                  } else {
                     long remainingTimeSeconds = currentTime.getEpochSecond() - farmingBlock.getLastTickGameTime().getEpochSecond();
                     ChunkSection section = commandBuffer.getComponent(sectionRef, ChunkSection.getComponentType());
                     int worldX = ChunkUtil.worldCoordFromLocalCoord(section.getX(), x);
                     int worldY = ChunkUtil.worldCoordFromLocalCoord(section.getY(), y);
                     int worldZ = ChunkUtil.worldCoordFromLocalCoord(section.getZ(), z);

                     while (currentStage < stages.length) {
                        FarmingStageData stage = stages[currentStage];
                        if (stage.shouldStop(commandBuffer, sectionRef, blockRef, x, y, z)) {
                           farmingBlock.setGrowthProgress(stages.length);
                           commandBuffer.removeEntity(blockRef, RemoveReason.REMOVE);
                           break;
                        }

                        Rangef range = stage.getDuration();
                        if (range == null) {
                           commandBuffer.removeEntity(blockRef, RemoveReason.REMOVE);
                           break;
                        }

                        double rand = HashUtil.random(farmingBlock.getGeneration(), worldX, worldY, worldZ);
                        double baseDuration = range.min + (range.max - range.min) * rand;
                        long remainingDurationSeconds = Math.round(baseDuration * (1.0 - currentProgress % 1.0));
                        double growthMultiplier = 1.0;
                        if (farmingConfig.getGrowthModifiers() != null) {
                           for (String modifierName : farmingConfig.getGrowthModifiers()) {
                              GrowthModifierAsset modifier = GrowthModifierAsset.getAssetMap().getAsset(modifierName);
                              if (modifier != null) {
                                 growthMultiplier *= modifier.getCurrentGrowthMultiplier(commandBuffer, sectionRef, blockRef, x, y, z, initialTick);
                              }
                           }
                        }

                        remainingDurationSeconds = Math.round(remainingDurationSeconds / growthMultiplier);
                        if (remainingTimeSeconds < remainingDurationSeconds) {
                           currentProgress += (float)(remainingTimeSeconds / (baseDuration / growthMultiplier));
                           farmingBlock.setGrowthProgress(currentProgress);
                           long nextGrowthInNanos = (remainingDurationSeconds - remainingTimeSeconds) * 1000000000L;
                           long randCap = (long)(
                              (15.0 + 10.0 * HashUtil.random(farmingBlock.getGeneration() ^ 3405692655L, worldX, worldY, worldZ))
                                 * world.getTps()
                                 * WorldTimeResource.getSecondsPerTick(world)
                                 * 1.0E9
                           );
                           long cappedNextGrowthInNanos = Math.min(nextGrowthInNanos, randCap);
                           blockSection.scheduleTick(ChunkUtil.indexBlock(x, y, z), currentTime.plusNanos(cappedNextGrowthInNanos));
                           break;
                        }

                        remainingTimeSeconds -= remainingDurationSeconds;
                        currentProgress = ++currentStage;
                        farmingBlock.setGrowthProgress(currentProgress);
                        farmingBlock.setGeneration(farmingBlock.getGeneration() + 1);
                        if (currentStage >= stages.length) {
                           if (stages[currentStage - 1].implementsShouldStop()) {
                              currentStage = stages.length - 1;
                              farmingBlock.setGrowthProgress(currentStage);
                              stages[currentStage].apply(commandBuffer, sectionRef, blockRef, x, y, z, stages[currentStage]);
                           } else {
                              farmingBlock.setGrowthProgress(stages.length);
                              commandBuffer.removeEntity(blockRef, RemoveReason.REMOVE);
                           }
                        } else {
                           farmingBlock.setExecutions(0);
                           stages[currentStage].apply(commandBuffer, sectionRef, blockRef, x, y, z, stages[currentStage - 1]);
                        }
                     }

                     farmingBlock.setLastTickGameTime(currentTime);
                  }
               }
            }
         }
      }
   }

   public static void harvest(
      @Nonnull World world,
      @Nonnull ComponentAccessor<EntityStore> store,
      @Nonnull Ref<EntityStore> ref,
      @Nonnull BlockType blockType,
      int rotationIndex,
      @Nonnull Vector3i blockPosition
   ) {
      if (world.getGameplayConfig().getWorldConfig().isBlockGatheringAllowed()) {
         harvest0(store, ref, blockType, rotationIndex, blockPosition);
      }
   }

   @NullableDecl
   public static CapturedNPCMetadata generateCapturedNPCMetadata(ComponentAccessor<EntityStore> componentAccessor, Ref<EntityStore> entityRef, int roleIndex) {
      PersistentModel persistentModel = componentAccessor.getComponent(entityRef, PersistentModel.getComponentType());
      if (persistentModel == null) {
         return null;
      } else {
         ModelAsset modelAsset = ModelAsset.getAssetMap().getAsset(persistentModel.getModelReference().getModelAssetId());
         CapturedNPCMetadata meta = new CapturedNPCMetadata();
         if (modelAsset != null) {
            meta.setIconPath(modelAsset.getIcon());
         }

         meta.setRoleIndex(roleIndex);
         return meta;
      }
   }

   protected static boolean harvest0(
      @Nonnull ComponentAccessor<EntityStore> store,
      @Nonnull Ref<EntityStore> ref,
      @Nonnull BlockType blockType,
      int rotationIndex,
      @Nonnull Vector3i blockPosition
   ) {
      FarmingData farmingConfig = blockType.getFarming();
      if (farmingConfig == null || farmingConfig.getStages() == null) {
         return false;
      } else if (blockType.getGathering().getHarvest() == null) {
         return false;
      } else {
         World world = store.getExternalData().getWorld();
         Vector3d centerPosition = new Vector3d();
         blockType.getBlockCenter(rotationIndex, centerPosition);
         centerPosition.add(blockPosition);
         if (farmingConfig.getStageSetAfterHarvest() == null) {
            giveDrops(store, ref, centerPosition, blockType);
            WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(blockPosition.x, blockPosition.z));
            if (chunk != null) {
               chunk.breakBlock(blockPosition.x, blockPosition.y, blockPosition.z);
            }

            return true;
         } else {
            giveDrops(store, ref, centerPosition, blockType);
            Map<String, FarmingStageData[]> stageSets = farmingConfig.getStages();
            FarmingStageData[] stages = stageSets.get(farmingConfig.getStartingStageSet());
            if (stages == null) {
               return false;
            } else {
               int currentStageIndex = stages.length - 1;
               FarmingStageData previousStage = stages[currentStageIndex];
               String newStageSet = farmingConfig.getStageSetAfterHarvest();
               FarmingStageData[] newStages = stageSets.get(newStageSet);
               if (newStages != null && newStages.length != 0) {
                  Store<ChunkStore> chunkStore = world.getChunkStore().getStore();
                  Ref<ChunkStore> chunkRef = world.getChunkStore().getChunkReference(ChunkUtil.indexChunkFromBlock(blockPosition.x, blockPosition.z));
                  if (chunkRef == null) {
                     return false;
                  } else {
                     BlockComponentChunk blockComponentChunk = chunkStore.getComponent(chunkRef, BlockComponentChunk.getComponentType());
                     if (blockComponentChunk == null) {
                        return false;
                     } else {
                        Instant now = store.getExternalData()
                           .getWorld()
                           .getEntityStore()
                           .getStore()
                           .getResource(WorldTimeResource.getResourceType())
                           .getGameTime();
                        int blockIndexColumn = ChunkUtil.indexBlockInColumn(blockPosition.x, blockPosition.y, blockPosition.z);
                        Ref<ChunkStore> blockRef = blockComponentChunk.getEntityReference(blockIndexColumn);
                        FarmingBlock farmingBlock;
                        if (blockRef == null) {
                           Holder<ChunkStore> blockEntity = ChunkStore.REGISTRY.newHolder();
                           blockEntity.putComponent(BlockModule.BlockStateInfo.getComponentType(), new BlockModule.BlockStateInfo(blockIndexColumn, chunkRef));
                           farmingBlock = new FarmingBlock();
                           farmingBlock.setLastTickGameTime(now);
                           farmingBlock.setCurrentStageSet(newStageSet);
                           blockEntity.addComponent(FarmingBlock.getComponentType(), farmingBlock);
                           blockRef = chunkStore.addEntity(blockEntity, AddReason.SPAWN);
                        } else {
                           farmingBlock = chunkStore.ensureAndGetComponent(blockRef, FarmingBlock.getComponentType());
                        }

                        farmingBlock.setCurrentStageSet(newStageSet);
                        farmingBlock.setGrowthProgress(0.0F);
                        farmingBlock.setExecutions(0);
                        farmingBlock.setGeneration(farmingBlock.getGeneration() + 1);
                        farmingBlock.setLastTickGameTime(now);
                        Ref<ChunkStore> sectionRef = world.getChunkStore()
                           .getChunkSectionReference(
                              ChunkUtil.chunkCoordinate(blockPosition.x),
                              ChunkUtil.chunkCoordinate(blockPosition.y),
                              ChunkUtil.chunkCoordinate(blockPosition.z)
                           );
                        if (sectionRef == null) {
                           return false;
                        } else if (blockRef == null) {
                           return false;
                        } else {
                           BlockSection section = chunkStore.getComponent(sectionRef, BlockSection.getComponentType());
                           if (section != null) {
                              section.scheduleTick(ChunkUtil.indexBlock(blockPosition.x, blockPosition.y, blockPosition.z), now);
                           }

                           newStages[0].apply(chunkStore, sectionRef, blockRef, blockPosition.x, blockPosition.y, blockPosition.z, previousStage);
                           return true;
                        }
                     }
                  }
               } else {
                  WorldChunk chunk = world.getChunkIfInMemory(ChunkUtil.indexChunkFromBlock(blockPosition.x, blockPosition.z));
                  if (chunk != null) {
                     chunk.breakBlock(blockPosition.x, blockPosition.y, blockPosition.z);
                  }

                  return false;
               }
            }
         }
      }
   }

   protected static void giveDrops(
      @Nonnull ComponentAccessor<EntityStore> store, @Nonnull Ref<EntityStore> ref, @Nonnull Vector3d origin, @Nonnull BlockType blockType
   ) {
      HarvestingDropType harvest = blockType.getGathering().getHarvest();
      String itemId = harvest.getItemId();
      String dropListId = harvest.getDropListId();
      BlockHarvestUtils.getDrops(blockType, 1, itemId, dropListId).forEach(itemStack -> ItemUtils.interactivelyPickupItem(ref, itemStack, origin, store));
   }
}
