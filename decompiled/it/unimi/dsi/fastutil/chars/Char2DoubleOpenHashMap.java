package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.AbstractDoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleCollection;
import it.unimi.dsi.fastutil.doubles.DoubleIterator;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
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
import java.util.function.DoubleConsumer;
import java.util.function.IntFunction;
import java.util.function.IntToDoubleFunction;

public class Char2DoubleOpenHashMap extends AbstractChar2DoubleMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient char[] key;
   protected transient double[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Char2DoubleMap.FastEntrySet entries;
   protected transient CharSet keys;
   protected transient DoubleCollection values;

   public Char2DoubleOpenHashMap(int expected, float f) {
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
         this.value = new double[this.n + 1];
      }
   }

   public Char2DoubleOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Char2DoubleOpenHashMap() {
      this(16, 0.75F);
   }

   public Char2DoubleOpenHashMap(Map<? extends Character, ? extends Double> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Char2DoubleOpenHashMap(Map<? extends Character, ? extends Double> m) {
      this(m, 0.75F);
   }

   public Char2DoubleOpenHashMap(Char2DoubleMap m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Char2DoubleOpenHashMap(Char2DoubleMap m) {
      this(m, 0.75F);
   }

   public Char2DoubleOpenHashMap(char[] k, double[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Char2DoubleOpenHashMap(char[] k, double[] v) {
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

   private double removeEntry(int pos) {
      double oldValue = this.value[pos];
      this.size--;
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private double removeNullEntry() {
      this.containsNullKey = false;
      double oldValue = this.value[this.n];
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends Character, ? extends Double> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(char k) {
      if (k == 0) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         char[] key = this.key;
         char curr;
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

   private void insert(int pos, char k, double v) {
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
   public double put(char k, double v) {
      int pos = this.find(k);
      if (pos < 0) {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      } else {
         double oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   private double addToValue(int pos, double incr) {
      double oldValue = this.value[pos];
      this.value[pos] = oldValue + incr;
      return oldValue;
   }

   public double addTo(char k, double incr) {
      int pos;
      if (k == 0) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, incr);
         }

         pos = this.n;
         this.containsNullKey = true;
      } else {
         char[] key = this.key;
         char curr;
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
      char[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         char curr;
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
   public double remove(char k) {
      if (k == 0) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         char[] key = this.key;
         char curr;
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
   public double get(char k) {
      if (k == 0) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         char[] key = this.key;
         char curr;
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
   public boolean containsKey(char k) {
      if (k == 0) {
         return this.containsNullKey;
      } else {
         char[] key = this.key;
         char curr;
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
   public boolean containsValue(double v) {
      double[] value = this.value;
      char[] key = this.key;
      if (this.containsNullKey && Double.doubleToLongBits(value[this.n]) == Double.doubleToLongBits(v)) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (key[i] != 0 && Double.doubleToLongBits(value[i]) == Double.doubleToLongBits(v)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public double getOrDefault(char k, double defaultValue) {
      if (k == 0) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         char[] key = this.key;
         char curr;
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
   public double putIfAbsent(char k, double v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(char k, double v) {
      if (k == 0) {
         if (this.containsNullKey && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[this.n])) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         char[] key = this.key;
         char curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
            return false;
         } else if (k == curr && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[pos])) {
            this.removeEntry(pos);
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (k == curr && Double.doubleToLongBits(v) == Double.doubleToLongBits(this.value[pos])) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(char k, double oldValue, double v) {
      int pos = this.find(k);
      if (pos >= 0 && Double.doubleToLongBits(oldValue) == Double.doubleToLongBits(this.value[pos])) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public double replace(char k, double v) {
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         double oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   @Override
   public double computeIfAbsent(char k, IntToDoubleFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         double newValue = mappingFunction.applyAsDouble(k);
         this.insert(-pos - 1, k, newValue);
         return newValue;
      }
   }

   @Override
   public double computeIfAbsent(char key, Char2DoubleFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         double newValue = mappingFunction.get(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public double computeIfAbsentNullable(char k, IntFunction<? extends Double> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         Double newValue = mappingFunction.apply(k);
         if (newValue == null) {
            return this.defRetValue;
         } else {
            double v = newValue;
            this.insert(-pos - 1, k, v);
            return v;
         }
      }
   }

   @Override
   public double computeIfPresent(char k, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Double newValue = remappingFunction.apply(k, this.value[pos]);
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
   public double compute(char k, BiFunction<? super Character, ? super Double, ? extends Double> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Double newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
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
         double newVal = newValue;
         if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
         } else {
            return this.value[pos] = newVal;
         }
      }
   }

   @Override
   public double merge(char k, double v, BiFunction<? super Double, ? super Double, ? extends Double> remappingFunction) {
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
         Double newValue = remappingFunction.apply(this.value[pos], v);
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

   public Char2DoubleMap.FastEntrySet char2DoubleEntrySet() {
      if (this.entries == null) {
         this.entries = new Char2DoubleOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public CharSet keySet() {
      if (this.keys == null) {
         this.keys = new Char2DoubleOpenHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public DoubleCollection values() {
      if (this.values == null) {
         this.values = new AbstractDoubleCollection() {
            @Override
            public DoubleIterator iterator() {
               return Char2DoubleOpenHashMap.this.new ValueIterator();
            }

            @Override
            public DoubleSpliterator spliterator() {
               return Char2DoubleOpenHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(DoubleConsumer consumer) {
               if (Char2DoubleOpenHashMap.this.containsNullKey) {
                  consumer.accept(Char2DoubleOpenHashMap.this.value[Char2DoubleOpenHashMap.this.n]);
               }

               int pos = Char2DoubleOpenHashMap.this.n;

               while (pos-- != 0) {
                  if (Char2DoubleOpenHashMap.this.key[pos] != 0) {
                     consumer.accept(Char2DoubleOpenHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Char2DoubleOpenHashMap.this.size;
            }

            @Override
            public boolean contains(double v) {
               return Char2DoubleOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Char2DoubleOpenHashMap.this.clear();
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
      double[] value = this.value;
      int mask = newN - 1;
      char[] newKey = new char[newN + 1];
      double[] newValue = new double[newN + 1];
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

   public Char2DoubleOpenHashMap clone() {
      Char2DoubleOpenHashMap c;
      try {
         c = (Char2DoubleOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (char[])this.key.clone();
      c.value = (double[])this.value.clone();
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
         var5 ^= HashCommon.double2int(this.value[i]);
         h += var5;
      }

      if (this.containsNullKey) {
         h += HashCommon.double2int(this.value[this.n]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      char[] key = this.key;
      double[] value = this.value;
      Char2DoubleOpenHashMap.EntryIterator i = new Char2DoubleOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeChar(key[e]);
         s.writeDouble(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      char[] key = this.key = new char[this.n + 1];
      double[] value = this.value = new double[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         char k = s.readChar();
         double v = s.readDouble();
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
      extends Char2DoubleOpenHashMap.MapIterator<Consumer<? super Char2DoubleMap.Entry>>
      implements ObjectIterator<Char2DoubleMap.Entry> {
      private Char2DoubleOpenHashMap.MapEntry entry;

      private EntryIterator() {
      }

      public Char2DoubleOpenHashMap.MapEntry next() {
         return this.entry = Char2DoubleOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Char2DoubleMap.Entry> action, int index) {
         action.accept(this.entry = Char2DoubleOpenHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Char2DoubleOpenHashMap.MapSpliterator<Consumer<? super Char2DoubleMap.Entry>, Char2DoubleOpenHashMap.EntrySpliterator>
      implements ObjectSpliterator<Char2DoubleMap.Entry> {
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

      final void acceptOnIndex(Consumer<? super Char2DoubleMap.Entry> action, int index) {
         action.accept(Char2DoubleOpenHashMap.this.new MapEntry(index));
      }

      final Char2DoubleOpenHashMap.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Char2DoubleOpenHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Char2DoubleOpenHashMap.MapIterator<Consumer<? super Char2DoubleMap.Entry>>
      implements ObjectIterator<Char2DoubleMap.Entry> {
      private final Char2DoubleOpenHashMap.MapEntry entry = Char2DoubleOpenHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Char2DoubleOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Char2DoubleMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Char2DoubleOpenHashMap.MapIterator<CharConsumer> implements CharIterator {
      public KeyIterator() {
      }

      final void acceptOnIndex(CharConsumer action, int index) {
         action.accept(Char2DoubleOpenHashMap.this.key[index]);
      }

      @Override
      public char nextChar() {
         return Char2DoubleOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractCharSet {
      private KeySet() {
      }

      @Override
      public CharIterator iterator() {
         return Char2DoubleOpenHashMap.this.new KeyIterator();
      }

      @Override
      public CharSpliterator spliterator() {
         return Char2DoubleOpenHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(CharConsumer consumer) {
         if (Char2DoubleOpenHashMap.this.containsNullKey) {
            consumer.accept(Char2DoubleOpenHashMap.this.key[Char2DoubleOpenHashMap.this.n]);
         }

         int pos = Char2DoubleOpenHashMap.this.n;

         while (pos-- != 0) {
            char k = Char2DoubleOpenHashMap.this.key[pos];
            if (k != 0) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Char2DoubleOpenHashMap.this.size;
      }

      @Override
      public boolean contains(char k) {
         return Char2DoubleOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(char k) {
         int oldSize = Char2DoubleOpenHashMap.this.size;
         Char2DoubleOpenHashMap.this.remove(k);
         return Char2DoubleOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Char2DoubleOpenHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Char2DoubleOpenHashMap.MapSpliterator<CharConsumer, Char2DoubleOpenHashMap.KeySpliterator>
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
         action.accept(Char2DoubleOpenHashMap.this.key[index]);
      }

      final Char2DoubleOpenHashMap.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Char2DoubleOpenHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Char2DoubleMap.Entry, Entry<Character, Double>, CharDoublePair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public char getCharKey() {
         return Char2DoubleOpenHashMap.this.key[this.index];
      }

      @Override
      public char leftChar() {
         return Char2DoubleOpenHashMap.this.key[this.index];
      }

      @Override
      public double getDoubleValue() {
         return Char2DoubleOpenHashMap.this.value[this.index];
      }

      @Override
      public double rightDouble() {
         return Char2DoubleOpenHashMap.this.value[this.index];
      }

      @Override
      public double setValue(double v) {
         double oldValue = Char2DoubleOpenHashMap.this.value[this.index];
         Char2DoubleOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public CharDoublePair right(double v) {
         Char2DoubleOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Character getKey() {
         return Char2DoubleOpenHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Double getValue() {
         return Char2DoubleOpenHashMap.this.value[this.index];
      }

      @Deprecated
      @Override
      public Double setValue(Double v) {
         return this.setValue(v.doubleValue());
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Character, Double> e = (Entry<Character, Double>)o;
            return Char2DoubleOpenHashMap.this.key[this.index] == e.getKey()
               && Double.doubleToLongBits(Char2DoubleOpenHashMap.this.value[this.index]) == Double.doubleToLongBits(e.getValue());
         }
      }

      @Override
      public int hashCode() {
         return Char2DoubleOpenHashMap.this.key[this.index] ^ HashCommon.double2int(Char2DoubleOpenHashMap.this.value[this.index]);
      }

      @Override
      public String toString() {
         return Char2DoubleOpenHashMap.this.key[this.index] + "=>" + Char2DoubleOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Char2DoubleMap.Entry> implements Char2DoubleMap.FastEntrySet {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Char2DoubleMap.Entry> iterator() {
         return Char2DoubleOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Char2DoubleMap.Entry> fastIterator() {
         return Char2DoubleOpenHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Char2DoubleMap.Entry> spliterator() {
         return Char2DoubleOpenHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Double) {
               char k = (Character)e.getKey();
               double v = (Double)e.getValue();
               if (k == 0) {
                  return Char2DoubleOpenHashMap.this.containsNullKey
                     && Double.doubleToLongBits(Char2DoubleOpenHashMap.this.value[Char2DoubleOpenHashMap.this.n]) == Double.doubleToLongBits(v);
               } else {
                  char[] key = Char2DoubleOpenHashMap.this.key;
                  char curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix((int)k) & Char2DoubleOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (k == curr) {
                     return Double.doubleToLongBits(Char2DoubleOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v);
                  } else {
                     while ((curr = key[pos = pos + 1 & Char2DoubleOpenHashMap.this.mask]) != 0) {
                        if (k == curr) {
                           return Double.doubleToLongBits(Char2DoubleOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v);
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
               if (e.getValue() != null && e.getValue() instanceof Double) {
                  char k = (Character)e.getKey();
                  double v = (Double)e.getValue();
                  if (k == 0) {
                     if (Char2DoubleOpenHashMap.this.containsNullKey
                        && Double.doubleToLongBits(Char2DoubleOpenHashMap.this.value[Char2DoubleOpenHashMap.this.n]) == Double.doubleToLongBits(v)) {
                        Char2DoubleOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     char[] key = Char2DoubleOpenHashMap.this.key;
                     char curr;
                     int pos;
                     if ((curr = key[pos = HashCommon.mix((int)k) & Char2DoubleOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (curr == k) {
                        if (Double.doubleToLongBits(Char2DoubleOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v)) {
                           Char2DoubleOpenHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while ((curr = key[pos = pos + 1 & Char2DoubleOpenHashMap.this.mask]) != 0) {
                           if (curr == k && Double.doubleToLongBits(Char2DoubleOpenHashMap.this.value[pos]) == Double.doubleToLongBits(v)) {
                              Char2DoubleOpenHashMap.this.removeEntry(pos);
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
         return Char2DoubleOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Char2DoubleOpenHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Char2DoubleMap.Entry> consumer) {
         if (Char2DoubleOpenHashMap.this.containsNullKey) {
            consumer.accept(Char2DoubleOpenHashMap.this.new MapEntry(Char2DoubleOpenHashMap.this.n));
         }

         int pos = Char2DoubleOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Char2DoubleOpenHashMap.this.key[pos] != 0) {
               consumer.accept(Char2DoubleOpenHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Char2DoubleMap.Entry> consumer) {
         Char2DoubleOpenHashMap.MapEntry entry = Char2DoubleOpenHashMap.this.new MapEntry();
         if (Char2DoubleOpenHashMap.this.containsNullKey) {
            entry.index = Char2DoubleOpenHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Char2DoubleOpenHashMap.this.n;

         while (pos-- != 0) {
            if (Char2DoubleOpenHashMap.this.key[pos] != 0) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Char2DoubleOpenHashMap.this.n;
      int last = -1;
      int c = Char2DoubleOpenHashMap.this.size;
      boolean mustReturnNullKey = Char2DoubleOpenHashMap.this.containsNullKey;
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
               return this.last = Char2DoubleOpenHashMap.this.n;
            } else {
               char[] key = Char2DoubleOpenHashMap.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != 0) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               char k = this.wrapped.getChar(-this.pos - 1);
               int p = HashCommon.mix((int)k) & Char2DoubleOpenHashMap.this.mask;

               while (k != key[p]) {
                  p = p + 1 & Char2DoubleOpenHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Char2DoubleOpenHashMap.this.n);
            this.c--;
         }

         char[] key = Char2DoubleOpenHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               char k = this.wrapped.getChar(-this.pos - 1);
               int p = HashCommon.mix((int)k) & Char2DoubleOpenHashMap.this.mask;

               while (k != key[p]) {
                  p = p + 1 & Char2DoubleOpenHashMap.this.mask;
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
         char[] key = Char2DoubleOpenHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            char curr;
            for (pos = pos + 1 & Char2DoubleOpenHashMap.this.mask; (curr = key[pos]) != 0; pos = pos + 1 & Char2DoubleOpenHashMap.this.mask) {
               int slot = HashCommon.mix((int)curr) & Char2DoubleOpenHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new CharArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Char2DoubleOpenHashMap.this.value[last] = Char2DoubleOpenHashMap.this.value[pos];
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
            if (this.last == Char2DoubleOpenHashMap.this.n) {
               Char2DoubleOpenHashMap.this.containsNullKey = false;
            } else {
               if (this.pos < 0) {
                  Char2DoubleOpenHashMap.this.remove(this.wrapped.getChar(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Char2DoubleOpenHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Char2DoubleOpenHashMap.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Char2DoubleOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Char2DoubleOpenHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Char2DoubleOpenHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Char2DoubleOpenHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Char2DoubleOpenHashMap.this.n);
            return true;
         } else {
            for (char[] key = Char2DoubleOpenHashMap.this.key; this.pos < this.max; this.pos++) {
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
            this.acceptOnIndex(action, Char2DoubleOpenHashMap.this.n);
         }

         for (char[] key = Char2DoubleOpenHashMap.this.key; this.pos < this.max; this.pos++) {
            if (key[this.pos] != 0) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Char2DoubleOpenHashMap.this.size - this.c
            : Math.min(
               (long)(Char2DoubleOpenHashMap.this.size - this.c),
               (long)((double)Char2DoubleOpenHashMap.this.realSize() / Char2DoubleOpenHashMap.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
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

            char[] key = Char2DoubleOpenHashMap.this.key;

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

   private final class ValueIterator extends Char2DoubleOpenHashMap.MapIterator<DoubleConsumer> implements DoubleIterator {
      public ValueIterator() {
      }

      final void acceptOnIndex(DoubleConsumer action, int index) {
         action.accept(Char2DoubleOpenHashMap.this.value[index]);
      }

      @Override
      public double nextDouble() {
         return Char2DoubleOpenHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Char2DoubleOpenHashMap.MapSpliterator<DoubleConsumer, Char2DoubleOpenHashMap.ValueSpliterator>
      implements DoubleSpliterator {
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

      final void acceptOnIndex(DoubleConsumer action, int index) {
         action.accept(Char2DoubleOpenHashMap.this.value[index]);
      }

      final Char2DoubleOpenHashMap.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Char2DoubleOpenHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
