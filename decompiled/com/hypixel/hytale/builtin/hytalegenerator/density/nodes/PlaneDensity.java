package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.math.vector.Vector3d;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PlaneDensity extends Density {
   public static final double ZERO_DELTA = 1.0E-9;
   private static final Vector3d ZERO_VECTOR = new Vector3d();
   @Nonnull
   private final Double2DoubleFunction distanceCurve;
   @Nonnull
   private final Vector3d planeNormal;
   private final boolean isPlaneHorizontal;
   private final boolean isAnchored;

   public PlaneDensity(@Nonnull Double2DoubleFunction distanceCurve, @Nonnull Vector3d planeNormal, boolean isAnchored) {
      this.distanceCurve = distanceCurve;
      this.planeNormal = planeNormal;
      this.isPlaneHorizontal = planeNormal.x == 0.0 && planeNormal.z == 0.0;
      this.isAnchored = isAnchored;
   }

   @Override
   public double process(@Nonnull Density.Context context) {
      if (this.planeNormal.length() == 0.0) {
         return 0.0;
      } else if (this.isAnchored) {
         return this.processAnchored(context.position.x, context.position.y, context.position.z, context);
      } else {
         double distance = 0.0;
         if (this.isPlaneHorizontal) {
            distance = context.position.y;
         } else {
            Vector3d nearestPoint = VectorUtil.nearestPointOnLine3d(context.position, ZERO_VECTOR, this.planeNormal);
            distance = nearestPoint.length();
         }

         return this.distanceCurve.get(distance);
      }
   }

   private double processAnchored(double x, double y, double z, @Nullable Density.Context context) {
      if (context == null) {
         return 0.0;
      } else {
         Vector3d position = new Vector3d(x, y, z);
         Vector3d p0 = context.densityAnchor;
         if (p0 == null) {
            return 0.0;
         } else {
            double distance = 0.0;
            if (this.isPlaneHorizontal) {
               distance = Math.abs(p0.y - position.y);
            }

            Vector3d pos = position.clone().addScaled(p0, -1.0);
            Vector3d vectorFromPlane = VectorUtil.nearestPointOnLine3d(pos, ZERO_VECTOR, this.planeNormal);
            distance = vectorFromPlane.length();
            return this.distanceCurve.get(distance);
         }
      }
   }
}
