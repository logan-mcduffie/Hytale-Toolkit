package com.hypixel.hytale.server.spawning.spawnmarkers;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.ExtraInfo;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.data.unknown.UnknownComponents;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.OrderPriority;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.spatial.SpatialResource;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.server.core.asset.type.model.config.Model;
import com.hypixel.hytale.server.core.entity.Entity;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.nameplate.Nameplate;
import com.hypixel.hytale.server.core.entity.reference.InvalidatablePersistentRef;
import com.hypixel.hytale.server.core.entity.reference.PersistentRefCount;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyEntityTypesQuery;
import com.hypixel.hytale.server.core.modules.entity.EntityModule;
import com.hypixel.hytale.server.core.modules.entity.component.FromPrefab;
import com.hypixel.hytale.server.core.modules.entity.component.FromWorldGen;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.HiddenFromAdventurePlayers;
import com.hypixel.hytale.server.core.modules.entity.component.Intangible;
import com.hypixel.hytale.server.core.modules.entity.component.ModelComponent;
import com.hypixel.hytale.server.core.modules.entity.component.PersistentModel;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.component.WorldGenId;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.system.PlayerSpatialSystem;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.time.WorldTimeResource;
import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.flock.StoredFlock;
import com.hypixel.hytale.server.npc.components.SpawnMarkerReference;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.spawning.assets.spawnmarker.config.SpawnMarker;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.bson.BsonDocument;

public class SpawnMarkerSystems {
   private static final HytaleLogger LOGGER = HytaleLogger.forEnclosingClass();

