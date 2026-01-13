package com.hypixel.hytale.server.core.modules.entity.teleport;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefChangeSystem;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.math.vector.Vector3f;
import com.hypixel.hytale.protocol.ModelTransform;
import com.hypixel.hytale.protocol.packets.player.ClientTeleport;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.component.CollisionResultComponent;
import com.hypixel.hytale.server.core.modules.entity.component.HeadRotation;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.core.util.PositionUtil;
import javax.annotation.Nonnull;

public class TeleportSystems {
   public static class MoveSystem extends RefChangeSystem<EntityStore, Teleport> {
      @Nonnull
      private final ComponentType<EntityStore, Teleport> teleportComponentType = Teleport.getComponentType();
      @Nonnull
      private final ComponentType<EntityStore, TransformComponent> transformComponentType = TransformComponent.getComponentType();
      @Nonnull
      private final ComponentType<EntityStore, HeadRotation> headRotationComponentType = HeadRotation.getComponentType();
      @Nonnull
      private final Query<EntityStore> query = Query.and(this.teleportComponentType, this.transformComponentType, Query.not(PlayerRef.getComponentType()));

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Nonnull
      @Override
      public ComponentType<EntityStore, Teleport> componentType() {
         return this.teleportComponentType;
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull Teleport teleport, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         TransformComponent transformComponent = commandBuffer.getComponent(ref, this.transformComponentType);

         assert transformComponent != null;

         transformComponent.teleportPosition(teleport.getPosition());
         transformComponent.teleportRotation(teleport.getRotation());
         HeadRotation headRotationComponent = commandBuffer.getComponent(ref, this.headRotationComponentType);
         if (headRotationComponent != null) {
            headRotationComponent.teleportRotation(teleport.getRotation());
         }

         World targetWorld = teleport.getWorld();
         if (targetWorld != null && !targetWorld.equals(store.getExternalData().getWorld())) {
            commandBuffer.run(s -> {
               Holder<EntityStore> holder = s.removeEntity(ref, RemoveReason.UNLOAD);
               targetWorld.execute(() -> targetWorld.getEntityStore().getStore().addEntity(holder, AddReason.LOAD));
            });
         }

         commandBuffer.removeComponent(ref, this.teleportComponentType);
      }

      public void onComponentRemoved(
         @Nonnull Ref<EntityStore> ref, @Nonnull Teleport component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      public void onComponentSet(
         @Nonnull Ref<EntityStore> ref,
         Teleport oldComponent,
         @Nonnull Teleport newComponent,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }
   }

