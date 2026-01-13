package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ILogger {
   void log(@NotNull SentryLevel var1, @NotNull String var2, @Nullable Object... var3);

   void log(@NotNull SentryLevel var1, @NotNull String var2, @Nullable Throwable var3);

   void log(@NotNull SentryLevel var1, @Nullable Throwable var2, @NotNull String var3, @Nullable Object... var4);

   boolean isEnabled(@Nullable SentryLevel var1);
}
