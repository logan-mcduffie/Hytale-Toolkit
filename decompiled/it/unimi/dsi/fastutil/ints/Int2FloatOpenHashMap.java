package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.floats.AbstractFloatCollection;
import it.unimi.dsi.fastutil.floats.FloatCollection;
import it.unimi.dsi.fastutil.floats.FloatConsumer;
import it.unimi.dsi.fastutil.floats.FloatIterator;
import it.unimi.dsi.fastutil.floats.FloatSpliterator;
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
import java.util.function.IntToDoubleFunction;

public class Int2FloatOpenHashMap extends AbstractInt2FloatMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient int[] key;
   protected transient float[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Int2FloatMap.FastEntrySet entries;
   protected transient IntSet keys;
   protected transient FloatCollection values;

   public Int2FloatOpenHashMap(int expected, float f) {
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
         this.value = new float[this.n + 1];
      }
   }

   public Int2FloatOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Int2FloatOpenHashMap() {
      this(16, 0.75F);
   }

   public Int2FloatOpenHashMap(Map<? extends Integer, ? extends Float> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Int2FloatOpenHashMap(Map<? extends Integer, ? extends Float> m) {
      this(m, 0.75F);
   }

   public Int2FloatOpenHashMap(Int2FloatMap m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Int2FloatOpenHashMap(Int2FloatMap m) {
      this(m, 0.75F);
   }

   public Int2FloatOpenHashMap(int[] k, float[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Int2FloatOpenHashMap(int[] k, float[] v) {
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

   private float removeEntry(int pos) {
      float oldValue = this.value[pos];
      this.size--;
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private float removeNullEntry() {
      this.containsNullKey = false;
      float oldValue = this.value[this.n];
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends Integer, ? extends Float> m) {
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

   private void insert(int pos, int k, float v) {
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
   public float put(int k, float v) {
      int pos = this.find(k);
      if (pos < 0) {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      } else {
         float oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   private float addToValue(int pos, float incr) {
      float oldValue = this.value[pos];
      this.value[pos] = oldValue + incr;
      return oldValue;
   }

   public float addTo(int k, float incr) {
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
               continue label30;
            }
         }

         key[last] = 0;
         return;
      }
   }

   @Override
   public float remove(int k) {
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

   @Override
   public float get(int k) {
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
   public boolean containsValue(float v) {
      float[] value = this.value;
      int[] key = this.key;
      if (this.containsNullKey && Float.floatToIntBits(value[this.n]) == Float.floatToIntBits(v)) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (key[i] != 0 && Float.floatToIntBits(value[i]) == Float.floatToIntBits(v)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public float getOrDefault(int k, float defaultValue) {
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
   public float putIfAbsent(int k, float v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(int k, float v) {
      if (k == 0) {
         if (this.containsNullKey && Float.floatToIntBits(v) == Float.floatToIntBits(this.value[this.n])) {
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
         } else if (k == curr && Float.floatToIntBits(v) == Float.floatToIntBits(this.value[pos])) {
            this.removeEntry(pos);
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (k == curr && Float.floatToIntBits(v) == Float.floatToIntBits(this.value[pos])) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(int k, float oldValue, float v) {
      int pos = this.find(k);
      if (pos >= 0 && Float.floatToIntBits(oldValue) == Float.floatToIntBits(this.value[pos])) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public float replace(int k, float v) {
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         float oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   @Override
   public float computeIfAbsent(int k, IntToDoubleFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         float newValue = SafeMath.safeDoubleToFloat(mappingFunction.applyAsDouble(k));
         this.insert(-pos - 1, k, newValue);
         return newValue;
      }
   }

   @Override
   public float computeIfAbsent(int key, Int2FloatFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         float newValue = mappingFunction.get(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public float computeIfAbsentNullable(int k, IntFunction<? extends Float> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         Float newValue = mappingFunction.apply(k);
         if (newValue == null) {
            return this.defRetValue;
         } else {
            float v = newValue;
            this.insert(-pos - 1, k, v);
            return v;
         }
      }
   }

   @Override
   public float computeIfPresent(int k, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Float newValue = remappingFunction.apply(k, this.value[pos]);
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
   public float compute(int k, BiFunction<? super Integer, ? super Float, ? extends Float> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Float newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
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
         float newVal = newValue;
         if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
         } else {
            return this.value[pos] = newVal;
         }
      }
   }

   @Override
   public float merge(int k, float v, BiFunction<? super Float, ? super Float, ? extends Float> remappingFunction) {
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
         Float newValue = remappingFunction.apply(this.value[pos], v);
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

   public Int2FloatMap.FastEntrySet int2FloatEntrySet() {
      if (this.entries == null) {
         this.entries = new Int2FloatOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public IntSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2FloatOpenHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public FloatCollection values() {
      if (this.values == null) {
         this.values = new AbstractFloatCollection() {
            @Override
            public FloatIterator iterator() {
               return Int2FloatOpenHashMap.this.new ValueIterator();
            }

            @Override
            public FloatSpliterator spliterator() {
               return Int2FloatOpenHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(FloatConsumer consumer) {
               if (Int2FloatOpenHashMap.this.containsNullKey) {
                  consumer.accept(Int2FloatOpenHashMap.this.value[Int2FloatOpenHashMap.this.n]);
               }

               int pos = Int2FloatOpenHashMap.this.n;

               while (pos-- != 0) {
                  if (Int2FloatOpenHashMap.this.key[pos] != 0) {
                     consumer.accept(Int2FloatOpenHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Int2FloatOpenHashMap.this.size;
            }

            @Override
            public boolean contains(float v) {
               return Int2FloatOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Int2FloatOpenHashMap.this.clear();
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
      float[] value = this.value;
      int mask = newN - 1;
      int[] newKey = new int[newN + 1];
      float[] newValue = new float[newN + 1];
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

   public Int2FloatOpenHashMap clone() {
      Int2FloatOpenHashMap c;
      try {
         c = (Int2FloatOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (int[])this.key.clone();
      c.value = (float[])this.value.clone();
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
         t ^= HashCommon.float2int(this.value[i]);
         h += t;
      }

      if (this.containsNullKey) {
         h += HashCommon.float2int(this.value[this.n]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int[] key = this.key;
      float[] value = this.value;
      Int2FloatOpenHashMap.EntryIterator i = new Int2FloatOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeInt(key[e]);
         s.writeFloat(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      int[] key = this.key = new int[this.n + 1];
      float[] value = this.value = new float[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         int k = s.readInt();
         float v = s.readFloat();
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
      }
   }

   private void checkTable() {
   }

   private final class EntryIterator
      extends Int2FloatOpenHashMap.MapIterator<Consumer<? super Int2FloatMap.Entry>>
      implements ObjectIterator<Int2FloatMap.Entry> {
      private Int2FloatOpenHashMap.MapEntry entry;

      private EntryIterator() {
      }

      public Int2FloatOpenHashMap.MapEntry next() {
         return this.entry = Int2FloatOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Int2FloatMap.Entry> action, int index) {
         action.accept(this.entry = Int2FloatOpenHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Int2FloatOpenHashMap.MapSpliterator<Consumer<? super Int2FloatMap.Entry>, Int2FloatOpenHashMap.EntrySpliterator>
      implements ObjectSpliterator<Int2FloatMap.Entry> {
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

      final void acceptOnIndex(Consumer<? super Int2FloatMap.Entry> action, int index) {
         action.accept(Int2FloatOpenHashMap.this.new MapEntry(index));
      }

      final Int2FloatOpenHashMap.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Int2FloatOpenHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Int2FloatOpenHashMap.MapIterator<Consumer<? super Int2FloatMap.Entry>>
      implements ObjectIterator<Int2FloatMap.Entry> {
      private final Int2FloatOpenHashMap.MapEntry entry = Int2FloatOpenHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Int2FloatOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Int2FloatMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Int2FloatOpenHashMap.MapIterator<java.util.function.IntConsumer> implements IntIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(java.util.function.IntConsumer action, int index) {
         action.accept(Int2FloatOpenHashMap.this.key[index]);
      }

      @Override
      public int nextInt() {
         return Int2FloatOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractIntSet {
      private KeySet() {
      }

      @Override
      public IntIterator iterator() {
         return Int2FloatOpenHashMap.this.new KeyIterator();
      }

      @Override
      public IntSpliterator spliterator() {
         return Int2FloatOpenHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(java.util.function.IntConsumer consumer) {
         if (Int2FloatOpenHashMap.this.containsNullKey) {
            consumer.accept(Int2FloatOpenHashMap.this.key[Int2FloatOpenHashMap.this.n]);
         }

         int pos = Int2FloatOpenHashMap.this.n;

         while (pos-- != 0) {
            int k = Int2FloatOpenHashMap.this.key[pos];
            if (k != 0) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Int2FloatOpenHashMap.this.size;
      }

      @Override
      public boolean contains(int k) {
         return Int2FloatOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(int k) {
         int oldSize = Int2FloatOpenHashMap.this.size;
         Int2FloatOpenHashMap.this.remove(k);
         return Int2FloatOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Int2FloatOpenHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Int2FloatOpenHashMap.MapSpliterator<java.util.function.IntConsumer, Int2FloatOpenHashMap.KeySpliterator>
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
         action.accept(Int2FloatOpenHashMap.this.key[index]);
      }

      final Int2FloatOpenHashMap.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Int2FloatOpenHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Int2FloatMap.Entry, Entry<Integer, Float>, IntFloatPair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public int getIntKey() {
         return Int2FloatOpenHashMap.this.key[this.index];
      }

      @Override
      public int leftInt() {
         return Int2FloatOpenHashMap.this.key[this.index];
      }

      @Override
      public float getFloatValue() {
         return Int2FloatOpenHashMap.this.value[this.index];
      }

      @Override
      public float rightFloat() {
         return Int2FloatOpenHashMap.this.value[this.index];
      }

      @Override
      public float setValue(float v) {
         float oldValue = Int2FloatOpenHashMap.this.value[this.index];
         Int2FloatOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public IntFloatPair right(float v) {
         Int2FloatOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Integer getKey() {
         return Int2FloatOpenHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Float getValue() {
         return Int2FloatOpenHashMap.this.value[this.index];
      }

      @Deprecated
      @Override
      public Float setValue(Float v) {
         return this.setValue(v.floatValue());
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Integer, Float> e = (Entry<Integer, Float>)o;
            return Int2FloatOpenHashMap.this.key[this.index] == e.getKey()
               && Float.floatToIntBits(Int2FloatOpenHashMap.this.value[this.index]) == Float.floatToIntBits(e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return Int2FloatOpenHashMap.this.key[this.index] ^ HashCommon.float2int(Int2FloatOpenHashMap.this.value[this.index]);
      }

      @Override
      public String toString() {
         return Int2FloatOpenHashMap.this.key[this.index] + "=>" + Int2FloatOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Int2FloatMap.Entry> implements Int2FloatMap.FastEntrySet {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Int2FloatMap.Entry> iterator() {
         return Int2FloatOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Int2FloatMap.Entry> fastIterator() {
         return Int2FloatOpenHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Int2FloatMap.Entry> spliterator() {
         return Int2FloatOpenHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Float) {
               int k = (Integer)e.getKey();
               float v = (Float)e.getValue();
               if (k == 0) {
                  return Int2FloatOpenHashMap.this.containsNullKey
                     && Float.floatToIntBits(Int2FloatOpenHashMap.this.value[Int2FloatOpenHashMap.this.n]) == Float.floatToIntBits(v);
               } else {
                  int[] key = Int2FloatOpenHashMap.this.key;
                  int curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(k) & Int2FloatOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (k == curr) {
                     return Float.floatToIntBits(Int2FloatOpenHashMap.this.value[pos]) == Float.floatToIntBits(v);
                  } else {
                     while ((curr = key[pos = pos + 1 & Int2FloatOpenHashMap.this.mask]) != 0) {
                        if (k == curr) {
                           return Float.floatToIntBits(Int2FloatOpenHashMap.this.value[pos]) == Float.floatToIntBits(v);
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
               if (e.getValue() != null && e.getValue() instanceof Float) {
                  int k = (Integer)e.getKey();
                  float v = (Float)e.getValue();
                  if (k == 0) {
                     if (Int2FloatOpenHashMap.this.containsNullKey
                        && Float.floatToIntBits(Int2FloatOpenHashMap.this.value[Int2FloatOpenHashMap.this.n]) == Float.floatToIntBits(v)) {
                        Int2FloatOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     int[] key = Int2FloatOpenHashMap.this.key;
                     int curr;
                     int pos;
                     if ((curr = key[pos = HashCommon.mix(k) & Int2FloatOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (curr == k) {
                        if (Float.floatToIntBits(Int2FloatOpenHashMap.this.value[pos]) == Float.floatToIntBits(v)) {
                           Int2FloatOpenHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while ((curr = key[pos = pos + 1 & Int2FloatOpenHashMap.this.mask]) != 0) {
                           if (curr == k && Float.floatToIntBits(Int2FloatOpenHashMap.this.value[pos]) == Float.floatToIntBits(v)) {
                              Int2FloatOpenHashMap.this.removeEntry(pos);
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
         return Int2FloatOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Int2FloatOpenHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Int2FloatMap.Entry> consumer) {
         if (Int2FloatOpenHashMap.this.containsNullKey) {
            consumer.accept(Int2FloatOpenHashMap.this.new MapEntry(Int2FloatOpenHashMap.this.n));
         }

         int pos = Int2FloatOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Int2FloatOpenHashMap.this.key[pos] != 0) {
               consumer.accept(Int2FloatOpenHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Int2FloatMap.Entry> consumer) {
         Int2FloatOpenHashMap.MapEntry entry = Int2FloatOpenHashMap.this.new MapEntry();
         if (Int2FloatOpenHashMap.this.containsNullKey) {
            entry.index = Int2FloatOpenHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Int2FloatOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Int2FloatOpenHashMap.this.key[pos] != 0) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Int2FloatOpenHashMap.this.n;
      int last = -1;
      int c = Int2FloatOpenHashMap.this.size;
      boolean mustReturnNullKey = Int2FloatOpenHashMap.this.containsNullKey;
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
               return this.last = Int2FloatOpenHashMap.this.n;
            } else {
               int[] key = Int2FloatOpenHashMap.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               int k = this.wrapped.getInt(-this.pos - 1);
               int p = HashCommon.mix(k) & Int2FloatOpenHashMap.this.mask;

               while (k != key[p]) {
                  p = p + 1 & Int2FloatOpenHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Int2FloatOpenHashMap.this.n);
            this.c--;
         }

         int[] key = Int2FloatOpenHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               int k = this.wrapped.getInt(-this.pos - 1);
               int p = HashCommon.mix(k) & Int2FloatOpenHashMap.this.mask;

               while (k != key[p]) {
                  p = p + 1 & Int2FloatOpenHashMap.this.mask;
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
         int[] key = Int2FloatOpenHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            int curr;
            for (pos = pos + 1 & Int2FloatOpenHashMap.this.mask; (curr = key[pos]) != 0; pos = pos + 1 & Int2FloatOpenHashMap.this.mask) {
               int slot = HashCommon.mix(curr) & Int2FloatOpenHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new IntArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Int2FloatOpenHashMap.this.value[last] = Int2FloatOpenHashMap.this.value[pos];
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
            if (this.last == Int2FloatOpenHashMap.this.n) {
               Int2FloatOpenHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Int2FloatOpenHashMap.this.remove(this.wrapped.getInt(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Int2FloatOpenHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Int2FloatOpenHashMap.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Int2FloatOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Int2FloatOpenHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Int2FloatOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Int2FloatOpenHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Int2FloatOpenHashMap.this.n);
            return true;
         } else {
            for (int[] key = Int2FloatOpenHashMap.this.key; this.pos < this.max; this.pos++) {
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
            this.acceptOnIndex(action, Int2FloatOpenHashMap.this.n);
         }

         for (int[] key = Int2FloatOpenHashMap.this.key; this.pos < this.max; this.pos++) {
            if (key[this.pos] != 0) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Int2FloatOpenHashMap.this.size - this.c
            : Math.min(
               (long)(Int2FloatOpenHashMap.this.size - this.c),
               (long)((double)Int2FloatOpenHashMap.this.realSize() / Int2FloatOpenHashMap.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
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

            int[] key = Int2FloatOpenHashMap.this.key;

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

   private final class ValueIterator extends Int2FloatOpenHashMap.MapIterator<FloatConsumer> implements FloatIterator {
      public ValueIterator() {
      }

      final void acceptOnIndex(FloatConsumer action, int index) {
         action.accept(Int2FloatOpenHashMap.this.value[index]);
      }

      @Override
      public float nextFloat() {
         return Int2FloatOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Int2FloatOpenHashMap.MapSpliterator<FloatConsumer, Int2FloatOpenHashMap.ValueSpliterator>
      implements FloatSpliterator {
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

      final void acceptOnIndex(FloatConsumer action, int index) {
         action.accept(Int2FloatOpenHashMap.this.value[index]);
      }

      final Int2FloatOpenHashMap.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Int2FloatOpenHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
