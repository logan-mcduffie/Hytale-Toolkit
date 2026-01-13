package it.unimi.dsi.fastutil.booleans;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

public abstract class AbstractBooleanList extends AbstractBooleanCollection implements BooleanList, BooleanStack {
   protected AbstractBooleanList() {
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
   public void add(int index, boolean k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean add(boolean k) {
      this.add(this.size(), k);
      return true;
   }

   @Override
   public boolean removeBoolean(int i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean set(int index, boolean k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(int index, Collection<? extends Boolean> c) {
      if (c instanceof BooleanCollection) {
         return this.addAll(index, (BooleanCollection)c);
      } else {
         this.ensureIndex(index);
         Iterator<? extends Boolean> i = c.iterator();
         boolean retVal = i.hasNext();

         while (i.hasNext()) {
            this.add(index++, i.next());
         }

         return retVal;
      }
   }

   @Override
   public boolean addAll(Collection<? extends Boolean> c) {
      return this.addAll(this.size(), c);
   }

   @Override
   public BooleanListIterator iterator() {
      return this.listIterator();
   }

   @Override
   public BooleanListIterator listIterator() {
      return this.listIterator(0);
   }

   @Override
   public BooleanListIterator listIterator(int index) {
      this.ensureIndex(index);
      return new BooleanIterators.AbstractIndexBasedListIterator(0, index) {
         @Override
         protected final boolean get(int i) {
            return AbstractBooleanList.this.getBoolean(i);
         }

         @Override
         protected final void add(int i, boolean k) {
            AbstractBooleanList.this.add(i, k);
         }

         @Override
         protected final void set(int i, boolean k) {
            AbstractBooleanList.this.set(i, k);
         }

         @Override
         protected final void remove(int i) {
            AbstractBooleanList.this.removeBoolean(i);
         }

         @Override
         protected final int getMaxPos() {
            return AbstractBooleanList.this.size();
         }
      };
   }

   @Override
   public boolean contains(boolean k) {
      return this.indexOf(k) >= 0;
   }

   @Override
   public int indexOf(boolean k) {
      BooleanListIterator i = this.listIterator();

      while (i.hasNext()) {
         boolean e = i.nextBoolean();
         if (k == e) {
            return i.previousIndex();
         }
      }

      return -1;
   }

   @Override
   public int lastIndexOf(boolean k) {
      BooleanListIterator i = this.listIterator(this.size());

      while (i.hasPrevious()) {
         boolean e = i.previousBoolean();
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
            this.add(false);
         }
      } else {
         while (i-- != size) {
            this.removeBoolean(i);
         }
      }
   }

   @Override
   public BooleanList subList(int from, int to) {
      this.ensureIndex(from);
      this.ensureIndex(to);
      if (from > to) {
         throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         return (BooleanList)(this instanceof RandomAccess
            ? new AbstractBooleanList.BooleanRandomAccessSubList(this, from, to)
            : new AbstractBooleanList.BooleanSubList(this, from, to));
      }
   }

   @Override
   public void forEach(BooleanConsumer action) {
      if (this instanceof RandomAccess) {
         int i = 0;

         for (int max = this.size(); i < max; i++) {
            action.accept(this.getBoolean(i));
         }
      } else {
         BooleanList.super.forEach(action);
      }
   }

   @Override
   public void removeElements(int from, int to) {
      this.ensureIndex(to);
      BooleanListIterator i = this.listIterator(from);
      int n = to - from;
      if (n < 0) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         while (n-- != 0) {
            i.nextBoolean();
            i.remove();
         }
      }
   }

   @Override
   public void addElements(int index, boolean[] a, int offset, int length) {
      this.ensureIndex(index);
      BooleanArrays.ensureOffsetLength(a, offset, length);
      if (this instanceof RandomAccess) {
         while (length-- != 0) {
            this.add(index++, a[offset++]);
         }
      } else {
         BooleanListIterator iter = this.listIterator(index);

         while (length-- != 0) {
            iter.add(a[offset++]);
         }
      }
   }

   @Override
   public void addElements(int index, boolean[] a) {
      this.addElements(index, a, 0, a.length);
   }

   @Override
   public void getElements(int from, boolean[] a, int offset, int length) {
      this.ensureIndex(from);
      BooleanArrays.ensureOffsetLength(a, offset, length);
      if (from + length > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
      } else {
         if (this instanceof RandomAccess) {
            int current = from;

            while (length-- != 0) {
               a[offset++] = this.getBoolean(current++);
            }
         } else {
            BooleanListIterator i = this.listIterator(from);

            while (length-- != 0) {
               a[offset++] = i.nextBoolean();
            }
         }
      }
   }

   @Override
   public void setElements(int index, boolean[] a, int offset, int length) {
      this.ensureIndex(index);
      BooleanArrays.ensureOffsetLength(a, offset, length);
      if (index + length > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
      } else {
         if (this instanceof RandomAccess) {
            for (int i = 0; i < length; i++) {
               this.set(i + index, a[i + offset]);
            }
         } else {
            BooleanListIterator iter = this.listIterator(index);
            int i = 0;

            while (i < length) {
               iter.nextBoolean();
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
      BooleanIterator i = this.iterator();
      int h = 1;
      int s = this.size();

      while (s-- != 0) {
         boolean k = i.nextBoolean();
         h = 31 * h + (k ? 1231 : 1237);
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
         } else if (l instanceof BooleanList) {
            BooleanListIterator i1 = this.listIterator();
            BooleanListIterator i2 = ((BooleanList)l).listIterator();

            while (s-- != 0) {
               if (i1.nextBoolean() != i2.nextBoolean()) {
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

   public int compareTo(List<? extends Boolean> l) {
      if (l == this) {
         return 0;
      } else if (l instanceof BooleanList) {
         BooleanListIterator i1 = this.listIterator();
         BooleanListIterator i2 = ((BooleanList)l).listIterator();

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
         ListIterator<? extends Boolean> i1 = this.listIterator();
         ListIterator<? extends Boolean> i2 = l.listIterator();

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
         return this.removeBoolean(this.size() - 1);
      }
   }

   @Override
   public boolean topBoolean() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getBoolean(this.size() - 1);
      }
   }

   @Override
   public boolean peekBoolean(int i) {
      return this.getBoolean(this.size() - 1 - i);
   }

   @Override
   public boolean rem(boolean k) {
      int index = this.indexOf(k);
      if (index == -1) {
         return false;
      } else {
         this.removeBoolean(index);
         return true;
      }
   }

   @Override
   public boolean[] toBooleanArray() {
      int size = this.size();
      if (size == 0) {
         return BooleanArrays.EMPTY_ARRAY;
      } else {
         boolean[] ret = new boolean[size];
         this.getElements(0, ret, 0, size);
         return ret;
      }
   }

   @Override
   public boolean[] toArray(boolean[] a) {
      int size = this.size();
      if (a.length < size) {
         a = Arrays.copyOf(a, size);
      }

      this.getElements(0, a, 0, size);
      return a;
   }

   @Override
   public boolean addAll(int index, BooleanCollection c) {
      this.ensureIndex(index);
      BooleanIterator i = c.iterator();
      boolean retVal = i.hasNext();

      while (i.hasNext()) {
         this.add(index++, i.nextBoolean());
      }

      return retVal;
   }

   @Override
   public boolean addAll(BooleanCollection c) {
      return this.addAll(this.size(), c);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      BooleanIterator i = this.iterator();
      int n = this.size();
      boolean first = true;
      s.append("[");

      while (n-- != 0) {
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

   public static class BooleanRandomAccessSubList extends AbstractBooleanList.BooleanSubList implements RandomAccess {
      private static final long serialVersionUID = -107070782945191929L;

      public BooleanRandomAccessSubList(BooleanList l, int from, int to) {
         super(l, from, to);
      }

      @Override
      public BooleanList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractBooleanList.BooleanRandomAccessSubList(this, from, to);
         }
      }
   }

   public static class BooleanSubList extends AbstractBooleanList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final BooleanList l;
      protected final int from;
      protected int to;

      public BooleanSubList(BooleanList l, int from, int to) {
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
      public boolean add(boolean k) {
         this.l.add(this.to, k);
         this.to++;

         assert this.assertRange();

         return true;
      }

      @Override
      public void add(int index, boolean k) {
         this.ensureIndex(index);
         this.l.add(this.from + index, k);
         this.to++;

         assert this.assertRange();
      }

      @Override
      public boolean addAll(int index, Collection<? extends Boolean> c) {
         this.ensureIndex(index);
         this.to = this.to + c.size();
         return this.l.addAll(this.from + index, c);
      }

      @Override
      public boolean getBoolean(int index) {
         this.ensureRestrictedIndex(index);
         return this.l.getBoolean(this.from + index);
      }

      @Override
      public boolean removeBoolean(int index) {
         this.ensureRestrictedIndex(index);
         this.to--;
         return this.l.removeBoolean(this.from + index);
      }

      @Override
      public boolean set(int index, boolean k) {
         this.ensureRestrictedIndex(index);
         return this.l.set(this.from + index, k);
      }

      @Override
      public int size() {
         return this.to - this.from;
      }

      @Override
      public void getElements(int from, boolean[] a, int offset, int length) {
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
      public void addElements(int index, boolean[] a, int offset, int length) {
         this.ensureIndex(index);
         this.l.addElements(this.from + index, a, offset, length);
         this.to += length;

         assert this.assertRange();
      }

      @Override
      public void setElements(int index, boolean[] a, int offset, int length) {
         this.ensureIndex(index);
         this.l.setElements(this.from + index, a, offset, length);

         assert this.assertRange();
      }

      @Override
      public BooleanListIterator listIterator(int index) {
         this.ensureIndex(index);
         return (BooleanListIterator)(this.l instanceof RandomAccess
            ? new AbstractBooleanList.BooleanSubList.RandomAccessIter(index)
            : new AbstractBooleanList.BooleanSubList.ParentWrappingIter(this.l.listIterator(index + this.from)));
      }

      @Override
      public BooleanSpliterator spliterator() {
         return (BooleanSpliterator)(this.l instanceof RandomAccess
            ? new AbstractBooleanList.IndexBasedSpliterator(this.l, this.from, this.to)
            : super.spliterator());
      }

      @Override
      public BooleanList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractBooleanList.BooleanSubList(this, from, to);
         }
      }

      @Override
      public boolean rem(boolean k) {
         int index = this.indexOf(k);
         if (index == -1) {
            return false;
         } else {
            this.to--;
            this.l.removeBoolean(this.from + index);

            assert this.assertRange();

            return true;
         }
      }

      @Override
      public boolean addAll(int index, BooleanCollection c) {
         this.ensureIndex(index);
         return super.addAll(index, c);
      }

      @Override
      public boolean addAll(int index, BooleanList l) {
         this.ensureIndex(index);
         return super.addAll(index, l);
      }

      private class ParentWrappingIter implements BooleanListIterator {
         private BooleanListIterator parent;

         ParentWrappingIter(BooleanListIterator parent) {
            this.parent = parent;
         }

         @Override
         public int nextIndex() {
            return this.parent.nextIndex() - BooleanSubList.this.from;
         }

         @Override
         public int previousIndex() {
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
         public int back(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int currentPos = this.parent.previousIndex();
               int parentNewPos = currentPos - n;
               if (parentNewPos < BooleanSubList.this.from - 1) {
                  parentNewPos = BooleanSubList.this.from - 1;
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
               if (parentNewPos > BooleanSubList.this.to) {
                  parentNewPos = BooleanSubList.this.to;
               }

               int toSkip = parentNewPos - currentPos;
               return this.parent.skip(toSkip);
            }
         }
      }

      private final class RandomAccessIter extends BooleanIterators.AbstractIndexBasedListIterator {
         RandomAccessIter(int pos) {
            super(0, pos);
         }

         @Override
         protected final boolean get(int i) {
            return BooleanSubList.this.l.getBoolean(BooleanSubList.this.from + i);
         }

         @Override
         protected final void add(int i, boolean k) {
            BooleanSubList.this.add(i, k);
         }

         @Override
         protected final void set(int i, boolean k) {
            BooleanSubList.this.set(i, k);
         }

         @Override
         protected final void remove(int i) {
            BooleanSubList.this.removeBoolean(i);
         }

         @Override
         protected final int getMaxPos() {
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

   static final class IndexBasedSpliterator extends BooleanSpliterators.LateBindingSizeIndexBasedSpliterator {
      final BooleanList l;

      IndexBasedSpliterator(BooleanList l, int pos) {
         super(pos);
         this.l = l;
      }

      IndexBasedSpliterator(BooleanList l, int pos, int maxPos) {
         super(pos, maxPos);
         this.l = l;
      }

      @Override
      protected final int getMaxPosFromBackingStore() {
         return this.l.size();
      }

      @Override
      protected final boolean get(int i) {
         return this.l.getBoolean(i);
      }

      protected final AbstractBooleanList.IndexBasedSpliterator makeForSplit(int pos, int maxPos) {
         return new AbstractBooleanList.IndexBasedSpliterator(this.l, pos, maxPos);
      }
   }
}
