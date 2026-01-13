package com.hypixel.hytale.builtin.beds.sleep.systems.player;

import com.hypixel.hytale.builtin.beds.sleep.components.SleepTracker;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class RegisterTrackerSystem extends HolderSystem<EntityStore> {
   @Override
   public void onEntityAdd(@NonNullDecl Holder<EntityStore> holder, @NonNullDecl AddReason reason, @NonNullDecl Store<EntityStore> store) {
      holder.ensureComponent(SleepTracker.getComponentType());
   }

   @Override
   public void onEntityRemoved(@NonNullDecl Holder<EntityStore> holder, @NonNullDecl RemoveReason reason, @NonNullDecl Store<EntityStore> store) {
   }

   @NullableDecl
   @Override
   public Query<EntityStore> getQuery() {
      return PlayerRef.getComponentType();
   }
}
