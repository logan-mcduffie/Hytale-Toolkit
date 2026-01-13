package com.hypixel.hytale.server.core.modules.entity.system;

import com.hypixel.hytale.assetstore.map.DefaultAssetMap;
import com.hypixel.hytale.common.util.RandomUtil;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentAccessor;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.shape.Box;
import com.hypixel.hytale.protocol.ComponentUpdate;
import com.hypixel.hytale.protocol.ComponentUpdateType;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.protocol.PlayerSkin;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.asset.type.model.config.ModelAsset;
import com.hypixel.hytale.server.core.cosmetics.CosmeticsModule;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.entities.player.movement.MovementManager;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesSystems;
import com.hypixel.hytale.server.core.modules.entity.component.ActiveAnimationComponent;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.PropComponent;
import com.hypixel.hytale.server.core.modules.entity.player.ApplyRandomSkinPersistedComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ModelSystems {
   public static class AnimationEntityTrackerUpdate extends EntityTickingSystem<EntityStore> {
      private final ComponentType<EntityStore, EntityTrackerSystems.Visible> visibleComponentType = EntityTrackerSystems.Visible.getComponentType();
      private final ComponentType<EntityStore, ActiveAnimationComponent> activeAnimationComponentType = ActiveAnimationComponent.getComponentType();
      private final Query<EntityStore> query = Query.and(this.visibleComponentType, this.activeAnimationComponentType);

      @Nullable
      @Override
      public SystemGroup<EntityStore> getGroup() {
         return EntityTrackerSystems.QUEUE_UPDATE_GROUP;
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
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
         EntityTrackerSystems.Visible visibleComponent = archetypeChunk.getComponent(index, this.visibleComponentType);

         assert visibleComponent != null;

         ActiveAnimationComponent activeAnimationComponent = archetypeChunk.getComponent(index, this.activeAnimationComponentType);

         assert activeAnimationComponent != null;

         Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
         if (activeAnimationComponent.consumeNetworkOutdated()) {
            queueUpdatesFor(ref, activeAnimationComponent, visibleComponent.visibleTo);
         } else if (!visibleComponent.newlyVisibleTo.isEmpty()) {
            queueUpdatesFor(ref, activeAnimationComponent, visibleComponent.newlyVisibleTo);
         }
      }

      private static void queueUpdatesFor(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull ActiveAnimationComponent animationComponent,
         @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> visibleTo
      ) {
         ComponentUpdate update = new ComponentUpdate();
         update.type = ComponentUpdateType.ActiveAnimations;
         update.activeAnimations = animationComponent.getActiveAnimations();

         for (Entry<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> entry : visibleTo.entrySet()) {
            entry.getValue().queueUpdate(ref, update);
         }
      }
   }

   public static class ApplyRandomSkin extends HolderSystem<EntityStore> {
      private final ComponentType<EntityStore, ModelComponent> modelComponentType = ModelComponent.getComponentType();
      private final ComponentType<EntityStore, ApplyRandomSkinPersistedComponent> randomSkinComponent = ApplyRandomSkinPersistedComponent.getComponentType();
      private final Query<EntityStore> query = Query.and(this.randomSkinComponent, this.modelComponentType);

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         PlayerSkin playerSkin = CosmeticsModule.get().generateRandomSkin(RandomUtil.getSecureRandom());
         holder.putComponent(PlayerSkinComponent.getComponentType(), new PlayerSkinComponent(playerSkin));
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }
   }

   public static class AssignNetworkIdToProps extends HolderSystem<EntityStore> {
      private final ComponentType<EntityStore, PropComponent> propComponentType = PropComponent.getComponentType();
      private final ComponentType<EntityStore, NetworkId> networkIdComponentType = NetworkId.getComponentType();
      private final Query<EntityStore> query = Query.and(this.propComponentType, Query.not(this.networkIdComponentType));

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         holder.addComponent(this.networkIdComponentType, new NetworkId(store.getExternalData().takeNextNetworkId()));
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }
   }

   public static class EnsurePropsPrefabCopyable extends HolderSystem<EntityStore> {
      private final ComponentType<EntityStore, PropComponent> propComponentType = PropComponent.getComponentType();

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         holder.ensureComponent(PrefabCopyableComponent.getComponentType());
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.propComponentType;
      }
   }

   public static class ModelChange extends RefChangeSystem<EntityStore, ModelComponent> {
      private final ComponentType<EntityStore, ModelComponent> modelComponentType = ModelComponent.getComponentType();
      private final ComponentType<EntityStore, PersistentModel> persistentModelComponentType = PersistentModel.getComponentType();

      @Override
      public Query<EntityStore> getQuery() {
         return this.persistentModelComponentType;
      }

      @Nonnull
      @Override
      public ComponentType<EntityStore, ModelComponent> componentType() {
         return this.modelComponentType;
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull ModelComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      public void onComponentSet(
         @Nonnull Ref<EntityStore> ref,
         ModelComponent oldComponent,
         @Nonnull ModelComponent newComponent,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         PersistentModel persistentModel = store.getComponent(ref, this.persistentModelComponentType);
         persistentModel.setModelReference(newComponent.getModel().toReference());
      }

      public void onComponentRemoved(
         @Nonnull Ref<EntityStore> ref, @Nonnull ModelComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         commandBuffer.removeComponent(ref, this.persistentModelComponentType);
      }
   }

   public static class ModelSpawned extends HolderSystem<EntityStore> {
      private final ComponentType<EntityStore, ModelComponent> modelComponentType = ModelComponent.getComponentType();
      private final ComponentType<EntityStore, BoundingBox> boundingBoxComponentType = BoundingBox.getComponentType();
      private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency<>(Order.AFTER, ModelSystems.SetRenderedModel.class));

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         Model model = holder.getComponent(this.modelComponentType).getModel();
         Box modelBoundingBox = model.getBoundingBox();
         if (modelBoundingBox != null) {
            BoundingBox boundingBox = holder.getComponent(this.boundingBoxComponentType);
            if (boundingBox == null) {
               boundingBox = new BoundingBox();
               holder.addComponent(this.boundingBoxComponentType, boundingBox);
            }

            boundingBox.setBoundingBox(modelBoundingBox);
            boundingBox.setDetailBoxes(model.getDetailBoxes());
         }
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
      }

      @Override
      public Query<EntityStore> getQuery() {
         return this.modelComponentType;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }
   }

   public static class PlayerConnect extends HolderSystem<EntityStore> {
      private final ComponentType<EntityStore, Player> playerComponentType = Player.getComponentType();
      private final ComponentType<EntityStore, ModelComponent> modelComponentType = ModelComponent.getComponentType();
      private final Query<EntityStore> query = Query.and(this.playerComponentType, Query.not(this.modelComponentType));
      private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency<>(Order.BEFORE, ModelSystems.ModelSpawned.class));

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         Player player = holder.getComponent(this.playerComponentType);
         DefaultAssetMap<String, ModelAsset> assetMap = ModelAsset.getAssetMap();
         String preset = player.getPlayerConfigData().getPreset();
         ModelAsset modelAsset = preset != null ? assetMap.getAsset(preset) : null;
         if (modelAsset != null) {
            Model model = Model.createUnitScaleModel(modelAsset);
            holder.addComponent(this.modelComponentType, new ModelComponent(model));
         } else {
            ModelAsset defaultModelAsset = assetMap.getAsset("Player");
            if (defaultModelAsset != null) {
               Model defaultModel = Model.createUnitScaleModel(defaultModelAsset);
               holder.addComponent(this.modelComponentType, new ModelComponent(defaultModel));
            }
         }
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
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
   }

   public static class PlayerUpdateMovementManager extends RefChangeSystem<EntityStore, ModelComponent> {
      private final ComponentType<EntityStore, ModelComponent> modelComponentType = ModelComponent.getComponentType();
      private final ComponentType<EntityStore, Player> playerComponentType = Player.getComponentType();
      private final Set<Dependency<EntityStore>> dependencies = Set.of(new SystemDependency<>(Order.AFTER, ModelSystems.UpdateBoundingBox.class));

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.playerComponentType;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }

      @Nonnull
      @Override
      public ComponentType<EntityStore, ModelComponent> componentType() {
         return this.modelComponentType;
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull ModelComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         this.updateMovementController(ref, commandBuffer);
      }

      public void onComponentSet(
         @Nonnull Ref<EntityStore> ref,
         ModelComponent oldComponent,
         @Nonnull ModelComponent newComponent,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         this.updateMovementController(ref, commandBuffer);
      }

      public void onComponentRemoved(
         @Nonnull Ref<EntityStore> ref, @Nonnull ModelComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         this.updateMovementController(ref, commandBuffer);
      }

      private void updateMovementController(@Nonnull Ref<EntityStore> ref, @Nonnull ComponentAccessor<EntityStore> componentAccessor) {
         MovementManager movementManagerComponent = componentAccessor.getComponent(ref, MovementManager.getComponentType());

         assert movementManagerComponent != null;

         movementManagerComponent.resetDefaultsAndUpdate(ref, componentAccessor);
      }
   }

   public static class SetRenderedModel extends HolderSystem<EntityStore> {
      private final ComponentType<EntityStore, ModelComponent> modelComponentType = ModelComponent.getComponentType();
      private final ComponentType<EntityStore, PersistentModel> persistentModelComponentType = PersistentModel.getComponentType();
      private final Query<EntityStore> query = Query.and(this.persistentModelComponentType, Query.not(this.modelComponentType));

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         PersistentModel persistentModel = holder.getComponent(this.persistentModelComponentType);
         holder.putComponent(this.modelComponentType, new ModelComponent(persistentModel.getModelReference().toModel()));
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }
   }

   public static class UpdateBoundingBox extends RefChangeSystem<EntityStore, ModelComponent> {
      private final ComponentType<EntityStore, ModelComponent> modelComponentType = ModelComponent.getComponentType();
      private final ComponentType<EntityStore, BoundingBox> boundingBoxComponentType = BoundingBox.getComponentType();
      private final ComponentType<EntityStore, MovementStatesComponent> movementStatesComponentType = MovementStatesComponent.getComponentType();

      @Override
      public Query<EntityStore> getQuery() {
         return this.boundingBoxComponentType;
      }

      @Nonnull
      @Override
      public ComponentType<EntityStore, ModelComponent> componentType() {
         return this.modelComponentType;
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull ModelComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         BoundingBox boundingBox = commandBuffer.getComponent(ref, this.boundingBoxComponentType);
         MovementStatesComponent movementStates = commandBuffer.getComponent(ref, this.movementStatesComponentType);
         updateBoundingBox(component.getModel(), boundingBox, movementStates);
      }

      public void onComponentSet(
         @Nonnull Ref<EntityStore> ref,
         ModelComponent oldComponent,
         @Nonnull ModelComponent newComponent,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         BoundingBox boundingBox = commandBuffer.getComponent(ref, this.boundingBoxComponentType);
         MovementStatesComponent movementStates = commandBuffer.getComponent(ref, this.movementStatesComponentType);
         updateBoundingBox(newComponent.getModel(), boundingBox, movementStates);
      }

      public void onComponentRemoved(
         @Nonnull Ref<EntityStore> ref, @Nonnull ModelComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         commandBuffer.getComponent(ref, this.boundingBoxComponentType).setBoundingBox(new Box());
      }

      protected static void updateBoundingBox(@Nonnull Model model, @Nonnull BoundingBox boundingBox, @Nullable MovementStatesComponent movementStatesComponent) {
         updateBoundingBox(model, boundingBox, movementStatesComponent != null ? movementStatesComponent.getMovementStates() : null);
      }

      protected static void updateBoundingBox(@Nonnull Model model, @Nonnull BoundingBox boundingBox, @Nullable MovementStates movementStates) {
         Box modelBoundingBox = model.getBoundingBox(movementStates);
         if (modelBoundingBox == null) {
            modelBoundingBox = new Box();
         }

         boundingBox.setBoundingBox(modelBoundingBox);
      }
   }

   public static class UpdateCrouchingBoundingBox extends EntityTickingSystem<EntityStore> {
      public static final Set<Dependency<EntityStore>> DEPENDENCIES = Collections.singleton(
         new SystemDependency<>(Order.BEFORE, MovementStatesSystems.TickingSystem.class)
      );
      private final ComponentType<EntityStore, MovementStatesComponent> movementStatesComponentType = MovementStatesComponent.getComponentType();
      private final ComponentType<EntityStore, BoundingBox> boundingBoxComponentType = BoundingBox.getComponentType();
      private final ComponentType<EntityStore, ModelComponent> modelComponentType = ModelComponent.getComponentType();
      private final Query<EntityStore> query = Query.and(this.movementStatesComponentType, this.boundingBoxComponentType, this.modelComponentType);

      @Override
      public boolean isParallel(int archetypeChunkSize, int taskCount) {
         return EntityTickingSystem.maybeUseParallel(archetypeChunkSize, taskCount);
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return DEPENDENCIES;
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         MovementStatesComponent movementStates = archetypeChunk.getComponent(index, this.movementStatesComponentType);
         MovementStates newMovementStates = movementStates.getMovementStates();
         MovementStates sentMovementStates = movementStates.getSentMovementStates();
         if (newMovementStates.crouching != sentMovementStates.crouching || newMovementStates.forcedCrouching != sentMovementStates.forcedCrouching) {
            Model model = archetypeChunk.getComponent(index, this.modelComponentType).getModel();
            BoundingBox boundingBox = archetypeChunk.getComponent(index, this.boundingBoxComponentType);
            ModelSystems.UpdateBoundingBox.updateBoundingBox(model, boundingBox, newMovementStates);
         }
      }
   }
}
