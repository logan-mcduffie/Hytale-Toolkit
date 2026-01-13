package com.hypixel.hytale.server.worldgen.loader;

import com.hypixel.hytale.server.core.prefab.selection.buffer.PrefabLoader;
import com.hypixel.hytale.server.worldgen.prefab.PrefabStoreRoot;
import com.hypixel.hytale.server.worldgen.util.cache.TimeoutCache;
import com.hypixel.hytale.sneakythrow.SneakyThrow;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class WorldGenPrefabLoader {
   public static final String PREFAB_FOLDER = "Prefabs";
   @Nonnull
   private final PrefabStoreRoot store;
   @Nonnull
   private final PrefabLoader prefabLoader;
   @Nonnull
   private final TimeoutCache<String, WorldGenPrefabSupplier[]> cache;

   public WorldGenPrefabLoader(@Nonnull PrefabStoreRoot store, @Nonnull Path dataFolder) {
      Path storePath = PrefabStoreRoot.resolvePrefabStore(store, dataFolder);
      this.store = store;
      this.prefabLoader = new PrefabLoader(storePath);
      this.cache = new TimeoutCache<>(30L, TimeUnit.SECONDS, SneakyThrow.sneakyFunction(key -> {
         List<WorldGenPrefabSupplier> suppliers = new ArrayList<>();
         this.resolve(key, path -> suppliers.add(new WorldGenPrefabSupplier(this, key, path)));
         return suppliers.toArray(WorldGenPrefabSupplier[]::new);
      }), null);
   }

   @Nonnull
   public PrefabStoreRoot getStore() {
      return this.store;
   }

   public Path getRootFolder() {
      return this.prefabLoader.getRootFolder();
   }

   @Nullable
   public WorldGenPrefabSupplier[] get(String prefabName) {
      return this.cache.get(prefabName);
   }

   private void resolve(@Nonnull String prefabName, @Nonnull Consumer<Path> consumer) throws IOException {
      this.prefabLoader.resolvePrefabs(prefabName, consumer);
   }
}
