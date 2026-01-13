package com.google.protobuf;

import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

class SmallSortedMap<K extends Comparable<K>, V> extends AbstractMap<K, V> {
   static final int DEFAULT_FIELD_MAP_ARRAY_SIZE = 16;
   private Object[] entries;
   private int entriesSize;
   private Map<K, V> overflowEntries = Collections.emptyMap();
   private boolean isImmutable;
   private volatile SmallSortedMap<K, V>.EntrySet lazyEntrySet;
   private Map<K, V> overflowEntriesDescending = Collections.emptyMap();

   static <FieldDescriptorT extends FieldSet.FieldDescriptorLite<FieldDescriptorT>> SmallSortedMap<FieldDescriptorT, Object> newFieldMap() {
      return new SmallSortedMap<FieldDescriptorT, Object>() {
         @Override
         public void makeImmutable() {
            if (!this.isImmutable()) {
               for (int i = 0; i < this.getNumArrayEntries(); i++) {
                  java.util.Map.Entry<FieldDescriptorT, Object> entry = this.getArrayEntryAt(i);
                  if (entry.getKey().isRepeated()) {
                     List<?> value = (List<?>)entry.getValue();
                     entry.setValue(Collections.unmodifiableList(value));
                  }
               }

               for (java.util.Map.Entry<FieldDescriptorT, Object> entry : this.getOverflowEntries()) {
                  if (entry.getKey().isRepeated()) {
                     List<?> value = (List<?>)entry.getValue();
                     entry.setValue(Collections.unmodifiableList(value));
                  }
               }
            }

            super.makeImmutable();
         }
      };
   }

   static <K extends Comparable<K>, V> SmallSortedMap<K, V> newInstanceForTest() {
      return new SmallSortedMap<>();
   }

   private SmallSortedMap() {
   }

   public void makeImmutable() {
      if (!this.isImmutable) {
         this.overflowEntries = this.overflowEntries.isEmpty() ? Collections.emptyMap() : Collections.unmodifiableMap(this.overflowEntries);
         this.overflowEntriesDescending = this.overflowEntriesDescending.isEmpty()
            ? Collections.emptyMap()
            : Collections.unmodifiableMap(this.overflowEntriesDescending);
         this.isImmutable = true;
      }
   }

   public boolean isImmutable() {
      return this.isImmutable;
   }

   public int getNumArrayEntries() {
      return this.entriesSize;
   }

   public java.util.Map.Entry<K, V> getArrayEntryAt(int index) {
      if (index >= this.entriesSize) {
         throw new ArrayIndexOutOfBoundsException(index);
      } else {
         return (SmallSortedMap.Entry)this.entries[index];
      }
   }

   public int getNumOverflowEntries() {
      return this.overflowEntries.size();
   }

   public Iterable<java.util.Map.Entry<K, V>> getOverflowEntries() {
      return this.overflowEntries.isEmpty() ? Collections.emptySet() : this.overflowEntries.entrySet();
   }

   @Override
   public int size() {
      return this.entriesSize + this.overflowEntries.size();
   }

   @Override
   public boolean containsKey(Object o) {
      K key = (K)o;
      return this.binarySearchInArray(key) >= 0 || this.overflowEntries.containsKey(key);
   }

   @Override
   public V get(Object o) {
      K key = (K)o;
      int index = this.binarySearchInArray(key);
      if (index >= 0) {
         SmallSortedMap<K, V>.Entry e = (SmallSortedMap.Entry)this.entries[index];
         return e.getValue();
      } else {
         return this.overflowEntries.get(key);
      }
   }

   public V put(K key, V value) {
      this.checkMutable();
      int index = this.binarySearchInArray(key);
      if (index >= 0) {
         SmallSortedMap<K, V>.Entry e = (SmallSortedMap.Entry)this.entries[index];
         return e.setValue(value);
      } else {
         this.ensureEntryArrayMutable();
         int insertionPoint = -(index + 1);
         if (insertionPoint >= 16) {
            return this.getOverflowEntriesMutable().put(key, value);
         } else {
            if (this.entriesSize == 16) {
               SmallSortedMap<K, V>.Entry lastEntryInArray = (SmallSortedMap.Entry)this.entries[15];
               this.entriesSize--;
               this.getOverflowEntriesMutable().put(lastEntryInArray.getKey(), lastEntryInArray.getValue());
            }

            System.arraycopy(this.entries, insertionPoint, this.entries, insertionPoint + 1, this.entries.length - insertionPoint - 1);
            this.entries[insertionPoint] = new SmallSortedMap.Entry(key, value);
            this.entriesSize++;
            return null;
         }
      }
   }

   @Override
   public void clear() {
      this.checkMutable();
      if (this.entriesSize != 0) {
         this.entries = null;
         this.entriesSize = 0;
      }

      if (!this.overflowEntries.isEmpty()) {
         this.overflowEntries.clear();
      }
   }

