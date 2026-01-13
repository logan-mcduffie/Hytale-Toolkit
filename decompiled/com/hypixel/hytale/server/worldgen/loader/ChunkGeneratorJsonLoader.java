package com.hypixel.hytale.server.worldgen.loader;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.hypixel.hytale.common.map.IWeightedMap;
import com.hypixel.hytale.common.map.WeightedMap;
import com.hypixel.hytale.common.util.ArrayUtil;
import com.hypixel.hytale.math.util.FastRandom;
import com.hypixel.hytale.math.vector.Vector2i;
import com.hypixel.hytale.procedurallib.json.Loader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.chunk.ChunkGenerator;
import com.hypixel.hytale.server.worldgen.chunk.MaskProvider;
import com.hypixel.hytale.server.worldgen.loader.climate.ClimateMaskJsonLoader;
import com.hypixel.hytale.server.worldgen.loader.context.FileContextLoader;
import com.hypixel.hytale.server.worldgen.loader.context.FileLoadingContext;
import com.hypixel.hytale.server.worldgen.loader.zone.ZonePatternProviderJsonLoader;
import com.hypixel.hytale.server.worldgen.prefab.PrefabStoreRoot;
import com.hypixel.hytale.server.worldgen.zone.Zone;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class ChunkGeneratorJsonLoader extends Loader<SeedStringResource, ChunkGenerator> {
   public ChunkGeneratorJsonLoader(SeedString<SeedStringResource> seed, Path dataFolder) {
      super(seed, dataFolder);
   }

   @Nonnull
   public ChunkGenerator load() {
      Path worldFile = this.dataFolder.resolve("World.json").toAbsolutePath();
      if (!Files.exists(worldFile)) {
         throw new IllegalArgumentException(String.valueOf(worldFile));
      } else if (!Files.isReadable(worldFile)) {
         throw new IllegalArgumentException(String.valueOf(worldFile));
      } else {
         JsonObject worldJson = this.loadWorldJson(worldFile);
         Vector2i worldSize = this.loadWorldSize(worldJson);
         Vector2i worldOffset = this.loadWorldOffset(worldJson);
         MaskProvider maskProvider = this.loadMaskProvider(worldJson, worldSize, worldOffset);
         PrefabStoreRoot prefabStore = this.loadPrefabStore(worldJson);
         Path overrideDataFolder = this.loadOverrideDataFolderPath(worldJson, this.dataFolder);
         this.seed.get().setPrefabStore(prefabStore);
         this.seed.get().setDataFolder(overrideDataFolder);
         ZonePatternProviderJsonLoader loader = this.loadZonePatternGenerator(maskProvider);
         FileLoadingContext loadingContext = new FileContextLoader(overrideDataFolder, loader.loadZoneRequirement()).load();
         Zone[] zones = new ZonesJsonLoader(this.seed, overrideDataFolder, loadingContext).load();
         loader.setZones(zones);
         return new ChunkGenerator(loader.load(), overrideDataFolder);
      }
   }

   @Nonnull
   private Path loadOverrideDataFolderPath(@Nonnull JsonObject worldJson, @Nonnull Path dataFolder) {
      if (worldJson.has("OverrideDataFolder")) {
         Path overrideFolder = dataFolder.resolve(worldJson.get("OverrideDataFolder").getAsString()).normalize();
         Path parent = dataFolder.getParent();
         if (overrideFolder.startsWith(parent) && Files.exists(overrideFolder)) {
            return overrideFolder;
         } else {
            throw new Error(String.format("Override folder '%s' must exist within: '%s'", overrideFolder.getFileName(), parent));
         }
      } else {
         return dataFolder;
      }
   }

   @Nonnull
   protected JsonObject loadWorldJson(@Nonnull Path file) {
      try {
         JsonObject worldJson;
         try (JsonReader reader = new JsonReader(Files.newBufferedReader(file))) {
            worldJson = JsonParser.parseReader(reader).getAsJsonObject();
         }

         return worldJson;
      } catch (Throwable var8) {
         throw new Error(String.format("Could not read JSON configuration for world. File: %s", file), var8);
      }
   }

   @Nonnull
   protected Vector2i loadWorldSize(@Nonnull JsonObject worldJson) {
      int width = 0;
      int height = 0;
      if (worldJson.has("Width")) {
         width = worldJson.get("Width").getAsInt();
      }

      if (worldJson.has("Height")) {
         height = worldJson.get("Height").getAsInt();
      }

      return new Vector2i(width, height);
   }

   @Nonnull
   protected Vector2i loadWorldOffset(@Nonnull JsonObject worldJson) {
      int offsetX = 0;
      int offsetY = 0;
      if (worldJson.has("OffsetX")) {
         offsetX = worldJson.get("OffsetX").getAsInt();
      }

      if (worldJson.has("OffsetY")) {
         offsetY = worldJson.get("OffsetY").getAsInt();
      }

      return new Vector2i(offsetX, offsetY);
   }

   @Nonnull
   protected MaskProvider loadMaskProvider(@Nonnull JsonObject worldJson, Vector2i worldSize, Vector2i worldOffset) {
      WeightedMap.Builder<String> builder = WeightedMap.builder(ArrayUtil.EMPTY_STRING_ARRAY);
      JsonElement masks = worldJson.get("Masks");
      if (masks == null) {
         builder.put("Mask.png", 1.0);
      } else if (masks.isJsonPrimitive()) {
         builder.put(masks.getAsString(), 1.0);
      } else if (masks.isJsonArray()) {
         JsonArray arr = masks.getAsJsonArray();
         if (arr.isEmpty()) {
            builder.put("Mask.png", 1.0);
         } else {
            for (int i = 0; i < arr.size(); i++) {
               builder.put(arr.get(i).getAsString(), 1.0);
            }
         }
      } else if (masks.isJsonObject()) {
         JsonObject obj = masks.getAsJsonObject();
         if (obj.size() == 0) {
            builder.put("Mask.png", 1.0);
         } else {
            for (String key : obj.keySet()) {
               builder.put(key, obj.get(key).getAsDouble());
            }
         }
      }

      IWeightedMap<String> weightedMap = builder.build();
      Path maskFile = this.dataFolder.resolve(weightedMap.get(new FastRandom(this.seed.hashCode())));
      return (MaskProvider)(maskFile.getFileName().endsWith("Mask.json")
         ? new ClimateMaskJsonLoader<>(this.seed, this.dataFolder, maskFile).load()
         : new MaskProviderJsonLoader(this.seed, this.dataFolder, worldJson.get("Randomizer"), maskFile, worldSize, worldOffset).load());
   }

   @Nonnull
   protected PrefabStoreRoot loadPrefabStore(@Nonnull JsonObject worldJson) {
      if (worldJson.has("PrefabStore")) {
         JsonElement storeJson = worldJson.get("PrefabStore");
         if (storeJson.isJsonPrimitive() && storeJson.getAsJsonPrimitive().isString()) {
            String store = storeJson.getAsString();

            try {
               return PrefabStoreRoot.valueOf(store);
            } catch (IllegalArgumentException var5) {
               throw new Error("Invalid PrefabStore name: " + store, var5);
            }
         } else {
            throw new Error("Expected 'PrefabStore' to be a string");
         }
      } else {
         return PrefabStoreRoot.DEFAULT;
      }
   }

   @Nonnull
   protected ZonePatternProviderJsonLoader loadZonePatternGenerator(MaskProvider maskProvider) {
      Path zoneFile = this.dataFolder.resolve("Zones.json");

      try {
         ZonePatternProviderJsonLoader var5;
         try (JsonReader reader = new JsonReader(Files.newBufferedReader(zoneFile))) {
            JsonObject zoneJson = JsonParser.parseReader(reader).getAsJsonObject();
            var5 = new ZonePatternProviderJsonLoader(this.seed, this.dataFolder, zoneJson, maskProvider);
         }

         return var5;
      } catch (Throwable var8) {
         throw new Error(String.format("Failed to read zone configuration file! File: %s", zoneFile.toString()), var8);
      }
   }

   public interface Constants {
      String KEY_WIDTH = "Width";
      String KEY_HEIGHT = "Height";
      String KEY_OFFSET_X = "OffsetX";
      String KEY_OFFSET_Y = "OffsetY";
      String KEY_RANDOMIZER = "Randomizer";
      String KEY_MASKS = "Masks";
      String KEY_PREFAB_STORE = "PrefabStore";
      String OVERRIDE_DATA_FOLDER = "OverrideDataFolder";
      String FILE_WORLD_JSON = "World.json";
      String FILE_ZONES_JSON = "Zones.json";
      String FILE_MASK_JSON = "Mask.json";
      String FILE_MASK_PNG = "Mask.png";
      String ERROR_WORLD_FILE_EXIST = "World configuration file does NOT exist! File not found: %s";
      String ERROR_WORLD_FILE_READ = "World configuration file is NOT readable! File: %s";
      String ERROR_WORLD_JSON_CORRUPT = "Could not read JSON configuration for world. File: %s";
      String ERROR_ZONE_FILE = "Failed to read zone configuration file! File: %s";
   }
}
