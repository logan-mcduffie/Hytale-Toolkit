package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
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
import java.util.function.IntFunction;

public class Byte2ReferenceLinkedOpenHashMap<V> extends AbstractByte2ReferenceSortedMap<V> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient byte[] key;
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
   protected transient Byte2ReferenceSortedMap.FastSortedEntrySet<V> entries;
   protected transient ByteSortedSet keys;
   protected transient ReferenceCollection<V> values;

   public Byte2ReferenceLinkedOpenHashMap(int expected, float f) {
      if (f <= 0.0F || f >= 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      } else if (expected < 0) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.arraySize(expected, f);
         this.mask = this.n - 1;
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = new byte[this.n + 1];
         this.value = (V[])(new Object[this.n + 1]);
         this.link = new long[this.n + 1];
      }
   }

   public Byte2ReferenceLinkedOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Byte2ReferenceLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Byte2ReferenceLinkedOpenHashMap(Map<? extends Byte, ? extends V> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Byte2ReferenceLinkedOpenHashMap(Map<? extends Byte, ? extends V> m) {
      this(m, 0.75F);
   }

   public Byte2ReferenceLinkedOpenHashMap(Byte2ReferenceMap<V> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Byte2ReferenceLinkedOpenHashMap(Byte2ReferenceMap<V> m) {
      this(m, 0.75F);
   }

   public Byte2ReferenceLinkedOpenHashMap(byte[] k, V[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Byte2ReferenceLinkedOpenHashMap(byte[] k, V[] v) {
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
   public void putAll(Map<? extends Byte, ? extends V> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(byte k) {
      if (k == 0) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         byte[] key = this.key;
         byte curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
            return -(pos + 1);
         } else if (k == curr) {
            return pos;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (k == curr) {
                  return pos;
               }
            }

            return -(pos + 1);
         }
      }
   }

   private void insert(int pos, byte k, V v) {
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
   public V put(byte k, V v) {
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
      byte[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         byte curr;
         for (pos = pos + 1 & this.mask; (curr = key[pos]) != 0; pos = pos + 1 & this.mask) {
            int slot = HashCommon.mix((int)curr) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               this.value[last] = this.value[pos];
               this.fixPointers(pos, last);
               continue label30;
            }
         }

         key[last] = 0;
         this.value[last] = null;
         return;
      }
   }

   @Override
   public V remove(byte k) {
      if (k == 0) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         byte[] key = this.key;
         byte curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (k == curr) {
            return this.removeEntry(pos);
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
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

   public V getAndMoveToFirst(byte k) {
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         byte[] key = this.key;
         byte curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (k == curr) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (k == curr) {
                  this.moveIndexToFirst(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public V getAndMoveToLast(byte k) {
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         byte[] key = this.key;
         byte curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (k == curr) {
            this.moveIndexToLast(pos);
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (k == curr) {
                  this.moveIndexToLast(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public V putAndMoveToFirst(byte k, V v) {
      int pos;
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         byte[] key = this.key;
         byte curr;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) != 0) {
            if (curr == k) {
               this.moveIndexToFirst(pos);
               return this.setValue(pos, v);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
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

   public V putAndMoveToLast(byte k, V v) {
      int pos;
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         byte[] key = this.key;
         byte curr;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) != 0) {
            if (curr == k) {
               this.moveIndexToLast(pos);
               return this.setValue(pos, v);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
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
   public V get(byte k) {
      if (k == 0) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         byte[] key = this.key;
         byte curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (k == curr) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (k == curr) {
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public boolean containsKey(byte k) {
      if (k == 0) {
         return this.containsNullKey;
      } else {
         byte[] key = this.key;
         byte curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
            return false;
         } else if (k == curr) {
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
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
      byte[] key = this.key;
      if (this.containsNullKey && value[this.n] == v) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (key[i] != 0 && value[i] == v) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public V getOrDefault(byte k, V defaultValue) {
      if (k == 0) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         byte[] key = this.key;
         byte curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
            return defaultValue;
         } else if (k == curr) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (k == curr) {
                  return this.value[pos];
               }
            }

            return defaultValue;
         }
      }
   }

   @Override
   public V putIfAbsent(byte k, V v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(byte k, Object v) {
      if (k == 0) {
         if (this.containsNullKey && v == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         byte[] key = this.key;
         byte curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
            return false;
         } else if (k == curr && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (k == curr && v == this.value[pos]) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(byte k, V oldValue, V v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public V replace(byte k, V v) {
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
   public V computeIfAbsent(byte k, IntFunction<? extends V> mappingFunction) {
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
   public V computeIfAbsent(byte key, Byte2ReferenceFunction<? extends V> mappingFunction) {
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
   public V computeIfPresent(byte k, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else if (this.value[pos] == null) {
         return this.defRetValue;
      } else {
         V newValue = (V)remappingFunction.apply(k, this.value[pos]);
         if (newValue == null) {
            if (k == 0) {
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
   public V compute(byte k, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      V newValue = (V)remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (k == 0) {
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
   public V merge(byte k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      Objects.requireNonNull(v);
      int pos = this.find(k);
      if (pos >= 0 && this.value[pos] != null) {
         V newValue = (V)remappingFunction.apply(this.value[pos], v);
         if (newValue == null) {
            if (k == 0) {
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
         Arrays.fill(this.key, (byte)0);
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
   public byte firstByteKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   @Override
   public byte lastByteKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   @Override
   public Byte2ReferenceSortedMap<V> tailMap(byte from) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Byte2ReferenceSortedMap<V> headMap(byte to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Byte2ReferenceSortedMap<V> subMap(byte from, byte to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ByteComparator comparator() {
      return null;
   }

   public Byte2ReferenceSortedMap.FastSortedEntrySet<V> byte2ReferenceEntrySet() {
      if (this.entries == null) {
         this.entries = new Byte2ReferenceLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public ByteSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Byte2ReferenceLinkedOpenHashMap.KeySet();
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
               return Byte2ReferenceLinkedOpenHashMap.this.new ValueIterator();
            }

            @Override
            public ObjectSpliterator<V> spliterator() {
               return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Byte2ReferenceLinkedOpenHashMap.this), 80);
            }

            @Override
            public void forEach(Consumer<? super V> consumer) {
               int i = Byte2ReferenceLinkedOpenHashMap.this.size;
               int next = Byte2ReferenceLinkedOpenHashMap.this.first;

               while (i-- != 0) {
                  int curr = next;
                  next = (int)Byte2ReferenceLinkedOpenHashMap.this.link[next];
                  consumer.accept(Byte2ReferenceLinkedOpenHashMap.this.value[curr]);
               }
            }

            @Override
            public int size() {
               return Byte2ReferenceLinkedOpenHashMap.this.size;
            }

            @Override
            public boolean contains(Object v) {
               return Byte2ReferenceLinkedOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Byte2ReferenceLinkedOpenHashMap.this.clear();
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
      byte[] key = this.key;
      V[] value = this.value;
      int mask = newN - 1;
      byte[] newKey = new byte[newN + 1];
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
         if (key[i] == 0) {
            pos = newN;
         } else {
            pos = HashCommon.mix(key[i]) & mask;

            while (newKey[pos] != 0) {
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

   public Byte2ReferenceLinkedOpenHashMap<V> clone() {
      Byte2ReferenceLinkedOpenHashMap<V> c;
      try {
         c = (Byte2ReferenceLinkedOpenHashMap<V>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (byte[])this.key.clone();
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
         while (this.key[i] == 0) {
            i++;
         }

         t = this.key[i];
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
      byte[] key = this.key;
      V[] value = this.value;
      Byte2ReferenceLinkedOpenHashMap<V>.EntryIterator i = new Byte2ReferenceLinkedOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeByte(key[e]);
         s.writeObject(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      byte[] key = this.key = new byte[this.n + 1];
      V[] value = this.value = (V[])(new Object[this.n + 1]);
      long[] link = this.link = new long[this.n + 1];
      int prev = -1;
      this.first = this.last = -1;
      int i = this.size;

      while (i-- != 0) {
         byte k = s.readByte();
         V v = (V)s.readObject();
         int pos;
         if (k == 0) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            pos = HashCommon.mix((int)k) & this.mask;

            while (key[pos] != 0) {
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
      extends Byte2ReferenceLinkedOpenHashMap<V>.MapIterator<Consumer<? super Byte2ReferenceMap.Entry<V>>>
      implements ObjectListIterator<Byte2ReferenceMap.Entry<V>> {
      private Byte2ReferenceLinkedOpenHashMap<V>.MapEntry entry;

      public EntryIterator() {
      }

      public EntryIterator(byte from) {
         super(from);
      }

      final void acceptOnIndex(Consumer<? super Byte2ReferenceMap.Entry<V>> action, int index) {
         action.accept(Byte2ReferenceLinkedOpenHashMap.this.new MapEntry(index));
      }

      public Byte2ReferenceLinkedOpenHashMap<V>.MapEntry next() {
         return this.entry = Byte2ReferenceLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Byte2ReferenceLinkedOpenHashMap<V>.MapEntry previous() {
         return this.entry = Byte2ReferenceLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class FastEntryIterator
      extends Byte2ReferenceLinkedOpenHashMap<V>.MapIterator<Consumer<? super Byte2ReferenceMap.Entry<V>>>
      implements ObjectListIterator<Byte2ReferenceMap.Entry<V>> {
      final Byte2ReferenceLinkedOpenHashMap<V>.MapEntry entry;

      public FastEntryIterator() {
         this.entry = Byte2ReferenceLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(byte from) {
         super(from);
         this.entry = Byte2ReferenceLinkedOpenHashMap.this.new MapEntry();
      }

      final void acceptOnIndex(Consumer<? super Byte2ReferenceMap.Entry<V>> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }

      public Byte2ReferenceLinkedOpenHashMap<V>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Byte2ReferenceLinkedOpenHashMap<V>.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private final class KeyIterator extends Byte2ReferenceLinkedOpenHashMap<V>.MapIterator<ByteConsumer> implements ByteListIterator {
      public KeyIterator(byte k) {
         super(k);
      }

      @Override
      public byte previousByte() {
         return Byte2ReferenceLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
      }

      final void acceptOnIndex(ByteConsumer action, int index) {
         action.accept(Byte2ReferenceLinkedOpenHashMap.this.key[index]);
      }

      @Override
      public byte nextByte() {
         return Byte2ReferenceLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractByteSortedSet {
      private static final int SPLITERATOR_CHARACTERISTICS = 337;

      private KeySet() {
      }

      public ByteListIterator iterator(byte from) {
         return Byte2ReferenceLinkedOpenHashMap.this.new KeyIterator(from);
      }

      public ByteListIterator iterator() {
         return Byte2ReferenceLinkedOpenHashMap.this.new KeyIterator();
      }

      @Override
      public ByteSpliterator spliterator() {
         return ByteSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Byte2ReferenceLinkedOpenHashMap.this), 337);
      }

      @Override
      public void forEach(ByteConsumer consumer) {
         int i = Byte2ReferenceLinkedOpenHashMap.this.size;
         int next = Byte2ReferenceLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Byte2ReferenceLinkedOpenHashMap.this.link[next];
            consumer.accept(Byte2ReferenceLinkedOpenHashMap.this.key[curr]);
         }
      }

      @Override
      public int size() {
         return Byte2ReferenceLinkedOpenHashMap.this.size;
      }

      @Override
      public boolean contains(byte k) {
         return Byte2ReferenceLinkedOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(byte k) {
         int oldSize = Byte2ReferenceLinkedOpenHashMap.this.size;
         Byte2ReferenceLinkedOpenHashMap.this.remove(k);
         return Byte2ReferenceLinkedOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Byte2ReferenceLinkedOpenHashMap.this.clear();
      }

      @Override
      public byte firstByte() {
         if (Byte2ReferenceLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Byte2ReferenceLinkedOpenHashMap.this.key[Byte2ReferenceLinkedOpenHashMap.this.first];
         }
      }

      @Override
      public byte lastByte() {
         if (Byte2ReferenceLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Byte2ReferenceLinkedOpenHashMap.this.key[Byte2ReferenceLinkedOpenHashMap.this.last];
         }
      }

      @Override
      public ByteComparator comparator() {
         return null;
      }

      @Override
      public ByteSortedSet tailSet(byte from) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ByteSortedSet headSet(byte to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ByteSortedSet subSet(byte from, byte to) {
         throw new UnsupportedOperationException();
      }
   }

   final class MapEntry implements Byte2ReferenceMap.Entry<V>, Entry<Byte, V>, ByteReferencePair<V> {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public byte getByteKey() {
         return Byte2ReferenceLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public byte leftByte() {
         return Byte2ReferenceLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public V getValue() {
         return Byte2ReferenceLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public V right() {
         return Byte2ReferenceLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public V setValue(V v) {
         V oldValue = Byte2ReferenceLinkedOpenHashMap.this.value[this.index];
         Byte2ReferenceLinkedOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      public ByteReferencePair<V> right(V v) {
         Byte2ReferenceLinkedOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Byte getKey() {
         return Byte2ReferenceLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Byte, V> e = (Entry<Byte, V>)o;
            return Byte2ReferenceLinkedOpenHashMap.this.key[this.index] == e.getKey() && Byte2ReferenceLinkedOpenHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return Byte2ReferenceLinkedOpenHashMap.this.key[this.index]
            ^ (
               Byte2ReferenceLinkedOpenHashMap.this.value[this.index] == null
                  ? 0
                  : System.identityHashCode(Byte2ReferenceLinkedOpenHashMap.this.value[this.index])
            );
      }

      @Override
      public String toString() {
         return Byte2ReferenceLinkedOpenHashMap.this.key[this.index] + "=>" + Byte2ReferenceLinkedOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Byte2ReferenceMap.Entry<V>> implements Byte2ReferenceSortedMap.FastSortedEntrySet<V> {
      private static final int SPLITERATOR_CHARACTERISTICS = 81;

      private MapEntrySet() {
      }

      @Override
      public ObjectBidirectionalIterator<Byte2ReferenceMap.Entry<V>> iterator() {
         return Byte2ReferenceLinkedOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectSpliterator<Byte2ReferenceMap.Entry<V>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Byte2ReferenceLinkedOpenHashMap.this), 81);
      }

      @Override
      public Comparator<? super Byte2ReferenceMap.Entry<V>> comparator() {
         return null;
      }

      public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> subSet(Byte2ReferenceMap.Entry<V> fromElement, Byte2ReferenceMap.Entry<V> toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> headSet(Byte2ReferenceMap.Entry<V> toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Byte2ReferenceMap.Entry<V>> tailSet(Byte2ReferenceMap.Entry<V> fromElement) {
         throw new UnsupportedOperationException();
      }

      public Byte2ReferenceMap.Entry<V> first() {
         if (Byte2ReferenceLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Byte2ReferenceLinkedOpenHashMap.this.new MapEntry(Byte2ReferenceLinkedOpenHashMap.this.first);
         }
      }

      public Byte2ReferenceMap.Entry<V> last() {
         if (Byte2ReferenceLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Byte2ReferenceLinkedOpenHashMap.this.new MapEntry(Byte2ReferenceLinkedOpenHashMap.this.last);
         }
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() != null && e.getKey() instanceof Byte) {
               byte k = (Byte)e.getKey();
               V v = (V)e.getValue();
               if (k == 0) {
                  return Byte2ReferenceLinkedOpenHashMap.this.containsNullKey
                     && Byte2ReferenceLinkedOpenHashMap.this.value[Byte2ReferenceLinkedOpenHashMap.this.n] == v;
               } else {
                  byte[] key = Byte2ReferenceLinkedOpenHashMap.this.key;
                  byte curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix((int)k) & Byte2ReferenceLinkedOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (k == curr) {
                     return Byte2ReferenceLinkedOpenHashMap.this.value[pos] == v;
                  } else {
                     while ((curr = key[pos = pos + 1 & Byte2ReferenceLinkedOpenHashMap.this.mask]) != 0) {
                        if (k == curr) {
                           return Byte2ReferenceLinkedOpenHashMap.this.value[pos] == v;
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
            if (e.getKey() != null && e.getKey() instanceof Byte) {
               byte k = (Byte)e.getKey();
               V v = (V)e.getValue();
               if (k == 0) {
                  if (Byte2ReferenceLinkedOpenHashMap.this.containsNullKey
                     && Byte2ReferenceLinkedOpenHashMap.this.value[Byte2ReferenceLinkedOpenHashMap.this.n] == v) {
                     Byte2ReferenceLinkedOpenHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  byte[] key = Byte2ReferenceLinkedOpenHashMap.this.key;
                  byte curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix((int)k) & Byte2ReferenceLinkedOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (curr == k) {
                     if (Byte2ReferenceLinkedOpenHashMap.this.value[pos] == v) {
                        Byte2ReferenceLinkedOpenHashMap.this.removeEntry(pos);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     while ((curr = key[pos = pos + 1 & Byte2ReferenceLinkedOpenHashMap.this.mask]) != 0) {
                        if (curr == k && Byte2ReferenceLinkedOpenHashMap.this.value[pos] == v) {
                           Byte2ReferenceLinkedOpenHashMap.this.removeEntry(pos);
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
         return Byte2ReferenceLinkedOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Byte2ReferenceLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Byte2ReferenceMap.Entry<V>> iterator(Byte2ReferenceMap.Entry<V> from) {
         return Byte2ReferenceLinkedOpenHashMap.this.new EntryIterator(from.getByteKey());
      }

      public ObjectListIterator<Byte2ReferenceMap.Entry<V>> fastIterator() {
         return Byte2ReferenceLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Byte2ReferenceMap.Entry<V>> fastIterator(Byte2ReferenceMap.Entry<V> from) {
         return Byte2ReferenceLinkedOpenHashMap.this.new FastEntryIterator(from.getByteKey());
      }

      @Override
      public void forEach(Consumer<? super Byte2ReferenceMap.Entry<V>> consumer) {
         int i = Byte2ReferenceLinkedOpenHashMap.this.size;
         int next = Byte2ReferenceLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Byte2ReferenceLinkedOpenHashMap.this.link[next];
            consumer.accept(Byte2ReferenceLinkedOpenHashMap.this.new MapEntry(curr));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Byte2ReferenceMap.Entry<V>> consumer) {
         Byte2ReferenceLinkedOpenHashMap<V>.MapEntry entry = Byte2ReferenceLinkedOpenHashMap.this.new MapEntry();
         int i = Byte2ReferenceLinkedOpenHashMap.this.size;
         int next = Byte2ReferenceLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            entry.index = next;
            next = (int)Byte2ReferenceLinkedOpenHashMap.this.link[next];
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
         this.next = Byte2ReferenceLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(byte from) {
         if (from == 0) {
            if (Byte2ReferenceLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Byte2ReferenceLinkedOpenHashMap.this.link[Byte2ReferenceLinkedOpenHashMap.this.n];
               this.prev = Byte2ReferenceLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
         } else if (Byte2ReferenceLinkedOpenHashMap.this.key[Byte2ReferenceLinkedOpenHashMap.this.last] == from) {
            this.prev = Byte2ReferenceLinkedOpenHashMap.this.last;
            this.index = Byte2ReferenceLinkedOpenHashMap.this.size;
         } else {
            for (int pos = HashCommon.mix((int)from) & Byte2ReferenceLinkedOpenHashMap.this.mask;
               Byte2ReferenceLinkedOpenHashMap.this.key[pos] != 0;
               pos = pos + 1 & Byte2ReferenceLinkedOpenHashMap.this.mask
            ) {
               if (Byte2ReferenceLinkedOpenHashMap.this.key[pos] == from) {
                  this.next = (int)Byte2ReferenceLinkedOpenHashMap.this.link[pos];
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
               this.index = Byte2ReferenceLinkedOpenHashMap.this.size;
            } else {
               int pos = Byte2ReferenceLinkedOpenHashMap.this.first;

               for (this.index = 1; pos != this.prev; this.index++) {
                  pos = (int)Byte2ReferenceLinkedOpenHashMap.this.link[pos];
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
            this.next = (int)Byte2ReferenceLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Byte2ReferenceLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
            this.next = (int)Byte2ReferenceLinkedOpenHashMap.this.link[this.curr];
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
               this.prev = (int)(Byte2ReferenceLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Byte2ReferenceLinkedOpenHashMap.this.link[this.curr];
            }

            Byte2ReferenceLinkedOpenHashMap.this.size--;
            if (this.prev == -1) {
               Byte2ReferenceLinkedOpenHashMap.this.first = this.next;
            } else {
               Byte2ReferenceLinkedOpenHashMap.this.link[this.prev] = Byte2ReferenceLinkedOpenHashMap.this.link[this.prev]
                  ^ (Byte2ReferenceLinkedOpenHashMap.this.link[this.prev] ^ this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Byte2ReferenceLinkedOpenHashMap.this.last = this.prev;
            } else {
               Byte2ReferenceLinkedOpenHashMap.this.link[this.next] = Byte2ReferenceLinkedOpenHashMap.this.link[this.next]
                  ^ (Byte2ReferenceLinkedOpenHashMap.this.link[this.next] ^ (this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int pos = this.curr;
            this.curr = -1;
            if (pos == Byte2ReferenceLinkedOpenHashMap.this.n) {
               Byte2ReferenceLinkedOpenHashMap.this.containsNullKey = false;
               Byte2ReferenceLinkedOpenHashMap.this.value[Byte2ReferenceLinkedOpenHashMap.this.n] = null;
            } else {
               byte[] key = Byte2ReferenceLinkedOpenHashMap.this.key;

               label61:
               while (true) {
                  int last = pos;

                  byte curr;
                  for (pos = pos + 1 & Byte2ReferenceLinkedOpenHashMap.this.mask;
                     (curr = key[pos]) != 0;
                     pos = pos + 1 & Byte2ReferenceLinkedOpenHashMap.this.mask
                  ) {
                     int slot = HashCommon.mix((int)curr) & Byte2ReferenceLinkedOpenHashMap.this.mask;
                     if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                        key[last] = curr;
                        Byte2ReferenceLinkedOpenHashMap.this.value[last] = Byte2ReferenceLinkedOpenHashMap.this.value[pos];
                        if (this.next == pos) {
                           this.next = last;
                        }

                        if (this.prev == pos) {
                           this.prev = last;
                        }

                        Byte2ReferenceLinkedOpenHashMap.this.fixPointers(pos, last);
                        continue label61;
                     }
                  }

                  key[last] = 0;
                  Byte2ReferenceLinkedOpenHashMap.this.value[last] = null;
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

      public void set(Byte2ReferenceMap.Entry<V> ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Byte2ReferenceMap.Entry<V> ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class ValueIterator extends Byte2ReferenceLinkedOpenHashMap<V>.MapIterator<Consumer<? super V>> implements ObjectListIterator<V> {
      @Override
      public V previous() {
         return Byte2ReferenceLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
      }

      final void acceptOnIndex(Consumer<? super V> action, int index) {
         action.accept(Byte2ReferenceLinkedOpenHashMap.this.value[index]);
      }

      @Override
      public V next() {
         return Byte2ReferenceLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }
}
