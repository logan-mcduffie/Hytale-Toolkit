package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueue;
import io.netty.util.internal.shaded.org.jctools.queues.MessagePassingQueueUtil;
import io.netty.util.internal.shaded.org.jctools.util.RangeUtil;
import java.util.concurrent.atomic.AtomicLongArray;
import java.util.concurrent.atomic.AtomicReferenceArray;

public class MpmcAtomicArrayQueue<E> extends MpmcAtomicArrayQueueL3Pad<E> {
   public static final int MAX_LOOK_AHEAD_STEP = Integer.getInteger("jctools.mpmc.max.lookahead.step", 4096);
   private final int lookAheadStep = Math.max(2, Math.min(this.capacity() / 4, MAX_LOOK_AHEAD_STEP));

   public MpmcAtomicArrayQueue(int capacity) {
      super(RangeUtil.checkGreaterThanOrEqual(capacity, 2, "capacity"));
   }

   @Override
   public boolean offer(E e) {
      if (null == e) {
         throw new NullPointerException();
      } else {
         int mask = this.mask;
         long capacity = mask + 1;
         AtomicLongArray sBuffer = this.sequenceBuffer;
         long cIndex = Long.MIN_VALUE;

         long pIndex;
         int seqOffset;
         long seq;
         do {
            pIndex = this.lvProducerIndex();
            seqOffset = AtomicQueueUtil.calcCircularLongElementOffset(pIndex, mask);
            seq = AtomicQueueUtil.lvLongElement(sBuffer, seqOffset);
            if (seq < pIndex) {
               if (pIndex - capacity >= cIndex && pIndex - capacity >= (cIndex = this.lvConsumerIndex())) {
                  return false;
               }

               seq = pIndex + 1L;
            }
         } while (seq > pIndex || !this.casProducerIndex(pIndex, pIndex + 1L));

         AtomicQueueUtil.spRefElement(this.buffer, AtomicQueueUtil.calcCircularRefElementOffset(pIndex, mask), e);
         AtomicQueueUtil.soLongElement(sBuffer, seqOffset, pIndex + 1L);
         return true;
      }
   }

   @Override
   public E poll() {
      AtomicLongArray sBuffer = this.sequenceBuffer;
      int mask = this.mask;
      long pIndex = -1L;

      long cIndex;
      long seq;
      int seqOffset;
      long expectedSeq;
      do {
         cIndex = this.lvConsumerIndex();
         seqOffset = AtomicQueueUtil.calcCircularLongElementOffset(cIndex, mask);
         seq = AtomicQueueUtil.lvLongElement(sBuffer, seqOffset);
         expectedSeq = cIndex + 1L;
         if (seq < expectedSeq) {
            if (cIndex >= pIndex && cIndex == (pIndex = this.lvProducerIndex())) {
               return null;
            }

            seq = expectedSeq + 1L;
         }
      } while (seq > expectedSeq || !this.casConsumerIndex(cIndex, cIndex + 1L));

      int offset = AtomicQueueUtil.calcCircularRefElementOffset(cIndex, mask);
      E e = AtomicQueueUtil.lpRefElement(this.buffer, offset);
      AtomicQueueUtil.spRefElement(this.buffer, offset, null);
      AtomicQueueUtil.soLongElement(sBuffer, seqOffset, cIndex + mask + 1L);
      return e;
   }

   @Override
   public E peek() {
      AtomicLongArray sBuffer = this.sequenceBuffer;
      int mask = this.mask;
      long pIndex = -1L;

      while (true) {
         long cIndex = this.lvConsumerIndex();
         int seqOffset = AtomicQueueUtil.calcCircularLongElementOffset(cIndex, mask);
         long seq = AtomicQueueUtil.lvLongElement(sBuffer, seqOffset);
         long expectedSeq = cIndex + 1L;
         if (seq < expectedSeq) {
            if (cIndex >= pIndex && cIndex == (pIndex = this.lvProducerIndex())) {
               return null;
            }
         } else if (seq == expectedSeq) {
            int offset = AtomicQueueUtil.calcCircularRefElementOffset(cIndex, mask);
            E e = AtomicQueueUtil.lvRefElement(this.buffer, offset);
            if (this.lvConsumerIndex() == cIndex) {
               return e;
            }
         }
      }
   }

