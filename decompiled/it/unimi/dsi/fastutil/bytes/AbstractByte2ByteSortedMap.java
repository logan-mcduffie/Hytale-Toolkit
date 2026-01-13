package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;

public abstract class AbstractByte2ByteSortedMap extends AbstractByte2ByteMap implements Byte2ByteSortedMap {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractByte2ByteSortedMap() {
   }

   @Override
   public ByteSortedSet keySet() {
      return new AbstractByte2ByteSortedMap.KeySet();
   }

   @Override
   public ByteCollection values() {
      return new AbstractByte2ByteSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractByteSortedSet {
      @Override
      public boolean contains(byte k) {
         return AbstractByte2ByteSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractByte2ByteSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractByte2ByteSortedMap.this.clear();
      }

      @Override
      public ByteComparator comparator() {
         return AbstractByte2ByteSortedMap.this.comparator();
      }

      @Override
      public byte firstByte() {
         return AbstractByte2ByteSortedMap.this.firstByteKey();
      }

      @Override
      public byte lastByte() {
         return AbstractByte2ByteSortedMap.this.lastByteKey();
      }

      @Override
      public ByteSortedSet headSet(byte to) {
         return AbstractByte2ByteSortedMap.this.headMap(to).keySet();
      }

      @Override
      public ByteSortedSet tailSet(byte from) {
         return AbstractByte2ByteSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public ByteSortedSet subSet(byte from, byte to) {
         return AbstractByte2ByteSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public ByteBidirectionalIterator iterator(byte from) {
         return new AbstractByte2ByteSortedMap.KeySetIterator(
            AbstractByte2ByteSortedMap.this.byte2ByteEntrySet().iterator(new AbstractByte2ByteMap.BasicEntry(from, (byte)0))
         );
      }

      @Override
      public ByteBidirectionalIterator iterator() {
         return new AbstractByte2ByteSortedMap.KeySetIterator(Byte2ByteSortedMaps.fastIterator(AbstractByte2ByteSortedMap.this));
      }
   }

   protected static class KeySetIterator implements ByteBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Byte2ByteMap.Entry> i;

      public KeySetIterator(ObjectBidirectionalIterator<Byte2ByteMap.Entry> i) {
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

   protected class ValuesCollection extends AbstractByteCollection {
      @Override
      public ByteIterator iterator() {
         return new AbstractByte2ByteSortedMap.ValuesIterator(Byte2ByteSortedMaps.fastIterator(AbstractByte2ByteSortedMap.this));
      }

      @Override
      public boolean contains(byte k) {
         return AbstractByte2ByteSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractByte2ByteSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractByte2ByteSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator implements ByteIterator {
      protected final ObjectBidirectionalIterator<Byte2ByteMap.Entry> i;

      public ValuesIterator(ObjectBidirectionalIterator<Byte2ByteMap.Entry> i) {
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
