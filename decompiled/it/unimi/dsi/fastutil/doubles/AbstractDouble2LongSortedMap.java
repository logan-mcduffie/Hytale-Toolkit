package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractDouble2LongSortedMap extends AbstractDouble2LongMap implements Double2LongSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2LongSortedMap() {
   }

   @Override
   public DoubleSortedSet keySet() {
      return new AbstractDouble2LongSortedMap.KeySet();
   }

   @Override
   public LongCollection values() {
      return new AbstractDouble2LongSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractDoubleSortedSet {
      @Override
      public boolean contains(double k) {
         return AbstractDouble2LongSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractDouble2LongSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractDouble2LongSortedMap.this.clear();
      }

      @Override
      public DoubleComparator comparator() {
         return AbstractDouble2LongSortedMap.this.comparator();
      }

      @Override
      public double firstDouble() {
         return AbstractDouble2LongSortedMap.this.firstDoubleKey();
      }

      @Override
      public double lastDouble() {
         return AbstractDouble2LongSortedMap.this.lastDoubleKey();
      }

      @Override
      public DoubleSortedSet headSet(double to) {
         return AbstractDouble2LongSortedMap.this.headMap(to).keySet();
      }

      @Override
      public DoubleSortedSet tailSet(double from) {
         return AbstractDouble2LongSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public DoubleSortedSet subSet(double from, double to) {
         return AbstractDouble2LongSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public DoubleBidirectionalIterator iterator(double from) {
         return new AbstractDouble2LongSortedMap.KeySetIterator(
            AbstractDouble2LongSortedMap.this.double2LongEntrySet().iterator(new AbstractDouble2LongMap.BasicEntry(from, 0L))
         );
      }

      @Override
      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2LongSortedMap.KeySetIterator(Double2LongSortedMaps.fastIterator(AbstractDouble2LongSortedMap.this));
      }
   }

   protected static class KeySetIterator implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2LongMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2LongMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractLongCollection {
      @Override
      public LongIterator iterator() {
         return new AbstractDouble2LongSortedMap.ValuesIterator(Double2LongSortedMaps.fastIterator(AbstractDouble2LongSortedMap.this));
      }

      @Override
      public boolean contains(long k) {
         return AbstractDouble2LongSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractDouble2LongSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractDouble2LongSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements LongIterator {
      protected final ObjectBidirectionalIterator<Double2LongMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2LongMap.Entry> i) {
         this.i = i;
      }

      @Override
      public long nextLong() {
         return this.i.next().getLongValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
