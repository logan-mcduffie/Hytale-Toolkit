package it.unimi.dsi.fastutil.longs;

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

public abstract class AbstractLongList extends AbstractLongCollection implements LongList, LongStack {
   protected AbstractLongList() {
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
   public void add(int index, long k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean add(long k) {
      this.add(this.size(), k);
      return true;
   }

   @Override
   public long removeLong(int i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public long set(int index, long k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(int index, Collection<? extends Long> c) {
      if (c instanceof LongCollection) {
         return this.addAll(index, (LongCollection)c);
      } else {
         this.ensureIndex(index);
         Iterator<? extends Long> i = c.iterator();
         boolean retVal = i.hasNext();

         while (i.hasNext()) {
            this.add(index++, i.next());
         }

         return retVal;
      }
   }

   @Override
   public boolean addAll(Collection<? extends Long> c) {
      return this.addAll(this.size(), c);
   }

   @Override
   public LongListIterator iterator() {
      return this.listIterator();
   }

   @Override
   public LongListIterator listIterator() {
      return this.listIterator(0);
   }

   @Override
   public LongListIterator listIterator(int index) {
      this.ensureIndex(index);
      return new LongIterators.AbstractIndexBasedListIterator(0, index) {
         @Override
         protected final long get(int i) {
            return AbstractLongList.this.getLong(i);
         }

         @Override
         protected final void add(int i, long k) {
            AbstractLongList.this.add(i, k);
         }

         @Override
         protected final void set(int i, long k) {
            AbstractLongList.this.set(i, k);
         }

         @Override
         protected final void remove(int i) {
            AbstractLongList.this.removeLong(i);
         }

         @Override
         protected final int getMaxPos() {
            return AbstractLongList.this.size();
         }
      };
   }

   @Override
   public boolean contains(long k) {
      return this.indexOf(k) >= 0;
   }

   @Override
   public int indexOf(long k) {
      LongListIterator i = this.listIterator();

      while (i.hasNext()) {
         long e = i.nextLong();
         if (k == e) {
            return i.previousIndex();
         }
      }

      return -1;
   }

   @Override
   public int lastIndexOf(long k) {
      LongListIterator i = this.listIterator(this.size());

      while (i.hasPrevious()) {
         long e = i.previousLong();
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
            this.add(0L);
         }
      } else {
         while (i-- != size) {
            this.removeLong(i);
         }
      }
   }

   @Override
   public LongList subList(int from, int to) {
      this.ensureIndex(from);
      this.ensureIndex(to);
      if (from > to) {
         throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         return (LongList)(this instanceof RandomAccess
            ? new AbstractLongList.LongRandomAccessSubList(this, from, to)
            : new AbstractLongList.LongSubList(this, from, to));
      }
   }

   @Override
   public void forEach(java.util.function.LongConsumer action) {
      if (this instanceof RandomAccess) {
         int i = 0;

         for (int max = this.size(); i < max; i++) {
            action.accept(this.getLong(i));
         }
      } else {
         LongList.super.forEach(action);
      }
   }

   @Override
   public void removeElements(int from, int to) {
      this.ensureIndex(to);
      LongListIterator i = this.listIterator(from);
      int n = to - from;
      if (n < 0) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         while (n-- != 0) {
            i.nextLong();
            i.remove();
         }
      }
   }

   @Override
   public void addElements(int index, long[] a, int offset, int length) {
      this.ensureIndex(index);
      LongArrays.ensureOffsetLength(a, offset, length);
      if (this instanceof RandomAccess) {
         while (length-- != 0) {
            this.add(index++, a[offset++]);
         }
      } else {
         LongListIterator iter = this.listIterator(index);

         while (length-- != 0) {
            iter.add(a[offset++]);
         }
      }
   }

   @Override
   public void addElements(int index, long[] a) {
      this.addElements(index, a, 0, a.length);
   }

   @Override
   public void getElements(int from, long[] a, int offset, int length) {
      this.ensureIndex(from);
      LongArrays.ensureOffsetLength(a, offset, length);
      if (from + length > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
      } else {
         if (this instanceof RandomAccess) {
            int current = from;

            while (length-- != 0) {
               a[offset++] = this.getLong(current++);
            }
         } else {
            LongListIterator i = this.listIterator(from);

            while (length-- != 0) {
               a[offset++] = i.nextLong();
            }
         }
      }
   }

   @Override
   public void setElements(int index, long[] a, int offset, int length) {
      this.ensureIndex(index);
      LongArrays.ensureOffsetLength(a, offset, length);
      if (index + length > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
      } else {
         if (this instanceof RandomAccess) {
            for (int i = 0; i < length; i++) {
               this.set(i + index, a[i + offset]);
            }
         } else {
            LongListIterator iter = this.listIterator(index);
            int i = 0;

            while (i < length) {
               iter.nextLong();
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
      LongIterator i = this.iterator();
      int h = 1;
      int s = this.size();

      while (s-- != 0) {
         long k = i.nextLong();
         h = 31 * h + HashCommon.long2int(k);
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
         } else if (l instanceof LongList) {
            LongListIterator i1 = this.listIterator();
            LongListIterator i2 = ((LongList)l).listIterator();

            while (s-- != 0) {
               if (i1.nextLong() != i2.nextLong()) {
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

   public int compareTo(List<? extends Long> l) {
      if (l == this) {
         return 0;
      } else if (l instanceof LongList) {
         LongListIterator i1 = this.listIterator();
         LongListIterator i2 = ((LongList)l).listIterator();

         while (i1.hasNext() && i2.hasNext()) {
            long e1 = i1.nextLong();
            long e2 = i2.nextLong();
            int r;
            if ((r = Long.compare(e1, e2)) != 0) {
               return r;
            }
         }

         return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
      } else {
         ListIterator<? extends Long> i1 = this.listIterator();
         ListIterator<? extends Long> i2 = l.listIterator();

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
   public void push(long o) {
      this.add(o);
   }

   @Override
   public long popLong() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeLong(this.size() - 1);
      }
   }

   @Override
   public long topLong() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getLong(this.size() - 1);
      }
   }

   @Override
   public long peekLong(int i) {
      return this.getLong(this.size() - 1 - i);
   }

   @Override
   public boolean rem(long k) {
      int index = this.indexOf(k);
      if (index == -1) {
         return false;
      } else {
         this.removeLong(index);
         return true;
      }
   }

   @Override
   public long[] toLongArray() {
      int size = this.size();
      if (size == 0) {
         return LongArrays.EMPTY_ARRAY;
      } else {
         long[] ret = new long[size];
         this.getElements(0, ret, 0, size);
         return ret;
      }
   }

   @Override
   public long[] toArray(long[] a) {
      int size = this.size();
      if (a.length < size) {
         a = Arrays.copyOf(a, size);
      }

      this.getElements(0, a, 0, size);
      return a;
   }

   @Override
   public boolean addAll(int index, LongCollection c) {
      this.ensureIndex(index);
      LongIterator i = c.iterator();
      boolean retVal = i.hasNext();

      while (i.hasNext()) {
         this.add(index++, i.nextLong());
      }

      return retVal;
   }

   @Override
   public boolean addAll(LongCollection c) {
      return this.addAll(this.size(), c);
   }

   @Override
   public final void replaceAll(LongUnaryOperator operator) {
      this.replaceAll(operator);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      LongIterator i = this.iterator();
      int n = this.size();
      boolean first = true;
      s.append("[");

      while (n-- != 0) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         long k = i.nextLong();
         s.append(String.valueOf(k));
      }

      s.append("]");
      return s.toString();
   }

   static final class IndexBasedSpliterator extends LongSpliterators.LateBindingSizeIndexBasedSpliterator {
      final LongList l;

      IndexBasedSpliterator(LongList l, int pos) {
         super(pos);
         this.l = l;
      }

      IndexBasedSpliterator(LongList l, int pos, int maxPos) {
         super(pos, maxPos);
         this.l = l;
      }

      @Override
      protected final int getMaxPosFromBackingStore() {
         return this.l.size();
      }

      @Override
      protected final long get(int i) {
         return this.l.getLong(i);
      }

      protected final AbstractLongList.IndexBasedSpliterator makeForSplit(int pos, int maxPos) {
         return new AbstractLongList.IndexBasedSpliterator(this.l, pos, maxPos);
      }
   }

   public static class LongRandomAccessSubList extends AbstractLongList.LongSubList implements RandomAccess {
      private static final long serialVersionUID = -107070782945191929L;

      public LongRandomAccessSubList(LongList l, int from, int to) {
         super(l, from, to);
      }

      @Override
      public LongList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractLongList.LongRandomAccessSubList(this, from, to);
         }
      }
   }

   public static class LongSubList extends AbstractLongList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final LongList l;
      protected final int from;
      protected int to;

      public LongSubList(LongList l, int from, int to) {
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
      public boolean add(long k) {
         this.l.add(this.to, k);
         this.to++;

         assert this.assertRange();

         return true;
      }

      @Override
      public void add(int index, long k) {
         this.ensureIndex(index);
         this.l.add(this.from + index, k);
         this.to++;

         assert this.assertRange();
      }

      @Override
      public boolean addAll(int index, Collection<? extends Long> c) {
         this.ensureIndex(index);
         this.to = this.to + c.size();
         return this.l.addAll(this.from + index, c);
      }

      @Override
      public long getLong(int index) {
         this.ensureRestrictedIndex(index);
         return this.l.getLong(this.from + index);
      }

      @Override
      public long removeLong(int index) {
         this.ensureRestrictedIndex(index);
         this.to--;
         return this.l.removeLong(this.from + index);
      }

      @Override
      public long set(int index, long k) {
         this.ensureRestrictedIndex(index);
         return this.l.set(this.from + index, k);
      }

      @Override
      public int size() {
         return this.to - this.from;
      }

      @Override
      public void getElements(int from, long[] a, int offset, int length) {
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
      public void addElements(int index, long[] a, int offset, int length) {
         this.ensureIndex(index);
         this.l.addElements(this.from + index, a, offset, length);
         this.to += length;

         assert this.assertRange();
      }

      @Override
      public void setElements(int index, long[] a, int offset, int length) {
         this.ensureIndex(index);
         this.l.setElements(this.from + index, a, offset, length);

         assert this.assertRange();
      }

      @Override
      public LongListIterator listIterator(int index) {
         this.ensureIndex(index);
         return (LongListIterator)(this.l instanceof RandomAccess
            ? new AbstractLongList.LongSubList.RandomAccessIter(index)
            : new AbstractLongList.LongSubList.ParentWrappingIter(this.l.listIterator(index + this.from)));
      }

      @Override
      public LongSpliterator spliterator() {
         return (LongSpliterator)(this.l instanceof RandomAccess ? new AbstractLongList.IndexBasedSpliterator(this.l, this.from, this.to) : super.spliterator());
      }

      @Override
      public LongList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractLongList.LongSubList(this, from, to);
         }
      }

      @Override
      public boolean rem(long k) {
         int index = this.indexOf(k);
         if (index == -1) {
            return false;
         } else {
            this.to--;
            this.l.removeLong(this.from + index);

            assert this.assertRange();

            return true;
         }
      }

      @Override
      public boolean addAll(int index, LongCollection c) {
         this.ensureIndex(index);
         return super.addAll(index, c);
      }

      @Override
      public boolean addAll(int index, LongList l) {
         this.ensureIndex(index);
         return super.addAll(index, l);
      }

      private class ParentWrappingIter implements LongListIterator {
         private LongListIterator parent;

         ParentWrappingIter(LongListIterator parent) {
            this.parent = parent;
         }

         @Override
         public int nextIndex() {
            return this.parent.nextIndex() - LongSubList.this.from;
         }

         @Override
         public int previousIndex() {
            return this.parent.previousIndex() - LongSubList.this.from;
         }

         @Override
         public boolean hasNext() {
            return this.parent.nextIndex() < LongSubList.this.to;
         }

         @Override
         public boolean hasPrevious() {
            return this.parent.previousIndex() >= LongSubList.this.from;
         }

         @Override
         public long nextLong() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.nextLong();
            }
         }

         @Override
         public long previousLong() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.previousLong();
            }
         }

         @Override
         public void add(long k) {
            this.parent.add(k);
         }

         @Override
         public void set(long k) {
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
               if (parentNewPos < LongSubList.this.from - 1) {
                  parentNewPos = LongSubList.this.from - 1;
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
               if (parentNewPos > LongSubList.this.to) {
                  parentNewPos = LongSubList.this.to;
               }

               int toSkip = parentNewPos - currentPos;
               return this.parent.skip(toSkip);
            }
         }
      }

      private final class RandomAccessIter extends LongIterators.AbstractIndexBasedListIterator {
         RandomAccessIter(int pos) {
            super(0, pos);
         }

         @Override
         protected final long get(int i) {
            return LongSubList.this.l.getLong(LongSubList.this.from + i);
         }

         @Override
         protected final void add(int i, long k) {
            LongSubList.this.add(i, k);
         }

         @Override
         protected final void set(int i, long k) {
            LongSubList.this.set(i, k);
         }

         @Override
         protected final void remove(int i) {
            LongSubList.this.removeLong(i);
         }

         @Override
         protected final int getMaxPos() {
            return LongSubList.this.to - LongSubList.this.from;
         }

         @Override
         public void add(long k) {
            super.add(k);

            assert LongSubList.this.assertRange();
         }

         @Override
         public void remove() {
            super.remove();

            assert LongSubList.this.assertRange();
         }
      }
   }
}
