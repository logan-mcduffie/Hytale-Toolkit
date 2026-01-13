package it.unimi.dsi.fastutil.chars;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.RandomAccess;

public abstract class AbstractCharList extends AbstractCharCollection implements CharList, CharStack {
   protected AbstractCharList() {
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
   public void add(int index, char k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean add(char k) {
      this.add(this.size(), k);
      return true;
   }

   @Override
   public char removeChar(int i) {
      throw new UnsupportedOperationException();
   }

   @Override
   public char set(int index, char k) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean addAll(int index, Collection<? extends Character> c) {
      if (c instanceof CharCollection) {
         return this.addAll(index, (CharCollection)c);
      } else {
         this.ensureIndex(index);
         Iterator<? extends Character> i = c.iterator();
         boolean retVal = i.hasNext();

         while (i.hasNext()) {
            this.add(index++, i.next());
         }

         return retVal;
      }
   }

   @Override
   public boolean addAll(Collection<? extends Character> c) {
      return this.addAll(this.size(), c);
   }

   @Override
   public CharListIterator iterator() {
      return this.listIterator();
   }

   @Override
   public CharListIterator listIterator() {
      return this.listIterator(0);
   }

   @Override
   public CharListIterator listIterator(int index) {
      this.ensureIndex(index);
      return new CharIterators.AbstractIndexBasedListIterator(0, index) {
         @Override
         protected final char get(int i) {
            return AbstractCharList.this.getChar(i);
         }

         @Override
         protected final void add(int i, char k) {
            AbstractCharList.this.add(i, k);
         }

         @Override
         protected final void set(int i, char k) {
            AbstractCharList.this.set(i, k);
         }

         @Override
         protected final void remove(int i) {
            AbstractCharList.this.removeChar(i);
         }

         @Override
         protected final int getMaxPos() {
            return AbstractCharList.this.size();
         }
      };
   }

   @Override
   public boolean contains(char k) {
      return this.indexOf(k) >= 0;
   }

   @Override
   public int indexOf(char k) {
      CharListIterator i = this.listIterator();

      while (i.hasNext()) {
         char e = i.nextChar();
         if (k == e) {
            return i.previousIndex();
         }
      }

      return -1;
   }

   @Override
   public int lastIndexOf(char k) {
      CharListIterator i = this.listIterator(this.size());

      while (i.hasPrevious()) {
         char e = i.previousChar();
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
            this.add('\u0000');
         }
      } else {
         while (i-- != size) {
            this.removeChar(i);
         }
      }
   }

   @Override
   public CharList subList(int from, int to) {
      this.ensureIndex(from);
      this.ensureIndex(to);
      if (from > to) {
         throw new IndexOutOfBoundsException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         return (CharList)(this instanceof RandomAccess
            ? new AbstractCharList.CharRandomAccessSubList(this, from, to)
            : new AbstractCharList.CharSubList(this, from, to));
      }
   }

   @Override
   public void forEach(CharConsumer action) {
      if (this instanceof RandomAccess) {
         int i = 0;

         for (int max = this.size(); i < max; i++) {
            action.accept(this.getChar(i));
         }
      } else {
         CharList.super.forEach(action);
      }
   }

   @Override
   public void removeElements(int from, int to) {
      this.ensureIndex(to);
      CharListIterator i = this.listIterator(from);
      int n = to - from;
      if (n < 0) {
         throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
      } else {
         while (n-- != 0) {
            i.nextChar();
            i.remove();
         }
      }
   }

   @Override
   public void addElements(int index, char[] a, int offset, int length) {
      this.ensureIndex(index);
      CharArrays.ensureOffsetLength(a, offset, length);
      if (this instanceof RandomAccess) {
         while (length-- != 0) {
            this.add(index++, a[offset++]);
         }
      } else {
         CharListIterator iter = this.listIterator(index);

         while (length-- != 0) {
            iter.add(a[offset++]);
         }
      }
   }

   @Override
   public void addElements(int index, char[] a) {
      this.addElements(index, a, 0, a.length);
   }

   @Override
   public void getElements(int from, char[] a, int offset, int length) {
      this.ensureIndex(from);
      CharArrays.ensureOffsetLength(a, offset, length);
      if (from + length > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (from + length) + ") is greater than list size (" + this.size() + ")");
      } else {
         if (this instanceof RandomAccess) {
            int current = from;

            while (length-- != 0) {
               a[offset++] = this.getChar(current++);
            }
         } else {
            CharListIterator i = this.listIterator(from);

            while (length-- != 0) {
               a[offset++] = i.nextChar();
            }
         }
      }
   }

