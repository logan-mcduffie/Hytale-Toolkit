package it.unimi.dsi.fastutil.ints;

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
import java.util.stream.IntStream;

public class IntOpenHashBigSet extends AbstractIntSet implements Serializable, Cloneable, Hash, Size64 {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient int[][] key;
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

   public IntOpenHashBigSet(long expected, float f) {
      if (f <= 0.0F || f > 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      } else if (this.n < 0L) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.bigArraySize(expected, f);
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = IntBigArrays.newBigArray(this.n);
         this.initMasks();
      }
   }

   public IntOpenHashBigSet(long expected) {
      this(expected, 0.75F);
   }

   public IntOpenHashBigSet() {
      this(16L, 0.75F);
   }

   public IntOpenHashBigSet(Collection<? extends Integer> c, float f) {
      this(Size64.sizeOf(c), f);
      this.addAll(c);
   }

   public IntOpenHashBigSet(Collection<? extends Integer> c) {
      this(c, 0.75F);
   }

   public IntOpenHashBigSet(IntCollection c, float f) {
      this(Size64.sizeOf(c), f);
      this.addAll(c);
   }

   public IntOpenHashBigSet(IntCollection c) {
      this(c, 0.75F);
   }

   public IntOpenHashBigSet(IntIterator i, float f) {
      this(16L, f);

      while (i.hasNext()) {
         this.add(i.nextInt());
      }
   }

   public IntOpenHashBigSet(IntIterator i) {
      this(i, 0.75F);
   }

   public IntOpenHashBigSet(Iterator<?> i, float f) {
      this(IntIterators.asIntIterator(i), f);
   }

   public IntOpenHashBigSet(Iterator<?> i) {
      this(IntIterators.asIntIterator(i));
   }

   public IntOpenHashBigSet(int[] a, int offset, int length, float f) {
      this(length < 0 ? 0L : length, f);
      IntArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public IntOpenHashBigSet(int[] a, int offset, int length) {
      this(a, offset, length, 0.75F);
   }

   public IntOpenHashBigSet(int[] a, float f) {
      this(a, 0, a.length, f);
   }

   public IntOpenHashBigSet(int[] a) {
      this(a, 0.75F);
   }

   public static IntOpenHashBigSet toBigSet(IntStream stream) {
      return stream.collect(IntOpenHashBigSet::new, IntOpenHashBigSet::add, IntOpenHashBigSet::addAll);
   }

   public static IntOpenHashBigSet toBigSetWithExpectedSize(IntStream stream, long expectedSize) {
      return stream.collect(() -> new IntOpenHashBigSet(expectedSize), IntOpenHashBigSet::add, IntOpenHashBigSet::addAll);
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
   public boolean addAll(Collection<? extends Integer> c) {
      long size = Size64.sizeOf(c);
      if (this.f <= 0.5) {
         this.ensureCapacity(size);
      } else {
         this.ensureCapacity(this.size64() + size);
      }

      return super.addAll(c);
   }

   @Override
   public boolean addAll(IntCollection c) {
      long size = Size64.sizeOf(c);
      if (this.f <= 0.5) {
         this.ensureCapacity(size);
      } else {
         this.ensureCapacity(this.size64() + size);
      }

      return super.addAll(c);
   }

   @Override
   public boolean add(int k) {
      if (k == 0) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
      } else {
         int[][] key = this.key;
         long h = HashCommon.mix((long)k);
         int displ;
         int base;
         int curr;
         if ((curr = key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) != 0) {
            if (curr == k) {
               return false;
            }

            while ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0) {
               if (curr == k) {
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
      int[][] key = this.key;

      label30:
      while (true) {
         long last = pos;

         for (pos = pos + 1L & this.mask; BigArrays.get(key, pos) != 0; pos = pos + 1L & this.mask) {
            long slot = HashCommon.mix((long)BigArrays.get(key, pos)) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               BigArrays.set(key, last, BigArrays.get(key, pos));
               continue label30;
            }
         }

         BigArrays.set(key, last, 0);
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
   public boolean remove(int k) {
      if (k == 0) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         int[][] key = this.key;
         long h = HashCommon.mix((long)k);
         int curr;
         int displ;
         int base;
         if ((curr = key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) == 0) {
            return false;
         } else if (curr == k) {
            return this.removeEntry(base, displ);
         } else {
            while ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0) {
               if (curr == k) {
                  return this.removeEntry(base, displ);
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean contains(int k) {
      if (k == 0) {
         return this.containsNull;
      } else {
         int[][] key = this.key;
         long h = HashCommon.mix((long)k);
         int curr;
         int displ;
         int base;
         if ((curr = key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) == 0) {
            return false;
         } else if (curr == k) {
            return true;
         } else {
            while ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0) {
               if (curr == k) {
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
         BigArrays.fill(this.key, 0);
      }
   }

   @Override
   public IntIterator iterator() {
      return new IntOpenHashBigSet.SetIterator();
   }

   @Override
   public IntSpliterator spliterator() {
      return new IntOpenHashBigSet.SetSpliterator();
   }

   @Override
   public void forEach(java.util.function.IntConsumer action) {
      if (this.containsNull) {
         action.accept(0);
      }

      long pos = 0L;
      long max = this.n;
      int[][] key = this.key;

      while (pos < max) {
         int gotten = BigArrays.get(key, pos++);
         if (gotten != 0) {
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
      int[][] key = this.key;
      int[][] newKey = IntBigArrays.newBigArray(newN);
      long mask = newN - 1L;
      int newSegmentMask = newKey[0].length - 1;
      int newBaseMask = newKey.length - 1;
      int base = 0;
      int displ = 0;

      for (long i = this.realSize(); i-- != 0L; base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) {
         while (key[base][displ] == 0) {
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
         }

         int k = key[base][displ];
         long h = HashCommon.mix((long)k);
         int b;
         int d;
         if (newKey[b = (int)((h & mask) >>> 27)][d = (int)(h & newSegmentMask)] != 0) {
            while (newKey[b = b + ((d = d + 1 & newSegmentMask) == 0 ? 1 : 0) & newBaseMask][d] != 0) {
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

   public IntOpenHashBigSet clone() {
      IntOpenHashBigSet c;
      try {
         c = (IntOpenHashBigSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = BigArrays.copy(this.key);
      c.containsNull = this.containsNull;
      return c;
   }

   @Override
   public int hashCode() {
      int[][] key = this.key;
      int h = 0;
      int base = 0;
      int displ = 0;

      for (long j = this.realSize(); j-- != 0L; base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) {
         while (key[base][displ] == 0) {
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
         }

         h += key[base][displ];
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      IntIterator i = this.iterator();
      s.defaultWriteObject();
      long j = this.size;

      while (j-- != 0L) {
         s.writeInt(i.nextInt());
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.bigArraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      int[][] key = this.key = IntBigArrays.newBigArray(this.n);
      this.initMasks();
      long i = this.size;

      while (i-- != 0L) {
         int k = s.readInt();
         if (k == 0) {
            this.containsNull = true;
         } else {
            long h = HashCommon.mix((long)k);
            int base;
            int displ;
            if (key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)] != 0) {
               while (key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ] != 0) {
               }
            }

            key[base][displ] = k;
         }
      }
   }

   private void checkTable() {
   }

   private class SetIterator implements IntIterator {
      int base = IntOpenHashBigSet.this.key.length;
      int displ;
      long last = -1L;
      long c = IntOpenHashBigSet.this.size;
      boolean mustReturnNull = IntOpenHashBigSet.this.containsNull;
      IntArrayList wrapped;

      private SetIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.c != 0L;
      }

      @Override
      public int nextInt() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.c--;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = IntOpenHashBigSet.this.n;
               return 0;
            } else {
               int[][] key = IntOpenHashBigSet.this.key;

               while (this.displ != 0 || this.base > 0) {
                  if (this.displ-- == 0) {
                     this.displ = key[--this.base].length - 1;
                  }

                  int k = key[this.base][this.displ];
                  if (k != 0) {
                     this.last = this.base * 134217728L + this.displ;
                     return k;
                  }
               }

               this.last = Long.MIN_VALUE;
               return this.wrapped.getInt(-(--this.base) - 1);
            }
         }
      }

      private final void shiftKeys(long pos) {
         int[][] key = IntOpenHashBigSet.this.key;

         label38:
         while (true) {
            long last = pos;

            int curr;
            for (pos = pos + 1L & IntOpenHashBigSet.this.mask; (curr = BigArrays.get(key, pos)) != 0; pos = pos + 1L & IntOpenHashBigSet.this.mask) {
               long slot = HashCommon.mix((long)curr) & IntOpenHashBigSet.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new IntArrayList();
                     }

                     this.wrapped.add(BigArrays.get(key, pos));
                  }

                  BigArrays.set(key, last, curr);
                  continue label38;
               }
            }

            BigArrays.set(key, last, 0);
            return;
         }
      }

      @Override
      public void remove() {
         if (this.last == -1L) {
            throw new IllegalStateException();
         } else {
            if (this.last == IntOpenHashBigSet.this.n) {
               IntOpenHashBigSet.this.containsNull = false;
            } else {
               if (this.base < 0) {
                  IntOpenHashBigSet.this.remove(this.wrapped.getInt(-this.base - 1));
                  this.last = -1L;
                  return;
               }

               this.shiftKeys(this.last);
            }

            IntOpenHashBigSet.this.size--;
            this.last = -1L;
         }
      }
   }

   private class SetSpliterator implements IntSpliterator {
      private static final int POST_SPLIT_CHARACTERISTICS = 257;
      long pos = 0L;
      long max;
      long c;
      boolean mustReturnNull;
      boolean hasSplit;

      SetSpliterator() {
         this.max = IntOpenHashBigSet.this.n;
         this.c = 0L;
         this.mustReturnNull = IntOpenHashBigSet.this.containsNull;
         this.hasSplit = false;
      }

      SetSpliterator(long pos, long max, boolean mustReturnNull, boolean hasSplit) {
         this.max = IntOpenHashBigSet.this.n;
         this.c = 0L;
         this.mustReturnNull = IntOpenHashBigSet.this.containsNull;
         this.hasSplit = false;
         this.pos = pos;
         this.max = max;
         this.mustReturnNull = mustReturnNull;
         this.hasSplit = hasSplit;
      }

      @Override
      public boolean tryAdvance(java.util.function.IntConsumer action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.c++;
            action.accept(0);
            return true;
         } else {
            for (int[][] key = IntOpenHashBigSet.this.key; this.pos < this.max; this.pos++) {
               int gotten = BigArrays.get(key, this.pos);
               if (gotten != 0) {
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
      public void forEachRemaining(java.util.function.IntConsumer action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            action.accept(0);
            this.c++;
         }

         for (int[][] key = IntOpenHashBigSet.this.key; this.pos < this.max; this.pos++) {
            int gotten = BigArrays.get(key, this.pos);
            if (gotten != 0) {
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
            ? IntOpenHashBigSet.this.size - this.c
            : Math.min(
               IntOpenHashBigSet.this.size - this.c,
               (long)((double)IntOpenHashBigSet.this.realSize() / IntOpenHashBigSet.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
            );
      }

      public IntOpenHashBigSet.SetSpliterator trySplit() {
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
               IntOpenHashBigSet.SetSpliterator split = IntOpenHashBigSet.this.new SetSpliterator(retPos, myNewPos, this.mustReturnNull, true);
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

            int[][] key = IntOpenHashBigSet.this.key;

            while (this.pos < this.max && n > 0L) {
               if (BigArrays.get(key, this.pos++) != 0) {
                  skipped++;
                  n--;
               }
            }

            return skipped;
         }
      }
   }
}
