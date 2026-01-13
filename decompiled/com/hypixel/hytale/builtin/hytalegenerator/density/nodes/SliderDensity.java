package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class SliderDensity extends Density {
   private final double slideX;
   private final double slideY;
   private final double slideZ;
   @Nullable
   private Density input;

   public SliderDensity(double slideX, double slideY, double slideZ, Density input) {
      this.slideX = slideX;
      this.slideY = slideY;
      this.slideZ = slideZ;
      this.input = input;
   }

   @Override
   public double process(@Nonnull Density.Context context) {
      if (this.input == null) {
         return 0.0;
      } else {
         Vector3d childPosition = new Vector3d(context.position.x - this.slideX, context.position.y - this.slideY, context.position.z - this.slideZ);
         Density.Context childContext = new Density.Context(context);
         childContext.position = childPosition;
         return this.input.process(childContext);
      }
   }

   @Override
   public void setInputs(@Nonnull Density[] inputs) {
      if (inputs.length == 0) {
         this.input = null;
      }

      this.input = inputs[0];
   }
}
