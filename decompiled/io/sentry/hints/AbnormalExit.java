package io.sentry.hints;

import org.jetbrains.annotations.Nullable;

public interface AbnormalExit {
   @Nullable
   String mechanism();

   boolean ignoreCurrentThread();

   @Nullable
   Long timestamp();
}
