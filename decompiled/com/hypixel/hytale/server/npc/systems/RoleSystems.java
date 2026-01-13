package com.hypixel.hytale.server.npc.systems;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.component.system.tick.TickingSystem;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.Frozen;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.NewSpawnComponent;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
import com.hypixel.hytale.server.core.modules.entity.system.ModelSystems;
import com.hypixel.hytale.server.core.modules.entity.system.TransformSystems;
import com.hypixel.hytale.server.core.modules.interaction.InteractionModule;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.NPCPlugin;
import com.hypixel.hytale.server.npc.components.StepComponent;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.movement.controllers.MotionController;
import com.hypixel.hytale.server.npc.role.Role;
import com.hypixel.hytale.server.npc.role.RoleDebugDisplay;
import com.hypixel.hytale.server.npc.role.support.EntitySupport;
import com.hypixel.hytale.server.npc.role.support.MarkedEntitySupport;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nonnull;

public class RoleSystems {
   private static final ThreadLocal<List<Ref<EntityStore>>> ENTITY_LIST = ThreadLocal.withInitial(ArrayList::new);

   public static class BehaviourTickSystem extends TickingSystem<EntityStore> {
      @Nonnull
      private final ComponentType<EntityStore, NPCEntity> npcComponentType;
      @Nonnull
      private final ComponentType<EntityStore, StepComponent> stepComponentType;
      @Nonnull
      private final ComponentType<EntityStore, Frozen> frozenComponentType;
      @Nonnull
      private final ComponentType<EntityStore, NewSpawnComponent> newSpawnComponentType;

      public BehaviourTickSystem(
         @Nonnull ComponentType<EntityStore, NPCEntity> npcComponentType, @Nonnull ComponentType<EntityStore, StepComponent> stepComponentType
      ) {
         this.npcComponentType = npcComponentType;
         this.stepComponentType = stepComponentType;
         this.frozenComponentType = Frozen.getComponentType();
         this.newSpawnComponentType = NewSpawnComponent.getComponentType();
      }

      @Override
      public void tick(float dt, int systemIndex, @Nonnull Store<EntityStore> store) {
         List<Ref<EntityStore>> entities = RoleSystems.ENTITY_LIST.get();
         store.forEachChunk(this.npcComponentType, (archetypeChunk, commandBuffer) -> {
            for (int index = 0; index < archetypeChunk.size(); index++) {
               entities.add(archetypeChunk.getReferenceTo(index));
            }
         });
         World world = store.getExternalData().getWorld();
         boolean isAllNpcFrozen = world.getWorldConfig().isAllNPCFrozen();

         for (Ref<EntityStore> entityReference : entities) {
            if (entityReference.isValid() && store.getComponent(entityReference, this.newSpawnComponentType) == null) {
               float tickLength;
               if (store.getComponent(entityReference, this.frozenComponentType) == null && !isAllNpcFrozen) {
                  tickLength = dt;
               } else {
                  StepComponent stepComponent = store.getComponent(entityReference, this.stepComponentType);
                  if (stepComponent == null) {
                     continue;
                  }

                  tickLength = stepComponent.getTickLength();
               }

               NPCEntity npcComponent = store.getComponent(entityReference, this.npcComponentType);

               assert npcComponent != null;

               try {
                  Role role = npcComponent.getRole();
                  boolean benchmarking = NPCPlugin.get().isBenchmarkingRole();
                  if (benchmarking) {
                     long start = System.nanoTime();
                     role.tick(entityReference, tickLength, store);
                     NPCPlugin.get().collectRoleTick(role.getRoleIndex(), System.nanoTime() - start);
                  } else {
                     role.tick(entityReference, tickLength, store);
                  }
               } catch (IllegalArgumentException | IllegalStateException | NullPointerException var15) {
                  NPCPlugin.get().getLogger().at(Level.SEVERE).withCause(var15).log("Failed to tick NPC: %s", npcComponent.getRoleName());
                  store.removeEntity(entityReference, RemoveReason.REMOVE);
               }
            }
         }

         entities.clear();
      }
   }

