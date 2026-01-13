package com.hypixel.hytale.builtin.portals.systems.voidevent;

import com.hypixel.hytale.builtin.portals.components.voidevent.VoidEvent;
import com.hypixel.hytale.builtin.portals.components.voidevent.config.VoidEventConfig;
import com.hypixel.hytale.builtin.portals.resources.PortalWorld;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.system.DelayedSystem;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class StartVoidEventInFragmentSystem extends DelayedSystem<EntityStore> {
   public StartVoidEventInFragmentSystem() {
      super(1.0F);
   }

   @Override
   public void delayedTick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
      PortalWorld portalWorld = store.getResource(PortalWorld.getResourceType());
      if (portalWorld.exists()) {
         if (portalWorld.getPortalType().isVoidInvasionEnabled()) {
            World world = store.getExternalData().getWorld();
            VoidEventConfig voidEventConfig = portalWorld.getVoidEventConfig();
            int timeLimitSeconds = portalWorld.getTimeLimitSeconds();
            int shouldStartAfter = voidEventConfig.getShouldStartAfterSeconds(timeLimitSeconds);
            int elapsedSeconds = (int)Math.ceil(portalWorld.getElapsedSeconds(world));
            Ref<EntityStore> voidEventRef = portalWorld.getVoidEventRef();
            boolean exists = voidEventRef != null;
            boolean shouldExist = elapsedSeconds >= shouldStartAfter;
            if (exists && !shouldExist) {
               store.removeEntity(voidEventRef, RemoveReason.REMOVE);
            }

            if (shouldExist && !exists) {
               Holder<EntityStore> holder = EntityStore.REGISTRY.newHolder();
               holder.addComponent(VoidEvent.getComponentType(), new VoidEvent());
               Ref<EntityStore> spawnedEventRef = store.addEntity(holder, AddReason.SPAWN);
               portalWorld.setVoidEventRef(spawnedEventRef);
            }
         }
      }
   }
}
