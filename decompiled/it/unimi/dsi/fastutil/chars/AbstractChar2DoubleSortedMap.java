package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractChar2DoubleSortedMap extends AbstractChar2DoubleMap implements Char2DoubleSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractChar2DoubleSortedMap() {
   }

   @Override
   public CharSortedSet keySet() {
      return new AbstractChar2DoubleSortedMap.KeySet();
   }

   @Override
   public DoubleCollection values() {
      return new AbstractChar2DoubleSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractCharSortedSet {
      @Override
      public boolean contains(char k) {
         return AbstractChar2DoubleSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractChar2DoubleSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractChar2DoubleSortedMap.this.clear();
      }

      @Override
      public CharComparator comparator() {
         return AbstractChar2DoubleSortedMap.this.comparator();
      }

      @Override
      public char firstChar() {
         return AbstractChar2DoubleSortedMap.this.firstCharKey();
      }

      @Override
      public char lastChar() {
         return AbstractChar2DoubleSortedMap.this.lastCharKey();
      }

      @Override
      public CharSortedSet headSet(char to) {
         return AbstractChar2DoubleSortedMap.this.headMap(to).keySet();
      }

      @Override
      public CharSortedSet tailSet(char from) {
         return AbstractChar2DoubleSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public CharSortedSet subSet(char from, char to) {
         return AbstractChar2DoubleSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public CharBidirectionalIterator iterator(char from) {
         return new AbstractChar2DoubleSortedMap.KeySetIterator(
            AbstractChar2DoubleSortedMap.this.char2DoubleEntrySet().iterator(new AbstractChar2DoubleMap.BasicEntry(from, 0.0))
         );
      }

      @Override
      public CharBidirectionalIterator iterator() {
         return new AbstractChar2DoubleSortedMap.KeySetIterator(Char2DoubleSortedMaps.fastIterator(AbstractChar2DoubleSortedMap.this));
      }
   }

   protected static class KeySetIterator implements CharBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Char2DoubleMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Char2DoubleMap.Entry> i) {
         this.i = i;
      }

      @Override
      public char nextChar() {
         return this.i.next().getCharKey();
      }

      @Override
      public char previousChar() {
         return this.i.previous().getCharKey();
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

   protected class ValuesCollection extends AbstractDoubleCollection {
      @Override
      public DoubleIterator iterator() {
         return new AbstractChar2DoubleSortedMap.ValuesIterator(Char2DoubleSortedMaps.fastIterator(AbstractChar2DoubleSortedMap.this));
      }

      @Override
      public boolean contains(double k) {
         return AbstractChar2DoubleSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractChar2DoubleSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractChar2DoubleSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements DoubleIterator {
      protected final ObjectBidirectionalIterator<Char2DoubleMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Char2DoubleMap.Entry> i) {
         this.i = i;
      }

      @Override
      public double nextDouble() {
         return this.i.next().getDoubleValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
