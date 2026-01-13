package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IPerformanceSnapshotCollector extends IPerformanceCollector {
   void setup();

   void collect(@NotNull PerformanceCollectionData var1);
}
