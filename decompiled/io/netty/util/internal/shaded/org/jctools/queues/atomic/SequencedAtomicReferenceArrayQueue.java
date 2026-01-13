package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicLongArray;

public abstract class SequencedAtomicReferenceArrayQueue<E> extends AtomicReferenceArrayQueue<E> {
   protected final AtomicLongArray sequenceBuffer;

   public SequencedAtomicReferenceArrayQueue(int capacity) {
      super(capacity);
      int actualCapacity = this.mask + 1;
      this.sequenceBuffer = new AtomicLongArray(actualCapacity);

      for (int i = 0; i < actualCapacity; i++) {
         this.soSequence(this.sequenceBuffer, i, i);
      }
   }

   protected final long calcSequenceOffset(long index) {
      return calcSequenceOffset(index, this.mask);
   }

   protected static int calcSequenceOffset(long index, int mask) {
      return (int)index & mask;
   }

   protected final void soSequence(AtomicLongArray buffer, int offset, long e) {
      buffer.lazySet(offset, e);
   }

   protected final long lvSequence(AtomicLongArray buffer, int offset) {
      return buffer.get(offset);
   }
}
