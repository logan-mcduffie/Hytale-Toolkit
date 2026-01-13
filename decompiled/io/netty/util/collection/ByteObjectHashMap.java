package io.netty.util.collection;

import io.netty.util.internal.MathUtil;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Map.Entry;

public class ByteObjectHashMap<V> implements ByteObjectMap<V> {
   public static final int DEFAULT_CAPACITY = 8;
   public static final float DEFAULT_LOAD_FACTOR = 0.5F;
   private static final Object NULL_VALUE = new Object();
   private int maxSize;
   private final float loadFactor;
   private byte[] keys;
   private V[] values;
   private int size;
   private int mask;
   private final Set<Byte> keySet = new ByteObjectHashMap.KeySet();
   private final Set<Entry<Byte, V>> entrySet = new ByteObjectHashMap.EntrySet();
   private final Iterable<ByteObjectMap.PrimitiveEntry<V>> entries = new Iterable<ByteObjectMap.PrimitiveEntry<V>>() {
      @Override
      public Iterator<ByteObjectMap.PrimitiveEntry<V>> iterator() {
         return ByteObjectHashMap.this.new PrimitiveIterator();
      }
   };

   public ByteObjectHashMap() {
      this(8, 0.5F);
   }

   public ByteObjectHashMap(int initialCapacity) {
      this(initialCapacity, 0.5F);
   }

   public ByteObjectHashMap(int initialCapacity, float loadFactor) {
      if (!(loadFactor <= 0.0F) && !(loadFactor > 1.0F)) {
         this.loadFactor = loadFactor;
         int capacity = MathUtil.safeFindNextPositivePowerOfTwo(initialCapacity);
         this.mask = capacity - 1;
         this.keys = new byte[capacity];
         V[] temp = (V[])(new Object[capacity]);
         this.values = temp;
         this.maxSize = this.calcMaxSize(capacity);
      } else {
         throw new IllegalArgumentException("loadFactor must be > 0 and <= 1");
      }
   }

   private static <T> T toExternal(T value) {
      assert value != null : "null is not a legitimate internal value. Concurrent Modification?";

      return value == NULL_VALUE ? null : value;
   }

   private static <T> T toInternal(T value) {
      return (T)(value == null ? NULL_VALUE : value);
   }

   @Override
   public V get(byte key) {
      int index = this.indexOf(key);
      return index == -1 ? null : toExternal(this.values[index]);
   }

   @Override
   public V put(byte key, V value) {
      int startIndex = this.hashIndex(key);
      int index = startIndex;

      while (this.values[index] != null) {
         if (this.keys[index] == key) {
            V previousValue = this.values[index];
            this.values[index] = toInternal(value);
            return toExternal(previousValue);
         }

         if ((index = this.probeNext(index)) == startIndex) {
            throw new IllegalStateException("Unable to insert");
         }
      }

      this.keys[index] = key;
      this.values[index] = toInternal(value);
      this.growSize();
      return null;
   }

   @Override
   public void putAll(Map<? extends Byte, ? extends V> sourceMap) {
      if (sourceMap instanceof ByteObjectHashMap) {
         ByteObjectHashMap<V> source = (ByteObjectHashMap<V>)sourceMap;

         for (int i = 0; i < source.values.length; i++) {
            V sourceValue = source.values[i];
            if (sourceValue != null) {
               this.put(source.keys[i], sourceValue);
            }
         }
      } else {
         for (Entry<? extends Byte, ? extends V> entry : sourceMap.entrySet()) {
            this.put(entry.getKey(), (V)entry.getValue());
         }
      }
   }

   @Override
   public V remove(byte key) {
      int index = this.indexOf(key);
      if (index == -1) {
         return null;
      } else {
         V prev = this.values[index];
         this.removeAt(index);
         return toExternal(prev);
      }
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.size == 0;
   }

   @Override
   public void clear() {
      Arrays.fill(this.keys, (byte)0);
      Arrays.fill(this.values, null);
      this.size = 0;
   }

   @Override
   public boolean containsKey(byte key) {
      return this.indexOf(key) >= 0;
   }

