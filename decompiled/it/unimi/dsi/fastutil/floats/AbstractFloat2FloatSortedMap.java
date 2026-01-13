package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractFloat2FloatSortedMap extends AbstractFloat2FloatMap implements Float2FloatSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractFloat2FloatSortedMap() {
   }

   @Override
   public FloatSortedSet keySet() {
      return new AbstractFloat2FloatSortedMap.KeySet();
   }

   @Override
   public FloatCollection values() {
      return new AbstractFloat2FloatSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractFloatSortedSet {
      @Override
      public boolean contains(float k) {
         return AbstractFloat2FloatSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractFloat2FloatSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractFloat2FloatSortedMap.this.clear();
      }

      @Override
      public FloatComparator comparator() {
         return AbstractFloat2FloatSortedMap.this.comparator();
      }

      @Override
      public float firstFloat() {
         return AbstractFloat2FloatSortedMap.this.firstFloatKey();
      }

      @Override
      public float lastFloat() {
         return AbstractFloat2FloatSortedMap.this.lastFloatKey();
      }

      @Override
      public FloatSortedSet headSet(float to) {
         return AbstractFloat2FloatSortedMap.this.headMap(to).keySet();
      }

      @Override
      public FloatSortedSet tailSet(float from) {
         return AbstractFloat2FloatSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public FloatSortedSet subSet(float from, float to) {
         return AbstractFloat2FloatSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public FloatBidirectionalIterator iterator(float from) {
         return new AbstractFloat2FloatSortedMap.KeySetIterator(
            AbstractFloat2FloatSortedMap.this.float2FloatEntrySet().iterator(new AbstractFloat2FloatMap.BasicEntry(from, 0.0F))
         );
      }

      @Override
      public FloatBidirectionalIterator iterator() {
         return new AbstractFloat2FloatSortedMap.KeySetIterator(Float2FloatSortedMaps.fastIterator(AbstractFloat2FloatSortedMap.this));
      }
   }

   protected static class KeySetIterator implements FloatBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Float2FloatMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Float2FloatMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractFloatCollection {
      @Override
      public FloatIterator iterator() {
         return new AbstractFloat2FloatSortedMap.ValuesIterator(Float2FloatSortedMaps.fastIterator(AbstractFloat2FloatSortedMap.this));
      }

      @Override
      public boolean contains(float k) {
         return AbstractFloat2FloatSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractFloat2FloatSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractFloat2FloatSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements FloatIterator {
      protected final ObjectBidirectionalIterator<Float2FloatMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Float2FloatMap.Entry> i) {
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
