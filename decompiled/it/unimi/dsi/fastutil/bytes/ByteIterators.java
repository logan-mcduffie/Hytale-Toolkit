package it.unimi.dsi.fastutil.bytes;

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

public final class ByteIterators {
   public static final ByteIterators.EmptyIterator EMPTY_ITERATOR = new ByteIterators.EmptyIterator();

   private ByteIterators() {
   }

   public static ByteListIterator singleton(byte element) {
      return new ByteIterators.SingletonIterator(element);
   }

   public static ByteListIterator wrap(byte[] array, int offset, int length) {
      ByteArrays.ensureOffsetLength(array, offset, length);
      return new ByteIterators.ArrayIterator(array, offset, length);
   }

   public static ByteListIterator wrap(byte[] array) {
      return new ByteIterators.ArrayIterator(array, 0, array.length);
   }

   public static int unwrap(ByteIterator i, byte[] array, int offset, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else if (offset >= 0 && offset + max <= array.length) {
         int j = max;

         while (j-- != 0 && i.hasNext()) {
            array[offset++] = i.nextByte();
         }

         return max - j - 1;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static int unwrap(ByteIterator i, byte[] array) {
      return unwrap(i, array, 0, array.length);
   }

   public static byte[] unwrap(ByteIterator i, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         byte[] array = new byte[16];
         int j = 0;

         while (max-- != 0 && i.hasNext()) {
            if (j == array.length) {
               array = ByteArrays.grow(array, j + 1);
            }

            array[j++] = i.nextByte();
         }

         return ByteArrays.trim(array, j);
      }
   }

   public static byte[] unwrap(ByteIterator i) {
      return unwrap(i, Integer.MAX_VALUE);
   }

   public static long unwrap(ByteIterator i, byte[][] array, long offset, long max) {
      if (max < 0L) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else if (offset >= 0L && offset + max <= BigArrays.length(array)) {
         long j = max;

         while (j-- != 0L && i.hasNext()) {
            BigArrays.set(array, offset++, i.nextByte());
         }

         return max - j - 1L;
      } else {
         throw new IllegalArgumentException();
      }
   }

   public static long unwrap(ByteIterator i, byte[][] array) {
      return unwrap(i, array, 0L, BigArrays.length(array));
   }

   public static int unwrap(ByteIterator i, ByteCollection c, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         int j = max;

         while (j-- != 0 && i.hasNext()) {
            c.add(i.nextByte());
         }

         return max - j - 1;
      }
   }

