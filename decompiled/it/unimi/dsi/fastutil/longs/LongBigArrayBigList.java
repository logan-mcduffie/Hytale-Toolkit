package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.Size64;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.stream.LongStream;

public class LongBigArrayBigList extends AbstractLongBigList implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353130L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected transient long[][] a;
   protected long size;

   protected LongBigArrayBigList(long[][] a, boolean dummy) {
      this.a = a;
   }

   public LongBigArrayBigList(long capacity) {
      if (capacity < 0L) {
         throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
      } else {
         if (capacity == 0L) {
            this.a = LongBigArrays.EMPTY_BIG_ARRAY;
         } else {
            this.a = LongBigArrays.newBigArray(capacity);
         }
      }
   }

   public LongBigArrayBigList() {
      this.a = LongBigArrays.DEFAULT_EMPTY_BIG_ARRAY;
   }

   public LongBigArrayBigList(LongCollection c) {
      this(Size64.sizeOf(c));
      if (c instanceof LongBigList) {
         ((LongBigList)c).getElements(0L, this.a, 0L, this.size = Size64.sizeOf(c));
      } else {
         LongIterator i = c.iterator();

         while (i.hasNext()) {
            this.add(i.nextLong());
         }
      }
   }

   public LongBigArrayBigList(LongBigList l) {
      this(l.size64());
      l.getElements(0L, this.a, 0L, this.size = l.size64());
   }

   public LongBigArrayBigList(long[][] a) {
      this(a, 0L, BigArrays.length(a));
   }

   public LongBigArrayBigList(long[][] a, long offset, long length) {
      this(length);
      BigArrays.copy(a, offset, this.a, 0L, length);
      this.size = length;
   }

   public LongBigArrayBigList(Iterator<? extends Long> i) {
      this();

      while (i.hasNext()) {
         this.add(i.next());
      }
   }

   public LongBigArrayBigList(LongIterator i) {
      this();

      while (i.hasNext()) {
         this.add(i.nextLong());
      }
   }

   public long[][] elements() {
      return this.a;
   }

   public static LongBigArrayBigList wrap(long[][] a, long length) {
      if (length > BigArrays.length(a)) {
         throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + BigArrays.length(a) + ")");
      } else {
         LongBigArrayBigList l = new LongBigArrayBigList(a, false);
         l.size = length;
         return l;
      }
   }

   public static LongBigArrayBigList wrap(long[][] a) {
      return wrap(a, BigArrays.length(a));
   }

   public static LongBigArrayBigList of() {
      return new LongBigArrayBigList();
   }

   public static LongBigArrayBigList of(long... init) {
      return wrap(BigArrays.wrap(init));
   }

   public static LongBigArrayBigList toBigList(LongStream stream) {
      return stream.collect(LongBigArrayBigList::new, LongBigArrayBigList::add, LongBigList::addAll);
   }

   public static LongBigArrayBigList toBigListWithExpectedSize(LongStream stream, long expectedSize) {
      return stream.collect(() -> new LongBigArrayBigList(expectedSize), LongBigArrayBigList::add, LongBigList::addAll);
   }

   public void ensureCapacity(long capacity) {
      if (capacity > BigArrays.length(this.a) && this.a != LongBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
         this.a = BigArrays.forceCapacity(this.a, capacity, this.size);

         assert this.size <= BigArrays.length(this.a);
      }
   }

   private void grow(long capacity) {
      long oldLength = BigArrays.length(this.a);
      if (capacity > oldLength) {
         if (this.a != LongBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
            capacity = Math.max(oldLength + (oldLength >> 1), capacity);
         } else if (capacity < 10L) {
            capacity = 10L;
         }

         this.a = BigArrays.forceCapacity(this.a, capacity, this.size);

         assert this.size <= BigArrays.length(this.a);
      }
   }

   @Override
   public void add(long index, long k) {
      this.ensureIndex(index);
      this.grow(this.size + 1L);
      if (index != this.size) {
         BigArrays.copy(this.a, index, this.a, index + 1L, this.size - index);
      }

      BigArrays.set(this.a, index, k);
      this.size++;

      assert this.size <= BigArrays.length(this.a);
   }

   @Override
   public boolean add(long k) {
      this.grow(this.size + 1L);
      BigArrays.set(this.a, this.size++, k);

      assert this.size <= BigArrays.length(this.a);

      return true;
   }

   @Override
   public long getLong(long index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return BigArrays.get(this.a, index);
      }
   }

   @Override
   public long indexOf(long k) {
      for (long i = 0L; i < this.size; i++) {
         if (k == BigArrays.get(this.a, i)) {
            return i;
         }
      }

      return -1L;
   }

   @Override
   public long lastIndexOf(long k) {
      long i = this.size;

      while (i-- != 0L) {
         if (k == BigArrays.get(this.a, i)) {
            return i;
         }
      }

      return -1L;
   }

   @Override
   public long removeLong(long index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         long old = BigArrays.get(this.a, index);
         this.size--;
         if (index != this.size) {
            BigArrays.copy(this.a, index + 1L, this.a, index, this.size - index);
         }

         assert this.size <= BigArrays.length(this.a);

         return old;
      }
   }

   @Override
   public boolean rem(long k) {
      long index = this.indexOf(k);
      if (index == -1L) {
         return false;
      } else {
         this.removeLong(index);

         assert this.size <= BigArrays.length(this.a);

         return true;
      }
   }

   @Override
   public long set(long index, long k) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         long old = BigArrays.get(this.a, index);
         BigArrays.set(this.a, index, k);
         return old;
      }
   }

   @Override
   public boolean removeAll(LongCollection c) {
      long[] s = null;
      long[] d = null;
      int ss = -1;
      int sd = 134217728;
      int ds = -1;
      int dd = 134217728;

      for (long i = 0L; i < this.size; i++) {
         if (sd == 134217728) {
            sd = 0;
            s = this.a[++ss];
         }

         if (!c.contains(s[sd])) {
            if (dd == 134217728) {
               d = this.a[++ds];
               dd = 0;
            }

            d[dd++] = s[sd];
         }

         sd++;
      }

      long j = BigArrays.index(ds, dd);
      boolean modified = this.size != j;
      this.size = j;
      return modified;
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      long[] s = null;
      long[] d = null;
      int ss = -1;
      int sd = 134217728;
      int ds = -1;
      int dd = 134217728;

      for (long i = 0L; i < this.size; i++) {
         if (sd == 134217728) {
            sd = 0;
            s = this.a[++ss];
         }

         if (!c.contains(s[sd])) {
            if (dd == 134217728) {
               d = this.a[++ds];
               dd = 0;
            }

            d[dd++] = s[sd];
         }

         sd++;
      }

      long j = BigArrays.index(ds, dd);
      boolean modified = this.size != j;
      this.size = j;
      return modified;
   }

   @Override
   public boolean addAll(long index, LongCollection c) {
      if (c instanceof LongList) {
         return this.addAll(index, (LongList)c);
      } else if (c instanceof LongBigList) {
         return this.addAll(index, (LongBigList)c);
      } else {
         this.ensureIndex(index);
         int n = c.size();
         if (n == 0) {
            return false;
         } else {
            this.grow(this.size + n);
            BigArrays.copy(this.a, index, this.a, index + n, this.size - index);
            LongIterator i = c.iterator();
            this.size += n;

            assert this.size <= BigArrays.length(this.a);

            while (n-- != 0) {
               BigArrays.set(this.a, index++, i.nextLong());
            }

            return true;
         }
      }
   }

   @Override
   public boolean addAll(long index, LongBigList list) {
      this.ensureIndex(index);
      long n = list.size64();
      if (n == 0L) {
         return false;
      } else {
         this.grow(this.size + n);
         BigArrays.copy(this.a, index, this.a, index + n, this.size - index);
         list.getElements(0L, this.a, index, n);
         this.size += n;

         assert this.size <= BigArrays.length(this.a);

         return true;
      }
   }

   @Override
   public boolean addAll(long index, LongList list) {
      this.ensureIndex(index);
      int n = list.size();
      if (n == 0) {
         return false;
      } else {
         this.grow(this.size + n);
         BigArrays.copy(this.a, index, this.a, index + n, this.size - index);
         this.size += n;

         assert this.size <= BigArrays.length(this.a);

         int segment = BigArrays.segment(index);
         int displ = BigArrays.displacement(index);
         int pos = 0;

         while (n > 0) {
            int l = Math.min(this.a[segment].length - displ, n);
            list.getElements(pos, this.a[segment], displ, l);
            if ((displ += l) == 134217728) {
               displ = 0;
               segment++;
            }

            pos += l;
            n -= l;
         }

         return true;
      }
   }

   @Override
   public void clear() {
      this.size = 0L;

      assert this.size <= BigArrays.length(this.a);
   }

   @Override
   public long size64() {
      return this.size;
   }

   @Override
   public void size(long size) {
      if (size > BigArrays.length(this.a)) {
         this.a = BigArrays.forceCapacity(this.a, size, this.size);
      }

      if (size > this.size) {
         BigArrays.fill(this.a, this.size, size, 0L);
      }

      this.size = size;
   }

   @Override
   public boolean isEmpty() {
      return this.size == 0L;
   }

   public void trim() {
      this.trim(0L);
   }

   public void trim(long n) {
      long arrayLength = BigArrays.length(this.a);
      if (n < arrayLength && this.size != arrayLength) {
         this.a = BigArrays.trim(this.a, Math.max(n, this.size));

         assert this.size <= BigArrays.length(this.a);
      }
   }

   @Override
   public LongBigList subList(long from, long to) {
      if (from == 0L && to == this.size64()) {
         return this;
      } else {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new LongBigArrayBigList.SubList(from, to);
         }
      }
   }

   @Override
   public void getElements(long from, long[][] a, long offset, long length) {
      BigArrays.copy(this.a, from, a, offset, length);
   }

   @Override
   public void getElements(long from, long[] a, int offset, int length) {
      BigArrays.copyFromBig(this.a, from, a, offset, length);
   }

   @Override
   public void removeElements(long from, long to) {
      BigArrays.ensureFromTo(this.size, from, to);
      BigArrays.copy(this.a, to, this.a, from, this.size - to);
      this.size -= to - from;
   }

   @Override
   public void addElements(long index, long[][] a, long offset, long length) {
      this.ensureIndex(index);
      BigArrays.ensureOffsetLength(a, offset, length);
      this.grow(this.size + length);
      BigArrays.copy(this.a, index, this.a, index + length, this.size - index);
      BigArrays.copy(a, offset, this.a, index, length);
      this.size += length;
   }

   @Override
   public void setElements(long index, long[][] a, long offset, long length) {
      BigArrays.copy(a, offset, this.a, index, length);
   }

   @Override
   public void forEach(java.util.function.LongConsumer action) {
      for (long i = 0L; i < this.size; i++) {
         action.accept(BigArrays.get(this.a, i));
      }
   }

   @Override
   public LongBigListIterator listIterator(final long index) {
      this.ensureIndex(index);
      return new LongBigListIterator() {
         long pos = index;
         long last = -1L;

         @Override
         public boolean hasNext() {
            return this.pos < LongBigArrayBigList.this.size;
         }

         @Override
         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         @Override
         public long nextLong() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return BigArrays.get(LongBigArrayBigList.this.a, this.last = this.pos++);
            }
         }

         @Override
         public long previousLong() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return BigArrays.get(LongBigArrayBigList.this.a, this.last = --this.pos);
            }
         }

         @Override
         public long nextIndex() {
            return this.pos;
         }

         @Override
         public long previousIndex() {
            return this.pos - 1L;
         }

         @Override
         public void add(long k) {
            LongBigArrayBigList.this.add(this.pos++, k);
            this.last = -1L;
         }

         @Override
         public void set(long k) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               LongBigArrayBigList.this.set(this.last, k);
            }
         }

         @Override
         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               LongBigArrayBigList.this.removeLong(this.last);
               if (this.last < this.pos) {
                  this.pos--;
               }

               this.last = -1L;
            }
         }

         @Override
         public void forEachRemaining(java.util.function.LongConsumer action) {
            while (this.pos < LongBigArrayBigList.this.size) {
               action.accept(BigArrays.get(LongBigArrayBigList.this.a, this.last = this.pos++));
            }
         }

         @Override
         public long back(long n) {
            if (n < 0L) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               long remaining = LongBigArrayBigList.this.size - this.pos;
               if (n < remaining) {
                  this.pos -= n;
               } else {
                  n = remaining;
                  this.pos = 0L;
               }

               this.last = this.pos;
               return n;
            }
         }

         @Override
         public long skip(long n) {
            if (n < 0L) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               long remaining = LongBigArrayBigList.this.size - this.pos;
               if (n < remaining) {
                  this.pos += n;
               } else {
                  n = remaining;
                  this.pos = LongBigArrayBigList.this.size;
               }

               this.last = this.pos - 1L;
               return n;
            }
         }
      };
   }

   @Override
   public LongSpliterator spliterator() {
      return new LongBigArrayBigList.Spliterator();
   }

   public LongBigArrayBigList clone() {
      LongBigArrayBigList c;
      if (this.getClass() == LongBigArrayBigList.class) {
         c = new LongBigArrayBigList(this.size);
         c.size = this.size;
      } else {
         try {
            c = (LongBigArrayBigList)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError(var3);
         }

         c.a = LongBigArrays.newBigArray(this.size);
      }

      BigArrays.copy(this.a, 0L, c.a, 0L, this.size);
      return c;
   }

   public boolean equals(LongBigArrayBigList l) {
      if (l == this) {
         return true;
      } else {
         long s = this.size64();
         if (s != l.size64()) {
            return false;
         } else {
            long[][] a1 = this.a;
            long[][] a2 = l.a;
            if (a1 == a2) {
               return true;
            } else {
               while (s-- != 0L) {
                  if (BigArrays.get(a1, s) != BigArrays.get(a2, s)) {
                     return false;
                  }
               }

               return true;
            }
         }
      }
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (o == null) {
         return false;
      } else if (!(o instanceof BigList)) {
         return false;
      } else if (o instanceof LongBigArrayBigList) {
         return this.equals((LongBigArrayBigList)o);
      } else {
         return o instanceof LongBigArrayBigList.SubList ? ((LongBigArrayBigList.SubList)o).equals(this) : super.equals(o);
      }
   }

   public int compareTo(LongBigArrayBigList l) {
      long s1 = this.size64();
      long s2 = l.size64();
      long[][] a1 = this.a;
      long[][] a2 = l.a;
      if (a1 == a2 && s1 == s2) {
         return 0;
      } else {
         int i;
         for (i = 0; i < s1 && i < s2; i++) {
            long e1 = BigArrays.get(a1, (long)i);
            long e2 = BigArrays.get(a2, (long)i);
            int r;
            if ((r = Long.compare(e1, e2)) != 0) {
               return r;
            }
         }

         return i < s2 ? -1 : (i < s1 ? 1 : 0);
      }
   }

   @Override
   public int compareTo(BigList<? extends Long> l) {
      if (l instanceof LongBigArrayBigList) {
         return this.compareTo((LongBigArrayBigList)l);
      } else {
         return l instanceof LongBigArrayBigList.SubList ? -((LongBigArrayBigList.SubList)l).compareTo(this) : super.compareTo(l);
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();

      for (int i = 0; i < this.size; i++) {
         s.writeLong(BigArrays.get(this.a, (long)i));
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.a = LongBigArrays.newBigArray(this.size);

      for (int i = 0; i < this.size; i++) {
         BigArrays.set(this.a, (long)i, s.readLong());
      }
   }

   private final class Spliterator implements LongSpliterator {
      boolean hasSplit = false;
      long pos;
      long max;

      public Spliterator() {
         this(0L, LongBigArrayBigList.this.size, false);
      }

      private Spliterator(long pos, long max, boolean hasSplit) {
         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
         this.hasSplit = hasSplit;
      }

      private long getWorkingMax() {
         return this.hasSplit ? this.max : LongBigArrayBigList.this.size;
      }

      @Override
      public int characteristics() {
         return 16720;
      }

      @Override
      public long estimateSize() {
         return this.getWorkingMax() - this.pos;
      }

      @Override
      public boolean tryAdvance(java.util.function.LongConsumer action) {
         if (this.pos >= this.getWorkingMax()) {
            return false;
         } else {
            action.accept(BigArrays.get(LongBigArrayBigList.this.a, this.pos++));
            return true;
         }
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
         for (long max = this.getWorkingMax(); this.pos < max; this.pos++) {
            action.accept(BigArrays.get(LongBigArrayBigList.this.a, this.pos));
         }
      }

      @Override
      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            long max = this.getWorkingMax();
            if (this.pos >= max) {
               return 0L;
            } else {
               long remaining = max - this.pos;
               if (n < remaining) {
                  this.pos += n;
                  return n;
               } else {
                  this.pos = max;
                  return remaining;
               }
            }
         }
      }

      @Override
      public LongSpliterator trySplit() {
         long max = this.getWorkingMax();
         long retLen = max - this.pos >> 1;
         if (retLen <= 1L) {
            return null;
         } else {
            this.max = max;
            long myNewPos = this.pos + retLen;
            myNewPos = BigArrays.nearestSegmentStart(myNewPos, this.pos + 1L, max - 1L);
            long oldPos = this.pos;
            this.pos = myNewPos;
            this.hasSplit = true;
            return LongBigArrayBigList.this.new Spliterator(oldPos, myNewPos, true);
         }
      }
   }

   private class SubList extends AbstractLongBigList.LongRandomAccessSubList {
      private static final long serialVersionUID = -3185226345314976296L;

      protected SubList(long from, long to) {
         super(LongBigArrayBigList.this, from, to);
      }

      private long[][] getParentArray() {
         return LongBigArrayBigList.this.a;
      }

      @Override
      public long getLong(long i) {
         this.ensureRestrictedIndex(i);
         return BigArrays.get(LongBigArrayBigList.this.a, i + this.from);
      }

      @Override
      public LongBigListIterator listIterator(long index) {
         return new LongBigArrayBigList.SubList.SubListIterator(index);
      }

      @Override
      public LongSpliterator spliterator() {
         return new LongBigArrayBigList.SubList.SubListSpliterator();
      }

      boolean contentsEquals(long[][] otherA, long otherAFrom, long otherATo) {
         if (LongBigArrayBigList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
            return true;
         } else if (otherATo - otherAFrom != this.size64()) {
            return false;
         } else {
            long pos = this.to;
            long otherPos = otherATo;

            while (--pos >= this.from) {
               if (BigArrays.get(LongBigArrayBigList.this.a, pos) != BigArrays.get(otherA, --otherPos)) {
                  return false;
               }
            }

            return true;
         }
      }

      @Override
      public boolean equals(Object o) {
         if (o == this) {
            return true;
         } else if (o == null) {
            return false;
         } else if (!(o instanceof BigList)) {
            return false;
         } else if (o instanceof LongBigArrayBigList) {
            LongBigArrayBigList other = (LongBigArrayBigList)o;
            return this.contentsEquals(other.a, 0L, other.size64());
         } else if (o instanceof LongBigArrayBigList.SubList) {
            LongBigArrayBigList.SubList other = (LongBigArrayBigList.SubList)o;
            return this.contentsEquals(other.getParentArray(), other.from, other.to);
         } else {
            return super.equals(o);
         }
      }

      int contentsCompareTo(long[][] otherA, long otherAFrom, long otherATo) {
         if (LongBigArrayBigList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
            return 0;
         } else {
            long i = this.from;

            for (long j = otherAFrom; i < this.to && i < otherATo; j++) {
               long e1 = BigArrays.get(LongBigArrayBigList.this.a, i);
               long e2 = BigArrays.get(otherA, j);
               int r;
               if ((r = Long.compare(e1, e2)) != 0) {
                  return r;
               }

               i++;
            }

            return i < otherATo ? -1 : (i < this.to ? 1 : 0);
         }
      }

      @Override
      public int compareTo(BigList<? extends Long> l) {
         if (l instanceof LongBigArrayBigList) {
            LongBigArrayBigList other = (LongBigArrayBigList)l;
            return this.contentsCompareTo(other.a, 0L, other.size64());
         } else if (l instanceof LongBigArrayBigList.SubList) {
            LongBigArrayBigList.SubList other = (LongBigArrayBigList.SubList)l;
            return this.contentsCompareTo(other.getParentArray(), other.from, other.to);
         } else {
            return super.compareTo(l);
         }
      }

      private final class SubListIterator extends LongBigListIterators.AbstractIndexBasedBigListIterator {
         SubListIterator(long index) {
            super(0L, index);
         }

         @Override
         protected final long get(long i) {
            return BigArrays.get(LongBigArrayBigList.this.a, SubList.this.from + i);
         }

         @Override
         protected final void add(long i, long k) {
            SubList.this.add(i, k);
         }

         @Override
         protected final void set(long i, long k) {
            SubList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            SubList.this.removeLong(i);
         }

         @Override
         protected final long getMaxPos() {
            return SubList.this.to - SubList.this.from;
         }

         @Override
         public long nextLong() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return BigArrays.get(LongBigArrayBigList.this.a, SubList.this.from + (this.lastReturned = this.pos++));
            }
         }

         @Override
         public long previousLong() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return BigArrays.get(LongBigArrayBigList.this.a, SubList.this.from + (this.lastReturned = --this.pos));
            }
         }

         @Override
         public void forEachRemaining(java.util.function.LongConsumer action) {
            long max = SubList.this.to - SubList.this.from;

            while (this.pos < max) {
               action.accept(BigArrays.get(LongBigArrayBigList.this.a, SubList.this.from + (this.lastReturned = this.pos++)));
            }
         }
      }

      private final class SubListSpliterator extends LongBigSpliterators.LateBindingSizeIndexBasedSpliterator {
         SubListSpliterator() {
            super(SubList.this.from);
         }

         private SubListSpliterator(long pos, long maxPos) {
            super(pos, maxPos);
         }

         @Override
         protected final long getMaxPosFromBackingStore() {
            return SubList.this.to;
         }

         @Override
         protected final long get(long i) {
            return BigArrays.get(LongBigArrayBigList.this.a, i);
         }

         protected final LongBigArrayBigList.SubList.SubListSpliterator makeForSplit(long pos, long maxPos) {
            return SubList.this.new SubListSpliterator(pos, maxPos);
         }

         @Override
         protected final long computeSplitPoint() {
            long defaultSplit = super.computeSplitPoint();
            return BigArrays.nearestSegmentStart(defaultSplit, this.pos + 1L, this.getMaxPos() - 1L);
         }

         @Override
         public boolean tryAdvance(java.util.function.LongConsumer action) {
            if (this.pos >= this.getMaxPos()) {
               return false;
            } else {
               action.accept(BigArrays.get(LongBigArrayBigList.this.a, this.pos++));
               return true;
            }
         }

         @Override
         public void forEachRemaining(java.util.function.LongConsumer action) {
            long max = this.getMaxPos();

            while (this.pos < max) {
               action.accept(BigArrays.get(LongBigArrayBigList.this.a, this.pos++));
            }
         }
      }
   }
}
