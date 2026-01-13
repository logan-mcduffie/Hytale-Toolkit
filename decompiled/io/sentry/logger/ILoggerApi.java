package io.sentry.logger;

import io.sentry.SentryDate;
import io.sentry.SentryLogLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ILoggerApi {
   void trace(@Nullable String var1, @Nullable Object... var2);

   void debug(@Nullable String var1, @Nullable Object... var2);

   void info(@Nullable String var1, @Nullable Object... var2);

   void warn(@Nullable String var1, @Nullable Object... var2);

   void error(@Nullable String var1, @Nullable Object... var2);

   void fatal(@Nullable String var1, @Nullable Object... var2);

   void log(@NotNull SentryLogLevel var1, @Nullable String var2, @Nullable Object... var3);

   void log(@NotNull SentryLogLevel var1, @Nullable SentryDate var2, @Nullable String var3, @Nullable Object... var4);

   void log(@NotNull SentryLogLevel var1, @NotNull SentryLogParameters var2, @Nullable String var3, @Nullable Object... var4);
}
