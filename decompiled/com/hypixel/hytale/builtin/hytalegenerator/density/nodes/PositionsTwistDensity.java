package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.VectorUtil;
import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.math.vector.Vector3d;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PositionsTwistDensity extends Density {
   @Nullable
   private Density input;
   @Nullable
   private PositionProvider positions;
   private Double2DoubleFunction twistCurve;
   private Vector3d twistAxis;
   private double maxDistance;
   private boolean distanceNormalized;
   private boolean zeroPositionsY;

   public PositionsTwistDensity(
      @Nullable Density input,
      @Nullable PositionProvider positions,
      @Nonnull Double2DoubleFunction twistCurve,
      @Nonnull Vector3d twistAxis,
      double maxDistance,
      boolean distanceNormalized,
      boolean zeroPositionsY
   ) {
      if (maxDistance < 0.0) {
         throw new IllegalArgumentException();
      } else {
         if (twistAxis.length() < 1.0E-9) {
            twistAxis = new Vector3d(0.0, 1.0, 0.0);
         }

         this.input = input;
         this.positions = positions;
         this.twistCurve = twistCurve;
         this.twistAxis = twistAxis;
         this.maxDistance = maxDistance;
         this.distanceNormalized = distanceNormalized;
         this.zeroPositionsY = zeroPositionsY;
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
         Vector3d queryPosition = context.position.clone();
         if (this.zeroPositionsY) {
            queryPosition.y = 0.0;
            min.y = -1.0;
            max.y = 1.0;
         }

         ArrayList<Vector3d> warpVectors = new ArrayList<>(10);
         ArrayList<Double> warpDistances = new ArrayList<>(10);
         Consumer<Vector3d> consumer = p -> {
            double distance = p.distanceTo(queryPosition);
            if (!(distance > this.maxDistance)) {
               double normalizedDistance = distance / this.maxDistance;
               Vector3d warpVectorx = samplePoint.clone();
               double twistAngle;
               if (this.distanceNormalized) {
                  twistAngle = this.twistCurve.applyAsDouble(normalizedDistance);
               } else {
                  twistAngle = this.twistCurve.applyAsDouble(distance);
               }

               twistAngle /= 180.0;
               twistAngle *= Math.PI;
               warpVectorx.subtract(p);
               VectorUtil.rotateAroundAxis(warpVectorx, this.twistAxis, twistAngle);
               warpVectorx.add(p);
               warpVectorx.subtract(samplePoint);
               warpVectors.add(warpVectorx);
               if (this.distanceNormalized) {
                  warpDistances.add(normalizedDistance);
               } else {
                  warpDistances.add(distance);
               }
            }
         };
         PositionProvider.Context positionsContext = new PositionProvider.Context(min, max, consumer, null, context.workerId);
         this.positions.positionsIn(positionsContext);
         if (warpVectors.isEmpty()) {
            return this.input.process(context);
         } else if (warpVectors.size() == 1) {
            Vector3d warpVector = warpVectors.getFirst();
            samplePoint.add(warpVector);
            Density.Context childContext = new Density.Context(context);
            childContext.position = samplePoint;
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

            Density.Context childContext = new Density.Context(context);
            childContext.position = samplePoint;
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
}
