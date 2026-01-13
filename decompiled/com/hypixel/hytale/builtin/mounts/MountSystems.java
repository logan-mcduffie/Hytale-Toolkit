package com.hypixel.hytale.builtin.mounts;

import com.hypixel.hytale.builtin.mounts.minecart.MinecartComponent;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.SystemGroup;
import com.hypixel.hytale.component.dependency.Dependency;
import com.hypixel.hytale.component.dependency.Order;
import com.hypixel.hytale.component.dependency.OrderPriority;
import com.hypixel.hytale.component.dependency.RootDependency;
import com.hypixel.hytale.component.dependency.SystemDependency;
import com.hypixel.hytale.component.dependency.SystemGroupDependency;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.AnimationSlot;
import com.hypixel.hytale.protocol.BlockMount;
import com.hypixel.hytale.protocol.ComponentUpdate;
import com.hypixel.hytale.protocol.ComponentUpdateType;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.protocol.MountController;
import com.hypixel.hytale.protocol.MountedUpdate;
import com.hypixel.hytale.protocol.MovementStates;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.BlockType;
import com.hypixel.hytale.server.core.asset.type.blocktype.config.mountpoints.BlockMountPoint;
import com.hypixel.hytale.server.core.entity.AnimationUtils;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.entity.movement.MovementStatesComponent;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.component.Interactable;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageModule;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.item.ItemComponent;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerInput;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSystems;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.modules.entity.teleport.TeleportSystems;
import com.hypixel.hytale.server.core.modules.entity.tracker.EntityTrackerSystems;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.modules.time.TimeResource;
import com.hypixel.hytale.server.core.prefab.PrefabCopyableComponent;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class MountSystems {
   private static void handleMountedRemoval(Ref<EntityStore> ref, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull MountedComponent component) {
      Ref<EntityStore> mountedToEntity = component.getMountedToEntity();
      if (mountedToEntity != null && mountedToEntity.isValid()) {
         MountedByComponent mountedBy = commandBuffer.getComponent(mountedToEntity, MountedByComponent.getComponentType());
         if (mountedBy != null) {
            mountedBy.removePassenger(ref);
         }
      }

      Ref<ChunkStore> mountedToBlock = component.getMountedToBlock();
      if (mountedToBlock != null && mountedToBlock.isValid()) {
         Store<ChunkStore> chunkStore = mountedToBlock.getStore();
         BlockMountComponent seatComponent = chunkStore.getComponent(mountedToBlock, BlockMountComponent.getComponentType());
         if (seatComponent != null) {
            seatComponent.removeSeatedEntity(ref);
            if (seatComponent.isDead()) {
               chunkStore.removeComponent(mountedToBlock, BlockMountComponent.getComponentType());
            }
         }
      }
   }

   public static class EnsureMinecartComponents extends HolderSystem<EntityStore> {
      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         holder.ensureComponent(Interactable.getComponentType());
         holder.putComponent(NetworkId.getComponentType(), new NetworkId(store.getExternalData().takeNextNetworkId()));
         holder.ensureComponent(PrefabCopyableComponent.getComponentType());
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
      }

      @Override
      public Query<EntityStore> getQuery() {
         return MinecartComponent.getComponentType();
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return RootDependency.firstSet();
      }
   }

   public static class HandleMountInput extends EntityTickingSystem<EntityStore> {
      private final Query<EntityStore> query = Query.and(MountedComponent.getComponentType(), PlayerInput.getComponentType());
      private final Set<Dependency<EntityStore>> deps = Set.of(new SystemDependency<>(Order.BEFORE, PlayerSystems.ProcessPlayerInput.class));

      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         MountedComponent mounted = archetypeChunk.getComponent(index, MountedComponent.getComponentType());

         assert mounted != null;

         PlayerInput input = archetypeChunk.getComponent(index, PlayerInput.getComponentType());

         assert input != null;

         MountController controller = mounted.getControllerType();
         Ref<EntityStore> targetRef = controller == MountController.BlockMount ? archetypeChunk.getReferenceTo(index) : mounted.getMountedToEntity();
         List<PlayerInput.InputUpdate> queue = input.getMovementUpdateQueue();

         for (int i = 0; i < queue.size(); i++) {
            PlayerInput.InputUpdate q = queue.get(i);
            if (controller == MountController.BlockMount && (q instanceof PlayerInput.RelativeMovement || q instanceof PlayerInput.AbsoluteMovement)) {
               if (mounted.getMountedDurationMs() < 600L) {
                  continue;
               }

               Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
               commandBuffer.removeComponent(ref, MountedComponent.getComponentType());
            }

            if (q instanceof PlayerInput.SetRiderMovementStates s) {
               MovementStates states = s.movementStates();
               MovementStatesComponent movementStatesComponent = archetypeChunk.getComponent(index, MovementStatesComponent.getComponentType());
               if (movementStatesComponent != null) {
                  movementStatesComponent.setMovementStates(states);
               }
            } else if (!(q instanceof PlayerInput.WishMovement)) {
               if (q instanceof PlayerInput.RelativeMovement relative) {
                  relative.apply(commandBuffer, archetypeChunk, index);
                  TransformComponent transform = commandBuffer.getComponent(targetRef, TransformComponent.getComponentType());
                  transform.getPosition().add(relative.getX(), relative.getY(), relative.getZ());
               } else if (q instanceof PlayerInput.AbsoluteMovement absolute) {
                  absolute.apply(commandBuffer, archetypeChunk, index);
                  TransformComponent transform = commandBuffer.getComponent(targetRef, TransformComponent.getComponentType());
                  transform.getPosition().assign(absolute.getX(), absolute.getY(), absolute.getZ());
               } else if (q instanceof PlayerInput.SetMovementStates sx) {
                  MovementStates states = sx.movementStates();
                  MovementStatesComponent movementStatesComponent = commandBuffer.getComponent(targetRef, MovementStatesComponent.getComponentType());
                  if (movementStatesComponent != null) {
                     movementStatesComponent.setMovementStates(states);
                  }
               } else if (q instanceof PlayerInput.SetBody body) {
                  body.apply(commandBuffer, archetypeChunk, index);
                  TransformComponent transform = commandBuffer.getComponent(targetRef, TransformComponent.getComponentType());
                  transform.getRotation().assign(body.direction().pitch, body.direction().yaw, body.direction().roll);
               } else if (q instanceof PlayerInput.SetHead head) {
                  head.apply(commandBuffer, archetypeChunk, index);
               }
            }
         }

         queue.clear();
      }

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return this.deps;
      }
   }

   public static class MountedEntityDeath extends RefChangeSystem<EntityStore, DeathComponent> {
      @Override
      public Query<EntityStore> getQuery() {
         return MountedComponent.getComponentType();
      }

      @Nonnull
      @Override
      public ComponentType<EntityStore, DeathComponent> componentType() {
         return DeathComponent.getComponentType();
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         commandBuffer.removeComponent(ref, MountedComponent.getComponentType());
      }

      public void onComponentSet(
         @Nonnull Ref<EntityStore> ref,
         @Nullable DeathComponent oldComponent,
         @Nonnull DeathComponent newComponent,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      public void onComponentRemoved(
         @Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }
   }

   public static class OnMinecartHit extends DamageEventSystem {
      private static final Duration HIT_RESET_TIME = Duration.ofSeconds(10L);
      private static final int NUMBER_OF_HITS = 3;
      @Nonnull
      private static final Query<EntityStore> QUERY = Archetype.of(MinecartComponent.getComponentType(), TransformComponent.getComponentType());
      @Nonnull
      private static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(
         new SystemGroupDependency<>(Order.AFTER, DamageModule.get().getGatherDamageGroup()),
         new SystemGroupDependency<>(Order.AFTER, DamageModule.get().getFilterDamageGroup()),
         new SystemGroupDependency<>(Order.BEFORE, DamageModule.get().getInspectDamageGroup())
      );

      @Override
      public Query<EntityStore> getQuery() {
         return QUERY;
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return DEPENDENCIES;
      }

      public void handle(
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer,
         @Nonnull Damage damage
      ) {
         MinecartComponent minecartComponent = archetypeChunk.getComponent(index, MinecartComponent.getComponentType());

         assert minecartComponent != null;

         Instant currentTime = commandBuffer.getResource(TimeResource.getResourceType()).getNow();
         if (minecartComponent.getLastHit() != null && currentTime.isAfter(minecartComponent.getLastHit().plus(HIT_RESET_TIME))) {
            minecartComponent.setLastHit(null);
            minecartComponent.setNumberOfHits(0);
         }

         if (!(damage.getAmount() <= 0.0F)) {
            minecartComponent.setNumberOfHits(minecartComponent.getNumberOfHits() + 1);
            minecartComponent.setLastHit(currentTime);
            if (minecartComponent.getNumberOfHits() == 3) {
               commandBuffer.removeEntity(archetypeChunk.getReferenceTo(index), RemoveReason.REMOVE);
               boolean shouldDropItem = true;
               if (damage.getSource() instanceof Damage.EntitySource source) {
                  Player playerComponent = source.getRef().isValid() ? commandBuffer.getComponent(source.getRef(), Player.getComponentType()) : null;
                  if (playerComponent != null) {
                     shouldDropItem = playerComponent.getGameMode() != GameMode.Creative;
                  }
               }

               if (shouldDropItem && minecartComponent.getSourceItem() != null) {
                  TransformComponent transform = archetypeChunk.getComponent(index, TransformComponent.getComponentType());

                  assert transform != null;

                  Holder<EntityStore> drop = ItemComponent.generateItemDrop(
                     commandBuffer, new ItemStack(minecartComponent.getSourceItem()), transform.getPosition(), transform.getRotation(), 0.0F, 1.0F, 0.0F
                  );
                  if (drop != null) {
                     commandBuffer.addEntity(drop, AddReason.SPAWN);
                  }
               }
            }
         }
      }
   }

   public static class PlayerMount extends RefChangeSystem<EntityStore, MountedComponent> {
      private final Query<EntityStore> query = PlayerInput.getComponentType();

      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Nonnull
      @Override
      public ComponentType<EntityStore, MountedComponent> componentType() {
         return MountedComponent.getComponentType();
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull MountedComponent component,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         MountedComponent mounted = commandBuffer.getComponent(ref, MountedComponent.getComponentType());

         assert mounted != null;

         PlayerInput input = commandBuffer.getComponent(ref, PlayerInput.getComponentType());

         assert input != null;

         Ref<EntityStore> mountRef = mounted.getMountedToEntity();
         if (mountRef != null && mountRef.isValid()) {
            int mountNetworkId = commandBuffer.getComponent(mountRef, NetworkId.getComponentType()).getId();
            input.setMountId(mountNetworkId);
            input.getMovementUpdateQueue().clear();
         }
      }

      public void onComponentSet(
         @Nonnull Ref<EntityStore> ref,
         @Nullable MountedComponent oldComponent,
         @Nonnull MountedComponent newComponent,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      public void onComponentRemoved(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull MountedComponent component,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         PlayerInput input = commandBuffer.getComponent(ref, PlayerInput.getComponentType());

         assert input != null;

         input.setMountId(0);
      }
   }

   public static class RemoveBlockSeat extends RefSystem<ChunkStore> {
      @Override
      public void onEntityAdded(
         @Nonnull Ref<ChunkStore> ref, @Nonnull AddReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<ChunkStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<ChunkStore> store, @Nonnull CommandBuffer<ChunkStore> commandBuffer
      ) {
         BlockMountComponent blockSeatComponent = commandBuffer.getComponent(ref, BlockMountComponent.getComponentType());

         assert blockSeatComponent != null;

         ObjectArrayList<? extends Ref<EntityStore>> dismounting = new ObjectArrayList<>(blockSeatComponent.getSeatedEntities());
         World world = ref.getStore().getExternalData().getWorld();

         for (Ref<EntityStore> seated : dismounting) {
            blockSeatComponent.removeSeatedEntity(seated);
            world.execute(() -> {
               if (seated.isValid()) {
                  seated.getStore().tryRemoveComponent(seated, MountedComponent.getComponentType());
               }
            });
         }
      }

      @Override
      public Query<ChunkStore> getQuery() {
         return BlockMountComponent.getComponentType();
      }
   }

   public static class RemoveMounted extends RefSystem<EntityStore> {
      @Override
      public void onEntityAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         MountedComponent mounted = commandBuffer.getComponent(ref, MountedComponent.getComponentType());
         commandBuffer.removeComponent(ref, MountedComponent.getComponentType());
         MountSystems.handleMountedRemoval(ref, commandBuffer, mounted);
      }

      @Override
      public Query<EntityStore> getQuery() {
         return MountedComponent.getComponentType();
      }
   }

   public static class RemoveMountedBy extends RefSystem<EntityStore> {
      @Override
      public void onEntityAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         MountedByComponent by = commandBuffer.getComponent(ref, MountedByComponent.getComponentType());

         for (Ref<EntityStore> p : by.getPassengers()) {
            if (p.isValid()) {
               MountedComponent mounted = commandBuffer.getComponent(p, MountedComponent.getComponentType());
               if (mounted != null) {
                  Ref<EntityStore> target = mounted.getMountedToEntity();
                  if (!target.isValid() || target.equals(ref)) {
                     commandBuffer.removeComponent(p, MountedComponent.getComponentType());
                  }
               }
            }
         }
      }

      @Override
      public Query<EntityStore> getQuery() {
         return MountedByComponent.getComponentType();
      }
   }

   public static class TeleportMountedEntity extends RefChangeSystem<EntityStore, Teleport> {
      private static final Set<Dependency<EntityStore>> DEPENDENCIES = Set.of(
         new SystemDependency<>(Order.BEFORE, TeleportSystems.MoveSystem.class, OrderPriority.CLOSEST),
         new SystemDependency<>(Order.BEFORE, TeleportSystems.PlayerMoveSystem.class, OrderPriority.CLOSEST)
      );

      @Override
      public Query<EntityStore> getQuery() {
         return MountedComponent.getComponentType();
      }

      @Nonnull
      @Override
      public ComponentType<EntityStore, Teleport> componentType() {
         return Teleport.getComponentType();
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull Teleport component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         commandBuffer.removeComponent(ref, MountedComponent.getComponentType());
      }

      public void onComponentSet(
         @Nonnull Ref<EntityStore> ref,
         @Nullable Teleport oldComponent,
         @Nonnull Teleport newComponent,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      public void onComponentRemoved(
         @Nonnull Ref<EntityStore> ref, @Nonnull Teleport component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      @Nonnull
      @Override
      public Set<Dependency<EntityStore>> getDependencies() {
         return DEPENDENCIES;
      }
   }

   public static class TrackedMounted extends RefChangeSystem<EntityStore, MountedComponent> {
      @Override
      public Query<EntityStore> getQuery() {
         return MountedComponent.getComponentType();
      }

      @Nonnull
      @Override
      public ComponentType<EntityStore, MountedComponent> componentType() {
         return MountedComponent.getComponentType();
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull MountedComponent component,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         Ref<EntityStore> target = component.getMountedToEntity();
         if (target != null && target.isValid()) {
            MountedByComponent mountedBy = commandBuffer.ensureAndGetComponent(target, MountedByComponent.getComponentType());
            mountedBy.addPassenger(ref);
         }
      }

      public void onComponentSet(
         @Nonnull Ref<EntityStore> ref,
         MountedComponent oldComponent,
         @Nonnull MountedComponent newComponent,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      public void onComponentRemoved(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull MountedComponent component,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         MountSystems.handleMountedRemoval(ref, commandBuffer, component);
      }
   }

   public static class TrackerRemove extends RefChangeSystem<EntityStore, MountedComponent> {
      @Override
      public Query<EntityStore> getQuery() {
         return EntityTrackerSystems.Visible.getComponentType();
      }

      @Nonnull
      @Override
      public ComponentType<EntityStore, MountedComponent> componentType() {
         return MountedComponent.getComponentType();
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull MountedComponent component,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      public void onComponentSet(
         @Nonnull Ref<EntityStore> ref,
         MountedComponent oldComponent,
         @Nonnull MountedComponent newComponent,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      public void onComponentRemoved(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull MountedComponent component,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         if (component.getControllerType() == MountController.BlockMount) {
            AnimationUtils.stopAnimation(ref, AnimationSlot.Movement, true, commandBuffer);
         }

         EntityTrackerSystems.Visible visibleComponent = store.getComponent(ref, EntityTrackerSystems.Visible.getComponentType());

         assert visibleComponent != null;

         for (EntityTrackerSystems.EntityViewer viewer : visibleComponent.visibleTo.values()) {
            viewer.queueRemove(ref, ComponentUpdateType.Mounted);
         }
      }
   }

   public static class TrackerUpdate extends EntityTickingSystem<EntityStore> {
      private final ComponentType<EntityStore, EntityTrackerSystems.Visible> componentType = EntityTrackerSystems.Visible.getComponentType();
      @Nonnull
      private final Query<EntityStore> query = Query.and(this.componentType, MountedComponent.getComponentType());

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
         EntityTrackerSystems.Visible visible = archetypeChunk.getComponent(index, this.componentType);
         MountedComponent mounted = archetypeChunk.getComponent(index, MountedComponent.getComponentType());
         Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
         if (mounted.consumeNetworkOutdated()) {
            queueUpdatesFor(ref, visible.visibleTo, mounted);
         } else if (!visible.newlyVisibleTo.isEmpty()) {
            queueUpdatesFor(ref, visible.newlyVisibleTo, mounted);
         }
      }

      private static void queueUpdatesFor(
         @Nonnull Ref<EntityStore> ref, @Nonnull Map<Ref<EntityStore>, EntityTrackerSystems.EntityViewer> visibleTo, @Nonnull MountedComponent component
      ) {
         ComponentUpdate update = new ComponentUpdate();
         update.type = ComponentUpdateType.Mounted;
         Ref<EntityStore> mountedToEntity = component.getMountedToEntity();
         Ref<ChunkStore> mountedToBlock = component.getMountedToBlock();
         Vector3f offset = component.getAttachmentOffset();
         com.hypixel.hytale.protocol.Vector3f netOffset = new com.hypixel.hytale.protocol.Vector3f(offset.x, offset.y, offset.z);
         MountedUpdate mountedUpdate;
         if (mountedToEntity != null) {
            int mountedToNetworkId = ref.getStore().getComponent(mountedToEntity, NetworkId.getComponentType()).getId();
            mountedUpdate = new MountedUpdate(mountedToNetworkId, netOffset, component.getControllerType(), null);
         } else {
            if (mountedToBlock == null) {
               throw new UnsupportedOperationException("Couldn't create MountedUpdate packet for MountedComponent");
            }

            BlockMountComponent blockMountComponent = mountedToBlock.getStore().getComponent(mountedToBlock, BlockMountComponent.getComponentType());
            if (blockMountComponent == null) {
               return;
            }

            BlockMountPoint occupiedSeat = blockMountComponent.getSeatBlockBySeatedEntity(ref);
            if (occupiedSeat == null) {
               return;
            }

            BlockType blockType = blockMountComponent.getExpectedBlockType();
            Vector3f position = occupiedSeat.computeWorldSpacePosition(blockMountComponent.getBlockPos());
            Vector3f rotationEuler = occupiedSeat.computeRotationEuler(blockMountComponent.getExpectedRotation());
            BlockMount blockMount = new BlockMount(
               blockMountComponent.getType(),
               new com.hypixel.hytale.protocol.Vector3f(position.x, position.y, position.z),
               new com.hypixel.hytale.protocol.Vector3f(rotationEuler.x, rotationEuler.y, rotationEuler.z),
               BlockType.getAssetMap().getIndex(blockType.getId())
            );
            mountedUpdate = new MountedUpdate(0, netOffset, component.getControllerType(), blockMount);
         }

         update.mounted = mountedUpdate;

         for (EntityTrackerSystems.EntityViewer viewer : visibleTo.values()) {
            viewer.queueUpdate(ref, update);
         }
      }
   }
}
