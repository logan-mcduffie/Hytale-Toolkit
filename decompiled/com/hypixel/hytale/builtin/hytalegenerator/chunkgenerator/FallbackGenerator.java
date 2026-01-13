package com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator;

import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedBlockChunk;
import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedBlockStateChunk;
import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedChunk;
import com.hypixel.hytale.server.core.universe.world.worldgen.GeneratedEntityChunk;
import javax.annotation.Nonnull;

public class FallbackGenerator implements ChunkGenerator {
   public static final FallbackGenerator INSTANCE = new FallbackGenerator();

   @Override
   public GeneratedChunk generate(@Nonnull ChunkRequest.Arguments arguments) {
      return new GeneratedChunk(
         new GeneratedBlockChunk(arguments.index(), arguments.x(), arguments.z()),
         new GeneratedBlockStateChunk(),
         new GeneratedEntityChunk(),
         GeneratedChunk.makeSections()
      );
   }
}
