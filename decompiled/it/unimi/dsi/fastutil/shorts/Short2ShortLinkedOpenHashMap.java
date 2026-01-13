package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
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
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public class Short2ShortLinkedOpenHashMap extends AbstractShort2ShortSortedMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient short[] key;
   protected transient short[] value;
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
   protected transient Short2ShortSortedMap.FastSortedEntrySet entries;
   protected transient ShortSortedSet keys;
   protected transient ShortCollection values;

   public Short2ShortLinkedOpenHashMap(int expected, float f) {
      if (f <= 0.0F || f >= 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      } else if (expected < 0) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.arraySize(expected, f);
         this.mask = this.n - 1;
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = new short[this.n + 1];
         this.value = new short[this.n + 1];
         this.link = new long[this.n + 1];
      }
   }

   public Short2ShortLinkedOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Short2ShortLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Short2ShortLinkedOpenHashMap(Map<? extends Short, ? extends Short> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Short2ShortLinkedOpenHashMap(Map<? extends Short, ? extends Short> m) {
      this(m, 0.75F);
   }

   public Short2ShortLinkedOpenHashMap(Short2ShortMap m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Short2ShortLinkedOpenHashMap(Short2ShortMap m) {
      this(m, 0.75F);
   }

   public Short2ShortLinkedOpenHashMap(short[] k, short[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Short2ShortLinkedOpenHashMap(short[] k, short[] v) {
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

   private short removeEntry(int pos) {
      short oldValue = this.value[pos];
      this.size--;
      this.fixPointers(pos);
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private short removeNullEntry() {
      this.containsNullKey = false;
      short oldValue = this.value[this.n];
      this.size--;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends Short, ? extends Short> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(short k) {
      if (k == 0) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         short[] key = this.key;
         short curr;
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

   private void insert(int pos, short k, short v) {
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
   public short put(short k, short v) {
      int pos = this.find(k);
      if (pos < 0) {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      } else {
         short oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   private short addToValue(int pos, short incr) {
      short oldValue = this.value[pos];
      this.value[pos] = (short)(oldValue + incr);
      return oldValue;
   }

   public short addTo(short k, short incr) {
      int pos;
      if (k == 0) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, incr);
         }

         pos = this.n;
         this.containsNullKey = true;
      } else {
         short[] key = this.key;
         short curr;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) != 0) {
            if (curr == k) {
               return this.addToValue(pos, incr);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (curr == k) {
                  return this.addToValue(pos, incr);
               }
            }
         }
      }

      this.key[pos] = k;
      this.value[pos] = (short)(this.defRetValue + incr);
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

      return this.defRetValue;
   }

   protected final void shiftKeys(int pos) {
      short[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         short curr;
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
         return;
      }
   }

   @Override
   public short remove(short k) {
      if (k == 0) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         short[] key = this.key;
         short curr;
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

   private short setValue(int pos, short v) {
      short oldValue = this.value[pos];
      this.value[pos] = v;
      return oldValue;
   }

   public short removeFirstShort() {
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
         short v = this.value[pos];
         if (pos == this.n) {
            this.containsNullKey = false;
         } else {
            this.shiftKeys(pos);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return v;
      }
   }

   public short removeLastShort() {
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
         short v = this.value[pos];
         if (pos == this.n) {
            this.containsNullKey = false;
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

   public short getAndMoveToFirst(short k) {
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         short[] key = this.key;
         short curr;
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

   public short getAndMoveToLast(short k) {
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         short[] key = this.key;
         short curr;
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

   public short putAndMoveToFirst(short k, short v) {
      int pos;
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         short[] key = this.key;
         short curr;
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

   public short putAndMoveToLast(short k, short v) {
      int pos;
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         short[] key = this.key;
         short curr;
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
   public short get(short k) {
      if (k == 0) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         short[] key = this.key;
         short curr;
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
   public boolean containsKey(short k) {
      if (k == 0) {
         return this.containsNullKey;
      } else {
         short[] key = this.key;
         short curr;
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
   public boolean containsValue(short v) {
      short[] value = this.value;
      short[] key = this.key;
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
   public short getOrDefault(short k, short defaultValue) {
      if (k == 0) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         short[] key = this.key;
         short curr;
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
   public short putIfAbsent(short k, short v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(short k, short v) {
      if (k == 0) {
         if (this.containsNullKey && v == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         short[] key = this.key;
         short curr;
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
   public boolean replace(short k, short oldValue, short v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public short replace(short k, short v) {
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         short oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   @Override
   public short computeIfAbsent(short k, IntUnaryOperator mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         short newValue = SafeMath.safeIntToShort(mappingFunction.applyAsInt(k));
         this.insert(-pos - 1, k, newValue);
         return newValue;
      }
   }

   @Override
   public short computeIfAbsent(short key, Short2ShortFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         short newValue = mappingFunction.get(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public short computeIfAbsentNullable(short k, IntFunction<? extends Short> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         Short newValue = mappingFunction.apply(k);
         if (newValue == null) {
            return this.defRetValue;
         } else {
            short v = newValue;
            this.insert(-pos - 1, k, v);
            return v;
         }
      }
   }

   @Override
   public short computeIfPresent(short k, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Short newValue = remappingFunction.apply(k, this.value[pos]);
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
   public short compute(short k, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Short newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (k == 0) {
               this.removeNullEntry();
            } else {
               this.removeEntry(pos);
            }
         }

         return this.defRetValue;
      } else {
         short newVal = newValue;
         if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
         } else {
            return this.value[pos] = newVal;
         }
      }
   }

   @Override
   public short merge(short k, short v, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         if (pos < 0) {
            this.insert(-pos - 1, k, v);
         } else {
            this.value[pos] = v;
         }

         return v;
      } else {
         Short newValue = remappingFunction.apply(this.value[pos], v);
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
   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
         Arrays.fill(this.key, (short)0);
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
   public short firstShortKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   @Override
   public short lastShortKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   @Override
   public Short2ShortSortedMap tailMap(short from) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Short2ShortSortedMap headMap(short to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Short2ShortSortedMap subMap(short from, short to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public ShortComparator comparator() {
      return null;
   }

   public Short2ShortSortedMap.FastSortedEntrySet short2ShortEntrySet() {
      if (this.entries == null) {
         this.entries = new Short2ShortLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public ShortSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Short2ShortLinkedOpenHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ShortCollection values() {
      if (this.values == null) {
         this.values = new AbstractShortCollection() {
            private static final int SPLITERATOR_CHARACTERISTICS = 336;

            @Override
            public ShortIterator iterator() {
               return Short2ShortLinkedOpenHashMap.this.new ValueIterator();
            }

            @Override
            public ShortSpliterator spliterator() {
               return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Short2ShortLinkedOpenHashMap.this), 336);
            }

            @Override
            public void forEach(ShortConsumer consumer) {
               int i = Short2ShortLinkedOpenHashMap.this.size;
               int next = Short2ShortLinkedOpenHashMap.this.first;

               while (i-- != 0) {
                  int curr = next;
                  next = (int)Short2ShortLinkedOpenHashMap.this.link[next];
                  consumer.accept(Short2ShortLinkedOpenHashMap.this.value[curr]);
               }
            }

            @Override
            public int size() {
               return Short2ShortLinkedOpenHashMap.this.size;
            }

            @Override
            public boolean contains(short v) {
               return Short2ShortLinkedOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Short2ShortLinkedOpenHashMap.this.clear();
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
      short[] key = this.key;
      short[] value = this.value;
      int mask = newN - 1;
      short[] newKey = new short[newN + 1];
      short[] newValue = new short[newN + 1];
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

   public Short2ShortLinkedOpenHashMap clone() {
      Short2ShortLinkedOpenHashMap c;
      try {
         c = (Short2ShortLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (short[])this.key.clone();
      c.value = (short[])this.value.clone();
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

         int var5 = this.key[i];
         var5 ^= this.value[i];
         h += var5;
      }

      if (this.containsNullKey) {
         h += this.value[this.n];
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      short[] key = this.key;
      short[] value = this.value;
      Short2ShortLinkedOpenHashMap.EntryIterator i = new Short2ShortLinkedOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeShort(key[e]);
         s.writeShort(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      short[] key = this.key = new short[this.n + 1];
      short[] value = this.value = new short[this.n + 1];
      long[] link = this.link = new long[this.n + 1];
      int prev = -1;
      this.first = this.last = -1;
      int i = this.size;

      while (i-- != 0) {
         short k = s.readShort();
         short v = s.readShort();
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
      extends Short2ShortLinkedOpenHashMap.MapIterator<Consumer<? super Short2ShortMap.Entry>>
      implements ObjectListIterator<Short2ShortMap.Entry> {
      private Short2ShortLinkedOpenHashMap.MapEntry entry;

      public EntryIterator() {
      }

      public EntryIterator(short from) {
         super(from);
      }

      final void acceptOnIndex(Consumer<? super Short2ShortMap.Entry> action, int index) {
         action.accept(Short2ShortLinkedOpenHashMap.this.new MapEntry(index));
      }

      public Short2ShortLinkedOpenHashMap.MapEntry next() {
         return this.entry = Short2ShortLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Short2ShortLinkedOpenHashMap.MapEntry previous() {
         return this.entry = Short2ShortLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class FastEntryIterator
      extends Short2ShortLinkedOpenHashMap.MapIterator<Consumer<? super Short2ShortMap.Entry>>
      implements ObjectListIterator<Short2ShortMap.Entry> {
      final Short2ShortLinkedOpenHashMap.MapEntry entry;

      public FastEntryIterator() {
         this.entry = Short2ShortLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(short from) {
         super(from);
         this.entry = Short2ShortLinkedOpenHashMap.this.new MapEntry();
      }

      final void acceptOnIndex(Consumer<? super Short2ShortMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }

      public Short2ShortLinkedOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Short2ShortLinkedOpenHashMap.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private final class KeyIterator extends Short2ShortLinkedOpenHashMap.MapIterator<ShortConsumer> implements ShortListIterator {
      public KeyIterator(short k) {
         super(k);
      }

      @Override
      public short previousShort() {
         return Short2ShortLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
      }

      final void acceptOnIndex(ShortConsumer action, int index) {
         action.accept(Short2ShortLinkedOpenHashMap.this.key[index]);
      }

      @Override
      public short nextShort() {
         return Short2ShortLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractShortSortedSet {
      private static final int SPLITERATOR_CHARACTERISTICS = 337;

      private KeySet() {
      }

      public ShortListIterator iterator(short from) {
         return Short2ShortLinkedOpenHashMap.this.new KeyIterator(from);
      }

      public ShortListIterator iterator() {
         return Short2ShortLinkedOpenHashMap.this.new KeyIterator();
      }

      @Override
      public ShortSpliterator spliterator() {
         return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Short2ShortLinkedOpenHashMap.this), 337);
      }

      @Override
      public void forEach(ShortConsumer consumer) {
         int i = Short2ShortLinkedOpenHashMap.this.size;
         int next = Short2ShortLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Short2ShortLinkedOpenHashMap.this.link[next];
            consumer.accept(Short2ShortLinkedOpenHashMap.this.key[curr]);
         }
      }

      @Override
      public int size() {
         return Short2ShortLinkedOpenHashMap.this.size;
      }

      @Override
      public boolean contains(short k) {
         return Short2ShortLinkedOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(short k) {
         int oldSize = Short2ShortLinkedOpenHashMap.this.size;
         Short2ShortLinkedOpenHashMap.this.remove(k);
         return Short2ShortLinkedOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Short2ShortLinkedOpenHashMap.this.clear();
      }

      @Override
      public short firstShort() {
         if (Short2ShortLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Short2ShortLinkedOpenHashMap.this.key[Short2ShortLinkedOpenHashMap.this.first];
         }
      }

      @Override
      public short lastShort() {
         if (Short2ShortLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Short2ShortLinkedOpenHashMap.this.key[Short2ShortLinkedOpenHashMap.this.last];
         }
      }

      @Override
      public ShortComparator comparator() {
         return null;
      }

      @Override
      public ShortSortedSet tailSet(short from) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ShortSortedSet headSet(short to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ShortSortedSet subSet(short from, short to) {
         throw new UnsupportedOperationException();
      }
   }

   final class MapEntry implements Short2ShortMap.Entry, Entry<Short, Short>, ShortShortPair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public short getShortKey() {
         return Short2ShortLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public short leftShort() {
         return Short2ShortLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public short getShortValue() {
         return Short2ShortLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public short rightShort() {
         return Short2ShortLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public short setValue(short v) {
         short oldValue = Short2ShortLinkedOpenHashMap.this.value[this.index];
         Short2ShortLinkedOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public ShortShortPair right(short v) {
         Short2ShortLinkedOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Short getKey() {
         return Short2ShortLinkedOpenHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Short getValue() {
         return Short2ShortLinkedOpenHashMap.this.value[this.index];
      }

      @Deprecated
      @Override
      public Short setValue(Short v) {
         return this.setValue(v.shortValue());
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Short, Short> e = (Entry<Short, Short>)o;
            return Short2ShortLinkedOpenHashMap.this.key[this.index] == e.getKey() && Short2ShortLinkedOpenHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return Short2ShortLinkedOpenHashMap.this.key[this.index] ^ Short2ShortLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public String toString() {
         return Short2ShortLinkedOpenHashMap.this.key[this.index] + "=>" + Short2ShortLinkedOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Short2ShortMap.Entry> implements Short2ShortSortedMap.FastSortedEntrySet {
      private static final int SPLITERATOR_CHARACTERISTICS = 81;

      private MapEntrySet() {
      }

      @Override
      public ObjectBidirectionalIterator<Short2ShortMap.Entry> iterator() {
         return Short2ShortLinkedOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectSpliterator<Short2ShortMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Short2ShortLinkedOpenHashMap.this), 81);
      }

      @Override
      public Comparator<? super Short2ShortMap.Entry> comparator() {
         return null;
      }

      public ObjectSortedSet<Short2ShortMap.Entry> subSet(Short2ShortMap.Entry fromElement, Short2ShortMap.Entry toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Short2ShortMap.Entry> headSet(Short2ShortMap.Entry toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Short2ShortMap.Entry> tailSet(Short2ShortMap.Entry fromElement) {
         throw new UnsupportedOperationException();
      }

      public Short2ShortMap.Entry first() {
         if (Short2ShortLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Short2ShortLinkedOpenHashMap.this.new MapEntry(Short2ShortLinkedOpenHashMap.this.first);
         }
      }

      public Short2ShortMap.Entry last() {
         if (Short2ShortLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Short2ShortLinkedOpenHashMap.this.new MapEntry(Short2ShortLinkedOpenHashMap.this.last);
         }
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Short) {
               short k = (Short)e.getKey();
               short v = (Short)e.getValue();
               if (k == 0) {
                  return Short2ShortLinkedOpenHashMap.this.containsNullKey && Short2ShortLinkedOpenHashMap.this.value[Short2ShortLinkedOpenHashMap.this.n] == v;
               } else {
                  short[] key = Short2ShortLinkedOpenHashMap.this.key;
                  short curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix((int)k) & Short2ShortLinkedOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (k == curr) {
                     return Short2ShortLinkedOpenHashMap.this.value[pos] == v;
                  } else {
                     while ((curr = key[pos = pos + 1 & Short2ShortLinkedOpenHashMap.this.mask]) != 0) {
                        if (k == curr) {
                           return Short2ShortLinkedOpenHashMap.this.value[pos] == v;
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
            if (e.getKey() != null && e.getKey() instanceof Short) {
               if (e.getValue() != null && e.getValue() instanceof Short) {
                  short k = (Short)e.getKey();
                  short v = (Short)e.getValue();
                  if (k == 0) {
                     if (Short2ShortLinkedOpenHashMap.this.containsNullKey && Short2ShortLinkedOpenHashMap.this.value[Short2ShortLinkedOpenHashMap.this.n] == v
                        )
                      {
                        Short2ShortLinkedOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     short[] key = Short2ShortLinkedOpenHashMap.this.key;
                     short curr;
                     int pos;
                     if ((curr = key[pos = HashCommon.mix((int)k) & Short2ShortLinkedOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (curr == k) {
                        if (Short2ShortLinkedOpenHashMap.this.value[pos] == v) {
                           Short2ShortLinkedOpenHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while ((curr = key[pos = pos + 1 & Short2ShortLinkedOpenHashMap.this.mask]) != 0) {
                           if (curr == k && Short2ShortLinkedOpenHashMap.this.value[pos] == v) {
                              Short2ShortLinkedOpenHashMap.this.removeEntry(pos);
                              return true;
                           }
                        }

                        return false;
                     }
                  }
               } else {
                  return false;
               }
            } else {
               return false;
            }
         }
      }

      @Override
      public int size() {
         return Short2ShortLinkedOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Short2ShortLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Short2ShortMap.Entry> iterator(Short2ShortMap.Entry from) {
         return Short2ShortLinkedOpenHashMap.this.new EntryIterator(from.getShortKey());
      }

      public ObjectListIterator<Short2ShortMap.Entry> fastIterator() {
         return Short2ShortLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Short2ShortMap.Entry> fastIterator(Short2ShortMap.Entry from) {
         return Short2ShortLinkedOpenHashMap.this.new FastEntryIterator(from.getShortKey());
      }

      @Override
      public void forEach(Consumer<? super Short2ShortMap.Entry> consumer) {
         int i = Short2ShortLinkedOpenHashMap.this.size;
         int next = Short2ShortLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Short2ShortLinkedOpenHashMap.this.link[next];
            consumer.accept(Short2ShortLinkedOpenHashMap.this.new MapEntry(curr));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Short2ShortMap.Entry> consumer) {
         Short2ShortLinkedOpenHashMap.MapEntry entry = Short2ShortLinkedOpenHashMap.this.new MapEntry();
         int i = Short2ShortLinkedOpenHashMap.this.size;
         int next = Short2ShortLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            entry.index = next;
            next = (int)Short2ShortLinkedOpenHashMap.this.link[next];
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
         this.next = Short2ShortLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(short from) {
         if (from == 0) {
            if (Short2ShortLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Short2ShortLinkedOpenHashMap.this.link[Short2ShortLinkedOpenHashMap.this.n];
               this.prev = Short2ShortLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
         } else if (Short2ShortLinkedOpenHashMap.this.key[Short2ShortLinkedOpenHashMap.this.last] == from) {
            this.prev = Short2ShortLinkedOpenHashMap.this.last;
            this.index = Short2ShortLinkedOpenHashMap.this.size;
         } else {
            for (int pos = HashCommon.mix((int)from) & Short2ShortLinkedOpenHashMap.this.mask;
               Short2ShortLinkedOpenHashMap.this.key[pos] != 0;
               pos = pos + 1 & Short2ShortLinkedOpenHashMap.this.mask
            ) {
               if (Short2ShortLinkedOpenHashMap.this.key[pos] == from) {
                  this.next = (int)Short2ShortLinkedOpenHashMap.this.link[pos];
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
               this.index = Short2ShortLinkedOpenHashMap.this.size;
            } else {
               int pos = Short2ShortLinkedOpenHashMap.this.first;

               for (this.index = 1; pos != this.prev; this.index++) {
                  pos = (int)Short2ShortLinkedOpenHashMap.this.link[pos];
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
            this.next = (int)Short2ShortLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Short2ShortLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
            this.next = (int)Short2ShortLinkedOpenHashMap.this.link[this.curr];
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
               this.prev = (int)(Short2ShortLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Short2ShortLinkedOpenHashMap.this.link[this.curr];
            }

            Short2ShortLinkedOpenHashMap.this.size--;
            if (this.prev == -1) {
               Short2ShortLinkedOpenHashMap.this.first = this.next;
            } else {
               Short2ShortLinkedOpenHashMap.this.link[this.prev] = Short2ShortLinkedOpenHashMap.this.link[this.prev]
                  ^ (Short2ShortLinkedOpenHashMap.this.link[this.prev] ^ this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Short2ShortLinkedOpenHashMap.this.last = this.prev;
            } else {
               Short2ShortLinkedOpenHashMap.this.link[this.next] = Short2ShortLinkedOpenHashMap.this.link[this.next]
                  ^ (Short2ShortLinkedOpenHashMap.this.link[this.next] ^ (this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int pos = this.curr;
            this.curr = -1;
            if (pos == Short2ShortLinkedOpenHashMap.this.n) {
               Short2ShortLinkedOpenHashMap.this.containsNullKey = false;
            } else {
               short[] key = Short2ShortLinkedOpenHashMap.this.key;

               label61:
               while (true) {
                  int last = pos;

                  short curr;
                  for (pos = pos + 1 & Short2ShortLinkedOpenHashMap.this.mask; (curr = key[pos]) != 0; pos = pos + 1 & Short2ShortLinkedOpenHashMap.this.mask) {
                     int slot = HashCommon.mix((int)curr) & Short2ShortLinkedOpenHashMap.this.mask;
                     if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                        key[last] = curr;
                        Short2ShortLinkedOpenHashMap.this.value[last] = Short2ShortLinkedOpenHashMap.this.value[pos];
                        if (this.next == pos) {
                           this.next = last;
                        }

                        if (this.prev == pos) {
                           this.prev = last;
                        }

                        Short2ShortLinkedOpenHashMap.this.fixPointers(pos, last);
                        continue label61;
                     }
                  }

                  key[last] = 0;
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

      public void set(Short2ShortMap.Entry ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Short2ShortMap.Entry ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class ValueIterator extends Short2ShortLinkedOpenHashMap.MapIterator<ShortConsumer> implements ShortListIterator {
      @Override
      public short previousShort() {
         return Short2ShortLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
      }

      final void acceptOnIndex(ShortConsumer action, int index) {
         action.accept(Short2ShortLinkedOpenHashMap.this.value[index]);
      }

      @Override
      public short nextShort() {
         return Short2ShortLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }
}
