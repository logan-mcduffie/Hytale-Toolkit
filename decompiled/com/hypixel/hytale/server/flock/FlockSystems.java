package com.hypixel.hytale.server.flock;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.group.EntityGroup;
import com.hypixel.hytale.server.core.event.events.ecs.ChangeGameModeEvent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.UUID;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class FlockSystems {
   public static class EntityRemoved extends RefSystem<EntityStore> {
      private final ComponentType<EntityStore, UUIDComponent> flockIdComponentType = UUIDComponent.getComponentType();
      private final ComponentType<EntityStore, EntityGroup> entityGroupComponentType = EntityGroup.getComponentType();
      private final ComponentType<EntityStore, Flock> flockComponentType;
      private final Archetype<EntityStore> archetype;

      public EntityRemoved(ComponentType<EntityStore, Flock> flockComponentType) {
         this.flockComponentType = flockComponentType;
         this.archetype = Archetype.of(this.flockIdComponentType, this.entityGroupComponentType, flockComponentType);
      }

      @Override
      public Query<EntityStore> getQuery() {
         return this.archetype;
      }

      @Override
      public void onEntityAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         UUID flockId = store.getComponent(ref, this.flockIdComponentType).getUuid();
         EntityGroup entityGroup = store.getComponent(ref, this.entityGroupComponentType);
         Flock flock = store.getComponent(ref, this.flockComponentType);
         switch (reason) {
            case REMOVE:
               entityGroup.setDissolved(true);

               for (Ref<EntityStore> memberRef : entityGroup.getMemberList()) {
                  commandBuffer.removeComponent(memberRef, FlockMembership.getComponentType());
                  TransformComponent transformComponent = commandBuffer.getComponent(memberRef, TransformComponent.getComponentType());

                  assert transformComponent != null;

                  transformComponent.markChunkDirty(commandBuffer);
               }

               flock.setRemovedStatus(Flock.FlockRemovedStatus.DISSOLVED);
               entityGroup.clear();
               if (flock.isTrace()) {
                  FlockPlugin.get().getLogger().at(Level.INFO).log("Flock %s: Dissolving", flockId);
               }
               break;
            case UNLOAD:
               flock.setRemovedStatus(Flock.FlockRemovedStatus.UNLOADED);
               entityGroup.clear();
               if (flock.isTrace()) {
                  FlockPlugin.get().getLogger().at(Level.INFO).log("Flock %s: Flock unloaded, size=%s", flockId, entityGroup.size());
               }
         }
      }
   }

   public static class PlayerChangeGameModeEventSystem extends EntityEventSystem<EntityStore, ChangeGameModeEvent> {
      public PlayerChangeGameModeEventSystem() {
         super(ChangeGameModeEvent.class);
      }

      public void handle(
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer,
         @Nonnull ChangeGameModeEvent event
      ) {
         if (event.getGameMode() != GameMode.Adventure) {
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            commandBuffer.tryRemoveComponent(ref, FlockMembership.getComponentType());
         }
      }

      @Nullable
      @Override
      public Query<EntityStore> getQuery() {
         return Archetype.empty();
      }
   }

   public static class Ticking extends EntityTickingSystem<EntityStore> {
      private final ComponentType<EntityStore, Flock> flockComponentType;

      public Ticking(ComponentType<EntityStore, Flock> flockComponentType) {
         this.flockComponentType = flockComponentType;
      }

      @Override
      public Query<EntityStore> getQuery() {
         return this.flockComponentType;
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
         Flock flock = archetypeChunk.getComponent(index, this.flockComponentType);
         flock.swapDamageDataBuffers();
      }
   }
}
