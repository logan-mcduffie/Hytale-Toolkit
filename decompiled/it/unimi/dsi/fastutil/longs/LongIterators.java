package it.unimi.dsi.fastutil.longs;

import it.unimi.dsi.fastutil.BigArrays;
import it.unimi.dsi.fastutil.bytes.ByteIterator;
import it.unimi.dsi.fastutil.chars.CharIterator;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.shorts.ShortIterator;
import java.io.Serializable;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.PrimitiveIterator.OfLong;
import java.util.function.Consumer;

public final class LongIterators {
   public static final LongIterators.EmptyIterator EMPTY_ITERATOR = new LongIterators.EmptyIterator();

   private LongIterators() {
   }

   public static LongListIterator singleton(long element) {
      return new LongIterators.SingletonIterator(element);
   }

   public static LongListIterator wrap(long[] array, int offset, int length) {
      LongArrays.ensureOffsetLength(array, offset, length);
      return new LongIterators.ArrayIterator(array, offset, length);
   }

   public static LongListIterator wrap(long[] array) {
      return new LongIterators.ArrayIterator(array, 0, array.length);
   }

   public static int unwrap(LongIterator i, long[] array, int offset, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else if (offset >= 0 && offset + max <= array.length) {
         int j = max;

         while (j-- != 0 && i.hasNext()) {
            array[offset++] = i.nextLong();
         }

         return max - j - 1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int unwrap(LongIterator i, long[] array) {
      return unwrap(i, array, 0, array.length);
   }

   public static long[] unwrap(LongIterator i, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         long[] array = new long[16];
         int j = 0;

         while (max-- != 0 && i.hasNext()) {
            if (j == array.length) {
               array = LongArrays.grow(array, j + 1);
            }

            array[j++] = i.nextLong();
         }

         return LongArrays.trim(array, j);
      }
   }

   public static long[] unwrap(LongIterator i) {
      return unwrap(i, Integer.MAX_VALUE);
   }

   public static long unwrap(LongIterator i, long[][] array, long offset, long max) {
      if (max < 0L) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else if (offset >= 0L && offset + max <= BigArrays.length(array)) {
         long j = max;

         while (j-- != 0L && i.hasNext()) {
            BigArrays.set(array, offset++, i.nextLong());
         }

         return max - j - 1L;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static long unwrap(LongIterator i, long[][] array) {
      return unwrap(i, array, 0L, BigArrays.length(array));
   }

   public static int unwrap(LongIterator i, LongCollection c, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         int j = max;

         while (j-- != 0 && i.hasNext()) {
            c.add(i.nextLong());
         }

         return max - j - 1;
      }
   }

   public static long[][] unwrapBig(LongIterator i, long max) {
      if (max < 0L) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         long[][] array = LongBigArrays.newBigArray(16L);
         long j = 0L;

         while (max-- != 0L && i.hasNext()) {
            if (j == BigArrays.length(array)) {
               array = BigArrays.grow(array, j + 1L);
            }

            BigArrays.set(array, j++, i.nextLong());
         }

         return BigArrays.trim(array, j);
      }
   }

   public static long[][] unwrapBig(LongIterator i) {
      return unwrapBig(i, Long.MAX_VALUE);
   }

   public static long unwrap(LongIterator i, LongCollection c) {
      long n;
      for (n = 0L; i.hasNext(); n++) {
         c.add(i.nextLong());
      }

      return n;
   }

   public static int pour(LongIterator i, LongCollection s, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         int j = max;

         while (j-- != 0 && i.hasNext()) {
            s.add(i.nextLong());
         }

         return max - j - 1;
      }
   }

   public static int pour(LongIterator i, LongCollection s) {
      return pour(i, s, Integer.MAX_VALUE);
   }

   public static LongList pour(LongIterator i, int max) {
      LongArrayList l = new LongArrayList();
      pour(i, l, max);
      l.trim();
      return l;
   }

   public static LongList pour(LongIterator i) {
      return pour(i, Integer.MAX_VALUE);
   }

   public static LongIterator asLongIterator(Iterator i) {
      if (i instanceof LongIterator) {
         return (LongIterator)i;
      } else {
         return (LongIterator)(i instanceof OfLong ? new LongIterators.PrimitiveIteratorWrapper((OfLong)i) : new LongIterators.IteratorWrapper(i));
      }
   }

   public static LongListIterator asLongIterator(ListIterator i) {
      return (LongListIterator)(i instanceof LongListIterator ? (LongListIterator)i : new LongIterators.ListIteratorWrapper(i));
   }

   public static boolean any(LongIterator iterator, java.util.function.LongPredicate predicate) {
      return indexOf(iterator, predicate) != -1;
   }

   public static boolean all(LongIterator iterator, java.util.function.LongPredicate predicate) {
      Objects.requireNonNull(predicate);

      while (iterator.hasNext()) {
         if (!predicate.test(iterator.nextLong())) {
            return false;
         }
      }

      return true;
   }

   public static int indexOf(LongIterator iterator, java.util.function.LongPredicate predicate) {
      for (int i = 0; iterator.hasNext(); i++) {
         if (predicate.test(iterator.nextLong())) {
            return i;
         }
      }

      return -1;
   }

