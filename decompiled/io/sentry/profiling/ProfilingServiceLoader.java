package io.sentry.profiling;

import io.sentry.IContinuousProfiler;
import io.sentry.ILogger;
import io.sentry.IProfileConverter;
import io.sentry.ISentryExecutorService;
import io.sentry.NoOpContinuousProfiler;
import io.sentry.NoOpProfileConverter;
import io.sentry.ScopesAdapter;
import io.sentry.SentryLevel;
import java.util.Iterator;
import java.util.ServiceLoader;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ProfilingServiceLoader {
   @NotNull
   public static IContinuousProfiler loadContinuousProfiler(
      ILogger logger, String profilingTracesDirPath, int profilingTracesHz, ISentryExecutorService executorService
   ) {
      try {
         JavaContinuousProfilerProvider provider = loadSingleProvider(JavaContinuousProfilerProvider.class);
         if (provider != null) {
            logger.log(SentryLevel.DEBUG, "Loaded continuous profiler from provider: %s", provider.getClass().getName());
            return provider.getContinuousProfiler(logger, profilingTracesDirPath, profilingTracesHz, executorService);
         } else {
            logger.log(SentryLevel.DEBUG, "No continuous profiler provider found, using NoOpContinuousProfiler");
            return NoOpContinuousProfiler.getInstance();
         }
      } catch (Throwable var5) {
         logger.log(SentryLevel.ERROR, "Failed to load continuous profiler provider, using NoOpContinuousProfiler", var5);
         return NoOpContinuousProfiler.getInstance();
      }
   }

   @NotNull
   public static IProfileConverter loadProfileConverter() {
      ILogger logger = ScopesAdapter.getInstance().getGlobalScope().getOptions().getLogger();

      try {
         JavaProfileConverterProvider provider = loadSingleProvider(JavaProfileConverterProvider.class);
         if (provider != null) {
            logger.log(SentryLevel.DEBUG, "Loaded profile converter from provider: %s", provider.getClass().getName());
            return provider.getProfileConverter();
         } else {
            logger.log(SentryLevel.DEBUG, "No profile converter provider found, using NoOpProfileConverter");
            return NoOpProfileConverter.getInstance();
         }
      } catch (Throwable var2) {
         logger.log(SentryLevel.ERROR, "Failed to load profile converter provider, using NoOpProfileConverter", var2);
         return NoOpProfileConverter.getInstance();
      }
   }

   @Nullable
   private static <T> T loadSingleProvider(Class<T> clazz) {
      ServiceLoader<T> serviceLoader = ServiceLoader.load(clazz);
      Iterator<T> iterator = serviceLoader.iterator();
      return iterator.hasNext() ? iterator.next() : null;
   }
}
