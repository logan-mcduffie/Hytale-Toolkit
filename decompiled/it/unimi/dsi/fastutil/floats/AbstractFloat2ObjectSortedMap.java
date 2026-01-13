package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public abstract class AbstractFloat2ObjectSortedMap<V> extends AbstractFloat2ObjectMap<V> implements Float2ObjectSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractFloat2ObjectSortedMap() {
   }

   @Override
   public FloatSortedSet keySet() {
      return new AbstractFloat2ObjectSortedMap.KeySet();
   }

   @Override
   public ObjectCollection<V> values() {
      return new AbstractFloat2ObjectSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractFloatSortedSet {
      @Override
      public boolean contains(float k) {
         return AbstractFloat2ObjectSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractFloat2ObjectSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractFloat2ObjectSortedMap.this.clear();
      }

      @Override
      public FloatComparator comparator() {
         return AbstractFloat2ObjectSortedMap.this.comparator();
      }

      @Override
      public float firstFloat() {
         return AbstractFloat2ObjectSortedMap.this.firstFloatKey();
      }

      @Override
      public float lastFloat() {
         return AbstractFloat2ObjectSortedMap.this.lastFloatKey();
      }

      @Override
      public FloatSortedSet headSet(float to) {
         return AbstractFloat2ObjectSortedMap.this.headMap(to).keySet();
      }

      @Override
      public FloatSortedSet tailSet(float from) {
         return AbstractFloat2ObjectSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public FloatSortedSet subSet(float from, float to) {
         return AbstractFloat2ObjectSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public FloatBidirectionalIterator iterator(float from) {
         return new AbstractFloat2ObjectSortedMap.KeySetIterator<>(
            AbstractFloat2ObjectSortedMap.this.float2ObjectEntrySet().iterator(new AbstractFloat2ObjectMap.BasicEntry<>(from, null))
         );
      }

      @Override
      public FloatBidirectionalIterator iterator() {
         return new AbstractFloat2ObjectSortedMap.KeySetIterator<>(Float2ObjectSortedMaps.fastIterator(AbstractFloat2ObjectSortedMap.this));
      }
   }

   protected static class KeySetIterator<V> implements FloatBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> i) {
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

   protected class ValuesCollection extends AbstractObjectCollection<V> {
      @Override
      public ObjectIterator<V> iterator() {
         return new AbstractFloat2ObjectSortedMap.ValuesIterator<>(Float2ObjectSortedMaps.fastIterator(AbstractFloat2ObjectSortedMap.this));
      }

      @Override
      public boolean contains(Object k) {
         return AbstractFloat2ObjectSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractFloat2ObjectSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractFloat2ObjectSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Float2ObjectMap.Entry<V>> i) {
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
