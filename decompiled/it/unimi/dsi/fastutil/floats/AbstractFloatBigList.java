package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.HashCommon;
import it.unimi.dsi.fastutil.doubles.DoubleSpliterator;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

public abstract class AbstractFloatBigList extends AbstractFloatCollection implements FloatBigList, FloatStack {
   protected AbstractFloatBigList() {
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
   public void add(long index, float k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean add(float k) {
      this.add(this.size64(), k);
      return true;
   }

   @Override
   public float removeFloat(long i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float set(long index, float k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(long index, Collection<? extends Float> c) {
      this.ensureIndex(index);
      Iterator<? extends Float> i = c.iterator();
      boolean retVal = i.hasNext();

      while (i.hasNext()) {
         this.add(index++, i.next());
      }

      return retVal;
   }

   @Override
   public boolean addAll(Collection<? extends Float> c) {
      return this.addAll(this.size64(), c);
   }

   @Override
   public FloatBigListIterator iterator() {
      return this.listIterator();
   }

   @Override
   public FloatBigListIterator listIterator() {
      return this.listIterator(0L);
   }

   @Override
   public FloatBigListIterator listIterator(long index) {
      this.ensureIndex(index);
      return new FloatBigListIterators.AbstractIndexBasedBigListIterator(0L, index) {
         @Override
         protected final float get(long i) {
            return AbstractFloatBigList.this.getFloat(i);
         }

         @Override
         protected final void add(long i, float k) {
            AbstractFloatBigList.this.add(i, k);
         }

         @Override
         protected final void set(long i, float k) {
            AbstractFloatBigList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            AbstractFloatBigList.this.removeFloat(i);
         }

         @Override
         protected final long getMaxPos() {
            return AbstractFloatBigList.this.size64();
         }
      };
   }

   @Override
   public DoubleSpliterator doubleSpliterator() {
      return this instanceof RandomAccess ? FloatSpliterators.widen(this.spliterator()) : super.doubleSpliterator();
   }

   @Override
   public boolean contains(float k) {
      return this.indexOf(k) >= 0L;
   }

   @Override
   public long indexOf(float k) {
      FloatBigListIterator i = this.listIterator();

      while (i.hasNext()) {
         float e = i.nextFloat();
         if (Float.floatToIntBits(k) == Float.floatToIntBits(e)) {
            return i.previousIndex();
         }
      }

      return -1L;
   }

   @Override
   public long lastIndexOf(float k) {
      FloatBigListIterator i = this.listIterator(this.size64());

      while (i.hasPrevious()) {
         float e = i.previousFloat();
         if (Float.floatToIntBits(k) == Float.floatToIntBits(e)) {
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
            this.add(0.0F);
         }
      } else {
         while (i-- != size) {
            this.remove(i);
         }
      }
   }

   @Override
   public FloatBigList subList(long from, long to) {
      this.ensureIndex(from);
      this.ensureIndex(to);
      if (from > to) {
         throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         return (FloatBigList)(this instanceof RandomAccess
            ? new AbstractFloatBigList.FloatRandomAccessSubList(this, from, to)
            : new AbstractFloatBigList.FloatSubList(this, from, to));
      }
   }

   @Override
   public void forEach(FloatConsumer action) {
      if (this instanceof RandomAccess) {
         long i = 0L;

         for (long max = this.size64(); i < max; i++) {
            action.accept(this.getFloat(i));
         }
      } else {
         super.forEach(action);
      }
   }

   @Override
   public void removeElements(long from, long to) {
      this.ensureIndex(to);
      FloatBigListIterator i = this.listIterator(from);
      long n = to - from;
      if (n < 0L) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         while (n-- != 0L) {
            i.nextFloat();
            i.remove();
         }
      }
   }

   @Override
   public void addElements(long index, float[][] a, long offset, long length) {
      this.ensureIndex(index);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (this instanceof RandomAccess) {
         while (length-- != 0L) {
            this.add(index++, BigArrays.get(a, offset++));
         }
      } else {
         FloatBigListIterator iter = this.listIterator(index);

         while (length-- != 0L) {
            iter.add(BigArrays.get(a, offset++));
         }
      }
   }

   @Override
   public void addElements(long index, float[][] a) {
      this.addElements(index, a, 0L, BigArrays.length(a));
   }

   @Override
   public void getElements(long from, float[][] a, long offset, long length) {
      this.ensureIndex(from);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (from + length > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
      } else {
         if (this instanceof RandomAccess) {
            long current = from;

            while (length-- != 0L) {
               BigArrays.set(a, offset++, this.getFloat(current++));
            }
         } else {
            FloatBigListIterator i = this.listIterator(from);

            while (length-- != 0L) {
               BigArrays.set(a, offset++, i.nextFloat());
            }
         }
      }
   }

   @Override
   public void setElements(long index, float[][] a, long offset, long length) {
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
            FloatBigListIterator iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.nextFloat();
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
      FloatIterator i = this.iterator();
      int h = 1;
      long s = this.size64();

      while (s-- != 0L) {
         float k = i.nextFloat();
         h = 31 * h + HashCommon.float2int(k);
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
         } else if (l instanceof FloatBigList) {
            FloatBigListIterator i1 = this.listIterator();
            FloatBigListIterator i2 = ((FloatBigList)l).listIterator();

            while (s-- != 0L) {
               if (i1.nextFloat() != i2.nextFloat()) {
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

   public int compareTo(BigList<? extends Float> l) {
      if (l == this) {
         return 0;
      } else if (l instanceof FloatBigList) {
         FloatBigListIterator i1 = this.listIterator();
         FloatBigListIterator i2 = ((FloatBigList)l).listIterator();

         while (i1.hasNext() && i2.hasNext()) {
            float e1 = i1.nextFloat();
            float e2 = i2.nextFloat();
            int r;
            if ((r = Float.compare(e1, e2)) != 0) {
               return r;
            }
         }

         return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
      } else {
         BigListIterator<? extends Float> i1 = this.listIterator();
         BigListIterator<? extends Float> i2 = l.listIterator();

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
   public void push(float o) {
      this.add(o);
   }

   @Override
   public float popFloat() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeFloat(this.size64() - 1L);
      }
   }

   @Override
   public float topFloat() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getFloat(this.size64() - 1L);
      }
   }

   @Override
   public float peekFloat(int i) {
      return this.getFloat(this.size64() - 1L - i);
   }

   @Override
   public boolean rem(float k) {
      long index = this.indexOf(k);
      if (index == -1L) {
         return false;
      } else {
         this.removeFloat(index);
         return true;
      }
   }

   @Override
   public boolean addAll(long index, FloatCollection c) {
      return this.addAll(index, c);
   }

   @Override
   public boolean addAll(FloatCollection c) {
      return this.addAll(this.size64(), c);
   }

   @Deprecated
   @Override
   public void add(long index, Float ok) {
      this.add(index, ok.floatValue());
   }

   @Deprecated
   @Override
   public Float set(long index, Float ok) {
      return this.set(index, ok.floatValue());
   }

   @Deprecated
   @Override
   public Float get(long index) {
      return this.getFloat(index);
   }

   @Deprecated
   @Override
   public long indexOf(Object ok) {
      return this.indexOf(((Float)ok).floatValue());
   }

   @Deprecated
   @Override
   public long lastIndexOf(Object ok) {
      return this.lastIndexOf(((Float)ok).floatValue());
   }

   @Deprecated
   @Override
   public Float remove(long index) {
      return this.removeFloat(index);
   }

   @Deprecated
   @Override
   public void push(Float o) {
      this.push(o.floatValue());
   }

   @Deprecated
   @Override
   public Float pop() {
      return this.popFloat();
   }

   @Deprecated
   @Override
   public Float top() {
      return this.topFloat();
   }

   @Deprecated
   @Override
   public Float peek(int i) {
      return this.peekFloat(i);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      FloatIterator i = this.iterator();
      long n = this.size64();
      boolean first = true;
      s.append("[");

      while (n-- != 0L) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         float k = i.nextFloat();
         s.append(String.valueOf(k));
      }

      s.append("]");
      return s.toString();
   }

   public static class FloatRandomAccessSubList extends AbstractFloatBigList.FloatSubList implements RandomAccess {
      private static final long serialVersionUID = -107070782945191929L;

      public FloatRandomAccessSubList(FloatBigList l, long from, long to) {
         super(l, from, to);
      }

      @Override
      public FloatBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractFloatBigList.FloatRandomAccessSubList(this, from, to);
         }
      }
   }

   public static class FloatSubList extends AbstractFloatBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatBigList l;
      protected final long from;
      protected long to;

      public FloatSubList(FloatBigList l, long from, long to) {
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
      public boolean add(float k) {
         this.l.add(this.to, k);
         this.to++;

         assert this.assertRange();

         return true;
      }

      @Override
      public void add(long index, float k) {
         this.ensureIndex(index);
         this.l.add(this.from + index, k);
         this.to++;

         assert this.assertRange();
      }

      @Override
      public boolean addAll(long index, Collection<? extends Float> c) {
         this.ensureIndex(index);
         this.to = this.to + c.size();
         return this.l.addAll(this.from + index, c);
      }

      @Override
      public float getFloat(long index) {
         this.ensureRestrictedIndex(index);
         return this.l.getFloat(this.from + index);
      }

      @Override
      public float removeFloat(long index) {
         this.ensureRestrictedIndex(index);
         this.to--;
         return this.l.removeFloat(this.from + index);
      }

      @Override
      public float set(long index, float k) {
         this.ensureRestrictedIndex(index);
         return this.l.set(this.from + index, k);
      }

      @Override
      public long size64() {
         return this.to - this.from;
      }

      @Override
      public void getElements(long from, float[][] a, long offset, long length) {
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
      public void addElements(long index, float[][] a, long offset, long length) {
         this.ensureIndex(index);
         this.l.addElements(this.from + index, a, offset, length);
         this.to += length;

         assert this.assertRange();
      }

      @Override
      public FloatBigListIterator listIterator(long index) {
         this.ensureIndex(index);
         return (FloatBigListIterator)(this.l instanceof RandomAccess
            ? new AbstractFloatBigList.FloatSubList.RandomAccessIter(index)
            : new AbstractFloatBigList.FloatSubList.ParentWrappingIter(this.l.listIterator(index + this.from)));
      }

      @Override
      public FloatSpliterator spliterator() {
         return (FloatSpliterator)(this.l instanceof RandomAccess
            ? new AbstractFloatBigList.IndexBasedSpliterator(this.l, this.from, this.to)
            : super.spliterator());
      }

      @Override
      public DoubleSpliterator doubleSpliterator() {
         return this.l instanceof RandomAccess ? FloatSpliterators.widen(this.spliterator()) : super.doubleSpliterator();
      }

      @Override
      public FloatBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractFloatBigList.FloatSubList(this, from, to);
         }
      }

      @Override
      public boolean rem(float k) {
         long index = this.indexOf(k);
         if (index == -1L) {
            return false;
         } else {
            this.to--;
            this.l.removeFloat(this.from + index);

            assert this.assertRange();

            return true;
         }
      }

      @Override
      public boolean addAll(long index, FloatCollection c) {
         return super.addAll(index, c);
      }

      @Override
      public boolean addAll(long index, FloatBigList l) {
         return super.addAll(index, l);
      }

      private class ParentWrappingIter implements FloatBigListIterator {
         private FloatBigListIterator parent;

         ParentWrappingIter(FloatBigListIterator parent) {
            this.parent = parent;
         }

         @Override
         public long nextIndex() {
            return this.parent.nextIndex() - FloatSubList.this.from;
         }

         @Override
         public long previousIndex() {
            return this.parent.previousIndex() - FloatSubList.this.from;
         }

         @Override
         public boolean hasNext() {
            return this.parent.nextIndex() < FloatSubList.this.to;
         }

         @Override
         public boolean hasPrevious() {
            return this.parent.previousIndex() >= FloatSubList.this.from;
         }

         @Override
         public float nextFloat() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.nextFloat();
            }
         }

         @Override
         public float previousFloat() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.previousFloat();
            }
         }

         @Override
         public void add(float k) {
            this.parent.add(k);
         }

         @Override
         public void set(float k) {
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
               if (parentNewPos < FloatSubList.this.from - 1L) {
                  parentNewPos = FloatSubList.this.from - 1L;
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
               if (parentNewPos > FloatSubList.this.to) {
                  parentNewPos = FloatSubList.this.to;
               }

               long toSkip = parentNewPos - currentPos;
               return this.parent.skip(toSkip);
            }
         }
      }

      private final class RandomAccessIter extends FloatBigListIterators.AbstractIndexBasedBigListIterator {
         RandomAccessIter(long pos) {
            super(0L, pos);
         }

         @Override
         protected final float get(long i) {
            return FloatSubList.this.l.getFloat(FloatSubList.this.from + i);
         }

         @Override
         protected final void add(long i, float k) {
            FloatSubList.this.add(i, k);
         }

         @Override
         protected final void set(long i, float k) {
            FloatSubList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            FloatSubList.this.removeFloat(i);
         }

         @Override
         protected final long getMaxPos() {
            return FloatSubList.this.to - FloatSubList.this.from;
         }

         @Override
         public void add(float k) {
            super.add(k);

            assert FloatSubList.this.assertRange();
         }

         @Override
         public void remove() {
            super.remove();

            assert FloatSubList.this.assertRange();
         }
      }
   }

   static final class IndexBasedSpliterator extends FloatBigSpliterators.LateBindingSizeIndexBasedSpliterator {
      final FloatBigList l;

      IndexBasedSpliterator(FloatBigList l, long pos) {
         super(pos);
         this.l = l;
      }

      IndexBasedSpliterator(FloatBigList l, long pos, long maxPos) {
         super(pos, maxPos);
         this.l = l;
      }

      @Override
      protected final long getMaxPosFromBackingStore() {
         return this.l.size64();
      }

      @Override
      protected final float get(long i) {
         return this.l.getFloat(i);
      }

      protected final AbstractFloatBigList.IndexBasedSpliterator makeForSplit(long pos, long maxPos) {
         return new AbstractFloatBigList.IndexBasedSpliterator(this.l, pos, maxPos);
      }
   }
}
