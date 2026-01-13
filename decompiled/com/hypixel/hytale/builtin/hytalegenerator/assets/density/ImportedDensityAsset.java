package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.LoggerUtil;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import javax.annotation.Nonnull;

public class ImportedDensityAsset extends DensityAsset {
   public static final BuilderCodec<ImportedDensityAsset> CODEC = BuilderCodec.builder(
         ImportedDensityAsset.class, ImportedDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("Name", Codec.STRING, true), (t, k) -> t.importedNodeName = k, k -> k.importedNodeName)
      .add()
      .build();
   private String importedNodeName = "";

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantValueDensity(0.0);
      } else {
         DensityAsset.Exported asset = getExportedAsset(this.importedNodeName);
         if (asset == null) {
            LoggerUtil.getLogger().warning("Couldn't find Density asset exported with name: '" + this.importedNodeName + "'. Using empty Node instead.");
            return new ConstantValueDensity(0.0);
         } else if (asset.singleInstance) {
            if (asset.builtInstance == null) {
               asset.builtInstance = asset.asset.build(argument);
            }

            return asset.builtInstance;
         } else {
            return asset.asset.build(argument);
         }
      }
   }

   @Override
   public DensityAsset[] inputs() {
      DensityAsset.Exported asset = getExportedAsset(this.importedNodeName);
      if (asset == null) {
         LoggerUtil.getLogger().warning("Couldn't find Density asset exported with name: '" + this.importedNodeName + "'. Using empty Node instead.");
         return new DensityAsset[0];
      } else {
         return asset.asset.inputs();
      }
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
      DensityAsset.Exported exported = getExportedAsset(this.importedNodeName);
      if (exported != null) {
         exported.builtInstance = null;

         for (DensityAsset input : this.inputs()) {
            input.cleanUp();
         }
      }
   }
}
