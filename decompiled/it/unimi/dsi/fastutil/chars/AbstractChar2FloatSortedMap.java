package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractChar2FloatSortedMap extends AbstractChar2FloatMap implements Char2FloatSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractChar2FloatSortedMap() {
   }

   @Override
   public CharSortedSet keySet() {
      return new AbstractChar2FloatSortedMap.KeySet();
   }

   @Override
   public FloatCollection values() {
      return new AbstractChar2FloatSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractCharSortedSet {
      @Override
      public boolean contains(char k) {
         return AbstractChar2FloatSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractChar2FloatSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractChar2FloatSortedMap.this.clear();
      }

      @Override
      public CharComparator comparator() {
         return AbstractChar2FloatSortedMap.this.comparator();
      }

      @Override
      public char firstChar() {
         return AbstractChar2FloatSortedMap.this.firstCharKey();
      }

      @Override
      public char lastChar() {
         return AbstractChar2FloatSortedMap.this.lastCharKey();
      }

      @Override
      public CharSortedSet headSet(char to) {
         return AbstractChar2FloatSortedMap.this.headMap(to).keySet();
      }

      @Override
      public CharSortedSet tailSet(char from) {
         return AbstractChar2FloatSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public CharSortedSet subSet(char from, char to) {
         return AbstractChar2FloatSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public CharBidirectionalIterator iterator(char from) {
         return new AbstractChar2FloatSortedMap.KeySetIterator(
            AbstractChar2FloatSortedMap.this.char2FloatEntrySet().iterator(new AbstractChar2FloatMap.BasicEntry(from, 0.0F))
         );
      }

      @Override
      public CharBidirectionalIterator iterator() {
         return new AbstractChar2FloatSortedMap.KeySetIterator(Char2FloatSortedMaps.fastIterator(AbstractChar2FloatSortedMap.this));
      }
   }

   protected static class KeySetIterator implements CharBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Char2FloatMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Char2FloatMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractFloatCollection {
      @Override
      public FloatIterator iterator() {
         return new AbstractChar2FloatSortedMap.ValuesIterator(Char2FloatSortedMaps.fastIterator(AbstractChar2FloatSortedMap.this));
      }

      @Override
      public boolean contains(float k) {
         return AbstractChar2FloatSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractChar2FloatSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractChar2FloatSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements FloatIterator {
      protected final ObjectBidirectionalIterator<Char2FloatMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Char2FloatMap.Entry> i) {
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
