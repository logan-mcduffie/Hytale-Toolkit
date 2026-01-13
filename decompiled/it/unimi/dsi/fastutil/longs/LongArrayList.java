package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.stream.LongStream;

public class LongArrayList extends AbstractLongList implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353130L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected transient long[] a;
   protected int size;

   private static final long[] copyArraySafe(long[] a, int length) {
      return length == 0 ? LongArrays.EMPTY_ARRAY : Arrays.copyOf(a, length);
   }

   private static final long[] copyArrayFromSafe(LongArrayList l) {
      return copyArraySafe(l.a, l.size);
   }

   protected LongArrayList(long[] a, boolean wrapped) {
      this.a = a;
   }

   private void initArrayFromCapacity(int capacity) {
      if (capacity < 0) {
         throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
      } else {
         if (capacity == 0) {
            this.a = LongArrays.EMPTY_ARRAY;
         } else {
            this.a = new long[capacity];
         }
      }
   }

   public LongArrayList(int capacity) {
      this.initArrayFromCapacity(capacity);
   }

   public LongArrayList() {
      this.a = LongArrays.DEFAULT_EMPTY_ARRAY;
   }

   public LongArrayList(Collection<? extends Long> c) {
      if (c instanceof LongArrayList) {
         this.a = copyArrayFromSafe((LongArrayList)c);
         this.size = this.a.length;
      } else {
         this.initArrayFromCapacity(c.size());
         if (c instanceof LongList) {
            ((LongList)c).getElements(0, this.a, 0, this.size = c.size());
         } else {
            this.size = LongIterators.unwrap(LongIterators.asLongIterator(c.iterator()), this.a);
         }
      }
   }

   public LongArrayList(LongCollection c) {
      if (c instanceof LongArrayList) {
         this.a = copyArrayFromSafe((LongArrayList)c);
         this.size = this.a.length;
      } else {
         this.initArrayFromCapacity(c.size());
         if (c instanceof LongList) {
            ((LongList)c).getElements(0, this.a, 0, this.size = c.size());
         } else {
            this.size = LongIterators.unwrap(c.iterator(), this.a);
         }
      }
   }

   public LongArrayList(LongList l) {
      if (l instanceof LongArrayList) {
         this.a = copyArrayFromSafe((LongArrayList)l);
         this.size = this.a.length;
      } else {
         this.initArrayFromCapacity(l.size());
         l.getElements(0, this.a, 0, this.size = l.size());
      }
   }

   public LongArrayList(long[] a) {
      this(a, 0, a.length);
   }

   public LongArrayList(long[] a, int offset, int length) {
      this(length);
      System.arraycopy(a, offset, this.a, 0, length);
      this.size = length;
   }

   public LongArrayList(Iterator<? extends Long> i) {
      this();

      while (i.hasNext()) {
         this.add(i.next());
      }
   }

   public LongArrayList(LongIterator i) {
      this();

      while (i.hasNext()) {
         this.add(i.nextLong());
      }
   }

   public long[] elements() {
      return this.a;
   }

   public static LongArrayList wrap(long[] a, int length) {
      if (length > a.length) {
         throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a.length + ")");
      } else {
         LongArrayList l = new LongArrayList(a, true);
         l.size = length;
         return l;
      }
   }

   public static LongArrayList wrap(long[] a) {
      return wrap(a, a.length);
   }

   public static LongArrayList of() {
      return new LongArrayList();
   }

   public static LongArrayList of(long... init) {
      return wrap(init);
   }

   public static LongArrayList toList(LongStream stream) {
      return stream.collect(LongArrayList::new, LongArrayList::add, LongList::addAll);
   }

   public static LongArrayList toListWithExpectedSize(LongStream stream, int expectedSize) {
      return expectedSize <= 10
         ? toList(stream)
         : stream.collect(
            new LongCollections.SizeDecreasingSupplier<>(expectedSize, size -> size <= 10 ? new LongArrayList() : new LongArrayList(size)),
            LongArrayList::add,
            LongList::addAll
         );
   }

   public void ensureCapacity(int capacity) {
      if (capacity > this.a.length && (this.a != LongArrays.DEFAULT_EMPTY_ARRAY || capacity > 10)) {
         this.a = LongArrays.ensureCapacity(this.a, capacity, this.size);

         assert this.size <= this.a.length;
      }
   }

   private void grow(int capacity) {
      if (capacity > this.a.length) {
         if (this.a != LongArrays.DEFAULT_EMPTY_ARRAY) {
            capacity = (int)Math.max(Math.min((long)this.a.length + (this.a.length >> 1), 2147483639L), (long)capacity);
         } else if (capacity < 10) {
            capacity = 10;
         }

         this.a = LongArrays.forceCapacity(this.a, capacity, this.size);

         assert this.size <= this.a.length;
      }
   }

   @Override
   public void add(int index, long k) {
      this.ensureIndex(index);
      this.grow(this.size + 1);
      if (index != this.size) {
         System.arraycopy(this.a, index, this.a, index + 1, this.size - index);
      }

      this.a[index] = k;
      this.size++;

      assert this.size <= this.a.length;
   }

   @Override
   public boolean add(long k) {
      this.grow(this.size + 1);
      this.a[this.size++] = k;

      assert this.size <= this.a.length;

      return true;
   }

   @Override
   public long getLong(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return this.a[index];
      }
   }

   @Override
   public int indexOf(long k) {
      for (int i = 0; i < this.size; i++) {
         if (k == this.a[i]) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public int lastIndexOf(long k) {
      int i = this.size;

      while (i-- != 0) {
         if (k == this.a[i]) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public long removeLong(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         long old = this.a[index];
         this.size--;
         if (index != this.size) {
            System.arraycopy(this.a, index + 1, this.a, index, this.size - index);
         }

         assert this.size <= this.a.length;

         return old;
      }
   }

   @Override
   public boolean rem(long k) {
      int index = this.indexOf(k);
      if (index == -1) {
         return false;
      } else {
         this.removeLong(index);

         assert this.size <= this.a.length;

         return true;
      }
   }

   @Override
   public long set(int index, long k) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         long old = this.a[index];
         this.a[index] = k;
         return old;
      }
   }

   @Override
   public void clear() {
      this.size = 0;

      assert this.size <= this.a.length;
   }

   @Override
   public int size() {
      return this.size;
   }

   @Override
   public void size(int size) {
      if (size > this.a.length) {
         this.a = LongArrays.forceCapacity(this.a, size, this.size);
      }

      if (size > this.size) {
         Arrays.fill(this.a, this.size, size, 0L);
      }

      this.size = size;
   }

   @Override
   public boolean isEmpty() {
      return this.size == 0;
   }

   public void trim() {
      this.trim(0);
   }

   public void trim(int n) {
      if (n < this.a.length && this.size != this.a.length) {
         long[] t = new long[Math.max(n, this.size)];
         System.arraycopy(this.a, 0, t, 0, this.size);
         this.a = t;

         assert this.size <= this.a.length;
      }
   }

   @Override
   public LongList subList(int from, int to) {
      if (from == 0 && to == this.size()) {
         return this;
      } else {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new LongArrayList.SubList(from, to);
         }
      }
   }

   @Override
   public void getElements(int from, long[] a, int offset, int length) {
      LongArrays.ensureOffsetLength(a, offset, length);
      System.arraycopy(this.a, from, a, offset, length);
   }

   @Override
   public void removeElements(int from, int to) {
      it.unimi.dsi.fastutil.Arrays.ensureFromTo(this.size, from, to);
      System.arraycopy(this.a, to, this.a, from, this.size - to);
      this.size -= to - from;
   }

   @Override
   public void addElements(int index, long[] a, int offset, int length) {
      this.ensureIndex(index);
      LongArrays.ensureOffsetLength(a, offset, length);
      this.grow(this.size + length);
      System.arraycopy(this.a, index, this.a, index + length, this.size - index);
      System.arraycopy(a, offset, this.a, index, length);
      this.size += length;
   }

   @Override
   public void setElements(int index, long[] a, int offset, int length) {
      this.ensureIndex(index);
      LongArrays.ensureOffsetLength(a, offset, length);
      if (index + length > this.size) {
         throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size + ")");
      } else {
         System.arraycopy(a, offset, this.a, index, length);
      }
   }

   @Override
   public void forEach(java.util.function.LongConsumer action) {
      for (int i = 0; i < this.size; i++) {
         action.accept(this.a[i]);
      }
   }

   @Override
   public boolean addAll(int index, LongCollection c) {
      if (c instanceof LongList) {
         return this.addAll(index, (LongList)c);
      } else {
         this.ensureIndex(index);
         int n = c.size();
         if (n == 0) {
            return false;
         } else {
            this.grow(this.size + n);
            System.arraycopy(this.a, index, this.a, index + n, this.size - index);
            LongIterator i = c.iterator();
            this.size += n;

            while (n-- != 0) {
               this.a[index++] = i.nextLong();
            }

            assert this.size <= this.a.length;

            return true;
         }
      }
   }

   @Override
   public boolean addAll(int index, LongList l) {
      this.ensureIndex(index);
      int n = l.size();
      if (n == 0) {
         return false;
      } else {
         this.grow(this.size + n);
         System.arraycopy(this.a, index, this.a, index + n, this.size - index);
         l.getElements(0, this.a, index, n);
         this.size += n;

         assert this.size <= this.a.length;

         return true;
      }
   }

   @Override
   public boolean removeAll(LongCollection c) {
      long[] a = this.a;
      int j = 0;

      for (int i = 0; i < this.size; i++) {
         if (!c.contains(a[i])) {
            a[j++] = a[i];
         }
      }

      boolean modified = this.size != j;
      this.size = j;
      return modified;
   }

   @Override
   public long[] toArray(long[] a) {
      if (a == null || a.length < this.size) {
         a = Arrays.copyOf(a, this.size);
      }

      System.arraycopy(this.a, 0, a, 0, this.size);
      return a;
   }

   @Override
   public LongListIterator listIterator(final int index) {
      this.ensureIndex(index);
      return new LongListIterator() {
         int pos = index;
         int last = -1;

         @Override
         public boolean hasNext() {
            return this.pos < LongArrayList.this.size;
         }

         @Override
         public boolean hasPrevious() {
            return this.pos > 0;
         }

         @Override
         public long nextLong() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return LongArrayList.this.a[this.last = this.pos++];
            }
         }

         @Override
         public long previousLong() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return LongArrayList.this.a[this.last = --this.pos];
            }
         }

         @Override
         public int nextIndex() {
            return this.pos;
         }

         @Override
         public int previousIndex() {
            return this.pos - 1;
         }

         @Override
         public void add(long k) {
            LongArrayList.this.add(this.pos++, k);
            this.last = -1;
         }

         @Override
         public void set(long k) {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               LongArrayList.this.set(this.last, k);
            }
         }

         @Override
         public void remove() {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               LongArrayList.this.removeLong(this.last);
               if (this.last < this.pos) {
                  this.pos--;
               }

               this.last = -1;
            }
         }

         @Override
         public void forEachRemaining(java.util.function.LongConsumer action) {
            while (this.pos < LongArrayList.this.size) {
               action.accept(LongArrayList.this.a[this.last = this.pos++]);
            }
         }

         @Override
         public int back(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int remaining = LongArrayList.this.size - this.pos;
               if (n < remaining) {
                  this.pos -= n;
               } else {
                  n = remaining;
                  this.pos = 0;
               }

               this.last = this.pos;
               return n;
            }
         }

         @Override
         public int skip(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int remaining = LongArrayList.this.size - this.pos;
               if (n < remaining) {
                  this.pos += n;
               } else {
                  n = remaining;
                  this.pos = LongArrayList.this.size;
               }

               this.last = this.pos - 1;
               return n;
            }
         }
      };
   }

   @Override
   public LongSpliterator spliterator() {
      return new LongArrayList.Spliterator();
   }

   @Override
   public void sort(LongComparator comp) {
      if (comp == null) {
         LongArrays.stableSort(this.a, 0, this.size);
      } else {
         LongArrays.stableSort(this.a, 0, this.size, comp);
      }
   }

   @Override
   public void unstableSort(LongComparator comp) {
      if (comp == null) {
         LongArrays.unstableSort(this.a, 0, this.size);
      } else {
         LongArrays.unstableSort(this.a, 0, this.size, comp);
      }
   }

   public LongArrayList clone() {
      LongArrayList cloned = null;
      if (this.getClass() == LongArrayList.class) {
         cloned = new LongArrayList(copyArraySafe(this.a, this.size), false);
         cloned.size = this.size;
      } else {
         try {
            cloned = (LongArrayList)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError(var3);
         }

         cloned.a = copyArraySafe(this.a, this.size);
      }

      return cloned;
   }

   public boolean equals(LongArrayList l) {
      if (l == this) {
         return true;
      } else {
         int s = this.size();
         if (s != l.size()) {
            return false;
         } else {
            long[] a1 = this.a;
            long[] a2 = l.a;
            if (a1 == a2 && s == l.size()) {
               return true;
            } else {
               while (s-- != 0) {
                  if (a1[s] != a2[s]) {
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
      } else if (!(o instanceof List)) {
         return false;
      } else if (o instanceof LongArrayList) {
         return this.equals((LongArrayList)o);
      } else {
         return o instanceof LongArrayList.SubList ? ((LongArrayList.SubList)o).equals(this) : super.equals(o);
      }
   }

   public int compareTo(LongArrayList l) {
      int s1 = this.size();
      int s2 = l.size();
      long[] a1 = this.a;
      long[] a2 = l.a;
      if (a1 == a2 && s1 == s2) {
         return 0;
      } else {
         int i;
         for (i = 0; i < s1 && i < s2; i++) {
            long e1 = a1[i];
            long e2 = a2[i];
            int r;
            if ((r = Long.compare(e1, e2)) != 0) {
               return r;
            }
         }

         return i < s2 ? -1 : (i < s1 ? 1 : 0);
      }
   }

   @Override
   public int compareTo(List<? extends Long> l) {
      if (l instanceof LongArrayList) {
         return this.compareTo((LongArrayList)l);
      } else {
         return l instanceof LongArrayList.SubList ? -((LongArrayList.SubList)l).compareTo(this) : super.compareTo(l);
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();

      for (int i = 0; i < this.size; i++) {
         s.writeLong(this.a[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.a = new long[this.size];

      for (int i = 0; i < this.size; i++) {
         this.a[i] = s.readLong();
      }
   }

   private final class Spliterator implements LongSpliterator {
      boolean hasSplit = false;
      int pos;
      int max;

      public Spliterator() {
         this(0, LongArrayList.this.size, false);
      }

      private Spliterator(int pos, int max, boolean hasSplit) {
         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
         this.hasSplit = hasSplit;
      }

      private int getWorkingMax() {
         return this.hasSplit ? this.max : LongArrayList.this.size;
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
            action.accept(LongArrayList.this.a[this.pos++]);
            return true;
         }
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
         for (int max = this.getWorkingMax(); this.pos < max; this.pos++) {
            action.accept(LongArrayList.this.a[this.pos]);
         }
      }

      @Override
      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            int max = this.getWorkingMax();
            if (this.pos >= max) {
               return 0L;
            } else {
               int remaining = max - this.pos;
               if (n < remaining) {
                  this.pos = SafeMath.safeLongToInt(this.pos + n);
                  return n;
               } else {
                  n = remaining;
                  this.pos = max;
                  return n;
               }
            }
         }
      }

      @Override
      public LongSpliterator trySplit() {
         int max = this.getWorkingMax();
         int retLen = max - this.pos >> 1;
         if (retLen <= 1) {
            return null;
         } else {
            this.max = max;
            int myNewPos = this.pos + retLen;
            int oldPos = this.pos;
            this.pos = myNewPos;
            this.hasSplit = true;
            return LongArrayList.this.new Spliterator(oldPos, myNewPos, true);
         }
      }
   }

   private class SubList extends AbstractLongList.LongRandomAccessSubList {
      private static final long serialVersionUID = -3185226345314976296L;

      protected SubList(int from, int to) {
         super(LongArrayList.this, from, to);
      }

      private long[] getParentArray() {
         return LongArrayList.this.a;
      }

      @Override
      public long getLong(int i) {
         this.ensureRestrictedIndex(i);
         return LongArrayList.this.a[i + this.from];
      }

      @Override
      public LongListIterator listIterator(int index) {
         return new LongArrayList.SubList.SubListIterator(index);
      }

      @Override
      public LongSpliterator spliterator() {
         return new LongArrayList.SubList.SubListSpliterator();
      }

      boolean contentsEquals(long[] otherA, int otherAFrom, int otherATo) {
         if (LongArrayList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
            return true;
         } else if (otherATo - otherAFrom != this.size()) {
            return false;
         } else {
            int pos = this.from;
            int otherPos = otherAFrom;

            while (pos < this.to) {
               if (LongArrayList.this.a[pos++] != otherA[otherPos++]) {
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
         } else if (!(o instanceof List)) {
            return false;
         } else if (o instanceof LongArrayList) {
            LongArrayList other = (LongArrayList)o;
            return this.contentsEquals(other.a, 0, other.size());
         } else if (o instanceof LongArrayList.SubList) {
            LongArrayList.SubList other = (LongArrayList.SubList)o;
            return this.contentsEquals(other.getParentArray(), other.from, other.to);
         } else {
            return super.equals(o);
         }
      }

      int contentsCompareTo(long[] otherA, int otherAFrom, int otherATo) {
         if (LongArrayList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
            return 0;
         } else {
            int i = this.from;

            for (int j = otherAFrom; i < this.to && i < otherATo; j++) {
               long e1 = LongArrayList.this.a[i];
               long e2 = otherA[j];
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
      public int compareTo(List<? extends Long> l) {
         if (l instanceof LongArrayList) {
            LongArrayList other = (LongArrayList)l;
            return this.contentsCompareTo(other.a, 0, other.size());
         } else if (l instanceof LongArrayList.SubList) {
            LongArrayList.SubList other = (LongArrayList.SubList)l;
            return this.contentsCompareTo(other.getParentArray(), other.from, other.to);
         } else {
            return super.compareTo(l);
         }
      }

      private final class SubListIterator extends LongIterators.AbstractIndexBasedListIterator {
         SubListIterator(int index) {
            super(0, index);
         }

         @Override
         protected final long get(int i) {
            return LongArrayList.this.a[SubList.this.from + i];
         }

         @Override
         protected final void add(int i, long k) {
            SubList.this.add(i, k);
         }

         @Override
         protected final void set(int i, long k) {
            SubList.this.set(i, k);
         }

         @Override
         protected final void remove(int i) {
            SubList.this.removeLong(i);
         }

         @Override
         protected final int getMaxPos() {
            return SubList.this.to - SubList.this.from;
         }

         @Override
         public long nextLong() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return LongArrayList.this.a[SubList.this.from + (this.lastReturned = this.pos++)];
            }
         }

         @Override
         public long previousLong() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return LongArrayList.this.a[SubList.this.from + (this.lastReturned = --this.pos)];
            }
         }

         @Override
         public void forEachRemaining(java.util.function.LongConsumer action) {
            int max = SubList.this.to - SubList.this.from;

            while (this.pos < max) {
               action.accept(LongArrayList.this.a[SubList.this.from + (this.lastReturned = this.pos++)]);
            }
         }
      }

      private final class SubListSpliterator extends LongSpliterators.LateBindingSizeIndexBasedSpliterator {
         SubListSpliterator() {
            super(SubList.this.from);
         }

         private SubListSpliterator(int pos, int maxPos) {
            super(pos, maxPos);
         }

         @Override
         protected final int getMaxPosFromBackingStore() {
            return SubList.this.to;
         }

         @Override
         protected final long get(int i) {
            return LongArrayList.this.a[i];
         }

         protected final LongArrayList.SubList.SubListSpliterator makeForSplit(int pos, int maxPos) {
            return SubList.this.new SubListSpliterator(pos, maxPos);
         }

         @Override
         public boolean tryAdvance(java.util.function.LongConsumer action) {
            if (this.pos >= this.getMaxPos()) {
               return false;
            } else {
               action.accept(LongArrayList.this.a[this.pos++]);
               return true;
            }
         }

         @Override
         public void forEachRemaining(java.util.function.LongConsumer action) {
            int max = this.getMaxPos();

            while (this.pos < max) {
               action.accept(LongArrayList.this.a[this.pos++]);
            }
         }
      }
   }
}
