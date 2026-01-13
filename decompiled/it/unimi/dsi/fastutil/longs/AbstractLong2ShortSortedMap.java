package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

public abstract class AbstractLong2ShortSortedMap extends AbstractLong2ShortMap implements Long2ShortSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2ShortSortedMap() {
   }

   @Override
   public LongSortedSet keySet() {
      return new AbstractLong2ShortSortedMap.KeySet();
   }

   @Override
   public ShortCollection values() {
      return new AbstractLong2ShortSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractLongSortedSet {
      @Override
      public boolean contains(long k) {
         return AbstractLong2ShortSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractLong2ShortSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractLong2ShortSortedMap.this.clear();
      }

      @Override
      public LongComparator comparator() {
         return AbstractLong2ShortSortedMap.this.comparator();
      }

      @Override
      public long firstLong() {
         return AbstractLong2ShortSortedMap.this.firstLongKey();
      }

      @Override
      public long lastLong() {
         return AbstractLong2ShortSortedMap.this.lastLongKey();
      }

      @Override
      public LongSortedSet headSet(long to) {
         return AbstractLong2ShortSortedMap.this.headMap(to).keySet();
      }

      @Override
      public LongSortedSet tailSet(long from) {
         return AbstractLong2ShortSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public LongSortedSet subSet(long from, long to) {
         return AbstractLong2ShortSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public LongBidirectionalIterator iterator(long from) {
         return new AbstractLong2ShortSortedMap.KeySetIterator(
            AbstractLong2ShortSortedMap.this.long2ShortEntrySet().iterator(new AbstractLong2ShortMap.BasicEntry(from, (short)0))
         );
      }

      @Override
      public LongBidirectionalIterator iterator() {
         return new AbstractLong2ShortSortedMap.KeySetIterator(Long2ShortSortedMaps.fastIterator(AbstractLong2ShortSortedMap.this));
      }
   }

   protected static class KeySetIterator implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2ShortMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2ShortMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractShortCollection {
      @Override
      public ShortIterator iterator() {
         return new AbstractLong2ShortSortedMap.ValuesIterator(Long2ShortSortedMaps.fastIterator(AbstractLong2ShortSortedMap.this));
      }

      @Override
      public boolean contains(short k) {
         return AbstractLong2ShortSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractLong2ShortSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractLong2ShortSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements ShortIterator {
      protected final ObjectBidirectionalIterator<Long2ShortMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2ShortMap.Entry> i) {
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
