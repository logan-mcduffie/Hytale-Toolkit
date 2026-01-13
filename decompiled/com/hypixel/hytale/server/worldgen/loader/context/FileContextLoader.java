package com.hypixel.hytale.server.worldgen.loader.context;

import com.google.gson.JsonParser;
import com.hypixel.hytale.logger.HytaleLogger;
import com.hypixel.hytale.server.worldgen.prefab.PrefabCategory;
import com.hypixel.hytale.server.worldgen.util.LogUtil;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Comparator;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.logging.Level;
import java.util.stream.Stream;
import javax.annotation.Nonnull;

public class FileContextLoader {
   private static final Comparator<Path> ZONES_ORDER = Comparator.comparing(Path::getFileName);
   private static final Comparator<Path> BIOME_ORDER = Comparator.comparing(BiomeFileContext::getBiomeType).thenComparing(Path::getFileName);
   private static final BiPredicate<Path, BasicFileAttributes> ZONE_FILTER = (path, attributes) -> Files.isDirectory(path);
   private static final BiPredicate<Path, BasicFileAttributes> BIOME_FILTER = (path, attributes) -> isValidBiomeFile(path);
   private final Path dataFolder;
   private final Set<String> zoneRequirement;

   public FileContextLoader(Path dataFolder, Set<String> zoneRequirement) {
      this.dataFolder = dataFolder;
      this.zoneRequirement = zoneRequirement;
   }

   @Nonnull
   public FileLoadingContext load() {
      FileLoadingContext context = new FileLoadingContext(this.dataFolder);
      Path zonesFolder = this.dataFolder.resolve("Zones");

      try (Stream<Path> stream = Files.find(zonesFolder, 1, ZONE_FILTER)) {
         stream.sorted(ZONES_ORDER).forEach(path -> {
            String zoneName = path.getFileName().toString();
            if (zoneName.startsWith("!")) {
               LogUtil.getLogger().at(Level.INFO).log("Zone \"%s\" is disabled. Remove \"!\" from folder name to enable it.", zoneName);
            } else if (this.zoneRequirement.contains(zoneName)) {
               ZoneFileContext zone = loadZoneContext(zoneName, path, context);
               context.getZones().register(zoneName, zone);
            }
         });
      } catch (IOException var9) {
         HytaleLogger.getLogger().at(Level.SEVERE).withCause(var9).log("Failed to load zones");
      }

      try {
         validateZones(context, this.zoneRequirement);
      } catch (Error var6) {
         throw new Error("Failed to validate zones!", var6);
      }

      loadPrefabCategories(this.dataFolder, context);
      return context;
   }

   protected static void loadPrefabCategories(@Nonnull Path folder, @Nonnull FileLoadingContext context) {
      Path path = folder.resolve("PrefabCategories.json");
      if (Files.exists(path)) {
         try {
            try (BufferedReader reader = Files.newBufferedReader(path)) {
               PrefabCategory.parse(JsonParser.parseReader(reader), context.getPrefabCategories()::register);
            }
         } catch (IOException var8) {
            throw new Error("Failed to open Categories.json", var8);
         }
      }
   }

   @Nonnull
   protected static ZoneFileContext loadZoneContext(String name, @Nonnull Path folder, @Nonnull FileLoadingContext context) {
      try {
         ZoneFileContext var5;
         try (Stream<Path> stream = Files.find(folder, 1, BIOME_FILTER)) {
            ZoneFileContext zone = context.createZone(name, folder);
            stream.sorted(BIOME_ORDER).forEach(path -> {
               BiomeFileContext.Type type = BiomeFileContext.getBiomeType(path);
               String biomeName = parseName(path, type);
               BiomeFileContext biome = zone.createBiome(biomeName, path, type);
               zone.getBiomes(type).register(biomeName, biome);
            });
            var5 = zone;
         }

         return var5;
      } catch (IOException var8) {
         throw new Error(String.format("Failed to list files in: %s", folder), var8);
      }
   }

   protected static int compareBiomePaths(@Nonnull Path a, @Nonnull Path b) {
      BiomeFileContext.Type typeA = BiomeFileContext.getBiomeType(a);
      BiomeFileContext.Type typeB = BiomeFileContext.getBiomeType(b);
      int result = typeA.compareTo(typeB);
      return result != 0 ? result : a.getFileName().compareTo(b.getFileName());
   }

   protected static boolean isValidBiomeFile(@Nonnull Path path) {
      if (Files.isDirectory(path)) {
         return false;
      } else {
         String filename = path.getFileName().toString();

         for (BiomeFileContext.Type type : BiomeFileContext.Type.values()) {
            if (filename.endsWith(type.getSuffix()) && filename.startsWith(type.getPrefix())) {
               return true;
            }
         }

         return false;
      }
   }

   protected static void validateZones(@Nonnull FileLoadingContext context, @Nonnull Set<String> zoneRequirement) throws Error {
      for (String key : zoneRequirement) {
         context.getZones().get(key);
      }
   }

   @Nonnull
   private static String parseName(@Nonnull Path path, @Nonnull BiomeFileContext.Type type) {
      String filename = path.getFileName().toString();
      int start = type.getPrefix().length();
      int end = filename.length() - type.getSuffix().length();
      return filename.substring(start, end);
   }

   public interface Constants {
      int ZONE_SEARCH_DEPTH = 1;
      int BIOME_SEARCH_DEPTH = 1;
      String IDENTIFIER_DISABLE_ZONE = "!";
      String INFO_ZONE_IS_DISABLED = "Zone \"%s\" is disabled. Remove \"!\" from folder name to enable it.";
      String ERROR_LIST_FILES = "Failed to list files in: %s";
      String ERROR_ZONE_VALIDATION = "Failed to validate zones!";
   }
}
