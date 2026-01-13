package com.hypixel.hytale.builtin.portals.utils.posqueries.predicates;

import com.hypixel.hytale.builtin.portals.utils.posqueries.PositionPredicate;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.World;

public final class NotNearPoint implements PositionPredicate {
   private final Vector3d point;
   private final double radiusSq;

   public NotNearPoint(Vector3d point, double radius) {
      this.point = point;
      this.radiusSq = radius * radius;
   }

   @Override
   public boolean test(World world, Vector3d origin) {
      return origin.distanceSquaredTo(this.point) >= this.radiusSq;
   }
}
