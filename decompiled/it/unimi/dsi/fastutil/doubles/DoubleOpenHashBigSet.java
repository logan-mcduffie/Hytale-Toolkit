package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.Hash;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.Size64;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.stream.DoubleStream;

public class DoubleOpenHashBigSet extends AbstractDoubleSet implements Serializable, Cloneable, Hash, Size64 {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient double[][] key;
   protected transient long mask;
   protected transient int segmentMask;
   protected transient int baseMask;
   protected transient boolean containsNull;
   protected transient long n;
   protected transient long maxFill;
   protected final transient long minN;
   protected final float f;
   protected long size;

   private void initMasks() {
      this.mask = this.n - 1L;
      this.segmentMask = this.key[0].length - 1;
      this.baseMask = this.key.length - 1;
   }

   public DoubleOpenHashBigSet(long expected, float f) {
      if (f <= 0.0F || f > 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      } else if (this.n < 0L) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.bigArraySize(expected, f);
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = DoubleBigArrays.newBigArray(this.n);
         this.initMasks();
      }
   }

   public DoubleOpenHashBigSet(long expected) {
      this(expected, 0.75F);
   }

   public DoubleOpenHashBigSet() {
      this(16L, 0.75F);
   }

   public DoubleOpenHashBigSet(Collection<? extends Double> c, float f) {
      this(Size64.sizeOf(c), f);
      this.addAll(c);
   }

   public DoubleOpenHashBigSet(Collection<? extends Double> c) {
      this(c, 0.75F);
   }

   public DoubleOpenHashBigSet(DoubleCollection c, float f) {
      this(Size64.sizeOf(c), f);
      this.addAll(c);
   }

   public DoubleOpenHashBigSet(DoubleCollection c) {
      this(c, 0.75F);
   }

   public DoubleOpenHashBigSet(DoubleIterator i, float f) {
      this(16L, f);

      while (i.hasNext()) {
         this.add(i.nextDouble());
      }
   }

   public DoubleOpenHashBigSet(DoubleIterator i) {
      this(i, 0.75F);
   }

   public DoubleOpenHashBigSet(Iterator<?> i, float f) {
      this(DoubleIterators.asDoubleIterator(i), f);
   }

   public DoubleOpenHashBigSet(Iterator<?> i) {
      this(DoubleIterators.asDoubleIterator(i));
   }

   public DoubleOpenHashBigSet(double[] a, int offset, int length, float f) {
      this(length < 0 ? 0L : length, f);
      DoubleArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public DoubleOpenHashBigSet(double[] a, int offset, int length) {
      this(a, offset, length, 0.75F);
   }

   public DoubleOpenHashBigSet(double[] a, float f) {
      this(a, 0, a.length, f);
   }

   public DoubleOpenHashBigSet(double[] a) {
      this(a, 0.75F);
   }

   public static DoubleOpenHashBigSet toBigSet(DoubleStream stream) {
      return stream.collect(DoubleOpenHashBigSet::new, DoubleOpenHashBigSet::add, DoubleOpenHashBigSet::addAll);
   }

   public static DoubleOpenHashBigSet toBigSetWithExpectedSize(DoubleStream stream, long expectedSize) {
      return stream.collect(() -> new DoubleOpenHashBigSet(expectedSize), DoubleOpenHashBigSet::add, DoubleOpenHashBigSet::addAll);
   }

   private long realSize() {
      return this.containsNull ? this.size - 1L : this.size;
   }

   public void ensureCapacity(long capacity) {
      long needed = HashCommon.bigArraySize(capacity, this.f);
      if (needed > this.n) {
         this.rehash(needed);
      }
   }

   @Override
   public boolean addAll(Collection<? extends Double> c) {
      long size = Size64.sizeOf(c);
      if (this.f <= 0.5) {
         this.ensureCapacity(size);
      } else {
         this.ensureCapacity(this.size64() + size);
      }

      return super.addAll(c);
   }

   @Override
   public boolean addAll(DoubleCollection c) {
      long size = Size64.sizeOf(c);
      if (this.f <= 0.5) {
         this.ensureCapacity(size);
      } else {
         this.ensureCapacity(this.size64() + size);
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
         double[][] key = this.key;
         long h = HashCommon.mix(Double.doubleToRawLongBits(k));
         int displ;
         int base;
         double curr;
         if (Double.doubleToLongBits(curr = key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) != 0L) {
            if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
               return false;
            }

            while (Double.doubleToLongBits(curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0L) {
               if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                  return false;
               }
            }
         }

         key[base][displ] = k;
      }

      if (this.size++ >= this.maxFill) {
         this.rehash(2L * this.n);
      }

      return true;
   }

   protected final void shiftKeys(long pos) {
      double[][] key = this.key;

      label30:
      while (true) {
         long last = pos;

         for (pos = pos + 1L & this.mask; Double.doubleToLongBits(BigArrays.get(key, pos)) != 0L; pos = pos + 1L & this.mask) {
            long slot = HashCommon.mix(Double.doubleToRawLongBits(BigArrays.get(key, pos))) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               BigArrays.set(key, last, BigArrays.get(key, pos));
               continue label30;
            }
         }

         BigArrays.set(key, last, 0.0);
         return;
      }
   }

   private boolean removeEntry(int base, int displ) {
      this.size--;
      this.shiftKeys(base * 134217728L + displ);
      if (this.n > this.minN && this.size < this.maxFill / 4L && this.n > 16L) {
         this.rehash(this.n / 2L);
      }

      return true;
   }

   private boolean removeNullEntry() {
      this.containsNull = false;
      this.size--;
      if (this.n > this.minN && this.size < this.maxFill / 4L && this.n > 16L) {
         this.rehash(this.n / 2L);
      }

      return true;
   }

   @Override
   public boolean remove(double k) {
      if (Double.doubleToLongBits(k) == 0L) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         double[][] key = this.key;
         long h = HashCommon.mix(Double.doubleToRawLongBits(k));
         double curr;
         int displ;
         int base;
         if (Double.doubleToLongBits(curr = key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) == 0L) {
            return false;
         } else if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
            return this.removeEntry(base, displ);
         } else {
            while (Double.doubleToLongBits(curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0L) {
               if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                  return this.removeEntry(base, displ);
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
         double[][] key = this.key;
         long h = HashCommon.mix(Double.doubleToRawLongBits(k));
         double curr;
         int displ;
         int base;
         if (Double.doubleToLongBits(curr = key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) == 0L) {
            return false;
         } else if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
            return true;
         } else {
            while (Double.doubleToLongBits(curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0L) {
               if (Double.doubleToLongBits(curr) == Double.doubleToLongBits(k)) {
                  return true;
               }
            }

            return false;
         }
      }
   }

   @Override
   public void clear() {
      if (this.size != 0L) {
         this.size = 0L;
         this.containsNull = false;
         BigArrays.fill(this.key, 0.0);
      }
   }

   @Override
   public DoubleIterator iterator() {
      return new DoubleOpenHashBigSet.SetIterator();
   }

   @Override
   public DoubleSpliterator spliterator() {
      return new DoubleOpenHashBigSet.SetSpliterator();
   }

   @Override
   public void forEach(java.util.function.DoubleConsumer action) {
      if (this.containsNull) {
         action.accept(0.0);
      }

      long pos = 0L;
      long max = this.n;
      double[][] key = this.key;

      while (pos < max) {
         double gotten = BigArrays.get(key, pos++);
         if (Double.doubleToLongBits(gotten) != 0L) {
            action.accept(gotten);
         }
      }
   }

   public boolean trim() {
      return this.trim(this.size);
   }

   public boolean trim(long n) {
      long l = HashCommon.bigArraySize(n, this.f);
      if (l < this.n && this.size <= HashCommon.maxFill(l, this.f)) {
         try {
            this.rehash(l);
            return true;
         } catch (OutOfMemoryError var6) {
            return false;
         }
      } else {
         return true;
      }
   }

   protected void rehash(long newN) {
      double[][] key = this.key;
      double[][] newKey = DoubleBigArrays.newBigArray(newN);
      long mask = newN - 1L;
      int newSegmentMask = newKey[0].length - 1;
      int newBaseMask = newKey.length - 1;
      int base = 0;
      int displ = 0;

      for (long i = this.realSize(); i-- != 0L; base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) {
         while (Double.doubleToLongBits(key[base][displ]) == 0L) {
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
         }

         double k = key[base][displ];
         long h = HashCommon.mix(Double.doubleToRawLongBits(k));
         int b;
         int d;
         if (Double.doubleToLongBits(newKey[b = (int)((h & mask) >>> 27)][d = (int)(h & newSegmentMask)]) != 0L) {
            while (Double.doubleToLongBits(newKey[b = b + ((d = d + 1 & newSegmentMask) == 0 ? 1 : 0) & newBaseMask][d]) != 0L) {
            }
         }

         newKey[b][d] = k;
      }

      this.n = newN;
      this.key = newKey;
      this.initMasks();
      this.maxFill = HashCommon.maxFill(this.n, this.f);
   }

   @Deprecated
   @Override
   public int size() {
      return (int)Math.min(2147483647L, this.size);
   }

   @Override
   public long size64() {
      return this.size;
   }

   @Override
   public boolean isEmpty() {
      return this.size == 0L;
   }

   public DoubleOpenHashBigSet clone() {
      DoubleOpenHashBigSet c;
      try {
         c = (DoubleOpenHashBigSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = BigArrays.copy(this.key);
      c.containsNull = this.containsNull;
      return c;
   }

   @Override
   public int hashCode() {
      double[][] key = this.key;
      int h = 0;
      int base = 0;
      int displ = 0;

      for (long j = this.realSize(); j-- != 0L; base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) {
         while (Double.doubleToLongBits(key[base][displ]) == 0L) {
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
         }

         h += HashCommon.double2int(key[base][displ]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      DoubleIterator i = this.iterator();
      s.defaultWriteObject();
      long j = this.size;

      while (j-- != 0L) {
         s.writeDouble(i.nextDouble());
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.bigArraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      double[][] key = this.key = DoubleBigArrays.newBigArray(this.n);
      this.initMasks();
      long i = this.size;

      while (i-- != 0L) {
         double k = s.readDouble();
         if (Double.doubleToLongBits(k) == 0L) {
            this.containsNull = true;
         } else {
            long h = HashCommon.mix(Double.doubleToRawLongBits(k));
            int base;
            int displ;
            if (Double.doubleToLongBits(key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) != 0L) {
               while (Double.doubleToLongBits(key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0L) {
               }
            }

            key[base][displ] = k;
         }
      }
   }

   private void checkTable() {
   }

   private class SetIterator implements DoubleIterator {
      int base = DoubleOpenHashBigSet.this.key.length;
      int displ;
      long last = -1L;
      long c = DoubleOpenHashBigSet.this.size;
      boolean mustReturnNull = DoubleOpenHashBigSet.this.containsNull;
      DoubleArrayList wrapped;

      private SetIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.c != 0L;
      }

      @Override
      public double nextDouble() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.c--;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = DoubleOpenHashBigSet.this.n;
               return 0.0;
            } else {
               double[][] key = DoubleOpenHashBigSet.this.key;

               while (this.displ != 0 || this.base > 0) {
                  if (this.displ-- == 0) {
                     this.displ = key[--this.base].length - 1;
                  }

                  double k = key[this.base][this.displ];
                  if (Double.doubleToLongBits(k) != 0L) {
                     this.last = this.base * 134217728L + this.displ;
                     return k;
                  }
               }

               this.last = Long.MIN_VALUE;
               return this.wrapped.getDouble(-(--this.base) - 1);
            }
         }
      }

      private final void shiftKeys(long pos) {
         double[][] key = DoubleOpenHashBigSet.this.key;

         label38:
         while (true) {
            long last = pos;

            double curr;
            for (pos = pos + 1L & DoubleOpenHashBigSet.this.mask;
               Double.doubleToLongBits(curr = BigArrays.get(key, pos)) != 0L;
               pos = pos + 1L & DoubleOpenHashBigSet.this.mask
            ) {
               long slot = HashCommon.mix(Double.doubleToRawLongBits(curr)) & DoubleOpenHashBigSet.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new DoubleArrayList();
                     }

                     this.wrapped.add(BigArrays.get(key, pos));
                  }

                  BigArrays.set(key, last, curr);
                  continue label38;
               }
            }

            BigArrays.set(key, last, 0.0);
            return;
         }
      }

      @Override
      public void remove() {
         if (this.last == -1L) {
            throw new IllegalStateException();
         } else {
            if (this.last == DoubleOpenHashBigSet.this.n) {
               DoubleOpenHashBigSet.this.containsNull = false;
            } else {
               if (this.base < 0) {
                  DoubleOpenHashBigSet.this.remove(this.wrapped.getDouble(-this.base - 1));
                  this.last = -1L;
                  return;
               }

               this.shiftKeys(this.last);
            }

            DoubleOpenHashBigSet.this.size--;
            this.last = -1L;
         }
      }
   }

   private class SetSpliterator implements DoubleSpliterator {
      private static final int POST_SPLIT_CHARACTERISTICS = 257;
      long pos = 0L;
      long max;
      long c;
      boolean mustReturnNull;
      boolean hasSplit;

      SetSpliterator() {
         this.max = DoubleOpenHashBigSet.this.n;
         this.c = 0L;
         this.mustReturnNull = DoubleOpenHashBigSet.this.containsNull;
         this.hasSplit = false;
      }

      SetSpliterator(long pos, long max, boolean mustReturnNull, boolean hasSplit) {
         this.max = DoubleOpenHashBigSet.this.n;
         this.c = 0L;
         this.mustReturnNull = DoubleOpenHashBigSet.this.containsNull;
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
            action.accept(0.0);
            return true;
         } else {
            for (double[][] key = DoubleOpenHashBigSet.this.key; this.pos < this.max; this.pos++) {
               double gotten = BigArrays.get(key, this.pos);
               if (Double.doubleToLongBits(gotten) != 0L) {
                  this.c++;
                  this.pos++;
                  action.accept(gotten);
                  return true;
               }
            }

            return false;
         }
      }

      @Override
      public void forEachRemaining(java.util.function.DoubleConsumer action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            action.accept(0.0);
            this.c++;
         }

         for (double[][] key = DoubleOpenHashBigSet.this.key; this.pos < this.max; this.pos++) {
            double gotten = BigArrays.get(key, this.pos);
            if (Double.doubleToLongBits(gotten) != 0L) {
               action.accept(gotten);
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
            ? DoubleOpenHashBigSet.this.size - this.c
            : Math.min(
               DoubleOpenHashBigSet.this.size - this.c,
               (long)((double)DoubleOpenHashBigSet.this.realSize() / DoubleOpenHashBigSet.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
            );
      }

      public DoubleOpenHashBigSet.SetSpliterator trySplit() {
         if (this.pos >= this.max - 1L) {
            return null;
         } else {
            long retLen = this.max - this.pos >> 1;
            if (retLen <= 1L) {
               return null;
            } else {
               long myNewPos = this.pos + retLen;
               myNewPos = BigArrays.nearestSegmentStart(myNewPos, this.pos + 1L, this.max - 1L);
               long retPos = this.pos;
               DoubleOpenHashBigSet.SetSpliterator split = DoubleOpenHashBigSet.this.new SetSpliterator(retPos, myNewPos, this.mustReturnNull, true);
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

            double[][] key = DoubleOpenHashBigSet.this.key;

            while (this.pos < this.max && n > 0L) {
               if (Double.doubleToLongBits(BigArrays.get(key, this.pos++)) != 0L) {
                  skipped++;
                  n--;
               }
            }

            return skipped;
         }
      }
   }
}
