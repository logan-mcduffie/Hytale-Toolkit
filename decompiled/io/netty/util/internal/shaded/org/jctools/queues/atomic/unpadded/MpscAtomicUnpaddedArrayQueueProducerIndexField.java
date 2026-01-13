package io.netty.util.internal.shaded.org.jctools.queues.atomic.unpadded;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class MpscAtomicUnpaddedArrayQueueProducerIndexField<E> extends MpscAtomicUnpaddedArrayQueueL1Pad<E> {
   private static final AtomicLongFieldUpdater<MpscAtomicUnpaddedArrayQueueProducerIndexField> P_INDEX_UPDATER = AtomicLongFieldUpdater.newUpdater(
      MpscAtomicUnpaddedArrayQueueProducerIndexField.class, "producerIndex"
   );
   private volatile long producerIndex;

   MpscAtomicUnpaddedArrayQueueProducerIndexField(int capacity) {
      super(capacity);
   }

   @Override
   public final long lvProducerIndex() {
      return this.producerIndex;
   }

   final boolean casProducerIndex(long expect, long newValue) {
      return P_INDEX_UPDATER.compareAndSet(this, expect, newValue);
   }
}
