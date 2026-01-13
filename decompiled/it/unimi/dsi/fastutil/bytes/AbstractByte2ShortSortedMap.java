package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;

public abstract class AbstractByte2ShortSortedMap extends AbstractByte2ShortMap implements Byte2ShortSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractByte2ShortSortedMap() {
   }

   @Override
   public ByteSortedSet keySet() {
      return new AbstractByte2ShortSortedMap.KeySet();
   }

   @Override
   public ShortCollection values() {
      return new AbstractByte2ShortSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractByteSortedSet {
      @Override
      public boolean contains(byte k) {
         return AbstractByte2ShortSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractByte2ShortSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractByte2ShortSortedMap.this.clear();
      }

      @Override
      public ByteComparator comparator() {
         return AbstractByte2ShortSortedMap.this.comparator();
      }

      @Override
      public byte firstByte() {
         return AbstractByte2ShortSortedMap.this.firstByteKey();
      }

      @Override
      public byte lastByte() {
         return AbstractByte2ShortSortedMap.this.lastByteKey();
      }

      @Override
      public ByteSortedSet headSet(byte to) {
         return AbstractByte2ShortSortedMap.this.headMap(to).keySet();
      }

      @Override
      public ByteSortedSet tailSet(byte from) {
         return AbstractByte2ShortSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public ByteSortedSet subSet(byte from, byte to) {
         return AbstractByte2ShortSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public ByteBidirectionalIterator iterator(byte from) {
         return new AbstractByte2ShortSortedMap.KeySetIterator(
            AbstractByte2ShortSortedMap.this.byte2ShortEntrySet().iterator(new AbstractByte2ShortMap.BasicEntry(from, (short)0))
         );
      }

      @Override
      public ByteBidirectionalIterator iterator() {
         return new AbstractByte2ShortSortedMap.KeySetIterator(Byte2ShortSortedMaps.fastIterator(AbstractByte2ShortSortedMap.this));
      }
   }

   protected static class KeySetIterator implements ByteBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Byte2ShortMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Byte2ShortMap.Entry> i) {
         this.i = i;
      }

      @Override
      public byte nextByte() {
         return this.i.next().getByteKey();
      }

      @Override
      public byte previousByte() {
         return this.i.previous().getByteKey();
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
         return new AbstractByte2ShortSortedMap.ValuesIterator(Byte2ShortSortedMaps.fastIterator(AbstractByte2ShortSortedMap.this));
      }

      @Override
      public boolean contains(short k) {
         return AbstractByte2ShortSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractByte2ShortSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractByte2ShortSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements ShortIterator {
      protected final ObjectBidirectionalIterator<Byte2ShortMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Byte2ShortMap.Entry> i) {
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
