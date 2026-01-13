package it.unimi.dsi.fastutil.floats;

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
import java.util.function.DoubleFunction;
import java.util.function.DoubleToLongFunction;
import java.util.function.LongConsumer;

public class Float2LongOpenHashMap extends AbstractFloat2LongMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient float[] key;
   protected transient long[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Float2LongMap.FastEntrySet entries;
   protected transient FloatSet keys;
   protected transient LongCollection values;

   public Float2LongOpenHashMap(int expected, float f) {
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
         this.value = new long[this.n + 1];
      }
   }

   public Float2LongOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Float2LongOpenHashMap() {
      this(16, 0.75F);
   }

   public Float2LongOpenHashMap(Map<? extends Float, ? extends Long> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Float2LongOpenHashMap(Map<? extends Float, ? extends Long> m) {
      this(m, 0.75F);
   }

   public Float2LongOpenHashMap(Float2LongMap m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Float2LongOpenHashMap(Float2LongMap m) {
      this(m, 0.75F);
   }

   public Float2LongOpenHashMap(float[] k, long[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Float2LongOpenHashMap(float[] k, long[] v) {
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
   public void putAll(Map<? extends Float, ? extends Long> m) {
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

   private void insert(int pos, float k, long v) {
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
   public long put(float k, long v) {
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

   public long addTo(float k, long incr) {
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
               continue label30;
            }
         }

         key[last] = 0.0F;
         return;
      }
   }

   @Override
   public long remove(float k) {
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

   @Override
   public long get(float k) {
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
   public boolean containsValue(long v) {
      long[] value = this.value;
      float[] key = this.key;
      if (this.containsNullKey && value[this.n] == v) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (Float.floatToIntBits(key[i]) != 0 && value[i] == v) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public long getOrDefault(float k, long defaultValue) {
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
   public long putIfAbsent(float k, long v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(float k, long v) {
      if (Float.floatToIntBits(k) == 0) {
         if (this.containsNullKey && v == this.value[this.n]) {
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
         } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(k) == Float.floatToIntBits(curr) && v == this.value[pos]) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(float k, long oldValue, long v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public long replace(float k, long v) {
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
   public long computeIfAbsent(float k, DoubleToLongFunction mappingFunction) {
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
   public long computeIfAbsent(float key, Float2LongFunction mappingFunction) {
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
   public long computeIfAbsentNullable(float k, DoubleFunction<? extends Long> mappingFunction) {
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
   public long computeIfPresent(float k, BiFunction<? super Float, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Long newValue = remappingFunction.apply(k, this.value[pos]);
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
   public long compute(float k, BiFunction<? super Float, ? super Long, ? extends Long> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Long newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
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
   public long merge(float k, long v, BiFunction<? super Long, ? super Long, ? extends Long> remappingFunction) {
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

   public Float2LongMap.FastEntrySet float2LongEntrySet() {
      if (this.entries == null) {
         this.entries = new Float2LongOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public FloatSet keySet() {
      if (this.keys == null) {
         this.keys = new Float2LongOpenHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public LongCollection values() {
      if (this.values == null) {
         this.values = new AbstractLongCollection() {
            @Override
            public LongIterator iterator() {
               return Float2LongOpenHashMap.this.new ValueIterator();
            }

            @Override
            public LongSpliterator spliterator() {
               return Float2LongOpenHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(LongConsumer consumer) {
               if (Float2LongOpenHashMap.this.containsNullKey) {
                  consumer.accept(Float2LongOpenHashMap.this.value[Float2LongOpenHashMap.this.n]);
               }

               int pos = Float2LongOpenHashMap.this.n;

               while (pos-- != 0) {
                  if (Float.floatToIntBits(Float2LongOpenHashMap.this.key[pos]) != 0) {
                     consumer.accept(Float2LongOpenHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Float2LongOpenHashMap.this.size;
            }

            @Override
            public boolean contains(long v) {
               return Float2LongOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Float2LongOpenHashMap.this.clear();
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
      long[] value = this.value;
      int mask = newN - 1;
      float[] newKey = new float[newN + 1];
      long[] newValue = new long[newN + 1];
      int i = this.n;
      int j = this.realSize();

      while (j-- != 0) {
         while (Float.floatToIntBits(key[--i]) == 0) {
         }

         int pos;
         if (Float.floatToIntBits(newKey[pos = HashCommon.mix(HashCommon.float2int(key[i])) & mask]) != 0) {
            while (Float.floatToIntBits(newKey[pos = pos + 1 & mask]) != 0) {
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

   public Float2LongOpenHashMap clone() {
      Float2LongOpenHashMap c;
      try {
         c = (Float2LongOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (float[])this.key.clone();
      c.value = (long[])this.value.clone();
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
         t ^= HashCommon.long2int(this.value[i]);
         h += t;
      }

      if (this.containsNullKey) {
         h += HashCommon.long2int(this.value[this.n]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      float[] key = this.key;
      long[] value = this.value;
      Float2LongOpenHashMap.EntryIterator i = new Float2LongOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeFloat(key[e]);
         s.writeLong(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      float[] key = this.key = new float[this.n + 1];
      long[] value = this.value = new long[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         float k = s.readFloat();
         long v = s.readLong();
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
      }
   }

   private void checkTable() {
   }

   private final class EntryIterator
      extends Float2LongOpenHashMap.MapIterator<Consumer<? super Float2LongMap.Entry>>
      implements ObjectIterator<Float2LongMap.Entry> {
      private Float2LongOpenHashMap.MapEntry entry;

      private EntryIterator() {
      }

      public Float2LongOpenHashMap.MapEntry next() {
         return this.entry = Float2LongOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Float2LongMap.Entry> action, int index) {
         action.accept(this.entry = Float2LongOpenHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Float2LongOpenHashMap.MapSpliterator<Consumer<? super Float2LongMap.Entry>, Float2LongOpenHashMap.EntrySpliterator>
      implements ObjectSpliterator<Float2LongMap.Entry> {
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

      final void acceptOnIndex(Consumer<? super Float2LongMap.Entry> action, int index) {
         action.accept(Float2LongOpenHashMap.this.new MapEntry(index));
      }

      final Float2LongOpenHashMap.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Float2LongOpenHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Float2LongOpenHashMap.MapIterator<Consumer<? super Float2LongMap.Entry>>
      implements ObjectIterator<Float2LongMap.Entry> {
      private final Float2LongOpenHashMap.MapEntry entry = Float2LongOpenHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Float2LongOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Float2LongMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Float2LongOpenHashMap.MapIterator<FloatConsumer> implements FloatIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(FloatConsumer action, int index) {
         action.accept(Float2LongOpenHashMap.this.key[index]);
      }

      @Override
      public float nextFloat() {
         return Float2LongOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractFloatSet {
      private KeySet() {
      }

      @Override
      public FloatIterator iterator() {
         return Float2LongOpenHashMap.this.new KeyIterator();
      }

      @Override
      public FloatSpliterator spliterator() {
         return Float2LongOpenHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(FloatConsumer consumer) {
         if (Float2LongOpenHashMap.this.containsNullKey) {
            consumer.accept(Float2LongOpenHashMap.this.key[Float2LongOpenHashMap.this.n]);
         }

         int pos = Float2LongOpenHashMap.this.n;

         while (pos-- != 0) {
            float k = Float2LongOpenHashMap.this.key[pos];
            if (Float.floatToIntBits(k) != 0) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Float2LongOpenHashMap.this.size;
      }

      @Override
      public boolean contains(float k) {
         return Float2LongOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(float k) {
         int oldSize = Float2LongOpenHashMap.this.size;
         Float2LongOpenHashMap.this.remove(k);
         return Float2LongOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Float2LongOpenHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Float2LongOpenHashMap.MapSpliterator<FloatConsumer, Float2LongOpenHashMap.KeySpliterator>
      implements FloatSpliterator {
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

      final void acceptOnIndex(FloatConsumer action, int index) {
         action.accept(Float2LongOpenHashMap.this.key[index]);
      }

      final Float2LongOpenHashMap.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Float2LongOpenHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Float2LongMap.Entry, Entry<Float, Long>, FloatLongPair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public float getFloatKey() {
         return Float2LongOpenHashMap.this.key[this.index];
      }

      @Override
      public float leftFloat() {
         return Float2LongOpenHashMap.this.key[this.index];
      }

      @Override
      public long getLongValue() {
         return Float2LongOpenHashMap.this.value[this.index];
      }

      @Override
      public long rightLong() {
         return Float2LongOpenHashMap.this.value[this.index];
      }

      @Override
      public long setValue(long v) {
         long oldValue = Float2LongOpenHashMap.this.value[this.index];
         Float2LongOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public FloatLongPair right(long v) {
         Float2LongOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Float getKey() {
         return Float2LongOpenHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Long getValue() {
         return Float2LongOpenHashMap.this.value[this.index];
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
            Entry<Float, Long> e = (Entry<Float, Long>)o;
            return Float.floatToIntBits(Float2LongOpenHashMap.this.key[this.index]) == Float.floatToIntBits(e.getKey())
               && Float2LongOpenHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.float2int(Float2LongOpenHashMap.this.key[this.index]) ^ HashCommon.long2int(Float2LongOpenHashMap.this.value[this.index]);
      }

      @Override
      public String toString() {
         return Float2LongOpenHashMap.this.key[this.index] + "=>" + Float2LongOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Float2LongMap.Entry> implements Float2LongMap.FastEntrySet {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Float2LongMap.Entry> iterator() {
         return Float2LongOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Float2LongMap.Entry> fastIterator() {
         return Float2LongOpenHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Float2LongMap.Entry> spliterator() {
         return Float2LongOpenHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Long) {
               float k = (Float)e.getKey();
               long v = (Long)e.getValue();
               if (Float.floatToIntBits(k) == 0) {
                  return Float2LongOpenHashMap.this.containsNullKey && Float2LongOpenHashMap.this.value[Float2LongOpenHashMap.this.n] == v;
               } else {
                  float[] key = Float2LongOpenHashMap.this.key;
                  float curr;
                  int pos;
                  if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & Float2LongOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                     return Float2LongOpenHashMap.this.value[pos] == v;
                  } else {
                     while (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2LongOpenHashMap.this.mask]) != 0) {
                        if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                           return Float2LongOpenHashMap.this.value[pos] == v;
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
               if (e.getValue() != null && e.getValue() instanceof Long) {
                  float k = (Float)e.getKey();
                  long v = (Long)e.getValue();
                  if (Float.floatToIntBits(k) == 0) {
                     if (Float2LongOpenHashMap.this.containsNullKey && Float2LongOpenHashMap.this.value[Float2LongOpenHashMap.this.n] == v) {
                        Float2LongOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     float[] key = Float2LongOpenHashMap.this.key;
                     float curr;
                     int pos;
                     if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & Float2LongOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
                        if (Float2LongOpenHashMap.this.value[pos] == v) {
                           Float2LongOpenHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2LongOpenHashMap.this.mask]) != 0) {
                           if (Float.floatToIntBits(curr) == Float.floatToIntBits(k) && Float2LongOpenHashMap.this.value[pos] == v) {
                              Float2LongOpenHashMap.this.removeEntry(pos);
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
         return Float2LongOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Float2LongOpenHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Float2LongMap.Entry> consumer) {
         if (Float2LongOpenHashMap.this.containsNullKey) {
            consumer.accept(Float2LongOpenHashMap.this.new MapEntry(Float2LongOpenHashMap.this.n));
         }

         int pos = Float2LongOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Float.floatToIntBits(Float2LongOpenHashMap.this.key[pos]) != 0) {
               consumer.accept(Float2LongOpenHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Float2LongMap.Entry> consumer) {
         Float2LongOpenHashMap.MapEntry entry = Float2LongOpenHashMap.this.new MapEntry();
         if (Float2LongOpenHashMap.this.containsNullKey) {
            entry.index = Float2LongOpenHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Float2LongOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Float.floatToIntBits(Float2LongOpenHashMap.this.key[pos]) != 0) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Float2LongOpenHashMap.this.n;
      int last = -1;
      int c = Float2LongOpenHashMap.this.size;
      boolean mustReturnNullKey = Float2LongOpenHashMap.this.containsNullKey;
      FloatArrayList wrapped;

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
               return this.last = Float2LongOpenHashMap.this.n;
            } else {
               float[] key = Float2LongOpenHashMap.this.key;

               while (--this.pos >= 0) {
                  if (Float.floatToIntBits(key[this.pos]) != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               float k = this.wrapped.getFloat(-this.pos - 1);
               int p = HashCommon.mix(HashCommon.float2int(k)) & Float2LongOpenHashMap.this.mask;

               while (Float.floatToIntBits(k) != Float.floatToIntBits(key[p])) {
                  p = p + 1 & Float2LongOpenHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Float2LongOpenHashMap.this.n);
            this.c--;
         }

         float[] key = Float2LongOpenHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               float k = this.wrapped.getFloat(-this.pos - 1);
               int p = HashCommon.mix(HashCommon.float2int(k)) & Float2LongOpenHashMap.this.mask;

               while (Float.floatToIntBits(k) != Float.floatToIntBits(key[p])) {
                  p = p + 1 & Float2LongOpenHashMap.this.mask;
               }

               this.acceptOnIndex(action, p);
               this.c--;
            } else if (Float.floatToIntBits(key[this.pos]) != 0) {
               this.acceptOnIndex(action, this.last = this.pos);
               this.c--;
            }
         }
      }

      private void shiftKeys(int pos) {
         float[] key = Float2LongOpenHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            float curr;
            for (pos = pos + 1 & Float2LongOpenHashMap.this.mask; Float.floatToIntBits(curr = key[pos]) != 0; pos = pos + 1 & Float2LongOpenHashMap.this.mask) {
               int slot = HashCommon.mix(HashCommon.float2int(curr)) & Float2LongOpenHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new FloatArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Float2LongOpenHashMap.this.value[last] = Float2LongOpenHashMap.this.value[pos];
                  continue label38;
               }
            }

            key[last] = 0.0F;
            return;
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Float2LongOpenHashMap.this.n) {
               Float2LongOpenHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Float2LongOpenHashMap.this.remove(this.wrapped.getFloat(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Float2LongOpenHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Float2LongOpenHashMap.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Float2LongOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Float2LongOpenHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Float2LongOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Float2LongOpenHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Float2LongOpenHashMap.this.n);
            return true;
         } else {
            for (float[] key = Float2LongOpenHashMap.this.key; this.pos < this.max; this.pos++) {
               if (Float.floatToIntBits(key[this.pos]) != 0) {
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
            this.acceptOnIndex(action, Float2LongOpenHashMap.this.n);
         }

         for (float[] key = Float2LongOpenHashMap.this.key; this.pos < this.max; this.pos++) {
            if (Float.floatToIntBits(key[this.pos]) != 0) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Float2LongOpenHashMap.this.size - this.c
            : Math.min(
               (long)(Float2LongOpenHashMap.this.size - this.c),
               (long)((double)Float2LongOpenHashMap.this.realSize() / Float2LongOpenHashMap.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
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

            float[] key = Float2LongOpenHashMap.this.key;

            while (this.pos < this.max && n > 0L) {
               if (Float.floatToIntBits(key[this.pos++]) != 0) {
                  skipped++;
                  n--;
               }
            }

            return skipped;
         }
      }
   }

   private final class ValueIterator extends Float2LongOpenHashMap.MapIterator<LongConsumer> implements LongIterator {
      public ValueIterator() {
      }

      final void acceptOnIndex(LongConsumer action, int index) {
         action.accept(Float2LongOpenHashMap.this.value[index]);
      }

      @Override
      public long nextLong() {
         return Float2LongOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Float2LongOpenHashMap.MapSpliterator<LongConsumer, Float2LongOpenHashMap.ValueSpliterator>
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
         action.accept(Float2LongOpenHashMap.this.value[index]);
      }

      final Float2LongOpenHashMap.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Float2LongOpenHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
