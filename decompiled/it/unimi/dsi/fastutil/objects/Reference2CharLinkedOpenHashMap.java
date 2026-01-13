package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.Size64;
import it.unimi.dsi.fastutil.chars.AbstractCharCollection;
import it.unimi.dsi.fastutil.chars.CharCollection;
import it.unimi.dsi.fastutil.chars.CharConsumer;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.chars.CharListIterator;
import it.unimi.dsi.fastutil.chars.CharSpliterator;
import it.unimi.dsi.fastutil.chars.CharSpliterators;
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
import java.util.function.ToIntFunction;

public class Reference2CharLinkedOpenHashMap<K> extends AbstractReference2CharSortedMap<K> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
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
   protected transient Reference2CharSortedMap.FastSortedEntrySet<K> entries;
   protected transient ReferenceSortedSet<K> keys;
   protected transient CharCollection values;

   public Reference2CharLinkedOpenHashMap(int expected, float f) {
      if (f <= 0.0F || f >= 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      } else if (expected < 0) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.arraySize(expected, f);
         this.mask = this.n - 1;
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = (K[])(new Object[this.n + 1]);
         this.value = new char[this.n + 1];
         this.link = new long[this.n + 1];
      }
   }

   public Reference2CharLinkedOpenHashMap(int expected) {
      this(expected, 0.75F);
   }

   public Reference2CharLinkedOpenHashMap() {
      this(16, 0.75F);
   }

   public Reference2CharLinkedOpenHashMap(Map<? extends K, ? extends Character> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Reference2CharLinkedOpenHashMap(Map<? extends K, ? extends Character> m) {
      this(m, 0.75F);
   }

   public Reference2CharLinkedOpenHashMap(Reference2CharMap<K> m, float f) {
      this(m.size(), f);
      this.putAll(m);
   }

   public Reference2CharLinkedOpenHashMap(Reference2CharMap<K> m) {
      this(m, 0.75F);
   }

   public Reference2CharLinkedOpenHashMap(K[] k, char[] v, float f) {
      this(k.length, f);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Reference2CharLinkedOpenHashMap(K[] k, char[] v) {
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
      this.key[this.n] = null;
      char oldValue = this.value[this.n];
      this.size--;
      this.fixPointers(this.n);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends K, ? extends Character> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(K k) {
      if (k == null) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) == null) {
            return -(pos + 1);
         } else if (k == curr) {
            return pos;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k == curr) {
                  return pos;
               }
            }

            return -(pos + 1);
         }
      }
   }

   private void insert(int pos, K k, char v) {
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
   public char put(K k, char v) {
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

   public char addTo(K k, char incr) {
      int pos;
      if (k == null) {
         if (this.containsNullKey) {
            return this.addToValue(this.n, incr);
         }

         pos = this.n;
         this.containsNullKey = true;
      } else {
         K[] key = this.key;
         K curr;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) != null) {
            if (curr == k) {
               return this.addToValue(pos, incr);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
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
      K[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         K curr;
         for (pos = pos + 1 & this.mask; (curr = key[pos]) != null; pos = pos + 1 & this.mask) {
            int slot = HashCommon.mix(System.identityHashCode(curr)) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               this.value[last] = this.value[pos];
               this.fixPointers(pos, last);
               continue label30;
            }
         }

         key[last] = null;
         return;
      }
   }

   @Override
   public char removeChar(Object k) {
      if (k == null) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (k == curr) {
            return this.removeEntry(pos);
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
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
            this.key[this.n] = null;
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
            this.key[this.n] = null;
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

   public char getAndMoveToFirst(K k) {
      if (k == null) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (k == curr) {
            this.moveIndexToFirst(pos);
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k == curr) {
                  this.moveIndexToFirst(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public char getAndMoveToLast(K k) {
      if (k == null) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.value[this.n];
         } else {
            return this.defRetValue;
         }
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (k == curr) {
            this.moveIndexToLast(pos);
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k == curr) {
                  this.moveIndexToLast(pos);
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   public char putAndMoveToFirst(K k, char v) {
      int pos;
      if (k == null) {
         if (this.containsNullKey) {
            this.moveIndexToFirst(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         K[] key = this.key;
         K curr;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) != null) {
            if (curr == k) {
               this.moveIndexToFirst(pos);
               return this.setValue(pos, v);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
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

   public char putAndMoveToLast(K k, char v) {
      int pos;
      if (k == null) {
         if (this.containsNullKey) {
            this.moveIndexToLast(this.n);
            return this.setValue(this.n, v);
         }

         this.containsNullKey = true;
         pos = this.n;
      } else {
         K[] key = this.key;
         K curr;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) != null) {
            if (curr == k) {
               this.moveIndexToLast(pos);
               return this.setValue(pos, v);
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
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
   public char getChar(Object k) {
      if (k == null) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (k == curr) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k == curr) {
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public boolean containsKey(Object k) {
      if (k == null) {
         return this.containsNullKey;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) == null) {
            return false;
         } else if (k == curr) {
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
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
      K[] key = this.key;
      if (this.containsNullKey && value[this.n] == v) {
         return true;
      } else {
         int i = this.n;

         while (i-- != 0) {
            if (key[i] != null && value[i] == v) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public char getOrDefault(Object k, char defaultValue) {
      if (k == null) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) == null) {
            return defaultValue;
         } else if (k == curr) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (k == curr) {
                  return this.value[pos];
               }
            }

            return defaultValue;
         }
      }
   }

   @Override
   public char putIfAbsent(K k, char v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(Object k, char v) {
      if (k == null) {
         if (this.containsNullKey && v == this.value[this.n]) {
            this.removeNullEntry();
            return true;
         } else {
            return false;
         }
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & this.mask]) == null) {
            return false;
         } else if (k == curr && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
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
   public boolean replace(K k, char oldValue, char v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public char replace(K k, char v) {
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
   public char computeIfAbsent(K k, ToIntFunction<? super K> mappingFunction) {
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
   public char computeIfAbsent(K key, Reference2CharFunction<? super K> mappingFunction) {
      Objects.requireNonNull(mappingFunction);
      int pos = this.find(key);
      if (pos >= 0) {
         return this.value[pos];
      } else if (!mappingFunction.containsKey(key)) {
         return this.defRetValue;
      } else {
         char newValue = mappingFunction.getChar(key);
         this.insert(-pos - 1, key, newValue);
         return newValue;
      }
   }

   @Override
   public char computeCharIfPresent(K k, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else {
         Character newValue = remappingFunction.apply(k, this.value[pos]);
         if (newValue == null) {
            if (k == null) {
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
   public char computeChar(K k, BiFunction<? super K, ? super Character, ? extends Character> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      Character newValue = remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (k == null) {
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
   public char merge(K k, char v, BiFunction<? super Character, ? super Character, ? extends Character> remappingFunction) {
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
            if (k == null) {
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
         Arrays.fill(this.key, null);
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
   public K firstKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.first];
      }
   }

   @Override
   public K lastKey() {
      if (this.size == 0) {
         throw new NoSuchElementException();
      } else {
         return this.key[this.last];
      }
   }

   @Override
   public Reference2CharSortedMap<K> tailMap(K from) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Reference2CharSortedMap<K> headMap(K to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Reference2CharSortedMap<K> subMap(K from, K to) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Comparator<? super K> comparator() {
      return null;
   }

   public Reference2CharSortedMap.FastSortedEntrySet<K> reference2CharEntrySet() {
      if (this.entries == null) {
         this.entries = new Reference2CharLinkedOpenHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public ReferenceSortedSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Reference2CharLinkedOpenHashMap.KeySet();
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
               return Reference2CharLinkedOpenHashMap.this.new ValueIterator();
            }

            @Override
            public CharSpliterator spliterator() {
               return CharSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Reference2CharLinkedOpenHashMap.this), 336);
            }

            @Override
            public void forEach(CharConsumer consumer) {
               int i = Reference2CharLinkedOpenHashMap.this.size;
               int next = Reference2CharLinkedOpenHashMap.this.first;

               while (i-- != 0) {
                  int curr = next;
                  next = (int)Reference2CharLinkedOpenHashMap.this.link[next];
                  consumer.accept(Reference2CharLinkedOpenHashMap.this.value[curr]);
               }
            }

            @Override
            public int size() {
               return Reference2CharLinkedOpenHashMap.this.size;
            }

            @Override
            public boolean contains(char v) {
               return Reference2CharLinkedOpenHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Reference2CharLinkedOpenHashMap.this.clear();
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
      K[] key = this.key;
      char[] value = this.value;
      int mask = newN - 1;
      K[] newKey = (K[])(new Object[newN + 1]);
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
         if (key[i] == null) {
            pos = newN;
         } else {
            pos = HashCommon.mix(System.identityHashCode(key[i])) & mask;

            while (newKey[pos] != null) {
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

   public Reference2CharLinkedOpenHashMap<K> clone() {
      Reference2CharLinkedOpenHashMap<K> c;
      try {
         c = (Reference2CharLinkedOpenHashMap<K>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (K[])((Object[])this.key.clone());
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
         while (this.key[i] == null) {
            i++;
         }

         if (this != this.key[i]) {
            t = System.identityHashCode(this.key[i]);
         }

         t ^= this.value[i];
         h += t;
      }

      if (this.containsNullKey) {
         h += this.value[this.n];
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      K[] key = this.key;
      char[] value = this.value;
      Reference2CharLinkedOpenHashMap<K>.EntryIterator i = new Reference2CharLinkedOpenHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeObject(key[e]);
         s.writeChar(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      K[] key = this.key = (K[])(new Object[this.n + 1]);
      char[] value = this.value = new char[this.n + 1];
      long[] link = this.link = new long[this.n + 1];
      int prev = -1;
      this.first = this.last = -1;
      int i = this.size;

      while (i-- != 0) {
         K k = (K)s.readObject();
         char v = s.readChar();
         int pos;
         if (k == null) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            pos = HashCommon.mix(System.identityHashCode(k)) & this.mask;

            while (key[pos] != null) {
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
      extends Reference2CharLinkedOpenHashMap<K>.MapIterator<Consumer<? super Reference2CharMap.Entry<K>>>
      implements ObjectListIterator<Reference2CharMap.Entry<K>> {
      private Reference2CharLinkedOpenHashMap<K>.MapEntry entry;

      public EntryIterator() {
      }

      public EntryIterator(K from) {
         super(from);
      }

      final void acceptOnIndex(Consumer<? super Reference2CharMap.Entry<K>> action, int index) {
         action.accept(Reference2CharLinkedOpenHashMap.this.new MapEntry(index));
      }

      public Reference2CharLinkedOpenHashMap<K>.MapEntry next() {
         return this.entry = Reference2CharLinkedOpenHashMap.this.new MapEntry(this.nextEntry());
      }

      public Reference2CharLinkedOpenHashMap<K>.MapEntry previous() {
         return this.entry = Reference2CharLinkedOpenHashMap.this.new MapEntry(this.previousEntry());
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class FastEntryIterator
      extends Reference2CharLinkedOpenHashMap<K>.MapIterator<Consumer<? super Reference2CharMap.Entry<K>>>
      implements ObjectListIterator<Reference2CharMap.Entry<K>> {
      final Reference2CharLinkedOpenHashMap<K>.MapEntry entry;

      public FastEntryIterator() {
         this.entry = Reference2CharLinkedOpenHashMap.this.new MapEntry();
      }

      public FastEntryIterator(K from) {
         super(from);
         this.entry = Reference2CharLinkedOpenHashMap.this.new MapEntry();
      }

      final void acceptOnIndex(Consumer<? super Reference2CharMap.Entry<K>> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }

      public Reference2CharLinkedOpenHashMap<K>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      public Reference2CharLinkedOpenHashMap<K>.MapEntry previous() {
         this.entry.index = this.previousEntry();
         return this.entry;
      }
   }

   private final class KeyIterator extends Reference2CharLinkedOpenHashMap<K>.MapIterator<Consumer<? super K>> implements ObjectListIterator<K> {
      public KeyIterator(K k) {
         super(k);
      }

      @Override
      public K previous() {
         return Reference2CharLinkedOpenHashMap.this.key[this.previousEntry()];
      }

      public KeyIterator() {
      }

      final void acceptOnIndex(Consumer<? super K> action, int index) {
         action.accept(Reference2CharLinkedOpenHashMap.this.key[index]);
      }

      @Override
      public K next() {
         return Reference2CharLinkedOpenHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractReferenceSortedSet<K> {
      private static final int SPLITERATOR_CHARACTERISTICS = 81;

      private KeySet() {
      }

      public ObjectListIterator<K> iterator(K from) {
         return Reference2CharLinkedOpenHashMap.this.new KeyIterator(from);
      }

      public ObjectListIterator<K> iterator() {
         return Reference2CharLinkedOpenHashMap.this.new KeyIterator();
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Reference2CharLinkedOpenHashMap.this), 81);
      }

      @Override
      public void forEach(Consumer<? super K> consumer) {
         int i = Reference2CharLinkedOpenHashMap.this.size;
         int next = Reference2CharLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Reference2CharLinkedOpenHashMap.this.link[next];
            consumer.accept(Reference2CharLinkedOpenHashMap.this.key[curr]);
         }
      }

      @Override
      public int size() {
         return Reference2CharLinkedOpenHashMap.this.size;
      }

      @Override
      public boolean contains(Object k) {
         return Reference2CharLinkedOpenHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(Object k) {
         int oldSize = Reference2CharLinkedOpenHashMap.this.size;
         Reference2CharLinkedOpenHashMap.this.removeChar(k);
         return Reference2CharLinkedOpenHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Reference2CharLinkedOpenHashMap.this.clear();
      }

      @Override
      public K first() {
         if (Reference2CharLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Reference2CharLinkedOpenHashMap.this.key[Reference2CharLinkedOpenHashMap.this.first];
         }
      }

      @Override
      public K last() {
         if (Reference2CharLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Reference2CharLinkedOpenHashMap.this.key[Reference2CharLinkedOpenHashMap.this.last];
         }
      }

      @Override
      public Comparator<? super K> comparator() {
         return null;
      }

      @Override
      public ReferenceSortedSet<K> tailSet(K from) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ReferenceSortedSet<K> headSet(K to) {
         throw new UnsupportedOperationException();
      }

      @Override
      public ReferenceSortedSet<K> subSet(K from, K to) {
         throw new UnsupportedOperationException();
      }
   }

   final class MapEntry implements Reference2CharMap.Entry<K>, Entry<K, Character>, ReferenceCharPair<K> {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public K getKey() {
         return Reference2CharLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public K left() {
         return Reference2CharLinkedOpenHashMap.this.key[this.index];
      }

      @Override
      public char getCharValue() {
         return Reference2CharLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public char rightChar() {
         return Reference2CharLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public char setValue(char v) {
         char oldValue = Reference2CharLinkedOpenHashMap.this.value[this.index];
         Reference2CharLinkedOpenHashMap.this.value[this.index] = v;
         return oldValue;
      }

      @Override
      public ReferenceCharPair<K> right(char v) {
         Reference2CharLinkedOpenHashMap.this.value[this.index] = v;
         return this;
      }

      @Deprecated
      @Override
      public Character getValue() {
         return Reference2CharLinkedOpenHashMap.this.value[this.index];
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
            Entry<K, Character> e = (Entry<K, Character>)o;
            return Reference2CharLinkedOpenHashMap.this.key[this.index] == e.getKey() && Reference2CharLinkedOpenHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return System.identityHashCode(Reference2CharLinkedOpenHashMap.this.key[this.index]) ^ Reference2CharLinkedOpenHashMap.this.value[this.index];
      }

      @Override
      public String toString() {
         return Reference2CharLinkedOpenHashMap.this.key[this.index] + "=>" + Reference2CharLinkedOpenHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSortedSet<Reference2CharMap.Entry<K>> implements Reference2CharSortedMap.FastSortedEntrySet<K> {
      private static final int SPLITERATOR_CHARACTERISTICS = 81;

      private MapEntrySet() {
      }

      @Override
      public ObjectBidirectionalIterator<Reference2CharMap.Entry<K>> iterator() {
         return Reference2CharLinkedOpenHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectSpliterator<Reference2CharMap.Entry<K>> spliterator() {
         return ObjectSpliterators.asSpliterator(this.iterator(), Size64.sizeOf(Reference2CharLinkedOpenHashMap.this), 81);
      }

      @Override
      public Comparator<? super Reference2CharMap.Entry<K>> comparator() {
         return null;
      }

      public ObjectSortedSet<Reference2CharMap.Entry<K>> subSet(Reference2CharMap.Entry<K> fromElement, Reference2CharMap.Entry<K> toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Reference2CharMap.Entry<K>> headSet(Reference2CharMap.Entry<K> toElement) {
         throw new UnsupportedOperationException();
      }

      public ObjectSortedSet<Reference2CharMap.Entry<K>> tailSet(Reference2CharMap.Entry<K> fromElement) {
         throw new UnsupportedOperationException();
      }

      public Reference2CharMap.Entry<K> first() {
         if (Reference2CharLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Reference2CharLinkedOpenHashMap.this.new MapEntry(Reference2CharLinkedOpenHashMap.this.first);
         }
      }

      public Reference2CharMap.Entry<K> last() {
         if (Reference2CharLinkedOpenHashMap.this.size == 0) {
            throw new NoSuchElementException();
         } else {
            return Reference2CharLinkedOpenHashMap.this.new MapEntry(Reference2CharLinkedOpenHashMap.this.last);
         }
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            if (e.getValue() != null && e.getValue() instanceof Character) {
               K k = (K)e.getKey();
               char v = (Character)e.getValue();
               if (k == null) {
                  return Reference2CharLinkedOpenHashMap.this.containsNullKey
                     && Reference2CharLinkedOpenHashMap.this.value[Reference2CharLinkedOpenHashMap.this.n] == v;
               } else {
                  K[] key = Reference2CharLinkedOpenHashMap.this.key;
                  K curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & Reference2CharLinkedOpenHashMap.this.mask]) == null) {
                     return false;
                  } else if (k == curr) {
                     return Reference2CharLinkedOpenHashMap.this.value[pos] == v;
                  } else {
                     while ((curr = key[pos = pos + 1 & Reference2CharLinkedOpenHashMap.this.mask]) != null) {
                        if (k == curr) {
                           return Reference2CharLinkedOpenHashMap.this.value[pos] == v;
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
            if (e.getValue() != null && e.getValue() instanceof Character) {
               K k = (K)e.getKey();
               char v = (Character)e.getValue();
               if (k == null) {
                  if (Reference2CharLinkedOpenHashMap.this.containsNullKey
                     && Reference2CharLinkedOpenHashMap.this.value[Reference2CharLinkedOpenHashMap.this.n] == v) {
                     Reference2CharLinkedOpenHashMap.this.removeNullEntry();
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  K[] key = Reference2CharLinkedOpenHashMap.this.key;
                  K curr;
                  int pos;
                  if ((curr = key[pos = HashCommon.mix(System.identityHashCode(k)) & Reference2CharLinkedOpenHashMap.this.mask]) == null) {
                     return false;
                  } else if (curr == k) {
                     if (Reference2CharLinkedOpenHashMap.this.value[pos] == v) {
                        Reference2CharLinkedOpenHashMap.this.removeEntry(pos);
                        return true;
                     } else {
                        return false;
                     }
                  } else {
                     while ((curr = key[pos = pos + 1 & Reference2CharLinkedOpenHashMap.this.mask]) != null) {
                        if (curr == k && Reference2CharLinkedOpenHashMap.this.value[pos] == v) {
                           Reference2CharLinkedOpenHashMap.this.removeEntry(pos);
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
         return Reference2CharLinkedOpenHashMap.this.size;
      }

      @Override
      public void clear() {
         Reference2CharLinkedOpenHashMap.this.clear();
      }

      public ObjectListIterator<Reference2CharMap.Entry<K>> iterator(Reference2CharMap.Entry<K> from) {
         return Reference2CharLinkedOpenHashMap.this.new EntryIterator(from.getKey());
      }

      public ObjectListIterator<Reference2CharMap.Entry<K>> fastIterator() {
         return Reference2CharLinkedOpenHashMap.this.new FastEntryIterator();
      }

      public ObjectListIterator<Reference2CharMap.Entry<K>> fastIterator(Reference2CharMap.Entry<K> from) {
         return Reference2CharLinkedOpenHashMap.this.new FastEntryIterator(from.getKey());
      }

      @Override
      public void forEach(Consumer<? super Reference2CharMap.Entry<K>> consumer) {
         int i = Reference2CharLinkedOpenHashMap.this.size;
         int next = Reference2CharLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            int curr = next;
            next = (int)Reference2CharLinkedOpenHashMap.this.link[next];
            consumer.accept(Reference2CharLinkedOpenHashMap.this.new MapEntry(curr));
         }
      }

      @Override
      public void fastForEach(Consumer<? super Reference2CharMap.Entry<K>> consumer) {
         Reference2CharLinkedOpenHashMap<K>.MapEntry entry = Reference2CharLinkedOpenHashMap.this.new MapEntry();
         int i = Reference2CharLinkedOpenHashMap.this.size;
         int next = Reference2CharLinkedOpenHashMap.this.first;

         while (i-- != 0) {
            entry.index = next;
            next = (int)Reference2CharLinkedOpenHashMap.this.link[next];
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
         this.next = Reference2CharLinkedOpenHashMap.this.first;
         this.index = 0;
      }

      private MapIterator(K from) {
         if (from == null) {
            if (Reference2CharLinkedOpenHashMap.this.containsNullKey) {
               this.next = (int)Reference2CharLinkedOpenHashMap.this.link[Reference2CharLinkedOpenHashMap.this.n];
               this.prev = Reference2CharLinkedOpenHashMap.this.n;
            } else {
               throw new NoSuchElementException("The key " + from + " does not belong to this map.");
            }
         } else if (Reference2CharLinkedOpenHashMap.this.key[Reference2CharLinkedOpenHashMap.this.last] == from) {
            this.prev = Reference2CharLinkedOpenHashMap.this.last;
            this.index = Reference2CharLinkedOpenHashMap.this.size;
         } else {
            for (int pos = HashCommon.mix(System.identityHashCode(from)) & Reference2CharLinkedOpenHashMap.this.mask;
               Reference2CharLinkedOpenHashMap.this.key[pos] != null;
               pos = pos + 1 & Reference2CharLinkedOpenHashMap.this.mask
            ) {
               if (Reference2CharLinkedOpenHashMap.this.key[pos] == from) {
                  this.next = (int)Reference2CharLinkedOpenHashMap.this.link[pos];
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
               this.index = Reference2CharLinkedOpenHashMap.this.size;
            } else {
               int pos = Reference2CharLinkedOpenHashMap.this.first;

               for (this.index = 1; pos != this.prev; this.index++) {
                  pos = (int)Reference2CharLinkedOpenHashMap.this.link[pos];
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
            this.next = (int)Reference2CharLinkedOpenHashMap.this.link[this.curr];
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
            this.prev = (int)(Reference2CharLinkedOpenHashMap.this.link[this.curr] >>> 32);
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
            this.next = (int)Reference2CharLinkedOpenHashMap.this.link[this.curr];
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
               this.prev = (int)(Reference2CharLinkedOpenHashMap.this.link[this.curr] >>> 32);
            } else {
               this.next = (int)Reference2CharLinkedOpenHashMap.this.link[this.curr];
            }

            Reference2CharLinkedOpenHashMap.this.size--;
            if (this.prev == -1) {
               Reference2CharLinkedOpenHashMap.this.first = this.next;
            } else {
               Reference2CharLinkedOpenHashMap.this.link[this.prev] = Reference2CharLinkedOpenHashMap.this.link[this.prev]
                  ^ (Reference2CharLinkedOpenHashMap.this.link[this.prev] ^ this.next & 4294967295L) & 4294967295L;
            }

            if (this.next == -1) {
               Reference2CharLinkedOpenHashMap.this.last = this.prev;
            } else {
               Reference2CharLinkedOpenHashMap.this.link[this.next] = Reference2CharLinkedOpenHashMap.this.link[this.next]
                  ^ (Reference2CharLinkedOpenHashMap.this.link[this.next] ^ (this.prev & 4294967295L) << 32) & -4294967296L;
            }

            int pos = this.curr;
            this.curr = -1;
            if (pos == Reference2CharLinkedOpenHashMap.this.n) {
               Reference2CharLinkedOpenHashMap.this.containsNullKey = false;
               Reference2CharLinkedOpenHashMap.this.key[Reference2CharLinkedOpenHashMap.this.n] = null;
            } else {
               K[] key = Reference2CharLinkedOpenHashMap.this.key;

               label61:
               while (true) {
                  int last = pos;

                  K curr;
                  for (pos = pos + 1 & Reference2CharLinkedOpenHashMap.this.mask;
                     (curr = key[pos]) != null;
                     pos = pos + 1 & Reference2CharLinkedOpenHashMap.this.mask
                  ) {
                     int slot = HashCommon.mix(System.identityHashCode(curr)) & Reference2CharLinkedOpenHashMap.this.mask;
                     if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                        key[last] = curr;
                        Reference2CharLinkedOpenHashMap.this.value[last] = Reference2CharLinkedOpenHashMap.this.value[pos];
                        if (this.next == pos) {
                           this.next = last;
                        }

                        if (this.prev == pos) {
                           this.prev = last;
                        }

                        Reference2CharLinkedOpenHashMap.this.fixPointers(pos, last);
                        continue label61;
                     }
                  }

                  key[last] = null;
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

      public void set(Reference2CharMap.Entry<K> ok) {
         throw new UnsupportedOperationException();
      }

      public void add(Reference2CharMap.Entry<K> ok) {
         throw new UnsupportedOperationException();
      }
   }

   private final class ValueIterator extends Reference2CharLinkedOpenHashMap<K>.MapIterator<CharConsumer> implements CharListIterator {
      @Override
      public char previousChar() {
         return Reference2CharLinkedOpenHashMap.this.value[this.previousEntry()];
      }

      public ValueIterator() {
      }

      final void acceptOnIndex(CharConsumer action, int index) {
         action.accept(Reference2CharLinkedOpenHashMap.this.value[index]);
      }

      @Override
      public char nextChar() {
         return Reference2CharLinkedOpenHashMap.this.value[this.nextEntry()];
      }
   }
}
