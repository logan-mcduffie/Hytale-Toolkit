package it.unimi.dsi.fastutil.chars;

import java.util.ListIterator;

public interface CharListIterator extends CharBidirectionalIterator, ListIterator<Character> {
   default void set(char k) {
      throw new UnsupportedOperationException();
   }

   default void add(char k) {
      throw new UnsupportedOperationException();
   }

   @Override
   default void remove() {
      throw new UnsupportedOperationException();
   }

   @Deprecated
   default void set(Character k) {
      this.set(k.charValue());
   }

   @Deprecated
   default void add(Character k) {
      this.add(k.charValue());
   }

   @Deprecated
   @Override
   default Character next() {
      return CharBidirectionalIterator.super.next();
   }

   @Deprecated
   @Override
   default Character previous() {
      return CharBidirectionalIterator.super.previous();
   }
}
