package io.netty.util.internal.shaded.org.jctools.queues.atomic;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class MpscAtomicArrayQueueProducerLimitField<E> extends MpscAtomicArrayQueueMidPad<E> {
   private static final AtomicLongFieldUpdater<MpscAtomicArrayQueueProducerLimitField> P_LIMIT_UPDATER = AtomicLongFieldUpdater.newUpdater(
      MpscAtomicArrayQueueProducerLimitField.class, "producerLimit"
   );
   private volatile long producerLimit;

   MpscAtomicArrayQueueProducerLimitField(int capacity) {
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
