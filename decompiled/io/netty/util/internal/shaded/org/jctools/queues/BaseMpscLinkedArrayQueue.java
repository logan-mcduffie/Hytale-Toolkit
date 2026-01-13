package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;
import io.netty.util.internal.shaded.org.jctools.util.UnsafeRefArrayAccess;
import java.util.Iterator;
import java.util.NoSuchElementException;

abstract class BaseMpscLinkedArrayQueue<E> extends BaseMpscLinkedArrayQueueColdProducerFields<E> implements MessagePassingQueue<E>, QueueProgressIndicators {
   private static final Object JUMP = new Object();
   private static final Object BUFFER_CONSUMED = new Object();
   private static final int CONTINUE_TO_P_INDEX_CAS = 0;
   private static final int RETRY = 1;
   private static final int QUEUE_FULL = 2;
   private static final int QUEUE_RESIZE = 3;

   public BaseMpscLinkedArrayQueue(int initialCapacity) {
      RangeUtil.checkGreaterThanOrEqual(initialCapacity, 2, "initialCapacity");
      int p2capacity = Pow2.roundToPowerOfTwo(initialCapacity);
      long mask = p2capacity - 1 << 1;
      E[] buffer = (E[])UnsafeRefArrayAccess.allocateRefArray(p2capacity + 1);
      this.producerBuffer = buffer;
      this.producerMask = mask;
      this.consumerBuffer = buffer;
      this.consumerMask = mask;
      this.soProducerLimit(mask);
   }

   @Override
   public int size() {
      return IndexedQueueSizeUtil.size(this, 2);
   }

   @Override
   public boolean isEmpty() {
      return (this.lvConsumerIndex() - this.lvProducerIndex()) / 2L == 0L;
   }

   @Override
   public String toString() {
      return this.getClass().getName();
   }

   @Override
   public boolean offer(E e) {
      if (null == e) {
         throw new NullPointerException();
      } else {
         while (true) {
            long producerLimit = this.lvProducerLimit();
            long pIndex = this.lvProducerIndex();
            if ((pIndex & 1L) != 1L) {
               long mask = this.producerMask;
               E[] buffer = this.producerBuffer;
               if (producerLimit <= pIndex) {
                  int result = this.offerSlowPath(mask, pIndex, producerLimit);
                  switch (result) {
                     case 0:
                     default:
                        break;
                     case 1:
                        continue;
                     case 2:
                        return false;
                     case 3:
                        this.resize(mask, buffer, pIndex, e, null);
                        return true;
                  }
               }

               if (this.casProducerIndex(pIndex, pIndex + 2L)) {
                  producerLimit = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(pIndex, mask);
                  UnsafeRefArrayAccess.soRefElement(buffer, producerLimit, e);
                  return true;
               }
            }
         }
      }
   }

   @Override
   public E poll() {
      E[] buffer = this.consumerBuffer;
      long cIndex = this.lpConsumerIndex();
      long mask = this.consumerMask;
      long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(cIndex, mask);
      Object e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
      if (e == null) {
         long pIndex = this.lvProducerIndex();
         if ((cIndex - pIndex) / 2L == 0L) {
            return null;
         }

         do {
            e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
         } while (e == null);
      }

      if (e == JUMP) {
         E[] nextBuffer = this.nextBuffer(buffer, mask);
         return this.newBufferPoll(nextBuffer, cIndex);
      } else {
         UnsafeRefArrayAccess.soRefElement(buffer, offset, null);
         this.soConsumerIndex(cIndex + 2L);
         return (E)e;
      }
   }

   @Override
   public E peek() {
      E[] buffer = this.consumerBuffer;
      long cIndex = this.lpConsumerIndex();
      long mask = this.consumerMask;
      long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(cIndex, mask);
      Object e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
      if (e == null) {
         long pIndex = this.lvProducerIndex();
         if ((cIndex - pIndex) / 2L == 0L) {
            return null;
         }

         do {
            e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
         } while (e == null);
      }

      return (E)(e == JUMP ? this.newBufferPeek(this.nextBuffer(buffer, mask), cIndex) : e);
   }

   private int offerSlowPath(long mask, long pIndex, long producerLimit) {
      long cIndex = this.lvConsumerIndex();
      long bufferCapacity = this.getCurrentBufferCapacity(mask);
      if (cIndex + bufferCapacity > pIndex) {
         return !this.casProducerLimit(producerLimit, cIndex + bufferCapacity) ? 1 : 0;
      } else if (this.availableInQueue(pIndex, cIndex) <= 0L) {
         return 2;
      } else {
         return this.casProducerIndex(pIndex, pIndex + 1L) ? 3 : 1;
      }
   }

