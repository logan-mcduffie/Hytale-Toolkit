package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.objects.AbstractObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectBidirectionalIterator;
import it.unimi.dsi.fastutil.objects.ObjectListIterator;
import it.unimi.dsi.fastutil.objects.ObjectSortedSet;
import it.unimi.dsi.fastutil.objects.ObjectSpliterator;
import it.unimi.dsi.fastutil.objects.ObjectSpliterators;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Map.Entry;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.IntFunction;
import java.util.function.IntUnaryOperator;

public class Char2CharLinkedOpenHashMap extends AbstractChar2CharSortedMap implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient char[] key;
   protected transient char[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected transient int first = -1;
   protected transient int last = -1;
   protected transient long[] link;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Char2CharSortedMap.FastSortedEntrySet entries;
   protected transient CharSortedSet keys;
   protected transient CharCollection values;

   public Char2CharLinkedOpenHashMap(int expected, float f) {
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
         this.value = new char[this.n + 1];
         this.link = new long[this.n + 1];
      }
   }

   public Char2CharLinkedOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Char2CharLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Char2CharLinkedOpenHashMap(Map<? extends Character, ? extends Character> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Char2CharLinkedOpenHashMap(Map<? extends Character, ? extends Character> m) {
      this(m, 0.75F);
   }

   public Char2CharLinkedOpenHashMap(Char2CharMap m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Char2CharLinkedOpenHashMap(Char2CharMap m) {
      this(m, 0.75F);
   }

   public Char2CharLinkedOpenHashMap(char[] k, char[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Char2CharLinkedOpenHashMap(char[] k, char[] v) {
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

   private char removeEntry(int pos) {
      char oldValue = this.value[pos];
      this.size--;
      this.fixPointers(pos);
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   private char removeNullEntry() {
      this.containsNullKey = false;
      char oldValue = this.value[this.n];
      this.size--;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends Character, ? extends Character> m) {
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

   private void insert(int pos, char k, char v) {
      if (pos == this.n) {
         this.containsNullKey = true;
      }

      this.key[pos] = k;
      this.value[pos] = v;
      if (this.size == 0) {
         this.first = this.last = pos;
         this.link[pos] = -1L;
      } else {
         this.link[this.last] = this.link[this.last] ^ (this.link[this.last] ^ pos & 4294967295L) & 4294967295L;
         this.link[pos] = (this.last & 4294967295L) << 32 | 4294967295L;
         this.last = pos;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }
   }

   @Override
   public char put(char k, char v) {
      int pos = this.find(k);
      if (pos < 0) {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      } else {
         char oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   private char addToValue(int pos, char incr) {
      char oldValue = this.value[pos];
      this.value[pos] = (char)(oldValue + incr);
      return oldValue;
   }

   public char addTo(char k, char incr) {
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
      this.value[pos] = (char)(this.defRetValue + incr);
      if (this.size == 0) {
         this.first = this.last = pos;
         this.link[pos] = -1L;
      } else {
         this.link[this.last] = this.link[this.last] ^ (this.link[this.last] ^ pos & 4294967295L) & 4294967295L;
         this.link[pos] = (this.last & 4294967295L) << 32 | 4294967295L;
         this.last = pos;
      }

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
               this.fixPointers(pos, last);
               continue label30;
            }
         }

         key[last] = 0;
         return;
      }
   }

   @Override
   public char remove(char k) {
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

   private char setValue(int pos, char v) {
      char oldValue = this.value[pos];
      this.value[pos] = v;
      return oldValue;
   }

   public char removeFirstChar() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         int pos = this.first;
         if (this.size == 1) {
            this.first = this.last = -1;
         } else {
            this.first = (int)this.link[pos];
            if (0 <= this.first) {
               this.link[this.first] = this.link[this.first] | -4294967296L;
            }
         }

         this.size--;
         char v = this.value[pos];
         if (pos == this.n) {
            this.containsNullKey = false;
         } else {
            this.shiftKeys(pos);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return v;
      }
   }

   public char removeLastChar() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         int pos = this.last;
         if (this.size == 1) {
            this.first = this.last = -1;
         } else {
            this.last = (int)(this.link[pos] >>> 32);
            if (0 <= this.last) {
               this.link[this.last] = this.link[this.last] | 4294967295L;
            }
         }

         this.size--;
         char v = this.value[pos];
         if (pos == this.n) {
            this.containsNullKey = false;
         } else {
            this.shiftKeys(pos);
         }

         if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
            this.rehash(this.n / 2);
         }

         return v;
      }
   }

   private void moveIndexToFirst(int i) {
      if (this.size != 1 && this.first != i) {
         if (this.last == i) {
            this.last = (int)(this.link[i] >>> 32);
            this.link[this.last] = this.link[this.last] | 4294967295L;
         } else {
            long linki = this.link[i];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            this.link[prev] = this.link[prev] ^ (this.link[prev] ^ linki & 4294967295L) & 4294967295L;
            this.link[next] = this.link[next] ^ (this.link[next] ^ linki & -4294967296L) & -4294967296L;
         }

         this.link[this.first] = this.link[this.first] ^ (this.link[this.first] ^ (i & 4294967295L) << 32) & -4294967296L;
         this.link[i] = -4294967296L | this.first & 4294967295L;
         this.first = i;
      }
   }

   private void moveIndexToLast(int i) {
      if (this.size != 1 && this.last != i) {
         if (this.first == i) {
            this.first = (int)this.link[i];
            this.link[this.first] = this.link[this.first] | -4294967296L;
         } else {
            long linki = this.link[i];
            int prev = (int)(linki >>> 32);
            int next = (int)linki;
            this.link[prev] = this.link[prev] ^ (this.link[prev] ^ linki & 4294967295L) & 4294967295L;
            this.link[next] = this.link[next] ^ (this.link[next] ^ linki & -4294967296L) & -4294967296L;
         }

         this.link[this.last] = this.link[this.last] ^ (this.link[this.last] ^ i & 4294967295L) & 4294967295L;
         this.link[i] = (this.last & 4294967295L) << 32 | 4294967295L;
         this.last = i;
      }
   }

   public char getAndMoveToFirst(char k) {
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         char[] key = this.key;
         char curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (k == curr) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (k == curr) {
                  this.moveIndexToFirst(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public char getAndMoveToLast(char k) {
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         char[] key = this.key;
         char curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
            return this.defRetValue;
         } else if (k == curr) {
            this.moveIndexToLast(pos);
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (k == curr) {
                  this.moveIndexToLast(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public char putAndMoveToFirst(char k, char v) {
      int pos;
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         char[] key = this.key;
         char curr;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) != 0) {
            if (curr == k) {
               this.moveIndexToFirst(pos);
               return this.setValue(pos, v);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (curr == k) {
                  this.moveIndexToFirst(pos);
                  return this.setValue(pos, v);
               }
            }
         }
      }

      this.key[pos] = k;
      this.value[pos] = v;
      if (this.size == 0) {
         this.first = this.last = pos;
         this.link[pos] = -1L;
      } else {
         this.link[this.first] = this.link[this.first] ^ (this.link[this.first] ^ (pos & 4294967295L) << 32) & -4294967296L;
         this.link[pos] = -4294967296L | this.first & 4294967295L;
         this.first = pos;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return this.defRetValue;
   }

   public char putAndMoveToLast(char k, char v) {
      int pos;
      if (k == 0) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         char[] key = this.key;
         char curr;
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) != 0) {
            if (curr == k) {
               this.moveIndexToLast(pos);
               return this.setValue(pos, v);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (curr == k) {
                  this.moveIndexToLast(pos);
                  return this.setValue(pos, v);
               }
            }
         }
      }

      this.key[pos] = k;
      this.value[pos] = v;
      if (this.size == 0) {
         this.first = this.last = pos;
         this.link[pos] = -1L;
      } else {
         this.link[this.last] = this.link[this.last] ^ (this.link[this.last] ^ pos & 4294967295L) & 4294967295L;
         this.link[pos] = (this.last & 4294967295L) << 32 | 4294967295L;
         this.last = pos;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size, this.f));
      }

      return this.defRetValue;
   }

   @Override
   public char get(char k) {
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
   public boolean containsValue(char v) {
      char[] value = this.value;
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
   public char getOrDefault(char k, char defaultValue) {
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
   public char putIfAbsent(char k, char v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(char k, char v) {
      if (k == 0) {
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
         if ((curr = key[pos = HashCommon.mix((int)k) & this.mask]) == 0) {
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
   public boolean replace(char k, char oldValue, char v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public char replace(char k, char v) {
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         char oldValue = this.value[pos];
         this.value[pos] = v;
         return oldValue;
      }
   }

   @Override
   public char computeIfAbsent(char k, IntUnaryOperator mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         char newValue = SafeMath.safeIntToChar(mappingFunction.applyAsInt(k));
         this.insert(-pos - 1, k, newValue);
         return newValue;
      }
   }

   @Override
   public char computeIfAbsent(char key, Char2CharFunction mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         char newValue = mappingFunction.get(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public char computeIfAbsentNullable(char k, IntFunction<? extends Character> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         Character newValue = mappingFunction.apply(k);
         if (newValue == null) {
            return this.defRetValue;
         } else {
            char v = newValue;
            this.insert(-pos - 1, k, v);
            return v;
         }
      }
   }

   @Override
   public char computeIfPresent(char k, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Character newValue = remappingFunction.apply(k, this.value[pos]);
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
   public char compute(char k, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Character newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
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
         char newVal = newValue;
         if (pos < 0) {
            this.insert(-pos - 1, k, newVal);
            return newVal;
         } else {
            return this.value[pos] = newVal;
         }
      }
   }

   @Override
   public char merge(char k, char v, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
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
         Character newValue = remappingFunction.apply(this.value[pos], v);
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
         this.first = this.last = -1;
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

   protected void fixPointers(int i) {
      if (this.size == 0) {
         this.first = this.last = -1;
      } else if (this.first == i) {
         this.first = (int)this.link[i];
         if (0 <= this.first) {
            this.link[this.first] = this.link[this.first] | -4294967296L;
         }
      } else if (this.last == i) {
         this.last = (int)(this.link[i] >>> 32);
         if (0 <= this.last) {
            this.link[this.last] = this.link[this.last] | 4294967295L;
         }
      } else {
         long linki = this.link[i];
         int prev = (int)(linki >>> 32);
         int next = (int)linki;
         this.link[prev] = this.link[prev] ^ (this.link[prev] ^ linki & 4294967295L) & 4294967295L;
         this.link[next] = this.link[next] ^ (this.link[next] ^ linki & -4294967296L) & -4294967296L;
      }
   }

   protected void fixPointers(int s, int d) {
      if (this.size == 1) {
         this.first = this.last = d;
         this.link[d] = -1L;
      } else if (this.first == s) {
         this.first = d;
         this.link[(int)this.link[s]] = this.link[(int)this.link[s]] ^ (this.link[(int)this.link[s]] ^ (d & 4294967295L) << 32) & -4294967296L;
         this.link[d] = this.link[s];
      } else if (this.last == s) {
         this.last = d;
         this.link[(int)(this.link[s] >>> 32)] = this.link[(int)(this.link[s] >>> 32)]
            ^ (this.link[(int)(this.link[s] >>> 32)] ^ d & 4294967295L) & 4294967295L;
         this.link[d] = this.link[s];
      } else {
         long links = this.link[s];
         int prev = (int)(links >>> 32);
         int next = (int)links;
         this.link[prev] = this.link[prev] ^ (this.link[prev] ^ d & 4294967295L) & 4294967295L;
         this.link[next] = this.link[next] ^ (this.link[next] ^ (d & 4294967295L) << 32) & -4294967296L;
         this.link[d] = links;
      }
   }

   @Override
   public char firstCharKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   @Override
   public char lastCharKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   @Override
   public Char2CharSortedMap tailMap(char from) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Char2CharSortedMap headMap(char to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Char2CharSortedMap subMap(char from, char to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public CharComparator comparator() {
      return null;
   }

   public Char2CharSortedMap.FastSortedEntrySet char2CharEntrySet() {
      if (this.entries == null) {
         this.entries = new Char2CharLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public CharSortedSet keySet() {
      if (this.keys == null) {
         this.keys = new Char2CharLinkedOpenHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public CharCollection values() {
      if (this.values == null) {
         this.values = new AbstractCharCollection() {
            private static final int SPLITERATOR_CHARACTERISTICS = 336;

            @Override
            public CharIterator iterator() {
               return Char2CharLinkedOpenHashMap.this.new ValueIterator();
            }

            @Override
            public CharSpliterator spliterator() {
               return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Char2CharLinkedOpenHashMap.this), 336);
            }

            @Override
            public void forEach(CharConsumer consumer) {
               int i = Char2CharLinkedOpenHashMap.this.size;
               int next = Char2CharLinkedOpenHashMap.this.first;

               while (i-- != 0) {
                  int curr = next;
                  next = (int)Char2CharLinkedOpenHashMap.this.link[next];
                  consumer.accept(Char2CharLinkedOpenHashMap.this.value[curr]);
               }
            }

            @Override
            public int size() {
               return Char2CharLinkedOpenHashMap.this.size;
            }

            @Override
            public boolean contains(char v) {
               return Char2CharLinkedOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Char2CharLinkedOpenHashMap.this.clear();
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
      char[] value = this.value;
      int mask = newN - 1;
      char[] newKey = new char[newN + 1];
      char[] newValue = new char[newN + 1];
      int i = this.first;
      int prev = -1;
      int newPrev = -1;
      long[] link = this.link;
      long[] newLink = new long[newN + 1];
      this.first = -1;
      int j = this.size;

      while (j-- != 0) {
         int pos;
         if (key[i] == 0) {
            pos = newN;
         } else {
            pos = HashCommon.mix(key[i]) & mask;

            while (newKey[pos] != 0) {
               pos = pos + 1 & mask;
            }
         }

         newKey[pos] = key[i];
         newValue[pos] = value[i];
         if (prev != -1) {
            newLink[newPrev] ^= (newLink[newPrev] ^ pos & 4294967295L) & 4294967295L;
            newLink[pos] ^= (newLink[pos] ^ (newPrev & 4294967295L) << 32) & -4294967296L;
            newPrev = pos;
         } else {
            newPrev = this.first = pos;
            newLink[pos] = -1L;
         }

         int t = i;
         i = (int)link[i];
         prev = t;
      }

      this.link = newLink;
      this.last = newPrev;
      if (newPrev != -1) {
         newLink[newPrev] |= 4294967295L;
      }

      this.n = newN;
      this.mask = mask;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = newKey;
      this.value = newValue;
   }

   public Char2CharLinkedOpenHashMap clone() {
      Char2CharLinkedOpenHashMap c;
      try {
         c = (Char2CharLinkedOpenHashMap)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (char[])this.key.clone();
      c.value = (char[])this.value.clone();
      c.link = (long[])this.link.clone();
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
         var5 ^= this.value[i];
         h += var5;
      }

      if (this.containsNullKey) {
         h += this.value[this.n];
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      char[] key = this.key;
      char[] value = this.value;
      Char2CharLinkedOpenHashMap.EntryIterator i = new Char2CharLinkedOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeChar(key[e]);
         s.writeChar(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      char[] key = this.key = new char[this.n + 1];
      char[] value = this.value = new char[this.n + 1];
      long[] link = this.link = new long[this.n + 1];
      int prev = -1;
      this.first = this.last = -1;
      int i = this.size;

      while (i-- != 0) {
         char k = s.readChar();
         char v = s.readChar();
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
         if (this.first != -1) {
            link[prev] ^= (link[prev] ^ pos & 4294967295L) & 4294967295L;
            link[pos] ^= (link[pos] ^ (prev & 4294967295L) << 32) & -4294967296L;
            prev = pos;
         } else {
            prev = this.first = pos;
            link[pos] |= -4294967296L;
         }
      }

      this.last = prev;
      if (prev != -1) {
         link[prev] |= 4294967295L;
      }
   }

   private void checkTable() {
   }

   private final class EntryIterator
      extends Char2CharLinkedOpenHashMap.MapIterator<Consumer<? super Char2CharMap.Entry>>
      implements ObjectListIterator<Char2CharMap.Entry> {
      private Char2CharLinkedOpenHashMap.MapEntry entry;

      public EntryIterator() {
      }

      public EntryIterator(char from) {
         super(from);
      }

      final void acceptOnIndex(Consumer<? super Char2CharMap.Entry> action, int index) {
         action.accept(Char2CharLinkedOpenHashMap.this.new MapEntry(index));
      }

      public Char2CharLinkedOpenHashMap.MapEntry next() {
         return this.entry = Char2CharLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Char2CharLinkedOpenHashMap.MapEntry previous() {
         return this.entry = Char2CharLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class FastEntryIterator
      extends Char2CharLinkedOpenHashMap.MapIterator<Consumer<? super Char2CharMap.Entry>>
      implements ObjectListIterator<Char2CharMap.Entry> {
      final Char2CharLinkedOpenHashMap.MapEntry entry;

      public FastEntryIterator() {
         this.entry = Char2CharLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(char from) {
         super(from);
         this.entry = Char2CharLinkedOpenHashMap.this.new MapEntry();
      }

      final void acceptOnIndex(Consumer<? super Char2CharMap.Entry> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }

      public Char2CharLinkedOpenHashMap.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Char2CharLinkedOpenHashMap.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private final class KeyIterator extends Char2CharLinkedOpenHashMap.MapIterator<CharConsumer> implements CharListIterator {
      public KeyIterator(char k) {
         super(k);
      }

      @Override
      public char previousChar() {
         return Char2CharLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
      }

      final void acceptOnIndex(CharConsumer action, int index) {
         action.accept(Char2CharLinkedOpenHashMap.this.key[index]);
      }

      @Override
      public char nextChar() {
         return Char2CharLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractCharSortedSet {
      private static final int SPLITERATOR_CHARACTERISTICS = 337;

      private KeySet() {
      }

      public CharListIterator iterator(char from) {
         return Char2CharLinkedOpenHashMap.this.new KeyIterator(from);
      }

      public CharListIterator iterator() {
         return Char2CharLinkedOpenHashMap.this.new KeyIterator();
      }

      @Override
      public CharSpliterator spliterator() {
         return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Char2CharLinkedOpenHashMap.this), 337);
      }

      @Override
      public void forEach(CharConsumer consumer) {
         int i = Char2CharLinkedOpenHashMap.this.size;
         int next = Char2CharLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Char2CharLinkedOpenHashMap.this.link[next];
            consumer.accept(Char2CharLinkedOpenHashMap.this.key[curr]);
         }
      }

      @Override
      public int size() {
         return Char2CharLinkedOpenHashMap.this.size;
      }

      @Override
      public boolean contains(char k) {
         return Char2CharLinkedOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(char k) {
         int oldSize = Char2CharLinkedOpenHashMap.this.size;
         Char2CharLinkedOpenHashMap.this.remove(k);
         return Char2CharLinkedOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Char2CharLinkedOpenHashMap.this.clear();
      }

      @Override
      public char firstChar() {
         if (Char2CharLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Char2CharLinkedOpenHashMap.this.key[Char2CharLinkedOpenHashMap.this.first];
         }
      }

      @Override
      public char lastChar() {
         if (Char2CharLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Char2CharLinkedOpenHashMap.this.key[Char2CharLinkedOpenHashMap.this.last];
         }
      }

      @Override
      public CharComparator comparator() {
         return null;
      }

      @Override
      public CharSortedSet tailSet(char from) {
         throw new UnsupportedOperationException();
      }

      @Override
      public CharSortedSet headSet(char to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public CharSortedSet subSet(char from, char to) {
         throw new UnsupportedOperationException();
      }
   }

   final class MapEntry implements Char2CharMap.Entry, Entry<Character, Character>, CharCharPair {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public char getCharKey() {
         return Char2CharLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public char leftChar() {
         return Char2CharLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public char getCharValue() {
         return Char2CharLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public char rightChar() {
         return Char2CharLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public char setValue(char v) {
         char oldValue = Char2CharLinkedOpenHashMap.this.value[this.index];
         Char2CharLinkedOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public CharCharPair right(char v) {
         Char2CharLinkedOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Character getKey() {
         return Char2CharLinkedOpenHashMap.this.key[this.index];
      }

      @Deprecated
      @Override
      public Character getValue() {
         return Char2CharLinkedOpenHashMap.this.value[this.index];
      }

      @Deprecated
      @Override
      public Character setValue(Character v) {
         return this.setValue(v.charValue());
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<Character, Character> e = (Entry<Character, Character>)o;
            return Char2CharLinkedOpenHashMap.this.key[this.index] == e.getKey() && Char2CharLinkedOpenHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return Char2CharLinkedOpenHashMap.this.key[this.index] ^ Char2CharLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public String toString() {
         return Char2CharLinkedOpenHashMap.this.key[this.index] + "=>" + Char2CharLinkedOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Char2CharMap.Entry> implements Char2CharSortedMap.FastSortedEntrySet {
      private static final int SPLITERATOR_CHARACTERISTICS = 81;

      private MapEntrySet() {
      }

      @Override
      public ObjectBidirectionalIterator<Char2CharMap.Entry> iterator() {
         return Char2CharLinkedOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectSpliterator<Char2CharMap.Entry> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Char2CharLinkedOpenHashMap.this), 81);
      }

      @Override
      public Comparator<? super Char2CharMap.Entry> comparator() {
         return null;
      }

      public ObjectSortedSet<Char2CharMap.Entry> subSet(Char2CharMap.Entry fromElement, Char2CharMap.Entry toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Char2CharMap.Entry> headSet(Char2CharMap.Entry toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Char2CharMap.Entry> tailSet(Char2CharMap.Entry fromElement) {
         throw new UnsupportedOperationException();
      }

      public Char2CharMap.Entry first() {
         if (Char2CharLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Char2CharLinkedOpenHashMap.this.new MapEntry(Char2CharLinkedOpenHashMap.this.first);
         }
      }

      public Char2CharMap.Entry last() {
         if (Char2CharLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Char2CharLinkedOpenHashMap.this.new MapEntry(Char2CharLinkedOpenHashMap.this.last);
         }
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getKey() == null || !(e.getKey() instanceof Character)) {
               return false;
            } else if (e.getValue() != null && e.getValue() instanceof Character) {
               char k = (Character)e.getKey();
               char v = (Character)e.getValue();
               if (k == 0) {
                  return Char2CharLinkedOpenHashMap.this.containsNullKey && Char2CharLinkedOpenHashMap.this.value[Char2CharLinkedOpenHashMap.this.n] == v;
               } else {
                  char[] key = Char2CharLinkedOpenHashMap.this.key;
                  char curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix((int)k) & Char2CharLinkedOpenHashMap.this.mask]) == 0) {
                     return false;
                  } else if (k == curr) {
                     return Char2CharLinkedOpenHashMap.this.value[pos] == v;
                  } else {
                     while ((curr = key[pos = pos + 1 & Char2CharLinkedOpenHashMap.this.mask]) != 0) {
                        if (k == curr) {
                           return Char2CharLinkedOpenHashMap.this.value[pos] == v;
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
               if (e.getValue() != null && e.getValue() instanceof Character) {
                  char k = (Character)e.getKey();
                  char v = (Character)e.getValue();
                  if (k == 0) {
                     if (Char2CharLinkedOpenHashMap.this.containsNullKey && Char2CharLinkedOpenHashMap.this.value[Char2CharLinkedOpenHashMap.this.n] == v) {
                        Char2CharLinkedOpenHashMap.this.removeNullEntry();
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     char[] key = Char2CharLinkedOpenHashMap.this.key;
                     char curr;
                     int pos;
                     if ((curr = key[pos = HashCommon.mix((int)k) & Char2CharLinkedOpenHashMap.this.mask]) == 0) {
                        return false;
                     } else if (curr == k) {
                        if (Char2CharLinkedOpenHashMap.this.value[pos] == v) {
                           Char2CharLinkedOpenHashMap.this.removeEntry(pos);
                           return true;
                        } else {
                           return false;
                        }
                     } else {
                        while ((curr = key[pos = pos + 1 & Char2CharLinkedOpenHashMap.this.mask]) != 0) {
                           if (curr == k && Char2CharLinkedOpenHashMap.this.value[pos] == v) {
                              Char2CharLinkedOpenHashMap.this.removeEntry(pos);
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
         return Char2CharLinkedOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Char2CharLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Char2CharMap.Entry> iterator(Char2CharMap.Entry from) {
         return Char2CharLinkedOpenHashMap.this.new EntryIterator(from.getCharKey());
      }

      public ObjectListIterator<Char2CharMap.Entry> fastIterator() {
         return Char2CharLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Char2CharMap.Entry> fastIterator(Char2CharMap.Entry from) {
         return Char2CharLinkedOpenHashMap.this.new FastEntryIterator(from.getCharKey());
      }

      @Override
      public void forEach(Consumer<? super Char2CharMap.Entry> consumer) {
         int i = Char2CharLinkedOpenHashMap.this.size;
         int next = Char2CharLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Char2CharLinkedOpenHashMap.this.link[next];
            consumer.accept(Char2CharLinkedOpenHashMap.this.new MapEntry(curr));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Char2CharMap.Entry> consumer) {
         Char2CharLinkedOpenHashMap.MapEntry entry = Char2CharLinkedOpenHashMap.this.new MapEntry();
         int i = Char2CharLinkedOpenHashMap.this.size;
         int next = Char2CharLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            entry.index = next;
            next = (int)Char2CharLinkedOpenHashMap.this.link[next];
            consumer.accept(entry);
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int prev = -1;
      int next = -1;
      int curr = -1;
      int index = -1;

      abstract void acceptOnIndex(ConsumerType var1, int var2);

      protected MapIterator() {
         this.next = Char2CharLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(char from) {
         if (from == 0) {
            if (Char2CharLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Char2CharLinkedOpenHashMap.this.link[Char2CharLinkedOpenHashMap.this.n];
               this.prev = Char2CharLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
         } else if (Char2CharLinkedOpenHashMap.this.key[Char2CharLinkedOpenHashMap.this.last] == from) {
            this.prev = Char2CharLinkedOpenHashMap.this.last;
            this.index = Char2CharLinkedOpenHashMap.this.size;
         } else {
            for (int pos = HashCommon.mix((int)from) & Char2CharLinkedOpenHashMap.this.mask;
               Char2CharLinkedOpenHashMap.this.key[pos] != 0;
               pos = pos + 1 & Char2CharLinkedOpenHashMap.this.mask
            ) {
               if (Char2CharLinkedOpenHashMap.this.key[pos] == from) {
                  this.next = (int)Char2CharLinkedOpenHashMap.this.link[pos];
                  this.prev = pos;
                  return;
               }
            }

            throw new NoSuchElementException("The key " + from + " does not belong to this map.");
         }
      }

      public boolean hasNext() {
         return this.next != -1;
      }

      public boolean hasPrevious() {
         return this.prev != -1;
      }

      private final void ensureIndexKnown() {
         if (this.index < 0) {
            if (this.prev == -1) {
               this.index = 0;
            } else if (this.next == -1) {
               this.index = Char2CharLinkedOpenHashMap.this.size;
            } else {
               int pos = Char2CharLinkedOpenHashMap.this.first;

               for (this.index = 1; pos != this.prev; this.index++) {
                  pos = (int)Char2CharLinkedOpenHashMap.this.link[pos];
               }
            }
         }
      }

      public int nextIndex() {
         this.ensureIndexKnown();
         return this.index;
      }

      public int previousIndex() {
         this.ensureIndexKnown();
         return this.index - 1;
      }

      public int nextEntry() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.next;
            this.next = (int)Char2CharLinkedOpenHashMap.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
               this.index++;
            }

            return this.curr;
         }
      }

      public int previousEntry() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = this.prev;
            this.prev = (int)(Char2CharLinkedOpenHashMap.this.link[this.curr] >>> 32);
            this.next = this.curr;
            if (this.index >= 0) {
               this.index--;
            }

            return this.curr;
         }
      }

      public void forEachRemaining(ConsumerType action) {
         while (this.hasNext()) {
            this.curr = this.next;
            this.next = (int)Char2CharLinkedOpenHashMap.this.link[this.curr];
            this.prev = this.curr;
            if (this.index >= 0) {
               this.index++;
            }

            this.acceptOnIndex(action, this.curr);
         }
      }

      public void remove() {
         this.ensureIndexKnown();
         if (this.curr == -1) {
            throw new IllegalStateException();
         } else {
            if (this.curr == this.prev) {
               this.index--;
               this.prev = (int)(Char2CharLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Char2CharLinkedOpenHashMap.this.link[this.curr];
            }

            Char2CharLinkedOpenHashMap.this.size--;
            if (this.prev == -1) {
               Char2CharLinkedOpenHashMap.this.first = this.next;
            } else {
               Char2CharLinkedOpenHashMap.this.link[this.prev] = Char2CharLinkedOpenHashMap.this.link[this.prev]
                  ^ (Char2CharLinkedOpenHashMap.this.link[this.prev] ^ this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Char2CharLinkedOpenHashMap.this.last = this.prev;
            } else {
               Char2CharLinkedOpenHashMap.this.link[this.next] = Char2CharLinkedOpenHashMap.this.link[this.next]
                  ^ (Char2CharLinkedOpenHashMap.this.link[this.next] ^ (this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int pos = this.curr;
            this.curr = -1;
            if (pos == Char2CharLinkedOpenHashMap.this.n) {
               Char2CharLinkedOpenHashMap.this.containsNullKey = false;
            } else {
               char[] key = Char2CharLinkedOpenHashMap.this.key;

               label61:
               while (true) {
                  int last = pos;

                  char curr;
                  for (pos = pos + 1 & Char2CharLinkedOpenHashMap.this.mask; (curr = key[pos]) != 0; pos = pos + 1 & Char2CharLinkedOpenHashMap.this.mask) {
                     int slot = HashCommon.mix((int)curr) & Char2CharLinkedOpenHashMap.this.mask;
                     if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                        key[last] = curr;
                        Char2CharLinkedOpenHashMap.this.value[last] = Char2CharLinkedOpenHashMap.this.value[pos];
                        if (this.next == pos) {
                           this.next = last;
                        }

                        if (this.prev == pos) {
                           this.prev = last;
                        }

                        Char2CharLinkedOpenHashMap.this.fixPointers(pos, last);
                        continue label61;
                     }
                  }

                  key[last] = 0;
                  return;
               }
            }
         }
      }

      public int skip(int n) {
         int i = n;

         while (i-- != 0 && this.hasNext()) {
            this.nextEntry();
         }

         return n - i - 1;
      }

      public int back(int n) {
         int i = n;

         while (i-- != 0 && this.hasPrevious()) {
            this.previousEntry();
         }

         return n - i - 1;
      }

      public void set(Char2CharMap.Entry ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Char2CharMap.Entry ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class ValueIterator extends Char2CharLinkedOpenHashMap.MapIterator<CharConsumer> implements CharListIterator {
      @Override
      public char previousChar() {
         return Char2CharLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
      }

      final void acceptOnIndex(CharConsumer action, int index) {
         action.accept(Char2CharLinkedOpenHashMap.this.value[index]);
      }

      @Override
      public char nextChar() {
         return Char2CharLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }
}
