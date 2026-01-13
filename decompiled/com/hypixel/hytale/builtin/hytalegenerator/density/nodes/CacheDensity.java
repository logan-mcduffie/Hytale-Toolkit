package com.hypixel.hytale.builtin.hytalegenerator.density.nodes;

import com.hypixel.hytale.builtin.hytalegenerator.density.Density;
import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class CacheDensity extends Density {
   private final WorkerIndexer.Data<CacheDensity.Cache> threadData;
   @Nonnull
   private Density input;

   public CacheDensity(@Nonnull Density input, int threadCount) {
      this.input = input;
      this.threadData = new WorkerIndexer.Data<>(threadCount, CacheDensity.Cache::new);
   }

   @Override
   public double process(@Nonnull Density.Context context) {
      CacheDensity.Cache cache = this.threadData.get(context.workerId);
      if (cache.position != null && cache.position.x == context.position.x && cache.position.y == context.position.y && cache.position.z == context.position.z) {
         return cache.value;
      } else {
         if (cache.position == null) {
            cache.position = new Vector3d();
         }

         cache.position.assign(context.position);
         cache.value = this.input.process(context);
         return cache.value;
      }
   }

   @Override
   public void setInputs(@Nonnull Density[] inputs) {
      assert inputs.length != 0;

      assert inputs[0] != null;

      this.input = inputs[0];
   }

   private static class Cache {
      Vector3d position;
      double value;
   }
}
