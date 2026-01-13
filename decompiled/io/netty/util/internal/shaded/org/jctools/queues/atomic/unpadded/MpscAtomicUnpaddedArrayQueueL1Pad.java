package io.netty.util.internal.shaded.org.jctools.queues.atomic.unpadded;

import io.netty.util.internal.shaded.org.jctools.queues.atomic.AtomicReferenceArrayQueue;

abstract class MpscAtomicUnpaddedArrayQueueL1Pad<E> extends AtomicReferenceArrayQueue<E> {
   MpscAtomicUnpaddedArrayQueueL1Pad(int capacity) {
      super(capacity);
   }
}
