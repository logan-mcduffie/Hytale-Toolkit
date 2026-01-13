package it.unimi.dsi.fastutil.shorts;

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

public class Short2LongOpenHashMap extends AbstractShort2LongMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient short[] key;
   protected transient long[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Short2LongMap.FastEntrySet entries;
   protected transient ShortSet keys;
   protected transient LongCollection values;

   public Short2LongOpenHashMap(int expected, float f) {
      if (f <= 0.0F || f >= 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      } else if (expected < 0) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.arraySize(expected, f);
         this.mask = this.n - 1;
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = new short[this.n + 1];
         this.value = new long[this.n + 1];
      }
   }

   public Short2LongOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Short2LongOpenHashMap() {
      this(16, 0.75F);
   }

   public Short2LongOpenHashMap(Map<? extends Short, ? extends Long> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Short2LongOpenHashMap(Map<? extends Short, ? extends Long> m) {
      this(m, 0.75F);
   }

   public Short2LongOpenHashMap(Short2LongMap m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Short2LongOpenHashMap(Short2LongMap m) {
      this(m, 0.75F);
   }

   public Short2LongOpenHashMap(short[] k, long[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Short2LongOpenHashMap(short[] k, long[] v) {
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
      long oldValue = this.value[this.n];
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends Short, ? extends Long> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(short k) {
      if (k == 0) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         short[] key = this.key;
         short curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
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

   private void insert(int pos, short k, long v) {
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
   public long put(short k, long v) {
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

   public long addTo(short k, long incr) {
      int pos;
      if (k == 0) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, incr);
         }

         pos = this.n;
         this.containsNullKey = true;
      } else {
         short[] key = this.key;
         short curr;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) != 0) {
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
      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return this.defRetValue;
   }

   protected final void shiftKeys(int pos) {
      short[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         short curr;
         for (pos = pos + 1 & this.mask; (curr = key[pos]) != 0; pos = pos + 1 & this.mask) {
            int slot = HashCommon.mix((int)curr) & this.mask;
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
   public long remove(short k) {
      if (k == 0) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         short[] key = this.key;
         short curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
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

   @Override
   public long get(short k) {
      if (k == 0) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         short[] key = this.key;
         short curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
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
   public boolean containsKey(short k) {
      if (k == 0) {
         return this.containsNullKey;
      } else {
         short[] key = this.key;
         short curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
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
   public boolean containsValue(long v) {
      long[] value = this.value;
      short[] key = this.key;
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
   public long getOrDefault(short k, long defaultValue) {
      if (k == 0) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         short[] key = this.key;
         short curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
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
   public long putIfAbsent(short k, long v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(short k, long v) {
      if (k == 0) {
         if (this.containsNullKey && v == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         short[] key = this.key;
         short curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
            return false;
         } else if (k == curr && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
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
   public boolean replace(short k, long oldValue, long v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public long replace(short k, long v) {
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
   public long computeIfAbsent(short k, IntToLongFunction mappingFunction) {
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
   public long computeIfAbsent(short key, Short2LongFunction mappingFunction) {
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
   public long computeIfAbsentNullable(short k, IntFunction<? extends Long> mappingFunction) {
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
   public long computeIfPresent(short k, BiFunction<? super Short, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Long newValue = remappingFunction.apply(k, this.value[pos]);
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
   public long compute(short k, BiFunction<? super Short, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Long newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
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
   public long merge(short k, long v, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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
         Arrays.fill(this.key, (short)0);
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

   public Short2LongMap.FastEntrySet short2LongEntrySet() {
      if (this.entries == null) {
         this.entries = new Short2LongOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public ShortSet keySet() {
      if (this.keys == null) {
         this.keys = new Short2LongOpenHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public LongCollection values() {
      if (this.values == null) {
         this.values = new AbstractLongCollection() {
            @Override
            public LongIterator iterator() {
               return Short2LongOpenHashMap.this.new ValueIterator();
            }

            @Override
            public LongSpliterator spliterator() {
               return Short2LongOpenHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(LongConsumer consumer) {
               if (Short2LongOpenHashMap.this.containsNullKey) {
                  consumer.accept(Short2LongOpenHashMap.this.value[Short2LongOpenHashMap.this.n]);
               }

               int pos = Short2LongOpenHashMap.this.n;

               while (pos-- != 0) {
                  if (Short2LongOpenHashMap.this.key[pos] != 0) {
                     consumer.accept(Short2LongOpenHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Short2LongOpenHashMap.this.size;
            }

            @Override
            public boolean contains(long v) {
               return Short2LongOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Short2LongOpenHashMap.this.clear();
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
      short[] key = this.key;
      long[] value = this.value;
      int mask = newN - 1;
      short[] newKey = new short[newN + 1];
      long[] newValue = new long[newN + 1];
      int i = this.n;
      int j = this.realSize();

      while (j-- != 0) {
         while (key[--i] == 0) {
         }

         int pos;
         if (newKey[pos = HashCommon.mix(key[i]) & mask] != 0) {
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

   public Short2LongOpenHashMap clone() {
      Short2LongOpenHashMap c;
      try {
         c = (Short2LongOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (short[])this.key.clone();
      c.value = (long[])this.value.clone();
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

         int var5 = this.key[i];
         var5 ^= HashCommon.long2int(this.value[i]);
         h += var5;
      }

      if (this.containsNullKey) {
         h += HashCommon.long2int(this.value[this.n]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      short[] key = this.key;
      long[] value = this.value;
      Short2LongOpenHashMap.EntryIterator i = new Short2LongOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeShort(key[e]);
         s.writeLong(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      short[] key = this.key = new short[this.n + 1];
      long[] value = this.value = new long[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         short k = s.readShort();
         long v = s.readLong();
         int pos;
         if (k == 0) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            pos = HashCommon.mix((int)k) & this.mask;

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
      extends Short2LongOpenHashMap.MapIterator<Consumer<? super Short2LongMap.Entry>>
      implements ObjectIterator<Short2LongMap.Entry> {
      private Short2LongOpenHashMap.MapEntry entry;

      private EntryIterator() {
      }

      public Short2LongOpenHashMap.MapEntry next() {
         return this.entry = Short2LongOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Short2LongMap.Entry> action, int index) {
         action.accept(this.entry = Short2LongOpenHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Short2LongOpenHashMap.MapSpliterator<Consumer<? super Short2LongMap.Entry>, Short2LongOpenHashMap.EntrySpliterator>
      implements ObjectSpliterator<Short2LongMap.Entry> {
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

      final void acceptOnIndex(Consumer<? super Short2LongMap.Entry> action, int index) {
         action.accept(Short2LongOpenHashMap.this.new MapEntry(index));
      }

      final Short2LongOpenHashMap.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Short2LongOpenHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Short2LongOpenHashMap.MapIterator<Consumer<? super Short2LongMap.Entry>>
      implements ObjectIterator<Short2LongMap.Entry> {
      private final Short2LongOpenHashMap.MapEntry entry = Short2LongOpenHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Short2LongOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Short2LongMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Short2LongOpenHashMap.MapIterator<ShortConsumer> implements ShortIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(ShortConsumer action, int index) {
         action.accept(Short2LongOpenHashMap.this.key[index]);
      }

      @Override
      public short nextShort() {
         return Short2LongOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractShortSet {
      private KeySet() {
      }

      @Override
      public ShortIterator iterator() {
         return Short2LongOpenHashMap.this.new KeyIterator();
      }

      @Override
      public ShortSpliterator spliterator() {
         return Short2LongOpenHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(ShortConsumer consumer) {
         if (Short2LongOpenHashMap.this.containsNullKey) {
            consumer.accept(Short2LongOpenHashMap.this.key[Short2LongOpenHashMap.this.n]);
         }

         int pos = Short2LongOpenHashMap.this.n;

         while (pos-- != 0) {
            short k = Short2LongOpenHashMap.this.key[pos];
            if (k != 0) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Short2LongOpenHashMap.this.size;
      }

      @Override
      public boolean contains(short k) {
         return Short2LongOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(short k) {
         int oldSize = Short2LongOpenHashMap.this.size;
         Short2LongOpenHashMap.this.remove(k);
         return Short2LongOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Short2LongOpenHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Short2LongOpenHashMap.MapSpliterator<ShortConsumer, Short2LongOpenHashMap.KeySpliterator>
      implements ShortSpliterator {
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

      final void acceptOnIndex(ShortConsumer action, int index) {
         action.accept(Short2LongOpenHashMap.this.key[index]);
      }

      final Short2LongOpenHashMap.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Short2LongOpenHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Short2LongMap.Entry, Entry<Short, Long>, ShortLongPair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public short getShortKey() {
         return Short2LongOpenHashMap.this.key[this.index];
      }

      @Override
      public short leftShort() {
         return Short2LongOpenHashMap.this.key[this.index];
      }

      @Override
      public long getLongValue() {
         return Short2LongOpenHashMap.this.value[this.index];
      }

      @Override
      public long rightLong() {
         return Short2LongOpenHashMap.this.value[this.index];
      }

      @Override
      public long setValue(long v) {
         long oldValue = Short2LongOpenHashMap.this.value[this.index];
         Short2LongOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public ShortLongPair right(long v) {
         Short2LongOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Short getKey() {
         return Short2LongOpenHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Long getValue() {
         return Short2LongOpenHashMap.this.value[this.index];
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
            Entry<Short, Long> e = (Entry<Short, Long>)o;
            return Short2LongOpenHashMap.this.key[this.index] == e.getKey() && Short2LongOpenHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return Short2LongOpenHashMap.this.key[this.index] ^ HashCommon.long2int(Short2LongOpenHashMap.this.value[this.index]);
      }

      @Override
      public String toString() {
         return Short2LongOpenHashMap.this.key[this.index] + "=>" + Short2LongOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Short2LongMap.Entry> implements Short2LongMap.FastEntrySet {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Short2LongMap.Entry> iterator() {
         return Short2LongOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Short2LongMap.Entry> fastIterator() {
         return Short2LongOpenHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Short2LongMap.Entry> spliterator() {
         return Short2LongOpenHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Short)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Long) {
               short k = (Short)e.getKey();
               long v = (Long)e.getValue();
               if (k == 0) {
                  return Short2LongOpenHashMap.this.containsNullKey && Short2LongOpenHashMap.this.value[Short2LongOpenHashMap.this.n] == v;
               } else {
                  short[] key = Short2LongOpenHashMap.this.key;
                  short curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix((int)k) & Short2LongOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (k == curr) {
                     return Short2LongOpenHashMap.this.value[pos] == v;
                  } else {
                     while ((curr = key[pos = pos + 1 & Short2LongOpenHashMap.this.mask]) != 0) {
                        if (k == curr) {
                           return Short2LongOpenHashMap.this.value[pos] == v;
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
            if (e.getKey() != null && e.getKey() instanceof Short) {
               if (e.getValue() != null && e.getValue() instanceof Long) {
                  short k = (Short)e.getKey();
                  long v = (Long)e.getValue();
                  if (k == 0) {
                     if (Short2LongOpenHashMap.this.containsNullKey && Short2LongOpenHashMap.this.value[Short2LongOpenHashMap.this.n] == v) {
                        Short2LongOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     short[] key = Short2LongOpenHashMap.this.key;
                     short curr;
                     int pos;
                     if ((curr = key[pos = HashCommon.mix((int)k) & Short2LongOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (curr == k) {
                        if (Short2LongOpenHashMap.this.value[pos] == v) {
                           Short2LongOpenHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while ((curr = key[pos = pos + 1 & Short2LongOpenHashMap.this.mask]) != 0) {
                           if (curr == k && Short2LongOpenHashMap.this.value[pos] == v) {
                              Short2LongOpenHashMap.this.removeEntry(pos);
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
         return Short2LongOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Short2LongOpenHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Short2LongMap.Entry> consumer) {
         if (Short2LongOpenHashMap.this.containsNullKey) {
            consumer.accept(Short2LongOpenHashMap.this.new MapEntry(Short2LongOpenHashMap.this.n));
         }

         int pos = Short2LongOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Short2LongOpenHashMap.this.key[pos] != 0) {
               consumer.accept(Short2LongOpenHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Short2LongMap.Entry> consumer) {
         Short2LongOpenHashMap.MapEntry entry = Short2LongOpenHashMap.this.new MapEntry();
         if (Short2LongOpenHashMap.this.containsNullKey) {
            entry.index = Short2LongOpenHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Short2LongOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Short2LongOpenHashMap.this.key[pos] != 0) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Short2LongOpenHashMap.this.n;
      int last = -1;
      int c = Short2LongOpenHashMap.this.size;
      boolean mustReturnNullKey = Short2LongOpenHashMap.this.containsNullKey;
      ShortArrayList wrapped;

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
               return this.last = Short2LongOpenHashMap.this.n;
            } else {
               short[] key = Short2LongOpenHashMap.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               short k = this.wrapped.getShort(-this.pos - 1);
               int p = HashCommon.mix((int)k) & Short2LongOpenHashMap.this.mask;

               while (k != key[p]) {
                  p = p + 1 & Short2LongOpenHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Short2LongOpenHashMap.this.n);
            this.c--;
         }

         short[] key = Short2LongOpenHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               short k = this.wrapped.getShort(-this.pos - 1);
               int p = HashCommon.mix((int)k) & Short2LongOpenHashMap.this.mask;

               while (k != key[p]) {
                  p = p + 1 & Short2LongOpenHashMap.this.mask;
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
         short[] key = Short2LongOpenHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            short curr;
            for (pos = pos + 1 & Short2LongOpenHashMap.this.mask; (curr = key[pos]) != 0; pos = pos + 1 & Short2LongOpenHashMap.this.mask) {
               int slot = HashCommon.mix((int)curr) & Short2LongOpenHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new ShortArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Short2LongOpenHashMap.this.value[last] = Short2LongOpenHashMap.this.value[pos];
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
            if (this.last == Short2LongOpenHashMap.this.n) {
               Short2LongOpenHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Short2LongOpenHashMap.this.remove(this.wrapped.getShort(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Short2LongOpenHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Short2LongOpenHashMap.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Short2LongOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Short2LongOpenHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Short2LongOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Short2LongOpenHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Short2LongOpenHashMap.this.n);
            return true;
         } else {
            for (short[] key = Short2LongOpenHashMap.this.key; this.pos < this.max; this.pos++) {
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
            this.acceptOnIndex(action, Short2LongOpenHashMap.this.n);
         }

         for (short[] key = Short2LongOpenHashMap.this.key; this.pos < this.max; this.pos++) {
            if (key[this.pos] != 0) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Short2LongOpenHashMap.this.size - this.c
            : Math.min(
               (long)(Short2LongOpenHashMap.this.size - this.c),
               (long)((double)Short2LongOpenHashMap.this.realSize() / Short2LongOpenHashMap.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
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

            short[] key = Short2LongOpenHashMap.this.key;

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

   private final class ValueIterator extends Short2LongOpenHashMap.MapIterator<LongConsumer> implements LongIterator {
      public ValueIterator() {
      }

      final void acceptOnIndex(LongConsumer action, int index) {
         action.accept(Short2LongOpenHashMap.this.value[index]);
      }

      @Override
      public long nextLong() {
         return Short2LongOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Short2LongOpenHashMap.MapSpliterator<LongConsumer, Short2LongOpenHashMap.ValueSpliterator>
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
         action.accept(Short2LongOpenHashMap.this.value[index]);
      }

      final Short2LongOpenHashMap.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Short2LongOpenHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
