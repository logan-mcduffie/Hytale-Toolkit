package com.hypixel.hytale.builtin.hytalegenerator.density.nodes.positions.returntypes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class CellValueReturnType extends ReturnType {
   @Nonnull
   private final Density sampleField;
   private final double defaultValue;

   public CellValueReturnType(@Nonnull Density sampleField, double defaultValue, int threadCount) {
      this.sampleField = sampleField;
      this.defaultValue = defaultValue;
   }

   @Override
   public double get(
      double distance0,
      double distance1,
      @Nonnull Vector3d samplePosition,
      @Nullable Vector3d closestPoint0,
      Vector3d closestPoint1,
      @Nonnull Density.Context context
   ) {
      if (closestPoint0 == null) {
         return this.defaultValue;
      } else {
         Density.Context childContext = new Density.Context(context);
         childContext.position = closestPoint0;
         return this.sampleField.process(childContext);
      }
   }
}
