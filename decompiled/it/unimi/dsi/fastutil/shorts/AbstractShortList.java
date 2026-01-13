package it.unimi.dsi.fastutil.shorts;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

public abstract class AbstractShortList extends AbstractShortCollection implements ShortList, ShortStack {
   protected AbstractShortList() {
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
   public void add(int index, short k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean add(short k) {
      this.add(this.size(), k);
      return true;
   }

   @Override
   public short removeShort(int i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public short set(int index, short k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(int index, Collection<? extends Short> c) {
      if (c instanceof ShortCollection) {
         return this.addAll(index, (ShortCollection)c);
      } else {
         this.ensureIndex(index);
         Iterator<? extends Short> i = c.iterator();
         boolean retVal = i.hasNext();

         while (i.hasNext()) {
            this.add(index++, i.next());
         }

         return retVal;
      }
   }

   @Override
   public boolean addAll(Collection<? extends Short> c) {
      return this.addAll(this.size(), c);
   }

   @Override
   public ShortListIterator iterator() {
      return this.listIterator();
   }

   @Override
   public ShortListIterator listIterator() {
      return this.listIterator(0);
   }

   @Override
   public ShortListIterator listIterator(int index) {
      this.ensureIndex(index);
      return new ShortIterators.AbstractIndexBasedListIterator(0, index) {
         @Override
         protected final short get(int i) {
            return AbstractShortList.this.getShort(i);
         }

         @Override
         protected final void add(int i, short k) {
            AbstractShortList.this.add(i, k);
         }

         @Override
         protected final void set(int i, short k) {
            AbstractShortList.this.set(i, k);
         }

         @Override
         protected final void remove(int i) {
            AbstractShortList.this.removeShort(i);
         }

         @Override
         protected final int getMaxPos() {
            return AbstractShortList.this.size();
         }
      };
   }

   @Override
   public boolean contains(short k) {
      return this.indexOf(k) >= 0;
   }

   @Override
   public int indexOf(short k) {
      ShortListIterator i = this.listIterator();

      while (i.hasNext()) {
         short e = i.nextShort();
         if (k == e) {
            return i.previousIndex();
         }
      }

      return -1;
   }

   @Override
   public int lastIndexOf(short k) {
      ShortListIterator i = this.listIterator(this.size());

      while (i.hasPrevious()) {
         short e = i.previousShort();
         if (k == e) {
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
            this.add((short)0);
         }
      } else {
         while (i-- != size) {
            this.removeShort(i);
         }
      }
   }

   @Override
   public ShortList subList(int from, int to) {
      this.ensureIndex(from);
      this.ensureIndex(to);
      if (from > to) {
         throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         return (ShortList)(this instanceof RandomAccess
            ? new AbstractShortList.ShortRandomAccessSubList(this, from, to)
            : new AbstractShortList.ShortSubList(this, from, to));
      }
   }

   @Override
   public void forEach(ShortConsumer action) {
      if (this instanceof RandomAccess) {
         int i = 0;

         for (int max = this.size(); i < max; i++) {
            action.accept(this.getShort(i));
         }
      } else {
         ShortList.super.forEach(action);
      }
   }

   @Override
   public void removeElements(int from, int to) {
      this.ensureIndex(to);
      ShortListIterator i = this.listIterator(from);
      int n = to - from;
      if (n < 0) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         while (n-- != 0) {
            i.nextShort();
            i.remove();
         }
      }
   }

   @Override
   public void addElements(int index, short[] a, int offset, int length) {
      this.ensureIndex(index);
      ShortArrays.ensureOffsetLength(a, offset, length);
      if (this instanceof RandomAccess) {
         while (length-- != 0) {
            this.add(index++, a[offset++]);
         }
      } else {
         ShortListIterator iter = this.listIterator(index);

         while (length-- != 0) {
            iter.add(a[offset++]);
         }
      }
   }

   @Override
   public void addElements(int index, short[] a) {
      this.addElements(index, a, 0, a.length);
   }

   @Override
   public void getElements(int from, short[] a, int offset, int length) {
      this.ensureIndex(from);
      ShortArrays.ensureOffsetLength(a, offset, length);
      if (from + length > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
      } else {
         if (this instanceof RandomAccess) {
            int current = from;

            while (length-- != 0) {
               a[offset++] = this.getShort(current++);
            }
         } else {
            ShortListIterator i = this.listIterator(from);

            while (length-- != 0) {
               a[offset++] = i.nextShort();
            }
         }
      }
   }

   @Override
   public void setElements(int index, short[] a, int offset, int length) {
      this.ensureIndex(index);
      ShortArrays.ensureOffsetLength(a, offset, length);
      if (index + length > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
      } else {
         if (this instanceof RandomAccess) {
            for (int i = 0; i < length; i++) {
               this.set(i + index, a[i + offset]);
            }
         } else {
            ShortListIterator iter = this.listIterator(index);
            int i = 0;

            while (i < length) {
               iter.nextShort();
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
      ShortIterator i = this.iterator();
      int h = 1;
      int s = this.size();

      while (s-- != 0) {
         short k = i.nextShort();
         h = 31 * h + k;
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
         } else if (l instanceof ShortList) {
            ShortListIterator i1 = this.listIterator();
            ShortListIterator i2 = ((ShortList)l).listIterator();

            while (s-- != 0) {
               if (i1.nextShort() != i2.nextShort()) {
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

   public int compareTo(List<? extends Short> l) {
      if (l == this) {
         return 0;
      } else if (l instanceof ShortList) {
         ShortListIterator i1 = this.listIterator();
         ShortListIterator i2 = ((ShortList)l).listIterator();

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
         ListIterator<? extends Short> i1 = this.listIterator();
         ListIterator<? extends Short> i2 = l.listIterator();

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
         return this.removeShort(this.size() - 1);
      }
   }

   @Override
   public short topShort() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getShort(this.size() - 1);
      }
   }

   @Override
   public short peekShort(int i) {
      return this.getShort(this.size() - 1 - i);
   }

   @Override
   public boolean rem(short k) {
      int index = this.indexOf(k);
      if (index == -1) {
         return false;
      } else {
         this.removeShort(index);
         return true;
      }
   }

   @Override
   public short[] toShortArray() {
      int size = this.size();
      if (size == 0) {
         return ShortArrays.EMPTY_ARRAY;
      } else {
         short[] ret = new short[size];
         this.getElements(0, ret, 0, size);
         return ret;
      }
   }

   @Override
   public short[] toArray(short[] a) {
      int size = this.size();
      if (a.length < size) {
         a = Arrays.copyOf(a, size);
      }

      this.getElements(0, a, 0, size);
      return a;
   }

   @Override
   public boolean addAll(int index, ShortCollection c) {
      this.ensureIndex(index);
      ShortIterator i = c.iterator();
      boolean retVal = i.hasNext();

      while (i.hasNext()) {
         this.add(index++, i.nextShort());
      }

      return retVal;
   }

   @Override
   public boolean addAll(ShortCollection c) {
      return this.addAll(this.size(), c);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ShortIterator i = this.iterator();
      int n = this.size();
      boolean first = true;
      s.append("[");

      while (n-- != 0) {
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

   static final class IndexBasedSpliterator extends ShortSpliterators.LateBindingSizeIndexBasedSpliterator {
      final ShortList l;

      IndexBasedSpliterator(ShortList l, int pos) {
         super(pos);
         this.l = l;
      }

      IndexBasedSpliterator(ShortList l, int pos, int maxPos) {
         super(pos, maxPos);
         this.l = l;
      }

      @Override
      protected final int getMaxPosFromBackingStore() {
         return this.l.size();
      }

      @Override
      protected final short get(int i) {
         return this.l.getShort(i);
      }

      protected final AbstractShortList.IndexBasedSpliterator makeForSplit(int pos, int maxPos) {
         return new AbstractShortList.IndexBasedSpliterator(this.l, pos, maxPos);
      }
   }

   public static class ShortRandomAccessSubList extends AbstractShortList.ShortSubList implements RandomAccess {
      private static final long serialVersionUID = -107070782945191929L;

      public ShortRandomAccessSubList(ShortList l, int from, int to) {
         super(l, from, to);
      }

      @Override
      public ShortList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractShortList.ShortRandomAccessSubList(this, from, to);
         }
      }
   }

   public static class ShortSubList extends AbstractShortList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ShortList l;
      protected final int from;
      protected int to;

      public ShortSubList(ShortList l, int from, int to) {
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
      public boolean add(short k) {
         this.l.add(this.to, k);
         this.to++;

         assert this.assertRange();

         return true;
      }

      @Override
      public void add(int index, short k) {
         this.ensureIndex(index);
         this.l.add(this.from + index, k);
         this.to++;

         assert this.assertRange();
      }

      @Override
      public boolean addAll(int index, Collection<? extends Short> c) {
         this.ensureIndex(index);
         this.to = this.to + c.size();
         return this.l.addAll(this.from + index, c);
      }

      @Override
      public short getShort(int index) {
         this.ensureRestrictedIndex(index);
         return this.l.getShort(this.from + index);
      }

      @Override
      public short removeShort(int index) {
         this.ensureRestrictedIndex(index);
         this.to--;
         return this.l.removeShort(this.from + index);
      }

      @Override
      public short set(int index, short k) {
         this.ensureRestrictedIndex(index);
         return this.l.set(this.from + index, k);
      }

      @Override
      public int size() {
         return this.to - this.from;
      }

      @Override
      public void getElements(int from, short[] a, int offset, int length) {
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
      public void addElements(int index, short[] a, int offset, int length) {
         this.ensureIndex(index);
         this.l.addElements(this.from + index, a, offset, length);
         this.to += length;

         assert this.assertRange();
      }

      @Override
      public void setElements(int index, short[] a, int offset, int length) {
         this.ensureIndex(index);
         this.l.setElements(this.from + index, a, offset, length);

         assert this.assertRange();
      }

      @Override
      public ShortListIterator listIterator(int index) {
         this.ensureIndex(index);
         return (ShortListIterator)(this.l instanceof RandomAccess
            ? new AbstractShortList.ShortSubList.RandomAccessIter(index)
            : new AbstractShortList.ShortSubList.ParentWrappingIter(this.l.listIterator(index + this.from)));
      }

      @Override
      public ShortSpliterator spliterator() {
         return (ShortSpliterator)(this.l instanceof RandomAccess
            ? new AbstractShortList.IndexBasedSpliterator(this.l, this.from, this.to)
            : super.spliterator());
      }

      @Override
      public ShortList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractShortList.ShortSubList(this, from, to);
         }
      }

      @Override
      public boolean rem(short k) {
         int index = this.indexOf(k);
         if (index == -1) {
            return false;
         } else {
            this.to--;
            this.l.removeShort(this.from + index);

            assert this.assertRange();

            return true;
         }
      }

      @Override
      public boolean addAll(int index, ShortCollection c) {
         this.ensureIndex(index);
         return super.addAll(index, c);
      }

      @Override
      public boolean addAll(int index, ShortList l) {
         this.ensureIndex(index);
         return super.addAll(index, l);
      }

      private class ParentWrappingIter implements ShortListIterator {
         private ShortListIterator parent;

         ParentWrappingIter(ShortListIterator parent) {
            this.parent = parent;
         }

         @Override
         public int nextIndex() {
            return this.parent.nextIndex() - ShortSubList.this.from;
         }

         @Override
         public int previousIndex() {
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
         public int back(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int currentPos = this.parent.previousIndex();
               int parentNewPos = currentPos - n;
               if (parentNewPos < ShortSubList.this.from - 1) {
                  parentNewPos = ShortSubList.this.from - 1;
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
               if (parentNewPos > ShortSubList.this.to) {
                  parentNewPos = ShortSubList.this.to;
               }

               int toSkip = parentNewPos - currentPos;
               return this.parent.skip(toSkip);
            }
         }
      }

      private final class RandomAccessIter extends ShortIterators.AbstractIndexBasedListIterator {
         RandomAccessIter(int pos) {
            super(0, pos);
         }

         @Override
         protected final short get(int i) {
            return ShortSubList.this.l.getShort(ShortSubList.this.from + i);
         }

         @Override
         protected final void add(int i, short k) {
            ShortSubList.this.add(i, k);
         }

         @Override
         protected final void set(int i, short k) {
            ShortSubList.this.set(i, k);
         }

         @Override
         protected final void remove(int i) {
            ShortSubList.this.removeShort(i);
         }

         @Override
         protected final int getMaxPos() {
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
