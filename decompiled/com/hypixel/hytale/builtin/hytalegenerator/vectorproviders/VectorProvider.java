package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.newsystem.TerrainDensityProvider;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class VectorProvider {
   @Nonnull
   public abstract Vector3d process(@Nonnull VectorProvider.Context var1);

   public static class Context {
      @Nonnull
      public Vector3d position;
      @Nonnull
      public WorkerIndexer.Id workerId;
      @Nullable
      public TerrainDensityProvider terrainDensityProvider;

      public Context(@Nonnull Vector3d position, @Nonnull WorkerIndexer.Id workerId, @Nullable TerrainDensityProvider terrainDensityProvider) {
         this.position = position;
         this.workerId = workerId;
         this.terrainDensityProvider = terrainDensityProvider;
      }

      public Context(@Nonnull VectorProvider.Context other) {
         this.position = other.position;
         this.workerId = other.workerId;
         this.terrainDensityProvider = other.terrainDensityProvider;
      }

      public Context(@Nonnull Density.Context other) {
         this.position = other.position;
         this.workerId = other.workerId;
         this.terrainDensityProvider = other.terrainDensityProvider;
      }
   }
}
