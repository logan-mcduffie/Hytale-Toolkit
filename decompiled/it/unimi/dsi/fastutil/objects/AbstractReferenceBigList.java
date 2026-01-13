package it.unimi.dsi.fastutil.objects;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.BigList;
import it.unimi.dsi.fastutil.BigListIterator;
import it.unimi.dsi.fastutil.Stack;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.RandomAccess;
import java.util.function.Consumer;

public abstract class AbstractReferenceBigList<K> extends AbstractReferenceCollection<K> implements ReferenceBigList<K>, Stack<K> {
   protected AbstractReferenceBigList() {
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
   public void add(long index, K k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean add(K k) {
      this.add(this.size64(), k);
      return true;
   }

   @Override
   public K remove(long i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public K set(long index, K k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(long index, Collection<? extends K> c) {
      this.ensureIndex(index);
      Iterator<? extends K> i = c.iterator();
      boolean retVal = i.hasNext();

      while (i.hasNext()) {
         this.add(index++, (K)i.next());
      }

      return retVal;
   }

   @Override
   public boolean addAll(Collection<? extends K> c) {
      return this.addAll(this.size64(), c);
   }

   @Override
   public ObjectBigListIterator<K> iterator() {
      return this.listIterator();
   }

   @Override
   public ObjectBigListIterator<K> listIterator() {
      return this.listIterator(0L);
   }

   @Override
   public ObjectBigListIterator<K> listIterator(long index) {
      this.ensureIndex(index);
      return new ObjectBigListIterators.AbstractIndexBasedBigListIterator<K>(0L, index) {
         @Override
         protected final K get(long i) {
            return (K)AbstractReferenceBigList.this.get(i);
         }

         @Override
         protected final void add(long i, K k) {
            AbstractReferenceBigList.this.add(i, k);
         }

         @Override
         protected final void set(long i, K k) {
            AbstractReferenceBigList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            AbstractReferenceBigList.this.remove(i);
         }

         @Override
         protected final long getMaxPos() {
            return AbstractReferenceBigList.this.size64();
         }
      };
   }

   @Override
   public boolean contains(Object k) {
      return this.indexOf(k) >= 0L;
   }

   @Override
   public long indexOf(Object k) {
      ObjectBigListIterator<K> i = this.listIterator();

      while (i.hasNext()) {
         K e = i.next();
         if (k == e) {
            return i.previousIndex();
         }
      }

      return -1L;
   }

   @Override
   public long lastIndexOf(Object k) {
      ObjectBigListIterator<K> i = this.listIterator(this.size64());

      while (i.hasPrevious()) {
         K e = i.previous();
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
            this.add(null);
         }
      } else {
         while (i-- != size) {
            this.remove(i);
         }
      }
   }

   @Override
   public ReferenceBigList<K> subList(long from, long to) {
      this.ensureIndex(from);
      this.ensureIndex(to);
      if (from > to) {
         throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         return (ReferenceBigList<K>)(this instanceof RandomAccess
            ? new AbstractReferenceBigList.ReferenceRandomAccessSubList<>(this, from, to)
            : new AbstractReferenceBigList.ReferenceSubList<>(this, from, to));
      }
   }

   @Override
   public void forEach(Consumer<? super K> action) {
      if (this instanceof RandomAccess) {
         long i = 0L;

         for (long max = this.size64(); i < max; i++) {
            action.accept(this.get(i));
         }
      } else {
         super.forEach(action);
      }
   }

   @Override
   public void removeElements(long from, long to) {
      this.ensureIndex(to);
      ObjectBigListIterator<K> i = this.listIterator(from);
      long n = to - from;
      if (n < 0L) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         while (n-- != 0L) {
            i.next();
            i.remove();
         }
      }
   }

   @Override
   public void addElements(long index, K[][] a, long offset, long length) {
      this.ensureIndex(index);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (this instanceof RandomAccess) {
         while (length-- != 0L) {
            this.add(index++, BigArrays.get(a, offset++));
         }
      } else {
         ObjectBigListIterator<K> iter = this.listIterator(index);

         while (length-- != 0L) {
            iter.add(BigArrays.get(a, offset++));
         }
      }
   }

   @Override
   public void addElements(long index, K[][] a) {
      this.addElements(index, a, 0L, BigArrays.length(a));
   }

   @Override
   public void getElements(long from, Object[][] a, long offset, long length) {
      this.ensureIndex(from);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (from + length > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
      } else {
         if (this instanceof RandomAccess) {
            long current = from;

            while (length-- != 0L) {
               BigArrays.set(a, offset++, this.get(current++));
            }
         } else {
            ObjectBigListIterator<K> i = this.listIterator(from);

            while (length-- != 0L) {
               BigArrays.set(a, offset++, i.next());
            }
         }
      }
   }

   @Override
   public void setElements(long index, K[][] a, long offset, long length) {
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
            ObjectBigListIterator<K> iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.next();
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
      ObjectIterator<K> i = this.iterator();
      int h = 1;
      long s = this.size64();

      while (s-- != 0L) {
         K k = i.next();
         h = 31 * h + System.identityHashCode(k);
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
         } else {
            BigListIterator<?> i1 = this.listIterator();
            BigListIterator<?> i2 = l.listIterator();

            while (s-- != 0L) {
               if (i1.next() != i2.next()) {
                  return false;
               }
            }

            return true;
         }
      }
   }

   @Override
   public void push(K o) {
      this.add(o);
   }

   @Override
   public K pop() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.remove(this.size64() - 1L);
      }
   }

   @Override
   public K top() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.get(this.size64() - 1L);
      }
   }

   @Override
   public K peek(int i) {
      return this.get(this.size64() - 1L - i);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ObjectIterator<K> i = this.iterator();
      long n = this.size64();
      boolean first = true;
      s.append("[");

      while (n-- != 0L) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         K k = i.next();
         if (this == k) {
            s.append("(this big list)");
         } else {
            s.append(String.valueOf(k));
         }
      }

      s.append("]");
      return s.toString();
   }

   static final class IndexBasedSpliterator<K> extends ObjectBigSpliterators.LateBindingSizeIndexBasedSpliterator<K> {
      final ReferenceBigList<K> l;

      IndexBasedSpliterator(ReferenceBigList<K> l, long pos) {
         super(pos);
         this.l = l;
      }

      IndexBasedSpliterator(ReferenceBigList<K> l, long pos, long maxPos) {
         super(pos, maxPos);
         this.l = l;
      }

      @Override
      protected final long getMaxPosFromBackingStore() {
         return this.l.size64();
      }

      @Override
      protected final K get(long i) {
         return this.l.get(i);
      }

      protected final AbstractReferenceBigList.IndexBasedSpliterator<K> makeForSplit(long pos, long maxPos) {
         return new AbstractReferenceBigList.IndexBasedSpliterator<>(this.l, pos, maxPos);
      }
   }

   public static class ReferenceRandomAccessSubList<K> extends AbstractReferenceBigList.ReferenceSubList<K> implements RandomAccess {
      private static final long serialVersionUID = -107070782945191929L;

      public ReferenceRandomAccessSubList(ReferenceBigList<K> l, long from, long to) {
         super(l, from, to);
      }

      @Override
      public ReferenceBigList<K> subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractReferenceBigList.ReferenceRandomAccessSubList<>(this, from, to);
         }
      }
   }

   public static class ReferenceSubList<K> extends AbstractReferenceBigList<K> implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ReferenceBigList<K> l;
      protected final long from;
      protected long to;

      public ReferenceSubList(ReferenceBigList<K> l, long from, long to) {
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
      public boolean add(K k) {
         this.l.add(this.to, k);
         this.to++;

         assert this.assertRange();

         return true;
      }

      @Override
      public void add(long index, K k) {
         this.ensureIndex(index);
         this.l.add(this.from + index, k);
         this.to++;

         assert this.assertRange();
      }

      @Override
      public boolean addAll(long index, Collection<? extends K> c) {
         this.ensureIndex(index);
         this.to = this.to + c.size();
         return this.l.addAll(this.from + index, c);
      }

      @Override
      public K get(long index) {
         this.ensureRestrictedIndex(index);
         return this.l.get(this.from + index);
      }

      @Override
      public K remove(long index) {
         this.ensureRestrictedIndex(index);
         this.to--;
         return this.l.remove(this.from + index);
      }

      @Override
      public K set(long index, K k) {
         this.ensureRestrictedIndex(index);
         return this.l.set(this.from + index, k);
      }

      @Override
      public long size64() {
         return this.to - this.from;
      }

      @Override
      public void getElements(long from, Object[][] a, long offset, long length) {
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
      public void addElements(long index, K[][] a, long offset, long length) {
         this.ensureIndex(index);
         this.l.addElements(this.from + index, a, offset, length);
         this.to += length;

         assert this.assertRange();
      }

      @Override
      public ObjectBigListIterator<K> listIterator(long index) {
         this.ensureIndex(index);
         return (ObjectBigListIterator<K>)(this.l instanceof RandomAccess
            ? new AbstractReferenceBigList.ReferenceSubList.RandomAccessIter(index)
            : new AbstractReferenceBigList.ReferenceSubList.ParentWrappingIter(this.l.listIterator(index + this.from)));
      }

      @Override
      public ObjectSpliterator<K> spliterator() {
         return (ObjectSpliterator<K>)(this.l instanceof RandomAccess
            ? new AbstractReferenceBigList.IndexBasedSpliterator<>(this.l, this.from, this.to)
            : super.spliterator());
      }

      @Override
      public ReferenceBigList<K> subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractReferenceBigList.ReferenceSubList<>(this, from, to);
         }
      }

      private class ParentWrappingIter implements ObjectBigListIterator<K> {
         private ObjectBigListIterator<K> parent;

         ParentWrappingIter(ObjectBigListIterator<K> parent) {
            this.parent = parent;
         }

         @Override
         public long nextIndex() {
            return this.parent.nextIndex() - ReferenceSubList.this.from;
         }

         @Override
         public long previousIndex() {
            return this.parent.previousIndex() - ReferenceSubList.this.from;
         }

         @Override
         public boolean hasNext() {
            return this.parent.nextIndex() < ReferenceSubList.this.to;
         }

         @Override
         public boolean hasPrevious() {
            return this.parent.previousIndex() >= ReferenceSubList.this.from;
         }

         @Override
         public K next() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.next();
            }
         }

         @Override
         public K previous() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.previous();
            }
         }

         @Override
         public void add(K k) {
            this.parent.add(k);
         }

         @Override
         public void set(K k) {
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
               if (parentNewPos < ReferenceSubList.this.from - 1L) {
                  parentNewPos = ReferenceSubList.this.from - 1L;
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
               if (parentNewPos > ReferenceSubList.this.to) {
                  parentNewPos = ReferenceSubList.this.to;
               }

               long toSkip = parentNewPos - currentPos;
               return this.parent.skip(toSkip);
            }
         }
      }

      private final class RandomAccessIter extends ObjectBigListIterators.AbstractIndexBasedBigListIterator<K> {
         RandomAccessIter(long pos) {
            super(0L, pos);
         }

         @Override
         protected final K get(long i) {
            return ReferenceSubList.this.l.get(ReferenceSubList.this.from + i);
         }

         @Override
         protected final void add(long i, K k) {
            ReferenceSubList.this.add(i, k);
         }

         @Override
         protected final void set(long i, K k) {
            ReferenceSubList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            ReferenceSubList.this.remove(i);
         }

         @Override
         protected final long getMaxPos() {
            return ReferenceSubList.this.to - ReferenceSubList.this.from;
         }

         @Override
         public void add(K k) {
            super.add(k);

            assert ReferenceSubList.this.assertRange();
         }

         @Override
         public void remove() {
            super.remove();

            assert ReferenceSubList.this.assertRange();
         }
      }
   }
}
