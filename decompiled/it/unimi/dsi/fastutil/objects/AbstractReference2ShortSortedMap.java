package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.util.Comparator;

public abstract class AbstractReference2ShortSortedMap<K> extends AbstractReference2ShortMap<K> implements Reference2ShortSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2ShortSortedMap() {
   }

   @Override
   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2ShortSortedMap.KeySet();
   }

   @Override
   public ShortCollection values() {
      return new AbstractReference2ShortSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractReferenceSortedSet<K> {
      @Override
      public boolean contains(Object k) {
         return AbstractReference2ShortSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractReference2ShortSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractReference2ShortSortedMap.this.clear();
      }

      @Override
      public Comparator<? super K> comparator() {
         return AbstractReference2ShortSortedMap.this.comparator();
      }

      @Override
      public K first() {
         return AbstractReference2ShortSortedMap.this.firstKey();
      }

      @Override
      public K last() {
         return AbstractReference2ShortSortedMap.this.lastKey();
      }

      @Override
      public ReferenceSortedSet<K> headSet(K to) {
         return AbstractReference2ShortSortedMap.this.headMap(to).keySet();
      }

      @Override
      public ReferenceSortedSet<K> tailSet(K from) {
         return AbstractReference2ShortSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public ReferenceSortedSet<K> subSet(K from, K to) {
         return AbstractReference2ShortSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return new AbstractReference2ShortSortedMap.KeySetIterator<>(
            AbstractReference2ShortSortedMap.this.reference2ShortEntrySet().iterator(new AbstractReference2ShortMap.BasicEntry<>(from, (short)0))
         );
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2ShortSortedMap.KeySetIterator<>(Reference2ShortSortedMaps.fastIterator(AbstractReference2ShortSortedMap.this));
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2ShortMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2ShortMap.Entry<K>> i) {
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
         return new AbstractReference2ShortSortedMap.ValuesIterator<>(Reference2ShortSortedMaps.fastIterator(AbstractReference2ShortSortedMap.this));
      }

      @Override
      public boolean contains(short k) {
         return AbstractReference2ShortSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractReference2ShortSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractReference2ShortSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<K> implements ShortIterator {
      protected final ObjectBidirectionalIterator<Reference2ShortMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2ShortMap.Entry<K>> i) {
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
