package it.unimi.dsi.fastutil.bytes;

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

public class ByteArrayFrontCodedBigList extends AbstractObjectBigList<byte[]> implements Serializable, Cloneable, RandomAccess {
   private static final long serialVersionUID = 1L;
   protected final long n;
   protected final int ratio;
   protected final byte[][] array;
   protected transient long[][] p;

   public ByteArrayFrontCodedBigList(Iterator<byte[]> arrays, int ratio) {
      if (ratio < 1) {
         throw new IllegalArgumentException("Illegal ratio (" + ratio + ")");
      } else {
         byte[][] array = ByteBigArrays.EMPTY_BIG_ARRAY;
         long[][] p = LongBigArrays.EMPTY_BIG_ARRAY;
         byte[][] a = new byte[2][];
         long curSize = 0L;
         long n = 0L;

         for (int b = 0; arrays.hasNext(); n++) {
            a[b] = arrays.next();
            int length = a[b].length;
            if (n % ratio == 0L) {
               p = BigArrays.grow(p, n / ratio + 1L);
               BigArrays.set(p, n / ratio, curSize);
               array = BigArrays.grow(array, curSize + ByteArrayFrontCodedList.count(length) + length, curSize);
               curSize += ByteArrayFrontCodedList.writeInt(array, length, curSize);
               BigArrays.copyToBig(a[b], 0, array, curSize, (long)length);
               curSize += length;
            } else {
               int minLength = Math.min(a[1 - b].length, length);
               int common = 0;

               while (common < minLength && a[0][common] == a[1][common]) {
                  common++;
               }

               length -= common;
               array = BigArrays.grow(array, curSize + ByteArrayFrontCodedList.count(length) + ByteArrayFrontCodedList.count(common) + length, curSize);
               curSize += ByteArrayFrontCodedList.writeInt(array, length, curSize);
               curSize += ByteArrayFrontCodedList.writeInt(array, common, curSize);
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

   public ByteArrayFrontCodedBigList(Collection<byte[]> c, int ratio) {
      this(c.iterator(), ratio);
   }

   public int ratio() {
      return this.ratio;
   }

   private int length(long index) {
      byte[][] array = this.array;
      int delta = (int)(index % this.ratio);
      long pos = BigArrays.get(this.p, index / this.ratio);
      int length = ByteArrayFrontCodedList.readInt(array, pos);
      if (delta == 0) {
         return length;
      } else {
         pos += ByteArrayFrontCodedList.count(length) + length;
         length = ByteArrayFrontCodedList.readInt(array, pos);
         int common = ByteArrayFrontCodedList.readInt(array, pos + ByteArrayFrontCodedList.count(length));

         for (int i = 0; i < delta - 1; i++) {
            pos += ByteArrayFrontCodedList.count(length) + ByteArrayFrontCodedList.count(common) + length;
            length = ByteArrayFrontCodedList.readInt(array, pos);
            common = ByteArrayFrontCodedList.readInt(array, pos + ByteArrayFrontCodedList.count(length));
         }

         return length + common;
      }
   }

   public int arrayLength(long index) {
      this.ensureRestrictedIndex(index);
      return this.length(index);
   }

   private int extract(long index, byte[] a, int offset, int length) {
      int delta = (int)(index % this.ratio);
      long startPos = BigArrays.get(this.p, index / this.ratio);
      long pos = startPos;
      int arrayLength = ByteArrayFrontCodedList.readInt(this.array, startPos);
      int currLen = 0;
      if (delta == 0) {
         pos = BigArrays.get(this.p, index / this.ratio) + ByteArrayFrontCodedList.count(arrayLength);
         BigArrays.copyFromBig(this.array, pos, a, offset, Math.min(length, arrayLength));
         return arrayLength;
      } else {
         int common = 0;

         for (int i = 0; i < delta; i++) {
            long prevArrayPos = pos + ByteArrayFrontCodedList.count(arrayLength) + (i != 0 ? ByteArrayFrontCodedList.count(common) : 0);
            pos = prevArrayPos + arrayLength;
            arrayLength = ByteArrayFrontCodedList.readInt(this.array, pos);
            common = ByteArrayFrontCodedList.readInt(this.array, pos + ByteArrayFrontCodedList.count(arrayLength));
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
               pos + ByteArrayFrontCodedList.count(arrayLength) + ByteArrayFrontCodedList.count(common),
               a,
               currLen + offset,
               Math.min(arrayLength, length - currLen)
            );
         }

         return arrayLength + common;
      }
   }

   public byte[] get(long index) {
      return this.getArray(index);
   }

   public byte[] getArray(long index) {
      this.ensureRestrictedIndex(index);
      int length = this.length(index);
      byte[] a = new byte[length];
      this.extract(index, a, 0, length);
      return a;
   }

   public int get(long index, byte[] a, int offset, int length) {
      this.ensureRestrictedIndex(index);
      ByteArrays.ensureOffsetLength(a, offset, length);
      int arrayLength = this.extract(index, a, offset, length);
      return length >= arrayLength ? arrayLength : length - arrayLength;
   }

   public int get(long index, byte[] a) {
      return this.get(index, a, 0, a.length);
   }

   @Override
   public long size64() {
      return this.n;
   }

   @Override
   public ObjectBigListIterator<byte[]> listIterator(final long start) {
      this.ensureIndex(start);
      return new ObjectBigListIterator<byte[]>() {
         byte[] s = ByteArrays.EMPTY_ARRAY;
         long i = 0L;
         long pos = 0L;
         boolean inSync;

         {
            if (start != 0L) {
               if (start == ByteArrayFrontCodedBigList.this.n) {
                  this.i = start;
               } else {
                  this.pos = BigArrays.get(ByteArrayFrontCodedBigList.this.p, start / ByteArrayFrontCodedBigList.this.ratio);
                  int j = (int)(start % ByteArrayFrontCodedBigList.this.ratio);
                  this.i = start - j;

                  while (j-- != 0) {
                     this.next();
                  }
               }
            }
         }

         @Override
         public boolean hasNext() {
            return this.i < ByteArrayFrontCodedBigList.this.n;
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

         public byte[] next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               int length;
               if (this.i % ByteArrayFrontCodedBigList.this.ratio == 0L) {
                  this.pos = BigArrays.get(ByteArrayFrontCodedBigList.this.p, this.i / ByteArrayFrontCodedBigList.this.ratio);
                  length = ByteArrayFrontCodedList.readInt(ByteArrayFrontCodedBigList.this.array, this.pos);
                  this.s = ByteArrays.ensureCapacity(this.s, length, 0);
                  BigArrays.copyFromBig(ByteArrayFrontCodedBigList.this.array, this.pos + ByteArrayFrontCodedList.count(length), this.s, 0, length);
                  this.pos = this.pos + (length + ByteArrayFrontCodedList.count(length));
                  this.inSync = true;
               } else if (this.inSync) {
                  length = ByteArrayFrontCodedList.readInt(ByteArrayFrontCodedBigList.this.array, this.pos);
                  int common = ByteArrayFrontCodedList.readInt(ByteArrayFrontCodedBigList.this.array, this.pos + ByteArrayFrontCodedList.count(length));
                  this.s = ByteArrays.ensureCapacity(this.s, length + common, common);
                  BigArrays.copyFromBig(
                     ByteArrayFrontCodedBigList.this.array,
                     this.pos + ByteArrayFrontCodedList.count(length) + ByteArrayFrontCodedList.count(common),
                     this.s,
                     common,
                     length
                  );
                  this.pos = this.pos + (ByteArrayFrontCodedList.count(length) + ByteArrayFrontCodedList.count(common) + length);
                  length += common;
               } else {
                  this.s = ByteArrays.ensureCapacity(this.s, length = ByteArrayFrontCodedBigList.this.length(this.i), 0);
                  ByteArrayFrontCodedBigList.this.extract(this.i, this.s, 0, length);
               }

               this.i++;
               return ByteArrays.copy(this.s, 0, length);
            }
         }

         public byte[] previous() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               this.inSync = false;
               return ByteArrayFrontCodedBigList.this.getArray(--this.i);
            }
         }
      };
   }

   public ByteArrayFrontCodedBigList clone() {
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

         s.append(ByteArrayList.wrap(this.getArray(i)).toString());
      }

      s.append("]");
      return s.toString();
   }

   protected long[][] rebuildPointerArray() {
      long[][] p = LongBigArrays.newBigArray((this.n + this.ratio - 1L) / this.ratio);
      byte[][] a = this.array;
      long pos = 0L;
      int skip = this.ratio - 1;
      long i = 0L;

      for (long j = 0L; i < this.n; i++) {
         int length = ByteArrayFrontCodedList.readInt(a, pos);
         int count = ByteArrayFrontCodedList.count(length);
         if (++skip == this.ratio) {
            skip = 0;
            BigArrays.set(p, j++, pos);
            pos += count + length;
         } else {
            pos += count + ByteArrayFrontCodedList.count(ByteArrayFrontCodedList.readInt(a, pos + count)) + length;
         }
      }

      return p;
   }

   public void dump(DataOutputStream array, DataOutputStream pointers) throws IOException {
      for (byte[] s : this.array) {
         for (byte e : s) {
            array.writeByte(e);
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
