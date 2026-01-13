package it.unimi.dsi.fastutil.booleans;

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

public class BooleanArrayList extends AbstractBooleanList implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353130L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected transient boolean[] a;
   protected int size;

   private static final boolean[] copyArraySafe(boolean[] a, int length) {
      return length == 0 ? BooleanArrays.EMPTY_ARRAY : Arrays.copyOf(a, length);
   }

   private static final boolean[] copyArrayFromSafe(BooleanArrayList l) {
      return copyArraySafe(l.a, l.size);
   }

   protected BooleanArrayList(boolean[] a, boolean wrapped) {
      this.a = a;
   }

   private void initArrayFromCapacity(int capacity) {
      if (capacity < 0) {
         throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
      } else {
         if (capacity == 0) {
            this.a = BooleanArrays.EMPTY_ARRAY;
         } else {
            this.a = new boolean[capacity];
         }
      }
   }

   public BooleanArrayList(int capacity) {
      this.initArrayFromCapacity(capacity);
   }

   public BooleanArrayList() {
      this.a = BooleanArrays.DEFAULT_EMPTY_ARRAY;
   }

   public BooleanArrayList(Collection<? extends Boolean> c) {
      if (c instanceof BooleanArrayList) {
         this.a = copyArrayFromSafe((BooleanArrayList)c);
         this.size = this.a.length;
      } else {
         this.initArrayFromCapacity(c.size());
         if (c instanceof BooleanList) {
            ((BooleanList)c).getElements(0, this.a, 0, this.size = c.size());
         } else {
            this.size = BooleanIterators.unwrap(BooleanIterators.asBooleanIterator(c.iterator()), this.a);
         }
      }
   }

   public BooleanArrayList(BooleanCollection c) {
      if (c instanceof BooleanArrayList) {
         this.a = copyArrayFromSafe((BooleanArrayList)c);
         this.size = this.a.length;
      } else {
         this.initArrayFromCapacity(c.size());
         if (c instanceof BooleanList) {
            ((BooleanList)c).getElements(0, this.a, 0, this.size = c.size());
         } else {
            this.size = BooleanIterators.unwrap(c.iterator(), this.a);
         }
      }
   }

   public BooleanArrayList(BooleanList l) {
      if (l instanceof BooleanArrayList) {
         this.a = copyArrayFromSafe((BooleanArrayList)l);
         this.size = this.a.length;
      } else {
         this.initArrayFromCapacity(l.size());
         l.getElements(0, this.a, 0, this.size = l.size());
      }
   }

   public BooleanArrayList(boolean[] a) {
      this(a, 0, a.length);
   }

   public BooleanArrayList(boolean[] a, int offset, int length) {
      this(length);
      System.arraycopy(a, offset, this.a, 0, length);
      this.size = length;
   }

   public BooleanArrayList(Iterator<? extends Boolean> i) {
      this();

      while (i.hasNext()) {
         this.add(i.next());
      }
   }

   public BooleanArrayList(BooleanIterator i) {
      this();

      while (i.hasNext()) {
         this.add(i.nextBoolean());
      }
   }

   public boolean[] elements() {
      return this.a;
   }

   public static BooleanArrayList wrap(boolean[] a, int length) {
      if (length > a.length) {
         throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a.length + ")");
      } else {
         BooleanArrayList l = new BooleanArrayList(a, true);
         l.size = length;
         return l;
      }
   }

   public static BooleanArrayList wrap(boolean[] a) {
      return wrap(a, a.length);
   }

   public static BooleanArrayList of() {
      return new BooleanArrayList();
   }

   public static BooleanArrayList of(boolean... init) {
      return wrap(init);
   }

   public void ensureCapacity(int capacity) {
      if (capacity > this.a.length && (this.a != BooleanArrays.DEFAULT_EMPTY_ARRAY || capacity > 10)) {
         this.a = BooleanArrays.ensureCapacity(this.a, capacity, this.size);

         assert this.size <= this.a.length;
      }
   }

   private void grow(int capacity) {
      if (capacity > this.a.length) {
         if (this.a != BooleanArrays.DEFAULT_EMPTY_ARRAY) {
            capacity = (int)Math.max(Math.min((long)this.a.length + (this.a.length >> 1), 2147483639L), (long)capacity);
         } else if (capacity < 10) {
            capacity = 10;
         }

         this.a = BooleanArrays.forceCapacity(this.a, capacity, this.size);

         assert this.size <= this.a.length;
      }
   }

   @Override
   public void add(int index, boolean k) {
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
   public boolean add(boolean k) {
      this.grow(this.size + 1);
      this.a[this.size++] = k;

      assert this.size <= this.a.length;

      return true;
   }

   @Override
   public boolean getBoolean(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return this.a[index];
      }
   }

   @Override
   public int indexOf(boolean k) {
      for (int i = 0; i < this.size; i++) {
         if (k == this.a[i]) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public int lastIndexOf(boolean k) {
      int i = this.size;

      while (i-- != 0) {
         if (k == this.a[i]) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public boolean removeBoolean(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         boolean old = this.a[index];
         this.size--;
         if (index != this.size) {
            System.arraycopy(this.a, index + 1, this.a, index, this.size - index);
         }

         assert this.size <= this.a.length;

         return old;
      }
   }

   @Override
   public boolean rem(boolean k) {
      int index = this.indexOf(k);
      if (index == -1) {
         return false;
      } else {
         this.removeBoolean(index);

         assert this.size <= this.a.length;

         return true;
      }
   }

   @Override
   public boolean set(int index, boolean k) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         boolean old = this.a[index];
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
         this.a = BooleanArrays.forceCapacity(this.a, size, this.size);
      }

      if (size > this.size) {
         Arrays.fill(this.a, this.size, size, false);
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
         boolean[] t = new boolean[Math.max(n, this.size)];
         System.arraycopy(this.a, 0, t, 0, this.size);
         this.a = t;

         assert this.size <= this.a.length;
      }
   }

   @Override
   public BooleanList subList(int from, int to) {
      if (from == 0 && to == this.size()) {
         return this;
      } else {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new BooleanArrayList.SubList(from, to);
         }
      }
   }

   @Override
   public void getElements(int from, boolean[] a, int offset, int length) {
      BooleanArrays.ensureOffsetLength(a, offset, length);
      System.arraycopy(this.a, from, a, offset, length);
   }

   @Override
   public void removeElements(int from, int to) {
      it.unimi.dsi.fastutil.Arrays.ensureFromTo(this.size, from, to);
      System.arraycopy(this.a, to, this.a, from, this.size - to);
      this.size -= to - from;
   }

   @Override
   public void addElements(int index, boolean[] a, int offset, int length) {
      this.ensureIndex(index);
      BooleanArrays.ensureOffsetLength(a, offset, length);
      this.grow(this.size + length);
      System.arraycopy(this.a, index, this.a, index + length, this.size - index);
      System.arraycopy(a, offset, this.a, index, length);
      this.size += length;
   }

   @Override
   public void setElements(int index, boolean[] a, int offset, int length) {
      this.ensureIndex(index);
      BooleanArrays.ensureOffsetLength(a, offset, length);
      if (index + length > this.size) {
         throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size + ")");
      } else {
         System.arraycopy(a, offset, this.a, index, length);
      }
   }

   @Override
   public void forEach(BooleanConsumer action) {
      for (int i = 0; i < this.size; i++) {
         action.accept(this.a[i]);
      }
   }

   @Override
   public boolean addAll(int index, BooleanCollection c) {
      if (c instanceof BooleanList) {
         return this.addAll(index, (BooleanList)c);
      } else {
         this.ensureIndex(index);
         int n = c.size();
         if (n == 0) {
            return false;
         } else {
            this.grow(this.size + n);
            System.arraycopy(this.a, index, this.a, index + n, this.size - index);
            BooleanIterator i = c.iterator();
            this.size += n;

            while (n-- != 0) {
               this.a[index++] = i.nextBoolean();
            }

            assert this.size <= this.a.length;

            return true;
         }
      }
   }

   @Override
   public boolean addAll(int index, BooleanList l) {
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
   public boolean removeAll(BooleanCollection c) {
      boolean[] a = this.a;
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
   public boolean[] toArray(boolean[] a) {
      if (a == null || a.length < this.size) {
         a = Arrays.copyOf(a, this.size);
      }

      System.arraycopy(this.a, 0, a, 0, this.size);
      return a;
   }

   @Override
   public BooleanListIterator listIterator(final int index) {
      this.ensureIndex(index);
      return new BooleanListIterator() {
         int pos = index;
         int last = -1;

         @Override
         public boolean hasNext() {
            return this.pos < BooleanArrayList.this.size;
         }

         @Override
         public boolean hasPrevious() {
            return this.pos > 0;
         }

         @Override
         public boolean nextBoolean() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return BooleanArrayList.this.a[this.last = this.pos++];
            }
         }

         @Override
         public boolean previousBoolean() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return BooleanArrayList.this.a[this.last = --this.pos];
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
         public void add(boolean k) {
            BooleanArrayList.this.add(this.pos++, k);
            this.last = -1;
         }

         @Override
         public void set(boolean k) {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               BooleanArrayList.this.set(this.last, k);
            }
         }

         @Override
         public void remove() {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               BooleanArrayList.this.removeBoolean(this.last);
               if (this.last < this.pos) {
                  this.pos--;
               }

               this.last = -1;
            }
         }

         @Override
         public void forEachRemaining(BooleanConsumer action) {
            while (this.pos < BooleanArrayList.this.size) {
               action.accept(BooleanArrayList.this.a[this.last = this.pos++]);
            }
         }

         @Override
         public int back(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int remaining = BooleanArrayList.this.size - this.pos;
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
               int remaining = BooleanArrayList.this.size - this.pos;
               if (n < remaining) {
                  this.pos += n;
               } else {
                  n = remaining;
                  this.pos = BooleanArrayList.this.size;
               }

               this.last = this.pos - 1;
               return n;
            }
         }
      };
   }

   @Override
   public BooleanSpliterator spliterator() {
      return new BooleanArrayList.Spliterator();
   }

   @Override
   public void sort(BooleanComparator comp) {
      if (comp == null) {
         BooleanArrays.stableSort(this.a, 0, this.size);
      } else {
         BooleanArrays.stableSort(this.a, 0, this.size, comp);
      }
   }

   @Override
   public void unstableSort(BooleanComparator comp) {
      if (comp == null) {
         BooleanArrays.unstableSort(this.a, 0, this.size);
      } else {
         BooleanArrays.unstableSort(this.a, 0, this.size, comp);
      }
   }

   public BooleanArrayList clone() {
      BooleanArrayList cloned = null;
      if (this.getClass() == BooleanArrayList.class) {
         cloned = new BooleanArrayList(copyArraySafe(this.a, this.size), false);
         cloned.size = this.size;
      } else {
         try {
            cloned = (BooleanArrayList)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError(var3);
         }

         cloned.a = copyArraySafe(this.a, this.size);
      }

      return cloned;
   }

   public boolean equals(BooleanArrayList l) {
      if (l == this) {
         return true;
      } else {
         int s = this.size();
         if (s != l.size()) {
            return false;
         } else {
            boolean[] a1 = this.a;
            boolean[] a2 = l.a;
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
      } else if (o instanceof BooleanArrayList) {
         return this.equals((BooleanArrayList)o);
      } else {
         return o instanceof BooleanArrayList.SubList ? ((BooleanArrayList.SubList)o).equals(this) : super.equals(o);
      }
   }

   public int compareTo(BooleanArrayList l) {
      int s1 = this.size();
      int s2 = l.size();
      boolean[] a1 = this.a;
      boolean[] a2 = l.a;
      if (a1 == a2 && s1 == s2) {
         return 0;
      } else {
         int i;
         for (i = 0; i < s1 && i < s2; i++) {
            boolean e1 = a1[i];
            boolean e2 = a2[i];
            int r;
            if ((r = Boolean.compare(e1, e2)) != 0) {
               return r;
            }
         }

         return i < s2 ? -1 : (i < s1 ? 1 : 0);
      }
   }

   @Override
   public int compareTo(List<? extends Boolean> l) {
      if (l instanceof BooleanArrayList) {
         return this.compareTo((BooleanArrayList)l);
      } else {
         return l instanceof BooleanArrayList.SubList ? -((BooleanArrayList.SubList)l).compareTo(this) : super.compareTo(l);
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();

      for (int i = 0; i < this.size; i++) {
         s.writeBoolean(this.a[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.a = new boolean[this.size];

      for (int i = 0; i < this.size; i++) {
         this.a[i] = s.readBoolean();
      }
   }

   private final class Spliterator implements BooleanSpliterator {
      boolean hasSplit = false;
      int pos;
      int max;

      public Spliterator() {
         this(0, BooleanArrayList.this.size, false);
      }

      private Spliterator(int pos, int max, boolean hasSplit) {
         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
         this.hasSplit = hasSplit;
      }

      private int getWorkingMax() {
         return this.hasSplit ? this.max : BooleanArrayList.this.size;
      }

      @Override
      public int characteristics() {
         return 16720;
      }

      @Override
      public long estimateSize() {
         return this.getWorkingMax() - this.pos;
      }

      public boolean tryAdvance(BooleanConsumer action) {
         if (this.pos >= this.getWorkingMax()) {
            return false;
         } else {
            action.accept(BooleanArrayList.this.a[this.pos++]);
            return true;
         }
      }

      public void forEachRemaining(BooleanConsumer action) {
         for (int max = this.getWorkingMax(); this.pos < max; this.pos++) {
            action.accept(BooleanArrayList.this.a[this.pos]);
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
      public BooleanSpliterator trySplit() {
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
            return BooleanArrayList.this.new Spliterator(oldPos, myNewPos, true);
         }
      }
   }

   private class SubList extends AbstractBooleanList.BooleanRandomAccessSubList {
      private static final long serialVersionUID = -3185226345314976296L;

      protected SubList(int from, int to) {
         super(BooleanArrayList.this, from, to);
      }

      private boolean[] getParentArray() {
         return BooleanArrayList.this.a;
      }

      @Override
      public boolean getBoolean(int i) {
         this.ensureRestrictedIndex(i);
         return BooleanArrayList.this.a[i + this.from];
      }

      @Override
      public BooleanListIterator listIterator(int index) {
         return new BooleanArrayList.SubList.SubListIterator(index);
      }

      @Override
      public BooleanSpliterator spliterator() {
         return new BooleanArrayList.SubList.SubListSpliterator();
      }

      boolean contentsEquals(boolean[] otherA, int otherAFrom, int otherATo) {
         if (BooleanArrayList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
            return true;
         } else if (otherATo - otherAFrom != this.size()) {
            return false;
         } else {
            int pos = this.from;
            int otherPos = otherAFrom;

            while (pos < this.to) {
               if (BooleanArrayList.this.a[pos++] != otherA[otherPos++]) {
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
         } else if (o instanceof BooleanArrayList) {
            BooleanArrayList other = (BooleanArrayList)o;
            return this.contentsEquals(other.a, 0, other.size());
         } else if (o instanceof BooleanArrayList.SubList) {
            BooleanArrayList.SubList other = (BooleanArrayList.SubList)o;
            return this.contentsEquals(other.getParentArray(), other.from, other.to);
         } else {
            return super.equals(o);
         }
      }

      int contentsCompareTo(boolean[] otherA, int otherAFrom, int otherATo) {
         if (BooleanArrayList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
            return 0;
         } else {
            int i = this.from;

            for (int j = otherAFrom; i < this.to && i < otherATo; j++) {
               boolean e1 = BooleanArrayList.this.a[i];
               boolean e2 = otherA[j];
               int r;
               if ((r = Boolean.compare(e1, e2)) != 0) {
                  return r;
               }

               i++;
            }

            return i < otherATo ? -1 : (i < this.to ? 1 : 0);
         }
      }

      @Override
      public int compareTo(List<? extends Boolean> l) {
         if (l instanceof BooleanArrayList) {
            BooleanArrayList other = (BooleanArrayList)l;
            return this.contentsCompareTo(other.a, 0, other.size());
         } else if (l instanceof BooleanArrayList.SubList) {
            BooleanArrayList.SubList other = (BooleanArrayList.SubList)l;
            return this.contentsCompareTo(other.getParentArray(), other.from, other.to);
         } else {
            return super.compareTo(l);
         }
      }

      private final class SubListIterator extends BooleanIterators.AbstractIndexBasedListIterator {
         SubListIterator(int index) {
            super(0, index);
         }

         @Override
         protected final boolean get(int i) {
            return BooleanArrayList.this.a[SubList.this.from + i];
         }

         @Override
         protected final void add(int i, boolean k) {
            SubList.this.add(i, k);
         }

         @Override
         protected final void set(int i, boolean k) {
            SubList.this.set(i, k);
         }

         @Override
         protected final void remove(int i) {
            SubList.this.removeBoolean(i);
         }

         @Override
         protected final int getMaxPos() {
            return SubList.this.to - SubList.this.from;
         }

         @Override
         public boolean nextBoolean() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return BooleanArrayList.this.a[SubList.this.from + (this.lastReturned = this.pos++)];
            }
         }

         @Override
         public boolean previousBoolean() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return BooleanArrayList.this.a[SubList.this.from + (this.lastReturned = --this.pos)];
            }
         }

         @Override
         public void forEachRemaining(BooleanConsumer action) {
            int max = SubList.this.to - SubList.this.from;

            while (this.pos < max) {
               action.accept(BooleanArrayList.this.a[SubList.this.from + (this.lastReturned = this.pos++)]);
            }
         }
      }

      private final class SubListSpliterator extends BooleanSpliterators.LateBindingSizeIndexBasedSpliterator {
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
         protected final boolean get(int i) {
            return BooleanArrayList.this.a[i];
         }

         protected final BooleanArrayList.SubList.SubListSpliterator makeForSplit(int pos, int maxPos) {
            return SubList.this.new SubListSpliterator(pos, maxPos);
         }

         @Override
         public boolean tryAdvance(BooleanConsumer action) {
            if (this.pos >= this.getMaxPos()) {
               return false;
            } else {
               action.accept(BooleanArrayList.this.a[this.pos++]);
               return true;
            }
         }

         @Override
         public void forEachRemaining(BooleanConsumer action) {
            int max = this.getMaxPos();

            while (this.pos < max) {
               action.accept(BooleanArrayList.this.a[this.pos++]);
            }
         }
      }
   }
}
