package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.longs.LongBigArrays;
import it.unimi.dsi.fastutil.objects.AbstractObjectBigList;
import it.unimi.dsi.fastutil.objects.ObjectBigListIterator;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;

public class ShortArrayFrontCodedBigList extends AbstractObjectBigList<short[]> implements Serializable, Cloneable, RandomAccess {
   private static final long serialVersionUID = 1L;
   protected final long n;
   protected final int ratio;
   protected final short[][] array;
   protected transient long[][] p;

   public ShortArrayFrontCodedBigList(Iterator<short[]> arrays, int ratio) {
      if (ratio < 1) {
         throw new IllegalArgumentException("Illegal ratio (" + ratio + ")");
      } else {
         short[][] array = ShortBigArrays.EMPTY_BIG_ARRAY;
         long[][] p = LongBigArrays.EMPTY_BIG_ARRAY;
         short[][] a = new short[2][];
         long curSize = 0L;
         long n = 0L;

         for (int b = 0; arrays.hasNext(); n++) {
            a[b] = arrays.next();
            int length = a[b].length;
            if (n % ratio == 0L) {
               p = BigArrays.grow(p, n / ratio + 1L);
               BigArrays.set(p, n / ratio, curSize);
               array = BigArrays.grow(array, curSize + ShortArrayFrontCodedList.count(length) + length, curSize);
               curSize += ShortArrayFrontCodedList.writeInt(array, length, curSize);
               BigArrays.copyToBig(a[b], 0, array, curSize, (long)length);
               curSize += length;
            } else {
               int minLength = Math.min(a[1 - b].length, length);
               int common = 0;

               while (common < minLength && a[0][common] == a[1][common]) {
                  common++;
               }

               length -= common;
               array = BigArrays.grow(array, curSize + ShortArrayFrontCodedList.count(length) + ShortArrayFrontCodedList.count(common) + length, curSize);
               curSize += ShortArrayFrontCodedList.writeInt(array, length, curSize);
               curSize += ShortArrayFrontCodedList.writeInt(array, common, curSize);
               BigArrays.copyToBig(a[b], common, array, curSize, (long)length);
               curSize += length;
            }

            b = 1 - b;
         }

         this.n = n;
         this.ratio = ratio;
         this.array = BigArrays.trim(array, curSize);
         this.p = BigArrays.trim(p, (n + ratio - 1L) / ratio);
      }
   }

   public ShortArrayFrontCodedBigList(Collection<short[]> c, int ratio) {
      this(c.iterator(), ratio);
   }

   public int ratio() {
      return this.ratio;
   }

   private int length(long index) {
      short[][] array = this.array;
      int delta = (int)(index % this.ratio);
      long pos = BigArrays.get(this.p, index / this.ratio);
      int length = ShortArrayFrontCodedList.readInt(array, pos);
      if (delta == 0) {
         return length;
      } else {
         pos += ShortArrayFrontCodedList.count(length) + length;
         length = ShortArrayFrontCodedList.readInt(array, pos);
         int common = ShortArrayFrontCodedList.readInt(array, pos + ShortArrayFrontCodedList.count(length));

         for (int i = 0; i < delta - 1; i++) {
            pos += ShortArrayFrontCodedList.count(length) + ShortArrayFrontCodedList.count(common) + length;
            length = ShortArrayFrontCodedList.readInt(array, pos);
            common = ShortArrayFrontCodedList.readInt(array, pos + ShortArrayFrontCodedList.count(length));
         }

         return length + common;
      }
   }

   public int arrayLength(long index) {
      this.ensureRestrictedIndex(index);
      return this.length(index);
   }

   private int extract(long index, short[] a, int offset, int length) {
      int delta = (int)(index % this.ratio);
      long startPos = BigArrays.get(this.p, index / this.ratio);
      long pos = startPos;
      int arrayLength = ShortArrayFrontCodedList.readInt(this.array, startPos);
      int currLen = 0;
      if (delta == 0) {
         pos = BigArrays.get(this.p, index / this.ratio) + ShortArrayFrontCodedList.count(arrayLength);
         BigArrays.copyFromBig(this.array, pos, a, offset, Math.min(length, arrayLength));
         return arrayLength;
      } else {
         int common = 0;

         for (int i = 0; i < delta; i++) {
            long prevArrayPos = pos + ShortArrayFrontCodedList.count(arrayLength) + (i != 0 ? ShortArrayFrontCodedList.count(common) : 0);
            pos = prevArrayPos + arrayLength;
            arrayLength = ShortArrayFrontCodedList.readInt(this.array, pos);
            common = ShortArrayFrontCodedList.readInt(this.array, pos + ShortArrayFrontCodedList.count(arrayLength));
            int actualCommon = Math.min(common, length);
            if (actualCommon <= currLen) {
               currLen = actualCommon;
            } else {
               BigArrays.copyFromBig(this.array, prevArrayPos, a, currLen + offset, actualCommon - currLen);
               currLen = actualCommon;
            }
         }

         if (currLen < length) {
            BigArrays.copyFromBig(
               this.array,
               pos + ShortArrayFrontCodedList.count(arrayLength) + ShortArrayFrontCodedList.count(common),
               a,
               currLen + offset,
               Math.min(arrayLength, length - currLen)
            );
         }

         return arrayLength + common;
      }
   }

   public short[] get(long index) {
      return this.getArray(index);
   }

