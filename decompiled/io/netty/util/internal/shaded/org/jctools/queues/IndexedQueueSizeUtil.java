package io.netty.util.internal.shaded.org.jctools.queues;

public final class IndexedQueueSizeUtil {
   public static final int PLAIN_DIVISOR = 1;
   public static final int IGNORE_PARITY_DIVISOR = 2;

   public static int size(IndexedQueueSizeUtil.IndexedQueue iq, int divisor) {
      long after = iq.lvConsumerIndex();

      long before;
      long currentProducerIndex;
      do {
         before = after;
         currentProducerIndex = iq.lvProducerIndex();
         after = iq.lvConsumerIndex();
      } while (before != after);

      long size = (currentProducerIndex - after) / divisor;
      return sanitizedSize(iq.capacity(), size);
   }

   public static int sanitizedSize(int capacity, long size) {
      if (size < 0L) {
         return 0;
      } else if (capacity != -1 && size > capacity) {
         return capacity;
      } else {
         return size > 2147483647L ? Integer.MAX_VALUE : (int)size;
      }
   }

   public static boolean isEmpty(IndexedQueueSizeUtil.IndexedQueue iq) {
      return iq.lvConsumerIndex() >= iq.lvProducerIndex();
   }

   public interface IndexedQueue {
      long lvConsumerIndex();

      long lvProducerIndex();

      int capacity();
   }
}
