package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.PriorityQueue;

public interface LongPriorityQueue extends PriorityQueue<Long> {
   void enqueue(long var1);

   long dequeueLong();

   long firstLong();

   default long lastLong() {
      throw new UnsupportedOperationException();
   }

   LongComparator comparator();

   @Deprecated
   default void enqueue(Long x) {
      this.enqueue(x.longValue());
   }

   @Deprecated
   default Long dequeue() {
      return this.dequeueLong();
   }

   @Deprecated
   default Long first() {
      return this.firstLong();
   }

   @Deprecated
   default Long last() {
      return this.lastLong();
   }
}
