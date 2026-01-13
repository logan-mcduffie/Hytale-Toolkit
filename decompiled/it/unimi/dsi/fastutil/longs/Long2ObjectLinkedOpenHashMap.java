package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
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
import java.util.function.LongFunction;

public class Long2ObjectLinkedOpenHashMap<V> extends AbstractLong2ObjectSortedMap<V> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient long[] key;
   protected transient V[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int first = -1;
   protected transient int last = -1;
   protected transient long[] link;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Long2ObjectSortedMap.FastSortedEntrySet<V> entries;
   protected transient LongSortedSet keys;
   protected transient ObjectCollection<V> values;

   public Long2ObjectLinkedOpenHashMap(int expected, float f) {
      if (f <= 0.0F || f >= 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      } else if (expected < 0) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.arraySize(expected, f);
         this.mask = this.n - 1;
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = new long[this.n + 1];
         this.value = (V[])(new Object[this.n + 1]);
         this.link = new long[this.n + 1];
      }
   }

   public Long2ObjectLinkedOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Long2ObjectLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Long2ObjectLinkedOpenHashMap(Map<? extends Long, ? extends V> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Long2ObjectLinkedOpenHashMap(Map<? extends Long, ? extends V> m) {
      this(m, 0.75F);
   }

   public Long2ObjectLinkedOpenHashMap(Long2ObjectMap<V> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Long2ObjectLinkedOpenHashMap(Long2ObjectMap<V> m) {
      this(m, 0.75F);
   }

   public Long2ObjectLinkedOpenHashMap(long[] k, V[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Long2ObjectLinkedOpenHashMap(long[] k, V[] v) {
      this(k, v, 0.75F);
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
   public void putAll(Map<? extends Long, ? extends V> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(long k) {
      if (k == 0L) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         long[] key = this.key;
         long curr;
         int pos;
         if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
            return -(pos + 1);
         } else if (k == curr) {
            return pos;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (k == curr) {
                  return pos;
               }
            }

            return -(pos + 1);
         }
      }
   }

   private void insert(int pos, long k, V v) {
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
   public V put(long k, V v) {
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
      long[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         long curr;
         for (pos = pos + 1 & this.mask; (curr = key[pos]) != 0L; pos = pos + 1 & this.mask) {
            int slot = (int)HashCommon.mix(curr) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               this.value[last] = this.value[pos];
               this.fixPointers(pos, last);
               continue label30;
            }
         }

         key[last] = 0L;
         this.value[last] = null;
         return;
      }
   }

   @Override
   public V remove(long k) {
      if (k == 0L) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         long[] key = this.key;
         long curr;
         int pos;
         if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (k == curr) {
            return this.removeEntry(pos);
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (k == curr) {
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

   public V getAndMoveToFirst(long k) {
      if (k == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         long[] key = this.key;
         long curr;
         int pos;
         if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (k == curr) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (k == curr) {
                  this.moveIndexToFirst(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public V getAndMoveToLast(long k) {
      if (k == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         long[] key = this.key;
         long curr;
         int pos;
         if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (k == curr) {
            this.moveIndexToLast(pos);
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (k == curr) {
                  this.moveIndexToLast(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public V putAndMoveToFirst(long k, V v) {
      int pos;
      if (k == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         long[] key = this.key;
         long curr;
         if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) != 0L) {
            if (curr == k) {
               this.moveIndexToFirst(pos);
               return this.setValue(pos, v);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (curr == k) {
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

   public V putAndMoveToLast(long k, V v) {
      int pos;
      if (k == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         long[] key = this.key;
         long curr;
         if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) != 0L) {
            if (curr == k) {
               this.moveIndexToLast(pos);
               return this.setValue(pos, v);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (curr == k) {
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
   public V get(long k) {
      if (k == 0L) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         long[] key = this.key;
         long curr;
         int pos;
         if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (k == curr) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (k == curr) {
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public boolean containsKey(long k) {
      if (k == 0L) {
         return this.containsNullKey;
      } else {
         long[] key = this.key;
         long curr;
         int pos;
         if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
            return false;
         } else if (k == curr) {
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (k == curr) {
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
      long[] key = this.key;
      if (this.containsNullKey && Objects.equals(value[this.n], v)) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (key[i] != 0L && Objects.equals(value[i], v)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public V getOrDefault(long k, V defaultValue) {
      if (k == 0L) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         long[] key = this.key;
         long curr;
         int pos;
         if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
            return defaultValue;
         } else if (k == curr) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (k == curr) {
                  return this.value[pos];
               }
            }

            return defaultValue;
         }
      }
   }

   @Override
   public V putIfAbsent(long k, V v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(long k, Object v) {
      if (k == 0L) {
         if (this.containsNullKey && Objects.equals(v, this.value[this.n])) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         long[] key = this.key;
         long curr;
         int pos;
         if ((curr = key[pos = (int)HashCommon.mix(k) & this.mask]) == 0L) {
            return false;
         } else if (k == curr && Objects.equals(v, this.value[pos])) {
            this.removeEntry(pos);
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (k == curr && Objects.equals(v, this.value[pos])) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(long k, V oldValue, V v) {
      int pos = this.find(k);
      if (pos >= 0 && Objects.equals(oldValue, this.value[pos])) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public V replace(long k, V v) {
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
   public V computeIfAbsent(long k, LongFunction<? extends V> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         V newValue = (V)mappingFunction.apply(k);
         this.insert(-pos - 1, k, newValue);
         return newValue;
      }
   }

   @Override
   public V computeIfAbsent(long key, Long2ObjectFunction<? extends V> mappingFunction) {
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
   public V computeIfPresent(long k, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else if (this.value[pos] == null) {
         return this.defRetValue;
      } else {
         V newValue = (V)remappingFunction.apply(k, this.value[pos]);
         if (newValue == null) {
            if (k == 0L) {
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
   public V compute(long k, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      V newValue = (V)remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (k == 0L) {
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
   public V merge(long k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      Objects.requireNonNull(v);
      int pos = this.find(k);
      if (pos >= 0 && this.value[pos] != null) {
         V newValue = (V)remappingFunction.apply(this.value[pos], v);
         if (newValue == null) {
            if (k == 0L) {
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
         Arrays.fill(this.key, 0L);
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
   public long firstLongKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   @Override
   public long lastLongKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   @Override
   public Long2ObjectSortedMap<V> tailMap(long from) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Long2ObjectSortedMap<V> headMap(long to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Long2ObjectSortedMap<V> subMap(long from, long to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public LongComparator comparator() {
      return null;
   }

   public Long2ObjectSortedMap.FastSortedEntrySet<V> long2ObjectEntrySet() {
      if (this.entries == null) {
         this.entries = new Long2ObjectLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public LongSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Long2ObjectLinkedOpenHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ObjectCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractObjectCollection<V>() {
            private static final int SPLITERATOR_CHARACTERISTICS = 80;

            @Override
            public ObjectIterator<V> iterator() {
               return Long2ObjectLinkedOpenHashMap.this.new ValueIterator();
            }

            @Override
            public ObjectSpliterator<V> spliterator() {
               return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Long2ObjectLinkedOpenHashMap.this), 80);
            }

            @Override
            public void forEach(Consumer<? super V> consumer) {
               int i = Long2ObjectLinkedOpenHashMap.this.size;
               int next = Long2ObjectLinkedOpenHashMap.this.first;

               while (i-- != 0) {
                  int curr = next;
                  next = (int)Long2ObjectLinkedOpenHashMap.this.link[next];
                  consumer.accept(Long2ObjectLinkedOpenHashMap.this.value[curr]);
               }
            }

            @Override
            public int size() {
               return Long2ObjectLinkedOpenHashMap.this.size;
            }

            @Override
            public boolean contains(Object v) {
               return Long2ObjectLinkedOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Long2ObjectLinkedOpenHashMap.this.clear();
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
      long[] key = this.key;
      V[] value = this.value;
      int mask = newN - 1;
      long[] newKey = new long[newN + 1];
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
         if (key[i] == 0L) {
            pos = newN;
         } else {
            pos = (int)HashCommon.mix(key[i]) & mask;

            while (newKey[pos] != 0L) {
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

   public Long2ObjectLinkedOpenHashMap<V> clone() {
      Long2ObjectLinkedOpenHashMap<V> c;
      try {
         c = (Long2ObjectLinkedOpenHashMap<V>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (long[])this.key.clone();
      c.value = (V[])((Object[])this.value.clone());
      c.link = (long[])this.link.clone();
      return c;
   }

   @Override
   public int hashCode() {
      int h = 0;
      int j = this.realSize();
      int i = 0;

      for (int t = 0; j-- != 0; i++) {
         while (this.key[i] == 0L) {
            i++;
         }

         t = HashCommon.long2int(this.key[i]);
         if (this != this.value[i]) {
            t ^= this.value[i] == null ? 0 : this.value[i].hashCode();
         }

         h += t;
      }

      if (this.containsNullKey) {
         h += this.value[this.n] == null ? 0 : this.value[this.n].hashCode();
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      long[] key = this.key;
      V[] value = this.value;
      Long2ObjectLinkedOpenHashMap<V>.EntryIterator i = new Long2ObjectLinkedOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeLong(key[e]);
         s.writeObject(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      long[] key = this.key = new long[this.n + 1];
      V[] value = this.value = (V[])(new Object[this.n + 1]);
      long[] link = this.link = new long[this.n + 1];
      int prev = -1;
      this.first = this.last = -1;
      int i = this.size;

      while (i-- != 0) {
         long k = s.readLong();
         V v = (V)s.readObject();
         int pos;
         if (k == 0L) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            pos = (int)HashCommon.mix(k) & this.mask;

            while (key[pos] != 0L) {
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
      extends Long2ObjectLinkedOpenHashMap<V>.MapIterator<Consumer<? super Long2ObjectMap.Entry<V>>>
      implements ObjectListIterator<Long2ObjectMap.Entry<V>> {
      private Long2ObjectLinkedOpenHashMap<V>.MapEntry entry;

      public EntryIterator() {
      }

      public EntryIterator(long from) {
         super(from);
      }

      final void acceptOnIndex(Consumer<? super Long2ObjectMap.Entry<V>> action, int index) {
         action.accept(Long2ObjectLinkedOpenHashMap.this.new MapEntry(index));
      }

      public Long2ObjectLinkedOpenHashMap<V>.MapEntry next() {
         return this.entry = Long2ObjectLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Long2ObjectLinkedOpenHashMap<V>.MapEntry previous() {
         return this.entry = Long2ObjectLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class FastEntryIterator
      extends Long2ObjectLinkedOpenHashMap<V>.MapIterator<Consumer<? super Long2ObjectMap.Entry<V>>>
      implements ObjectListIterator<Long2ObjectMap.Entry<V>> {
      final Long2ObjectLinkedOpenHashMap<V>.MapEntry entry;

      public FastEntryIterator() {
         this.entry = Long2ObjectLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(long from) {
         super(from);
         this.entry = Long2ObjectLinkedOpenHashMap.this.new MapEntry();
      }

      final void acceptOnIndex(Consumer<? super Long2ObjectMap.Entry<V>> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }

      public Long2ObjectLinkedOpenHashMap<V>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Long2ObjectLinkedOpenHashMap<V>.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private final class KeyIterator extends Long2ObjectLinkedOpenHashMap<V>.MapIterator<java.util.function.LongConsumer> implements LongListIterator {
      public KeyIterator(long k) {
         super(k);
      }

      @Override
      public long previousLong() {
         return Long2ObjectLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
      }

      final void acceptOnIndex(java.util.function.LongConsumer action, int index) {
         action.accept(Long2ObjectLinkedOpenHashMap.this.key[index]);
      }

      @Override
      public long nextLong() {
         return Long2ObjectLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractLongSortedSet {
      private static final int SPLITERATOR_CHARACTERISTICS = 337;

      private KeySet() {
      }

      public LongListIterator iterator(long from) {
         return Long2ObjectLinkedOpenHashMap.this.new KeyIterator(from);
      }

      public LongListIterator iterator() {
         return Long2ObjectLinkedOpenHashMap.this.new KeyIterator();
      }

      @Override
      public LongSpliterator spliterator() {
         return LongSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Long2ObjectLinkedOpenHashMap.this), 337);
      }

      @Override
      public void forEach(java.util.function.LongConsumer consumer) {
         int i = Long2ObjectLinkedOpenHashMap.this.size;
         int next = Long2ObjectLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Long2ObjectLinkedOpenHashMap.this.link[next];
            consumer.accept(Long2ObjectLinkedOpenHashMap.this.key[curr]);
         }
      }

      @Override
      public int size() {
         return Long2ObjectLinkedOpenHashMap.this.size;
      }

      @Override
      public boolean contains(long k) {
         return Long2ObjectLinkedOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(long k) {
         int oldSize = Long2ObjectLinkedOpenHashMap.this.size;
         Long2ObjectLinkedOpenHashMap.this.remove(k);
         return Long2ObjectLinkedOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Long2ObjectLinkedOpenHashMap.this.clear();
      }

      @Override
      public long firstLong() {
         if (Long2ObjectLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Long2ObjectLinkedOpenHashMap.this.key[Long2ObjectLinkedOpenHashMap.this.first];
         }
      }

      @Override
      public long lastLong() {
         if (Long2ObjectLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Long2ObjectLinkedOpenHashMap.this.key[Long2ObjectLinkedOpenHashMap.this.last];
         }
      }

      @Override
      public LongComparator comparator() {
         return null;
      }

      @Override
      public LongSortedSet tailSet(long from) {
         throw new UnsupportedOperationException();
      }

      @Override
      public LongSortedSet headSet(long to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public LongSortedSet subSet(long from, long to) {
         throw new UnsupportedOperationException();
      }
   }

   final class MapEntry implements Long2ObjectMap.Entry<V>, Entry<Long, V>, LongObjectPair<V> {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public long getLongKey() {
         return Long2ObjectLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public long leftLong() {
         return Long2ObjectLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public V getValue() {
         return Long2ObjectLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public V right() {
         return Long2ObjectLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public V setValue(V v) {
         V oldValue = Long2ObjectLinkedOpenHashMap.this.value[this.index];
         Long2ObjectLinkedOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      public LongObjectPair<V> right(V v) {
         Long2ObjectLinkedOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Long getKey() {
         return Long2ObjectLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Long, V> e = (Entry<Long, V>)o;
            return Long2ObjectLinkedOpenHashMap.this.key[this.index] == e.getKey()
               && Objects.equals(Long2ObjectLinkedOpenHashMap.this.value[this.index], e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.long2int(Long2ObjectLinkedOpenHashMap.this.key[this.index])
            ^ (Long2ObjectLinkedOpenHashMap.this.value[this.index] == null ? 0 : Long2ObjectLinkedOpenHashMap.this.value[this.index].hashCode());
      }

      @Override
      public String toString() {
         return Long2ObjectLinkedOpenHashMap.this.key[this.index] + "=>" + Long2ObjectLinkedOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Long2ObjectMap.Entry<V>> implements Long2ObjectSortedMap.FastSortedEntrySet<V> {
      private static final int SPLITERATOR_CHARACTERISTICS = 81;

      private MapEntrySet() {
      }

      @Override
      public ObjectBidirectionalIterator<Long2ObjectMap.Entry<V>> iterator() {
         return Long2ObjectLinkedOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectSpliterator<Long2ObjectMap.Entry<V>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Long2ObjectLinkedOpenHashMap.this), 81);
      }

      @Override
      public Comparator<? super Long2ObjectMap.Entry<V>> comparator() {
         return null;
      }

      public ObjectSortedSet<Long2ObjectMap.Entry<V>> subSet(Long2ObjectMap.Entry<V> fromElement, Long2ObjectMap.Entry<V> toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Long2ObjectMap.Entry<V>> headSet(Long2ObjectMap.Entry<V> toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Long2ObjectMap.Entry<V>> tailSet(Long2ObjectMap.Entry<V> fromElement) {
         throw new UnsupportedOperationException();
      }

      public Long2ObjectMap.Entry<V> first() {
         if (Long2ObjectLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Long2ObjectLinkedOpenHashMap.this.new MapEntry(Long2ObjectLinkedOpenHashMap.this.first);
         }
      }

      public Long2ObjectMap.Entry<V> last() {
         if (Long2ObjectLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Long2ObjectLinkedOpenHashMap.this.new MapEntry(Long2ObjectLinkedOpenHashMap.this.last);
         }
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() != null && e.getKey() instanceof Long) {
               long k = (Long)e.getKey();
               V v = (V)e.getValue();
               if (k == 0L) {
                  return Long2ObjectLinkedOpenHashMap.this.containsNullKey
                     && Objects.equals(Long2ObjectLinkedOpenHashMap.this.value[Long2ObjectLinkedOpenHashMap.this.n], v);
               } else {
                  long[] key = Long2ObjectLinkedOpenHashMap.this.key;
                  long curr;
                  int pos;
                  if ((curr = key[pos = (int)HashCommon.mix(k) & Long2ObjectLinkedOpenHashMap.this.mask]) == 0L) {
                     return false;
                  } else if (k == curr) {
                     return Objects.equals(Long2ObjectLinkedOpenHashMap.this.value[pos], v);
                  } else {
                     while ((curr = key[pos = pos + 1 & Long2ObjectLinkedOpenHashMap.this.mask]) != 0L) {
                        if (k == curr) {
                           return Objects.equals(Long2ObjectLinkedOpenHashMap.this.value[pos], v);
                        }
                     }

                     return false;
                  }
               }
            } else {
               return false;
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() != null && e.getKey() instanceof Long) {
               long k = (Long)e.getKey();
               V v = (V)e.getValue();
               if (k == 0L) {
                  if (Long2ObjectLinkedOpenHashMap.this.containsNullKey
                     && Objects.equals(Long2ObjectLinkedOpenHashMap.this.value[Long2ObjectLinkedOpenHashMap.this.n], v)) {
                     Long2ObjectLinkedOpenHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  long[] key = Long2ObjectLinkedOpenHashMap.this.key;
                  long curr;
                  int pos;
                  if ((curr = key[pos = (int)HashCommon.mix(k) & Long2ObjectLinkedOpenHashMap.this.mask]) == 0L) {
                     return false;
                  } else if (curr == k) {
                     if (Objects.equals(Long2ObjectLinkedOpenHashMap.this.value[pos], v)) {
                        Long2ObjectLinkedOpenHashMap.this.removeEntry(pos);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     while ((curr = key[pos = pos + 1 & Long2ObjectLinkedOpenHashMap.this.mask]) != 0L) {
                        if (curr == k && Objects.equals(Long2ObjectLinkedOpenHashMap.this.value[pos], v)) {
                           Long2ObjectLinkedOpenHashMap.this.removeEntry(pos);
                           return true;
                        }
                     }

                     return false;
                  }
               }
            } else {
               return false;
            }
         }
      }

      @Override
      public int size() {
         return Long2ObjectLinkedOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Long2ObjectLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Long2ObjectMap.Entry<V>> iterator(Long2ObjectMap.Entry<V> from) {
         return Long2ObjectLinkedOpenHashMap.this.new EntryIterator(from.getLongKey());
      }

      public ObjectListIterator<Long2ObjectMap.Entry<V>> fastIterator() {
         return Long2ObjectLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Long2ObjectMap.Entry<V>> fastIterator(Long2ObjectMap.Entry<V> from) {
         return Long2ObjectLinkedOpenHashMap.this.new FastEntryIterator(from.getLongKey());
      }

      @Override
      public void forEach(Consumer<? super Long2ObjectMap.Entry<V>> consumer) {
         int i = Long2ObjectLinkedOpenHashMap.this.size;
         int next = Long2ObjectLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Long2ObjectLinkedOpenHashMap.this.link[next];
            consumer.accept(Long2ObjectLinkedOpenHashMap.this.new MapEntry(curr));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Long2ObjectMap.Entry<V>> consumer) {
         Long2ObjectLinkedOpenHashMap<V>.MapEntry entry = Long2ObjectLinkedOpenHashMap.this.new MapEntry();
         int i = Long2ObjectLinkedOpenHashMap.this.size;
         int next = Long2ObjectLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            entry.index = next;
            next = (int)Long2ObjectLinkedOpenHashMap.this.link[next];
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
         this.next = Long2ObjectLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(long from) {
         if (from == 0L) {
            if (Long2ObjectLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Long2ObjectLinkedOpenHashMap.this.link[Long2ObjectLinkedOpenHashMap.this.n];
               this.prev = Long2ObjectLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
         } else if (Long2ObjectLinkedOpenHashMap.this.key[Long2ObjectLinkedOpenHashMap.this.last] == from) {
            this.prev = Long2ObjectLinkedOpenHashMap.this.last;
            this.index = Long2ObjectLinkedOpenHashMap.this.size;
         } else {
            for (int pos = (int)HashCommon.mix(from) & Long2ObjectLinkedOpenHashMap.this.mask;
               Long2ObjectLinkedOpenHashMap.this.key[pos] != 0L;
               pos = pos + 1 & Long2ObjectLinkedOpenHashMap.this.mask
            ) {
               if (Long2ObjectLinkedOpenHashMap.this.key[pos] == from) {
                  this.next = (int)Long2ObjectLinkedOpenHashMap.this.link[pos];
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
               this.index = Long2ObjectLinkedOpenHashMap.this.size;
            } else {
               int pos = Long2ObjectLinkedOpenHashMap.this.first;

               for (this.index = 1; pos != this.prev; this.index++) {
                  pos = (int)Long2ObjectLinkedOpenHashMap.this.link[pos];
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
            this.next = (int)Long2ObjectLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Long2ObjectLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
            this.next = (int)Long2ObjectLinkedOpenHashMap.this.link[this.curr];
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
               this.prev = (int)(Long2ObjectLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Long2ObjectLinkedOpenHashMap.this.link[this.curr];
            }

            Long2ObjectLinkedOpenHashMap.this.size--;
            if (this.prev == -1) {
               Long2ObjectLinkedOpenHashMap.this.first = this.next;
            } else {
               Long2ObjectLinkedOpenHashMap.this.link[this.prev] = Long2ObjectLinkedOpenHashMap.this.link[this.prev]
                  ^ (Long2ObjectLinkedOpenHashMap.this.link[this.prev] ^ this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Long2ObjectLinkedOpenHashMap.this.last = this.prev;
            } else {
               Long2ObjectLinkedOpenHashMap.this.link[this.next] = Long2ObjectLinkedOpenHashMap.this.link[this.next]
                  ^ (Long2ObjectLinkedOpenHashMap.this.link[this.next] ^ (this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int pos = this.curr;
            this.curr = -1;
            if (pos == Long2ObjectLinkedOpenHashMap.this.n) {
               Long2ObjectLinkedOpenHashMap.this.containsNullKey = false;
               Long2ObjectLinkedOpenHashMap.this.value[Long2ObjectLinkedOpenHashMap.this.n] = null;
            } else {
               long[] key = Long2ObjectLinkedOpenHashMap.this.key;

               label61:
               while (true) {
                  int last = pos;

                  long curr;
                  for (pos = pos + 1 & Long2ObjectLinkedOpenHashMap.this.mask; (curr = key[pos]) != 0L; pos = pos + 1 & Long2ObjectLinkedOpenHashMap.this.mask) {
                     int slot = (int)HashCommon.mix(curr) & Long2ObjectLinkedOpenHashMap.this.mask;
                     if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                        key[last] = curr;
                        Long2ObjectLinkedOpenHashMap.this.value[last] = Long2ObjectLinkedOpenHashMap.this.value[pos];
                        if (this.next == pos) {
                           this.next = last;
                        }

                        if (this.prev == pos) {
                           this.prev = last;
                        }

                        Long2ObjectLinkedOpenHashMap.this.fixPointers(pos, last);
                        continue label61;
                     }
                  }

                  key[last] = 0L;
                  Long2ObjectLinkedOpenHashMap.this.value[last] = null;
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

      public void set(Long2ObjectMap.Entry<V> ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Long2ObjectMap.Entry<V> ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class ValueIterator extends Long2ObjectLinkedOpenHashMap<V>.MapIterator<Consumer<? super V>> implements ObjectListIterator<V> {
      @Override
      public V previous() {
         return Long2ObjectLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
      }

      final void acceptOnIndex(Consumer<? super V> action, int index) {
         action.accept(Long2ObjectLinkedOpenHashMap.this.value[index]);
      }

      @Override
      public V next() {
         return Long2ObjectLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }
}
