package it.unimi.dsi.fastutil.chars;

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

public class CharBigArrayBigList extends AbstractCharBigList implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353130L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected transient char[][] a;
   protected long size;

   protected CharBigArrayBigList(char[][] a, boolean dummy) {
      this.a = a;
   }

   public CharBigArrayBigList(long capacity) {
      if (capacity < 0L) {
         throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
      } else {
         if (capacity == 0L) {
            this.a = CharBigArrays.EMPTY_BIG_ARRAY;
         } else {
            this.a = CharBigArrays.newBigArray(capacity);
         }
      }
   }

   public CharBigArrayBigList() {
      this.a = CharBigArrays.DEFAULT_EMPTY_BIG_ARRAY;
   }

   public CharBigArrayBigList(CharCollection c) {
      this(Size64.sizeOf(c));
      if (c instanceof CharBigList) {
         ((CharBigList)c).getElements(0L, this.a, 0L, this.size = Size64.sizeOf(c));
      } else {
         CharIterator i = c.iterator();

         while (i.hasNext()) {
            this.add(i.nextChar());
         }
      }
   }

   public CharBigArrayBigList(CharBigList l) {
      this(l.size64());
      l.getElements(0L, this.a, 0L, this.size = l.size64());
   }

   public CharBigArrayBigList(char[][] a) {
      this(a, 0L, BigArrays.length(a));
   }

   public CharBigArrayBigList(char[][] a, long offset, long length) {
      this(length);
      BigArrays.copy(a, offset, this.a, 0L, length);
      this.size = length;
   }

   public CharBigArrayBigList(Iterator<? extends Character> i) {
      this();

      while (i.hasNext()) {
         this.add(i.next());
      }
   }

   public CharBigArrayBigList(CharIterator i) {
      this();

      while (i.hasNext()) {
         this.add(i.nextChar());
      }
   }

   public char[][] elements() {
      return this.a;
   }

   public static CharBigArrayBigList wrap(char[][] a, long length) {
      if (length > BigArrays.length(a)) {
         throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + BigArrays.length(a) + ")");
      } else {
         CharBigArrayBigList l = new CharBigArrayBigList(a, false);
         l.size = length;
         return l;
      }
   }

   public static CharBigArrayBigList wrap(char[][] a) {
      return wrap(a, BigArrays.length(a));
   }

   public static CharBigArrayBigList of() {
      return new CharBigArrayBigList();
   }

   public static CharBigArrayBigList of(char... init) {
      return wrap(BigArrays.wrap(init));
   }

   public void ensureCapacity(long capacity) {
      if (capacity > BigArrays.length(this.a) && this.a != CharBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
         this.a = BigArrays.forceCapacity(this.a, capacity, this.size);

         assert this.size <= BigArrays.length(this.a);
      }
   }

   private void grow(long capacity) {
      long oldLength = BigArrays.length(this.a);
      if (capacity > oldLength) {
         if (this.a != CharBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
            capacity = Math.max(oldLength + (oldLength >> 1), capacity);
         } else if (capacity < 10L) {
            capacity = 10L;
         }

         this.a = BigArrays.forceCapacity(this.a, capacity, this.size);

         assert this.size <= BigArrays.length(this.a);
      }
   }

   @Override
   public void add(long index, char k) {
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
   public boolean add(char k) {
      this.grow(this.size + 1L);
      BigArrays.set(this.a, this.size++, k);

      assert this.size <= BigArrays.length(this.a);

      return true;
   }

   @Override
   public char getChar(long index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return BigArrays.get(this.a, index);
      }
   }

   @Override
   public long indexOf(char k) {
      for (long i = 0L; i < this.size; i++) {
         if (k == BigArrays.get(this.a, i)) {
            return i;
         }
      }

      return -1L;
   }

   @Override
   public long lastIndexOf(char k) {
      long i = this.size;

      while (i-- != 0L) {
         if (k == BigArrays.get(this.a, i)) {
            return i;
         }
      }

      return -1L;
   }

   @Override
   public char removeChar(long index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         char old = BigArrays.get(this.a, index);
         this.size--;
         if (index != this.size) {
            BigArrays.copy(this.a, index + 1L, this.a, index, this.size - index);
         }

         assert this.size <= BigArrays.length(this.a);

         return old;
      }
   }

   @Override
   public boolean rem(char k) {
      long index = this.indexOf(k);
      if (index == -1L) {
         return false;
      } else {
         this.removeChar(index);

         assert this.size <= BigArrays.length(this.a);

         return true;
      }
   }

   @Override
   public char set(long index, char k) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         char old = BigArrays.get(this.a, index);
         BigArrays.set(this.a, index, k);
         return old;
      }
   }

   @Override
   public boolean removeAll(CharCollection c) {
      char[] s = null;
      char[] d = null;
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
      char[] s = null;
      char[] d = null;
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
   public boolean addAll(long index, CharCollection c) {
      if (c instanceof CharList) {
         return this.addAll(index, (CharList)c);
      } else if (c instanceof CharBigList) {
         return this.addAll(index, (CharBigList)c);
      } else {
         this.ensureIndex(index);
         int n = c.size();
         if (n == 0) {
            return false;
         } else {
            this.grow(this.size + n);
            BigArrays.copy(this.a, index, this.a, index + n, this.size - index);
            CharIterator i = c.iterator();
            this.size += n;

            assert this.size <= BigArrays.length(this.a);

            while (n-- != 0) {
               BigArrays.set(this.a, index++, i.nextChar());
            }

            return true;
         }
      }
   }

   @Override
   public boolean addAll(long index, CharBigList list) {
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
   public boolean addAll(long index, CharList list) {
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
         BigArrays.fill(this.a, this.size, size, '\u0000');
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
   public CharBigList subList(long from, long to) {
      if (from == 0L && to == this.size64()) {
         return this;
      } else {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new CharBigArrayBigList.SubList(from, to);
         }
      }
   }

   @Override
   public void getElements(long from, char[][] a, long offset, long length) {
      BigArrays.copy(this.a, from, a, offset, length);
   }

   @Override
   public void getElements(long from, char[] a, int offset, int length) {
      BigArrays.copyFromBig(this.a, from, a, offset, length);
   }

   @Override
   public void removeElements(long from, long to) {
      BigArrays.ensureFromTo(this.size, from, to);
      BigArrays.copy(this.a, to, this.a, from, this.size - to);
      this.size -= to - from;
   }

   @Override
   public void addElements(long index, char[][] a, long offset, long length) {
      this.ensureIndex(index);
      BigArrays.ensureOffsetLength(a, offset, length);
      this.grow(this.size + length);
      BigArrays.copy(this.a, index, this.a, index + length, this.size - index);
      BigArrays.copy(a, offset, this.a, index, length);
      this.size += length;
   }

   @Override
   public void setElements(long index, char[][] a, long offset, long length) {
      BigArrays.copy(a, offset, this.a, index, length);
   }

   @Override
   public void forEach(CharConsumer action) {
      for (long i = 0L; i < this.size; i++) {
         action.accept(BigArrays.get(this.a, i));
      }
   }

   @Override
   public CharBigListIterator listIterator(final long index) {
      this.ensureIndex(index);
      return new CharBigListIterator() {
         long pos = index;
         long last = -1L;

         @Override
         public boolean hasNext() {
            return this.pos < CharBigArrayBigList.this.size;
         }

         @Override
         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         @Override
         public char nextChar() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return BigArrays.get(CharBigArrayBigList.this.a, this.last = this.pos++);
            }
         }

         @Override
         public char previousChar() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return BigArrays.get(CharBigArrayBigList.this.a, this.last = --this.pos);
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
         public void add(char k) {
            CharBigArrayBigList.this.add(this.pos++, k);
            this.last = -1L;
         }

         @Override
         public void set(char k) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               CharBigArrayBigList.this.set(this.last, k);
            }
         }

         @Override
         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               CharBigArrayBigList.this.removeChar(this.last);
               if (this.last < this.pos) {
                  this.pos--;
               }

               this.last = -1L;
            }
         }

         @Override
         public void forEachRemaining(CharConsumer action) {
            while (this.pos < CharBigArrayBigList.this.size) {
               action.accept(BigArrays.get(CharBigArrayBigList.this.a, this.last = this.pos++));
            }
         }

         @Override
         public long back(long n) {
            if (n < 0L) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               long remaining = CharBigArrayBigList.this.size - this.pos;
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
               long remaining = CharBigArrayBigList.this.size - this.pos;
               if (n < remaining) {
                  this.pos += n;
               } else {
                  n = remaining;
                  this.pos = CharBigArrayBigList.this.size;
               }

               this.last = this.pos - 1L;
               return n;
            }
         }
      };
   }

   @Override
   public CharSpliterator spliterator() {
      return new CharBigArrayBigList.Spliterator();
   }

   public CharBigArrayBigList clone() {
      CharBigArrayBigList c;
      if (this.getClass() == CharBigArrayBigList.class) {
         c = new CharBigArrayBigList(this.size);
         c.size = this.size;
      } else {
         try {
            c = (CharBigArrayBigList)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError(var3);
         }

         c.a = CharBigArrays.newBigArray(this.size);
      }

      BigArrays.copy(this.a, 0L, c.a, 0L, this.size);
      return c;
   }

   public boolean equals(CharBigArrayBigList l) {
      if (l == this) {
         return true;
      } else {
         long s = this.size64();
         if (s != l.size64()) {
            return false;
         } else {
            char[][] a1 = this.a;
            char[][] a2 = l.a;
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
      } else if (o instanceof CharBigArrayBigList) {
         return this.equals((CharBigArrayBigList)o);
      } else {
         return o instanceof CharBigArrayBigList.SubList ? ((CharBigArrayBigList.SubList)o).equals(this) : super.equals(o);
      }
   }

   public int compareTo(CharBigArrayBigList l) {
      long s1 = this.size64();
      long s2 = l.size64();
      char[][] a1 = this.a;
      char[][] a2 = l.a;
      if (a1 == a2 && s1 == s2) {
         return 0;
      } else {
         int i;
         for (i = 0; i < s1 && i < s2; i++) {
            char e1 = BigArrays.get(a1, (long)i);
            char e2 = BigArrays.get(a2, (long)i);
            int r;
            if ((r = Character.compare(e1, e2)) != 0) {
               return r;
            }
         }

         return i < s2 ? -1 : (i < s1 ? 1 : 0);
      }
   }

   @Override
   public int compareTo(BigList<? extends Character> l) {
      if (l instanceof CharBigArrayBigList) {
         return this.compareTo((CharBigArrayBigList)l);
      } else {
         return l instanceof CharBigArrayBigList.SubList ? -((CharBigArrayBigList.SubList)l).compareTo(this) : super.compareTo(l);
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();

      for (int i = 0; i < this.size; i++) {
         s.writeChar(BigArrays.get(this.a, (long)i));
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.a = CharBigArrays.newBigArray(this.size);

      for (int i = 0; i < this.size; i++) {
         BigArrays.set(this.a, (long)i, s.readChar());
      }
   }

   private final class Spliterator implements CharSpliterator {
      boolean hasSplit = false;
      long pos;
      long max;

      public Spliterator() {
         this(0L, CharBigArrayBigList.this.size, false);
      }

      private Spliterator(long pos, long max, boolean hasSplit) {
         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
         this.hasSplit = hasSplit;
      }

      private long getWorkingMax() {
         return this.hasSplit ? this.max : CharBigArrayBigList.this.size;
      }

      @Override
      public int characteristics() {
         return 16720;
      }

      @Override
      public long estimateSize() {
         return this.getWorkingMax() - this.pos;
      }

      public boolean tryAdvance(CharConsumer action) {
         if (this.pos >= this.getWorkingMax()) {
            return false;
         } else {
            action.accept(BigArrays.get(CharBigArrayBigList.this.a, this.pos++));
            return true;
         }
      }

      public void forEachRemaining(CharConsumer action) {
         for (long max = this.getWorkingMax(); this.pos < max; this.pos++) {
            action.accept(BigArrays.get(CharBigArrayBigList.this.a, this.pos));
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
      public CharSpliterator trySplit() {
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
            return CharBigArrayBigList.this.new Spliterator(oldPos, myNewPos, true);
         }
      }
   }

   private class SubList extends AbstractCharBigList.CharRandomAccessSubList {
      private static final long serialVersionUID = -3185226345314976296L;

      protected SubList(long from, long to) {
         super(CharBigArrayBigList.this, from, to);
      }

      private char[][] getParentArray() {
         return CharBigArrayBigList.this.a;
      }

      @Override
      public char getChar(long i) {
         this.ensureRestrictedIndex(i);
         return BigArrays.get(CharBigArrayBigList.this.a, i + this.from);
      }

      @Override
      public CharBigListIterator listIterator(long index) {
         return new CharBigArrayBigList.SubList.SubListIterator(index);
      }

      @Override
      public CharSpliterator spliterator() {
         return new CharBigArrayBigList.SubList.SubListSpliterator();
      }

      boolean contentsEquals(char[][] otherA, long otherAFrom, long otherATo) {
         if (CharBigArrayBigList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
            return true;
         } else if (otherATo - otherAFrom != this.size64()) {
            return false;
         } else {
            long pos = this.to;
            long otherPos = otherATo;

            while (--pos >= this.from) {
               if (BigArrays.get(CharBigArrayBigList.this.a, pos) != BigArrays.get(otherA, --otherPos)) {
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
         } else if (o instanceof CharBigArrayBigList) {
            CharBigArrayBigList other = (CharBigArrayBigList)o;
            return this.contentsEquals(other.a, 0L, other.size64());
         } else if (o instanceof CharBigArrayBigList.SubList) {
            CharBigArrayBigList.SubList other = (CharBigArrayBigList.SubList)o;
            return this.contentsEquals(other.getParentArray(), other.from, other.to);
         } else {
            return super.equals(o);
         }
      }

      int contentsCompareTo(char[][] otherA, long otherAFrom, long otherATo) {
         if (CharBigArrayBigList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
            return 0;
         } else {
            long i = this.from;

            for (long j = otherAFrom; i < this.to && i < otherATo; j++) {
               char e1 = BigArrays.get(CharBigArrayBigList.this.a, i);
               char e2 = BigArrays.get(otherA, j);
               int r;
               if ((r = Character.compare(e1, e2)) != 0) {
                  return r;
               }

               i++;
            }

            return i < otherATo ? -1 : (i < this.to ? 1 : 0);
         }
      }

      @Override
      public int compareTo(BigList<? extends Character> l) {
         if (l instanceof CharBigArrayBigList) {
            CharBigArrayBigList other = (CharBigArrayBigList)l;
            return this.contentsCompareTo(other.a, 0L, other.size64());
         } else if (l instanceof CharBigArrayBigList.SubList) {
            CharBigArrayBigList.SubList other = (CharBigArrayBigList.SubList)l;
            return this.contentsCompareTo(other.getParentArray(), other.from, other.to);
         } else {
            return super.compareTo(l);
         }
      }

      private final class SubListIterator extends CharBigListIterators.AbstractIndexBasedBigListIterator {
         SubListIterator(long index) {
            super(0L, index);
         }

         @Override
         protected final char get(long i) {
            return BigArrays.get(CharBigArrayBigList.this.a, SubList.this.from + i);
         }

         @Override
         protected final void add(long i, char k) {
            SubList.this.add(i, k);
         }

         @Override
         protected final void set(long i, char k) {
            SubList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            SubList.this.removeChar(i);
         }

         @Override
         protected final long getMaxPos() {
            return SubList.this.to - SubList.this.from;
         }

         @Override
         public char nextChar() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return BigArrays.get(CharBigArrayBigList.this.a, SubList.this.from + (this.lastReturned = this.pos++));
            }
         }

         @Override
         public char previousChar() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return BigArrays.get(CharBigArrayBigList.this.a, SubList.this.from + (this.lastReturned = --this.pos));
            }
         }

         @Override
         public void forEachRemaining(CharConsumer action) {
            long max = SubList.this.to - SubList.this.from;

            while (this.pos < max) {
               action.accept(BigArrays.get(CharBigArrayBigList.this.a, SubList.this.from + (this.lastReturned = this.pos++)));
            }
         }
      }

      private final class SubListSpliterator extends CharBigSpliterators.LateBindingSizeIndexBasedSpliterator {
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
         protected final char get(long i) {
            return BigArrays.get(CharBigArrayBigList.this.a, i);
         }

         protected final CharBigArrayBigList.SubList.SubListSpliterator makeForSplit(long pos, long maxPos) {
            return SubList.this.new SubListSpliterator(pos, maxPos);
         }

         @Override
         protected final long computeSplitPoint() {
            long defaultSplit = super.computeSplitPoint();
            return BigArrays.nearestSegmentStart(defaultSplit, this.pos + 1L, this.getMaxPos() - 1L);
         }

         @Override
         public boolean tryAdvance(CharConsumer action) {
            if (this.pos >= this.getMaxPos()) {
               return false;
            } else {
               action.accept(BigArrays.get(CharBigArrayBigList.this.a, this.pos++));
               return true;
            }
         }

         @Override
         public void forEachRemaining(CharConsumer action) {
            long max = this.getMaxPos();

            while (this.pos < max) {
               action.accept(BigArrays.get(CharBigArrayBigList.this.a, this.pos++));
            }
         }
      }
   }
}