   protected abstract long availableInQueue(long var1, long var3);

   private E[] nextBuffer(E[] buffer, long mask) {
      long offset = nextArrayOffset(mask);
      E[] nextBuffer = (E[])((Object[])UnsafeRefArrayAccess.lvRefElement(buffer, offset));
      this.consumerBuffer = nextBuffer;
      this.consumerMask = LinkedArrayQueueUtil.length(nextBuffer) - 2 << 1;
      UnsafeRefArrayAccess.soRefElement(buffer, offset, BUFFER_CONSUMED);
      return nextBuffer;
   }

   private static long nextArrayOffset(long mask) {
      return LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(mask + 2L, Long.MAX_VALUE);
   }

   private E newBufferPoll(E[] nextBuffer, long cIndex) {
      long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(cIndex, this.consumerMask);
      E n = UnsafeRefArrayAccess.lvRefElement(nextBuffer, offset);
      if (n == null) {
         throw new IllegalStateException("new buffer must have at least one element");
      } else {
         UnsafeRefArrayAccess.soRefElement(nextBuffer, offset, null);
         this.soConsumerIndex(cIndex + 2L);
         return n;
      }
   }

   private E newBufferPeek(E[] nextBuffer, long cIndex) {
      long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(cIndex, this.consumerMask);
      E n = UnsafeRefArrayAccess.lvRefElement(nextBuffer, offset);
      if (null == n) {
         throw new IllegalStateException("new buffer must have at least one element");
      } else {
         return n;
      }
   }

   @Override
   public long currentProducerIndex() {
      return this.lvProducerIndex() / 2L;
   }

   @Override
   public long currentConsumerIndex() {
      return this.lvConsumerIndex() / 2L;
   }

   @Override
   public abstract int capacity();

   @Override
   public boolean relaxedOffer(E e) {
      return this.offer(e);
   }

   @Override
   public E relaxedPoll() {
      E[] buffer = this.consumerBuffer;
      long cIndex = this.lpConsumerIndex();
      long mask = this.consumerMask;
      long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(cIndex, mask);
      Object e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
      if (e == null) {
         return null;
      } else if (e == JUMP) {
         E[] nextBuffer = this.nextBuffer(buffer, mask);
         return this.newBufferPoll(nextBuffer, cIndex);
      } else {
         UnsafeRefArrayAccess.soRefElement(buffer, offset, null);
         this.soConsumerIndex(cIndex + 2L);
         return (E)e;
      }
   }

   @Override
   public E relaxedPeek() {
      E[] buffer = this.consumerBuffer;
      long cIndex = this.lpConsumerIndex();
      long mask = this.consumerMask;
      long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(cIndex, mask);
      Object e = UnsafeRefArrayAccess.lvRefElement(buffer, offset);
      return (E)(e == JUMP ? this.newBufferPeek(this.nextBuffer(buffer, mask), cIndex) : e);
   }

