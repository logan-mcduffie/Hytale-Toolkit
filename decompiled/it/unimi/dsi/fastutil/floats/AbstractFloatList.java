package it.unimi.dsi.fastutil.floats;

import it.unimi.dsi.fastutil.HashCommon;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

public abstract class AbstractFloatList extends AbstractFloatCollection implements FloatList, FloatStack {
   protected AbstractFloatList() {
   }

   protected void ensureIndex(int index) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index > this.size()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than list size (" + this.size() + ")");
      }
   }

   protected void ensureRestrictedIndex(int index) {
      if (index < 0) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is negative");
      } else if (index >= this.size()) {
         throw new IndexOutOfBoundsException("Index (" + index + ") is greater than or equal to list size (" + this.size() + ")");
      }
   }

   @Override
   public void add(int index, float k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean add(float k) {
      this.add(this.size(), k);
      return true;
   }

   @Override
   public float removeFloat(int i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public float set(int index, float k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(int index, Collection<? extends Float> c) {
      if (c instanceof FloatCollection) {
         return this.addAll(index, (FloatCollection)c);
      } else {
         this.ensureIndex(index);
         Iterator<? extends Float> i = c.iterator();
         boolean retVal = i.hasNext();

         while (i.hasNext()) {
            this.add(index++, i.next());
         }

         return retVal;
      }
   }

   @Override
   public boolean addAll(Collection<? extends Float> c) {
      return this.addAll(this.size(), c);
   }

   @Override
   public FloatListIterator iterator() {
      return this.listIterator();
   }

   @Override
   public FloatListIterator listIterator() {
      return this.listIterator(0);
   }

   @Override
   public FloatListIterator listIterator(int index) {
      this.ensureIndex(index);
      return new FloatIterators.AbstractIndexBasedListIterator(0, index) {
         @Override
         protected final float get(int i) {
            return AbstractFloatList.this.getFloat(i);
         }

         @Override
         protected final void add(int i, float k) {
            AbstractFloatList.this.add(i, k);
         }

         @Override
         protected final void set(int i, float k) {
            AbstractFloatList.this.set(i, k);
         }

         @Override
         protected final void remove(int i) {
            AbstractFloatList.this.removeFloat(i);
         }

         @Override
         protected final int getMaxPos() {
            return AbstractFloatList.this.size();
         }
      };
   }

   @Override
   public boolean contains(float k) {
      return this.indexOf(k) >= 0;
   }

   @Override
   public int indexOf(float k) {
      FloatListIterator i = this.listIterator();

      while (i.hasNext()) {
         float e = i.nextFloat();
         if (Float.floatToIntBits(k) == Float.floatToIntBits(e)) {
            return i.previousIndex();
         }
      }

      return -1;
   }

   @Override
   public int lastIndexOf(float k) {
      FloatListIterator i = this.listIterator(this.size());

      while (i.hasPrevious()) {
         float e = i.previousFloat();
         if (Float.floatToIntBits(k) == Float.floatToIntBits(e)) {
            return i.nextIndex();
         }
      }

      return -1;
   }

   @Override
   public void size(int size) {
      int i = this.size();
      if (size > i) {
         while (i++ < size) {
            this.add(0.0F);
         }
      } else {
         while (i-- != size) {
            this.removeFloat(i);
         }
      }
   }

   @Override
   public FloatList subList(int from, int to) {
      this.ensureIndex(from);
      this.ensureIndex(to);
      if (from > to) {
         throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         return (FloatList)(this instanceof RandomAccess
            ? new AbstractFloatList.FloatRandomAccessSubList(this, from, to)
            : new AbstractFloatList.FloatSubList(this, from, to));
      }
   }

   @Override
   public void forEach(FloatConsumer action) {
      if (this instanceof RandomAccess) {
         int i = 0;

         for (int max = this.size(); i < max; i++) {
            action.accept(this.getFloat(i));
         }
      } else {
         FloatList.super.forEach(action);
      }
   }

   @Override
   public void removeElements(int from, int to) {
      this.ensureIndex(to);
      FloatListIterator i = this.listIterator(from);
      int n = to - from;
      if (n < 0) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         while (n-- != 0) {
            i.nextFloat();
            i.remove();
         }
      }
   }

   @Override
   public void addElements(int index, float[] a, int offset, int length) {
      this.ensureIndex(index);
      FloatArrays.ensureOffsetLength(a, offset, length);
      if (this instanceof RandomAccess) {
         while (length-- != 0) {
            this.add(index++, a[offset++]);
         }
      } else {
         FloatListIterator iter = this.listIterator(index);

         while (length-- != 0) {
            iter.add(a[offset++]);
         }
      }
   }

   @Override
   public void addElements(int index, float[] a) {
      this.addElements(index, a, 0, a.length);
   }

   @Override
   public void getElements(int from, float[] a, int offset, int length) {
      this.ensureIndex(from);
      FloatArrays.ensureOffsetLength(a, offset, length);
      if (from + length > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
      } else {
         if (this instanceof RandomAccess) {
            int current = from;

            while (length-- != 0) {
               a[offset++] = this.getFloat(current++);
            }
         } else {
            FloatListIterator i = this.listIterator(from);

            while (length-- != 0) {
               a[offset++] = i.nextFloat();
            }
         }
      }
   }

   @Override
   public void setElements(int index, float[] a, int offset, int length) {
      this.ensureIndex(index);
      FloatArrays.ensureOffsetLength(a, offset, length);
      if (index + length > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
      } else {
         if (this instanceof RandomAccess) {
            for (int i = 0; i < length; i++) {
               this.set(i + index, a[i + offset]);
            }
         } else {
            FloatListIterator iter = this.listIterator(index);
            int i = 0;

            while (i < length) {
               iter.nextFloat();
               iter.set(a[offset + i++]);
            }
         }
      }
   }

   @Override
   public void clear() {
      this.removeElements(0, this.size());
   }

   @Override
   public int hashCode() {
      FloatIterator i = this.iterator();
      int h = 1;
      int s = this.size();

      while (s-- != 0) {
         float k = i.nextFloat();
         h = 31 * h + HashCommon.float2int(k);
      }

      return h;
   }

   @Override
   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof List)) {
         return false;
      } else {
         List<?> l = (List<?>)o;
         int s = this.size();
         if (s != l.size()) {
            return false;
         } else if (l instanceof FloatList) {
            FloatListIterator i1 = this.listIterator();
            FloatListIterator i2 = ((FloatList)l).listIterator();

            while (s-- != 0) {
               if (i1.nextFloat() != i2.nextFloat()) {
                  return false;
               }
            }

            return true;
         } else {
            ListIterator<?> i1 = this.listIterator();
            ListIterator<?> i2 = l.listIterator();

            while (s-- != 0) {
               if (!Objects.equals(i1.next(), i2.next())) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   public int compareTo(List<? extends Float> l) {
      if (l == this) {
         return 0;
      } else if (l instanceof FloatList) {
         FloatListIterator i1 = this.listIterator();
         FloatListIterator i2 = ((FloatList)l).listIterator();

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
         ListIterator<? extends Float> i1 = this.listIterator();
         ListIterator<? extends Float> i2 = l.listIterator();

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
         return this.removeFloat(this.size() - 1);
      }
   }

   @Override
   public float topFloat() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getFloat(this.size() - 1);
      }
   }

   @Override
   public float peekFloat(int i) {
      return this.getFloat(this.size() - 1 - i);
   }

   @Override
   public boolean rem(float k) {
      int index = this.indexOf(k);
      if (index == -1) {
         return false;
      } else {
         this.removeFloat(index);
         return true;
      }
   }

   @Override
   public float[] toFloatArray() {
      int size = this.size();
      if (size == 0) {
         return FloatArrays.EMPTY_ARRAY;
      } else {
         float[] ret = new float[size];
         this.getElements(0, ret, 0, size);
         return ret;
      }
   }

   @Override
   public float[] toArray(float[] a) {
      int size = this.size();
      if (a.length < size) {
         a = Arrays.copyOf(a, size);
      }

      this.getElements(0, a, 0, size);
      return a;
   }

   @Override
   public boolean addAll(int index, FloatCollection c) {
      this.ensureIndex(index);
      FloatIterator i = c.iterator();
      boolean retVal = i.hasNext();

      while (i.hasNext()) {
         this.add(index++, i.nextFloat());
      }

      return retVal;
   }

   @Override
   public boolean addAll(FloatCollection c) {
      return this.addAll(this.size(), c);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      FloatIterator i = this.iterator();
      int n = this.size();
      boolean first = true;
      s.append("[");

      while (n-- != 0) {
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

   public static class FloatRandomAccessSubList extends AbstractFloatList.FloatSubList implements RandomAccess {
      private static final long serialVersionUID = -107070782945191929L;

      public FloatRandomAccessSubList(FloatList l, int from, int to) {
         super(l, from, to);
      }

      @Override
      public FloatList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractFloatList.FloatRandomAccessSubList(this, from, to);
         }
      }
   }

   public static class FloatSubList extends AbstractFloatList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final FloatList l;
      protected final int from;
      protected int to;

      public FloatSubList(FloatList l, int from, int to) {
         this.l = l;
         this.from = from;
         this.to = to;
      }

      private boolean assertRange() {
         assert this.from <= this.l.size();

         assert this.to <= this.l.size();

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
      public void add(int index, float k) {
         this.ensureIndex(index);
         this.l.add(this.from + index, k);
         this.to++;

         assert this.assertRange();
      }

      @Override
      public boolean addAll(int index, Collection<? extends Float> c) {
         this.ensureIndex(index);
         this.to = this.to + c.size();
         return this.l.addAll(this.from + index, c);
      }

      @Override
      public float getFloat(int index) {
         this.ensureRestrictedIndex(index);
         return this.l.getFloat(this.from + index);
      }

      @Override
      public float removeFloat(int index) {
         this.ensureRestrictedIndex(index);
         this.to--;
         return this.l.removeFloat(this.from + index);
      }

      @Override
      public float set(int index, float k) {
         this.ensureRestrictedIndex(index);
         return this.l.set(this.from + index, k);
      }

      @Override
      public int size() {
         return this.to - this.from;
      }

      @Override
      public void getElements(int from, float[] a, int offset, int length) {
         this.ensureIndex(from);
         if (from + length > this.size()) {
            throw new IndexOutOfBoundsException("End index (" + from + length + ") is greater than list size (" + this.size() + ")");
         } else {
            this.l.getElements(this.from + from, a, offset, length);
         }
      }

      @Override
      public void removeElements(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         this.l.removeElements(this.from + from, this.from + to);
         this.to -= to - from;

         assert this.assertRange();
      }

      @Override
      public void addElements(int index, float[] a, int offset, int length) {
         this.ensureIndex(index);
         this.l.addElements(this.from + index, a, offset, length);
         this.to += length;

         assert this.assertRange();
      }

      @Override
      public void setElements(int index, float[] a, int offset, int length) {
         this.ensureIndex(index);
         this.l.setElements(this.from + index, a, offset, length);

         assert this.assertRange();
      }

      @Override
      public FloatListIterator listIterator(int index) {
         this.ensureIndex(index);
         return (FloatListIterator)(this.l instanceof RandomAccess
            ? new AbstractFloatList.FloatSubList.RandomAccessIter(index)
            : new AbstractFloatList.FloatSubList.ParentWrappingIter(this.l.listIterator(index + this.from)));
      }

      @Override
      public FloatSpliterator spliterator() {
         return (FloatSpliterator)(this.l instanceof RandomAccess
            ? new AbstractFloatList.IndexBasedSpliterator(this.l, this.from, this.to)
            : super.spliterator());
      }

      @Override
      public FloatList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractFloatList.FloatSubList(this, from, to);
         }
      }

      @Override
      public boolean rem(float k) {
         int index = this.indexOf(k);
         if (index == -1) {
            return false;
         } else {
            this.to--;
            this.l.removeFloat(this.from + index);

            assert this.assertRange();

            return true;
         }
      }

      @Override
      public boolean addAll(int index, FloatCollection c) {
         this.ensureIndex(index);
         return super.addAll(index, c);
      }

      @Override
      public boolean addAll(int index, FloatList l) {
         this.ensureIndex(index);
         return super.addAll(index, l);
      }

      private class ParentWrappingIter implements FloatListIterator {
         private FloatListIterator parent;

         ParentWrappingIter(FloatListIterator parent) {
            this.parent = parent;
         }

         @Override
         public int nextIndex() {
            return this.parent.nextIndex() - FloatSubList.this.from;
         }

         @Override
         public int previousIndex() {
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
         public int back(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int currentPos = this.parent.previousIndex();
               int parentNewPos = currentPos - n;
               if (parentNewPos < FloatSubList.this.from - 1) {
                  parentNewPos = FloatSubList.this.from - 1;
               }

               int toSkip = parentNewPos - currentPos;
               return this.parent.back(toSkip);
            }
         }

         @Override
         public int skip(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int currentPos = this.parent.nextIndex();
               int parentNewPos = currentPos + n;
               if (parentNewPos > FloatSubList.this.to) {
                  parentNewPos = FloatSubList.this.to;
               }

               int toSkip = parentNewPos - currentPos;
               return this.parent.skip(toSkip);
            }
         }
      }

      private final class RandomAccessIter extends FloatIterators.AbstractIndexBasedListIterator {
         RandomAccessIter(int pos) {
            super(0, pos);
         }

         @Override
         protected final float get(int i) {
            return FloatSubList.this.l.getFloat(FloatSubList.this.from + i);
         }

         @Override
         protected final void add(int i, float k) {
            FloatSubList.this.add(i, k);
         }

         @Override
         protected final void set(int i, float k) {
            FloatSubList.this.set(i, k);
         }

         @Override
         protected final void remove(int i) {
            FloatSubList.this.removeFloat(i);
         }

         @Override
         protected final int getMaxPos() {
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

   static final class IndexBasedSpliterator extends FloatSpliterators.LateBindingSizeIndexBasedSpliterator {
      final FloatList l;

      IndexBasedSpliterator(FloatList l, int pos) {
         super(pos);
         this.l = l;
      }

      IndexBasedSpliterator(FloatList l, int pos, int maxPos) {
         super(pos, maxPos);
         this.l = l;
      }

      @Override
      protected final int getMaxPosFromBackingStore() {
         return this.l.size();
      }

      @Override
      protected final float get(int i) {
         return this.l.getFloat(i);
      }

      protected final AbstractFloatList.IndexBasedSpliterator makeForSplit(int pos, int maxPos) {
         return new AbstractFloatList.IndexBasedSpliterator(this.l, pos, maxPos);
      }
   }
}
