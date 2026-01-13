package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface ShortBidirectionalIterator extends ShortIterator, ObjectBidirectionalIterator<Short> {
   short previousShort();

   @Deprecated
   default Short previous() {
      return this.previousShort();
   }

   @Override
   default int back(int n) {
      int i = n;

      while (i-- != 0 && this.hasPrevious()) {
         this.previousShort();
      }

      return n - i - 1;
   }

   @Override
   default int skip(int n) {
      return ShortIterator.super.skip(n);
   }
}
