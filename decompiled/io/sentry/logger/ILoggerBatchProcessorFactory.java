package io.sentry.logger;

import io.sentry.SentryClient;
import io.sentry.SentryOptions;
import org.jetbrains.annotations.NotNull;

public interface ILoggerBatchProcessorFactory {
   @NotNull
   ILoggerBatchProcessor create(@NotNull SentryOptions var1, @NotNull SentryClient var2);
}
