package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractDouble2CharSortedMap extends AbstractDouble2CharMap implements Double2CharSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractDouble2CharSortedMap() {
   }

   @Override
   public DoubleSortedSet keySet() {
      return new AbstractDouble2CharSortedMap.KeySet();
   }

   @Override
   public CharCollection values() {
      return new AbstractDouble2CharSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractDoubleSortedSet {
      @Override
      public boolean contains(double k) {
         return AbstractDouble2CharSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractDouble2CharSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractDouble2CharSortedMap.this.clear();
      }

      @Override
      public DoubleComparator comparator() {
         return AbstractDouble2CharSortedMap.this.comparator();
      }

      @Override
      public double firstDouble() {
         return AbstractDouble2CharSortedMap.this.firstDoubleKey();
      }

      @Override
      public double lastDouble() {
         return AbstractDouble2CharSortedMap.this.lastDoubleKey();
      }

      @Override
      public DoubleSortedSet headSet(double to) {
         return AbstractDouble2CharSortedMap.this.headMap(to).keySet();
      }

      @Override
      public DoubleSortedSet tailSet(double from) {
         return AbstractDouble2CharSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public DoubleSortedSet subSet(double from, double to) {
         return AbstractDouble2CharSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public DoubleBidirectionalIterator iterator(double from) {
         return new AbstractDouble2CharSortedMap.KeySetIterator(
            AbstractDouble2CharSortedMap.this.double2CharEntrySet().iterator(new AbstractDouble2CharMap.BasicEntry(from, '\u0000'))
         );
      }

      @Override
      public DoubleBidirectionalIterator iterator() {
         return new AbstractDouble2CharSortedMap.KeySetIterator(Double2CharSortedMaps.fastIterator(AbstractDouble2CharSortedMap.this));
      }
   }

   protected static class KeySetIterator implements DoubleBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Double2CharMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Double2CharMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractCharCollection {
      @Override
      public CharIterator iterator() {
         return new AbstractDouble2CharSortedMap.ValuesIterator(Double2CharSortedMaps.fastIterator(AbstractDouble2CharSortedMap.this));
      }

      @Override
      public boolean contains(char k) {
         return AbstractDouble2CharSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractDouble2CharSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractDouble2CharSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements CharIterator {
      protected final ObjectBidirectionalIterator<Double2CharMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Double2CharMap.Entry> i) {
         this.i = i;
      }

      @Override
      public char nextChar() {
         return this.i.next().getCharValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
