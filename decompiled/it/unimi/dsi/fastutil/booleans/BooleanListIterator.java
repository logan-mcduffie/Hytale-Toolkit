package it.unimi.dsi.fastutil.booleans;

import java.util.ListIterator;

public interface BooleanListIterator extends BooleanBidirectionalIterator, ListIterator<Boolean> {
   default void set(boolean k) {
      throw new UnsupportedOperationException();
   }

   default void add(boolean k) {
      throw new UnsupportedOperationException();
   }

   @Override
   default void remove() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default void set(Boolean k) {
      this.set(k.booleanValue());
   }

   @Deprecated
   default void add(Boolean k) {
      this.add(k.booleanValue());
   }

   @Deprecated
   @Override
   default Boolean next() {
      return BooleanBidirectionalIterator.super.next();
   }

   @Deprecated
   @Override
   default Boolean previous() {
      return BooleanBidirectionalIterator.super.previous();
   }
}
