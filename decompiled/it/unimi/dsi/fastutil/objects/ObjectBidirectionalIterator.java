package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BidirectionalIterator;

public interface ObjectBidirectionalIterator<K> extends ObjectIterator<K>, BidirectionalIterator<K> {
   default int back(int n) {
      int i = n;

      while (i-- != 0 && this.hasPrevious()) {
         this.previous();
      }

      return n - i - 1;
   }

   @Override
   default int skip(int n) {
      return ObjectIterator.super.skip(n);
   }
}
