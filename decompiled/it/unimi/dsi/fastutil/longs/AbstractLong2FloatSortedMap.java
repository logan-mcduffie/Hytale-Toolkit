package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractLong2FloatSortedMap extends AbstractLong2FloatMap implements Long2FloatSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2FloatSortedMap() {
   }

   @Override
   public LongSortedSet keySet() {
      return new AbstractLong2FloatSortedMap.KeySet();
   }

   @Override
   public FloatCollection values() {
      return new AbstractLong2FloatSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractLongSortedSet {
      @Override
      public boolean contains(long k) {
         return AbstractLong2FloatSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractLong2FloatSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractLong2FloatSortedMap.this.clear();
      }

      @Override
      public LongComparator comparator() {
         return AbstractLong2FloatSortedMap.this.comparator();
      }

      @Override
      public long firstLong() {
         return AbstractLong2FloatSortedMap.this.firstLongKey();
      }

      @Override
      public long lastLong() {
         return AbstractLong2FloatSortedMap.this.lastLongKey();
      }

      @Override
      public LongSortedSet headSet(long to) {
         return AbstractLong2FloatSortedMap.this.headMap(to).keySet();
      }

      @Override
      public LongSortedSet tailSet(long from) {
         return AbstractLong2FloatSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public LongSortedSet subSet(long from, long to) {
         return AbstractLong2FloatSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public LongBidirectionalIterator iterator(long from) {
         return new AbstractLong2FloatSortedMap.KeySetIterator(
            AbstractLong2FloatSortedMap.this.long2FloatEntrySet().iterator(new AbstractLong2FloatMap.BasicEntry(from, 0.0F))
         );
      }

      @Override
      public LongBidirectionalIterator iterator() {
         return new AbstractLong2FloatSortedMap.KeySetIterator(Long2FloatSortedMaps.fastIterator(AbstractLong2FloatSortedMap.this));
      }
   }

   protected static class KeySetIterator implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2FloatMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2FloatMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractFloatCollection {
      @Override
      public FloatIterator iterator() {
         return new AbstractLong2FloatSortedMap.ValuesIterator(Long2FloatSortedMaps.fastIterator(AbstractLong2FloatSortedMap.this));
      }

      @Override
      public boolean contains(float k) {
         return AbstractLong2FloatSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractLong2FloatSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractLong2FloatSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements FloatIterator {
      protected final ObjectBidirectionalIterator<Long2FloatMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2FloatMap.Entry> i) {
         this.i = i;
      }

      @Override
      public float nextFloat() {
         return this.i.next().getFloatValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
