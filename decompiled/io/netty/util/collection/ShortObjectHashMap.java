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

public class ShortObjectHashMap<V> implements ShortObjectMap<V> {
   public static final int DEFAULT_CAPACITY = 8;
   public static final float DEFAULT_LOAD_FACTOR = 0.5F;
   private static final Object NULL_VALUE = new Object();
   private int maxSize;
   private final float loadFactor;
   private short[] keys;
   private V[] values;
   private int size;
   private int mask;
   private final Set<Short> keySet = new ShortObjectHashMap.KeySet();
   private final Set<Entry<Short, V>> entrySet = new ShortObjectHashMap.EntrySet();
   private final Iterable<ShortObjectMap.PrimitiveEntry<V>> entries = new Iterable<ShortObjectMap.PrimitiveEntry<V>>() {
      @Override
      public Iterator<ShortObjectMap.PrimitiveEntry<V>> iterator() {
         return ShortObjectHashMap.this.new PrimitiveIterator();
      }
   };

   public ShortObjectHashMap() {
      this(8, 0.5F);
   }

   public ShortObjectHashMap(int initialCapacity) {
      this(initialCapacity, 0.5F);
   }

   public ShortObjectHashMap(int initialCapacity, float loadFactor) {
      if (!(loadFactor <= 0.0F) && !(loadFactor > 1.0F)) {
         this.loadFactor = loadFactor;
         int capacity = MathUtil.safeFindNextPositivePowerOfTwo(initialCapacity);
         this.mask = capacity - 1;
         this.keys = new short[capacity];
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
   public V get(short key) {
      int index = this.indexOf(key);
      return index == -1 ? null : toExternal(this.values[index]);
   }

   @Override
   public V put(short key, V value) {
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
   public void putAll(Map<? extends Short, ? extends V> sourceMap) {
      if (sourceMap instanceof ShortObjectHashMap) {
         ShortObjectHashMap<V> source = (ShortObjectHashMap<V>)sourceMap;

         for (int i = 0; i < source.values.length; i++) {
            V sourceValue = source.values[i];
            if (sourceValue != null) {
               this.put(source.keys[i], sourceValue);
            }
         }
      } else {
         for (Entry<? extends Short, ? extends V> entry : sourceMap.entrySet()) {
            this.put(entry.getKey(), (V)entry.getValue());
         }
      }
   }

   @Override
   public V remove(short key) {
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
      Arrays.fill(this.keys, (short)0);
      Arrays.fill(this.values, null);
      this.size = 0;
   }

   @Override
   public boolean containsKey(short key) {
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
   public Iterable<ShortObjectMap.PrimitiveEntry<V>> entries() {
      return this.entries;
   }

   @Override
   public Collection<V> values() {
      return new AbstractCollection<V>() {
         @Override
         public Iterator<V> iterator() {
            return new Iterator<V>() {
               final ShortObjectHashMap<V>.PrimitiveIterator iter = ShortObjectHashMap.this.new PrimitiveIterator();

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
            return ShortObjectHashMap.this.size;
         }
      };
   }

   @Override
   public int hashCode() {
      int hash = this.size;

      for (short key : this.keys) {
         hash ^= hashCode(key);
      }

      return hash;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      } else if (!(obj instanceof ShortObjectMap)) {
         return false;
      } else {
         ShortObjectMap other = (ShortObjectMap)obj;
         if (this.size != other.size()) {
            return false;
         } else {
            for (int i = 0; i < this.values.length; i++) {
               V value = this.values[i];
               if (value != null) {
                  short key = this.keys[i];
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

   public V put(Short key, V value) {
      return this.put(this.objectToKey(key), value);
   }

   @Override
   public V remove(Object key) {
      return this.remove(this.objectToKey(key));
   }

   @Override
   public Set<Short> keySet() {
      return this.keySet;
   }

   @Override
   public Set<Entry<Short, V>> entrySet() {
      return this.entrySet;
   }

   private short objectToKey(Object key) {
      return (Short)key;
   }

   private int indexOf(short key) {
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

   private int hashIndex(short key) {
      return hashCode(key) & this.mask;
   }

   private static int hashCode(short key) {
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
         short key = this.keys[i];
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
      short[] oldKeys = this.keys;
      V[] oldVals = this.values;
      this.keys = new short[newCapacity];
      V[] temp = (V[])(new Object[newCapacity]);
      this.values = temp;
      this.maxSize = this.calcMaxSize(newCapacity);
      this.mask = newCapacity - 1;

      for (int i = 0; i < oldVals.length; i++) {
         V oldVal = oldVals[i];
         if (oldVal != null) {
            short oldKey = oldKeys[i];
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

   protected String keyToString(short key) {
      return Short.toString(key);
   }

   private final class EntrySet extends AbstractSet<Entry<Short, V>> {
      private EntrySet() {
      }

      @Override
      public Iterator<Entry<Short, V>> iterator() {
         return ShortObjectHashMap.this.new MapIterator();
      }

      @Override
      public int size() {
         return ShortObjectHashMap.this.size();
      }
   }

   private final class KeySet extends AbstractSet<Short> {
      private KeySet() {
      }

      @Override
      public int size() {
         return ShortObjectHashMap.this.size();
      }

      @Override
      public boolean contains(Object o) {
         return ShortObjectHashMap.this.containsKey(o);
      }

      @Override
      public boolean remove(Object o) {
         return ShortObjectHashMap.this.remove(o) != null;
      }

      @Override
      public boolean retainAll(Collection<?> retainedKeys) {
         boolean changed = false;
         Iterator<ShortObjectMap.PrimitiveEntry<V>> iter = ShortObjectHashMap.this.entries().iterator();

         while (iter.hasNext()) {
            ShortObjectMap.PrimitiveEntry<V> entry = iter.next();
            if (!retainedKeys.contains(entry.key())) {
               changed = true;
               iter.remove();
            }
         }

         return changed;
      }

      @Override
      public void clear() {
         ShortObjectHashMap.this.clear();
      }

      @Override
      public Iterator<Short> iterator() {
         return new Iterator<Short>() {
            private final Iterator<Entry<Short, V>> iter = ShortObjectHashMap.this.entrySet.iterator();

            @Override
            public boolean hasNext() {
               return this.iter.hasNext();
            }

            public Short next() {
               return this.iter.next().getKey();
            }

            @Override
            public void remove() {
               this.iter.remove();
            }
         };
      }
   }

   final class MapEntry implements Entry<Short, V> {
      private final int entryIndex;

      MapEntry(int entryIndex) {
         this.entryIndex = entryIndex;
      }

      public Short getKey() {
         this.verifyExists();
         return ShortObjectHashMap.this.keys[this.entryIndex];
      }

      @Override
      public V getValue() {
         this.verifyExists();
         return ShortObjectHashMap.toExternal(ShortObjectHashMap.this.values[this.entryIndex]);
      }

      @Override
      public V setValue(V value) {
         this.verifyExists();
         V prevValue = ShortObjectHashMap.toExternal(ShortObjectHashMap.this.values[this.entryIndex]);
         ShortObjectHashMap.this.values[this.entryIndex] = ShortObjectHashMap.toInternal(value);
         return prevValue;
      }

      private void verifyExists() {
         if (ShortObjectHashMap.this.values[this.entryIndex] == null) {
            throw new IllegalStateException("The map entry has been removed");
         }
      }
   }

   private final class MapIterator implements Iterator<Entry<Short, V>> {
      private final ShortObjectHashMap<V>.PrimitiveIterator iter = ShortObjectHashMap.this.new PrimitiveIterator();

      private MapIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.iter.hasNext();
      }

      public Entry<Short, V> next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.iter.next();
            return ShortObjectHashMap.this.new MapEntry(this.iter.entryIndex);
         }
      }

      @Override
      public void remove() {
         this.iter.remove();
      }
   }

   private final class PrimitiveIterator implements Iterator<ShortObjectMap.PrimitiveEntry<V>>, ShortObjectMap.PrimitiveEntry<V> {
      private int prevIndex = -1;
      private int nextIndex = -1;
      private int entryIndex = -1;

      private PrimitiveIterator() {
      }

      private void scanNext() {
         while (++this.nextIndex != ShortObjectHashMap.this.values.length && ShortObjectHashMap.this.values[this.nextIndex] == null) {
         }
      }

      @Override
      public boolean hasNext() {
         if (this.nextIndex == -1) {
            this.scanNext();
         }

         return this.nextIndex != ShortObjectHashMap.this.values.length;
      }

      public ShortObjectMap.PrimitiveEntry<V> next() {
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
            if (ShortObjectHashMap.this.removeAt(this.prevIndex)) {
               this.nextIndex = this.prevIndex;
            }

            this.prevIndex = -1;
         }
      }

      @Override
      public short key() {
         return ShortObjectHashMap.this.keys[this.entryIndex];
      }

      @Override
      public V value() {
         return ShortObjectHashMap.toExternal(ShortObjectHashMap.this.values[this.entryIndex]);
      }

      @Override
      public void setValue(V value) {
         ShortObjectHashMap.this.values[this.entryIndex] = ShortObjectHashMap.toInternal(value);
      }
   }
}
