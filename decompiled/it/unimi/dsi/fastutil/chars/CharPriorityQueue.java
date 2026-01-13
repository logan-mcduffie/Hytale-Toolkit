package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.PriorityQueue;

public interface CharPriorityQueue extends PriorityQueue<Character> {
   void enqueue(char var1);

   char dequeueChar();

   char firstChar();

   default char lastChar() {
      throw new UnsupportedOperationException();
   }

   CharComparator comparator();

   @Deprecated
   default void enqueue(Character x) {
      this.enqueue(x.charValue());
   }

   @Deprecated
   default Character dequeue() {
      return this.dequeueChar();
   }

   @Deprecated
   default Character first() {
      return this.firstChar();
   }

   @Deprecated
   default Character last() {
      return this.lastChar();
   }
}
