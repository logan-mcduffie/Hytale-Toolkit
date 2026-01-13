package it.unimi.dsi.fastutil.ints;

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
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public class Int2DoubleLinkedOpenHashMap extends AbstractInt2DoubleSortedMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient int[] key;
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
   protected transient Int2DoubleSortedMap.FastSortedEntrySet entries;
   protected transient IntSortedSet keys;
   protected transient DoubleCollection values;

   public Int2DoubleLinkedOpenHashMap(int expected, float f) {
      if (f <= 0.0F || f >= 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      } else if (expected < 0) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.arraySize(expected, f);
         this.mask = this.n - 1;
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = new int[this.n + 1];
         this.value = new double[this.n + 1];
         this.link = new long[this.n + 1];
      }
   }

   public Int2DoubleLinkedOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Int2DoubleLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Int2DoubleLinkedOpenHashMap(Map<? extends Integer, ? extends Double> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Int2DoubleLinkedOpenHashMap(Map<? extends Integer, ? extends Double> m) {
      this(m, 0.75F);
   }

   public Int2DoubleLinkedOpenHashMap(Int2DoubleMap m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Int2DoubleLinkedOpenHashMap(Int2DoubleMap m) {
      this(m, 0.75F);
   }

   public Int2DoubleLinkedOpenHashMap(int[] k, double[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Int2DoubleLinkedOpenHashMap(int[] k, double[] v) {
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
   public void putAll(Map<? extends Integer, ? extends Double> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(int k) {
      if (k == 0) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0) {
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

   private void insert(int pos, int k, double v) {
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
   public double put(int k, double v) {
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

   public double addTo(int k, double incr) {
      int pos;
      if (k == 0) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, incr);
         }

         pos = this.n;
         this.containsNullKey = true;
      } else {
         int[] key = this.key;
         int curr;
         if ((curr = key[pos = HashCommon.mix(k) & this.mask]) != 0) {
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
      int[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         int curr;
         for (pos = pos + 1 & this.mask; (curr = key[pos]) != 0; pos = pos + 1 & this.mask) {
            int slot = HashCommon.mix(curr) & this.mask;
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
   public double remove(int k) {
      if (k == 0) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0) {
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

   public double getAndMoveToFirst(int k) {
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0) {
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

   public double getAndMoveToLast(int k) {
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0) {
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

   public double putAndMoveToFirst(int k, double v) {
      int pos;
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         int[] key = this.key;
         int curr;
         if ((curr = key[pos = HashCommon.mix(k) & this.mask]) != 0) {
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

   public double putAndMoveToLast(int k, double v) {
      int pos;
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         int[] key = this.key;
         int curr;
         if ((curr = key[pos = HashCommon.mix(k) & this.mask]) != 0) {
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
   public double get(int k) {
      if (k == 0) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0) {
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
   public boolean containsKey(int k) {
      if (k == 0) {
         return this.containsNullKey;
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0) {
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
   public boolean containsValue(double v) {
      double[] value = this.value;
      int[] key = this.key;
      if (this.containsNullKey && Double.doubleToLongBits(value[this.n]) == Double.doubleToLongBits(v)) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (key[i] != 0 && Double.doubleToLongBits(value[i]) == Double.doubleToLongBits(v)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public double getOrDefault(int k, double defaultValue) {
      if (k == 0) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0) {
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
   public double putIfAbsent(int k, double v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(int k, double v) {
      if (k == 0) {
         if (this.containsNullKey && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[this.n])) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0) {
            return false;
         } else if (k == curr && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[pos])) {
            this.removeEntry(pos);
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (k == curr && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[pos])) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(int k, double oldValue, double v) {
      int pos = this.find(k);
      if (pos >= 0 && Double.doubleToLongBits(oldValue) == Double.doubleToLongBits(this.value[pos])) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public double replace(int k, double v) {
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
   public double computeIfAbsent(int k, IntToDoubleFunction mappingFunction) {
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
   public double computeIfAbsent(int key, Int2DoubleFunction mappingFunction) {
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
   public double computeIfAbsentNullable(int k, IntFunction<? extends Double> mappingFunction) {
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
   public double computeIfPresent(int k, BiFunction<? super Integer, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Double newValue = remappingFunction.apply(k, this.value[pos]);
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
   public double compute(int k, BiFunction<? super Integer, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Double newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
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
   public double merge(int k, double v, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
         Arrays.fill(this.key, 0);
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
   public int firstIntKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   @Override
   public int lastIntKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   @Override
   public Int2DoubleSortedMap tailMap(int from) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Int2DoubleSortedMap headMap(int to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Int2DoubleSortedMap subMap(int from, int to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public IntComparator comparator() {
      return null;
   }

   public Int2DoubleSortedMap.FastSortedEntrySet int2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Int2DoubleLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public IntSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2DoubleLinkedOpenHashMap.KeySet();
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
               return Int2DoubleLinkedOpenHashMap.this.new ValueIterator();
            }

            @Override
            public DoubleSpliterator spliterator() {
               return DoubleSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Int2DoubleLinkedOpenHashMap.this), 336);
            }

            @Override
            public void forEach(DoubleConsumer consumer) {
               int i = Int2DoubleLinkedOpenHashMap.this.size;
               int next = Int2DoubleLinkedOpenHashMap.this.first;

               while (i-- != 0) {
                  int curr = next;
                  next = (int)Int2DoubleLinkedOpenHashMap.this.link[next];
                  consumer.accept(Int2DoubleLinkedOpenHashMap.this.value[curr]);
               }
            }

            @Override
            public int size() {
               return Int2DoubleLinkedOpenHashMap.this.size;
            }

            @Override
            public boolean contains(double v) {
               return Int2DoubleLinkedOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Int2DoubleLinkedOpenHashMap.this.clear();
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
      int[] key = this.key;
      double[] value = this.value;
      int mask = newN - 1;
      int[] newKey = new int[newN + 1];
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

   public Int2DoubleLinkedOpenHashMap clone() {
      Int2DoubleLinkedOpenHashMap c;
      try {
         c = (Int2DoubleLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (int[])this.key.clone();
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
         while (this.key[i] == 0) {
            i++;
         }

         t = this.key[i];
         t ^= HashCommon.double2int(this.value[i]);
         h += t;
      }

      if (this.containsNullKey) {
         h += HashCommon.double2int(this.value[this.n]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int[] key = this.key;
      double[] value = this.value;
      Int2DoubleLinkedOpenHashMap.EntryIterator i = new Int2DoubleLinkedOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeInt(key[e]);
         s.writeDouble(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      int[] key = this.key = new int[this.n + 1];
      double[] value = this.value = new double[this.n + 1];
      long[] link = this.link = new long[this.n + 1];
      int prev = -1;
      this.first = this.last = -1;
      int i = this.size;

      while (i-- != 0) {
         int k = s.readInt();
         double v = s.readDouble();
         int pos;
         if (k == 0) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            pos = HashCommon.mix(k) & this.mask;

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
      extends Int2DoubleLinkedOpenHashMap.MapIterator<Consumer<? super Int2DoubleMap.Entry>>
      implements ObjectListIterator<Int2DoubleMap.Entry> {
      private Int2DoubleLinkedOpenHashMap.MapEntry entry;

      public EntryIterator() {
      }

      public EntryIterator(int from) {
         super(from);
      }

      final void acceptOnIndex(Consumer<? super Int2DoubleMap.Entry> action, int index) {
         action.accept(Int2DoubleLinkedOpenHashMap.this.new MapEntry(index));
      }

      public Int2DoubleLinkedOpenHashMap.MapEntry next() {
         return this.entry = Int2DoubleLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Int2DoubleLinkedOpenHashMap.MapEntry previous() {
         return this.entry = Int2DoubleLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class FastEntryIterator
      extends Int2DoubleLinkedOpenHashMap.MapIterator<Consumer<? super Int2DoubleMap.Entry>>
      implements ObjectListIterator<Int2DoubleMap.Entry> {
      final Int2DoubleLinkedOpenHashMap.MapEntry entry;

      public FastEntryIterator() {
         this.entry = Int2DoubleLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(int from) {
         super(from);
         this.entry = Int2DoubleLinkedOpenHashMap.this.new MapEntry();
      }

      final void acceptOnIndex(Consumer<? super Int2DoubleMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }

      public Int2DoubleLinkedOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Int2DoubleLinkedOpenHashMap.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private final class KeyIterator extends Int2DoubleLinkedOpenHashMap.MapIterator<java.util.function.IntConsumer> implements IntListIterator {
      public KeyIterator(int k) {
         super(k);
      }

      @Override
      public int previousInt() {
         return Int2DoubleLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
      }

      final void acceptOnIndex(java.util.function.IntConsumer action, int index) {
         action.accept(Int2DoubleLinkedOpenHashMap.this.key[index]);
      }

      @Override
      public int nextInt() {
         return Int2DoubleLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractIntSortedSet {
      private static final int SPLITERATOR_CHARACTERISTICS = 337;

      private KeySet() {
      }

      public IntListIterator iterator(int from) {
         return Int2DoubleLinkedOpenHashMap.this.new KeyIterator(from);
      }

      public IntListIterator iterator() {
         return Int2DoubleLinkedOpenHashMap.this.new KeyIterator();
      }

      @Override
      public IntSpliterator spliterator() {
         return IntSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Int2DoubleLinkedOpenHashMap.this), 337);
      }

      @Override
      public void forEach(java.util.function.IntConsumer consumer) {
         int i = Int2DoubleLinkedOpenHashMap.this.size;
         int next = Int2DoubleLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Int2DoubleLinkedOpenHashMap.this.link[next];
            consumer.accept(Int2DoubleLinkedOpenHashMap.this.key[curr]);
         }
      }

      @Override
      public int size() {
         return Int2DoubleLinkedOpenHashMap.this.size;
      }

      @Override
      public boolean contains(int k) {
         return Int2DoubleLinkedOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(int k) {
         int oldSize = Int2DoubleLinkedOpenHashMap.this.size;
         Int2DoubleLinkedOpenHashMap.this.remove(k);
         return Int2DoubleLinkedOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Int2DoubleLinkedOpenHashMap.this.clear();
      }

      @Override
      public int firstInt() {
         if (Int2DoubleLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Int2DoubleLinkedOpenHashMap.this.key[Int2DoubleLinkedOpenHashMap.this.first];
         }
      }

      @Override
      public int lastInt() {
         if (Int2DoubleLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Int2DoubleLinkedOpenHashMap.this.key[Int2DoubleLinkedOpenHashMap.this.last];
         }
      }

      @Override
      public IntComparator comparator() {
         return null;
      }

      @Override
      public IntSortedSet tailSet(int from) {
         throw new UnsupportedOperationException();
      }

      @Override
      public IntSortedSet headSet(int to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public IntSortedSet subSet(int from, int to) {
         throw new UnsupportedOperationException();
      }
   }

   final class MapEntry implements Int2DoubleMap.Entry, Entry<Integer, Double>, IntDoublePair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public int getIntKey() {
         return Int2DoubleLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public int leftInt() {
         return Int2DoubleLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public double getDoubleValue() {
         return Int2DoubleLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public double rightDouble() {
         return Int2DoubleLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public double setValue(double v) {
         double oldValue = Int2DoubleLinkedOpenHashMap.this.value[this.index];
         Int2DoubleLinkedOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public IntDoublePair right(double v) {
         Int2DoubleLinkedOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Integer getKey() {
         return Int2DoubleLinkedOpenHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Double getValue() {
         return Int2DoubleLinkedOpenHashMap.this.value[this.index];
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
            Entry<Integer, Double> e = (Entry<Integer, Double>)o;
            return Int2DoubleLinkedOpenHashMap.this.key[this.index] == e.getKey()
               && Double.doubleToLongBits(Int2DoubleLinkedOpenHashMap.this.value[this.index]) == Double.doubleToLongBits(e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return Int2DoubleLinkedOpenHashMap.this.key[this.index] ^ HashCommon.double2int(Int2DoubleLinkedOpenHashMap.this.value[this.index]);
      }

      @Override
      public String toString() {
         return Int2DoubleLinkedOpenHashMap.this.key[this.index] + "=>" + Int2DoubleLinkedOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Int2DoubleMap.Entry> implements Int2DoubleSortedMap.FastSortedEntrySet {
      private static final int SPLITERATOR_CHARACTERISTICS = 81;

      private MapEntrySet() {
      }

      @Override
      public ObjectBidirectionalIterator<Int2DoubleMap.Entry> iterator() {
         return Int2DoubleLinkedOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectSpliterator<Int2DoubleMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Int2DoubleLinkedOpenHashMap.this), 81);
      }

      @Override
      public Comparator<? super Int2DoubleMap.Entry> comparator() {
         return null;
      }

      public ObjectSortedSet<Int2DoubleMap.Entry> subSet(Int2DoubleMap.Entry fromElement, Int2DoubleMap.Entry toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Int2DoubleMap.Entry> headSet(Int2DoubleMap.Entry toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Int2DoubleMap.Entry> tailSet(Int2DoubleMap.Entry fromElement) {
         throw new UnsupportedOperationException();
      }

      public Int2DoubleMap.Entry first() {
         if (Int2DoubleLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Int2DoubleLinkedOpenHashMap.this.new MapEntry(Int2DoubleLinkedOpenHashMap.this.first);
         }
      }

      public Int2DoubleMap.Entry last() {
         if (Int2DoubleLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Int2DoubleLinkedOpenHashMap.this.new MapEntry(Int2DoubleLinkedOpenHashMap.this.last);
         }
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               int k = (Integer)e.getKey();
               double v = (Double)e.getValue();
               if (k == 0) {
                  return Int2DoubleLinkedOpenHashMap.this.containsNullKey
                     && Double.doubleToLongBits(Int2DoubleLinkedOpenHashMap.this.value[Int2DoubleLinkedOpenHashMap.this.n]) == Double.doubleToLongBits(v);
               } else {
                  int[] key = Int2DoubleLinkedOpenHashMap.this.key;
                  int curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(k) & Int2DoubleLinkedOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (k == curr) {
                     return Double.doubleToLongBits(Int2DoubleLinkedOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v);
                  } else {
                     while ((curr = key[pos = pos + 1 & Int2DoubleLinkedOpenHashMap.this.mask]) != 0) {
                        if (k == curr) {
                           return Double.doubleToLongBits(Int2DoubleLinkedOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v);
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
            if (e.getKey() != null && e.getKey() instanceof Integer) {
               if (e.getValue() != null && e.getValue() instanceof Double) {
                  int k = (Integer)e.getKey();
                  double v = (Double)e.getValue();
                  if (k == 0) {
                     if (Int2DoubleLinkedOpenHashMap.this.containsNullKey
                        && Double.doubleToLongBits(Int2DoubleLinkedOpenHashMap.this.value[Int2DoubleLinkedOpenHashMap.this.n]) == Double.doubleToLongBits(v)) {
                        Int2DoubleLinkedOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     int[] key = Int2DoubleLinkedOpenHashMap.this.key;
                     int curr;
                     int pos;
                     if ((curr = key[pos = HashCommon.mix(k) & Int2DoubleLinkedOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (curr == k) {
                        if (Double.doubleToLongBits(Int2DoubleLinkedOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v)) {
                           Int2DoubleLinkedOpenHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while ((curr = key[pos = pos + 1 & Int2DoubleLinkedOpenHashMap.this.mask]) != 0) {
                           if (curr == k && Double.doubleToLongBits(Int2DoubleLinkedOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v)) {
                              Int2DoubleLinkedOpenHashMap.this.removeEntry(pos);
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
         return Int2DoubleLinkedOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Int2DoubleLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Int2DoubleMap.Entry> iterator(Int2DoubleMap.Entry from) {
         return Int2DoubleLinkedOpenHashMap.this.new EntryIterator(from.getIntKey());
      }

      public ObjectListIterator<Int2DoubleMap.Entry> fastIterator() {
         return Int2DoubleLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Int2DoubleMap.Entry> fastIterator(Int2DoubleMap.Entry from) {
         return Int2DoubleLinkedOpenHashMap.this.new FastEntryIterator(from.getIntKey());
      }

      @Override
      public void forEach(Consumer<? super Int2DoubleMap.Entry> consumer) {
         int i = Int2DoubleLinkedOpenHashMap.this.size;
         int next = Int2DoubleLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Int2DoubleLinkedOpenHashMap.this.link[next];
            consumer.accept(Int2DoubleLinkedOpenHashMap.this.new MapEntry(curr));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Int2DoubleMap.Entry> consumer) {
         Int2DoubleLinkedOpenHashMap.MapEntry entry = Int2DoubleLinkedOpenHashMap.this.new MapEntry();
         int i = Int2DoubleLinkedOpenHashMap.this.size;
         int next = Int2DoubleLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            entry.index = next;
            next = (int)Int2DoubleLinkedOpenHashMap.this.link[next];
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
         this.next = Int2DoubleLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(int from) {
         if (from == 0) {
            if (Int2DoubleLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Int2DoubleLinkedOpenHashMap.this.link[Int2DoubleLinkedOpenHashMap.this.n];
               this.prev = Int2DoubleLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
         } else if (Int2DoubleLinkedOpenHashMap.this.key[Int2DoubleLinkedOpenHashMap.this.last] == from) {
            this.prev = Int2DoubleLinkedOpenHashMap.this.last;
            this.index = Int2DoubleLinkedOpenHashMap.this.size;
         } else {
            for (int pos = HashCommon.mix(from) & Int2DoubleLinkedOpenHashMap.this.mask;
               Int2DoubleLinkedOpenHashMap.this.key[pos] != 0;
               pos = pos + 1 & Int2DoubleLinkedOpenHashMap.this.mask
            ) {
               if (Int2DoubleLinkedOpenHashMap.this.key[pos] == from) {
                  this.next = (int)Int2DoubleLinkedOpenHashMap.this.link[pos];
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
               this.index = Int2DoubleLinkedOpenHashMap.this.size;
            } else {
               int pos = Int2DoubleLinkedOpenHashMap.this.first;

               for (this.index = 1; pos != this.prev; this.index++) {
                  pos = (int)Int2DoubleLinkedOpenHashMap.this.link[pos];
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
            this.next = (int)Int2DoubleLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Int2DoubleLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
            this.next = (int)Int2DoubleLinkedOpenHashMap.this.link[this.curr];
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
               this.prev = (int)(Int2DoubleLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Int2DoubleLinkedOpenHashMap.this.link[this.curr];
            }

            Int2DoubleLinkedOpenHashMap.this.size--;
            if (this.prev == -1) {
               Int2DoubleLinkedOpenHashMap.this.first = this.next;
            } else {
               Int2DoubleLinkedOpenHashMap.this.link[this.prev] = Int2DoubleLinkedOpenHashMap.this.link[this.prev]
                  ^ (Int2DoubleLinkedOpenHashMap.this.link[this.prev] ^ this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Int2DoubleLinkedOpenHashMap.this.last = this.prev;
            } else {
               Int2DoubleLinkedOpenHashMap.this.link[this.next] = Int2DoubleLinkedOpenHashMap.this.link[this.next]
                  ^ (Int2DoubleLinkedOpenHashMap.this.link[this.next] ^ (this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int pos = this.curr;
            this.curr = -1;
            if (pos == Int2DoubleLinkedOpenHashMap.this.n) {
               Int2DoubleLinkedOpenHashMap.this.containsNullKey = false;
            } else {
               int[] key = Int2DoubleLinkedOpenHashMap.this.key;

               label61:
               while (true) {
                  int last = pos;

                  int curr;
                  for (pos = pos + 1 & Int2DoubleLinkedOpenHashMap.this.mask; (curr = key[pos]) != 0; pos = pos + 1 & Int2DoubleLinkedOpenHashMap.this.mask) {
                     int slot = HashCommon.mix(curr) & Int2DoubleLinkedOpenHashMap.this.mask;
                     if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                        key[last] = curr;
                        Int2DoubleLinkedOpenHashMap.this.value[last] = Int2DoubleLinkedOpenHashMap.this.value[pos];
                        if (this.next == pos) {
                           this.next = last;
                        }

                        if (this.prev == pos) {
                           this.prev = last;
                        }

                        Int2DoubleLinkedOpenHashMap.this.fixPointers(pos, last);
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

      public void set(Int2DoubleMap.Entry ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Int2DoubleMap.Entry ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class ValueIterator extends Int2DoubleLinkedOpenHashMap.MapIterator<DoubleConsumer> implements DoubleListIterator {
      @Override
      public double previousDouble() {
         return Int2DoubleLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
      }

      final void acceptOnIndex(DoubleConsumer action, int index) {
         action.accept(Int2DoubleLinkedOpenHashMap.this.value[index]);
      }

      @Override
      public double nextDouble() {
         return Int2DoubleLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }
}
