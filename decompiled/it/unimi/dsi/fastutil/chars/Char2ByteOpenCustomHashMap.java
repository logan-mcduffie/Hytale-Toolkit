package it.unimi.dsi.fastutil.chars;

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
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public class Char2ByteOpenCustomHashMap extends AbstractChar2ByteMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient char[] key;
   protected transient byte[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected CharHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Char2ByteMap.FastEntrySet entries;
   protected transient CharSet keys;
   protected transient ByteCollection values;

   public Char2ByteOpenCustomHashMap(int expected, float f, CharHash.Strategy strategy) {
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
         this.key = new char[this.n + 1];
         this.value = new byte[this.n + 1];
      }
   }

   public Char2ByteOpenCustomHashMap(int expected, CharHash.Strategy strategy) {
      this(expected, 0.75F, strategy);
   }

   public Char2ByteOpenCustomHashMap(CharHash.Strategy strategy) {
      this(16, 0.75F, strategy);
   }

   public Char2ByteOpenCustomHashMap(Map<? extends Character, ? extends Byte> m, float f, CharHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Char2ByteOpenCustomHashMap(Map<? extends Character, ? extends Byte> m, CharHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Char2ByteOpenCustomHashMap(Char2ByteMap m, float f, CharHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Char2ByteOpenCustomHashMap(Char2ByteMap m, CharHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Char2ByteOpenCustomHashMap(char[] k, byte[] v, float f, CharHash.Strategy strategy) {
      this(k.length, f, strategy);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Char2ByteOpenCustomHashMap(char[] k, byte[] v, CharHash.Strategy strategy) {
      this(k, v, 0.75F, strategy);
   }

   public CharHash.Strategy strategy() {
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
   public void putAll(Map<? extends Character, ? extends Byte> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(char k) {
      if (this.strategy.equals(k, '\u0000')) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         char[] key = this.key;
         char curr;
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

   private void insert(int pos, char k, byte v) {
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
   public byte put(char k, byte v) {
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

   public byte addTo(char k, byte incr) {
      int pos;
      if (this.strategy.equals(k, '\u0000')) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, incr);
         }

         pos = this.n;
         this.containsNullKey = true;
      } else {
         char[] key = this.key;
         char curr;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) != 0) {
            if (this.strategy.equals(curr, k)) {
               return this.addToValue(pos, incr);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
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
      char[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         char curr;
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
   public byte remove(char k) {
      if (this.strategy.equals(k, '\u0000')) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         char[] key = this.key;
         char curr;
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
   public byte get(char k) {
      if (this.strategy.equals(k, '\u0000')) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         char[] key = this.key;
         char curr;
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
   public boolean containsKey(char k) {
      if (this.strategy.equals(k, '\u0000')) {
         return this.containsNullKey;
      } else {
         char[] key = this.key;
         char curr;
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
   public boolean containsValue(byte v) {
      byte[] value = this.value;
      char[] key = this.key;
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
   public byte getOrDefault(char k, byte defaultValue) {
      if (this.strategy.equals(k, '\u0000')) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         char[] key = this.key;
         char curr;
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
   public byte putIfAbsent(char k, byte v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(char k, byte v) {
      if (this.strategy.equals(k, '\u0000')) {
         if (this.containsNullKey && v == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         char[] key = this.key;
         char curr;
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
   public boolean replace(char k, byte oldValue, byte v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public byte replace(char k, byte v) {
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
   public byte computeIfAbsent(char k, IntUnaryOperator mappingFunction) {
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
   public byte computeIfAbsent(char key, Char2ByteFunction mappingFunction) {
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
   public byte computeIfAbsentNullable(char k, IntFunction<? extends Byte> mappingFunction) {
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
   public byte computeIfPresent(char k, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Byte newValue = remappingFunction.apply(k, this.value[pos]);
         if (newValue == null) {
            if (this.strategy.equals(k, '\u0000')) {
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
   public byte compute(char k, BiFunction<? super Character, ? super Byte, ? extends Byte> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Byte newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (this.strategy.equals(k, '\u0000')) {
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
   public byte merge(char k, byte v, BiFunction<? super Byte, ? super Byte, ? extends Byte> remappingFunction) {
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
            if (this.strategy.equals(k, '\u0000')) {
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
         Arrays.fill(this.key, '\u0000');
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

   public Char2ByteMap.FastEntrySet char2ByteEntrySet() {
      if (this.entries == null) {
         this.entries = new Char2ByteOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public CharSet keySet() {
      if (this.keys == null) {
         this.keys = new Char2ByteOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ByteCollection values() {
      if (this.values == null) {
         this.values = new AbstractByteCollection() {
            @Override
            public ByteIterator iterator() {
               return Char2ByteOpenCustomHashMap.this.new ValueIterator();
            }

            @Override
            public ByteSpliterator spliterator() {
               return Char2ByteOpenCustomHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(ByteConsumer consumer) {
               if (Char2ByteOpenCustomHashMap.this.containsNullKey) {
                  consumer.accept(Char2ByteOpenCustomHashMap.this.value[Char2ByteOpenCustomHashMap.this.n]);
               }

               int pos = Char2ByteOpenCustomHashMap.this.n;

               while (pos-- != 0) {
                  if (Char2ByteOpenCustomHashMap.this.key[pos] != 0) {
                     consumer.accept(Char2ByteOpenCustomHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Char2ByteOpenCustomHashMap.this.size;
            }

            @Override
            public boolean contains(byte v) {
               return Char2ByteOpenCustomHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Char2ByteOpenCustomHashMap.this.clear();
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
      char[] key = this.key;
      byte[] value = this.value;
      int mask = newN - 1;
      char[] newKey = new char[newN + 1];
      byte[] newValue = new byte[newN + 1];
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

   public Char2ByteOpenCustomHashMap clone() {
      Char2ByteOpenCustomHashMap c;
      try {
         c = (Char2ByteOpenCustomHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (char[])this.key.clone();
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
         while (this.key[i] == 0) {
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
      char[] key = this.key;
      byte[] value = this.value;
      Char2ByteOpenCustomHashMap.EntryIterator i = new Char2ByteOpenCustomHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeChar(key[e]);
         s.writeByte(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      char[] key = this.key = new char[this.n + 1];
      byte[] value = this.value = new byte[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         char k = s.readChar();
         byte v = s.readByte();
         int pos;
         if (this.strategy.equals(k, '\u0000')) {
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
      extends Char2ByteOpenCustomHashMap.MapIterator<Consumer<? super Char2ByteMap.Entry>>
      implements ObjectIterator<Char2ByteMap.Entry> {
      private Char2ByteOpenCustomHashMap.MapEntry entry;

      private EntryIterator() {
      }

      public Char2ByteOpenCustomHashMap.MapEntry next() {
         return this.entry = Char2ByteOpenCustomHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Char2ByteMap.Entry> action, int index) {
         action.accept(this.entry = Char2ByteOpenCustomHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Char2ByteOpenCustomHashMap.MapSpliterator<Consumer<? super Char2ByteMap.Entry>, Char2ByteOpenCustomHashMap.EntrySpliterator>
      implements ObjectSpliterator<Char2ByteMap.Entry> {
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

      final void acceptOnIndex(Consumer<? super Char2ByteMap.Entry> action, int index) {
         action.accept(Char2ByteOpenCustomHashMap.this.new MapEntry(index));
      }

      final Char2ByteOpenCustomHashMap.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Char2ByteOpenCustomHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Char2ByteOpenCustomHashMap.MapIterator<Consumer<? super Char2ByteMap.Entry>>
      implements ObjectIterator<Char2ByteMap.Entry> {
      private final Char2ByteOpenCustomHashMap.MapEntry entry = Char2ByteOpenCustomHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Char2ByteOpenCustomHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Char2ByteMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Char2ByteOpenCustomHashMap.MapIterator<CharConsumer> implements CharIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(CharConsumer action, int index) {
         action.accept(Char2ByteOpenCustomHashMap.this.key[index]);
      }

      @Override
      public char nextChar() {
         return Char2ByteOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractCharSet {
      private KeySet() {
      }

      @Override
      public CharIterator iterator() {
         return Char2ByteOpenCustomHashMap.this.new KeyIterator();
      }

      @Override
      public CharSpliterator spliterator() {
         return Char2ByteOpenCustomHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(CharConsumer consumer) {
         if (Char2ByteOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Char2ByteOpenCustomHashMap.this.key[Char2ByteOpenCustomHashMap.this.n]);
         }

         int pos = Char2ByteOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            char k = Char2ByteOpenCustomHashMap.this.key[pos];
            if (k != 0) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Char2ByteOpenCustomHashMap.this.size;
      }

      @Override
      public boolean contains(char k) {
         return Char2ByteOpenCustomHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(char k) {
         int oldSize = Char2ByteOpenCustomHashMap.this.size;
         Char2ByteOpenCustomHashMap.this.remove(k);
         return Char2ByteOpenCustomHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Char2ByteOpenCustomHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Char2ByteOpenCustomHashMap.MapSpliterator<CharConsumer, Char2ByteOpenCustomHashMap.KeySpliterator>
      implements CharSpliterator {
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

      final void acceptOnIndex(CharConsumer action, int index) {
         action.accept(Char2ByteOpenCustomHashMap.this.key[index]);
      }

      final Char2ByteOpenCustomHashMap.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Char2ByteOpenCustomHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Char2ByteMap.Entry, Entry<Character, Byte>, CharBytePair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public char getCharKey() {
         return Char2ByteOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public char leftChar() {
         return Char2ByteOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public byte getByteValue() {
         return Char2ByteOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public byte rightByte() {
         return Char2ByteOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public byte setValue(byte v) {
         byte oldValue = Char2ByteOpenCustomHashMap.this.value[this.index];
         Char2ByteOpenCustomHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public CharBytePair right(byte v) {
         Char2ByteOpenCustomHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Character getKey() {
         return Char2ByteOpenCustomHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Byte getValue() {
         return Char2ByteOpenCustomHashMap.this.value[this.index];
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
            Entry<Character, Byte> e = (Entry<Character, Byte>)o;
            return Char2ByteOpenCustomHashMap.this.strategy.equals(Char2ByteOpenCustomHashMap.this.key[this.index], e.getKey())
               && Char2ByteOpenCustomHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return Char2ByteOpenCustomHashMap.this.strategy.hashCode(Char2ByteOpenCustomHashMap.this.key[this.index])
            ^ Char2ByteOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public String toString() {
         return Char2ByteOpenCustomHashMap.this.key[this.index] + "=>" + Char2ByteOpenCustomHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Char2ByteMap.Entry> implements Char2ByteMap.FastEntrySet {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Char2ByteMap.Entry> iterator() {
         return Char2ByteOpenCustomHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Char2ByteMap.Entry> fastIterator() {
         return Char2ByteOpenCustomHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Char2ByteMap.Entry> spliterator() {
         return Char2ByteOpenCustomHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Byte) {
               char k = (Character)e.getKey();
               byte v = (Byte)e.getValue();
               if (Char2ByteOpenCustomHashMap.this.strategy.equals(k, '\u0000')) {
                  return Char2ByteOpenCustomHashMap.this.containsNullKey && Char2ByteOpenCustomHashMap.this.value[Char2ByteOpenCustomHashMap.this.n] == v;
               } else {
                  char[] key = Char2ByteOpenCustomHashMap.this.key;
                  char curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(Char2ByteOpenCustomHashMap.this.strategy.hashCode(k)) & Char2ByteOpenCustomHashMap.this.mask]) == 0) {
                     return false;
                  } else if (Char2ByteOpenCustomHashMap.this.strategy.equals(k, curr)) {
                     return Char2ByteOpenCustomHashMap.this.value[pos] == v;
                  } else {
                     while ((curr = key[pos = pos + 1 & Char2ByteOpenCustomHashMap.this.mask]) != 0) {
                        if (Char2ByteOpenCustomHashMap.this.strategy.equals(k, curr)) {
                           return Char2ByteOpenCustomHashMap.this.value[pos] == v;
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
            if (e.getKey() != null && e.getKey() instanceof Character) {
               if (e.getValue() != null && e.getValue() instanceof Byte) {
                  char k = (Character)e.getKey();
                  byte v = (Byte)e.getValue();
                  if (Char2ByteOpenCustomHashMap.this.strategy.equals(k, '\u0000')) {
                     if (Char2ByteOpenCustomHashMap.this.containsNullKey && Char2ByteOpenCustomHashMap.this.value[Char2ByteOpenCustomHashMap.this.n] == v) {
                        Char2ByteOpenCustomHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     char[] key = Char2ByteOpenCustomHashMap.this.key;
                     char curr;
                     int pos;
                     if ((curr = key[pos = HashCommon.mix(Char2ByteOpenCustomHashMap.this.strategy.hashCode(k)) & Char2ByteOpenCustomHashMap.this.mask]) == 0) {
                        return false;
                     } else if (Char2ByteOpenCustomHashMap.this.strategy.equals(curr, k)) {
                        if (Char2ByteOpenCustomHashMap.this.value[pos] == v) {
                           Char2ByteOpenCustomHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while ((curr = key[pos = pos + 1 & Char2ByteOpenCustomHashMap.this.mask]) != 0) {
                           if (Char2ByteOpenCustomHashMap.this.strategy.equals(curr, k) && Char2ByteOpenCustomHashMap.this.value[pos] == v) {
                              Char2ByteOpenCustomHashMap.this.removeEntry(pos);
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
         return Char2ByteOpenCustomHashMap.this.size;
      }

      @Override
      public void clear() {
         Char2ByteOpenCustomHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Char2ByteMap.Entry> consumer) {
         if (Char2ByteOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Char2ByteOpenCustomHashMap.this.new MapEntry(Char2ByteOpenCustomHashMap.this.n));
         }

         int pos = Char2ByteOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Char2ByteOpenCustomHashMap.this.key[pos] != 0) {
               consumer.accept(Char2ByteOpenCustomHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Char2ByteMap.Entry> consumer) {
         Char2ByteOpenCustomHashMap.MapEntry entry = Char2ByteOpenCustomHashMap.this.new MapEntry();
         if (Char2ByteOpenCustomHashMap.this.containsNullKey) {
            entry.index = Char2ByteOpenCustomHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Char2ByteOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Char2ByteOpenCustomHashMap.this.key[pos] != 0) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Char2ByteOpenCustomHashMap.this.n;
      int last = -1;
      int c = Char2ByteOpenCustomHashMap.this.size;
      boolean mustReturnNullKey = Char2ByteOpenCustomHashMap.this.containsNullKey;
      CharArrayList wrapped;

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
               return this.last = Char2ByteOpenCustomHashMap.this.n;
            } else {
               char[] key = Char2ByteOpenCustomHashMap.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               char k = this.wrapped.getChar(-this.pos - 1);
               int p = HashCommon.mix(Char2ByteOpenCustomHashMap.this.strategy.hashCode(k)) & Char2ByteOpenCustomHashMap.this.mask;

               while (!Char2ByteOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Char2ByteOpenCustomHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Char2ByteOpenCustomHashMap.this.n);
            this.c--;
         }

         char[] key = Char2ByteOpenCustomHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               char k = this.wrapped.getChar(-this.pos - 1);
               int p = HashCommon.mix(Char2ByteOpenCustomHashMap.this.strategy.hashCode(k)) & Char2ByteOpenCustomHashMap.this.mask;

               while (!Char2ByteOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Char2ByteOpenCustomHashMap.this.mask;
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
         char[] key = Char2ByteOpenCustomHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            char curr;
            for (pos = pos + 1 & Char2ByteOpenCustomHashMap.this.mask; (curr = key[pos]) != 0; pos = pos + 1 & Char2ByteOpenCustomHashMap.this.mask) {
               int slot = HashCommon.mix(Char2ByteOpenCustomHashMap.this.strategy.hashCode(curr)) & Char2ByteOpenCustomHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new CharArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Char2ByteOpenCustomHashMap.this.value[last] = Char2ByteOpenCustomHashMap.this.value[pos];
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
            if (this.last == Char2ByteOpenCustomHashMap.this.n) {
               Char2ByteOpenCustomHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Char2ByteOpenCustomHashMap.this.remove(this.wrapped.getChar(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Char2ByteOpenCustomHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Char2ByteOpenCustomHashMap.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Char2ByteOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Char2ByteOpenCustomHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Char2ByteOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Char2ByteOpenCustomHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Char2ByteOpenCustomHashMap.this.n);
            return true;
         } else {
            for (char[] key = Char2ByteOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
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
            this.acceptOnIndex(action, Char2ByteOpenCustomHashMap.this.n);
         }

         for (char[] key = Char2ByteOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
            if (key[this.pos] != 0) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Char2ByteOpenCustomHashMap.this.size - this.c
            : Math.min(
               (long)(Char2ByteOpenCustomHashMap.this.size - this.c),
               (long)((double)Char2ByteOpenCustomHashMap.this.realSize() / Char2ByteOpenCustomHashMap.this.n * (this.max - this.pos))
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

            char[] key = Char2ByteOpenCustomHashMap.this.key;

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

   private final class ValueIterator extends Char2ByteOpenCustomHashMap.MapIterator<ByteConsumer> implements ByteIterator {
      public ValueIterator() {
      }

      final void acceptOnIndex(ByteConsumer action, int index) {
         action.accept(Char2ByteOpenCustomHashMap.this.value[index]);
      }

      @Override
      public byte nextByte() {
         return Char2ByteOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Char2ByteOpenCustomHashMap.MapSpliterator<ByteConsumer, Char2ByteOpenCustomHashMap.ValueSpliterator>
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
         action.accept(Char2ByteOpenCustomHashMap.this.value[index]);
      }

      final Char2ByteOpenCustomHashMap.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Char2ByteOpenCustomHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
