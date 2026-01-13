package com.hypixel.hytale.builtin.hytalegenerator.assets.materialproviders;

import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.BiDouble2DoubleFunction;
import com.hypixel.hytale.builtin.hytalegenerator.material.Material;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.HorizontalMaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.materialproviders.MaterialProvider;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.BaseHeightReference;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import javax.annotation.Nonnull;

public class SimpleHorizontalMaterialProviderAsset extends MaterialProviderAsset {
   public static final BuilderCodec<SimpleHorizontalMaterialProviderAsset> CODEC = BuilderCodec.builder(
         SimpleHorizontalMaterialProviderAsset.class, SimpleHorizontalMaterialProviderAsset::new, MaterialProviderAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("TopY", Codec.INTEGER, true), (t, k) -> t.topY = k, k -> k.topY)
      .add()
      .append(new KeyedCodec<>("BottomY", Codec.INTEGER, true), (t, k) -> t.bottomY = k, k -> k.bottomY)
      .add()
      .append(new KeyedCodec<>("Material", MaterialProviderAsset.CODEC, true), (t, k) -> t.materialProviderAsset = k, k -> k.materialProviderAsset)
      .add()
      .append(new KeyedCodec<>("TopBaseHeight", Codec.STRING, false), (t, k) -> t.topBaseHeightName = k, t -> t.topBaseHeightName)
      .add()
      .append(new KeyedCodec<>("BottomBaseHeight", Codec.STRING, false), (t, k) -> t.bottomBaseHeightName = k, t -> t.bottomBaseHeightName)
      .add()
      .build();
   private int topY = 0;
   private int bottomY = 0;
   private MaterialProviderAsset materialProviderAsset = new ConstantMaterialProviderAsset();
   private String topBaseHeightName = "";
   private String bottomBaseHeightName = "";

   @Nonnull
   @Override
   public MaterialProvider<Material> build(@Nonnull MaterialProviderAsset.Argument argument) {
      if (super.skip()) {
         return MaterialProvider.noMaterialProvider();
      } else {
         BiDouble2DoubleFunction topFunction = (x, z) -> this.topY;
         BiDouble2DoubleFunction bottomFunction = (x, z) -> this.bottomY;
         if (!this.topBaseHeightName.isEmpty()) {
            BaseHeightReference topHeightDataLayer = argument.referenceBundle.getLayerWithName(this.topBaseHeightName, BaseHeightReference.class);
            if (topHeightDataLayer != null) {
               BiDouble2DoubleFunction baseHeight = topHeightDataLayer.getHeightFunction();
               topFunction = (x, z) -> baseHeight.apply(x, z) + this.topY;
            } else {
               HytaleLogger.getLogger()
                  .atConfig()
                  .log("Couldn't find height data layer with name \"" + this.topBaseHeightName + "\", using a zero-constant Density node.");
            }
         }

         if (!this.bottomBaseHeightName.isEmpty()) {
            BaseHeightReference bottomHeightDataLayer = argument.referenceBundle.getLayerWithName(this.bottomBaseHeightName, BaseHeightReference.class);
            if (bottomHeightDataLayer != null) {
               BiDouble2DoubleFunction baseHeight = bottomHeightDataLayer.getHeightFunction();
               bottomFunction = (x, z) -> baseHeight.apply(x, z) + this.bottomY;
            } else {
               HytaleLogger.getLogger()
                  .atConfig()
                  .log("Couldn't find height data layer with name \"" + this.bottomBaseHeightName + "\", using a zero-constant Density node.");
            }
         }

         return new HorizontalMaterialProvider<>(this.materialProviderAsset.build(argument), topFunction::apply, bottomFunction::apply);
      }
   }

   @Override
   public void cleanUp() {
      this.materialProviderAsset.cleanUp();
   }
}
