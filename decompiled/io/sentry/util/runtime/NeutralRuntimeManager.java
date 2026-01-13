package io.sentry.util.runtime;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class NeutralRuntimeManager implements IRuntimeManager {
   @Override
   public <T> T runWithRelaxedPolicy(@NotNull IRuntimeManager.IRuntimeManagerCallback<T> toRun) {
      return toRun.run();
   }

   @Override
   public void runWithRelaxedPolicy(@NotNull Runnable toRun) {
      toRun.run();
   }
}