   @Override
   public V remove(Object o) {
      this.checkMutable();
      K key = (K)o;
      int index = this.binarySearchInArray(key);
      if (index >= 0) {
         return this.removeArrayEntryAt(index);
      } else {
         return this.overflowEntries.isEmpty() ? null : this.overflowEntries.remove(key);
      }
   }

   private V removeArrayEntryAt(int index) {
      this.checkMutable();
      V removed = (V)((SmallSortedMap.Entry)this.entries[index]).getValue();
      System.arraycopy(this.entries, index + 1, this.entries, index, this.entriesSize - index - 1);
      this.entriesSize--;
      if (!this.overflowEntries.isEmpty()) {
         Iterator<java.util.Map.Entry<K, V>> iterator = this.getOverflowEntriesMutable().entrySet().iterator();
         this.entries[this.entriesSize] = new SmallSortedMap.Entry(iterator.next());
         this.entriesSize++;
         iterator.remove();
      }

      return removed;
   }

   private int binarySearchInArray(K key) {
      int left = 0;
      int right = this.entriesSize - 1;
      if (right >= 0) {
         int cmp = key.compareTo((K)((SmallSortedMap.Entry)this.entries[right]).getKey());
         if (cmp > 0) {
            return -(right + 2);
         }

         if (cmp == 0) {
            return right;
         }
      }

      while (left <= right) {
         int mid = (left + right) / 2;
         int cmpx = key.compareTo((K)((SmallSortedMap.Entry)this.entries[mid]).getKey());
         if (cmpx < 0) {
            right = mid - 1;
         } else {
            if (cmpx <= 0) {
               return mid;
            }

            left = mid + 1;
         }
      }

      return -(left + 1);
   }

   @Override
   public Set<java.util.Map.Entry<K, V>> entrySet() {
      if (this.lazyEntrySet == null) {
         this.lazyEntrySet = new SmallSortedMap.EntrySet();
      }

      return this.lazyEntrySet;
   }

   Set<java.util.Map.Entry<K, V>> descendingEntrySet() {
      return new SmallSortedMap.DescendingEntrySet();
   }

   private void checkMutable() {
      if (this.isImmutable) {
         throw new UnsupportedOperationException();
      }
   }

   private SortedMap<K, V> getOverflowEntriesMutable() {
      this.checkMutable();
      if (this.overflowEntries.isEmpty() && !(this.overflowEntries instanceof TreeMap)) {
         this.overflowEntries = new TreeMap<>();
         this.overflowEntriesDescending = ((TreeMap)this.overflowEntries).descendingMap();
      }

      return (SortedMap<K, V>)this.overflowEntries;
   }

   private void ensureEntryArrayMutable() {
      this.checkMutable();
      if (this.entries == null) {
         this.entries = new Object[16];
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) {
         return true;
      } else if (!(o instanceof SmallSortedMap)) {
         return super.equals(o);
      } else {
         SmallSortedMap<?, ?> other = (SmallSortedMap<?, ?>)o;
         int size = this.size();
         if (size != other.size()) {
            return false;
         } else {
            int numArrayEntries = this.getNumArrayEntries();
            if (numArrayEntries != other.getNumArrayEntries()) {
               return this.entrySet().equals(other.entrySet());
            } else {
               for (int i = 0; i < numArrayEntries; i++) {
                  if (!this.getArrayEntryAt(i).equals(other.getArrayEntryAt(i))) {
                     return false;
                  }
               }

               return numArrayEntries != size ? this.overflowEntries.equals(other.overflowEntries) : true;
            }
         }
      }
   }

   @Override
   public int hashCode() {
      int h = 0;
      int listSize = this.getNumArrayEntries();

      for (int i = 0; i < listSize; i++) {
         h += this.entries[i].hashCode();
      }

      if (this.getNumOverflowEntries() > 0) {
         h += this.overflowEntries.hashCode();
      }

      return h;
   }

   private class DescendingEntryIterator implements Iterator<java.util.Map.Entry<K, V>> {
      private int pos = SmallSortedMap.this.entriesSize;
      private Iterator<java.util.Map.Entry<K, V>> lazyOverflowIterator;

      private DescendingEntryIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.pos > 0 && this.pos <= SmallSortedMap.this.entriesSize || this.getOverflowIterator().hasNext();
      }

      public java.util.Map.Entry<K, V> next() {
         return (java.util.Map.Entry<K, V>)(this.getOverflowIterator().hasNext()
            ? (java.util.Map.Entry)this.getOverflowIterator().next()
            : (SmallSortedMap.Entry)SmallSortedMap.this.entries[--this.pos]);
      }

      @Override
      public void remove() {
         throw new UnsupportedOperationException();
      }

