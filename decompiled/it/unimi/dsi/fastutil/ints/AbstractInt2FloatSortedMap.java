package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractInt2FloatSortedMap extends AbstractInt2FloatMap implements Int2FloatSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2FloatSortedMap() {
   }

   @Override
   public IntSortedSet keySet() {
      return new AbstractInt2FloatSortedMap.KeySet();
   }

   @Override
   public FloatCollection values() {
      return new AbstractInt2FloatSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractIntSortedSet {
      @Override
      public boolean contains(int k) {
         return AbstractInt2FloatSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractInt2FloatSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractInt2FloatSortedMap.this.clear();
      }

      @Override
      public IntComparator comparator() {
         return AbstractInt2FloatSortedMap.this.comparator();
      }

      @Override
      public int firstInt() {
         return AbstractInt2FloatSortedMap.this.firstIntKey();
      }

      @Override
      public int lastInt() {
         return AbstractInt2FloatSortedMap.this.lastIntKey();
      }

      @Override
      public IntSortedSet headSet(int to) {
         return AbstractInt2FloatSortedMap.this.headMap(to).keySet();
      }

      @Override
      public IntSortedSet tailSet(int from) {
         return AbstractInt2FloatSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public IntSortedSet subSet(int from, int to) {
         return AbstractInt2FloatSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public IntBidirectionalIterator iterator(int from) {
         return new AbstractInt2FloatSortedMap.KeySetIterator(
            AbstractInt2FloatSortedMap.this.int2FloatEntrySet().iterator(new AbstractInt2FloatMap.BasicEntry(from, 0.0F))
         );
      }

      @Override
      public IntBidirectionalIterator iterator() {
         return new AbstractInt2FloatSortedMap.KeySetIterator(Int2FloatSortedMaps.fastIterator(AbstractInt2FloatSortedMap.this));
      }
   }

   protected static class KeySetIterator implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2FloatMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2FloatMap.Entry> i) {
         this.i = i;
      }

      @Override
      public int nextInt() {
         return this.i.next().getIntKey();
      }

      @Override
      public int previousInt() {
         return this.i.previous().getIntKey();
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
         return new AbstractInt2FloatSortedMap.ValuesIterator(Int2FloatSortedMaps.fastIterator(AbstractInt2FloatSortedMap.this));
      }

      @Override
      public boolean contains(float k) {
         return AbstractInt2FloatSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractInt2FloatSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractInt2FloatSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements FloatIterator {
      protected final ObjectBidirectionalIterator<Int2FloatMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2FloatMap.Entry> i) {
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
