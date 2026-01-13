package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharIterator;
import java.util.Comparator;

public abstract class AbstractReference2CharSortedMap<K> extends AbstractReference2CharMap<K> implements Reference2CharSortedMap<K> {
   private static final long serialVersionUID = -1773560792952436569L;

   protected AbstractReference2CharSortedMap() {
   }

   @Override
   public ReferenceSortedSet<K> keySet() {
      return new AbstractReference2CharSortedMap.KeySet();
   }

   @Override
   public CharCollection values() {
      return new AbstractReference2CharSortedMap.ValuesCollection();
   }

   protected class KeySet extends AbstractReferenceSortedSet<K> {
      @Override
      public boolean contains(Object k) {
         return AbstractReference2CharSortedMap.this.containsKey(k);
      }

      @Override
      public int size() {
         return AbstractReference2CharSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractReference2CharSortedMap.this.clear();
      }

      @Override
      public Comparator<? super K> comparator() {
         return AbstractReference2CharSortedMap.this.comparator();
      }

      @Override
      public K first() {
         return AbstractReference2CharSortedMap.this.firstKey();
      }

      @Override
      public K last() {
         return AbstractReference2CharSortedMap.this.lastKey();
      }

      @Override
      public ReferenceSortedSet<K> headSet(K to) {
         return AbstractReference2CharSortedMap.this.headMap(to).keySet();
      }

      @Override
      public ReferenceSortedSet<K> tailSet(K from) {
         return AbstractReference2CharSortedMap.this.tailMap(from).keySet();
      }

      @Override
      public ReferenceSortedSet<K> subSet(K from, K to) {
         return AbstractReference2CharSortedMap.this.subMap(from, to).keySet();
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator(K from) {
         return new AbstractReference2CharSortedMap.KeySetIterator<>(
            AbstractReference2CharSortedMap.this.reference2CharEntrySet().iterator(new AbstractReference2CharMap.BasicEntry<>(from, '\u0000'))
         );
      }

      @Override
      public ObjectBidirectionalIterator<K> iterator() {
         return new AbstractReference2CharSortedMap.KeySetIterator<>(Reference2CharSortedMaps.fastIterator(AbstractReference2CharSortedMap.this));
      }
   }

   protected static class KeySetIterator<K> implements ObjectBidirectionalIterator<K> {
      protected final ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> i;

      public KeySetIterator(ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> i) {
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

   protected class ValuesCollection extends AbstractCharCollection {
      @Override
      public CharIterator iterator() {
         return new AbstractReference2CharSortedMap.ValuesIterator<>(Reference2CharSortedMaps.fastIterator(AbstractReference2CharSortedMap.this));
      }

      @Override
      public boolean contains(char k) {
         return AbstractReference2CharSortedMap.this.containsValue(k);
      }

      @Override
      public int size() {
         return AbstractReference2CharSortedMap.this.size();
      }

      @Override
      public void clear() {
         AbstractReference2CharSortedMap.this.clear();
      }
   }

   protected static class ValuesIterator<K> implements CharIterator {
      protected final ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> i;

      public ValuesIterator(ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> i) {
         this.i = i;
      }

      @Override
      public char nextChar() {
         return this.i.next().getCharValue();
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }
   }
}
