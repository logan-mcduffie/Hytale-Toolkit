package io.sentry.util;

import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ExceptionUtils {
   @NotNull
   public static Throwable findRootCause(@NotNull Throwable throwable) {
      Objects.requireNonNull(throwable, "throwable cannot be null");
      Throwable rootCause = throwable;

      while (rootCause.getCause() != null && rootCause.getCause() != rootCause) {
         rootCause = rootCause.getCause();
      }

      return rootCause;
   }

   @Internal
   public static boolean isIgnored(@NotNull Set<Class<? extends Throwable>> ignoredExceptionsForType, @NotNull Throwable throwable) {
      return ignoredExceptionsForType.contains(throwable.getClass());
   }
}
