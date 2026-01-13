package com.hypixel.hytale.server.core.modules.debug;

import com.hypixel.hytale.math.matrix.Matrix4d;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.DebugShape;
import com.hypixel.hytale.protocol.packets.player.ClearDebugShapes;
import com.hypixel.hytale.protocol.packets.player.DisplayDebug;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageSystems;
import com.hypixel.hytale.server.core.modules.splitvelocity.SplitVelocity;
import com.hypixel.hytale.server.core.modules.splitvelocity.VelocityConfig;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class DebugUtils {
   public static boolean DISPLAY_FORCES = false;

   public static void add(@Nonnull World world, @Nonnull DebugShape shape, @Nonnull Matrix4d matrix, @Nonnull Vector3f color, float time, boolean fade) {
      DisplayDebug packet = new DisplayDebug(shape, matrix.asFloatData(), new com.hypixel.hytale.protocol.Vector3f(color.x, color.y, color.z), time, fade, null);

      for (PlayerRef playerRef : world.getPlayerRefs()) {
         playerRef.getPacketHandler().write(packet);
      }
   }

   public static void addFrustum(
      @Nonnull World world, @Nonnull Matrix4d matrix, @Nonnull Matrix4d frustumProjection, @Nonnull Vector3f color, float time, boolean fade
   ) {
      DisplayDebug packet = new DisplayDebug(
         DebugShape.Frustum,
         matrix.asFloatData(),
         new com.hypixel.hytale.protocol.Vector3f(color.x, color.y, color.z),
         time,
         fade,
         frustumProjection.asFloatData()
      );

      for (PlayerRef playerRef : world.getPlayerRefs()) {
         playerRef.getPacketHandler().write(packet);
      }
   }

   public static void clear(@Nonnull World world) {
      ClearDebugShapes packet = new ClearDebugShapes();

      for (PlayerRef playerRef : world.getPlayerRefs()) {
         playerRef.getPacketHandler().write(packet);
      }
   }

   public static void addArrow(@Nonnull World world, @Nonnull Matrix4d baseMatrix, @Nonnull Vector3f color, double length, float time, boolean fade) {
      double adjustedLength = length - 0.3;
      if (adjustedLength > 0.0) {
         Matrix4d matrix = new Matrix4d(baseMatrix);
         matrix.translate(0.0, adjustedLength * 0.5, 0.0);
         matrix.scale(0.1F, adjustedLength, 0.1F);
         add(world, DebugShape.Cylinder, matrix, color, time, fade);
      }

      Matrix4d matrix = new Matrix4d(baseMatrix);
      matrix.translate(0.0, adjustedLength + 0.15, 0.0);
      matrix.scale(0.3F, 0.3F, 0.3F);
      add(world, DebugShape.Cone, matrix, color, time, fade);
   }

   public static void addSphere(@Nonnull World world, @Nonnull Vector3d pos, @Nonnull Vector3f color, double scale, float time) {
      Matrix4d matrix = makeMatrix(pos, scale);
      add(world, DebugShape.Sphere, matrix, color, time, true);
   }

   public static void addCone(@Nonnull World world, @Nonnull Vector3d pos, @Nonnull Vector3f color, double scale, float time) {
      Matrix4d matrix = makeMatrix(pos, scale);
      add(world, DebugShape.Cone, matrix, color, time, true);
   }

   public static void addCube(@Nonnull World world, @Nonnull Vector3d pos, @Nonnull Vector3f color, double scale, float time) {
      Matrix4d matrix = makeMatrix(pos, scale);
      add(world, DebugShape.Cube, matrix, color, time, true);
   }

   public static void addCylinder(@Nonnull World world, @Nonnull Vector3d pos, @Nonnull Vector3f color, double scale, float time) {
      Matrix4d matrix = makeMatrix(pos, scale);
      add(world, DebugShape.Cylinder, matrix, color, time, true);
   }

   public static void addArrow(@Nonnull World world, @Nonnull Vector3d position, @Nonnull Vector3d direction, @Nonnull Vector3f color, float time, boolean fade) {
      Vector3d directionClone = direction.clone();
      Matrix4d tmp = new Matrix4d();
      Matrix4d matrix = new Matrix4d();
      matrix.identity();
      matrix.translate(position);
      double angleY = Math.atan2(directionClone.z, directionClone.x);
      matrix.rotateAxis(angleY + (Math.PI / 2), 0.0, 1.0, 0.0, tmp);
      double angleX = Math.atan2(Math.sqrt(directionClone.x * directionClone.x + directionClone.z * directionClone.z), directionClone.y);
      matrix.rotateAxis(angleX, 1.0, 0.0, 0.0, tmp);
      addArrow(world, matrix, color, directionClone.length(), time, fade);
   }

   public static void addForce(@Nonnull World world, @Nonnull Vector3d position, @Nonnull Vector3d force, @Nullable VelocityConfig velocityConfig) {
      if (DISPLAY_FORCES) {
         Vector3d forceClone = force.clone();
         if (velocityConfig == null || SplitVelocity.SHOULD_MODIFY_VELOCITY) {
            forceClone.x = forceClone.x / DamageSystems.HackKnockbackValues.PLAYER_KNOCKBACK_SCALE;
            forceClone.z = forceClone.z / DamageSystems.HackKnockbackValues.PLAYER_KNOCKBACK_SCALE;
         }

         Matrix4d tmp = new Matrix4d();
         Matrix4d matrix = new Matrix4d();
         matrix.identity();
         matrix.translate(position);
         double angleY = Math.atan2(forceClone.z, forceClone.x);
         matrix.rotateAxis(angleY + (Math.PI / 2), 0.0, 1.0, 0.0, tmp);
         double angleX = Math.atan2(Math.sqrt(forceClone.x * forceClone.x + forceClone.z * forceClone.z), forceClone.y);
         matrix.rotateAxis(angleX, 1.0, 0.0, 0.0, tmp);
         Random random = new Random();
         Vector3f color = new Vector3f(random.nextFloat(), random.nextFloat(), random.nextFloat());
         addArrow(world, matrix, color, forceClone.length(), 10.0F, true);
      }
   }

   @Nonnull
   private static Matrix4d makeMatrix(@Nonnull Vector3d pos, double scale) {
      Matrix4d matrix = new Matrix4d();
      matrix.identity();
      matrix.translate(pos);
      matrix.scale(scale, scale, scale);
      return matrix;
   }
}
