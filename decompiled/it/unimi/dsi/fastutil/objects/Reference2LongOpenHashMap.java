package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.longs.AbstractLongCollection;
import it.unimi.dsi.fastutil.longs.LongCollection;
import it.unimi.dsi.fastutil.longs.LongIterator;
import it.unimi.dsi.fastutil.longs.LongSpliterator;
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
import java.util.function.LongConsumer;
import java.util.function.ToLongFunction;

public class Reference2LongOpenHashMap<K> extends AbstractReference2LongMap<K> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
   protected transient long[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Reference2LongMap.FastEntrySet<K> entries;
   protected transient ReferenceSet<K> keys;
   protected transient LongCollection values;

   public Reference2LongOpenHashMap(int expected, float f) {
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
         this.value = new long[this.n + 1];
      }
   }

   public Reference2LongOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Reference2LongOpenHashMap() {
      this(16, 0.75F);
   }

   public Reference2LongOpenHashMap(Map<? extends K, ? extends Long> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Reference2LongOpenHashMap(Map<? extends K, ? extends Long> m) {
      this(m, 0.75F);
   }

   public Reference2LongOpenHashMap(Reference2LongMap<K> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Reference2LongOpenHashMap(Reference2LongMap<K> m) {
      this(m, 0.75F);
   }

   public Reference2LongOpenHashMap(K[] k, long[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Reference2LongOpenHashMap(K[] k, long[] v) {
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
      this.key[this.n] = null;
      long oldValue = this.value[this.n];
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends K, ? extends Long> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(K k) {
      if (k == null) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) == null) {
            return -(pos + 1);
         } else if (k == curr) {
            return pos;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k == curr) {
                  return pos;
               }
            }

            return -(pos + 1);
         }
      }
   }

   private void insert(int pos, K k, long v) {
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
   public long put(K k, long v) {
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

   public long addTo(K k, long incr) {
      int pos;
      if (k == null) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, incr);
         }

         pos = this.n;
         this.containsNullKey = true;
      } else {
         K[] key = this.key;
         K curr;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) != null) {
            if (curr == k) {
               return this.addToValue(pos, incr);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (curr == k) {
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
            int slot = HashCommon.mix(System.identityHashCode(curr)) & this.mask;
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
   public long removeLong(Object k) {
      if (k == null) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (k == curr) {
            return this.removeEntry(pos);
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k == curr) {
                  return this.removeEntry(pos);
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public long getLong(Object k) {
      if (k == null) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (k == curr) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k == curr) {
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public boolean containsKey(Object k) {
      if (k == null) {
         return this.containsNullKey;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) == null) {
            return false;
         } else if (k == curr) {
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k == curr) {
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
      K[] key = this.key;
      if (this.containsNullKey && value[this.n] == v) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (key[i] != null && value[i] == v) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public long getOrDefault(Object k, long defaultValue) {
      if (k == null) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) == null) {
            return defaultValue;
         } else if (k == curr) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k == curr) {
                  return this.value[pos];
               }
            }

            return defaultValue;
         }
      }
   }

   @Override
   public long putIfAbsent(K k, long v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(Object k, long v) {
      if (k == null) {
         if (this.containsNullKey && v == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) == null) {
            return false;
         } else if (k == curr && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
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
   public boolean replace(K k, long oldValue, long v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public long replace(K k, long v) {
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
   public long computeIfAbsent(K k, ToLongFunction<? super K> mappingFunction) {
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
   public long computeIfAbsent(K key, Reference2LongFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         long newValue = mappingFunction.getLong(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public long computeLongIfPresent(K k, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Long newValue = remappingFunction.apply(k, this.value[pos]);
         if (newValue == null) {
            if (k == null) {
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
   public long computeLong(K k, BiFunction<? super K, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Long newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (k == null) {
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
   public long merge(K k, long v, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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
            if (k == null) {
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

   public Reference2LongMap.FastEntrySet<K> reference2LongEntrySet() {
      if (this.entries == null) {
         this.entries = new Reference2LongOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public ReferenceSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Reference2LongOpenHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public LongCollection values() {
      if (this.values == null) {
         this.values = new AbstractLongCollection() {
            @Override
            public LongIterator iterator() {
               return Reference2LongOpenHashMap.this.new ValueIterator();
            }

            @Override
            public LongSpliterator spliterator() {
               return Reference2LongOpenHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(LongConsumer consumer) {
               if (Reference2LongOpenHashMap.this.containsNullKey) {
                  consumer.accept(Reference2LongOpenHashMap.this.value[Reference2LongOpenHashMap.this.n]);
               }

               int pos = Reference2LongOpenHashMap.this.n;

               while (pos-- != 0) {
                  if (Reference2LongOpenHashMap.this.key[pos] != null) {
                     consumer.accept(Reference2LongOpenHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Reference2LongOpenHashMap.this.size;
            }

            @Override
            public boolean contains(long v) {
               return Reference2LongOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Reference2LongOpenHashMap.this.clear();
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
      long[] value = this.value;
      int mask = newN - 1;
      K[] newKey = (K[])(new Object[newN + 1]);
      long[] newValue = new long[newN + 1];
      int i = this.n;
      int j = this.realSize();

      while (j-- != 0) {
         while (key[--i] == null) {
         }

         int pos;
         if (newKey[pos = HashCommon.mix(System.identityHashCode(key[i])) & mask] != null) {
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

   public Reference2LongOpenHashMap<K> clone() {
      Reference2LongOpenHashMap<K> c;
      try {
         c = (Reference2LongOpenHashMap<K>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (K[])((Object[])this.key.clone());
      c.value = (long[])this.value.clone();
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
            t = System.identityHashCode(this.key[i]);
         }

         t ^= HashCommon.long2int(this.value[i]);
         h += t;
      }

      if (this.containsNullKey) {
         h += HashCommon.long2int(this.value[this.n]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      K[] key = this.key;
      long[] value = this.value;
      Reference2LongOpenHashMap<K>.EntryIterator i = new Reference2LongOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeObject(key[e]);
         s.writeLong(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      K[] key = this.key = (K[])(new Object[this.n + 1]);
      long[] value = this.value = new long[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         K k = (K)s.readObject();
         long v = s.readLong();
         int pos;
         if (k == null) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;

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
      extends Reference2LongOpenHashMap<K>.MapIterator<Consumer<? super Reference2LongMap.Entry<K>>>
      implements ObjectIterator<Reference2LongMap.Entry<K>> {
      private Reference2LongOpenHashMap<K>.MapEntry entry;

      private EntryIterator() {
      }

      public Reference2LongOpenHashMap<K>.MapEntry next() {
         return this.entry = Reference2LongOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Reference2LongMap.Entry<K>> action, int index) {
         action.accept(this.entry = Reference2LongOpenHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Reference2LongOpenHashMap<K>.MapSpliterator<Consumer<? super Reference2LongMap.Entry<K>>, Reference2LongOpenHashMap<K>.EntrySpliterator>
      implements ObjectSpliterator<Reference2LongMap.Entry<K>> {
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

      final void acceptOnIndex(Consumer<? super Reference2LongMap.Entry<K>> action, int index) {
         action.accept(Reference2LongOpenHashMap.this.new MapEntry(index));
      }

      final Reference2LongOpenHashMap<K>.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Reference2LongOpenHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Reference2LongOpenHashMap<K>.MapIterator<Consumer<? super Reference2LongMap.Entry<K>>>
      implements ObjectIterator<Reference2LongMap.Entry<K>> {
      private final Reference2LongOpenHashMap<K>.MapEntry entry = Reference2LongOpenHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Reference2LongOpenHashMap<K>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Reference2LongMap.Entry<K>> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Reference2LongOpenHashMap<K>.MapIterator<Consumer<? super K>> implements ObjectIterator<K> {
      public KeyIterator() {
      }

      final void acceptOnIndex(Consumer<? super K> action, int index) {
         action.accept(Reference2LongOpenHashMap.this.key[index]);
      }

      @Override
      public K next() {
         return Reference2LongOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractReferenceSet<K> {
      private KeySet() {
      }

      @Override
      public ObjectIterator<K> iterator() {
         return Reference2LongOpenHashMap.this.new KeyIterator();
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return Reference2LongOpenHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(Consumer<? super K> consumer) {
         if (Reference2LongOpenHashMap.this.containsNullKey) {
            consumer.accept(Reference2LongOpenHashMap.this.key[Reference2LongOpenHashMap.this.n]);
         }

         int pos = Reference2LongOpenHashMap.this.n;

         while (pos-- != 0) {
            K k = Reference2LongOpenHashMap.this.key[pos];
            if (k != null) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Reference2LongOpenHashMap.this.size;
      }

      @Override
      public boolean contains(Object k) {
         return Reference2LongOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(Object k) {
         int oldSize = Reference2LongOpenHashMap.this.size;
         Reference2LongOpenHashMap.this.removeLong(k);
         return Reference2LongOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Reference2LongOpenHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Reference2LongOpenHashMap<K>.MapSpliterator<Consumer<? super K>, Reference2LongOpenHashMap<K>.KeySpliterator>
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
         action.accept(Reference2LongOpenHashMap.this.key[index]);
      }

      final Reference2LongOpenHashMap<K>.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Reference2LongOpenHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Reference2LongMap.Entry<K>, Entry<K, Long>, ReferenceLongPair<K> {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public K getKey() {
         return Reference2LongOpenHashMap.this.key[this.index];
      }

      @Override
      public K left() {
         return Reference2LongOpenHashMap.this.key[this.index];
      }

      @Override
      public long getLongValue() {
         return Reference2LongOpenHashMap.this.value[this.index];
      }

      @Override
      public long rightLong() {
         return Reference2LongOpenHashMap.this.value[this.index];
      }

      @Override
      public long setValue(long v) {
         long oldValue = Reference2LongOpenHashMap.this.value[this.index];
         Reference2LongOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public ReferenceLongPair<K> right(long v) {
         Reference2LongOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Long getValue() {
         return Reference2LongOpenHashMap.this.value[this.index];
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
            Entry<K, Long> e = (Entry<K, Long>)o;
            return Reference2LongOpenHashMap.this.key[this.index] == e.getKey() && Reference2LongOpenHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(Reference2LongOpenHashMap.this.key[this.index]) ^ HashCommon.long2int(Reference2LongOpenHashMap.this.value[this.index]);
      }

      @Override
      public String toString() {
         return Reference2LongOpenHashMap.this.key[this.index] + "=>" + Reference2LongOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Reference2LongMap.Entry<K>> implements Reference2LongMap.FastEntrySet<K> {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Reference2LongMap.Entry<K>> iterator() {
         return Reference2LongOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Reference2LongMap.Entry<K>> fastIterator() {
         return Reference2LongOpenHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Reference2LongMap.Entry<K>> spliterator() {
         return Reference2LongOpenHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getValue() != null && e.getValue() instanceof Long) {
               K k = (K)e.getKey();
               long v = (Long)e.getValue();
               if (k == null) {
                  return Reference2LongOpenHashMap.this.containsNullKey && Reference2LongOpenHashMap.this.value[Reference2LongOpenHashMap.this.n] == v;
               } else {
                  K[] key = Reference2LongOpenHashMap.this.key;
                  K curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & Reference2LongOpenHashMap.this.mask]) == null) {
                     return false;
                  } else if (k == curr) {
                     return Reference2LongOpenHashMap.this.value[pos] == v;
                  } else {
                     while ((curr = key[pos = pos + 1 & Reference2LongOpenHashMap.this.mask]) != null) {
                        if (k == curr) {
                           return Reference2LongOpenHashMap.this.value[pos] == v;
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
            if (e.getValue() != null && e.getValue() instanceof Long) {
               K k = (K)e.getKey();
               long v = (Long)e.getValue();
               if (k == null) {
                  if (Reference2LongOpenHashMap.this.containsNullKey && Reference2LongOpenHashMap.this.value[Reference2LongOpenHashMap.this.n] == v) {
                     Reference2LongOpenHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  K[] key = Reference2LongOpenHashMap.this.key;
                  K curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & Reference2LongOpenHashMap.this.mask]) == null) {
                     return false;
                  } else if (curr == k) {
                     if (Reference2LongOpenHashMap.this.value[pos] == v) {
                        Reference2LongOpenHashMap.this.removeEntry(pos);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     while ((curr = key[pos = pos + 1 & Reference2LongOpenHashMap.this.mask]) != null) {
                        if (curr == k && Reference2LongOpenHashMap.this.value[pos] == v) {
                           Reference2LongOpenHashMap.this.removeEntry(pos);
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
         return Reference2LongOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Reference2LongOpenHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Reference2LongMap.Entry<K>> consumer) {
         if (Reference2LongOpenHashMap.this.containsNullKey) {
            consumer.accept(Reference2LongOpenHashMap.this.new MapEntry(Reference2LongOpenHashMap.this.n));
         }

         int pos = Reference2LongOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Reference2LongOpenHashMap.this.key[pos] != null) {
               consumer.accept(Reference2LongOpenHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Reference2LongMap.Entry<K>> consumer) {
         Reference2LongOpenHashMap<K>.MapEntry entry = Reference2LongOpenHashMap.this.new MapEntry();
         if (Reference2LongOpenHashMap.this.containsNullKey) {
            entry.index = Reference2LongOpenHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Reference2LongOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Reference2LongOpenHashMap.this.key[pos] != null) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Reference2LongOpenHashMap.this.n;
      int last = -1;
      int c = Reference2LongOpenHashMap.this.size;
      boolean mustReturnNullKey = Reference2LongOpenHashMap.this.containsNullKey;
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
               return this.last = Reference2LongOpenHashMap.this.n;
            } else {
               K[] key = Reference2LongOpenHashMap.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != null) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               K k = this.wrapped.get(-this.pos - 1);
               int p = HashCommon.mix(System.identityHashCode(k)) & Reference2LongOpenHashMap.this.mask;

               while (k != key[p]) {
                  p = p + 1 & Reference2LongOpenHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Reference2LongOpenHashMap.this.n);
            this.c--;
         }

         K[] key = Reference2LongOpenHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               K k = this.wrapped.get(-this.pos - 1);
               int p = HashCommon.mix(System.identityHashCode(k)) & Reference2LongOpenHashMap.this.mask;

               while (k != key[p]) {
                  p = p + 1 & Reference2LongOpenHashMap.this.mask;
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
         K[] key = Reference2LongOpenHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            K curr;
            for (pos = pos + 1 & Reference2LongOpenHashMap.this.mask; (curr = key[pos]) != null; pos = pos + 1 & Reference2LongOpenHashMap.this.mask) {
               int slot = HashCommon.mix(System.identityHashCode(curr)) & Reference2LongOpenHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new ReferenceArrayList<>(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Reference2LongOpenHashMap.this.value[last] = Reference2LongOpenHashMap.this.value[pos];
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
            if (this.last == Reference2LongOpenHashMap.this.n) {
               Reference2LongOpenHashMap.this.containsNullKey = false;
               Reference2LongOpenHashMap.this.key[Reference2LongOpenHashMap.this.n] = null;
            } else {
               if (this.pos < 0) {
                  Reference2LongOpenHashMap.this.removeLong(this.wrapped.set(-this.pos - 1, null));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Reference2LongOpenHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Reference2LongOpenHashMap<K>.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Reference2LongOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Reference2LongOpenHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Reference2LongOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Reference2LongOpenHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Reference2LongOpenHashMap.this.n);
            return true;
         } else {
            for (K[] key = Reference2LongOpenHashMap.this.key; this.pos < this.max; this.pos++) {
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
            this.acceptOnIndex(action, Reference2LongOpenHashMap.this.n);
         }

         for (K[] key = Reference2LongOpenHashMap.this.key; this.pos < this.max; this.pos++) {
            if (key[this.pos] != null) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Reference2LongOpenHashMap.this.size - this.c
            : Math.min(
               (long)(Reference2LongOpenHashMap.this.size - this.c),
               (long)((double)Reference2LongOpenHashMap.this.realSize() / Reference2LongOpenHashMap.this.n * (this.max - this.pos))
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

            K[] key = Reference2LongOpenHashMap.this.key;

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

   private final class ValueIterator extends Reference2LongOpenHashMap<K>.MapIterator<LongConsumer> implements LongIterator {
      public ValueIterator() {
      }

      final void acceptOnIndex(LongConsumer action, int index) {
         action.accept(Reference2LongOpenHashMap.this.value[index]);
      }

      @Override
      public long nextLong() {
         return Reference2LongOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Reference2LongOpenHashMap<K>.MapSpliterator<LongConsumer, Reference2LongOpenHashMap<K>.ValueSpliterator>
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
         action.accept(Reference2LongOpenHashMap.this.value[index]);
      }

      final Reference2LongOpenHashMap<K>.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Reference2LongOpenHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
