package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractDouble2IntSortedMap extends AbstractDouble2IntMap implements Double2IntSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2IntSortedMap() {
   }

   @Override
   public DoubleSortedSet keySet() {
      return new AbstractDouble2IntSortedMap.KeySet();
   }

   @Override
   public IntCollection values() {
      return new AbstractDouble2IntSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractDoubleSortedSet {
      @Override
      public boolean contains(double k) {
         return AbstractDouble2IntSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractDouble2IntSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractDouble2IntSortedMap.this.clear();
      }

      @Override
      public DoubleComparator comparator() {
         return AbstractDouble2IntSortedMap.this.comparator();
      }

      @Override
      public double firstDouble() {
         return AbstractDouble2IntSortedMap.this.firstDoubleKey();
      }

      @Override
      public double lastDouble() {
         return AbstractDouble2IntSortedMap.this.lastDoubleKey();
      }

      @Override
      public DoubleSortedSet headSet(double to) {
         return AbstractDouble2IntSortedMap.this.headMap(to).keySet();
      }

      @Override
      public DoubleSortedSet tailSet(double from) {
         return AbstractDouble2IntSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public DoubleSortedSet subSet(double from, double to) {
         return AbstractDouble2IntSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public DoubleBidirectionalIterator iterator(double from) {
         return new AbstractDouble2IntSortedMap.KeySetIterator(
            AbstractDouble2IntSortedMap.this.double2IntEntrySet().iterator(new AbstractDouble2IntMap.BasicEntry(from, 0))
         );
      }

      @Override
      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2IntSortedMap.KeySetIterator(Double2IntSortedMaps.fastIterator(AbstractDouble2IntSortedMap.this));
      }
   }

   protected static class KeySetIterator implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2IntMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2IntMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractIntCollection {
      @Override
      public IntIterator iterator() {
         return new AbstractDouble2IntSortedMap.ValuesIterator(Double2IntSortedMaps.fastIterator(AbstractDouble2IntSortedMap.this));
      }

      @Override
      public boolean contains(int k) {
         return AbstractDouble2IntSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractDouble2IntSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractDouble2IntSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements IntIterator {
      protected final ObjectBidirectionalIterator<Double2IntMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2IntMap.Entry> i) {
         this.i = i;
      }

      @Override
      public int nextInt() {
         return this.i.next().getIntValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
