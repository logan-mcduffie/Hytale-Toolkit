package com.hypixel.hytale.builtin.portals.utils.posqueries;

import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.server.core.universe.world.World;

public interface PositionPredicate {
   boolean test(World var1, Vector3d var2);
}
