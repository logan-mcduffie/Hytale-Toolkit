package com.hypixel.hytale.server.core.prefab;

import com.hypixel.hytale.assetstore.AssetPack;
import com.hypixel.hytale.common.plugin.PluginManifest;
import com.hypixel.hytale.server.core.asset.AssetModule;
import com.hypixel.hytale.server.core.prefab.config.SelectionPrefabSerializer;
import com.hypixel.hytale.server.core.prefab.selection.standard.BlockSelection;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.AssetUtil;
import com.hypixel.hytale.server.core.util.BsonUtil;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class PrefabStore {
   public static final Predicate<Path> PREFAB_FILTER = path -> path.toString().endsWith(".prefab.json");
   public static final Path PREFABS_PATH = Path.of("prefabs");
   private static final String DEFAULT_WORLDGEN_NAME = "Default";
   private static final PrefabStore INSTANCE = new PrefabStore();
   private final Map<Path, BlockSelection> PREFAB_CACHE = new ConcurrentHashMap<>();

   private PrefabStore() {
   }

   @Nonnull
   public BlockSelection getServerPrefab(@Nonnull String key) {
      return this.getPrefab(this.getServerPrefabsPath().resolve(key));
   }

   @Nonnull
   public BlockSelection getPrefab(@Nonnull Path path) {
      return this.PREFAB_CACHE.computeIfAbsent(path.toAbsolutePath().normalize(), p -> {
         if (Files.exists(p)) {
            return SelectionPrefabSerializer.deserialize(BsonUtil.readDocument(p).join());
         } else {
            throw new PrefabLoadException(PrefabLoadException.Type.NOT_FOUND);
         }
      });
   }

   public Path getServerPrefabsPath() {
      return PREFABS_PATH;
   }

   @Nonnull
   public Map<Path, BlockSelection> getServerPrefabDir(@Nonnull String key) {
      return this.getPrefabDir(this.getServerPrefabsPath().resolve(key));
   }

   @Nonnull
   public Map<Path, BlockSelection> getPrefabDir(@Nonnull Path dir) {
      try {
         Map var3;
         try (Stream<Path> stream = Files.list(dir)) {
            var3 = stream.filter(PREFAB_FILTER).sorted().collect(Collectors.toMap(Function.identity(), this::getPrefab, (u, v) -> {
               throw new IllegalStateException(String.format("Duplicate key %s", u));
            }, LinkedHashMap::new));
         }

         return var3;
      } catch (IOException var7) {
         throw new RuntimeException("Failed to list directory " + dir, var7);
      }
   }

   public void saveServerPrefab(@Nonnull String key, @Nonnull BlockSelection prefab) {
      this.saveWorldGenPrefab(key, prefab, false);
   }

   public void saveWorldGenPrefab(@Nonnull String key, @Nonnull BlockSelection prefab, boolean overwrite) {
      this.savePrefab(this.getWorldGenPrefabsPath().resolve(key), prefab, overwrite);
   }

   public void savePrefab(@Nonnull Path path, @Nonnull BlockSelection prefab, boolean overwrite) {
      File file = path.toFile();
      if (file.exists() && !overwrite) {
         throw new PrefabSaveException(PrefabSaveException.Type.ALREADY_EXISTS);
      } else {
         file.getParentFile().mkdirs();

         try {
            BsonUtil.writeDocument(path, SelectionPrefabSerializer.serialize(prefab)).join();
         } catch (Throwable var6) {
            throw new PrefabSaveException(PrefabSaveException.Type.ERROR, var6);
         }

         this.PREFAB_CACHE.remove(path);
      }
   }

   @Nonnull
   public Path getWorldGenPrefabsPath() {
      return this.getWorldGenPrefabsPath("Default");
   }

   public Path getAssetRootPath() {
      return AssetUtil.getHytaleAssetsPath();
   }

   @Nonnull
   public Path getWorldGenPrefabsPath(@Nullable String name) {
      name = name == null ? "Default" : name;
      return Universe.getWorldGenPath().resolve(name).resolve("Prefabs");
   }

   public void saveServerPrefab(@Nonnull String key, @Nonnull BlockSelection prefab, boolean overwrite) {
      this.savePrefab(this.getServerPrefabsPath().resolve(key), prefab, overwrite);
   }

   @Nonnull
   public Path getAssetPrefabsPath() {
      return AssetUtil.getHytaleAssetsPath().resolve("Server").resolve("Prefabs");
   }

   @Nonnull
   public Path getAssetPrefabsPathForPack(@Nonnull AssetPack pack) {
      return pack.getRoot().resolve("Server").resolve("Prefabs");
   }

   @Nonnull
   public List<PrefabStore.AssetPackPrefabPath> getAllAssetPrefabPaths() {
      List<PrefabStore.AssetPackPrefabPath> result = new ObjectArrayList<>();

      for (AssetPack pack : AssetModule.get().getAssetPacks()) {
         Path prefabsPath = this.getAssetPrefabsPathForPack(pack);
         if (Files.isDirectory(prefabsPath)) {
            result.add(new PrefabStore.AssetPackPrefabPath(pack, prefabsPath));
         }
      }

      return result;
   }

   @Nullable
   public BlockSelection getAssetPrefabFromAnyPack(@Nonnull String key) {
      for (AssetPack pack : AssetModule.get().getAssetPacks()) {
         Path prefabsPath = this.getAssetPrefabsPathForPack(pack);
         Path prefabPath = prefabsPath.resolve(key);
         if (Files.exists(prefabPath)) {
            return this.getPrefab(prefabPath);
         }
      }

      return null;
   }

   @Nullable
   public Path findAssetPrefabPath(@Nonnull String key) {
      for (AssetPack pack : AssetModule.get().getAssetPacks()) {
         Path prefabsPath = this.getAssetPrefabsPathForPack(pack);
         Path prefabPath = prefabsPath.resolve(key);
         if (Files.exists(prefabPath)) {
            return prefabPath;
         }
      }

      return null;
   }

   @Nullable
   public AssetPack findAssetPackForPrefabPath(@Nonnull Path prefabPath) {
      Path normalizedPath = prefabPath.toAbsolutePath().normalize();

      for (AssetPack pack : AssetModule.get().getAssetPacks()) {
         Path prefabsPath = this.getAssetPrefabsPathForPack(pack).toAbsolutePath().normalize();
         if (normalizedPath.startsWith(prefabsPath)) {
            return pack;
         }
      }

      return null;
   }

   @Nonnull
   public BlockSelection getAssetPrefab(@Nonnull String key) {
      return this.getPrefab(this.getAssetPrefabsPath().resolve(key));
   }

   @Nonnull
   public Map<Path, BlockSelection> getAssetPrefabDir(@Nonnull String key) {
      return this.getPrefabDir(this.getAssetPrefabsPath().resolve(key));
   }

   public void saveAssetPrefab(@Nonnull String key, @Nonnull BlockSelection prefab) {
      this.saveWorldGenPrefab(key, prefab, false);
   }

   public void saveAssetPrefab(@Nonnull String key, @Nonnull BlockSelection prefab, boolean overwrite) {
      this.savePrefab(this.getAssetPrefabsPath().resolve(key), prefab, overwrite);
   }

   @Nonnull
   public BlockSelection getWorldGenPrefab(@Nonnull String key) {
      return this.getWorldGenPrefab(this.getWorldGenPrefabsPath(), key);
   }

   @Nonnull
   public BlockSelection getWorldGenPrefab(@Nonnull Path prefabsPath, @Nonnull String key) {
      return this.getPrefab(prefabsPath.resolve(key));
   }

   @Nonnull
   public Map<Path, BlockSelection> getWorldGenPrefabDir(@Nonnull String key) {
      return this.getPrefabDir(this.getWorldGenPrefabsPath().resolve(key));
   }

   public void saveWorldGenPrefab(@Nonnull String key, @Nonnull BlockSelection prefab) {
      this.saveWorldGenPrefab(key, prefab, false);
   }

   public static PrefabStore get() {
      return INSTANCE;
   }

   public record AssetPackPrefabPath(@Nullable AssetPack pack, @Nonnull Path prefabsPath) {
      public boolean isBasePack() {
         return this.pack != null && this.pack.equals(AssetModule.get().getBaseAssetPack());
      }

      public boolean isFromAssetPack() {
         return this.pack != null;
      }

      @Nonnull
      public String getPackName() {
         return this.pack != null ? this.pack.getName() : "Server";
      }

      @Nonnull
      public String getDisplayName() {
         if (this.pack == null) {
            return "Server";
         } else if (this.isBasePack()) {
            return "Assets";
         } else {
            PluginManifest manifest = this.pack.getManifest();
            return manifest != null ? manifest.getName() : this.pack.getName();
         }
      }
   }
}
