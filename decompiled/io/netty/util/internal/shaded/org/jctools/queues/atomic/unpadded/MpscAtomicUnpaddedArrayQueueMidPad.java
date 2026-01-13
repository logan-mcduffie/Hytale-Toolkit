package io.netty.util.internal.shaded.org.jctools.queues.atomic.unpadded;

abstract class MpscAtomicUnpaddedArrayQueueMidPad<E> extends MpscAtomicUnpaddedArrayQueueProducerIndexField<E> {
   MpscAtomicUnpaddedArrayQueueMidPad(int capacity) {
      super(capacity);
   }
}
