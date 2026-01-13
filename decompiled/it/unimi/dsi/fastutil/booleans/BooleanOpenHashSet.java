package it.unimi.dsi.fastutil.booleans;

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

public class BooleanOpenHashSet extends AbstractBooleanSet implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient boolean[] key;
   protected transient int mask;
   protected transient boolean containsNull;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;

   public BooleanOpenHashSet(int expected, float f) {
      if (f <= 0.0F || f >= 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      } else if (expected < 0) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.arraySize(expected, f);
         this.mask = this.n - 1;
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = new boolean[this.n + 1];
      }
   }

   public BooleanOpenHashSet(int expected) {
      this(expected, 0.75F);
   }

   public BooleanOpenHashSet() {
      this(16, 0.75F);
   }

   public BooleanOpenHashSet(Collection<? extends Boolean> c, float f) {
      this(c.size(), f);
      this.addAll(c);
   }

   public BooleanOpenHashSet(Collection<? extends Boolean> c) {
      this(c, 0.75F);
   }

   public BooleanOpenHashSet(BooleanCollection c, float f) {
      this(c.size(), f);
      this.addAll(c);
   }

   public BooleanOpenHashSet(BooleanCollection c) {
      this(c, 0.75F);
   }

   public BooleanOpenHashSet(BooleanIterator i, float f) {
      this(16, f);

      while (i.hasNext()) {
         this.add(i.nextBoolean());
      }
   }

   public BooleanOpenHashSet(BooleanIterator i) {
      this(i, 0.75F);
   }

   public BooleanOpenHashSet(Iterator<?> i, float f) {
      this(BooleanIterators.asBooleanIterator(i), f);
   }

   public BooleanOpenHashSet(Iterator<?> i) {
      this(BooleanIterators.asBooleanIterator(i));
   }

   public BooleanOpenHashSet(boolean[] a, int offset, int length, float f) {
      this(length < 0 ? 0 : length, f);
      BooleanArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public BooleanOpenHashSet(boolean[] a, int offset, int length) {
      this(a, offset, length, 0.75F);
   }

   public BooleanOpenHashSet(boolean[] a, float f) {
      this(a, 0, a.length, f);
   }

   public BooleanOpenHashSet(boolean[] a) {
      this(a, 0.75F);
   }

   public static BooleanOpenHashSet of() {
      return new BooleanOpenHashSet();
   }

   public static BooleanOpenHashSet of(boolean e) {
      BooleanOpenHashSet result = new BooleanOpenHashSet(1, 0.75F);
      result.add(e);
      return result;
   }

   public static BooleanOpenHashSet of(boolean e0, boolean e1) {
      BooleanOpenHashSet result = new BooleanOpenHashSet(2, 0.75F);
      result.add(e0);
      if (!result.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else {
         return result;
      }
   }

   public static BooleanOpenHashSet of(boolean e0, boolean e1, boolean e2) {
      BooleanOpenHashSet result = new BooleanOpenHashSet(3, 0.75F);
      result.add(e0);
      if (!result.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else if (!result.add(e2)) {
         throw new IllegalArgumentException("Duplicate element: " + e2);
      } else {
         return result;
      }
   }

   public static BooleanOpenHashSet of(boolean... a) {
      BooleanOpenHashSet result = new BooleanOpenHashSet(a.length, 0.75F);

      for (boolean element : a) {
         if (!result.add(element)) {
            throw new IllegalArgumentException("Duplicate element " + element);
         }
      }

      return result;
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
   public boolean addAll(BooleanCollection c) {
      if (this.f <= 0.5) {
         this.ensureCapacity(c.size());
      } else {
         this.tryCapacity(this.size() + c.size());
      }

      return super.addAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends Boolean> c) {
      if (this.f <= 0.5) {
         this.ensureCapacity(c.size());
      } else {
         this.tryCapacity(this.size() + c.size());
      }

      return super.addAll(c);
   }

   @Override
   public boolean add(boolean k) {
      if (!k) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
      } else {
         boolean[] key = this.key;
         int pos;
         boolean curr;
         if (curr = key[pos = (k ? 262886248 : -878682501) & this.mask]) {
            if (curr == k) {
               return false;
            }

            while (curr = key[pos = pos + 1 & this.mask]) {
               if (curr == k) {
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
      boolean[] key = this.key;

      label35:
      while (true) {
         int last = pos;

         boolean curr;
         for (pos = pos + 1 & this.mask; curr = key[pos]; pos = pos + 1 & this.mask) {
            int slot = (curr ? 262886248 : -878682501) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               continue label35;
            }
         }

         key[last] = false;
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
      this.key[this.n] = false;
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   @Override
   public boolean remove(boolean k) {
      if (!k) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         boolean[] key = this.key;
         boolean curr;
         int pos;
         if (!(curr = key[pos = (k ? 262886248 : -878682501) & this.mask])) {
            return false;
         } else if (k == curr) {
            return this.removeEntry(pos);
         } else {
            while (curr = key[pos = pos + 1 & this.mask]) {
               if (k == curr) {
                  return this.removeEntry(pos);
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean contains(boolean k) {
      if (!k) {
         return this.containsNull;
      } else {
         boolean[] key = this.key;
         boolean curr;
         int pos;
         if (!(curr = key[pos = (k ? 262886248 : -878682501) & this.mask])) {
            return false;
         } else if (k == curr) {
            return true;
         } else {
            while (curr = key[pos = pos + 1 & this.mask]) {
               if (k == curr) {
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
         Arrays.fill(this.key, false);
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
   public BooleanIterator iterator() {
      return new BooleanOpenHashSet.SetIterator();
   }

   @Override
   public BooleanSpliterator spliterator() {
      return new BooleanOpenHashSet.SetSpliterator();
   }

   @Override
   public void forEach(BooleanConsumer action) {
      if (this.containsNull) {
         action.accept(this.key[this.n]);
      }

      boolean[] key = this.key;
      int pos = this.n;

      while (pos-- != 0) {
         if (key[pos]) {
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
      boolean[] key = this.key;
      int mask = newN - 1;
      boolean[] newKey = new boolean[newN + 1];
      int i = this.n;
      int j = this.realSize();

      while (j-- != 0) {
         while (!key[--i]) {
         }

         int pos;
         if (newKey[pos = (key[i] ? 262886248 : -878682501) & mask]) {
            while (newKey[pos = pos + 1 & mask]) {
            }
         }

         newKey[pos] = key[i];
      }

      this.n = newN;
      this.mask = mask;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = newKey;
   }

   public BooleanOpenHashSet clone() {
      BooleanOpenHashSet c;
      try {
         c = (BooleanOpenHashSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (boolean[])this.key.clone();
      c.containsNull = this.containsNull;
      return c;
   }

   @Override
   public int hashCode() {
      int h = 0;
      int j = this.realSize();

      for (int i = 0; j-- != 0; i++) {
         while (!this.key[i]) {
            i++;
         }

         h += this.key[i] ? 1231 : 1237;
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      BooleanIterator i = this.iterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         s.writeBoolean(i.nextBoolean());
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      boolean[] key = this.key = new boolean[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         boolean k = s.readBoolean();
         int pos;
         if (!k) {
            pos = this.n;
            this.containsNull = true;
         } else if (key[pos = (k ? 262886248 : -878682501) & this.mask]) {
            while (key[pos = pos + 1 & this.mask]) {
            }
         }

         key[pos] = k;
      }
   }

   private void checkTable() {
   }

   private final class SetIterator implements BooleanIterator {
      int pos = BooleanOpenHashSet.this.n;
      int last = -1;
      int c = BooleanOpenHashSet.this.size;
      boolean mustReturnNull = BooleanOpenHashSet.this.containsNull;
      BooleanArrayList wrapped;

      private SetIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.c != 0;
      }

      @Override
      public boolean nextBoolean() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.c--;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = BooleanOpenHashSet.this.n;
               return BooleanOpenHashSet.this.key[BooleanOpenHashSet.this.n];
            } else {
               boolean[] key = BooleanOpenHashSet.this.key;

               while (--this.pos >= 0) {
                  if (key[this.pos]) {
                     return key[this.last = this.pos];
                  }
               }

               this.last = Integer.MIN_VALUE;
               return this.wrapped.getBoolean(-this.pos - 1);
            }
         }
      }

      private final void shiftKeys(int pos) {
         boolean[] key = BooleanOpenHashSet.this.key;

         label43:
         while (true) {
            int last = pos;

            boolean curr;
            for (pos = pos + 1 & BooleanOpenHashSet.this.mask; curr = key[pos]; pos = pos + 1 & BooleanOpenHashSet.this.mask) {
               int slot = (curr ? 262886248 : -878682501) & BooleanOpenHashSet.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new BooleanArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  continue label43;
               }
            }

            key[last] = false;
            return;
         }
      }

      @Override
      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == BooleanOpenHashSet.this.n) {
               BooleanOpenHashSet.this.containsNull = false;
               BooleanOpenHashSet.this.key[BooleanOpenHashSet.this.n] = false;
            } else {
               if (this.pos < 0) {
                  BooleanOpenHashSet.this.remove(this.wrapped.getBoolean(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            BooleanOpenHashSet.this.size--;
            this.last = -1;
         }
      }

      @Override
      public void forEachRemaining(BooleanConsumer action) {
         boolean[] key = BooleanOpenHashSet.this.key;
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.last = BooleanOpenHashSet.this.n;
            action.accept(key[BooleanOpenHashSet.this.n]);
            this.c--;
         }

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               action.accept(this.wrapped.getBoolean(-this.pos - 1));
               this.c--;
            } else if (key[this.pos]) {
               action.accept(key[this.last = this.pos]);
               this.c--;
            }
         }
      }
   }

   private final class SetSpliterator implements BooleanSpliterator {
      private static final int POST_SPLIT_CHARACTERISTICS = 257;
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      SetSpliterator() {
         this.max = BooleanOpenHashSet.this.n;
         this.c = 0;
         this.mustReturnNull = BooleanOpenHashSet.this.containsNull;
         this.hasSplit = false;
      }

      SetSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = BooleanOpenHashSet.this.n;
         this.c = 0;
         this.mustReturnNull = BooleanOpenHashSet.this.containsNull;
         this.hasSplit = false;
         this.pos = pos;
         this.max = max;
         this.mustReturnNull = mustReturnNull;
         this.hasSplit = hasSplit;
      }

      public boolean tryAdvance(BooleanConsumer action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.c++;
            action.accept(BooleanOpenHashSet.this.key[BooleanOpenHashSet.this.n]);
            return true;
         } else {
            for (boolean[] key = BooleanOpenHashSet.this.key; this.pos < this.max; this.pos++) {
               if (key[this.pos]) {
                  this.c++;
                  action.accept(key[this.pos++]);
                  return true;
               }
            }

            return false;
         }
      }

      public void forEachRemaining(BooleanConsumer action) {
         boolean[] key = BooleanOpenHashSet.this.key;
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            action.accept(key[BooleanOpenHashSet.this.n]);
            this.c++;
         }

         for (; this.pos < this.max; this.pos++) {
            if (key[this.pos]) {
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
            ? BooleanOpenHashSet.this.size - this.c
            : Math.min(
               (long)(BooleanOpenHashSet.this.size - this.c),
               (long)((double)BooleanOpenHashSet.this.realSize() / BooleanOpenHashSet.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
            );
      }

      public BooleanOpenHashSet.SetSpliterator trySplit() {
         if (this.pos >= this.max - 1) {
            return null;
         } else {
            int retLen = this.max - this.pos >> 1;
            if (retLen <= 1) {
               return null;
            } else {
               int myNewPos = this.pos + retLen;
               int retPos = this.pos;
               BooleanOpenHashSet.SetSpliterator split = BooleanOpenHashSet.this.new SetSpliterator(retPos, myNewPos, this.mustReturnNull, true);
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

            boolean[] key = BooleanOpenHashSet.this.key;

            while (this.pos < this.max && n > 0L) {
               if (key[this.pos++]) {
                  skipped++;
                  n--;
               }
            }

            return skipped;
         }
      }
   }
}
