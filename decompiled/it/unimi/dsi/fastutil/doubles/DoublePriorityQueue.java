package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.PriorityQueue;

public interface DoublePriorityQueue extends PriorityQueue<Double> {
   void enqueue(double var1);

   double dequeueDouble();

   double firstDouble();

   default double lastDouble() {
      throw new UnsupportedOperationException();
   }

   DoubleComparator comparator();

   @Deprecated
   default void enqueue(Double x) {
      this.enqueue(x.doubleValue());
   }

   @Deprecated
   default Double dequeue() {
      return this.dequeueDouble();
   }

   @Deprecated
   default Double first() {
      return this.firstDouble();
   }

   @Deprecated
   default Double last() {
      return this.lastDouble();
   }
}
