package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.DoubleFunction;

public class Double2BooleanOpenHashMap extends AbstractDouble2BooleanMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient double[] key;
   protected transient boolean[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Double2BooleanMap.FastEntrySet entries;
   protected transient DoubleSet keys;
   protected transient BooleanCollection values;

   public Double2BooleanOpenHashMap(int expected, float f) {
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
         this.value = new boolean[this.n + 1];
      }
   }

   public Double2BooleanOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Double2BooleanOpenHashMap() {
      this(16, 0.75F);
   }

   public Double2BooleanOpenHashMap(Map<? extends Double, ? extends Boolean> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Double2BooleanOpenHashMap(Map<? extends Double, ? extends Boolean> m) {
      this(m, 0.75F);
   }

   public Double2BooleanOpenHashMap(Double2BooleanMap m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Double2BooleanOpenHashMap(Double2BooleanMap m) {
      this(m, 0.75F);
   }

   public Double2BooleanOpenHashMap(double[] k, boolean[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Double2BooleanOpenHashMap(double[] k, boolean[] v) {
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

   private boolean removeEntry(int pos) {
      boolean oldValue = this.value[pos];
      this.size--;
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private boolean removeNullEntry() {
      this.containsNullKey = false;
      boolean oldValue = this.value[this.n];
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends Double, ? extends Boolean> m) {
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

   private void insert(int pos, double k, boolean v) {
      if (pos == this.n) {
         this.containsNullKey = true;
      }

      this.key[pos] = k;
      this.value[pos] = v;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }
   }

   @Override
   public boolean put(double k, boolean v) {
      int pos = this.find(k);
      if (pos < 0) {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      } else {
         boolean oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
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
               continue label30;
            }
         }

         key[last] = 0.0;
         return;
      }
   }

   @Override
   public boolean remove(double k) {
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

   @Override
   public boolean get(double k) {
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
   public boolean containsValue(boolean v) {
      boolean[] value = this.value;
      double[] key = this.key;
      if (this.containsNullKey && value[this.n] == v) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (Double.doubleToLongBits(key[i]) != 0L && value[i] == v) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public boolean getOrDefault(double k, boolean defaultValue) {
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
   public boolean putIfAbsent(double k, boolean v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(double k, boolean v) {
      if (Double.doubleToLongBits(k) == 0L) {
         if (this.containsNullKey && v == this.value[this.n]) {
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
         } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
         } else {
            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr) && v == this.value[pos]) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(double k, boolean oldValue, boolean v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean replace(double k, boolean v) {
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         boolean oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   @Override
   public boolean computeIfAbsent(double k, java.util.function.DoublePredicate mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         boolean newValue = mappingFunction.test(k);
         this.insert(-pos - 1, k, newValue);
         return newValue;
      }
   }

   @Override
   public boolean computeIfAbsent(double key, Double2BooleanFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         boolean newValue = mappingFunction.get(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public boolean computeIfAbsentNullable(double k, DoubleFunction<? extends Boolean> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         Boolean newValue = mappingFunction.apply(k);
         if (newValue == null) {
            return this.defRetValue;
         } else {
            boolean v = newValue;
            this.insert(-pos - 1, k, v);
            return v;
         }
      }
   }

   @Override
   public boolean computeIfPresent(double k, BiFunction<? super Double, ? super Boolean, ? extends Boolean> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Boolean newValue = remappingFunction.apply(k, this.value[pos]);
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
   public boolean compute(double k, BiFunction<? super Double, ? super Boolean, ? extends Boolean> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Boolean newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
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
         boolean newVal = newValue;
         if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
         } else {
            return this.value[pos] = newVal;
         }
      }
   }

   @Override
   public boolean merge(double k, boolean v, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
         Boolean newValue = remappingFunction.apply(this.value[pos], v);
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

   public Double2BooleanMap.FastEntrySet double2BooleanEntrySet() {
      if (this.entries == null) {
         this.entries = new Double2BooleanOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public DoubleSet keySet() {
      if (this.keys == null) {
         this.keys = new Double2BooleanOpenHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public BooleanCollection values() {
      if (this.values == null) {
         this.values = new AbstractBooleanCollection() {
            @Override
            public BooleanIterator iterator() {
               return Double2BooleanOpenHashMap.this.new ValueIterator();
            }

            @Override
            public BooleanSpliterator spliterator() {
               return Double2BooleanOpenHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(BooleanConsumer consumer) {
               if (Double2BooleanOpenHashMap.this.containsNullKey) {
                  consumer.accept(Double2BooleanOpenHashMap.this.value[Double2BooleanOpenHashMap.this.n]);
               }

               int pos = Double2BooleanOpenHashMap.this.n;

               while (pos-- != 0) {
                  if (Double.doubleToLongBits(Double2BooleanOpenHashMap.this.key[pos]) != 0L) {
                     consumer.accept(Double2BooleanOpenHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Double2BooleanOpenHashMap.this.size;
            }

            @Override
            public boolean contains(boolean v) {
               return Double2BooleanOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Double2BooleanOpenHashMap.this.clear();
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
      boolean[] value = this.value;
      int mask = newN - 1;
      double[] newKey = new double[newN + 1];
      boolean[] newValue = new boolean[newN + 1];
      int i = this.n;
      int j = this.realSize();

      while (j-- != 0) {
         while (Double.doubleToLongBits(key[--i]) == 0L) {
         }

         int pos;
         if (Double.doubleToLongBits(newKey[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(key[i])) & mask]) != 0L) {
            while (Double.doubleToLongBits(newKey[pos = pos + 1 & mask]) != 0L) {
            }
         }

         newKey[pos] = key[i];
         newValue[pos] = value[i];
      }

      newValue[newN] = value[this.n];
      this.n = newN;
      this.mask = mask;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = newKey;
      this.value = newValue;
   }

   public Double2BooleanOpenHashMap clone() {
      Double2BooleanOpenHashMap c;
      try {
         c = (Double2BooleanOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (double[])this.key.clone();
      c.value = (boolean[])this.value.clone();
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
         t ^= this.value[i] ? 1231 : 1237;
         h += t;
      }

      if (this.containsNullKey) {
         h += this.value[this.n] ? 1231 : 1237;
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      double[] key = this.key;
      boolean[] value = this.value;
      Double2BooleanOpenHashMap.EntryIterator i = new Double2BooleanOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeDouble(key[e]);
         s.writeBoolean(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      double[] key = this.key = new double[this.n + 1];
      boolean[] value = this.value = new boolean[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         double k = s.readDouble();
         boolean v = s.readBoolean();
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
      }
   }

   private void checkTable() {
   }

   private final class EntryIterator
      extends Double2BooleanOpenHashMap.MapIterator<Consumer<? super Double2BooleanMap.Entry>>
      implements ObjectIterator<Double2BooleanMap.Entry> {
      private Double2BooleanOpenHashMap.MapEntry entry;

      private EntryIterator() {
      }

      public Double2BooleanOpenHashMap.MapEntry next() {
         return this.entry = Double2BooleanOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Double2BooleanMap.Entry> action, int index) {
         action.accept(this.entry = Double2BooleanOpenHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Double2BooleanOpenHashMap.MapSpliterator<Consumer<? super Double2BooleanMap.Entry>, Double2BooleanOpenHashMap.EntrySpliterator>
      implements ObjectSpliterator<Double2BooleanMap.Entry> {
      private static final int POST_SPLIT_CHARACTERISTICS = 1;

      EntrySpliterator() {
      }

      EntrySpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         super(pos, max, mustReturnNull, hasSplit);
      }

      @Override
      public int characteristics() {
         return this.hasSplit ? 1 : 65;
      }

      final void acceptOnIndex(Consumer<? super Double2BooleanMap.Entry> action, int index) {
         action.accept(Double2BooleanOpenHashMap.this.new MapEntry(index));
      }

      final Double2BooleanOpenHashMap.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Double2BooleanOpenHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Double2BooleanOpenHashMap.MapIterator<Consumer<? super Double2BooleanMap.Entry>>
      implements ObjectIterator<Double2BooleanMap.Entry> {
      private final Double2BooleanOpenHashMap.MapEntry entry = Double2BooleanOpenHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Double2BooleanOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Double2BooleanMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Double2BooleanOpenHashMap.MapIterator<java.util.function.DoubleConsumer> implements DoubleIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(java.util.function.DoubleConsumer action, int index) {
         action.accept(Double2BooleanOpenHashMap.this.key[index]);
      }

      @Override
      public double nextDouble() {
         return Double2BooleanOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractDoubleSet {
      private KeySet() {
      }

      @Override
      public DoubleIterator iterator() {
         return Double2BooleanOpenHashMap.this.new KeyIterator();
      }

      @Override
      public DoubleSpliterator spliterator() {
         return Double2BooleanOpenHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(java.util.function.DoubleConsumer consumer) {
         if (Double2BooleanOpenHashMap.this.containsNullKey) {
            consumer.accept(Double2BooleanOpenHashMap.this.key[Double2BooleanOpenHashMap.this.n]);
         }

         int pos = Double2BooleanOpenHashMap.this.n;

         while (pos-- != 0) {
            double k = Double2BooleanOpenHashMap.this.key[pos];
            if (Double.doubleToLongBits(k) != 0L) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Double2BooleanOpenHashMap.this.size;
      }

      @Override
      public boolean contains(double k) {
         return Double2BooleanOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(double k) {
         int oldSize = Double2BooleanOpenHashMap.this.size;
         Double2BooleanOpenHashMap.this.remove(k);
         return Double2BooleanOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Double2BooleanOpenHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Double2BooleanOpenHashMap.MapSpliterator<java.util.function.DoubleConsumer, Double2BooleanOpenHashMap.KeySpliterator>
      implements DoubleSpliterator {
      private static final int POST_SPLIT_CHARACTERISTICS = 257;

      KeySpliterator() {
      }

      KeySpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         super(pos, max, mustReturnNull, hasSplit);
      }

      @Override
      public int characteristics() {
         return this.hasSplit ? 257 : 321;
      }

      final void acceptOnIndex(java.util.function.DoubleConsumer action, int index) {
         action.accept(Double2BooleanOpenHashMap.this.key[index]);
      }

      final Double2BooleanOpenHashMap.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Double2BooleanOpenHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Double2BooleanMap.Entry, Entry<Double, Boolean>, DoubleBooleanPair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public double getDoubleKey() {
         return Double2BooleanOpenHashMap.this.key[this.index];
      }

      @Override
      public double leftDouble() {
         return Double2BooleanOpenHashMap.this.key[this.index];
      }

      @Override
      public boolean getBooleanValue() {
         return Double2BooleanOpenHashMap.this.value[this.index];
      }

      @Override
      public boolean rightBoolean() {
         return Double2BooleanOpenHashMap.this.value[this.index];
      }

      @Override
      public boolean setValue(boolean v) {
         boolean oldValue = Double2BooleanOpenHashMap.this.value[this.index];
         Double2BooleanOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public DoubleBooleanPair right(boolean v) {
         Double2BooleanOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Double getKey() {
         return Double2BooleanOpenHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Boolean getValue() {
         return Double2BooleanOpenHashMap.this.value[this.index];
      }

      @Deprecated
      @Override
      public Boolean setValue(Boolean v) {
         return this.setValue(v.booleanValue());
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Double, Boolean> e = (Entry<Double, Boolean>)o;
            return Double.doubleToLongBits(Double2BooleanOpenHashMap.this.key[this.index]) == Double.doubleToLongBits(e.getKey())
               && Double2BooleanOpenHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.double2int(Double2BooleanOpenHashMap.this.key[this.index]) ^ (Double2BooleanOpenHashMap.this.value[this.index] ? 1231 : 1237);
      }

      @Override
      public String toString() {
         return Double2BooleanOpenHashMap.this.key[this.index] + "=>" + Double2BooleanOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Double2BooleanMap.Entry> implements Double2BooleanMap.FastEntrySet {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Double2BooleanMap.Entry> iterator() {
         return Double2BooleanOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Double2BooleanMap.Entry> fastIterator() {
         return Double2BooleanOpenHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Double2BooleanMap.Entry> spliterator() {
         return Double2BooleanOpenHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Double)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Boolean) {
               double k = (Double)e.getKey();
               boolean v = (Boolean)e.getValue();
               if (Double.doubleToLongBits(k) == 0L) {
                  return Double2BooleanOpenHashMap.this.containsNullKey && Double2BooleanOpenHashMap.this.value[Double2BooleanOpenHashMap.this.n] == v;
               } else {
                  double[] key = Double2BooleanOpenHashMap.this.key;
                  double curr;
                  int pos;
                  if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & Double2BooleanOpenHashMap.this.mask]) == 0L
                     )
                   {
                     return false;
                  } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                     return Double2BooleanOpenHashMap.this.value[pos] == v;
                  } else {
                     while (Double.doubleToLongBits(curr = key[pos = pos + 1 & Double2BooleanOpenHashMap.this.mask]) != 0L) {
                        if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                           return Double2BooleanOpenHashMap.this.value[pos] == v;
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
               if (e.getValue() != null && e.getValue() instanceof Boolean) {
                  double k = (Double)e.getKey();
                  boolean v = (Boolean)e.getValue();
                  if (Double.doubleToLongBits(k) == 0L) {
                     if (Double2BooleanOpenHashMap.this.containsNullKey && Double2BooleanOpenHashMap.this.value[Double2BooleanOpenHashMap.this.n] == v) {
                        Double2BooleanOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     double[] key = Double2BooleanOpenHashMap.this.key;
                     double curr;
                     int pos;
                     if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & Double2BooleanOpenHashMap.this.mask])
                        == 0L) {
                        return false;
                     } else if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                        if (Double2BooleanOpenHashMap.this.value[pos] == v) {
                           Double2BooleanOpenHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while (Double.doubleToLongBits(curr = key[pos = pos + 1 & Double2BooleanOpenHashMap.this.mask]) != 0L) {
                           if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k) && Double2BooleanOpenHashMap.this.value[pos] == v) {
                              Double2BooleanOpenHashMap.this.removeEntry(pos);
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
         return Double2BooleanOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Double2BooleanOpenHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Double2BooleanMap.Entry> consumer) {
         if (Double2BooleanOpenHashMap.this.containsNullKey) {
            consumer.accept(Double2BooleanOpenHashMap.this.new MapEntry(Double2BooleanOpenHashMap.this.n));
         }

         int pos = Double2BooleanOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Double.doubleToLongBits(Double2BooleanOpenHashMap.this.key[pos]) != 0L) {
               consumer.accept(Double2BooleanOpenHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Double2BooleanMap.Entry> consumer) {
         Double2BooleanOpenHashMap.MapEntry entry = Double2BooleanOpenHashMap.this.new MapEntry();
         if (Double2BooleanOpenHashMap.this.containsNullKey) {
            entry.index = Double2BooleanOpenHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Double2BooleanOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Double.doubleToLongBits(Double2BooleanOpenHashMap.this.key[pos]) != 0L) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Double2BooleanOpenHashMap.this.n;
      int last = -1;
      int c = Double2BooleanOpenHashMap.this.size;
      boolean mustReturnNullKey = Double2BooleanOpenHashMap.this.containsNullKey;
      DoubleArrayList wrapped;

      private MapIterator() {
      }

      abstract void acceptOnIndex(ConsumerType var1, int var2);

      public boolean hasNext() {
         return this.c != 0;
      }

      public int nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.c--;
            if (this.mustReturnNullKey) {
               this.mustReturnNullKey = false;
               return this.last = Double2BooleanOpenHashMap.this.n;
            } else {
               double[] key = Double2BooleanOpenHashMap.this.key;

               while (--this.pos >= 0) {
                  if (Double.doubleToLongBits(key[this.pos]) != 0L) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               double k = this.wrapped.getDouble(-this.pos - 1);
               int p = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & Double2BooleanOpenHashMap.this.mask;

               while (Double.doubleToLongBits(k) != Double.doubleToLongBits(key[p])) {
                  p = p + 1 & Double2BooleanOpenHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Double2BooleanOpenHashMap.this.n);
            this.c--;
         }

         double[] key = Double2BooleanOpenHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               double k = this.wrapped.getDouble(-this.pos - 1);
               int p = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & Double2BooleanOpenHashMap.this.mask;

               while (Double.doubleToLongBits(k) != Double.doubleToLongBits(key[p])) {
                  p = p + 1 & Double2BooleanOpenHashMap.this.mask;
               }

               this.acceptOnIndex(action, p);
               this.c--;
            } else if (Double.doubleToLongBits(key[this.pos]) != 0L) {
               this.acceptOnIndex(action, this.last = this.pos);
               this.c--;
            }
         }
      }

      private void shiftKeys(int pos) {
         double[] key = Double2BooleanOpenHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            double curr;
            for (pos = pos + 1 & Double2BooleanOpenHashMap.this.mask;
               Double.doubleToLongBits(curr = key[pos]) != 0L;
               pos = pos + 1 & Double2BooleanOpenHashMap.this.mask
            ) {
               int slot = (int)HashCommon.mix(Double.doubleToRawLongBits(curr)) & Double2BooleanOpenHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new DoubleArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Double2BooleanOpenHashMap.this.value[last] = Double2BooleanOpenHashMap.this.value[pos];
                  continue label38;
               }
            }

            key[last] = 0.0;
            return;
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Double2BooleanOpenHashMap.this.n) {
               Double2BooleanOpenHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Double2BooleanOpenHashMap.this.remove(this.wrapped.getDouble(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Double2BooleanOpenHashMap.this.size--;
            this.last = -1;
         }
      }

      public int skip(int n) {
         int i = n;

         while (i-- != 0 && this.hasNext()) {
            this.nextEntry();
         }

         return n - i - 1;
      }
   }

   private abstract class MapSpliterator<ConsumerType, SplitType extends Double2BooleanOpenHashMap.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Double2BooleanOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Double2BooleanOpenHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Double2BooleanOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Double2BooleanOpenHashMap.this.containsNullKey;
         this.hasSplit = false;
         this.pos = pos;
         this.max = max;
         this.mustReturnNull = mustReturnNull;
         this.hasSplit = hasSplit;
      }

      abstract void acceptOnIndex(ConsumerType var1, int var2);

      abstract SplitType makeForSplit(int var1, int var2, boolean var3);

      public boolean tryAdvance(ConsumerType action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.c++;
            this.acceptOnIndex(action, Double2BooleanOpenHashMap.this.n);
            return true;
         } else {
            for (double[] key = Double2BooleanOpenHashMap.this.key; this.pos < this.max; this.pos++) {
               if (Double.doubleToLongBits(key[this.pos]) != 0L) {
                  this.c++;
                  this.acceptOnIndex(action, this.pos++);
                  return true;
               }
            }

            return false;
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.c++;
            this.acceptOnIndex(action, Double2BooleanOpenHashMap.this.n);
         }

         for (double[] key = Double2BooleanOpenHashMap.this.key; this.pos < this.max; this.pos++) {
            if (Double.doubleToLongBits(key[this.pos]) != 0L) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Double2BooleanOpenHashMap.this.size - this.c
            : Math.min(
               (long)(Double2BooleanOpenHashMap.this.size - this.c),
               (long)((double)Double2BooleanOpenHashMap.this.realSize() / Double2BooleanOpenHashMap.this.n * (this.max - this.pos))
                  + (this.mustReturnNull ? 1 : 0)
            );
      }

      public SplitType trySplit() {
         if (this.pos >= this.max - 1) {
            return null;
         } else {
            int retLen = this.max - this.pos >> 1;
            if (retLen <= 1) {
               return null;
            } else {
               int myNewPos = this.pos + retLen;
               int retPos = this.pos;
               SplitType split = this.makeForSplit(retPos, myNewPos, this.mustReturnNull);
               this.pos = myNewPos;
               this.mustReturnNull = false;
               this.hasSplit = true;
               return split;
            }
         }
      }

      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (n == 0L) {
            return 0L;
         } else {
            long skipped = 0L;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               skipped++;
               n--;
            }

            double[] key = Double2BooleanOpenHashMap.this.key;

            while (this.pos < this.max && n > 0L) {
               if (Double.doubleToLongBits(key[this.pos++]) != 0L) {
                  skipped++;
                  n--;
               }
            }

            return skipped;
         }
      }
   }

   private final class ValueIterator extends Double2BooleanOpenHashMap.MapIterator<BooleanConsumer> implements BooleanIterator {
      public ValueIterator() {
      }

      final void acceptOnIndex(BooleanConsumer action, int index) {
         action.accept(Double2BooleanOpenHashMap.this.value[index]);
      }

      @Override
      public boolean nextBoolean() {
         return Double2BooleanOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Double2BooleanOpenHashMap.MapSpliterator<BooleanConsumer, Double2BooleanOpenHashMap.ValueSpliterator>
      implements BooleanSpliterator {
      private static final int POST_SPLIT_CHARACTERISTICS = 256;

      ValueSpliterator() {
      }

      ValueSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         super(pos, max, mustReturnNull, hasSplit);
      }

      @Override
      public int characteristics() {
         return this.hasSplit ? 256 : 320;
      }

      final void acceptOnIndex(BooleanConsumer action, int index) {
         action.accept(Double2BooleanOpenHashMap.this.value[index]);
      }

      final Double2BooleanOpenHashMap.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Double2BooleanOpenHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
