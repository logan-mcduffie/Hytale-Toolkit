package it.unimi.dsi.fastutil.doubles;

import java.util.ListIterator;

public interface DoubleListIterator extends DoubleBidirectionalIterator, ListIterator<Double> {
   default void set(double k) {
      throw new UnsupportedOperationException();
   }

   default void add(double k) {
      throw new UnsupportedOperationException();
   }

   @Override
   default void remove() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default void set(Double k) {
      this.set(k.doubleValue());
   }

   @Deprecated
   default void add(Double k) {
      this.add(k.doubleValue());
   }

   @Deprecated
   @Override
   default Double next() {
      return DoubleBidirectionalIterator.super.next();
   }

   @Deprecated
   @Override
   default Double previous() {
      return DoubleBidirectionalIterator.super.previous();
   }
}
