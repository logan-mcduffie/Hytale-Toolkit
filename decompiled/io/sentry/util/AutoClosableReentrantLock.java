package io.sentry.util;

import io.sentry.ISentryLifecycleToken;
import java.util.concurrent.locks.ReentrantLock;
import org.jetbrains.annotations.NotNull;

public final class AutoClosableReentrantLock extends ReentrantLock {
   private static final long serialVersionUID = -3283069816958445549L;

   public ISentryLifecycleToken acquire() {
      this.lock();
      return new AutoClosableReentrantLock.AutoClosableReentrantLockLifecycleToken(this);
   }

   static final class AutoClosableReentrantLockLifecycleToken implements ISentryLifecycleToken {
      @NotNull
      private final ReentrantLock lock;

      AutoClosableReentrantLockLifecycleToken(@NotNull ReentrantLock lock) {
         this.lock = lock;
      }

      @Override
      public void close() {
         this.lock.unlock();
      }
   }
}
