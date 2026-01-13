package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;

public abstract class AbstractByte2ObjectSortedMap<V> extends AbstractByte2ObjectMap<V> implements Byte2ObjectSortedMap<V> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractByte2ObjectSortedMap() {
   }

   @Override
   public ByteSortedSet keySet() {
      return new AbstractByte2ObjectSortedMap.KeySet();
   }

   @Override
   public ObjectCollection<V> values() {
      return new AbstractByte2ObjectSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractByteSortedSet {
      @Override
      public boolean contains(byte k) {
         return AbstractByte2ObjectSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractByte2ObjectSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractByte2ObjectSortedMap.this.clear();
      }

      @Override
      public ByteComparator comparator() {
         return AbstractByte2ObjectSortedMap.this.comparator();
      }

      @Override
      public byte firstByte() {
         return AbstractByte2ObjectSortedMap.this.firstByteKey();
      }

      @Override
      public byte lastByte() {
         return AbstractByte2ObjectSortedMap.this.lastByteKey();
      }

      @Override
      public ByteSortedSet headSet(byte to) {
         return AbstractByte2ObjectSortedMap.this.headMap(to).keySet();
      }

      @Override
      public ByteSortedSet tailSet(byte from) {
         return AbstractByte2ObjectSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public ByteSortedSet subSet(byte from, byte to) {
         return AbstractByte2ObjectSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public ByteBidirectionalIterator iterator(byte from) {
         return new AbstractByte2ObjectSortedMap.KeySetIterator<>(
            AbstractByte2ObjectSortedMap.this.byte2ObjectEntrySet().iterator(new AbstractByte2ObjectMap.BasicEntry<>(from, null))
         );
      }

      @Override
      public ByteBidirectionalIterator iterator() {
         return new AbstractByte2ObjectSortedMap.KeySetIterator<>(Byte2ObjectSortedMaps.fastIterator(AbstractByte2ObjectSortedMap.this));
      }
   }

   protected static class KeySetIterator<V> implements ByteBidirectionalIterator {
      protected final ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> i) {
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

   protected class ValuesCollection extends AbstractObjectCollection<V> {
      @Override
      public ObjectIterator<V> iterator() {
         return new AbstractByte2ObjectSortedMap.ValuesIterator<>(Byte2ObjectSortedMaps.fastIterator(AbstractByte2ObjectSortedMap.this));
      }

      @Override
      public boolean contains(Object k) {
         return AbstractByte2ObjectSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractByte2ObjectSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractByte2ObjectSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<V> implements ObjectIterator<V> {
      protected final ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Byte2ObjectMap.Entry<V>> i) {
         this.i = i;
      }

      @Override
      public V next() {
         return this.i.next().getValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
