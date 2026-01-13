package io.sentry;

import org.jetbrains.annotations.NotNull;

public interface Integration {
   void register(@NotNull IScopes var1, @NotNull SentryOptions var2);
}
