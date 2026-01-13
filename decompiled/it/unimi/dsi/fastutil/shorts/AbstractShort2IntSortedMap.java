package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractShort2IntSortedMap extends AbstractShort2IntMap implements Short2IntSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractShort2IntSortedMap() {
   }

   @Override
   public ShortSortedSet keySet() {
      return new AbstractShort2IntSortedMap.KeySet();
   }

   @Override
   public IntCollection values() {
      return new AbstractShort2IntSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractShortSortedSet {
      @Override
      public boolean contains(short k) {
         return AbstractShort2IntSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractShort2IntSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractShort2IntSortedMap.this.clear();
      }

      @Override
      public ShortComparator comparator() {
         return AbstractShort2IntSortedMap.this.comparator();
      }

      @Override
      public short firstShort() {
         return AbstractShort2IntSortedMap.this.firstShortKey();
      }

      @Override
      public short lastShort() {
         return AbstractShort2IntSortedMap.this.lastShortKey();
      }

      @Override
      public ShortSortedSet headSet(short to) {
         return AbstractShort2IntSortedMap.this.headMap(to).keySet();
      }

      @Override
      public ShortSortedSet tailSet(short from) {
         return AbstractShort2IntSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public ShortSortedSet subSet(short from, short to) {
         return AbstractShort2IntSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public ShortBidirectionalIterator iterator(short from) {
         return new AbstractShort2IntSortedMap.KeySetIterator(
            AbstractShort2IntSortedMap.this.short2IntEntrySet().iterator(new AbstractShort2IntMap.BasicEntry(from, 0))
         );
      }

      @Override
      public ShortBidirectionalIterator iterator() {
         return new AbstractShort2IntSortedMap.KeySetIterator(Short2IntSortedMaps.fastIterator(AbstractShort2IntSortedMap.this));
      }
   }

   protected static class KeySetIterator implements ShortBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Short2IntMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Short2IntMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractIntCollection {
      @Override
      public IntIterator iterator() {
         return new AbstractShort2IntSortedMap.ValuesIterator(Short2IntSortedMaps.fastIterator(AbstractShort2IntSortedMap.this));
      }

      @Override
      public boolean contains(int k) {
         return AbstractShort2IntSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractShort2IntSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractShort2IntSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements IntIterator {
      protected final ObjectBidirectionalIterator<Short2IntMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Short2IntMap.Entry> i) {
         this.i = i;
      }

      @Override
      public int nextInt() {
         return this.i.next().getIntValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
