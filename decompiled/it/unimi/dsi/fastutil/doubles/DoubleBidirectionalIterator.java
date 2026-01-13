package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public interface DoubleBidirectionalIterator extends DoubleIterator, ObjectBidirectionalIterator<Double> {
   double previousDouble();

   @Deprecated
   default Double previous() {
      return this.previousDouble();
   }

   @Override
   default int back(int n) {
      int i = n;

      while (i-- != 0 && this.hasPrevious()) {
         this.previousDouble();
      }

      return n - i - 1;
   }

   @Override
   default int skip(int n) {
      return DoubleIterator.super.skip(n);
   }
}
