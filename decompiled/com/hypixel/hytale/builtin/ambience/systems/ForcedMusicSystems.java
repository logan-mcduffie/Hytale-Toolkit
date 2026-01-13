package com.hypixel.hytale.builtin.ambience.systems;

import com.hypixel.hytale.builtin.ambience.components.AmbienceTracker;
import com.hypixel.hytale.builtin.ambience.resources.AmbienceResource;
import com.hypixel.hytale.component.AddReason;
import com.hypixel.hytale.component.Archetype;
import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Holder;
import com.hypixel.hytale.component.RemoveReason;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.HolderSystem;
import com.hypixel.hytale.component.system.tick.EntityTickingSystem;
import com.hypixel.hytale.protocol.packets.world.UpdateEnvironmentMusic;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ForcedMusicSystems {
   private static final Query<EntityStore> TICK_QUERY = Archetype.of(
      Player.getComponentType(), PlayerRef.getComponentType(), AmbienceTracker.getComponentType()
   );

   public static class PlayerAdded extends HolderSystem<EntityStore> {
      @Override
      public void onEntityAdd(@Nonnull Holder<EntityStore> holder, @Nonnull AddReason reason, @Nonnull Store<EntityStore> store) {
         holder.ensureComponent(AmbienceTracker.getComponentType());
      }

      @Override
      public void onEntityRemoved(@Nonnull Holder<EntityStore> holder, @Nonnull RemoveReason reason, @Nonnull Store<EntityStore> store) {
         AmbienceTracker tracker = holder.getComponent(AmbienceTracker.getComponentType());
         PlayerRef playerRef = holder.getComponent(PlayerRef.getComponentType());
         UpdateEnvironmentMusic pooledPacket = tracker.getMusicPacket();
         pooledPacket.environmentIndex = 0;
         playerRef.getPacketHandler().write(pooledPacket);
      }

      @Nullable
      @Override
      public Query<EntityStore> getQuery() {
         return PlayerRef.getComponentType();
      }
   }

   public static class Tick extends EntityTickingSystem<EntityStore> {
      @Override
      public void tick(
         float dt,
         int index,
         @Nonnull ArchetypeChunk<EntityStore> archetypeChunk,
         @Nonnull Store<EntityStore> store,
         @Nonnull CommandBuffer<EntityStore> commandBuffer
      ) {
         AmbienceResource ambienceResource = store.getResource(AmbienceResource.getResourceType());
         AmbienceTracker tracker = archetypeChunk.getComponent(index, AmbienceTracker.getComponentType());
         PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
         int have = tracker.getForcedMusicIndex();
         int desired = ambienceResource.getForcedMusicIndex();
         if (have != desired) {
            tracker.setForcedMusicIndex(desired);
            UpdateEnvironmentMusic pooledPacket = tracker.getMusicPacket();
            pooledPacket.environmentIndex = desired;
            playerRef.getPacketHandler().write(pooledPacket);
         }
      }

      @Nullable
      @Override
      public Query<EntityStore> getQuery() {
         return ForcedMusicSystems.TICK_QUERY;
      }
   }
}
