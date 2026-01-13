package com.hypixel.hytale.builtin.hytalegenerator.vectorproviders;

import com.hypixel.hytale.builtin.hytalegenerator.threadindexer.WorkerIndexer;
import com.hypixel.hytale.math.vector.Vector3d;
import javax.annotation.Nonnull;

public class CacheVectorProvider extends VectorProvider {
   @Nonnull
   private final VectorProvider vectorProvider;
   @Nonnull
   private final WorkerIndexer.Data<CacheVectorProvider.Cache> threadData;

   public CacheVectorProvider(@Nonnull VectorProvider vectorProvider, int threadCount) {
      if (threadCount <= 0) {
         throw new IllegalArgumentException("threadCount must be greater than 0");
      } else {
         this.vectorProvider = vectorProvider;
         this.threadData = new WorkerIndexer.Data<>(threadCount, CacheVectorProvider.Cache::new);
      }
   }

   @Nonnull
   @Override
   public Vector3d process(@Nonnull VectorProvider.Context context) {
      CacheVectorProvider.Cache cache = this.threadData.get(context.workerId);
      if (cache.position != null && cache.position.equals(context.position)) {
         return cache.value;
      } else {
         if (cache.position == null) {
            cache.position = new Vector3d();
         }

         cache.position.assign(context.position);
         cache.value = this.vectorProvider.process(context);
         return cache.value;
      }
   }

   public static class Cache {
      Vector3d position;
      Vector3d value;
   }
}
