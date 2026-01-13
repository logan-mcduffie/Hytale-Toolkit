package com.hypixel.hytale.server.core.universe.world.storage.provider;

import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.server.core.universe.world.storage.ChunkStore;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkLoader;
import com.hypixel.hytale.server.core.universe.world.storage.IChunkSaver;
import java.io.IOException;
import javax.annotation.Nonnull;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;

public class DefaultChunkStorageProvider implements IChunkStorageProvider {
   @Nonnull
   public static final DefaultChunkStorageProvider INSTANCE = new DefaultChunkStorageProvider();
   public static final String ID = "Hytale";
   @Nonnull
   public static final BuilderCodec<DefaultChunkStorageProvider> CODEC = BuilderCodec.builder(DefaultChunkStorageProvider.class, () -> INSTANCE)
      .documentation("Selects the default recommended storage as decided by the server.")
      .build();
   @Nonnull
   public static final IChunkStorageProvider DEFAULT = new IndexedStorageChunkStorageProvider();

   @NonNullDecl
   @Override
   public IChunkLoader getLoader(@NonNullDecl Store<ChunkStore> store) throws IOException {
      return DEFAULT.getLoader(store);
   }

   @Nonnull
   @Override
   public IChunkSaver getSaver(@NonNullDecl Store<ChunkStore> store) throws IOException {
      return DEFAULT.getSaver(store);
   }

   @Nonnull
   @Override
   public String toString() {
      return "DefaultChunkStorageProvider{DEFAULT=" + DEFAULT + "}";
   }
}
