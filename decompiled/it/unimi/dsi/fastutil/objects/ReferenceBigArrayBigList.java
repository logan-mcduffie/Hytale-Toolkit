package it.unimi.dsi.fastutil.objects;

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
import java.util.function.Consumer;
import java.util.stream.Collector;

public class ReferenceBigArrayBigList<K> extends AbstractReferenceBigList<K> implements RandomAccess, Cloneable, Serializable {
   private static final long serialVersionUID = -7046029254386353131L;
   public static final int DEFAULT_INITIAL_CAPACITY = 10;
   protected final boolean wrapped;
   protected transient K[][] a;
   protected long size;
   private static final Collector<Object, ?, ReferenceBigArrayBigList<Object>> TO_LIST_COLLECTOR = Collector.of(
      ReferenceBigArrayBigList::new, ReferenceBigArrayBigList::add, ReferenceBigArrayBigList::combine
   );

   protected ReferenceBigArrayBigList(K[][] a, boolean dummy) {
      this.a = a;
      this.wrapped = true;
   }

   public ReferenceBigArrayBigList(long capacity) {
      if (capacity < 0L) {
         throw new IllegalArgumentException("Initial capacity (" + capacity + ") is negative");
      } else {
         if (capacity == 0L) {
            this.a = (K[][])ObjectBigArrays.EMPTY_BIG_ARRAY;
         } else {
            this.a = (K[][])ObjectBigArrays.newBigArray(capacity);
         }

         this.wrapped = false;
      }
   }

   public ReferenceBigArrayBigList() {
      this.a = (K[][])ObjectBigArrays.DEFAULT_EMPTY_BIG_ARRAY;
      this.wrapped = false;
   }

   public ReferenceBigArrayBigList(ReferenceCollection<? extends K> c) {
      this(Size64.sizeOf(c));
      if (c instanceof ReferenceBigList) {
         ((ReferenceBigList)c).getElements(0L, this.a, 0L, this.size = Size64.sizeOf(c));
      } else {
         ObjectIterator<? extends K> i = c.iterator();

         while (i.hasNext()) {
            this.add((K)i.next());
         }
      }
   }

   public ReferenceBigArrayBigList(Collection<? extends K> c) {
      this(Size64.sizeOf(c));
      if (c instanceof ReferenceBigList) {
         ((ReferenceBigList)c).getElements(0L, this.a, 0L, this.size = Size64.sizeOf(c));
      } else {
         Iterator<? extends K> i = c.iterator();

         while (i.hasNext()) {
            this.add((K)i.next());
         }
      }
   }

   public ReferenceBigArrayBigList(ReferenceBigList<? extends K> l) {
      this(l.size64());
      l.getElements(0L, this.a, 0L, this.size = l.size64());
   }

   public ReferenceBigArrayBigList(K[][] a) {
      this(a, 0L, BigArrays.length(a));
   }

   public ReferenceBigArrayBigList(K[][] a, long offset, long length) {
      this(length);
      BigArrays.copy(a, offset, this.a, 0L, length);
      this.size = length;
   }

   public ReferenceBigArrayBigList(Iterator<? extends K> i) {
      this();

      while (i.hasNext()) {
         this.add((K)i.next());
      }
   }

   public ReferenceBigArrayBigList(ObjectIterator<? extends K> i) {
      this();

      while (i.hasNext()) {
         this.add((K)i.next());
      }
   }

   public K[][] elements() {
      return this.a;
   }

   public static <K> ReferenceBigArrayBigList<K> wrap(K[][] a, long length) {
      if (length > BigArrays.length(a)) {
         throw new IllegalArgumentException("The specified length (" + length + ") is greater than the array size (" + BigArrays.length(a) + ")");
      } else {
         ReferenceBigArrayBigList<K> l = new ReferenceBigArrayBigList<>(a, false);
         l.size = length;
         return l;
      }
   }

   public static <K> ReferenceBigArrayBigList<K> wrap(K[][] a) {
      return wrap(a, BigArrays.length(a));
   }

   public static <K> ReferenceBigArrayBigList<K> of() {
      return new ReferenceBigArrayBigList<>();
   }

