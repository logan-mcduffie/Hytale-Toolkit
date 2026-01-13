package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class VectorWarpDensity extends Density {
   @Nullable
   private Density input;
   @Nullable
   private Density warpInput;
   private final double warpFactor;
   @Nonnull
   private final Vector3d warpVector;

   public VectorWarpDensity(@Nonnull Density input, @Nonnull Density warpInput, double warpFactor, @Nonnull Vector3d warpVector) {
      this.input = input;
      this.warpInput = warpInput;
      this.warpFactor = warpFactor;
      this.warpVector = warpVector;
   }

   @Override
   public double process(@Nonnull Density.Context context) {
      if (this.input == null) {
         return 0.0;
      } else if (this.warpInput == null) {
         return this.input.process(context);
      } else {
         double warp = this.warpInput.process(context);
         warp *= this.warpFactor;
         Vector3d samplePoint = this.warpVector.clone();
         samplePoint.setLength(1.0);
         samplePoint.scale(warp);
         samplePoint.add(context.position);
         Density.Context childContext = new Density.Context(context);
         childContext.position = samplePoint;
         return this.input.process(childContext);
      }
   }

   @Override
   public void setInputs(@Nonnull Density[] inputs) {
      if (inputs.length == 0) {
         this.input = null;
      }

      this.input = inputs[0];
      if (inputs.length < 2) {
         this.warpInput = null;
      }

      this.warpInput = inputs[1];
   }
}
