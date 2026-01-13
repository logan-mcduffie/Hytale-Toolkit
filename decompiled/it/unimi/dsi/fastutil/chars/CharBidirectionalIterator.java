package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface CharBidirectionalIterator extends CharIterator, ObjectBidirectionalIterator<Character> {
   char previousChar();

   @Deprecated
   default Character previous() {
      return this.previousChar();
   }

   @Override
   default int back(int n) {
      int i = n;

      while (i-- != 0 && this.hasPrevious()) {
         this.previousChar();
      }

      return n - i - 1;
   }

   @Override
   default int skip(int n) {
      return CharIterator.super.skip(n);
   }
}
