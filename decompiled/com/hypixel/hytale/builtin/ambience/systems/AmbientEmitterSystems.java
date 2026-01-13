package com.hypixel.hytale.builtin.ambience.systems;

import com.hypixel.hytale.builtin.ambience.AmbiencePlugin;
import com.hypixel.hytale.builtin.ambience.components.AmbientEmitterComponent;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.NonSerialized;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.server.core.asset.type.soundevent.config.SoundEvent;
import com.hypixel.hytale.server.core.modules.entity.component.AudioComponent;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AmbientEmitterSystems {
   public static class EntityAdded extends HolderSystem<EntityStore> {
      private final Query<EntityStore> query = Query.and(AmbientEmitterComponent.getComponentType(), TransformComponent.getComponentType());

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         if (!holder.getArchetype().contains(NetworkId.getComponentType())) {
            holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
         }

         holder.ensureComponent(Intangible.getComponentType());
         holder.ensureComponent(PrefabCopyableComponent.getComponentType());
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
      }

      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }
   }

   public static class EntityRefAdded extends RefSystem<EntityStore> {
      private final Query<EntityStore> query = Query.and(AmbientEmitterComponent.getComponentType(), TransformComponent.getComponentType());

      @Override
      public void onEntityAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         AmbientEmitterComponent emitterComponent = store.getComponent(ref, AmbientEmitterComponent.getComponentType());

         assert emitterComponent != null;

         TransformComponent transformComponent = store.getComponent(ref, TransformComponent.getComponentType());

         assert transformComponent != null;

         Holder<EntityStore> emitterHolder = EntityStore.REGISTRY.newHolder();
         emitterHolder.addComponent(TransformComponent.getComponentType(), transformComponent.clone());
         AudioComponent audioComponent = new AudioComponent();
         audioComponent.addSound(SoundEvent.getAssetMap().getIndex(emitterComponent.getSoundEventId()));
         emitterHolder.addComponent(AudioComponent.getComponentType(), audioComponent);
         emitterHolder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
         emitterHolder.ensureComponent(Intangible.getComponentType());
         emitterHolder.addComponent(EntityStore.REGISTRY.getNonSerializedComponentType(), NonSerialized.get());
         emitterComponent.setSpawnedEmitter(commandBuffer.addEntity(emitterHolder, AddReason.SPAWN));
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         if (reason == RemoveReason.REMOVE) {
            AmbientEmitterComponent emitterComponent = store.getComponent(ref, AmbientEmitterComponent.getComponentType());

            assert emitterComponent != null;

            Ref<EntityStore> emitterRef = emitterComponent.getSpawnedEmitter();
            if (emitterRef != null) {
               commandBuffer.removeEntity(emitterRef, RemoveReason.REMOVE);
            }
         }
      }

      @Nullable
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }
   }

   public static class Ticking extends EntityTickingSystem<EntityStore> {
      private final Query<EntityStore> query = Query.and(AmbientEmitterComponent.getComponentType(), TransformComponent.getComponentType());

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         AmbientEmitterComponent emitter = archetypeChunk.getComponent(index, AmbientEmitterComponent.getComponentType());

         assert emitter != null;

         TransformComponent transform = archetypeChunk.getComponent(index, TransformComponent.getComponentType());

         assert transform != null;

         if (emitter.getSpawnedEmitter() != null && emitter.getSpawnedEmitter().isValid()) {
            TransformComponent ownedEmitterTransform = store.getComponent(emitter.getSpawnedEmitter(), TransformComponent.getComponentType());
            if (transform.getPosition().distanceSquaredTo(ownedEmitterTransform.getPosition()) > 1.0) {
               ownedEmitterTransform.setPosition(transform.getPosition());
            }
         } else {
            AmbiencePlugin.get()
               .getLogger()
               .at(Level.WARNING)
               .log("Ambient emitter lost at %s: %d %s", transform.getPosition(), archetypeChunk.getReferenceTo(index).getIndex(), emitter.getSoundEventId());
            commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
         }
      }

      @Nullable
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }
   }
}