   @SafeVarargs
   public static <K> ReferenceBigArrayBigList<K> of(K... init) {
      return wrap((K[][])BigArrays.wrap(init));
   }

   private ReferenceBigArrayBigList<K> combine(ReferenceBigArrayBigList<? extends K> toAddFrom) {
      this.addAll(toAddFrom);
      return this;
   }

   public static <K> Collector<K, ?, ReferenceBigArrayBigList<K>> toBigList() {
      return (Collector<K, ?, ReferenceBigArrayBigList<K>>)TO_LIST_COLLECTOR;
   }

   public static <K> Collector<K, ?, ReferenceBigArrayBigList<K>> toBigListWithExpectedSize(long expectedSize) {
      return Collector.of(() -> new ReferenceBigArrayBigList(expectedSize), ReferenceBigArrayBigList::add, ReferenceBigArrayBigList::combine);
   }

   public void ensureCapacity(long capacity) {
      if (capacity > BigArrays.length(this.a) && this.a != ObjectBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
         if (this.wrapped) {
            this.a = (K[][])BigArrays.forceCapacity(this.a, capacity, this.size);
         } else if (capacity > BigArrays.length(this.a)) {
            Object[][] t = ObjectBigArrays.newBigArray(capacity);
            BigArrays.copy(this.a, 0L, t, 0L, this.size);
            this.a = (K[][])t;
         }

         assert this.size <= BigArrays.length(this.a);
      }
   }

   private void grow(long capacity) {
      long oldLength = BigArrays.length(this.a);
      if (capacity > oldLength) {
         if (this.a != ObjectBigArrays.DEFAULT_EMPTY_BIG_ARRAY) {
            capacity = Math.max(oldLength + (oldLength >> 1), capacity);
         } else if (capacity < 10L) {
            capacity = 10L;
         }

         if (this.wrapped) {
            this.a = (K[][])BigArrays.forceCapacity(this.a, capacity, this.size);
         } else {
            Object[][] t = ObjectBigArrays.newBigArray(capacity);
            BigArrays.copy(this.a, 0L, t, 0L, this.size);
            this.a = (K[][])t;
         }

         assert this.size <= BigArrays.length(this.a);
      }
   }

   @Override
   public void add(long index, K k) {
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
   public boolean add(K k) {
      this.grow(this.size + 1L);
      BigArrays.set(this.a, this.size++, k);

      assert this.size <= BigArrays.length(this.a);

      return true;
   }

   @Override
   public K get(long index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         return BigArrays.get(this.a, index);
      }
   }

   @Override
   public long indexOf(Object k) {
      for (long i = 0L; i < this.size; i++) {
         if (k == BigArrays.get(this.a, i)) {
            return i;
         }
      }

      return -1L;
   }

   @Override
   public long lastIndexOf(Object k) {
      long i = this.size;

      while (i-- != 0L) {
         if (k == BigArrays.get(this.a, i)) {
            return i;
         }
      }

      return -1L;
   }

   @Override
   public K remove(long index) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         K old = BigArrays.get(this.a, index);
         this.size--;
         if (index != this.size) {
            BigArrays.copy(this.a, index + 1L, this.a, index, this.size - index);
         }

         BigArrays.set(this.a, this.size, null);

         assert this.size <= BigArrays.length(this.a);

