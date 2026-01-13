package io.netty.util.internal.shaded.org.jctools.queues.atomic.unpadded;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueueUtil;
import io.netty.util.internal.shaded.org.jctools.queues.atomic.AtomicQueueUtil;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MpscAtomicUnpaddedArrayQueue<E> extends MpscAtomicUnpaddedArrayQueueL3Pad<E> {
   public MpscAtomicUnpaddedArrayQueue(int capacity) {
      super(capacity);
   }

   public boolean offerIfBelowThreshold(E e, int threshold) {
      if (null == e) {
         throw new NullPointerException();
      } else {
         int mask = this.mask;
         long capacity = mask + 1;
         long producerLimit = this.lvProducerLimit();

         long pIndex;
         do {
            pIndex = this.lvProducerIndex();
            long available = producerLimit - pIndex;
            long size = capacity - available;
            if (size >= threshold) {
               long cIndex = this.lvConsumerIndex();
               size = pIndex - cIndex;
               if (size >= threshold) {
                  return false;
               }

               producerLimit = cIndex + capacity;
               this.soProducerLimit(producerLimit);
            }
         } while (!this.casProducerIndex(pIndex, pIndex + 1L));

         int offset = AtomicQueueUtil.calcCircularRefElementOffset(pIndex, mask);
         AtomicQueueUtil.soRefElement(this.buffer, offset, e);
         return true;
      }
   }

   @Override
   public boolean offer(E e) {
      if (null == e) {
         throw new NullPointerException();
      } else {
         int mask = this.mask;
         long producerLimit = this.lvProducerLimit();

         long pIndex;
         do {
            pIndex = this.lvProducerIndex();
            if (pIndex >= producerLimit) {
               long cIndex = this.lvConsumerIndex();
               producerLimit = cIndex + mask + 1L;
               if (pIndex >= producerLimit) {
                  return false;
               }

               this.soProducerLimit(producerLimit);
            }
         } while (!this.casProducerIndex(pIndex, pIndex + 1L));

         int offset = AtomicQueueUtil.calcCircularRefElementOffset(pIndex, mask);
         AtomicQueueUtil.soRefElement(this.buffer, offset, e);
         return true;
      }
   }

   public final int failFastOffer(E e) {
      if (null == e) {
         throw new NullPointerException();
      } else {
         int mask = this.mask;
         long capacity = mask + 1;
         long pIndex = this.lvProducerIndex();
         long producerLimit = this.lvProducerLimit();
         if (pIndex >= producerLimit) {
            long cIndex = this.lvConsumerIndex();
            producerLimit = cIndex + capacity;
            if (pIndex >= producerLimit) {
               return 1;
            }

            this.soProducerLimit(producerLimit);
         }

         if (!this.casProducerIndex(pIndex, pIndex + 1L)) {
            return -1;
         } else {
            int offset = AtomicQueueUtil.calcCircularRefElementOffset(pIndex, mask);
            AtomicQueueUtil.soRefElement(this.buffer, offset, e);
            return 0;
         }
      }
   }

   @Override
   public E poll() {
      long cIndex = this.lpConsumerIndex();
      int offset = AtomicQueueUtil.calcCircularRefElementOffset(cIndex, this.mask);
      AtomicReferenceArray<E> buffer = this.buffer;
      E e = AtomicQueueUtil.lvRefElement(buffer, offset);
      if (null == e) {
         if (cIndex == this.lvProducerIndex()) {
            return null;
         }

         do {
            e = AtomicQueueUtil.lvRefElement(buffer, offset);
         } while (e == null);
      }

      AtomicQueueUtil.spRefElement(buffer, offset, null);
      this.soConsumerIndex(cIndex + 1L);
      return e;
   }

   @Override
   public E peek() {
      AtomicReferenceArray<E> buffer = this.buffer;
      long cIndex = this.lpConsumerIndex();
      int offset = AtomicQueueUtil.calcCircularRefElementOffset(cIndex, this.mask);
      E e = AtomicQueueUtil.lvRefElement(buffer, offset);
      if (null == e) {
         if (cIndex == this.lvProducerIndex()) {
            return null;
         }

         do {
            e = AtomicQueueUtil.lvRefElement(buffer, offset);
         } while (e == null);
      }

      return e;
   }

   @Override
   public boolean relaxedOffer(E e) {
      return this.offer(e);
   }

   @Override
   public E relaxedPoll() {
      AtomicReferenceArray<E> buffer = this.buffer;
      long cIndex = this.lpConsumerIndex();
      int offset = AtomicQueueUtil.calcCircularRefElementOffset(cIndex, this.mask);
      E e = AtomicQueueUtil.lvRefElement(buffer, offset);
      if (null == e) {
         return null;
      } else {
         AtomicQueueUtil.spRefElement(buffer, offset, null);
         this.soConsumerIndex(cIndex + 1L);
         return e;
      }
   }

   @Override
   public E relaxedPeek() {
      AtomicReferenceArray<E> buffer = this.buffer;
      int mask = this.mask;
      long cIndex = this.lpConsumerIndex();
      return AtomicQueueUtil.lvRefElement(buffer, AtomicQueueUtil.calcCircularRefElementOffset(cIndex, mask));
   }

   @Override
   public int drain(MessagePassingQueue.Consumer<E> c, int limit) {
      if (null == c) {
         throw new IllegalArgumentException("c is null");
      } else if (limit < 0) {
         throw new IllegalArgumentException("limit is negative: " + limit);
      } else if (limit == 0) {
         return 0;
      } else {
         AtomicReferenceArray<E> buffer = this.buffer;
         int mask = this.mask;
         long cIndex = this.lpConsumerIndex();

         for (int i = 0; i < limit; i++) {
            long index = cIndex + i;
            int offset = AtomicQueueUtil.calcCircularRefElementOffset(index, mask);
            E e = AtomicQueueUtil.lvRefElement(buffer, offset);
            if (null == e) {
               return i;
            }

            AtomicQueueUtil.spRefElement(buffer, offset, null);
            this.soConsumerIndex(index + 1L);
            c.accept(e);
         }

         return limit;
      }
   }

   @Override
   public int fill(MessagePassingQueue.Supplier<E> s, int limit) {
      if (null == s) {
         throw new IllegalArgumentException("supplier is null");
      } else if (limit < 0) {
         throw new IllegalArgumentException("limit is negative:" + limit);
      } else if (limit == 0) {
         return 0;
      } else {
         int mask = this.mask;
         long capacity = mask + 1;
         long producerLimit = this.lvProducerLimit();

         long pIndex;
         int actualLimit;
         do {
            pIndex = this.lvProducerIndex();
            long available = producerLimit - pIndex;
            if (available <= 0L) {
               long cIndex = this.lvConsumerIndex();
               producerLimit = cIndex + capacity;
               available = producerLimit - pIndex;
               if (available <= 0L) {
                  return 0;
               }

               this.soProducerLimit(producerLimit);
            }

            actualLimit = Math.min((int)available, limit);
         } while (!this.casProducerIndex(pIndex, pIndex + actualLimit));

         AtomicReferenceArray<E> buffer = this.buffer;

         for (int i = 0; i < actualLimit; i++) {
            int offset = AtomicQueueUtil.calcCircularRefElementOffset(pIndex + i, mask);
            AtomicQueueUtil.soRefElement(buffer, offset, s.get());
         }

         return actualLimit;
      }
   }

   @Override
   public int drain(MessagePassingQueue.Consumer<E> c) {
      return this.drain(c, this.capacity());
   }

   @Override
   public int fill(MessagePassingQueue.Supplier<E> s) {
      return MessagePassingQueueUtil.fillBounded(this, s);
   }

   @Override
   public void drain(MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy w, MessagePassingQueue.ExitCondition exit) {
      MessagePassingQueueUtil.drain(this, c, w, exit);
   }

   @Override
   public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
      MessagePassingQueueUtil.fill(this, s, wait, exit);
   }

   @Deprecated
   public int weakOffer(E e) {
      return this.failFastOffer(e);
   }
}
