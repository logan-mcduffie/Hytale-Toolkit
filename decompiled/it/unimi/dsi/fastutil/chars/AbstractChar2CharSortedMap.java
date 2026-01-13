package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractChar2CharSortedMap extends AbstractChar2CharMap implements Char2CharSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractChar2CharSortedMap() {
   }

   @Override
   public CharSortedSet keySet() {
      return new AbstractChar2CharSortedMap.KeySet();
   }

   @Override
   public CharCollection values() {
      return new AbstractChar2CharSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractCharSortedSet {
      @Override
      public boolean contains(char k) {
         return AbstractChar2CharSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractChar2CharSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractChar2CharSortedMap.this.clear();
      }

      @Override
      public CharComparator comparator() {
         return AbstractChar2CharSortedMap.this.comparator();
      }

      @Override
      public char firstChar() {
         return AbstractChar2CharSortedMap.this.firstCharKey();
      }

      @Override
      public char lastChar() {
         return AbstractChar2CharSortedMap.this.lastCharKey();
      }

      @Override
      public CharSortedSet headSet(char to) {
         return AbstractChar2CharSortedMap.this.headMap(to).keySet();
      }

      @Override
      public CharSortedSet tailSet(char from) {
         return AbstractChar2CharSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public CharSortedSet subSet(char from, char to) {
         return AbstractChar2CharSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public CharBidirectionalIterator iterator(char from) {
         return new AbstractChar2CharSortedMap.KeySetIterator(
            AbstractChar2CharSortedMap.this.char2CharEntrySet().iterator(new AbstractChar2CharMap.BasicEntry(from, '\u0000'))
         );
      }

      @Override
      public CharBidirectionalIterator iterator() {
         return new AbstractChar2CharSortedMap.KeySetIterator(Char2CharSortedMaps.fastIterator(AbstractChar2CharSortedMap.this));
      }
   }

   protected static class KeySetIterator implements CharBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Char2CharMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Char2CharMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractCharCollection {
      @Override
      public CharIterator iterator() {
         return new AbstractChar2CharSortedMap.ValuesIterator(Char2CharSortedMaps.fastIterator(AbstractChar2CharSortedMap.this));
      }

      @Override
      public boolean contains(char k) {
         return AbstractChar2CharSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractChar2CharSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractChar2CharSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements CharIterator {
      protected final ObjectBidirectionalIterator<Char2CharMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Char2CharMap.Entry> i) {
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
