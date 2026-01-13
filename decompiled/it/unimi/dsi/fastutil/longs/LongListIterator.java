package it.unimi.dsi.fastutil.longs;

import java.util.ListIterator;

public interface LongListIterator extends LongBidirectionalIterator, ListIterator<Long> {
   default void set(long k) {
      throw new UnsupportedOperationException();
   }

   default void add(long k) {
      throw new UnsupportedOperationException();
   }

   @Override
   default void remove() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default void set(Long k) {
      this.set(k.longValue());
   }

   @Deprecated
   default void add(Long k) {
      this.add(k.longValue());
   }

   @Deprecated
   @Override
   default Long next() {
      return LongBidirectionalIterator.super.next();
   }

   @Deprecated
   @Override
   default Long previous() {
      return LongBidirectionalIterator.super.previous();
   }
}
