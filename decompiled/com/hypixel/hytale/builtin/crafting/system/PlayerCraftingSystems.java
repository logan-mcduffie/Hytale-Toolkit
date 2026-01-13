package com.hypixel.hytale.builtin.crafting.system;

import com.hypixel.hytale.builtin.crafting.component.CraftingManager;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public class PlayerCraftingSystems {
   public static class CraftingManagerAddSystem extends HolderSystem<EntityStore> {
      private final ComponentType<EntityStore, Player> playerComponentType = Player.getComponentType();
      private final ComponentType<EntityStore, CraftingManager> craftingManagerComponentType;

      public CraftingManagerAddSystem(ComponentType<EntityStore, CraftingManager> craftingManagerComponentType) {
         this.craftingManagerComponentType = craftingManagerComponentType;
      }

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         Player player = holder.getComponent(Player.getComponentType());
         if (player == null) {
            throw new UnsupportedOperationException("Cannot have null player component during crafting system creation");
         } else {
            holder.ensureComponent(this.craftingManagerComponentType);
         }
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
         CraftingManager craftingManager = holder.getComponent(this.craftingManagerComponentType);
         if (craftingManager != null) {
            Player player = holder.getComponent(this.playerComponentType);

            try {
               Ref<EntityStore> ref = player.getReference();
               craftingManager.cancelAllCrafting(ref, store);
            } finally {
               World world = store.getExternalData().getWorld();
               if (world.getWorldConfig().isSavingPlayers() && player != null) {
                  player.saveConfig(world, holder);
               }
            }
         }
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.playerComponentType;
      }
   }

   public static class PlayerCraftingSystem extends EntityTickingSystem<EntityStore> {
      private final ComponentType<EntityStore, CraftingManager> craftingManagerComponentType;

      public PlayerCraftingSystem(ComponentType<EntityStore, CraftingManager> craftingManagerComponentType) {
         this.craftingManagerComponentType = craftingManagerComponentType;
      }

      @Override
      public Query<EntityStore> getQuery() {
         return this.craftingManagerComponentType;
      }

      @Override
      public boolean isParallel(int archetypeChunkSize, int taskCount) {
         return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
      }

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
         CraftingManager craftingManagerComponent = archetypeChunk.getComponent(index, this.craftingManagerComponentType);
         craftingManagerComponent.tick(ref, commandBuffer, dt);
      }
   }
}