      private Iterator<java.util.Map.Entry<K, V>> getOverflowIterator() {
         if (this.lazyOverflowIterator == null) {
            this.lazyOverflowIterator = SmallSortedMap.this.overflowEntriesDescending.entrySet().iterator();
         }

         return this.lazyOverflowIterator;
      }
   }

   private class DescendingEntrySet extends SmallSortedMap<K, V>.EntrySet {
      private DescendingEntrySet() {
      }

      @Override
      public Iterator<java.util.Map.Entry<K, V>> iterator() {
         return SmallSortedMap.this.new DescendingEntryIterator();
      }
   }

   private class Entry implements java.util.Map.Entry<K, V>, Comparable<SmallSortedMap<K, V>.Entry> {
      private final K key;
      private V value;

      Entry(java.util.Map.Entry<K, V> copy) {
         this(copy.getKey(), copy.getValue());
      }

      Entry(K key, V value) {
         this.key = key;
         this.value = value;
      }

      public K getKey() {
         return this.key;
      }

      @Override
      public V getValue() {
         return this.value;
      }

      public int compareTo(SmallSortedMap<K, V>.Entry other) {
         return this.getKey().compareTo(other.getKey());
      }

      @Override
      public V setValue(V newValue) {
         SmallSortedMap.this.checkMutable();
         V oldValue = this.value;
         this.value = newValue;
         return oldValue;
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (!(o instanceof java.util.Map.Entry)) {
            return false;
         } else {
            java.util.Map.Entry<?, ?> other = (java.util.Map.Entry<?, ?>)o;
            return this.equals(this.key, other.getKey()) && this.equals(this.value, other.getValue());
         }
      }

      @Override
      public int hashCode() {
         return (this.key == null ? 0 : this.key.hashCode()) ^ (this.value == null ? 0 : this.value.hashCode());
      }

      @Override
      public String toString() {
         return this.key + "=" + this.value;
      }

      private boolean equals(Object o1, Object o2) {
         return o1 == null ? o2 == null : o1.equals(o2);
      }
   }

   private class EntryIterator implements Iterator<java.util.Map.Entry<K, V>> {
      private int pos = -1;
      private boolean nextCalledBeforeRemove;
      private Iterator<java.util.Map.Entry<K, V>> lazyOverflowIterator;

      private EntryIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.pos + 1 < SmallSortedMap.this.entriesSize || !SmallSortedMap.this.overflowEntries.isEmpty() && this.getOverflowIterator().hasNext();
      }

      public java.util.Map.Entry<K, V> next() {
         this.nextCalledBeforeRemove = true;
         return (java.util.Map.Entry<K, V>)(++this.pos < SmallSortedMap.this.entriesSize
            ? (SmallSortedMap.Entry)SmallSortedMap.this.entries[this.pos]
            : (java.util.Map.Entry)this.getOverflowIterator().next());
      }

      @Override
      public void remove() {
         if (!this.nextCalledBeforeRemove) {
            throw new IllegalStateException("remove() was called before next()");
         } else {
            this.nextCalledBeforeRemove = false;
            SmallSortedMap.this.checkMutable();
            if (this.pos < SmallSortedMap.this.entriesSize) {
               SmallSortedMap.this.removeArrayEntryAt(this.pos--);
            } else {
               this.getOverflowIterator().remove();
            }
         }
      }

      private Iterator<java.util.Map.Entry<K, V>> getOverflowIterator() {
         if (this.lazyOverflowIterator == null) {
            this.lazyOverflowIterator = SmallSortedMap.this.overflowEntries.entrySet().iterator();
         }

         return this.lazyOverflowIterator;
      }
   }

   private class EntrySet extends AbstractSet<java.util.Map.Entry<K, V>> {
      private EntrySet() {
      }

      @Override
      public Iterator<java.util.Map.Entry<K, V>> iterator() {
         return SmallSortedMap.this.new EntryIterator();
      }

      @Override
      public int size() {
         return SmallSortedMap.this.size();
      }

      @Override
      public boolean contains(Object o) {
         java.util.Map.Entry<K, V> entry = (java.util.Map.Entry<K, V>)o;
         V existing = SmallSortedMap.this.get(entry.getKey());
         V value = entry.getValue();
         return existing == value || existing != null && existing.equals(value);
      }

      public boolean add(java.util.Map.Entry<K, V> entry) {
         if (!this.contains(entry)) {
            SmallSortedMap.this.put(entry.getKey(), entry.getValue());
            return true;
         } else {
            return false;
         }
      }

      @Override
      public boolean remove(Object o) {
         java.util.Map.Entry<K, V> entry = (java.util.Map.Entry<K, V>)o;
         if (this.contains(entry)) {
            SmallSortedMap.this.remove(entry.getKey());
            return true;
         } else {
            return false;
         }
      }

      @Override
      public void clear() {
         SmallSortedMap.this.clear();
      }
   }
}