         return old;
      }
   }

   @Override
   public boolean remove(Object k) {
      long index = this.indexOf(k);
      if (index == -1L) {
         return false;
      } else {
         this.remove(index);

         assert this.size <= BigArrays.length(this.a);

         return true;
      }
   }

   @Override
   public K set(long index, K k) {
      if (index >= this.size) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size + ")");
      } else {
         K old = BigArrays.get(this.a, index);
         BigArrays.set(this.a, index, k);
         return old;
      }
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      K[] s = null;
      K[] d = null;
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
      BigArrays.fill(this.a, j, this.size, null);
      boolean modified = this.size != j;
      this.size = j;
      return modified;
   }

   @Override
   public boolean addAll(long index, Collection<? extends K> c) {
      if (c instanceof ReferenceList) {
         return this.addAll(index, (ReferenceList<? extends K>)c);
      } else if (c instanceof ReferenceBigList) {
         return this.addAll(index, (ReferenceBigList<? extends K>)c);
      } else {
         this.ensureIndex(index);
         int n = c.size();
         if (n == 0) {
            return false;
         } else {
            this.grow(this.size + n);
            BigArrays.copy(this.a, index, this.a, index + n, this.size - index);
            Iterator<? extends K> i = c.iterator();
            this.size += n;

            assert this.size <= BigArrays.length(this.a);

            while (n-- != 0) {
               BigArrays.set(this.a, index++, (K)i.next());
            }

            return true;
         }
      }
   }

   @Override
   public boolean addAll(long index, ReferenceBigList<? extends K> list) {
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
   public boolean addAll(long index, ReferenceList<? extends K> list) {
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
      BigArrays.fill(this.a, 0L, this.size, null);
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
         this.a = (K[][])BigArrays.forceCapacity(this.a, size, this.size);
      }

      if (size > this.size) {
         BigArrays.fill(this.a, this.size, size, null);
      } else {
         BigArrays.fill(this.a, size, this.size, null);
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
         this.a = (K[][])BigArrays.trim(this.a, Math.max(n, this.size));

         assert this.size <= BigArrays.length(this.a);
      }
   }

   @Override
   public ReferenceBigList<K> subList(long from, long to) {
      if (from == 0L && to == this.size64()) {
         return this;
      } else {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new ReferenceBigArrayBigList.SubList(from, to);
         }
      }
   }

   @Override
   public void getElements(long from, Object[][] a, long offset, long length) {
      BigArrays.copy(this.a, from, a, offset, length);
   }

   @Override
   public void getElements(long from, Object[] a, int offset, int length) {
      BigArrays.copyFromBig(this.a, from, a, offset, length);
   }

   @Override
   public void removeElements(long from, long to) {
      BigArrays.ensureFromTo(this.size, from, to);
      BigArrays.copy(this.a, to, this.a, from, this.size - to);
      this.size -= to - from;
      BigArrays.fill(this.a, this.size, this.size + to - from, null);
   }

   @Override
   public void addElements(long index, K[][] a, long offset, long length) {
      this.ensureIndex(index);
      BigArrays.ensureOffsetLength(a, offset, length);
      this.grow(this.size + length);
      BigArrays.copy(this.a, index, this.a, index + length, this.size - index);
      BigArrays.copy(a, offset, this.a, index, length);
      this.size += length;
   }

   @Override
   public void setElements(long index, Object[][] a, long offset, long length) {
      BigArrays.copy(a, offset, this.a, index, length);
   }

   @Override
   public void forEach(Consumer<? super K> action) {
      for (long i = 0L; i < this.size; i++) {
         action.accept(BigArrays.get(this.a, i));
      }
   }

   @Override
   public ObjectBigListIterator<K> listIterator(final long index) {
      this.ensureIndex(index);
      return new ObjectBigListIterator<K>() {
         long pos = index;
         long last = -1L;

         @Override
         public boolean hasNext() {
            return this.pos < ReferenceBigArrayBigList.this.size;
         }

         @Override
         public boolean hasPrevious() {
            return this.pos > 0L;
         }

         @Override
         public K next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return BigArrays.get(ReferenceBigArrayBigList.this.a, this.last = this.pos++);
            }
         }

         @Override
         public K previous() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return BigArrays.get(ReferenceBigArrayBigList.this.a, this.last = --this.pos);
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
         public void add(K k) {
            ReferenceBigArrayBigList.this.add(this.pos++, k);
            this.last = -1L;
         }

         @Override
         public void set(K k) {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               ReferenceBigArrayBigList.this.set(this.last, k);
            }
         }

         @Override
         public void remove() {
            if (this.last == -1L) {
               throw new IllegalStateException();
            } else {
               ReferenceBigArrayBigList.this.remove(this.last);
               if (this.last < this.pos) {
                  this.pos--;
               }

               this.last = -1L;
            }
         }

         @Override
         public void forEachRemaining(Consumer<? super K> action) {
            while (this.pos < ReferenceBigArrayBigList.this.size) {
               action.accept(BigArrays.get(ReferenceBigArrayBigList.this.a, this.last = this.pos++));
            }
         }

         @Override
         public long back(long n) {
            if (n < 0L) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               long remaining = ReferenceBigArrayBigList.this.size - this.pos;
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
               long remaining = ReferenceBigArrayBigList.this.size - this.pos;
               if (n < remaining) {
                  this.pos += n;
               } else {
                  n = remaining;
                  this.pos = ReferenceBigArrayBigList.this.size;
               }

               this.last = this.pos - 1L;
               return n;
            }
         }
      };
   }

   @Override
   public ObjectSpliterator<K> spliterator() {
      return new ReferenceBigArrayBigList.Spliterator();
   }

   public ReferenceBigArrayBigList<K> clone() {
      ReferenceBigArrayBigList<K> c;
      if (this.getClass() == ReferenceBigArrayBigList.class) {
         c = new ReferenceBigArrayBigList<>(this.size);
         c.size = this.size;
      } else {
         try {
            c = (ReferenceBigArrayBigList<K>)super.clone();
         } catch (CloneNotSupportedException var3) {
            throw new InternalError(var3);
         }

         c.a = (K[][])ObjectBigArrays.newBigArray(this.size);
      }

      BigArrays.copy(this.a, 0L, c.a, 0L, this.size);
      return c;
   }

   public boolean equals(ReferenceBigArrayBigList<K> l) {
      if (l == this) {
         return true;
      } else {
         long s = this.size64();
         if (s != l.size64()) {
            return false;
         } else {
            K[][] a1 = this.a;
            K[][] a2 = l.a;
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
      } else if (o instanceof ReferenceBigArrayBigList) {
         return this.equals((ReferenceBigArrayBigList<K>)o);
      } else {
         return o instanceof ReferenceBigArrayBigList.SubList ? ((ReferenceBigArrayBigList.SubList)o).equals(this) : super.equals(o);
      }
   }

   private void writeObject(ObjectOutputStream s) throws IOException {
      s.defaultWriteObject();

      for (int i = 0; i < this.size; i++) {
         s.writeObject(BigArrays.get(this.a, (long)i));
      }
   }

   private void readObject(ObjectInputStream s) throws IOException, ClassNotFoundException {
      s.defaultReadObject();
      this.a = (K[][])ObjectBigArrays.newBigArray(this.size);

      for (int i = 0; i < this.size; i++) {
         BigArrays.set(this.a, i, s.readObject());
      }
   }

   private final class Spliterator implements ObjectSpliterator<K> {
      boolean hasSplit = false;
      long pos;
      long max;

      public Spliterator() {
         this(0L, ReferenceBigArrayBigList.this.size, false);
      }

      private Spliterator(long pos, long max, boolean hasSplit) {
         assert pos <= max : "pos " + pos + " must be <= max " + max;

         this.pos = pos;
         this.max = max;
         this.hasSplit = hasSplit;
      }

      private long getWorkingMax() {
         return this.hasSplit ? this.max : ReferenceBigArrayBigList.this.size;
      }

      @Override
      public int characteristics() {
         return 16464;
      }

      @Override
      public long estimateSize() {
         return this.getWorkingMax() - this.pos;
      }

      @Override
      public boolean tryAdvance(Consumer<? super K> action) {
         if (this.pos >= this.getWorkingMax()) {
            return false;
         } else {
            action.accept(BigArrays.get(ReferenceBigArrayBigList.this.a, this.pos++));
            return true;
         }
      }

      @Override
      public void forEachRemaining(Consumer<? super K> action) {
         for (long max = this.getWorkingMax(); this.pos < max; this.pos++) {
            action.accept(BigArrays.get(ReferenceBigArrayBigList.this.a, this.pos));
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
      public ObjectSpliterator<K> trySplit() {
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
            return ReferenceBigArrayBigList.this.new Spliterator(oldPos, myNewPos, true);
         }
      }
   }

   private class SubList extends AbstractReferenceBigList.ReferenceRandomAccessSubList<K> {
      private static final long serialVersionUID = -3185226345314976296L;

      protected SubList(long from, long to) {
         super(ReferenceBigArrayBigList.this, from, to);
      }

      private K[][] getParentArray() {
         return ReferenceBigArrayBigList.this.a;
      }

      @Override
      public K get(long i) {
         this.ensureRestrictedIndex(i);
         return BigArrays.get(ReferenceBigArrayBigList.this.a, i + this.from);
      }

      @Override
      public ObjectBigListIterator<K> listIterator(long index) {
         return new ReferenceBigArrayBigList.SubList.SubListIterator(index);
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return new ReferenceBigArrayBigList.SubList.SubListSpliterator();
      }

      boolean contentsEquals(K[][] otherA, long otherAFrom, long otherATo) {
         if (ReferenceBigArrayBigList.this.a == otherA && this.from == otherAFrom && this.to == otherATo) {
            return true;
         } else if (otherATo - otherAFrom != this.size64()) {
            return false;
         } else {
            long pos = this.to;
            long otherPos = otherATo;

            while (--pos >= this.from) {
               if (BigArrays.get(ReferenceBigArrayBigList.this.a, pos) != BigArrays.get(otherA, --otherPos)) {
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
         } else if (o instanceof ReferenceBigArrayBigList) {
            ReferenceBigArrayBigList<K> other = (ReferenceBigArrayBigList<K>)o;
            return this.contentsEquals(other.a, 0L, other.size64());
         } else if (o instanceof ReferenceBigArrayBigList.SubList) {
            ReferenceBigArrayBigList<K>.SubList other = (ReferenceBigArrayBigList.SubList)o;
            return this.contentsEquals(other.getParentArray(), other.from, other.to);
         } else {
            return super.equals(o);
         }
      }

      private final class SubListIterator extends ObjectBigListIterators.AbstractIndexBasedBigListIterator<K> {
         SubListIterator(long index) {
            super(0L, index);
         }

         @Override
         protected final K get(long i) {
            return BigArrays.get(ReferenceBigArrayBigList.this.a, SubList.this.from + i);
         }

         @Override
         protected final void add(long i, K k) {
            SubList.this.add(i, k);
         }

         @Override
         protected final void set(long i, K k) {
            SubList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            SubList.this.remove(i);
         }

         @Override
         protected final long getMaxPos() {
            return SubList.this.to - SubList.this.from;
         }

         @Override
         public K next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return BigArrays.get(ReferenceBigArrayBigList.this.a, SubList.this.from + (this.lastReturned = this.pos++));
            }
         }

         @Override
         public K previous() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return BigArrays.get(ReferenceBigArrayBigList.this.a, SubList.this.from + (this.lastReturned = --this.pos));
            }
         }

         @Override
         public void forEachRemaining(Consumer<? super K> action) {
            long max = SubList.this.to - SubList.this.from;

            while (this.pos < max) {
               action.accept(BigArrays.get(ReferenceBigArrayBigList.this.a, SubList.this.from + (this.lastReturned = this.pos++)));
            }
         }
      }

      private final class SubListSpliterator extends ObjectBigSpliterators.LateBindingSizeIndexBasedSpliterator<K> {
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
         protected final K get(long i) {
            return BigArrays.get(ReferenceBigArrayBigList.this.a, i);
         }

         protected final ReferenceBigArrayBigList<K>.SubList.SubListSpliterator makeForSplit(long pos, long maxPos) {
            return SubList.this.new SubListSpliterator(pos, maxPos);
         }

         @Override
         protected final long computeSplitPoint() {
            long defaultSplit = super.computeSplitPoint();
            return BigArrays.nearestSegmentStart(defaultSplit, this.pos + 1L, this.getMaxPos() - 1L);
         }

         @Override
         public boolean tryAdvance(Consumer<? super K> action) {
            if (this.pos >= this.getMaxPos()) {
               return false;
            } else {
               action.accept(BigArrays.get(ReferenceBigArrayBigList.this.a, this.pos++));
               return true;
            }
         }

         @Override
         public void forEachRemaining(Consumer<? super K> action) {
            long max = this.getMaxPos();

            while (this.pos < max) {
               action.accept(BigArrays.get(ReferenceBigArrayBigList.this.a, this.pos++));
            }
         }
      }
   }
}
