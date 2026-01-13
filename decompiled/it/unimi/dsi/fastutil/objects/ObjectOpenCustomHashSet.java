package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.function.Consumer;

public class ObjectOpenCustomHashSet<K> extends AbstractObjectSet<K> implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient K[] key;
   protected transient int mask;
   protected transient boolean containsNull;
   protected Hash.Strategy<? super K> strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;

   public ObjectOpenCustomHashSet(int expected, float f, Hash.Strategy<? super K> strategy) {
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
      }
   }

   public ObjectOpenCustomHashSet(int expected, Hash.Strategy<? super K> strategy) {
      this(expected, 0.75F, strategy);
   }

   public ObjectOpenCustomHashSet(Hash.Strategy<? super K> strategy) {
      this(16, 0.75F, strategy);
   }

   public ObjectOpenCustomHashSet(Collection<? extends K> c, float f, Hash.Strategy<? super K> strategy) {
      this(c.size(), f, strategy);
      this.addAll(c);
   }

   public ObjectOpenCustomHashSet(Collection<? extends K> c, Hash.Strategy<? super K> strategy) {
      this(c, 0.75F, strategy);
   }

   public ObjectOpenCustomHashSet(ObjectCollection<? extends K> c, float f, Hash.Strategy<? super K> strategy) {
      this(c.size(), f, strategy);
      this.addAll(c);
   }

   public ObjectOpenCustomHashSet(ObjectCollection<? extends K> c, Hash.Strategy<? super K> strategy) {
      this(c, 0.75F, strategy);
   }

   public ObjectOpenCustomHashSet(Iterator<? extends K> i, float f, Hash.Strategy<? super K> strategy) {
      this(16, f, strategy);

      while (i.hasNext()) {
         this.add((K)i.next());
      }
   }

   public ObjectOpenCustomHashSet(Iterator<? extends K> i, Hash.Strategy<? super K> strategy) {
      this(i, 0.75F, strategy);
   }

   public ObjectOpenCustomHashSet(K[] a, int offset, int length, float f, Hash.Strategy<? super K> strategy) {
      this(length < 0 ? 0 : length, f, strategy);
      ObjectArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public ObjectOpenCustomHashSet(K[] a, int offset, int length, Hash.Strategy<? super K> strategy) {
      this(a, offset, length, 0.75F, strategy);
   }

   public ObjectOpenCustomHashSet(K[] a, float f, Hash.Strategy<? super K> strategy) {
      this(a, 0, a.length, f, strategy);
   }

   public ObjectOpenCustomHashSet(K[] a, Hash.Strategy<? super K> strategy) {
      this(a, 0.75F, strategy);
   }

   public Hash.Strategy<? super K> strategy() {
      return this.strategy;
   }

   private int realSize() {
      return this.containsNull ? this.size - 1 : this.size;
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

   @Override
   public boolean addAll(Collection<? extends K> c) {
      if (this.f <= 0.5) {
         this.ensureCapacity(c.size());
      } else {
         this.tryCapacity(this.size() + c.size());
      }

      return super.addAll(c);
   }

   @Override
   public boolean add(K k) {
      if (this.strategy.equals(k, null)) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
         this.key[this.n] = k;
      } else {
         K[] key = this.key;
         int pos;
         K curr;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) != null) {
            if (this.strategy.equals(curr, k)) {
               return false;
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals(curr, k)) {
                  return false;
               }
            }
         }

         key[pos] = k;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return true;
   }

   public K addOrGet(K k) {
      if (this.strategy.equals(k, null)) {
         if (this.containsNull) {
            return this.key[this.n];
         }

         this.containsNull = true;
         this.key[this.n] = k;
      } else {
         K[] key = this.key;
         int pos;
         K curr;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) != null) {
            if (this.strategy.equals(curr, k)) {
               return curr;
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals(curr, k)) {
                  return curr;
               }
            }
         }

         key[pos] = k;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(HashCommon.arraySize(this.size + 1, this.f));
      }

      return k;
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
               continue label30;
            }
         }

         key[last] = null;
         return;
      }
   }

   private boolean removeEntry(int pos) {
      this.size--;
      this.shiftKeys(pos);
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   private boolean removeNullEntry() {
      this.containsNull = false;
      this.key[this.n] = null;
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   @Override
   public boolean remove(Object k) {
      if (this.strategy.equals((K)k, null)) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode((K)k)) & this.mask]) == null) {
            return false;
         } else if (this.strategy.equals((K)k, curr)) {
            return this.removeEntry(pos);
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals((K)k, curr)) {
                  return this.removeEntry(pos);
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean contains(Object k) {
      if (this.strategy.equals((K)k, null)) {
         return this.containsNull;
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

   public K get(Object k) {
      if (this.strategy.equals((K)k, null)) {
         return this.key[this.n];
      } else {
         K[] key = this.key;
         K curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode((K)k)) & this.mask]) == null) {
            return null;
         } else if (this.strategy.equals((K)k, curr)) {
            return curr;
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != null) {
               if (this.strategy.equals((K)k, curr)) {
                  return curr;
               }
            }

            return null;
         }
      }
   }

   @Override
   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNull = false;
         Arrays.fill(this.key, null);
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

   @Override
   public ObjectIterator<K> iterator() {
      return new ObjectOpenCustomHashSet.SetIterator();
   }

   @Override
   public ObjectSpliterator<K> spliterator() {
      return new ObjectOpenCustomHashSet.SetSpliterator();
   }

   @Override
   public void forEach(Consumer<? super K> action) {
      if (this.containsNull) {
         action.accept(this.key[this.n]);
      }

      K[] key = this.key;
      int pos = this.n;

      while (pos-- != 0) {
         if (key[pos] != null) {
            action.accept(key[pos]);
         }
      }
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
      int mask = newN - 1;
      K[] newKey = (K[])(new Object[newN + 1]);
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
      }

      this.n = newN;
      this.mask = mask;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = newKey;
   }

   public ObjectOpenCustomHashSet<K> clone() {
      ObjectOpenCustomHashSet<K> c;
      try {
         c = (ObjectOpenCustomHashSet<K>)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (K[])((Object[])this.key.clone());
      c.containsNull = this.containsNull;
      c.strategy = this.strategy;
      return c;
   }

   @Override
   public int hashCode() {
      int h = 0;
      int j = this.realSize();

      for (int i = 0; j-- != 0; i++) {
         while (this.key[i] == null) {
            i++;
         }

         if (this != this.key[i]) {
            h += this.strategy.hashCode(this.key[i]);
         }
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      ObjectIterator<K> i = this.iterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         s.writeObject(i.next());
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      K[] key = this.key = (K[])(new Object[this.n + 1]);
      int i = this.size;

      while (i-- != 0) {
         K k = (K)s.readObject();
         int pos;
         if (this.strategy.equals(k, null)) {
            pos = this.n;
            this.containsNull = true;
         } else if (key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask] != null) {
            while (key[pos = pos + 1 & this.mask] != null) {
            }
         }

         key[pos] = k;
      }
   }

   private void checkTable() {
   }

   private final class SetIterator implements ObjectIterator<K> {
      int pos = ObjectOpenCustomHashSet.this.n;
      int last = -1;
      int c = ObjectOpenCustomHashSet.this.size;
      boolean mustReturnNull = ObjectOpenCustomHashSet.this.containsNull;
      ObjectArrayList<K> wrapped;

      private SetIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.c != 0;
      }

      @Override
      public K next() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.c--;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = ObjectOpenCustomHashSet.this.n;
               return ObjectOpenCustomHashSet.this.key[ObjectOpenCustomHashSet.this.n];
            } else {
               K[] key = ObjectOpenCustomHashSet.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != null) {
                     return key[this.last = this.pos];
                  }
               }

               this.last = Integer.MIN_VALUE;
               return this.wrapped.get(-this.pos - 1);
            }
         }
      }

      private final void shiftKeys(int pos) {
         K[] key = ObjectOpenCustomHashSet.this.key;

         label38:
         while (true) {
            int last = pos;

            K curr;
            for (pos = pos + 1 & ObjectOpenCustomHashSet.this.mask; (curr = key[pos]) != null; pos = pos + 1 & ObjectOpenCustomHashSet.this.mask) {
               int slot = HashCommon.mix(ObjectOpenCustomHashSet.this.strategy.hashCode(curr)) & ObjectOpenCustomHashSet.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new ObjectArrayList<>(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  continue label38;
               }
            }

            key[last] = null;
            return;
         }
      }

      @Override
      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == ObjectOpenCustomHashSet.this.n) {
               ObjectOpenCustomHashSet.this.containsNull = false;
               ObjectOpenCustomHashSet.this.key[ObjectOpenCustomHashSet.this.n] = null;
            } else {
               if (this.pos < 0) {
                  ObjectOpenCustomHashSet.this.remove(this.wrapped.set(-this.pos - 1, null));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            ObjectOpenCustomHashSet.this.size--;
            this.last = -1;
         }
      }

      @Override
      public void forEachRemaining(Consumer<? super K> action) {
         K[] key = ObjectOpenCustomHashSet.this.key;
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.last = ObjectOpenCustomHashSet.this.n;
            action.accept(key[ObjectOpenCustomHashSet.this.n]);
            this.c--;
         }

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               action.accept(this.wrapped.get(-this.pos - 1));
               this.c--;
            } else if (key[this.pos] != null) {
               action.accept(key[this.last = this.pos]);
               this.c--;
            }
         }
      }
   }

   private final class SetSpliterator implements ObjectSpliterator<K> {
      private static final int POST_SPLIT_CHARACTERISTICS = 1;
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      SetSpliterator() {
         this.max = ObjectOpenCustomHashSet.this.n;
         this.c = 0;
         this.mustReturnNull = ObjectOpenCustomHashSet.this.containsNull;
         this.hasSplit = false;
      }

      SetSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = ObjectOpenCustomHashSet.this.n;
         this.c = 0;
         this.mustReturnNull = ObjectOpenCustomHashSet.this.containsNull;
         this.hasSplit = false;
         this.pos = pos;
         this.max = max;
         this.mustReturnNull = mustReturnNull;
         this.hasSplit = hasSplit;
      }

      @Override
      public boolean tryAdvance(Consumer<? super K> action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.c++;
            action.accept(ObjectOpenCustomHashSet.this.key[ObjectOpenCustomHashSet.this.n]);
            return true;
         } else {
            for (K[] key = ObjectOpenCustomHashSet.this.key; this.pos < this.max; this.pos++) {
               if (key[this.pos] != null) {
                  this.c++;
                  action.accept(key[this.pos++]);
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public void forEachRemaining(Consumer<? super K> action) {
         K[] key = ObjectOpenCustomHashSet.this.key;
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            action.accept(key[ObjectOpenCustomHashSet.this.n]);
            this.c++;
         }

         for (; this.pos < this.max; this.pos++) {
            if (key[this.pos] != null) {
               action.accept(key[this.pos]);
               this.c++;
            }
         }
      }

      @Override
      public int characteristics() {
         return this.hasSplit ? 1 : 65;
      }

      @Override
      public long estimateSize() {
         return !this.hasSplit
            ? ObjectOpenCustomHashSet.this.size - this.c
            : Math.min(
               (long)(ObjectOpenCustomHashSet.this.size - this.c),
               (long)((double)ObjectOpenCustomHashSet.this.realSize() / ObjectOpenCustomHashSet.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
            );
      }

      public ObjectOpenCustomHashSet<K>.SetSpliterator trySplit() {
         if (this.pos >= this.max - 1) {
            return null;
         } else {
            int retLen = this.max - this.pos >> 1;
            if (retLen <= 1) {
               return null;
            } else {
               int myNewPos = this.pos + retLen;
               int retPos = this.pos;
               ObjectOpenCustomHashSet<K>.SetSpliterator split = ObjectOpenCustomHashSet.this.new SetSpliterator(retPos, myNewPos, this.mustReturnNull, true);
               this.pos = myNewPos;
               this.mustReturnNull = false;
               this.hasSplit = true;
               return split;
            }
         }
      }

      @Override
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

            K[] key = ObjectOpenCustomHashSet.this.key;

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
}
