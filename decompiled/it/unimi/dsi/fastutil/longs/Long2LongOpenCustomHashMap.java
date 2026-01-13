package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
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
import java.util.function.LongFunction;

public class Long2LongOpenCustomHashMap extends AbstractLong2LongMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient long[] key;
   protected transient long[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected LongHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Long2LongMap.FastEntrySet entries;
   protected transient LongSet keys;
   protected transient LongCollection values;

   public Long2LongOpenCustomHashMap(int expected, float f, LongHash.Strategy strategy) {
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
         this.key = new long[this.n + 1];
         this.value = new long[this.n + 1];
      }
   }

   public Long2LongOpenCustomHashMap(int expected, LongHash.Strategy strategy) {
      this(expected, 0.75F, strategy);
   }

   public Long2LongOpenCustomHashMap(LongHash.Strategy strategy) {
      this(16, 0.75F, strategy);
   }

   public Long2LongOpenCustomHashMap(Map<? extends Long, ? extends Long> m, float f, LongHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Long2LongOpenCustomHashMap(Map<? extends Long, ? extends Long> m, LongHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Long2LongOpenCustomHashMap(Long2LongMap m, float f, LongHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Long2LongOpenCustomHashMap(Long2LongMap m, LongHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Long2LongOpenCustomHashMap(long[] k, long[] v, float f, LongHash.Strategy strategy) {
      this(k.length, f, strategy);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Long2LongOpenCustomHashMap(long[] k, long[] v, LongHash.Strategy strategy) {
      this(k, v, 0.75F, strategy);
   }

   public LongHash.Strategy strategy() {
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
   public void putAll(Map<? extends Long, ? extends Long> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(long k) {
      if (this.strategy.equals(k, 0L)) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         long[] key = this.key;
         long curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0L) {
            return -(pos + 1);
         } else if (this.strategy.equals(k, curr)) {
            return pos;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (this.strategy.equals(k, curr)) {
                  return pos;
               }
            }

            return -(pos + 1);
         }
      }
   }

   private void insert(int pos, long k, long v) {
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
   public long put(long k, long v) {
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

   public long addTo(long k, long incr) {
      int pos;
      if (this.strategy.equals(k, 0L)) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, incr);
         }

         pos = this.n;
         this.containsNullKey = true;
      } else {
         long[] key = this.key;
         long curr;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) != 0L) {
            if (this.strategy.equals(curr, k)) {
               return this.addToValue(pos, incr);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
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
      long[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         long curr;
         for (pos = pos + 1 & this.mask; (curr = key[pos]) != 0L; pos = pos + 1 & this.mask) {
            int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               this.value[last] = this.value[pos];
               continue label30;
            }
         }

         key[last] = 0L;
         return;
      }
   }

   @Override
   public long remove(long k) {
      if (this.strategy.equals(k, 0L)) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         long[] key = this.key;
         long curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (this.strategy.equals(k, curr)) {
                  return this.removeEntry(pos);
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public long get(long k) {
      if (this.strategy.equals(k, 0L)) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         long[] key = this.key;
         long curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0L) {
            return this.defRetValue;
         } else if (this.strategy.equals(k, curr)) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (this.strategy.equals(k, curr)) {
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public boolean containsKey(long k) {
      if (this.strategy.equals(k, 0L)) {
         return this.containsNullKey;
      } else {
         long[] key = this.key;
         long curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0L) {
            return false;
         } else if (this.strategy.equals(k, curr)) {
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
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
      long[] key = this.key;
      if (this.containsNullKey && value[this.n] == v) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (key[i] != 0L && value[i] == v) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public long getOrDefault(long k, long defaultValue) {
      if (this.strategy.equals(k, 0L)) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         long[] key = this.key;
         long curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0L) {
            return defaultValue;
         } else if (this.strategy.equals(k, curr)) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (this.strategy.equals(k, curr)) {
                  return this.value[pos];
               }
            }

            return defaultValue;
         }
      }
   }

   @Override
   public long putIfAbsent(long k, long v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(long k, long v) {
      if (this.strategy.equals(k, 0L)) {
         if (this.containsNullKey && v == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         long[] key = this.key;
         long curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0L) {
            return false;
         } else if (this.strategy.equals(k, curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0L) {
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
   public boolean replace(long k, long oldValue, long v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public long replace(long k, long v) {
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
   public long computeIfAbsent(long k, java.util.function.LongUnaryOperator mappingFunction) {
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
   public long computeIfAbsent(long key, Long2LongFunction mappingFunction) {
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
   public long computeIfAbsentNullable(long k, LongFunction<? extends Long> mappingFunction) {
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
   public long computeIfPresent(long k, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Long newValue = remappingFunction.apply(k, this.value[pos]);
         if (newValue == null) {
            if (this.strategy.equals(k, 0L)) {
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
   public long compute(long k, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Long newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (this.strategy.equals(k, 0L)) {
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
   public long merge(long k, long v, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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
            if (this.strategy.equals(k, 0L)) {
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
         Arrays.fill(this.key, 0L);
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

   public Long2LongMap.FastEntrySet long2LongEntrySet() {
      if (this.entries == null) {
         this.entries = new Long2LongOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public LongSet keySet() {
      if (this.keys == null) {
         this.keys = new Long2LongOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public LongCollection values() {
      if (this.values == null) {
         this.values = new AbstractLongCollection() {
            @Override
            public LongIterator iterator() {
               return Long2LongOpenCustomHashMap.this.new ValueIterator();
            }

            @Override
            public LongSpliterator spliterator() {
               return Long2LongOpenCustomHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(java.util.function.LongConsumer consumer) {
               if (Long2LongOpenCustomHashMap.this.containsNullKey) {
                  consumer.accept(Long2LongOpenCustomHashMap.this.value[Long2LongOpenCustomHashMap.this.n]);
               }

               int pos = Long2LongOpenCustomHashMap.this.n;

               while (pos-- != 0) {
                  if (Long2LongOpenCustomHashMap.this.key[pos] != 0L) {
                     consumer.accept(Long2LongOpenCustomHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Long2LongOpenCustomHashMap.this.size;
            }

            @Override
            public boolean contains(long v) {
               return Long2LongOpenCustomHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Long2LongOpenCustomHashMap.this.clear();
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
      long[] value = this.value;
      int mask = newN - 1;
      long[] newKey = new long[newN + 1];
      long[] newValue = new long[newN + 1];
      int i = this.n;
      int j = this.realSize();

      while (j-- != 0) {
         while (key[--i] == 0L) {
         }

         int pos;
         if (newKey[pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask] != 0L) {
            while (newKey[pos = pos + 1 & mask] != 0L) {
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

   public Long2LongOpenCustomHashMap clone() {
      Long2LongOpenCustomHashMap c;
      try {
         c = (Long2LongOpenCustomHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (long[])this.key.clone();
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
         while (this.key[i] == 0L) {
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
      long[] key = this.key;
      long[] value = this.value;
      Long2LongOpenCustomHashMap.EntryIterator i = new Long2LongOpenCustomHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeLong(key[e]);
         s.writeLong(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      long[] key = this.key = new long[this.n + 1];
      long[] value = this.value = new long[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         long k = s.readLong();
         long v = s.readLong();
         int pos;
         if (this.strategy.equals(k, 0L)) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;

            while (key[pos] != 0L) {
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
      extends Long2LongOpenCustomHashMap.MapIterator<Consumer<? super Long2LongMap.Entry>>
      implements ObjectIterator<Long2LongMap.Entry> {
      private Long2LongOpenCustomHashMap.MapEntry entry;

      private EntryIterator() {
      }

      public Long2LongOpenCustomHashMap.MapEntry next() {
         return this.entry = Long2LongOpenCustomHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Long2LongMap.Entry> action, int index) {
         action.accept(this.entry = Long2LongOpenCustomHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Long2LongOpenCustomHashMap.MapSpliterator<Consumer<? super Long2LongMap.Entry>, Long2LongOpenCustomHashMap.EntrySpliterator>
      implements ObjectSpliterator<Long2LongMap.Entry> {
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

      final void acceptOnIndex(Consumer<? super Long2LongMap.Entry> action, int index) {
         action.accept(Long2LongOpenCustomHashMap.this.new MapEntry(index));
      }

      final Long2LongOpenCustomHashMap.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Long2LongOpenCustomHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Long2LongOpenCustomHashMap.MapIterator<Consumer<? super Long2LongMap.Entry>>
      implements ObjectIterator<Long2LongMap.Entry> {
      private final Long2LongOpenCustomHashMap.MapEntry entry = Long2LongOpenCustomHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Long2LongOpenCustomHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Long2LongMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Long2LongOpenCustomHashMap.MapIterator<java.util.function.LongConsumer> implements LongIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(java.util.function.LongConsumer action, int index) {
         action.accept(Long2LongOpenCustomHashMap.this.key[index]);
      }

      @Override
      public long nextLong() {
         return Long2LongOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractLongSet {
      private KeySet() {
      }

      @Override
      public LongIterator iterator() {
         return Long2LongOpenCustomHashMap.this.new KeyIterator();
      }

      @Override
      public LongSpliterator spliterator() {
         return Long2LongOpenCustomHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(java.util.function.LongConsumer consumer) {
         if (Long2LongOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Long2LongOpenCustomHashMap.this.key[Long2LongOpenCustomHashMap.this.n]);
         }

         int pos = Long2LongOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            long k = Long2LongOpenCustomHashMap.this.key[pos];
            if (k != 0L) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Long2LongOpenCustomHashMap.this.size;
      }

      @Override
      public boolean contains(long k) {
         return Long2LongOpenCustomHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(long k) {
         int oldSize = Long2LongOpenCustomHashMap.this.size;
         Long2LongOpenCustomHashMap.this.remove(k);
         return Long2LongOpenCustomHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Long2LongOpenCustomHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Long2LongOpenCustomHashMap.MapSpliterator<java.util.function.LongConsumer, Long2LongOpenCustomHashMap.KeySpliterator>
      implements LongSpliterator {
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

      final void acceptOnIndex(java.util.function.LongConsumer action, int index) {
         action.accept(Long2LongOpenCustomHashMap.this.key[index]);
      }

      final Long2LongOpenCustomHashMap.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Long2LongOpenCustomHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Long2LongMap.Entry, Entry<Long, Long>, LongLongPair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public long getLongKey() {
         return Long2LongOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public long leftLong() {
         return Long2LongOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public long getLongValue() {
         return Long2LongOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public long rightLong() {
         return Long2LongOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public long setValue(long v) {
         long oldValue = Long2LongOpenCustomHashMap.this.value[this.index];
         Long2LongOpenCustomHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public LongLongPair right(long v) {
         Long2LongOpenCustomHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Long getKey() {
         return Long2LongOpenCustomHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Long getValue() {
         return Long2LongOpenCustomHashMap.this.value[this.index];
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
            Entry<Long, Long> e = (Entry<Long, Long>)o;
            return Long2LongOpenCustomHashMap.this.strategy.equals(Long2LongOpenCustomHashMap.this.key[this.index], e.getKey())
               && Long2LongOpenCustomHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return Long2LongOpenCustomHashMap.this.strategy.hashCode(Long2LongOpenCustomHashMap.this.key[this.index])
            ^ HashCommon.long2int(Long2LongOpenCustomHashMap.this.value[this.index]);
      }

      @Override
      public String toString() {
         return Long2LongOpenCustomHashMap.this.key[this.index] + "=>" + Long2LongOpenCustomHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Long2LongMap.Entry> implements Long2LongMap.FastEntrySet {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Long2LongMap.Entry> iterator() {
         return Long2LongOpenCustomHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Long2LongMap.Entry> fastIterator() {
         return Long2LongOpenCustomHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Long2LongMap.Entry> spliterator() {
         return Long2LongOpenCustomHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Long)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Long) {
               long k = (Long)e.getKey();
               long v = (Long)e.getValue();
               if (Long2LongOpenCustomHashMap.this.strategy.equals(k, 0L)) {
                  return Long2LongOpenCustomHashMap.this.containsNullKey && Long2LongOpenCustomHashMap.this.value[Long2LongOpenCustomHashMap.this.n] == v;
               } else {
                  long[] key = Long2LongOpenCustomHashMap.this.key;
                  long curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(Long2LongOpenCustomHashMap.this.strategy.hashCode(k)) & Long2LongOpenCustomHashMap.this.mask]) == 0L) {
                     return false;
                  } else if (Long2LongOpenCustomHashMap.this.strategy.equals(k, curr)) {
                     return Long2LongOpenCustomHashMap.this.value[pos] == v;
                  } else {
                     while ((curr = key[pos = pos + 1 & Long2LongOpenCustomHashMap.this.mask]) != 0L) {
                        if (Long2LongOpenCustomHashMap.this.strategy.equals(k, curr)) {
                           return Long2LongOpenCustomHashMap.this.value[pos] == v;
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
               if (e.getValue() != null && e.getValue() instanceof Long) {
                  long k = (Long)e.getKey();
                  long v = (Long)e.getValue();
                  if (Long2LongOpenCustomHashMap.this.strategy.equals(k, 0L)) {
                     if (Long2LongOpenCustomHashMap.this.containsNullKey && Long2LongOpenCustomHashMap.this.value[Long2LongOpenCustomHashMap.this.n] == v) {
                        Long2LongOpenCustomHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     long[] key = Long2LongOpenCustomHashMap.this.key;
                     long curr;
                     int pos;
                     if ((curr = key[pos = HashCommon.mix(Long2LongOpenCustomHashMap.this.strategy.hashCode(k)) & Long2LongOpenCustomHashMap.this.mask]) == 0L) {
                        return false;
                     } else if (Long2LongOpenCustomHashMap.this.strategy.equals(curr, k)) {
                        if (Long2LongOpenCustomHashMap.this.value[pos] == v) {
                           Long2LongOpenCustomHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while ((curr = key[pos = pos + 1 & Long2LongOpenCustomHashMap.this.mask]) != 0L) {
                           if (Long2LongOpenCustomHashMap.this.strategy.equals(curr, k) && Long2LongOpenCustomHashMap.this.value[pos] == v) {
                              Long2LongOpenCustomHashMap.this.removeEntry(pos);
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
         return Long2LongOpenCustomHashMap.this.size;
      }

      @Override
      public void clear() {
         Long2LongOpenCustomHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Long2LongMap.Entry> consumer) {
         if (Long2LongOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Long2LongOpenCustomHashMap.this.new MapEntry(Long2LongOpenCustomHashMap.this.n));
         }

         int pos = Long2LongOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Long2LongOpenCustomHashMap.this.key[pos] != 0L) {
               consumer.accept(Long2LongOpenCustomHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Long2LongMap.Entry> consumer) {
         Long2LongOpenCustomHashMap.MapEntry entry = Long2LongOpenCustomHashMap.this.new MapEntry();
         if (Long2LongOpenCustomHashMap.this.containsNullKey) {
            entry.index = Long2LongOpenCustomHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Long2LongOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Long2LongOpenCustomHashMap.this.key[pos] != 0L) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Long2LongOpenCustomHashMap.this.n;
      int last = -1;
      int c = Long2LongOpenCustomHashMap.this.size;
      boolean mustReturnNullKey = Long2LongOpenCustomHashMap.this.containsNullKey;
      LongArrayList wrapped;

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
               return this.last = Long2LongOpenCustomHashMap.this.n;
            } else {
               long[] key = Long2LongOpenCustomHashMap.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != 0L) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               long k = this.wrapped.getLong(-this.pos - 1);
               int p = HashCommon.mix(Long2LongOpenCustomHashMap.this.strategy.hashCode(k)) & Long2LongOpenCustomHashMap.this.mask;

               while (!Long2LongOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Long2LongOpenCustomHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Long2LongOpenCustomHashMap.this.n);
            this.c--;
         }

         long[] key = Long2LongOpenCustomHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               long k = this.wrapped.getLong(-this.pos - 1);
               int p = HashCommon.mix(Long2LongOpenCustomHashMap.this.strategy.hashCode(k)) & Long2LongOpenCustomHashMap.this.mask;

               while (!Long2LongOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Long2LongOpenCustomHashMap.this.mask;
               }

               this.acceptOnIndex(action, p);
               this.c--;
            } else if (key[this.pos] != 0L) {
               this.acceptOnIndex(action, this.last = this.pos);
               this.c--;
            }
         }
      }

      private void shiftKeys(int pos) {
         long[] key = Long2LongOpenCustomHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            long curr;
            for (pos = pos + 1 & Long2LongOpenCustomHashMap.this.mask; (curr = key[pos]) != 0L; pos = pos + 1 & Long2LongOpenCustomHashMap.this.mask) {
               int slot = HashCommon.mix(Long2LongOpenCustomHashMap.this.strategy.hashCode(curr)) & Long2LongOpenCustomHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new LongArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Long2LongOpenCustomHashMap.this.value[last] = Long2LongOpenCustomHashMap.this.value[pos];
                  continue label38;
               }
            }

            key[last] = 0L;
            return;
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Long2LongOpenCustomHashMap.this.n) {
               Long2LongOpenCustomHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Long2LongOpenCustomHashMap.this.remove(this.wrapped.getLong(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Long2LongOpenCustomHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Long2LongOpenCustomHashMap.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Long2LongOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Long2LongOpenCustomHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Long2LongOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Long2LongOpenCustomHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Long2LongOpenCustomHashMap.this.n);
            return true;
         } else {
            for (long[] key = Long2LongOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
               if (key[this.pos] != 0L) {
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
            this.acceptOnIndex(action, Long2LongOpenCustomHashMap.this.n);
         }

         for (long[] key = Long2LongOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
            if (key[this.pos] != 0L) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Long2LongOpenCustomHashMap.this.size - this.c
            : Math.min(
               (long)(Long2LongOpenCustomHashMap.this.size - this.c),
               (long)((double)Long2LongOpenCustomHashMap.this.realSize() / Long2LongOpenCustomHashMap.this.n * (this.max - this.pos))
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

            long[] key = Long2LongOpenCustomHashMap.this.key;

            while (this.pos < this.max && n > 0L) {
               if (key[this.pos++] != 0L) {
                  skipped++;
                  n--;
               }
            }

            return skipped;
         }
      }
   }

   private final class ValueIterator extends Long2LongOpenCustomHashMap.MapIterator<java.util.function.LongConsumer> implements LongIterator {
      public ValueIterator() {
      }

      final void acceptOnIndex(java.util.function.LongConsumer action, int index) {
         action.accept(Long2LongOpenCustomHashMap.this.value[index]);
      }

      @Override
      public long nextLong() {
         return Long2LongOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Long2LongOpenCustomHashMap.MapSpliterator<java.util.function.LongConsumer, Long2LongOpenCustomHashMap.ValueSpliterator>
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

      final void acceptOnIndex(java.util.function.LongConsumer action, int index) {
         action.accept(Long2LongOpenCustomHashMap.this.value[index]);
      }

      final Long2LongOpenCustomHashMap.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Long2LongOpenCustomHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
