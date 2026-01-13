package it.unimi.dsi.fastutil.bytes;

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

public abstract class AbstractByteBigList extends AbstractByteCollection implements ByteBigList, ByteStack {
   protected AbstractByteBigList() {
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
   public void add(long index, byte k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean add(byte k) {
      this.add(this.size64(), k);
      return true;
   }

   @Override
   public byte removeByte(long i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public byte set(long index, byte k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(long index, Collection<? extends Byte> c) {
      this.ensureIndex(index);
      Iterator<? extends Byte> i = c.iterator();
      boolean retVal = i.hasNext();

      while (i.hasNext()) {
         this.add(index++, i.next());
      }

      return retVal;
   }

   @Override
   public boolean addAll(Collection<? extends Byte> c) {
      return this.addAll(this.size64(), c);
   }

   @Override
   public ByteBigListIterator iterator() {
      return this.listIterator();
   }

   @Override
   public ByteBigListIterator listIterator() {
      return this.listIterator(0L);
   }

   @Override
   public ByteBigListIterator listIterator(long index) {
      this.ensureIndex(index);
      return new ByteBigListIterators.AbstractIndexBasedBigListIterator(0L, index) {
         @Override
         protected final byte get(long i) {
            return AbstractByteBigList.this.getByte(i);
         }

         @Override
         protected final void add(long i, byte k) {
            AbstractByteBigList.this.add(i, k);
         }

         @Override
         protected final void set(long i, byte k) {
            AbstractByteBigList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            AbstractByteBigList.this.removeByte(i);
         }

         @Override
         protected final long getMaxPos() {
            return AbstractByteBigList.this.size64();
         }
      };
   }

   @Override
   public IntSpliterator intSpliterator() {
      return this instanceof RandomAccess ? ByteSpliterators.widen(this.spliterator()) : super.intSpliterator();
   }

   @Override
   public boolean contains(byte k) {
      return this.indexOf(k) >= 0L;
   }

   @Override
   public long indexOf(byte k) {
      ByteBigListIterator i = this.listIterator();

      while (i.hasNext()) {
         byte e = i.nextByte();
         if (k == e) {
            return i.previousIndex();
         }
      }

      return -1L;
   }

   @Override
   public long lastIndexOf(byte k) {
      ByteBigListIterator i = this.listIterator(this.size64());

      while (i.hasPrevious()) {
         byte e = i.previousByte();
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
            this.add((byte)0);
         }
      } else {
         while (i-- != size) {
            this.remove(i);
         }
      }
   }

   @Override
   public ByteBigList subList(long from, long to) {
      this.ensureIndex(from);
      this.ensureIndex(to);
      if (from > to) {
         throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         return (ByteBigList)(this instanceof RandomAccess
            ? new AbstractByteBigList.ByteRandomAccessSubList(this, from, to)
            : new AbstractByteBigList.ByteSubList(this, from, to));
      }
   }

   @Override
   public void forEach(ByteConsumer action) {
      if (this instanceof RandomAccess) {
         long i = 0L;

         for (long max = this.size64(); i < max; i++) {
            action.accept(this.getByte(i));
         }
      } else {
         super.forEach(action);
      }
   }

   @Override
   public void removeElements(long from, long to) {
      this.ensureIndex(to);
      ByteBigListIterator i = this.listIterator(from);
      long n = to - from;
      if (n < 0L) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         while (n-- != 0L) {
            i.nextByte();
            i.remove();
         }
      }
   }

   @Override
   public void addElements(long index, byte[][] a, long offset, long length) {
      this.ensureIndex(index);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (this instanceof RandomAccess) {
         while (length-- != 0L) {
            this.add(index++, BigArrays.get(a, offset++));
         }
      } else {
         ByteBigListIterator iter = this.listIterator(index);

         while (length-- != 0L) {
            iter.add(BigArrays.get(a, offset++));
         }
      }
   }

   @Override
   public void addElements(long index, byte[][] a) {
      this.addElements(index, a, 0L, BigArrays.length(a));
   }

   @Override
   public void getElements(long from, byte[][] a, long offset, long length) {
      this.ensureIndex(from);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (from + length > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
      } else {
         if (this instanceof RandomAccess) {
            long current = from;

            while (length-- != 0L) {
               BigArrays.set(a, offset++, this.getByte(current++));
            }
         } else {
            ByteBigListIterator i = this.listIterator(from);

            while (length-- != 0L) {
               BigArrays.set(a, offset++, i.nextByte());
            }
         }
      }
   }

   @Override
   public void setElements(long index, byte[][] a, long offset, long length) {
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
            ByteBigListIterator iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.nextByte();
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
      ByteIterator i = this.iterator();
      int h = 1;
      long s = this.size64();

      while (s-- != 0L) {
         byte k = i.nextByte();
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
         } else if (l instanceof ByteBigList) {
            ByteBigListIterator i1 = this.listIterator();
            ByteBigListIterator i2 = ((ByteBigList)l).listIterator();

            while (s-- != 0L) {
               if (i1.nextByte() != i2.nextByte()) {
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

   public int compareTo(BigList<? extends Byte> l) {
      if (l == this) {
         return 0;
      } else if (l instanceof ByteBigList) {
         ByteBigListIterator i1 = this.listIterator();
         ByteBigListIterator i2 = ((ByteBigList)l).listIterator();

         while (i1.hasNext() && i2.hasNext()) {
            byte e1 = i1.nextByte();
            byte e2 = i2.nextByte();
            int r;
            if ((r = Byte.compare(e1, e2)) != 0) {
               return r;
            }
         }

         return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
      } else {
         BigListIterator<? extends Byte> i1 = this.listIterator();
         BigListIterator<? extends Byte> i2 = l.listIterator();

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
   public void push(byte o) {
      this.add(o);
   }

   @Override
   public byte popByte() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeByte(this.size64() - 1L);
      }
   }

   @Override
   public byte topByte() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getByte(this.size64() - 1L);
      }
   }

   @Override
   public byte peekByte(int i) {
      return this.getByte(this.size64() - 1L - i);
   }

   @Override
   public boolean rem(byte k) {
      long index = this.indexOf(k);
      if (index == -1L) {
         return false;
      } else {
         this.removeByte(index);
         return true;
      }
   }

   @Override
   public boolean addAll(long index, ByteCollection c) {
      return this.addAll(index, c);
   }

   @Override
   public boolean addAll(ByteCollection c) {
      return this.addAll(this.size64(), c);
   }

   @Deprecated
   @Override
   public void add(long index, Byte ok) {
      this.add(index, ok.byteValue());
   }

   @Deprecated
   @Override
   public Byte set(long index, Byte ok) {
      return this.set(index, ok.byteValue());
   }

   @Deprecated
   @Override
   public Byte get(long index) {
      return this.getByte(index);
   }

   @Deprecated
   @Override
   public long indexOf(Object ok) {
      return this.indexOf(((Byte)ok).byteValue());
   }

   @Deprecated
   @Override
   public long lastIndexOf(Object ok) {
      return this.lastIndexOf(((Byte)ok).byteValue());
   }

   @Deprecated
   @Override
   public Byte remove(long index) {
      return this.removeByte(index);
   }

   @Deprecated
   @Override
   public void push(Byte o) {
      this.push(o.byteValue());
   }

   @Deprecated
   @Override
   public Byte pop() {
      return this.popByte();
   }

   @Deprecated
   @Override
   public Byte top() {
      return this.topByte();
   }

   @Deprecated
   @Override
   public Byte peek(int i) {
      return this.peekByte(i);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      ByteIterator i = this.iterator();
      long n = this.size64();
      boolean first = true;
      s.append("[");

      while (n-- != 0L) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         byte k = i.nextByte();
         s.append(String.valueOf((int)k));
      }

      s.append("]");
      return s.toString();
   }

   public static class ByteRandomAccessSubList extends AbstractByteBigList.ByteSubList implements RandomAccess {
      private static final long serialVersionUID = -107070782945191929L;

      public ByteRandomAccessSubList(ByteBigList l, long from, long to) {
         super(l, from, to);
      }

      @Override
      public ByteBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractByteBigList.ByteRandomAccessSubList(this, from, to);
         }
      }
   }

   public static class ByteSubList extends AbstractByteBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final ByteBigList l;
      protected final long from;
      protected long to;

      public ByteSubList(ByteBigList l, long from, long to) {
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
      public boolean add(byte k) {
         this.l.add(this.to, k);
         this.to++;

         assert this.assertRange();

         return true;
      }

      @Override
      public void add(long index, byte k) {
         this.ensureIndex(index);
         this.l.add(this.from + index, k);
         this.to++;

         assert this.assertRange();
      }

      @Override
      public boolean addAll(long index, Collection<? extends Byte> c) {
         this.ensureIndex(index);
         this.to = this.to + c.size();
         return this.l.addAll(this.from + index, c);
      }

      @Override
      public byte getByte(long index) {
         this.ensureRestrictedIndex(index);
         return this.l.getByte(this.from + index);
      }

      @Override
      public byte removeByte(long index) {
         this.ensureRestrictedIndex(index);
         this.to--;
         return this.l.removeByte(this.from + index);
      }

      @Override
      public byte set(long index, byte k) {
         this.ensureRestrictedIndex(index);
         return this.l.set(this.from + index, k);
      }

      @Override
      public long size64() {
         return this.to - this.from;
      }

      @Override
      public void getElements(long from, byte[][] a, long offset, long length) {
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
      public void addElements(long index, byte[][] a, long offset, long length) {
         this.ensureIndex(index);
         this.l.addElements(this.from + index, a, offset, length);
         this.to += length;

         assert this.assertRange();
      }

      @Override
      public ByteBigListIterator listIterator(long index) {
         this.ensureIndex(index);
         return (ByteBigListIterator)(this.l instanceof RandomAccess
            ? new AbstractByteBigList.ByteSubList.RandomAccessIter(index)
            : new AbstractByteBigList.ByteSubList.ParentWrappingIter(this.l.listIterator(index + this.from)));
      }

      @Override
      public ByteSpliterator spliterator() {
         return (ByteSpliterator)(this.l instanceof RandomAccess
            ? new AbstractByteBigList.IndexBasedSpliterator(this.l, this.from, this.to)
            : super.spliterator());
      }

      @Override
      public IntSpliterator intSpliterator() {
         return this.l instanceof RandomAccess ? ByteSpliterators.widen(this.spliterator()) : super.intSpliterator();
      }

      @Override
      public ByteBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractByteBigList.ByteSubList(this, from, to);
         }
      }

      @Override
      public boolean rem(byte k) {
         long index = this.indexOf(k);
         if (index == -1L) {
            return false;
         } else {
            this.to--;
            this.l.removeByte(this.from + index);

            assert this.assertRange();

            return true;
         }
      }

      @Override
      public boolean addAll(long index, ByteCollection c) {
         return super.addAll(index, c);
      }

      @Override
      public boolean addAll(long index, ByteBigList l) {
         return super.addAll(index, l);
      }

      private class ParentWrappingIter implements ByteBigListIterator {
         private ByteBigListIterator parent;

         ParentWrappingIter(ByteBigListIterator parent) {
            this.parent = parent;
         }

         @Override
         public long nextIndex() {
            return this.parent.nextIndex() - ByteSubList.this.from;
         }

         @Override
         public long previousIndex() {
            return this.parent.previousIndex() - ByteSubList.this.from;
         }

         @Override
         public boolean hasNext() {
            return this.parent.nextIndex() < ByteSubList.this.to;
         }

         @Override
         public boolean hasPrevious() {
            return this.parent.previousIndex() >= ByteSubList.this.from;
         }

         @Override
         public byte nextByte() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.nextByte();
            }
         }

