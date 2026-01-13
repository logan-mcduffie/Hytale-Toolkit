package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
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
import java.util.function.IntFunction;
import java.util.function.IntToLongFunction;
import java.util.function.LongConsumer;

public class Int2LongOpenCustomHashMap extends AbstractInt2LongMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient int[] key;
   protected transient long[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected IntHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Int2LongMap.FastEntrySet entries;
   protected transient IntSet keys;
   protected transient LongCollection values;

   public Int2LongOpenCustomHashMap(int expected, float f, IntHash.Strategy strategy) {
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
         this.key = new int[this.n + 1];
         this.value = new long[this.n + 1];
      }
   }

   public Int2LongOpenCustomHashMap(int expected, IntHash.Strategy strategy) {
      this(expected, 0.75F, strategy);
   }

   public Int2LongOpenCustomHashMap(IntHash.Strategy strategy) {
      this(16, 0.75F, strategy);
   }

   public Int2LongOpenCustomHashMap(Map<? extends Integer, ? extends Long> m, float f, IntHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Int2LongOpenCustomHashMap(Map<? extends Integer, ? extends Long> m, IntHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Int2LongOpenCustomHashMap(Int2LongMap m, float f, IntHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Int2LongOpenCustomHashMap(Int2LongMap m, IntHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Int2LongOpenCustomHashMap(int[] k, long[] v, float f, IntHash.Strategy strategy) {
      this(k.length, f, strategy);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Int2LongOpenCustomHashMap(int[] k, long[] v, IntHash.Strategy strategy) {
      this(k, v, 0.75F, strategy);
   }

   public IntHash.Strategy strategy() {
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

   private long removeEntry(int pos) {
      long oldValue = this.value[pos];
      this.size--;
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private long removeNullEntry() {
      this.containsNullKey = false;
      long oldValue = this.value[this.n];
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends Integer, ? extends Long> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(int k) {
      if (this.strategy.equals(k, 0)) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return -(pos + 1);
         } else if (this.strategy.equals(k, curr)) {
            return pos;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr)) {
                  return pos;
               }
            }

            return -(pos + 1);
         }
      }
   }

   private void insert(int pos, int k, long v) {
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
   public long put(int k, long v) {
      int pos = this.find(k);
      if (pos < 0) {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      } else {
         long oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   private long addToValue(int pos, long incr) {
      long oldValue = this.value[pos];
      this.value[pos] = oldValue + incr;
      return oldValue;
   }

   public long addTo(int k, long incr) {
      int pos;
      if (this.strategy.equals(k, 0)) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, incr);
         }

         pos = this.n;
         this.containsNullKey = true;
      } else {
         int[] key = this.key;
         int curr;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) != 0) {
            if (this.strategy.equals(curr, k)) {
               return this.addToValue(pos, incr);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
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
      int[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         int curr;
         for (pos = pos + 1 & this.mask; (curr = key[pos]) != 0; pos = pos + 1 & this.mask) {
            int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               this.value[last] = this.value[pos];
               continue label30;
            }
         }

         key[last] = 0;
         return;
      }
   }

   @Override
   public long remove(int k) {
      if (this.strategy.equals(k, 0)) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr)) {
                  return this.removeEntry(pos);
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public long get(int k) {
      if (this.strategy.equals(k, 0)) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (this.strategy.equals(k, curr)) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr)) {
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public boolean containsKey(int k) {
      if (this.strategy.equals(k, 0)) {
         return this.containsNullKey;
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(k, curr)) {
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean containsValue(long v) {
      long[] value = this.value;
      int[] key = this.key;
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
   public long getOrDefault(int k, long defaultValue) {
      if (this.strategy.equals(k, 0)) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return defaultValue;
         } else if (this.strategy.equals(k, curr)) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr)) {
                  return this.value[pos];
               }
            }

            return defaultValue;
         }
      }
   }

   @Override
   public long putIfAbsent(int k, long v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(int k, long v) {
      if (this.strategy.equals(k, 0)) {
         if (this.containsNullKey && v == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         int[] key = this.key;
         int curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(k, curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr) && v == this.value[pos]) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(int k, long oldValue, long v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public long replace(int k, long v) {
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         long oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   @Override
   public long computeIfAbsent(int k, IntToLongFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         long newValue = mappingFunction.applyAsLong(k);
         this.insert(-pos - 1, k, newValue);
         return newValue;
      }
   }

   @Override
   public long computeIfAbsent(int key, Int2LongFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         long newValue = mappingFunction.get(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public long computeIfAbsentNullable(int k, IntFunction<? extends Long> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         Long newValue = mappingFunction.apply(k);
         if (newValue == null) {
            return this.defRetValue;
         } else {
            long v = newValue;
            this.insert(-pos - 1, k, v);
            return v;
         }
      }
   }

   @Override
   public long computeIfPresent(int k, BiFunction<? super Integer, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Long newValue = remappingFunction.apply(k, this.value[pos]);
         if (newValue == null) {
            if (this.strategy.equals(k, 0)) {
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
   public long compute(int k, BiFunction<? super Integer, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Long newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (this.strategy.equals(k, 0)) {
               this.removeNullEntry();
            } else {
               this.removeEntry(pos);
            }
         }

         return this.defRetValue;
      } else {
         long newVal = newValue;
         if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
         } else {
            return this.value[pos] = newVal;
         }
      }
   }

   @Override
   public long merge(int k, long v, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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
         Long newValue = remappingFunction.apply(this.value[pos], v);
         if (newValue == null) {
            if (this.strategy.equals(k, 0)) {
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

   public Int2LongMap.FastEntrySet int2LongEntrySet() {
      if (this.entries == null) {
         this.entries = new Int2LongOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public IntSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2LongOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public LongCollection values() {
      if (this.values == null) {
         this.values = new AbstractLongCollection() {
            @Override
            public LongIterator iterator() {
               return Int2LongOpenCustomHashMap.this.new ValueIterator();
            }

            @Override
            public LongSpliterator spliterator() {
               return Int2LongOpenCustomHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(LongConsumer consumer) {
               if (Int2LongOpenCustomHashMap.this.containsNullKey) {
                  consumer.accept(Int2LongOpenCustomHashMap.this.value[Int2LongOpenCustomHashMap.this.n]);
               }

               int pos = Int2LongOpenCustomHashMap.this.n;

               while (pos-- != 0) {
                  if (Int2LongOpenCustomHashMap.this.key[pos] != 0) {
                     consumer.accept(Int2LongOpenCustomHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Int2LongOpenCustomHashMap.this.size;
            }

            @Override
            public boolean contains(long v) {
               return Int2LongOpenCustomHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Int2LongOpenCustomHashMap.this.clear();
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
      long[] value = this.value;
      int mask = newN - 1;
      int[] newKey = new int[newN + 1];
      long[] newValue = new long[newN + 1];
      int i = this.n;
      int j = this.realSize();

      while (j-- != 0) {
         while (key[--i] == 0) {
         }

         int pos;
         if (newKey[pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask] != 0) {
            while (newKey[pos = pos + 1 & mask] != 0) {
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

   public Int2LongOpenCustomHashMap clone() {
      Int2LongOpenCustomHashMap c;
      try {
         c = (Int2LongOpenCustomHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (int[])this.key.clone();
      c.value = (long[])this.value.clone();
      c.strategy = this.strategy;
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

         t = this.strategy.hashCode(this.key[i]);
         t ^= HashCommon.long2int(this.value[i]);
         h += t;
      }

      if (this.containsNullKey) {
         h += HashCommon.long2int(this.value[this.n]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int[] key = this.key;
      long[] value = this.value;
      Int2LongOpenCustomHashMap.EntryIterator i = new Int2LongOpenCustomHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeInt(key[e]);
         s.writeLong(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      int[] key = this.key = new int[this.n + 1];
      long[] value = this.value = new long[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         int k = s.readInt();
         long v = s.readLong();
         int pos;
         if (this.strategy.equals(k, 0)) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;

            while (key[pos] != 0) {
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
      extends Int2LongOpenCustomHashMap.MapIterator<Consumer<? super Int2LongMap.Entry>>
      implements ObjectIterator<Int2LongMap.Entry> {
      private Int2LongOpenCustomHashMap.MapEntry entry;

      private EntryIterator() {
      }

      public Int2LongOpenCustomHashMap.MapEntry next() {
         return this.entry = Int2LongOpenCustomHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Int2LongMap.Entry> action, int index) {
         action.accept(this.entry = Int2LongOpenCustomHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Int2LongOpenCustomHashMap.MapSpliterator<Consumer<? super Int2LongMap.Entry>, Int2LongOpenCustomHashMap.EntrySpliterator>
      implements ObjectSpliterator<Int2LongMap.Entry> {
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

      final void acceptOnIndex(Consumer<? super Int2LongMap.Entry> action, int index) {
         action.accept(Int2LongOpenCustomHashMap.this.new MapEntry(index));
      }

      final Int2LongOpenCustomHashMap.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Int2LongOpenCustomHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Int2LongOpenCustomHashMap.MapIterator<Consumer<? super Int2LongMap.Entry>>
      implements ObjectIterator<Int2LongMap.Entry> {
      private final Int2LongOpenCustomHashMap.MapEntry entry = Int2LongOpenCustomHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Int2LongOpenCustomHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Int2LongMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Int2LongOpenCustomHashMap.MapIterator<java.util.function.IntConsumer> implements IntIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(java.util.function.IntConsumer action, int index) {
         action.accept(Int2LongOpenCustomHashMap.this.key[index]);
      }

      @Override
      public int nextInt() {
         return Int2LongOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractIntSet {
      private KeySet() {
      }

      @Override
      public IntIterator iterator() {
         return Int2LongOpenCustomHashMap.this.new KeyIterator();
      }

      @Override
      public IntSpliterator spliterator() {
         return Int2LongOpenCustomHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(java.util.function.IntConsumer consumer) {
         if (Int2LongOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Int2LongOpenCustomHashMap.this.key[Int2LongOpenCustomHashMap.this.n]);
         }

         int pos = Int2LongOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            int k = Int2LongOpenCustomHashMap.this.key[pos];
            if (k != 0) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Int2LongOpenCustomHashMap.this.size;
      }

      @Override
      public boolean contains(int k) {
         return Int2LongOpenCustomHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(int k) {
         int oldSize = Int2LongOpenCustomHashMap.this.size;
         Int2LongOpenCustomHashMap.this.remove(k);
         return Int2LongOpenCustomHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Int2LongOpenCustomHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Int2LongOpenCustomHashMap.MapSpliterator<java.util.function.IntConsumer, Int2LongOpenCustomHashMap.KeySpliterator>
      implements IntSpliterator {
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

      final void acceptOnIndex(java.util.function.IntConsumer action, int index) {
         action.accept(Int2LongOpenCustomHashMap.this.key[index]);
      }

      final Int2LongOpenCustomHashMap.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Int2LongOpenCustomHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Int2LongMap.Entry, Entry<Integer, Long>, IntLongPair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public int getIntKey() {
         return Int2LongOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public int leftInt() {
         return Int2LongOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public long getLongValue() {
         return Int2LongOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public long rightLong() {
         return Int2LongOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public long setValue(long v) {
         long oldValue = Int2LongOpenCustomHashMap.this.value[this.index];
         Int2LongOpenCustomHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public IntLongPair right(long v) {
         Int2LongOpenCustomHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Integer getKey() {
         return Int2LongOpenCustomHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Long getValue() {
         return Int2LongOpenCustomHashMap.this.value[this.index];
      }

      @Deprecated
      @Override
      public Long setValue(Long v) {
         return this.setValue(v.longValue());
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Integer, Long> e = (Entry<Integer, Long>)o;
            return Int2LongOpenCustomHashMap.this.strategy.equals(Int2LongOpenCustomHashMap.this.key[this.index], e.getKey())
               && Int2LongOpenCustomHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return Int2LongOpenCustomHashMap.this.strategy.hashCode(Int2LongOpenCustomHashMap.this.key[this.index])
            ^ HashCommon.long2int(Int2LongOpenCustomHashMap.this.value[this.index]);
      }

      @Override
      public String toString() {
         return Int2LongOpenCustomHashMap.this.key[this.index] + "=>" + Int2LongOpenCustomHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Int2LongMap.Entry> implements Int2LongMap.FastEntrySet {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Int2LongMap.Entry> iterator() {
         return Int2LongOpenCustomHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Int2LongMap.Entry> fastIterator() {
         return Int2LongOpenCustomHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Int2LongMap.Entry> spliterator() {
         return Int2LongOpenCustomHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Long) {
               int k = (Integer)e.getKey();
               long v = (Long)e.getValue();
               if (Int2LongOpenCustomHashMap.this.strategy.equals(k, 0)) {
                  return Int2LongOpenCustomHashMap.this.containsNullKey && Int2LongOpenCustomHashMap.this.value[Int2LongOpenCustomHashMap.this.n] == v;
               } else {
                  int[] key = Int2LongOpenCustomHashMap.this.key;
                  int curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(Int2LongOpenCustomHashMap.this.strategy.hashCode(k)) & Int2LongOpenCustomHashMap.this.mask]) == 0) {
                     return false;
                  } else if (Int2LongOpenCustomHashMap.this.strategy.equals(k, curr)) {
                     return Int2LongOpenCustomHashMap.this.value[pos] == v;
                  } else {
                     while ((curr = key[pos = pos + 1 & Int2LongOpenCustomHashMap.this.mask]) != 0) {
                        if (Int2LongOpenCustomHashMap.this.strategy.equals(k, curr)) {
                           return Int2LongOpenCustomHashMap.this.value[pos] == v;
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
               if (e.getValue() != null && e.getValue() instanceof Long) {
                  int k = (Integer)e.getKey();
                  long v = (Long)e.getValue();
                  if (Int2LongOpenCustomHashMap.this.strategy.equals(k, 0)) {
                     if (Int2LongOpenCustomHashMap.this.containsNullKey && Int2LongOpenCustomHashMap.this.value[Int2LongOpenCustomHashMap.this.n] == v) {
                        Int2LongOpenCustomHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     int[] key = Int2LongOpenCustomHashMap.this.key;
                     int curr;
                     int pos;
                     if ((curr = key[pos = HashCommon.mix(Int2LongOpenCustomHashMap.this.strategy.hashCode(k)) & Int2LongOpenCustomHashMap.this.mask]) == 0) {
                        return false;
                     } else if (Int2LongOpenCustomHashMap.this.strategy.equals(curr, k)) {
                        if (Int2LongOpenCustomHashMap.this.value[pos] == v) {
                           Int2LongOpenCustomHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while ((curr = key[pos = pos + 1 & Int2LongOpenCustomHashMap.this.mask]) != 0) {
                           if (Int2LongOpenCustomHashMap.this.strategy.equals(curr, k) && Int2LongOpenCustomHashMap.this.value[pos] == v) {
                              Int2LongOpenCustomHashMap.this.removeEntry(pos);
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
         return Int2LongOpenCustomHashMap.this.size;
      }

      @Override
      public void clear() {
         Int2LongOpenCustomHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Int2LongMap.Entry> consumer) {
         if (Int2LongOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Int2LongOpenCustomHashMap.this.new MapEntry(Int2LongOpenCustomHashMap.this.n));
         }

         int pos = Int2LongOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Int2LongOpenCustomHashMap.this.key[pos] != 0) {
               consumer.accept(Int2LongOpenCustomHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Int2LongMap.Entry> consumer) {
         Int2LongOpenCustomHashMap.MapEntry entry = Int2LongOpenCustomHashMap.this.new MapEntry();
         if (Int2LongOpenCustomHashMap.this.containsNullKey) {
            entry.index = Int2LongOpenCustomHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Int2LongOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Int2LongOpenCustomHashMap.this.key[pos] != 0) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Int2LongOpenCustomHashMap.this.n;
      int last = -1;
      int c = Int2LongOpenCustomHashMap.this.size;
      boolean mustReturnNullKey = Int2LongOpenCustomHashMap.this.containsNullKey;
      IntArrayList wrapped;

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
               return this.last = Int2LongOpenCustomHashMap.this.n;
            } else {
               int[] key = Int2LongOpenCustomHashMap.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               int k = this.wrapped.getInt(-this.pos - 1);
               int p = HashCommon.mix(Int2LongOpenCustomHashMap.this.strategy.hashCode(k)) & Int2LongOpenCustomHashMap.this.mask;

               while (!Int2LongOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Int2LongOpenCustomHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Int2LongOpenCustomHashMap.this.n);
            this.c--;
         }

         int[] key = Int2LongOpenCustomHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               int k = this.wrapped.getInt(-this.pos - 1);
               int p = HashCommon.mix(Int2LongOpenCustomHashMap.this.strategy.hashCode(k)) & Int2LongOpenCustomHashMap.this.mask;

               while (!Int2LongOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Int2LongOpenCustomHashMap.this.mask;
               }

               this.acceptOnIndex(action, p);
               this.c--;
            } else if (key[this.pos] != 0) {
               this.acceptOnIndex(action, this.last = this.pos);
               this.c--;
            }
         }
      }

      private void shiftKeys(int pos) {
         int[] key = Int2LongOpenCustomHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            int curr;
            for (pos = pos + 1 & Int2LongOpenCustomHashMap.this.mask; (curr = key[pos]) != 0; pos = pos + 1 & Int2LongOpenCustomHashMap.this.mask) {
               int slot = HashCommon.mix(Int2LongOpenCustomHashMap.this.strategy.hashCode(curr)) & Int2LongOpenCustomHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new IntArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Int2LongOpenCustomHashMap.this.value[last] = Int2LongOpenCustomHashMap.this.value[pos];
                  continue label38;
               }
            }

            key[last] = 0;
            return;
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Int2LongOpenCustomHashMap.this.n) {
               Int2LongOpenCustomHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Int2LongOpenCustomHashMap.this.remove(this.wrapped.getInt(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Int2LongOpenCustomHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Int2LongOpenCustomHashMap.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Int2LongOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Int2LongOpenCustomHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Int2LongOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Int2LongOpenCustomHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Int2LongOpenCustomHashMap.this.n);
            return true;
         } else {
            for (int[] key = Int2LongOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
               if (key[this.pos] != 0) {
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
            this.acceptOnIndex(action, Int2LongOpenCustomHashMap.this.n);
         }

         for (int[] key = Int2LongOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
            if (key[this.pos] != 0) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Int2LongOpenCustomHashMap.this.size - this.c
            : Math.min(
               (long)(Int2LongOpenCustomHashMap.this.size - this.c),
               (long)((double)Int2LongOpenCustomHashMap.this.realSize() / Int2LongOpenCustomHashMap.this.n * (this.max - this.pos))
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

            int[] key = Int2LongOpenCustomHashMap.this.key;

            while (this.pos < this.max && n > 0L) {
               if (key[this.pos++] != 0) {
                  skipped++;
                  n--;
               }
            }

            return skipped;
         }
      }
   }

   private final class ValueIterator extends Int2LongOpenCustomHashMap.MapIterator<LongConsumer> implements LongIterator {
      public ValueIterator() {
      }

      final void acceptOnIndex(LongConsumer action, int index) {
         action.accept(Int2LongOpenCustomHashMap.this.value[index]);
      }

      @Override
      public long nextLong() {
         return Int2LongOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Int2LongOpenCustomHashMap.MapSpliterator<LongConsumer, Int2LongOpenCustomHashMap.ValueSpliterator>
      implements LongSpliterator {
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

      final void acceptOnIndex(LongConsumer action, int index) {
         action.accept(Int2LongOpenCustomHashMap.this.value[index]);
      }

      final Int2LongOpenCustomHashMap.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Int2LongOpenCustomHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
