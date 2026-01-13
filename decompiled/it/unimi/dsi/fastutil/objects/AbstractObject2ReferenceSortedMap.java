package it.unimi.dsi.fastutil.objects;

import java.util.Comparator;

public abstract class AbstractObject2ReferenceSortedMap<K, V> extends AbstractObject2ReferenceMap<K, V> implements Object2ReferenceSortedMap<K, V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2ReferenceSortedMap() {
   }

   @Override
   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2ReferenceSortedMap.KeySet();
   }

   @Override
   public ReferenceCollection<V> values() {
      return new AbstractObject2ReferenceSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractObjectSortedSet<K> {
      @Override
      public boolean contains(Object k) {
         return AbstractObject2ReferenceSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractObject2ReferenceSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractObject2ReferenceSortedMap.this.clear();
      }

      @Override
      public Comparator<? super K> comparator() {
         return AbstractObject2ReferenceSortedMap.this.comparator();
      }

      @Override
      public K first() {
         return AbstractObject2ReferenceSortedMap.this.firstKey();
      }

      @Override
      public K last() {
         return AbstractObject2ReferenceSortedMap.this.lastKey();
      }

      @Override
      public ObjectSortedSet<K> headSet(K to) {
         return AbstractObject2ReferenceSortedMap.this.headMap(to).keySet();
      }

      @Override
      public ObjectSortedSet<K> tailSet(K from) {
         return AbstractObject2ReferenceSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public ObjectSortedSet<K> subSet(K from, K to) {
         return AbstractObject2ReferenceSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return new AbstractObject2ReferenceSortedMap.KeySetIterator<>(
            AbstractObject2ReferenceSortedMap.this.object2ReferenceEntrySet().iterator(new AbstractObject2ReferenceMap.BasicEntry<>(from, null))
         );
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2ReferenceSortedMap.KeySetIterator<>(Object2ReferenceSortedMaps.fastIterator(AbstractObject2ReferenceSortedMap.this));
      }
   }

   protected static class KeySetIterator<K, V> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> i) {
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

   protected class ValuesCollection extends AbstractReferenceCollection<V> {
      @Override
      public ObjectIterator<V> iterator() {
         return new AbstractObject2ReferenceSortedMap.ValuesIterator<>(Object2ReferenceSortedMaps.fastIterator(AbstractObject2ReferenceSortedMap.this));
      }

      @Override
      public boolean contains(Object k) {
         return AbstractObject2ReferenceSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractObject2ReferenceSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractObject2ReferenceSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<K, V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> i) {
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
