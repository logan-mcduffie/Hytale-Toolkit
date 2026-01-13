package com.hypixel.hytale.builtin.hytalegenerator.assets.worldstructures;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetCodecMapCodec;
import com.hypixel.hytale.assetstore.codec.ContainedAssetCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.biomemap.BiomeMap;
import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.material.SolidMaterial;
import com.hypixel.hytale.builtin.hytalegenerator.seed.SeedBox;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import javax.annotation.Nonnull;

public abstract class WorldStructureAsset implements Cleanable, JsonAssetWithMap<String, DefaultAssetMap<String, WorldStructureAsset>> {
   public static final AssetCodecMapCodec<String, WorldStructureAsset> CODEC = new AssetCodecMapCodec<>(
      Codec.STRING, (t, k) -> t.id = k, t -> t.id, (t, data) -> t.data = data, t -> t.data
   );
   public static final Codec<String> CHILD_ASSET_CODEC = new ContainedAssetCodec<>(WorldStructureAsset.class, CODEC);
   public static final Codec<String[]> CHILD_ASSET_CODEC_ARRAY = new ArrayCodec<>(CHILD_ASSET_CODEC, String[]::new);
   public static final BuilderCodec<WorldStructureAsset> ABSTRACT_CODEC = BuilderCodec.abstractBuilder(WorldStructureAsset.class).build();
   private String id;
   private AssetExtraInfo.Data data;

   protected WorldStructureAsset() {
   }

   public abstract BiomeMap<SolidMaterial> buildBiomeMap(@Nonnull WorldStructureAsset.Argument var1);

   public abstract int getBiomeTransitionDistance();

   public abstract int getMaxBiomeEdgeDistance();

   public String getId() {
      return this.id;
   }

   @Override
   public void cleanUp() {
   }

   public static class Argument {
      public MaterialCache materialCache;
      public SeedBox parentSeed;
      public WorkerIndexer workerIndexer;

      public Argument(@Nonnull MaterialCache materialCache, @Nonnull SeedBox parentSeed, @Nonnull WorkerIndexer workerIndexer) {
         this.materialCache = materialCache;
         this.parentSeed = parentSeed;
         this.workerIndexer = workerIndexer;
      }

      public Argument(@Nonnull WorldStructureAsset.Argument argument) {
         this.materialCache = argument.materialCache;
         this.parentSeed = argument.parentSeed;
         this.workerIndexer = argument.workerIndexer;
      }
   }
}
