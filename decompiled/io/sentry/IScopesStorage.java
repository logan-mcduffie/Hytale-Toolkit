package io.sentry;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IScopesStorage {
   void init();

   @NotNull
   ISentryLifecycleToken set(@Nullable IScopes var1);

   @Nullable
   IScopes get();

   void close();
}
