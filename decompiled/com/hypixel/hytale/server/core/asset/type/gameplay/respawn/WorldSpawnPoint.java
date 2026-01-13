package com.hypixel.hytale.server.core.asset.type.gameplay.respawn;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.modules.entity.teleport.Teleport;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class WorldSpawnPoint implements RespawnController {
   public static final WorldSpawnPoint INSTANCE = new WorldSpawnPoint();
   public static final BuilderCodec<WorldSpawnPoint> CODEC = BuilderCodec.builder(WorldSpawnPoint.class, () -> INSTANCE).build();

   @Override
   public void respawnPlayer(@NonNullDecl World world, @NonNullDecl Ref<EntityStore> playerReference, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
      ISpawnProvider spawnProvider = world.getWorldConfig().getSpawnProvider();
      Transform spawnPoint = spawnProvider.getSpawnPoint(playerReference, commandBuffer);
      commandBuffer.addComponent(playerReference, Teleport.getComponentType(), new Teleport(null, spawnPoint));
   }
}
