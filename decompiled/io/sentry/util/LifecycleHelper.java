package io.sentry.util;

import io.sentry.ISentryLifecycleToken;
import org.jetbrains.annotations.Nullable;

public final class LifecycleHelper {
   public static void close(@Nullable Object tokenObject) {
      if (tokenObject != null && tokenObject instanceof ISentryLifecycleToken) {
         ISentryLifecycleToken token = (ISentryLifecycleToken)tokenObject;
         token.close();
      }
   }
}
