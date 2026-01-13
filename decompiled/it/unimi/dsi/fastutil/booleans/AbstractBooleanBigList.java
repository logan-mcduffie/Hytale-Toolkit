package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

public abstract class AbstractBooleanBigList extends AbstractBooleanCollection implements BooleanBigList, BooleanStack {
   protected AbstractBooleanBigList() {
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
   public void add(long index, boolean k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean add(boolean k) {
      this.add(this.size64(), k);
      return true;
   }

   @Override
   public boolean removeBoolean(long i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean set(long index, boolean k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(long index, Collection<? extends Boolean> c) {
      this.ensureIndex(index);
      Iterator<? extends Boolean> i = c.iterator();
      boolean retVal = i.hasNext();

      while (i.hasNext()) {
         this.add(index++, i.next());
      }

      return retVal;
   }

   @Override
   public boolean addAll(Collection<? extends Boolean> c) {
      return this.addAll(this.size64(), c);
   }

   @Override
   public BooleanBigListIterator iterator() {
      return this.listIterator();
   }

   @Override
   public BooleanBigListIterator listIterator() {
      return this.listIterator(0L);
   }

   @Override
   public BooleanBigListIterator listIterator(long index) {
      this.ensureIndex(index);
      return new BooleanBigListIterators.AbstractIndexBasedBigListIterator(0L, index) {
         @Override
         protected final boolean get(long i) {
            return AbstractBooleanBigList.this.getBoolean(i);
         }

         @Override
         protected final void add(long i, boolean k) {
            AbstractBooleanBigList.this.add(i, k);
         }

         @Override
         protected final void set(long i, boolean k) {
            AbstractBooleanBigList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            AbstractBooleanBigList.this.removeBoolean(i);
         }

         @Override
         protected final long getMaxPos() {
            return AbstractBooleanBigList.this.size64();
         }
      };
   }

   @Override
   public boolean contains(boolean k) {
      return this.indexOf(k) >= 0L;
   }

   @Override
   public long indexOf(boolean k) {
      BooleanBigListIterator i = this.listIterator();

      while (i.hasNext()) {
         boolean e = i.nextBoolean();
         if (k == e) {
            return i.previousIndex();
         }
      }

      return -1L;
   }

   @Override
   public long lastIndexOf(boolean k) {
      BooleanBigListIterator i = this.listIterator(this.size64());

      while (i.hasPrevious()) {
         boolean e = i.previousBoolean();
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
            this.add(false);
         }
      } else {
         while (i-- != size) {
            this.remove(i);
         }
      }
   }

   @Override
   public BooleanBigList subList(long from, long to) {
      this.ensureIndex(from);
      this.ensureIndex(to);
      if (from > to) {
         throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         return (BooleanBigList)(this instanceof RandomAccess
            ? new AbstractBooleanBigList.BooleanRandomAccessSubList(this, from, to)
            : new AbstractBooleanBigList.BooleanSubList(this, from, to));
      }
   }

   @Override
   public void forEach(BooleanConsumer action) {
      if (this instanceof RandomAccess) {
         long i = 0L;

         for (long max = this.size64(); i < max; i++) {
            action.accept(this.getBoolean(i));
         }
      } else {
         super.forEach(action);
      }
   }

   @Override
   public void removeElements(long from, long to) {
      this.ensureIndex(to);
      BooleanBigListIterator i = this.listIterator(from);
      long n = to - from;
      if (n < 0L) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         while (n-- != 0L) {
            i.nextBoolean();
            i.remove();
         }
      }
   }

   @Override
   public void addElements(long index, boolean[][] a, long offset, long length) {
      this.ensureIndex(index);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (this instanceof RandomAccess) {
         while (length-- != 0L) {
            this.add(index++, BigArrays.get(a, offset++));
         }
      } else {
         BooleanBigListIterator iter = this.listIterator(index);

         while (length-- != 0L) {
            iter.add(BigArrays.get(a, offset++));
         }
      }
   }

   @Override
   public void addElements(long index, boolean[][] a) {
      this.addElements(index, a, 0L, BigArrays.length(a));
   }

   @Override
   public void getElements(long from, boolean[][] a, long offset, long length) {
      this.ensureIndex(from);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (from + length > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
      } else {
         if (this instanceof RandomAccess) {
            long current = from;

            while (length-- != 0L) {
               BigArrays.set(a, offset++, this.getBoolean(current++));
            }
         } else {
            BooleanBigListIterator i = this.listIterator(from);

            while (length-- != 0L) {
               BigArrays.set(a, offset++, i.nextBoolean());
            }
         }
      }
   }

   @Override
   public void setElements(long index, boolean[][] a, long offset, long length) {
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
            BooleanBigListIterator iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.nextBoolean();
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
      BooleanIterator i = this.iterator();
      int h = 1;
      long s = this.size64();

      while (s-- != 0L) {
         boolean k = i.nextBoolean();
         h = 31 * h + (k ? 1231 : 1237);
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
         } else if (l instanceof BooleanBigList) {
            BooleanBigListIterator i1 = this.listIterator();
            BooleanBigListIterator i2 = ((BooleanBigList)l).listIterator();

            while (s-- != 0L) {
               if (i1.nextBoolean() != i2.nextBoolean()) {
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

   public int compareTo(BigList<? extends Boolean> l) {
      if (l == this) {
         return 0;
      } else if (l instanceof BooleanBigList) {
         BooleanBigListIterator i1 = this.listIterator();
         BooleanBigListIterator i2 = ((BooleanBigList)l).listIterator();

         while (i1.hasNext() && i2.hasNext()) {
            boolean e1 = i1.nextBoolean();
            boolean e2 = i2.nextBoolean();
            int r;
            if ((r = Boolean.compare(e1, e2)) != 0) {
               return r;
            }
         }

         return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
      } else {
         BigListIterator<? extends Boolean> i1 = this.listIterator();
         BigListIterator<? extends Boolean> i2 = l.listIterator();

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
   public void push(boolean o) {
      this.add(o);
   }

   @Override
   public boolean popBoolean() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeBoolean(this.size64() - 1L);
      }
   }

   @Override
   public boolean topBoolean() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getBoolean(this.size64() - 1L);
      }
   }

   @Override
   public boolean peekBoolean(int i) {
      return this.getBoolean(this.size64() - 1L - i);
   }

   @Override
   public boolean rem(boolean k) {
      long index = this.indexOf(k);
      if (index == -1L) {
         return false;
      } else {
         this.removeBoolean(index);
         return true;
      }
   }

   @Override
   public boolean addAll(long index, BooleanCollection c) {
      return this.addAll(index, c);
   }

   @Override
   public boolean addAll(BooleanCollection c) {
      return this.addAll(this.size64(), c);
   }

   @Deprecated
   @Override
   public void add(long index, Boolean ok) {
      this.add(index, ok.booleanValue());
   }

   @Deprecated
   @Override
   public Boolean set(long index, Boolean ok) {
      return this.set(index, ok.booleanValue());
   }

   @Deprecated
   @Override
   public Boolean get(long index) {
      return this.getBoolean(index);
   }

   @Deprecated
   @Override
   public long indexOf(Object ok) {
      return this.indexOf(((Boolean)ok).booleanValue());
   }

   @Deprecated
   @Override
   public long lastIndexOf(Object ok) {
      return this.lastIndexOf(((Boolean)ok).booleanValue());
   }

   @Deprecated
   @Override
   public Boolean remove(long index) {
      return this.removeBoolean(index);
   }

   @Deprecated
   @Override
   public void push(Boolean o) {
      this.push(o.booleanValue());
   }

   @Deprecated
   @Override
   public Boolean pop() {
      return this.popBoolean();
   }

   @Deprecated
   @Override
   public Boolean top() {
      return this.topBoolean();
   }

   @Deprecated
   @Override
   public Boolean peek(int i) {
      return this.peekBoolean(i);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      BooleanIterator i = this.iterator();
      long n = this.size64();
      boolean first = true;
      s.append("[");

      while (n-- != 0L) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         boolean k = i.nextBoolean();
         s.append(String.valueOf(k));
      }

      s.append("]");
      return s.toString();
   }

   public static class BooleanRandomAccessSubList extends AbstractBooleanBigList.BooleanSubList implements RandomAccess {
      private static final long serialVersionUID = -107070782945191929L;

      public BooleanRandomAccessSubList(BooleanBigList l, long from, long to) {
         super(l, from, to);
      }

      @Override
      public BooleanBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractBooleanBigList.BooleanRandomAccessSubList(this, from, to);
         }
      }
   }

   public static class BooleanSubList extends AbstractBooleanBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final BooleanBigList l;
      protected final long from;
      protected long to;

      public BooleanSubList(BooleanBigList l, long from, long to) {
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
      public boolean add(boolean k) {
         this.l.add(this.to, k);
         this.to++;

         assert this.assertRange();

         return true;
      }

      @Override
      public void add(long index, boolean k) {
         this.ensureIndex(index);
         this.l.add(this.from + index, k);
         this.to++;

         assert this.assertRange();
      }

      @Override
      public boolean addAll(long index, Collection<? extends Boolean> c) {
         this.ensureIndex(index);
         this.to = this.to + c.size();
         return this.l.addAll(this.from + index, c);
      }

      @Override
      public boolean getBoolean(long index) {
         this.ensureRestrictedIndex(index);
         return this.l.getBoolean(this.from + index);
      }

      @Override
      public boolean removeBoolean(long index) {
         this.ensureRestrictedIndex(index);
         this.to--;
         return this.l.removeBoolean(this.from + index);
      }

      @Override
      public boolean set(long index, boolean k) {
         this.ensureRestrictedIndex(index);
         return this.l.set(this.from + index, k);
      }

      @Override
      public long size64() {
         return this.to - this.from;
      }

      @Override
      public void getElements(long from, boolean[][] a, long offset, long length) {
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
      public void addElements(long index, boolean[][] a, long offset, long length) {
         this.ensureIndex(index);
         this.l.addElements(this.from + index, a, offset, length);
         this.to += length;

         assert this.assertRange();
      }

      @Override
      public BooleanBigListIterator listIterator(long index) {
         this.ensureIndex(index);
         return (BooleanBigListIterator)(this.l instanceof RandomAccess
            ? new AbstractBooleanBigList.BooleanSubList.RandomAccessIter(index)
            : new AbstractBooleanBigList.BooleanSubList.ParentWrappingIter(this.l.listIterator(index + this.from)));
      }

      @Override
      public BooleanSpliterator spliterator() {
         return (BooleanSpliterator)(this.l instanceof RandomAccess
            ? new AbstractBooleanBigList.IndexBasedSpliterator(this.l, this.from, this.to)
            : super.spliterator());
      }

      @Override
      public BooleanBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractBooleanBigList.BooleanSubList(this, from, to);
         }
      }

      @Override
      public boolean rem(boolean k) {
         long index = this.indexOf(k);
         if (index == -1L) {
            return false;
         } else {
            this.to--;
            this.l.removeBoolean(this.from + index);

            assert this.assertRange();

            return true;
         }
      }

      @Override
      public boolean addAll(long index, BooleanCollection c) {
         return super.addAll(index, c);
      }

      @Override
      public boolean addAll(long index, BooleanBigList l) {
         return super.addAll(index, l);
      }

      private class ParentWrappingIter implements BooleanBigListIterator {
         private BooleanBigListIterator parent;

         ParentWrappingIter(BooleanBigListIterator parent) {
            this.parent = parent;
         }

         @Override
         public long nextIndex() {
            return this.parent.nextIndex() - BooleanSubList.this.from;
         }

         @Override
         public long previousIndex() {
            return this.parent.previousIndex() - BooleanSubList.this.from;
         }

         @Override
         public boolean hasNext() {
            return this.parent.nextIndex() < BooleanSubList.this.to;
         }

         @Override
         public boolean hasPrevious() {
            return this.parent.previousIndex() >= BooleanSubList.this.from;
         }

         @Override
         public boolean nextBoolean() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.nextBoolean();
            }
         }

