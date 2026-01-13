package io.netty.util.internal.shaded.org.jctools.queues;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeLongArrayAccess;

public abstract class ConcurrentSequencedCircularArrayQueue<E> extends ConcurrentCircularArrayQueue<E> {
   protected final long[] sequenceBuffer;

   public ConcurrentSequencedCircularArrayQueue(int capacity) {
      super(capacity);
      int actualCapacity = (int)(this.mask + 1L);
      this.sequenceBuffer = UnsafeLongArrayAccess.allocateLongArray(actualCapacity);

      for (long i = 0L; i < actualCapacity; i++) {
         UnsafeLongArrayAccess.soLongElement(this.sequenceBuffer, UnsafeLongArrayAccess.calcCircularLongElementOffset(i, this.mask), i);
      }
   }
}
