package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.vectorproviders.VectorProvider;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class AngleDensity extends Density {
   private static final double HALF_PI = Math.PI / 2;
   @Nonnull
   private VectorProvider vectorProvider;
   @Nonnull
   private final Vector3d vector;
   private final boolean toAxis;

   public AngleDensity(@Nonnull VectorProvider vectorProvider, @Nonnull Vector3d vector, boolean toAxis) {
      this.vector = vector.clone();
      this.vectorProvider = vectorProvider;
      this.toAxis = toAxis;
   }

   @Override
   public double process(@Nonnull Density.Context context) {
      Vector3d otherVector = this.vectorProvider.process(new VectorProvider.Context(context));
      double slopeAngle = VectorUtil.angle(this.vector, otherVector);
      if (this.toAxis && slopeAngle > Math.PI / 2) {
         slopeAngle = Math.PI - slopeAngle;
      }

      slopeAngle /= Math.PI;
      return slopeAngle * 180.0;
   }

   @Override
   public void setInputs(@Nonnull Density[] inputs) {
   }
}