         @Override
         public byte previousByte() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.previousByte();
            }
         }

         @Override
         public void add(byte k) {
            this.parent.add(k);
         }

         @Override
         public void set(byte k) {
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
               if (parentNewPos < ByteSubList.this.from - 1L) {
                  parentNewPos = ByteSubList.this.from - 1L;
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
               if (parentNewPos > ByteSubList.this.to) {
                  parentNewPos = ByteSubList.this.to;
               }

               long toSkip = parentNewPos - currentPos;
               return this.parent.skip(toSkip);
            }
         }
      }

      private final class RandomAccessIter extends ByteBigListIterators.AbstractIndexBasedBigListIterator {
         RandomAccessIter(long pos) {
            super(0L, pos);
         }

         @Override
         protected final byte get(long i) {
            return ByteSubList.this.l.getByte(ByteSubList.this.from + i);
         }

         @Override
         protected final void add(long i, byte k) {
            ByteSubList.this.add(i, k);
         }

         @Override
         protected final void set(long i, byte k) {
            ByteSubList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            ByteSubList.this.removeByte(i);
         }

         @Override
         protected final long getMaxPos() {
            return ByteSubList.this.to - ByteSubList.this.from;
         }

         @Override
         public void add(byte k) {
            super.add(k);

            assert ByteSubList.this.assertRange();
         }

         @Override
         public void remove() {
            super.remove();

            assert ByteSubList.this.assertRange();
         }
      }
   }

   static final class IndexBasedSpliterator extends ByteBigSpliterators.LateBindingSizeIndexBasedSpliterator {
      final ByteBigList l;

      IndexBasedSpliterator(ByteBigList l, long pos) {
         super(pos);
         this.l = l;
      }

      IndexBasedSpliterator(ByteBigList l, long pos, long maxPos) {
         super(pos, maxPos);
         this.l = l;
      }

      @Override
      protected final long getMaxPosFromBackingStore() {
         return this.l.size64();
      }

      @Override
      protected final byte get(long i) {
         return this.l.getByte(i);
      }

      protected final AbstractByteBigList.IndexBasedSpliterator makeForSplit(long pos, long maxPos) {
         return new AbstractByteBigList.IndexBasedSpliterator(this.l, pos, maxPos);
      }
   }
}
