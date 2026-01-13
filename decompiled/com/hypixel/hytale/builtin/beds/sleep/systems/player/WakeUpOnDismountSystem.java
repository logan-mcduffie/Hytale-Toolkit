package com.hypixel.hytale.builtin.beds.sleep.systems.player;

import com.hypixel.hytale.builtin.beds.sleep.components.PlayerSomnolence;
import com.hypixel.hytale.builtin.mounts.MountedComponent;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.protocol.BlockMountType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class WakeUpOnDismountSystem extends RefChangeSystem<EntityStore, MountedComponent> {
   @Override
   public ComponentType<EntityStore, MountedComponent> componentType() {
      return MountedComponent.getComponentType();
   }

   @Override
   public Query<EntityStore> getQuery() {
      return MountedComponent.getComponentType();
   }

   public void onComponentAdded(
      @NonNullDecl Ref<EntityStore> ref,
      @NonNullDecl MountedComponent component,
      @NonNullDecl Store<EntityStore> store,
      @NonNullDecl CommandBuffer<EntityStore> commandBuffer
   ) {
   }

   public void onComponentSet(
      @NonNullDecl Ref<EntityStore> ref,
      @NullableDecl MountedComponent oldComponent,
      @NonNullDecl MountedComponent newComponent,
      @NonNullDecl Store<EntityStore> store,
      @NonNullDecl CommandBuffer<EntityStore> commandBuffer
   ) {
   }

   public void onComponentRemoved(
      @NonNullDecl Ref<EntityStore> ref,
      @NonNullDecl MountedComponent component,
      @NonNullDecl Store<EntityStore> store,
      @NonNullDecl CommandBuffer<EntityStore> commandBuffer
   ) {
      if (component.getBlockMountType() == BlockMountType.Bed) {
         commandBuffer.putComponent(ref, PlayerSomnolence.getComponentType(), PlayerSomnolence.AWAKE);
      }
   }
}
