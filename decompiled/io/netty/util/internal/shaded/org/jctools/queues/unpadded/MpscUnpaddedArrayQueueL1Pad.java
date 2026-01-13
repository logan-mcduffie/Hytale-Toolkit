package io.netty.util.internal.shaded.org.jctools.queues.unpadded;

abstract class MpscUnpaddedArrayQueueL1Pad<E> extends ConcurrentCircularUnpaddedArrayQueue<E> {
   MpscUnpaddedArrayQueueL1Pad(int capacity) {
      super(capacity);
   }
}
