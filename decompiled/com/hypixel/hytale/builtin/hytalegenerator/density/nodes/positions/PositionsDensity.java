package com.hypixel.hytale.builtin.hytalegenerator.density.nodes.positions;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.positions.distancefunctions.DistanceFunction;
import com.hypixel.hytale.builtin.hytalegenerator.density.nodes.positions.returntypes.ReturnType;
import com.hypixel.hytale.builtin.hytalegenerator.positionproviders.PositionProvider;
import com.hypixel.hytale.math.vector.Vector3d;
import it.unimi.dsi.fastutil.doubles.Double2DoubleFunction;
import java.util.function.Consumer;
import javax.annotation.Nonnull;

public class PositionsDensity extends Density {
   @Nonnull
   private final PositionProvider positionProvider;
   private final double maxDistance;
   private final double maxDistanceRaw;
   @Nonnull
   private final ReturnType returnType;
   @Nonnull
   private final DistanceFunction distanceFunction;

   public PositionsDensity(
      @Nonnull PositionProvider positionsField, @Nonnull ReturnType returnType, @Nonnull DistanceFunction distanceFunction, double maxDistance
   ) {
      if (maxDistance < 0.0) {
         throw new IllegalArgumentException("negative distance");
      } else {
         this.positionProvider = positionsField;
         this.maxDistance = maxDistance;
         this.maxDistanceRaw = maxDistance * maxDistance;
         this.returnType = returnType;
         this.distanceFunction = distanceFunction;
      }
   }

   @Nonnull
   public static Double2DoubleFunction cellNoiseDistanceFunction(double maxDistance) {
      return d -> d / maxDistance - 1.0;
   }

   @Override
   public double process(@Nonnull Density.Context context) {
      Vector3d min = context.position.clone().subtract(this.maxDistance);
      Vector3d max = context.position.clone().add(this.maxDistance);
      double[] distance = new double[]{Double.MAX_VALUE, Double.MAX_VALUE};
      boolean[] hasClosestPoint = new boolean[2];
      Vector3d closestPoint = new Vector3d();
      Vector3d previousClosestPoint = new Vector3d();
      Vector3d localPoint = new Vector3d();
      Consumer<Vector3d> positionsConsumer = providedPoint -> {
         localPoint.x = providedPoint.x - context.position.x;
         localPoint.y = providedPoint.y - context.position.y;
         localPoint.z = providedPoint.z - context.position.z;
         double newDistance = this.distanceFunction.getDistance(localPoint);
         if (!(this.maxDistanceRaw < newDistance)) {
            distance[1] = Math.max(Math.min(distance[1], newDistance), distance[0]);
            if (newDistance < distance[0]) {
               distance[0] = newDistance;
               previousClosestPoint.assign(closestPoint);
               closestPoint.assign(providedPoint);
               hasClosestPoint[1] = hasClosestPoint[0];
               hasClosestPoint[0] = true;
            }
         }
      };
      PositionProvider.Context positionsContext = new PositionProvider.Context();
      positionsContext.minInclusive = min;
      positionsContext.maxExclusive = max;
      positionsContext.consumer = positionsConsumer;
      positionsContext.workerId = context.workerId;
      this.positionProvider.positionsIn(positionsContext);
      distance[0] = Math.sqrt(distance[0]);
      distance[1] = Math.sqrt(distance[1]);
      return this.returnType
         .get(
            distance[0],
            distance[1],
            context.position.clone(),
            hasClosestPoint[0] ? closestPoint : null,
            hasClosestPoint[1] ? previousClosestPoint : null,
            context
         );
   }
}