   public static class PostBehaviourSupportTickSystem extends SteppableTickingSystem {
      @Nonnull
      private final ComponentType<EntityStore, NPCEntity> npcComponentType;
      @Nonnull
      private final ComponentType<EntityStore, TransformComponent> transformComponentType;
      @Nonnull
      private final Query<EntityStore> query;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies = Set.of(
         new SystemDependency<>(Order.AFTER, SteeringSystem.class), new SystemDependency<>(Order.BEFORE, TransformSystems.EntityTrackerUpdate.class)
      );

      public PostBehaviourSupportTickSystem(@Nonnull ComponentType<EntityStore, NPCEntity> npcComponentType) {
         this.npcComponentType = npcComponentType;
         this.transformComponentType = TransformComponent.getComponentType();
         this.query = Query.and(npcComponentType, this.transformComponentType);
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }

      @Override
      public boolean isParallel(int archetypeChunkSize, int taskCount) {
         return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Override
      public void steppedTick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         NPCEntity npcComponent = archetypeChunk.getComponent(index, this.npcComponentType);

         assert npcComponent != null;

         Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
         Role role = npcComponent.getRole();
         MotionController activeMotionController = role.getActiveMotionController();
         activeMotionController.clearOverrides();
         activeMotionController.constrainRotations(role, archetypeChunk.getComponent(index, this.transformComponentType));
         role.getCombatSupport().tick(dt);
         role.getWorldSupport().tick(dt);
         EntitySupport entitySupport = role.getEntitySupport();
         entitySupport.tick(dt);
         entitySupport.handleNominatedDisplayName(ref, commandBuffer);
         role.getStateSupport().update(commandBuffer);
         npcComponent.clearDamageData();
         role.getMarkedEntitySupport().setTargetSlotToIgnoreForAvoidance(Integer.MIN_VALUE);
         role.setReachedTerminalAction(false);
         role.getPositionCache().clear(dt);
      }
   }

   public static class PreBehaviourSupportTickSystem extends SteppableTickingSystem {
      @Nonnull
      private final ComponentType<EntityStore, NPCEntity> npcComponentType;
      @Nonnull
      private final ComponentType<EntityStore, Player> playerComponentType;
      @Nonnull
      private final ComponentType<EntityStore, DeathComponent> deathComponentType;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies;

      public PreBehaviourSupportTickSystem(@Nonnull ComponentType<EntityStore, NPCEntity> npcComponentType) {
         this.npcComponentType = npcComponentType;
         this.playerComponentType = Player.getComponentType();
         this.deathComponentType = DeathComponent.getComponentType();
         this.dependencies = Set.of(new SystemDependency<>(Order.BEFORE, RoleSystems.BehaviourTickSystem.class));
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }

      @Override
      public boolean isParallel(int archetypeChunkSize, int taskCount) {
         return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.npcComponentType;
      }

      @Override
      public void steppedTick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         NPCEntity npcComponent = archetypeChunk.getComponent(index, this.npcComponentType);

         assert npcComponent != null;

         Role role = npcComponent.getRole();
         MarkedEntitySupport markedEntitySupport = role.getMarkedEntitySupport();
         Ref<EntityStore>[] entityTargets = markedEntitySupport.getEntityTargets();

