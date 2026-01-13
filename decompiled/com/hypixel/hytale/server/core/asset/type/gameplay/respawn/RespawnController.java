package com.hypixel.hytale.server.core.asset.type.gameplay.respawn;

import com.hypixel.hytale.codec.lookup.CodecMapCodec;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.server.core.universe.world.World;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import javax.annotation.Nonnull;

public interface RespawnController {
   @Nonnull
   CodecMapCodec<RespawnController> CODEC = new CodecMapCodec<>("Type");

   void respawnPlayer(@Nonnull World var1, @Nonnull Ref<EntityStore> var2, @Nonnull CommandBuffer<EntityStore> var3);
}
