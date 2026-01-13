package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;

public abstract class AbstractFloat2ReferenceSortedMap<V> extends AbstractFloat2ReferenceMap<V> implements Float2ReferenceSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractFloat2ReferenceSortedMap() {
   }

   @Override
   public FloatSortedSet keySet() {
      return new AbstractFloat2ReferenceSortedMap.KeySet();
   }

   @Override
   public ReferenceCollection<V> values() {
      return new AbstractFloat2ReferenceSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractFloatSortedSet {
      @Override
      public boolean contains(float k) {
         return AbstractFloat2ReferenceSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractFloat2ReferenceSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractFloat2ReferenceSortedMap.this.clear();
      }

      @Override
      public FloatComparator comparator() {
         return AbstractFloat2ReferenceSortedMap.this.comparator();
      }

      @Override
      public float firstFloat() {
         return AbstractFloat2ReferenceSortedMap.this.firstFloatKey();
      }

      @Override
      public float lastFloat() {
         return AbstractFloat2ReferenceSortedMap.this.lastFloatKey();
      }

      @Override
      public FloatSortedSet headSet(float to) {
         return AbstractFloat2ReferenceSortedMap.this.headMap(to).keySet();
      }

      @Override
      public FloatSortedSet tailSet(float from) {
         return AbstractFloat2ReferenceSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public FloatSortedSet subSet(float from, float to) {
         return AbstractFloat2ReferenceSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public FloatBidirectionalIterator iterator(float from) {
         return new AbstractFloat2ReferenceSortedMap.KeySetIterator<>(
            AbstractFloat2ReferenceSortedMap.this.float2ReferenceEntrySet().iterator(new AbstractFloat2ReferenceMap.BasicEntry<>(from, null))
         );
      }

      @Override
      public FloatBidirectionalIterator iterator() {
         return new AbstractFloat2ReferenceSortedMap.KeySetIterator<>(Float2ReferenceSortedMaps.fastIterator(AbstractFloat2ReferenceSortedMap.this));
      }
   }

   protected static class KeySetIterator<V> implements FloatBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> i) {
         this.i = i;
      }

      @Override
      public float nextFloat() {
         return this.i.next().getFloatKey();
      }

      @Override
      public float previousFloat() {
         return this.i.previous().getFloatKey();
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
         return new AbstractFloat2ReferenceSortedMap.ValuesIterator<>(Float2ReferenceSortedMaps.fastIterator(AbstractFloat2ReferenceSortedMap.this));
      }

      @Override
      public boolean contains(Object k) {
         return AbstractFloat2ReferenceSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractFloat2ReferenceSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractFloat2ReferenceSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Float2ReferenceMap.Entry<V>> i) {
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
