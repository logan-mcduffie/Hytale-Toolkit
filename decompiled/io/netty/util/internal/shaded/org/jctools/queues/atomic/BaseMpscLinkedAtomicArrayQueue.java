package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.IndexedQueueSizeUtil;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueueUtil;
import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import io.netty.util.internal.shaded.org.jctools.util.PortableJvmInfo;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReferenceArray;

abstract class BaseMpscLinkedAtomicArrayQueue<E>
   extends BaseMpscLinkedAtomicArrayQueueColdProducerFields<E>
   implements MessagePassingQueue<E>,
   QueueProgressIndicators {
   private static final Object JUMP = new Object();
   private static final Object BUFFER_CONSUMED = new Object();
   private static final int CONTINUE_TO_P_INDEX_CAS = 0;
   private static final int RETRY = 1;
   private static final int QUEUE_FULL = 2;
   private static final int QUEUE_RESIZE = 3;

   public BaseMpscLinkedAtomicArrayQueue(int initialCapacity) {
      RangeUtil.checkGreaterThanOrEqual(initialCapacity, 2, "initialCapacity");
      int p2capacity = Pow2.roundToPowerOfTwo(initialCapacity);
      long mask = p2capacity - 1 << 1;
      AtomicReferenceArray<E> buffer = AtomicQueueUtil.allocateRefArray(p2capacity + 1);
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
               AtomicReferenceArray<E> buffer = this.producerBuffer;
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
                  int offset = AtomicQueueUtil.modifiedCalcCircularRefElementOffset(pIndex, mask);
                  AtomicQueueUtil.soRefElement(buffer, offset, e);
                  return true;
               }
            }
         }
      }
   }

   @Override
   public E poll() {
      AtomicReferenceArray<E> buffer = this.consumerBuffer;
      long cIndex = this.lpConsumerIndex();
      long mask = this.consumerMask;
      int offset = AtomicQueueUtil.modifiedCalcCircularRefElementOffset(cIndex, mask);
      Object e = AtomicQueueUtil.lvRefElement(buffer, offset);
      if (e == null) {
         long pIndex = this.lvProducerIndex();
         if ((cIndex - pIndex) / 2L == 0L) {
            return null;
         }

         do {
            e = AtomicQueueUtil.lvRefElement(buffer, offset);
         } while (e == null);
      }

      if (e == JUMP) {
         AtomicReferenceArray<E> nextBuffer = this.nextBuffer(buffer, mask);
         return this.newBufferPoll(nextBuffer, cIndex);
      } else {
         AtomicQueueUtil.soRefElement(buffer, offset, null);
         this.soConsumerIndex(cIndex + 2L);
         return (E)e;
      }
   }

   @Override
   public E peek() {
      AtomicReferenceArray<E> buffer = this.consumerBuffer;
      long cIndex = this.lpConsumerIndex();
      long mask = this.consumerMask;
      int offset = AtomicQueueUtil.modifiedCalcCircularRefElementOffset(cIndex, mask);
      Object e = AtomicQueueUtil.lvRefElement(buffer, offset);
      if (e == null) {
         long pIndex = this.lvProducerIndex();
         if ((cIndex - pIndex) / 2L == 0L) {
            return null;
         }

         do {
            e = AtomicQueueUtil.lvRefElement(buffer, offset);
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

   private AtomicReferenceArray<E> nextBuffer(AtomicReferenceArray<E> buffer, long mask) {
      int offset = nextArrayOffset(mask);
      AtomicReferenceArray<E> nextBuffer = (AtomicReferenceArray<E>)AtomicQueueUtil.<E>lvRefElement(buffer, offset);
      this.consumerBuffer = nextBuffer;
      this.consumerMask = AtomicQueueUtil.length(nextBuffer) - 2 << 1;
      AtomicQueueUtil.soRefElement(buffer, offset, BUFFER_CONSUMED);
      return nextBuffer;
   }

   private static int nextArrayOffset(long mask) {
      return AtomicQueueUtil.modifiedCalcCircularRefElementOffset(mask + 2L, Long.MAX_VALUE);
   }

   private E newBufferPoll(AtomicReferenceArray<E> nextBuffer, long cIndex) {
      int offset = AtomicQueueUtil.modifiedCalcCircularRefElementOffset(cIndex, this.consumerMask);
      E n = AtomicQueueUtil.lvRefElement(nextBuffer, offset);
      if (n == null) {
         throw new IllegalStateException("new buffer must have at least one element");
      } else {
         AtomicQueueUtil.soRefElement(nextBuffer, offset, null);
         this.soConsumerIndex(cIndex + 2L);
         return n;
      }
   }

   private E newBufferPeek(AtomicReferenceArray<E> nextBuffer, long cIndex) {
      int offset = AtomicQueueUtil.modifiedCalcCircularRefElementOffset(cIndex, this.consumerMask);
      E n = AtomicQueueUtil.lvRefElement(nextBuffer, offset);
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
      AtomicReferenceArray<E> buffer = this.consumerBuffer;
      long cIndex = this.lpConsumerIndex();
      long mask = this.consumerMask;
      int offset = AtomicQueueUtil.modifiedCalcCircularRefElementOffset(cIndex, mask);
      Object e = AtomicQueueUtil.lvRefElement(buffer, offset);
      if (e == null) {
         return null;
      } else if (e == JUMP) {
         AtomicReferenceArray<E> nextBuffer = this.nextBuffer(buffer, mask);
         return this.newBufferPoll(nextBuffer, cIndex);
      } else {
         AtomicQueueUtil.soRefElement(buffer, offset, null);
         this.soConsumerIndex(cIndex + 2L);
         return (E)e;
      }
   }

   @Override
   public E relaxedPeek() {
      AtomicReferenceArray<E> buffer = this.consumerBuffer;
      long cIndex = this.lpConsumerIndex();
      long mask = this.consumerMask;
      int offset = AtomicQueueUtil.modifiedCalcCircularRefElementOffset(cIndex, mask);
      Object e = AtomicQueueUtil.lvRefElement(buffer, offset);
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
               AtomicReferenceArray<E> buffer = this.producerBuffer;
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
                     int offset = AtomicQueueUtil.modifiedCalcCircularRefElementOffset(pIndex + 2L * var14, mask);
                     AtomicQueueUtil.soRefElement(buffer, offset, s.get());
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
      return new BaseMpscLinkedAtomicArrayQueue.WeakIterator<>(this.consumerBuffer, this.lvConsumerIndex(), this.lvProducerIndex());
   }

   private void resize(long oldMask, AtomicReferenceArray<E> oldBuffer, long pIndex, E e, MessagePassingQueue.Supplier<E> s) {
      assert e != null && s == null || e == null || s != null;

      int newBufferLength = this.getNextBufferSize(oldBuffer);

      AtomicReferenceArray<E> newBuffer;
      try {
         newBuffer = AtomicQueueUtil.allocateRefArray(newBufferLength);
      } catch (OutOfMemoryError var17) {
         assert this.lvProducerIndex() == pIndex + 1L;

         this.soProducerIndex(pIndex);
         throw var17;
      }

      this.producerBuffer = newBuffer;
      int newMask = newBufferLength - 2 << 1;
      this.producerMask = newMask;
      int offsetInOld = AtomicQueueUtil.modifiedCalcCircularRefElementOffset(pIndex, oldMask);
      int offsetInNew = AtomicQueueUtil.modifiedCalcCircularRefElementOffset(pIndex, newMask);
      AtomicQueueUtil.soRefElement(newBuffer, offsetInNew, e == null ? s.get() : e);
      AtomicQueueUtil.soRefElement(oldBuffer, nextArrayOffset(oldMask), newBuffer);
      long cIndex = this.lvConsumerIndex();
      long availableInQueue = this.availableInQueue(pIndex, cIndex);
      RangeUtil.checkPositive(availableInQueue, "availableInQueue");
      this.soProducerLimit(pIndex + Math.min((long)newMask, availableInQueue));
      this.soProducerIndex(pIndex + 2L);
      AtomicQueueUtil.soRefElement(oldBuffer, offsetInOld, JUMP);
   }

   protected abstract int getNextBufferSize(AtomicReferenceArray<E> var1);

   protected abstract long getCurrentBufferCapacity(long var1);

   private static class WeakIterator<E> implements Iterator<E> {
      private final long pIndex;
      private long nextIndex;
      private E nextElement;
      private AtomicReferenceArray<E> currentBuffer;
      private int mask;

      WeakIterator(AtomicReferenceArray<E> currentBuffer, long cIndex, long pIndex) {
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

      private void setBuffer(AtomicReferenceArray<E> buffer) {
         this.currentBuffer = buffer;
         this.mask = AtomicQueueUtil.length(buffer) - 2;
      }

      private E getNext() {
         while (this.nextIndex < this.pIndex) {
            long index = this.nextIndex++;
            E e = AtomicQueueUtil.lvRefElement(this.currentBuffer, AtomicQueueUtil.calcCircularRefElementOffset(index, this.mask));
            if (e != null) {
               if (e != BaseMpscLinkedAtomicArrayQueue.JUMP) {
                  return e;
               }

               int nextBufferIndex = this.mask + 1;
               Object nextBuffer = AtomicQueueUtil.lvRefElement(this.currentBuffer, AtomicQueueUtil.calcRefElementOffset(nextBufferIndex));
               if (nextBuffer != BaseMpscLinkedAtomicArrayQueue.BUFFER_CONSUMED && nextBuffer != null) {
                  this.setBuffer((AtomicReferenceArray<E>)nextBuffer);
                  e = AtomicQueueUtil.lvRefElement(this.currentBuffer, AtomicQueueUtil.calcCircularRefElementOffset(index, this.mask));
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
