package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

public abstract class AbstractShortBigList extends AbstractShortCollection implements ShortBigList, ShortStack {
   protected AbstractShortBigList() {
   }

   protected void ensureIndex(long index) {
      if (index < 0L) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size64()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size64() + ")");
      }
   }

   protected void ensureRestrictedIndex(long index) {
      if (index < 0L) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index >= this.size64()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size64() + ")");
      }
   }

   @Override
   public void add(long index, short k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean add(short k) {
      this.add(this.size64(), k);
      return true;
   }

   @Override
   public short removeShort(long i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short set(long index, short k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(long index, Collection<? extends Short> c) {
      this.ensureIndex(index);
      Iterator<? extends Short> i = c.iterator();
      boolean retVal = i.hasNext();

      while (i.hasNext()) {
         this.add(index++, i.next());
      }

      return retVal;
   }

   @Override
   public boolean addAll(Collection<? extends Short> c) {
      return this.addAll(this.size64(), c);
   }

   @Override
   public ShortBigListIterator iterator() {
      return this.listIterator();
   }

   @Override
   public ShortBigListIterator listIterator() {
      return this.listIterator(0L);
   }

   @Override
   public ShortBigListIterator listIterator(long index) {
      this.ensureIndex(index);
      return new ShortBigListIterators.AbstractIndexBasedBigListIterator(0L, index) {
         @Override
         protected final short get(long i) {
            return AbstractShortBigList.this.getShort(i);
         }

         @Override
         protected final void add(long i, short k) {
            AbstractShortBigList.this.add(i, k);
         }

         @Override
         protected final void set(long i, short k) {
            AbstractShortBigList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            AbstractShortBigList.this.removeShort(i);
         }

         @Override
         protected final long getMaxPos() {
            return AbstractShortBigList.this.size64();
         }
      };
   }

   @Override
   public IntSpliterator intSpliterator() {
      return this instanceof RandomAccess ? ShortSpliterators.widen(this.spliterator()) : super.intSpliterator();
   }

   @Override
   public boolean contains(short k) {
      return this.indexOf(k) >= 0L;
   }

   @Override
   public long indexOf(short k) {
      ShortBigListIterator i = this.listIterator();

      while (i.hasNext()) {
         short e = i.nextShort();
         if (k == e) {
            return i.previousIndex();
         }
      }

      return -1L;
   }

   @Override
   public long lastIndexOf(short k) {
      ShortBigListIterator i = this.listIterator(this.size64());

      while (i.hasPrevious()) {
         short e = i.previousShort();
         if (k == e) {
            return i.nextIndex();
         }
      }

      return -1L;
   }

   @Override
   public void size(long size) {
      long i = this.size64();
      if (size > i) {
         while (i++ < size) {
            this.add((short)0);
         }
      } else {
         while (i-- != size) {
            this.remove(i);
         }
      }
   }

   @Override
   public ShortBigList subList(long from, long to) {
      this.ensureIndex(from);
      this.ensureIndex(to);
      if (from > to) {
         throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         return (ShortBigList)(this instanceof RandomAccess
            ? new AbstractShortBigList.ShortRandomAccessSubList(this, from, to)
            : new AbstractShortBigList.ShortSubList(this, from, to));
      }
   }

   @Override
   public void forEach(ShortConsumer action) {
      if (this instanceof RandomAccess) {
         long i = 0L;

         for (long max = this.size64(); i < max; i++) {
            action.accept(this.getShort(i));
         }
      } else {
         super.forEach(action);
      }
   }

   @Override
   public void removeElements(long from, long to) {
      this.ensureIndex(to);
      ShortBigListIterator i = this.listIterator(from);
      long n = to - from;
      if (n < 0L) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         while (n-- != 0L) {
            i.nextShort();
            i.remove();
         }
      }
   }

   @Override
   public void addElements(long index, short[][] a, long offset, long length) {
      this.ensureIndex(index);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (this instanceof RandomAccess) {
         while (length-- != 0L) {
            this.add(index++, BigArrays.get(a, offset++));
         }
      } else {
         ShortBigListIterator iter = this.listIterator(index);

         while (length-- != 0L) {
            iter.add(BigArrays.get(a, offset++));
         }
      }
   }

   @Override
   public void addElements(long index, short[][] a) {
      this.addElements(index, a, 0L, BigArrays.length(a));
   }

   @Override
   public void getElements(long from, short[][] a, long offset, long length) {
      this.ensureIndex(from);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (from + length > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
      } else {
         if (this instanceof RandomAccess) {
            long current = from;

            while (length-- != 0L) {
               BigArrays.set(a, offset++, this.getShort(current++));
            }
         } else {
            ShortBigListIterator i = this.listIterator(from);

            while (length-- != 0L) {
               BigArrays.set(a, offset++, i.nextShort());
            }
         }
      }
   }

   @Override
   public void setElements(long index, short[][] a, long offset, long length) {
      this.ensureIndex(index);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (index + length > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size64() + ")");
      } else {
         if (this instanceof RandomAccess) {
            for (long i = 0L; i < length; i++) {
               this.set(i + index, BigArrays.get(a, i + offset));
            }
         } else {
            ShortBigListIterator iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.nextShort();
               iter.set(BigArrays.get(a, offset + i++));
            }
         }
      }
   }

   @Override
   public void clear() {
      this.removeElements(0L, this.size64());
   }

   @Deprecated
   @Override
   public int size() {
      return (int)Math.min(2147483647L, this.size64());
   }

   @Override
   public int hashCode() {
      ShortIterator i = this.iterator();
      int h = 1;
      long s = this.size64();

      while (s-- != 0L) {
         short k = i.nextShort();
         h = 31 * h + k;
      }

      return h;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof BigList)) {
         return false;
      } else {
         BigList<?> l = (BigList<?>)o;
         long s = this.size64();
         if (s != l.size64()) {
            return false;
         } else if (l instanceof ShortBigList) {
            ShortBigListIterator i1 = this.listIterator();
            ShortBigListIterator i2 = ((ShortBigList)l).listIterator();

            while (s-- != 0L) {
               if (i1.nextShort() != i2.nextShort()) {
                  return false;
               }
            }

            return true;
         } else {
            BigListIterator<?> i1 = this.listIterator();
            BigListIterator<?> i2 = l.listIterator();

            while (s-- != 0L) {
               if (!Objects.equals(i1.next(), i2.next())) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int compareTo(BigList<? extends Short> l) {
      if (l == this) {
         return 0;
      } else if (l instanceof ShortBigList) {
         ShortBigListIterator i1 = this.listIterator();
         ShortBigListIterator i2 = ((ShortBigList)l).listIterator();

         while (i1.hasNext() && i2.hasNext()) {
            short e1 = i1.nextShort();
            short e2 = i2.nextShort();
            int r;
            if ((r = Short.compare(e1, e2)) != 0) {
               return r;
            }
         }

         return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
      } else {
         BigListIterator<? extends Short> i1 = this.listIterator();
         BigListIterator<? extends Short> i2 = l.listIterator();

         while (i1.hasNext() && i2.hasNext()) {
            int r;
            if ((r = ((Comparable)i1.next()).compareTo(i2.next())) != 0) {
               return r;
            }
         }

         return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
      }
   }

   @Override
   public void push(short o) {
      this.add(o);
   }

   @Override
   public short popShort() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeShort(this.size64() - 1L);
      }
   }

   @Override
   public short topShort() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getShort(this.size64() - 1L);
      }
   }

   @Override
   public short peekShort(int i) {
      return this.getShort(this.size64() - 1L - i);
   }

   @Override
   public boolean rem(short k) {
      long index = this.indexOf(k);
      if (index == -1L) {
         return false;
      } else {
         this.removeShort(index);
         return true;
      }
   }

   @Override
   public boolean addAll(long index, ShortCollection c) {
      return this.addAll(index, c);
   }

   @Override
   public boolean addAll(ShortCollection c) {
      return this.addAll(this.size64(), c);
   }

   @Deprecated
   @Override
   public void add(long index, Short ok) {
      this.add(index, ok.shortValue());
   }

   @Deprecated
   @Override
   public Short set(long index, Short ok) {
      return this.set(index, ok.shortValue());
   }

   @Deprecated
   @Override
   public Short get(long index) {
      return this.getShort(index);
   }

   @Deprecated
   @Override
   public long indexOf(Object ok) {
      return this.indexOf(((Short)ok).shortValue());
   }

   @Deprecated
   @Override
   public long lastIndexOf(Object ok) {
      return this.lastIndexOf(((Short)ok).shortValue());
   }

   @Deprecated
   @Override
   public Short remove(long index) {
      return this.removeShort(index);
   }

   @Deprecated
   @Override
   public void push(Short o) {
      this.push(o.shortValue());
   }

   @Deprecated
   @Override
   public Short pop() {
      return this.popShort();
   }

   @Deprecated
   @Override
   public Short top() {
      return this.topShort();
   }

   @Deprecated
   @Override
   public Short peek(int i) {
      return this.peekShort(i);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ShortIterator i = this.iterator();
      long n = this.size64();
      boolean first = true;
      s.append("[");

      while (n-- != 0L) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         short k = i.nextShort();
         s.append(String.valueOf((int)k));
      }

      s.append("]");
      return s.toString();
   }

   static final class IndexBasedSpliterator extends ShortBigSpliterators.LateBindingSizeIndexBasedSpliterator {
      final ShortBigList l;

      IndexBasedSpliterator(ShortBigList l, long pos) {
         super(pos);
         this.l = l;
      }

      IndexBasedSpliterator(ShortBigList l, long pos, long maxPos) {
         super(pos, maxPos);
         this.l = l;
      }

      @Override
      protected final long getMaxPosFromBackingStore() {
         return this.l.size64();
      }

      @Override
      protected final short get(long i) {
         return this.l.getShort(i);
      }

      protected final AbstractShortBigList.IndexBasedSpliterator makeForSplit(long pos, long maxPos) {
         return new AbstractShortBigList.IndexBasedSpliterator(this.l, pos, maxPos);
      }
   }

   public static class ShortRandomAccessSubList extends AbstractShortBigList.ShortSubList implements RandomAccess {
      private static final long serialVersionUID = -107070782945191929L;

      public ShortRandomAccessSubList(ShortBigList l, long from, long to) {
         super(l, from, to);
      }

      @Override
      public ShortBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractShortBigList.ShortRandomAccessSubList(this, from, to);
         }
      }
   }

   public static class ShortSubList extends AbstractShortBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortBigList l;
      protected final long from;
      protected long to;

      public ShortSubList(ShortBigList l, long from, long to) {
         this.l = l;
         this.from = from;
         this.to = to;
      }

      private boolean assertRange() {
         assert this.from <= this.l.size64();

         assert this.to <= this.l.size64();

         assert this.to >= this.from;

         return true;
      }

      @Override
      public boolean add(short k) {
         this.l.add(this.to, k);
         this.to++;

         assert this.assertRange();

         return true;
      }

      @Override
      public void add(long index, short k) {
         this.ensureIndex(index);
         this.l.add(this.from + index, k);
         this.to++;

         assert this.assertRange();
      }

      @Override
      public boolean addAll(long index, Collection<? extends Short> c) {
         this.ensureIndex(index);
         this.to = this.to + c.size();
         return this.l.addAll(this.from + index, c);
      }

      @Override
      public short getShort(long index) {
         this.ensureRestrictedIndex(index);
         return this.l.getShort(this.from + index);
      }

      @Override
      public short removeShort(long index) {
         this.ensureRestrictedIndex(index);
         this.to--;
         return this.l.removeShort(this.from + index);
      }

      @Override
      public short set(long index, short k) {
         this.ensureRestrictedIndex(index);
         return this.l.set(this.from + index, k);
      }

      @Override
      public long size64() {
         return this.to - this.from;
      }

      @Override
      public void getElements(long from, short[][] a, long offset, long length) {
         this.ensureIndex(from);
         if (from + length > this.size64()) {
            throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + this.size64() + ")");
         } else {
            this.l.getElements(this.from + from, a, offset, length);
         }
      }

      @Override
      public void removeElements(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         this.l.removeElements(this.from + from, this.from + to);
         this.to -= to - from;

         assert this.assertRange();
      }

      @Override
      public void addElements(long index, short[][] a, long offset, long length) {
         this.ensureIndex(index);
         this.l.addElements(this.from + index, a, offset, length);
         this.to += length;

         assert this.assertRange();
      }

      @Override
      public ShortBigListIterator listIterator(long index) {
         this.ensureIndex(index);
         return (ShortBigListIterator)(this.l instanceof RandomAccess
            ? new AbstractShortBigList.ShortSubList.RandomAccessIter(index)
            : new AbstractShortBigList.ShortSubList.ParentWrappingIter(this.l.listIterator(index + this.from)));
      }

      @Override
      public ShortSpliterator spliterator() {
         return (ShortSpliterator)(this.l instanceof RandomAccess
            ? new AbstractShortBigList.IndexBasedSpliterator(this.l, this.from, this.to)
            : super.spliterator());
      }

      @Override
      public IntSpliterator intSpliterator() {
         return this.l instanceof RandomAccess ? ShortSpliterators.widen(this.spliterator()) : super.intSpliterator();
      }

      @Override
      public ShortBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractShortBigList.ShortSubList(this, from, to);
         }
      }

      @Override
      public boolean rem(short k) {
         long index = this.indexOf(k);
         if (index == -1L) {
            return false;
         } else {
            this.to--;
            this.l.removeShort(this.from + index);

            assert this.assertRange();

            return true;
         }
      }

      @Override
      public boolean addAll(long index, ShortCollection c) {
         return super.addAll(index, c);
      }

      @Override
      public boolean addAll(long index, ShortBigList l) {
         return super.addAll(index, l);
      }

      private class ParentWrappingIter implements ShortBigListIterator {
         private ShortBigListIterator parent;

         ParentWrappingIter(ShortBigListIterator parent) {
            this.parent = parent;
         }

         @Override
         public long nextIndex() {
            return this.parent.nextIndex() - ShortSubList.this.from;
         }

         @Override
         public long previousIndex() {
            return this.parent.previousIndex() - ShortSubList.this.from;
         }

         @Override
         public boolean hasNext() {
            return this.parent.nextIndex() < ShortSubList.this.to;
         }

         @Override
         public boolean hasPrevious() {
            return this.parent.previousIndex() >= ShortSubList.this.from;
         }

         @Override
         public short nextShort() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.nextShort();
            }
         }

         @Override
         public short previousShort() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.previousShort();
            }
         }

         @Override
         public void add(short k) {
            this.parent.add(k);
         }

         @Override
         public void set(short k) {
            this.parent.set(k);
         }

         @Override
         public void remove() {
            this.parent.remove();
         }

         @Override
         public long back(long n) {
            if (n < 0L) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               long currentPos = this.parent.previousIndex();
               long parentNewPos = currentPos - n;
               if (parentNewPos < ShortSubList.this.from - 1L) {
                  parentNewPos = ShortSubList.this.from - 1L;
               }

               long toSkip = parentNewPos - currentPos;
               return this.parent.back(toSkip);
            }
         }

         @Override
         public long skip(long n) {
            if (n < 0L) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               long currentPos = this.parent.nextIndex();
               long parentNewPos = currentPos + n;
               if (parentNewPos > ShortSubList.this.to) {
                  parentNewPos = ShortSubList.this.to;
               }

               long toSkip = parentNewPos - currentPos;
               return this.parent.skip(toSkip);
            }
         }
      }

      private final class RandomAccessIter extends ShortBigListIterators.AbstractIndexBasedBigListIterator {
         RandomAccessIter(long pos) {
            super(0L, pos);
         }

         @Override
         protected final short get(long i) {
            return ShortSubList.this.l.getShort(ShortSubList.this.from + i);
         }

         @Override
         protected final void add(long i, short k) {
            ShortSubList.this.add(i, k);
         }

         @Override
         protected final void set(long i, short k) {
            ShortSubList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            ShortSubList.this.removeShort(i);
         }

         @Override
         protected final long getMaxPos() {
            return ShortSubList.this.to - ShortSubList.this.from;
         }

         @Override
         public void add(short k) {
            super.add(k);

            assert ShortSubList.this.assertRange();
         }

         @Override
         public void remove() {
            super.remove();

            assert ShortSubList.this.assertRange();
         }
      }
   }
}
