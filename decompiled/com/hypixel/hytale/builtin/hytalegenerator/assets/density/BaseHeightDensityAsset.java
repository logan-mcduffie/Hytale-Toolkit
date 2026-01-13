package com.hypixel.hytale.builtin.hytalegenerator.assets.density;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.BaseHeightDensity;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.ConstantValueDensity;
import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.BiDouble2DoubleFunction;
import com.hypixel.hytale.builtin.hytalegenerator.referencebundle.BaseHeightReference;
import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.logger.HytaleLogger;
import javax.annotation.Nonnull;

public class BaseHeightDensityAsset extends DensityAsset {
   public static final BuilderCodec<BaseHeightDensityAsset> CODEC = BuilderCodec.builder(
         BaseHeightDensityAsset.class, BaseHeightDensityAsset::new, DensityAsset.ABSTRACT_CODEC
      )
      .append(new KeyedCodec<>("BaseHeightName", Codec.STRING, false), (t, k) -> t.baseHeightName = k, t -> t.baseHeightName)
      .add()
      .append(new KeyedCodec<>("Distance", Codec.BOOLEAN, false), (t, k) -> t.isDistance = k, t -> t.isDistance)
      .add()
      .build();
   private String baseHeightName = "";
   private boolean isDistance = false;

   @Nonnull
   @Override
   public Density build(@Nonnull DensityAsset.Argument argument) {
      if (this.isSkipped()) {
         return new ConstantValueDensity(0.0);
      } else {
         BaseHeightReference heightDataLayer = argument.referenceBundle.getLayerWithName(this.baseHeightName, BaseHeightReference.class);
         if (heightDataLayer == null) {
            HytaleLogger.getLogger()
               .atConfig()
               .log("Couldn't find height data layer with name \"" + this.baseHeightName + "\", using a zero-constant Density node.");
            return new ConstantValueDensity(0.0);
         } else {
            BiDouble2DoubleFunction yFunction = heightDataLayer.getHeightFunction();
            return new BaseHeightDensity(yFunction, this.isDistance);
         }
      }
   }

   @Override
   public void cleanUp() {
      this.cleanUpInputs();
   }
}
