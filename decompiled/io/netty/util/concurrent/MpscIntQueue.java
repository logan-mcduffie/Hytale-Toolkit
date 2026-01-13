package io.netty.util.concurrent;

import io.netty.util.internal.MathUtil;
import io.netty.util.internal.ObjectUtil;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicIntegerArray;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.function.IntConsumer;
import java.util.function.IntSupplier;

public interface MpscIntQueue {
   static MpscIntQueue create(int size, int emptyValue) {
      return new MpscIntQueue.MpscAtomicIntegerArrayQueue(size, emptyValue);
   }

   boolean offer(int var1);

   int poll();

   int drain(int var1, IntConsumer var2);

   int fill(int var1, IntSupplier var2);

   boolean isEmpty();

   int size();

   public static final class MpscAtomicIntegerArrayQueue extends AtomicIntegerArray implements MpscIntQueue {
      private static final long serialVersionUID = 8740338425124821455L;
      private static final AtomicLongFieldUpdater<MpscIntQueue.MpscAtomicIntegerArrayQueue> PRODUCER_INDEX = AtomicLongFieldUpdater.newUpdater(
         MpscIntQueue.MpscAtomicIntegerArrayQueue.class, "producerIndex"
      );
      private static final AtomicLongFieldUpdater<MpscIntQueue.MpscAtomicIntegerArrayQueue> PRODUCER_LIMIT = AtomicLongFieldUpdater.newUpdater(
         MpscIntQueue.MpscAtomicIntegerArrayQueue.class, "producerLimit"
      );
      private static final AtomicLongFieldUpdater<MpscIntQueue.MpscAtomicIntegerArrayQueue> CONSUMER_INDEX = AtomicLongFieldUpdater.newUpdater(
         MpscIntQueue.MpscAtomicIntegerArrayQueue.class, "consumerIndex"
      );
      private final int mask;
      private final int emptyValue;
      private volatile long producerIndex;
      private volatile long producerLimit;
      private volatile long consumerIndex;

      public MpscAtomicIntegerArrayQueue(int capacity, int emptyValue) {
         super(MathUtil.safeFindNextPositivePowerOfTwo(capacity));
         if (emptyValue != 0) {
            this.emptyValue = emptyValue;
            int end = this.length() - 1;

            for (int i = 0; i < end; i++) {
               this.lazySet(i, emptyValue);
            }

            this.getAndSet(end, emptyValue);
         } else {
            this.emptyValue = 0;
         }

         this.mask = this.length() - 1;
      }

      @Override
      public boolean offer(int value) {
         if (value == this.emptyValue) {
            throw new IllegalArgumentException("Cannot offer the \"empty\" value: " + this.emptyValue);
         } else {
            int mask = this.mask;
            long producerLimit = this.producerLimit;

            long pIndex;
            do {
               pIndex = this.producerIndex;
               if (pIndex >= producerLimit) {
                  long cIndex = this.consumerIndex;
                  producerLimit = cIndex + mask + 1L;
                  if (pIndex >= producerLimit) {
                     return false;
                  }

                  PRODUCER_LIMIT.lazySet(this, producerLimit);
               }
            } while (!PRODUCER_INDEX.compareAndSet(this, pIndex, pIndex + 1L));

            int offset = (int)(pIndex & mask);
            this.lazySet(offset, value);
            return true;
         }
      }

      @Override
      public int poll() {
         long cIndex = this.consumerIndex;
         int offset = (int)(cIndex & this.mask);
         int value = this.get(offset);
         if (this.emptyValue == value) {
            if (cIndex == this.producerIndex) {
               return this.emptyValue;
            }

            do {
               value = this.get(offset);
            } while (this.emptyValue == value);
         }

         this.lazySet(offset, this.emptyValue);
         CONSUMER_INDEX.lazySet(this, cIndex + 1L);
         return value;
      }

      @Override
      public int drain(int limit, IntConsumer consumer) {
         Objects.requireNonNull(consumer, "consumer");
         ObjectUtil.checkPositiveOrZero(limit, "limit");
         if (limit == 0) {
            return 0;
         } else {
            int mask = this.mask;
            long cIndex = this.consumerIndex;

            for (int i = 0; i < limit; i++) {
               long index = cIndex + i;
               int offset = (int)(index & mask);
               int value = this.get(offset);
               if (this.emptyValue == value) {
                  return i;
               }

               this.lazySet(offset, this.emptyValue);
               CONSUMER_INDEX.lazySet(this, index + 1L);
               consumer.accept(value);
            }

            return limit;
         }
      }

      @Override
      public int fill(int limit, IntSupplier supplier) {
         Objects.requireNonNull(supplier, "supplier");
         ObjectUtil.checkPositiveOrZero(limit, "limit");
         if (limit == 0) {
            return 0;
         } else {
            int mask = this.mask;
            long capacity = mask + 1;
            long producerLimit = this.producerLimit;

            long pIndex;
            int actualLimit;
            do {
               pIndex = this.producerIndex;
               long available = producerLimit - pIndex;
               if (available <= 0L) {
                  long cIndex = this.consumerIndex;
                  producerLimit = cIndex + capacity;
                  available = producerLimit - pIndex;
                  if (available <= 0L) {
                     return 0;
                  }

                  PRODUCER_LIMIT.lazySet(this, producerLimit);
               }

               actualLimit = Math.min((int)available, limit);
            } while (!PRODUCER_INDEX.compareAndSet(this, pIndex, pIndex + actualLimit));

            for (int i = 0; i < actualLimit; i++) {
               int offset = (int)(pIndex + i & mask);
               this.lazySet(offset, supplier.getAsInt());
            }

            return actualLimit;
         }
      }

      @Override
      public boolean isEmpty() {
         long cIndex = this.consumerIndex;
         long pIndex = this.producerIndex;
         return cIndex >= pIndex;
      }

      @Override
      public int size() {
         long after = this.consumerIndex;

         long before;
         long pIndex;
         do {
            before = after;
            pIndex = this.producerIndex;
            after = this.consumerIndex;
         } while (before != after);

         long size = pIndex - after;
         return size < 0L ? 0 : (size > 2147483647L ? Integer.MAX_VALUE : (int)size);
      }
   }
}
