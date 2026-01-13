package com.hypixel.hytale.server.npc.systems;

import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.ResourceType;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.math.util.ChunkUtil;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.GameMode;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.modules.entity.AllLegacyLivingEntityTypesQuery;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathComponent;
import com.hypixel.hytale.server.core.modules.entity.damage.DeathSystems;
import com.hypixel.hytale.server.core.modules.entity.player.PlayerSettings;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.blackboard.Blackboard;
import com.hypixel.hytale.server.npc.blackboard.view.event.entity.EntityEventType;
import com.hypixel.hytale.server.npc.blackboard.view.event.entity.EntityEventView;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import javax.annotation.Nonnull;

public class NPCDeathSystems {
   public static class EntityViewSystem extends DeathSystems.OnDeathSystem {
      @Nonnull
      private final ComponentType<EntityStore, Player> playerComponentType = Player.getComponentType();
      @Nonnull
      private final ComponentType<EntityStore, TransformComponent> transformComponentType = TransformComponent.getComponentType();
      @Nonnull
      private final ResourceType<EntityStore, Blackboard> blackboardResourceType = Blackboard.getResourceType();
      @Nonnull
      private final Query<EntityStore> query = Query.and(Query.or(NPCEntity.getComponentType(), this.playerComponentType), this.transformComponentType);

      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return this.query;
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         Damage deathInfo = component.getDeathInfo();
         if (deathInfo != null && deathInfo.getSource() instanceof Damage.EntitySource entitySource) {
            Ref<EntityStore> attackerRef = entitySource.getRef();
            if (attackerRef.isValid()) {
               Player attackerPlayerComponent = commandBuffer.getComponent(attackerRef, Player.getComponentType());
               if (attackerPlayerComponent != null && attackerPlayerComponent.getGameMode() == GameMode.Creative) {
                  PlayerSettings playerSettingsComponent = commandBuffer.getComponent(attackerRef, PlayerSettings.getComponentType());
                  if (playerSettingsComponent == null || !playerSettingsComponent.creativeSettings().allowNPCDetection()) {
                     return;
                  }
               }

               TransformComponent transformComponent = store.getComponent(ref, this.transformComponentType);

               assert transformComponent != null;

               Vector3d position = transformComponent.getPosition();
               Blackboard blackboardResource = store.getResource(this.blackboardResourceType);
               EntityEventView entityEventView = blackboardResource.getView(
                  EntityEventView.class, ChunkUtil.chunkCoordinate(position.x), ChunkUtil.chunkCoordinate(position.z)
               );
               entityEventView.processAttackedEvent(ref, attackerRef, commandBuffer, EntityEventType.DEATH);
            }
         }
      }
   }

   public static class NPCKillsEntitySystem extends DeathSystems.OnDeathSystem {
      @Nonnull
      @Override
      public Query<EntityStore> getQuery() {
         return AllLegacyLivingEntityTypesQuery.INSTANCE;
      }

      public void onComponentAdded(
         @Nonnull Ref<EntityStore> ref, @Nonnull DeathComponent component, @Nonnull Store<EntityStore> store, @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         Damage deathInfo = component.getDeathInfo();
         if (deathInfo != null && deathInfo.getSource() instanceof Damage.EntitySource) {
            Ref<EntityStore> attackerReference = ((Damage.EntitySource)deathInfo.getSource()).getRef();
            NPCEntity attackerNpcComponent = store.getComponent(attackerReference, NPCEntity.getComponentType());
            if (attackerNpcComponent != null) {
               TransformComponent entityTransformComponent = store.getComponent(ref, TransformComponent.getComponentType());

               assert entityTransformComponent != null;

               attackerNpcComponent.getDamageData().onKill(ref, entityTransformComponent.getPosition().clone());
            }
         }
      }
   }
}
