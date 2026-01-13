package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractLong2DoubleSortedMap extends AbstractLong2DoubleMap implements Long2DoubleSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2DoubleSortedMap() {
   }

   @Override
   public LongSortedSet keySet() {
      return new AbstractLong2DoubleSortedMap.KeySet();
   }

   @Override
   public DoubleCollection values() {
      return new AbstractLong2DoubleSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractLongSortedSet {
      @Override
      public boolean contains(long k) {
         return AbstractLong2DoubleSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractLong2DoubleSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractLong2DoubleSortedMap.this.clear();
      }

      @Override
      public LongComparator comparator() {
         return AbstractLong2DoubleSortedMap.this.comparator();
      }

      @Override
      public long firstLong() {
         return AbstractLong2DoubleSortedMap.this.firstLongKey();
      }

      @Override
      public long lastLong() {
         return AbstractLong2DoubleSortedMap.this.lastLongKey();
      }

      @Override
      public LongSortedSet headSet(long to) {
         return AbstractLong2DoubleSortedMap.this.headMap(to).keySet();
      }

      @Override
      public LongSortedSet tailSet(long from) {
         return AbstractLong2DoubleSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public LongSortedSet subSet(long from, long to) {
         return AbstractLong2DoubleSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public LongBidirectionalIterator iterator(long from) {
         return new AbstractLong2DoubleSortedMap.KeySetIterator(
            AbstractLong2DoubleSortedMap.this.long2DoubleEntrySet().iterator(new AbstractLong2DoubleMap.BasicEntry(from, 0.0))
         );
      }

      @Override
      public LongBidirectionalIterator iterator() {
         return new AbstractLong2DoubleSortedMap.KeySetIterator(Long2DoubleSortedMaps.fastIterator(AbstractLong2DoubleSortedMap.this));
      }
   }

   protected static class KeySetIterator implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2DoubleMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2DoubleMap.Entry> i) {
         this.i = i;
      }

      @Override
      public long nextLong() {
         return this.i.next().getLongKey();
      }

      @Override
      public long previousLong() {
         return this.i.previous().getLongKey();
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

   protected class ValuesCollection extends AbstractDoubleCollection {
      @Override
      public DoubleIterator iterator() {
         return new AbstractLong2DoubleSortedMap.ValuesIterator(Long2DoubleSortedMaps.fastIterator(AbstractLong2DoubleSortedMap.this));
      }

      @Override
      public boolean contains(double k) {
         return AbstractLong2DoubleSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractLong2DoubleSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractLong2DoubleSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements DoubleIterator {
      protected final ObjectBidirectionalIterator<Long2DoubleMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2DoubleMap.Entry> i) {
         this.i = i;
      }

      @Override
      public double nextDouble() {
         return this.i.next().getDoubleValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
