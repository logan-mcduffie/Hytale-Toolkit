package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractInt2ByteSortedMap extends AbstractInt2ByteMap implements Int2ByteSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractInt2ByteSortedMap() {
   }

   @Override
   public IntSortedSet keySet() {
      return new AbstractInt2ByteSortedMap.KeySet();
   }

   @Override
   public ByteCollection values() {
      return new AbstractInt2ByteSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractIntSortedSet {
      @Override
      public boolean contains(int k) {
         return AbstractInt2ByteSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractInt2ByteSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractInt2ByteSortedMap.this.clear();
      }

      @Override
      public IntComparator comparator() {
         return AbstractInt2ByteSortedMap.this.comparator();
      }

      @Override
      public int firstInt() {
         return AbstractInt2ByteSortedMap.this.firstIntKey();
      }

      @Override
      public int lastInt() {
         return AbstractInt2ByteSortedMap.this.lastIntKey();
      }

      @Override
      public IntSortedSet headSet(int to) {
         return AbstractInt2ByteSortedMap.this.headMap(to).keySet();
      }

      @Override
      public IntSortedSet tailSet(int from) {
         return AbstractInt2ByteSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public IntSortedSet subSet(int from, int to) {
         return AbstractInt2ByteSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public IntBidirectionalIterator iterator(int from) {
         return new AbstractInt2ByteSortedMap.KeySetIterator(
            AbstractInt2ByteSortedMap.this.int2ByteEntrySet().iterator(new AbstractInt2ByteMap.BasicEntry(from, (byte)0))
         );
      }

      @Override
      public IntBidirectionalIterator iterator() {
         return new AbstractInt2ByteSortedMap.KeySetIterator(Int2ByteSortedMaps.fastIterator(AbstractInt2ByteSortedMap.this));
      }
   }

   protected static class KeySetIterator implements IntBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Int2ByteMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Int2ByteMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractByteCollection {
      @Override
      public ByteIterator iterator() {
         return new AbstractInt2ByteSortedMap.ValuesIterator(Int2ByteSortedMaps.fastIterator(AbstractInt2ByteSortedMap.this));
      }

      @Override
      public boolean contains(byte k) {
         return AbstractInt2ByteSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractInt2ByteSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractInt2ByteSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements ByteIterator {
      protected final ObjectBidirectionalIterator<Int2ByteMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Int2ByteMap.Entry> i) {
         this.i = i;
      }

      @Override
      public byte nextByte() {
         return this.i.next().getByteValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