   public static class PlayerMoveCompleteSystem extends RefChangeSystem<EntityStore, PendingTeleport> {
      @Nonnull
      private final ComponentType<EntityStore, PendingTeleport> pendingComponentType = PendingTeleport.getComponentType();
      @Nonnull
      private final ComponentType<EntityStore, Player> playerComponentType = Player.getComponentType();
      @Nonnull
      private final ComponentType<EntityStore, TransformComponent> transformComponentType = TransformComponent.getComponentType();
      @Nonnull
      private final ComponentType<EntityStore, CollisionResultComponent> collisionResultComponentType = CollisionResultComponent.getComponentType();
      @Nonnull
      private final Query<EntityStore> query = Query.and(this.playerComponentType, this.transformComponentType);

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Nonnull
      @Override
      public ComponentType<EntityStore, PendingTeleport> componentType() {
         return this.pendingComponentType;
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull PendingTeleport component,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      public void onComponentSet(
         @Nonnull Ref<EntityStore> ref,
         PendingTeleport oldComponent,
         @Nonnull PendingTeleport newComponent,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      public void onComponentRemoved(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull PendingTeleport component,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         Player playerComponent = commandBuffer.getComponent(ref, this.playerComponentType);

         assert playerComponent != null;

         TransformComponent transformComponent = commandBuffer.getComponent(ref, this.transformComponentType);

         assert transformComponent != null;

         CollisionResultComponent collisionResultComponent = commandBuffer.getComponent(ref, this.collisionResultComponentType);
         if (collisionResultComponent != null) {
            collisionResultComponent.getCollisionStartPosition().assign(transformComponent.getPosition());
         }

         playerComponent.moveTo(ref, component.getPosition().x, component.getPosition().y, component.getPosition().z, commandBuffer);
      }
   }

   public static class PlayerMoveSystem extends RefChangeSystem<EntityStore, Teleport> {
      @Nonnull
      private final ComponentType<EntityStore, Teleport> teleportComponentType = Teleport.getComponentType();
      @Nonnull
      private final ComponentType<EntityStore, TransformComponent> transformComponentType = TransformComponent.getComponentType();
      @Nonnull
      private final ComponentType<EntityStore, HeadRotation> headRotationComponentType = HeadRotation.getComponentType();
      @Nonnull
      private final ComponentType<EntityStore, PlayerRef> playerRefComponentType = PlayerRef.getComponentType();
      @Nonnull
      private final ComponentType<EntityStore, Player> playerComponentType = Player.getComponentType();
      @Nonnull
      private final ComponentType<EntityStore, PendingTeleport> pendingTeleportComponentType = PendingTeleport.getComponentType();
      @Nonnull
      private final Query<EntityStore> query = Query.and(
         this.teleportComponentType, this.playerRefComponentType, this.transformComponentType, this.playerComponentType
      );

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      @Nonnull
      @Override
      public ComponentType<EntityStore, Teleport> componentType() {
         return this.teleportComponentType;
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull Teleport teleport, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         World targetWorld = teleport.getWorld();
         if (targetWorld != null && !targetWorld.equals(store.getExternalData().getWorld())) {
            this.teleportToWorld(ref, teleport, commandBuffer, targetWorld);
         } else {
            this.teleportToPosition(ref, teleport, commandBuffer);
         }
      }

      public void onComponentRemoved(
         @Nonnull Ref<EntityStore> ref, @Nonnull Teleport component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      public void onComponentSet(
         @Nonnull Ref<EntityStore> ref,
         Teleport oldComponent,
         @Nonnull Teleport newComponent,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }

      private void teleportToWorld(
         @Nonnull Ref<EntityStore> ref, @Nonnull Teleport teleport, @Nonnull CommandBuffer<EntityStore> commandBuffer, @Nonnull World targetWorld
      ) {
         PlayerRef playerRefComponent = commandBuffer.getComponent(ref, this.playerRefComponentType);

         assert playerRefComponent != null;

         commandBuffer.removeComponent(ref, this.teleportComponentType);
         commandBuffer.run(s -> {
            playerRefComponent.removeFromStore();
            targetWorld.addPlayer(playerRefComponent, new Transform(teleport.getPosition(), teleport.getRotation()));
         });
      }

      private void teleportToPosition(@Nonnull Ref<EntityStore> ref, @Nonnull Teleport teleport, @Nonnull CommandBuffer<EntityStore> commandBuffer) {
         TransformComponent transformComponent = commandBuffer.getComponent(ref, this.transformComponentType);

         assert transformComponent != null;

         PlayerRef playerRefComponent = commandBuffer.getComponent(ref, this.playerRefComponentType);

         assert playerRefComponent != null;

         Player playerComponent = commandBuffer.getComponent(ref, this.playerComponentType);

         assert playerComponent != null;

         PendingTeleport pendingTeleportComponent = commandBuffer.ensureAndGetComponent(ref, this.pendingTeleportComponentType);
         Vector3d teleportPosition = teleport.getPosition();
         Vector3f teleportRotation = teleport.getRotation();
         transformComponent.teleportPosition(teleportPosition);
         transformComponent.teleportRotation(teleportRotation);
         HeadRotation headRotationComponent = commandBuffer.getComponent(ref, this.headRotationComponentType);
         if (headRotationComponent != null) {
            Vector3f teleportHeadRotation = teleport.getHeadRotation();
            headRotationComponent.teleportRotation(teleportHeadRotation != null ? teleportHeadRotation : teleportRotation);
         }

         playerComponent.getWindowManager().validateWindows();
         int id = pendingTeleportComponent.queueTeleport(teleport);
         ClientTeleport teleportPacket = new ClientTeleport(
            (byte)id,
            new ModelTransform(
               PositionUtil.toPositionPacket(transformComponent.getPosition()),
               PositionUtil.toDirectionPacket(transformComponent.getRotation()),
               headRotationComponent != null
                  ? PositionUtil.toDirectionPacket(headRotationComponent.getRotation())
                  : PositionUtil.toDirectionPacket(transformComponent.getRotation())
            ),
            teleport.isResetVelocity()
         );
         playerRefComponent.getPacketHandler().write(teleportPacket);
         commandBuffer.removeComponent(ref, this.teleportComponentType);
      }
   }
}
