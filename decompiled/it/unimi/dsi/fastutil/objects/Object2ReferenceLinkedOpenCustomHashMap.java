package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;

public class Object2ReferenceLinkedOpenCustomHashMap<K, V> extends AbstractObject2ReferenceSortedMap<K, V> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
   protected transient V[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected Hash.Strategy<? super K> strategy;
   protected transient int first = -1;
   protected transient int last = -1;
   protected transient long[] link;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Object2ReferenceSortedMap.FastSortedEntrySet<K, V> entries;
   protected transient ObjectSortedSet<K> keys;
   protected transient ReferenceCollection<V> values;

   public Object2ReferenceLinkedOpenCustomHashMap(int expected, float f, Hash.Strategy<? super K> strategy) {
      this.strategy = strategy;
      if (f <= 0.0F || f >= 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      } else if (expected < 0) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.arraySize(expected, f);
         this.mask = this.n - 1;
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = (K[])(new Object[this.n + 1]);
         this.value = (V[])(new Object[this.n + 1]);
         this.link = new long[this.n + 1];
      }
   }

   public Object2ReferenceLinkedOpenCustomHashMap(int expected, Hash.Strategy<? super K> strategy) {
      this(expected, 0.75F, strategy);
   }

   public Object2ReferenceLinkedOpenCustomHashMap(Hash.Strategy<? super K> strategy) {
      this(16, 0.75F, strategy);
   }

   public Object2ReferenceLinkedOpenCustomHashMap(Map<? extends K, ? extends V> m, float f, Hash.Strategy<? super K> strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Object2ReferenceLinkedOpenCustomHashMap(Map<? extends K, ? extends V> m, Hash.Strategy<? super K> strategy) {
      this(m, 0.75F, strategy);
   }

   public Object2ReferenceLinkedOpenCustomHashMap(Object2ReferenceMap<K, V> m, float f, Hash.Strategy<? super K> strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Object2ReferenceLinkedOpenCustomHashMap(Object2ReferenceMap<K, V> m, Hash.Strategy<? super K> strategy) {
      this(m, 0.75F, strategy);
   }

   public Object2ReferenceLinkedOpenCustomHashMap(K[] k, V[] v, float f, Hash.Strategy<? super K> strategy) {
      this(k.length, f, strategy);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Object2ReferenceLinkedOpenCustomHashMap(K[] k, V[] v, Hash.Strategy<? super K> strategy) {
      this(k, v, 0.75F, strategy);
   }

   public Hash.Strategy<? super K> strategy() {
      return this.strategy;
   }

   private int realSize() {
      return this.containsNullKey ? this.size - 1 : this.size;
   }

   public void ensureCapacity(int capacity) {
      int needed = HashCommon.arraySize(capacity, this.f);
      if (needed > this.n) {
         this.rehash(needed);
      }
   }

   private void tryCapacity(long capacity) {
      int needed = (int)Math.min(1073741824L, Math.max(2L, HashCommon.nextPowerOfTwo((long)Math.ceil((float)capacity / this.f))));
      if (needed > this.n) {
         this.rehash(needed);
      }
   }

   private V removeEntry(int pos) {
      V oldValue = this.value[pos];
      this.value[pos] = null;
      this.size--;
      this.fixPointers(pos);
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private V removeNullEntry() {
      this.containsNullKey = false;
      this.key[this.n] = null;
      V oldValue = this.value[this.n];
      this.value[this.n] = null;
      this.size--;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(K k) {
      if (this.strategy.equals(k, null)) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == null) {
            return -(pos + 1);
         } else if (this.strategy.equals(k, curr)) {
            return pos;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals(k, curr)) {
                  return pos;
               }
            }

            return -(pos + 1);
         }
      }
   }

   private void insert(int pos, K k, V v) {
      if (pos == this.n) {
         this.containsNullKey = true;
      }

      this.key[pos] = k;
      this.value[pos] = v;
      if (this.size == 0) {
         this.first = this.last = pos;
         this.link[pos] = -1L;
      } else {
         this.link[this.last] = this.link[this.last] ^ (this.link[this.last] ^ pos & 4294967295L) & 4294967295L;
         this.link[pos] = (this.last & 4294967295L) << 32 | 4294967295L;
         this.last = pos;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }
   }

   @Override
   public V put(K k, V v) {
      int pos = this.find(k);
      if (pos < 0) {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      } else {
         V oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   protected final void shiftKeys(int pos) {
      K[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         K curr;
         for (pos = pos + 1 & this.mask; (curr = key[pos]) != null; pos = pos + 1 & this.mask) {
            int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               this.value[last] = this.value[pos];
               this.fixPointers(pos, last);
               continue label30;
            }
         }

         key[last] = null;
         this.value[last] = null;
         return;
      }
   }

   @Override
   public V remove(Object k) {
      if (this.strategy.equals((K)k, null)) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode((K)k)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (this.strategy.equals((K)k, curr)) {
            return this.removeEntry(pos);
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals((K)k, curr)) {
                  return this.removeEntry(pos);
               }
            }

            return this.defRetValue;
         }
      }
   }

   private V setValue(int pos, V v) {
      V oldValue = this.value[pos];
      this.value[pos] = v;
      return oldValue;
   }

   public V removeFirst() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         int pos = this.first;
         if (this.size == 1) {
            this.first = this.last = -1;
         } else {
            this.first = (int)this.link[pos];
            if (0 <= this.first) {
               this.link[this.first] = this.link[this.first] | -4294967296L;
            }
         }

         this.size--;
         V v = this.value[pos];
         if (pos == this.n) {
            this.containsNullKey = false;
            this.key[this.n] = null;
            this.value[this.n] = null;
         } else {
            this.shiftKeys(pos);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return v;
      }
   }

   public V removeLast() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         int pos = this.last;
         if (this.size == 1) {
            this.first = this.last = -1;
         } else {
            this.last = (int)(this.link[pos] >>> 32);
            if (0 <= this.last) {
               this.link[this.last] = this.link[this.last] | 4294967295L;
            }
         }

         this.size--;
         V v = this.value[pos];
         if (pos == this.n) {
            this.containsNullKey = false;
            this.key[this.n] = null;
            this.value[this.n] = null;
         } else {
            this.shiftKeys(pos);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return v;
      }
   }

   private void moveIndexToFirst(int i) {
      if (this.size != 1 && this.first != i) {
         if (this.last == i) {
            this.last = (int)(this.link[i] >>> 32);
            this.link[this.last] = this.link[this.last] | 4294967295L;
         } else {
            long linki = this.link[i];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            this.link[prev] = this.link[prev] ^ (this.link[prev] ^ linki & 4294967295L) & 4294967295L;
            this.link[next] = this.link[next] ^ (this.link[next] ^ linki & -4294967296L) & -4294967296L;
         }

         this.link[this.first] = this.link[this.first] ^ (this.link[this.first] ^ (i & 4294967295L) << 32) & -4294967296L;
         this.link[i] = -4294967296L | this.first & 4294967295L;
         this.first = i;
      }
   }

   private void moveIndexToLast(int i) {
      if (this.size != 1 && this.last != i) {
         if (this.first == i) {
            this.first = (int)this.link[i];
            this.link[this.first] = this.link[this.first] | -4294967296L;
         } else {
            long linki = this.link[i];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            this.link[prev] = this.link[prev] ^ (this.link[prev] ^ linki & 4294967295L) & 4294967295L;
            this.link[next] = this.link[next] ^ (this.link[next] ^ linki & -4294967296L) & -4294967296L;
         }

         this.link[this.last] = this.link[this.last] ^ (this.link[this.last] ^ i & 4294967295L) & 4294967295L;
         this.link[i] = (this.last & 4294967295L) << 32 | 4294967295L;
         this.last = i;
      }
   }

   public V getAndMoveToFirst(K k) {
      if (this.strategy.equals(k, null)) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (this.strategy.equals(k, curr)) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals(k, curr)) {
                  this.moveIndexToFirst(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public V getAndMoveToLast(K k) {
      if (this.strategy.equals(k, null)) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (this.strategy.equals(k, curr)) {
            this.moveIndexToLast(pos);
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals(k, curr)) {
                  this.moveIndexToLast(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public V putAndMoveToFirst(K k, V v) {
      int pos;
      if (this.strategy.equals(k, null)) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         K[] key = this.key;
         K curr;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) != null) {
            if (this.strategy.equals(curr, k)) {
               this.moveIndexToFirst(pos);
               return this.setValue(pos, v);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals(curr, k)) {
                  this.moveIndexToFirst(pos);
                  return this.setValue(pos, v);
               }
            }
         }
      }

      this.key[pos] = k;
      this.value[pos] = v;
      if (this.size == 0) {
         this.first = this.last = pos;
         this.link[pos] = -1L;
      } else {
         this.link[this.first] = this.link[this.first] ^ (this.link[this.first] ^ (pos & 4294967295L) << 32) & -4294967296L;
         this.link[pos] = -4294967296L | this.first & 4294967295L;
         this.first = pos;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return this.defRetValue;
   }

   public V putAndMoveToLast(K k, V v) {
      int pos;
      if (this.strategy.equals(k, null)) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         K[] key = this.key;
         K curr;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) != null) {
            if (this.strategy.equals(curr, k)) {
               this.moveIndexToLast(pos);
               return this.setValue(pos, v);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals(curr, k)) {
                  this.moveIndexToLast(pos);
                  return this.setValue(pos, v);
               }
            }
         }
      }

      this.key[pos] = k;
      this.value[pos] = v;
      if (this.size == 0) {
         this.first = this.last = pos;
         this.link[pos] = -1L;
      } else {
         this.link[this.last] = this.link[this.last] ^ (this.link[this.last] ^ pos & 4294967295L) & 4294967295L;
         this.link[pos] = (this.last & 4294967295L) << 32 | 4294967295L;
         this.last = pos;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return this.defRetValue;
   }

   @Override
   public V get(Object k) {
      if (this.strategy.equals((K)k, null)) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode((K)k)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (this.strategy.equals((K)k, curr)) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals((K)k, curr)) {
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public boolean containsKey(Object k) {
      if (this.strategy.equals((K)k, null)) {
         return this.containsNullKey;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode((K)k)) & this.mask]) == null) {
            return false;
         } else if (this.strategy.equals((K)k, curr)) {
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals((K)k, curr)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean containsValue(Object v) {
      V[] value = this.value;
      K[] key = this.key;
      if (this.containsNullKey && value[this.n] == v) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (key[i] != null && value[i] == v) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public V getOrDefault(Object k, V defaultValue) {
      if (this.strategy.equals((K)k, null)) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode((K)k)) & this.mask]) == null) {
            return defaultValue;
         } else if (this.strategy.equals((K)k, curr)) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals((K)k, curr)) {
                  return this.value[pos];
               }
            }

            return defaultValue;
         }
      }
   }

   @Override
   public V putIfAbsent(K k, V v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(Object k, Object v) {
      if (this.strategy.equals((K)k, null)) {
         if (this.containsNullKey && v == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode((K)k)) & this.mask]) == null) {
            return false;
         } else if (this.strategy.equals((K)k, curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals((K)k, curr) && v == this.value[pos]) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(K k, V oldValue, V v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public V replace(K k, V v) {
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         V oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   @Override
   public V computeIfAbsent(K key, Object2ReferenceFunction<? super K, ? extends V> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         V newValue = (V)mappingFunction.get(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else if (this.value[pos] == null) {
         return this.defRetValue;
      } else {
         V newValue = (V)remappingFunction.apply(k, this.value[pos]);
         if (newValue == null) {
            if (this.strategy.equals(k, null)) {
               this.removeNullEntry();
            } else {
               this.removeEntry(pos);
            }

            return this.defRetValue;
         } else {
            return this.value[pos] = newValue;
         }
      }
   }

   @Override
   public V compute(K k, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      V newValue = (V)remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (this.strategy.equals(k, null)) {
               this.removeNullEntry();
            } else {
               this.removeEntry(pos);
            }
         }

         return this.defRetValue;
      } else if (pos < 0) {
         this.insert(-pos - 1, k, newValue);
         return newValue;
      } else {
         return this.value[pos] = newValue;
      }
   }

   @Override
   public V merge(K k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      Objects.requireNonNull(v);
      int pos = this.find(k);
      if (pos >= 0 && this.value[pos] != null) {
         V newValue = (V)remappingFunction.apply(this.value[pos], v);
         if (newValue == null) {
            if (this.strategy.equals(k, null)) {
               this.removeNullEntry();
            } else {
               this.removeEntry(pos);
            }

            return this.defRetValue;
         } else {
            return this.value[pos] = newValue;
         }
      } else {
         if (pos < 0) {
            this.insert(-pos - 1, k, v);
         } else {
            this.value[pos] = v;
         }

         return v;
      }
   }

   @Override
   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
         Arrays.fill(this.key, null);
         Arrays.fill(this.value, null);
         this.first = this.last = -1;
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

   protected void fixPointers(int i) {
      if (this.size == 0) {
         this.first = this.last = -1;
      } else if (this.first == i) {
         this.first = (int)this.link[i];
         if (0 <= this.first) {
            this.link[this.first] = this.link[this.first] | -4294967296L;
         }
      } else if (this.last == i) {
         this.last = (int)(this.link[i] >>> 32);
         if (0 <= this.last) {
            this.link[this.last] = this.link[this.last] | 4294967295L;
         }
      } else {
         long linki = this.link[i];
         int prev = (int)(linki >>> 32);
         int next = (int)linki;
         this.link[prev] = this.link[prev] ^ (this.link[prev] ^ linki & 4294967295L) & 4294967295L;
         this.link[next] = this.link[next] ^ (this.link[next] ^ linki & -4294967296L) & -4294967296L;
      }
   }

   protected void fixPointers(int s, int d) {
      if (this.size == 1) {
         this.first = this.last = d;
         this.link[d] = -1L;
      } else if (this.first == s) {
         this.first = d;
         this.link[(int)this.link[s]] = this.link[(int)this.link[s]] ^ (this.link[(int)this.link[s]] ^ (d & 4294967295L) << 32) & -4294967296L;
         this.link[d] = this.link[s];
      } else if (this.last == s) {
         this.last = d;
         this.link[(int)(this.link[s] >>> 32)] = this.link[(int)(this.link[s] >>> 32)]
            ^ (this.link[(int)(this.link[s] >>> 32)] ^ d & 4294967295L) & 4294967295L;
         this.link[d] = this.link[s];
      } else {
         long links = this.link[s];
         int prev = (int)(links >>> 32);
         int next = (int)links;
         this.link[prev] = this.link[prev] ^ (this.link[prev] ^ d & 4294967295L) & 4294967295L;
         this.link[next] = this.link[next] ^ (this.link[next] ^ (d & 4294967295L) << 32) & -4294967296L;
         this.link[d] = links;
      }
   }

   @Override
   public K firstKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   @Override
   public K lastKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   @Override
   public Object2ReferenceSortedMap<K, V> tailMap(K from) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object2ReferenceSortedMap<K, V> headMap(K to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Object2ReferenceSortedMap<K, V> subMap(K from, K to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Comparator<? super K> comparator() {
      return null;
   }

   public Object2ReferenceSortedMap.FastSortedEntrySet<K, V> object2ReferenceEntrySet() {
      if (this.entries == null) {
         this.entries = new Object2ReferenceLinkedOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public ObjectSortedSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Object2ReferenceLinkedOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ReferenceCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractReferenceCollection<V>() {
            private static final int SPLITERATOR_CHARACTERISTICS = 80;

            @Override
            public ObjectIterator<V> iterator() {
               return Object2ReferenceLinkedOpenCustomHashMap.this.new ValueIterator();
            }

            @Override
            public ObjectSpliterator<V> spliterator() {
               return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Object2ReferenceLinkedOpenCustomHashMap.this), 80);
            }

            @Override
            public void forEach(Consumer<? super V> consumer) {
               int i = Object2ReferenceLinkedOpenCustomHashMap.this.size;
               int next = Object2ReferenceLinkedOpenCustomHashMap.this.first;

               while (i-- != 0) {
                  int curr = next;
                  next = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[next];
                  consumer.accept(Object2ReferenceLinkedOpenCustomHashMap.this.value[curr]);
               }
            }

            @Override
            public int size() {
               return Object2ReferenceLinkedOpenCustomHashMap.this.size;
            }

            @Override
            public boolean contains(Object v) {
               return Object2ReferenceLinkedOpenCustomHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Object2ReferenceLinkedOpenCustomHashMap.this.clear();
            }
         };
      }

      return this.values;
   }

   public boolean trim() {
      return this.trim(this.size);
   }

   public boolean trim(int n) {
      int l = HashCommon.nextPowerOfTwo((int)Math.ceil(n / this.f));
      if (l < this.n && this.size <= HashCommon.maxFill(l, this.f)) {
         try {
            this.rehash(l);
            return true;
         } catch (OutOfMemoryError var4) {
            return false;
         }
      } else {
         return true;
      }
   }

   protected void rehash(int newN) {
      K[] key = this.key;
      V[] value = this.value;
      int mask = newN - 1;
      K[] newKey = (K[])(new Object[newN + 1]);
      V[] newValue = (V[])(new Object[newN + 1]);
      int i = this.first;
      int prev = -1;
      int newPrev = -1;
      long[] link = this.link;
      long[] newLink = new long[newN + 1];
      this.first = -1;
      int j = this.size;

      while (j-- != 0) {
         int pos;
         if (this.strategy.equals(key[i], null)) {
            pos = newN;
         } else {
            pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask;

            while (newKey[pos] != null) {
               pos = pos + 1 & mask;
            }
         }

         newKey[pos] = key[i];
         newValue[pos] = value[i];
         if (prev != -1) {
            newLink[newPrev] ^= (newLink[newPrev] ^ pos & 4294967295L) & 4294967295L;
            newLink[pos] ^= (newLink[pos] ^ (newPrev & 4294967295L) << 32) & -4294967296L;
            newPrev = pos;
         } else {
            newPrev = this.first = pos;
            newLink[pos] = -1L;
         }

         int t = i;
         i = (int)link[i];
         prev = t;
      }

      this.link = newLink;
      this.last = newPrev;
      if (newPrev != -1) {
         newLink[newPrev] |= 4294967295L;
      }

      this.n = newN;
      this.mask = mask;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = newKey;
      this.value = newValue;
   }

   public Object2ReferenceLinkedOpenCustomHashMap<K, V> clone() {
      Object2ReferenceLinkedOpenCustomHashMap<K, V> c;
      try {
         c = (Object2ReferenceLinkedOpenCustomHashMap<K, V>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (K[])((Object[])this.key.clone());
      c.value = (V[])((Object[])this.value.clone());
      c.link = (long[])this.link.clone();
      c.strategy = this.strategy;
      return c;
   }

   @Override
   public int hashCode() {
      int h = 0;
      int j = this.realSize();
      int i = 0;

      for (int t = 0; j-- != 0; i++) {
         while (this.key[i] == null) {
            i++;
         }

         if (this != this.key[i]) {
            t = this.strategy.hashCode(this.key[i]);
         }

         if (this != this.value[i]) {
            t ^= this.value[i] == null ? 0 : System.identityHashCode(this.value[i]);
         }

         h += t;
      }

      if (this.containsNullKey) {
         h += this.value[this.n] == null ? 0 : System.identityHashCode(this.value[this.n]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      K[] key = this.key;
      V[] value = this.value;
      Object2ReferenceLinkedOpenCustomHashMap<K, V>.EntryIterator i = new Object2ReferenceLinkedOpenCustomHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeObject(key[e]);
         s.writeObject(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      K[] key = this.key = (K[])(new Object[this.n + 1]);
      V[] value = this.value = (V[])(new Object[this.n + 1]);
      long[] link = this.link = new long[this.n + 1];
      int prev = -1;
      this.first = this.last = -1;
      int i = this.size;

      while (i-- != 0) {
         K k = (K)s.readObject();
         V v = (V)s.readObject();
         int pos;
         if (this.strategy.equals(k, null)) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;

            while (key[pos] != null) {
               pos = pos + 1 & this.mask;
            }
         }

         key[pos] = k;
         value[pos] = v;
         if (this.first != -1) {
            link[prev] ^= (link[prev] ^ pos & 4294967295L) & 4294967295L;
            link[pos] ^= (link[pos] ^ (prev & 4294967295L) << 32) & -4294967296L;
            prev = pos;
         } else {
            prev = this.first = pos;
            link[pos] |= -4294967296L;
         }
      }

      this.last = prev;
      if (prev != -1) {
         link[prev] |= 4294967295L;
      }
   }

   private void checkTable() {
   }

   private final class EntryIterator
      extends Object2ReferenceLinkedOpenCustomHashMap<K, V>.MapIterator<Consumer<? super Object2ReferenceMap.Entry<K, V>>>
      implements ObjectListIterator<Object2ReferenceMap.Entry<K, V>> {
      private Object2ReferenceLinkedOpenCustomHashMap<K, V>.MapEntry entry;

      public EntryIterator() {
      }

      public EntryIterator(K from) {
         super(from);
      }

      final void acceptOnIndex(Consumer<? super Object2ReferenceMap.Entry<K, V>> action, int index) {
         action.accept(Object2ReferenceLinkedOpenCustomHashMap.this.new MapEntry(index));
      }

      public Object2ReferenceLinkedOpenCustomHashMap<K, V>.MapEntry next() {
         return this.entry = Object2ReferenceLinkedOpenCustomHashMap.this.new MapEntry(this.nextEntry());
      }

      public Object2ReferenceLinkedOpenCustomHashMap<K, V>.MapEntry previous() {
         return this.entry = Object2ReferenceLinkedOpenCustomHashMap.this.new MapEntry(this.previousEntry());
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class FastEntryIterator
      extends Object2ReferenceLinkedOpenCustomHashMap<K, V>.MapIterator<Consumer<? super Object2ReferenceMap.Entry<K, V>>>
      implements ObjectListIterator<Object2ReferenceMap.Entry<K, V>> {
      final Object2ReferenceLinkedOpenCustomHashMap<K, V>.MapEntry entry;

      public FastEntryIterator() {
         this.entry = Object2ReferenceLinkedOpenCustomHashMap.this.new MapEntry();
      }

      public FastEntryIterator(K from) {
         super(from);
         this.entry = Object2ReferenceLinkedOpenCustomHashMap.this.new MapEntry();
      }

      final void acceptOnIndex(Consumer<? super Object2ReferenceMap.Entry<K, V>> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }

      public Object2ReferenceLinkedOpenCustomHashMap<K, V>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Object2ReferenceLinkedOpenCustomHashMap<K, V>.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private final class KeyIterator extends Object2ReferenceLinkedOpenCustomHashMap<K, V>.MapIterator<Consumer<? super K>> implements ObjectListIterator<K> {
      public KeyIterator(K k) {
         super(k);
      }

      @Override
      public K previous() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
      }

      final void acceptOnIndex(Consumer<? super K> action, int index) {
         action.accept(Object2ReferenceLinkedOpenCustomHashMap.this.key[index]);
      }

      @Override
      public K next() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractObjectSortedSet<K> {
      private static final int SPLITERATOR_CHARACTERISTICS = 81;

      private KeySet() {
      }

      public ObjectListIterator<K> iterator(K from) {
         return Object2ReferenceLinkedOpenCustomHashMap.this.new KeyIterator(from);
      }

      public ObjectListIterator<K> iterator() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.new KeyIterator();
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Object2ReferenceLinkedOpenCustomHashMap.this), 81);
      }

      @Override
      public void forEach(Consumer<? super K> consumer) {
         int i = Object2ReferenceLinkedOpenCustomHashMap.this.size;
         int next = Object2ReferenceLinkedOpenCustomHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[next];
            consumer.accept(Object2ReferenceLinkedOpenCustomHashMap.this.key[curr]);
         }
      }

      @Override
      public int size() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.size;
      }

      @Override
      public boolean contains(Object k) {
         return Object2ReferenceLinkedOpenCustomHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(Object k) {
         int oldSize = Object2ReferenceLinkedOpenCustomHashMap.this.size;
         Object2ReferenceLinkedOpenCustomHashMap.this.remove(k);
         return Object2ReferenceLinkedOpenCustomHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Object2ReferenceLinkedOpenCustomHashMap.this.clear();
      }

      @Override
      public K first() {
         if (Object2ReferenceLinkedOpenCustomHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Object2ReferenceLinkedOpenCustomHashMap.this.key[Object2ReferenceLinkedOpenCustomHashMap.this.first];
         }
      }

      @Override
      public K last() {
         if (Object2ReferenceLinkedOpenCustomHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Object2ReferenceLinkedOpenCustomHashMap.this.key[Object2ReferenceLinkedOpenCustomHashMap.this.last];
         }
      }

      @Override
      public Comparator<? super K> comparator() {
         return null;
      }

      @Override
      public ObjectSortedSet<K> tailSet(K from) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSortedSet<K> headSet(K to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ObjectSortedSet<K> subSet(K from, K to) {
         throw new UnsupportedOperationException();
      }
   }

   final class MapEntry implements Object2ReferenceMap.Entry<K, V>, Entry<K, V>, ObjectReferencePair<K, V> {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public K getKey() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public K left() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public V getValue() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public V right() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public V setValue(V v) {
         V oldValue = Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index];
         Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index] = v;
         return oldValue;
      }

      public ObjectReferencePair<K, V> right(V v) {
         Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index] = v;
         return this;
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<K, V> e = (Entry<K, V>)o;
            return Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(Object2ReferenceLinkedOpenCustomHashMap.this.key[this.index], e.getKey())
               && Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.strategy.hashCode(Object2ReferenceLinkedOpenCustomHashMap.this.key[this.index])
            ^ (
               Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index] == null
                  ? 0
                  : System.identityHashCode(Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index])
            );
      }

      @Override
      public String toString() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.key[this.index] + "=>" + Object2ReferenceLinkedOpenCustomHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet
      extends AbstractObjectSortedSet<Object2ReferenceMap.Entry<K, V>>
      implements Object2ReferenceSortedMap.FastSortedEntrySet<K, V> {
      private static final int SPLITERATOR_CHARACTERISTICS = 81;

      private MapEntrySet() {
      }

      @Override
      public ObjectBidirectionalIterator<Object2ReferenceMap.Entry<K, V>> iterator() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectSpliterator<Object2ReferenceMap.Entry<K, V>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Object2ReferenceLinkedOpenCustomHashMap.this), 81);
      }

      @Override
      public Comparator<? super Object2ReferenceMap.Entry<K, V>> comparator() {
         return null;
      }

      public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> subSet(Object2ReferenceMap.Entry<K, V> fromElement, Object2ReferenceMap.Entry<K, V> toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> headSet(Object2ReferenceMap.Entry<K, V> toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Object2ReferenceMap.Entry<K, V>> tailSet(Object2ReferenceMap.Entry<K, V> fromElement) {
         throw new UnsupportedOperationException();
      }

      public Object2ReferenceMap.Entry<K, V> first() {
         if (Object2ReferenceLinkedOpenCustomHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Object2ReferenceLinkedOpenCustomHashMap.this.new MapEntry(Object2ReferenceLinkedOpenCustomHashMap.this.first);
         }
      }

      public Object2ReferenceMap.Entry<K, V> last() {
         if (Object2ReferenceLinkedOpenCustomHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Object2ReferenceLinkedOpenCustomHashMap.this.new MapEntry(Object2ReferenceLinkedOpenCustomHashMap.this.last);
         }
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            K k = (K)e.getKey();
            V v = (V)e.getValue();
            if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(k, null)) {
               return Object2ReferenceLinkedOpenCustomHashMap.this.containsNullKey
                  && Object2ReferenceLinkedOpenCustomHashMap.this.value[Object2ReferenceLinkedOpenCustomHashMap.this.n] == v;
            } else {
               K[] key = Object2ReferenceLinkedOpenCustomHashMap.this.key;
               K curr;
               int pos;
               if ((
                     curr = key[pos = HashCommon.mix(Object2ReferenceLinkedOpenCustomHashMap.this.strategy.hashCode(k))
                        & Object2ReferenceLinkedOpenCustomHashMap.this.mask]
                  )
                  == null) {
                  return false;
               } else if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(k, curr)) {
                  return Object2ReferenceLinkedOpenCustomHashMap.this.value[pos] == v;
               } else {
                  while ((curr = key[pos = pos + 1 & Object2ReferenceLinkedOpenCustomHashMap.this.mask]) != null) {
                     if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(k, curr)) {
                        return Object2ReferenceLinkedOpenCustomHashMap.this.value[pos] == v;
                     }
                  }

                  return false;
               }
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            K k = (K)e.getKey();
            V v = (V)e.getValue();
            if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(k, null)) {
               if (Object2ReferenceLinkedOpenCustomHashMap.this.containsNullKey
                  && Object2ReferenceLinkedOpenCustomHashMap.this.value[Object2ReferenceLinkedOpenCustomHashMap.this.n] == v) {
                  Object2ReferenceLinkedOpenCustomHashMap.this.removeNullEntry();
                  return true;
               } else {
                  return false;
               }
            } else {
               K[] key = Object2ReferenceLinkedOpenCustomHashMap.this.key;
               K curr;
               int pos;
               if ((
                     curr = key[pos = HashCommon.mix(Object2ReferenceLinkedOpenCustomHashMap.this.strategy.hashCode(k))
                        & Object2ReferenceLinkedOpenCustomHashMap.this.mask]
                  )
                  == null) {
                  return false;
               } else if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(curr, k)) {
                  if (Object2ReferenceLinkedOpenCustomHashMap.this.value[pos] == v) {
                     Object2ReferenceLinkedOpenCustomHashMap.this.removeEntry(pos);
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  while ((curr = key[pos = pos + 1 & Object2ReferenceLinkedOpenCustomHashMap.this.mask]) != null) {
                     if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(curr, k) && Object2ReferenceLinkedOpenCustomHashMap.this.value[pos] == v) {
                        Object2ReferenceLinkedOpenCustomHashMap.this.removeEntry(pos);
                        return true;
                     }
                  }

                  return false;
               }
            }
         }
      }

      @Override
      public int size() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.size;
      }

      @Override
      public void clear() {
         Object2ReferenceLinkedOpenCustomHashMap.this.clear();
      }

      public ObjectListIterator<Object2ReferenceMap.Entry<K, V>> iterator(Object2ReferenceMap.Entry<K, V> from) {
         return Object2ReferenceLinkedOpenCustomHashMap.this.new EntryIterator(from.getKey());
      }

      public ObjectListIterator<Object2ReferenceMap.Entry<K, V>> fastIterator() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Object2ReferenceMap.Entry<K, V>> fastIterator(Object2ReferenceMap.Entry<K, V> from) {
         return Object2ReferenceLinkedOpenCustomHashMap.this.new FastEntryIterator(from.getKey());
      }

      @Override
      public void forEach(Consumer<? super Object2ReferenceMap.Entry<K, V>> consumer) {
         int i = Object2ReferenceLinkedOpenCustomHashMap.this.size;
         int next = Object2ReferenceLinkedOpenCustomHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[next];
            consumer.accept(Object2ReferenceLinkedOpenCustomHashMap.this.new MapEntry(curr));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Object2ReferenceMap.Entry<K, V>> consumer) {
         Object2ReferenceLinkedOpenCustomHashMap<K, V>.MapEntry entry = Object2ReferenceLinkedOpenCustomHashMap.this.new MapEntry();
         int i = Object2ReferenceLinkedOpenCustomHashMap.this.size;
         int next = Object2ReferenceLinkedOpenCustomHashMap.this.first;

         while (i-- != 0) {
            entry.index = next;
            next = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[next];
            consumer.accept(entry);
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int prev = -1;
      int next = -1;
      int curr = -1;
      int index = -1;

      abstract void acceptOnIndex(ConsumerType var1, int var2);

      protected MapIterator() {
         this.next = Object2ReferenceLinkedOpenCustomHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(K from) {
         if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(from, null)) {
            if (Object2ReferenceLinkedOpenCustomHashMap.this.containsNullKey) {
               this.next = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[Object2ReferenceLinkedOpenCustomHashMap.this.n];
               this.prev = Object2ReferenceLinkedOpenCustomHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
         } else if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy
            .equals(Object2ReferenceLinkedOpenCustomHashMap.this.key[Object2ReferenceLinkedOpenCustomHashMap.this.last], from)) {
            this.prev = Object2ReferenceLinkedOpenCustomHashMap.this.last;
            this.index = Object2ReferenceLinkedOpenCustomHashMap.this.size;
         } else {
            for (int pos = HashCommon.mix(Object2ReferenceLinkedOpenCustomHashMap.this.strategy.hashCode(from))
                  & Object2ReferenceLinkedOpenCustomHashMap.this.mask;
               Object2ReferenceLinkedOpenCustomHashMap.this.key[pos] != null;
               pos = pos + 1 & Object2ReferenceLinkedOpenCustomHashMap.this.mask
            ) {
               if (Object2ReferenceLinkedOpenCustomHashMap.this.strategy.equals(Object2ReferenceLinkedOpenCustomHashMap.this.key[pos], from)) {
                  this.next = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[pos];
                  this.prev = pos;
                  return;
               }
            }

            throw new NoSuchElementException("The key " + from + " does not belong to this map.");
         }
      }

      public boolean hasNext() {
         return this.next != -1;
      }

      public boolean hasPrevious() {
         return this.prev != -1;
      }

      private final void ensureIndexKnown() {
         if (this.index < 0) {
            if (this.prev == -1) {
               this.index = 0;
            } else if (this.next == -1) {
               this.index = Object2ReferenceLinkedOpenCustomHashMap.this.size;
            } else {
               int pos = Object2ReferenceLinkedOpenCustomHashMap.this.first;

               for (this.index = 1; pos != this.prev; this.index++) {
                  pos = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[pos];
               }
            }
         }
      }

      public int nextIndex() {
         this.ensureIndexKnown();
         return this.index;
      }

      public int previousIndex() {
         this.ensureIndexKnown();
         return this.index - 1;
      }

      public int nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.next;
            this.next = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
               this.index++;
            }

            return this.curr;
         }
      }

      public int previousEntry() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev;
            this.prev = (int)(Object2ReferenceLinkedOpenCustomHashMap.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
               this.index--;
            }

            return this.curr;
         }
      }

      public void forEachRemaining(ConsumerType action) {
         while (this.hasNext()) {
            this.curr = this.next;
            this.next = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
               this.index++;
            }

            this.acceptOnIndex(action, this.curr);
         }
      }

      public void remove() {
         this.ensureIndexKnown();
         if (this.curr == -1) {
            throw new IllegalStateException();
         } else {
            if (this.curr == this.prev) {
               this.index--;
               this.prev = (int)(Object2ReferenceLinkedOpenCustomHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Object2ReferenceLinkedOpenCustomHashMap.this.link[this.curr];
            }

            Object2ReferenceLinkedOpenCustomHashMap.this.size--;
            if (this.prev == -1) {
               Object2ReferenceLinkedOpenCustomHashMap.this.first = this.next;
            } else {
               Object2ReferenceLinkedOpenCustomHashMap.this.link[this.prev] = Object2ReferenceLinkedOpenCustomHashMap.this.link[this.prev]
                  ^ (Object2ReferenceLinkedOpenCustomHashMap.this.link[this.prev] ^ this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Object2ReferenceLinkedOpenCustomHashMap.this.last = this.prev;
            } else {
               Object2ReferenceLinkedOpenCustomHashMap.this.link[this.next] = Object2ReferenceLinkedOpenCustomHashMap.this.link[this.next]
                  ^ (Object2ReferenceLinkedOpenCustomHashMap.this.link[this.next] ^ (this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int pos = this.curr;
            this.curr = -1;
            if (pos == Object2ReferenceLinkedOpenCustomHashMap.this.n) {
               Object2ReferenceLinkedOpenCustomHashMap.this.containsNullKey = false;
               Object2ReferenceLinkedOpenCustomHashMap.this.key[Object2ReferenceLinkedOpenCustomHashMap.this.n] = null;
               Object2ReferenceLinkedOpenCustomHashMap.this.value[Object2ReferenceLinkedOpenCustomHashMap.this.n] = null;
            } else {
               K[] key = Object2ReferenceLinkedOpenCustomHashMap.this.key;

               label61:
               while (true) {
                  int last = pos;

                  K curr;
                  for (pos = pos + 1 & Object2ReferenceLinkedOpenCustomHashMap.this.mask;
                     (curr = key[pos]) != null;
                     pos = pos + 1 & Object2ReferenceLinkedOpenCustomHashMap.this.mask
                  ) {
                     int slot = HashCommon.mix(Object2ReferenceLinkedOpenCustomHashMap.this.strategy.hashCode(curr))
                        & Object2ReferenceLinkedOpenCustomHashMap.this.mask;
                     if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                        key[last] = curr;
                        Object2ReferenceLinkedOpenCustomHashMap.this.value[last] = Object2ReferenceLinkedOpenCustomHashMap.this.value[pos];
                        if (this.next == pos) {
                           this.next = last;
                        }

                        if (this.prev == pos) {
                           this.prev = last;
                        }

                        Object2ReferenceLinkedOpenCustomHashMap.this.fixPointers(pos, last);
                        continue label61;
                     }
                  }

                  key[last] = null;
                  Object2ReferenceLinkedOpenCustomHashMap.this.value[last] = null;
                  return;
               }
            }
         }
      }

      public int skip(int n) {
         int i = n;

         while (i-- != 0 && this.hasNext()) {
            this.nextEntry();
         }

         return n - i - 1;
      }

      public int back(int n) {
         int i = n;

         while (i-- != 0 && this.hasPrevious()) {
            this.previousEntry();
         }

         return n - i - 1;
      }

      public void set(Object2ReferenceMap.Entry<K, V> ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Object2ReferenceMap.Entry<K, V> ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class ValueIterator extends Object2ReferenceLinkedOpenCustomHashMap<K, V>.MapIterator<Consumer<? super V>> implements ObjectListIterator<V> {
      @Override
      public V previous() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
      }

      final void acceptOnIndex(Consumer<? super V> action, int index) {
         action.accept(Object2ReferenceLinkedOpenCustomHashMap.this.value[index]);
      }

      @Override
      public V next() {
         return Object2ReferenceLinkedOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }
}
