package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IPerformanceContinuousCollector extends IPerformanceCollector {
   void onSpanStarted(@NotNull ISpan var1);

   void onSpanFinished(@NotNull ISpan var1);

   void clear();
}
