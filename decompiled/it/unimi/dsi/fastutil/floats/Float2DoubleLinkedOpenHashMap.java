package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleListIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterators;
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
import java.util.function.DoubleConsumer;
import java.util.function.DoubleFunction;
import java.util.function.DoubleUnaryOperator;

public class Float2DoubleLinkedOpenHashMap extends AbstractFloat2DoubleSortedMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient float[] key;
   protected transient double[] value;
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
   protected transient Float2DoubleSortedMap.FastSortedEntrySet entries;
   protected transient FloatSortedSet keys;
   protected transient DoubleCollection values;

   public Float2DoubleLinkedOpenHashMap(int expected, float f) {
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
         this.value = new double[this.n + 1];
         this.link = new long[this.n + 1];
      }
   }

   public Float2DoubleLinkedOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Float2DoubleLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Float2DoubleLinkedOpenHashMap(Map<? extends Float, ? extends Double> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Float2DoubleLinkedOpenHashMap(Map<? extends Float, ? extends Double> m) {
      this(m, 0.75F);
   }

   public Float2DoubleLinkedOpenHashMap(Float2DoubleMap m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Float2DoubleLinkedOpenHashMap(Float2DoubleMap m) {
      this(m, 0.75F);
   }

   public Float2DoubleLinkedOpenHashMap(float[] k, double[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Float2DoubleLinkedOpenHashMap(float[] k, double[] v) {
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

   private double removeEntry(int pos) {
      double oldValue = this.value[pos];
      this.size--;
      this.fixPointers(pos);
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private double removeNullEntry() {
      this.containsNullKey = false;
      double oldValue = this.value[this.n];
      this.size--;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends Float, ? extends Double> m) {
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

   private void insert(int pos, float k, double v) {
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
   public double put(float k, double v) {
      int pos = this.find(k);
      if (pos < 0) {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      } else {
         double oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   private double addToValue(int pos, double incr) {
      double oldValue = this.value[pos];
      this.value[pos] = oldValue + incr;
      return oldValue;
   }

   public double addTo(float k, double incr) {
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
   public double remove(float k) {
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

   private double setValue(int pos, double v) {
      double oldValue = this.value[pos];
      this.value[pos] = v;
      return oldValue;
   }

   public double removeFirstDouble() {
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
         double v = this.value[pos];
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

   public double removeLastDouble() {
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
         double v = this.value[pos];
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

   public double getAndMoveToFirst(float k) {
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

   public double getAndMoveToLast(float k) {
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

   public double putAndMoveToFirst(float k, double v) {
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

   public double putAndMoveToLast(float k, double v) {
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
   public double get(float k) {
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
   public boolean containsValue(double v) {
      double[] value = this.value;
      float[] key = this.key;
      if (this.containsNullKey && Double.doubleToLongBits(value[this.n]) == Double.doubleToLongBits(v)) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (Float.floatToIntBits(key[i]) != 0 && Double.doubleToLongBits(value[i]) == Double.doubleToLongBits(v)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public double getOrDefault(float k, double defaultValue) {
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
   public double putIfAbsent(float k, double v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(float k, double v) {
      if (Float.floatToIntBits(k) == 0) {
         if (this.containsNullKey && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[this.n])) {
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
         } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr) && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[pos])) {
            this.removeEntry(pos);
            return true;
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(k) == Float.floatToIntBits(curr) && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[pos])) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(float k, double oldValue, double v) {
      int pos = this.find(k);
      if (pos >= 0 && Double.doubleToLongBits(oldValue) == Double.doubleToLongBits(this.value[pos])) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public double replace(float k, double v) {
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         double oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   @Override
   public double computeIfAbsent(float k, DoubleUnaryOperator mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         double newValue = mappingFunction.applyAsDouble(k);
         this.insert(-pos - 1, k, newValue);
         return newValue;
      }
   }

   @Override
   public double computeIfAbsent(float key, Float2DoubleFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         double newValue = mappingFunction.get(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public double computeIfAbsentNullable(float k, DoubleFunction<? extends Double> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         Double newValue = mappingFunction.apply(k);
         if (newValue == null) {
            return this.defRetValue;
         } else {
            double v = newValue;
            this.insert(-pos - 1, k, v);
            return v;
         }
      }
   }

   @Override
   public double computeIfPresent(float k, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Double newValue = remappingFunction.apply(k, this.value[pos]);
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
   public double compute(float k, BiFunction<? super Float, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Double newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
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
         double newVal = newValue;
         if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
         } else {
            return this.value[pos] = newVal;
         }
      }
   }

   @Override
   public double merge(float k, double v, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
         Double newValue = remappingFunction.apply(this.value[pos], v);
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
   public Float2DoubleSortedMap tailMap(float from) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Float2DoubleSortedMap headMap(float to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Float2DoubleSortedMap subMap(float from, float to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public FloatComparator comparator() {
      return null;
   }

   public Float2DoubleSortedMap.FastSortedEntrySet float2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Float2DoubleLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public FloatSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Float2DoubleLinkedOpenHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new AbstractDoubleCollection() {
            private static final int SPLITERATOR_CHARACTERISTICS = 336;

            @Override
            public DoubleIterator iterator() {
               return Float2DoubleLinkedOpenHashMap.this.new ValueIterator();
            }

            @Override
            public DoubleSpliterator spliterator() {
               return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Float2DoubleLinkedOpenHashMap.this), 336);
            }

            @Override
            public void forEach(DoubleConsumer consumer) {
               int i = Float2DoubleLinkedOpenHashMap.this.size;
               int next = Float2DoubleLinkedOpenHashMap.this.first;

               while (i-- != 0) {
                  int curr = next;
                  next = (int)Float2DoubleLinkedOpenHashMap.this.link[next];
                  consumer.accept(Float2DoubleLinkedOpenHashMap.this.value[curr]);
               }
            }

            @Override
            public int size() {
               return Float2DoubleLinkedOpenHashMap.this.size;
            }

            @Override
            public boolean contains(double v) {
               return Float2DoubleLinkedOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Float2DoubleLinkedOpenHashMap.this.clear();
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
      double[] value = this.value;
      int mask = newN - 1;
      float[] newKey = new float[newN + 1];
      double[] newValue = new double[newN + 1];
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

   public Float2DoubleLinkedOpenHashMap clone() {
      Float2DoubleLinkedOpenHashMap c;
      try {
         c = (Float2DoubleLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (float[])this.key.clone();
      c.value = (double[])this.value.clone();
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
         t ^= HashCommon.double2int(this.value[i]);
         h += t;
      }

      if (this.containsNullKey) {
         h += HashCommon.double2int(this.value[this.n]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      float[] key = this.key;
      double[] value = this.value;
      Float2DoubleLinkedOpenHashMap.EntryIterator i = new Float2DoubleLinkedOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeFloat(key[e]);
         s.writeDouble(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      float[] key = this.key = new float[this.n + 1];
      double[] value = this.value = new double[this.n + 1];
      long[] link = this.link = new long[this.n + 1];
      int prev = -1;
      this.first = this.last = -1;
      int i = this.size;

      while (i-- != 0) {
         float k = s.readFloat();
         double v = s.readDouble();
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
      extends Float2DoubleLinkedOpenHashMap.MapIterator<Consumer<? super Float2DoubleMap.Entry>>
      implements ObjectListIterator<Float2DoubleMap.Entry> {
      private Float2DoubleLinkedOpenHashMap.MapEntry entry;

      public EntryIterator() {
      }

      public EntryIterator(float from) {
         super(from);
      }

      final void acceptOnIndex(Consumer<? super Float2DoubleMap.Entry> action, int index) {
         action.accept(Float2DoubleLinkedOpenHashMap.this.new MapEntry(index));
      }

      public Float2DoubleLinkedOpenHashMap.MapEntry next() {
         return this.entry = Float2DoubleLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Float2DoubleLinkedOpenHashMap.MapEntry previous() {
         return this.entry = Float2DoubleLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class FastEntryIterator
      extends Float2DoubleLinkedOpenHashMap.MapIterator<Consumer<? super Float2DoubleMap.Entry>>
      implements ObjectListIterator<Float2DoubleMap.Entry> {
      final Float2DoubleLinkedOpenHashMap.MapEntry entry;

      public FastEntryIterator() {
         this.entry = Float2DoubleLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(float from) {
         super(from);
         this.entry = Float2DoubleLinkedOpenHashMap.this.new MapEntry();
      }

      final void acceptOnIndex(Consumer<? super Float2DoubleMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }

      public Float2DoubleLinkedOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Float2DoubleLinkedOpenHashMap.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private final class KeyIterator extends Float2DoubleLinkedOpenHashMap.MapIterator<FloatConsumer> implements FloatListIterator {
      public KeyIterator(float k) {
         super(k);
      }

      @Override
      public float previousFloat() {
         return Float2DoubleLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
      }

      final void acceptOnIndex(FloatConsumer action, int index) {
         action.accept(Float2DoubleLinkedOpenHashMap.this.key[index]);
      }

      @Override
      public float nextFloat() {
         return Float2DoubleLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractFloatSortedSet {
      private static final int SPLITERATOR_CHARACTERISTICS = 337;

      private KeySet() {
      }

      public FloatListIterator iterator(float from) {
         return Float2DoubleLinkedOpenHashMap.this.new KeyIterator(from);
      }

      public FloatListIterator iterator() {
         return Float2DoubleLinkedOpenHashMap.this.new KeyIterator();
      }

      @Override
      public FloatSpliterator spliterator() {
         return FloatSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Float2DoubleLinkedOpenHashMap.this), 337);
      }

      @Override
      public void forEach(FloatConsumer consumer) {
         int i = Float2DoubleLinkedOpenHashMap.this.size;
         int next = Float2DoubleLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Float2DoubleLinkedOpenHashMap.this.link[next];
            consumer.accept(Float2DoubleLinkedOpenHashMap.this.key[curr]);
         }
      }

      @Override
      public int size() {
         return Float2DoubleLinkedOpenHashMap.this.size;
      }

      @Override
      public boolean contains(float k) {
         return Float2DoubleLinkedOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(float k) {
         int oldSize = Float2DoubleLinkedOpenHashMap.this.size;
         Float2DoubleLinkedOpenHashMap.this.remove(k);
         return Float2DoubleLinkedOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Float2DoubleLinkedOpenHashMap.this.clear();
      }

      @Override
      public float firstFloat() {
         if (Float2DoubleLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Float2DoubleLinkedOpenHashMap.this.key[Float2DoubleLinkedOpenHashMap.this.first];
         }
      }

      @Override
      public float lastFloat() {
         if (Float2DoubleLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Float2DoubleLinkedOpenHashMap.this.key[Float2DoubleLinkedOpenHashMap.this.last];
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

   final class MapEntry implements Float2DoubleMap.Entry, Entry<Float, Double>, FloatDoublePair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public float getFloatKey() {
         return Float2DoubleLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public float leftFloat() {
         return Float2DoubleLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public double getDoubleValue() {
         return Float2DoubleLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public double rightDouble() {
         return Float2DoubleLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public double setValue(double v) {
         double oldValue = Float2DoubleLinkedOpenHashMap.this.value[this.index];
         Float2DoubleLinkedOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public FloatDoublePair right(double v) {
         Float2DoubleLinkedOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Float getKey() {
         return Float2DoubleLinkedOpenHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Double getValue() {
         return Float2DoubleLinkedOpenHashMap.this.value[this.index];
      }

      @Deprecated
      @Override
      public Double setValue(Double v) {
         return this.setValue(v.doubleValue());
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Float, Double> e = (Entry<Float, Double>)o;
            return Float.floatToIntBits(Float2DoubleLinkedOpenHashMap.this.key[this.index]) == Float.floatToIntBits(e.getKey())
               && Double.doubleToLongBits(Float2DoubleLinkedOpenHashMap.this.value[this.index]) == Double.doubleToLongBits(e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.float2int(Float2DoubleLinkedOpenHashMap.this.key[this.index])
            ^ HashCommon.double2int(Float2DoubleLinkedOpenHashMap.this.value[this.index]);
      }

      @Override
      public String toString() {
         return Float2DoubleLinkedOpenHashMap.this.key[this.index] + "=>" + Float2DoubleLinkedOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Float2DoubleMap.Entry> implements Float2DoubleSortedMap.FastSortedEntrySet {
      private static final int SPLITERATOR_CHARACTERISTICS = 81;

      private MapEntrySet() {
      }

      @Override
      public ObjectBidirectionalIterator<Float2DoubleMap.Entry> iterator() {
         return Float2DoubleLinkedOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectSpliterator<Float2DoubleMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Float2DoubleLinkedOpenHashMap.this), 81);
      }

      @Override
      public Comparator<? super Float2DoubleMap.Entry> comparator() {
         return null;
      }

      public ObjectSortedSet<Float2DoubleMap.Entry> subSet(Float2DoubleMap.Entry fromElement, Float2DoubleMap.Entry toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Float2DoubleMap.Entry> headSet(Float2DoubleMap.Entry toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Float2DoubleMap.Entry> tailSet(Float2DoubleMap.Entry fromElement) {
         throw new UnsupportedOperationException();
      }

      public Float2DoubleMap.Entry first() {
         if (Float2DoubleLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Float2DoubleLinkedOpenHashMap.this.new MapEntry(Float2DoubleLinkedOpenHashMap.this.first);
         }
      }

      public Float2DoubleMap.Entry last() {
         if (Float2DoubleLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Float2DoubleLinkedOpenHashMap.this.new MapEntry(Float2DoubleLinkedOpenHashMap.this.last);
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
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               float k = (Float)e.getKey();
               double v = (Double)e.getValue();
               if (Float.floatToIntBits(k) == 0) {
                  return Float2DoubleLinkedOpenHashMap.this.containsNullKey
                     && Double.doubleToLongBits(Float2DoubleLinkedOpenHashMap.this.value[Float2DoubleLinkedOpenHashMap.this.n]) == Double.doubleToLongBits(v);
               } else {
                  float[] key = Float2DoubleLinkedOpenHashMap.this.key;
                  float curr;
                  int pos;
                  if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & Float2DoubleLinkedOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                     return Double.doubleToLongBits(Float2DoubleLinkedOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v);
                  } else {
                     while (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2DoubleLinkedOpenHashMap.this.mask]) != 0) {
                        if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                           return Double.doubleToLongBits(Float2DoubleLinkedOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v);
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
               if (e.getValue() != null && e.getValue() instanceof Double) {
                  float k = (Float)e.getKey();
                  double v = (Double)e.getValue();
                  if (Float.floatToIntBits(k) == 0) {
                     if (Float2DoubleLinkedOpenHashMap.this.containsNullKey
                        && Double.doubleToLongBits(Float2DoubleLinkedOpenHashMap.this.value[Float2DoubleLinkedOpenHashMap.this.n])
                           == Double.doubleToLongBits(v)) {
                        Float2DoubleLinkedOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     float[] key = Float2DoubleLinkedOpenHashMap.this.key;
                     float curr;
                     int pos;
                     if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & Float2DoubleLinkedOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
                        if (Double.doubleToLongBits(Float2DoubleLinkedOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v)) {
                           Float2DoubleLinkedOpenHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2DoubleLinkedOpenHashMap.this.mask]) != 0) {
                           if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)
                              && Double.doubleToLongBits(Float2DoubleLinkedOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v)) {
                              Float2DoubleLinkedOpenHashMap.this.removeEntry(pos);
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
         return Float2DoubleLinkedOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Float2DoubleLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Float2DoubleMap.Entry> iterator(Float2DoubleMap.Entry from) {
         return Float2DoubleLinkedOpenHashMap.this.new EntryIterator(from.getFloatKey());
      }

      public ObjectListIterator<Float2DoubleMap.Entry> fastIterator() {
         return Float2DoubleLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Float2DoubleMap.Entry> fastIterator(Float2DoubleMap.Entry from) {
         return Float2DoubleLinkedOpenHashMap.this.new FastEntryIterator(from.getFloatKey());
      }

      @Override
      public void forEach(Consumer<? super Float2DoubleMap.Entry> consumer) {
         int i = Float2DoubleLinkedOpenHashMap.this.size;
         int next = Float2DoubleLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Float2DoubleLinkedOpenHashMap.this.link[next];
            consumer.accept(Float2DoubleLinkedOpenHashMap.this.new MapEntry(curr));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Float2DoubleMap.Entry> consumer) {
         Float2DoubleLinkedOpenHashMap.MapEntry entry = Float2DoubleLinkedOpenHashMap.this.new MapEntry();
         int i = Float2DoubleLinkedOpenHashMap.this.size;
         int next = Float2DoubleLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            entry.index = next;
            next = (int)Float2DoubleLinkedOpenHashMap.this.link[next];
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
         this.next = Float2DoubleLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(float from) {
         if (Float.floatToIntBits(from) == 0) {
            if (Float2DoubleLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Float2DoubleLinkedOpenHashMap.this.link[Float2DoubleLinkedOpenHashMap.this.n];
               this.prev = Float2DoubleLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
         } else if (Float.floatToIntBits(Float2DoubleLinkedOpenHashMap.this.key[Float2DoubleLinkedOpenHashMap.this.last]) == Float.floatToIntBits(from)) {
            this.prev = Float2DoubleLinkedOpenHashMap.this.last;
            this.index = Float2DoubleLinkedOpenHashMap.this.size;
         } else {
            for (int pos = HashCommon.mix(HashCommon.float2int(from)) & Float2DoubleLinkedOpenHashMap.this.mask;
               Float.floatToIntBits(Float2DoubleLinkedOpenHashMap.this.key[pos]) != 0;
               pos = pos + 1 & Float2DoubleLinkedOpenHashMap.this.mask
            ) {
               if (Float.floatToIntBits(Float2DoubleLinkedOpenHashMap.this.key[pos]) == Float.floatToIntBits(from)) {
                  this.next = (int)Float2DoubleLinkedOpenHashMap.this.link[pos];
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
               this.index = Float2DoubleLinkedOpenHashMap.this.size;
            } else {
               int pos = Float2DoubleLinkedOpenHashMap.this.first;

               for (this.index = 1; pos != this.prev; this.index++) {
                  pos = (int)Float2DoubleLinkedOpenHashMap.this.link[pos];
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
            this.next = (int)Float2DoubleLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Float2DoubleLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
            this.next = (int)Float2DoubleLinkedOpenHashMap.this.link[this.curr];
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
               this.prev = (int)(Float2DoubleLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Float2DoubleLinkedOpenHashMap.this.link[this.curr];
            }

            Float2DoubleLinkedOpenHashMap.this.size--;
            if (this.prev == -1) {
               Float2DoubleLinkedOpenHashMap.this.first = this.next;
            } else {
               Float2DoubleLinkedOpenHashMap.this.link[this.prev] = Float2DoubleLinkedOpenHashMap.this.link[this.prev]
                  ^ (Float2DoubleLinkedOpenHashMap.this.link[this.prev] ^ this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Float2DoubleLinkedOpenHashMap.this.last = this.prev;
            } else {
               Float2DoubleLinkedOpenHashMap.this.link[this.next] = Float2DoubleLinkedOpenHashMap.this.link[this.next]
                  ^ (Float2DoubleLinkedOpenHashMap.this.link[this.next] ^ (this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int pos = this.curr;
            this.curr = -1;
            if (pos == Float2DoubleLinkedOpenHashMap.this.n) {
               Float2DoubleLinkedOpenHashMap.this.containsNullKey = false;
            } else {
               float[] key = Float2DoubleLinkedOpenHashMap.this.key;

               label61:
               while (true) {
                  int last = pos;

                  float curr;
                  for (pos = pos + 1 & Float2DoubleLinkedOpenHashMap.this.mask;
                     Float.floatToIntBits(curr = key[pos]) != 0;
                     pos = pos + 1 & Float2DoubleLinkedOpenHashMap.this.mask
                  ) {
                     int slot = HashCommon.mix(HashCommon.float2int(curr)) & Float2DoubleLinkedOpenHashMap.this.mask;
                     if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                        key[last] = curr;
                        Float2DoubleLinkedOpenHashMap.this.value[last] = Float2DoubleLinkedOpenHashMap.this.value[pos];
                        if (this.next == pos) {
                           this.next = last;
                        }

                        if (this.prev == pos) {
                           this.prev = last;
                        }

                        Float2DoubleLinkedOpenHashMap.this.fixPointers(pos, last);
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

      public void set(Float2DoubleMap.Entry ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Float2DoubleMap.Entry ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class ValueIterator extends Float2DoubleLinkedOpenHashMap.MapIterator<DoubleConsumer> implements DoubleListIterator {
      @Override
      public double previousDouble() {
         return Float2DoubleLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
      }

      final void acceptOnIndex(DoubleConsumer action, int index) {
         action.accept(Float2DoubleLinkedOpenHashMap.this.value[index]);
      }

      @Override
      public double nextDouble() {
         return Float2DoubleLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }
}
