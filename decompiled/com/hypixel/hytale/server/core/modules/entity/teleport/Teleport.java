package com.hypixel.hytale.server.core.modules.entity.teleport;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Teleport implements Component<EntityStore> {
   @Nullable
   private final World world;
   @Nonnull
   private final Vector3d position = new Vector3d();
   @Nonnull
   private final Vector3f rotation = new Vector3f();
   @Nullable
   private Vector3f headRotation;
   private boolean resetVelocity = true;

   @Nonnull
   public static ComponentType<EntityStore, Teleport> getComponentType() {
      return EntityModule.get().getTeleportComponentType();
   }

   public Teleport(@Nullable World world, @Nonnull Transform transform) {
      this(world, transform.getPosition(), transform.getRotation());
   }

   public Teleport(@Nullable World world, @Nonnull Vector3d position, @Nonnull Vector3f rotation) {
      this.world = world;
      this.position.assign(position);
      this.rotation.assign(rotation);
   }

   public Teleport(@Nonnull Transform transform) {
      this(null, transform.getPosition(), transform.getRotation());
   }

   public Teleport(@Nonnull Vector3d position, @Nonnull Vector3f rotation) {
      this.world = null;
      this.position.assign(position);
      this.rotation.assign(rotation);
   }

   @Nonnull
   public Teleport withHeadRotation(@Nonnull Vector3f headRotation) {
      this.headRotation = headRotation;
      return this;
   }

   public Teleport withResetRoll() {
      this.rotation.setRoll(0.0F);
      return this;
   }

   public Teleport withoutVelocityReset() {
      this.resetVelocity = false;
      return this;
   }

   @Nullable
   public World getWorld() {
      return this.world;
   }

   @Nonnull
   public Vector3d getPosition() {
      return this.position;
   }

   @Nonnull
   public Vector3f getRotation() {
      return this.rotation;
   }

   @Nullable
   public Vector3f getHeadRotation() {
      return this.headRotation;
   }

   public boolean isResetVelocity() {
      return this.resetVelocity;
   }

   @Nonnull
   public Teleport clone() {
      return new Teleport(this.world, this.position, this.rotation);
   }
}
