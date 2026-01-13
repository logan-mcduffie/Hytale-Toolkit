package it.unimi.dsi.fastutil.shorts;

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

public class ShortOpenCustomHashSet extends AbstractShortSet implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient short[] key;
   protected transient int mask;
   protected transient boolean containsNull;
   protected ShortHash.Strategy strategy;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;

   public ShortOpenCustomHashSet(int expected, float f, ShortHash.Strategy strategy) {
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
         this.key = new short[this.n + 1];
      }
   }

   public ShortOpenCustomHashSet(int expected, ShortHash.Strategy strategy) {
      this(expected, 0.75F, strategy);
   }

   public ShortOpenCustomHashSet(ShortHash.Strategy strategy) {
      this(16, 0.75F, strategy);
   }

   public ShortOpenCustomHashSet(Collection<? extends Short> c, float f, ShortHash.Strategy strategy) {
      this(c.size(), f, strategy);
      this.addAll(c);
   }

   public ShortOpenCustomHashSet(Collection<? extends Short> c, ShortHash.Strategy strategy) {
      this(c, 0.75F, strategy);
   }

   public ShortOpenCustomHashSet(ShortCollection c, float f, ShortHash.Strategy strategy) {
      this(c.size(), f, strategy);
      this.addAll(c);
   }

   public ShortOpenCustomHashSet(ShortCollection c, ShortHash.Strategy strategy) {
      this(c, 0.75F, strategy);
   }

   public ShortOpenCustomHashSet(ShortIterator i, float f, ShortHash.Strategy strategy) {
      this(16, f, strategy);

      while (i.hasNext()) {
         this.add(i.nextShort());
      }
   }

   public ShortOpenCustomHashSet(ShortIterator i, ShortHash.Strategy strategy) {
      this(i, 0.75F, strategy);
   }

   public ShortOpenCustomHashSet(Iterator<?> i, float f, ShortHash.Strategy strategy) {
      this(ShortIterators.asShortIterator(i), f, strategy);
   }

   public ShortOpenCustomHashSet(Iterator<?> i, ShortHash.Strategy strategy) {
      this(ShortIterators.asShortIterator(i), strategy);
   }

   public ShortOpenCustomHashSet(short[] a, int offset, int length, float f, ShortHash.Strategy strategy) {
      this(length < 0 ? 0 : length, f, strategy);
      ShortArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public ShortOpenCustomHashSet(short[] a, int offset, int length, ShortHash.Strategy strategy) {
      this(a, offset, length, 0.75F, strategy);
   }

   public ShortOpenCustomHashSet(short[] a, float f, ShortHash.Strategy strategy) {
      this(a, 0, a.length, f, strategy);
   }

   public ShortOpenCustomHashSet(short[] a, ShortHash.Strategy strategy) {
      this(a, 0.75F, strategy);
   }

   public ShortHash.Strategy strategy() {
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
   public boolean addAll(ShortCollection c) {
      if (this.f <= 0.5) {
         this.ensureCapacity(c.size());
      } else {
         this.tryCapacity(this.size() + c.size());
      }

      return super.addAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends Short> c) {
      if (this.f <= 0.5) {
         this.ensureCapacity(c.size());
      } else {
         this.tryCapacity(this.size() + c.size());
      }

      return super.addAll(c);
   }

   @Override
   public boolean add(short k) {
      if (this.strategy.equals(k, (short)0)) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
         this.key[this.n] = k;
      } else {
         short[] key = this.key;
         int pos;
         short curr;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) != 0) {
            if (this.strategy.equals(curr, k)) {
               return false;
            }

            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
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

   protected final void shiftKeys(int pos) {
      short[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         short curr;
         for (pos = pos + 1 & this.mask; (curr = key[pos]) != 0; pos = pos + 1 & this.mask) {
            int slot = HashCommon.mix(this.strategy.hashCode(curr)) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               continue label30;
            }
         }

         key[last] = 0;
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
      this.key[this.n] = 0;
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   @Override
   public boolean remove(short k) {
      if (this.strategy.equals(k, (short)0)) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         short[] key = this.key;
         short curr;
         int pos;
         if ((curr = key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask]) == 0) {
            return false;
         } else if (this.strategy.equals(k, curr)) {
            return this.removeEntry(pos);
         } else {
            while ((curr = key[pos = pos + 1 & this.mask]) != 0) {
               if (this.strategy.equals(k, curr)) {
                  return this.removeEntry(pos);
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean contains(short k) {
      if (this.strategy.equals(k, (short)0)) {
         return this.containsNull;
      } else {
         short[] key = this.key;
         short curr;
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
   public void clear() {
      if (this.size != 0) {
         this.size = 0;
         this.containsNull = false;
         Arrays.fill(this.key, (short)0);
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
   public ShortIterator iterator() {
      return new ShortOpenCustomHashSet.SetIterator();
   }

   @Override
   public ShortSpliterator spliterator() {
      return new ShortOpenCustomHashSet.SetSpliterator();
   }

   @Override
   public void forEach(ShortConsumer action) {
      if (this.containsNull) {
         action.accept(this.key[this.n]);
      }

      short[] key = this.key;
      int pos = this.n;

      while (pos-- != 0) {
         if (key[pos] != 0) {
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
      short[] key = this.key;
      int mask = newN - 1;
      short[] newKey = new short[newN + 1];
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
      }

      this.n = newN;
      this.mask = mask;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = newKey;
   }

   public ShortOpenCustomHashSet clone() {
      ShortOpenCustomHashSet c;
      try {
         c = (ShortOpenCustomHashSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (short[])this.key.clone();
      c.containsNull = this.containsNull;
      c.strategy = this.strategy;
      return c;
   }

   @Override
   public int hashCode() {
      int h = 0;
      int j = this.realSize();

      for (int i = 0; j-- != 0; i++) {
         while (this.key[i] == 0) {
            i++;
         }

         h += this.strategy.hashCode(this.key[i]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      ShortIterator i = this.iterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         s.writeShort(i.nextShort());
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      short[] key = this.key = new short[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         short k = s.readShort();
         int pos;
         if (this.strategy.equals(k, (short)0)) {
            pos = this.n;
            this.containsNull = true;
         } else if (key[pos = HashCommon.mix(this.strategy.hashCode(k)) & this.mask] != 0) {
            while (key[pos = pos + 1 & this.mask] != 0) {
            }
         }

         key[pos] = k;
      }
   }

   private void checkTable() {
   }

   private final class SetIterator implements ShortIterator {
      int pos = ShortOpenCustomHashSet.this.n;
      int last = -1;
      int c = ShortOpenCustomHashSet.this.size;
      boolean mustReturnNull = ShortOpenCustomHashSet.this.containsNull;
      ShortArrayList wrapped;

      private SetIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.c != 0;
      }

      @Override
      public short nextShort() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.c--;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = ShortOpenCustomHashSet.this.n;
               return ShortOpenCustomHashSet.this.key[ShortOpenCustomHashSet.this.n];
            } else {
               short[] key = ShortOpenCustomHashSet.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos] != 0) {
                     return key[this.last = this.pos];
                  }
               }

               this.last = Integer.MIN_VALUE;
               return this.wrapped.getShort(-this.pos - 1);
            }
         }
      }

      private final void shiftKeys(int pos) {
         short[] key = ShortOpenCustomHashSet.this.key;

         label38:
         while (true) {
            int last = pos;

            short curr;
            for (pos = pos + 1 & ShortOpenCustomHashSet.this.mask; (curr = key[pos]) != 0; pos = pos + 1 & ShortOpenCustomHashSet.this.mask) {
               int slot = HashCommon.mix(ShortOpenCustomHashSet.this.strategy.hashCode(curr)) & ShortOpenCustomHashSet.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new ShortArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  continue label38;
               }
            }

            key[last] = 0;
            return;
         }
      }

      @Override
      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == ShortOpenCustomHashSet.this.n) {
               ShortOpenCustomHashSet.this.containsNull = false;
               ShortOpenCustomHashSet.this.key[ShortOpenCustomHashSet.this.n] = 0;
            } else {
               if (this.pos < 0) {
                  ShortOpenCustomHashSet.this.remove(this.wrapped.getShort(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            ShortOpenCustomHashSet.this.size--;
            this.last = -1;
         }
      }

      @Override
      public void forEachRemaining(ShortConsumer action) {
         short[] key = ShortOpenCustomHashSet.this.key;
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.last = ShortOpenCustomHashSet.this.n;
            action.accept(key[ShortOpenCustomHashSet.this.n]);
            this.c--;
         }

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               action.accept(this.wrapped.getShort(-this.pos - 1));
               this.c--;
            } else if (key[this.pos] != 0) {
               action.accept(key[this.last = this.pos]);
               this.c--;
            }
         }
      }
   }

   private final class SetSpliterator implements ShortSpliterator {
      private static final int POST_SPLIT_CHARACTERISTICS = 257;
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      SetSpliterator() {
         this.max = ShortOpenCustomHashSet.this.n;
         this.c = 0;
         this.mustReturnNull = ShortOpenCustomHashSet.this.containsNull;
         this.hasSplit = false;
      }

      SetSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = ShortOpenCustomHashSet.this.n;
         this.c = 0;
         this.mustReturnNull = ShortOpenCustomHashSet.this.containsNull;
         this.hasSplit = false;
         this.pos = pos;
         this.max = max;
         this.mustReturnNull = mustReturnNull;
         this.hasSplit = hasSplit;
      }

      public boolean tryAdvance(ShortConsumer action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.c++;
            action.accept(ShortOpenCustomHashSet.this.key[ShortOpenCustomHashSet.this.n]);
            return true;
         } else {
            for (short[] key = ShortOpenCustomHashSet.this.key; this.pos < this.max; this.pos++) {
               if (key[this.pos] != 0) {
                  this.c++;
                  action.accept(key[this.pos++]);
                  return true;
               }
            }

            return false;
         }
      }

      public void forEachRemaining(ShortConsumer action) {
         short[] key = ShortOpenCustomHashSet.this.key;
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            action.accept(key[ShortOpenCustomHashSet.this.n]);
            this.c++;
         }

         for (; this.pos < this.max; this.pos++) {
            if (key[this.pos] != 0) {
               action.accept(key[this.pos]);
               this.c++;
            }
         }
      }

      @Override
      public int characteristics() {
         return this.hasSplit ? 257 : 321;
      }

      @Override
      public long estimateSize() {
         return !this.hasSplit
            ? ShortOpenCustomHashSet.this.size - this.c
            : Math.min(
               (long)(ShortOpenCustomHashSet.this.size - this.c),
               (long)((double)ShortOpenCustomHashSet.this.realSize() / ShortOpenCustomHashSet.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
            );
      }

      public ShortOpenCustomHashSet.SetSpliterator trySplit() {
         if (this.pos >= this.max - 1) {
            return null;
         } else {
            int retLen = this.max - this.pos >> 1;
            if (retLen <= 1) {
               return null;
            } else {
               int myNewPos = this.pos + retLen;
               int retPos = this.pos;
               ShortOpenCustomHashSet.SetSpliterator split = ShortOpenCustomHashSet.this.new SetSpliterator(retPos, myNewPos, this.mustReturnNull, true);
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

            short[] key = ShortOpenCustomHashSet.this.key;

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
}
