package it.unimi.dsi.fastutil.objects;

import java.util.Comparator;

public abstract class AbstractReference2ObjectSortedMap<K, V> extends AbstractReference2ObjectMap<K, V> implements Reference2ObjectSortedMap<K, V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2ObjectSortedMap() {
   }

   @Override
   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2ObjectSortedMap.KeySet();
   }

   @Override
   public ObjectCollection<V> values() {
      return new AbstractReference2ObjectSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractReferenceSortedSet<K> {
      @Override
      public boolean contains(Object k) {
         return AbstractReference2ObjectSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractReference2ObjectSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractReference2ObjectSortedMap.this.clear();
      }

      @Override
      public Comparator<? super K> comparator() {
         return AbstractReference2ObjectSortedMap.this.comparator();
      }

      @Override
      public K first() {
         return AbstractReference2ObjectSortedMap.this.firstKey();
      }

      @Override
      public K last() {
         return AbstractReference2ObjectSortedMap.this.lastKey();
      }

      @Override
      public ReferenceSortedSet<K> headSet(K to) {
         return AbstractReference2ObjectSortedMap.this.headMap(to).keySet();
      }

      @Override
      public ReferenceSortedSet<K> tailSet(K from) {
         return AbstractReference2ObjectSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public ReferenceSortedSet<K> subSet(K from, K to) {
         return AbstractReference2ObjectSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return new AbstractReference2ObjectSortedMap.KeySetIterator<>(
            AbstractReference2ObjectSortedMap.this.reference2ObjectEntrySet().iterator(new AbstractReference2ObjectMap.BasicEntry<>(from, null))
         );
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2ObjectSortedMap.KeySetIterator<>(Reference2ObjectSortedMaps.fastIterator(AbstractReference2ObjectSortedMap.this));
      }
   }

   protected static class KeySetIterator<K, V> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> i) {
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

   protected class ValuesCollection extends AbstractObjectCollection<V> {
      @Override
      public ObjectIterator<V> iterator() {
         return new AbstractReference2ObjectSortedMap.ValuesIterator<>(Reference2ObjectSortedMaps.fastIterator(AbstractReference2ObjectSortedMap.this));
      }

      @Override
      public boolean contains(Object k) {
         return AbstractReference2ObjectSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractReference2ObjectSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractReference2ObjectSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<K, V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2ObjectMap.Entry<K, V>> i) {
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
