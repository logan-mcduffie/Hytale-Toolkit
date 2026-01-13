package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.HashCommon;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

public abstract class AbstractDoubleBigList extends AbstractDoubleCollection implements DoubleBigList, DoubleStack {
   protected AbstractDoubleBigList() {
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
   public void add(long index, double k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean add(double k) {
      this.add(this.size64(), k);
      return true;
   }

   @Override
   public double removeDouble(long i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public double set(long index, double k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(long index, Collection<? extends Double> c) {
      this.ensureIndex(index);
      Iterator<? extends Double> i = c.iterator();
      boolean retVal = i.hasNext();

      while (i.hasNext()) {
         this.add(index++, i.next());
      }

      return retVal;
   }

   @Override
   public boolean addAll(Collection<? extends Double> c) {
      return this.addAll(this.size64(), c);
   }

   @Override
   public DoubleBigListIterator iterator() {
      return this.listIterator();
   }

   @Override
   public DoubleBigListIterator listIterator() {
      return this.listIterator(0L);
   }

   @Override
   public DoubleBigListIterator listIterator(long index) {
      this.ensureIndex(index);
      return new DoubleBigListIterators.AbstractIndexBasedBigListIterator(0L, index) {
         @Override
         protected final double get(long i) {
            return AbstractDoubleBigList.this.getDouble(i);
         }

         @Override
         protected final void add(long i, double k) {
            AbstractDoubleBigList.this.add(i, k);
         }

         @Override
         protected final void set(long i, double k) {
            AbstractDoubleBigList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            AbstractDoubleBigList.this.removeDouble(i);
         }

         @Override
         protected final long getMaxPos() {
            return AbstractDoubleBigList.this.size64();
         }
      };
   }

   @Override
   public boolean contains(double k) {
      return this.indexOf(k) >= 0L;
   }

   @Override
   public long indexOf(double k) {
      DoubleBigListIterator i = this.listIterator();

      while (i.hasNext()) {
         double e = i.nextDouble();
         if (Double.doubleToLongBits(k) == Double.doubleToLongBits(e)) {
            return i.previousIndex();
         }
      }

      return -1L;
   }

   @Override
   public long lastIndexOf(double k) {
      DoubleBigListIterator i = this.listIterator(this.size64());

      while (i.hasPrevious()) {
         double e = i.previousDouble();
         if (Double.doubleToLongBits(k) == Double.doubleToLongBits(e)) {
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
            this.add(0.0);
         }
      } else {
         while (i-- != size) {
            this.remove(i);
         }
      }
   }

   @Override
   public DoubleBigList subList(long from, long to) {
      this.ensureIndex(from);
      this.ensureIndex(to);
      if (from > to) {
         throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         return (DoubleBigList)(this instanceof RandomAccess
            ? new AbstractDoubleBigList.DoubleRandomAccessSubList(this, from, to)
            : new AbstractDoubleBigList.DoubleSubList(this, from, to));
      }
   }

   @Override
   public void forEach(java.util.function.DoubleConsumer action) {
      if (this instanceof RandomAccess) {
         long i = 0L;

         for (long max = this.size64(); i < max; i++) {
            action.accept(this.getDouble(i));
         }
      } else {
         super.forEach(action);
      }
   }

   @Override
   public void removeElements(long from, long to) {
      this.ensureIndex(to);
      DoubleBigListIterator i = this.listIterator(from);
      long n = to - from;
      if (n < 0L) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         while (n-- != 0L) {
            i.nextDouble();
            i.remove();
         }
      }
   }

   @Override
   public void addElements(long index, double[][] a, long offset, long length) {
      this.ensureIndex(index);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (this instanceof RandomAccess) {
         while (length-- != 0L) {
            this.add(index++, BigArrays.get(a, offset++));
         }
      } else {
         DoubleBigListIterator iter = this.listIterator(index);

         while (length-- != 0L) {
            iter.add(BigArrays.get(a, offset++));
         }
      }
   }

   @Override
   public void addElements(long index, double[][] a) {
      this.addElements(index, a, 0L, BigArrays.length(a));
   }

   @Override
   public void getElements(long from, double[][] a, long offset, long length) {
      this.ensureIndex(from);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (from + length > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
      } else {
         if (this instanceof RandomAccess) {
            long current = from;

            while (length-- != 0L) {
               BigArrays.set(a, offset++, this.getDouble(current++));
            }
         } else {
            DoubleBigListIterator i = this.listIterator(from);

            while (length-- != 0L) {
               BigArrays.set(a, offset++, i.nextDouble());
            }
         }
      }
   }

   @Override
   public void setElements(long index, double[][] a, long offset, long length) {
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
            DoubleBigListIterator iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.nextDouble();
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
      DoubleIterator i = this.iterator();
      int h = 1;
      long s = this.size64();

      while (s-- != 0L) {
         double k = i.nextDouble();
         h = 31 * h + HashCommon.double2int(k);
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
         } else if (l instanceof DoubleBigList) {
            DoubleBigListIterator i1 = this.listIterator();
            DoubleBigListIterator i2 = ((DoubleBigList)l).listIterator();

            while (s-- != 0L) {
               if (i1.nextDouble() != i2.nextDouble()) {
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

   public int compareTo(BigList<? extends Double> l) {
      if (l == this) {
         return 0;
      } else if (l instanceof DoubleBigList) {
         DoubleBigListIterator i1 = this.listIterator();
         DoubleBigListIterator i2 = ((DoubleBigList)l).listIterator();

         while (i1.hasNext() && i2.hasNext()) {
            double e1 = i1.nextDouble();
            double e2 = i2.nextDouble();
            int r;
            if ((r = Double.compare(e1, e2)) != 0) {
               return r;
            }
         }

         return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
      } else {
         BigListIterator<? extends Double> i1 = this.listIterator();
         BigListIterator<? extends Double> i2 = l.listIterator();

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
   public void push(double o) {
      this.add(o);
   }

   @Override
   public double popDouble() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeDouble(this.size64() - 1L);
      }
   }

   @Override
   public double topDouble() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getDouble(this.size64() - 1L);
      }
   }

   @Override
   public double peekDouble(int i) {
      return this.getDouble(this.size64() - 1L - i);
   }

   @Override
   public boolean rem(double k) {
      long index = this.indexOf(k);
      if (index == -1L) {
         return false;
      } else {
         this.removeDouble(index);
         return true;
      }
   }

   @Override
   public boolean addAll(long index, DoubleCollection c) {
      return this.addAll(index, c);
   }

   @Override
   public boolean addAll(DoubleCollection c) {
      return this.addAll(this.size64(), c);
   }

   @Deprecated
   @Override
   public void add(long index, Double ok) {
      this.add(index, ok.doubleValue());
   }

   @Deprecated
   @Override
   public Double set(long index, Double ok) {
      return this.set(index, ok.doubleValue());
   }

   @Deprecated
   @Override
   public Double get(long index) {
      return this.getDouble(index);
   }

   @Deprecated
   @Override
   public long indexOf(Object ok) {
      return this.indexOf(((Double)ok).doubleValue());
   }

   @Deprecated
   @Override
   public long lastIndexOf(Object ok) {
      return this.lastIndexOf(((Double)ok).doubleValue());
   }

   @Deprecated
   @Override
   public Double remove(long index) {
      return this.removeDouble(index);
   }

   @Deprecated
   @Override
   public void push(Double o) {
      this.push(o.doubleValue());
   }

   @Deprecated
   @Override
   public Double pop() {
      return this.popDouble();
   }

   @Deprecated
   @Override
   public Double top() {
      return this.topDouble();
   }

   @Deprecated
   @Override
   public Double peek(int i) {
      return this.peekDouble(i);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      DoubleIterator i = this.iterator();
      long n = this.size64();
      boolean first = true;
      s.append("[");

      while (n-- != 0L) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         double k = i.nextDouble();
         s.append(String.valueOf(k));
      }

      s.append("]");
      return s.toString();
   }

   public static class DoubleRandomAccessSubList extends AbstractDoubleBigList.DoubleSubList implements RandomAccess {
      private static final long serialVersionUID = -107070782945191929L;

      public DoubleRandomAccessSubList(DoubleBigList l, long from, long to) {
         super(l, from, to);
      }

      @Override
      public DoubleBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractDoubleBigList.DoubleRandomAccessSubList(this, from, to);
         }
      }
   }

   public static class DoubleSubList extends AbstractDoubleBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final DoubleBigList l;
      protected final long from;
      protected long to;

      public DoubleSubList(DoubleBigList l, long from, long to) {
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
      public boolean add(double k) {
         this.l.add(this.to, k);
         this.to++;

         assert this.assertRange();

         return true;
      }

      @Override
      public void add(long index, double k) {
         this.ensureIndex(index);
         this.l.add(this.from + index, k);
         this.to++;

         assert this.assertRange();
      }

      @Override
      public boolean addAll(long index, Collection<? extends Double> c) {
         this.ensureIndex(index);
         this.to = this.to + c.size();
         return this.l.addAll(this.from + index, c);
      }

      @Override
      public double getDouble(long index) {
         this.ensureRestrictedIndex(index);
         return this.l.getDouble(this.from + index);
      }

      @Override
      public double removeDouble(long index) {
         this.ensureRestrictedIndex(index);
         this.to--;
         return this.l.removeDouble(this.from + index);
      }

      @Override
      public double set(long index, double k) {
         this.ensureRestrictedIndex(index);
         return this.l.set(this.from + index, k);
      }

      @Override
      public long size64() {
         return this.to - this.from;
      }

      @Override
      public void getElements(long from, double[][] a, long offset, long length) {
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
      public void addElements(long index, double[][] a, long offset, long length) {
         this.ensureIndex(index);
         this.l.addElements(this.from + index, a, offset, length);
         this.to += length;

         assert this.assertRange();
      }

      @Override
      public DoubleBigListIterator listIterator(long index) {
         this.ensureIndex(index);
         return (DoubleBigListIterator)(this.l instanceof RandomAccess
            ? new AbstractDoubleBigList.DoubleSubList.RandomAccessIter(index)
            : new AbstractDoubleBigList.DoubleSubList.ParentWrappingIter(this.l.listIterator(index + this.from)));
      }

      @Override
      public DoubleSpliterator spliterator() {
         return (DoubleSpliterator)(this.l instanceof RandomAccess
            ? new AbstractDoubleBigList.IndexBasedSpliterator(this.l, this.from, this.to)
            : super.spliterator());
      }

      @Override
      public DoubleBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractDoubleBigList.DoubleSubList(this, from, to);
         }
      }

      @Override
      public boolean rem(double k) {
         long index = this.indexOf(k);
         if (index == -1L) {
            return false;
         } else {
            this.to--;
            this.l.removeDouble(this.from + index);

            assert this.assertRange();

            return true;
         }
      }

      @Override
      public boolean addAll(long index, DoubleCollection c) {
         return super.addAll(index, c);
      }

      @Override
      public boolean addAll(long index, DoubleBigList l) {
         return super.addAll(index, l);
      }

      private class ParentWrappingIter implements DoubleBigListIterator {
         private DoubleBigListIterator parent;

         ParentWrappingIter(DoubleBigListIterator parent) {
            this.parent = parent;
         }

         @Override
         public long nextIndex() {
            return this.parent.nextIndex() - DoubleSubList.this.from;
         }

         @Override
         public long previousIndex() {
            return this.parent.previousIndex() - DoubleSubList.this.from;
         }

         @Override
         public boolean hasNext() {
            return this.parent.nextIndex() < DoubleSubList.this.to;
         }

         @Override
         public boolean hasPrevious() {
            return this.parent.previousIndex() >= DoubleSubList.this.from;
         }

         @Override
         public double nextDouble() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.nextDouble();
            }
         }

         @Override
         public double previousDouble() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.previousDouble();
            }
         }

         @Override
         public void add(double k) {
            this.parent.add(k);
         }

         @Override
         public void set(double k) {
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
               if (parentNewPos < DoubleSubList.this.from - 1L) {
                  parentNewPos = DoubleSubList.this.from - 1L;
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
               if (parentNewPos > DoubleSubList.this.to) {
                  parentNewPos = DoubleSubList.this.to;
               }

               long toSkip = parentNewPos - currentPos;
               return this.parent.skip(toSkip);
            }
         }
      }

      private final class RandomAccessIter extends DoubleBigListIterators.AbstractIndexBasedBigListIterator {
         RandomAccessIter(long pos) {
            super(0L, pos);
         }

         @Override
         protected final double get(long i) {
            return DoubleSubList.this.l.getDouble(DoubleSubList.this.from + i);
         }

         @Override
         protected final void add(long i, double k) {
            DoubleSubList.this.add(i, k);
         }

         @Override
         protected final void set(long i, double k) {
            DoubleSubList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            DoubleSubList.this.removeDouble(i);
         }

         @Override
         protected final long getMaxPos() {
            return DoubleSubList.this.to - DoubleSubList.this.from;
         }

         @Override
         public void add(double k) {
            super.add(k);

            assert DoubleSubList.this.assertRange();
         }

         @Override
         public void remove() {
            super.remove();

            assert DoubleSubList.this.assertRange();
         }
      }
   }

   static final class IndexBasedSpliterator extends DoubleBigSpliterators.LateBindingSizeIndexBasedSpliterator {
      final DoubleBigList l;

      IndexBasedSpliterator(DoubleBigList l, long pos) {
         super(pos);
         this.l = l;
      }

      IndexBasedSpliterator(DoubleBigList l, long pos, long maxPos) {
         super(pos, maxPos);
         this.l = l;
      }

      @Override
      protected final long getMaxPosFromBackingStore() {
         return this.l.size64();
      }

      @Override
      protected final double get(long i) {
         return this.l.getDouble(i);
      }

      protected final AbstractDoubleBigList.IndexBasedSpliterator makeForSplit(long pos, long maxPos) {
         return new AbstractDoubleBigList.IndexBasedSpliterator(this.l, pos, maxPos);
      }
   }
}
