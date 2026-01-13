package it.unimi.dsi.fastutil.chars;

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

public abstract class AbstractCharBigList extends AbstractCharCollection implements CharBigList, CharStack {
   protected AbstractCharBigList() {
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
   public void add(long index, char k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean add(char k) {
      this.add(this.size64(), k);
      return true;
   }

   @Override
   public char removeChar(long i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public char set(long index, char k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(long index, Collection<? extends Character> c) {
      this.ensureIndex(index);
      Iterator<? extends Character> i = c.iterator();
      boolean retVal = i.hasNext();

      while (i.hasNext()) {
         this.add(index++, i.next());
      }

      return retVal;
   }

   @Override
   public boolean addAll(Collection<? extends Character> c) {
      return this.addAll(this.size64(), c);
   }

   @Override
   public CharBigListIterator iterator() {
      return this.listIterator();
   }

   @Override
   public CharBigListIterator listIterator() {
      return this.listIterator(0L);
   }

   @Override
   public CharBigListIterator listIterator(long index) {
      this.ensureIndex(index);
      return new CharBigListIterators.AbstractIndexBasedBigListIterator(0L, index) {
         @Override
         protected final char get(long i) {
            return AbstractCharBigList.this.getChar(i);
         }

         @Override
         protected final void add(long i, char k) {
            AbstractCharBigList.this.add(i, k);
         }

         @Override
         protected final void set(long i, char k) {
            AbstractCharBigList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            AbstractCharBigList.this.removeChar(i);
         }

         @Override
         protected final long getMaxPos() {
            return AbstractCharBigList.this.size64();
         }
      };
   }

   @Override
   public IntSpliterator intSpliterator() {
      return this instanceof RandomAccess ? CharSpliterators.widen(this.spliterator()) : super.intSpliterator();
   }

   @Override
   public boolean contains(char k) {
      return this.indexOf(k) >= 0L;
   }

   @Override
   public long indexOf(char k) {
      CharBigListIterator i = this.listIterator();

      while (i.hasNext()) {
         char e = i.nextChar();
         if (k == e) {
            return i.previousIndex();
         }
      }

      return -1L;
   }

   @Override
   public long lastIndexOf(char k) {
      CharBigListIterator i = this.listIterator(this.size64());

      while (i.hasPrevious()) {
         char e = i.previousChar();
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
            this.add('\u0000');
         }
      } else {
         while (i-- != size) {
            this.remove(i);
         }
      }
   }

   @Override
   public CharBigList subList(long from, long to) {
      this.ensureIndex(from);
      this.ensureIndex(to);
      if (from > to) {
         throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         return (CharBigList)(this instanceof RandomAccess
            ? new AbstractCharBigList.CharRandomAccessSubList(this, from, to)
            : new AbstractCharBigList.CharSubList(this, from, to));
      }
   }

   @Override
   public void forEach(CharConsumer action) {
      if (this instanceof RandomAccess) {
         long i = 0L;

         for (long max = this.size64(); i < max; i++) {
            action.accept(this.getChar(i));
         }
      } else {
         super.forEach(action);
      }
   }

   @Override
   public void removeElements(long from, long to) {
      this.ensureIndex(to);
      CharBigListIterator i = this.listIterator(from);
      long n = to - from;
      if (n < 0L) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         while (n-- != 0L) {
            i.nextChar();
            i.remove();
         }
      }
   }

   @Override
   public void addElements(long index, char[][] a, long offset, long length) {
      this.ensureIndex(index);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (this instanceof RandomAccess) {
         while (length-- != 0L) {
            this.add(index++, BigArrays.get(a, offset++));
         }
      } else {
         CharBigListIterator iter = this.listIterator(index);

         while (length-- != 0L) {
            iter.add(BigArrays.get(a, offset++));
         }
      }
   }

   @Override
   public void addElements(long index, char[][] a) {
      this.addElements(index, a, 0L, BigArrays.length(a));
   }

   @Override
   public void getElements(long from, char[][] a, long offset, long length) {
      this.ensureIndex(from);
      BigArrays.ensureOffsetLength(a, offset, length);
      if (from + length > this.size64()) {
         throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size64() + ")");
      } else {
         if (this instanceof RandomAccess) {
            long current = from;

            while (length-- != 0L) {
               BigArrays.set(a, offset++, this.getChar(current++));
            }
         } else {
            CharBigListIterator i = this.listIterator(from);

            while (length-- != 0L) {
               BigArrays.set(a, offset++, i.nextChar());
            }
         }
      }
   }

   @Override
   public void setElements(long index, char[][] a, long offset, long length) {
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
            CharBigListIterator iter = this.listIterator(index);
            long i = 0L;

            while (i < length) {
               iter.nextChar();
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
      CharIterator i = this.iterator();
      int h = 1;
      long s = this.size64();

      while (s-- != 0L) {
         char k = i.nextChar();
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
         } else if (l instanceof CharBigList) {
            CharBigListIterator i1 = this.listIterator();
            CharBigListIterator i2 = ((CharBigList)l).listIterator();

            while (s-- != 0L) {
               if (i1.nextChar() != i2.nextChar()) {
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

   public int compareTo(BigList<? extends Character> l) {
      if (l == this) {
         return 0;
      } else if (l instanceof CharBigList) {
         CharBigListIterator i1 = this.listIterator();
         CharBigListIterator i2 = ((CharBigList)l).listIterator();

         while (i1.hasNext() && i2.hasNext()) {
            char e1 = i1.nextChar();
            char e2 = i2.nextChar();
            int r;
            if ((r = Character.compare(e1, e2)) != 0) {
               return r;
            }
         }

         return i2.hasNext() ? -1 : (i1.hasNext() ? 1 : 0);
      } else {
         BigListIterator<? extends Character> i1 = this.listIterator();
         BigListIterator<? extends Character> i2 = l.listIterator();

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
   public void push(char o) {
      this.add(o);
   }

   @Override
   public char popChar() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.removeChar(this.size64() - 1L);
      }
   }

   @Override
   public char topChar() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getChar(this.size64() - 1L);
      }
   }

   @Override
   public char peekChar(int i) {
      return this.getChar(this.size64() - 1L - i);
   }

   @Override
   public boolean rem(char k) {
      long index = this.indexOf(k);
      if (index == -1L) {
         return false;
      } else {
         this.removeChar(index);
         return true;
      }
   }

   @Override
   public boolean addAll(long index, CharCollection c) {
      return this.addAll(index, c);
   }

   @Override
   public boolean addAll(CharCollection c) {
      return this.addAll(this.size64(), c);
   }

   @Deprecated
   @Override
   public void add(long index, Character ok) {
      this.add(index, ok.charValue());
   }

   @Deprecated
   @Override
   public Character set(long index, Character ok) {
      return this.set(index, ok.charValue());
   }

   @Deprecated
   @Override
   public Character get(long index) {
      return this.getChar(index);
   }

   @Deprecated
   @Override
   public long indexOf(Object ok) {
      return this.indexOf(((Character)ok).charValue());
   }

   @Deprecated
   @Override
   public long lastIndexOf(Object ok) {
      return this.lastIndexOf(((Character)ok).charValue());
   }

   @Deprecated
   @Override
   public Character remove(long index) {
      return this.removeChar(index);
   }

   @Deprecated
   @Override
   public void push(Character o) {
      this.push(o.charValue());
   }

   @Deprecated
   @Override
   public Character pop() {
      return this.popChar();
   }

   @Deprecated
   @Override
   public Character top() {
      return this.topChar();
   }

   @Deprecated
   @Override
   public Character peek(int i) {
      return this.peekChar(i);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      CharIterator i = this.iterator();
      long n = this.size64();
      boolean first = true;
      s.append("[");

      while (n-- != 0L) {
         if (first) {
            first = false;
         } else {
            s.append(", ");
         }

         char k = i.nextChar();
         s.append(String.valueOf(k));
      }

      s.append("]");
      return s.toString();
   }

   public static class CharRandomAccessSubList extends AbstractCharBigList.CharSubList implements RandomAccess {
      private static final long serialVersionUID = -107070782945191929L;

      public CharRandomAccessSubList(CharBigList l, long from, long to) {
         super(l, from, to);
      }

      @Override
      public CharBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractCharBigList.CharRandomAccessSubList(this, from, to);
         }
      }
   }

   public static class CharSubList extends AbstractCharBigList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharBigList l;
      protected final long from;
      protected long to;

      public CharSubList(CharBigList l, long from, long to) {
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
      public boolean add(char k) {
         this.l.add(this.to, k);
         this.to++;

         assert this.assertRange();

         return true;
      }

      @Override
      public void add(long index, char k) {
         this.ensureIndex(index);
         this.l.add(this.from + index, k);
         this.to++;

         assert this.assertRange();
      }

      @Override
      public boolean addAll(long index, Collection<? extends Character> c) {
         this.ensureIndex(index);
         this.to = this.to + c.size();
         return this.l.addAll(this.from + index, c);
      }

      @Override
      public char getChar(long index) {
         this.ensureRestrictedIndex(index);
         return this.l.getChar(this.from + index);
      }

      @Override
      public char removeChar(long index) {
         this.ensureRestrictedIndex(index);
         this.to--;
         return this.l.removeChar(this.from + index);
      }

      @Override
      public char set(long index, char k) {
         this.ensureRestrictedIndex(index);
         return this.l.set(this.from + index, k);
      }

      @Override
      public long size64() {
         return this.to - this.from;
      }

      @Override
      public void getElements(long from, char[][] a, long offset, long length) {
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
      public void addElements(long index, char[][] a, long offset, long length) {
         this.ensureIndex(index);
         this.l.addElements(this.from + index, a, offset, length);
         this.to += length;

         assert this.assertRange();
      }

      @Override
      public CharBigListIterator listIterator(long index) {
         this.ensureIndex(index);
         return (CharBigListIterator)(this.l instanceof RandomAccess
            ? new AbstractCharBigList.CharSubList.RandomAccessIter(index)
            : new AbstractCharBigList.CharSubList.ParentWrappingIter(this.l.listIterator(index + this.from)));
      }

      @Override
      public CharSpliterator spliterator() {
         return (CharSpliterator)(this.l instanceof RandomAccess
            ? new AbstractCharBigList.IndexBasedSpliterator(this.l, this.from, this.to)
            : super.spliterator());
      }

      @Override
      public IntSpliterator intSpliterator() {
         return this.l instanceof RandomAccess ? CharSpliterators.widen(this.spliterator()) : super.intSpliterator();
      }

      @Override
      public CharBigList subList(long from, long to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractCharBigList.CharSubList(this, from, to);
         }
      }

      @Override
      public boolean rem(char k) {
         long index = this.indexOf(k);
         if (index == -1L) {
            return false;
         } else {
            this.to--;
            this.l.removeChar(this.from + index);

            assert this.assertRange();

            return true;
         }
      }

      @Override
      public boolean addAll(long index, CharCollection c) {
         return super.addAll(index, c);
      }

      @Override
      public boolean addAll(long index, CharBigList l) {
         return super.addAll(index, l);
      }

      private class ParentWrappingIter implements CharBigListIterator {
         private CharBigListIterator parent;

         ParentWrappingIter(CharBigListIterator parent) {
            this.parent = parent;
         }

         @Override
         public long nextIndex() {
            return this.parent.nextIndex() - CharSubList.this.from;
         }

         @Override
         public long previousIndex() {
            return this.parent.previousIndex() - CharSubList.this.from;
         }

         @Override
         public boolean hasNext() {
            return this.parent.nextIndex() < CharSubList.this.to;
         }

         @Override
         public boolean hasPrevious() {
            return this.parent.previousIndex() >= CharSubList.this.from;
         }

         @Override
         public char nextChar() {
            if (!this.hasNext()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.nextChar();
            }
         }

         @Override
         public char previousChar() {
            if (!this.hasPrevious()) {
               throw new NoSuchElementException();
            } else {
               return this.parent.previousChar();
            }
         }

         @Override
         public void add(char k) {
            this.parent.add(k);
         }

         @Override
         public void set(char k) {
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
               if (parentNewPos < CharSubList.this.from - 1L) {
                  parentNewPos = CharSubList.this.from - 1L;
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
               if (parentNewPos > CharSubList.this.to) {
                  parentNewPos = CharSubList.this.to;
               }

               long toSkip = parentNewPos - currentPos;
               return this.parent.skip(toSkip);
            }
         }
      }

      private final class RandomAccessIter extends CharBigListIterators.AbstractIndexBasedBigListIterator {
         RandomAccessIter(long pos) {
            super(0L, pos);
         }

         @Override
         protected final char get(long i) {
            return CharSubList.this.l.getChar(CharSubList.this.from + i);
         }

         @Override
         protected final void add(long i, char k) {
            CharSubList.this.add(i, k);
         }

         @Override
         protected final void set(long i, char k) {
            CharSubList.this.set(i, k);
         }

         @Override
         protected final void remove(long i) {
            CharSubList.this.removeChar(i);
         }

         @Override
         protected final long getMaxPos() {
            return CharSubList.this.to - CharSubList.this.from;
         }

         @Override
         public void add(char k) {
            super.add(k);

            assert CharSubList.this.assertRange();
         }

         @Override
         public void remove() {
            super.remove();

            assert CharSubList.this.assertRange();
         }
      }
   }

   static final class IndexBasedSpliterator extends CharBigSpliterators.LateBindingSizeIndexBasedSpliterator {
      final CharBigList l;

      IndexBasedSpliterator(CharBigList l, long pos) {
         super(pos);
         this.l = l;
      }

      IndexBasedSpliterator(CharBigList l, long pos, long maxPos) {
         super(pos, maxPos);
         this.l = l;
      }

      @Override
      protected final long getMaxPosFromBackingStore() {
         return this.l.size64();
      }

      @Override
      protected final char get(long i) {
         return this.l.getChar(i);
      }

      protected final AbstractCharBigList.IndexBasedSpliterator makeForSplit(long pos, long maxPos) {
         return new AbstractCharBigList.IndexBasedSpliterator(this.l, pos, maxPos);
      }
   }
}
