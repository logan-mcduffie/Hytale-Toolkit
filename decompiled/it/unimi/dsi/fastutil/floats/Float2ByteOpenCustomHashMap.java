package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.AbstractByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteCollection;
import it.unimi.dsi.fastutil.bytes.ByteConsumer;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
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
import java.util.function.DoubleToIntFunction;

public class Float2ByteOpenCustomHashMap extends AbstractFloat2ByteMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient float[] key;
   protected transient byte[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected FloatHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Float2ByteMap.FastEntrySet entries;
   protected transient FloatSet keys;
   protected transient ByteCollection values;

   public Float2ByteOpenCustomHashMap(int expected, float f, FloatHash.Strategy strategy) {
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
         this.key = new float[this.n + 1];
         this.value = new byte[this.n + 1];
      }
   }

   public Float2ByteOpenCustomHashMap(int expected, FloatHash.Strategy strategy) {
      this(expected, 0.75F, strategy);
   }

   public Float2ByteOpenCustomHashMap(FloatHash.Strategy strategy) {
      this(16, 0.75F, strategy);
   }

   public Float2ByteOpenCustomHashMap(Map<? extends Float, ? extends Byte> m, float f, FloatHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Float2ByteOpenCustomHashMap(Map<? extends Float, ? extends Byte> m, FloatHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Float2ByteOpenCustomHashMap(Float2ByteMap m, float f, FloatHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Float2ByteOpenCustomHashMap(Float2ByteMap m, FloatHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Float2ByteOpenCustomHashMap(float[] k, byte[] v, float f, FloatHash.Strategy strategy) {
      this(k.length, f, strategy);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Float2ByteOpenCustomHashMap(float[] k, byte[] v, FloatHash.Strategy strategy) {
      this(k, v, 0.75F, strategy);
   }

   public FloatHash.Strategy strategy() {
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

   private byte removeEntry(int pos) {
      byte oldValue = this.value[pos];
      this.size--;
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private byte removeNullEntry() {
      this.containsNullKey = false;
      byte oldValue = this.value[this.n];
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends Float, ? extends Byte> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(float k) {
      if (this.strategy.equals(k, 0.0F)) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return -(pos + 1);
         } else if (this.strategy.equals(k, curr)) {
            return pos;
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr)) {
                  return pos;
               }
            }

            return -(pos + 1);
         }
      }
   }

   private void insert(int pos, float k, byte v) {
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
   public byte put(float k, byte v) {
      int pos = this.find(k);
      if (pos < 0) {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      } else {
         byte oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   private byte addToValue(int pos, byte incr) {
      byte oldValue = this.value[pos];
      this.value[pos] = (byte)(oldValue + incr);
      return oldValue;
   }

   public byte addTo(float k, byte incr) {
      int pos;
      if (this.strategy.equals(k, 0.0F)) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, incr);
         }

         pos = this.n;
         this.containsNullKey = true;
      } else {
         float[] key = this.key;
         float curr;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) != 0) {
            if (this.strategy.equals(curr, k)) {
               return this.addToValue(pos, incr);
            }

            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(curr, k)) {
                  return this.addToValue(pos, incr);
               }
            }
         }
      }

      this.key[pos] = k;
      this.value[pos] = (byte)(this.defRetValue + incr);
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
            int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
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
   public byte remove(float k) {
      if (this.strategy.equals(k, 0.0F)) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr)) {
                  return this.removeEntry(pos);
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public byte get(float k) {
      if (this.strategy.equals(k, 0.0F)) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (this.strategy.equals(k, curr)) {
            return this.value[pos];
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr)) {
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public boolean containsKey(float k) {
      if (this.strategy.equals(k, 0.0F)) {
         return this.containsNullKey;
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(k, curr)) {
            return true;
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean containsValue(byte v) {
      byte[] value = this.value;
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
   public byte getOrDefault(float k, byte defaultValue) {
      if (this.strategy.equals(k, 0.0F)) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         float[] key = this.key;
         float curr;
         int pos;
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return defaultValue;
         } else if (this.strategy.equals(k, curr)) {
            return this.value[pos];
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr)) {
                  return this.value[pos];
               }
            }

            return defaultValue;
         }
      }
   }

   @Override
   public byte putIfAbsent(float k, byte v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(float k, byte v) {
      if (this.strategy.equals(k, 0.0F)) {
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
         if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(k, curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
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
   public boolean replace(float k, byte oldValue, byte v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public byte replace(float k, byte v) {
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         byte oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   @Override
   public byte computeIfAbsent(float k, DoubleToIntFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         byte newValue = SafeMath.safeIntToByte(mappingFunction.applyAsInt(k));
         this.insert(-pos - 1, k, newValue);
         return newValue;
      }
   }

   @Override
   public byte computeIfAbsent(float key, Float2ByteFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         byte newValue = mappingFunction.get(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public byte computeIfAbsentNullable(float k, DoubleFunction<? extends Byte> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         Byte newValue = mappingFunction.apply(k);
         if (newValue == null) {
            return this.defRetValue;
         } else {
            byte v = newValue;
            this.insert(-pos - 1, k, v);
            return v;
         }
      }
   }

   @Override
   public byte computeIfPresent(float k, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Byte newValue = remappingFunction.apply(k, this.value[pos]);
         if (newValue == null) {
            if (this.strategy.equals(k, 0.0F)) {
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
   public byte compute(float k, BiFunction<? super Float, ? super Byte, ? extends Byte> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Byte newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (this.strategy.equals(k, 0.0F)) {
               this.removeNullEntry();
            } else {
               this.removeEntry(pos);
            }
         }

         return this.defRetValue;
      } else {
         byte newVal = newValue;
         if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
         } else {
            return this.value[pos] = newVal;
         }
      }
   }

   @Override
   public byte merge(float k, byte v, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
         Byte newValue = remappingFunction.apply(this.value[pos], v);
         if (newValue == null) {
            if (this.strategy.equals(k, 0.0F)) {
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

   public Float2ByteMap.FastEntrySet float2ByteEntrySet() {
      if (this.entries == null) {
         this.entries = new Float2ByteOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public FloatSet keySet() {
      if (this.keys == null) {
         this.keys = new Float2ByteOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ByteCollection values() {
      if (this.values == null) {
         this.values = new AbstractByteCollection() {
            @Override
            public ByteIterator iterator() {
               return Float2ByteOpenCustomHashMap.this.new ValueIterator();
            }

            @Override
            public ByteSpliterator spliterator() {
               return Float2ByteOpenCustomHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(ByteConsumer consumer) {
               if (Float2ByteOpenCustomHashMap.this.containsNullKey) {
                  consumer.accept(Float2ByteOpenCustomHashMap.this.value[Float2ByteOpenCustomHashMap.this.n]);
               }

               int pos = Float2ByteOpenCustomHashMap.this.n;

               while (pos-- != 0) {
                  if (Float.floatToIntBits(Float2ByteOpenCustomHashMap.this.key[pos]) != 0) {
                     consumer.accept(Float2ByteOpenCustomHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Float2ByteOpenCustomHashMap.this.size;
            }

            @Override
            public boolean contains(byte v) {
               return Float2ByteOpenCustomHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Float2ByteOpenCustomHashMap.this.clear();
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
      byte[] value = this.value;
      int mask = newN - 1;
      float[] newKey = new float[newN + 1];
      byte[] newValue = new byte[newN + 1];
      int i = this.n;
      int j = this.realSize();

      while (j-- != 0) {
         while (Float.floatToIntBits(key[--i]) == 0) {
         }

         int pos;
         if (Float.floatToIntBits(newKey[pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask]) != 0) {
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

   public Float2ByteOpenCustomHashMap clone() {
      Float2ByteOpenCustomHashMap c;
      try {
         c = (Float2ByteOpenCustomHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (float[])this.key.clone();
      c.value = (byte[])this.value.clone();
      c.strategy = this.strategy;
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

         t = this.strategy.hashCode(this.key[i]);
         t ^= this.value[i];
         h += t;
      }

      if (this.containsNullKey) {
         h += this.value[this.n];
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      float[] key = this.key;
      byte[] value = this.value;
      Float2ByteOpenCustomHashMap.EntryIterator i = new Float2ByteOpenCustomHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeFloat(key[e]);
         s.writeByte(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      float[] key = this.key = new float[this.n + 1];
      byte[] value = this.value = new byte[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         float k = s.readFloat();
         byte v = s.readByte();
         int pos;
         if (this.strategy.equals(k, 0.0F)) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;

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
      extends Float2ByteOpenCustomHashMap.MapIterator<Consumer<? super Float2ByteMap.Entry>>
      implements ObjectIterator<Float2ByteMap.Entry> {
      private Float2ByteOpenCustomHashMap.MapEntry entry;

      private EntryIterator() {
      }

      public Float2ByteOpenCustomHashMap.MapEntry next() {
         return this.entry = Float2ByteOpenCustomHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Float2ByteMap.Entry> action, int index) {
         action.accept(this.entry = Float2ByteOpenCustomHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Float2ByteOpenCustomHashMap.MapSpliterator<Consumer<? super Float2ByteMap.Entry>, Float2ByteOpenCustomHashMap.EntrySpliterator>
      implements ObjectSpliterator<Float2ByteMap.Entry> {
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

      final void acceptOnIndex(Consumer<? super Float2ByteMap.Entry> action, int index) {
         action.accept(Float2ByteOpenCustomHashMap.this.new MapEntry(index));
      }

      final Float2ByteOpenCustomHashMap.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Float2ByteOpenCustomHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Float2ByteOpenCustomHashMap.MapIterator<Consumer<? super Float2ByteMap.Entry>>
      implements ObjectIterator<Float2ByteMap.Entry> {
      private final Float2ByteOpenCustomHashMap.MapEntry entry = Float2ByteOpenCustomHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Float2ByteOpenCustomHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Float2ByteMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Float2ByteOpenCustomHashMap.MapIterator<FloatConsumer> implements FloatIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(FloatConsumer action, int index) {
         action.accept(Float2ByteOpenCustomHashMap.this.key[index]);
      }

      @Override
      public float nextFloat() {
         return Float2ByteOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractFloatSet {
      private KeySet() {
      }

      @Override
      public FloatIterator iterator() {
         return Float2ByteOpenCustomHashMap.this.new KeyIterator();
      }

      @Override
      public FloatSpliterator spliterator() {
         return Float2ByteOpenCustomHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(FloatConsumer consumer) {
         if (Float2ByteOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Float2ByteOpenCustomHashMap.this.key[Float2ByteOpenCustomHashMap.this.n]);
         }

         int pos = Float2ByteOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            float k = Float2ByteOpenCustomHashMap.this.key[pos];
            if (Float.floatToIntBits(k) != 0) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Float2ByteOpenCustomHashMap.this.size;
      }

      @Override
      public boolean contains(float k) {
         return Float2ByteOpenCustomHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(float k) {
         int oldSize = Float2ByteOpenCustomHashMap.this.size;
         Float2ByteOpenCustomHashMap.this.remove(k);
         return Float2ByteOpenCustomHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Float2ByteOpenCustomHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Float2ByteOpenCustomHashMap.MapSpliterator<FloatConsumer, Float2ByteOpenCustomHashMap.KeySpliterator>
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
         action.accept(Float2ByteOpenCustomHashMap.this.key[index]);
      }

      final Float2ByteOpenCustomHashMap.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Float2ByteOpenCustomHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Float2ByteMap.Entry, Entry<Float, Byte>, FloatBytePair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public float getFloatKey() {
         return Float2ByteOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public float leftFloat() {
         return Float2ByteOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public byte getByteValue() {
         return Float2ByteOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public byte rightByte() {
         return Float2ByteOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public byte setValue(byte v) {
         byte oldValue = Float2ByteOpenCustomHashMap.this.value[this.index];
         Float2ByteOpenCustomHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public FloatBytePair right(byte v) {
         Float2ByteOpenCustomHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Float getKey() {
         return Float2ByteOpenCustomHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Byte getValue() {
         return Float2ByteOpenCustomHashMap.this.value[this.index];
      }

      @Deprecated
      @Override
      public Byte setValue(Byte v) {
         return this.setValue(v.byteValue());
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Float, Byte> e = (Entry<Float, Byte>)o;
            return Float2ByteOpenCustomHashMap.this.strategy.equals(Float2ByteOpenCustomHashMap.this.key[this.index], e.getKey())
               && Float2ByteOpenCustomHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return Float2ByteOpenCustomHashMap.this.strategy.hashCode(Float2ByteOpenCustomHashMap.this.key[this.index])
            ^ Float2ByteOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public String toString() {
         return Float2ByteOpenCustomHashMap.this.key[this.index] + "=>" + Float2ByteOpenCustomHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Float2ByteMap.Entry> implements Float2ByteMap.FastEntrySet {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Float2ByteMap.Entry> iterator() {
         return Float2ByteOpenCustomHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Float2ByteMap.Entry> fastIterator() {
         return Float2ByteOpenCustomHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Float2ByteMap.Entry> spliterator() {
         return Float2ByteOpenCustomHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Float)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Byte) {
               float k = (Float)e.getKey();
               byte v = (Byte)e.getValue();
               if (Float2ByteOpenCustomHashMap.this.strategy.equals(k, 0.0F)) {
                  return Float2ByteOpenCustomHashMap.this.containsNullKey && Float2ByteOpenCustomHashMap.this.value[Float2ByteOpenCustomHashMap.this.n] == v;
               } else {
                  float[] key = Float2ByteOpenCustomHashMap.this.key;
                  float curr;
                  int pos;
                  if (Float.floatToIntBits(
                        curr = key[pos = HashCommon.mix(Float2ByteOpenCustomHashMap.this.strategy.hashCode(k)) & Float2ByteOpenCustomHashMap.this.mask]
                     )
                     == 0) {
                     return false;
                  } else if (Float2ByteOpenCustomHashMap.this.strategy.equals(k, curr)) {
                     return Float2ByteOpenCustomHashMap.this.value[pos] == v;
                  } else {
                     while (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2ByteOpenCustomHashMap.this.mask]) != 0) {
                        if (Float2ByteOpenCustomHashMap.this.strategy.equals(k, curr)) {
                           return Float2ByteOpenCustomHashMap.this.value[pos] == v;
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
               if (e.getValue() != null && e.getValue() instanceof Byte) {
                  float k = (Float)e.getKey();
                  byte v = (Byte)e.getValue();
                  if (Float2ByteOpenCustomHashMap.this.strategy.equals(k, 0.0F)) {
                     if (Float2ByteOpenCustomHashMap.this.containsNullKey && Float2ByteOpenCustomHashMap.this.value[Float2ByteOpenCustomHashMap.this.n] == v) {
                        Float2ByteOpenCustomHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     float[] key = Float2ByteOpenCustomHashMap.this.key;
                     float curr;
                     int pos;
                     if (Float.floatToIntBits(
                           curr = key[pos = HashCommon.mix(Float2ByteOpenCustomHashMap.this.strategy.hashCode(k)) & Float2ByteOpenCustomHashMap.this.mask]
                        )
                        == 0) {
                        return false;
                     } else if (Float2ByteOpenCustomHashMap.this.strategy.equals(curr, k)) {
                        if (Float2ByteOpenCustomHashMap.this.value[pos] == v) {
                           Float2ByteOpenCustomHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2ByteOpenCustomHashMap.this.mask]) != 0) {
                           if (Float2ByteOpenCustomHashMap.this.strategy.equals(curr, k) && Float2ByteOpenCustomHashMap.this.value[pos] == v) {
                              Float2ByteOpenCustomHashMap.this.removeEntry(pos);
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
         return Float2ByteOpenCustomHashMap.this.size;
      }

      @Override
      public void clear() {
         Float2ByteOpenCustomHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Float2ByteMap.Entry> consumer) {
         if (Float2ByteOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Float2ByteOpenCustomHashMap.this.new MapEntry(Float2ByteOpenCustomHashMap.this.n));
         }

         int pos = Float2ByteOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Float.floatToIntBits(Float2ByteOpenCustomHashMap.this.key[pos]) != 0) {
               consumer.accept(Float2ByteOpenCustomHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Float2ByteMap.Entry> consumer) {
         Float2ByteOpenCustomHashMap.MapEntry entry = Float2ByteOpenCustomHashMap.this.new MapEntry();
         if (Float2ByteOpenCustomHashMap.this.containsNullKey) {
            entry.index = Float2ByteOpenCustomHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Float2ByteOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Float.floatToIntBits(Float2ByteOpenCustomHashMap.this.key[pos]) != 0) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Float2ByteOpenCustomHashMap.this.n;
      int last = -1;
      int c = Float2ByteOpenCustomHashMap.this.size;
      boolean mustReturnNullKey = Float2ByteOpenCustomHashMap.this.containsNullKey;
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
               return this.last = Float2ByteOpenCustomHashMap.this.n;
            } else {
               float[] key = Float2ByteOpenCustomHashMap.this.key;

               while (--this.pos >= 0) {
                  if (Float.floatToIntBits(key[this.pos]) != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               float k = this.wrapped.getFloat(-this.pos - 1);
               int p = HashCommon.mix(Float2ByteOpenCustomHashMap.this.strategy.hashCode(k)) & Float2ByteOpenCustomHashMap.this.mask;

               while (!Float2ByteOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Float2ByteOpenCustomHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Float2ByteOpenCustomHashMap.this.n);
            this.c--;
         }

         float[] key = Float2ByteOpenCustomHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               float k = this.wrapped.getFloat(-this.pos - 1);
               int p = HashCommon.mix(Float2ByteOpenCustomHashMap.this.strategy.hashCode(k)) & Float2ByteOpenCustomHashMap.this.mask;

               while (!Float2ByteOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Float2ByteOpenCustomHashMap.this.mask;
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
         float[] key = Float2ByteOpenCustomHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            float curr;
            for (pos = pos + 1 & Float2ByteOpenCustomHashMap.this.mask;
               Float.floatToIntBits(curr = key[pos]) != 0;
               pos = pos + 1 & Float2ByteOpenCustomHashMap.this.mask
            ) {
               int slot = HashCommon.mix(Float2ByteOpenCustomHashMap.this.strategy.hashCode(curr)) & Float2ByteOpenCustomHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new FloatArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Float2ByteOpenCustomHashMap.this.value[last] = Float2ByteOpenCustomHashMap.this.value[pos];
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
            if (this.last == Float2ByteOpenCustomHashMap.this.n) {
               Float2ByteOpenCustomHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Float2ByteOpenCustomHashMap.this.remove(this.wrapped.getFloat(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Float2ByteOpenCustomHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Float2ByteOpenCustomHashMap.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Float2ByteOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Float2ByteOpenCustomHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Float2ByteOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Float2ByteOpenCustomHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Float2ByteOpenCustomHashMap.this.n);
            return true;
         } else {
            for (float[] key = Float2ByteOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
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
            this.acceptOnIndex(action, Float2ByteOpenCustomHashMap.this.n);
         }

         for (float[] key = Float2ByteOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
            if (Float.floatToIntBits(key[this.pos]) != 0) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Float2ByteOpenCustomHashMap.this.size - this.c
            : Math.min(
               (long)(Float2ByteOpenCustomHashMap.this.size - this.c),
               (long)((double)Float2ByteOpenCustomHashMap.this.realSize() / Float2ByteOpenCustomHashMap.this.n * (this.max - this.pos))
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

            float[] key = Float2ByteOpenCustomHashMap.this.key;

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

   private final class ValueIterator extends Float2ByteOpenCustomHashMap.MapIterator<ByteConsumer> implements ByteIterator {
      public ValueIterator() {
      }

      final void acceptOnIndex(ByteConsumer action, int index) {
         action.accept(Float2ByteOpenCustomHashMap.this.value[index]);
      }

      @Override
      public byte nextByte() {
         return Float2ByteOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Float2ByteOpenCustomHashMap.MapSpliterator<ByteConsumer, Float2ByteOpenCustomHashMap.ValueSpliterator>
      implements ByteSpliterator {
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

      final void acceptOnIndex(ByteConsumer action, int index) {
         action.accept(Float2ByteOpenCustomHashMap.this.value[index]);
      }

      final Float2ByteOpenCustomHashMap.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Float2ByteOpenCustomHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
