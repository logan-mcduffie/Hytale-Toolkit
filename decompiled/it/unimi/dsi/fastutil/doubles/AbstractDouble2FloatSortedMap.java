package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractDouble2FloatSortedMap extends AbstractDouble2FloatMap implements Double2FloatSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2FloatSortedMap() {
   }

   @Override
   public DoubleSortedSet keySet() {
      return new AbstractDouble2FloatSortedMap.KeySet();
   }

   @Override
   public FloatCollection values() {
      return new AbstractDouble2FloatSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractDoubleSortedSet {
      @Override
      public boolean contains(double k) {
         return AbstractDouble2FloatSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractDouble2FloatSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractDouble2FloatSortedMap.this.clear();
      }

      @Override
      public DoubleComparator comparator() {
         return AbstractDouble2FloatSortedMap.this.comparator();
      }

      @Override
      public double firstDouble() {
         return AbstractDouble2FloatSortedMap.this.firstDoubleKey();
      }

      @Override
      public double lastDouble() {
         return AbstractDouble2FloatSortedMap.this.lastDoubleKey();
      }

      @Override
      public DoubleSortedSet headSet(double to) {
         return AbstractDouble2FloatSortedMap.this.headMap(to).keySet();
      }

      @Override
      public DoubleSortedSet tailSet(double from) {
         return AbstractDouble2FloatSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public DoubleSortedSet subSet(double from, double to) {
         return AbstractDouble2FloatSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public DoubleBidirectionalIterator iterator(double from) {
         return new AbstractDouble2FloatSortedMap.KeySetIterator(
            AbstractDouble2FloatSortedMap.this.double2FloatEntrySet().iterator(new AbstractDouble2FloatMap.BasicEntry(from, 0.0F))
         );
      }

      @Override
      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2FloatSortedMap.KeySetIterator(Double2FloatSortedMaps.fastIterator(AbstractDouble2FloatSortedMap.this));
      }
   }

   protected static class KeySetIterator implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2FloatMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2FloatMap.Entry> i) {
         this.i = i;
      }

      @Override
      public double nextDouble() {
         return this.i.next().getDoubleKey();
      }

      @Override
      public double previousDouble() {
         return this.i.previous().getDoubleKey();
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
         return new AbstractDouble2FloatSortedMap.ValuesIterator(Double2FloatSortedMaps.fastIterator(AbstractDouble2FloatSortedMap.this));
      }

      @Override
      public boolean contains(float k) {
         return AbstractDouble2FloatSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractDouble2FloatSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractDouble2FloatSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements FloatIterator {
      protected final ObjectBidirectionalIterator<Double2FloatMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2FloatMap.Entry> i) {
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
