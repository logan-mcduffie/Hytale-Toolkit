package com.hypixel.hytale.builtin.adventure.npcobjectives.systems;

import com.hypixel.hytale.builtin.adventure.npcobjectives.resources.KillTrackerResource;
import com.hypixel.hytale.builtin.adventure.npcobjectives.transaction.KillTaskTransaction;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class KillTrackerSystem extends DeathSystems.OnDeathSystem {
   @Nullable
   @Override
   public Query<EntityStore> getQuery() {
      return NPCEntity.getComponentType();
   }

   public void onComponentAdded(
      @Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
   ) {
      NPCEntity entity = store.getComponent(ref, NPCEntity.getComponentType());
      KillTrackerResource tracker = store.getResource(KillTrackerResource.getResourceType());
      List<KillTaskTransaction> killTasks = tracker.getKillTasks();
      int size = killTasks.size();

      for (int i = size - 1; i >= 0; i--) {
         KillTaskTransaction entry = killTasks.get(i);
         entry.getTask().checkKilledEntity(store, ref, entry.getObjective(), entity, component.getDeathInfo());
      }
   }
}
