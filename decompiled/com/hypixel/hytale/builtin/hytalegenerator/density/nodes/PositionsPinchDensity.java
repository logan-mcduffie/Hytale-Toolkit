package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.math.vector.Vector3d;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PositionsPinchDensity extends Density {
   @Nullable
   private Density input;
   @Nullable
   private PositionProvider positions;
   private Double2DoubleFunction pinchCurve;
   private double maxDistance;
   private boolean distanceNormalized;

   public PositionsPinchDensity(
      @Nullable Density input, @Nullable PositionProvider positions, @Nonnull Double2DoubleFunction pinchCurve, double maxDistance, boolean distanceNormalized
   ) {
      if (maxDistance < 0.0) {
         throw new IllegalArgumentException();
      } else {
         this.input = input;
         this.positions = positions;
         this.pinchCurve = pinchCurve;
         this.maxDistance = maxDistance;
         this.distanceNormalized = distanceNormalized;
      }
   }

   @Override
   public double process(@Nonnull Density.Context context) {
      if (this.input == null) {
         return 0.0;
      } else if (this.positions == null) {
         return this.input.process(context);
      } else {
         Vector3d min = new Vector3d(context.position.x - this.maxDistance, context.position.y - this.maxDistance, context.position.z - this.maxDistance);
         Vector3d max = new Vector3d(context.position.x + this.maxDistance, context.position.y + this.maxDistance, context.position.z + this.maxDistance);
         Vector3d samplePoint = context.position.clone();
         ArrayList<Vector3d> warpVectors = new ArrayList<>(10);
         ArrayList<Double> warpDistances = new ArrayList<>(10);
         Consumer<Vector3d> consumer = p -> {
            double distance = p.distanceTo(samplePoint);
            if (!(distance > this.maxDistance)) {
               double normalizedDistance = distance / this.maxDistance;
               Vector3d warpVectorx = p.clone().addScaled(samplePoint, -1.0);
               double radialDistance;
               if (this.distanceNormalized) {
                  radialDistance = this.pinchCurve.applyAsDouble(normalizedDistance);
                  radialDistance *= this.maxDistance;
               } else {
                  radialDistance = this.pinchCurve.applyAsDouble(distance);
               }

               if (!(Math.abs(warpVectorx.length()) < 1.0E-9)) {
                  warpVectorx.setLength(radialDistance);
               }

               warpVectors.add(warpVectorx);
               warpDistances.add(normalizedDistance);
            }
         };
         PositionProvider.Context positionsContext = new PositionProvider.Context();
         positionsContext.minInclusive = min;
         positionsContext.maxExclusive = max;
         positionsContext.consumer = consumer;
         positionsContext.workerId = context.workerId;
         this.positions.positionsIn(positionsContext);
         if (warpVectors.isEmpty()) {
            return this.input.process(context);
         } else if (warpVectors.size() == 1) {
            Vector3d warpVector = warpVectors.getFirst();
            samplePoint.add(warpVector);
            Density.Context childContext = new Density.Context(context);
            context.position = samplePoint;
            return this.input.process(childContext);
         } else {
            int possiblePointsSize = warpVectors.size();
            ArrayList<Double> weights = new ArrayList<>(warpDistances.size());
            double totalWeight = 0.0;

            for (int i = 0; i < possiblePointsSize; i++) {
               double distance = warpDistances.get(i);
               double weight = 1.0 - distance;
               weights.add(weight);
               totalWeight += weight;
            }

            for (int i = 0; i < possiblePointsSize; i++) {
               double weight = weights.get(i) / totalWeight;
               Vector3d warpVector = warpVectors.get(i);
               warpVector.scale(weight);
               samplePoint.add(warpVector);
            }

            return this.input.process(context);
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
}
