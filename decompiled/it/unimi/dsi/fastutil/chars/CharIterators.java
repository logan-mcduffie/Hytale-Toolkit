package it.unimi.dsi.fastutil.chars;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntIterators;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator.OfInt;
import java.util.function.Consumer;
import java.util.function.IntPredicate;

public final class CharIterators {
   public static final CharIterators.EmptyIterator EMPTY_ITERATOR = new CharIterators.EmptyIterator();

   private CharIterators() {
   }

   public static CharListIterator singleton(char element) {
      return new CharIterators.SingletonIterator(element);
   }

   public static CharListIterator wrap(char[] array, int offset, int length) {
      CharArrays.ensureOffsetLength(array, offset, length);
      return new CharIterators.ArrayIterator(array, offset, length);
   }

   public static CharListIterator wrap(char[] array) {
      return new CharIterators.ArrayIterator(array, 0, array.length);
   }

   public static int unwrap(CharIterator i, char[] array, int offset, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else if (offset >= 0 && offset + max <= array.length) {
         int j = max;

         while (j-- != 0 && i.hasNext()) {
            array[offset++] = i.nextChar();
         }

         return max - j - 1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int unwrap(CharIterator i, char[] array) {
      return unwrap(i, array, 0, array.length);
   }

   public static char[] unwrap(CharIterator i, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         char[] array = new char[16];
         int j = 0;

         while (max-- != 0 && i.hasNext()) {
            if (j == array.length) {
               array = CharArrays.grow(array, j + 1);
            }

            array[j++] = i.nextChar();
         }

         return CharArrays.trim(array, j);
      }
   }

   public static char[] unwrap(CharIterator i) {
      return unwrap(i, Integer.MAX_VALUE);
   }

   public static long unwrap(CharIterator i, char[][] array, long offset, long max) {
      if (max < 0L) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else if (offset >= 0L && offset + max <= BigArrays.length(array)) {
         long j = max;

         while (j-- != 0L && i.hasNext()) {
            BigArrays.set(array, offset++, i.nextChar());
         }

         return max - j - 1L;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static long unwrap(CharIterator i, char[][] array) {
      return unwrap(i, array, 0L, BigArrays.length(array));
   }

   public static int unwrap(CharIterator i, CharCollection c, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         int j = max;

         while (j-- != 0 && i.hasNext()) {
            c.add(i.nextChar());
         }

         return max - j - 1;
      }
   }

   public static char[][] unwrapBig(CharIterator i, long max) {
      if (max < 0L) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         char[][] array = CharBigArrays.newBigArray(16L);
         long j = 0L;

         while (max-- != 0L && i.hasNext()) {
            if (j == BigArrays.length(array)) {
               array = BigArrays.grow(array, j + 1L);
            }

            BigArrays.set(array, j++, i.nextChar());
         }

         return BigArrays.trim(array, j);
      }
   }

   public static char[][] unwrapBig(CharIterator i) {
      return unwrapBig(i, Long.MAX_VALUE);
   }

   public static long unwrap(CharIterator i, CharCollection c) {
      long n;
      for (n = 0L; i.hasNext(); n++) {
         c.add(i.nextChar());
      }

      return n;
   }

   public static int pour(CharIterator i, CharCollection s, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         int j = max;

         while (j-- != 0 && i.hasNext()) {
            s.add(i.nextChar());
         }

         return max - j - 1;
      }
   }

   public static int pour(CharIterator i, CharCollection s) {
      return pour(i, s, Integer.MAX_VALUE);
   }

   public static CharList pour(CharIterator i, int max) {
      CharArrayList l = new CharArrayList();
      pour(i, l, max);
      l.trim();
      return l;
   }

   public static CharList pour(CharIterator i) {
      return pour(i, Integer.MAX_VALUE);
   }

   public static CharIterator asCharIterator(Iterator i) {
      return (CharIterator)(i instanceof CharIterator ? (CharIterator)i : new CharIterators.IteratorWrapper(i));
   }

   public static CharIterator narrow(OfInt i) {
      return new CharIterators.CheckedPrimitiveIteratorWrapper(i);
   }

   public static CharIterator uncheckedNarrow(OfInt i) {
      return new CharIterators.PrimitiveIteratorWrapper(i);
   }

   public static IntIterator widen(CharIterator i) {
      return IntIterators.wrap(i);
   }

   public static CharListIterator asCharIterator(ListIterator i) {
      return (CharListIterator)(i instanceof CharListIterator ? (CharListIterator)i : new CharIterators.ListIteratorWrapper(i));
   }

   public static boolean any(CharIterator iterator, CharPredicate predicate) {
      return indexOf(iterator, predicate) != -1;
   }

   public static boolean any(CharIterator iterator, IntPredicate predicate) {
      return any(iterator, predicate instanceof CharPredicate ? (CharPredicate)predicate : predicate::test);
   }

