package com.hypixel.hytale.builtin.worldgen;

import com.hypixel.hytale.codec.lookup.Priority;
import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.IWorldGenProvider;
import com.hypixel.hytale.server.worldgen.BiomeDataSystem;
import com.hypixel.hytale.server.worldgen.HytaleWorldGenProvider;
import javax.annotation.Nonnull;

public class WorldGenPlugin extends JavaPlugin {
   private static WorldGenPlugin instance;

   public static WorldGenPlugin get() {
      return instance;
   }

   public WorldGenPlugin(@Nonnull JavaPluginInit init) {
      super(init);
   }

   @Override
   protected void setup() {
      instance = this;
      this.getEntityStoreRegistry().registerSystem(new BiomeDataSystem());
      IWorldGenProvider.CODEC.register(Priority.DEFAULT.before(1), "Hytale", HytaleWorldGenProvider.class, HytaleWorldGenProvider.CODEC);
   }
}
