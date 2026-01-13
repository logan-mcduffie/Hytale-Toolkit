package io.netty.util.concurrent;

import io.netty.util.internal.ObjectUtil;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

final class DefaultMockTicker implements MockTicker {
   private final ReentrantLock lock = new ReentrantLock(true);
   private final Condition tickCondition = this.lock.newCondition();
   private final Condition sleeperCondition = this.lock.newCondition();
   private final AtomicLong nanoTime = new AtomicLong();
   private final Set<Thread> sleepers = Collections.newSetFromMap(new IdentityHashMap<>());

   @Override
   public long nanoTime() {
      return this.nanoTime.get();
   }

   @Override
   public void sleep(long delay, TimeUnit unit) throws InterruptedException {
      ObjectUtil.checkPositiveOrZero(delay, "delay");
      Objects.requireNonNull(unit, "unit");
      if (delay != 0L) {
         long delayNanos = unit.toNanos(delay);
         this.lock.lockInterruptibly();

         try {
            long startTimeNanos = this.nanoTime();
            this.sleepers.add(Thread.currentThread());
            this.sleeperCondition.signalAll();

            do {
               this.tickCondition.await();
            } while (this.nanoTime() - startTimeNanos < delayNanos);
         } finally {
            this.sleepers.remove(Thread.currentThread());
            this.lock.unlock();
         }
      }
   }

   public void awaitSleepingThread(Thread thread) throws InterruptedException {
      this.lock.lockInterruptibly();

      try {
         while (!this.sleepers.contains(thread)) {
            this.sleeperCondition.await();
         }
      } finally {
         this.lock.unlock();
      }
   }

   @Override
   public void advance(long amount, TimeUnit unit) {
      ObjectUtil.checkPositiveOrZero(amount, "amount");
      Objects.requireNonNull(unit, "unit");
      if (amount != 0L) {
         long amountNanos = unit.toNanos(amount);
         this.lock.lock();

         try {
            this.nanoTime.addAndGet(amountNanos);
            this.tickCondition.signalAll();
         } finally {
            this.lock.unlock();
         }
      }
   }
}
