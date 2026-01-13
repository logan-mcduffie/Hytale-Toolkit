package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.PriorityQueue;

public interface ShortPriorityQueue extends PriorityQueue<Short> {
   void enqueue(short var1);

   short dequeueShort();

   short firstShort();

   default short lastShort() {
      throw new UnsupportedOperationException();
   }

   ShortComparator comparator();

   @Deprecated
   default void enqueue(Short x) {
      this.enqueue(x.shortValue());
   }

   @Deprecated
   default Short dequeue() {
      return this.dequeueShort();
   }

   @Deprecated
   default Short first() {
      return this.firstShort();
   }

   @Deprecated
   default Short last() {
      return this.lastShort();
   }
}
