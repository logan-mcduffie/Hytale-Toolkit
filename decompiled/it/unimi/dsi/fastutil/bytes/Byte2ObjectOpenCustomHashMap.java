package it.unimi.dsi.fastutil.bytes;

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
import java.util.function.IntFunction;

public class Byte2ObjectOpenCustomHashMap<V> extends AbstractByte2ObjectMap<V> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient byte[] key;
   protected transient V[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected ByteHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Byte2ObjectMap.FastEntrySet<V> entries;
   protected transient ByteSet keys;
   protected transient ObjectCollection<V> values;

   public Byte2ObjectOpenCustomHashMap(int expected, float f, ByteHash.Strategy strategy) {
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
         this.value = (V[])(new Object[this.n + 1]);
      }
   }

   public Byte2ObjectOpenCustomHashMap(int expected, ByteHash.Strategy strategy) {
      this(expected, 0.75F, strategy);
   }

   public Byte2ObjectOpenCustomHashMap(ByteHash.Strategy strategy) {
      this(16, 0.75F, strategy);
   }

   public Byte2ObjectOpenCustomHashMap(Map<? extends Byte, ? extends V> m, float f, ByteHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Byte2ObjectOpenCustomHashMap(Map<? extends Byte, ? extends V> m, ByteHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Byte2ObjectOpenCustomHashMap(Byte2ObjectMap<V> m, float f, ByteHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Byte2ObjectOpenCustomHashMap(Byte2ObjectMap<V> m, ByteHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Byte2ObjectOpenCustomHashMap(byte[] k, V[] v, float f, ByteHash.Strategy strategy) {
      this(k.length, f, strategy);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Byte2ObjectOpenCustomHashMap(byte[] k, V[] v, ByteHash.Strategy strategy) {
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
   public void putAll(Map<? extends Byte, ? extends V> m) {
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

   private void insert(int pos, byte k, V v) {
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
   public V put(byte k, V v) {
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
         this.value[last] = null;
         return;
      }
   }

   @Override
   public V remove(byte k) {
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
   public V get(byte k) {
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
   public boolean containsValue(Object v) {
      V[] value = this.value;
      byte[] key = this.key;
      if (this.containsNullKey && Objects.equals(value[this.n], v)) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (key[i] != 0 && Objects.equals(value[i], v)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public V getOrDefault(byte k, V defaultValue) {
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
   public V putIfAbsent(byte k, V v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(byte k, Object v) {
      if (this.strategy.equals(k, (byte)0)) {
         if (this.containsNullKey && Objects.equals(v, this.value[this.n])) {
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
         } else if (this.strategy.equals(k, curr) && Objects.equals(v, this.value[pos])) {
            this.removeEntry(pos);
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr) && Objects.equals(v, this.value[pos])) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(byte k, V oldValue, V v) {
      int pos = this.find(k);
      if (pos >= 0 && Objects.equals(oldValue, this.value[pos])) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public V replace(byte k, V v) {
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
   public V computeIfAbsent(byte k, IntFunction<? extends V> mappingFunction) {
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
   public V computeIfAbsent(byte key, Byte2ObjectFunction<? extends V> mappingFunction) {
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
   public V computeIfPresent(byte k, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else if (this.value[pos] == null) {
         return this.defRetValue;
      } else {
         V newValue = (V)remappingFunction.apply(k, this.value[pos]);
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
   public V compute(byte k, BiFunction<? super Byte, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      V newValue = (V)remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (this.strategy.equals(k, (byte)0)) {
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
   public V merge(byte k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      Objects.requireNonNull(v);
      int pos = this.find(k);
      if (pos >= 0 && this.value[pos] != null) {
         V newValue = (V)remappingFunction.apply(this.value[pos], v);
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
         Arrays.fill(this.key, (byte)0);
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

   public Byte2ObjectMap.FastEntrySet<V> byte2ObjectEntrySet() {
      if (this.entries == null) {
         this.entries = new Byte2ObjectOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public ByteSet keySet() {
      if (this.keys == null) {
         this.keys = new Byte2ObjectOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ObjectCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractObjectCollection<V>() {
            @Override
            public ObjectIterator<V> iterator() {
               return Byte2ObjectOpenCustomHashMap.this.new ValueIterator();
            }

            @Override
            public ObjectSpliterator<V> spliterator() {
               return Byte2ObjectOpenCustomHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(Consumer<? super V> consumer) {
               if (Byte2ObjectOpenCustomHashMap.this.containsNullKey) {
                  consumer.accept(Byte2ObjectOpenCustomHashMap.this.value[Byte2ObjectOpenCustomHashMap.this.n]);
               }

               int pos = Byte2ObjectOpenCustomHashMap.this.n;

               while (pos-- != 0) {
                  if (Byte2ObjectOpenCustomHashMap.this.key[pos] != 0) {
                     consumer.accept(Byte2ObjectOpenCustomHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Byte2ObjectOpenCustomHashMap.this.size;
            }

            @Override
            public boolean contains(Object v) {
               return Byte2ObjectOpenCustomHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Byte2ObjectOpenCustomHashMap.this.clear();
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
      V[] value = this.value;
      int mask = newN - 1;
      byte[] newKey = new byte[newN + 1];
      V[] newValue = (V[])(new Object[newN + 1]);
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

   public Byte2ObjectOpenCustomHashMap<V> clone() {
      Byte2ObjectOpenCustomHashMap<V> c;
      try {
         c = (Byte2ObjectOpenCustomHashMap<V>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (byte[])this.key.clone();
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
         while (this.key[i] == 0) {
            i++;
         }

         t = this.strategy.hashCode(this.key[i]);
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
      byte[] key = this.key;
      V[] value = this.value;
      Byte2ObjectOpenCustomHashMap<V>.EntryIterator i = new Byte2ObjectOpenCustomHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeByte(key[e]);
         s.writeObject(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      byte[] key = this.key = new byte[this.n + 1];
      V[] value = this.value = (V[])(new Object[this.n + 1]);
      int i = this.size;

      while (i-- != 0) {
         byte k = s.readByte();
         V v = (V)s.readObject();
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
      extends Byte2ObjectOpenCustomHashMap<V>.MapIterator<Consumer<? super Byte2ObjectMap.Entry<V>>>
      implements ObjectIterator<Byte2ObjectMap.Entry<V>> {
      private Byte2ObjectOpenCustomHashMap<V>.MapEntry entry;

      private EntryIterator() {
      }

      public Byte2ObjectOpenCustomHashMap<V>.MapEntry next() {
         return this.entry = Byte2ObjectOpenCustomHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Byte2ObjectMap.Entry<V>> action, int index) {
         action.accept(this.entry = Byte2ObjectOpenCustomHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Byte2ObjectOpenCustomHashMap<V>.MapSpliterator<Consumer<? super Byte2ObjectMap.Entry<V>>, Byte2ObjectOpenCustomHashMap<V>.EntrySpliterator>
      implements ObjectSpliterator<Byte2ObjectMap.Entry<V>> {
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

      final void acceptOnIndex(Consumer<? super Byte2ObjectMap.Entry<V>> action, int index) {
         action.accept(Byte2ObjectOpenCustomHashMap.this.new MapEntry(index));
      }

      final Byte2ObjectOpenCustomHashMap<V>.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Byte2ObjectOpenCustomHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Byte2ObjectOpenCustomHashMap<V>.MapIterator<Consumer<? super Byte2ObjectMap.Entry<V>>>
      implements ObjectIterator<Byte2ObjectMap.Entry<V>> {
      private final Byte2ObjectOpenCustomHashMap<V>.MapEntry entry = Byte2ObjectOpenCustomHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Byte2ObjectOpenCustomHashMap<V>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Byte2ObjectMap.Entry<V>> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Byte2ObjectOpenCustomHashMap<V>.MapIterator<ByteConsumer> implements ByteIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(ByteConsumer action, int index) {
         action.accept(Byte2ObjectOpenCustomHashMap.this.key[index]);
      }

      @Override
      public byte nextByte() {
         return Byte2ObjectOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractByteSet {
      private KeySet() {
      }

      @Override
      public ByteIterator iterator() {
         return Byte2ObjectOpenCustomHashMap.this.new KeyIterator();
      }

      @Override
      public ByteSpliterator spliterator() {
         return Byte2ObjectOpenCustomHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(ByteConsumer consumer) {
         if (Byte2ObjectOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Byte2ObjectOpenCustomHashMap.this.key[Byte2ObjectOpenCustomHashMap.this.n]);
         }

         int pos = Byte2ObjectOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            byte k = Byte2ObjectOpenCustomHashMap.this.key[pos];
            if (k != 0) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Byte2ObjectOpenCustomHashMap.this.size;
      }

      @Override
      public boolean contains(byte k) {
         return Byte2ObjectOpenCustomHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(byte k) {
         int oldSize = Byte2ObjectOpenCustomHashMap.this.size;
         Byte2ObjectOpenCustomHashMap.this.remove(k);
         return Byte2ObjectOpenCustomHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Byte2ObjectOpenCustomHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Byte2ObjectOpenCustomHashMap<V>.MapSpliterator<ByteConsumer, Byte2ObjectOpenCustomHashMap<V>.KeySpliterator>
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
         action.accept(Byte2ObjectOpenCustomHashMap.this.key[index]);
      }

      final Byte2ObjectOpenCustomHashMap<V>.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Byte2ObjectOpenCustomHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Byte2ObjectMap.Entry<V>, Entry<Byte, V>, ByteObjectPair<V> {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public byte getByteKey() {
         return Byte2ObjectOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public byte leftByte() {
         return Byte2ObjectOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public V getValue() {
         return Byte2ObjectOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public V right() {
         return Byte2ObjectOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public V setValue(V v) {
         V oldValue = Byte2ObjectOpenCustomHashMap.this.value[this.index];
         Byte2ObjectOpenCustomHashMap.this.value[this.index] = v;
         return oldValue;
      }

      public ByteObjectPair<V> right(V v) {
         Byte2ObjectOpenCustomHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Byte getKey() {
         return Byte2ObjectOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Byte, V> e = (Entry<Byte, V>)o;
            return Byte2ObjectOpenCustomHashMap.this.strategy.equals(Byte2ObjectOpenCustomHashMap.this.key[this.index], e.getKey())
               && Objects.equals(Byte2ObjectOpenCustomHashMap.this.value[this.index], e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return Byte2ObjectOpenCustomHashMap.this.strategy.hashCode(Byte2ObjectOpenCustomHashMap.this.key[this.index])
            ^ (Byte2ObjectOpenCustomHashMap.this.value[this.index] == null ? 0 : Byte2ObjectOpenCustomHashMap.this.value[this.index].hashCode());
      }

      @Override
      public String toString() {
         return Byte2ObjectOpenCustomHashMap.this.key[this.index] + "=>" + Byte2ObjectOpenCustomHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Byte2ObjectMap.Entry<V>> implements Byte2ObjectMap.FastEntrySet<V> {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Byte2ObjectMap.Entry<V>> iterator() {
         return Byte2ObjectOpenCustomHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Byte2ObjectMap.Entry<V>> fastIterator() {
         return Byte2ObjectOpenCustomHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Byte2ObjectMap.Entry<V>> spliterator() {
         return Byte2ObjectOpenCustomHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() != null && e.getKey() instanceof Byte) {
               byte k = (Byte)e.getKey();
               V v = (V)e.getValue();
               if (Byte2ObjectOpenCustomHashMap.this.strategy.equals(k, (byte)0)) {
                  return Byte2ObjectOpenCustomHashMap.this.containsNullKey
                     && Objects.equals(Byte2ObjectOpenCustomHashMap.this.value[Byte2ObjectOpenCustomHashMap.this.n], v);
               } else {
                  byte[] key = Byte2ObjectOpenCustomHashMap.this.key;
                  byte curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(Byte2ObjectOpenCustomHashMap.this.strategy.hashCode(k)) & Byte2ObjectOpenCustomHashMap.this.mask]) == 0) {
                     return false;
                  } else if (Byte2ObjectOpenCustomHashMap.this.strategy.equals(k, curr)) {
                     return Objects.equals(Byte2ObjectOpenCustomHashMap.this.value[pos], v);
                  } else {
                     while ((curr = key[pos = pos + 1 & Byte2ObjectOpenCustomHashMap.this.mask]) != 0) {
                        if (Byte2ObjectOpenCustomHashMap.this.strategy.equals(k, curr)) {
                           return Objects.equals(Byte2ObjectOpenCustomHashMap.this.value[pos], v);
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
               byte k = (Byte)e.getKey();
               V v = (V)e.getValue();
               if (Byte2ObjectOpenCustomHashMap.this.strategy.equals(k, (byte)0)) {
                  if (Byte2ObjectOpenCustomHashMap.this.containsNullKey
                     && Objects.equals(Byte2ObjectOpenCustomHashMap.this.value[Byte2ObjectOpenCustomHashMap.this.n], v)) {
                     Byte2ObjectOpenCustomHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  byte[] key = Byte2ObjectOpenCustomHashMap.this.key;
                  byte curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(Byte2ObjectOpenCustomHashMap.this.strategy.hashCode(k)) & Byte2ObjectOpenCustomHashMap.this.mask]) == 0) {
                     return false;
                  } else if (Byte2ObjectOpenCustomHashMap.this.strategy.equals(curr, k)) {
                     if (Objects.equals(Byte2ObjectOpenCustomHashMap.this.value[pos], v)) {
                        Byte2ObjectOpenCustomHashMap.this.removeEntry(pos);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     while ((curr = key[pos = pos + 1 & Byte2ObjectOpenCustomHashMap.this.mask]) != 0) {
                        if (Byte2ObjectOpenCustomHashMap.this.strategy.equals(curr, k) && Objects.equals(Byte2ObjectOpenCustomHashMap.this.value[pos], v)) {
                           Byte2ObjectOpenCustomHashMap.this.removeEntry(pos);
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
         return Byte2ObjectOpenCustomHashMap.this.size;
      }

      @Override
      public void clear() {
         Byte2ObjectOpenCustomHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Byte2ObjectMap.Entry<V>> consumer) {
         if (Byte2ObjectOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Byte2ObjectOpenCustomHashMap.this.new MapEntry(Byte2ObjectOpenCustomHashMap.this.n));
         }

         int pos = Byte2ObjectOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Byte2ObjectOpenCustomHashMap.this.key[pos] != 0) {
               consumer.accept(Byte2ObjectOpenCustomHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Byte2ObjectMap.Entry<V>> consumer) {
         Byte2ObjectOpenCustomHashMap<V>.MapEntry entry = Byte2ObjectOpenCustomHashMap.this.new MapEntry();
         if (Byte2ObjectOpenCustomHashMap.this.containsNullKey) {
            entry.index = Byte2ObjectOpenCustomHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Byte2ObjectOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Byte2ObjectOpenCustomHashMap.this.key[pos] != 0) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Byte2ObjectOpenCustomHashMap.this.n;
      int last = -1;
      int c = Byte2ObjectOpenCustomHashMap.this.size;
      boolean mustReturnNullKey = Byte2ObjectOpenCustomHashMap.this.containsNullKey;
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
               return this.last = Byte2ObjectOpenCustomHashMap.this.n;
            } else {
               byte[] key = Byte2ObjectOpenCustomHashMap.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               byte k = this.wrapped.getByte(-this.pos - 1);
               int p = HashCommon.mix(Byte2ObjectOpenCustomHashMap.this.strategy.hashCode(k)) & Byte2ObjectOpenCustomHashMap.this.mask;

               while (!Byte2ObjectOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Byte2ObjectOpenCustomHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Byte2ObjectOpenCustomHashMap.this.n);
            this.c--;
         }

         byte[] key = Byte2ObjectOpenCustomHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               byte k = this.wrapped.getByte(-this.pos - 1);
               int p = HashCommon.mix(Byte2ObjectOpenCustomHashMap.this.strategy.hashCode(k)) & Byte2ObjectOpenCustomHashMap.this.mask;

               while (!Byte2ObjectOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Byte2ObjectOpenCustomHashMap.this.mask;
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
         byte[] key = Byte2ObjectOpenCustomHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            byte curr;
            for (pos = pos + 1 & Byte2ObjectOpenCustomHashMap.this.mask; (curr = key[pos]) != 0; pos = pos + 1 & Byte2ObjectOpenCustomHashMap.this.mask) {
               int slot = HashCommon.mix(Byte2ObjectOpenCustomHashMap.this.strategy.hashCode(curr)) & Byte2ObjectOpenCustomHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new ByteArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Byte2ObjectOpenCustomHashMap.this.value[last] = Byte2ObjectOpenCustomHashMap.this.value[pos];
                  continue label38;
               }
            }

            key[last] = 0;
            Byte2ObjectOpenCustomHashMap.this.value[last] = null;
            return;
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Byte2ObjectOpenCustomHashMap.this.n) {
               Byte2ObjectOpenCustomHashMap.this.containsNullKey = false;
               Byte2ObjectOpenCustomHashMap.this.value[Byte2ObjectOpenCustomHashMap.this.n] = null;
            } else {
               if (this.pos < 0) {
                  Byte2ObjectOpenCustomHashMap.this.remove(this.wrapped.getByte(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Byte2ObjectOpenCustomHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Byte2ObjectOpenCustomHashMap<V>.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Byte2ObjectOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Byte2ObjectOpenCustomHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Byte2ObjectOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Byte2ObjectOpenCustomHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Byte2ObjectOpenCustomHashMap.this.n);
            return true;
         } else {
            for (byte[] key = Byte2ObjectOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
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
            this.acceptOnIndex(action, Byte2ObjectOpenCustomHashMap.this.n);
         }

         for (byte[] key = Byte2ObjectOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
            if (key[this.pos] != 0) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Byte2ObjectOpenCustomHashMap.this.size - this.c
            : Math.min(
               (long)(Byte2ObjectOpenCustomHashMap.this.size - this.c),
               (long)((double)Byte2ObjectOpenCustomHashMap.this.realSize() / Byte2ObjectOpenCustomHashMap.this.n * (this.max - this.pos))
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

            byte[] key = Byte2ObjectOpenCustomHashMap.this.key;

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

   private final class ValueIterator extends Byte2ObjectOpenCustomHashMap<V>.MapIterator<Consumer<? super V>> implements ObjectIterator<V> {
      public ValueIterator() {
      }

      final void acceptOnIndex(Consumer<? super V> action, int index) {
         action.accept(Byte2ObjectOpenCustomHashMap.this.value[index]);
      }

      @Override
      public V next() {
         return Byte2ObjectOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Byte2ObjectOpenCustomHashMap<V>.MapSpliterator<Consumer<? super V>, Byte2ObjectOpenCustomHashMap<V>.ValueSpliterator>
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
         action.accept(Byte2ObjectOpenCustomHashMap.this.value[index]);
      }

      final Byte2ObjectOpenCustomHashMap<V>.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Byte2ObjectOpenCustomHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
