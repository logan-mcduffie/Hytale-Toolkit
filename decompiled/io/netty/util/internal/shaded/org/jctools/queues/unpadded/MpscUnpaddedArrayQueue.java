package io.netty.util.internal.shaded.org.jctools.queues.unpadded;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueueUtil;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;

public class MpscUnpaddedArrayQueue<E> extends MpscUnpaddedArrayQueueL3Pad<E> {
   public MpscUnpaddedArrayQueue(int capacity) {
      super(capacity);
   }

   public boolean offerIfBelowThreshold(E e, int threshold) {
      if (null == e) {
         throw new NullPointerException();
      } else {
         long mask = this.mask;
         long capacity = mask + 1L;
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

         long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(pIndex, mask);
         UnsafeRefArrayAccess.soRefElement(this.buffer, offset, e);
         return true;
      }
   }

   @Override
   public boolean offer(E e) {
      if (null == e) {
         throw new NullPointerException();
      } else {
         long mask = this.mask;
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

         long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(pIndex, mask);
         UnsafeRefArrayAccess.soRefElement(this.buffer, offset, e);
         return true;
      }
   }

   public final int failFastOffer(E e) {
      if (null == e) {
         throw new NullPointerException();
      } else {
         long mask = this.mask;
         long capacity = mask + 1L;
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
            long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(pIndex, mask);
            UnsafeRefArrayAccess.soRefElement(this.buffer, offset, e);
            return 0;
         }
      }
   }

   @Override
   public E poll() {
      long cIndex = this.lpConsumerIndex();
      long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(cIndex, this.mask);
      E[] buffer = this.buffer;
      E e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
      if (null == e) {
         if (cIndex == this.lvProducerIndex()) {
            return null;
         }

         do {
            e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
         } while (e == null);
      }

      UnsafeRefArrayAccess.spRefElement(buffer, offset, null);
      this.soConsumerIndex(cIndex + 1L);
      return e;
   }

   @Override
   public E peek() {
      E[] buffer = this.buffer;
      long cIndex = this.lpConsumerIndex();
      long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(cIndex, this.mask);
      E e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
      if (null == e) {
         if (cIndex == this.lvProducerIndex()) {
            return null;
         }

         do {
            e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
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
      E[] buffer = this.buffer;
      long cIndex = this.lpConsumerIndex();
      long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(cIndex, this.mask);
      E e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
      if (null == e) {
         return null;
      } else {
         UnsafeRefArrayAccess.spRefElement(buffer, offset, null);
         this.soConsumerIndex(cIndex + 1L);
         return e;
      }
   }

   @Override
   public E relaxedPeek() {
      E[] buffer = this.buffer;
      long mask = this.mask;
      long cIndex = this.lpConsumerIndex();
      return UnsafeRefArrayAccess.lvRefElement(buffer, UnsafeRefArrayAccess.calcCircularRefElementOffset(cIndex, mask));
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
         E[] buffer = this.buffer;
         long mask = this.mask;
         long cIndex = this.lpConsumerIndex();

         for (int i = 0; i < limit; i++) {
            long index = cIndex + i;
            long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(index, mask);
            E e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
            if (null == e) {
               return i;
            }

            UnsafeRefArrayAccess.spRefElement(buffer, offset, null);
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
         long mask = this.mask;
         long capacity = mask + 1L;
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

         E[] buffer = this.buffer;

         for (int i = 0; i < actualLimit; i++) {
            long offset = UnsafeRefArrayAccess.calcCircularRefElementOffset(pIndex + i, mask);
            UnsafeRefArrayAccess.soRefElement(buffer, offset, s.get());
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
}