         @Override
         public boolean previousBoolean() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.previousBoolean();
            }
         }

         @Override
         public void add(boolean k) {
            this.parent.add(k);
         }

         @Override
         public void set(boolean k) {
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
               if (parentNewPos < BooleanSubList.this.from - 1L) {
                  parentNewPos = BooleanSubList.this.from - 1L;
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
               if (parentNewPos > BooleanSubList.this.to) {
                  parentNewPos = BooleanSubList.this.to;
               }

               long toSkip = parentNewPos - currentPos;
               return this.parent.skip(toSkip);
            }
         }
      }

      private final class RandomAccessIter extends BooleanBigListIterators.AbstractIndexBasedBigListIterator {
         RandomAccessIter(long pos) {
            super(0L, pos);
         }

         @Override
         protected final boolean get(long i) {
            return BooleanSubList.this.l.getBoolean(BooleanSubList.this.from + i);
         }

         @Override
         protected final void add(long i, boolean k) {
            BooleanSubList.this.add(i, k);
         }

         @Override
         protected final void set(long i, boolean k) {
            BooleanSubList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            BooleanSubList.this.removeBoolean(i);
         }

         @Override
         protected final long getMaxPos() {
            return BooleanSubList.this.to - BooleanSubList.this.from;
         }

         @Override
         public void add(boolean k) {
            super.add(k);

            assert BooleanSubList.this.assertRange();
         }

         @Override
         public void remove() {
            super.remove();

            assert BooleanSubList.this.assertRange();
         }
      }
   }

   static final class IndexBasedSpliterator extends BooleanBigSpliterators.LateBindingSizeIndexBasedSpliterator {
      final BooleanBigList l;

      IndexBasedSpliterator(BooleanBigList l, long pos) {
         super(pos);
         this.l = l;
      }

      IndexBasedSpliterator(BooleanBigList l, long pos, long maxPos) {
         super(pos, maxPos);
         this.l = l;
      }

      @Override
      protected final long getMaxPosFromBackingStore() {
         return this.l.size64();
      }

      @Override
      protected final boolean get(long i) {
         return this.l.getBoolean(i);
      }

      protected final AbstractBooleanBigList.IndexBasedSpliterator makeForSplit(long pos, long maxPos) {
         return new AbstractBooleanBigList.IndexBasedSpliterator(this.l, pos, maxPos);
      }
   }
}
