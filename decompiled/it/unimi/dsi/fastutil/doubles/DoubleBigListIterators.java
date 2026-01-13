package it.unimi.dsi.fastutil.doubles;

import it.unimi.dsi.fastutil.SafeMath;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.function.Consumer;

public final class DoubleBigListIterators {
   public static final DoubleBigListIterators.EmptyBigListIterator EMPTY_BIG_LIST_ITERATOR = new DoubleBigListIterators.EmptyBigListIterator();

   private DoubleBigListIterators() {
   }

   public static DoubleBigListIterator singleton(double element) {
      return new DoubleBigListIterators.SingletonBigListIterator(element);
   }

   public static DoubleBigListIterator unmodifiable(DoubleBigListIterator i) {
      return new DoubleBigListIterators.UnmodifiableBigListIterator(i);
   }

   public static DoubleBigListIterator asBigListIterator(DoubleListIterator i) {
      return new DoubleBigListIterators.BigListIteratorListIterator(i);
   }

   public abstract static class AbstractIndexBasedBigIterator extends AbstractDoubleIterator {
      protected final long minPos;
      protected long pos;
      protected long lastReturned;

      protected AbstractIndexBasedBigIterator(long minPos, long initialPos) {
         this.minPos = minPos;
         this.pos = initialPos;
      }

      protected abstract double get(long var1);

      protected abstract void remove(long var1);

      protected abstract long getMaxPos();

      @Override
      public boolean hasNext() {
         return this.pos < this.getMaxPos();
      }

      @Override
      public double nextDouble() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.get(this.lastReturned = this.pos++);
         }
      }

      @Override
      public void remove() {
         if (this.lastReturned == -1L) {
            throw new IllegalStateException();
         } else {
            this.remove(this.lastReturned);
            if (this.lastReturned < this.pos) {
               this.pos--;
            }

            this.lastReturned = -1L;
         }
      }

      @Override
      public void forEachRemaining(java.util.function.DoubleConsumer action) {
         while (this.pos < this.getMaxPos()) {
            action.accept(this.get(this.lastReturned = this.pos++));
         }
      }

      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            long max = this.getMaxPos();
            long remaining = max - this.pos;
            if (n < remaining) {
               this.pos += n;
            } else {
               n = remaining;
               this.pos = max;
            }

            this.lastReturned = this.pos - 1L;
            return n;
         }
      }

      @Override
      public int skip(int n) {
         return SafeMath.safeLongToInt(this.skip((long)n));
      }
   }

   public abstract static class AbstractIndexBasedBigListIterator extends DoubleBigListIterators.AbstractIndexBasedBigIterator implements DoubleBigListIterator {
      protected AbstractIndexBasedBigListIterator(long minPos, long initialPos) {
         super(minPos, initialPos);
      }

      protected abstract void add(long var1, double var3);

      protected abstract void set(long var1, double var3);

      @Override
      public boolean hasPrevious() {
         return this.pos > this.minPos;
      }

      @Override
      public double previousDouble() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return this.get(this.lastReturned = --this.pos);
         }
      }

      @Override
      public long nextIndex() {
         return this.pos;
      }

      @Override
      public long previousIndex() {
         return this.pos - 1L;
      }

      @Override
      public void add(double k) {
         this.add(this.pos++, k);
         this.lastReturned = -1L;
      }

      @Override
      public void set(double k) {
         if (this.lastReturned == -1L) {
            throw new IllegalStateException();
         } else {
            this.set(this.lastReturned, k);
         }
      }

      @Override
      public long back(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            long remaining = this.pos - this.minPos;
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

   public static class BigListIteratorListIterator implements DoubleBigListIterator {
      protected final DoubleListIterator i;

      protected BigListIteratorListIterator(DoubleListIterator i) {
         this.i = i;
      }

      private int intDisplacement(long n) {
         if (n >= -2147483648L && n <= 2147483647L) {
            return (int)n;
         } else {
            throw new IndexOutOfBoundsException("This big iterator is restricted to 32-bit displacements");
         }
      }

      @Override
      public void set(double ok) {
         this.i.set(ok);
      }

      @Override
      public void add(double ok) {
         this.i.add(ok);
      }

      @Override
      public int back(int n) {
         return this.i.back(n);
      }

      @Override
      public long back(long n) {
         return this.i.back(this.intDisplacement(n));
      }

      @Override
      public void remove() {
         this.i.remove();
      }

      @Override
      public int skip(int n) {
         return this.i.skip(n);
      }

      @Override
      public long skip(long n) {
         return this.i.skip(this.intDisplacement(n));
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
      public double nextDouble() {
         return this.i.nextDouble();
      }

      @Override
      public double previousDouble() {
         return this.i.previousDouble();
      }

      @Override
      public long nextIndex() {
         return this.i.nextIndex();
      }

      @Override
      public long previousIndex() {
         return this.i.previousIndex();
      }

      @Override
      public void forEachRemaining(java.util.function.DoubleConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Double> action) {
         this.i.forEachRemaining(action);
      }
   }

   public static class EmptyBigListIterator implements DoubleBigListIterator, Serializable, Cloneable {
      private static final long serialVersionUID = -7046029254386353129L;

      protected EmptyBigListIterator() {
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
      public double nextDouble() {
         throw new NoSuchElementException();
      }

      @Override
      public double previousDouble() {
         throw new NoSuchElementException();
      }

      @Override
      public long nextIndex() {
         return 0L;
      }

      @Override
      public long previousIndex() {
         return -1L;
      }

      @Override
      public long skip(long n) {
         return 0L;
      }

      @Override
      public long back(long n) {
         return 0L;
      }

      @Override
      public Object clone() {
         return DoubleBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }

      @Override
      public void forEachRemaining(java.util.function.DoubleConsumer action) {
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Double> action) {
      }

      private Object readResolve() {
         return DoubleBigListIterators.EMPTY_BIG_LIST_ITERATOR;
      }
   }

   private static class SingletonBigListIterator implements DoubleBigListIterator {
      private final double element;
      private int curr;

      public SingletonBigListIterator(double element) {
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
      public double nextDouble() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      @Override
      public double previousDouble() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 0;
            return this.element;
         }
      }

      @Override
      public void forEachRemaining(java.util.function.DoubleConsumer action) {
         Objects.requireNonNull(action);
         if (this.curr == 0) {
            action.accept(this.element);
            this.curr = 1;
         }
      }

      @Override
      public long nextIndex() {
         return this.curr;
      }

      @Override
      public long previousIndex() {
         return this.curr - 1;
      }

      @Override
      public long back(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (n != 0L && this.curr >= 1) {
            this.curr = 1;
            return 1L;
         } else {
            return 0L;
         }
      }

      @Override
      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (n != 0L && this.curr <= 0) {
            this.curr = 0;
            return 1L;
         } else {
            return 0L;
         }
      }
   }

   public static class UnmodifiableBigListIterator implements DoubleBigListIterator {
      protected final DoubleBigListIterator i;

      public UnmodifiableBigListIterator(DoubleBigListIterator i) {
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
      public double nextDouble() {
         return this.i.nextDouble();
      }

      @Override
      public double previousDouble() {
         return this.i.previousDouble();
      }

      @Override
      public long nextIndex() {
         return this.i.nextIndex();
      }

      @Override
      public long previousIndex() {
         return this.i.previousIndex();
      }

      @Override
      public void forEachRemaining(java.util.function.DoubleConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Double> action) {
         this.i.forEachRemaining(action);
      }
   }
}
