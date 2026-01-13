package io.sentry.transport;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import org.jetbrains.annotations.NotNull;

public final class ReusableCountLatch {
   @NotNull
   private final ReusableCountLatch.Sync sync;

   public ReusableCountLatch(int initialCount) {
      if (initialCount < 0) {
         throw new IllegalArgumentException("negative initial count '" + initialCount + "' is not allowed");
      } else {
         this.sync = new ReusableCountLatch.Sync(initialCount);
      }
   }

   public ReusableCountLatch() {
      this(0);
   }

   public int getCount() {
      return this.sync.getCount();
   }

   public void decrement() {
      this.sync.decrement();
   }

   public void increment() {
      this.sync.increment();
   }

   public void waitTillZero() throws InterruptedException {
      this.sync.acquireSharedInterruptibly(1);
   }

   public boolean waitTillZero(long timeout, @NotNull TimeUnit unit) throws InterruptedException {
      return this.sync.tryAcquireSharedNanos(1, unit.toNanos(timeout));
   }

   private static final class Sync extends AbstractQueuedSynchronizer {
      private static final long serialVersionUID = 5970133580157457018L;

      Sync(int count) {
         this.setState(count);
      }

      private int getCount() {
         return this.getState();
      }

      private void increment() {
         int oldCount;
         int newCount;
         do {
            oldCount = this.getState();
            newCount = oldCount + 1;
         } while (!this.compareAndSetState(oldCount, newCount));
      }

      private void decrement() {
         this.releaseShared(1);
      }

      @Override
      public int tryAcquireShared(int acquires) {
         return this.getState() == 0 ? 1 : -1;
      }

      @Override
      public boolean tryReleaseShared(int releases) {
         int oldCount;
         int newCount;
         do {
            oldCount = this.getState();
            if (oldCount == 0) {
               return false;
            }

            newCount = oldCount - 1;
         } while (!this.compareAndSetState(oldCount, newCount));

         return newCount == 0;
      }
   }
}
