package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface BooleanBidirectionalIterator extends BooleanIterator, ObjectBidirectionalIterator<Boolean> {
   boolean previousBoolean();

   @Deprecated
   default Boolean previous() {
      return this.previousBoolean();
   }

   @Override
   default int back(int n) {
      int i = n;

      while (i-- != 0 && this.hasPrevious()) {
         this.previousBoolean();
      }

      return n - i - 1;
   }

   @Override
   default int skip(int n) {
      return BooleanIterator.super.skip(n);
   }
}
