package io.sentry.util.thread;

import io.sentry.protocol.SentryThread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class NoOpThreadChecker implements IThreadChecker {
   private static final NoOpThreadChecker instance = new NoOpThreadChecker();

   public static NoOpThreadChecker getInstance() {
      return instance;
   }

   @Override
   public boolean isMainThread(long threadId) {
      return false;
   }

   @Override
   public boolean isMainThread(@NotNull Thread thread) {
      return false;
   }

   @Override
   public boolean isMainThread() {
      return false;
   }

   @Override
   public boolean isMainThread(@NotNull SentryThread sentryThread) {
      return false;
   }

   @NotNull
   @Override
   public String getCurrentThreadName() {
      return "";
   }

   @Override
   public long currentThreadSystemId() {
      return 0L;
   }
}
