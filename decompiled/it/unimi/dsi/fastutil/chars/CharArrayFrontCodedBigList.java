package it.unimi.dsi.fastutil.chars;

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

public class CharArrayFrontCodedBigList extends AbstractObjectBigList<char[]> implements Serializable, Cloneable, RandomAccess {
   private static final long serialVersionUID = 1L;
   protected final long n;
   protected final int ratio;
   protected final char[][] array;
   protected transient long[][] p;

   public CharArrayFrontCodedBigList(Iterator<char[]> arrays, int ratio) {
      if (ratio < 1) {
         throw new IllegalArgumentException("Illegal ratio (" + ratio + ")");
      } else {
         char[][] array = CharBigArrays.EMPTY_BIG_ARRAY;
         long[][] p = LongBigArrays.EMPTY_BIG_ARRAY;
         char[][] a = new char[2][];
         long curSize = 0L;
         long n = 0L;

         for (int b = 0; arrays.hasNext(); n++) {
            a[b] = arrays.next();
            int length = a[b].length;
            if (n % ratio == 0L) {
               p = BigArrays.grow(p, n / ratio + 1L);
               BigArrays.set(p, n / ratio, curSize);
               array = BigArrays.grow(array, curSize + CharArrayFrontCodedList.count(length) + length, curSize);
               curSize += CharArrayFrontCodedList.writeInt(array, length, curSize);
               BigArrays.copyToBig(a[b], 0, array, curSize, (long)length);
               curSize += length;
            } else {
               int minLength = Math.min(a[1 - b].length, length);
               int common = 0;

               while (common < minLength && a[0][common] == a[1][common]) {
                  common++;
               }

               length -= common;
               array = BigArrays.grow(array, curSize + CharArrayFrontCodedList.count(length) + CharArrayFrontCodedList.count(common) + length, curSize);
               curSize += CharArrayFrontCodedList.writeInt(array, length, curSize);
               curSize += CharArrayFrontCodedList.writeInt(array, common, curSize);
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

   public CharArrayFrontCodedBigList(Collection<char[]> c, int ratio) {
      this(c.iterator(), ratio);
   }

   public int ratio() {
      return this.ratio;
   }

   private int length(long index) {
      char[][] array = this.array;
      int delta = (int)(index % this.ratio);
      long pos = BigArrays.get(this.p, index / this.ratio);
      int length = CharArrayFrontCodedList.readInt(array, pos);
      if (delta == 0) {
         return length;
      } else {
         pos += CharArrayFrontCodedList.count(length) + length;
         length = CharArrayFrontCodedList.readInt(array, pos);
         int common = CharArrayFrontCodedList.readInt(array, pos + CharArrayFrontCodedList.count(length));

         for (int i = 0; i < delta - 1; i++) {
            pos += CharArrayFrontCodedList.count(length) + CharArrayFrontCodedList.count(common) + length;
            length = CharArrayFrontCodedList.readInt(array, pos);
            common = CharArrayFrontCodedList.readInt(array, pos + CharArrayFrontCodedList.count(length));
         }

         return length + common;
      }
   }

   public int arrayLength(long index) {
      this.ensureRestrictedIndex(index);
      return this.length(index);
   }

   private int extract(long index, char[] a, int offset, int length) {
      int delta = (int)(index % this.ratio);
      long startPos = BigArrays.get(this.p, index / this.ratio);
      long pos = startPos;
      int arrayLength = CharArrayFrontCodedList.readInt(this.array, startPos);
      int currLen = 0;
      if (delta == 0) {
         pos = BigArrays.get(this.p, index / this.ratio) + CharArrayFrontCodedList.count(arrayLength);
         BigArrays.copyFromBig(this.array, pos, a, offset, Math.min(length, arrayLength));
         return arrayLength;
      } else {
         int common = 0;

         for (int i = 0; i < delta; i++) {
            long prevArrayPos = pos + CharArrayFrontCodedList.count(arrayLength) + (i != 0 ? CharArrayFrontCodedList.count(common) : 0);
            pos = prevArrayPos + arrayLength;
            arrayLength = CharArrayFrontCodedList.readInt(this.array, pos);
            common = CharArrayFrontCodedList.readInt(this.array, pos + CharArrayFrontCodedList.count(arrayLength));
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
               pos + CharArrayFrontCodedList.count(arrayLength) + CharArrayFrontCodedList.count(common),
               a,
               currLen + offset,
               Math.min(arrayLength, length - currLen)
            );
         }

         return arrayLength + common;
      }
   }

   public char[] get(long index) {
      return this.getArray(index);
   }

   public char[] getArray(long index) {
      this.ensureRestrictedIndex(index);
      int length = this.length(index);
      char[] a = new char[length];
      this.extract(index, a, 0, length);
      return a;
   }

   public int get(long index, char[] a, int offset, int length) {
      this.ensureRestrictedIndex(index);
      CharArrays.ensureOffsetLength(a, offset, length);
      int arrayLength = this.extract(index, a, offset, length);
      return length >= arrayLength ? arrayLength : length - arrayLength;
   }

   public int get(long index, char[] a) {
      return this.get(index, a, 0, a.length);
   }

   @Override
   public long size64() {
      return this.n;
   }

   @Override
   public ObjectBigListIterator<char[]> listIterator(final long start) {
      this.ensureIndex(start);
      return new ObjectBigListIterator<char[]>() {
         char[] s = CharArrays.EMPTY_ARRAY;
         long i = 0L;
         long pos = 0L;
         boolean inSync;

         {
            if (start != 0L) {
               if (start == CharArrayFrontCodedBigList.this.n) {
                  this.i = start;
               } else {
                  this.pos = BigArrays.get(CharArrayFrontCodedBigList.this.p, start / CharArrayFrontCodedBigList.this.ratio);
                  int j = (int)(start % CharArrayFrontCodedBigList.this.ratio);
                  this.i = start - j;

                  while (j-- != 0) {
                     this.next();
                  }
               }
            }
         }

         @Override
         public boolean hasNext() {
            return this.i < CharArrayFrontCodedBigList.this.n;
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

         public char[] next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               int length;
               if (this.i % CharArrayFrontCodedBigList.this.ratio == 0L) {
                  this.pos = BigArrays.get(CharArrayFrontCodedBigList.this.p, this.i / CharArrayFrontCodedBigList.this.ratio);
                  length = CharArrayFrontCodedList.readInt(CharArrayFrontCodedBigList.this.array, this.pos);
                  this.s = CharArrays.ensureCapacity(this.s, length, 0);
                  BigArrays.copyFromBig(CharArrayFrontCodedBigList.this.array, this.pos + CharArrayFrontCodedList.count(length), this.s, 0, length);
                  this.pos = this.pos + (length + CharArrayFrontCodedList.count(length));
                  this.inSync = true;
               } else if (this.inSync) {
                  length = CharArrayFrontCodedList.readInt(CharArrayFrontCodedBigList.this.array, this.pos);
                  int common = CharArrayFrontCodedList.readInt(CharArrayFrontCodedBigList.this.array, this.pos + CharArrayFrontCodedList.count(length));
                  this.s = CharArrays.ensureCapacity(this.s, length + common, common);
                  BigArrays.copyFromBig(
                     CharArrayFrontCodedBigList.this.array,
                     this.pos + CharArrayFrontCodedList.count(length) + CharArrayFrontCodedList.count(common),
                     this.s,
                     common,
                     length
                  );
                  this.pos = this.pos + (CharArrayFrontCodedList.count(length) + CharArrayFrontCodedList.count(common) + length);
                  length += common;
               } else {
                  this.s = CharArrays.ensureCapacity(this.s, length = CharArrayFrontCodedBigList.this.length(this.i), 0);
                  CharArrayFrontCodedBigList.this.extract(this.i, this.s, 0, length);
               }

               this.i++;
               return CharArrays.copy(this.s, 0, length);
            }
         }

         public char[] previous() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               this.inSync = false;
               return CharArrayFrontCodedBigList.this.getArray(--this.i);
            }
         }
      };
   }

   public CharArrayFrontCodedBigList clone() {
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

         s.append(CharArrayList.wrap(this.getArray(i)).toString());
      }

      s.append("]");
      return s.toString();
   }

   protected long[][] rebuildPointerArray() {
      long[][] p = LongBigArrays.newBigArray((this.n + this.ratio - 1L) / this.ratio);
      char[][] a = this.array;
      long pos = 0L;
      int skip = this.ratio - 1;
      long i = 0L;

      for (long j = 0L; i < this.n; i++) {
         int length = CharArrayFrontCodedList.readInt(a, pos);
         int count = CharArrayFrontCodedList.count(length);
         if (++skip == this.ratio) {
            skip = 0;
            BigArrays.set(p, j++, pos);
            pos += count + length;
         } else {
            pos += count + CharArrayFrontCodedList.count(CharArrayFrontCodedList.readInt(a, pos + count)) + length;
         }
      }

      return p;
   }

   public void dump(DataOutputStream array, DataOutputStream pointers) throws IOException {
      for (char[] s : this.array) {
         for (char e : s) {
            array.writeChar(e);
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
