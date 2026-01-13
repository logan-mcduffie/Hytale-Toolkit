package com.hypixel.hytale.server.worldgen;

import com.hypixel.hytale.codec.Codec;
import com.hypixel.hytale.codec.KeyedCodec;
import com.hypixel.hytale.codec.builder.BuilderCodec;
import com.hypixel.hytale.common.util.PathUtil;
import com.hypixel.hytale.procedurallib.json.SeedString;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenLoadException;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.IWorldGenProvider;
import com.hypixel.hytale.server.worldgen.loader.ChunkGeneratorJsonLoader;
import com.hypixel.hytale.server.worldgen.prefab.PrefabStoreRoot;
import java.nio.file.Files;
import java.nio.file.Path;
import javax.annotation.Nonnull;

public class HytaleWorldGenProvider implements IWorldGenProvider {
   public static final String ID = "Hytale";
   public static final BuilderCodec<HytaleWorldGenProvider> CODEC = BuilderCodec.builder(HytaleWorldGenProvider.class, HytaleWorldGenProvider::new)
      .documentation("The standard generator for Hytale.")
      .<String>append(new KeyedCodec<>("Name", Codec.STRING), (config, s) -> config.name = s, config -> config.name)
      .documentation("The name of the generator to use. \"*Default*\" if not provided.")
      .add()
      .<String>append(new KeyedCodec<>("Path", Codec.STRING), (config, s) -> config.path = s, config -> config.path)
      .documentation("The path to the world generation configuration. \n\nDefaults to the server provided world generation folder if not set.")
      .add()
      .build();
   private String name = "Default";
   private String path;

   @Nonnull
   @Override
   public IWorldGen getGenerator() throws WorldGenLoadException {
      Path worldGenPath;
      if (this.path != null) {
         worldGenPath = PathUtil.get(this.path);
      } else {
         worldGenPath = Universe.getWorldGenPath();
      }

      if (!"Default".equals(this.name) || !Files.exists(worldGenPath.resolve("World.json"))) {
         worldGenPath = worldGenPath.resolve(this.name);
      }

      try {
         return new ChunkGeneratorJsonLoader(new SeedString<>("ChunkGenerator", new SeedStringResource(PrefabStoreRoot.DEFAULT, worldGenPath)), worldGenPath)
            .load();
      } catch (Error var3) {
         throw new WorldGenLoadException("Failed to load world gen!", var3);
      }
   }

   @Nonnull
   @Override
   public String toString() {
      return "HytaleWorldGenProvider{name='" + this.name + "', path='" + this.path + "'}";
   }
}
