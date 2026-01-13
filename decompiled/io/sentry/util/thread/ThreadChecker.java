package io.sentry.util.thread;

import io.sentry.protocol.SentryThread;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.ApiStatus.Internal;

@Internal
public final class ThreadChecker implements IThreadChecker {
   private static final long mainThreadId = Thread.currentThread().getId();
   private static final ThreadChecker instance = new ThreadChecker();

   public static ThreadChecker getInstance() {
      return instance;
   }

   private ThreadChecker() {
   }

   @Override
   public boolean isMainThread(long threadId) {
      return mainThreadId == threadId;
   }

   @Override
   public boolean isMainThread(@NotNull Thread thread) {
      return this.isMainThread(thread.getId());
   }

   @Override
   public boolean isMainThread() {
      return this.isMainThread(Thread.currentThread());
   }

   @Override
   public boolean isMainThread(@NotNull SentryThread sentryThread) {
      Long threadId = sentryThread.getId();
      return threadId != null && this.isMainThread(threadId);
   }

   @NotNull
   @Override
   public String getCurrentThreadName() {
      return Thread.currentThread().getName();
   }

   @Override
   public long currentThreadSystemId() {
      return Thread.currentThread().getId();
   }
}
