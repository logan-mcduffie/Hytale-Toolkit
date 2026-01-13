package com.hypixel.hytale.server.worldgen;

import com.hypixel.hytale.procedurallib.condition.IIntCondition;
import com.hypixel.hytale.procedurallib.json.SeedResource;
import com.hypixel.hytale.procedurallib.logic.ResultBuffer;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
import com.hypixel.hytale.server.worldgen.loader.WorldGenPrefabLoader;
import com.hypixel.hytale.server.worldgen.loader.prefab.BlockPlacementMaskRegistry;
import com.hypixel.hytale.server.worldgen.loader.util.FileMaskCache;
import com.hypixel.hytale.server.worldgen.prefab.PrefabStoreRoot;
import com.hypixel.hytale.server.worldgen.util.LogUtil;
import java.nio.file.Path;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class SeedStringResource implements SeedResource {
   @Nonnull
   protected final FileMaskCache<IIntCondition> biomeMaskRegistry;
   @Nonnull
   protected final BlockPlacementMaskRegistry blockMaskRegistry;
   @Nonnull
   protected Path dataFolder;
   @Nonnull
   protected WorldGenPrefabLoader loader;

   public SeedStringResource(@Nonnull PrefabStoreRoot prefabStore, @Nonnull Path dataFolder) {
      this.dataFolder = dataFolder;
      this.loader = new WorldGenPrefabLoader(prefabStore, dataFolder);
      this.biomeMaskRegistry = new FileMaskCache<>();
      this.blockMaskRegistry = new BlockPlacementMaskRegistry();
   }

   public WorldGenPrefabLoader getLoader() {
      return this.loader;
   }

   public void setPrefabStore(@Nonnull PrefabStoreRoot prefabStore) {
      if (prefabStore != this.loader.getStore()) {
         LogUtil.getLogger().at(Level.INFO).log("Set prefab-store to: %s", prefabStore.name());
         this.loader = new WorldGenPrefabLoader(prefabStore, this.dataFolder);
      }
   }

   public void setDataFolder(@Nonnull Path dataFolder) {
      if (!dataFolder.equals(this.dataFolder)) {
         LogUtil.getLogger().at(Level.INFO).log("Set data-folder to: %s", dataFolder);
         this.dataFolder = dataFolder;
         this.loader = new WorldGenPrefabLoader(this.loader.getStore(), dataFolder);
      }
   }

   @Nonnull
   @Override
   public ResultBuffer.Bounds2d localBounds2d() {
      return ChunkGenerator.getResource().bounds2d;
   }

   @Nonnull
   @Override
   public ResultBuffer.ResultBuffer2d localBuffer2d() {
      return ChunkGenerator.getResource().resultBuffer2d;
   }

   @Nonnull
   @Override
   public ResultBuffer.ResultBuffer3d localBuffer3d() {
      return ChunkGenerator.getResource().resultBuffer3d;
   }

   @Override
   public void writeSeedReport(String seedReport) {
      LogUtil.getLogger().at(Level.FINE).log(seedReport);
   }

   @Nonnull
   public FileMaskCache<IIntCondition> getBiomeMaskRegistry() {
      return this.biomeMaskRegistry;
   }

   @Nonnull
   public BlockPlacementMaskRegistry getBlockMaskRegistry() {
      return this.blockMaskRegistry;
   }
}
