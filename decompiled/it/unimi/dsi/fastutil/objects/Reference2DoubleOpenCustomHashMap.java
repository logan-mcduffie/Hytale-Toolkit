package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
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
import java.util.function.DoubleConsumer;
import java.util.function.ToDoubleFunction;

public class Reference2DoubleOpenCustomHashMap<K> extends AbstractReference2DoubleMap<K> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
   protected transient double[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected Hash.Strategy<? super K> strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Reference2DoubleMap.FastEntrySet<K> entries;
   protected transient ReferenceSet<K> keys;
   protected transient DoubleCollection values;

   public Reference2DoubleOpenCustomHashMap(int expected, float f, Hash.Strategy<? super K> strategy) {
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
         this.value = new double[this.n + 1];
      }
   }

   public Reference2DoubleOpenCustomHashMap(int expected, Hash.Strategy<? super K> strategy) {
      this(expected, 0.75F, strategy);
   }

   public Reference2DoubleOpenCustomHashMap(Hash.Strategy<? super K> strategy) {
      this(16, 0.75F, strategy);
   }

   public Reference2DoubleOpenCustomHashMap(Map<? extends K, ? extends Double> m, float f, Hash.Strategy<? super K> strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Reference2DoubleOpenCustomHashMap(Map<? extends K, ? extends Double> m, Hash.Strategy<? super K> strategy) {
      this(m, 0.75F, strategy);
   }

   public Reference2DoubleOpenCustomHashMap(Reference2DoubleMap<K> m, float f, Hash.Strategy<? super K> strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Reference2DoubleOpenCustomHashMap(Reference2DoubleMap<K> m, Hash.Strategy<? super K> strategy) {
      this(m, 0.75F, strategy);
   }

   public Reference2DoubleOpenCustomHashMap(K[] k, double[] v, float f, Hash.Strategy<? super K> strategy) {
      this(k.length, f, strategy);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Reference2DoubleOpenCustomHashMap(K[] k, double[] v, Hash.Strategy<? super K> strategy) {
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

   private double removeEntry(int pos) {
      double oldValue = this.value[pos];
      this.size--;
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private double removeNullEntry() {
      this.containsNullKey = false;
      this.key[this.n] = null;
      double oldValue = this.value[this.n];
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends K, ? extends Double> m) {
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

   private void insert(int pos, K k, double v) {
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
   public double put(K k, double v) {
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

   public double addTo(K k, double incr) {
      int pos;
      if (this.strategy.equals(k, null)) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, incr);
         }

         pos = this.n;
         this.containsNullKey = true;
      } else {
         K[] key = this.key;
         K curr;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) != null) {
            if (this.strategy.equals(curr, k)) {
               return this.addToValue(pos, incr);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals(curr, k)) {
                  return this.addToValue(pos, incr);
               }
            }
         }
      }

      this.key[pos] = k;
      this.value[pos] = this.defRetValue + incr;
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return this.defRetValue;
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
               continue label30;
            }
         }

         key[last] = null;
         return;
      }
   }

   @Override
   public double removeDouble(Object k) {
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

   @Override
   public double getDouble(Object k) {
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
   public boolean containsValue(double v) {
      double[] value = this.value;
      K[] key = this.key;
      if (this.containsNullKey && Double.doubleToLongBits(value[this.n]) == Double.doubleToLongBits(v)) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (key[i] != null && Double.doubleToLongBits(value[i]) == Double.doubleToLongBits(v)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public double getOrDefault(Object k, double defaultValue) {
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
   public double putIfAbsent(K k, double v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(Object k, double v) {
      if (this.strategy.equals((K)k, null)) {
         if (this.containsNullKey && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[this.n])) {
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
         } else if (this.strategy.equals((K)k, curr) && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[pos])) {
            this.removeEntry(pos);
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals((K)k, curr) && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[pos])) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(K k, double oldValue, double v) {
      int pos = this.find(k);
      if (pos >= 0 && Double.doubleToLongBits(oldValue) == Double.doubleToLongBits(this.value[pos])) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public double replace(K k, double v) {
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
   public double computeIfAbsent(K k, ToDoubleFunction<? super K> mappingFunction) {
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
   public double computeIfAbsent(K key, Reference2DoubleFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         double newValue = mappingFunction.getDouble(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public double computeDoubleIfPresent(K k, BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Double newValue = remappingFunction.apply(k, this.value[pos]);
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
   public double computeDouble(K k, BiFunction<? super K, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Double newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (this.strategy.equals(k, null)) {
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
   public double merge(K k, double v, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNullKey = false;
         Arrays.fill(this.key, null);
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

   public Reference2DoubleMap.FastEntrySet<K> reference2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Reference2DoubleOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public ReferenceSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Reference2DoubleOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new AbstractDoubleCollection() {
            @Override
            public DoubleIterator iterator() {
               return Reference2DoubleOpenCustomHashMap.this.new ValueIterator();
            }

            @Override
            public DoubleSpliterator spliterator() {
               return Reference2DoubleOpenCustomHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(DoubleConsumer consumer) {
               if (Reference2DoubleOpenCustomHashMap.this.containsNullKey) {
                  consumer.accept(Reference2DoubleOpenCustomHashMap.this.value[Reference2DoubleOpenCustomHashMap.this.n]);
               }

               int pos = Reference2DoubleOpenCustomHashMap.this.n;

               while (pos-- != 0) {
                  if (Reference2DoubleOpenCustomHashMap.this.key[pos] != null) {
                     consumer.accept(Reference2DoubleOpenCustomHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Reference2DoubleOpenCustomHashMap.this.size;
            }

            @Override
            public boolean contains(double v) {
               return Reference2DoubleOpenCustomHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Reference2DoubleOpenCustomHashMap.this.clear();
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
      double[] value = this.value;
      int mask = newN - 1;
      K[] newKey = (K[])(new Object[newN + 1]);
      double[] newValue = new double[newN + 1];
      int i = this.n;
      int j = this.realSize();

      while (j-- != 0) {
         while (key[--i] == null) {
         }

         int pos;
         if (newKey[pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask] != null) {
            while (newKey[pos = pos + 1 & mask] != null) {
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

   public Reference2DoubleOpenCustomHashMap<K> clone() {
      Reference2DoubleOpenCustomHashMap<K> c;
      try {
         c = (Reference2DoubleOpenCustomHashMap<K>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (K[])((Object[])this.key.clone());
      c.value = (double[])this.value.clone();
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

         t ^= HashCommon.double2int(this.value[i]);
         h += t;
      }

      if (this.containsNullKey) {
         h += HashCommon.double2int(this.value[this.n]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      K[] key = this.key;
      double[] value = this.value;
      Reference2DoubleOpenCustomHashMap<K>.EntryIterator i = new Reference2DoubleOpenCustomHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeObject(key[e]);
         s.writeDouble(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      K[] key = this.key = (K[])(new Object[this.n + 1]);
      double[] value = this.value = new double[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         K k = (K)s.readObject();
         double v = s.readDouble();
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
      }
   }

   private void checkTable() {
   }

   private final class EntryIterator
      extends Reference2DoubleOpenCustomHashMap<K>.MapIterator<Consumer<? super Reference2DoubleMap.Entry<K>>>
      implements ObjectIterator<Reference2DoubleMap.Entry<K>> {
      private Reference2DoubleOpenCustomHashMap<K>.MapEntry entry;

      private EntryIterator() {
      }

      public Reference2DoubleOpenCustomHashMap<K>.MapEntry next() {
         return this.entry = Reference2DoubleOpenCustomHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Reference2DoubleMap.Entry<K>> action, int index) {
         action.accept(this.entry = Reference2DoubleOpenCustomHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Reference2DoubleOpenCustomHashMap<K>.MapSpliterator<Consumer<? super Reference2DoubleMap.Entry<K>>, Reference2DoubleOpenCustomHashMap<K>.EntrySpliterator>
      implements ObjectSpliterator<Reference2DoubleMap.Entry<K>> {
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

      final void acceptOnIndex(Consumer<? super Reference2DoubleMap.Entry<K>> action, int index) {
         action.accept(Reference2DoubleOpenCustomHashMap.this.new MapEntry(index));
      }

      final Reference2DoubleOpenCustomHashMap<K>.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Reference2DoubleOpenCustomHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Reference2DoubleOpenCustomHashMap<K>.MapIterator<Consumer<? super Reference2DoubleMap.Entry<K>>>
      implements ObjectIterator<Reference2DoubleMap.Entry<K>> {
      private final Reference2DoubleOpenCustomHashMap<K>.MapEntry entry = Reference2DoubleOpenCustomHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Reference2DoubleOpenCustomHashMap<K>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Reference2DoubleMap.Entry<K>> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Reference2DoubleOpenCustomHashMap<K>.MapIterator<Consumer<? super K>> implements ObjectIterator<K> {
      public KeyIterator() {
      }

      final void acceptOnIndex(Consumer<? super K> action, int index) {
         action.accept(Reference2DoubleOpenCustomHashMap.this.key[index]);
      }

      @Override
      public K next() {
         return Reference2DoubleOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractReferenceSet<K> {
      private KeySet() {
      }

      @Override
      public ObjectIterator<K> iterator() {
         return Reference2DoubleOpenCustomHashMap.this.new KeyIterator();
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return Reference2DoubleOpenCustomHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(Consumer<? super K> consumer) {
         if (Reference2DoubleOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Reference2DoubleOpenCustomHashMap.this.key[Reference2DoubleOpenCustomHashMap.this.n]);
         }

         int pos = Reference2DoubleOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            K k = Reference2DoubleOpenCustomHashMap.this.key[pos];
            if (k != null) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Reference2DoubleOpenCustomHashMap.this.size;
      }

      @Override
      public boolean contains(Object k) {
         return Reference2DoubleOpenCustomHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(Object k) {
         int oldSize = Reference2DoubleOpenCustomHashMap.this.size;
         Reference2DoubleOpenCustomHashMap.this.removeDouble(k);
         return Reference2DoubleOpenCustomHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Reference2DoubleOpenCustomHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Reference2DoubleOpenCustomHashMap<K>.MapSpliterator<Consumer<? super K>, Reference2DoubleOpenCustomHashMap<K>.KeySpliterator>
      implements ObjectSpliterator<K> {
      private static final int POST_SPLIT_CHARACTERISTICS = 1;

      KeySpliterator() {
      }

      KeySpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         super(pos, max, mustReturnNull, hasSplit);
      }

      @Override
      public int characteristics() {
         return this.hasSplit ? 1 : 65;
      }

      final void acceptOnIndex(Consumer<? super K> action, int index) {
         action.accept(Reference2DoubleOpenCustomHashMap.this.key[index]);
      }

      final Reference2DoubleOpenCustomHashMap<K>.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Reference2DoubleOpenCustomHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Reference2DoubleMap.Entry<K>, Entry<K, Double>, ReferenceDoublePair<K> {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public K getKey() {
         return Reference2DoubleOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public K left() {
         return Reference2DoubleOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public double getDoubleValue() {
         return Reference2DoubleOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public double rightDouble() {
         return Reference2DoubleOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public double setValue(double v) {
         double oldValue = Reference2DoubleOpenCustomHashMap.this.value[this.index];
         Reference2DoubleOpenCustomHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public ReferenceDoublePair<K> right(double v) {
         Reference2DoubleOpenCustomHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Double getValue() {
         return Reference2DoubleOpenCustomHashMap.this.value[this.index];
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
            Entry<K, Double> e = (Entry<K, Double>)o;
            return Reference2DoubleOpenCustomHashMap.this.strategy.equals(Reference2DoubleOpenCustomHashMap.this.key[this.index], e.getKey())
               && Double.doubleToLongBits(Reference2DoubleOpenCustomHashMap.this.value[this.index]) == Double.doubleToLongBits(e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return Reference2DoubleOpenCustomHashMap.this.strategy.hashCode(Reference2DoubleOpenCustomHashMap.this.key[this.index])
            ^ HashCommon.double2int(Reference2DoubleOpenCustomHashMap.this.value[this.index]);
      }

      @Override
      public String toString() {
         return Reference2DoubleOpenCustomHashMap.this.key[this.index] + "=>" + Reference2DoubleOpenCustomHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Reference2DoubleMap.Entry<K>> implements Reference2DoubleMap.FastEntrySet<K> {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Reference2DoubleMap.Entry<K>> iterator() {
         return Reference2DoubleOpenCustomHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Reference2DoubleMap.Entry<K>> fastIterator() {
         return Reference2DoubleOpenCustomHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Reference2DoubleMap.Entry<K>> spliterator() {
         return Reference2DoubleOpenCustomHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getValue() != null && e.getValue() instanceof Double) {
               K k = (K)e.getKey();
               double v = (Double)e.getValue();
               if (Reference2DoubleOpenCustomHashMap.this.strategy.equals(k, null)) {
                  return Reference2DoubleOpenCustomHashMap.this.containsNullKey
                     && Double.doubleToLongBits(Reference2DoubleOpenCustomHashMap.this.value[Reference2DoubleOpenCustomHashMap.this.n])
                        == Double.doubleToLongBits(v);
               } else {
                  K[] key = Reference2DoubleOpenCustomHashMap.this.key;
                  K curr;
                  int pos;
                  if ((
                        curr = key[pos = HashCommon.mix(Reference2DoubleOpenCustomHashMap.this.strategy.hashCode(k))
                           & Reference2DoubleOpenCustomHashMap.this.mask]
                     )
                     == null) {
                     return false;
                  } else if (Reference2DoubleOpenCustomHashMap.this.strategy.equals(k, curr)) {
                     return Double.doubleToLongBits(Reference2DoubleOpenCustomHashMap.this.value[pos]) == Double.doubleToLongBits(v);
                  } else {
                     while ((curr = key[pos = pos + 1 & Reference2DoubleOpenCustomHashMap.this.mask]) != null) {
                        if (Reference2DoubleOpenCustomHashMap.this.strategy.equals(k, curr)) {
                           return Double.doubleToLongBits(Reference2DoubleOpenCustomHashMap.this.value[pos]) == Double.doubleToLongBits(v);
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
            if (e.getValue() != null && e.getValue() instanceof Double) {
               K k = (K)e.getKey();
               double v = (Double)e.getValue();
               if (Reference2DoubleOpenCustomHashMap.this.strategy.equals(k, null)) {
                  if (Reference2DoubleOpenCustomHashMap.this.containsNullKey
                     && Double.doubleToLongBits(Reference2DoubleOpenCustomHashMap.this.value[Reference2DoubleOpenCustomHashMap.this.n])
                        == Double.doubleToLongBits(v)) {
                     Reference2DoubleOpenCustomHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  K[] key = Reference2DoubleOpenCustomHashMap.this.key;
                  K curr;
                  int pos;
                  if ((
                        curr = key[pos = HashCommon.mix(Reference2DoubleOpenCustomHashMap.this.strategy.hashCode(k))
                           & Reference2DoubleOpenCustomHashMap.this.mask]
                     )
                     == null) {
                     return false;
                  } else if (Reference2DoubleOpenCustomHashMap.this.strategy.equals(curr, k)) {
                     if (Double.doubleToLongBits(Reference2DoubleOpenCustomHashMap.this.value[pos]) == Double.doubleToLongBits(v)) {
                        Reference2DoubleOpenCustomHashMap.this.removeEntry(pos);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     while ((curr = key[pos = pos + 1 & Reference2DoubleOpenCustomHashMap.this.mask]) != null) {
                        if (Reference2DoubleOpenCustomHashMap.this.strategy.equals(curr, k)
                           && Double.doubleToLongBits(Reference2DoubleOpenCustomHashMap.this.value[pos]) == Double.doubleToLongBits(v)) {
                           Reference2DoubleOpenCustomHashMap.this.removeEntry(pos);
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
         return Reference2DoubleOpenCustomHashMap.this.size;
      }

      @Override
      public void clear() {
         Reference2DoubleOpenCustomHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Reference2DoubleMap.Entry<K>> consumer) {
         if (Reference2DoubleOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Reference2DoubleOpenCustomHashMap.this.new MapEntry(Reference2DoubleOpenCustomHashMap.this.n));
         }

         int pos = Reference2DoubleOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Reference2DoubleOpenCustomHashMap.this.key[pos] != null) {
               consumer.accept(Reference2DoubleOpenCustomHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Reference2DoubleMap.Entry<K>> consumer) {
         Reference2DoubleOpenCustomHashMap<K>.MapEntry entry = Reference2DoubleOpenCustomHashMap.this.new MapEntry();
         if (Reference2DoubleOpenCustomHashMap.this.containsNullKey) {
            entry.index = Reference2DoubleOpenCustomHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Reference2DoubleOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Reference2DoubleOpenCustomHashMap.this.key[pos] != null) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Reference2DoubleOpenCustomHashMap.this.n;
      int last = -1;
      int c = Reference2DoubleOpenCustomHashMap.this.size;
      boolean mustReturnNullKey = Reference2DoubleOpenCustomHashMap.this.containsNullKey;
      ReferenceArrayList<K> wrapped;

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
               return this.last = Reference2DoubleOpenCustomHashMap.this.n;
            } else {
               K[] key = Reference2DoubleOpenCustomHashMap.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != null) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               K k = this.wrapped.get(-this.pos - 1);
               int p = HashCommon.mix(Reference2DoubleOpenCustomHashMap.this.strategy.hashCode(k)) & Reference2DoubleOpenCustomHashMap.this.mask;

               while (!Reference2DoubleOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Reference2DoubleOpenCustomHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Reference2DoubleOpenCustomHashMap.this.n);
            this.c--;
         }

         K[] key = Reference2DoubleOpenCustomHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               K k = this.wrapped.get(-this.pos - 1);
               int p = HashCommon.mix(Reference2DoubleOpenCustomHashMap.this.strategy.hashCode(k)) & Reference2DoubleOpenCustomHashMap.this.mask;

               while (!Reference2DoubleOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Reference2DoubleOpenCustomHashMap.this.mask;
               }

               this.acceptOnIndex(action, p);
               this.c--;
            } else if (key[this.pos] != null) {
               this.acceptOnIndex(action, this.last = this.pos);
               this.c--;
            }
         }
      }

      private void shiftKeys(int pos) {
         K[] key = Reference2DoubleOpenCustomHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            K curr;
            for (pos = pos + 1 & Reference2DoubleOpenCustomHashMap.this.mask;
               (curr = key[pos]) != null;
               pos = pos + 1 & Reference2DoubleOpenCustomHashMap.this.mask
            ) {
               int slot = HashCommon.mix(Reference2DoubleOpenCustomHashMap.this.strategy.hashCode(curr)) & Reference2DoubleOpenCustomHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new ReferenceArrayList<>(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Reference2DoubleOpenCustomHashMap.this.value[last] = Reference2DoubleOpenCustomHashMap.this.value[pos];
                  continue label38;
               }
            }

            key[last] = null;
            return;
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Reference2DoubleOpenCustomHashMap.this.n) {
               Reference2DoubleOpenCustomHashMap.this.containsNullKey = false;
               Reference2DoubleOpenCustomHashMap.this.key[Reference2DoubleOpenCustomHashMap.this.n] = null;
            } else {
               if (this.pos < 0) {
                  Reference2DoubleOpenCustomHashMap.this.removeDouble(this.wrapped.set(-this.pos - 1, null));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Reference2DoubleOpenCustomHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Reference2DoubleOpenCustomHashMap<K>.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Reference2DoubleOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Reference2DoubleOpenCustomHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Reference2DoubleOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Reference2DoubleOpenCustomHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Reference2DoubleOpenCustomHashMap.this.n);
            return true;
         } else {
            for (K[] key = Reference2DoubleOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
               if (key[this.pos] != null) {
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
            this.acceptOnIndex(action, Reference2DoubleOpenCustomHashMap.this.n);
         }

         for (K[] key = Reference2DoubleOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
            if (key[this.pos] != null) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Reference2DoubleOpenCustomHashMap.this.size - this.c
            : Math.min(
               (long)(Reference2DoubleOpenCustomHashMap.this.size - this.c),
               (long)((double)Reference2DoubleOpenCustomHashMap.this.realSize() / Reference2DoubleOpenCustomHashMap.this.n * (this.max - this.pos))
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

            K[] key = Reference2DoubleOpenCustomHashMap.this.key;

            while (this.pos < this.max && n > 0L) {
               if (key[this.pos++] != null) {
                  skipped++;
                  n--;
               }
            }

            return skipped;
         }
      }
   }

   private final class ValueIterator extends Reference2DoubleOpenCustomHashMap<K>.MapIterator<DoubleConsumer> implements DoubleIterator {
      public ValueIterator() {
      }

      final void acceptOnIndex(DoubleConsumer action, int index) {
         action.accept(Reference2DoubleOpenCustomHashMap.this.value[index]);
      }

      @Override
      public double nextDouble() {
         return Reference2DoubleOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Reference2DoubleOpenCustomHashMap<K>.MapSpliterator<DoubleConsumer, Reference2DoubleOpenCustomHashMap<K>.ValueSpliterator>
      implements DoubleSpliterator {
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

      final void acceptOnIndex(DoubleConsumer action, int index) {
         action.accept(Reference2DoubleOpenCustomHashMap.this.value[index]);
      }

      final Reference2DoubleOpenCustomHashMap<K>.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Reference2DoubleOpenCustomHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