   @Override
   public void setElements(int index, char[] a, int offset, int length) {
      this.ensureIndex(index);
      CharArrays.ensureOffsetLength(a, offset, length);
      if (index + length > this.size()) {
         throw new IndexOutOfBoundsException("End index (" + (index + length) + ") is greater than list size (" + this.size() + ")");
      } else {
         if (this instanceof RandomAccess) {
            for (int i = 0; i < length; i++) {
               this.set(i + index, a[i + offset]);
            }
         } else {
            CharListIterator iter = this.listIterator(index);
            int i = 0;

            while (i < length) {
               iter.nextChar();
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
      CharIterator i = this.iterator();
      int h = 1;
      int s = this.size();

      while (s-- != 0) {
         char k = i.nextChar();
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
         } else if (l instanceof CharList) {
            CharListIterator i1 = this.listIterator();
            CharListIterator i2 = ((CharList)l).listIterator();

            while (s-- != 0) {
               if (i1.nextChar() != i2.nextChar()) {
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

   public int compareTo(List<? extends Character> l) {
      if (l == this) {
         return 0;
      } else if (l instanceof CharList) {
         CharListIterator i1 = this.listIterator();
         CharListIterator i2 = ((CharList)l).listIterator();

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
         ListIterator<? extends Character> i1 = this.listIterator();
         ListIterator<? extends Character> i2 = l.listIterator();

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
         return this.removeChar(this.size() - 1);
      }
   }

   @Override
   public char topChar() {
      if (this.isEmpty()) {
         throw new NoSuchElementException();
      } else {
         return this.getChar(this.size() - 1);
      }
   }

   @Override
   public char peekChar(int i) {
      return this.getChar(this.size() - 1 - i);
   }

   @Override
   public boolean rem(char k) {
      int index = this.indexOf(k);
      if (index == -1) {
         return false;
      } else {
         this.removeChar(index);
         return true;
      }
   }

   @Override
   public char[] toCharArray() {
      int size = this.size();
      if (size == 0) {
         return CharArrays.EMPTY_ARRAY;
      } else {
         char[] ret = new char[size];
         this.getElements(0, ret, 0, size);
         return ret;
      }
   }

   @Override
   public char[] toArray(char[] a) {
      int size = this.size();
      if (a.length < size) {
         a = Arrays.copyOf(a, size);
      }

      this.getElements(0, a, 0, size);
      return a;
   }

   @Override
   public boolean addAll(int index, CharCollection c) {
      this.ensureIndex(index);
      CharIterator i = c.iterator();
      boolean retVal = i.hasNext();

      while (i.hasNext()) {
         this.add(index++, i.nextChar());
      }

      return retVal;
   }

   @Override
   public boolean addAll(CharCollection c) {
      return this.addAll(this.size(), c);
   }

   @Override
   public String toString() {
      StringBuilder s = new StringBuilder();
      CharIterator i = this.iterator();
      int n = this.size();
      boolean first = true;
      s.append("[");

      while (n-- != 0) {
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

   public static class CharRandomAccessSubList extends AbstractCharList.CharSubList implements RandomAccess {
      private static final long serialVersionUID = -107070782945191929L;

      public CharRandomAccessSubList(CharList l, int from, int to) {
         super(l, from, to);
      }

      @Override
      public CharList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractCharList.CharRandomAccessSubList(this, from, to);
         }
      }
   }

   public static class CharSubList extends AbstractCharList implements Serializable {
      private static final long serialVersionUID = -7046029254386353129L;
      protected final CharList l;
      protected final int from;
      protected int to;

      public CharSubList(CharList l, int from, int to) {
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
      public boolean add(char k) {
         this.l.add(this.to, k);
         this.to++;

         assert this.assertRange();

         return true;
      }

      @Override
      public void add(int index, char k) {
         this.ensureIndex(index);
         this.l.add(this.from + index, k);
         this.to++;

         assert this.assertRange();
      }

      @Override
      public boolean addAll(int index, Collection<? extends Character> c) {
         this.ensureIndex(index);
         this.to = this.to + c.size();
         return this.l.addAll(this.from + index, c);
      }

      @Override
      public char getChar(int index) {
         this.ensureRestrictedIndex(index);
         return this.l.getChar(this.from + index);
      }

      @Override
      public char removeChar(int index) {
         this.ensureRestrictedIndex(index);
         this.to--;
         return this.l.removeChar(this.from + index);
      }

      @Override
      public char set(int index, char k) {
         this.ensureRestrictedIndex(index);
         return this.l.set(this.from + index, k);
      }

      @Override
      public int size() {
         return this.to - this.from;
      }

      @Override
      public void getElements(int from, char[] a, int offset, int length) {
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
      public void addElements(int index, char[] a, int offset, int length) {
         this.ensureIndex(index);
         this.l.addElements(this.from + index, a, offset, length);
         this.to += length;

         assert this.assertRange();
      }

      @Override
      public void setElements(int index, char[] a, int offset, int length) {
         this.ensureIndex(index);
         this.l.setElements(this.from + index, a, offset, length);

         assert this.assertRange();
      }

      @Override
      public CharListIterator listIterator(int index) {
         this.ensureIndex(index);
         return (CharListIterator)(this.l instanceof RandomAccess
            ? new AbstractCharList.CharSubList.RandomAccessIter(index)
            : new AbstractCharList.CharSubList.ParentWrappingIter(this.l.listIterator(index + this.from)));
      }

      @Override
      public CharSpliterator spliterator() {
         return (CharSpliterator)(this.l instanceof RandomAccess ? new AbstractCharList.IndexBasedSpliterator(this.l, this.from, this.to) : super.spliterator());
      }

      @Override
      public CharList subList(int from, int to) {
         this.ensureIndex(from);
         this.ensureIndex(to);
         if (from > to) {
            throw new IllegalArgumentException("Start index (" + from + ") is greater than end index (" + to + ")");
         } else {
            return new AbstractCharList.CharSubList(this, from, to);
         }
      }

      @Override
      public boolean rem(char k) {
         int index = this.indexOf(k);
         if (index == -1) {
            return false;
         } else {
            this.to--;
            this.l.removeChar(this.from + index);

            assert this.assertRange();

            return true;
         }
      }

      @Override
      public boolean addAll(int index, CharCollection c) {
         this.ensureIndex(index);
         return super.addAll(index, c);
      }

      @Override
      public boolean addAll(int index, CharList l) {
         this.ensureIndex(index);
         return super.addAll(index, l);
      }

      private class ParentWrappingIter implements CharListIterator {
         private CharListIterator parent;

         ParentWrappingIter(CharListIterator parent) {
            this.parent = parent;
         }

         @Override
         public int nextIndex() {
            return this.parent.nextIndex() - CharSubList.this.from;
         }

         @Override
         public int previousIndex() {
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
         public int back(int n) {
            if (n < 0) {
               throw new IllegalArgumentException("Argument must be nonnegative: " + n);
            } else {
               int currentPos = this.parent.previousIndex();
               int parentNewPos = currentPos - n;
               if (parentNewPos < CharSubList.this.from - 1) {
                  parentNewPos = CharSubList.this.from - 1;
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
               if (parentNewPos > CharSubList.this.to) {
                  parentNewPos = CharSubList.this.to;
               }

               int toSkip = parentNewPos - currentPos;
               return this.parent.skip(toSkip);
            }
         }
      }

      private final class RandomAccessIter extends CharIterators.AbstractIndexBasedListIterator {
         RandomAccessIter(int pos) {
            super(0, pos);
         }

         @Override
         protected final char get(int i) {
            return CharSubList.this.l.getChar(CharSubList.this.from + i);
         }

         @Override
         protected final void add(int i, char k) {
            CharSubList.this.add(i, k);
         }

         @Override
         protected final void set(int i, char k) {
            CharSubList.this.set(i, k);
         }

         @Override
         protected final void remove(int i) {
            CharSubList.this.removeChar(i);
         }

         @Override
         protected final int getMaxPos() {
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

   static final class IndexBasedSpliterator extends CharSpliterators.LateBindingSizeIndexBasedSpliterator {
      final CharList l;

      IndexBasedSpliterator(CharList l, int pos) {
         super(pos);
         this.l = l;
      }

      IndexBasedSpliterator(CharList l, int pos, int maxPos) {
         super(pos, maxPos);
         this.l = l;
      }

      @Override
      protected final int getMaxPosFromBackingStore() {
         return this.l.size();
      }

      @Override
      protected final char get(int i) {
         return this.l.getChar(i);
      }

      protected final AbstractCharList.IndexBasedSpliterator makeForSplit(int pos, int maxPos) {
         return new AbstractCharList.IndexBasedSpliterator(this.l, pos, maxPos);
      }
   }
}
