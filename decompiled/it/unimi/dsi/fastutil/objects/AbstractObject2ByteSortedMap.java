package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import java.util.Comparator;

public abstract class AbstractObject2ByteSortedMap<K> extends AbstractObject2ByteMap<K> implements Object2ByteSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2ByteSortedMap() {
   }

   @Override
   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2ByteSortedMap.KeySet();
   }

   @Override
   public ByteCollection values() {
      return new AbstractObject2ByteSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractObjectSortedSet<K> {
      @Override
      public boolean contains(Object k) {
         return AbstractObject2ByteSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractObject2ByteSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractObject2ByteSortedMap.this.clear();
      }

      @Override
      public Comparator<? super K> comparator() {
         return AbstractObject2ByteSortedMap.this.comparator();
      }

      @Override
      public K first() {
         return AbstractObject2ByteSortedMap.this.firstKey();
      }

      @Override
      public K last() {
         return AbstractObject2ByteSortedMap.this.lastKey();
      }

      @Override
      public ObjectSortedSet<K> headSet(K to) {
         return AbstractObject2ByteSortedMap.this.headMap(to).keySet();
      }

      @Override
      public ObjectSortedSet<K> tailSet(K from) {
         return AbstractObject2ByteSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public ObjectSortedSet<K> subSet(K from, K to) {
         return AbstractObject2ByteSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return new AbstractObject2ByteSortedMap.KeySetIterator<>(
            AbstractObject2ByteSortedMap.this.object2ByteEntrySet().iterator(new AbstractObject2ByteMap.BasicEntry<>(from, (byte)0))
         );
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2ByteSortedMap.KeySetIterator<>(Object2ByteSortedMaps.fastIterator(AbstractObject2ByteSortedMap.this));
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> i) {
         this.i = i;
      }

      @Override
      public K next() {
         return this.i.next().getKey();
      }

      @Override
      public K previous() {
         return this.i.previous().getKey();
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
         return new AbstractObject2ByteSortedMap.ValuesIterator<>(Object2ByteSortedMaps.fastIterator(AbstractObject2ByteSortedMap.this));
      }

      @Override
      public boolean contains(byte k) {
         return AbstractObject2ByteSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractObject2ByteSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractObject2ByteSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<K> implements ByteIterator {
      protected final ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2ByteMap.Entry<K>> i) {
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
