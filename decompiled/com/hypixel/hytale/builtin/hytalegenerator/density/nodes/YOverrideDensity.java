package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class YOverrideDensity extends Density {
   @Nonnull
   private Density input;
   private final double value;

   public YOverrideDensity(@Nonnull Density input, double value) {
      this.input = input;
      this.value = value;
   }

   @Override
   public double process(@Nonnull Density.Context context) {
      Vector3d childPosition = new Vector3d(context.position.x, this.value, context.position.z);
      Density.Context childContext = new Density.Context(context);
      childContext.position = childPosition;
      return this.input.process(childContext);
   }

   @Override
   public void setInputs(@Nonnull Density[] inputs) {
      assert inputs.length == 1;

      assert inputs[0] != null;

      this.input = inputs[0];
   }
}
