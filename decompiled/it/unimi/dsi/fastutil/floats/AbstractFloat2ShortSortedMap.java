package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

public abstract class AbstractFloat2ShortSortedMap extends AbstractFloat2ShortMap implements Float2ShortSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractFloat2ShortSortedMap() {
   }

   @Override
   public FloatSortedSet keySet() {
      return new AbstractFloat2ShortSortedMap.KeySet();
   }

   @Override
   public ShortCollection values() {
      return new AbstractFloat2ShortSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractFloatSortedSet {
      @Override
      public boolean contains(float k) {
         return AbstractFloat2ShortSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractFloat2ShortSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractFloat2ShortSortedMap.this.clear();
      }

      @Override
      public FloatComparator comparator() {
         return AbstractFloat2ShortSortedMap.this.comparator();
      }

      @Override
      public float firstFloat() {
         return AbstractFloat2ShortSortedMap.this.firstFloatKey();
      }

      @Override
      public float lastFloat() {
         return AbstractFloat2ShortSortedMap.this.lastFloatKey();
      }

      @Override
      public FloatSortedSet headSet(float to) {
         return AbstractFloat2ShortSortedMap.this.headMap(to).keySet();
      }

      @Override
      public FloatSortedSet tailSet(float from) {
         return AbstractFloat2ShortSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public FloatSortedSet subSet(float from, float to) {
         return AbstractFloat2ShortSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public FloatBidirectionalIterator iterator(float from) {
         return new AbstractFloat2ShortSortedMap.KeySetIterator(
            AbstractFloat2ShortSortedMap.this.float2ShortEntrySet().iterator(new AbstractFloat2ShortMap.BasicEntry(from, (short)0))
         );
      }

      @Override
      public FloatBidirectionalIterator iterator() {
         return new AbstractFloat2ShortSortedMap.KeySetIterator(Float2ShortSortedMaps.fastIterator(AbstractFloat2ShortSortedMap.this));
      }
   }

   protected static class KeySetIterator implements FloatBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Float2ShortMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Float2ShortMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractShortCollection {
      @Override
      public ShortIterator iterator() {
         return new AbstractFloat2ShortSortedMap.ValuesIterator(Float2ShortSortedMaps.fastIterator(AbstractFloat2ShortSortedMap.this));
      }

      @Override
      public boolean contains(short k) {
         return AbstractFloat2ShortSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractFloat2ShortSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractFloat2ShortSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements ShortIterator {
      protected final ObjectBidirectionalIterator<Float2ShortMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Float2ShortMap.Entry> i) {
         this.i = i;
      }

      @Override
      public short nextShort() {
         return this.i.next().getShortValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
