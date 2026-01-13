package it.unimi.dsi.fastutil.floats;

import java.util.ListIterator;

public interface FloatListIterator extends FloatBidirectionalIterator, ListIterator<Float> {
   default void set(float k) {
      throw new UnsupportedOperationException();
   }

   default void add(float k) {
      throw new UnsupportedOperationException();
   }

   @Override
   default void remove() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default void set(Float k) {
      this.set(k.floatValue());
   }

   @Deprecated
   default void add(Float k) {
      this.add(k.floatValue());
   }

   @Deprecated
   @Override
   default Float next() {
      return FloatBidirectionalIterator.super.next();
   }

   @Deprecated
   @Override
   default Float previous() {
      return FloatBidirectionalIterator.super.previous();
   }
}
