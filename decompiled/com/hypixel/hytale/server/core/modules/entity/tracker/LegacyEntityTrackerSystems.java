package com.hypixel.hytale.server.core.modules.entity.tracker;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.ComponentUpdate;
import com.hypixel.hytale.protocol.ComponentUpdateType;
import com.hypixel.hytale.protocol.EntityUpdate;
import com.hypixel.hytale.protocol.Equipment;
import com.hypixel.hytale.protocol.ModelTransform;
import com.hypixel.hytale.protocol.packets.entities.EntityUpdates;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.EntityUtils;
import com.hypixel.hytale.server.core.entity.LivingEntity;
import com.hypixel.hytale.server.core.entity.effect.EffectControllerComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.inventory.container.ItemContainer;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.BoundingBox;
import com.hypixel.hytale.server.core.modules.entity.component.EntityScaleComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.modules.entity.component.Invulnerable;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.RespondToHit;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSkinComponent;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.projectile.component.PredictedProjectile;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.PositionUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class LegacyEntityTrackerSystems {
   @Deprecated
   public static void sendPlayerSelf(@Nonnull Ref<EntityStore> viewerRef, @Nonnull Store<EntityStore> store) {
      EntityTrackerSystems.EntityViewer viewer = store.getComponent(viewerRef, EntityTrackerSystems.EntityViewer.getComponentType());
      if (viewer == null) {
         throw new IllegalArgumentException("Not EntityViewer");
      } else {
         LivingEntity entity = (LivingEntity)EntityUtils.getEntity(viewerRef, store);
         TransformComponent transformComponent = store.getComponent(viewerRef, TransformComponent.getComponentType());
         HeadRotation headRotationComponent = store.getComponent(viewerRef, HeadRotation.getComponentType());
         ModelComponent modelComponent = store.getComponent(viewerRef, ModelComponent.getComponentType());
         EntityStatMap statMapComponent = store.getComponent(viewerRef, EntityStatMap.getComponentType());
         PredictedProjectile predictionComponent = store.getComponent(viewerRef, PredictedProjectile.getComponentType());
         EffectControllerComponent effectControllerComponent = store.getComponent(viewerRef, EffectControllerComponent.getComponentType());
         Nameplate nameplateComponent = store.getComponent(viewerRef, Nameplate.getComponentType());
         EntityUpdate entityUpdate = new EntityUpdate();
         entityUpdate.networkId = entity.getNetworkId();
         ObjectArrayList<ComponentUpdate> list = new ObjectArrayList<>();
         if (store.getArchetype(viewerRef).contains(Interactable.getComponentType())) {
            ComponentUpdate update = new ComponentUpdate();
            update.type = ComponentUpdateType.Interactable;
            list.add(update);
         }

         if (store.getArchetype(viewerRef).contains(Intangible.getComponentType())) {
            ComponentUpdate update = new ComponentUpdate();
            update.type = ComponentUpdateType.Intangible;
            list.add(update);
         }

         if (store.getArchetype(viewerRef).contains(Invulnerable.getComponentType())) {
            ComponentUpdate update = new ComponentUpdate();
            update.type = ComponentUpdateType.Invulnerable;
            list.add(update);
         }

         if (store.getArchetype(viewerRef).contains(RespondToHit.getComponentType())) {
            ComponentUpdate update = new ComponentUpdate();
            update.type = ComponentUpdateType.RespondToHit;
            list.add(update);
         }

         if (nameplateComponent != null) {
            ComponentUpdate update = new ComponentUpdate();
            update.type = ComponentUpdateType.Nameplate;
            update.nameplate = new com.hypixel.hytale.protocol.Nameplate();
            update.nameplate.text = nameplateComponent.getText();
            list.add(update);
         }

         if (predictionComponent != null) {
            ComponentUpdate update = new ComponentUpdate();
            update.type = ComponentUpdateType.Prediction;
            update.predictionId = predictionComponent.getUuid();
            list.add(update);
         }

         ComponentUpdate update = new ComponentUpdate();
         update.type = ComponentUpdateType.Model;
         update.model = modelComponent != null ? modelComponent.getModel().toPacket() : null;
         EntityScaleComponent entityScaleComponent = store.getComponent(viewerRef, EntityScaleComponent.getComponentType());
         if (entityScaleComponent != null) {
            update.entityScale = entityScaleComponent.getScale();
         }

         list.add(update);
         update = new ComponentUpdate();
         update.type = ComponentUpdateType.PlayerSkin;
         PlayerSkinComponent component = store.getComponent(viewerRef, PlayerSkinComponent.getComponentType());
         update.skin = component != null ? component.getPlayerSkin() : null;
         list.add(update);
         Inventory inventory = entity.getInventory();
         ComponentUpdate updatex = new ComponentUpdate();
         updatex.type = ComponentUpdateType.Equipment;
         updatex.equipment = new Equipment();
         ItemContainer armor = inventory.getArmor();
         updatex.equipment.armorIds = new String[armor.getCapacity()];
         Arrays.fill(updatex.equipment.armorIds, "");
         armor.forEachWithMeta((slot, itemStack, armorIds) -> armorIds[slot] = itemStack.getItemId(), updatex.equipment.armorIds);
         ItemStack itemInHand = inventory.getItemInHand();
         updatex.equipment.rightHandItemId = itemInHand != null ? itemInHand.getItemId() : "Empty";
         ItemStack utilityItem = inventory.getUtilityItem();
         updatex.equipment.leftHandItemId = utilityItem != null ? utilityItem.getItemId() : "Empty";
         list.add(updatex);
         update = new ComponentUpdate();
         update.type = ComponentUpdateType.Transform;
         update.transform = new ModelTransform();
         update.transform.position = PositionUtil.toPositionPacket(transformComponent.getPosition());
         update.transform.bodyOrientation = PositionUtil.toDirectionPacket(transformComponent.getRotation());
         update.transform.lookOrientation = PositionUtil.toDirectionPacket(headRotationComponent.getRotation());
         list.add(update);
         update = new ComponentUpdate();
         update.type = ComponentUpdateType.EntityEffects;
         update.entityEffectUpdates = effectControllerComponent.createInitUpdates();
         list.add(update);
         update = new ComponentUpdate();
         update.type = ComponentUpdateType.EntityStats;
         update.entityStatUpdates = statMapComponent.createInitUpdate(true);
         list.add(update);
         entityUpdate.updates = list.toArray(ComponentUpdate[]::new);
         viewer.packetReceiver.writeNoCache(new EntityUpdates(null, new EntityUpdate[]{entityUpdate}));
      }
   }

   @Deprecated
   public static boolean clear(@Nonnull Player player, @Nonnull Holder<EntityStore> holder) {
      World world = player.getWorld();
      if (world != null && world.isInThread()) {
         return EntityTrackerSystems.clear(player.getReference(), world.getEntityStore().getStore());
      } else {
         EntityTrackerSystems.EntityViewer entityViewerComponent = holder.getComponent(EntityTrackerSystems.EntityViewer.getComponentType());
         if (entityViewerComponent == null) {
            return false;
         } else {
            entityViewerComponent.sent.clear();
            return true;
         }
      }
   }

   public static class LegacyEntityModel extends EntityTickingSystem<EntityStore> {
      private final ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType;
      private final ComponentType<EntityStore, ModelComponent> modelComponentType;
      @Nonnull
      private final Query<EntityStore> query;

      public LegacyEntityModel(ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType) {
         this.componentType = componentType;
         this.modelComponentType = ModelComponent.getComponentType();
         this.query = Query.and(componentType, this.modelComponentType);
      }

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
         EntityTrackerSystems.Visible visibleComponent = archetypeChunk.getComponent(index, this.componentType);

         assert visibleComponent != null;

         ModelComponent modelComponent = archetypeChunk.getComponent(index, this.modelComponentType);

         assert modelComponent != null;

         float entityScale = 0.0F;
         boolean scaleOutdated = false;
         EntityScaleComponent entityScaleComponent = archetypeChunk.getComponent(index, EntityScaleComponent.getComponentType());
         if (entityScaleComponent != null) {
            entityScale = entityScaleComponent.getScale();
            scaleOutdated = entityScaleComponent.consumeNetworkOutdated();
         }

         boolean modelOutdated = modelComponent.consumeNetworkOutdated();
         if (modelOutdated || scaleOutdated) {
            queueUpdatesFor(archetypeChunk.getReferenceTo(index), modelComponent, entityScale, visibleComponent.visibleTo);
         } else if (!visibleComponent.newlyVisibleTo.isEmpty()) {
            queueUpdatesFor(archetypeChunk.getReferenceTo(index), modelComponent, entityScale, visibleComponent.newlyVisibleTo);
         }
      }

      private static void queueUpdatesFor(
         Ref<EntityStore> ref, @Nullable ModelComponent model, float entityScale, @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> visibleTo
      ) {
         ComponentUpdate update = new ComponentUpdate();
         update.type = ComponentUpdateType.Model;
         update.model = model != null ? model.getModel().toPacket() : null;
         update.entityScale = entityScale;

         for (EntityTrackerSystems.EntityViewer viewer : visibleTo.values()) {
            viewer.queueUpdate(ref, update);
         }
      }
   }

   public static class LegacyEntitySkin extends EntityTickingSystem<EntityStore> {
      private final ComponentType<EntityStore, PlayerSkinComponent> playerSkinComponentComponentType;
      private final ComponentType<EntityStore, EntityTrackerSystems.Visible> visibleComponentType;
      @Nonnull
      private final Query<EntityStore> query;

      public LegacyEntitySkin(
         ComponentType<EntityStore, EntityTrackerSystems.Visible> visibleComponentType,
         ComponentType<EntityStore, PlayerSkinComponent> playerSkinComponentComponentType
      ) {
         this.visibleComponentType = visibleComponentType;
         this.playerSkinComponentComponentType = playerSkinComponentComponentType;
         this.query = Query.and(visibleComponentType, playerSkinComponentComponentType);
      }

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

         if (archetypeChunk.getComponent(index, this.playerSkinComponentComponentType).consumeNetworkOutdated()) {
            queueUpdatesFor(
               archetypeChunk.getReferenceTo(index), archetypeChunk.getComponent(index, this.playerSkinComponentComponentType), visibleComponent.visibleTo
            );
         } else if (!visibleComponent.newlyVisibleTo.isEmpty()) {
            queueUpdatesFor(
               archetypeChunk.getReferenceTo(index), archetypeChunk.getComponent(index, this.playerSkinComponentComponentType), visibleComponent.newlyVisibleTo
            );
         }
      }

      private static void queueUpdatesFor(
         Ref<EntityStore> ref, @Nonnull PlayerSkinComponent component, @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> visibleTo
      ) {
         ComponentUpdate update = new ComponentUpdate();
         update.type = ComponentUpdateType.PlayerSkin;
         update.skin = component.getPlayerSkin();

         for (EntityTrackerSystems.EntityViewer viewer : visibleTo.values()) {
            viewer.queueUpdate(ref, update);
         }
      }
   }

   public static class LegacyEquipment extends EntityTickingSystem<EntityStore> {
      private final ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType;
      @Nonnull
      private final Query<EntityStore> query;

      public LegacyEquipment(ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType) {
         this.componentType = componentType;
         this.query = Query.and(componentType, AllLegacyLivingEntityTypesQuery.INSTANCE);
      }

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
         EntityTrackerSystems.Visible visibleComponent = archetypeChunk.getComponent(index, this.componentType);

         assert visibleComponent != null;

         LivingEntity entity = (LivingEntity)EntityUtils.getEntity(index, archetypeChunk);

         assert entity != null;

         if (entity.consumeEquipmentNetworkOutdated()) {
            queueUpdatesFor(archetypeChunk.getReferenceTo(index), entity, visibleComponent.visibleTo);
         } else if (!visibleComponent.newlyVisibleTo.isEmpty()) {
            queueUpdatesFor(archetypeChunk.getReferenceTo(index), entity, visibleComponent.newlyVisibleTo);
         }
      }

      private static void queueUpdatesFor(
         @Nonnull Ref<EntityStore> ref, @Nonnull LivingEntity entity, @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> visibleTo
      ) {
         ComponentUpdate update = new ComponentUpdate();
         update.type = ComponentUpdateType.Equipment;
         update.equipment = new Equipment();
         Inventory inventory = entity.getInventory();
         ItemContainer armor = inventory.getArmor();
         update.equipment.armorIds = new String[armor.getCapacity()];
         Arrays.fill(update.equipment.armorIds, "");
         armor.forEachWithMeta((slot, itemStack, armorIds) -> armorIds[slot] = itemStack.getItemId(), update.equipment.armorIds);
         ItemStack itemInHand = inventory.getItemInHand();
         update.equipment.rightHandItemId = itemInHand != null ? itemInHand.getItemId() : "Empty";
         ItemStack utilityItem = inventory.getUtilityItem();
         update.equipment.leftHandItemId = utilityItem != null ? utilityItem.getItemId() : "Empty";

         for (EntityTrackerSystems.EntityViewer viewer : visibleTo.values()) {
            viewer.queueUpdate(ref, update);
         }
      }
   }

   public static class LegacyHideFromEntity extends EntityTickingSystem<EntityStore> {
      private final ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> entityViewerComponentType;
      private final ComponentType<EntityStore, PlayerSettings> playerSettingsComponentType;
      @Nonnull
      private final Query<EntityStore> query;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies;

      public LegacyHideFromEntity(ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> entityViewerComponentType) {
         this.entityViewerComponentType = entityViewerComponentType;
         this.playerSettingsComponentType = EntityModule.get().getPlayerSettingsComponentType();
         this.query = Query.and(entityViewerComponentType, AllLegacyLivingEntityTypesQuery.INSTANCE);
         this.dependencies = Collections.singleton(new SystemDependency<>(Order.AFTER, EntityTrackerSystems.CollectVisible.class));
      }

      @Nullable
      @Override
      public SystemGroup<EntityStore> getGroup() {
         return EntityTrackerSystems.FIND_VISIBLE_ENTITIES_GROUP;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
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
         Ref<EntityStore> viewerRef = archetypeChunk.getReferenceTo(index);
         PlayerSettings settings = archetypeChunk.getComponent(index, this.playerSettingsComponentType);
         if (settings == null) {
            settings = PlayerSettings.defaults();
         }

         EntityTrackerSystems.EntityViewer entityViewerComponent = archetypeChunk.getComponent(index, this.entityViewerComponentType);

         assert entityViewerComponent != null;

         Iterator<Ref<EntityStore>> iterator = entityViewerComponent.visible.iterator();

         while (iterator.hasNext()) {
            Ref<EntityStore> ref = iterator.next();
            Entity entity = EntityUtils.getEntity(ref, commandBuffer);
            if (entity != null && entity.isHiddenFromLivingEntity(ref, viewerRef, commandBuffer) && canHideEntities(entity, settings)) {
               entityViewerComponent.hiddenCount++;
               iterator.remove();
            }
         }
      }

      private static boolean canHideEntities(Entity entity, @Nonnull PlayerSettings settings) {
         return entity instanceof Player && !settings.showEntityMarkers();
      }
   }

   public static class LegacyLODCull extends EntityTickingSystem<EntityStore> {
      public static final double ENTITY_LOD_RATIO_DEFAULT = 3.5E-5;
      public static double ENTITY_LOD_RATIO = 3.5E-5;
      private final ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> componentType;
      private final ComponentType<EntityStore, BoundingBox> boundingBoxComponentType;
      @Nonnull
      private final Query<EntityStore> query;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies;

      public LegacyLODCull(ComponentType<EntityStore, EntityTrackerSystems.EntityViewer> componentType) {
         this.componentType = componentType;
         this.boundingBoxComponentType = BoundingBox.getComponentType();
         this.query = Query.and(componentType, TransformComponent.getComponentType());
         this.dependencies = Collections.singleton(new SystemDependency<>(Order.AFTER, EntityTrackerSystems.CollectVisible.class));
      }

      @Nullable
      @Override
      public SystemGroup<EntityStore> getGroup() {
         return EntityTrackerSystems.FIND_VISIBLE_ENTITIES_GROUP;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
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
         EntityTrackerSystems.EntityViewer entityViewerComponent = archetypeChunk.getComponent(index, this.componentType);

         assert entityViewerComponent != null;

         TransformComponent transformComponent = archetypeChunk.getComponent(index, TransformComponent.getComponentType());

         assert transformComponent != null;

         Vector3d position = transformComponent.getPosition();
         Iterator<Ref<EntityStore>> iterator = entityViewerComponent.visible.iterator();

         while (iterator.hasNext()) {
            Ref<EntityStore> ref = iterator.next();
            BoundingBox boundingBoxComponent = commandBuffer.getComponent(ref, this.boundingBoxComponentType);
            if (boundingBoxComponent != null) {
               TransformComponent otherTransformComponent = commandBuffer.getComponent(ref, TransformComponent.getComponentType());

               assert otherTransformComponent != null;

               double distanceSq = otherTransformComponent.getPosition().distanceSquaredTo(position);
               double maximumThickness = boundingBoxComponent.getBoundingBox().getMaximumThickness();
               if (maximumThickness < ENTITY_LOD_RATIO * distanceSq) {
                  entityViewerComponent.lodExcludedCount++;
                  iterator.remove();
               }
            }
         }
      }
   }
}
