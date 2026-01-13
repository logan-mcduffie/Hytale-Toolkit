package io.sentry.logger;

import io.sentry.SentryLogEvent;
import org.jetbrains.annotations.NotNull;

public interface ILoggerBatchProcessor {
   void add(@NotNull SentryLogEvent var1);

   void close(boolean var1);

   void flush(long var1);
}
