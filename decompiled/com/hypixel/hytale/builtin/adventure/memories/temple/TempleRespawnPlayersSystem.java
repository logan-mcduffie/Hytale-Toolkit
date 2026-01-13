package com.hypixel.hytale.builtin.adventure.memories.temple;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.tick.DelayedEntitySystem;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.math.vector.Vector3d;
import com.hypixel.hytale.protocol.SoundCategory;
import com.hypixel.hytale.server.core.asset.type.gameplay.GameplayConfig;
import com.hypixel.hytale.server.core.modules.entity.component.TransformComponent;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.SoundUtil;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class TempleRespawnPlayersSystem extends DelayedEntitySystem<EntityStore> {
   public static final Query<EntityStore> QUERY = Query.and(PlayerRef.getComponentType(), TransformComponent.getComponentType());

   public TempleRespawnPlayersSystem() {
      super(1.0F);
   }

   @Override
   public void tick(
      float dt,
      int index,
      @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
      @NonNullDecl Store<EntityStore> store,
      @NonNullDecl CommandBuffer<EntityStore> commandBuffer
   ) {
      World world = store.getExternalData().getWorld();
      GameplayConfig gameplayConfig = world.getGameplayConfig();
      ForgottenTempleConfig config = gameplayConfig.getPluginConfig().get(ForgottenTempleConfig.class);
      if (config != null) {
         Vector3d position = archetypeChunk.getComponent(index, TransformComponent.getComponentType()).getPosition();
         if (!(position.getY() > config.getMinYRespawn())) {
            Ref<EntityStore> ref = archetypeChunk.getReferenceTo(index);
            ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
            Transform spawnPoint = spawnProvider.getSpawnPoint(ref, commandBuffer);
            commandBuffer.addComponent(ref, Teleport.getComponentType(), new Teleport(null, spawnPoint));
            PlayerRef playerRef = archetypeChunk.getComponent(index, PlayerRef.getComponentType());
            SoundUtil.playSoundEvent2dToPlayer(playerRef, config.getRespawnSoundIndex(), SoundCategory.SFX);
         }
      }
   }

   @NullableDecl
   @Override
   public Query<EntityStore> getQuery() {
      return QUERY;
   }
}
