package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.IndexedQueueSizeUtil;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.QueueProgressIndicators;
import io.netty.util.internal.shaded.org.jctools.queues.SupportsIterator;
import io.netty.util.internal.shaded.org.jctools.util.Pow2;
import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.atomic.AtomicReferenceArray;

public abstract class AtomicReferenceArrayQueue<E>
   extends AbstractQueue<E>
   implements IndexedQueueSizeUtil.IndexedQueue,
   QueueProgressIndicators,
   MessagePassingQueue<E>,
   SupportsIterator {
   protected final AtomicReferenceArray<E> buffer;
   protected final int mask;

   public AtomicReferenceArrayQueue(int capacity) {
      int actualCapacity = Pow2.roundToPowerOfTwo(capacity);
      this.mask = actualCapacity - 1;
      this.buffer = new AtomicReferenceArray<>(actualCapacity);
   }

   @Override
   public String toString() {
      return this.getClass().getName();
   }

   @Override
   public void clear() {
      while (this.poll() != null) {
      }
   }

   @Override
   public final int capacity() {
      return this.mask + 1;
   }

   @Override
   public final int size() {
      return IndexedQueueSizeUtil.size(this, 1);
   }

   @Override
   public final boolean isEmpty() {
      return IndexedQueueSizeUtil.isEmpty(this);
   }

   @Override
   public final long currentProducerIndex() {
      return this.lvProducerIndex();
   }

   @Override
   public final long currentConsumerIndex() {
      return this.lvConsumerIndex();
   }

   @Override
   public final Iterator<E> iterator() {
      long cIndex = this.lvConsumerIndex();
      long pIndex = this.lvProducerIndex();
      return new AtomicReferenceArrayQueue.WeakIterator<>(cIndex, pIndex, this.mask, this.buffer);
   }

   private static class WeakIterator<E> implements Iterator<E> {
      private final long pIndex;
      private final int mask;
      private final AtomicReferenceArray<E> buffer;
      private long nextIndex;
      private E nextElement;

      WeakIterator(long cIndex, long pIndex, int mask, AtomicReferenceArray<E> buffer) {
         this.nextIndex = cIndex;
         this.pIndex = pIndex;
         this.mask = mask;
         this.buffer = buffer;
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

      private E getNext() {
         int mask = this.mask;
         AtomicReferenceArray<E> buffer = this.buffer;

         while (this.nextIndex < this.pIndex) {
            int offset = AtomicQueueUtil.calcCircularRefElementOffset(this.nextIndex++, mask);
            E e = AtomicQueueUtil.lvRefElement(buffer, offset);
            if (e != null) {
               return e;
            }
         }

         return null;
      }
   }
}