   @Override
   public boolean relaxedOffer(E e) {
      if (null == e) {
         throw new NullPointerException();
      } else {
         int mask = this.mask;
         AtomicLongArray sBuffer = this.sequenceBuffer;

         long pIndex;
         int seqOffset;
         long seq;
         do {
            pIndex = this.lvProducerIndex();
            seqOffset = AtomicQueueUtil.calcCircularLongElementOffset(pIndex, mask);
            seq = AtomicQueueUtil.lvLongElement(sBuffer, seqOffset);
            if (seq < pIndex) {
               return false;
            }
         } while (seq > pIndex || !this.casProducerIndex(pIndex, pIndex + 1L));

         AtomicQueueUtil.spRefElement(this.buffer, AtomicQueueUtil.calcCircularRefElementOffset(pIndex, mask), e);
         AtomicQueueUtil.soLongElement(sBuffer, seqOffset, pIndex + 1L);
         return true;
      }
   }

   @Override
   public E relaxedPoll() {
      AtomicLongArray sBuffer = this.sequenceBuffer;
      int mask = this.mask;

      long cIndex;
      int seqOffset;
      long seq;
      long expectedSeq;
      do {
         cIndex = this.lvConsumerIndex();
         seqOffset = AtomicQueueUtil.calcCircularLongElementOffset(cIndex, mask);
         seq = AtomicQueueUtil.lvLongElement(sBuffer, seqOffset);
         expectedSeq = cIndex + 1L;
         if (seq < expectedSeq) {
            return null;
         }
      } while (seq > expectedSeq || !this.casConsumerIndex(cIndex, cIndex + 1L));

      int offset = AtomicQueueUtil.calcCircularRefElementOffset(cIndex, mask);
      E e = AtomicQueueUtil.lpRefElement(this.buffer, offset);
      AtomicQueueUtil.spRefElement(this.buffer, offset, null);
      AtomicQueueUtil.soLongElement(sBuffer, seqOffset, cIndex + mask + 1L);
      return e;
   }

