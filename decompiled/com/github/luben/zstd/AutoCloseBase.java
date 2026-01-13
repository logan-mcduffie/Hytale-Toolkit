package com.github.luben.zstd;

import java.io.Closeable;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;

abstract class AutoCloseBase implements Closeable {
   private static final AtomicIntegerFieldUpdater<AutoCloseBase> SHARED_LOCK_UPDATER = AtomicIntegerFieldUpdater.newUpdater(AutoCloseBase.class, "sharedLock");
   private static final int SHARED_LOCK_CLOSED = -1;
   private volatile int sharedLock;

   void storeFence() {
      this.sharedLock = 0;
   }

   void acquireSharedLock() {
      int var1;
      do {
         var1 = this.sharedLock;
         if (var1 < 0) {
            throw new IllegalStateException("Closed");
         }

         if (var1 == Integer.MAX_VALUE) {
            throw new IllegalStateException("Shared lock overflow");
         }
      } while (!SHARED_LOCK_UPDATER.compareAndSet(this, var1, var1 + 1));
   }

   void releaseSharedLock() {
      int var1;
      do {
         var1 = this.sharedLock;
         if (var1 < 0) {
            throw new IllegalStateException("Closed");
         }

         if (var1 == 0) {
            throw new IllegalStateException("Shared lock underflow");
         }
      } while (!SHARED_LOCK_UPDATER.compareAndSet(this, var1, var1 - 1));
   }

   abstract void doClose();

   @Override
   public void close() {
      synchronized (this) {
         if (this.sharedLock != -1) {
            if (!SHARED_LOCK_UPDATER.compareAndSet(this, 0, -1)) {
               throw new IllegalStateException("Attempt to close while in use");
            } else {
               this.doClose();
            }
         }
      }
   }
}