   public static boolean all(CharIterator iterator, CharPredicate predicate) {
      Objects.requireNonNull(predicate);

      while (iterator.hasNext()) {
         if (!predicate.test(iterator.nextChar())) {
            return false;
         }
      }

      return true;
   }

   public static boolean all(CharIterator iterator, IntPredicate predicate) {
      return all(iterator, predicate instanceof CharPredicate ? (CharPredicate)predicate : predicate::test);
   }

   public static int indexOf(CharIterator iterator, CharPredicate predicate) {
      for (int i = 0; iterator.hasNext(); i++) {
         if (predicate.test(iterator.nextChar())) {
            return i;
         }
      }

      return -1;
   }

   public static int indexOf(CharIterator iterator, IntPredicate predicate) {
      return indexOf(iterator, predicate instanceof CharPredicate ? (CharPredicate)predicate : predicate::test);
   }

   public static CharListIterator fromTo(char from, char to) {
      return new CharIterators.IntervalIterator(from, to);
   }

   public static CharIterator concat(CharIterator... a) {
      return concat(a, 0, a.length);
   }

   public static CharIterator concat(CharIterator[] a, int offset, int length) {
      return new CharIterators.IteratorConcatenator(a, offset, length);
   }

   public static CharIterator unmodifiable(CharIterator i) {
      return new CharIterators.UnmodifiableIterator(i);
   }

   public static CharBidirectionalIterator unmodifiable(CharBidirectionalIterator i) {
      return new CharIterators.UnmodifiableBidirectionalIterator(i);
   }

   public static CharListIterator unmodifiable(CharListIterator i) {
      return new CharIterators.UnmodifiableListIterator(i);
   }

   public abstract static class AbstractIndexBasedIterator extends AbstractCharIterator {
      protected final int minPos;
      protected int pos;
      protected int lastReturned;

      protected AbstractIndexBasedIterator(int minPos, int initialPos) {
         this.minPos = minPos;
         this.pos = initialPos;
      }

      protected abstract char get(int var1);

      protected abstract void remove(int var1);

      protected abstract int getMaxPos();

      @Override
      public boolean hasNext() {
         return this.pos < this.getMaxPos();
      }

