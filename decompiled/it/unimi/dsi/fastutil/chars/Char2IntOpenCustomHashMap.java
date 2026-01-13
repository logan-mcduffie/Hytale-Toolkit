package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.ints.AbstractIntCollection;
import it.unimi.dsi.fastutil.ints.IntCollection;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
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
import java.util.function.IntConsumer;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public class Char2IntOpenCustomHashMap extends AbstractChar2IntMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient char[] key;
   protected transient int[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected CharHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Char2IntMap.FastEntrySet entries;
   protected transient CharSet keys;
   protected transient IntCollection values;

   public Char2IntOpenCustomHashMap(int expected, float f, CharHash.Strategy strategy) {
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
         this.value = new int[this.n + 1];
      }
   }

   public Char2IntOpenCustomHashMap(int expected, CharHash.Strategy strategy) {
      this(expected, 0.75F, strategy);
   }

   public Char2IntOpenCustomHashMap(CharHash.Strategy strategy) {
      this(16, 0.75F, strategy);
   }

   public Char2IntOpenCustomHashMap(Map<? extends Character, ? extends Integer> m, float f, CharHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Char2IntOpenCustomHashMap(Map<? extends Character, ? extends Integer> m, CharHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Char2IntOpenCustomHashMap(Char2IntMap m, float f, CharHash.Strategy strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Char2IntOpenCustomHashMap(Char2IntMap m, CharHash.Strategy strategy) {
      this(m, 0.75F, strategy);
   }

   public Char2IntOpenCustomHashMap(char[] k, int[] v, float f, CharHash.Strategy strategy) {
      this(k.length, f, strategy);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Char2IntOpenCustomHashMap(char[] k, int[] v, CharHash.Strategy strategy) {
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

   private int removeEntry(int pos) {
      int oldValue = this.value[pos];
      this.size--;
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private int removeNullEntry() {
      this.containsNullKey = false;
      int oldValue = this.value[this.n];
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends Character, ? extends Integer> m) {
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

   private void insert(int pos, char k, int v) {
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
   public int put(char k, int v) {
      int pos = this.find(k);
      if (pos < 0) {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      } else {
         int oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   private int addToValue(int pos, int incr) {
      int oldValue = this.value[pos];
      this.value[pos] = oldValue + incr;
      return oldValue;
   }

   public int addTo(char k, int incr) {
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
      this.value[pos] = this.defRetValue + incr;
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
   public int remove(char k) {
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
   public int get(char k) {
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
   public boolean containsValue(int v) {
      int[] value = this.value;
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
   public int getOrDefault(char k, int defaultValue) {
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
   public int putIfAbsent(char k, int v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(char k, int v) {
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
   public boolean replace(char k, int oldValue, int v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public int replace(char k, int v) {
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         int oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   @Override
   public int computeIfAbsent(char k, IntUnaryOperator mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         int newValue = mappingFunction.applyAsInt(k);
         this.insert(-pos - 1, k, newValue);
         return newValue;
      }
   }

   @Override
   public int computeIfAbsent(char key, Char2IntFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         int newValue = mappingFunction.get(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public int computeIfAbsentNullable(char k, IntFunction<? extends Integer> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         Integer newValue = mappingFunction.apply(k);
         if (newValue == null) {
            return this.defRetValue;
         } else {
            int v = newValue;
            this.insert(-pos - 1, k, v);
            return v;
         }
      }
   }

   @Override
   public int computeIfPresent(char k, BiFunction<? super Character, ? super Integer, ? extends Integer> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Integer newValue = remappingFunction.apply(k, this.value[pos]);
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
   public int compute(char k, BiFunction<? super Character, ? super Integer, ? extends Integer> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Integer newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
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
         int newVal = newValue;
         if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
         } else {
            return this.value[pos] = newVal;
         }
      }
   }

   @Override
   public int merge(char k, int v, BiFunction<? super Integer, ? super Integer, ? extends Integer> remappingFunction) {
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
         Integer newValue = remappingFunction.apply(this.value[pos], v);
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

   public Char2IntMap.FastEntrySet char2IntEntrySet() {
      if (this.entries == null) {
         this.entries = new Char2IntOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public CharSet keySet() {
      if (this.keys == null) {
         this.keys = new Char2IntOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public IntCollection values() {
      if (this.values == null) {
         this.values = new AbstractIntCollection() {
            @Override
            public IntIterator iterator() {
               return Char2IntOpenCustomHashMap.this.new ValueIterator();
            }

            @Override
            public IntSpliterator spliterator() {
               return Char2IntOpenCustomHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(IntConsumer consumer) {
               if (Char2IntOpenCustomHashMap.this.containsNullKey) {
                  consumer.accept(Char2IntOpenCustomHashMap.this.value[Char2IntOpenCustomHashMap.this.n]);
               }

               int pos = Char2IntOpenCustomHashMap.this.n;

               while (pos-- != 0) {
                  if (Char2IntOpenCustomHashMap.this.key[pos] != 0) {
                     consumer.accept(Char2IntOpenCustomHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Char2IntOpenCustomHashMap.this.size;
            }

            @Override
            public boolean contains(int v) {
               return Char2IntOpenCustomHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Char2IntOpenCustomHashMap.this.clear();
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
      int[] value = this.value;
      int mask = newN - 1;
      char[] newKey = new char[newN + 1];
      int[] newValue = new int[newN + 1];
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

   public Char2IntOpenCustomHashMap clone() {
      Char2IntOpenCustomHashMap c;
      try {
         c = (Char2IntOpenCustomHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (char[])this.key.clone();
      c.value = (int[])this.value.clone();
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
      int[] value = this.value;
      Char2IntOpenCustomHashMap.EntryIterator i = new Char2IntOpenCustomHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeChar(key[e]);
         s.writeInt(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      char[] key = this.key = new char[this.n + 1];
      int[] value = this.value = new int[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         char k = s.readChar();
         int v = s.readInt();
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
      extends Char2IntOpenCustomHashMap.MapIterator<Consumer<? super Char2IntMap.Entry>>
      implements ObjectIterator<Char2IntMap.Entry> {
      private Char2IntOpenCustomHashMap.MapEntry entry;

      private EntryIterator() {
      }

      public Char2IntOpenCustomHashMap.MapEntry next() {
         return this.entry = Char2IntOpenCustomHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Char2IntMap.Entry> action, int index) {
         action.accept(this.entry = Char2IntOpenCustomHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Char2IntOpenCustomHashMap.MapSpliterator<Consumer<? super Char2IntMap.Entry>, Char2IntOpenCustomHashMap.EntrySpliterator>
      implements ObjectSpliterator<Char2IntMap.Entry> {
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

      final void acceptOnIndex(Consumer<? super Char2IntMap.Entry> action, int index) {
         action.accept(Char2IntOpenCustomHashMap.this.new MapEntry(index));
      }

      final Char2IntOpenCustomHashMap.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Char2IntOpenCustomHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Char2IntOpenCustomHashMap.MapIterator<Consumer<? super Char2IntMap.Entry>>
      implements ObjectIterator<Char2IntMap.Entry> {
      private final Char2IntOpenCustomHashMap.MapEntry entry = Char2IntOpenCustomHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Char2IntOpenCustomHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Char2IntMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Char2IntOpenCustomHashMap.MapIterator<CharConsumer> implements CharIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(CharConsumer action, int index) {
         action.accept(Char2IntOpenCustomHashMap.this.key[index]);
      }

      @Override
      public char nextChar() {
         return Char2IntOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractCharSet {
      private KeySet() {
      }

      @Override
      public CharIterator iterator() {
         return Char2IntOpenCustomHashMap.this.new KeyIterator();
      }

      @Override
      public CharSpliterator spliterator() {
         return Char2IntOpenCustomHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(CharConsumer consumer) {
         if (Char2IntOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Char2IntOpenCustomHashMap.this.key[Char2IntOpenCustomHashMap.this.n]);
         }

         int pos = Char2IntOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            char k = Char2IntOpenCustomHashMap.this.key[pos];
            if (k != 0) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Char2IntOpenCustomHashMap.this.size;
      }

      @Override
      public boolean contains(char k) {
         return Char2IntOpenCustomHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(char k) {
         int oldSize = Char2IntOpenCustomHashMap.this.size;
         Char2IntOpenCustomHashMap.this.remove(k);
         return Char2IntOpenCustomHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Char2IntOpenCustomHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Char2IntOpenCustomHashMap.MapSpliterator<CharConsumer, Char2IntOpenCustomHashMap.KeySpliterator>
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
         action.accept(Char2IntOpenCustomHashMap.this.key[index]);
      }

      final Char2IntOpenCustomHashMap.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Char2IntOpenCustomHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Char2IntMap.Entry, Entry<Character, Integer>, CharIntPair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public char getCharKey() {
         return Char2IntOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public char leftChar() {
         return Char2IntOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public int getIntValue() {
         return Char2IntOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public int rightInt() {
         return Char2IntOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public int setValue(int v) {
         int oldValue = Char2IntOpenCustomHashMap.this.value[this.index];
         Char2IntOpenCustomHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public CharIntPair right(int v) {
         Char2IntOpenCustomHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Character getKey() {
         return Char2IntOpenCustomHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Integer getValue() {
         return Char2IntOpenCustomHashMap.this.value[this.index];
      }

      @Deprecated
      @Override
      public Integer setValue(Integer v) {
         return this.setValue(v.intValue());
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Character, Integer> e = (Entry<Character, Integer>)o;
            return Char2IntOpenCustomHashMap.this.strategy.equals(Char2IntOpenCustomHashMap.this.key[this.index], e.getKey())
               && Char2IntOpenCustomHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return Char2IntOpenCustomHashMap.this.strategy.hashCode(Char2IntOpenCustomHashMap.this.key[this.index])
            ^ Char2IntOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public String toString() {
         return Char2IntOpenCustomHashMap.this.key[this.index] + "=>" + Char2IntOpenCustomHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Char2IntMap.Entry> implements Char2IntMap.FastEntrySet {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Char2IntMap.Entry> iterator() {
         return Char2IntOpenCustomHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Char2IntMap.Entry> fastIterator() {
         return Char2IntOpenCustomHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Char2IntMap.Entry> spliterator() {
         return Char2IntOpenCustomHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Integer) {
               char k = (Character)e.getKey();
               int v = (Integer)e.getValue();
               if (Char2IntOpenCustomHashMap.this.strategy.equals(k, '\u0000')) {
                  return Char2IntOpenCustomHashMap.this.containsNullKey && Char2IntOpenCustomHashMap.this.value[Char2IntOpenCustomHashMap.this.n] == v;
               } else {
                  char[] key = Char2IntOpenCustomHashMap.this.key;
                  char curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(Char2IntOpenCustomHashMap.this.strategy.hashCode(k)) & Char2IntOpenCustomHashMap.this.mask]) == 0) {
                     return false;
                  } else if (Char2IntOpenCustomHashMap.this.strategy.equals(k, curr)) {
                     return Char2IntOpenCustomHashMap.this.value[pos] == v;
                  } else {
                     while ((curr = key[pos = pos + 1 & Char2IntOpenCustomHashMap.this.mask]) != 0) {
                        if (Char2IntOpenCustomHashMap.this.strategy.equals(k, curr)) {
                           return Char2IntOpenCustomHashMap.this.value[pos] == v;
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
               if (e.getValue() != null && e.getValue() instanceof Integer) {
                  char k = (Character)e.getKey();
                  int v = (Integer)e.getValue();
                  if (Char2IntOpenCustomHashMap.this.strategy.equals(k, '\u0000')) {
                     if (Char2IntOpenCustomHashMap.this.containsNullKey && Char2IntOpenCustomHashMap.this.value[Char2IntOpenCustomHashMap.this.n] == v) {
                        Char2IntOpenCustomHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     char[] key = Char2IntOpenCustomHashMap.this.key;
                     char curr;
                     int pos;
                     if ((curr = key[pos = HashCommon.mix(Char2IntOpenCustomHashMap.this.strategy.hashCode(k)) & Char2IntOpenCustomHashMap.this.mask]) == 0) {
                        return false;
                     } else if (Char2IntOpenCustomHashMap.this.strategy.equals(curr, k)) {
                        if (Char2IntOpenCustomHashMap.this.value[pos] == v) {
                           Char2IntOpenCustomHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while ((curr = key[pos = pos + 1 & Char2IntOpenCustomHashMap.this.mask]) != 0) {
                           if (Char2IntOpenCustomHashMap.this.strategy.equals(curr, k) && Char2IntOpenCustomHashMap.this.value[pos] == v) {
                              Char2IntOpenCustomHashMap.this.removeEntry(pos);
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
         return Char2IntOpenCustomHashMap.this.size;
      }

      @Override
      public void clear() {
         Char2IntOpenCustomHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Char2IntMap.Entry> consumer) {
         if (Char2IntOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Char2IntOpenCustomHashMap.this.new MapEntry(Char2IntOpenCustomHashMap.this.n));
         }

         int pos = Char2IntOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Char2IntOpenCustomHashMap.this.key[pos] != 0) {
               consumer.accept(Char2IntOpenCustomHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Char2IntMap.Entry> consumer) {
         Char2IntOpenCustomHashMap.MapEntry entry = Char2IntOpenCustomHashMap.this.new MapEntry();
         if (Char2IntOpenCustomHashMap.this.containsNullKey) {
            entry.index = Char2IntOpenCustomHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Char2IntOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Char2IntOpenCustomHashMap.this.key[pos] != 0) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Char2IntOpenCustomHashMap.this.n;
      int last = -1;
      int c = Char2IntOpenCustomHashMap.this.size;
      boolean mustReturnNullKey = Char2IntOpenCustomHashMap.this.containsNullKey;
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
               return this.last = Char2IntOpenCustomHashMap.this.n;
            } else {
               char[] key = Char2IntOpenCustomHashMap.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               char k = this.wrapped.getChar(-this.pos - 1);
               int p = HashCommon.mix(Char2IntOpenCustomHashMap.this.strategy.hashCode(k)) & Char2IntOpenCustomHashMap.this.mask;

               while (!Char2IntOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Char2IntOpenCustomHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Char2IntOpenCustomHashMap.this.n);
            this.c--;
         }

         char[] key = Char2IntOpenCustomHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               char k = this.wrapped.getChar(-this.pos - 1);
               int p = HashCommon.mix(Char2IntOpenCustomHashMap.this.strategy.hashCode(k)) & Char2IntOpenCustomHashMap.this.mask;

               while (!Char2IntOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Char2IntOpenCustomHashMap.this.mask;
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
         char[] key = Char2IntOpenCustomHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            char curr;
            for (pos = pos + 1 & Char2IntOpenCustomHashMap.this.mask; (curr = key[pos]) != 0; pos = pos + 1 & Char2IntOpenCustomHashMap.this.mask) {
               int slot = HashCommon.mix(Char2IntOpenCustomHashMap.this.strategy.hashCode(curr)) & Char2IntOpenCustomHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new CharArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Char2IntOpenCustomHashMap.this.value[last] = Char2IntOpenCustomHashMap.this.value[pos];
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
            if (this.last == Char2IntOpenCustomHashMap.this.n) {
               Char2IntOpenCustomHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Char2IntOpenCustomHashMap.this.remove(this.wrapped.getChar(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Char2IntOpenCustomHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Char2IntOpenCustomHashMap.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Char2IntOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Char2IntOpenCustomHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Char2IntOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Char2IntOpenCustomHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Char2IntOpenCustomHashMap.this.n);
            return true;
         } else {
            for (char[] key = Char2IntOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
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
            this.acceptOnIndex(action, Char2IntOpenCustomHashMap.this.n);
         }

         for (char[] key = Char2IntOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
            if (key[this.pos] != 0) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Char2IntOpenCustomHashMap.this.size - this.c
            : Math.min(
               (long)(Char2IntOpenCustomHashMap.this.size - this.c),
               (long)((double)Char2IntOpenCustomHashMap.this.realSize() / Char2IntOpenCustomHashMap.this.n * (this.max - this.pos))
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

            char[] key = Char2IntOpenCustomHashMap.this.key;

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

   private final class ValueIterator extends Char2IntOpenCustomHashMap.MapIterator<IntConsumer> implements IntIterator {
      public ValueIterator() {
      }

      final void acceptOnIndex(IntConsumer action, int index) {
         action.accept(Char2IntOpenCustomHashMap.this.value[index]);
      }

      @Override
      public int nextInt() {
         return Char2IntOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Char2IntOpenCustomHashMap.MapSpliterator<IntConsumer, Char2IntOpenCustomHashMap.ValueSpliterator>
      implements IntSpliterator {
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

      final void acceptOnIndex(IntConsumer action, int index) {
         action.accept(Char2IntOpenCustomHashMap.this.value[index]);
      }

      final Char2IntOpenCustomHashMap.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Char2IntOpenCustomHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
