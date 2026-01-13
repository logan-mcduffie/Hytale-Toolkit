package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractShort2CharSortedMap extends AbstractShort2CharMap implements Short2CharSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractShort2CharSortedMap() {
   }

   @Override
   public ShortSortedSet keySet() {
      return new AbstractShort2CharSortedMap.KeySet();
   }

   @Override
   public CharCollection values() {
      return new AbstractShort2CharSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractShortSortedSet {
      @Override
      public boolean contains(short k) {
         return AbstractShort2CharSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractShort2CharSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractShort2CharSortedMap.this.clear();
      }

      @Override
      public ShortComparator comparator() {
         return AbstractShort2CharSortedMap.this.comparator();
      }

      @Override
      public short firstShort() {
         return AbstractShort2CharSortedMap.this.firstShortKey();
      }

      @Override
      public short lastShort() {
         return AbstractShort2CharSortedMap.this.lastShortKey();
      }

      @Override
      public ShortSortedSet headSet(short to) {
         return AbstractShort2CharSortedMap.this.headMap(to).keySet();
      }

      @Override
      public ShortSortedSet tailSet(short from) {
         return AbstractShort2CharSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public ShortSortedSet subSet(short from, short to) {
         return AbstractShort2CharSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public ShortBidirectionalIterator iterator(short from) {
         return new AbstractShort2CharSortedMap.KeySetIterator(
            AbstractShort2CharSortedMap.this.short2CharEntrySet().iterator(new AbstractShort2CharMap.BasicEntry(from, '\u0000'))
         );
      }

      @Override
      public ShortBidirectionalIterator iterator() {
         return new AbstractShort2CharSortedMap.KeySetIterator(Short2CharSortedMaps.fastIterator(AbstractShort2CharSortedMap.this));
      }
   }

   protected static class KeySetIterator implements ShortBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Short2CharMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Short2CharMap.Entry> i) {
         this.i = i;
      }

      @Override
      public short nextShort() {
         return this.i.next().getShortKey();
      }

      @Override
      public short previousShort() {
         return this.i.previous().getShortKey();
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
         return new AbstractShort2CharSortedMap.ValuesIterator(Short2CharSortedMaps.fastIterator(AbstractShort2CharSortedMap.this));
      }

      @Override
      public boolean contains(char k) {
         return AbstractShort2CharSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractShort2CharSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractShort2CharSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements CharIterator {
      protected final ObjectBidirectionalIterator<Short2CharMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Short2CharMap.Entry> i) {
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
