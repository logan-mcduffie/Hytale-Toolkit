package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

public abstract class AbstractInt2ShortSortedMap extends AbstractInt2ShortMap implements Int2ShortSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2ShortSortedMap() {
   }

   @Override
   public IntSortedSet keySet() {
      return new AbstractInt2ShortSortedMap.KeySet();
   }

   @Override
   public ShortCollection values() {
      return new AbstractInt2ShortSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractIntSortedSet {
      @Override
      public boolean contains(int k) {
         return AbstractInt2ShortSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractInt2ShortSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractInt2ShortSortedMap.this.clear();
      }

      @Override
      public IntComparator comparator() {
         return AbstractInt2ShortSortedMap.this.comparator();
      }

      @Override
      public int firstInt() {
         return AbstractInt2ShortSortedMap.this.firstIntKey();
      }

      @Override
      public int lastInt() {
         return AbstractInt2ShortSortedMap.this.lastIntKey();
      }

      @Override
      public IntSortedSet headSet(int to) {
         return AbstractInt2ShortSortedMap.this.headMap(to).keySet();
      }

      @Override
      public IntSortedSet tailSet(int from) {
         return AbstractInt2ShortSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public IntSortedSet subSet(int from, int to) {
         return AbstractInt2ShortSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public IntBidirectionalIterator iterator(int from) {
         return new AbstractInt2ShortSortedMap.KeySetIterator(
            AbstractInt2ShortSortedMap.this.int2ShortEntrySet().iterator(new AbstractInt2ShortMap.BasicEntry(from, (short)0))
         );
      }

      @Override
      public IntBidirectionalIterator iterator() {
         return new AbstractInt2ShortSortedMap.KeySetIterator(Int2ShortSortedMaps.fastIterator(AbstractInt2ShortSortedMap.this));
      }
   }

   protected static class KeySetIterator implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2ShortMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2ShortMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractShortCollection {
      @Override
      public ShortIterator iterator() {
         return new AbstractInt2ShortSortedMap.ValuesIterator(Int2ShortSortedMaps.fastIterator(AbstractInt2ShortSortedMap.this));
      }

      @Override
      public boolean contains(short k) {
         return AbstractInt2ShortSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractInt2ShortSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractInt2ShortSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements ShortIterator {
      protected final ObjectBidirectionalIterator<Int2ShortMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2ShortMap.Entry> i) {
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
