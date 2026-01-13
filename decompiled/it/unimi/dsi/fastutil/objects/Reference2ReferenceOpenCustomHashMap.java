package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
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

public class Reference2ReferenceOpenCustomHashMap<K, V> extends AbstractReference2ReferenceMap<K, V> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
   protected transient V[] value;
   protected transient int mask;
   protected transient boolean containsNullKey;
   protected Hash.Strategy<? super K> strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;
   protected transient Reference2ReferenceMap.FastEntrySet<K, V> entries;
   protected transient ReferenceSet<K> keys;
   protected transient ReferenceCollection<V> values;

   public Reference2ReferenceOpenCustomHashMap(int expected, float f, Hash.Strategy<? super K> strategy) {
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
         this.key = (K[])(new Object[this.n + 1]);
         this.value = (V[])(new Object[this.n + 1]);
      }
   }

   public Reference2ReferenceOpenCustomHashMap(int expected, Hash.Strategy<? super K> strategy) {
      this(expected, 0.75F, strategy);
   }

   public Reference2ReferenceOpenCustomHashMap(Hash.Strategy<? super K> strategy) {
      this(16, 0.75F, strategy);
   }

   public Reference2ReferenceOpenCustomHashMap(Map<? extends K, ? extends V> m, float f, Hash.Strategy<? super K> strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Reference2ReferenceOpenCustomHashMap(Map<? extends K, ? extends V> m, Hash.Strategy<? super K> strategy) {
      this(m, 0.75F, strategy);
   }

   public Reference2ReferenceOpenCustomHashMap(Reference2ReferenceMap<K, V> m, float f, Hash.Strategy<? super K> strategy) {
      this(m.size(), f, strategy);
      this.putAll(m);
   }

   public Reference2ReferenceOpenCustomHashMap(Reference2ReferenceMap<K, V> m, Hash.Strategy<? super K> strategy) {
      this(m, 0.75F, strategy);
   }

   public Reference2ReferenceOpenCustomHashMap(K[] k, V[] v, float f, Hash.Strategy<? super K> strategy) {
      this(k.length, f, strategy);
      if (k.length != v.length) {
         throw new IllegalArgumentException("The key array and the value array have different lengths (" + k.length + " and " + v.length + ")");
      } else {
         for (int i = 0; i < k.length; i++) {
            this.put(k[i], v[i]);
         }
      }
   }

   public Reference2ReferenceOpenCustomHashMap(K[] k, V[] v, Hash.Strategy<? super K> strategy) {
      this(k, v, 0.75F, strategy);
   }

   public Hash.Strategy<? super K> strategy() {
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
      this.key[this.n] = null;
      V oldValue = this.value[this.n];
      this.value[this.n] = null;
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return oldValue;
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> m) {
      if (this.f <= 0.5) {
         this.ensureCapacity(m.size());
      } else {
         this.tryCapacity(this.size() + m.size());
      }

      super.putAll(m);
   }

   private int find(K k) {
      if (this.strategy.equals(k, null)) {
         return this.containsNullKey ? this.n : -(this.n + 1);
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == null) {
            return -(pos + 1);
         } else if (this.strategy.equals(k, curr)) {
            return pos;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals(k, curr)) {
                  return pos;
               }
            }

            return -(pos + 1);
         }
      }
   }

   private void insert(int pos, K k, V v) {
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
   public V put(K k, V v) {
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
      K[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         K curr;
         for (pos = pos + 1 & this.mask; (curr = key[pos]) != null; pos = pos + 1 & this.mask) {
            int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               this.value[last] = this.value[pos];
               continue label30;
            }
         }

         key[last] = null;
         this.value[last] = null;
         return;
      }
   }

   @Override
   public V remove(Object k) {
      if (this.strategy.equals((K)k, null)) {
         return this.containsNullKey ? this.removeNullEntry() : this.defRetValue;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode((K)k)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (this.strategy.equals((K)k, curr)) {
            return this.removeEntry(pos);
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals((K)k, curr)) {
                  return this.removeEntry(pos);
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public V get(Object k) {
      if (this.strategy.equals((K)k, null)) {
         return this.containsNullKey ? this.value[this.n] : this.defRetValue;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode((K)k)) & this.mask]) == null) {
            return this.defRetValue;
         } else if (this.strategy.equals((K)k, curr)) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals((K)k, curr)) {
                  return this.value[pos];
               }
            }

            return this.defRetValue;
         }
      }
   }

   @Override
   public boolean containsKey(Object k) {
      if (this.strategy.equals((K)k, null)) {
         return this.containsNullKey;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode((K)k)) & this.mask]) == null) {
            return false;
         } else if (this.strategy.equals((K)k, curr)) {
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals((K)k, curr)) {
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
   public V getOrDefault(Object k, V defaultValue) {
      if (this.strategy.equals((K)k, null)) {
         return this.containsNullKey ? this.value[this.n] : defaultValue;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode((K)k)) & this.mask]) == null) {
            return defaultValue;
         } else if (this.strategy.equals((K)k, curr)) {
            return this.value[pos];
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals((K)k, curr)) {
                  return this.value[pos];
               }
            }

            return defaultValue;
         }
      }
   }

   @Override
   public V putIfAbsent(K k, V v) {
      int pos = this.find(k);
      if (pos >= 0) {
         return this.value[pos];
      } else {
         this.insert(-pos - 1, k, v);
         return this.defRetValue;
      }
   }

   @Override
   public boolean remove(Object k, Object v) {
      if (this.strategy.equals((K)k, null)) {
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
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode((K)k)) & this.mask]) == null) {
            return false;
         } else if (this.strategy.equals((K)k, curr) && v == this.value[pos]) {
            this.removeEntry(pos);
            return true;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals((K)k, curr) && v == this.value[pos]) {
                  this.removeEntry(pos);
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean replace(K k, V oldValue, V v) {
      int pos = this.find(k);
      if (pos >= 0 && oldValue == this.value[pos]) {
         this.value[pos] = v;
         return true;
      } else {
         return false;
      }
   }

   @Override
   public V replace(K k, V v) {
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
   public V computeIfAbsent(K key, Reference2ReferenceFunction<? super K, ? extends V> mappingFunction) {
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
   public V computeIfPresent(K k, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      if (pos < 0) {
         return this.defRetValue;
      } else if (this.value[pos] == null) {
         return this.defRetValue;
      } else {
         V newValue = (V)remappingFunction.apply(k, this.value[pos]);
         if (newValue == null) {
            if (this.strategy.equals(k, null)) {
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
   public V compute(K k, BiFunction<? super K, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      int pos = this.find(k);
      V newValue = (V)remappingFunction.apply(k, pos >= 0 ? this.value[pos] : null);
      if (newValue == null) {
         if (pos >= 0) {
            if (this.strategy.equals(k, null)) {
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
   public V merge(K k, V v, BiFunction<? super V, ? super V, ? extends V> remappingFunction) {
      Objects.requireNonNull(remappingFunction);
      Objects.requireNonNull(v);
      int pos = this.find(k);
      if (pos >= 0 && this.value[pos] != null) {
         V newValue = (V)remappingFunction.apply(this.value[pos], v);
         if (newValue == null) {
            if (this.strategy.equals(k, null)) {
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
         Arrays.fill(this.key, null);
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

   public Reference2ReferenceMap.FastEntrySet<K, V> reference2ReferenceEntrySet() {
      if (this.entries == null) {
         this.entries = new Reference2ReferenceOpenCustomHashMap.MapEntrySet();
      }

      return this.entries;
   }

   @Override
   public ReferenceSet<K> keySet() {
      if (this.keys == null) {
         this.keys = new Reference2ReferenceOpenCustomHashMap.KeySet();
      }

      return this.keys;
   }

   @Override
   public ReferenceCollection<V> values() {
      if (this.values == null) {
         this.values = new AbstractReferenceCollection<V>() {
            @Override
            public ObjectIterator<V> iterator() {
               return Reference2ReferenceOpenCustomHashMap.this.new ValueIterator();
            }

            @Override
            public ObjectSpliterator<V> spliterator() {
               return Reference2ReferenceOpenCustomHashMap.this.new ValueSpliterator();
            }

            @Override
            public void forEach(Consumer<? super V> consumer) {
               if (Reference2ReferenceOpenCustomHashMap.this.containsNullKey) {
                  consumer.accept(Reference2ReferenceOpenCustomHashMap.this.value[Reference2ReferenceOpenCustomHashMap.this.n]);
               }

               int pos = Reference2ReferenceOpenCustomHashMap.this.n;

               while (pos-- != 0) {
                  if (Reference2ReferenceOpenCustomHashMap.this.key[pos] != null) {
                     consumer.accept(Reference2ReferenceOpenCustomHashMap.this.value[pos]);
                  }
               }
            }

            @Override
            public int size() {
               return Reference2ReferenceOpenCustomHashMap.this.size;
            }

            @Override
            public boolean contains(Object v) {
               return Reference2ReferenceOpenCustomHashMap.this.containsValue(v);
            }

            @Override
            public void clear() {
               Reference2ReferenceOpenCustomHashMap.this.clear();
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
      V[] value = this.value;
      int mask = newN - 1;
      K[] newKey = (K[])(new Object[newN + 1]);
      V[] newValue = (V[])(new Object[newN + 1]);
      int i = this.n;
      int j = this.realSize();

      while (j-- != 0) {
         while (key[--i] == null) {
         }

         int pos;
         if (newKey[pos = HashCommon.mix(this.strategy.hashCode(key[i])) & mask] != null) {
            while (newKey[pos = pos + 1 & mask] != null) {
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

   public Reference2ReferenceOpenCustomHashMap<K, V> clone() {
      Reference2ReferenceOpenCustomHashMap<K, V> c;
      try {
         c = (Reference2ReferenceOpenCustomHashMap<K, V>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.keys = null;
      c.values = null;
      c.entries = null;
      c.containsNullKey = this.containsNullKey;
      c.key = (K[])((Object[])this.key.clone());
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
         while (this.key[i] == null) {
            i++;
         }

         if (this != this.key[i]) {
            t = this.strategy.hashCode(this.key[i]);
         }

         if (this != this.value[i]) {
            t ^= this.value[i] == null ? 0 : System.identityHashCode(this.value[i]);
         }

         h += t;
      }

      if (this.containsNullKey) {
         h += this.value[this.n] == null ? 0 : System.identityHashCode(this.value[this.n]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      K[] key = this.key;
      V[] value = this.value;
      Reference2ReferenceOpenCustomHashMap<K, V>.EntryIterator i = new Reference2ReferenceOpenCustomHashMap.EntryIterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         int e = i.nextEntry();
         s.writeObject(key[e]);
         s.writeObject(value[e]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      K[] key = this.key = (K[])(new Object[this.n + 1]);
      V[] value = this.value = (V[])(new Object[this.n + 1]);
      int i = this.size;

      while (i-- != 0) {
         K k = (K)s.readObject();
         V v = (V)s.readObject();
         int pos;
         if (this.strategy.equals(k, null)) {
            pos = this.n;
            this.containsNullKey = true;
         } else {
            pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask;

            while (key[pos] != null) {
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
      extends Reference2ReferenceOpenCustomHashMap<K, V>.MapIterator<Consumer<? super Reference2ReferenceMap.Entry<K, V>>>
      implements ObjectIterator<Reference2ReferenceMap.Entry<K, V>> {
      private Reference2ReferenceOpenCustomHashMap<K, V>.MapEntry entry;

      private EntryIterator() {
      }

      public Reference2ReferenceOpenCustomHashMap<K, V>.MapEntry next() {
         return this.entry = Reference2ReferenceOpenCustomHashMap.this.new MapEntry(this.nextEntry());
      }

      final void acceptOnIndex(Consumer<? super Reference2ReferenceMap.Entry<K, V>> action, int index) {
         action.accept(this.entry = Reference2ReferenceOpenCustomHashMap.this.new MapEntry(index));
      }

      @Override
      public void remove() {
         super.remove();
         this.entry.index = -1;
      }
   }

   private final class EntrySpliterator
      extends Reference2ReferenceOpenCustomHashMap<K, V>.MapSpliterator<Consumer<? super Reference2ReferenceMap.Entry<K, V>>, Reference2ReferenceOpenCustomHashMap<K, V>.EntrySpliterator>
      implements ObjectSpliterator<Reference2ReferenceMap.Entry<K, V>> {
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

      final void acceptOnIndex(Consumer<? super Reference2ReferenceMap.Entry<K, V>> action, int index) {
         action.accept(Reference2ReferenceOpenCustomHashMap.this.new MapEntry(index));
      }

      final Reference2ReferenceOpenCustomHashMap<K, V>.EntrySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Reference2ReferenceOpenCustomHashMap.this.new EntrySpliterator(pos, max, mustReturnNull, true);
      }
   }

   private final class FastEntryIterator
      extends Reference2ReferenceOpenCustomHashMap<K, V>.MapIterator<Consumer<? super Reference2ReferenceMap.Entry<K, V>>>
      implements ObjectIterator<Reference2ReferenceMap.Entry<K, V>> {
      private final Reference2ReferenceOpenCustomHashMap<K, V>.MapEntry entry = Reference2ReferenceOpenCustomHashMap.this.new MapEntry();

      private FastEntryIterator() {
      }

      public Reference2ReferenceOpenCustomHashMap<K, V>.MapEntry next() {
         this.entry.index = this.nextEntry();
         return this.entry;
      }

      final void acceptOnIndex(Consumer<? super Reference2ReferenceMap.Entry<K, V>> action, int index) {
         this.entry.index = index;
         action.accept(this.entry);
      }
   }

   private final class KeyIterator extends Reference2ReferenceOpenCustomHashMap<K, V>.MapIterator<Consumer<? super K>> implements ObjectIterator<K> {
      public KeyIterator() {
      }

      final void acceptOnIndex(Consumer<? super K> action, int index) {
         action.accept(Reference2ReferenceOpenCustomHashMap.this.key[index]);
      }

      @Override
      public K next() {
         return Reference2ReferenceOpenCustomHashMap.this.key[this.nextEntry()];
      }
   }

   private final class KeySet extends AbstractReferenceSet<K> {
      private KeySet() {
      }

      @Override
      public ObjectIterator<K> iterator() {
         return Reference2ReferenceOpenCustomHashMap.this.new KeyIterator();
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return Reference2ReferenceOpenCustomHashMap.this.new KeySpliterator();
      }

      @Override
      public void forEach(Consumer<? super K> consumer) {
         if (Reference2ReferenceOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Reference2ReferenceOpenCustomHashMap.this.key[Reference2ReferenceOpenCustomHashMap.this.n]);
         }

         int pos = Reference2ReferenceOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            K k = Reference2ReferenceOpenCustomHashMap.this.key[pos];
            if (k != null) {
               consumer.accept(k);
            }
         }
      }

      @Override
      public int size() {
         return Reference2ReferenceOpenCustomHashMap.this.size;
      }

      @Override
      public boolean contains(Object k) {
         return Reference2ReferenceOpenCustomHashMap.this.containsKey(k);
      }

      @Override
      public boolean remove(Object k) {
         int oldSize = Reference2ReferenceOpenCustomHashMap.this.size;
         Reference2ReferenceOpenCustomHashMap.this.remove(k);
         return Reference2ReferenceOpenCustomHashMap.this.size != oldSize;
      }

      @Override
      public void clear() {
         Reference2ReferenceOpenCustomHashMap.this.clear();
      }
   }

   private final class KeySpliterator
      extends Reference2ReferenceOpenCustomHashMap<K, V>.MapSpliterator<Consumer<? super K>, Reference2ReferenceOpenCustomHashMap<K, V>.KeySpliterator>
      implements ObjectSpliterator<K> {
      private static final int POST_SPLIT_CHARACTERISTICS = 1;

      KeySpliterator() {
      }

      KeySpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         super(pos, max, mustReturnNull, hasSplit);
      }

      @Override
      public int characteristics() {
         return this.hasSplit ? 1 : 65;
      }

      final void acceptOnIndex(Consumer<? super K> action, int index) {
         action.accept(Reference2ReferenceOpenCustomHashMap.this.key[index]);
      }

      final Reference2ReferenceOpenCustomHashMap<K, V>.KeySpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Reference2ReferenceOpenCustomHashMap.this.new KeySpliterator(pos, max, mustReturnNull, true);
      }
   }

   final class MapEntry implements Reference2ReferenceMap.Entry<K, V>, Entry<K, V>, ReferenceReferencePair<K, V> {
      int index;

      MapEntry(int index) {
         this.index = index;
      }

      MapEntry() {
      }

      @Override
      public K getKey() {
         return Reference2ReferenceOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public K left() {
         return Reference2ReferenceOpenCustomHashMap.this.key[this.index];
      }

      @Override
      public V getValue() {
         return Reference2ReferenceOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public V right() {
         return Reference2ReferenceOpenCustomHashMap.this.value[this.index];
      }

      @Override
      public V setValue(V v) {
         V oldValue = Reference2ReferenceOpenCustomHashMap.this.value[this.index];
         Reference2ReferenceOpenCustomHashMap.this.value[this.index] = v;
         return oldValue;
      }

      public ReferenceReferencePair<K, V> right(V v) {
         Reference2ReferenceOpenCustomHashMap.this.value[this.index] = v;
         return this;
      }

      @Override
      public boolean equals(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<K, V> e = (Entry<K, V>)o;
            return Reference2ReferenceOpenCustomHashMap.this.strategy.equals(Reference2ReferenceOpenCustomHashMap.this.key[this.index], e.getKey())
               && Reference2ReferenceOpenCustomHashMap.this.value[this.index] == e.getValue();
         }
      }

      @Override
      public int hashCode() {
         return Reference2ReferenceOpenCustomHashMap.this.strategy.hashCode(Reference2ReferenceOpenCustomHashMap.this.key[this.index])
            ^ (
               Reference2ReferenceOpenCustomHashMap.this.value[this.index] == null
                  ? 0
                  : System.identityHashCode(Reference2ReferenceOpenCustomHashMap.this.value[this.index])
            );
      }

      @Override
      public String toString() {
         return Reference2ReferenceOpenCustomHashMap.this.key[this.index] + "=>" + Reference2ReferenceOpenCustomHashMap.this.value[this.index];
      }
   }

   private final class MapEntrySet extends AbstractObjectSet<Reference2ReferenceMap.Entry<K, V>> implements Reference2ReferenceMap.FastEntrySet<K, V> {
      private MapEntrySet() {
      }

      @Override
      public ObjectIterator<Reference2ReferenceMap.Entry<K, V>> iterator() {
         return Reference2ReferenceOpenCustomHashMap.this.new EntryIterator();
      }

      @Override
      public ObjectIterator<Reference2ReferenceMap.Entry<K, V>> fastIterator() {
         return Reference2ReferenceOpenCustomHashMap.this.new FastEntryIterator();
      }

      @Override
      public ObjectSpliterator<Reference2ReferenceMap.Entry<K, V>> spliterator() {
         return Reference2ReferenceOpenCustomHashMap.this.new EntrySpliterator();
      }

      @Override
      public boolean contains(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            K k = (K)e.getKey();
            V v = (V)e.getValue();
            if (Reference2ReferenceOpenCustomHashMap.this.strategy.equals(k, null)) {
               return Reference2ReferenceOpenCustomHashMap.this.containsNullKey
                  && Reference2ReferenceOpenCustomHashMap.this.value[Reference2ReferenceOpenCustomHashMap.this.n] == v;
            } else {
               K[] key = Reference2ReferenceOpenCustomHashMap.this.key;
               K curr;
               int pos;
               if ((
                     curr = key[pos = HashCommon.mix(Reference2ReferenceOpenCustomHashMap.this.strategy.hashCode(k))
                        & Reference2ReferenceOpenCustomHashMap.this.mask]
                  )
                  == null) {
                  return false;
               } else if (Reference2ReferenceOpenCustomHashMap.this.strategy.equals(k, curr)) {
                  return Reference2ReferenceOpenCustomHashMap.this.value[pos] == v;
               } else {
                  while ((curr = key[pos = pos + 1 & Reference2ReferenceOpenCustomHashMap.this.mask]) != null) {
                     if (Reference2ReferenceOpenCustomHashMap.this.strategy.equals(k, curr)) {
                        return Reference2ReferenceOpenCustomHashMap.this.value[pos] == v;
                     }
                  }

                  return false;
               }
            }
         }
      }

      @Override
      public boolean remove(Object o) {
         if (!(o instanceof Entry)) {
            return false;
         } else {
            Entry<?, ?> e = (Entry<?, ?>)o;
            K k = (K)e.getKey();
            V v = (V)e.getValue();
            if (Reference2ReferenceOpenCustomHashMap.this.strategy.equals(k, null)) {
               if (Reference2ReferenceOpenCustomHashMap.this.containsNullKey
                  && Reference2ReferenceOpenCustomHashMap.this.value[Reference2ReferenceOpenCustomHashMap.this.n] == v) {
                  Reference2ReferenceOpenCustomHashMap.this.removeNullEntry();
                  return true;
               } else {
                  return false;
               }
            } else {
               K[] key = Reference2ReferenceOpenCustomHashMap.this.key;
               K curr;
               int pos;
               if ((
                     curr = key[pos = HashCommon.mix(Reference2ReferenceOpenCustomHashMap.this.strategy.hashCode(k))
                        & Reference2ReferenceOpenCustomHashMap.this.mask]
                  )
                  == null) {
                  return false;
               } else if (Reference2ReferenceOpenCustomHashMap.this.strategy.equals(curr, k)) {
                  if (Reference2ReferenceOpenCustomHashMap.this.value[pos] == v) {
                     Reference2ReferenceOpenCustomHashMap.this.removeEntry(pos);
                     return true;
                  } else {
                     return false;
                  }
               } else {
                  while ((curr = key[pos = pos + 1 & Reference2ReferenceOpenCustomHashMap.this.mask]) != null) {
                     if (Reference2ReferenceOpenCustomHashMap.this.strategy.equals(curr, k) && Reference2ReferenceOpenCustomHashMap.this.value[pos] == v) {
                        Reference2ReferenceOpenCustomHashMap.this.removeEntry(pos);
                        return true;
                     }
                  }

                  return false;
               }
            }
         }
      }

      @Override
      public int size() {
         return Reference2ReferenceOpenCustomHashMap.this.size;
      }

      @Override
      public void clear() {
         Reference2ReferenceOpenCustomHashMap.this.clear();
      }

      @Override
      public void forEach(Consumer<? super Reference2ReferenceMap.Entry<K, V>> consumer) {
         if (Reference2ReferenceOpenCustomHashMap.this.containsNullKey) {
            consumer.accept(Reference2ReferenceOpenCustomHashMap.this.new MapEntry(Reference2ReferenceOpenCustomHashMap.this.n));
         }

         int pos = Reference2ReferenceOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Reference2ReferenceOpenCustomHashMap.this.key[pos] != null) {
               consumer.accept(Reference2ReferenceOpenCustomHashMap.this.new MapEntry(pos));
            }
         }
      }

      @Override
      public void fastForEach(Consumer<? super Reference2ReferenceMap.Entry<K, V>> consumer) {
         Reference2ReferenceOpenCustomHashMap<K, V>.MapEntry entry = Reference2ReferenceOpenCustomHashMap.this.new MapEntry();
         if (Reference2ReferenceOpenCustomHashMap.this.containsNullKey) {
            entry.index = Reference2ReferenceOpenCustomHashMap.this.n;
            consumer.accept(entry);
         }

         int pos = Reference2ReferenceOpenCustomHashMap.this.n;

         while (pos-- != 0) {
            if (Reference2ReferenceOpenCustomHashMap.this.key[pos] != null) {
               entry.index = pos;
               consumer.accept(entry);
            }
         }
      }
   }

   private abstract class MapIterator<ConsumerType> {
      int pos = Reference2ReferenceOpenCustomHashMap.this.n;
      int last = -1;
      int c = Reference2ReferenceOpenCustomHashMap.this.size;
      boolean mustReturnNullKey = Reference2ReferenceOpenCustomHashMap.this.containsNullKey;
      ReferenceArrayList<K> wrapped;

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
               return this.last = Reference2ReferenceOpenCustomHashMap.this.n;
            } else {
               K[] key = Reference2ReferenceOpenCustomHashMap.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != null) {
                     return this.last = this.pos;
                  }
               }

               this.last = Integer.MIN_VALUE;
               K k = this.wrapped.get(-this.pos - 1);
               int p = HashCommon.mix(Reference2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Reference2ReferenceOpenCustomHashMap.this.mask;

               while (!Reference2ReferenceOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Reference2ReferenceOpenCustomHashMap.this.mask;
               }

               return p;
            }
         }
      }

      public void forEachRemaining(ConsumerType action) {
         if (this.mustReturnNullKey) {
            this.mustReturnNullKey = false;
            this.acceptOnIndex(action, this.last = Reference2ReferenceOpenCustomHashMap.this.n);
            this.c--;
         }

         K[] key = Reference2ReferenceOpenCustomHashMap.this.key;

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               K k = this.wrapped.get(-this.pos - 1);
               int p = HashCommon.mix(Reference2ReferenceOpenCustomHashMap.this.strategy.hashCode(k)) & Reference2ReferenceOpenCustomHashMap.this.mask;

               while (!Reference2ReferenceOpenCustomHashMap.this.strategy.equals(k, key[p])) {
                  p = p + 1 & Reference2ReferenceOpenCustomHashMap.this.mask;
               }

               this.acceptOnIndex(action, p);
               this.c--;
            } else if (key[this.pos] != null) {
               this.acceptOnIndex(action, this.last = this.pos);
               this.c--;
            }
         }
      }

      private void shiftKeys(int pos) {
         K[] key = Reference2ReferenceOpenCustomHashMap.this.key;

         label38:
         while (true) {
            int last = pos;

            K curr;
            for (pos = pos + 1 & Reference2ReferenceOpenCustomHashMap.this.mask;
               (curr = key[pos]) != null;
               pos = pos + 1 & Reference2ReferenceOpenCustomHashMap.this.mask
            ) {
               int slot = HashCommon.mix(Reference2ReferenceOpenCustomHashMap.this.strategy.hashCode(curr)) & Reference2ReferenceOpenCustomHashMap.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new ReferenceArrayList<>(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  Reference2ReferenceOpenCustomHashMap.this.value[last] = Reference2ReferenceOpenCustomHashMap.this.value[pos];
                  continue label38;
               }
            }

            key[last] = null;
            Reference2ReferenceOpenCustomHashMap.this.value[last] = null;
            return;
         }
      }

      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == Reference2ReferenceOpenCustomHashMap.this.n) {
               Reference2ReferenceOpenCustomHashMap.this.containsNullKey = false;
               Reference2ReferenceOpenCustomHashMap.this.key[Reference2ReferenceOpenCustomHashMap.this.n] = null;
               Reference2ReferenceOpenCustomHashMap.this.value[Reference2ReferenceOpenCustomHashMap.this.n] = null;
            } else {
               if (this.pos < 0) {
                  Reference2ReferenceOpenCustomHashMap.this.remove(this.wrapped.set(-this.pos - 1, null));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            Reference2ReferenceOpenCustomHashMap.this.size--;
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

   private abstract class MapSpliterator<ConsumerType, SplitType extends Reference2ReferenceOpenCustomHashMap<K, V>.MapSpliterator<ConsumerType, SplitType>> {
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      MapSpliterator() {
         this.max = Reference2ReferenceOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Reference2ReferenceOpenCustomHashMap.this.containsNullKey;
         this.hasSplit = false;
      }

      MapSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = Reference2ReferenceOpenCustomHashMap.this.n;
         this.c = 0;
         this.mustReturnNull = Reference2ReferenceOpenCustomHashMap.this.containsNullKey;
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
            this.acceptOnIndex(action, Reference2ReferenceOpenCustomHashMap.this.n);
            return true;
         } else {
            for (K[] key = Reference2ReferenceOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
               if (key[this.pos] != null) {
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
            this.acceptOnIndex(action, Reference2ReferenceOpenCustomHashMap.this.n);
         }

         for (K[] key = Reference2ReferenceOpenCustomHashMap.this.key; this.pos < this.max; this.pos++) {
            if (key[this.pos] != null) {
               this.acceptOnIndex(action, this.pos);
               this.c++;
            }
         }
      }

      public long estimateSize() {
         return !this.hasSplit
            ? Reference2ReferenceOpenCustomHashMap.this.size - this.c
            : Math.min(
               (long)(Reference2ReferenceOpenCustomHashMap.this.size - this.c),
               (long)((double)Reference2ReferenceOpenCustomHashMap.this.realSize() / Reference2ReferenceOpenCustomHashMap.this.n * (this.max - this.pos))
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

            K[] key = Reference2ReferenceOpenCustomHashMap.this.key;

            while (this.pos < this.max && n > 0L) {
               if (key[this.pos++] != null) {
                  skipped++;
                  n--;
               }
            }

            return skipped;
         }
      }
   }

   private final class ValueIterator extends Reference2ReferenceOpenCustomHashMap<K, V>.MapIterator<Consumer<? super V>> implements ObjectIterator<V> {
      public ValueIterator() {
      }

      final void acceptOnIndex(Consumer<? super V> action, int index) {
         action.accept(Reference2ReferenceOpenCustomHashMap.this.value[index]);
      }

      @Override
      public V next() {
         return Reference2ReferenceOpenCustomHashMap.this.value[this.nextEntry()];
      }
   }

   private final class ValueSpliterator
      extends Reference2ReferenceOpenCustomHashMap<K, V>.MapSpliterator<Consumer<? super V>, Reference2ReferenceOpenCustomHashMap<K, V>.ValueSpliterator>
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
         action.accept(Reference2ReferenceOpenCustomHashMap.this.value[index]);
      }

      final Reference2ReferenceOpenCustomHashMap<K, V>.ValueSpliterator makeForSplit(int pos, int max, boolean mustReturnNull) {
         return Reference2ReferenceOpenCustomHashMap.this.new ValueSpliterator(pos, max, mustReturnNull, true);
      }
   }
}
