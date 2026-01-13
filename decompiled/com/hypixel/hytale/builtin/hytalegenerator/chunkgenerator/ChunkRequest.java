package com.hypixel.hytale.builtin.hytalegenerator.chunkgenerator;

import com.hypixel.hytale.math.vector.Transform;
import java.util.Objects;
import java.util.function.LongPredicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public record ChunkRequest(@Nonnull ChunkRequest.GeneratorProfile generatorProfile, @Nonnull ChunkRequest.Arguments arguments) {
   public record Arguments(int seed, long index, int x, int z, @Nullable LongPredicate stillNeeded) {
   }

   public static final class GeneratorProfile {
      @Nonnull
      private final String worldStructureName;
      @Nonnull
      private final Transform spawnPosition;
      private int seed;

      public GeneratorProfile(@Nonnull String worldStructureName, @Nonnull Transform spawnPosition, int seed) {
         this.worldStructureName = worldStructureName;
         this.spawnPosition = spawnPosition;
         this.seed = seed;
      }

      @Nonnull
      public String worldStructureName() {
         return this.worldStructureName;
      }

      @Nonnull
      public Transform spawnPosition() {
         return this.spawnPosition;
      }

      public int seed() {
         return this.seed;
      }

      public void setSeed(int seed) {
         this.seed = seed;
      }

      @Override
      public boolean equals(Object obj) {
         if (obj == this) {
            return true;
         } else if (obj != null && obj.getClass() == this.getClass()) {
            ChunkRequest.GeneratorProfile that = (ChunkRequest.GeneratorProfile)obj;
            return Objects.equals(this.worldStructureName, that.worldStructureName)
               && Objects.equals(this.spawnPosition, that.spawnPosition)
               && this.seed == that.seed;
         } else {
            return false;
         }
      }

      @Override
      public int hashCode() {
         return Objects.hash(this.worldStructureName, this.spawnPosition, this.seed);
      }

      @Override
      public String toString() {
         return "GeneratorProfile[worldStructureName=" + this.worldStructureName + ", spawnPosition=" + this.spawnPosition + ", seed=" + this.seed + "]";
      }
   }
}
