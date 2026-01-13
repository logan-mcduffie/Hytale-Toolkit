package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public abstract class AbstractLong2ObjectSortedMap<V> extends AbstractLong2ObjectMap<V> implements Long2ObjectSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractLong2ObjectSortedMap() {
   }

   @Override
   public LongSortedSet keySet() {
      return new AbstractLong2ObjectSortedMap.KeySet();
   }

   @Override
   public ObjectCollection<V> values() {
      return new AbstractLong2ObjectSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractLongSortedSet {
      @Override
      public boolean contains(long k) {
         return AbstractLong2ObjectSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractLong2ObjectSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractLong2ObjectSortedMap.this.clear();
      }

      @Override
      public LongComparator comparator() {
         return AbstractLong2ObjectSortedMap.this.comparator();
      }

      @Override
      public long firstLong() {
         return AbstractLong2ObjectSortedMap.this.firstLongKey();
      }

      @Override
      public long lastLong() {
         return AbstractLong2ObjectSortedMap.this.lastLongKey();
      }

      @Override
      public LongSortedSet headSet(long to) {
         return AbstractLong2ObjectSortedMap.this.headMap(to).keySet();
      }

      @Override
      public LongSortedSet tailSet(long from) {
         return AbstractLong2ObjectSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public LongSortedSet subSet(long from, long to) {
         return AbstractLong2ObjectSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public LongBidirectionalIterator iterator(long from) {
         return new AbstractLong2ObjectSortedMap.KeySetIterator<>(
            AbstractLong2ObjectSortedMap.this.long2ObjectEntrySet().iterator(new AbstractLong2ObjectMap.BasicEntry<>(from, null))
         );
      }

      @Override
      public LongBidirectionalIterator iterator() {
         return new AbstractLong2ObjectSortedMap.KeySetIterator<>(Long2ObjectSortedMaps.fastIterator(AbstractLong2ObjectSortedMap.this));
      }
   }

   protected static class KeySetIterator<V> implements LongBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> i) {
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

   protected class ValuesCollection extends AbstractObjectCollection<V> {
      @Override
      public ObjectIterator<V> iterator() {
         return new AbstractLong2ObjectSortedMap.ValuesIterator<>(Long2ObjectSortedMaps.fastIterator(AbstractLong2ObjectSortedMap.this));
      }

      @Override
      public boolean contains(Object k) {
         return AbstractLong2ObjectSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractLong2ObjectSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractLong2ObjectSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> i) {
         this.i = i;
      }

      @Override
      public V next() {
         return this.i.next().getValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
