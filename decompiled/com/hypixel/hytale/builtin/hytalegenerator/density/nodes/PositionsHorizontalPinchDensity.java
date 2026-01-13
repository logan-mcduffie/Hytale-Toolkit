package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.framework.math.Calculator;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.vector.Vector3d;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.ArrayList;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public class PositionsHorizontalPinchDensity extends Density {
   @Nonnull
   private Density input;
   @Nonnull
   private final PositionProvider positions;
   @Nonnull
   private final Double2DoubleFunction pinchCurve;
   @Nonnull
   private final WorkerIndexer.Data<PositionsHorizontalPinchDensity.Cache> threadData;
   private final double maxDistance;
   private final boolean distanceNormalized;
   private final double positionsMinY;
   private final double positionsMaxY;

   public PositionsHorizontalPinchDensity(
      @Nonnull Density input,
      @Nonnull PositionProvider positions,
      @Nonnull Double2DoubleFunction pinchCurve,
      double maxDistance,
      boolean distanceNormalized,
      double positionsMinY,
      double positionsMaxY,
      int threadCount
   ) {
      if (maxDistance < 0.0) {
         throw new IllegalArgumentException();
      } else {
         if (positionsMinY > positionsMaxY) {
            positionsMinY = positionsMaxY;
         }

         this.input = input;
         this.positions = positions;
         this.pinchCurve = pinchCurve;
         this.maxDistance = maxDistance;
         this.distanceNormalized = distanceNormalized;
         this.positionsMinY = positionsMinY;
         this.positionsMaxY = positionsMaxY;
         this.threadData = new WorkerIndexer.Data<>(threadCount, PositionsHorizontalPinchDensity.Cache::new);
      }
   }

   @Override
   public double process(@Nonnull Density.Context context) {
      if (this.input == null) {
         return 0.0;
      } else if (this.positions == null) {
         return this.input.process(context);
      } else {
         PositionsHorizontalPinchDensity.Cache cache = this.threadData.get(context.workerId);
         Vector3d warpVector;
         if (cache.x == context.position.x && cache.z == context.position.z && !cache.hasValue) {
            warpVector = cache.warpVector;
         } else {
            warpVector = this.calculateWarpVector(context);
            cache.warpVector = warpVector;
         }

         Vector3d position = new Vector3d(warpVector.x + context.position.x, warpVector.y + context.position.y, warpVector.z + context.position.z);
         Density.Context childContext = new Density.Context(context);
         childContext.position = position;
         return this.input.process(childContext);
      }
   }

   @Override
   public void setInputs(@Nonnull Density[] inputs) {
      if (inputs.length == 0) {
         this.input = new ConstantValueDensity(0.0);
      }

      this.input = inputs[0];
   }

   public Vector3d calculateWarpVector(@Nonnull Density.Context context) {
      Vector3d position = context.position;
      Vector3d min = new Vector3d(position.x - this.maxDistance, this.positionsMinY, position.z - this.maxDistance);
      Vector3d max = new Vector3d(position.x + this.maxDistance, this.positionsMaxY, position.z + this.maxDistance);
      Vector3d samplePoint = position.clone();
      ArrayList<Vector3d> warpVectors = new ArrayList<>(10);
      ArrayList<Double> warpDistances = new ArrayList<>(10);
      Consumer<Vector3d> consumer = iteratedPosition -> {
         double distance = Calculator.distance(iteratedPosition.x, iteratedPosition.z, samplePoint.x, samplePoint.z);
         if (!(distance > this.maxDistance)) {
            double normalizedDistance = distance / this.maxDistance;
            Vector3d warpVectorx = iteratedPosition.clone().addScaled(samplePoint, -1.0);
            warpVectorx.setY(0.0);
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
      this.positions.positionsIn(positionsContext);
      if (warpVectors.isEmpty()) {
         return new Vector3d(0.0, 0.0, 0.0);
      } else if (warpVectors.size() == 1) {
         return warpVectors.getFirst();
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

         Vector3d totalWarpVector = new Vector3d();

         for (int i = 0; i < possiblePointsSize; i++) {
            double weight = weights.get(i) / totalWeight;
            Vector3d warpVector = warpVectors.get(i);
            warpVector.scale(weight);
            totalWarpVector.add(warpVector);
         }

         return totalWarpVector;
      }
   }

   private static class Cache {
      double x;
      double z;
      Vector3d warpVector;
      boolean hasValue;
   }
}
