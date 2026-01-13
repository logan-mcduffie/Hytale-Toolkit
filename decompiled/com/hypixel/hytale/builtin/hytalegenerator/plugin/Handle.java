package com.hypixel.hytale.builtin.hytalegenerator.plugin;

import com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator.ChunkRequest;
import com.hypixel.hytale.math.vector.Transform;
import com.hypixel.hytale.server.core.universe.world.spawn.ISpawnProvider;
import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedChunk;
import com.hypixel.hytale.server.core.universe.world.worldgen.IWorldGen;
import com.hypixel.hytale.server.core.universe.world.worldgen.WorldGenTimingsCollector;
import java.util.concurrent.CompletableFuture;
import java.util.function.LongPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class Handle implements IWorldGen {
   @Nonnull
   private final HytaleGenerator plugin;
   @Nonnull
   private final ChunkRequest.GeneratorProfile profile;

   public Handle(@Nonnull HytaleGenerator plugin, @Nonnull ChunkRequest.GeneratorProfile profile) {
      this.plugin = plugin;
      this.profile = profile;
   }

   @Override
   public CompletableFuture<GeneratedChunk> generate(int seed, long index, int x, int z, LongPredicate stillNeeded) {
      ChunkRequest.Arguments arguments = new ChunkRequest.Arguments(seed, index, x, z, stillNeeded);
      this.profile.setSeed(seed);
      ChunkRequest request = new ChunkRequest(this.profile, arguments);
      return this.plugin.submitChunkRequest(request);
   }

   @Nonnull
   public ChunkRequest.GeneratorProfile getProfile() {
      return this.profile;
   }

   @Override
   public Transform[] getSpawnPoints(int seed) {
      return new Transform[]{this.profile.spawnPosition().clone()};
   }

   @Nonnull
   @Override
   public ISpawnProvider getDefaultSpawnProvider(int seed) {
      return IWorldGen.super.getDefaultSpawnProvider(seed);
   }

   @Nullable
   @Override
   public WorldGenTimingsCollector getTimings() {
      return null;
   }
}
