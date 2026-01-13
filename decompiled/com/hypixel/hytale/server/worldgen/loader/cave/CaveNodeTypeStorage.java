package com.hypixel.hytale.server.worldgen.loader.cave;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.worldgen.SeedStringResource;
import com.hypixel.hytale.server.worldgen.cave.CaveNodeType;
import com.hypixel.hytale.server.worldgen.loader.context.ZoneFileContext;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import javax.annotation.Nonnull;

public class CaveNodeTypeStorage {
   protected final SeedString<SeedStringResource> seed;
   protected final Path dataFolder;
   protected final Path caveFolder;
   protected final ZoneFileContext zoneContext;
   @Nonnull
   protected final Map<String, CaveNodeType> caveNodeTypes;

   public CaveNodeTypeStorage(SeedString<SeedStringResource> seed, Path dataFolder, Path caveFolder, ZoneFileContext zoneContext) {
      this.seed = seed;
      this.dataFolder = dataFolder;
      this.caveFolder = caveFolder;
      this.zoneContext = zoneContext;
      this.caveNodeTypes = new HashMap<>();
   }

   public SeedString<SeedStringResource> getSeed() {
      return this.seed;
   }

   public void add(String name, CaveNodeType caveNodeType) {
      if (this.caveNodeTypes.containsKey(name)) {
         throw new Error(String.format("CaveNodeType (%s) has already been added to CaveNodeTypeStorage!", name));
      } else {
         this.caveNodeTypes.put(name, caveNodeType);
      }
   }

   @Nonnull
   public CaveNodeType getOrLoadCaveNodeType(@Nonnull String name) {
      CaveNodeType caveNodeType = this.getCaveNodeType(name);
      if (caveNodeType == null) {
         caveNodeType = this.loadCaveNodeType(name);
      }

      return caveNodeType;
   }

   public CaveNodeType getCaveNodeType(String name) {
      return this.caveNodeTypes.get(name);
   }

   @Nonnull
   public CaveNodeType loadCaveNodeType(@Nonnull String name) {
      Path file = this.caveFolder.resolve(String.format("%s.node.json", name.replace(".", File.separator)));

      try {
         CaveNodeType var5;
         try (JsonReader reader = new JsonReader(Files.newBufferedReader(file))) {
            JsonObject caveNodeJson = JsonParser.parseReader(reader).getAsJsonObject();
            var5 = new CaveNodeTypeJsonLoader(this.seed, this.dataFolder, caveNodeJson, name, this, this.zoneContext).load();
         }

         return var5;
      } catch (Throwable var8) {
         throw new Error(String.format("Error while loading CaveNodeType %s for world generator from %s", name, file.toString()), var8);
      }
   }

   public interface Constants {
      String ERROR_ALREADY_ADDED = "CaveNodeType (%s) has already been added to CaveNodeTypeStorage!";
      String ERROR_LOADING_CAVE_NODE_TYPE = "Error while loading CaveNodeType %s for world generator from %s";
      String FILE_SUFFIX = "%s.node.json";
   }
}
