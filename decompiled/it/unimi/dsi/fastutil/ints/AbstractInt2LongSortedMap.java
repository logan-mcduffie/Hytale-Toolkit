package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractInt2LongSortedMap extends AbstractInt2LongMap implements Int2LongSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2LongSortedMap() {
   }

   @Override
   public IntSortedSet keySet() {
      return new AbstractInt2LongSortedMap.KeySet();
   }

   @Override
   public LongCollection values() {
      return new AbstractInt2LongSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractIntSortedSet {
      @Override
      public boolean contains(int k) {
         return AbstractInt2LongSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractInt2LongSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractInt2LongSortedMap.this.clear();
      }

      @Override
      public IntComparator comparator() {
         return AbstractInt2LongSortedMap.this.comparator();
      }

      @Override
      public int firstInt() {
         return AbstractInt2LongSortedMap.this.firstIntKey();
      }

      @Override
      public int lastInt() {
         return AbstractInt2LongSortedMap.this.lastIntKey();
      }

      @Override
      public IntSortedSet headSet(int to) {
         return AbstractInt2LongSortedMap.this.headMap(to).keySet();
      }

      @Override
      public IntSortedSet tailSet(int from) {
         return AbstractInt2LongSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public IntSortedSet subSet(int from, int to) {
         return AbstractInt2LongSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public IntBidirectionalIterator iterator(int from) {
         return new AbstractInt2LongSortedMap.KeySetIterator(
            AbstractInt2LongSortedMap.this.int2LongEntrySet().iterator(new AbstractInt2LongMap.BasicEntry(from, 0L))
         );
      }

      @Override
      public IntBidirectionalIterator iterator() {
         return new AbstractInt2LongSortedMap.KeySetIterator(Int2LongSortedMaps.fastIterator(AbstractInt2LongSortedMap.this));
      }
   }

   protected static class KeySetIterator implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2LongMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2LongMap.Entry> i) {
         this.i = i;
      }

      @Override
      public int nextInt() {
         return this.i.next().getIntKey();
      }

      @Override
      public int previousInt() {
         return this.i.previous().getIntKey();
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
         return new AbstractInt2LongSortedMap.ValuesIterator(Int2LongSortedMaps.fastIterator(AbstractInt2LongSortedMap.this));
      }

      @Override
      public boolean contains(long k) {
         return AbstractInt2LongSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractInt2LongSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractInt2LongSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements LongIterator {
      protected final ObjectBidirectionalIterator<Int2LongMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2LongMap.Entry> i) {
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
