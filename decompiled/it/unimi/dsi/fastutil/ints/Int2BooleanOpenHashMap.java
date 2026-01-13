package it.unimi.dsi.fastutil.ints;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.booleans.AbstractBooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanCollection;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.booleans.BooleanIterator;
import it.unimi.dsi.fastutil.booleans.BooleanSpliterator;
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

public class Int2BooleanOpenHashMap extends AbstractInt2BooleanMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient int[] key;
   protected transient boolean[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Int2BooleanMap.FastEntrySet entries;
   protected transient IntSet keys;
   protected transient BooleanCollection values;

   public Int2BooleanOpenHashMap(int expected, float f) {
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
         this.value = new boolean[this.n + 1];
      }
   }

   public Int2BooleanOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Int2BooleanOpenHashMap() {
      this(16, 0.75F);
   }

   public Int2BooleanOpenHashMap(Map<? extends Integer, ? extends Boolean> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Int2BooleanOpenHashMap(Map<? extends Integer, ? extends Boolean> m) {
      this(m, 0.75F);
   }

   public Int2BooleanOpenHashMap(Int2BooleanMap m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Int2BooleanOpenHashMap(Int2BooleanMap m) {
      this(m, 0.75F);
   }

   public Int2BooleanOpenHashMap(int[] k, boolean[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Int2BooleanOpenHashMap(int[] k, boolean[] v) {
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

   private boolean removeEntry(int pos) {
      boolean oldValue = this.value[pos];
      this.size--;
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private boolean removeNullEntry() {
      this.containsNullKey = false;
      boolean oldValue = this.value[this.n];
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends Integer, ? extends Boolean> m) {
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

   private void insert(int pos, int k, boolean v) {
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
   public boolean put(int k, boolean v) {
      int pos = this.find(k);
      if (pos < 0) {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      } else {
         boolean oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
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
   public boolean remove(int k) {
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
   public boolean get(int k) {
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
   public boolean containsValue(boolean v) {
      boolean[] value = this.value;
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
   public boolean getOrDefault(int k, boolean defaultValue) {
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
   public boolean putIfAbsent(int k, boolean v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(int k, boolean v) {
      if (k == 0) {
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
         if ((curr = key[pos = HashCommon.mix(k) & this.mask]) == 0) {
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
   public boolean replace(int k, boolean oldValue, boolean v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean replace(int k, boolean v) {
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         boolean oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   @Override
   public boolean computeIfAbsent(int k, java.util.function.IntPredicate mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         boolean newValue = mappingFunction.test(k);
         this.insert(-pos - 1, k, newValue);
         return newValue;
      }
   }

   @Override
   public boolean computeIfAbsent(int key, Int2BooleanFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         boolean newValue = mappingFunction.get(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public boolean computeIfAbsentNullable(int k, IntFunction<? extends Boolean> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         Boolean newValue = mappingFunction.apply(k);
         if (newValue == null) {
            return this.defRetValue;
         } else {
            boolean v = newValue;
            this.insert(-pos - 1, k, v);
            return v;
         }
      }
   }

   @Override
   public boolean computeIfPresent(int k, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Boolean newValue = remappingFunction.apply(k, this.value[pos]);
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
   public boolean compute(int k, BiFunction<? super Integer, ? super Boolean, ? extends Boolean> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Boolean newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
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
         boolean newVal = newValue;
         if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
         } else {
            return this.value[pos] = newVal;
         }
      }
   }

   @Override
   public boolean merge(int k, boolean v, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
         Boolean newValue = remappingFunction.apply(this.value[pos], v);
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

   public Int2BooleanMap.FastEntrySet int2BooleanEntrySet() {
      if (this.entries == null) {
         this.entries = new Int2BooleanOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public IntSet keySet() {
      if (this.keys == null) {
         this.keys = new Int2BooleanOpenHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public BooleanCollection values() {
      if (this.values == null) {
         this.values = new AbstractBooleanCollection() {
            @Override
            public BooleanIterator iterator() {
               return Int2BooleanOpenHashMap.this.new ValueIterator();
            }

            @Override
            public BooleanSpliterator spliterator() {
               return Int2BooleanOpenHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(BooleanConsumer consumer) {
               if (Int2BooleanOpenHashMap.this.containsNullKey) {
                  consumer.accept(Int2BooleanOpenHashMap.this.value[Int2BooleanOpenHashMap.this.n]);
               }

               int pos = Int2BooleanOpenHashMap.this.n;

               while (pos-- != 0) {
                  if (Int2BooleanOpenHashMap.this.key[pos] != 0) {
                     consumer.accept(Int2BooleanOpenHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Int2BooleanOpenHashMap.this.size;
            }

            @Override
            public boolean contains(boolean v) {
               return Int2BooleanOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Int2BooleanOpenHashMap.this.clear();
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
      boolean[] value = this.value;
      int mask = newN - 1;
      int[] newKey = new int[newN + 1];
      boolean[] newValue = new boolean[newN + 1];
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

   public Int2BooleanOpenHashMap clone() {
      Int2BooleanOpenHashMap c;
      try {
         c = (Int2BooleanOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (int[])this.key.clone();
      c.value = (boolean[])this.value.clone();
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
         t ^= this.value[i] ? 1231 : 1237;
         h += t;
      }

      if (this.containsNullKey) {
         h += this.value[this.n] ? 1231 : 1237;
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      int[] key = this.key;
      boolean[] value = this.value;
      Int2BooleanOpenHashMap.EntryIterator i = new Int2BooleanOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeInt(key[e]);
         s.writeBoolean(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      int[] key = this.key = new int[this.n + 1];
      boolean[] value = this.value = new boolean[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         int k = s.readInt();
         boolean v = s.readBoolean();
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
      extends Int2BooleanOpenHashMap.MapIterator<Consumer<? super Int2BooleanMap.Entry>>
      implements ObjectIterator<Int2BooleanMap.Entry> {
      private Int2BooleanOpenHashMap.MapEntry entry;

      private EntryIterator() {
      }

      public Int2BooleanOpenHashMap.MapEntry next() {
         return this.entry = Int2BooleanOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Int2BooleanMap.Entry> action, int index) {
         action.accept(this.entry = Int2BooleanOpenHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Int2BooleanOpenHashMap.MapSpliterator<Consumer<? super Int2BooleanMap.Entry>, Int2BooleanOpenHashMap.EntrySpliterator>
      implements ObjectSpliterator<Int2BooleanMap.Entry> {
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

      final void acceptOnIndex(Consumer<? super Int2BooleanMap.Entry> action, int index) {
         action.accept(Int2BooleanOpenHashMap.this.new MapEntry(index));
      }

      final Int2BooleanOpenHashMap.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Int2BooleanOpenHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Int2BooleanOpenHashMap.MapIterator<Consumer<? super Int2BooleanMap.Entry>>
      implements ObjectIterator<Int2BooleanMap.Entry> {
      private final Int2BooleanOpenHashMap.MapEntry entry = Int2BooleanOpenHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Int2BooleanOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Int2BooleanMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Int2BooleanOpenHashMap.MapIterator<java.util.function.IntConsumer> implements IntIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(java.util.function.IntConsumer action, int index) {
         action.accept(Int2BooleanOpenHashMap.this.key[index]);
      }

      @Override
      public int nextInt() {
         return Int2BooleanOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractIntSet {
      private KeySet() {
      }

      @Override
      public IntIterator iterator() {
         return Int2BooleanOpenHashMap.this.new KeyIterator();
      }

      @Override
      public IntSpliterator spliterator() {
         return Int2BooleanOpenHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(java.util.function.IntConsumer consumer) {
         if (Int2BooleanOpenHashMap.this.containsNullKey) {
            consumer.accept(Int2BooleanOpenHashMap.this.key[Int2BooleanOpenHashMap.this.n]);
         }

         int pos = Int2BooleanOpenHashMap.this.n;

         while (pos-- != 0) {
            int k = Int2BooleanOpenHashMap.this.key[pos];
            if (k != 0) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Int2BooleanOpenHashMap.this.size;
      }

      @Override
      public boolean contains(int k) {
         return Int2BooleanOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(int k) {
         int oldSize = Int2BooleanOpenHashMap.this.size;
         Int2BooleanOpenHashMap.this.remove(k);
         return Int2BooleanOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Int2BooleanOpenHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Int2BooleanOpenHashMap.MapSpliterator<java.util.function.IntConsumer, Int2BooleanOpenHashMap.KeySpliterator>
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
         action.accept(Int2BooleanOpenHashMap.this.key[index]);
      }

      final Int2BooleanOpenHashMap.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Int2BooleanOpenHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Int2BooleanMap.Entry, Entry<Integer, Boolean>, IntBooleanPair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public int getIntKey() {
         return Int2BooleanOpenHashMap.this.key[this.index];
      }

      @Override
      public int leftInt() {
         return Int2BooleanOpenHashMap.this.key[this.index];
      }

      @Override
      public boolean getBooleanValue() {
         return Int2BooleanOpenHashMap.this.value[this.index];
      }

      @Override
      public boolean rightBoolean() {
         return Int2BooleanOpenHashMap.this.value[this.index];
      }

      @Override
      public boolean setValue(boolean v) {
         boolean oldValue = Int2BooleanOpenHashMap.this.value[this.index];
         Int2BooleanOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public IntBooleanPair right(boolean v) {
         Int2BooleanOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Integer getKey() {
         return Int2BooleanOpenHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Boolean getValue() {
         return Int2BooleanOpenHashMap.this.value[this.index];
      }

      @Deprecated
      @Override
      public Boolean setValue(Boolean v) {
         return this.setValue(v.booleanValue());
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Integer, Boolean> e = (Entry<Integer, Boolean>)o;
            return Int2BooleanOpenHashMap.this.key[this.index] == e.getKey() && Int2BooleanOpenHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return Int2BooleanOpenHashMap.this.key[this.index] ^ (Int2BooleanOpenHashMap.this.value[this.index] ? 1231 : 1237);
      }

      @Override
      public String toString() {
         return Int2BooleanOpenHashMap.this.key[this.index] + "=>" + Int2BooleanOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Int2BooleanMap.Entry> implements Int2BooleanMap.FastEntrySet {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Int2BooleanMap.Entry> iterator() {
         return Int2BooleanOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Int2BooleanMap.Entry> fastIterator() {
         return Int2BooleanOpenHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Int2BooleanMap.Entry> spliterator() {
         return Int2BooleanOpenHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Integer)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Boolean) {
               int k = (Integer)e.getKey();
               boolean v = (Boolean)e.getValue();
               if (k == 0) {
                  return Int2BooleanOpenHashMap.this.containsNullKey && Int2BooleanOpenHashMap.this.value[Int2BooleanOpenHashMap.this.n] == v;
               } else {
                  int[] key = Int2BooleanOpenHashMap.this.key;
                  int curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(k) & Int2BooleanOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (k == curr) {
                     return Int2BooleanOpenHashMap.this.value[pos] == v;
                  } else {
                     while ((curr = key[pos = pos + 1 & Int2BooleanOpenHashMap.this.mask]) != 0) {
                        if (k == curr) {
                           return Int2BooleanOpenHashMap.this.value[pos] == v;
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
               if (e.getValue() != null && e.getValue() instanceof Boolean) {
                  int k = (Integer)e.getKey();
                  boolean v = (Boolean)e.getValue();
                  if (k == 0) {
                     if (Int2BooleanOpenHashMap.this.containsNullKey && Int2BooleanOpenHashMap.this.value[Int2BooleanOpenHashMap.this.n] == v) {
                        Int2BooleanOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     int[] key = Int2BooleanOpenHashMap.this.key;
                     int curr;
                     int pos;
                     if ((curr = key[pos = HashCommon.mix(k) & Int2BooleanOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (curr == k) {
                        if (Int2BooleanOpenHashMap.this.value[pos] == v) {
                           Int2BooleanOpenHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while ((curr = key[pos = pos + 1 & Int2BooleanOpenHashMap.this.mask]) != 0) {
                           if (curr == k && Int2BooleanOpenHashMap.this.value[pos] == v) {
                              Int2BooleanOpenHashMap.this.removeEntry(pos);
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
         return Int2BooleanOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Int2BooleanOpenHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Int2BooleanMap.Entry> consumer) {
         if (Int2BooleanOpenHashMap.this.containsNullKey) {
            consumer.accept(Int2BooleanOpenHashMap.this.new MapEntry(Int2BooleanOpenHashMap.this.n));
         }

         int pos = Int2BooleanOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Int2BooleanOpenHashMap.this.key[pos] != 0) {
               consumer.accept(Int2BooleanOpenHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Int2BooleanMap.Entry> consumer) {
         Int2BooleanOpenHashMap.MapEntry entry = Int2BooleanOpenHashMap.this.new MapEntry();
         if (Int2BooleanOpenHashMap.this.containsNullKey) {
            entry.index = Int2BooleanOpenHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Int2BooleanOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Int2BooleanOpenHashMap.this.key[pos] != 0) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Int2BooleanOpenHashMap.this.n;
      int last = -1;
      int c = Int2BooleanOpenHashMap.this.size;
      boolean mustReturnNullKey = Int2BooleanOpenHashMap.this.containsNullKey;
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
               return this.last = Int2BooleanOpenHashMap.this.n;
            } else {
               int[] key = Int2BooleanOpenHashMap.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               int k = this.wrapped.getInt(-this.pos - 1);
               int p = HashCommon.mix(k) & Int2BooleanOpenHashMap.this.mask;

               while (k != key[p]) {
                  p = p + 1 & Int2BooleanOpenHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Int2BooleanOpenHashMap.this.n);
            this.c--;
         }

         int[] key = Int2BooleanOpenHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               int k = this.wrapped.getInt(-this.pos - 1);
               int p = HashCommon.mix(k) & Int2BooleanOpenHashMap.this.mask;

               while (k != key[p]) {
                  p = p + 1 & Int2BooleanOpenHashMap.this.mask;
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
         int[] key = Int2BooleanOpenHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            int curr;
            for (pos = pos + 1 & Int2BooleanOpenHashMap.this.mask; (curr = key[pos]) != 0; pos = pos + 1 & Int2BooleanOpenHashMap.this.mask) {
               int slot = HashCommon.mix(curr) & Int2BooleanOpenHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new IntArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Int2BooleanOpenHashMap.this.value[last] = Int2BooleanOpenHashMap.this.value[pos];
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
            if (this.last == Int2BooleanOpenHashMap.this.n) {
               Int2BooleanOpenHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Int2BooleanOpenHashMap.this.remove(this.wrapped.getInt(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Int2BooleanOpenHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Int2BooleanOpenHashMap.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Int2BooleanOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Int2BooleanOpenHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Int2BooleanOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Int2BooleanOpenHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Int2BooleanOpenHashMap.this.n);
            return true;
         } else {
            for (int[] key = Int2BooleanOpenHashMap.this.key; this.pos < this.max; this.pos++) {
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
            this.acceptOnIndex(action, Int2BooleanOpenHashMap.this.n);
         }

         for (int[] key = Int2BooleanOpenHashMap.this.key; this.pos < this.max; this.pos++) {
            if (key[this.pos] != 0) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Int2BooleanOpenHashMap.this.size - this.c
            : Math.min(
               (long)(Int2BooleanOpenHashMap.this.size - this.c),
               (long)((double)Int2BooleanOpenHashMap.this.realSize() / Int2BooleanOpenHashMap.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
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

            int[] key = Int2BooleanOpenHashMap.this.key;

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

   private final class ValueIterator extends Int2BooleanOpenHashMap.MapIterator<BooleanConsumer> implements BooleanIterator {
      public ValueIterator() {
      }

      final void acceptOnIndex(BooleanConsumer action, int index) {
         action.accept(Int2BooleanOpenHashMap.this.value[index]);
      }

      @Override
      public boolean nextBoolean() {
         return Int2BooleanOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Int2BooleanOpenHashMap.MapSpliterator<BooleanConsumer, Int2BooleanOpenHashMap.ValueSpliterator>
      implements BooleanSpliterator {
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

      final void acceptOnIndex(BooleanConsumer action, int index) {
         action.accept(Int2BooleanOpenHashMap.this.value[index]);
      }

      final Int2BooleanOpenHashMap.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Int2BooleanOpenHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
