package it.unimi.dsi.fastutil.floats;

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
import it.unimi.dsi.fastutil.shorts.AbstractShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortCollection;
import it.unimi.dsi.fastutil.shorts.ShortConsumer;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import it.unimi.dsi.fastutil.shorts.ShortListIterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterator;
import it.unimi.dsi.fastutil.shorts.ShortSpliterators;
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
import java.util.function.DoubleFunction;
import java.util.function.DoubleToIntFunction;

public class Float2ShortLinkedOpenHashMap extends AbstractFloat2ShortSortedMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient float[] key;
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
   protected transient Float2ShortSortedMap.FastSortedEntrySet entries;
   protected transient FloatSortedSet keys;
   protected transient ShortCollection values;

   public Float2ShortLinkedOpenHashMap(int expected, float f) {
      if (f <= 0.0F || f >= 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      } else if (expected < 0) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.arraySize(expected, f);
         this.mask = this.n - 1;
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = new float[this.n + 1];
         this.value = new short[this.n + 1];
         this.link = new long[this.n + 1];
      }
   }

   public Float2ShortLinkedOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Float2ShortLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Float2ShortLinkedOpenHashMap(Map<? extends Float, ? extends Short> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Float2ShortLinkedOpenHashMap(Map<? extends Float, ? extends Short> m) {
      this(m, 0.75F);
   }

   public Float2ShortLinkedOpenHashMap(Float2ShortMap m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Float2ShortLinkedOpenHashMap(Float2ShortMap m) {
      this(m, 0.75F);
   }

   public Float2ShortLinkedOpenHashMap(float[] k, short[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Float2ShortLinkedOpenHashMap(float[] k, short[] v) {
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
   public void putAll(Map<? extends Float, ? extends Short> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(float k) {
      if (Float.floatToIntBits(k) == 0) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) == 0) {
            return -(pos + 1);
         } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
            return pos;
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                  return pos;
               }
            }

            return -(pos + 1);
         }
      }
   }

   private void insert(int pos, float k, short v) {
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
   public short put(float k, short v) {
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

   public short addTo(float k, short incr) {
      int pos;
      if (Float.floatToIntBits(k) == 0) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, incr);
         }

         pos = this.n;
         this.containsNullKey = true;
      } else {
         float[] key = this.key;
         float curr;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) != 0) {
            if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
               return this.addToValue(pos, incr);
            }

            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
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
      float[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         float curr;
         for (pos = pos + 1 & this.mask; Float.floatToIntBits(curr = key[pos]) != 0; pos = pos + 1 & this.mask) {
            int slot = HashCommon.mix(HashCommon.float2int(curr)) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               this.value[last] = this.value[pos];
               this.fixPointers(pos, last);
               continue label30;
            }
         }

         key[last] = 0.0F;
         return;
      }
   }

   @Override
   public short remove(float k) {
      if (Float.floatToIntBits(k) == 0) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
            return this.removeEntry(pos);
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
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

   public short getAndMoveToFirst(float k) {
      if (Float.floatToIntBits(k) == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                  this.moveIndexToFirst(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public short getAndMoveToLast(float k) {
      if (Float.floatToIntBits(k) == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
            this.moveIndexToLast(pos);
            return this.value[pos];
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                  this.moveIndexToLast(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public short putAndMoveToFirst(float k, short v) {
      int pos;
      if (Float.floatToIntBits(k) == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         float[] key = this.key;
         float curr;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) != 0) {
            if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
               this.moveIndexToFirst(pos);
               return this.setValue(pos, v);
            }

            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
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

   public short putAndMoveToLast(float k, short v) {
      int pos;
      if (Float.floatToIntBits(k) == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         float[] key = this.key;
         float curr;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) != 0) {
            if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
               this.moveIndexToLast(pos);
               return this.setValue(pos, v);
            }

            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
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
   public short get(float k) {
      if (Float.floatToIntBits(k) == 0) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
            return this.value[pos];
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public boolean containsKey(float k) {
      if (Float.floatToIntBits(k) == 0) {
         return this.containsNullKey;
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) == 0) {
            return false;
         } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
            return true;
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
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
      float[] key = this.key;
      if (this.containsNullKey && value[this.n] == v) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (Float.floatToIntBits(key[i]) != 0 && value[i] == v) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public short getOrDefault(float k, short defaultValue) {
      if (Float.floatToIntBits(k) == 0) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) == 0) {
            return defaultValue;
         } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
            return this.value[pos];
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                  return this.value[pos];
               }
            }

            return defaultValue;
         }
      }
   }

   @Override
   public short putIfAbsent(float k, short v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(float k, short v) {
      if (Float.floatToIntBits(k) == 0) {
         if (this.containsNullKey && v == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask]) == 0) {
            return false;
         } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(k) == Float.floatToIntBits(curr) && v == this.value[pos]) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(float k, short oldValue, short v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public short replace(float k, short v) {
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
   public short computeIfAbsent(float k, DoubleToIntFunction mappingFunction) {
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
   public short computeIfAbsent(float key, Float2ShortFunction mappingFunction) {
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
   public short computeIfAbsentNullable(float k, DoubleFunction<? extends Short> mappingFunction) {
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
   public short computeIfPresent(float k, BiFunction<? super Float, ? super Short, ? extends Short> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Short newValue = remappingFunction.apply(k, this.value[pos]);
         if (newValue == null) {
            if (Float.floatToIntBits(k) == 0) {
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
   public short compute(float k, BiFunction<? super Float, ? super Short, ? extends Short> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Short newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (Float.floatToIntBits(k) == 0) {
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
   public short merge(float k, short v, BiFunction<? super Short, ? super Short, ? extends Short> remappingFunction) {
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
            if (Float.floatToIntBits(k) == 0) {
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
         Arrays.fill(this.key, 0.0F);
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
   public float firstFloatKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   @Override
   public float lastFloatKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   @Override
   public Float2ShortSortedMap tailMap(float from) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Float2ShortSortedMap headMap(float to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Float2ShortSortedMap subMap(float from, float to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public FloatComparator comparator() {
      return null;
   }

   public Float2ShortSortedMap.FastSortedEntrySet float2ShortEntrySet() {
      if (this.entries == null) {
         this.entries = new Float2ShortLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public FloatSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Float2ShortLinkedOpenHashMap.KeySet();
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
               return Float2ShortLinkedOpenHashMap.this.new ValueIterator();
            }

            @Override
            public ShortSpliterator spliterator() {
               return ShortSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Float2ShortLinkedOpenHashMap.this), 336);
            }

            @Override
            public void forEach(ShortConsumer consumer) {
               int i = Float2ShortLinkedOpenHashMap.this.size;
               int next = Float2ShortLinkedOpenHashMap.this.first;

               while (i-- != 0) {
                  int curr = next;
                  next = (int)Float2ShortLinkedOpenHashMap.this.link[next];
                  consumer.accept(Float2ShortLinkedOpenHashMap.this.value[curr]);
               }
            }

            @Override
            public int size() {
               return Float2ShortLinkedOpenHashMap.this.size;
            }

            @Override
            public boolean contains(short v) {
               return Float2ShortLinkedOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Float2ShortLinkedOpenHashMap.this.clear();
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
      float[] key = this.key;
      short[] value = this.value;
      int mask = newN - 1;
      float[] newKey = new float[newN + 1];
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
         if (Float.floatToIntBits(key[i]) == 0) {
            pos = newN;
         } else {
            pos = HashCommon.mix(HashCommon.float2int(key[i])) & mask;

            while (Float.floatToIntBits(newKey[pos]) != 0) {
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

   public Float2ShortLinkedOpenHashMap clone() {
      Float2ShortLinkedOpenHashMap c;
      try {
         c = (Float2ShortLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (float[])this.key.clone();
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
         while (Float.floatToIntBits(this.key[i]) == 0) {
            i++;
         }

         t = HashCommon.float2int(this.key[i]);
         t ^= this.value[i];
         h += t;
      }

      if (this.containsNullKey) {
         h += this.value[this.n];
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      float[] key = this.key;
      short[] value = this.value;
      Float2ShortLinkedOpenHashMap.EntryIterator i = new Float2ShortLinkedOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeFloat(key[e]);
         s.writeShort(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      float[] key = this.key = new float[this.n + 1];
      short[] value = this.value = new short[this.n + 1];
      long[] link = this.link = new long[this.n + 1];
      int prev = -1;
      this.first = this.last = -1;
      int i = this.size;

      while (i-- != 0) {
         float k = s.readFloat();
         short v = s.readShort();
         int pos;
         if (Float.floatToIntBits(k) == 0) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            pos = HashCommon.mix(HashCommon.float2int(k)) & this.mask;

            while (Float.floatToIntBits(key[pos]) != 0) {
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
      extends Float2ShortLinkedOpenHashMap.MapIterator<Consumer<? super Float2ShortMap.Entry>>
      implements ObjectListIterator<Float2ShortMap.Entry> {
      private Float2ShortLinkedOpenHashMap.MapEntry entry;

      public EntryIterator() {
      }

      public EntryIterator(float from) {
         super(from);
      }

      final void acceptOnIndex(Consumer<? super Float2ShortMap.Entry> action, int index) {
         action.accept(Float2ShortLinkedOpenHashMap.this.new MapEntry(index));
      }

      public Float2ShortLinkedOpenHashMap.MapEntry next() {
         return this.entry = Float2ShortLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Float2ShortLinkedOpenHashMap.MapEntry previous() {
         return this.entry = Float2ShortLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class FastEntryIterator
      extends Float2ShortLinkedOpenHashMap.MapIterator<Consumer<? super Float2ShortMap.Entry>>
      implements ObjectListIterator<Float2ShortMap.Entry> {
      final Float2ShortLinkedOpenHashMap.MapEntry entry;

      public FastEntryIterator() {
         this.entry = Float2ShortLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(float from) {
         super(from);
         this.entry = Float2ShortLinkedOpenHashMap.this.new MapEntry();
      }

      final void acceptOnIndex(Consumer<? super Float2ShortMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }

      public Float2ShortLinkedOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Float2ShortLinkedOpenHashMap.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private final class KeyIterator extends Float2ShortLinkedOpenHashMap.MapIterator<FloatConsumer> implements FloatListIterator {
      public KeyIterator(float k) {
         super(k);
      }

      @Override
      public float previousFloat() {
         return Float2ShortLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
      }

      final void acceptOnIndex(FloatConsumer action, int index) {
         action.accept(Float2ShortLinkedOpenHashMap.this.key[index]);
      }

      @Override
      public float nextFloat() {
         return Float2ShortLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractFloatSortedSet {
      private static final int SPLITERATOR_CHARACTERISTICS = 337;

      private KeySet() {
      }

      public FloatListIterator iterator(float from) {
         return Float2ShortLinkedOpenHashMap.this.new KeyIterator(from);
      }

      public FloatListIterator iterator() {
         return Float2ShortLinkedOpenHashMap.this.new KeyIterator();
      }

      @Override
      public FloatSpliterator spliterator() {
         return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Float2ShortLinkedOpenHashMap.this), 337);
      }

      @Override
      public void forEach(FloatConsumer consumer) {
         int i = Float2ShortLinkedOpenHashMap.this.size;
         int next = Float2ShortLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Float2ShortLinkedOpenHashMap.this.link[next];
            consumer.accept(Float2ShortLinkedOpenHashMap.this.key[curr]);
         }
      }

      @Override
      public int size() {
         return Float2ShortLinkedOpenHashMap.this.size;
      }

      @Override
      public boolean contains(float k) {
         return Float2ShortLinkedOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(float k) {
         int oldSize = Float2ShortLinkedOpenHashMap.this.size;
         Float2ShortLinkedOpenHashMap.this.remove(k);
         return Float2ShortLinkedOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Float2ShortLinkedOpenHashMap.this.clear();
      }

      @Override
      public float firstFloat() {
         if (Float2ShortLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Float2ShortLinkedOpenHashMap.this.key[Float2ShortLinkedOpenHashMap.this.first];
         }
      }

      @Override
      public float lastFloat() {
         if (Float2ShortLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Float2ShortLinkedOpenHashMap.this.key[Float2ShortLinkedOpenHashMap.this.last];
         }
      }

      @Override
      public FloatComparator comparator() {
         return null;
      }

      @Override
      public FloatSortedSet tailSet(float from) {
         throw new UnsupportedOperationException();
      }

      @Override
      public FloatSortedSet headSet(float to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public FloatSortedSet subSet(float from, float to) {
         throw new UnsupportedOperationException();
      }
   }

   final class MapEntry implements Float2ShortMap.Entry, Entry<Float, Short>, FloatShortPair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public float getFloatKey() {
         return Float2ShortLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public float leftFloat() {
         return Float2ShortLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public short getShortValue() {
         return Float2ShortLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public short rightShort() {
         return Float2ShortLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public short setValue(short v) {
         short oldValue = Float2ShortLinkedOpenHashMap.this.value[this.index];
         Float2ShortLinkedOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public FloatShortPair right(short v) {
         Float2ShortLinkedOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Float getKey() {
         return Float2ShortLinkedOpenHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Short getValue() {
         return Float2ShortLinkedOpenHashMap.this.value[this.index];
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
            Entry<Float, Short> e = (Entry<Float, Short>)o;
            return Float.floatToIntBits(Float2ShortLinkedOpenHashMap.this.key[this.index]) == Float.floatToIntBits(e.getKey())
               && Float2ShortLinkedOpenHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.float2int(Float2ShortLinkedOpenHashMap.this.key[this.index]) ^ Float2ShortLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public String toString() {
         return Float2ShortLinkedOpenHashMap.this.key[this.index] + "=>" + Float2ShortLinkedOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Float2ShortMap.Entry> implements Float2ShortSortedMap.FastSortedEntrySet {
      private static final int SPLITERATOR_CHARACTERISTICS = 81;

      private MapEntrySet() {
      }

      @Override
      public ObjectBidirectionalIterator<Float2ShortMap.Entry> iterator() {
         return Float2ShortLinkedOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectSpliterator<Float2ShortMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Float2ShortLinkedOpenHashMap.this), 81);
      }

      @Override
      public Comparator<? super Float2ShortMap.Entry> comparator() {
         return null;
      }

      public ObjectSortedSet<Float2ShortMap.Entry> subSet(Float2ShortMap.Entry fromElement, Float2ShortMap.Entry toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Float2ShortMap.Entry> headSet(Float2ShortMap.Entry toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Float2ShortMap.Entry> tailSet(Float2ShortMap.Entry fromElement) {
         throw new UnsupportedOperationException();
      }

      public Float2ShortMap.Entry first() {
         if (Float2ShortLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Float2ShortLinkedOpenHashMap.this.new MapEntry(Float2ShortLinkedOpenHashMap.this.first);
         }
      }

      public Float2ShortMap.Entry last() {
         if (Float2ShortLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Float2ShortLinkedOpenHashMap.this.new MapEntry(Float2ShortLinkedOpenHashMap.this.last);
         }
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Short) {
               float k = (Float)e.getKey();
               short v = (Short)e.getValue();
               if (Float.floatToIntBits(k) == 0) {
                  return Float2ShortLinkedOpenHashMap.this.containsNullKey && Float2ShortLinkedOpenHashMap.this.value[Float2ShortLinkedOpenHashMap.this.n] == v;
               } else {
                  float[] key = Float2ShortLinkedOpenHashMap.this.key;
                  float curr;
                  int pos;
                  if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & Float2ShortLinkedOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                     return Float2ShortLinkedOpenHashMap.this.value[pos] == v;
                  } else {
                     while (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2ShortLinkedOpenHashMap.this.mask]) != 0) {
                        if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                           return Float2ShortLinkedOpenHashMap.this.value[pos] == v;
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
            if (e.getKey() != null && e.getKey() instanceof Float) {
               if (e.getValue() != null && e.getValue() instanceof Short) {
                  float k = (Float)e.getKey();
                  short v = (Short)e.getValue();
                  if (Float.floatToIntBits(k) == 0) {
                     if (Float2ShortLinkedOpenHashMap.this.containsNullKey && Float2ShortLinkedOpenHashMap.this.value[Float2ShortLinkedOpenHashMap.this.n] == v
                        )
                      {
                        Float2ShortLinkedOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     float[] key = Float2ShortLinkedOpenHashMap.this.key;
                     float curr;
                     int pos;
                     if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & Float2ShortLinkedOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
                        if (Float2ShortLinkedOpenHashMap.this.value[pos] == v) {
                           Float2ShortLinkedOpenHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2ShortLinkedOpenHashMap.this.mask]) != 0) {
                           if (Float.floatToIntBits(curr) == Float.floatToIntBits(k) && Float2ShortLinkedOpenHashMap.this.value[pos] == v) {
                              Float2ShortLinkedOpenHashMap.this.removeEntry(pos);
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
         return Float2ShortLinkedOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Float2ShortLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Float2ShortMap.Entry> iterator(Float2ShortMap.Entry from) {
         return Float2ShortLinkedOpenHashMap.this.new EntryIterator(from.getFloatKey());
      }

      public ObjectListIterator<Float2ShortMap.Entry> fastIterator() {
         return Float2ShortLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Float2ShortMap.Entry> fastIterator(Float2ShortMap.Entry from) {
         return Float2ShortLinkedOpenHashMap.this.new FastEntryIterator(from.getFloatKey());
      }

      @Override
      public void forEach(Consumer<? super Float2ShortMap.Entry> consumer) {
         int i = Float2ShortLinkedOpenHashMap.this.size;
         int next = Float2ShortLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Float2ShortLinkedOpenHashMap.this.link[next];
            consumer.accept(Float2ShortLinkedOpenHashMap.this.new MapEntry(curr));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Float2ShortMap.Entry> consumer) {
         Float2ShortLinkedOpenHashMap.MapEntry entry = Float2ShortLinkedOpenHashMap.this.new MapEntry();
         int i = Float2ShortLinkedOpenHashMap.this.size;
         int next = Float2ShortLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            entry.index = next;
            next = (int)Float2ShortLinkedOpenHashMap.this.link[next];
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
         this.next = Float2ShortLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(float from) {
         if (Float.floatToIntBits(from) == 0) {
            if (Float2ShortLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Float2ShortLinkedOpenHashMap.this.link[Float2ShortLinkedOpenHashMap.this.n];
               this.prev = Float2ShortLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
         } else if (Float.floatToIntBits(Float2ShortLinkedOpenHashMap.this.key[Float2ShortLinkedOpenHashMap.this.last]) == Float.floatToIntBits(from)) {
            this.prev = Float2ShortLinkedOpenHashMap.this.last;
            this.index = Float2ShortLinkedOpenHashMap.this.size;
         } else {
            for (int pos = HashCommon.mix(HashCommon.float2int(from)) & Float2ShortLinkedOpenHashMap.this.mask;
               Float.floatToIntBits(Float2ShortLinkedOpenHashMap.this.key[pos]) != 0;
               pos = pos + 1 & Float2ShortLinkedOpenHashMap.this.mask
            ) {
               if (Float.floatToIntBits(Float2ShortLinkedOpenHashMap.this.key[pos]) == Float.floatToIntBits(from)) {
                  this.next = (int)Float2ShortLinkedOpenHashMap.this.link[pos];
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
               this.index = Float2ShortLinkedOpenHashMap.this.size;
            } else {
               int pos = Float2ShortLinkedOpenHashMap.this.first;

               for (this.index = 1; pos != this.prev; this.index++) {
                  pos = (int)Float2ShortLinkedOpenHashMap.this.link[pos];
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
            this.next = (int)Float2ShortLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Float2ShortLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
            this.next = (int)Float2ShortLinkedOpenHashMap.this.link[this.curr];
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
               this.prev = (int)(Float2ShortLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Float2ShortLinkedOpenHashMap.this.link[this.curr];
            }

            Float2ShortLinkedOpenHashMap.this.size--;
            if (this.prev == -1) {
               Float2ShortLinkedOpenHashMap.this.first = this.next;
            } else {
               Float2ShortLinkedOpenHashMap.this.link[this.prev] = Float2ShortLinkedOpenHashMap.this.link[this.prev]
                  ^ (Float2ShortLinkedOpenHashMap.this.link[this.prev] ^ this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Float2ShortLinkedOpenHashMap.this.last = this.prev;
            } else {
               Float2ShortLinkedOpenHashMap.this.link[this.next] = Float2ShortLinkedOpenHashMap.this.link[this.next]
                  ^ (Float2ShortLinkedOpenHashMap.this.link[this.next] ^ (this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int pos = this.curr;
            this.curr = -1;
            if (pos == Float2ShortLinkedOpenHashMap.this.n) {
               Float2ShortLinkedOpenHashMap.this.containsNullKey = false;
            } else {
               float[] key = Float2ShortLinkedOpenHashMap.this.key;

               label61:
               while (true) {
                  int last = pos;

                  float curr;
                  for (pos = pos + 1 & Float2ShortLinkedOpenHashMap.this.mask;
                     Float.floatToIntBits(curr = key[pos]) != 0;
                     pos = pos + 1 & Float2ShortLinkedOpenHashMap.this.mask
                  ) {
                     int slot = HashCommon.mix(HashCommon.float2int(curr)) & Float2ShortLinkedOpenHashMap.this.mask;
                     if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                        key[last] = curr;
                        Float2ShortLinkedOpenHashMap.this.value[last] = Float2ShortLinkedOpenHashMap.this.value[pos];
                        if (this.next == pos) {
                           this.next = last;
                        }

                        if (this.prev == pos) {
                           this.prev = last;
                        }

                        Float2ShortLinkedOpenHashMap.this.fixPointers(pos, last);
                        continue label61;
                     }
                  }

                  key[last] = 0.0F;
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

      public void set(Float2ShortMap.Entry ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Float2ShortMap.Entry ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class ValueIterator extends Float2ShortLinkedOpenHashMap.MapIterator<ShortConsumer> implements ShortListIterator {
      @Override
      public short previousShort() {
         return Float2ShortLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
      }

      final void acceptOnIndex(ShortConsumer action, int index) {
         action.accept(Float2ShortLinkedOpenHashMap.this.value[index]);
      }

      @Override
      public short nextShort() {
         return Float2ShortLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }
}