   public static LongBidirectionalIterator fromTo(long from, long to) {
      return new LongIterators.IntervalIterator(from, to);
   }

   public static LongIterator concat(LongIterator... a) {
      return concat(a, 0, a.length);
   }

   public static LongIterator concat(LongIterator[] a, int offset, int length) {
      return new LongIterators.IteratorConcatenator(a, offset, length);
   }

   public static LongIterator unmodifiable(LongIterator i) {
      return new LongIterators.UnmodifiableIterator(i);
   }

   public static LongBidirectionalIterator unmodifiable(LongBidirectionalIterator i) {
      return new LongIterators.UnmodifiableBidirectionalIterator(i);
   }

   public static LongListIterator unmodifiable(LongListIterator i) {
      return new LongIterators.UnmodifiableListIterator(i);
   }

   public static LongIterator wrap(ByteIterator iterator) {
      return new LongIterators.ByteIteratorWrapper(iterator);
   }

   public static LongIterator wrap(ShortIterator iterator) {
      return new LongIterators.ShortIteratorWrapper(iterator);
   }

   public static LongIterator wrap(CharIterator iterator) {
      return new LongIterators.CharIteratorWrapper(iterator);
   }

   public static LongIterator wrap(IntIterator iterator) {
      return new LongIterators.IntIteratorWrapper(iterator);
   }

   public abstract static class AbstractIndexBasedIterator extends AbstractLongIterator {
      protected final int minPos;
      protected int pos;
      protected int lastReturned;

      protected AbstractIndexBasedIterator(int minPos, int initialPos) {
         this.minPos = minPos;
         this.pos = initialPos;
      }

      protected abstract long get(int var1);

      protected abstract void remove(int var1);

      protected abstract int getMaxPos();

      @Override
      public boolean hasNext() {
         return this.pos < this.getMaxPos();
      }

