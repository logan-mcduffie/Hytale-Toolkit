package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatListIterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterators;
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
import java.util.function.DoubleFunction;

public class Double2FloatLinkedOpenHashMap extends AbstractDouble2FloatSortedMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient double[] key;
   protected transient float[] value;
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
   protected transient Double2FloatSortedMap.FastSortedEntrySet entries;
   protected transient DoubleSortedSet keys;
   protected transient FloatCollection values;

   public Double2FloatLinkedOpenHashMap(int expected, float f) {
      if (f <= 0.0F || f >= 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      } else if (expected < 0) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.arraySize(expected, f);
         this.mask = this.n - 1;
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = new double[this.n + 1];
         this.value = new float[this.n + 1];
         this.link = new long[this.n + 1];
      }
   }

   public Double2FloatLinkedOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Double2FloatLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Double2FloatLinkedOpenHashMap(Map<? extends Double, ? extends Float> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Double2FloatLinkedOpenHashMap(Map<? extends Double, ? extends Float> m) {
      this(m, 0.75F);
   }

   public Double2FloatLinkedOpenHashMap(Double2FloatMap m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Double2FloatLinkedOpenHashMap(Double2FloatMap m) {
      this(m, 0.75F);
   }

   public Double2FloatLinkedOpenHashMap(double[] k, float[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Double2FloatLinkedOpenHashMap(double[] k, float[] v) {
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

   private float removeEntry(int pos) {
      float oldValue = this.value[pos];
      this.size--;
      this.fixPointers(pos);
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private float removeNullEntry() {
      this.containsNullKey = false;
      float oldValue = this.value[this.n];
      this.size--;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Float> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(double k) {
      if (Double.doubleToLongBits(k) == 0L) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         double[] key = this.key;
         double curr;
         int pos;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) == 0L) {
            return -(pos + 1);
         } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return pos;
         } else {
            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                  return pos;
               }
            }

            return -(pos + 1);
         }
      }
   }

   private void insert(int pos, double k, float v) {
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
   public float put(double k, float v) {
      int pos = this.find(k);
      if (pos < 0) {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      } else {
         float oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   private float addToValue(int pos, float incr) {
      float oldValue = this.value[pos];
      this.value[pos] = oldValue + incr;
      return oldValue;
   }

   public float addTo(double k, float incr) {
      int pos;
      if (Double.doubleToLongBits(k) == 0L) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, incr);
         }

         pos = this.n;
         this.containsNullKey = true;
      } else {
         double[] key = this.key;
         double curr;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) != 0L) {
            if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
               return this.addToValue(pos, incr);
            }

            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                  return this.addToValue(pos, incr);
               }
            }
         }
      }

      this.key[pos] = k;
      this.value[pos] = this.defRetValue + incr;
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
      double[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         double curr;
         for (pos = pos + 1 & this.mask; Double.doubleToLongBits(curr = key[pos]) != 0L; pos = pos + 1 & this.mask) {
            int slot = (int)HashCommon.mix(Double.doubleToRawLongBits(curr)) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               this.value[last] = this.value[pos];
               this.fixPointers(pos, last);
               continue label30;
            }
         }

         key[last] = 0.0;
         return;
      }
   }

   @Override
   public float remove(double k) {
      if (Double.doubleToLongBits(k) == 0L) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         double[] key = this.key;
         double curr;
         int pos;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return this.removeEntry(pos);
         } else {
            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                  return this.removeEntry(pos);
               }
            }

            return this.defRetValue;
         }
      }
   }

   private float setValue(int pos, float v) {
      float oldValue = this.value[pos];
      this.value[pos] = v;
      return oldValue;
   }

   public float removeFirstFloat() {
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
         float v = this.value[pos];
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

   public float removeLastFloat() {
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
         float v = this.value[pos];
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

   public float getAndMoveToFirst(double k) {
      if (Double.doubleToLongBits(k) == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         double[] key = this.key;
         double curr;
         int pos;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
         } else {
            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                  this.moveIndexToFirst(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public float getAndMoveToLast(double k) {
      if (Double.doubleToLongBits(k) == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         double[] key = this.key;
         double curr;
         int pos;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            this.moveIndexToLast(pos);
            return this.value[pos];
         } else {
            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                  this.moveIndexToLast(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public float putAndMoveToFirst(double k, float v) {
      int pos;
      if (Double.doubleToLongBits(k) == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         double[] key = this.key;
         double curr;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) != 0L) {
            if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
               this.moveIndexToFirst(pos);
               return this.setValue(pos, v);
            }

            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
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

   public float putAndMoveToLast(double k, float v) {
      int pos;
      if (Double.doubleToLongBits(k) == 0L) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         double[] key = this.key;
         double curr;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) != 0L) {
            if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
               this.moveIndexToLast(pos);
               return this.setValue(pos, v);
            }

            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
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
   public float get(double k) {
      if (Double.doubleToLongBits(k) == 0L) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         double[] key = this.key;
         double curr;
         int pos;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return this.value[pos];
         } else {
            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public boolean containsKey(double k) {
      if (Double.doubleToLongBits(k) == 0L) {
         return this.containsNullKey;
      } else {
         double[] key = this.key;
         double curr;
         int pos;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) == 0L) {
            return false;
         } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return true;
         } else {
            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean containsValue(float v) {
      float[] value = this.value;
      double[] key = this.key;
      if (this.containsNullKey && Float.floatToIntBits(value[this.n]) == Float.floatToIntBits(v)) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (Double.doubleToLongBits(key[i]) != 0L && Float.floatToIntBits(value[i]) == Float.floatToIntBits(v)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public float getOrDefault(double k, float defaultValue) {
      if (Double.doubleToLongBits(k) == 0L) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         double[] key = this.key;
         double curr;
         int pos;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) == 0L) {
            return defaultValue;
         } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return this.value[pos];
         } else {
            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                  return this.value[pos];
               }
            }

            return defaultValue;
         }
      }
   }

   @Override
   public float putIfAbsent(double k, float v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(double k, float v) {
      if (Double.doubleToLongBits(k) == 0L) {
         if (this.containsNullKey && Float.floatToIntBits(v) == Float.floatToIntBits(this.value[this.n])) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         double[] key = this.key;
         double curr;
         int pos;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) == 0L) {
            return false;
         } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr) && Float.floatToIntBits(v) == Float.floatToIntBits(this.value[pos])) {
            this.removeEntry(pos);
            return true;
         } else {
            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr) && Float.floatToIntBits(v) == Float.floatToIntBits(this.value[pos])) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(double k, float oldValue, float v) {
      int pos = this.find(k);
      if (pos >= 0 && Float.floatToIntBits(oldValue) == Float.floatToIntBits(this.value[pos])) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public float replace(double k, float v) {
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         float oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   @Override
   public float computeIfAbsent(double k, java.util.function.DoubleUnaryOperator mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         float newValue = SafeMath.safeDoubleToFloat(mappingFunction.applyAsDouble(k));
         this.insert(-pos - 1, k, newValue);
         return newValue;
      }
   }

   @Override
   public float computeIfAbsent(double key, Double2FloatFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         float newValue = mappingFunction.get(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public float computeIfAbsentNullable(double k, DoubleFunction<? extends Float> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         Float newValue = mappingFunction.apply(k);
         if (newValue == null) {
            return this.defRetValue;
         } else {
            float v = newValue;
            this.insert(-pos - 1, k, v);
            return v;
         }
      }
   }

   @Override
   public float computeIfPresent(double k, BiFunction<? super Double, ? super Float, ? extends Float> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Float newValue = remappingFunction.apply(k, this.value[pos]);
         if (newValue == null) {
            if (Double.doubleToLongBits(k) == 0L) {
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
   public float compute(double k, BiFunction<? super Double, ? super Float, ? extends Float> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Float newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (Double.doubleToLongBits(k) == 0L) {
               this.removeNullEntry();
            } else {
               this.removeEntry(pos);
            }
         }

         return this.defRetValue;
      } else {
         float newVal = newValue;
         if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
         } else {
            return this.value[pos] = newVal;
         }
      }
   }

   @Override
   public float merge(double k, float v, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
         Float newValue = remappingFunction.apply(this.value[pos], v);
         if (newValue == null) {
            if (Double.doubleToLongBits(k) == 0L) {
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
         Arrays.fill(this.key, 0.0);
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
   public double firstDoubleKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   @Override
   public double lastDoubleKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   @Override
   public Double2FloatSortedMap tailMap(double from) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Double2FloatSortedMap headMap(double to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Double2FloatSortedMap subMap(double from, double to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public DoubleComparator comparator() {
      return null;
   }

   public Double2FloatSortedMap.FastSortedEntrySet double2FloatEntrySet() {
      if (this.entries == null) {
         this.entries = new Double2FloatLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public DoubleSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Double2FloatLinkedOpenHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public FloatCollection values() {
      if (this.values == null) {
         this.values = new AbstractFloatCollection() {
            private static final int SPLITERATOR_CHARACTERISTICS = 336;

            @Override
            public FloatIterator iterator() {
               return Double2FloatLinkedOpenHashMap.this.new ValueIterator();
            }

            @Override
            public FloatSpliterator spliterator() {
               return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Double2FloatLinkedOpenHashMap.this), 336);
            }

            @Override
            public void forEach(FloatConsumer consumer) {
               int i = Double2FloatLinkedOpenHashMap.this.size;
               int next = Double2FloatLinkedOpenHashMap.this.first;

               while (i-- != 0) {
                  int curr = next;
                  next = (int)Double2FloatLinkedOpenHashMap.this.link[next];
                  consumer.accept(Double2FloatLinkedOpenHashMap.this.value[curr]);
               }
            }

            @Override
            public int size() {
               return Double2FloatLinkedOpenHashMap.this.size;
            }

            @Override
            public boolean contains(float v) {
               return Double2FloatLinkedOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Double2FloatLinkedOpenHashMap.this.clear();
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
      double[] key = this.key;
      float[] value = this.value;
      int mask = newN - 1;
      double[] newKey = new double[newN + 1];
      float[] newValue = new float[newN + 1];
      int i = this.first;
      int prev = -1;
      int newPrev = -1;
      long[] link = this.link;
      long[] newLink = new long[newN + 1];
      this.first = -1;
      int j = this.size;

      while (j-- != 0) {
         int pos;
         if (Double.doubleToLongBits(key[i]) == 0L) {
            pos = newN;
         } else {
            pos = (int)HashCommon.mix(Double.doubleToRawLongBits(key[i])) & mask;

            while (Double.doubleToLongBits(newKey[pos]) != 0L) {
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

   public Double2FloatLinkedOpenHashMap clone() {
      Double2FloatLinkedOpenHashMap c;
      try {
         c = (Double2FloatLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (double[])this.key.clone();
      c.value = (float[])this.value.clone();
      c.link = (long[])this.link.clone();
      return c;
   }

   @Override
   public int hashCode() {
      int h = 0;
      int j = this.realSize();
      int i = 0;

      for (int t = 0; j-- != 0; i++) {
         while (Double.doubleToLongBits(this.key[i]) == 0L) {
            i++;
         }

         t = HashCommon.double2int(this.key[i]);
         t ^= HashCommon.float2int(this.value[i]);
         h += t;
      }

      if (this.containsNullKey) {
         h += HashCommon.float2int(this.value[this.n]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      double[] key = this.key;
      float[] value = this.value;
      Double2FloatLinkedOpenHashMap.EntryIterator i = new Double2FloatLinkedOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeDouble(key[e]);
         s.writeFloat(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      double[] key = this.key = new double[this.n + 1];
      float[] value = this.value = new float[this.n + 1];
      long[] link = this.link = new long[this.n + 1];
      int prev = -1;
      this.first = this.last = -1;
      int i = this.size;

      while (i-- != 0) {
         double k = s.readDouble();
         float v = s.readFloat();
         int pos;
         if (Double.doubleToLongBits(k) == 0L) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask;

            while (Double.doubleToLongBits(key[pos]) != 0L) {
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
      extends Double2FloatLinkedOpenHashMap.MapIterator<Consumer<? super Double2FloatMap.Entry>>
      implements ObjectListIterator<Double2FloatMap.Entry> {
      private Double2FloatLinkedOpenHashMap.MapEntry entry;

      public EntryIterator() {
      }

      public EntryIterator(double from) {
         super(from);
      }

      final void acceptOnIndex(Consumer<? super Double2FloatMap.Entry> action, int index) {
         action.accept(Double2FloatLinkedOpenHashMap.this.new MapEntry(index));
      }

      public Double2FloatLinkedOpenHashMap.MapEntry next() {
         return this.entry = Double2FloatLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Double2FloatLinkedOpenHashMap.MapEntry previous() {
         return this.entry = Double2FloatLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class FastEntryIterator
      extends Double2FloatLinkedOpenHashMap.MapIterator<Consumer<? super Double2FloatMap.Entry>>
      implements ObjectListIterator<Double2FloatMap.Entry> {
      final Double2FloatLinkedOpenHashMap.MapEntry entry;

      public FastEntryIterator() {
         this.entry = Double2FloatLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(double from) {
         super(from);
         this.entry = Double2FloatLinkedOpenHashMap.this.new MapEntry();
      }

      final void acceptOnIndex(Consumer<? super Double2FloatMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }

      public Double2FloatLinkedOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Double2FloatLinkedOpenHashMap.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private final class KeyIterator extends Double2FloatLinkedOpenHashMap.MapIterator<java.util.function.DoubleConsumer> implements DoubleListIterator {
      public KeyIterator(double k) {
         super(k);
      }

      @Override
      public double previousDouble() {
         return Double2FloatLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
      }

      final void acceptOnIndex(java.util.function.DoubleConsumer action, int index) {
         action.accept(Double2FloatLinkedOpenHashMap.this.key[index]);
      }

      @Override
      public double nextDouble() {
         return Double2FloatLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractDoubleSortedSet {
      private static final int SPLITERATOR_CHARACTERISTICS = 337;

      private KeySet() {
      }

      public DoubleListIterator iterator(double from) {
         return Double2FloatLinkedOpenHashMap.this.new KeyIterator(from);
      }

      public DoubleListIterator iterator() {
         return Double2FloatLinkedOpenHashMap.this.new KeyIterator();
      }

      @Override
      public DoubleSpliterator spliterator() {
         return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Double2FloatLinkedOpenHashMap.this), 337);
      }

      @Override
      public void forEach(java.util.function.DoubleConsumer consumer) {
         int i = Double2FloatLinkedOpenHashMap.this.size;
         int next = Double2FloatLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Double2FloatLinkedOpenHashMap.this.link[next];
            consumer.accept(Double2FloatLinkedOpenHashMap.this.key[curr]);
         }
      }

      @Override
      public int size() {
         return Double2FloatLinkedOpenHashMap.this.size;
      }

      @Override
      public boolean contains(double k) {
         return Double2FloatLinkedOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(double k) {
         int oldSize = Double2FloatLinkedOpenHashMap.this.size;
         Double2FloatLinkedOpenHashMap.this.remove(k);
         return Double2FloatLinkedOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Double2FloatLinkedOpenHashMap.this.clear();
      }

      @Override
      public double firstDouble() {
         if (Double2FloatLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Double2FloatLinkedOpenHashMap.this.key[Double2FloatLinkedOpenHashMap.this.first];
         }
      }

      @Override
      public double lastDouble() {
         if (Double2FloatLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Double2FloatLinkedOpenHashMap.this.key[Double2FloatLinkedOpenHashMap.this.last];
         }
      }

      @Override
      public DoubleComparator comparator() {
         return null;
      }

      @Override
      public DoubleSortedSet tailSet(double from) {
         throw new UnsupportedOperationException();
      }

      @Override
      public DoubleSortedSet headSet(double to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public DoubleSortedSet subSet(double from, double to) {
         throw new UnsupportedOperationException();
      }
   }

   final class MapEntry implements Double2FloatMap.Entry, Entry<Double, Float>, DoubleFloatPair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public double getDoubleKey() {
         return Double2FloatLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public double leftDouble() {
         return Double2FloatLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public float getFloatValue() {
         return Double2FloatLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public float rightFloat() {
         return Double2FloatLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public float setValue(float v) {
         float oldValue = Double2FloatLinkedOpenHashMap.this.value[this.index];
         Double2FloatLinkedOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public DoubleFloatPair right(float v) {
         Double2FloatLinkedOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Double getKey() {
         return Double2FloatLinkedOpenHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Float getValue() {
         return Double2FloatLinkedOpenHashMap.this.value[this.index];
      }

      @Deprecated
      @Override
      public Float setValue(Float v) {
         return this.setValue(v.floatValue());
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Double, Float> e = (Entry<Double, Float>)o;
            return Double.doubleToLongBits(Double2FloatLinkedOpenHashMap.this.key[this.index]) == Double.doubleToLongBits(e.getKey())
               && Float.floatToIntBits(Double2FloatLinkedOpenHashMap.this.value[this.index]) == Float.floatToIntBits(e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.double2int(Double2FloatLinkedOpenHashMap.this.key[this.index])
            ^ HashCommon.float2int(Double2FloatLinkedOpenHashMap.this.value[this.index]);
      }

      @Override
      public String toString() {
         return Double2FloatLinkedOpenHashMap.this.key[this.index] + "=>" + Double2FloatLinkedOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Double2FloatMap.Entry> implements Double2FloatSortedMap.FastSortedEntrySet {
      private static final int SPLITERATOR_CHARACTERISTICS = 81;

      private MapEntrySet() {
      }

      @Override
      public ObjectBidirectionalIterator<Double2FloatMap.Entry> iterator() {
         return Double2FloatLinkedOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectSpliterator<Double2FloatMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Double2FloatLinkedOpenHashMap.this), 81);
      }

      @Override
      public Comparator<? super Double2FloatMap.Entry> comparator() {
         return null;
      }

      public ObjectSortedSet<Double2FloatMap.Entry> subSet(Double2FloatMap.Entry fromElement, Double2FloatMap.Entry toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Double2FloatMap.Entry> headSet(Double2FloatMap.Entry toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Double2FloatMap.Entry> tailSet(Double2FloatMap.Entry fromElement) {
         throw new UnsupportedOperationException();
      }

      public Double2FloatMap.Entry first() {
         if (Double2FloatLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Double2FloatLinkedOpenHashMap.this.new MapEntry(Double2FloatLinkedOpenHashMap.this.first);
         }
      }

      public Double2FloatMap.Entry last() {
         if (Double2FloatLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Double2FloatLinkedOpenHashMap.this.new MapEntry(Double2FloatLinkedOpenHashMap.this.last);
         }
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Float) {
               double k = (Double)e.getKey();
               float v = (Float)e.getValue();
               if (Double.doubleToLongBits(k) == 0L) {
                  return Double2FloatLinkedOpenHashMap.this.containsNullKey
                     && Float.floatToIntBits(Double2FloatLinkedOpenHashMap.this.value[Double2FloatLinkedOpenHashMap.this.n]) == Float.floatToIntBits(v);
               } else {
                  double[] key = Double2FloatLinkedOpenHashMap.this.key;
                  double curr;
                  int pos;
                  if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & Double2FloatLinkedOpenHashMap.this.mask])
                     == 0L) {
                     return false;
                  } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                     return Float.floatToIntBits(Double2FloatLinkedOpenHashMap.this.value[pos]) == Float.floatToIntBits(v);
                  } else {
                     while (Double.doubleToLongBits(curr = key[pos = pos + 1 & Double2FloatLinkedOpenHashMap.this.mask]) != 0L) {
                        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                           return Float.floatToIntBits(Double2FloatLinkedOpenHashMap.this.value[pos]) == Float.floatToIntBits(v);
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
            if (e.getKey() != null && e.getKey() instanceof Double) {
               if (e.getValue() != null && e.getValue() instanceof Float) {
                  double k = (Double)e.getKey();
                  float v = (Float)e.getValue();
                  if (Double.doubleToLongBits(k) == 0L) {
                     if (Double2FloatLinkedOpenHashMap.this.containsNullKey
                        && Float.floatToIntBits(Double2FloatLinkedOpenHashMap.this.value[Double2FloatLinkedOpenHashMap.this.n]) == Float.floatToIntBits(v)) {
                        Double2FloatLinkedOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     double[] key = Double2FloatLinkedOpenHashMap.this.key;
                     double curr;
                     int pos;
                     if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & Double2FloatLinkedOpenHashMap.this.mask])
                        == 0L) {
                        return false;
                     } else if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                        if (Float.floatToIntBits(Double2FloatLinkedOpenHashMap.this.value[pos]) == Float.floatToIntBits(v)) {
                           Double2FloatLinkedOpenHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while (Double.doubleToLongBits(curr = key[pos = pos + 1 & Double2FloatLinkedOpenHashMap.this.mask]) != 0L) {
                           if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)
                              && Float.floatToIntBits(Double2FloatLinkedOpenHashMap.this.value[pos]) == Float.floatToIntBits(v)) {
                              Double2FloatLinkedOpenHashMap.this.removeEntry(pos);
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
         return Double2FloatLinkedOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Double2FloatLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Double2FloatMap.Entry> iterator(Double2FloatMap.Entry from) {
         return Double2FloatLinkedOpenHashMap.this.new EntryIterator(from.getDoubleKey());
      }

      public ObjectListIterator<Double2FloatMap.Entry> fastIterator() {
         return Double2FloatLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Double2FloatMap.Entry> fastIterator(Double2FloatMap.Entry from) {
         return Double2FloatLinkedOpenHashMap.this.new FastEntryIterator(from.getDoubleKey());
      }

      @Override
      public void forEach(Consumer<? super Double2FloatMap.Entry> consumer) {
         int i = Double2FloatLinkedOpenHashMap.this.size;
         int next = Double2FloatLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Double2FloatLinkedOpenHashMap.this.link[next];
            consumer.accept(Double2FloatLinkedOpenHashMap.this.new MapEntry(curr));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Double2FloatMap.Entry> consumer) {
         Double2FloatLinkedOpenHashMap.MapEntry entry = Double2FloatLinkedOpenHashMap.this.new MapEntry();
         int i = Double2FloatLinkedOpenHashMap.this.size;
         int next = Double2FloatLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            entry.index = next;
            next = (int)Double2FloatLinkedOpenHashMap.this.link[next];
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
         this.next = Double2FloatLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(double from) {
         if (Double.doubleToLongBits(from) == 0L) {
            if (Double2FloatLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Double2FloatLinkedOpenHashMap.this.link[Double2FloatLinkedOpenHashMap.this.n];
               this.prev = Double2FloatLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
         } else if (Double.doubleToLongBits(Double2FloatLinkedOpenHashMap.this.key[Double2FloatLinkedOpenHashMap.this.last]) == Double.doubleToLongBits(from)) {
            this.prev = Double2FloatLinkedOpenHashMap.this.last;
            this.index = Double2FloatLinkedOpenHashMap.this.size;
         } else {
            for (int pos = (int)HashCommon.mix(Double.doubleToRawLongBits(from)) & Double2FloatLinkedOpenHashMap.this.mask;
               Double.doubleToLongBits(Double2FloatLinkedOpenHashMap.this.key[pos]) != 0L;
               pos = pos + 1 & Double2FloatLinkedOpenHashMap.this.mask
            ) {
               if (Double.doubleToLongBits(Double2FloatLinkedOpenHashMap.this.key[pos]) == Double.doubleToLongBits(from)) {
                  this.next = (int)Double2FloatLinkedOpenHashMap.this.link[pos];
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
               this.index = Double2FloatLinkedOpenHashMap.this.size;
            } else {
               int pos = Double2FloatLinkedOpenHashMap.this.first;

               for (this.index = 1; pos != this.prev; this.index++) {
                  pos = (int)Double2FloatLinkedOpenHashMap.this.link[pos];
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
            this.next = (int)Double2FloatLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Double2FloatLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
            this.next = (int)Double2FloatLinkedOpenHashMap.this.link[this.curr];
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
               this.prev = (int)(Double2FloatLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Double2FloatLinkedOpenHashMap.this.link[this.curr];
            }

            Double2FloatLinkedOpenHashMap.this.size--;
            if (this.prev == -1) {
               Double2FloatLinkedOpenHashMap.this.first = this.next;
            } else {
               Double2FloatLinkedOpenHashMap.this.link[this.prev] = Double2FloatLinkedOpenHashMap.this.link[this.prev]
                  ^ (Double2FloatLinkedOpenHashMap.this.link[this.prev] ^ this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Double2FloatLinkedOpenHashMap.this.last = this.prev;
            } else {
               Double2FloatLinkedOpenHashMap.this.link[this.next] = Double2FloatLinkedOpenHashMap.this.link[this.next]
                  ^ (Double2FloatLinkedOpenHashMap.this.link[this.next] ^ (this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int pos = this.curr;
            this.curr = -1;
            if (pos == Double2FloatLinkedOpenHashMap.this.n) {
               Double2FloatLinkedOpenHashMap.this.containsNullKey = false;
            } else {
               double[] key = Double2FloatLinkedOpenHashMap.this.key;

               label61:
               while (true) {
                  int last = pos;

                  double curr;
                  for (pos = pos + 1 & Double2FloatLinkedOpenHashMap.this.mask;
                     Double.doubleToLongBits(curr = key[pos]) != 0L;
                     pos = pos + 1 & Double2FloatLinkedOpenHashMap.this.mask
                  ) {
                     int slot = (int)HashCommon.mix(Double.doubleToRawLongBits(curr)) & Double2FloatLinkedOpenHashMap.this.mask;
                     if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                        key[last] = curr;
                        Double2FloatLinkedOpenHashMap.this.value[last] = Double2FloatLinkedOpenHashMap.this.value[pos];
                        if (this.next == pos) {
                           this.next = last;
                        }

                        if (this.prev == pos) {
                           this.prev = last;
                        }

                        Double2FloatLinkedOpenHashMap.this.fixPointers(pos, last);
                        continue label61;
                     }
                  }

                  key[last] = 0.0;
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

      public void set(Double2FloatMap.Entry ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Double2FloatMap.Entry ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class ValueIterator extends Double2FloatLinkedOpenHashMap.MapIterator<FloatConsumer> implements FloatListIterator {
      @Override
      public float previousFloat() {
         return Double2FloatLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
      }

      final void acceptOnIndex(FloatConsumer action, int index) {
         action.accept(Double2FloatLinkedOpenHashMap.this.value[index]);
      }

      @Override
      public float nextFloat() {
         return Double2FloatLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }
}
