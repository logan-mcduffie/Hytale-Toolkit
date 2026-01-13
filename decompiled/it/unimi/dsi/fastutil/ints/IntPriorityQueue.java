package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.PriorityQueue;

public interface IntPriorityQueue extends PriorityQueue<Integer> {
   void enqueue(int var1);

   int dequeueInt();

   int firstInt();

   default int lastInt() {
      throw new UnsupportedOperationException();
   }

   IntComparator comparator();

   @Deprecated
   default void enqueue(Integer x) {
      this.enqueue(x.intValue());
   }

   @Deprecated
   default Integer dequeue() {
      return this.dequeueInt();
   }

   @Deprecated
   default Integer first() {
      return this.firstInt();
   }

   @Deprecated
   default Integer last() {
      return this.lastInt();
   }
}
