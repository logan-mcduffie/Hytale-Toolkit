package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

public abstract class AbstractDouble2ShortSortedMap extends AbstractDouble2ShortMap implements Double2ShortSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2ShortSortedMap() {
   }

   @Override
   public DoubleSortedSet keySet() {
      return new AbstractDouble2ShortSortedMap.KeySet();
   }

   @Override
   public ShortCollection values() {
      return new AbstractDouble2ShortSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractDoubleSortedSet {
      @Override
      public boolean contains(double k) {
         return AbstractDouble2ShortSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractDouble2ShortSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractDouble2ShortSortedMap.this.clear();
      }

      @Override
      public DoubleComparator comparator() {
         return AbstractDouble2ShortSortedMap.this.comparator();
      }

      @Override
      public double firstDouble() {
         return AbstractDouble2ShortSortedMap.this.firstDoubleKey();
      }

      @Override
      public double lastDouble() {
         return AbstractDouble2ShortSortedMap.this.lastDoubleKey();
      }

      @Override
      public DoubleSortedSet headSet(double to) {
         return AbstractDouble2ShortSortedMap.this.headMap(to).keySet();
      }

      @Override
      public DoubleSortedSet tailSet(double from) {
         return AbstractDouble2ShortSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public DoubleSortedSet subSet(double from, double to) {
         return AbstractDouble2ShortSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public DoubleBidirectionalIterator iterator(double from) {
         return new AbstractDouble2ShortSortedMap.KeySetIterator(
            AbstractDouble2ShortSortedMap.this.double2ShortEntrySet().iterator(new AbstractDouble2ShortMap.BasicEntry(from, (short)0))
         );
      }

      @Override
      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2ShortSortedMap.KeySetIterator(Double2ShortSortedMaps.fastIterator(AbstractDouble2ShortSortedMap.this));
      }
   }

   protected static class KeySetIterator implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2ShortMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2ShortMap.Entry> i) {
         this.i = i;
      }

      @Override
      public double nextDouble() {
         return this.i.next().getDoubleKey();
      }

      @Override
      public double previousDouble() {
         return this.i.previous().getDoubleKey();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }

      @Override
      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }
   }

   protected class ValuesCollection extends AbstractShortCollection {
      @Override
      public ShortIterator iterator() {
         return new AbstractDouble2ShortSortedMap.ValuesIterator(Double2ShortSortedMaps.fastIterator(AbstractDouble2ShortSortedMap.this));
      }

      @Override
      public boolean contains(short k) {
         return AbstractDouble2ShortSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractDouble2ShortSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractDouble2ShortSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements ShortIterator {
      protected final ObjectBidirectionalIterator<Double2ShortMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2ShortMap.Entry> i) {
         this.i = i;
      }

      @Override
      public short nextShort() {
         return this.i.next().getShortValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
