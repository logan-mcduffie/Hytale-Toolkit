package it.unimi.dsi.fastutil.longs;

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
import java.util.stream.LongStream;

public class LongOpenHashBigSet extends AbstractLongSet implements Serializable, Cloneable, Hash, Size64 {
   private static final long serialVersionUID = 0L;
   private static final boolean ASSERTS = false;
   protected transient long[][] key;
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

   public LongOpenHashBigSet(long expected, float f) {
      if (f <= 0.0F || f > 1.0F) {
         throw new IllegalArgumentException("Load factor must be greater than 0 and smaller than or equal to 1");
      } else if (this.n < 0L) {
         throw new IllegalArgumentException("The expected number of elements must be nonnegative");
      } else {
         this.f = f;
         this.minN = this.n = HashCommon.bigArraySize(expected, f);
         this.maxFill = HashCommon.maxFill(this.n, f);
         this.key = LongBigArrays.newBigArray(this.n);
         this.initMasks();
      }
   }

   public LongOpenHashBigSet(long expected) {
      this(expected, 0.75F);
   }

   public LongOpenHashBigSet() {
      this(16L, 0.75F);
   }

   public LongOpenHashBigSet(Collection<? extends Long> c, float f) {
      this(Size64.sizeOf(c), f);
      this.addAll(c);
   }

   public LongOpenHashBigSet(Collection<? extends Long> c) {
      this(c, 0.75F);
   }

   public LongOpenHashBigSet(LongCollection c, float f) {
      this(Size64.sizeOf(c), f);
      this.addAll(c);
   }

   public LongOpenHashBigSet(LongCollection c) {
      this(c, 0.75F);
   }

   public LongOpenHashBigSet(LongIterator i, float f) {
      this(16L, f);

      while (i.hasNext()) {
         this.add(i.nextLong());
      }
   }

   public LongOpenHashBigSet(LongIterator i) {
      this(i, 0.75F);
   }

   public LongOpenHashBigSet(Iterator<?> i, float f) {
      this(LongIterators.asLongIterator(i), f);
   }

   public LongOpenHashBigSet(Iterator<?> i) {
      this(LongIterators.asLongIterator(i));
   }

   public LongOpenHashBigSet(long[] a, int offset, int length, float f) {
      this(length < 0 ? 0L : length, f);
      LongArrays.ensureOffsetLength(a, offset, length);

      for (int i = 0; i < length; i++) {
         this.add(a[offset + i]);
      }
   }

   public LongOpenHashBigSet(long[] a, int offset, int length) {
      this(a, offset, length, 0.75F);
   }

   public LongOpenHashBigSet(long[] a, float f) {
      this(a, 0, a.length, f);
   }

   public LongOpenHashBigSet(long[] a) {
      this(a, 0.75F);
   }

   public static LongOpenHashBigSet toBigSet(LongStream stream) {
      return stream.collect(LongOpenHashBigSet::new, LongOpenHashBigSet::add, LongOpenHashBigSet::addAll);
   }

