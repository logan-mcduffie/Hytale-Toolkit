package it.unimi.dsi.fastutil.booleans;

import it.unimi.dsi.fastutil.BigArrays;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

public final class BooleanIterators {
   public static final BooleanIterators.EmptyIterator EMPTY_ITERATOR = new BooleanIterators.EmptyIterator();

   private BooleanIterators() {
   }

   public static BooleanListIterator singleton(boolean element) {
      return new BooleanIterators.SingletonIterator(element);
   }

   public static BooleanListIterator wrap(boolean[] array, int offset, int length) {
      BooleanArrays.ensureOffsetLength(array, offset, length);
      return new BooleanIterators.ArrayIterator(array, offset, length);
   }

   public static BooleanListIterator wrap(boolean[] array) {
      return new BooleanIterators.ArrayIterator(array, 0, array.length);
   }

   public static int unwrap(BooleanIterator i, boolean[] array, int offset, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else if (offset >= 0 && offset + max <= array.length) {
         int j = max;

         while (j-- != 0 && i.hasNext()) {
            array[offset++] = i.nextBoolean();
         }

         return max - j - 1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int unwrap(BooleanIterator i, boolean[] array) {
      return unwrap(i, array, 0, array.length);
   }

   public static boolean[] unwrap(BooleanIterator i, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         boolean[] array = new boolean[16];
         int j = 0;

         while (max-- != 0 && i.hasNext()) {
            if (j == array.length) {
               array = BooleanArrays.grow(array, j + 1);
            }

            array[j++] = i.nextBoolean();
         }

         return BooleanArrays.trim(array, j);
      }
   }

   public static boolean[] unwrap(BooleanIterator i) {
      return unwrap(i, Integer.MAX_VALUE);
   }

   public static long unwrap(BooleanIterator i, boolean[][] array, long offset, long max) {
      if (max < 0L) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else if (offset >= 0L && offset + max <= BigArrays.length(array)) {
         long j = max;

         while (j-- != 0L && i.hasNext()) {
            BigArrays.set(array, offset++, i.nextBoolean());
         }

         return max - j - 1L;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static long unwrap(BooleanIterator i, boolean[][] array) {
      return unwrap(i, array, 0L, BigArrays.length(array));
   }

   public static int unwrap(BooleanIterator i, BooleanCollection c, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         int j = max;

         while (j-- != 0 && i.hasNext()) {
            c.add(i.nextBoolean());
         }

         return max - j - 1;
      }
   }

   public static boolean[][] unwrapBig(BooleanIterator i, long max) {
      if (max < 0L) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         boolean[][] array = BooleanBigArrays.newBigArray(16L);
         long j = 0L;

         while (max-- != 0L && i.hasNext()) {
            if (j == BigArrays.length(array)) {
               array = BigArrays.grow(array, j + 1L);
            }

            BigArrays.set(array, j++, i.nextBoolean());
         }

         return BigArrays.trim(array, j);
      }
   }

   public static boolean[][] unwrapBig(BooleanIterator i) {
      return unwrapBig(i, Long.MAX_VALUE);
   }

   public static long unwrap(BooleanIterator i, BooleanCollection c) {
      long n;
      for (n = 0L; i.hasNext(); n++) {
         c.add(i.nextBoolean());
      }

      return n;
   }

   public static int pour(BooleanIterator i, BooleanCollection s, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         int j = max;

         while (j-- != 0 && i.hasNext()) {
            s.add(i.nextBoolean());
         }

         return max - j - 1;
      }
   }

   public static int pour(BooleanIterator i, BooleanCollection s) {
      return pour(i, s, Integer.MAX_VALUE);
   }

   public static BooleanList pour(BooleanIterator i, int max) {
      BooleanArrayList l = new BooleanArrayList();
      pour(i, l, max);
      l.trim();
      return l;
   }

   public static BooleanList pour(BooleanIterator i) {
      return pour(i, Integer.MAX_VALUE);
   }

   public static BooleanIterator asBooleanIterator(Iterator i) {
      return (BooleanIterator)(i instanceof BooleanIterator ? (BooleanIterator)i : new BooleanIterators.IteratorWrapper(i));
   }

   public static BooleanListIterator asBooleanIterator(ListIterator i) {
      return (BooleanListIterator)(i instanceof BooleanListIterator ? (BooleanListIterator)i : new BooleanIterators.ListIteratorWrapper(i));
   }

   public static boolean any(BooleanIterator iterator, BooleanPredicate predicate) {
      return indexOf(iterator, predicate) != -1;
   }

   public static boolean all(BooleanIterator iterator, BooleanPredicate predicate) {
      Objects.requireNonNull(predicate);

      while (iterator.hasNext()) {
         if (!predicate.test(iterator.nextBoolean())) {
            return false;
         }
      }

      return true;
   }

   public static int indexOf(BooleanIterator iterator, BooleanPredicate predicate) {
      for (int i = 0; iterator.hasNext(); i++) {
         if (predicate.test(iterator.nextBoolean())) {
            return i;
         }
      }

      return -1;
   }

   public static BooleanIterator concat(BooleanIterator... a) {
      return concat(a, 0, a.length);
   }

   public static BooleanIterator concat(BooleanIterator[] a, int offset, int length) {
      return new BooleanIterators.IteratorConcatenator(a, offset, length);
   }

   public static BooleanIterator unmodifiable(BooleanIterator i) {
      return new BooleanIterators.UnmodifiableIterator(i);
   }

   public static BooleanBidirectionalIterator unmodifiable(BooleanBidirectionalIterator i) {
      return new BooleanIterators.UnmodifiableBidirectionalIterator(i);
   }

   public static BooleanListIterator unmodifiable(BooleanListIterator i) {
      return new BooleanIterators.UnmodifiableListIterator(i);
   }

