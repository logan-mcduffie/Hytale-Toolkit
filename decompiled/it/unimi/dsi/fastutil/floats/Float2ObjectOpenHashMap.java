package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.objects.AbstractObjectCollection;
import it.unimi.dsi.fastutil.objects.AbstractObjectSet;
import it.unimi.dsi.fastutil.objects.ObjectCollection;
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

public class Float2ObjectOpenHashMap<V> extends AbstractFloat2ObjectMap<V> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient float[] key;
   protected transient V[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Float2ObjectMap.FastEntrySet<V> entries;
   protected transient FloatSet keys;
   protected transient ObjectCollection<V> values;

   public Float2ObjectOpenHashMap(int expected, float f) {
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
         this.value = (V[])(new Object[this.n + 1]);
      }
   }

   public Float2ObjectOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Float2ObjectOpenHashMap() {
      this(16, 0.75F);
   }

   public Float2ObjectOpenHashMap(Map<? extends Float, ? extends V> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Float2ObjectOpenHashMap(Map<? extends Float, ? extends V> m) {
      this(m, 0.75F);
   }

   public Float2ObjectOpenHashMap(Float2ObjectMap<V> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Float2ObjectOpenHashMap(Float2ObjectMap<V> m) {
      this(m, 0.75F);
   }

   public Float2ObjectOpenHashMap(float[] k, V[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Float2ObjectOpenHashMap(float[] k, V[] v) {
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
   public void putAll(Map<? extends Float, ? extends V> m) {
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

   private void insert(int pos, float k, V v) {
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
   public V put(float k, V v) {
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
         this.value[last] = null;
         return;
      }
   }

   @Override
   public V remove(float k) {
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
   public V get(float k) {
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
   public boolean containsValue(Object v) {
      V[] value = this.value;
      float[] key = this.key;
      if (this.containsNullKey && Objects.equals(value[this.n], v)) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (Float.floatToIntBits(key[i]) != 0 && Objects.equals(value[i], v)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public V getOrDefault(float k, V defaultValue) {
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
   public V putIfAbsent(float k, V v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(float k, Object v) {
      if (Float.floatToIntBits(k) == 0) {
         if (this.containsNullKey && Objects.equals(v, this.value[this.n])) {
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
         } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr) && Objects.equals(v, this.value[pos])) {
            this.removeEntry(pos);
            return true;
         } else {
            while (Float.floatToIntBits(curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (Float.floatToIntBits(k) == Float.floatToIntBits(curr) && Objects.equals(v, this.value[pos])) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(float k, V oldValue, V v) {
      int pos = this.find(k);
      if (pos >= 0 && Objects.equals(oldValue, this.value[pos])) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public V replace(float k, V v) {
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
   public V computeIfAbsent(float k, DoubleFunction<? extends V> mappingFunction) {
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
   public V computeIfAbsent(float key, Float2ObjectFunction<? extends V> mappingFunction) {
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
   public V computeIfPresent(float k, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else if (this.value[pos] == null) {
         return this.defRetValue;
      } else {
         V newValue = (V)remappingFunction.apply(k, this.value[pos]);
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
   public V compute(float k, BiFunction<? super Float, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      V newValue = (V)remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (Float.floatToIntBits(k) == 0) {
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
   public V merge(float k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      Objects.requireNonNull(v);
      int pos = this.find(k);
      if (pos >= 0 && this.value[pos] != null) {
         V newValue = (V)remappingFunction.apply(this.value[pos], v);
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
         Arrays.fill(this.key, 0.0F);
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

   public Float2ObjectMap.FastEntrySet<V> float2ObjectEntrySet() {
      if (this.entries == null) {
         this.entries = new Float2ObjectOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public FloatSet keySet() {
      if (this.keys == null) {
         this.keys = new Float2ObjectOpenHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ObjectCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractObjectCollection<V>() {
            @Override
            public ObjectIterator<V> iterator() {
               return Float2ObjectOpenHashMap.this.new ValueIterator();
            }

            @Override
            public ObjectSpliterator<V> spliterator() {
               return Float2ObjectOpenHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(Consumer<? super V> consumer) {
               if (Float2ObjectOpenHashMap.this.containsNullKey) {
                  consumer.accept(Float2ObjectOpenHashMap.this.value[Float2ObjectOpenHashMap.this.n]);
               }

               int pos = Float2ObjectOpenHashMap.this.n;

               while (pos-- != 0) {
                  if (Float.floatToIntBits(Float2ObjectOpenHashMap.this.key[pos]) != 0) {
                     consumer.accept(Float2ObjectOpenHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Float2ObjectOpenHashMap.this.size;
            }

            @Override
            public boolean contains(Object v) {
               return Float2ObjectOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Float2ObjectOpenHashMap.this.clear();
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
      V[] value = this.value;
      int mask = newN - 1;
      float[] newKey = new float[newN + 1];
      V[] newValue = (V[])(new Object[newN + 1]);
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

   public Float2ObjectOpenHashMap<V> clone() {
      Float2ObjectOpenHashMap<V> c;
      try {
         c = (Float2ObjectOpenHashMap<V>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (float[])this.key.clone();
      c.value = (V[])((Object[])this.value.clone());
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
         if (this != this.value[i]) {
            t ^= this.value[i] == null ? 0 : this.value[i].hashCode();
         }

         h += t;
      }

      if (this.containsNullKey) {
         h += this.value[this.n] == null ? 0 : this.value[this.n].hashCode();
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      float[] key = this.key;
      V[] value = this.value;
      Float2ObjectOpenHashMap<V>.EntryIterator i = new Float2ObjectOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeFloat(key[e]);
         s.writeObject(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      float[] key = this.key = new float[this.n + 1];
      V[] value = this.value = (V[])(new Object[this.n + 1]);
      int i = this.size;

      while (i-- != 0) {
         float k = s.readFloat();
         V v = (V)s.readObject();
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
      extends Float2ObjectOpenHashMap<V>.MapIterator<Consumer<? super Float2ObjectMap.Entry<V>>>
      implements ObjectIterator<Float2ObjectMap.Entry<V>> {
      private Float2ObjectOpenHashMap<V>.MapEntry entry;

      private EntryIterator() {
      }

      public Float2ObjectOpenHashMap<V>.MapEntry next() {
         return this.entry = Float2ObjectOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Float2ObjectMap.Entry<V>> action, int index) {
         action.accept(this.entry = Float2ObjectOpenHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Float2ObjectOpenHashMap<V>.MapSpliterator<Consumer<? super Float2ObjectMap.Entry<V>>, Float2ObjectOpenHashMap<V>.EntrySpliterator>
      implements ObjectSpliterator<Float2ObjectMap.Entry<V>> {
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

      final void acceptOnIndex(Consumer<? super Float2ObjectMap.Entry<V>> action, int index) {
         action.accept(Float2ObjectOpenHashMap.this.new MapEntry(index));
      }

      final Float2ObjectOpenHashMap<V>.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Float2ObjectOpenHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Float2ObjectOpenHashMap<V>.MapIterator<Consumer<? super Float2ObjectMap.Entry<V>>>
      implements ObjectIterator<Float2ObjectMap.Entry<V>> {
      private final Float2ObjectOpenHashMap<V>.MapEntry entry = Float2ObjectOpenHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Float2ObjectOpenHashMap<V>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Float2ObjectMap.Entry<V>> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Float2ObjectOpenHashMap<V>.MapIterator<FloatConsumer> implements FloatIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(FloatConsumer action, int index) {
         action.accept(Float2ObjectOpenHashMap.this.key[index]);
      }

      @Override
      public float nextFloat() {
         return Float2ObjectOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractFloatSet {
      private KeySet() {
      }

      @Override
      public FloatIterator iterator() {
         return Float2ObjectOpenHashMap.this.new KeyIterator();
      }

      @Override
      public FloatSpliterator spliterator() {
         return Float2ObjectOpenHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(FloatConsumer consumer) {
         if (Float2ObjectOpenHashMap.this.containsNullKey) {
            consumer.accept(Float2ObjectOpenHashMap.this.key[Float2ObjectOpenHashMap.this.n]);
         }

         int pos = Float2ObjectOpenHashMap.this.n;

         while (pos-- != 0) {
            float k = Float2ObjectOpenHashMap.this.key[pos];
            if (Float.floatToIntBits(k) != 0) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Float2ObjectOpenHashMap.this.size;
      }

      @Override
      public boolean contains(float k) {
         return Float2ObjectOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(float k) {
         int oldSize = Float2ObjectOpenHashMap.this.size;
         Float2ObjectOpenHashMap.this.remove(k);
         return Float2ObjectOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Float2ObjectOpenHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Float2ObjectOpenHashMap<V>.MapSpliterator<FloatConsumer, Float2ObjectOpenHashMap<V>.KeySpliterator>
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
         action.accept(Float2ObjectOpenHashMap.this.key[index]);
      }

      final Float2ObjectOpenHashMap<V>.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Float2ObjectOpenHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Float2ObjectMap.Entry<V>, Entry<Float, V>, FloatObjectPair<V> {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public float getFloatKey() {
         return Float2ObjectOpenHashMap.this.key[this.index];
      }

      @Override
      public float leftFloat() {
         return Float2ObjectOpenHashMap.this.key[this.index];
      }

      @Override
      public V getValue() {
         return Float2ObjectOpenHashMap.this.value[this.index];
      }

      @Override
      public V right() {
         return Float2ObjectOpenHashMap.this.value[this.index];
      }

      @Override
      public V setValue(V v) {
         V oldValue = Float2ObjectOpenHashMap.this.value[this.index];
         Float2ObjectOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      public FloatObjectPair<V> right(V v) {
         Float2ObjectOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Float getKey() {
         return Float2ObjectOpenHashMap.this.key[this.index];
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Float, V> e = (Entry<Float, V>)o;
            return Float.floatToIntBits(Float2ObjectOpenHashMap.this.key[this.index]) == Float.floatToIntBits(e.getKey())
               && Objects.equals(Float2ObjectOpenHashMap.this.value[this.index], e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return HashCommon.float2int(Float2ObjectOpenHashMap.this.key[this.index])
            ^ (Float2ObjectOpenHashMap.this.value[this.index] == null ? 0 : Float2ObjectOpenHashMap.this.value[this.index].hashCode());
      }

      @Override
      public String toString() {
         return Float2ObjectOpenHashMap.this.key[this.index] + "=>" + Float2ObjectOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Float2ObjectMap.Entry<V>> implements Float2ObjectMap.FastEntrySet<V> {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Float2ObjectMap.Entry<V>> iterator() {
         return Float2ObjectOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Float2ObjectMap.Entry<V>> fastIterator() {
         return Float2ObjectOpenHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Float2ObjectMap.Entry<V>> spliterator() {
         return Float2ObjectOpenHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() != null && e.getKey() instanceof Float) {
               float k = (Float)e.getKey();
               V v = (V)e.getValue();
               if (Float.floatToIntBits(k) == 0) {
                  return Float2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Float2ObjectOpenHashMap.this.value[Float2ObjectOpenHashMap.this.n], v);
               } else {
                  float[] key = Float2ObjectOpenHashMap.this.key;
                  float curr;
                  int pos;
                  if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & Float2ObjectOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                     return Objects.equals(Float2ObjectOpenHashMap.this.value[pos], v);
                  } else {
                     while (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2ObjectOpenHashMap.this.mask]) != 0) {
                        if (Float.floatToIntBits(k) == Float.floatToIntBits(curr)) {
                           return Objects.equals(Float2ObjectOpenHashMap.this.value[pos], v);
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
               float k = (Float)e.getKey();
               V v = (V)e.getValue();
               if (Float.floatToIntBits(k) == 0) {
                  if (Float2ObjectOpenHashMap.this.containsNullKey && Objects.equals(Float2ObjectOpenHashMap.this.value[Float2ObjectOpenHashMap.this.n], v)) {
                     Float2ObjectOpenHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  float[] key = Float2ObjectOpenHashMap.this.key;
                  float curr;
                  int pos;
                  if (Float.floatToIntBits(curr = key[pos = HashCommon.mix(HashCommon.float2int(k)) & Float2ObjectOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (Float.floatToIntBits(curr) == Float.floatToIntBits(k)) {
                     if (Objects.equals(Float2ObjectOpenHashMap.this.value[pos], v)) {
                        Float2ObjectOpenHashMap.this.removeEntry(pos);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     while (Float.floatToIntBits(curr = key[pos = pos + 1 & Float2ObjectOpenHashMap.this.mask]) != 0) {
                        if (Float.floatToIntBits(curr) == Float.floatToIntBits(k) && Objects.equals(Float2ObjectOpenHashMap.this.value[pos], v)) {
                           Float2ObjectOpenHashMap.this.removeEntry(pos);
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
         return Float2ObjectOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Float2ObjectOpenHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Float2ObjectMap.Entry<V>> consumer) {
         if (Float2ObjectOpenHashMap.this.containsNullKey) {
            consumer.accept(Float2ObjectOpenHashMap.this.new MapEntry(Float2ObjectOpenHashMap.this.n));
         }

         int pos = Float2ObjectOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Float.floatToIntBits(Float2ObjectOpenHashMap.this.key[pos]) != 0) {
               consumer.accept(Float2ObjectOpenHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Float2ObjectMap.Entry<V>> consumer) {
         Float2ObjectOpenHashMap<V>.MapEntry entry = Float2ObjectOpenHashMap.this.new MapEntry();
         if (Float2ObjectOpenHashMap.this.containsNullKey) {
            entry.index = Float2ObjectOpenHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Float2ObjectOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Float.floatToIntBits(Float2ObjectOpenHashMap.this.key[pos]) != 0) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Float2ObjectOpenHashMap.this.n;
      int last = -1;
      int c = Float2ObjectOpenHashMap.this.size;
      boolean mustReturnNullKey = Float2ObjectOpenHashMap.this.containsNullKey;
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
               return this.last = Float2ObjectOpenHashMap.this.n;
            } else {
               float[] key = Float2ObjectOpenHashMap.this.key;

               while (--this.pos >= 0) {
                  if (Float.floatToIntBits(key[this.pos]) != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               float k = this.wrapped.getFloat(-this.pos - 1);
               int p = HashCommon.mix(HashCommon.float2int(k)) & Float2ObjectOpenHashMap.this.mask;

               while (Float.floatToIntBits(k) != Float.floatToIntBits(key[p])) {
                  p = p + 1 & Float2ObjectOpenHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Float2ObjectOpenHashMap.this.n);
            this.c--;
         }

         float[] key = Float2ObjectOpenHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               float k = this.wrapped.getFloat(-this.pos - 1);
               int p = HashCommon.mix(HashCommon.float2int(k)) & Float2ObjectOpenHashMap.this.mask;

               while (Float.floatToIntBits(k) != Float.floatToIntBits(key[p])) {
                  p = p + 1 & Float2ObjectOpenHashMap.this.mask;
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
         float[] key = Float2ObjectOpenHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            float curr;
            for (pos = pos + 1 & Float2ObjectOpenHashMap.this.mask;
               Float.floatToIntBits(curr = key[pos]) != 0;
               pos = pos + 1 & Float2ObjectOpenHashMap.this.mask
            ) {
               int slot = HashCommon.mix(HashCommon.float2int(curr)) & Float2ObjectOpenHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new FloatArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Float2ObjectOpenHashMap.this.value[last] = Float2ObjectOpenHashMap.this.value[pos];
                  continue label38;
               }
            }

            key[last] = 0.0F;
            Float2ObjectOpenHashMap.this.value[last] = null;
            return;
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Float2ObjectOpenHashMap.this.n) {
               Float2ObjectOpenHashMap.this.containsNullKey = false;
               Float2ObjectOpenHashMap.this.value[Float2ObjectOpenHashMap.this.n] = null;
            } else {
               if (this.pos < 0) {
                  Float2ObjectOpenHashMap.this.remove(this.wrapped.getFloat(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Float2ObjectOpenHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Float2ObjectOpenHashMap<V>.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Float2ObjectOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Float2ObjectOpenHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Float2ObjectOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Float2ObjectOpenHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Float2ObjectOpenHashMap.this.n);
            return true;
         } else {
            for (float[] key = Float2ObjectOpenHashMap.this.key; this.pos < this.max; this.pos++) {
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
            this.acceptOnIndex(action, Float2ObjectOpenHashMap.this.n);
         }

         for (float[] key = Float2ObjectOpenHashMap.this.key; this.pos < this.max; this.pos++) {
            if (Float.floatToIntBits(key[this.pos]) != 0) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Float2ObjectOpenHashMap.this.size - this.c
            : Math.min(
               (long)(Float2ObjectOpenHashMap.this.size - this.c),
               (long)((double)Float2ObjectOpenHashMap.this.realSize() / Float2ObjectOpenHashMap.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
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

            float[] key = Float2ObjectOpenHashMap.this.key;

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

   private final class ValueIterator extends Float2ObjectOpenHashMap<V>.MapIterator<Consumer<? super V>> implements ObjectIterator<V> {
      public ValueIterator() {
      }

      final void acceptOnIndex(Consumer<? super V> action, int index) {
         action.accept(Float2ObjectOpenHashMap.this.value[index]);
      }

      @Override
      public V next() {
         return Float2ObjectOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Float2ObjectOpenHashMap<V>.MapSpliterator<Consumer<? super V>, Float2ObjectOpenHashMap<V>.ValueSpliterator>
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
         action.accept(Float2ObjectOpenHashMap.this.value[index]);
      }

      final Float2ObjectOpenHashMap<V>.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Float2ObjectOpenHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
