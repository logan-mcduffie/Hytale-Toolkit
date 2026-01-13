package com.hypixel.hytale.builtin.mounts;

import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.protocol.packets.interaction.MountNPC;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.modules.entity.tracker.NetworkId;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import com.hypixel.hytale.server.npc.systems.RoleChangeSystem;
import javax.annotation.Nonnull;

public class NPCMountSystems {
   public static class DismountOnMountDeath extends DeathSystems.OnDeathSystem {
      @Override
      public Query<EntityStore> getQuery() {
         return NPCMountComponent.getComponentType();
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         NPCMountComponent mountComponent = store.getComponent(ref, NPCMountComponent.getComponentType());

         assert mountComponent != null;

         PlayerRef playerRef = mountComponent.getOwnerPlayerRef();
         if (playerRef != null) {
            MountPlugin.resetOriginalPlayerMovementSettings(playerRef, store);
         }
      }
   }

   public static class DismountOnPlayerDeath extends DeathSystems.OnDeathSystem {
      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return Player.getComponentType();
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         Player playerComponent = store.getComponent(ref, Player.getComponentType());

         assert playerComponent != null;

         MountPlugin.checkDismountNpc(commandBuffer, playerComponent);
      }
   }

   public static class OnAdd extends RefSystem<EntityStore> {
      @Nonnull
      private final ComponentType<EntityStore, NPCMountComponent> mountComponentType;

      public OnAdd(@Nonnull ComponentType<EntityStore, NPCMountComponent> mountRoleChangeComponentType) {
         this.mountComponentType = mountRoleChangeComponentType;
      }

      @Override
      public Query<EntityStore> getQuery() {
         return this.mountComponentType;
      }

      @Override
      public void onEntityAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         NPCMountComponent mountComponent = store.getComponent(ref, this.mountComponentType);

         assert mountComponent != null;

         PlayerRef playerRef = mountComponent.getOwnerPlayerRef();
         if (playerRef == null) {
            resetOriginalRoleMount(ref, store, commandBuffer, mountComponent);
         } else {
            NPCEntity npcComponent = store.getComponent(ref, NPCEntity.getComponentType());

            assert npcComponent != null;

            NetworkId networkIdComponent = store.getComponent(ref, NetworkId.getComponentType());

            assert networkIdComponent != null;

            int networkId = networkIdComponent.getId();
            MountNPC packet = new MountNPC(mountComponent.getAnchorX(), mountComponent.getAnchorY(), mountComponent.getAnchorZ(), networkId);
            Player playerComponent = playerRef.getComponent(Player.getComponentType());

            assert playerComponent != null;

            playerComponent.setMountEntityId(networkId);
            playerRef.getPacketHandler().write(packet);
         }
      }

      private static void resetOriginalRoleMount(
         @Nonnull Ref<EntityStore> ref,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer,
         @Nonnull NPCMountComponent mountComponent
      ) {
         NPCEntity npcComponent = store.getComponent(ref, NPCEntity.getComponentType());

         assert npcComponent != null;

         RoleChangeSystem.requestRoleChange(ref, npcComponent.getRole(), mountComponent.getOriginalRoleIndex(), false, "Idle", null, store);
         commandBuffer.removeComponent(ref, NPCMountComponent.getComponentType());
      }

      @Override
      public void onEntityRemove(
         @Nonnull Ref<EntityStore> ref, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
      }
   }
}