   public static LongOpenHashBigSet toBigSetWithExpectedSize(LongStream stream, long expectedSize) {
      return stream.collect(() -> new LongOpenHashBigSet(expectedSize), LongOpenHashBigSet::add, LongOpenHashBigSet::addAll);
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
   public boolean addAll(Collection<? extends Long> c) {
      long size = Size64.sizeOf(c);
      if (this.f <= 0.5) {
         this.ensureCapacity(size);
      } else {
         this.ensureCapacity(this.size64() + size);
      }

      return super.addAll(c);
   }

   @Override
   public boolean addAll(LongCollection c) {
      long size = Size64.sizeOf(c);
      if (this.f <= 0.5) {
         this.ensureCapacity(size);
      } else {
         this.ensureCapacity(this.size64() + size);
      }

      return super.addAll(c);
   }

   @Override
   public boolean add(long k) {
      if (k == 0L) {
         if (this.containsNull) {
            return false;
         }

         this.containsNull = true;
      } else {
         long[][] key = this.key;
         long h = HashCommon.mix(k);
         int displ;
         int base;
         long curr;
         if ((curr = key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) != 0L) {
            if (curr == k) {
               return false;
            }

            while ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0L) {
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
      long[][] key = this.key;

      label30:
      while (true) {
         long last = pos;

         for (pos = pos + 1L & this.mask; BigArrays.get(key, pos) != 0L; pos = pos + 1L & this.mask) {
            long slot = HashCommon.mix(BigArrays.get(key, pos)) & this.mask;
            if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
               BigArrays.set(key, last, BigArrays.get(key, pos));
               continue label30;
            }
         }

         BigArrays.set(key, last, 0L);
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
   public boolean remove(long k) {
      if (k == 0L) {
         return this.containsNull ? this.removeNullEntry() : false;
      } else {
         long[][] key = this.key;
         long h = HashCommon.mix(k);
         long curr;
         int displ;
         int base;
         if ((curr = key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) == 0L) {
            return false;
         } else if (curr == k) {
            return this.removeEntry(base, displ);
         } else {
            while ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0L) {
               if (curr == k) {
                  return this.removeEntry(base, displ);
               }
            }

            return false;
         }
      }
   }

   @Override
   public boolean contains(long k) {
      if (k == 0L) {
         return this.containsNull;
      } else {
         long[][] key = this.key;
         long h = HashCommon.mix(k);
         long curr;
         int displ;
         int base;
         if ((curr = key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)]) == 0L) {
            return false;
         } else if (curr == k) {
            return true;
         } else {
            while ((curr = key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ]) != 0L) {
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
         BigArrays.fill(this.key, 0L);
      }
   }

   @Override
   public LongIterator iterator() {
      return new LongOpenHashBigSet.SetIterator();
   }

   @Override
   public LongSpliterator spliterator() {
      return new LongOpenHashBigSet.SetSpliterator();
   }

   @Override
   public void forEach(java.util.function.LongConsumer action) {
      if (this.containsNull) {
         action.accept(0L);
      }

      long pos = 0L;
      long max = this.n;
      long[][] key = this.key;

      while (pos < max) {
         long gotten = BigArrays.get(key, pos++);
         if (gotten != 0L) {
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
      long[][] key = this.key;
      long[][] newKey = LongBigArrays.newBigArray(newN);
      long mask = newN - 1L;
      int newSegmentMask = newKey[0].length - 1;
      int newBaseMask = newKey.length - 1;
      int base = 0;
      int displ = 0;

      for (long i = this.realSize(); i-- != 0L; base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) {
         while (key[base][displ] == 0L) {
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
         }

         long k = key[base][displ];
         long h = HashCommon.mix(k);
         int b;
         int d;
         if (newKey[b = (int)((h & mask) >>> 27)][d = (int)(h & newSegmentMask)] != 0L) {
            while (newKey[b = b + ((d = d + 1 & newSegmentMask) == 0 ? 1 : 0) & newBaseMask][d] != 0L) {
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

   public LongOpenHashBigSet clone() {
      LongOpenHashBigSet c;
      try {
         c = (LongOpenHashBigSet)super.clone();
      } catch (CloneNotSupportedException var3) {
         throw new InternalError();
      }

      c.key = BigArrays.copy(this.key);
      c.containsNull = this.containsNull;
      return c;
   }

   @Override
   public int hashCode() {
      long[][] key = this.key;
      int h = 0;
      int base = 0;
      int displ = 0;

      for (long j = this.realSize(); j-- != 0L; base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) {
         while (key[base][displ] == 0L) {
            base += (displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0;
         }

         h += HashCommon.long2int(key[base][displ]);
      }

      return h;
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      LongIterator i = this.iterator();
      s.defaultWriteObject();
      long j = this.size;

      while (j-- != 0L) {
         s.writeLong(i.nextLong());
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.n = HashCommon.bigArraySize(this.size, this.f);
      this.maxFill = HashCommon.maxFill(this.n, this.f);
      long[][] key = this.key = LongBigArrays.newBigArray(this.n);
      this.initMasks();
      long i = this.size;

      while (i-- != 0L) {
         long k = s.readLong();
         if (k == 0L) {
            this.containsNull = true;
         } else {
            long h = HashCommon.mix(k);
            int base;
            int displ;
            if (key[base = (int)((h & this.mask) >>> 27)][displ = (int)(h & this.segmentMask)] != 0L) {
               while (key[base = base + ((displ = displ + 1 & this.segmentMask) == 0 ? 1 : 0) & this.baseMask][displ] != 0L) {
               }
            }

            key[base][displ] = k;
         }
      }
   }

   private void checkTable() {
   }

   private class SetIterator implements LongIterator {
      int base = LongOpenHashBigSet.this.key.length;
      int displ;
      long last = -1L;
      long c = LongOpenHashBigSet.this.size;
      boolean mustReturnNull = LongOpenHashBigSet.this.containsNull;
      LongArrayList wrapped;

      private SetIterator() {
      }

      @Override
      public boolean hasNext() {
         return this.c != 0L;
      }

      @Override
      public long nextLong() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.c--;
            if (this.mustReturnNull) {
               this.mustReturnNull = false;
               this.last = LongOpenHashBigSet.this.n;
               return 0L;
            } else {
               long[][] key = LongOpenHashBigSet.this.key;

               while (this.displ != 0 || this.base > 0) {
                  if (this.displ-- == 0) {
                     this.displ = key[--this.base].length - 1;
                  }

                  long k = key[this.base][this.displ];
                  if (k != 0L) {
                     this.last = this.base * 134217728L + this.displ;
                     return k;
                  }
               }

               this.last = Long.MIN_VALUE;
               return this.wrapped.getLong(-(--this.base) - 1);
            }
         }
      }

      private final void shiftKeys(long pos) {
         long[][] key = LongOpenHashBigSet.this.key;

         label38:
         while (true) {
            long last = pos;

            long curr;
            for (pos = pos + 1L & LongOpenHashBigSet.this.mask; (curr = BigArrays.get(key, pos)) != 0L; pos = pos + 1L & LongOpenHashBigSet.this.mask) {
               long slot = HashCommon.mix(curr) & LongOpenHashBigSet.this.mask;
               if (last <= pos ? last >= slot || slot > pos : last >= slot && slot > pos) {
                  if (pos < last) {
                     if (this.wrapped == null) {
                        this.wrapped = new LongArrayList();
                     }

                     this.wrapped.add(BigArrays.get(key, pos));
                  }

                  BigArrays.set(key, last, curr);
                  continue label38;
               }
            }

            BigArrays.set(key, last, 0L);
            return;
         }
      }

      @Override
      public void remove() {
         if (this.last == -1L) {
            throw new IllegalStateException();
         } else {
            if (this.last == LongOpenHashBigSet.this.n) {
               LongOpenHashBigSet.this.containsNull = false;
            } else {
               if (this.base < 0) {
                  LongOpenHashBigSet.this.remove(this.wrapped.getLong(-this.base - 1));
                  this.last = -1L;
                  return;
               }

               this.shiftKeys(this.last);
            }

            LongOpenHashBigSet.this.size--;
            this.last = -1L;
         }
      }
   }

   private class SetSpliterator implements LongSpliterator {
      private static final int POST_SPLIT_CHARACTERISTICS = 257;
      long pos = 0L;
      long max;
      long c;
      boolean mustReturnNull;
      boolean hasSplit;

      SetSpliterator() {
         this.max = LongOpenHashBigSet.this.n;
         this.c = 0L;
         this.mustReturnNull = LongOpenHashBigSet.this.containsNull;
         this.hasSplit = false;
      }

      SetSpliterator(long pos, long max, boolean mustReturnNull, boolean hasSplit) {
         this.max = LongOpenHashBigSet.this.n;
         this.c = 0L;
         this.mustReturnNull = LongOpenHashBigSet.this.containsNull;
         this.hasSplit = false;
         this.pos = pos;
         this.max = max;
         this.mustReturnNull = mustReturnNull;
         this.hasSplit = hasSplit;
      }

      @Override
      public boolean tryAdvance(java.util.function.LongConsumer action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            this.c++;
            action.accept(0L);
            return true;
         } else {
            for (long[][] key = LongOpenHashBigSet.this.key; this.pos < this.max; this.pos++) {
               long gotten = BigArrays.get(key, this.pos);
               if (gotten != 0L) {
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
      public void forEachRemaining(java.util.function.LongConsumer action) {
         if (this.mustReturnNull) {
            this.mustReturnNull = false;
            action.accept(0L);
            this.c++;
         }

         for (long[][] key = LongOpenHashBigSet.this.key; this.pos < this.max; this.pos++) {
            long gotten = BigArrays.get(key, this.pos);
            if (gotten != 0L) {
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
            ? LongOpenHashBigSet.this.size - this.c
            : Math.min(
               LongOpenHashBigSet.this.size - this.c,
               (long)((double)LongOpenHashBigSet.this.realSize() / LongOpenHashBigSet.this.n * (this.max - this.pos)) + (this.mustReturnNull ? 1 : 0)
            );
      }

      public LongOpenHashBigSet.SetSpliterator trySplit() {
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
               LongOpenHashBigSet.SetSpliterator split = LongOpenHashBigSet.this.new SetSpliterator(retPos, myNewPos, this.mustReturnNull, true);
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

            long[][] key = LongOpenHashBigSet.this.key;

            while (this.pos < this.max && n > 0L) {
               if (BigArrays.get(key, this.pos++) != 0L) {
                  skipped++;
                  n--;
               }
            }

            return skipped;
         }
      }
   }
}
