package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import java.util.Comparator;

public abstract class AbstractReference2BooleanSortedMap<K> extends AbstractReference2BooleanMap<K> implements Reference2BooleanSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2BooleanSortedMap() {
   }

   @Override
   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2BooleanSortedMap.KeySet();
   }

   @Override
   public BooleanCollection values() {
      return new AbstractReference2BooleanSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractReferenceSortedSet<K> {
      @Override
      public boolean contains(Object k) {
         return AbstractReference2BooleanSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractReference2BooleanSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractReference2BooleanSortedMap.this.clear();
      }

      @Override
      public Comparator<? super K> comparator() {
         return AbstractReference2BooleanSortedMap.this.comparator();
      }

      @Override
      public K first() {
         return AbstractReference2BooleanSortedMap.this.firstKey();
      }

      @Override
      public K last() {
         return AbstractReference2BooleanSortedMap.this.lastKey();
      }

      @Override
      public ReferenceSortedSet<K> headSet(K to) {
         return AbstractReference2BooleanSortedMap.this.headMap(to).keySet();
      }

      @Override
      public ReferenceSortedSet<K> tailSet(K from) {
         return AbstractReference2BooleanSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public ReferenceSortedSet<K> subSet(K from, K to) {
         return AbstractReference2BooleanSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return new AbstractReference2BooleanSortedMap.KeySetIterator<>(
            AbstractReference2BooleanSortedMap.this.reference2BooleanEntrySet().iterator(new AbstractReference2BooleanMap.BasicEntry<>(from, false))
         );
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2BooleanSortedMap.KeySetIterator<>(Reference2BooleanSortedMaps.fastIterator(AbstractReference2BooleanSortedMap.this));
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> i) {
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

   protected class ValuesCollection extends AbstractBooleanCollection {
      @Override
      public BooleanIterator iterator() {
         return new AbstractReference2BooleanSortedMap.ValuesIterator<>(Reference2BooleanSortedMaps.fastIterator(AbstractReference2BooleanSortedMap.this));
      }

      @Override
      public boolean contains(boolean k) {
         return AbstractReference2BooleanSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractReference2BooleanSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractReference2BooleanSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<K> implements BooleanIterator {
      protected final ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2BooleanMap.Entry<K>> i) {
         this.i = i;
      }

      @Override
      public boolean nextBoolean() {
         return this.i.next().getBooleanValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
