package io.sentry.util;

import io.sentry.ILogger;
import io.sentry.SentryLevel;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class LogUtils {
   public static void logNotInstanceOf(@NotNull Class<?> expectedClass, @Nullable Object sentrySdkHint, @NotNull ILogger logger) {
      logger.log(
         SentryLevel.DEBUG, "%s is not %s", sentrySdkHint != null ? sentrySdkHint.getClass().getCanonicalName() : "Hint", expectedClass.getCanonicalName()
      );
   }
}
