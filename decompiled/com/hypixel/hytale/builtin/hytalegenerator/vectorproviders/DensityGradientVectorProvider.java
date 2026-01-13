package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class DensityGradientVectorProvider extends VectorProvider {
   @Nonnull
   private final Density density;
   private final double sampleDistance;

   public DensityGradientVectorProvider(@Nonnull Density density, double sampleDistance) {
      assert sampleDistance >= 0.0;

      this.density = density;
      this.sampleDistance = Math.max(0.0, sampleDistance);
   }

   @Nonnull
   @Override
   public Vector3d process(@Nonnull VectorProvider.Context context) {
      double valueAtOrigin = this.density.process(new Density.Context(context));
      double maxX = context.position.x + this.sampleDistance;
      double maxY = context.position.y + this.sampleDistance;
      double maxZ = context.position.z + this.sampleDistance;
      Density.Context childContext = new Density.Context(context);
      childContext.position = new Vector3d(maxX, context.position.y, context.position.z);
      double deltaX = this.density.process(childContext) - valueAtOrigin;
      childContext.position = new Vector3d(context.position.x, maxY, context.position.z);
      double deltaY = this.density.process(childContext) - valueAtOrigin;
      childContext.position = new Vector3d(context.position.x, context.position.y, maxZ);
      double deltaZ = this.density.process(childContext) - valueAtOrigin;
      return new Vector3d(deltaX, deltaY, deltaZ);
   }
}
