package com.hypixel.hytale.builtin.hytalegenerator.assets.material;

import com.hypixel.hytale.assetstore.AssetExtraInfo;
import com.hypixel.hytale.assetstore.codec.AssetBuilderCodec;
import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.assetstore.map.JsonAssetWithMap;
import com.hypixel.hytale.builtin.hytalegenerator.assets.Cleanable;
import com.hypixel.hytale.builtin.hytalegenerator.material.FluidMaterial;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.material.MaterialCache;
import com.hypixel.hytale.builtin.hytalegenerator.material.SolidMaterial;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import javax.annotation.Nonnull;

public class MaterialAsset implements JsonAssetWithMap<String, DefaultAssetMap<String, MaterialAsset>>, Cleanable {
   public static final AssetBuilderCodec<String, MaterialAsset> CODEC = AssetBuilderCodec.builder(
         MaterialAsset.class,
         MaterialAsset::new,
         Codec.STRING,
         (asset, id) -> asset.id = id,
         config -> config.id,
         (config, data) -> config.data = data,
         config -> config.data
      )
      .append(new KeyedCodec<>("Solid", Codec.STRING, true), (t, value) -> t.solidName = value, t -> t.solidName)
      .add()
      .append(new KeyedCodec<>("Fluid", Codec.STRING, true), (t, value) -> t.fluidName = value, t -> t.fluidName)
      .add()
      .build();
   private String id;
   private AssetExtraInfo.Data data;
   @Nonnull
   private String solidName = "";
   @Nonnull
   private String fluidName = "";

   public MaterialAsset() {
   }

   public MaterialAsset(@Nonnull String solidName, @Nonnull String fluidName) {
      this.solidName = solidName;
      this.fluidName = fluidName;
   }

   public Material build(@Nonnull MaterialCache materialCache) {
      SolidMaterial solid = materialCache.EMPTY_AIR;
      if (!this.solidName.isEmpty()) {
         solid = materialCache.getSolidMaterial(this.solidName);
      }

      FluidMaterial fluid = materialCache.EMPTY_FLUID;
      if (!this.fluidName.isEmpty()) {
         fluid = materialCache.getFluidMaterial(this.fluidName);
      }

      return new Material(solid, fluid);
   }

   public String getId() {
      return this.id;
   }

   @Override
   public void cleanUp() {
   }
}