   @Override
   public boolean containsValue(Object value) {
      V v1 = toInternal((V)value);

      for (V v2 : this.values) {
         if (v2 != null && v2.equals(v1)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public Iterable<ByteObjectMap.PrimitiveEntry<V>> entries() {
      return this.entries;
   }

   @Override
   public Collection<V> values() {
      return new AbstractCollection<V>() {
         @Override
         public Iterator<V> iterator() {
            return new Iterator<V>() {
               final ByteObjectHashMap<V>.PrimitiveIterator iter = ByteObjectHashMap.this.new PrimitiveIterator();

               @Override
               public boolean hasNext() {
                  return this.iter.hasNext();
               }

               @Override
               public V next() {
                  return this.iter.next().value();
               }

               @Override
               public void remove() {
                  this.iter.remove();
               }
            };
         }

         @Override
         public int size() {
            return ByteObjectHashMap.this.size;
         }
      };
   }

   @Override
   public int hashCode() {
      int hash = this.size;

      for (byte key : this.keys) {
         hash ^= hashCode(key);
      }

      return hash;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof ByteObjectMap)) {
         return false;
      } else {
         ByteObjectMap other = (ByteObjectMap)obj;
         if (this.size != other.size()) {
            return false;
         } else {
            for (int i = 0; i < this.values.length; i++) {
               V value = this.values[i];
               if (value != null) {
                  byte key = this.keys[i];
                  Object otherValue = other.get(key);
                  if (value == NULL_VALUE) {
                     if (otherValue != null) {
                        return false;
                     }
                  } else if (!value.equals(otherValue)) {
                     return false;
                  }
               }
            }

            return true;
         }
      }
   }

   @Override
   public boolean containsKey(Object key) {
      return this.containsKey(this.objectToKey(key));
   }

   @Override
   public V get(Object key) {
      return this.get(this.objectToKey(key));
   }

   public V put(Byte key, V value) {
      return this.put(this.objectToKey(key), value);
   }

   @Override
   public V remove(Object key) {
      return this.remove(this.objectToKey(key));
   }

   @Override
   public Set<Byte> keySet() {
      return this.keySet;
   }

   @Override
   public Set<Entry<Byte, V>> entrySet() {
      return this.entrySet;
   }

   private byte objectToKey(Object key) {
      return (Byte)key;
   }

   private int indexOf(byte key) {
      int startIndex = this.hashIndex(key);
      int index = startIndex;

      while (this.values[index] != null) {
         if (key == this.keys[index]) {
            return index;
         }

         if ((index = this.probeNext(index)) == startIndex) {
            return -1;
         }
      }

      return -1;
   }

   private int hashIndex(byte key) {
      return hashCode(key) & this.mask;
   }

   private static int hashCode(byte key) {
      return key;
   }

   private int probeNext(int index) {
      return index + 1 & this.mask;
   }

   private void growSize() {
      this.size++;
      if (this.size > this.maxSize) {
         if (this.keys.length == Integer.MAX_VALUE) {
            throw new IllegalStateException("Max capacity reached at size=" + this.size);
         }

         this.rehash(this.keys.length << 1);
      }
   }

   private boolean removeAt(int index) {
      this.size--;
      this.keys[index] = 0;
      this.values[index] = null;
      int nextFree = index;
      int i = this.probeNext(index);

      for (V value = this.values[i]; value != null; value = this.values[i = this.probeNext(i)]) {
         byte key = this.keys[i];
         int bucket = this.hashIndex(key);
         if (i < bucket && (bucket <= nextFree || nextFree <= i) || bucket <= nextFree && nextFree <= i) {
            this.keys[nextFree] = key;
            this.values[nextFree] = value;
            this.keys[i] = 0;
            this.values[i] = null;
            nextFree = i;
         }
      }

      return nextFree != index;
   }

   private int calcMaxSize(int capacity) {
      int upperBound = capacity - 1;
      return Math.min(upperBound, (int)(capacity * this.loadFactor));
   }

   private void rehash(int newCapacity) {
      byte[] oldKeys = this.keys;
      V[] oldVals = this.values;
      this.keys = new byte[newCapacity];
      V[] temp = (V[])(new Object[newCapacity]);
      this.values = temp;
      this.maxSize = this.calcMaxSize(newCapacity);
      this.mask = newCapacity - 1;

      for (int i = 0; i < oldVals.length; i++) {
         V oldVal = oldVals[i];
         if (oldVal != null) {
            byte oldKey = oldKeys[i];
            int index = this.hashIndex(oldKey);

            while (this.values[index] != null) {
               index = this.probeNext(index);
            }

            this.keys[index] = oldKey;
            this.values[index] = oldVal;
         }
      }
   }

   @Override
   public String toString() {
      if (this.isEmpty()) {
         return "{}";
      } else {
         StringBuilder sb = new StringBuilder(4 * this.size);
         sb.append('{');
         boolean first = true;

         for (int i = 0; i < this.values.length; i++) {
            V value = this.values[i];
            if (value != null) {
               if (!first) {
                  sb.append(", ");
               }

               sb.append(this.keyToString(this.keys[i])).append('=').append(value == this ? "(this Map)" : toExternal(value));
               first = false;
            }
         }

         return sb.append('}').toString();
      }
   }

   protected String keyToString(byte key) {
      return Byte.toString(key);
   }

   private final class EntrySet extends AbstractSet<Entry<Byte, V>> {
      private EntrySet() {
      }

      @Override
      public Iterator<Entry<Byte, V>> iterator() {
         return ByteObjectHashMap.this.new MapIterator();
      }

      @Override
      public int size() {
         return ByteObjectHashMap.this.size();
      }
   }

   private final class KeySet extends AbstractSet<Byte> {
      private KeySet() {
      }

      @Override
      public int size() {
         return ByteObjectHashMap.this.size();
      }

      @Override
      public boolean contains(Object o) {
         return ByteObjectHashMap.this.containsKey(o);
      }

      @Override
      public boolean remove(Object o) {
         return ByteObjectHashMap.this.remove(o) != null;
      }

      @Override
      public boolean retainAll(Collection<?> retainedKeys) {
         boolean changed = false;
         Iterator<ByteObjectMap.PrimitiveEntry<V>> iter = ByteObjectHashMap.this.entries().iterator();

         while (iter.hasNext()) {
            ByteObjectMap.PrimitiveEntry<V> entry = iter.next();
            if (!retainedKeys.contains(entry.key())) {
               changed = true;
               iter.remove();
            }
         }

         return changed;
      }

      @Override
      public void clear() {
         ByteObjectHashMap.this.clear();
      }

      @Override
      public Iterator<Byte> iterator() {
         return new Iterator<Byte>() {
            private final Iterator<Entry<Byte, V>> iter = ByteObjectHashMap.this.entrySet.iterator();

            @Override
            public boolean hasNext() {
               return this.iter.hasNext();
            }

            public Byte next() {
               return this.iter.next().getKey();
            }

            @Override
            public void remove() {
               this.iter.remove();
            }
         };
      }
   }

   final class MapEntry implements Entry<Byte, V> {
      private final int entryIndex;

      MapEntry(int entryIndex) {
         this.entryIndex = entryIndex;
      }

      public Byte getKey() {
         this.verifyExists();
         return ByteObjectHashMap.this.keys[this.entryIndex];
      }

      @Override
      public V getValue() {
         this.verifyExists();
         return ByteObjectHashMap.toExternal(ByteObjectHashMap.this.values[this.entryIndex]);
      }

      @Override
      public V setValue(V value) {
         this.verifyExists();
         V prevValue = ByteObjectHashMap.toExternal(ByteObjectHashMap.this.values[this.entryIndex]);
         ByteObjectHashMap.this.values[this.entryIndex] = ByteObjectHashMap.toInternal(value);
         return prevValue;
      }

      private void verifyExists() {
         if (ByteObjectHashMap.this.values[this.entryIndex] == null) {
            throw new IllegalStateException("The map entry has been removed");
         }
      }
   }

   private final class MapIterator implements Iterator<Entry<Byte, V>> {
      private final ByteObjectHashMap<V>.PrimitiveIterator iter = ByteObjectHashMap.this.new PrimitiveIterator();

      private MapIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.iter.hasNext();
      }

      public Entry<Byte, V> next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.iter.next();
            return ByteObjectHashMap.this.new MapEntry(this.iter.entryIndex);
         }
      }

      @Override
      public void remove() {
         this.iter.remove();
      }
   }

   private final class PrimitiveIterator implements Iterator<ByteObjectMap.PrimitiveEntry<V>>, ByteObjectMap.PrimitiveEntry<V> {
      private int prevIndex = -1;
      private int nextIndex = -1;
      private int entryIndex = -1;

      private PrimitiveIterator() {
      }

      private void scanNext() {
         while (++this.nextIndex != ByteObjectHashMap.this.values.length && ByteObjectHashMap.this.values[this.nextIndex] == null) {
         }
      }

      @Override
      public boolean hasNext() {
         if (this.nextIndex == -1) {
            this.scanNext();
         }

         return this.nextIndex != ByteObjectHashMap.this.values.length;
      }

      public ByteObjectMap.PrimitiveEntry<V> next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.prevIndex = this.nextIndex;
            this.scanNext();
            this.entryIndex = this.prevIndex;
            return this;
         }
      }

      @Override
      public void remove() {
         if (this.prevIndex == -1) {
            throw new IllegalStateException("next must be called before each remove.");
         } else {
            if (ByteObjectHashMap.this.removeAt(this.prevIndex)) {
               this.nextIndex = this.prevIndex;
            }

            this.prevIndex = -1;
         }
      }

      @Override
      public byte key() {
         return ByteObjectHashMap.this.keys[this.entryIndex];
      }

      @Override
      public V value() {
         return ByteObjectHashMap.toExternal(ByteObjectHashMap.this.values[this.entryIndex]);
      }

      @Override
      public void setValue(V value) {
         ByteObjectHashMap.this.values[this.entryIndex] = ByteObjectHashMap.toInternal(value);
      }
   }
}
