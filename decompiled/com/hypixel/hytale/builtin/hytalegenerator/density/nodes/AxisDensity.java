package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.math.vector.Vector3d;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import javax.annotation.Nonnull;

public class AxisDensity extends Density {
   public static final double ZERO_DELTA = 1.0E-9;
   private static final Vector3d ZERO_VECTOR = new Vector3d();
   @Nonnull
   private final Double2DoubleFunction distanceCurve;
   @Nonnull
   private final Vector3d axis;
   private final boolean isAnchored;

   public AxisDensity(@Nonnull Double2DoubleFunction distanceCurve, @Nonnull Vector3d axis, boolean isAnchored) {
      this.distanceCurve = distanceCurve;
      this.axis = axis;
      this.isAnchored = isAnchored;
   }

   @Override
   public double process(@Nonnull Density.Context context) {
      if (this.axis.length() == 0.0) {
         return 0.0;
      } else if (this.isAnchored) {
         return this.processAnchored(context);
      } else {
         double distance = VectorUtil.distanceToLine3d(context.position, ZERO_VECTOR, this.axis);
         return this.distanceCurve.get(distance);
      }
   }

   private double processAnchored(@Nonnull Density.Context context) {
      if (context == null) {
         return 0.0;
      } else {
         Vector3d anchor = context.densityAnchor;
         if (anchor == null) {
            return 0.0;
         } else {
            Vector3d position = context.position.clone().subtract(anchor);
            double distance = VectorUtil.distanceToLine3d(position, ZERO_VECTOR, this.axis);
            return this.distanceCurve.get(distance);
         }
      }
   }
}
