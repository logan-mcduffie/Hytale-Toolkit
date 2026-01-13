package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractLong2LongSortedMap extends AbstractLong2LongMap implements Long2LongSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2LongSortedMap() {
   }

   @Override
   public LongSortedSet keySet() {
      return new AbstractLong2LongSortedMap.KeySet();
   }

   @Override
   public LongCollection values() {
      return new AbstractLong2LongSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractLongSortedSet {
      @Override
      public boolean contains(long k) {
         return AbstractLong2LongSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractLong2LongSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractLong2LongSortedMap.this.clear();
      }

      @Override
      public LongComparator comparator() {
         return AbstractLong2LongSortedMap.this.comparator();
      }

      @Override
      public long firstLong() {
         return AbstractLong2LongSortedMap.this.firstLongKey();
      }

      @Override
      public long lastLong() {
         return AbstractLong2LongSortedMap.this.lastLongKey();
      }

      @Override
      public LongSortedSet headSet(long to) {
         return AbstractLong2LongSortedMap.this.headMap(to).keySet();
      }

      @Override
      public LongSortedSet tailSet(long from) {
         return AbstractLong2LongSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public LongSortedSet subSet(long from, long to) {
         return AbstractLong2LongSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public LongBidirectionalIterator iterator(long from) {
         return new AbstractLong2LongSortedMap.KeySetIterator(
            AbstractLong2LongSortedMap.this.long2LongEntrySet().iterator(new AbstractLong2LongMap.BasicEntry(from, 0L))
         );
      }

      @Override
      public LongBidirectionalIterator iterator() {
         return new AbstractLong2LongSortedMap.KeySetIterator(Long2LongSortedMaps.fastIterator(AbstractLong2LongSortedMap.this));
      }
   }

   protected static class KeySetIterator implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2LongMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2LongMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractLongCollection {
      @Override
      public LongIterator iterator() {
         return new AbstractLong2LongSortedMap.ValuesIterator(Long2LongSortedMaps.fastIterator(AbstractLong2LongSortedMap.this));
      }

      @Override
      public boolean contains(long k) {
         return AbstractLong2LongSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractLong2LongSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractLong2LongSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements LongIterator {
      protected final ObjectBidirectionalIterator<Long2LongMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2LongMap.Entry> i) {
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
