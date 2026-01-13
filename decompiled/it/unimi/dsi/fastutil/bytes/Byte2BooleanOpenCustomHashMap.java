package it.unimi.dsi.fastutil.bytes;

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
import java.util.function.IntPredicate;

public class Byte2BooleanOpenCustomHashMap extends AbstractByte2BooleanMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient byte[] key;
   protected transient boolean[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected ByteHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Byte2BooleanMap.FastEntrySet entries;
   protected transient ByteSet keys;
   protected transient BooleanCollection values;

   public Byte2BooleanOpenCustomHashMap(int expected, float f, ByteHash.Strategy strategy) {
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
         this.key = new byte[this.n + 1];
         this.value = new boolean[this.n + 1];
      }
   }

   public Byte2BooleanOpenCustomHashMap(int expected, ByteHash.Strategy strategy) {
      this(expected, 0.75F, strategy);
   }

   public Byte2BooleanOpenCustomHashMap(ByteHash.Strategy strategy) {
      this(16, 0.75F, strategy);
   }

   public Byte2BooleanOpenCustomHashMap(Map<? extends Byte, ? extends Boolean> m, float f, ByteHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Byte2BooleanOpenCustomHashMap(Map<? extends Byte, ? extends Boolean> m, ByteHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Byte2BooleanOpenCustomHashMap(Byte2BooleanMap m, float f, ByteHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Byte2BooleanOpenCustomHashMap(Byte2BooleanMap m, ByteHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Byte2BooleanOpenCustomHashMap(byte[] k, boolean[] v, float f, ByteHash.Strategy strategy) {
      this(k.length, f, strategy);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Byte2BooleanOpenCustomHashMap(byte[] k, boolean[] v, ByteHash.Strategy strategy) {
      this(k, v, 0.75F, strategy);
   }

   public ByteHash.Strategy strategy() {
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
   public void putAll(Map<? extends Byte, ? extends Boolean> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(byte k) {
      if (this.strategy.equals(k, (byte)0)) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         byte[] key = this.key;
         byte curr;
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

   private void insert(int pos, byte k, boolean v) {
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
   public boolean put(byte k, boolean v) {
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
      byte[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         byte curr;
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
   public boolean remove(byte k) {
      if (this.strategy.equals(k, (byte)0)) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         byte[] key = this.key;
         byte curr;
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
   public boolean get(byte k) {
      if (this.strategy.equals(k, (byte)0)) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         byte[] key = this.key;
         byte curr;
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
   public boolean containsKey(byte k) {
      if (this.strategy.equals(k, (byte)0)) {
         return this.containsNullKey;
      } else {
         byte[] key = this.key;
         byte curr;
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
   public boolean containsValue(boolean v) {
      boolean[] value = this.value;
      byte[] key = this.key;
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
   public boolean getOrDefault(byte k, boolean defaultValue) {
      if (this.strategy.equals(k, (byte)0)) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         byte[] key = this.key;
         byte curr;
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
   public boolean putIfAbsent(byte k, boolean v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(byte k, boolean v) {
      if (this.strategy.equals(k, (byte)0)) {
         if (this.containsNullKey && v == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         byte[] key = this.key;
         byte curr;
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
   public boolean replace(byte k, boolean oldValue, boolean v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean replace(byte k, boolean v) {
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
   public boolean computeIfAbsent(byte k, IntPredicate mappingFunction) {
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
   public boolean computeIfAbsent(byte key, Byte2BooleanFunction mappingFunction) {
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
   public boolean computeIfAbsentNullable(byte k, IntFunction<? extends Boolean> mappingFunction) {
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
   public boolean computeIfPresent(byte k, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Boolean newValue = remappingFunction.apply(k, this.value[pos]);
         if (newValue == null) {
            if (this.strategy.equals(k, (byte)0)) {
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
   public boolean compute(byte k, BiFunction<? super Byte, ? super Boolean, ? extends Boolean> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Boolean newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (this.strategy.equals(k, (byte)0)) {
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
   public boolean merge(byte k, boolean v, BiFunction<? super Boolean, ? super Boolean, ? extends Boolean> remappingFunction) {
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
            if (this.strategy.equals(k, (byte)0)) {
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
         Arrays.fill(this.key, (byte)0);
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

   public Byte2BooleanMap.FastEntrySet byte2BooleanEntrySet() {
      if (this.entries == null) {
         this.entries = new Byte2BooleanOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public ByteSet keySet() {
      if (this.keys == null) {
         this.keys = new Byte2BooleanOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public BooleanCollection values() {
      if (this.values == null) {
         this.values = new AbstractBooleanCollection() {
            @Override
            public BooleanIterator iterator() {
               return Byte2BooleanOpenCustomHashMap.this.new ValueIterator();
            }

            @Override
            public BooleanSpliterator spliterator() {
               return Byte2BooleanOpenCustomHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(BooleanConsumer consumer) {
               if (Byte2BooleanOpenCustomHashMap.this.containsNullKey) {
                  consumer.accept(Byte2BooleanOpenCustomHashMap.this.value[Byte2BooleanOpenCustomHashMap.this.n]);
               }

               int pos = Byte2BooleanOpenCustomHashMap.this.n;

               while (pos-- != 0) {
                  if (Byte2BooleanOpenCustomHashMap.this.key[pos] != 0) {
                     consumer.accept(Byte2BooleanOpenCustomHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Byte2BooleanOpenCustomHashMap.this.size;
            }

            @Override
            public boolean contains(boolean v) {
               return Byte2BooleanOpenCustomHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Byte2BooleanOpenCustomHashMap.this.clear();
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
      byte[] key = this.key;
      boolean[] value = this.value;
      int mask = newN - 1;
      byte[] newKey = new byte[newN + 1];
      boolean[] newValue = new boolean[newN + 1];
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

   public Byte2BooleanOpenCustomHashMap clone() {
      Byte2BooleanOpenCustomHashMap c;
      try {
         c = (Byte2BooleanOpenCustomHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (byte[])this.key.clone();
      c.value = (boolean[])this.value.clone();
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
         t ^= this.value[i] ? 1231 : 1237;
         h += t;
      }

      if (this.containsNullKey) {
         h += this.value[this.n] ? 1231 : 1237;
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      byte[] key = this.key;
      boolean[] value = this.value;
      Byte2BooleanOpenCustomHashMap.EntryIterator i = new Byte2BooleanOpenCustomHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeByte(key[e]);
         s.writeBoolean(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      byte[] key = this.key = new byte[this.n + 1];
      boolean[] value = this.value = new boolean[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         byte k = s.readByte();
         boolean v = s.readBoolean();
         int pos;
         if (this.strategy.equals(k, (byte)0)) {
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
      extends Byte2BooleanOpenCustomHashMap.MapIterator<Consumer<? super Byte2BooleanMap.Entry>>
      implements ObjectIterator<Byte2BooleanMap.Entry> {
      private Byte2BooleanOpenCustomHashMap.MapEntry entry;

      private EntryIterator() {
      }

      public Byte2BooleanOpenCustomHashMap.MapEntry next() {
         return this.entry = Byte2BooleanOpenCustomHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Byte2BooleanMap.Entry> action, int index) {
         action.accept(this.entry = Byte2BooleanOpenCustomHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Byte2BooleanOpenCustomHashMap.MapSpliterator<Consumer<? super Byte2BooleanMap.Entry>, Byte2BooleanOpenCustomHashMap.EntrySpliterator>
      implements ObjectSpliterator<Byte2BooleanMap.Entry> {
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

      final void acceptOnIndex(Consumer<? super Byte2BooleanMap.Entry> action, int index) {
         action.accept(Byte2BooleanOpenCustomHashMap.this.new MapEntry(index));
      }

      final Byte2BooleanOpenCustomHashMap.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Byte2BooleanOpenCustomHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Byte2BooleanOpenCustomHashMap.MapIterator<Consumer<? super Byte2BooleanMap.Entry>>
      implements ObjectIterator<Byte2BooleanMap.Entry> {
      private final Byte2BooleanOpenCustomHashMap.MapEntry entry = Byte2BooleanOpenCustomHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Byte2BooleanOpenCustomHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Byte2BooleanMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Byte2BooleanOpenCustomHashMap.MapIterator<ByteConsumer> implements ByteIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(ByteConsumer action, int index) {
         action.accept(Byte2BooleanOpenCustomHashMap.this.key[index]);
      }

      @Override
      public byte nextByte() {
         return Byte2BooleanOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractByteSet {
      private KeySet() {
      }

      @Override
      public ByteIterator iterator() {
         return Byte2BooleanOpenCustomHashMap.this.new KeyIterator();
      }

      @Override
      public ByteSpliterator spliterator() {
         return Byte2BooleanOpenCustomHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(ByteConsumer consumer) {
         if (Byte2BooleanOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Byte2BooleanOpenCustomHashMap.this.key[Byte2BooleanOpenCustomHashMap.this.n]);
         }

         int pos = Byte2BooleanOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            byte k = Byte2BooleanOpenCustomHashMap.this.key[pos];
            if (k != 0) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Byte2BooleanOpenCustomHashMap.this.size;
      }

      @Override
      public boolean contains(byte k) {
         return Byte2BooleanOpenCustomHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(byte k) {
         int oldSize = Byte2BooleanOpenCustomHashMap.this.size;
         Byte2BooleanOpenCustomHashMap.this.remove(k);
         return Byte2BooleanOpenCustomHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Byte2BooleanOpenCustomHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Byte2BooleanOpenCustomHashMap.MapSpliterator<ByteConsumer, Byte2BooleanOpenCustomHashMap.KeySpliterator>
      implements ByteSpliterator {
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

      final void acceptOnIndex(ByteConsumer action, int index) {
         action.accept(Byte2BooleanOpenCustomHashMap.this.key[index]);
      }

      final Byte2BooleanOpenCustomHashMap.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Byte2BooleanOpenCustomHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Byte2BooleanMap.Entry, Entry<Byte, Boolean>, ByteBooleanPair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public byte getByteKey() {
         return Byte2BooleanOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public byte leftByte() {
         return Byte2BooleanOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public boolean getBooleanValue() {
         return Byte2BooleanOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public boolean rightBoolean() {
         return Byte2BooleanOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public boolean setValue(boolean v) {
         boolean oldValue = Byte2BooleanOpenCustomHashMap.this.value[this.index];
         Byte2BooleanOpenCustomHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public ByteBooleanPair right(boolean v) {
         Byte2BooleanOpenCustomHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Byte getKey() {
         return Byte2BooleanOpenCustomHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Boolean getValue() {
         return Byte2BooleanOpenCustomHashMap.this.value[this.index];
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
            Entry<Byte, Boolean> e = (Entry<Byte, Boolean>)o;
            return Byte2BooleanOpenCustomHashMap.this.strategy.equals(Byte2BooleanOpenCustomHashMap.this.key[this.index], e.getKey())
               && Byte2BooleanOpenCustomHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return Byte2BooleanOpenCustomHashMap.this.strategy.hashCode(Byte2BooleanOpenCustomHashMap.this.key[this.index])
            ^ (Byte2BooleanOpenCustomHashMap.this.value[this.index] ? 1231 : 1237);
      }

      @Override
      public String toString() {
         return Byte2BooleanOpenCustomHashMap.this.key[this.index] + "=>" + Byte2BooleanOpenCustomHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Byte2BooleanMap.Entry> implements Byte2BooleanMap.FastEntrySet {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Byte2BooleanMap.Entry> iterator() {
         return Byte2BooleanOpenCustomHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Byte2BooleanMap.Entry> fastIterator() {
         return Byte2BooleanOpenCustomHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Byte2BooleanMap.Entry> spliterator() {
         return Byte2BooleanOpenCustomHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Byte)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Boolean) {
               byte k = (Byte)e.getKey();
               boolean v = (Boolean)e.getValue();
               if (Byte2BooleanOpenCustomHashMap.this.strategy.equals(k, (byte)0)) {
                  return Byte2BooleanOpenCustomHashMap.this.containsNullKey
                     && Byte2BooleanOpenCustomHashMap.this.value[Byte2BooleanOpenCustomHashMap.this.n] == v;
               } else {
                  byte[] key = Byte2BooleanOpenCustomHashMap.this.key;
                  byte curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(Byte2BooleanOpenCustomHashMap.this.strategy.hashCode(k)) & Byte2BooleanOpenCustomHashMap.this.mask])
                     == 0) {
                     return false;
                  } else if (Byte2BooleanOpenCustomHashMap.this.strategy.equals(k, curr)) {
                     return Byte2BooleanOpenCustomHashMap.this.value[pos] == v;
                  } else {
                     while ((curr = key[pos = pos + 1 & Byte2BooleanOpenCustomHashMap.this.mask]) != 0) {
                        if (Byte2BooleanOpenCustomHashMap.this.strategy.equals(k, curr)) {
                           return Byte2BooleanOpenCustomHashMap.this.value[pos] == v;
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
            if (e.getKey() != null && e.getKey() instanceof Byte) {
               if (e.getValue() != null && e.getValue() instanceof Boolean) {
                  byte k = (Byte)e.getKey();
                  boolean v = (Boolean)e.getValue();
                  if (Byte2BooleanOpenCustomHashMap.this.strategy.equals(k, (byte)0)) {
                     if (Byte2BooleanOpenCustomHashMap.this.containsNullKey
                        && Byte2BooleanOpenCustomHashMap.this.value[Byte2BooleanOpenCustomHashMap.this.n] == v) {
                        Byte2BooleanOpenCustomHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     byte[] key = Byte2BooleanOpenCustomHashMap.this.key;
                     byte curr;
                     int pos;
                     if ((curr = key[pos = HashCommon.mix(Byte2BooleanOpenCustomHashMap.this.strategy.hashCode(k)) & Byte2BooleanOpenCustomHashMap.this.mask])
                        == 0) {
                        return false;
                     } else if (Byte2BooleanOpenCustomHashMap.this.strategy.equals(curr, k)) {
                        if (Byte2BooleanOpenCustomHashMap.this.value[pos] == v) {
                           Byte2BooleanOpenCustomHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while ((curr = key[pos = pos + 1 & Byte2BooleanOpenCustomHashMap.this.mask]) != 0) {
                           if (Byte2BooleanOpenCustomHashMap.this.strategy.equals(curr, k) && Byte2BooleanOpenCustomHashMap.this.value[pos] == v) {
                              Byte2BooleanOpenCustomHashMap.this.removeEntry(pos);
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
         return Byte2BooleanOpenCustomHashMap.this.size;
      }

      @Override
      public void clear() {
         Byte2BooleanOpenCustomHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Byte2BooleanMap.Entry> consumer) {
         if (Byte2BooleanOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Byte2BooleanOpenCustomHashMap.this.new MapEntry(Byte2BooleanOpenCustomHashMap.this.n));
         }

         int pos = Byte2BooleanOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Byte2BooleanOpenCustomHashMap.this.key[pos] != 0) {
               consumer.accept(Byte2BooleanOpenCustomHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Byte2BooleanMap.Entry> consumer) {
         Byte2BooleanOpenCustomHashMap.MapEntry entry = Byte2BooleanOpenCustomHashMap.this.new MapEntry();
         if (Byte2BooleanOpenCustomHashMap.this.containsNullKey) {
            entry.index = Byte2BooleanOpenCustomHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Byte2BooleanOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Byte2BooleanOpenCustomHashMap.this.key[pos] != 0) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Byte2BooleanOpenCustomHashMap.this.n;
      int last = -1;
      int c = Byte2BooleanOpenCustomHashMap.this.size;
      boolean mustReturnNullKey = Byte2BooleanOpenCustomHashMap.this.containsNullKey;
      ByteArrayList wrapped;

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
               return this.last = Byte2BooleanOpenCustomHashMap.this.n;
            } else {
               byte[] key = Byte2BooleanOpenCustomHashMap.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               byte k = this.wrapped.getByte(-this.pos - 1);
               int p = HashCommon.mix(Byte2BooleanOpenCustomHashMap.this.strategy.hashCode(k)) & Byte2BooleanOpenCustomHashMap.this.mask;

               while (!Byte2BooleanOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Byte2BooleanOpenCustomHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Byte2BooleanOpenCustomHashMap.this.n);
            this.c--;
         }

         byte[] key = Byte2BooleanOpenCustomHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               byte k = this.wrapped.getByte(-this.pos - 1);
               int p = HashCommon.mix(Byte2BooleanOpenCustomHashMap.this.strategy.hashCode(k)) & Byte2BooleanOpenCustomHashMap.this.mask;

               while (!Byte2BooleanOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Byte2BooleanOpenCustomHashMap.this.mask;
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
         byte[] key = Byte2BooleanOpenCustomHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            byte curr;
            for (pos = pos + 1 & Byte2BooleanOpenCustomHashMap.this.mask; (curr = key[pos]) != 0; pos = pos + 1 & Byte2BooleanOpenCustomHashMap.this.mask) {
               int slot = HashCommon.mix(Byte2BooleanOpenCustomHashMap.this.strategy.hashCode(curr)) & Byte2BooleanOpenCustomHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new ByteArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Byte2BooleanOpenCustomHashMap.this.value[last] = Byte2BooleanOpenCustomHashMap.this.value[pos];
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
            if (this.last == Byte2BooleanOpenCustomHashMap.this.n) {
               Byte2BooleanOpenCustomHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Byte2BooleanOpenCustomHashMap.this.remove(this.wrapped.getByte(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Byte2BooleanOpenCustomHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Byte2BooleanOpenCustomHashMap.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Byte2BooleanOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Byte2BooleanOpenCustomHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Byte2BooleanOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Byte2BooleanOpenCustomHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Byte2BooleanOpenCustomHashMap.this.n);
            return true;
         } else {
            for (byte[] key = Byte2BooleanOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
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
            this.acceptOnIndex(action, Byte2BooleanOpenCustomHashMap.this.n);
         }

         for (byte[] key = Byte2BooleanOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
            if (key[this.pos] != 0) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Byte2BooleanOpenCustomHashMap.this.size - this.c
            : Math.min(
               (long)(Byte2BooleanOpenCustomHashMap.this.size - this.c),
               (long)((double)Byte2BooleanOpenCustomHashMap.this.realSize() / Byte2BooleanOpenCustomHashMap.this.n * (this.max - this.pos))
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

            byte[] key = Byte2BooleanOpenCustomHashMap.this.key;

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

   private final class ValueIterator extends Byte2BooleanOpenCustomHashMap.MapIterator<BooleanConsumer> implements BooleanIterator {
      public ValueIterator() {
      }

      final void acceptOnIndex(BooleanConsumer action, int index) {
         action.accept(Byte2BooleanOpenCustomHashMap.this.value[index]);
      }

      @Override
      public boolean nextBoolean() {
         return Byte2BooleanOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Byte2BooleanOpenCustomHashMap.MapSpliterator<BooleanConsumer, Byte2BooleanOpenCustomHashMap.ValueSpliterator>
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
         action.accept(Byte2BooleanOpenCustomHashMap.this.value[index]);
      }

      final Byte2BooleanOpenCustomHashMap.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Byte2BooleanOpenCustomHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
