package com.hypixel.hytale.builtin.hytalegenerator.plugin;

import com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator.ChunkRequest;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenLoadException;
import com.hypixel.hytale.server.core.universe.world.worldgen.provider.IWorldGenProvider;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class HandleProvider implements IWorldGenProvider {
   public static final String ID = "HytaleGenerator";
   public static final String DEFAULT_WORLD_STRUCTURE_NAME = "Default";
   public static final Transform DEFAULT_PLAYER_SPAWN = new Transform(0.0, 140.0, 0.0);
   @Nonnull
   private final HytaleGenerator plugin;
   @Nonnull
   private String worldStructureName = "Default";
   @Nonnull
   private Transform playerSpawn = DEFAULT_PLAYER_SPAWN;

   public HandleProvider(@Nonnull HytaleGenerator plugin) {
      this.plugin = plugin;
   }

   public void setWorldStructureName(@Nullable String worldStructureName) {
      this.worldStructureName = worldStructureName;
   }

   public void setPlayerSpawn(@Nullable Transform worldSpawn) {
      if (worldSpawn == null) {
         this.playerSpawn = DEFAULT_PLAYER_SPAWN;
      } else {
         this.playerSpawn = worldSpawn.clone();
      }
   }

   @Nonnull
   public String getWorldStructureName() {
      return this.worldStructureName;
   }

   @Nonnull
   public Transform getPlayerSpawn() {
      return this.playerSpawn;
   }

   @Override
   public IWorldGen getGenerator() throws WorldGenLoadException {
      return new Handle(this.plugin, new ChunkRequest.GeneratorProfile(this.worldStructureName, this.playerSpawn.clone(), 0));
   }
}
