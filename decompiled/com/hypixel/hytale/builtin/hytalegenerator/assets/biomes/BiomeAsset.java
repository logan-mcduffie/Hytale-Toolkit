package com.hypixel.hytale.builtin.hytalegenerator.assets.biomes;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.AssetKeyValidator;
import com.hypixel.hytale.assetstore.AssetRegistry;
import com.hypixel.hytale.assetstore.AssetStore;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.PropField;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.assets.density.DensityAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.environmentproviders.ConstantEnvironmentProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.environmentproviders.EnvironmentProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.ConstantMaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders.MaterialProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.propstageiterations.PropRuntimeAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.terrains.DensityTerrainAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.terrains.TerrainAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.tintproviders.ConstantTintProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.assets.tintproviders.TintProviderAsset;
import com.hypixel.hytale.builtin.hytalegenerator.biome.BiomeType;
import com.hypixel.hytale.builtin.hytalegenerator.biome.SimpleBiomeType;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.environmentproviders.EnvironmentProvider;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.propdistributions.Assignments;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.ReferenceBundle;
import com.hypixel.hytale.builtin.hytalegenerator.seed.SeedBox;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.builtin.hytalegenerator.tintproviders.TintProvider;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.codecs.array.ArrayCodec;
import com.hypixel.hytale.codec.validation.ValidatorCache;
import javax.annotation.Nonnull;

public class BiomeAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, BiomeAsset>>, Cleanable {
   public static final ValidatorCache<String> VALIDATOR_CACHE = new ValidatorCache<>(new AssetKeyValidator<>(BiomeAsset::getAssetStore));
   private static AssetStore<String, BiomeAsset, DefaultAssetMap<String, BiomeAsset>> STORE;
   public static final AssetBuilderCodec<String, BiomeAsset> CODEC = AssetBuilderCodec.builder(
         BiomeAsset.class,
         BiomeAsset::new,
         Codec.STRING,
         (asset, id) -> asset.id = id,
         config -> config.id,
         (config, data) -> config.data = data,
         config -> config.data
      )
      .append(new KeyedCodec<>("Terrain", TerrainAsset.CODEC, true), (asset, t) -> asset.terrainAsset = t, asset -> asset.terrainAsset)
      .add()
      .append(
         new KeyedCodec<>("FloatingFunctionNodes", new ArrayCodec<>(DensityAsset.CODEC, DensityAsset[]::new), true),
         (asset, t) -> asset.floatingFunctionNodeAssets = t,
         asset -> asset.floatingFunctionNodeAssets
      )
      .add()
      .append(new KeyedCodec<>("Name", Codec.STRING, true), (asset, t) -> asset.biomeName = t, asset -> asset.biomeName)
      .add()
      .append(
         new KeyedCodec<>("MaterialProvider", MaterialProviderAsset.CODEC, true),
         (asset, materialProvider) -> asset.materialProviderAsset = materialProvider,
         asset -> asset.materialProviderAsset
      )
      .add()
      .append(
         new KeyedCodec<>("Props", new ArrayCodec<>(PropRuntimeAsset.CODEC, PropRuntimeAsset[]::new), true),
         (asset, materialProvider) -> asset.propRuntimeAssets = materialProvider,
         asset -> asset.propRuntimeAssets
      )
      .add()
      .append(
         new KeyedCodec<>("EnvironmentProvider", EnvironmentProviderAsset.CODEC, true),
         (asset, environmentProvider) -> asset.environmentProviderAsset = environmentProvider,
         asset -> asset.environmentProviderAsset
      )
      .add()
      .append(
         new KeyedCodec<>("TintProvider", TintProviderAsset.CODEC, true),
         (asset, tintProvider) -> asset.tintProviderAsset = tintProvider,
         asset -> asset.tintProviderAsset
      )
      .add()
      .build();
   private String id;
   private AssetExtraInfo.Data data;
   private TerrainAsset terrainAsset = new DensityTerrainAsset();
   private MaterialProviderAsset materialProviderAsset = new ConstantMaterialProviderAsset();
   private PropRuntimeAsset[] propRuntimeAssets = new PropRuntimeAsset[0];
   private EnvironmentProviderAsset environmentProviderAsset = new ConstantEnvironmentProviderAsset();
   private TintProviderAsset tintProviderAsset = new ConstantTintProviderAsset();
   private String biomeName = "DefaultName";
   private DensityAsset[] floatingFunctionNodeAssets = new DensityAsset[0];

   public static AssetStore<String, BiomeAsset, DefaultAssetMap<String, BiomeAsset>> getAssetStore() {
      if (STORE == null) {
         STORE = AssetRegistry.getAssetStore(BiomeAsset.class);
      }

      return STORE;
   }

   private BiomeAsset() {
   }

   @Override
   public void cleanUp() {
      this.terrainAsset.cleanUp();
      this.materialProviderAsset.cleanUp();

      for (PropRuntimeAsset propRuntimeAsset : this.propRuntimeAssets) {
         propRuntimeAsset.cleanUp();
      }

      this.environmentProviderAsset.cleanUp();
      this.tintProviderAsset.cleanUp();

      for (DensityAsset densityAsset : this.floatingFunctionNodeAssets) {
         densityAsset.cleanUp();
      }
   }

   public BiomeType build(
      @Nonnull MaterialCache materialCache, @Nonnull SeedBox parentSeed, @Nonnull ReferenceBundle referenceBundle, @Nonnull WorkerIndexer workerIndexer
   ) {
      MaterialProvider<Material> materialProvider = this.materialProviderAsset
         .build(new MaterialProviderAsset.Argument(parentSeed, materialCache, referenceBundle, workerIndexer));
      Density density = this.terrainAsset.buildDensity(parentSeed, referenceBundle, workerIndexer);
      EnvironmentProvider environments = EnvironmentProvider.noEnvironmentProvider();
      if (this.environmentProviderAsset != null) {
         environments = this.environmentProviderAsset.build(new EnvironmentProviderAsset.Argument(parentSeed, materialCache, referenceBundle, workerIndexer));
      }

      TintProvider tints = TintProvider.noTintProvider();
      if (this.tintProviderAsset != null) {
         tints = this.tintProviderAsset.build(new TintProviderAsset.Argument(parentSeed, materialCache, referenceBundle, workerIndexer));
      }

      SimpleBiomeType biome = new SimpleBiomeType(this.biomeName, density, materialProvider, environments, tints);

      for (PropRuntimeAsset fieldAsset : this.propRuntimeAssets) {
         if (!fieldAsset.isSkip()) {
            PositionProvider positionProvider = fieldAsset.buildPositionProvider(parentSeed, referenceBundle, workerIndexer);
            Assignments distribution = fieldAsset.buildPropDistribution(parentSeed, materialCache, fieldAsset.getRuntime(), referenceBundle, workerIndexer);
            PropField field = new PropField(fieldAsset.getRuntime(), distribution, positionProvider);
            biome.addPropFieldTo(field);
         }
      }

      return biome;
   }

   public String getBiomeName() {
      return this.biomeName;
   }

   public String getId() {
      return this.id;
   }
}
