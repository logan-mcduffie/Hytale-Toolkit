package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.Comparator;

public abstract class AbstractObject2LongSortedMap<K> extends AbstractObject2LongMap<K> implements Object2LongSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2LongSortedMap() {
   }

   @Override
   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2LongSortedMap.KeySet();
   }

   @Override
   public LongCollection values() {
      return new AbstractObject2LongSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractObjectSortedSet<K> {
      @Override
      public boolean contains(Object k) {
         return AbstractObject2LongSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractObject2LongSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractObject2LongSortedMap.this.clear();
      }

      @Override
      public Comparator<? super K> comparator() {
         return AbstractObject2LongSortedMap.this.comparator();
      }

      @Override
      public K first() {
         return AbstractObject2LongSortedMap.this.firstKey();
      }

      @Override
      public K last() {
         return AbstractObject2LongSortedMap.this.lastKey();
      }

      @Override
      public ObjectSortedSet<K> headSet(K to) {
         return AbstractObject2LongSortedMap.this.headMap(to).keySet();
      }

      @Override
      public ObjectSortedSet<K> tailSet(K from) {
         return AbstractObject2LongSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public ObjectSortedSet<K> subSet(K from, K to) {
         return AbstractObject2LongSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return new AbstractObject2LongSortedMap.KeySetIterator<>(
            AbstractObject2LongSortedMap.this.object2LongEntrySet().iterator(new AbstractObject2LongMap.BasicEntry<>(from, 0L))
         );
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2LongSortedMap.KeySetIterator<>(Object2LongSortedMaps.fastIterator(AbstractObject2LongSortedMap.this));
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2LongMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2LongMap.Entry<K>> i) {
         this.i = i;
      }

      @Override
      public K next() {
         return this.i.next().getKey();
      }

      @Override
      public K previous() {
         return this.i.previous().getKey();
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
         return new AbstractObject2LongSortedMap.ValuesIterator<>(Object2LongSortedMaps.fastIterator(AbstractObject2LongSortedMap.this));
      }

      @Override
      public boolean contains(long k) {
         return AbstractObject2LongSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractObject2LongSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractObject2LongSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<K> implements LongIterator {
      protected final ObjectBidirectionalIterator<Object2LongMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2LongMap.Entry<K>> i) {
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
