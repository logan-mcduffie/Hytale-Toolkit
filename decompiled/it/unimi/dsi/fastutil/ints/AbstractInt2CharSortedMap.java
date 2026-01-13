package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractInt2CharSortedMap extends AbstractInt2CharMap implements Int2CharSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2CharSortedMap() {
   }

   @Override
   public IntSortedSet keySet() {
      return new AbstractInt2CharSortedMap.KeySet();
   }

   @Override
   public CharCollection values() {
      return new AbstractInt2CharSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractIntSortedSet {
      @Override
      public boolean contains(int k) {
         return AbstractInt2CharSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractInt2CharSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractInt2CharSortedMap.this.clear();
      }

      @Override
      public IntComparator comparator() {
         return AbstractInt2CharSortedMap.this.comparator();
      }

      @Override
      public int firstInt() {
         return AbstractInt2CharSortedMap.this.firstIntKey();
      }

      @Override
      public int lastInt() {
         return AbstractInt2CharSortedMap.this.lastIntKey();
      }

      @Override
      public IntSortedSet headSet(int to) {
         return AbstractInt2CharSortedMap.this.headMap(to).keySet();
      }

      @Override
      public IntSortedSet tailSet(int from) {
         return AbstractInt2CharSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public IntSortedSet subSet(int from, int to) {
         return AbstractInt2CharSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public IntBidirectionalIterator iterator(int from) {
         return new AbstractInt2CharSortedMap.KeySetIterator(
            AbstractInt2CharSortedMap.this.int2CharEntrySet().iterator(new AbstractInt2CharMap.BasicEntry(from, '\u0000'))
         );
      }

      @Override
      public IntBidirectionalIterator iterator() {
         return new AbstractInt2CharSortedMap.KeySetIterator(Int2CharSortedMaps.fastIterator(AbstractInt2CharSortedMap.this));
      }
   }

   protected static class KeySetIterator implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2CharMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2CharMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractCharCollection {
      @Override
      public CharIterator iterator() {
         return new AbstractInt2CharSortedMap.ValuesIterator(Int2CharSortedMaps.fastIterator(AbstractInt2CharSortedMap.this));
      }

      @Override
      public boolean contains(char k) {
         return AbstractInt2CharSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractInt2CharSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractInt2CharSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements CharIterator {
      protected final ObjectBidirectionalIterator<Int2CharMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2CharMap.Entry> i) {
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