   public static class AddedFromWorldGen extends HolderSystem<EntityStore> {
      private final ComponentType<EntityStore, SpawnMarkerEntity> componentType = SpawnMarkerEntity.getComponentType();
      private final ComponentType<EntityStore, WorldGenId> worldGenIdComponentType = WorldGenId.getComponentType();
      private final ComponentType<EntityStore, FromWorldGen> fromWorldGenComponentType = FromWorldGen.getComponentType();
      private final Query<EntityStore> query = Query.and(this.componentType, this.fromWorldGenComponentType);

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Nullable
      @Override
      public SystemGroup<EntityStore> getGroup() {
         return EntityModule.get().getPreClearMarkersGroup();
      }

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         holder.putComponent(this.worldGenIdComponentType, new WorldGenId(holder.getComponent(this.fromWorldGenComponentType).getWorldGenId()));
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
      }
   }

   public static class CacheMarker extends RefSystem<EntityStore> {
      private final ComponentType<EntityStore, SpawnMarkerEntity> componentType;

      public CacheMarker(ComponentType<EntityStore, SpawnMarkerEntity> componentType) {
         this.componentType = componentType;
      }

      @Override
      public Query<EntityStore> getQuery() {
         return this.componentType;
      }

      @Override
      public void onEntityAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         SpawnMarkerEntity entity = store.getComponent(ref, this.componentType);
         SpawnMarker marker = SpawnMarker.getAssetMap().getAsset(entity.getSpawnMarkerId());
         if (marker == null) {
            SpawnMarkerSystems.LOGGER.at(Level.SEVERE).log("Marker %s removed due to missing spawn marker type: %s", ref, entity.getSpawnMarkerId());
            commandBuffer.removeEntity(ref, RemoveReason.REMOVE);
         } else {
            entity.setCachedMarker(marker);
         }
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }
   }

   public static class EnsureNetworkSendable extends HolderSystem<EntityStore> {
      private final Query<EntityStore> query = SpawnMarkerEntity.getComponentType();

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         if (!holder.getArchetype().contains(NetworkId.getComponentType())) {
            holder.addComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
         }

         holder.ensureComponent(Intangible.getComponentType());
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
      }

      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }
   }

   public static class EntityAdded extends RefSystem<EntityStore> {
      private final ComponentType<EntityStore, SpawnMarkerEntity> componentType;
      @Nonnull
      private final ComponentType<EntityStore, UUIDComponent> uuidComponentType;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies;
      @Nonnull
      private final Query<EntityStore> query;

      public EntityAdded(ComponentType<EntityStore, SpawnMarkerEntity> componentType) {
         this.componentType = componentType;
         this.uuidComponentType = UUIDComponent.getComponentType();
         this.dependencies = Set.of(new SystemDependency<>(Order.AFTER, SpawnMarkerSystems.CacheMarker.class));
         this.query = Query.and(componentType, this.uuidComponentType);
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
      public void onEntityAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         SpawnMarkerEntity entity = store.getComponent(ref, this.componentType);
         HytaleLogger.Api context = SpawnMarkerSystems.LOGGER.at(Level.FINE);
         if (context.isEnabled()) {
            context.log("Loaded marker %s", store.getComponent(ref, this.uuidComponentType));
         }

         if (entity.getStoredFlock() != null) {
            entity.setTempStorageList(new ObjectArrayList<>());
         }

         if (entity.getSpawnCount() != 0) {
            entity.refreshTimeout();
         }

         commandBuffer.ensureComponent(ref, PrefabCopyableComponent.getComponentType());
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }
   }

   public static class EntityAddedFromExternal extends RefSystem<EntityStore> {
      @Nonnull
      private final Query<EntityStore> query;
      private final ComponentType<EntityStore, SpawnMarkerEntity> componentType;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies;

      public EntityAddedFromExternal(ComponentType<EntityStore, SpawnMarkerEntity> componentType) {
         this.query = Query.and(componentType, Query.or(FromPrefab.getComponentType(), FromWorldGen.getComponentType()));
         this.componentType = componentType;
         this.dependencies = Set.of(
            new SystemDependency<>(Order.BEFORE, SpawnMarkerSystems.EntityAdded.class),
            new SystemDependency<>(Order.AFTER, SpawnMarkerSystems.CacheMarker.class)
         );
      }

      @Override
      public void onEntityAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         SpawnMarkerEntity entity = store.getComponent(ref, this.componentType);
         entity.setSpawnCount(0);
         entity.setRespawnCounter(0.0);
         entity.setSpawnAfter(null);
         entity.setGameTimeRespawn(null);
         if (entity.getCachedMarker().getDeactivationDistance() > 0.0) {
            entity.setStoredFlock(new StoredFlock());
         }
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
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

      @Nullable
      @Override
      public SystemGroup<EntityStore> getGroup() {
         return EntityModule.get().getPreClearMarkersGroup();
      }
   }

   @Deprecated(forRemoval = true)
   public static class LegacyEntityMigration extends EntityModule.MigrationSystem {
      private final ComponentType<EntityStore, PersistentModel> persistentModelComponentType = PersistentModel.getComponentType();
      private final ComponentType<EntityStore, Nameplate> nameplateComponentType = Nameplate.getComponentType();
      private final ComponentType<EntityStore, UUIDComponent> uuidComponentType = UUIDComponent.getComponentType();
      private final ComponentType<EntityStore, UnknownComponents<EntityStore>> unknownComponentsComponentType = EntityStore.REGISTRY.getUnknownComponentType();
      private final Query<EntityStore> query = Query.and(this.unknownComponentsComponentType, Query.not(AllLegacyEntityTypesQuery.INSTANCE));

      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         Map<String, BsonDocument> unknownComponents = holder.getComponent(this.unknownComponentsComponentType).getUnknownComponents();
         BsonDocument spawnMarker = unknownComponents.remove("SpawnMarker");
         if (spawnMarker != null) {
            if (!holder.getArchetype().contains(this.persistentModelComponentType)) {
               Model.ModelReference modelReference = Entity.MODEL.get(spawnMarker).get();
               holder.addComponent(this.persistentModelComponentType, new PersistentModel(modelReference));
            }

            if (!holder.getArchetype().contains(this.nameplateComponentType)) {
               holder.addComponent(this.nameplateComponentType, new Nameplate(Entity.DISPLAY_NAME.get(spawnMarker).get()));
            }

            if (!holder.getArchetype().contains(this.uuidComponentType)) {
               holder.addComponent(this.uuidComponentType, new UUIDComponent(Entity.UUID.get(spawnMarker).get()));
            }

            holder.ensureComponent(HiddenFromAdventurePlayers.getComponentType());
            int worldgenId = Codec.INTEGER.decode(spawnMarker.get("WorldgenId"));
            if (worldgenId != 0) {
               holder.addComponent(WorldGenId.getComponentType(), new WorldGenId(worldgenId));
            }

            SpawnMarkerEntity marker = SpawnMarkerEntity.CODEC.decode(spawnMarker, new ExtraInfo(5));
            holder.addComponent(SpawnMarkerEntity.getComponentType(), marker);
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
         return RootDependency.firstSet();
      }
   }

   public static class Ticking extends EntityTickingSystem<EntityStore> {
      private final ComponentType<EntityStore, SpawnMarkerEntity> componentType;
      @Nullable
      private final ComponentType<EntityStore, NPCEntity> npcComponentType;
      private final ComponentType<EntityStore, PersistentRefCount> referenceIdComponentType;
      private final ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> playerSpatialComponent;
      @Nonnull
      private final Set<Dependency<EntityStore>> dependencies;
      private final Query<EntityStore> query;
      private final ComponentType<EntityStore, TransformComponent> transformComponentType = TransformComponent.getComponentType();
      private final ComponentType<EntityStore, HeadRotation> headRotationComponentType = HeadRotation.getComponentType();
      private final ComponentType<EntityStore, ModelComponent> modelComponentType = ModelComponent.getComponentType();

      public Ticking(
         ComponentType<EntityStore, SpawnMarkerEntity> componentType,
         ResourceType<EntityStore, SpatialResource<Ref<EntityStore>, EntityStore>> playerSpatialComponent
      ) {
         this.componentType = componentType;
         this.npcComponentType = NPCEntity.getComponentType();
         this.referenceIdComponentType = PersistentRefCount.getComponentType();
         this.playerSpatialComponent = playerSpatialComponent;
         this.dependencies = Set.of(new SystemDependency<>(Order.AFTER, PlayerSpatialSystem.class, OrderPriority.CLOSEST));
         this.query = Archetype.of(componentType, this.transformComponentType);
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.dependencies;
      }

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
         SpawnMarkerEntity entity = archetypeChunk.getComponent(index, this.componentType);
         TransformComponent transform = archetypeChunk.getComponent(index, this.transformComponentType);
         World world = store.getExternalData().getWorld();
         SpawnMarker cachedMarker = entity.getCachedMarker();
         if (entity.getSpawnCount() > 0) {
            StoredFlock storedFlock = entity.getStoredFlock();
            if (storedFlock != null) {
               SpatialResource<Ref<EntityStore>, EntityStore> spatialResource = store.getResource(this.playerSpatialComponent);
               ObjectList<Ref<EntityStore>> results = SpatialResource.getThreadLocalReferenceList();
               spatialResource.getSpatialStructure().collect(transform.getPosition(), cachedMarker.getDeactivationDistance(), results);
               boolean hasPlayersInRange = !results.isEmpty();
               if (!hasPlayersInRange) {
                  if (!storedFlock.hasStoredNPCs() && entity.tickTimeToDeactivation(dt)) {
                     InvalidatablePersistentRef[] npcReferences = entity.getNpcReferences();
                     if (npcReferences == null) {
                        return;
                     }

                     if (!entity.isDespawnStarted()) {
                        List<Pair<Ref<EntityStore>, NPCEntity>> tempStorageList = entity.getTempStorageList();

                        for (InvalidatablePersistentRef reference : npcReferences) {
                           Ref<EntityStore> npcRef = reference.getEntity(commandBuffer);
                           if (npcRef != null) {
                              NPCEntity npcComponent = commandBuffer.getComponent(npcRef, this.npcComponentType);

                              assert npcComponent != null;

                              tempStorageList.add(Pair.of(npcRef, npcComponent));
                              boolean isDead = commandBuffer.getArchetype(npcRef).contains(DeathComponent.getComponentType());
                              if (isDead || npcComponent.getRole().getStateSupport().isInBusyState()) {
                                 entity.setTimeToDeactivation(cachedMarker.getDeactivationTime());
                                 tempStorageList.clear();
                                 return;
                              }
                           }
                        }

                        for (int i = 0; i < tempStorageList.size(); i++) {
                           Pair<Ref<EntityStore>, NPCEntity> npcPair = tempStorageList.get(i);
                           Ref<EntityStore> npcRef = npcPair.first();
                           NPCEntity npcComponentx = npcPair.second();
                           ModelComponent modelComponent = commandBuffer.getComponent(npcRef, this.modelComponentType);
                           if (modelComponent != null && modelComponent.getModel().getAnimationSetMap().containsKey("Despawn")) {
                              double despawnAnimationTime = npcComponentx.getRole().getDespawnAnimationTime();
                              if (despawnAnimationTime > entity.getTimeToDeactivation()) {
                                 entity.setTimeToDeactivation(despawnAnimationTime);
                              }

                              npcComponentx.playAnimation(npcRef, AnimationSlot.Status, "Despawn", commandBuffer);
                           }
                        }

                        entity.setDespawnStarted(true);
                        tempStorageList.clear();
                        return;
                     }

                     PersistentRefCount refId = archetypeChunk.getComponent(index, this.referenceIdComponentType);
                     if (refId != null) {
                        refId.increment();
                     }

                     Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
                     commandBuffer.run(
                        _store -> {
                           ObjectList<Ref<EntityStore>> tempStorageList = SpatialResource.getThreadLocalReferenceList();

                           for (InvalidatablePersistentRef referencex : npcReferences) {
                              Ref<EntityStore> npcRef = referencex.getEntity(_store);
                              if (npcRef == null) {
                                 SpawnMarkerSystems.LOGGER
                                    .atWarning()
                                    .log("Connection with NPC from marker at %s lost due to being invalid/already unloaded", transform.getPosition());
                              } else {
                                 SpawnMarkerReference spawnMarkerReference = _store.ensureAndGetComponent(npcRef, SpawnMarkerReference.getComponentType());
                                 spawnMarkerReference.getReference().setEntity(ref, store);
                                 tempStorageList.add(npcRef);
                              }
                           }

                           storedFlock.storeNPCs(tempStorageList, _store);
                           entity.setNpcReferences(null);
                        }
                     );
                  }

                  return;
               }

               if (storedFlock.hasStoredNPCs()) {
                  commandBuffer.run(_store -> {
                     ObjectList<Ref<EntityStore>> tempStorageList = SpatialResource.getThreadLocalReferenceList();
                     storedFlock.restoreNPCs(tempStorageList, _store);
                     entity.setSpawnCount(tempStorageList.size());
                     Vector3d position = entity.getSpawnPosition();
                     Vector3f rotation = transform.getRotation();
                     InvalidatablePersistentRef[] npcReferencesx = new InvalidatablePersistentRef[tempStorageList.size()];
                     int ix = 0;

                     for (int bound = tempStorageList.size(); ix < bound; ix++) {
                        Ref<EntityStore> refx = tempStorageList.get(ix);
                        NPCEntity npc = _store.getComponent(refx, this.npcComponentType);
                        TransformComponent npcTransform = _store.getComponent(refx, this.transformComponentType);
                        HeadRotation npcHeadRotation = _store.getComponent(refx, this.headRotationComponentType);
                        InvalidatablePersistentRef referencex = new InvalidatablePersistentRef();
                        referencex.setEntity(refx, _store);
                        npcReferencesx[ix] = referencex;
                        npcTransform.getPosition().assign(position);
                        npcTransform.getRotation().assign(rotation);
                        npcHeadRotation.setRotation(rotation);
                        npc.playAnimation(refx, AnimationSlot.Status, null, commandBuffer);
                     }

                     entity.setNpcReferences(npcReferencesx);
                     entity.setDespawnStarted(false);
                     entity.setTimeToDeactivation(cachedMarker.getDeactivationTime());
                  });
               }
            }

            if (entity.tickSpawnLostTimeout(dt)) {
               PersistentRefCount refId = archetypeChunk.getComponent(index, this.referenceIdComponentType);
               if (refId != null) {
                  refId.increment();
                  SpawnMarkerSystems.LOGGER.at(Level.FINE).log("Marker lost spawned NPC and changed reference ID to %s", refId.get());
               }

               Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
               commandBuffer.run(_store -> entity.spawnNPC(ref, cachedMarker, _store));
            }
         } else if (world.getWorldConfig().isSpawnMarkersEnabled()
            && !cachedMarker.isManualTrigger()
            && (entity.getSuppressedBy() == null || entity.getSuppressedBy().isEmpty())) {
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            WorldTimeResource worldTimeResource = commandBuffer.getResource(WorldTimeResource.getResourceType());
            if (cachedMarker.isRealtimeRespawn()) {
               if (entity.tickRespawnTimer(dt)) {
                  commandBuffer.run(_store -> entity.spawnNPC(ref, cachedMarker, _store));
               }
            } else if (entity.getSpawnAfter() == null || worldTimeResource.getGameTime().isAfter(entity.getSpawnAfter())) {
               commandBuffer.run(_store -> entity.spawnNPC(ref, cachedMarker, _store));
            }
         }
      }
   }
}
