package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.PriorityQueue;

public interface FloatPriorityQueue extends PriorityQueue<Float> {
   void enqueue(float var1);

   float dequeueFloat();

   float firstFloat();

   default float lastFloat() {
      throw new UnsupportedOperationException();
   }

   FloatComparator comparator();

   @Deprecated
   default void enqueue(Float x) {
      this.enqueue(x.floatValue());
   }

   @Deprecated
   default Float dequeue() {
      return this.dequeueFloat();
   }

   @Deprecated
   default Float first() {
      return this.firstFloat();
   }

   @Deprecated
   default Float last() {
      return this.lastFloat();
   }
}
