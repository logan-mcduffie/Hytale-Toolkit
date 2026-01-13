package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractLong2BooleanSortedMap extends AbstractLong2BooleanMap implements Long2BooleanSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2BooleanSortedMap() {
   }

   @Override
   public LongSortedSet keySet() {
      return new AbstractLong2BooleanSortedMap.KeySet();
   }

   @Override
   public BooleanCollection values() {
      return new AbstractLong2BooleanSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractLongSortedSet {
      @Override
      public boolean contains(long k) {
         return AbstractLong2BooleanSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractLong2BooleanSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractLong2BooleanSortedMap.this.clear();
      }

      @Override
      public LongComparator comparator() {
         return AbstractLong2BooleanSortedMap.this.comparator();
      }

      @Override
      public long firstLong() {
         return AbstractLong2BooleanSortedMap.this.firstLongKey();
      }

      @Override
      public long lastLong() {
         return AbstractLong2BooleanSortedMap.this.lastLongKey();
      }

      @Override
      public LongSortedSet headSet(long to) {
         return AbstractLong2BooleanSortedMap.this.headMap(to).keySet();
      }

      @Override
      public LongSortedSet tailSet(long from) {
         return AbstractLong2BooleanSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public LongSortedSet subSet(long from, long to) {
         return AbstractLong2BooleanSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public LongBidirectionalIterator iterator(long from) {
         return new AbstractLong2BooleanSortedMap.KeySetIterator(
            AbstractLong2BooleanSortedMap.this.long2BooleanEntrySet().iterator(new AbstractLong2BooleanMap.BasicEntry(from, false))
         );
      }

      @Override
      public LongBidirectionalIterator iterator() {
         return new AbstractLong2BooleanSortedMap.KeySetIterator(Long2BooleanSortedMaps.fastIterator(AbstractLong2BooleanSortedMap.this));
      }
   }

   protected static class KeySetIterator implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2BooleanMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2BooleanMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractBooleanCollection {
      @Override
      public BooleanIterator iterator() {
         return new AbstractLong2BooleanSortedMap.ValuesIterator(Long2BooleanSortedMaps.fastIterator(AbstractLong2BooleanSortedMap.this));
      }

      @Override
      public boolean contains(boolean k) {
         return AbstractLong2BooleanSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractLong2BooleanSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractLong2BooleanSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements BooleanIterator {
      protected final ObjectBidirectionalIterator<Long2BooleanMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2BooleanMap.Entry> i) {
         this.i = i;
      }

      @Override
      public boolean nextBoolean() {
         return this.i.next().getBooleanValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
