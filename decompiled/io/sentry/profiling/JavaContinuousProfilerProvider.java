package io.sentry.profiling;

import io.sentry.IContinuousProfiler;
import io.sentry.ILogger;
import io.sentry.ISentryExecutorService;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface JavaContinuousProfilerProvider {
   @NotNull
   IContinuousProfiler getContinuousProfiler(ILogger var1, String var2, int var3, ISentryExecutorService var4);
}