   public static byte[][] unwrapBig(ByteIterator i, long max) {
      if (max < 0L) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         byte[][] array = ByteBigArrays.newBigArray(16L);
         long j = 0L;

         while (max-- != 0L && i.hasNext()) {
            if (j == BigArrays.length(array)) {
               array = BigArrays.grow(array, j + 1L);
            }

            BigArrays.set(array, j++, i.nextByte());
         }

         return BigArrays.trim(array, j);
      }
   }

   public static byte[][] unwrapBig(ByteIterator i) {
      return unwrapBig(i, Long.MAX_VALUE);
   }

   public static long unwrap(ByteIterator i, ByteCollection c) {
      long n;
      for (n = 0L; i.hasNext(); n++) {
         c.add(i.nextByte());
      }

      return n;
   }

   public static int pour(ByteIterator i, ByteCollection s, int max) {
      if (max < 0) {
         throw new IllegalArgumentException("The maximum number of elements (" + max + ") is negative");
      } else {
         int j = max;

         while (j-- != 0 && i.hasNext()) {
            s.add(i.nextByte());
         }

         return max - j - 1;
      }
   }

   public static int pour(ByteIterator i, ByteCollection s) {
      return pour(i, s, Integer.MAX_VALUE);
   }

   public static ByteList pour(ByteIterator i, int max) {
      ByteArrayList l = new ByteArrayList();
      pour(i, l, max);
      l.trim();
      return l;
   }

   public static ByteList pour(ByteIterator i) {
      return pour(i, Integer.MAX_VALUE);
   }

   public static ByteIterator asByteIterator(Iterator i) {
      return (ByteIterator)(i instanceof ByteIterator ? (ByteIterator)i : new ByteIterators.IteratorWrapper(i));
   }

   public static ByteIterator narrow(OfInt i) {
      return new ByteIterators.CheckedPrimitiveIteratorWrapper(i);
   }

   public static ByteIterator uncheckedNarrow(OfInt i) {
      return new ByteIterators.PrimitiveIteratorWrapper(i);
   }

   public static IntIterator widen(ByteIterator i) {
      return IntIterators.wrap(i);
   }

   public static ByteListIterator asByteIterator(ListIterator i) {
      return (ByteListIterator)(i instanceof ByteListIterator ? (ByteListIterator)i : new ByteIterators.ListIteratorWrapper(i));
   }

   public static boolean any(ByteIterator iterator, BytePredicate predicate) {
      return indexOf(iterator, predicate) != -1;
   }

   public static boolean any(ByteIterator iterator, IntPredicate predicate) {
      return any(iterator, predicate instanceof BytePredicate ? (BytePredicate)predicate : predicate::test);
   }

   public static boolean all(ByteIterator iterator, BytePredicate predicate) {
      Objects.requireNonNull(predicate);

      while (iterator.hasNext()) {
         if (!predicate.test(iterator.nextByte())) {
            return false;
         }
      }

      return true;
   }

   public static boolean all(ByteIterator iterator, IntPredicate predicate) {
      return all(iterator, predicate instanceof BytePredicate ? (BytePredicate)predicate : predicate::test);
   }

   public static int indexOf(ByteIterator iterator, BytePredicate predicate) {
      for (int i = 0; iterator.hasNext(); i++) {
         if (predicate.test(iterator.nextByte())) {
            return i;
         }
      }

      return -1;
   }

   public static int indexOf(ByteIterator iterator, IntPredicate predicate) {
      return indexOf(iterator, predicate instanceof BytePredicate ? (BytePredicate)predicate : predicate::test);
   }

   public static ByteListIterator fromTo(byte from, byte to) {
      return new ByteIterators.IntervalIterator(from, to);
   }

   public static ByteIterator concat(ByteIterator... a) {
      return concat(a, 0, a.length);
   }

   public static ByteIterator concat(ByteIterator[] a, int offset, int length) {
      return new ByteIterators.IteratorConcatenator(a, offset, length);
   }

   public static ByteIterator unmodifiable(ByteIterator i) {
      return new ByteIterators.UnmodifiableIterator(i);
   }

   public static ByteBidirectionalIterator unmodifiable(ByteBidirectionalIterator i) {
      return new ByteIterators.UnmodifiableBidirectionalIterator(i);
   }

   public static ByteListIterator unmodifiable(ByteListIterator i) {
      return new ByteIterators.UnmodifiableListIterator(i);
   }

   public abstract static class AbstractIndexBasedIterator extends AbstractByteIterator {
      protected final int minPos;
      protected int pos;
      protected int lastReturned;

      protected AbstractIndexBasedIterator(int minPos, int initialPos) {
         this.minPos = minPos;
         this.pos = initialPos;
      }

      protected abstract byte get(int var1);

      protected abstract void remove(int var1);

      protected abstract int getMaxPos();

      @Override
      public boolean hasNext() {
         return this.pos < this.getMaxPos();
      }

      @Override
      public byte nextByte() {
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
      public void forEachRemaining(ByteConsumer action) {
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

   public abstract static class AbstractIndexBasedListIterator extends ByteIterators.AbstractIndexBasedIterator implements ByteListIterator {
      protected AbstractIndexBasedListIterator(int minPos, int initialPos) {
         super(minPos, initialPos);
      }

      protected abstract void add(int var1, byte var2);

      protected abstract void set(int var1, byte var2);

      @Override
      public boolean hasPrevious() {
         return this.pos > this.minPos;
      }

      @Override
      public byte previousByte() {
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
      public void add(byte k) {
         this.add(this.pos++, k);
         this.lastReturned = -1;
      }

      @Override
      public void set(byte k) {
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

   private static class ArrayIterator implements ByteListIterator {
      private final byte[] array;
      private final int offset;
      private final int length;
      private int curr;

      public ArrayIterator(byte[] array, int offset, int length) {
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
      public byte nextByte() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + this.curr++];
         }
      }

      @Override
      public byte previousByte() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return this.array[this.offset + --this.curr];
         }
      }

      @Override
      public void forEachRemaining(ByteConsumer action) {
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

   private static class CheckedPrimitiveIteratorWrapper extends ByteIterators.PrimitiveIteratorWrapper {
      public CheckedPrimitiveIteratorWrapper(OfInt i) {
         super(i);
      }

      @Override
      public byte nextByte() {
         return SafeMath.safeIntToByte(this.i.nextInt());
      }

      @Override
      public void forEachRemaining(ByteConsumer action) {
         this.i.forEachRemaining(value -> action.accept(SafeMath.safeIntToByte(value)));
      }
   }

   public static class EmptyIterator implements ByteListIterator, Serializable, Cloneable {
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
      public byte nextByte() {
         throw new NoSuchElementException();
      }

      @Override
      public byte previousByte() {
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
      public void forEachRemaining(ByteConsumer action) {
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Byte> action) {
      }

      @Override
      public Object clone() {
         return ByteIterators.EMPTY_ITERATOR;
      }

      private Object readResolve() {
         return ByteIterators.EMPTY_ITERATOR;
      }
   }

   private static class IntervalIterator implements ByteListIterator {
      private final byte from;
      private final byte to;
      byte curr;

      public IntervalIterator(byte from, byte to) {
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
      public byte nextByte() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            return this.curr++;
         }
      }

      @Override
      public byte previousByte() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            return --this.curr;
         }
      }

      @Override
      public void forEachRemaining(ByteConsumer action) {
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
            this.curr = (byte)(this.curr + n);
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
            this.curr = (byte)(this.curr - n);
            return n;
         } else {
            n = this.curr - this.from;
            this.curr = this.from;
            return n;
         }
      }
   }

   private static class IteratorConcatenator implements ByteIterator {
      final ByteIterator[] a;
      int offset;
      int length;
      int lastOffset = -1;

      public IteratorConcatenator(ByteIterator[] a, int offset, int length) {
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
      public byte nextByte() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            byte next = this.a[this.lastOffset = this.offset].nextByte();
            this.advance();
            return next;
         }
      }

      @Override
      public void forEachRemaining(ByteConsumer action) {
         while (this.length > 0) {
            this.a[this.lastOffset = this.offset].forEachRemaining(action);
            this.advance();
         }
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Byte> action) {
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

   private static class IteratorWrapper implements ByteIterator {
      final Iterator<Byte> i;

      public IteratorWrapper(Iterator<Byte> i) {
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
      public byte nextByte() {
         return this.i.next();
      }

      @Override
      public void forEachRemaining(ByteConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Byte> action) {
         this.i.forEachRemaining(action);
      }
   }

   private static class ListIteratorWrapper implements ByteListIterator {
      final ListIterator<Byte> i;

      public ListIteratorWrapper(ListIterator<Byte> i) {
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
      public void set(byte k) {
         this.i.set(k);
      }

      @Override
      public void add(byte k) {
         this.i.add(k);
      }

      @Override
      public void remove() {
         this.i.remove();
      }

      @Override
      public byte nextByte() {
         return this.i.next();
      }

      @Override
      public byte previousByte() {
         return this.i.previous();
      }

      @Override
      public void forEachRemaining(ByteConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Byte> action) {
         this.i.forEachRemaining(action);
      }
   }

   private static class PrimitiveIteratorWrapper implements ByteIterator {
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
      public byte nextByte() {
         return (byte)this.i.nextInt();
      }

      @Override
      public void forEachRemaining(ByteConsumer action) {
         this.i.forEachRemaining(action);
      }
   }

   private static class SingletonIterator implements ByteListIterator {
      private final byte element;
      private byte curr;

      public SingletonIterator(byte element) {
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
      public byte nextByte() {
         if (!this.hasNext()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 1;
            return this.element;
         }
      }

      @Override
      public byte previousByte() {
         if (!this.hasPrevious()) {
            throw new NoSuchElementException();
         } else {
            this.curr = 0;
            return this.element;
         }
      }

      @Override
      public void forEachRemaining(ByteConsumer action) {
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

   public static class UnmodifiableBidirectionalIterator implements ByteBidirectionalIterator {
      protected final ByteBidirectionalIterator i;

      public UnmodifiableBidirectionalIterator(ByteBidirectionalIterator i) {
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
      public byte nextByte() {
         return this.i.nextByte();
      }

      @Override
      public byte previousByte() {
         return this.i.previousByte();
      }

      @Override
      public void forEachRemaining(ByteConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Byte> action) {
         this.i.forEachRemaining(action);
      }
   }

   public static class UnmodifiableIterator implements ByteIterator {
      protected final ByteIterator i;

      public UnmodifiableIterator(ByteIterator i) {
         this.i = i;
      }

      @Override
      public boolean hasNext() {
         return this.i.hasNext();
      }

      @Override
      public byte nextByte() {
         return this.i.nextByte();
      }

      @Override
      public void forEachRemaining(ByteConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Byte> action) {
         this.i.forEachRemaining(action);
      }
   }

   public static class UnmodifiableListIterator implements ByteListIterator {
      protected final ByteListIterator i;

      public UnmodifiableListIterator(ByteListIterator i) {
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
      public byte nextByte() {
         return this.i.nextByte();
      }

      @Override
      public byte previousByte() {
         return this.i.previousByte();
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
      public void forEachRemaining(ByteConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Byte> action) {
         this.i.forEachRemaining(action);
      }
   }
}
