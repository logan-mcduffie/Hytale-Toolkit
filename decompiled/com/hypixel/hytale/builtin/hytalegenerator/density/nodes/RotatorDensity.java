package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class RotatorDensity extends Density {
   private static final Vector3d Y_AXIS = new Vector3d(0.0, 1.0, 0.0);
   @Nullable
   private Density input;
   private Vector3d rotationAxis;
   private Vector3d tiltAxis;
   private double tiltAngle;
   private final double spinAngle;
   @Nonnull
   private final RotatorDensity.SpecialCase axisSpecialCase;

   public RotatorDensity(@Nonnull Density input, @Nonnull Vector3d newYAxis, double spinAngle) {
      this.input = input;
      this.spinAngle = spinAngle * Math.PI / 180.0;
      Vector3d yAxis = new Vector3d(0.0, 1.0, 0.0);
      this.rotationAxis = newYAxis.cross(yAxis);
      if (this.rotationAxis.length() < 1.0E-8) {
         this.rotationAxis = yAxis;
         if (newYAxis.dot(yAxis) < 0.0) {
            this.axisSpecialCase = RotatorDensity.SpecialCase.INVERTED_Y_AXIS;
         } else {
            this.axisSpecialCase = RotatorDensity.SpecialCase.Y_AXIS;
         }
      } else {
         this.axisSpecialCase = RotatorDensity.SpecialCase.NONE;
      }

      this.rotationAxis.normalize();
      if (this.axisSpecialCase == RotatorDensity.SpecialCase.INVERTED_Y_AXIS || this.axisSpecialCase == RotatorDensity.SpecialCase.Y_AXIS) {
         this.tiltAxis = new Vector3d();
         this.tiltAngle = 0.0;
      }

      this.tiltAxis = yAxis.cross(newYAxis);
      this.tiltAngle = Math.acos(newYAxis.dot(yAxis) / (newYAxis.length() * yAxis.length()));
   }

   @Override
   public double process(@Nonnull Density.Context context) {
      if (this.input == null) {
         return 0.0;
      } else {
         Vector3d childPosition = context.position.clone();
         switch (this.axisSpecialCase) {
            case INVERTED_Y_AXIS:
               childPosition.scale(-1.0);
            case NONE:
               VectorUtil.rotateAroundAxis(childPosition, this.tiltAxis, this.tiltAngle);
            case Y_AXIS:
            default:
               VectorUtil.rotateAroundAxis(childPosition, Y_AXIS, this.spinAngle);
               Density.Context childContext = new Density.Context(context);
               childContext.position = childPosition;
               return this.input.process(childContext);
         }
      }
   }

   @Override
   public void setInputs(@Nonnull Density[] inputs) {
      if (inputs.length == 0) {
         this.input = null;
      }

      this.input = inputs[0];
   }

   private static enum SpecialCase {
      NONE,
      Y_AXIS,
      INVERTED_Y_AXIS;
   }
}