   public short[] getArray(long index) {
      this.ensureRestrictedIndex(index);
      int length = this.length(index);
      short[] a = new short[length];
      this.extract(index, a, 0, length);
      return a;
   }

   public int get(long index, short[] a, int offset, int length) {
      this.ensureRestrictedIndex(index);
      ShortArrays.ensureOffsetLength(a, offset, length);
      int arrayLength = this.extract(index, a, offset, length);
      return length >= arrayLength ? arrayLength : length - arrayLength;
   }

   public int get(long index, short[] a) {
      return this.get(index, a, 0, a.length);
   }

   @Override
   public long size64() {
      return this.n;
   }

   @Override
   public ObjectBigListIterator<short[]> listIterator(final long start) {
      this.ensureIndex(start);
      return new ObjectBigListIterator<short[]>() {
         short[] s = ShortArrays.EMPTY_ARRAY;
         long i = 0L;
         long pos = 0L;
         boolean inSync;

         {
            if (start != 0L) {
               if (start == ShortArrayFrontCodedBigList.this.n) {
                  this.i = start;
               } else {
                  this.pos = BigArrays.get(ShortArrayFrontCodedBigList.this.p, start / ShortArrayFrontCodedBigList.this.ratio);
                  int j = (int)(start % ShortArrayFrontCodedBigList.this.ratio);
                  this.i = start - j;

                  while (j-- != 0) {
                     this.next();
                  }
               }
            }
         }

         @Override
         public boolean hasNext() {
            return this.i < ShortArrayFrontCodedBigList.this.n;
         }

         @Override
         public boolean hasPrevious() {
            return this.i > 0L;
         }

         @Override
         public long previousIndex() {
            return this.i - 1L;
         }

         @Override
         public long nextIndex() {
            return this.i;
         }

         public short[] next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               int length;
               if (this.i % ShortArrayFrontCodedBigList.this.ratio == 0L) {
                  this.pos = BigArrays.get(ShortArrayFrontCodedBigList.this.p, this.i / ShortArrayFrontCodedBigList.this.ratio);
                  length = ShortArrayFrontCodedList.readInt(ShortArrayFrontCodedBigList.this.array, this.pos);
                  this.s = ShortArrays.ensureCapacity(this.s, length, 0);
                  BigArrays.copyFromBig(ShortArrayFrontCodedBigList.this.array, this.pos + ShortArrayFrontCodedList.count(length), this.s, 0, length);
                  this.pos = this.pos + (length + ShortArrayFrontCodedList.count(length));
                  this.inSync = true;
               } else if (this.inSync) {
                  length = ShortArrayFrontCodedList.readInt(ShortArrayFrontCodedBigList.this.array, this.pos);
                  int common = ShortArrayFrontCodedList.readInt(ShortArrayFrontCodedBigList.this.array, this.pos + ShortArrayFrontCodedList.count(length));
                  this.s = ShortArrays.ensureCapacity(this.s, length + common, common);
                  BigArrays.copyFromBig(
                     ShortArrayFrontCodedBigList.this.array,
                     this.pos + ShortArrayFrontCodedList.count(length) + ShortArrayFrontCodedList.count(common),
                     this.s,
                     common,
                     length
                  );
                  this.pos = this.pos + (ShortArrayFrontCodedList.count(length) + ShortArrayFrontCodedList.count(common) + length);
                  length += common;
               } else {
                  this.s = ShortArrays.ensureCapacity(this.s, length = ShortArrayFrontCodedBigList.this.length(this.i), 0);
                  ShortArrayFrontCodedBigList.this.extract(this.i, this.s, 0, length);
               }

               this.i++;
               return ShortArrays.copy(this.s, 0, length);
            }
         }

         public short[] previous() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               this.inSync = false;
               return ShortArrayFrontCodedBigList.this.getArray(--this.i);
            }
         }
      };
   }

   public ShortArrayFrontCodedBigList clone() {
      return this;
   }

   @Override
   public String toString() {
      StringBuffer s = new StringBuffer();
      s.append("[");

      for (long i = 0L; i < this.n; i++) {
         if (i != 0L) {
            s.append(", ");
         }

         s.append(ShortArrayList.wrap(this.getArray(i)).toString());
      }

      s.append("]");
      return s.toString();
   }

   protected long[][] rebuildPointerArray() {
      long[][] p = LongBigArrays.newBigArray((this.n + this.ratio - 1L) / this.ratio);
      short[][] a = this.array;
      long pos = 0L;
      int skip = this.ratio - 1;
      long i = 0L;

      for (long j = 0L; i < this.n; i++) {
         int length = ShortArrayFrontCodedList.readInt(a, pos);
         int count = ShortArrayFrontCodedList.count(length);
         if (++skip == this.ratio) {
            skip = 0;
            BigArrays.set(p, j++, pos);
            pos += count + length;
         } else {
            pos += count + ShortArrayFrontCodedList.count(ShortArrayFrontCodedList.readInt(a, pos + count)) + length;
         }
      }

      return p;
   }

   public void dump(DataOutputStream array, DataOutputStream pointers) throws IOException {
      for (short[] s : this.array) {
         for (short e : s) {
            array.writeShort(e);
         }
      }

      for (long[] s : this.p) {
         for (long e : s) {
            pointers.writeLong(e);
         }
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.p = this.rebuildPointerArray();
   }
}
