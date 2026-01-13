package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class JavaMemoryCollector implements IPerformanceSnapshotCollector {
   @NotNull
   private final Runtime runtime = Runtime.getRuntime();

   @Override
   public void setup() {
   }

   @Override
   public void collect(@NotNull PerformanceCollectionData performanceCollectionData) {
      long usedMemory = this.runtime.totalMemory() - this.runtime.freeMemory();
      performanceCollectionData.setUsedHeapMemory(usedMemory);
   }
}
