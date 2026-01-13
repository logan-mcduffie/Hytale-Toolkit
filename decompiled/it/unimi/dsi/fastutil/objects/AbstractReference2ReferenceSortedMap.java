package it.unimi.dsi.fastutil.objects;

import java.util.Comparator;

public abstract class AbstractReference2ReferenceSortedMap<K, V> extends AbstractReference2ReferenceMap<K, V> implements Reference2ReferenceSortedMap<K, V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2ReferenceSortedMap() {
   }

   @Override
   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2ReferenceSortedMap.KeySet();
   }

   @Override
   public ReferenceCollection<V> values() {
      return new AbstractReference2ReferenceSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractReferenceSortedSet<K> {
      @Override
      public boolean contains(Object k) {
         return AbstractReference2ReferenceSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractReference2ReferenceSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractReference2ReferenceSortedMap.this.clear();
      }

      @Override
      public Comparator<? super K> comparator() {
         return AbstractReference2ReferenceSortedMap.this.comparator();
      }

      @Override
      public K first() {
         return AbstractReference2ReferenceSortedMap.this.firstKey();
      }

      @Override
      public K last() {
         return AbstractReference2ReferenceSortedMap.this.lastKey();
      }

      @Override
      public ReferenceSortedSet<K> headSet(K to) {
         return AbstractReference2ReferenceSortedMap.this.headMap(to).keySet();
      }

      @Override
      public ReferenceSortedSet<K> tailSet(K from) {
         return AbstractReference2ReferenceSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public ReferenceSortedSet<K> subSet(K from, K to) {
         return AbstractReference2ReferenceSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return new AbstractReference2ReferenceSortedMap.KeySetIterator<>(
            AbstractReference2ReferenceSortedMap.this.reference2ReferenceEntrySet().iterator(new AbstractReference2ReferenceMap.BasicEntry<>(from, null))
         );
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2ReferenceSortedMap.KeySetIterator<>(Reference2ReferenceSortedMaps.fastIterator(AbstractReference2ReferenceSortedMap.this));
      }
   }

   protected static class KeySetIterator<K, V> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2ReferenceMap.Entry<K, V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2ReferenceMap.Entry<K, V>> i) {
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
         return new AbstractReference2ReferenceSortedMap.ValuesIterator<>(Reference2ReferenceSortedMaps.fastIterator(AbstractReference2ReferenceSortedMap.this));
      }

      @Override
      public boolean contains(Object k) {
         return AbstractReference2ReferenceSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractReference2ReferenceSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractReference2ReferenceSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<K, V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Reference2ReferenceMap.Entry<K, V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2ReferenceMap.Entry<K, V>> i) {
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
