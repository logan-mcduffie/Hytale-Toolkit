package it.unimi.dsi.fastutil.doubles;

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
import java.util.stream.DoubleStream;

public class DoubleOpenHashSet extends AbstractDoubleSet implements Serializable, Cloneable, Hash {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient double[] key;
   protected transient int mask;
   protected transient boolean containsNull;
   protected transient int n;
   protected transient int maxFill;
   protected final transient int minN;
   protected int size;
   protected final float f;

   public DoubleOpenHashSet(int expected, float f) {
      if (f <= 0.0F || f >= 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than 1");
      } else if (expected < 0) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.arraySize(expected, f);
         this.mask = this.n - 1;
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = new double[this.n + 1];
      }
   }

   public DoubleOpenHashSet(int expected) {
      this(expected, 0.75F);
   }

   public DoubleOpenHashSet() {
      this(16, 0.75F);
   }

   public DoubleOpenHashSet(Collection<? extends Double> c, float f) {
      this(c.size(), f);
      this.addAll(c);
   }

   public DoubleOpenHashSet(Collection<? extends Double> c) {
      this(c, 0.75F);
   }

   public DoubleOpenHashSet(DoubleCollection c, float f) {
      this(c.size(), f);
      this.addAll(c);
   }

   public DoubleOpenHashSet(DoubleCollection c) {
      this(c, 0.75F);
   }

   public DoubleOpenHashSet(DoubleIterator i, float f) {
      this(16, f);

      while (i.hasNext()) {
         this.add(i.nextDouble());
      }
   }

   public DoubleOpenHashSet(DoubleIterator i) {
      this(i, 0.75F);
   }

   public DoubleOpenHashSet(Iterator<?> i, float f) {
      this(DoubleIterators.asDoubleIterator(i), f);
   }

   public DoubleOpenHashSet(Iterator<?> i) {
      this(DoubleIterators.asDoubleIterator(i));
   }

   public DoubleOpenHashSet(double[] a, int offset, int length, float f) {
      this(length < 0 ? 0 : length, f);
      DoubleArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public DoubleOpenHashSet(double[] a, int offset, int length) {
      this(a, offset, length, 0.75F);
   }

   public DoubleOpenHashSet(double[] a, float f) {
      this(a, 0, a.length, f);
   }

   public DoubleOpenHashSet(double[] a) {
      this(a, 0.75F);
   }

   public static DoubleOpenHashSet of() {
      return new DoubleOpenHashSet();
   }

   public static DoubleOpenHashSet of(double e) {
      DoubleOpenHashSet result = new DoubleOpenHashSet(1, 0.75F);
      result.add(e);
      return result;
   }

   public static DoubleOpenHashSet of(double e0, double e1) {
      DoubleOpenHashSet result = new DoubleOpenHashSet(2, 0.75F);
      result.add(e0);
      if (!result.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else {
         return result;
      }
   }

   public static DoubleOpenHashSet of(double e0, double e1, double e2) {
      DoubleOpenHashSet result = new DoubleOpenHashSet(3, 0.75F);
      result.add(e0);
      if (!result.add(e1)) {
         throw new IllegalArgumentException("Duplicate element: " + e1);
      } else if (!result.add(e2)) {
         throw new IllegalArgumentException("Duplicate element: " + e2);
      } else {
         return result;
      }
   }

   public static DoubleOpenHashSet of(double... a) {
      DoubleOpenHashSet result = new DoubleOpenHashSet(a.length, 0.75F);

      for (double element : a) {
         if (!result.add(element)) {
            throw new IllegalArgumentException("Duplicate element " + element);
         }
      }

      return result;
   }

   public static DoubleOpenHashSet toSet(DoubleStream stream) {
      return stream.collect(DoubleOpenHashSet::new, DoubleOpenHashSet::add, DoubleOpenHashSet::addAll);
   }

   public static DoubleOpenHashSet toSetWithExpectedSize(DoubleStream stream, int expectedSize) {
      return expectedSize <= 16
         ? toSet(stream)
         : stream.collect(
            new DoubleCollections.SizeDecreasingSupplier<>(expectedSize, size -> size <= 16 ? new DoubleOpenHashSet() : new DoubleOpenHashSet(size)),
            DoubleOpenHashSet::add,
            DoubleOpenHashSet::addAll
         );
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
   public boolean addAll(DoubleCollection c) {
      if (this.f <= 0.5) {
         this.ensureCapacity(c.size());
      } else {
         this.tryCapacity(this.size() + c.size());
      }

      return super.addAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends Double> c) {
      if (this.f <= 0.5) {
         this.ensureCapacity(c.size());
      } else {
         this.tryCapacity(this.size() + c.size());
      }

      return super.addAll(c);
   }

   @Override
   public boolean add(double k) {
      if (Double.doubleToLongBits(k) == 0L) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
      } else {
         double[] key = this.key;
         int pos;
         double curr;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) != 0L) {
            if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
               return false;
            }

            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
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
      double[] key = this.key;

      label30:
      while (true) {
         int last = pos;

         double curr;
         for (pos = pos + 1 & this.mask; Double.doubleToLongBits(curr = key[pos]) != 0L; pos = pos + 1 & this.mask) {
            int slot = (int)HashCommon.mix(Double.doubleToRawLongBits(curr)) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               key[last] = curr;
               continue label30;
            }
         }

         key[last] = 0.0;
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
      this.key[this.n] = 0.0;
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4 && this.n > 16) {
         this.rehash(this.n / 2);
      }

      return true;
   }

   @Override
   public boolean remove(double k) {
      if (Double.doubleToLongBits(k) == 0L) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         double[] key = this.key;
         double curr;
         int pos;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) == 0L) {
            return false;
         } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return this.removeEntry(pos);
         } else {
            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
                  return this.removeEntry(pos);
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean contains(double k) {
      if (Double.doubleToLongBits(k) == 0L) {
         return this.containsNull;
      } else {
         double[] key = this.key;
         double curr;
         int pos;
         if (Double.doubleToLongBits(curr = key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) == 0L) {
            return false;
         } else if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
            return true;
         } else {
            while (Double.doubleToLongBits(curr = key[pos = pos + 1 & this.mask]) != 0L) {
               if (Double.doubleToLongBits(k) == Double.doubleToLongBits(curr)) {
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
         Arrays.fill(this.key, 0.0);
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
   public DoubleIterator iterator() {
      return new DoubleOpenHashSet.SetIterator();
   }

   @Override
   public DoubleSpliterator spliterator() {
      return new DoubleOpenHashSet.SetSpliterator();
   }

   @Override
   public void forEach(java.util.function.DoubleConsumer action) {
      if (this.containsNull) {
         action.accept(this.key[this.n]);
      }

      double[] key = this.key;
      int pos = this.n;

      while (pos-- != 0) {
         if (Double.doubleToLongBits(key[pos]) != 0L) {
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
      double[] key = this.key;
      int mask = newN - 1;
      double[] newKey = new double[newN + 1];
      int i = this.n;
      int j = this.realSize();

      while (j-- != 0) {
         while (Double.doubleToLongBits(key[--i]) == 0L) {
         }

         int pos;
         if (Double.doubleToLongBits(newKey[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(key[i])) & mask]) != 0L) {
            while (Double.doubleToLongBits(newKey[pos = pos + 1 & mask]) != 0L) {
            }
         }

         newKey[pos] = key[i];
      }

      this.n = newN;
      this.mask = mask;
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.key = newKey;
   }

   public DoubleOpenHashSet clone() {
      DoubleOpenHashSet c;
      try {
         c = (DoubleOpenHashSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = (double[])this.key.clone();
      c.containsNull = this.containsNull;
      return c;
   }

   @Override
   public int hashCode() {
      int h = 0;
      int j = this.realSize();

      for (int i = 0; j-- != 0; i++) {
         while (Double.doubleToLongBits(this.key[i]) == 0L) {
            i++;
         }

         h += HashCommon.double2int(this.key[i]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      DoubleIterator i = this.iterator();
      s.defaultWriteObject();
      int j = this.size;

      while (j-- != 0) {
         s.writeDouble(i.nextDouble());
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.arraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      this.mask = this.n - 1;
      double[] key = this.key = new double[this.n + 1];
      int i = this.size;

      while (i-- != 0) {
         double k = s.readDouble();
         int pos;
         if (Double.doubleToLongBits(k) == 0L) {
            pos = this.n;
            this.containsNull = true;
         } else if (Double.doubleToLongBits(key[pos = (int)HashCommon.mix(Double.doubleToRawLongBits(k)) & this.mask]) != 0L) {
            while (Double.doubleToLongBits(key[pos = pos + 1 & this.mask]) != 0L) {
            }
         }

         key[pos] = k;
      }
   }

   private void checkTable() {
   }

   private final class SetIterator implements DoubleIterator {
      int pos = DoubleOpenHashSet.this.n;
      int last = -1;
      int c = DoubleOpenHashSet.this.size;
      boolean mustReturnNull = DoubleOpenHashSet.this.containsNull;
      DoubleArrayList wrapped;

      private SetIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.c != 0;
      }

      @Override
      public double nextDouble() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.c--;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = DoubleOpenHashSet.this.n;
               return DoubleOpenHashSet.this.key[DoubleOpenHashSet.this.n];
            } else {
               double[] key = DoubleOpenHashSet.this.key;

               while (--this.pos >= 0) {
                  if (Double.doubleToLongBits(key[this.pos]) != 0L) {
                     return key[this.last = this.pos];
                  }
               }

               this.last = Integer.MIN_VALUE;
               return this.wrapped.getDouble(-this.pos - 1);
            }
         }
      }

      private final void shiftKeys(int pos) {
         double[] key = DoubleOpenHashSet.this.key;

         label38:
         while (true) {
            int last = pos;

            double curr;
            for (pos = pos + 1 & DoubleOpenHashSet.this.mask; Double.doubleToLongBits(curr = key[pos]) != 0L; pos = pos + 1 & DoubleOpenHashSet.this.mask) {
               int slot = (int)HashCommon.mix(Double.doubleToRawLongBits(curr)) & DoubleOpenHashSet.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new DoubleArrayList(2);
                     }

                     this.wrapped.add(key[pos]);
                  }

                  key[last] = curr;
                  continue label38;
               }
            }

            key[last] = 0.0;
            return;
         }
      }

      @Override
      public void remove() {
         if (this.last == -1) {
            throw new IllegalStateException();
         } else {
            if (this.last == DoubleOpenHashSet.this.n) {
               DoubleOpenHashSet.this.containsNull = false;
               DoubleOpenHashSet.this.key[DoubleOpenHashSet.this.n] = 0.0;
            } else {
               if (this.pos < 0) {
                  DoubleOpenHashSet.this.remove(this.wrapped.getDouble(-this.pos - 1));
                  this.last = -1;
                  return;
               }

               this.shiftKeys(this.last);
            }

            DoubleOpenHashSet.this.size--;
            this.last = -1;
         }
      }

      @Override
      public void forEachRemaining(java.util.function.DoubleConsumer action) {
         double[] key = DoubleOpenHashSet.this.key;
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.last = DoubleOpenHashSet.this.n;
            action.accept(key[DoubleOpenHashSet.this.n]);
            this.c--;
         }

         while (this.c != 0) {
            if (--this.pos < 0) {
               this.last = Integer.MIN_VALUE;
               action.accept(this.wrapped.getDouble(-this.pos - 1));
               this.c--;
            } else if (Double.doubleToLongBits(key[this.pos]) != 0L) {
               action.accept(key[this.last = this.pos]);
               this.c--;
            }
         }
      }
   }

   private final class SetSpliterator implements DoubleSpliterator {
      private static final int POST_SPLIT_CHARACTERISTICS = 257;
      int pos = 0;
      int max;
      int c;
      boolean mustReturnNull;
      boolean hasSplit;

      SetSpliterator() {
         this.max = DoubleOpenHashSet.this.n;
         this.c = 0;
         this.mustReturnNull = DoubleOpenHashSet.this.containsNull;
         this.hasSplit = false;
      }

      SetSpliterator(int pos, int max, boolean mustReturnNull, boolean hasSplit) {
         this.max = DoubleOpenHashSet.this.n;
         this.c = 0;
         this.mustReturnNull = DoubleOpenHashSet.this.containsNull;
         this.hasSplit = false;
         this.pos = pos;
         this.max = max;
         this.mustReturnNull = mustReturnNull;
         this.hasSplit = hasSplit;
      }

      @Override
      public boolean tryAdvance(java.util.function.DoubleConsumer action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.c++;
            action.accept(DoubleOpenHashSet.this.key[DoubleOpenHashSet.this.n]);
            return true;
         } else {
            for (double[] key = DoubleOpenHashSet.this.key; this.pos < this.max; this.pos++) {
               if (Double.doubleToLongBits(key[this.pos]) != 0L) {
                  this.c++;
                  action.accept(key[this.pos++]);
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public void forEachRemaining(java.util.function.DoubleConsumer action) {
         double[] key = DoubleOpenHashSet.this.key;
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            action.accept(key[DoubleOpenHashSet.this.n]);
            this.c++;
         }

         for (; this.pos < this.max; this.pos++) {
            if (Double.doubleToLongBits(key[this.pos]) != 0L) {
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
            ? DoubleOpenHashSet.this.size - this.c
            : Math.min(
               (long)(DoubleOpenHashSet.this.size - this.c),
               (long)((double)DoubleOpenHashSet.this.realSize() / DoubleOpenHashSet.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
            );
      }

      public DoubleOpenHashSet.SetSpliterator trySplit() {
         if (this.pos >= this.max - 1) {
            return null;
         } else {
            int retLen = this.max - this.pos >> 1;
            if (retLen <= 1) {
               return null;
            } else {
               int myNewPos = this.pos + retLen;
               int retPos = this.pos;
               DoubleOpenHashSet.SetSpliterator split = DoubleOpenHashSet.this.new SetSpliterator(retPos, myNewPos, this.mustReturnNull, true);
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

            double[] key = DoubleOpenHashSet.this.key;

            while (this.pos < this.max && n > 0L) {
               if (Double.doubleToLongBits(key[this.pos++]) != 0L) {
                  skipped++;
                  n--;
               }
            }

            return skipped;
         }
      }
   }
}
