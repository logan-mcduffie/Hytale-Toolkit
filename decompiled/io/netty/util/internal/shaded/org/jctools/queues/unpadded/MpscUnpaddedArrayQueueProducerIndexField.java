package io.netty.util.internal.shaded.org.jctools.queues.unpadded;

import io.netty.util.internal.shaded.org.jctools.util.UnsafeAccess;

abstract class MpscUnpaddedArrayQueueProducerIndexField<E> extends MpscUnpaddedArrayQueueL1Pad<E> {
   private static final long P_INDEX_OFFSET = UnsafeAccess.fieldOffset(MpscUnpaddedArrayQueueProducerIndexField.class, "producerIndex");
   private volatile long producerIndex;

   MpscUnpaddedArrayQueueProducerIndexField(int capacity) {
      super(capacity);
   }

   @Override
   public final long lvProducerIndex() {
      return this.producerIndex;
   }

   final boolean casProducerIndex(long expect, long newValue) {
      return UnsafeAccess.UNSAFE.compareAndSwapLong(this, P_INDEX_OFFSET, expect, newValue);
   }
}
