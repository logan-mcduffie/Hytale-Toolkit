package com.hypixel.hytale.builtin.adventure.farming;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.builtin.adventure.farming.component.CoopResidentComponent;
import com.hypixel.hytale.builtin.adventure.farming.config.FarmingCoopAsset;
import com.hypixel.hytale.builtin.adventure.farming.config.modifiers.FertilizerGrowthModifierAsset;
import com.hypixel.hytale.builtin.adventure.farming.config.modifiers.LightLevelGrowthModifierAsset;
import com.hypixel.hytale.builtin.adventure.farming.config.modifiers.WaterGrowthModifierAsset;
import com.hypixel.hytale.builtin.adventure.farming.config.stages.BlockStateFarmingStageData;
import com.hypixel.hytale.builtin.adventure.farming.config.stages.BlockTypeFarmingStageData;
import com.hypixel.hytale.builtin.adventure.farming.config.stages.PrefabFarmingStageData;
import com.hypixel.hytale.builtin.adventure.farming.config.stages.spread.DirectionalGrowthBehaviour;
import com.hypixel.hytale.builtin.adventure.farming.config.stages.spread.SpreadFarmingStageData;
import com.hypixel.hytale.builtin.adventure.farming.config.stages.spread.SpreadGrowthBehaviour;
import com.hypixel.hytale.builtin.adventure.farming.interactions.ChangeFarmingStageInteraction;
import com.hypixel.hytale.builtin.adventure.farming.interactions.FertilizeSoilInteraction;
import com.hypixel.hytale.builtin.adventure.farming.interactions.HarvestCropInteraction;
import com.hypixel.hytale.builtin.adventure.farming.interactions.UseCaptureCrateInteraction;
import com.hypixel.hytale.builtin.adventure.farming.interactions.UseCoopInteraction;
import com.hypixel.hytale.builtin.adventure.farming.interactions.UseWateringCanInteraction;
import com.hypixel.hytale.builtin.adventure.farming.states.CoopBlock;
import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlock;
import com.hypixel.hytale.builtin.adventure.farming.states.FarmingBlockState;
import com.hypixel.hytale.builtin.adventure.farming.states.TilledSoilBlock;
import com.hypixel.hytale.builtin.tagset.config.NPCGroup;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.event.EventPriority;
import com.hypixel.hytale.server.core.asset.HytaleAssetStore;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.FarmingStageData;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.farming.GrowthModifierAsset;
import com.hypixel.hytale.server.core.asset.type.item.config.ItemDropList;
import com.hypixel.hytale.server.core.asset.type.weather.config.Weather;
import com.hypixel.hytale.server.core.modules.interaction.interaction.config.Interaction;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.chunk.BlockComponentChunk;
import com.hypixel.hytale.server.core.universe.world.events.ChunkPreLoadProcessEvent;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import javax.annotation.Nonnull;

public class FarmingPlugin extends JavaPlugin {
   protected static FarmingPlugin instance;
   private ComponentType<ChunkStore, TilledSoilBlock> tiledSoilBlockComponentType;
   private ComponentType<ChunkStore, FarmingBlock> farmingBlockComponentType;
   private ComponentType<ChunkStore, FarmingBlockState> farmingBlockStateComponentType;
   private ComponentType<ChunkStore, CoopBlock> coopBlockStateComponentType;
   private ComponentType<EntityStore, CoopResidentComponent> coopResidentComponentType;

   public static FarmingPlugin get() {
      return instance;
   }

   public FarmingPlugin(@Nonnull JavaPluginInit init) {
      super(init);
   }

