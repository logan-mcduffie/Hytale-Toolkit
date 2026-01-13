package com.hypixel.hytale.server.core.universe.world.storage.provider;

import com.hypixel.hytale.codec.lookup.BuilderCodecMapCodec;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkLoader;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
import java.io.IOException;
import javax.annotation.Nonnull;

public interface IChunkStorageProvider {
   @Nonnull
   BuilderCodecMapCodec<IChunkStorageProvider> CODEC = new BuilderCodecMapCodec<>("Type", true);

   @Nonnull
   IChunkLoader getLoader(@Nonnull Store<ChunkStore> var1) throws IOException;

   @Nonnull
   IChunkSaver getSaver(@Nonnull Store<ChunkStore> var1) throws IOException;
}