      @Override
      public long nextLong() {
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
      public void forEachRemaining(java.util.function.LongConsumer action) {
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

   public abstract static class AbstractIndexBasedListIterator extends LongIterators.AbstractIndexBasedIterator implements LongListIterator {
      protected AbstractIndexBasedListIterator(int minPos, int initialPos) {
         super(minPos, initialPos);
      }

      protected abstract void add(int var1, long var2);

      protected abstract void set(int var1, long var2);

      @Override
      public boolean hasPrevious() {
         return this.pos > this.minPos;
      }

      @Override
      public long previousLong() {
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
      public void add(long k) {
         this.add(this.pos++, k);
         this.lastReturned = -1;
      }

      @Override
      public void set(long k) {
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

   private static class ArrayIterator implements LongListIterator {
      private final long[] array;
      private final int offset;
      private final int length;
      private int curr;

      public ArrayIterator(long[] array, int offset, int length) {
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
      public long nextLong() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + this.curr++];
         }
      }

      @Override
      public long previousLong() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + --this.curr];
         }
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
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

   private static final class ByteIteratorWrapper implements LongIterator {
      final ByteIterator iterator;

      public ByteIteratorWrapper(ByteIterator iterator) {
         this.iterator = iterator;
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      @Deprecated
      @Override
      public Long next() {
         return (long)this.iterator.nextByte();
      }

      @Override
      public long nextLong() {
         return this.iterator.nextByte();
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
         this.iterator.forEachRemaining(action::accept);
      }

      @Override
      public void remove() {
         this.iterator.remove();
      }

      @Override
      public int skip(int n) {
         return this.iterator.skip(n);
      }
   }

   private static final class CharIteratorWrapper implements LongIterator {
      final CharIterator iterator;

      public CharIteratorWrapper(CharIterator iterator) {
         this.iterator = iterator;
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      @Deprecated
      @Override
      public Long next() {
         return (long)this.iterator.nextChar();
      }

      @Override
      public long nextLong() {
         return this.iterator.nextChar();
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
         this.iterator.forEachRemaining(action::accept);
      }

      @Override
      public void remove() {
         this.iterator.remove();
      }

      @Override
      public int skip(int n) {
         return this.iterator.skip(n);
      }
   }

   public static class EmptyIterator implements LongListIterator, Serializable, Cloneable {
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
      public long nextLong() {
         throw new NoSuchElementException();
      }

      @Override
      public long previousLong() {
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
      public void forEachRemaining(java.util.function.LongConsumer action) {
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Long> action) {
      }

      @Override
      public Object clone() {
         return LongIterators.EMPTY_ITERATOR;
      }

      private Object readResolve() {
         return LongIterators.EMPTY_ITERATOR;
      }
   }

   private static final class IntIteratorWrapper implements LongIterator {
      final IntIterator iterator;

      public IntIteratorWrapper(IntIterator iterator) {
         this.iterator = iterator;
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      @Deprecated
      @Override
      public Long next() {
         return (long)this.iterator.nextInt();
      }

      @Override
      public long nextLong() {
         return this.iterator.nextInt();
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
         this.iterator.forEachRemaining(action::accept);
      }

      @Override
      public void remove() {
         this.iterator.remove();
      }

      @Override
      public int skip(int n) {
         return this.iterator.skip(n);
      }
   }

   private static class IntervalIterator implements LongBidirectionalIterator {
      private final long from;
      private final long to;
      long curr;

      public IntervalIterator(long from, long to) {
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
      public long nextLong() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.curr++;
         }
      }

      @Override
      public long previousLong() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return --this.curr;
         }
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
         Objects.requireNonNull(action);

         while (this.curr < this.to) {
            action.accept(this.curr);
            this.curr++;
         }
      }

      @Override
      public int skip(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (this.curr + n <= this.to) {
            this.curr += n;
            return n;
         } else {
            n = (int)(this.to - this.curr);
            this.curr = this.to;
            return n;
         }
      }

      @Override
      public int back(int n) {
         if (this.curr - n >= this.from) {
            this.curr -= n;
            return n;
         } else {
            n = (int)(this.curr - this.from);
            this.curr = this.from;
            return n;
         }
      }
   }

   private static class IteratorConcatenator implements LongIterator {
      final LongIterator[] a;
      int offset;
      int length;
      int lastOffset = -1;

      public IteratorConcatenator(LongIterator[] a, int offset, int length) {
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
      public long nextLong() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            long next = this.a[this.lastOffset = this.offset].nextLong();
            this.advance();
            return next;
         }
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
         while (this.length > 0) {
            this.a[this.lastOffset = this.offset].forEachRemaining(action);
            this.advance();
         }
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Long> action) {
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

   private static class IteratorWrapper implements LongIterator {
      final Iterator<Long> i;

      public IteratorWrapper(Iterator<Long> i) {
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
      public long nextLong() {
         return this.i.next();
      }

      @Override
      public void forEachRemaining(LongConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
         this.i.forEachRemaining(action instanceof Consumer ? (Consumer)action : action::accept);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Long> action) {
         this.i.forEachRemaining(action);
      }
   }

   private static class ListIteratorWrapper implements LongListIterator {
      final ListIterator<Long> i;

      public ListIteratorWrapper(ListIterator<Long> i) {
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
      public void set(long k) {
         this.i.set(k);
      }

      @Override
      public void add(long k) {
         this.i.add(k);
      }

      @Override
      public void remove() {
         this.i.remove();
      }

      @Override
      public long nextLong() {
         return this.i.next();
      }

      @Override
      public long previousLong() {
         return this.i.previous();
      }

      @Override
      public void forEachRemaining(LongConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
         this.i.forEachRemaining(action instanceof Consumer ? (Consumer)action : action::accept);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Long> action) {
         this.i.forEachRemaining(action);
      }
   }

   private static class PrimitiveIteratorWrapper implements LongIterator {
      final OfLong i;

      public PrimitiveIteratorWrapper(OfLong i) {
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
      public long nextLong() {
         return this.i.nextLong();
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
         this.i.forEachRemaining(action);
      }
   }

   private static final class ShortIteratorWrapper implements LongIterator {
      final ShortIterator iterator;

      public ShortIteratorWrapper(ShortIterator iterator) {
         this.iterator = iterator;
      }

      @Override
      public boolean hasNext() {
         return this.iterator.hasNext();
      }

      @Deprecated
      @Override
      public Long next() {
         return (long)this.iterator.nextShort();
      }

      @Override
      public long nextLong() {
         return this.iterator.nextShort();
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
         this.iterator.forEachRemaining(action::accept);
      }

      @Override
      public void remove() {
         this.iterator.remove();
      }

      @Override
      public int skip(int n) {
         return this.iterator.skip(n);
      }
   }

   private static class SingletonIterator implements LongListIterator {
      private final long element;
      private byte curr;

      public SingletonIterator(long element) {
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
      public long nextLong() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      @Override
      public long previousLong() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 0;
            return this.element;
         }
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
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

   public static class UnmodifiableBidirectionalIterator implements LongBidirectionalIterator {
      protected final LongBidirectionalIterator i;

      public UnmodifiableBidirectionalIterator(LongBidirectionalIterator i) {
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
      public long nextLong() {
         return this.i.nextLong();
      }

      @Override
      public long previousLong() {
         return this.i.previousLong();
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Long> action) {
         this.i.forEachRemaining(action);
      }
   }

   public static class UnmodifiableIterator implements LongIterator {
      protected final LongIterator i;

      public UnmodifiableIterator(LongIterator i) {
         this.i = i;
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }

      @Override
      public long nextLong() {
         return this.i.nextLong();
      }

      @Override
      public void forEachRemaining(java.util.function.LongConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Long> action) {
         this.i.forEachRemaining(action);
      }
   }

   public static class UnmodifiableListIterator implements LongListIterator {
      protected final LongListIterator i;

      public UnmodifiableListIterator(LongListIterator i) {
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
      public long nextLong() {
         return this.i.nextLong();
      }

      @Override
      public long previousLong() {
         return this.i.previousLong();
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
      public void forEachRemaining(java.util.function.LongConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Long> action) {
         this.i.forEachRemaining(action);
      }
   }
}