   @Override
   public int fill(MessagePassingQueue.Supplier<E> s) {
      long result = 0L;
      int capacity = this.capacity();

      do {
         int filled = this.fill(s, PortableJvmInfo.RECOMENDED_OFFER_BATCH);
         if (filled == 0) {
            return (int)result;
         }

         result += filled;
      } while (result <= capacity);

      return (int)result;
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
         while (true) {
            long producerLimit = this.lvProducerLimit();
            long pIndex = this.lvProducerIndex();
            if ((pIndex & 1L) != 1L) {
               long mask = this.producerMask;
               E[] buffer = this.producerBuffer;
               long batchIndex = Math.min(producerLimit, pIndex + 2L * limit);
               if (pIndex >= producerLimit) {
                  int result = this.offerSlowPath(mask, pIndex, producerLimit);
                  switch (result) {
                     case 0:
                     case 1:
                        continue;
                     case 2:
                        return 0;
                     case 3:
                        this.resize(mask, buffer, pIndex, null, s);
                        return 1;
                  }
               }

               if (this.casProducerIndex(pIndex, batchIndex)) {
                  int claimedSlots = (int)((batchIndex - pIndex) / 2L);

                  for (int var14 = 0; var14 < claimedSlots; var14++) {
                     long offset = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(pIndex + 2L * var14, mask);
                     UnsafeRefArrayAccess.soRefElement(buffer, offset, s.get());
                  }

                  return claimedSlots;
               }
            }
         }
      }
   }

   @Override
   public void fill(MessagePassingQueue.Supplier<E> s, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
      MessagePassingQueueUtil.fill(this, s, wait, exit);
   }

   @Override
   public int drain(MessagePassingQueue.Consumer<E> c) {
      return this.drain(c, this.capacity());
   }

   @Override
   public int drain(MessagePassingQueue.Consumer<E> c, int limit) {
      return MessagePassingQueueUtil.drain(this, c, limit);
   }

   @Override
   public void drain(MessagePassingQueue.Consumer<E> c, MessagePassingQueue.WaitStrategy wait, MessagePassingQueue.ExitCondition exit) {
      MessagePassingQueueUtil.drain(this, c, wait, exit);
   }

   @Override
   public Iterator<E> iterator() {
      return new BaseMpscLinkedArrayQueue.WeakIterator<>(this.consumerBuffer, this.lvConsumerIndex(), this.lvProducerIndex());
   }

   private void resize(long oldMask, E[] oldBuffer, long pIndex, E e, MessagePassingQueue.Supplier<E> s) {
      assert e != null && s == null || e == null || s != null;

      int newBufferLength = this.getNextBufferSize(oldBuffer);

      E[] newBuffer;
      try {
         newBuffer = (E[])UnsafeRefArrayAccess.allocateRefArray(newBufferLength);
      } catch (OutOfMemoryError var19) {
         assert this.lvProducerIndex() == pIndex + 1L;

         this.soProducerIndex(pIndex);
         throw var19;
      }

      this.producerBuffer = newBuffer;
      int newMask = newBufferLength - 2 << 1;
      this.producerMask = newMask;
      long offsetInOld = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(pIndex, oldMask);
      long offsetInNew = LinkedArrayQueueUtil.modifiedCalcCircularRefElementOffset(pIndex, newMask);
      UnsafeRefArrayAccess.soRefElement(newBuffer, offsetInNew, e == null ? s.get() : e);
      UnsafeRefArrayAccess.soRefElement(oldBuffer, nextArrayOffset(oldMask), (E)newBuffer);
      long cIndex = this.lvConsumerIndex();
      long availableInQueue = this.availableInQueue(pIndex, cIndex);
      RangeUtil.checkPositive(availableInQueue, "availableInQueue");
      this.soProducerLimit(pIndex + Math.min((long)newMask, availableInQueue));
      this.soProducerIndex(pIndex + 2L);
      UnsafeRefArrayAccess.soRefElement(oldBuffer, offsetInOld, JUMP);
   }

   protected abstract int getNextBufferSize(E[] var1);

   protected abstract long getCurrentBufferCapacity(long var1);

   private static class WeakIterator<E> implements Iterator<E> {
      private final long pIndex;
      private long nextIndex;
      private E nextElement;
      private E[] currentBuffer;
      private int mask;

      WeakIterator(E[] currentBuffer, long cIndex, long pIndex) {
         this.pIndex = pIndex >> 1;
         this.nextIndex = cIndex >> 1;
         this.setBuffer(currentBuffer);
         this.nextElement = this.getNext();
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException("remove");
      }

      @Override
      public boolean hasNext() {
         return this.nextElement != null;
      }

      @Override
      public E next() {
         E e = this.nextElement;
         if (e == null) {
            throw new NoSuchElementException();
         } else {
            this.nextElement = this.getNext();
            return e;
         }
      }

      private void setBuffer(E[] buffer) {
         this.currentBuffer = buffer;
         this.mask = LinkedArrayQueueUtil.length(buffer) - 2;
      }

      private E getNext() {
         while (this.nextIndex < this.pIndex) {
            long index = this.nextIndex++;
            E e = UnsafeRefArrayAccess.lvRefElement(this.currentBuffer, UnsafeRefArrayAccess.calcCircularRefElementOffset(index, this.mask));
            if (e != null) {
               if (e != BaseMpscLinkedArrayQueue.JUMP) {
                  return e;
               }

               int nextBufferIndex = this.mask + 1;
               Object nextBuffer = UnsafeRefArrayAccess.lvRefElement(this.currentBuffer, UnsafeRefArrayAccess.calcRefElementOffset(nextBufferIndex));
               if (nextBuffer != BaseMpscLinkedArrayQueue.BUFFER_CONSUMED && nextBuffer != null) {
                  this.setBuffer((E[])((Object[])nextBuffer));
                  e = UnsafeRefArrayAccess.lvRefElement(this.currentBuffer, UnsafeRefArrayAccess.calcCircularRefElementOffset(index, this.mask));
                  if (e == null) {
                     continue;
                  }

                  return e;
               }

               return null;
            }
         }

         return null;
      }
   }
}
