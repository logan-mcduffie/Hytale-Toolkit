package it.unimi.dsi.fastutil.shorts;

import java.util.ListIterator;

public interface ShortListIterator extends ShortBidirectionalIterator, ListIterator<Short> {
   default void set(short k) {
      throw new UnsupportedOperationException();
   }

   default void add(short k) {
      throw new UnsupportedOperationException();
   }

   @Override
   default void remove() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default void set(Short k) {
      this.set(k.shortValue());
   }

   @Deprecated
   default void add(Short k) {
      this.add(k.shortValue());
   }

   @Deprecated
   @Override
   default Short next() {
      return ShortBidirectionalIterator.super.next();
   }

   @Deprecated
   @Override
   default Short previous() {
      return ShortBidirectionalIterator.super.previous();
   }
}