   @Override
   public E relaxedPeek() {
      AtomicLongArray sBuffer = this.sequenceBuffer;
      int mask = this.mask;

      while (true) {
         long cIndex = this.lvConsumerIndex();
         int seqOffset = AtomicQueueUtil.calcCircularLongElementOffset(cIndex, mask);
         long seq = AtomicQueueUtil.lvLongElement(sBuffer, seqOffset);
         long expectedSeq = cIndex + 1L;
         if (seq < expectedSeq) {
            return null;
         }

         if (seq == expectedSeq) {
            int offset = AtomicQueueUtil.calcCircularRefElementOffset(cIndex, mask);
            E e = AtomicQueueUtil.lvRefElement(this.buffer, offset);
            if (this.lvConsumerIndex() == cIndex) {
               return e;
            }
         }
      }
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
         AtomicLongArray sBuffer = this.sequenceBuffer;
         int mask = this.mask;
         AtomicReferenceArray<E> buffer = this.buffer;
         int maxLookAheadStep = Math.min(this.lookAheadStep, limit);
         int consumed = 0;

         while (consumed < limit) {
            int remaining = limit - consumed;
            int lookAheadStep = Math.min(remaining, maxLookAheadStep);
            long cIndex = this.lvConsumerIndex();
            long lookAheadIndex = cIndex + lookAheadStep - 1L;
            int lookAheadSeqOffset = AtomicQueueUtil.calcCircularLongElementOffset(lookAheadIndex, mask);
            long lookAheadSeq = AtomicQueueUtil.lvLongElement(sBuffer, lookAheadSeqOffset);
            long expectedLookAheadSeq = lookAheadIndex + 1L;
            if (lookAheadSeq != expectedLookAheadSeq || !this.casConsumerIndex(cIndex, expectedLookAheadSeq)) {
               return lookAheadSeq < expectedLookAheadSeq && this.notAvailable(cIndex, mask, sBuffer, cIndex + 1L)
                  ? consumed
                  : consumed + this.drainOneByOne(c, remaining);
            }

            for (int i = 0; i < lookAheadStep; i++) {
               long index = cIndex + i;
               int seqOffset = AtomicQueueUtil.calcCircularLongElementOffset(index, mask);
               int offset = AtomicQueueUtil.calcCircularRefElementOffset(index, mask);
               long expectedSeq = index + 1L;

               while (AtomicQueueUtil.lvLongElement(sBuffer, seqOffset) != expectedSeq) {
               }

               E e = AtomicQueueUtil.lpRefElement(buffer, offset);
               AtomicQueueUtil.spRefElement(buffer, offset, null);
               AtomicQueueUtil.soLongElement(sBuffer, seqOffset, index + mask + 1L);
               c.accept(e);
            }

            consumed += lookAheadStep;
         }

         return limit;
      }
   }

   private int drainOneByOne(MessagePassingQueue.Consumer<E> c, int limit) {
      AtomicLongArray sBuffer = this.sequenceBuffer;
      int mask = this.mask;
      AtomicReferenceArray<E> buffer = this.buffer;

      for (int i = 0; i < limit; i++) {
         long cIndex;
         int seqOffset;
         long seq;
         long expectedSeq;
         do {
            cIndex = this.lvConsumerIndex();
            seqOffset = AtomicQueueUtil.calcCircularLongElementOffset(cIndex, mask);
            seq = AtomicQueueUtil.lvLongElement(sBuffer, seqOffset);
            expectedSeq = cIndex + 1L;
            if (seq < expectedSeq) {
               return i;
            }
         } while (seq > expectedSeq || !this.casConsumerIndex(cIndex, cIndex + 1L));

         int offset = AtomicQueueUtil.calcCircularRefElementOffset(cIndex, mask);
         E e = AtomicQueueUtil.lpRefElement(buffer, offset);
         AtomicQueueUtil.spRefElement(buffer, offset, null);
         AtomicQueueUtil.soLongElement(sBuffer, seqOffset, cIndex + mask + 1L);
         c.accept(e);
      }

      return limit;
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
         AtomicLongArray sBuffer = this.sequenceBuffer;
         int mask = this.mask;
         AtomicReferenceArray<E> buffer = this.buffer;
         int maxLookAheadStep = Math.min(this.lookAheadStep, limit);
         int produced = 0;

         while (produced < limit) {
            int remaining = limit - produced;
            int lookAheadStep = Math.min(remaining, maxLookAheadStep);
            long pIndex = this.lvProducerIndex();
            long lookAheadIndex = pIndex + lookAheadStep - 1L;
            int lookAheadSeqOffset = AtomicQueueUtil.calcCircularLongElementOffset(lookAheadIndex, mask);
            long lookAheadSeq = AtomicQueueUtil.lvLongElement(sBuffer, lookAheadSeqOffset);
            if (lookAheadSeq != lookAheadIndex || !this.casProducerIndex(pIndex, lookAheadIndex + 1L)) {
               return lookAheadSeq < lookAheadIndex && this.notAvailable(pIndex, mask, sBuffer, pIndex) ? produced : produced + this.fillOneByOne(s, remaining);
            }

            for (int i = 0; i < lookAheadStep; i++) {
               long index = pIndex + i;
               int seqOffset = AtomicQueueUtil.calcCircularLongElementOffset(index, mask);
               int offset = AtomicQueueUtil.calcCircularRefElementOffset(index, mask);

               while (AtomicQueueUtil.lvLongElement(sBuffer, seqOffset) != index) {
               }

               AtomicQueueUtil.soRefElement(buffer, offset, s.get());
               AtomicQueueUtil.soLongElement(sBuffer, seqOffset, index + 1L);
            }

            produced += lookAheadStep;
         }

         return limit;
      }
   }

   private boolean notAvailable(long index, int mask, AtomicLongArray sBuffer, long expectedSeq) {
      int seqOffset = AtomicQueueUtil.calcCircularLongElementOffset(index, mask);
      long seq = AtomicQueueUtil.lvLongElement(sBuffer, seqOffset);
      return seq < expectedSeq;
   }

   private int fillOneByOne(MessagePassingQueue.Supplier<E> s, int limit) {
      AtomicLongArray sBuffer = this.sequenceBuffer;
      int mask = this.mask;
      AtomicReferenceArray<E> buffer = this.buffer;

      for (int i = 0; i < limit; i++) {
         long pIndex;
         int seqOffset;
         long seq;
         do {
            pIndex = this.lvProducerIndex();
            seqOffset = AtomicQueueUtil.calcCircularLongElementOffset(pIndex, mask);
            seq = AtomicQueueUtil.lvLongElement(sBuffer, seqOffset);
            if (seq < pIndex) {
               return i;
            }
         } while (seq > pIndex || !this.casProducerIndex(pIndex, pIndex + 1L));

         AtomicQueueUtil.soRefElement(buffer, AtomicQueueUtil.calcCircularRefElementOffset(pIndex, mask), s.get());
         AtomicQueueUtil.soLongElement(sBuffer, seqOffset, pIndex + 1L);
      }

      return limit;
   }

   @Override
   public int drain(MessagePassingQueue.Consumer<E> c) {
      return MessagePassingQueueUtil.drain(this, c);
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