   public abstract static class AbstractIndexBasedIterator extends AbstractBooleanIterator {
      protected final int minPos;
      protected int pos;
      protected int lastReturned;

      protected AbstractIndexBasedIterator(int minPos, int initialPos) {
         this.minPos = minPos;
         this.pos = initialPos;
      }

      protected abstract boolean get(int var1);

      protected abstract void remove(int var1);

      protected abstract int getMaxPos();

      @Override
      public boolean hasNext() {
         return this.pos < this.getMaxPos();
      }

      @Override
      public boolean nextBoolean() {
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
      public void forEachRemaining(BooleanConsumer action) {
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

   public abstract static class AbstractIndexBasedListIterator extends BooleanIterators.AbstractIndexBasedIterator implements BooleanListIterator {
      protected AbstractIndexBasedListIterator(int minPos, int initialPos) {
         super(minPos, initialPos);
      }

      protected abstract void add(int var1, boolean var2);

      protected abstract void set(int var1, boolean var2);

      @Override
      public boolean hasPrevious() {
         return this.pos > this.minPos;
      }

      @Override
      public boolean previousBoolean() {
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
      public void add(boolean k) {
         this.add(this.pos++, k);
         this.lastReturned = -1;
      }

      @Override
      public void set(boolean k) {
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

   private static class ArrayIterator implements BooleanListIterator {
      private final boolean[] array;
      private final int offset;
      private final int length;
      private int curr;

      public ArrayIterator(boolean[] array, int offset, int length) {
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
      public boolean nextBoolean() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + this.curr++];
         }
      }

      @Override
      public boolean previousBoolean() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + --this.curr];
         }
      }

      @Override
      public void forEachRemaining(BooleanConsumer action) {
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

   public static class EmptyIterator implements BooleanListIterator, Serializable, Cloneable {
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
      public boolean nextBoolean() {
         throw new NoSuchElementException();
      }

      @Override
      public boolean previousBoolean() {
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
      public void forEachRemaining(BooleanConsumer action) {
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Boolean> action) {
      }

      @Override
      public Object clone() {
         return BooleanIterators.EMPTY_ITERATOR;
      }

      private Object readResolve() {
         return BooleanIterators.EMPTY_ITERATOR;
      }
   }

   private static class IteratorConcatenator implements BooleanIterator {
      final BooleanIterator[] a;
      int offset;
      int length;
      int lastOffset = -1;

      public IteratorConcatenator(BooleanIterator[] a, int offset, int length) {
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
      public boolean nextBoolean() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            boolean next = this.a[this.lastOffset = this.offset].nextBoolean();
            this.advance();
            return next;
         }
      }

      @Override
      public void forEachRemaining(BooleanConsumer action) {
         while (this.length > 0) {
            this.a[this.lastOffset = this.offset].forEachRemaining(action);
            this.advance();
         }
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Boolean> action) {
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

   private static class IteratorWrapper implements BooleanIterator {
      final Iterator<Boolean> i;

      public IteratorWrapper(Iterator<Boolean> i) {
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
      public boolean nextBoolean() {
         return this.i.next();
      }

      @Override
      public void forEachRemaining(BooleanConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Boolean> action) {
         this.i.forEachRemaining(action);
      }
   }

   private static class ListIteratorWrapper implements BooleanListIterator {
      final ListIterator<Boolean> i;

      public ListIteratorWrapper(ListIterator<Boolean> i) {
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
      public void set(boolean k) {
         this.i.set(k);
      }

      @Override
      public void add(boolean k) {
         this.i.add(k);
      }

      @Override
      public void remove() {
         this.i.remove();
      }

      @Override
      public boolean nextBoolean() {
         return this.i.next();
      }

      @Override
      public boolean previousBoolean() {
         return this.i.previous();
      }

      @Override
      public void forEachRemaining(BooleanConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Boolean> action) {
         this.i.forEachRemaining(action);
      }
   }

   private static class SingletonIterator implements BooleanListIterator {
      private final boolean element;
      private byte curr;

      public SingletonIterator(boolean element) {
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
      public boolean nextBoolean() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      @Override
      public boolean previousBoolean() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 0;
            return this.element;
         }
      }

      @Override
      public void forEachRemaining(BooleanConsumer action) {
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

   public static class UnmodifiableBidirectionalIterator implements BooleanBidirectionalIterator {
      protected final BooleanBidirectionalIterator i;

      public UnmodifiableBidirectionalIterator(BooleanBidirectionalIterator i) {
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
      public boolean nextBoolean() {
         return this.i.nextBoolean();
      }

      @Override
      public boolean previousBoolean() {
         return this.i.previousBoolean();
      }

      @Override
      public void forEachRemaining(BooleanConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Boolean> action) {
         this.i.forEachRemaining(action);
      }
   }

   public static class UnmodifiableIterator implements BooleanIterator {
      protected final BooleanIterator i;

      public UnmodifiableIterator(BooleanIterator i) {
         this.i = i;
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }

      @Override
      public boolean nextBoolean() {
         return this.i.nextBoolean();
      }

      @Override
      public void forEachRemaining(BooleanConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Boolean> action) {
         this.i.forEachRemaining(action);
      }
   }

   public static class UnmodifiableListIterator implements BooleanListIterator {
      protected final BooleanListIterator i;

      public UnmodifiableListIterator(BooleanListIterator i) {
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
      public boolean nextBoolean() {
         return this.i.nextBoolean();
      }

      @Override
      public boolean previousBoolean() {
         return this.i.previousBoolean();
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
      public void forEachRemaining(BooleanConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Boolean> action) {
         this.i.forEachRemaining(action);
      }
   }
}
