package io.sentry.util.thread;

import io.sentry.protocol.SentryThread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public interface IThreadChecker {
   boolean isMainThread(long var1);

   boolean isMainThread(@NotNull Thread var1);

   boolean isMainThread();

   boolean isMainThread(@NotNull SentryThread var1);

   @NotNull
   String getCurrentThreadName();

   long currentThreadSystemId();
}
