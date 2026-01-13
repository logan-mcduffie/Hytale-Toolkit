package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.framework.interfaces.functions.BiDouble2DoubleFunction;
import javax.annotation.Nonnull;

public class BaseHeightDensity extends Density {
   @Nonnull
   private final BiDouble2DoubleFunction heightFunction;
   private final boolean isDistance;

   public BaseHeightDensity(@Nonnull BiDouble2DoubleFunction heightFunction, boolean isDistance) {
      this.heightFunction = heightFunction;
      this.isDistance = isDistance;
   }

   @Override
   public double process(@Nonnull Density.Context context) {
      return this.isDistance
         ? context.position.y - this.heightFunction.apply(context.position.x, context.position.z)
         : this.heightFunction.apply(context.position.x, context.position.z);
   }

   public boolean skipInputs(double y) {
      return true;
   }
}
