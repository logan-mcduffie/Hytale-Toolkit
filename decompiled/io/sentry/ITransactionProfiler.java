package io.sentry;

import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface ITransactionProfiler {
   boolean isRunning();

   void start();

   void bindTransaction(@NotNull ITransaction var1);

   @Nullable
   ProfilingTraceData onTransactionFinish(@NotNull ITransaction var1, @Nullable List<PerformanceCollectionData> var2, @NotNull SentryOptions var3);

   void close();
}
