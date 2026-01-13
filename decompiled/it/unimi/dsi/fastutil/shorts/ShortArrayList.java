package it.unimi.dsi.fastutil.shorts;

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

public class ShortArrayList extends AbstractShortList implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353130L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected transient short[] a;
   protected int size;

   private static final short[] copyArraySafe(short[] a, int length) {
      return length == 0 ? ShortArrays.EMPTY_ARRAY : Arrays.copyOf(a, length);
   }

   private static final short[] copyArrayFromSafe(ShortArrayList l) {
      return copyArraySafe(l.a, l.size);
   }

   protected ShortArrayList(short[] a, boolean wrapped) {
      this.a = a;
   }

   private void initArrayFromCapacity(int capacity) {
      if (capacity < 0) {
         throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
      } else {
         if (capacity == 0) {
            this.a = ShortArrays.EMPTY_ARRAY;
         } else {
            this.a = new short[capacity];
         }
      }
   }

   public ShortArrayList(int capacity) {
      this.initArrayFromCapacity(capacity);
   }

   public ShortArrayList() {
      this.a = ShortArrays.DEFAULT_EMPTY_ARRAY;
   }

   public ShortArrayList(Collection<? extends Short> c) {
      if (c instanceof ShortArrayList) {
         this.a = copyArrayFromSafe((ShortArrayList)c);
         this.size = this.a.length;
      } else {
         this.initArrayFromCapacity(c.size());
         if (c instanceof ShortList) {
            ((ShortList)c).getElements(0, this.a, 0, this.size = c.size());
         } else {
            this.size = ShortIterators.unwrap(ShortIterators.asShortIterator(c.iterator()), this.a);
         }
      }
   }

   public ShortArrayList(ShortCollection c) {
      if (c instanceof ShortArrayList) {
         this.a = copyArrayFromSafe((ShortArrayList)c);
         this.size = this.a.length;
      } else {
         this.initArrayFromCapacity(c.size());
         if (c instanceof ShortList) {
            ((ShortList)c).getElements(0, this.a, 0, this.size = c.size());
         } else {
            this.size = ShortIterators.unwrap(c.iterator(), this.a);
         }
      }
   }

   public ShortArrayList(ShortList l) {
      if (l instanceof ShortArrayList) {
         this.a = copyArrayFromSafe((ShortArrayList)l);
         this.size = this.a.length;
      } else {
         this.initArrayFromCapacity(l.size());
         l.getElements(0, this.a, 0, this.size = l.size());
      }
   }

   public ShortArrayList(short[] a) {
      this(a, 0, a.length);
   }

   public ShortArrayList(short[] a, int offset, int length) {
      this(length);
      System.arraycopy(a, offset, this.a, 0, length);
      this.size = length;
   }

   public ShortArrayList(Iterator<? extends Short> i) {
      this();

      while (i.hasNext()) {
         this.add(i.next());
      }
   }

   public ShortArrayList(ShortIterator i) {
      this();

      while (i.hasNext()) {
         this.add(i.nextShort());
      }
   }

   public short[] elements() {
      return this.a;
   }

   public static ShortArrayList wrap(short[] a, int length) {
      if (length > a.length) {
         throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + a.length + ")");
      } else {
         ShortArrayList l = new ShortArrayList(a, true);
         l.size = length;
         return l;
      }
   }

   public static ShortArrayList wrap(short[] a) {
      return wrap(a, a.length);
   }

   public static ShortArrayList of() {
      return new ShortArrayList();
   }

   public static ShortArrayList of(short... init) {
      return wrap(init);
   }

   public void ensureCapacity(int capacity) {
      if (capacity > this.a.length && (this.a != ShortArrays.DEFAULT_EMPTY_ARRAY || capacity > 10)) {
         this.a = ShortArrays.ensureCapacity(this.a, capacity, this.size);

         assert this.size <= this.a.length;
      }
   }

   private void grow(int capacity) {
      if (capacity > this.a.length) {
         if (this.a != ShortArrays.DEFAULT_EMPTY_ARRAY) {
            capacity = (int)Math.max(Math.min((long)this.a.length + (this.a.length >> 1), 2147483639L), (long)capacity);
         } else if (capacity < 10) {
            capacity = 10;
         }

         this.a = ShortArrays.forceCapacity(this.a, capacity, this.size);

         assert this.size <= this.a.length;
      }
   }

   @Override
   public void add(int index, short k) {
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
   public boolean add(short k) {
      this.grow(this.size + 1);
      this.a[this.size++] = k;

      assert this.size <= this.a.length;

      return true;
   }

   @Override
   public short getShort(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return this.a[index];
      }
   }

   @Override
   public int indexOf(short k) {
      for (int i = 0; i < this.size; i++) {
         if (k == this.a[i]) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public int lastIndexOf(short k) {
      int i = this.size;

      while (i-- != 0) {
         if (k == this.a[i]) {
            return i;
         }
      }

      return -1;
   }

   @Override
   public short removeShort(int index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         short old = this.a[index];
         this.size--;
         if (index != this.size) {
            System.arraycopy(this.a, index + 1, this.a, index, this.size - index);
         }

         assert this.size <= this.a.length;

         return old;
      }
   }

   @Override
   public boolean rem(short k) {
      int index = this.indexOf(k);
      if (index == -1) {
         return false;
      } else {
         this.removeShort(index);

         assert this.size <= this.a.length;

         return true;
      }
   }

   @Override
   public short set(int index, short k) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         short old = this.a[index];
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
         this.a = ShortArrays.forceCapacity(this.a, size, this.size);
      }

      if (size > this.size) {
         Arrays.fill(this.a, this.size, size, (short)0);
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
         short[] t = new short[Math.max(n, this.size)];
         System.arraycopy(this.a, 0, t, 0, this.size);
         this.a = t;

         assert this.size <= this.a.length;
      }
   }

   @Override
   public ShortList subList(int from, int to) {
      if (from == 0 && to == this.size()) {
         return this;
      } else {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new ShortArrayList.SubList(from, to);
         }
      }
   }

   @Override
   public void getElements(int from, short[] a, int offset, int length) {
      ShortArrays.ensureOffsetLength(a, offset, length);
      System.arraycopy(this.a, from, a, offset, length);
   }

   @Override
   public void removeElements(int from, int to) {
      it.unimi.dsi.fastutil.Arrays.ensureFromTo(this.size, from, to);
      System.arraycopy(this.a, to, this.a, from, this.size - to);
      this.size -= to - from;
   }

   @Override
   public void addElements(int index, short[] a, int offset, int length) {
      this.ensureIndex(index);
      ShortArrays.ensureOffsetLength(a, offset, length);
      this.grow(this.size + length);
      System.arraycopy(this.a, index, this.a, index + length, this.size - index);
      System.arraycopy(a, offset, this.a, index, length);
      this.size += length;
   }

   @Override
   public void setElements(int index, short[] a, int offset, int length) {
      this.ensureIndex(index);
      ShortArrays.ensureOffsetLength(a, offset, length);
      if (index + length > this.size) {
         throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size + ")");
      } else {
         System.arraycopy(a, offset, this.a, index, length);
      }
   }

   @Override
   public void forEach(ShortConsumer action) {
      for (int i = 0; i < this.size; i++) {
         action.accept(this.a[i]);
      }
   }

   @Override
   public boolean addAll(int index, ShortCollection c) {
      if (c instanceof ShortList) {
         return this.addAll(index, (ShortList)c);
      } else {
         this.ensureIndex(index);
         int n = c.size();
         if (n == 0) {
            return false;
         } else {
            this.grow(this.size + n);
            System.arraycopy(this.a, index, this.a, index + n, this.size - index);
            ShortIterator i = c.iterator();
            this.size += n;

            while (n-- != 0) {
               this.a[index++] = i.nextShort();
            }

            assert this.size <= this.a.length;

            return true;
         }
      }
   }

   @Override
   public boolean addAll(int index, ShortList l) {
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
   public boolean removeAll(ShortCollection c) {
      short[] a = this.a;
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
   public short[] toArray(short[] a) {
      if (a == null || a.length < this.size) {
         a = Arrays.copyOf(a, this.size);
      }

      System.arraycopy(this.a, 0, a, 0, this.size);
      return a;
   }

   @Override
   public ShortListIterator listIterator(final int index) {
      this.ensureIndex(index);
      return new ShortListIterator() {
         int pos = index;
         int last = -1;

         @Override
         public boolean hasNext() {
            return this.pos < ShortArrayList.this.size;
         }

         @Override
         public boolean hasPrevious() {
            return this.pos > 0;
         }

         @Override
         public short nextShort() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return ShortArrayList.this.a[this.last = this.pos++];
            }
         }

         @Override
         public short previousShort() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return ShortArrayList.this.a[this.last = --this.pos];
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
         public void add(short k) {
            ShortArrayList.this.add(this.pos++, k);
            this.last = -1;
         }

         @Override
         public void set(short k) {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               ShortArrayList.this.set(this.last, k);
            }
         }

         @Override
         public void remove() {
            if (this.last == -1) {
               throw new IllegalStateException();
            } else {
               ShortArrayList.this.removeShort(this.last);
               if (this.last < this.pos) {
                  this.pos--;
               }

               this.last = -1;
            }
         }

         @Override
         public void forEachRemaining(ShortConsumer action) {
            while (this.pos < ShortArrayList.this.size) {
               action.accept(ShortArrayList.this.a[this.last = this.pos++]);
            }
         }

         @Override
         public int back(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int remaining = ShortArrayList.this.size - this.pos;
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
               int remaining = ShortArrayList.this.size - this.pos;
               if (n < remaining) {
                  this.pos += n;
               } else {
                  n = remaining;
                  this.pos = ShortArrayList.this.size;
               }

               this.last = this.pos - 1;
               return n;
            }
         }
      };
   }

   @Override
   public ShortSpliterator spliterator() {
      return new ShortArrayList.Spliterator();
   }

   @Override
   public void sort(ShortComparator comp) {
      if (comp == null) {
         ShortArrays.stableSort(this.a, 0, this.size);
      } else {
         ShortArrays.stableSort(this.a, 0, this.size, comp);
      }
   }

   @Override
   public void unstableSort(ShortComparator comp) {
      if (comp == null) {
         ShortArrays.unstableSort(this.a, 0, this.size);
      } else {
         ShortArrays.unstableSort(this.a, 0, this.size, comp);
      }
   }

   public ShortArrayList clone() {
      ShortArrayList cloned = null;
      if (this.getClass() == ShortArrayList.class) {
         cloned = new ShortArrayList(copyArraySafe(this.a, this.size), false);
         cloned.size = this.size;
      } else {
         try {
            cloned = (ShortArrayList)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError(var3);
         }

         cloned.a = copyArraySafe(this.a, this.size);
      }

      return cloned;
   }

   public boolean equals(ShortArrayList l) {
      if (l == this) {
         return true;
      } else {
         int s = this.size();
         if (s != l.size()) {
            return false;
         } else {
            short[] a1 = this.a;
            short[] a2 = l.a;
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
      } else if (o instanceof ShortArrayList) {
         return this.equals((ShortArrayList)o);
      } else {
         return o instanceof ShortArrayList.SubList ? ((ShortArrayList.SubList)o).equals(this) : super.equals(o);
      }
   }

   public int compareTo(ShortArrayList l) {
      int s1 = this.size();
      int s2 = l.size();
      short[] a1 = this.a;
      short[] a2 = l.a;
      if (a1 == a2 && s1 == s2) {
         return 0;
      } else {
         int i;
         for (i = 0; i < s1 && i < s2; i++) {
            short e1 = a1[i];
            short e2 = a2[i];
            int r;
            if ((r = Short.compare(e1, e2)) != 0) {
               return r;
            }
         }

         return i < s2 ? -1 : (i < s1 ? 1 : 0);
      }
   }

   @Override
   public int compareTo(List<? extends Short> l) {
      if (l instanceof ShortArrayList) {
         return this.compareTo((ShortArrayList)l);
      } else {
         return l instanceof ShortArrayList.SubList ? -((ShortArrayList.SubList)l).compareTo(this) : super.compareTo(l);
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();

      for (int i = 0; i < this.size; i++) {
         s.writeShort(this.a[i]);
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.a = new short[this.size];

      for (int i = 0; i < this.size; i++) {
         this.a[i] = s.readShort();
      }
   }

   private final class Spliterator implements ShortSpliterator {
      boolean hasSplit = false;
      int pos;
      int max;

      public Spliterator() {
         this(0, ShortArrayList.this.size, false);
      }

      private Spliterator(int pos, int max, boolean hasSplit) {
         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
         this.hasSplit = hasSplit;
      }

      private int getWorkingMax() {
         return this.hasSplit ? this.max : ShortArrayList.this.size;
      }

      @Override
      public int characteristics() {
         return 16720;
      }

      @Override
      public long estimateSize() {
         return this.getWorkingMax() - this.pos;
      }

      public boolean tryAdvance(ShortConsumer action) {
         if (this.pos >= this.getWorkingMax()) {
            return false;
         } else {
            action.accept(ShortArrayList.this.a[this.pos++]);
            return true;
         }
      }

      public void forEachRemaining(ShortConsumer action) {
         for (int max = this.getWorkingMax(); this.pos < max; this.pos++) {
            action.accept(ShortArrayList.this.a[this.pos]);
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
      public ShortSpliterator trySplit() {
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
            return ShortArrayList.this.new Spliterator(oldPos, myNewPos, true);
         }
      }
   }

   private class SubList extends AbstractShortList.ShortRandomAccessSubList {
      private static final long serialVersionUID = -3185226345314976296L;

      protected SubList(int from, int to) {
         super(ShortArrayList.this, from, to);
      }

      private short[] getParentArray() {
         return ShortArrayList.this.a;
      }

      @Override
      public short getShort(int i) {
         this.ensureRestrictedIndex(i);
         return ShortArrayList.this.a[i + this.from];
      }

      @Override
      public ShortListIterator listIterator(int index) {
         return new ShortArrayList.SubList.SubListIterator(index);
      }

      @Override
      public ShortSpliterator spliterator() {
         return new ShortArrayList.SubList.SubListSpliterator();
      }

      boolean contentsEquals(short[] otherA, int otherAFrom, int otherATo) {
         if (ShortArrayList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
            return true;
         } else if (otherATo - otherAFrom != this.size()) {
            return false;
         } else {
            int pos = this.from;
            int otherPos = otherAFrom;

            while (pos < this.to) {
               if (ShortArrayList.this.a[pos++] != otherA[otherPos++]) {
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
         } else if (o instanceof ShortArrayList) {
            ShortArrayList other = (ShortArrayList)o;
            return this.contentsEquals(other.a, 0, other.size());
         } else if (o instanceof ShortArrayList.SubList) {
            ShortArrayList.SubList other = (ShortArrayList.SubList)o;
            return this.contentsEquals(other.getParentArray(), other.from, other.to);
         } else {
            return super.equals(o);
         }
      }

      int contentsCompareTo(short[] otherA, int otherAFrom, int otherATo) {
         if (ShortArrayList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
            return 0;
         } else {
            int i = this.from;

            for (int j = otherAFrom; i < this.to && i < otherATo; j++) {
               short e1 = ShortArrayList.this.a[i];
               short e2 = otherA[j];
               int r;
               if ((r = Short.compare(e1, e2)) != 0) {
                  return r;
               }

               i++;
            }

            return i < otherATo ? -1 : (i < this.to ? 1 : 0);
         }
      }

      @Override
      public int compareTo(List<? extends Short> l) {
         if (l instanceof ShortArrayList) {
            ShortArrayList other = (ShortArrayList)l;
            return this.contentsCompareTo(other.a, 0, other.size());
         } else if (l instanceof ShortArrayList.SubList) {
            ShortArrayList.SubList other = (ShortArrayList.SubList)l;
            return this.contentsCompareTo(other.getParentArray(), other.from, other.to);
         } else {
            return super.compareTo(l);
         }
      }

      private final class SubListIterator extends ShortIterators.AbstractIndexBasedListIterator {
         SubListIterator(int index) {
            super(0, index);
         }

         @Override
         protected final short get(int i) {
            return ShortArrayList.this.a[SubList.this.from + i];
         }

         @Override
         protected final void add(int i, short k) {
            SubList.this.add(i, k);
         }

         @Override
         protected final void set(int i, short k) {
            SubList.this.set(i, k);
         }

         @Override
         protected final void remove(int i) {
            SubList.this.removeShort(i);
         }

         @Override
         protected final int getMaxPos() {
            return SubList.this.to - SubList.this.from;
         }

         @Override
         public short nextShort() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return ShortArrayList.this.a[SubList.this.from + (this.lastReturned = this.pos++)];
            }
         }

         @Override
         public short previousShort() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return ShortArrayList.this.a[SubList.this.from + (this.lastReturned = --this.pos)];
            }
         }

         @Override
         public void forEachRemaining(ShortConsumer action) {
            int max = SubList.this.to - SubList.this.from;

            while (this.pos < max) {
               action.accept(ShortArrayList.this.a[SubList.this.from + (this.lastReturned = this.pos++)]);
            }
         }
      }

      private final class SubListSpliterator extends ShortSpliterators.LateBindingSizeIndexBasedSpliterator {
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
         protected final short get(int i) {
            return ShortArrayList.this.a[i];
         }

         protected final ShortArrayList.SubList.SubListSpliterator makeForSplit(int pos, int maxPos) {
            return SubList.this.new SubListSpliterator(pos, maxPos);
         }

         @Override
         public boolean tryAdvance(ShortConsumer action) {
            if (this.pos >= this.getMaxPos()) {
               return false;
            } else {
               action.accept(ShortArrayList.this.a[this.pos++]);
               return true;
            }
         }

         @Override
         public void forEachRemaining(ShortConsumer action) {
            int max = this.getMaxPos();

            while (this.pos < max) {
               action.accept(ShortArrayList.this.a[this.pos++]);
            }
         }
      }
   }
}
