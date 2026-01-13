package io.netty.util.internal.shaded.org.jctools.queues.atomic.unpadded;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class MpscAtomicUnpaddedArrayQueueProducerLimitField<E> extends MpscAtomicUnpaddedArrayQueueMidPad<E> {
   private static final AtomicLongFieldUpdater<MpscAtomicUnpaddedArrayQueueProducerLimitField> P_LIMIT_UPDATER = AtomicLongFieldUpdater.newUpdater(
      MpscAtomicUnpaddedArrayQueueProducerLimitField.class, "producerLimit"
   );
   private volatile long producerLimit;

   MpscAtomicUnpaddedArrayQueueProducerLimitField(int capacity) {
      super(capacity);
      this.producerLimit = capacity;
   }

   final long lvProducerLimit() {
      return this.producerLimit;
   }

   final void soProducerLimit(long newValue) {
      P_LIMIT_UPDATER.lazySet(this, newValue);
   }
}
