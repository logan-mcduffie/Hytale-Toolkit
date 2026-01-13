package io.sentry.util.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IRuntimeManager {
   <T> T runWithRelaxedPolicy(@NotNull IRuntimeManager.IRuntimeManagerCallback<T> var1);

   void runWithRelaxedPolicy(@NotNull Runnable var1);

   public interface IRuntimeManagerCallback<T> {
      T run();
   }
}
