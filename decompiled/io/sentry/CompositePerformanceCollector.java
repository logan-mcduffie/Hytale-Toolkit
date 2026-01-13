package io.sentry;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

public interface CompositePerformanceCollector {
   void start(@NotNull ITransaction var1);

   void start(@NotNull String var1);

   void onSpanStarted(@NotNull ISpan var1);

   void onSpanFinished(@NotNull ISpan var1);

   @Nullable
   List<PerformanceCollectionData> stop(@NotNull ITransaction var1);

   @Nullable
   List<PerformanceCollectionData> stop(@NotNull String var1);

   @Internal
   void close();
}
