package io.netty.util.internal.shaded.org.jctools.queues.atomic.unpadded;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

abstract class MpscAtomicUnpaddedArrayQueueConsumerIndexField<E> extends MpscAtomicUnpaddedArrayQueueL2Pad<E> {
   private static final AtomicLongFieldUpdater<MpscAtomicUnpaddedArrayQueueConsumerIndexField> C_INDEX_UPDATER = AtomicLongFieldUpdater.newUpdater(
      MpscAtomicUnpaddedArrayQueueConsumerIndexField.class, "consumerIndex"
   );
   private volatile long consumerIndex;

   MpscAtomicUnpaddedArrayQueueConsumerIndexField(int capacity) {
      super(capacity);
   }

   @Override
   public final long lvConsumerIndex() {
      return this.consumerIndex;
   }

   final long lpConsumerIndex() {
      return this.consumerIndex;
   }

   final void soConsumerIndex(long newValue) {
      C_INDEX_UPDATER.lazySet(this, newValue);
   }
}
