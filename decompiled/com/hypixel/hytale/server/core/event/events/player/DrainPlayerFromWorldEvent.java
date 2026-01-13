package com.hypixel.hytale.server.core.event.events.player;

import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.event.IEvent;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class DrainPlayerFromWorldEvent implements IEvent<String> {
   private final Holder<EntityStore> holder;
   private World world;
   private Transform transform;

   public DrainPlayerFromWorldEvent(Holder<EntityStore> holder, World world, Transform transform) {
      this.holder = holder;
      this.world = world;
      this.transform = transform;
   }

   public Holder<EntityStore> getHolder() {
      return this.holder;
   }

   public World getWorld() {
      return this.world;
   }

   public void setWorld(World world) {
      this.world = world;
   }

   public Transform getTransform() {
      return this.transform;
   }

   public void setTransform(Transform transform) {
      this.transform = transform;
   }

   @Nonnull
   @Override
   public String toString() {
      return "DrainPlayerFromWorldEvent{world=" + this.world + ", transform=" + this.transform + "} " + super.toString();
   }
}
