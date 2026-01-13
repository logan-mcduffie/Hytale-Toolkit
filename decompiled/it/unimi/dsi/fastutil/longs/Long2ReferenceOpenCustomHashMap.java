package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.AbstractReferenceCollection;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ReferenceCollection;
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

public class Long2ReferenceOpenCustomHashMap<V> extends AbstractLong2ReferenceMap<V> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient long[] key;
   protected transient V[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected LongHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Long2ReferenceMap.FastEntrySet<V> entries;
   protected transient LongSet keys;
   protected transient ReferenceCollection<V> values;

   public Long2ReferenceOpenCustomHashMap(int expected, float f, LongHash.Strategy strategy) {
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
         this.value = (V[])(new Object[this.n + 1]);
      }
   }

   public Long2ReferenceOpenCustomHashMap(int expected, LongHash.Strategy strategy) {
      this(expected, 0.75F, strategy);
   }

   public Long2ReferenceOpenCustomHashMap(LongHash.Strategy strategy) {
      this(16, 0.75F, strategy);
   }

   public Long2ReferenceOpenCustomHashMap(Map<? extends Long, ? extends V> m, float f, LongHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Long2ReferenceOpenCustomHashMap(Map<? extends Long, ? extends V> m, LongHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Long2ReferenceOpenCustomHashMap(Long2ReferenceMap<V> m, float f, LongHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Long2ReferenceOpenCustomHashMap(Long2ReferenceMap<V> m, LongHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Long2ReferenceOpenCustomHashMap(long[] k, V[] v, float f, LongHash.Strategy strategy) {
      this(k.length, f, strategy);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Long2ReferenceOpenCustomHashMap(long[] k, V[] v, LongHash.Strategy strategy) {
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

   private V removeEntry(int pos) {
      V oldValue = this.value[pos];
      this.value[pos] = null;
      this.size--;
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
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends Long, ? extends V> m) {
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

   private void insert(int pos, long k, V v) {
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
   public V put(long k, V v) {
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
         this.value[last] = null;
         return;
      }
   }

   @Override
   public V remove(long k) {
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
   public V get(long k) {
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
   public boolean containsValue(Object v) {
      V[] value = this.value;
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
   public V getOrDefault(long k, V defaultValue) {
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
   public V putIfAbsent(long k, V v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(long k, Object v) {
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
   public boolean replace(long k, V oldValue, V v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public V replace(long k, V v) {
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
   public V computeIfAbsent(long k, LongFunction<? extends V> mappingFunction) {
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
   public V computeIfAbsent(long key, Long2ReferenceFunction<? extends V> mappingFunction) {
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
   public V computeIfPresent(long k, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else if (this.value[pos] == null) {
         return this.defRetValue;
      } else {
         V newValue = (V)remappingFunction.apply(k, this.value[pos]);
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
   public V compute(long k, BiFunction<? super Long, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      V newValue = (V)remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (this.strategy.equals(k, 0L)) {
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
   public V merge(long k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      Objects.requireNonNull(v);
      int pos = this.find(k);
      if (pos >= 0 && this.value[pos] != null) {
         V newValue = (V)remappingFunction.apply(this.value[pos], v);
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
         Arrays.fill(this.key, 0L);
         Arrays.fill(this.value, null);
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

   public Long2ReferenceMap.FastEntrySet<V> long2ReferenceEntrySet() {
      if (this.entries == null) {
         this.entries = new Long2ReferenceOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public LongSet keySet() {
      if (this.keys == null) {
         this.keys = new Long2ReferenceOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ReferenceCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractReferenceCollection<V>() {
            @Override
            public ObjectIterator<V> iterator() {
               return Long2ReferenceOpenCustomHashMap.this.new ValueIterator();
            }

            @Override
            public ObjectSpliterator<V> spliterator() {
               return Long2ReferenceOpenCustomHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(Consumer<? super V> consumer) {
               if (Long2ReferenceOpenCustomHashMap.this.containsNullKey) {
                  consumer.accept(Long2ReferenceOpenCustomHashMap.this.value[Long2ReferenceOpenCustomHashMap.this.n]);
               }

               int pos = Long2ReferenceOpenCustomHashMap.this.n;

               while (pos-- != 0) {
                  if (Long2ReferenceOpenCustomHashMap.this.key[pos] != 0L) {
                     consumer.accept(Long2ReferenceOpenCustomHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Long2ReferenceOpenCustomHashMap.this.size;
            }

            @Override
            public boolean contains(Object v) {
               return Long2ReferenceOpenCustomHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Long2ReferenceOpenCustomHashMap.this.clear();
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
      V[] value = this.value;
      int mask = newN - 1;
      long[] newKey = new long[newN + 1];
      V[] newValue = (V[])(new Object[newN + 1]);
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

   public Long2ReferenceOpenCustomHashMap<V> clone() {
      Long2ReferenceOpenCustomHashMap<V> c;
      try {
         c = (Long2ReferenceOpenCustomHashMap<V>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (long[])this.key.clone();
      c.value = (V[])((Object[])this.value.clone());
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
      long[] key = this.key;
      V[] value = this.value;
      Long2ReferenceOpenCustomHashMap<V>.EntryIterator i = new Long2ReferenceOpenCustomHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeLong(key[e]);
         s.writeObject(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      long[] key = this.key = new long[this.n + 1];
      V[] value = this.value = (V[])(new Object[this.n + 1]);
      int i = this.size;

      while (i-- != 0) {
         long k = s.readLong();
         V v = (V)s.readObject();
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
      extends Long2ReferenceOpenCustomHashMap<V>.MapIterator<Consumer<? super Long2ReferenceMap.Entry<V>>>
      implements ObjectIterator<Long2ReferenceMap.Entry<V>> {
      private Long2ReferenceOpenCustomHashMap<V>.MapEntry entry;

      private EntryIterator() {
      }

      public Long2ReferenceOpenCustomHashMap<V>.MapEntry next() {
         return this.entry = Long2ReferenceOpenCustomHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Long2ReferenceMap.Entry<V>> action, int index) {
         action.accept(this.entry = Long2ReferenceOpenCustomHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Long2ReferenceOpenCustomHashMap<V>.MapSpliterator<Consumer<? super Long2ReferenceMap.Entry<V>>, Long2ReferenceOpenCustomHashMap<V>.EntrySpliterator>
      implements ObjectSpliterator<Long2ReferenceMap.Entry<V>> {
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

      final void acceptOnIndex(Consumer<? super Long2ReferenceMap.Entry<V>> action, int index) {
         action.accept(Long2ReferenceOpenCustomHashMap.this.new MapEntry(index));
      }

      final Long2ReferenceOpenCustomHashMap<V>.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Long2ReferenceOpenCustomHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Long2ReferenceOpenCustomHashMap<V>.MapIterator<Consumer<? super Long2ReferenceMap.Entry<V>>>
      implements ObjectIterator<Long2ReferenceMap.Entry<V>> {
      private final Long2ReferenceOpenCustomHashMap<V>.MapEntry entry = Long2ReferenceOpenCustomHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Long2ReferenceOpenCustomHashMap<V>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Long2ReferenceMap.Entry<V>> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Long2ReferenceOpenCustomHashMap<V>.MapIterator<java.util.function.LongConsumer> implements LongIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(java.util.function.LongConsumer action, int index) {
         action.accept(Long2ReferenceOpenCustomHashMap.this.key[index]);
      }

      @Override
      public long nextLong() {
         return Long2ReferenceOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractLongSet {
      private KeySet() {
      }

      @Override
      public LongIterator iterator() {
         return Long2ReferenceOpenCustomHashMap.this.new KeyIterator();
      }

      @Override
      public LongSpliterator spliterator() {
         return Long2ReferenceOpenCustomHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(java.util.function.LongConsumer consumer) {
         if (Long2ReferenceOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Long2ReferenceOpenCustomHashMap.this.key[Long2ReferenceOpenCustomHashMap.this.n]);
         }

         int pos = Long2ReferenceOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            long k = Long2ReferenceOpenCustomHashMap.this.key[pos];
            if (k != 0L) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Long2ReferenceOpenCustomHashMap.this.size;
      }

      @Override
      public boolean contains(long k) {
         return Long2ReferenceOpenCustomHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(long k) {
         int oldSize = Long2ReferenceOpenCustomHashMap.this.size;
         Long2ReferenceOpenCustomHashMap.this.remove(k);
         return Long2ReferenceOpenCustomHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Long2ReferenceOpenCustomHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Long2ReferenceOpenCustomHashMap<V>.MapSpliterator<java.util.function.LongConsumer, Long2ReferenceOpenCustomHashMap<V>.KeySpliterator>
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
         action.accept(Long2ReferenceOpenCustomHashMap.this.key[index]);
      }

      final Long2ReferenceOpenCustomHashMap<V>.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Long2ReferenceOpenCustomHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Long2ReferenceMap.Entry<V>, Entry<Long, V>, LongReferencePair<V> {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public long getLongKey() {
         return Long2ReferenceOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public long leftLong() {
         return Long2ReferenceOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public V getValue() {
         return Long2ReferenceOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public V right() {
         return Long2ReferenceOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public V setValue(V v) {
         V oldValue = Long2ReferenceOpenCustomHashMap.this.value[this.index];
         Long2ReferenceOpenCustomHashMap.this.value[this.index] = v;
         return oldValue;
      }

      public LongReferencePair<V> right(V v) {
         Long2ReferenceOpenCustomHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Long getKey() {
         return Long2ReferenceOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Long, V> e = (Entry<Long, V>)o;
            return Long2ReferenceOpenCustomHashMap.this.strategy.equals(Long2ReferenceOpenCustomHashMap.this.key[this.index], e.getKey())
               && Long2ReferenceOpenCustomHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(Long2ReferenceOpenCustomHashMap.this.key[this.index])
            ^ (
               Long2ReferenceOpenCustomHashMap.this.value[this.index] == null
                  ? 0
                  : System.identityHashCode(Long2ReferenceOpenCustomHashMap.this.value[this.index])
            );
      }

      @Override
      public String toString() {
         return Long2ReferenceOpenCustomHashMap.this.key[this.index] + "=>" + Long2ReferenceOpenCustomHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Long2ReferenceMap.Entry<V>> implements Long2ReferenceMap.FastEntrySet<V> {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Long2ReferenceMap.Entry<V>> iterator() {
         return Long2ReferenceOpenCustomHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Long2ReferenceMap.Entry<V>> fastIterator() {
         return Long2ReferenceOpenCustomHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Long2ReferenceMap.Entry<V>> spliterator() {
         return Long2ReferenceOpenCustomHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() != null && e.getKey() instanceof Long) {
               long k = (Long)e.getKey();
               V v = (V)e.getValue();
               if (Long2ReferenceOpenCustomHashMap.this.strategy.equals(k, 0L)) {
                  return Long2ReferenceOpenCustomHashMap.this.containsNullKey
                     && Long2ReferenceOpenCustomHashMap.this.value[Long2ReferenceOpenCustomHashMap.this.n] == v;
               } else {
                  long[] key = Long2ReferenceOpenCustomHashMap.this.key;
                  long curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ReferenceOpenCustomHashMap.this.mask])
                     == 0L) {
                     return false;
                  } else if (Long2ReferenceOpenCustomHashMap.this.strategy.equals(k, curr)) {
                     return Long2ReferenceOpenCustomHashMap.this.value[pos] == v;
                  } else {
                     while ((curr = key[pos = pos + 1 & Long2ReferenceOpenCustomHashMap.this.mask]) != 0L) {
                        if (Long2ReferenceOpenCustomHashMap.this.strategy.equals(k, curr)) {
                           return Long2ReferenceOpenCustomHashMap.this.value[pos] == v;
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
               long k = (Long)e.getKey();
               V v = (V)e.getValue();
               if (Long2ReferenceOpenCustomHashMap.this.strategy.equals(k, 0L)) {
                  if (Long2ReferenceOpenCustomHashMap.this.containsNullKey
                     && Long2ReferenceOpenCustomHashMap.this.value[Long2ReferenceOpenCustomHashMap.this.n] == v) {
                     Long2ReferenceOpenCustomHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  long[] key = Long2ReferenceOpenCustomHashMap.this.key;
                  long curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ReferenceOpenCustomHashMap.this.mask])
                     == 0L) {
                     return false;
                  } else if (Long2ReferenceOpenCustomHashMap.this.strategy.equals(curr, k)) {
                     if (Long2ReferenceOpenCustomHashMap.this.value[pos] == v) {
                        Long2ReferenceOpenCustomHashMap.this.removeEntry(pos);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     while ((curr = key[pos = pos + 1 & Long2ReferenceOpenCustomHashMap.this.mask]) != 0L) {
                        if (Long2ReferenceOpenCustomHashMap.this.strategy.equals(curr, k) && Long2ReferenceOpenCustomHashMap.this.value[pos] == v) {
                           Long2ReferenceOpenCustomHashMap.this.removeEntry(pos);
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
         return Long2ReferenceOpenCustomHashMap.this.size;
      }

      @Override
      public void clear() {
         Long2ReferenceOpenCustomHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Long2ReferenceMap.Entry<V>> consumer) {
         if (Long2ReferenceOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Long2ReferenceOpenCustomHashMap.this.new MapEntry(Long2ReferenceOpenCustomHashMap.this.n));
         }

         int pos = Long2ReferenceOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Long2ReferenceOpenCustomHashMap.this.key[pos] != 0L) {
               consumer.accept(Long2ReferenceOpenCustomHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Long2ReferenceMap.Entry<V>> consumer) {
         Long2ReferenceOpenCustomHashMap<V>.MapEntry entry = Long2ReferenceOpenCustomHashMap.this.new MapEntry();
         if (Long2ReferenceOpenCustomHashMap.this.containsNullKey) {
            entry.index = Long2ReferenceOpenCustomHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Long2ReferenceOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Long2ReferenceOpenCustomHashMap.this.key[pos] != 0L) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Long2ReferenceOpenCustomHashMap.this.n;
      int last = -1;
      int c = Long2ReferenceOpenCustomHashMap.this.size;
      boolean mustReturnNullKey = Long2ReferenceOpenCustomHashMap.this.containsNullKey;
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
               return this.last = Long2ReferenceOpenCustomHashMap.this.n;
            } else {
               long[] key = Long2ReferenceOpenCustomHashMap.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != 0L) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               long k = this.wrapped.getLong(-this.pos - 1);
               int p = HashCommon.mix(Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ReferenceOpenCustomHashMap.this.mask;

               while (!Long2ReferenceOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Long2ReferenceOpenCustomHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Long2ReferenceOpenCustomHashMap.this.n);
            this.c--;
         }

         long[] key = Long2ReferenceOpenCustomHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               long k = this.wrapped.getLong(-this.pos - 1);
               int p = HashCommon.mix(Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Long2ReferenceOpenCustomHashMap.this.mask;

               while (!Long2ReferenceOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Long2ReferenceOpenCustomHashMap.this.mask;
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
         long[] key = Long2ReferenceOpenCustomHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            long curr;
            for (pos = pos + 1 & Long2ReferenceOpenCustomHashMap.this.mask; (curr = key[pos]) != 0L; pos = pos + 1 & Long2ReferenceOpenCustomHashMap.this.mask) {
               int slot = HashCommon.mix(Long2ReferenceOpenCustomHashMap.this.strategy.hashCode(curr)) & Long2ReferenceOpenCustomHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new LongArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Long2ReferenceOpenCustomHashMap.this.value[last] = Long2ReferenceOpenCustomHashMap.this.value[pos];
                  continue label38;
               }
            }

            key[last] = 0L;
            Long2ReferenceOpenCustomHashMap.this.value[last] = null;
            return;
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Long2ReferenceOpenCustomHashMap.this.n) {
               Long2ReferenceOpenCustomHashMap.this.containsNullKey = false;
               Long2ReferenceOpenCustomHashMap.this.value[Long2ReferenceOpenCustomHashMap.this.n] = null;
            } else {
               if (this.pos < 0) {
                  Long2ReferenceOpenCustomHashMap.this.remove(this.wrapped.getLong(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Long2ReferenceOpenCustomHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Long2ReferenceOpenCustomHashMap<V>.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Long2ReferenceOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Long2ReferenceOpenCustomHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Long2ReferenceOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Long2ReferenceOpenCustomHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Long2ReferenceOpenCustomHashMap.this.n);
            return true;
         } else {
            for (long[] key = Long2ReferenceOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
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
            this.acceptOnIndex(action, Long2ReferenceOpenCustomHashMap.this.n);
         }

         for (long[] key = Long2ReferenceOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
            if (key[this.pos] != 0L) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Long2ReferenceOpenCustomHashMap.this.size - this.c
            : Math.min(
               (long)(Long2ReferenceOpenCustomHashMap.this.size - this.c),
               (long)((double)Long2ReferenceOpenCustomHashMap.this.realSize() / Long2ReferenceOpenCustomHashMap.this.n * (this.max - this.pos))
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

            long[] key = Long2ReferenceOpenCustomHashMap.this.key;

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

   private final class ValueIterator extends Long2ReferenceOpenCustomHashMap<V>.MapIterator<Consumer<? super V>> implements ObjectIterator<V> {
      public ValueIterator() {
      }

      final void acceptOnIndex(Consumer<? super V> action, int index) {
         action.accept(Long2ReferenceOpenCustomHashMap.this.value[index]);
      }

      @Override
      public V next() {
         return Long2ReferenceOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Long2ReferenceOpenCustomHashMap<V>.MapSpliterator<Consumer<? super V>, Long2ReferenceOpenCustomHashMap<V>.ValueSpliterator>
      implements ObjectSpliterator<V> {
      private static final int POST_SPLIT_CHARACTERISTICS = 0;

      ValueSpliterator() {
      }

      ValueSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         super(pos, max, mustReturnNull, hasSplit);
      }

      @Override
      public int characteristics() {
         return this.hasSplit ? 0 : 64;
      }

      final void acceptOnIndex(Consumer<? super V> action, int index) {
         action.accept(Long2ReferenceOpenCustomHashMap.this.value[index]);
      }

      final Long2ReferenceOpenCustomHashMap<V>.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Long2ReferenceOpenCustomHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