   @Override
   protected void setup() {
      instance = this;
      this.getAssetRegistry()
         .register(
            ((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)HytaleAssetStore.builder(
                              GrowthModifierAsset.class, new DefaultAssetMap()
                           )
                           .setPath("Farming/Modifiers"))
                        .setCodec(GrowthModifierAsset.CODEC))
                     .loadsAfter(Weather.class))
                  .setKeyFunction(GrowthModifierAsset::getId))
               .build()
         );
      this.getAssetRegistry()
         .register(
            ((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)((HytaleAssetStore.Builder)HytaleAssetStore.builder(
                              FarmingCoopAsset.class, new DefaultAssetMap()
                           )
                           .setPath("Farming/Coops"))
                        .setCodec(FarmingCoopAsset.CODEC))
                     .loadsAfter(ItemDropList.class, NPCGroup.class))
                  .setKeyFunction(FarmingCoopAsset::getId))
               .build()
         );
      this.getCodecRegistry(Interaction.CODEC)
         .register("HarvestCrop", HarvestCropInteraction.class, HarvestCropInteraction.CODEC)
         .register("FertilizeSoil", FertilizeSoilInteraction.class, FertilizeSoilInteraction.CODEC)
         .register("ChangeFarmingStage", ChangeFarmingStageInteraction.class, ChangeFarmingStageInteraction.CODEC)
         .register("UseWateringCan", UseWateringCanInteraction.class, UseWateringCanInteraction.CODEC)
         .register("UseCoop", UseCoopInteraction.class, UseCoopInteraction.CODEC)
         .register("UseCaptureCrate", UseCaptureCrateInteraction.class, UseCaptureCrateInteraction.CODEC);
      this.getCodecRegistry(GrowthModifierAsset.CODEC).register("Fertilizer", FertilizerGrowthModifierAsset.class, FertilizerGrowthModifierAsset.CODEC);
      this.getCodecRegistry(GrowthModifierAsset.CODEC).register("LightLevel", LightLevelGrowthModifierAsset.class, LightLevelGrowthModifierAsset.CODEC);
      this.getCodecRegistry(GrowthModifierAsset.CODEC).register("Water", WaterGrowthModifierAsset.class, WaterGrowthModifierAsset.CODEC);
      this.getCodecRegistry(FarmingStageData.CODEC).register("BlockType", BlockTypeFarmingStageData.class, BlockTypeFarmingStageData.CODEC);
      this.getCodecRegistry(FarmingStageData.CODEC).register("BlockState", BlockStateFarmingStageData.class, BlockStateFarmingStageData.CODEC);
      this.getCodecRegistry(FarmingStageData.CODEC).register("Prefab", PrefabFarmingStageData.class, PrefabFarmingStageData.CODEC);
      this.getCodecRegistry(FarmingStageData.CODEC).register("Spread", SpreadFarmingStageData.class, SpreadFarmingStageData.CODEC);
      this.getCodecRegistry(SpreadGrowthBehaviour.CODEC).register("Directional", DirectionalGrowthBehaviour.class, DirectionalGrowthBehaviour.CODEC);
      this.tiledSoilBlockComponentType = this.getChunkStoreRegistry().registerComponent(TilledSoilBlock.class, "TilledSoil", TilledSoilBlock.CODEC);
      this.farmingBlockComponentType = this.getChunkStoreRegistry().registerComponent(FarmingBlock.class, "FarmingBlock", FarmingBlock.CODEC);
      this.farmingBlockStateComponentType = this.getChunkStoreRegistry().registerComponent(FarmingBlockState.class, "Farming", FarmingBlockState.CODEC);
      this.coopBlockStateComponentType = this.getChunkStoreRegistry().registerComponent(CoopBlock.class, "Coop", CoopBlock.CODEC);
      this.coopResidentComponentType = this.getEntityStoreRegistry()
         .registerComponent(CoopResidentComponent.class, "CoopResident", CoopResidentComponent.CODEC);
      this.getChunkStoreRegistry().registerSystem(new FarmingSystems.OnSoilAdded());
      this.getChunkStoreRegistry().registerSystem(new FarmingSystems.OnFarmBlockAdded());
      this.getChunkStoreRegistry().registerSystem(new FarmingSystems.Ticking());
      this.getChunkStoreRegistry().registerSystem(new FarmingSystems.MigrateFarming());
      this.getChunkStoreRegistry().registerSystem(new FarmingSystems.OnCoopAdded());
      this.getEntityStoreRegistry().registerSystem(new FarmingSystems.CoopResidentEntitySystem());
      this.getEntityStoreRegistry().registerSystem(new FarmingSystems.CoopResidentTicking());
      this.getEventRegistry().registerGlobal(EventPriority.LAST, ChunkPreLoadProcessEvent.class, FarmingPlugin::preventSpreadOnNew);
   }

   private static void preventSpreadOnNew(ChunkPreLoadProcessEvent event) {
      if (event.isNewlyGenerated()) {
         BlockComponentChunk components = event.getHolder().getComponent(BlockComponentChunk.getComponentType());
         if (components != null) {
            Int2ObjectMap<Holder<ChunkStore>> holders = components.getEntityHolders();
            holders.values().forEach(v -> {
               FarmingBlock farming = v.getComponent(FarmingBlock.getComponentType());
               if (farming != null) {
                  farming.setSpreadRate(0.0F);
               }
            });
         }
      }
   }

   public ComponentType<ChunkStore, TilledSoilBlock> getTiledSoilBlockComponentType() {
      return this.tiledSoilBlockComponentType;
   }

   public ComponentType<ChunkStore, FarmingBlock> getFarmingBlockComponentType() {
      return this.farmingBlockComponentType;
   }

   public ComponentType<ChunkStore, FarmingBlockState> getFarmingBlockStateComponentType() {
      return this.farmingBlockStateComponentType;
   }

   public ComponentType<ChunkStore, CoopBlock> getCoopBlockStateComponentType() {
      return this.coopBlockStateComponentType;
   }

   public ComponentType<EntityStore, CoopResidentComponent> getCoopResidentComponentType() {
      return this.coopResidentComponentType;
   }
}
