package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.util.Comparator;

public abstract class AbstractObject2ShortSortedMap<K> extends AbstractObject2ShortMap<K> implements Object2ShortSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractObject2ShortSortedMap() {
   }

   @Override
   public ObjectSortedSet<K> keySet() {
      return new AbstractObject2ShortSortedMap.KeySet();
   }

   @Override
   public ShortCollection values() {
      return new AbstractObject2ShortSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractObjectSortedSet<K> {
      @Override
      public boolean contains(Object k) {
         return AbstractObject2ShortSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractObject2ShortSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractObject2ShortSortedMap.this.clear();
      }

      @Override
      public Comparator<? super K> comparator() {
         return AbstractObject2ShortSortedMap.this.comparator();
      }

      @Override
      public K first() {
         return AbstractObject2ShortSortedMap.this.firstKey();
      }

      @Override
      public K last() {
         return AbstractObject2ShortSortedMap.this.lastKey();
      }

      @Override
      public ObjectSortedSet<K> headSet(K to) {
         return AbstractObject2ShortSortedMap.this.headMap(to).keySet();
      }

      @Override
      public ObjectSortedSet<K> tailSet(K from) {
         return AbstractObject2ShortSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public ObjectSortedSet<K> subSet(K from, K to) {
         return AbstractObject2ShortSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return new AbstractObject2ShortSortedMap.KeySetIterator<>(
            AbstractObject2ShortSortedMap.this.object2ShortEntrySet().iterator(new AbstractObject2ShortMap.BasicEntry<>(from, (short)0))
         );
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractObject2ShortSortedMap.KeySetIterator<>(Object2ShortSortedMaps.fastIterator(AbstractObject2ShortSortedMap.this));
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> i) {
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

   protected class ValuesCollection extends AbstractShortCollection {
      @Override
      public ShortIterator iterator() {
         return new AbstractObject2ShortSortedMap.ValuesIterator<>(Object2ShortSortedMaps.fastIterator(AbstractObject2ShortSortedMap.this));
      }

      @Override
      public boolean contains(short k) {
         return AbstractObject2ShortSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractObject2ShortSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractObject2ShortSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<K> implements ShortIterator {
      protected final ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Object2ShortMap.Entry<K>> i) {
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