      @Override
      public char nextChar() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.get(this.lastReturned = this.pos++);
         }
      }

      @Override
      public void remove() {
         if (this.lastReturned == -1) {
            throw new IllegalStateException();
         } else {
            this.remove(this.lastReturned);
            if (this.lastReturned < this.pos) {
               this.pos--;
            }

            this.lastReturned = -1;
         }
      }

      @Override
      public void forEachRemaining(CharConsumer action) {
         while (this.pos < this.getMaxPos()) {
            action.accept(this.get(this.lastReturned = this.pos++));
         }
      }

      @Override
      public int skip(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            int max = this.getMaxPos();
            int remaining = max - this.pos;
            if (n < remaining) {
               this.pos += n;
            } else {
               n = remaining;
               this.pos = max;
            }

            this.lastReturned = this.pos - 1;
            return n;
         }
      }
   }

   public abstract static class AbstractIndexBasedListIterator extends CharIterators.AbstractIndexBasedIterator implements CharListIterator {
      protected AbstractIndexBasedListIterator(int minPos, int initialPos) {
         super(minPos, initialPos);
      }

      protected abstract void add(int var1, char var2);

      protected abstract void set(int var1, char var2);

      @Override
      public boolean hasPrevious() {
         return this.pos > this.minPos;
      }

      @Override
      public char previousChar() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return this.get(this.lastReturned = --this.pos);
         }
      }

      @Override
      public int nextIndex() {
         return this.pos;
      }

      @Override
      public int previousIndex() {
         return this.pos - 1;
      }

      @Override
      public void add(char k) {
         this.add(this.pos++, k);
         this.lastReturned = -1;
      }

      @Override
      public void set(char k) {
         if (this.lastReturned == -1) {
            throw new IllegalStateException();
         } else {
            this.set(this.lastReturned, k);
         }
      }

      @Override
      public int back(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            int remaining = this.pos - this.minPos;
            if (n < remaining) {
               this.pos -= n;
            } else {
               n = remaining;
               this.pos = this.minPos;
            }

            this.lastReturned = this.pos;
            return n;
         }
      }
   }

   private static class ArrayIterator implements CharListIterator {
      private final char[] array;
      private final int offset;
      private final int length;
      private int curr;

      public ArrayIterator(char[] array, int offset, int length) {
         this.array = array;
         this.offset = offset;
         this.length = length;
      }

      @Override
      public boolean hasNext() {
         return this.curr < this.length;
      }

      @Override
      public boolean hasPrevious() {
         return this.curr > 0;
      }

      @Override
      public char nextChar() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + this.curr++];
         }
      }

      @Override
      public char previousChar() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + --this.curr];
         }
      }

      @Override
      public void forEachRemaining(CharConsumer action) {
         Objects.requireNonNull(action);

         while (this.curr < this.length) {
            action.accept(this.array[this.offset + this.curr]);
            this.curr++;
         }
      }

      @Override
      public int skip(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (n <= this.length - this.curr) {
            this.curr += n;
            return n;
         } else {
            n = this.length - this.curr;
            this.curr = this.length;
            return n;
         }
      }

      @Override
      public int back(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (n <= this.curr) {
            this.curr -= n;
            return n;
         } else {
            n = this.curr;
            this.curr = 0;
            return n;
         }
      }

      @Override
      public int nextIndex() {
         return this.curr;
      }

      @Override
      public int previousIndex() {
         return this.curr - 1;
      }
   }

   private static class CheckedPrimitiveIteratorWrapper extends CharIterators.PrimitiveIteratorWrapper {
      public CheckedPrimitiveIteratorWrapper(OfInt i) {
         super(i);
      }

      @Override
      public char nextChar() {
         return SafeMath.safeIntToChar(this.i.nextInt());
      }

      @Override
      public void forEachRemaining(CharConsumer action) {
         this.i.forEachRemaining(value -> action.accept(SafeMath.safeIntToChar(value)));
      }
   }

   public static class EmptyIterator implements CharListIterator, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyIterator() {
      }

      @Override
      public boolean hasNext() {
         return false;
      }

      @Override
      public boolean hasPrevious() {
         return false;
      }

      @Override
      public char nextChar() {
         throw new NoSuchElementException();
      }

      @Override
      public char previousChar() {
         throw new NoSuchElementException();
      }

      @Override
      public int nextIndex() {
         return 0;
      }

      @Override
      public int previousIndex() {
         return -1;
      }

      @Override
      public int skip(int n) {
         return 0;
      }

      @Override
      public int back(int n) {
         return 0;
      }

      @Override
      public void forEachRemaining(CharConsumer action) {
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Character> action) {
      }

      @Override
      public Object clone() {
         return CharIterators.EMPTY_ITERATOR;
      }

      private Object readResolve() {
         return CharIterators.EMPTY_ITERATOR;
      }
   }

   private static class IntervalIterator implements CharListIterator {
      private final char from;
      private final char to;
      char curr;

      public IntervalIterator(char from, char to) {
         this.from = this.curr = from;
         this.to = to;
      }

      @Override
      public boolean hasNext() {
         return this.curr < this.to;
      }

      @Override
      public boolean hasPrevious() {
         return this.curr > this.from;
      }

      @Override
      public char nextChar() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.curr++;
         }
      }

      @Override
      public char previousChar() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return --this.curr;
         }
      }

      @Override
      public void forEachRemaining(CharConsumer action) {
         Objects.requireNonNull(action);

         while (this.curr < this.to) {
            action.accept(this.curr);
            this.curr++;
         }
      }

      @Override
      public int nextIndex() {
         return this.curr - this.from;
      }

      @Override
      public int previousIndex() {
         return this.curr - this.from - 1;
      }

      @Override
      public int skip(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (this.curr + n <= this.to) {
            this.curr = (char)(this.curr + n);
            return n;
         } else {
            n = this.to - this.curr;
            this.curr = this.to;
            return n;
         }
      }

      @Override
      public int back(int n) {
         if (this.curr - n >= this.from) {
            this.curr = (char)(this.curr - n);
            return n;
         } else {
            n = this.curr - this.from;
            this.curr = this.from;
            return n;
         }
      }
   }

   private static class IteratorConcatenator implements CharIterator {
      final CharIterator[] a;
      int offset;
      int length;
      int lastOffset = -1;

      public IteratorConcatenator(CharIterator[] a, int offset, int length) {
         this.a = a;
         this.offset = offset;
         this.length = length;
         this.advance();
      }

      private void advance() {
         while (this.length != 0 && !this.a[this.offset].hasNext()) {
            this.length--;
            this.offset++;
         }
      }

      @Override
      public boolean hasNext() {
         return this.length > 0;
      }

      @Override
      public char nextChar() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            char next = this.a[this.lastOffset = this.offset].nextChar();
            this.advance();
            return next;
         }
      }

      @Override
      public void forEachRemaining(CharConsumer action) {
         while (this.length > 0) {
            this.a[this.lastOffset = this.offset].forEachRemaining(action);
            this.advance();
         }
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Character> action) {
         while (this.length > 0) {
            this.a[this.lastOffset = this.offset].forEachRemaining(action);
            this.advance();
         }
      }

      @Override
      public void remove() {
         if (this.lastOffset == -1) {
            throw new IllegalStateException();
         } else {
            this.a[this.lastOffset].remove();
         }
      }

      @Override
      public int skip(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            this.lastOffset = -1;

            int skipped;
            for (skipped = 0; skipped < n && this.length != 0; this.offset++) {
               skipped += this.a[this.offset].skip(n - skipped);
               if (this.a[this.offset].hasNext()) {
                  break;
               }

               this.length--;
            }

            return skipped;
         }
      }
   }

   private static class IteratorWrapper implements CharIterator {
      final Iterator<Character> i;

      public IteratorWrapper(Iterator<Character> i) {
         this.i = i;
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }

      @Override
      public void remove() {
         this.i.remove();
      }

      @Override
      public char nextChar() {
         return this.i.next();
      }

      @Override
      public void forEachRemaining(CharConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Character> action) {
         this.i.forEachRemaining(action);
      }
   }

   private static class ListIteratorWrapper implements CharListIterator {
      final ListIterator<Character> i;

      public ListIteratorWrapper(ListIterator<Character> i) {
         this.i = i;
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }

      @Override
      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      @Override
      public int nextIndex() {
         return this.i.nextIndex();
      }

      @Override
      public int previousIndex() {
         return this.i.previousIndex();
      }

      @Override
      public void set(char k) {
         this.i.set(k);
      }

      @Override
      public void add(char k) {
         this.i.add(k);
      }

      @Override
      public void remove() {
         this.i.remove();
      }

      @Override
      public char nextChar() {
         return this.i.next();
      }

      @Override
      public char previousChar() {
         return this.i.previous();
      }

      @Override
      public void forEachRemaining(CharConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Character> action) {
         this.i.forEachRemaining(action);
      }
   }

   private static class PrimitiveIteratorWrapper implements CharIterator {
      final OfInt i;

      public PrimitiveIteratorWrapper(OfInt i) {
         this.i = i;
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }

      @Override
      public void remove() {
         this.i.remove();
      }

      @Override
      public char nextChar() {
         return (char)this.i.nextInt();
      }

      @Override
      public void forEachRemaining(CharConsumer action) {
         this.i.forEachRemaining(action);
      }
   }

   private static class SingletonIterator implements CharListIterator {
      private final char element;
      private byte curr;

      public SingletonIterator(char element) {
         this.element = element;
      }

      @Override
      public boolean hasNext() {
         return this.curr == 0;
      }

      @Override
      public boolean hasPrevious() {
         return this.curr == 1;
      }

      @Override
      public char nextChar() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      @Override
      public char previousChar() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 0;
            return this.element;
         }
      }

      @Override
      public void forEachRemaining(CharConsumer action) {
         Objects.requireNonNull(action);
         if (this.curr == 0) {
            action.accept(this.element);
            this.curr = 1;
         }
      }

      @Override
      public int nextIndex() {
         return this.curr;
      }

      @Override
      public int previousIndex() {
         return this.curr - 1;
      }

      @Override
      public int back(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (n != 0 && this.curr >= 1) {
            this.curr = 1;
            return 1;
         } else {
            return 0;
         }
      }

      @Override
      public int skip(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (n != 0 && this.curr <= 0) {
            this.curr = 0;
            return 1;
         } else {
            return 0;
         }
      }
   }

   public static class UnmodifiableBidirectionalIterator implements CharBidirectionalIterator {
      protected final CharBidirectionalIterator i;

      public UnmodifiableBidirectionalIterator(CharBidirectionalIterator i) {
         this.i = i;
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }

      @Override
      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      @Override
      public char nextChar() {
         return this.i.nextChar();
      }

      @Override
      public char previousChar() {
         return this.i.previousChar();
      }

      @Override
      public void forEachRemaining(CharConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Character> action) {
         this.i.forEachRemaining(action);
      }
   }

   public static class UnmodifiableIterator implements CharIterator {
      protected final CharIterator i;

      public UnmodifiableIterator(CharIterator i) {
         this.i = i;
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }

      @Override
      public char nextChar() {
         return this.i.nextChar();
      }

      @Override
      public void forEachRemaining(CharConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Character> action) {
         this.i.forEachRemaining(action);
      }
   }

   public static class UnmodifiableListIterator implements CharListIterator {
      protected final CharListIterator i;

      public UnmodifiableListIterator(CharListIterator i) {
         this.i = i;
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }

      @Override
      public boolean hasPrevious() {
         return this.i.hasPrevious();
      }

      @Override
      public char nextChar() {
         return this.i.nextChar();
      }

      @Override
      public char previousChar() {
         return this.i.previousChar();
      }

      @Override
      public int nextIndex() {
         return this.i.nextIndex();
      }

      @Override
      public int previousIndex() {
         return this.i.previousIndex();
      }

      @Override
      public void forEachRemaining(CharConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Character> action) {
         this.i.forEachRemaining(action);
      }
   }
}