         for (int i = 0; i < entityTargets.length; i++) {
            Ref<EntityStore> targetReference = entityTargets[i];
            if (targetReference != null) {
               if (!targetReference.isValid()) {
                  entityTargets[i] = null;
               } else {
                  Player playerComponent = commandBuffer.getComponent(targetReference, this.playerComponentType);
                  if (playerComponent != null && playerComponent.getGameMode() != GameMode.Adventure) {
                     if (playerComponent.getGameMode() != GameMode.Creative) {
                        entityTargets[i] = null;
                        continue;
                     }

                     PlayerSettings playerSettingsComponent = commandBuffer.getComponent(targetReference, PlayerSettings.getComponentType());
                     if (playerSettingsComponent == null || !playerSettingsComponent.creativeSettings().allowNPCDetection()) {
                        entityTargets[i] = null;
                        continue;
                     }
                  }

                  DeathComponent deathComponent = commandBuffer.getComponent(targetReference, this.deathComponentType);
                  if (deathComponent != null) {
                     entityTargets[i] = null;
                  }
               }
            }
         }

         role.clearOnceIfNeeded();
         role.getBodySteering().clear();
         role.getHeadSteering().clear();
         role.getIgnoredEntitiesForAvoidance().clear();
         npcComponent.invalidateCachedHorizontalSpeedMultiplier();
      }
   }

   public static class RoleActivateSystem extends HolderSystem<EntityStore> {
      @Nonnull
      private final ComponentType<EntityStore, NPCEntity> npcComponentType;
      @Nonnull
      private final ComponentType<EntityStore, ModelComponent> modelComponentType;
      @Nonnull
      private final ComponentType<EntityStore, BoundingBox> boundingBoxComponentType;
      @Nonnull
      private final Query<EntityStore> query;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies;

      public RoleActivateSystem(@Nonnull ComponentType<EntityStore, NPCEntity> npcComponentType) {
         this.npcComponentType = npcComponentType;
         this.modelComponentType = ModelComponent.getComponentType();
         this.boundingBoxComponentType = BoundingBox.getComponentType();
         this.query = Query.and(npcComponentType, this.modelComponentType, this.boundingBoxComponentType);
         this.dependencies = Set.of(
            new SystemDependency<>(Order.AFTER, BalancingInitialisationSystem.class), new SystemDependency<>(Order.AFTER, ModelSystems.ModelSpawned.class)
         );
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         NPCEntity npcComponent = holder.getComponent(this.npcComponentType);

         assert npcComponent != null;

         Role role = npcComponent.getRole();
         role.getStateSupport().activate();
         role.getDebugSupport().activate();
         ModelComponent modelComponent = holder.getComponent(this.modelComponentType);

         assert modelComponent != null;

         BoundingBox boundingBoxComponent = holder.getComponent(this.boundingBoxComponentType);

         assert boundingBoxComponent != null;

         role.updateMotionControllers(null, modelComponent.getModel(), boundingBoxComponent.getBoundingBox(), null);
         role.clearOnce();
         role.getActiveMotionController().activate();
         holder.ensureComponent(InteractionModule.get().getChainingDataComponent());
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
         NPCEntity npcComponent = holder.getComponent(this.npcComponentType);

         assert npcComponent != null;

         Role role = npcComponent.getRole();
         role.getActiveMotionController().deactivate();
         role.getWorldSupport().resetAllBlockSensors();
      }
   }

   public static class RoleDebugSystem extends SteppableTickingSystem {
      @Nonnull
      private final ComponentType<EntityStore, NPCEntity> npcComponentType;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies;

      public RoleDebugSystem(@Nonnull ComponentType<EntityStore, NPCEntity> npcComponentType, @Nonnull Set<Dependency<EntityStore>> dependencies) {
         this.npcComponentType = npcComponentType;
         this.dependencies = dependencies;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }

      @Override
      public boolean isParallel(int archetypeChunkSize, int taskCount) {
         return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.npcComponentType;
      }

      @Override
      public void steppedTick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         NPCEntity npcComponent = archetypeChunk.getComponent(index, this.npcComponentType);

         assert npcComponent != null;

         Role role = npcComponent.getRole();
         RoleDebugDisplay debugDisplay = role.getDebugSupport().getDebugDisplay();
         if (debugDisplay != null) {
            debugDisplay.display(role, index, archetypeChunk, commandBuffer);
         }
      }
   }
}
