package it.unimi.dsi.fastutil.bytes;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.ints.IntSpliterator;
import it.unimi.dsi.fastutil.ints.IntSpliterators;
import java.io.Serializable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Spliterator;
import java.util.Spliterator.OfInt;
import java.util.function.Consumer;
import java.util.function.IntConsumer;
import java.util.function.IntPredicate;

public final class ByteSpliterators {
   static final int BASE_SPLITERATOR_CHARACTERISTICS = 256;
   public static final int COLLECTION_SPLITERATOR_CHARACTERISTICS = 320;
   public static final int LIST_SPLITERATOR_CHARACTERISTICS = 16720;
   public static final int SET_SPLITERATOR_CHARACTERISTICS = 321;
   private static final int SORTED_CHARACTERISTICS = 20;
   public static final int SORTED_SET_SPLITERATOR_CHARACTERISTICS = 341;
   public static final ByteSpliterators.EmptySpliterator EMPTY_SPLITERATOR = new ByteSpliterators.EmptySpliterator();

   private ByteSpliterators() {
   }

   public static ByteSpliterator singleton(byte element) {
      return new ByteSpliterators.SingletonSpliterator(element);
   }

   public static ByteSpliterator singleton(byte element, ByteComparator comparator) {
      return new ByteSpliterators.SingletonSpliterator(element, comparator);
   }

   public static ByteSpliterator wrap(byte[] array, int offset, int length) {
      ByteArrays.ensureOffsetLength(array, offset, length);
      return new ByteSpliterators.ArraySpliterator(array, offset, length, 0);
   }

   public static ByteSpliterator wrap(byte[] array) {
      return new ByteSpliterators.ArraySpliterator(array, 0, array.length, 0);
   }

   public static ByteSpliterator wrap(byte[] array, int offset, int length, int additionalCharacteristics) {
      ByteArrays.ensureOffsetLength(array, offset, length);
      return new ByteSpliterators.ArraySpliterator(array, offset, length, additionalCharacteristics);
   }

   public static ByteSpliterator wrapPreSorted(byte[] array, int offset, int length, int additionalCharacteristics, ByteComparator comparator) {
      ByteArrays.ensureOffsetLength(array, offset, length);
      return new ByteSpliterators.ArraySpliteratorWithComparator(array, offset, length, additionalCharacteristics, comparator);
   }

   public static ByteSpliterator wrapPreSorted(byte[] array, int offset, int length, ByteComparator comparator) {
      return wrapPreSorted(array, offset, length, 0, comparator);
   }

   public static ByteSpliterator wrapPreSorted(byte[] array, ByteComparator comparator) {
      return wrapPreSorted(array, 0, array.length, comparator);
   }

   public static ByteSpliterator asByteSpliterator(Spliterator i) {
      return (ByteSpliterator)(i instanceof ByteSpliterator ? (ByteSpliterator)i : new ByteSpliterators.SpliteratorWrapper(i));
   }

   public static ByteSpliterator asByteSpliterator(Spliterator i, ByteComparator comparatorOverride) {
      if (i instanceof ByteSpliterator) {
         throw new IllegalArgumentException("Cannot override comparator on instance that is already a " + ByteSpliterator.class.getSimpleName());
      } else {
         return (ByteSpliterator)(i instanceof OfInt
            ? new ByteSpliterators.PrimitiveSpliteratorWrapperWithComparator((OfInt)i, comparatorOverride)
            : new ByteSpliterators.SpliteratorWrapperWithComparator(i, comparatorOverride));
      }
   }

   public static ByteSpliterator narrow(OfInt i) {
      return new ByteSpliterators.PrimitiveSpliteratorWrapper(i);
   }

   public static IntSpliterator widen(ByteSpliterator i) {
      return IntSpliterators.wrap(i);
   }

   public static void onEachMatching(ByteSpliterator spliterator, BytePredicate predicate, ByteConsumer action) {
      Objects.requireNonNull(predicate);
      Objects.requireNonNull(action);
      spliterator.forEachRemaining(value -> {
         if (predicate.test(value)) {
            action.accept(value);
         }
      });
   }

   public static void onEachMatching(ByteSpliterator spliterator, IntPredicate predicate, IntConsumer action) {
      Objects.requireNonNull(predicate);
      Objects.requireNonNull(action);
      spliterator.forEachRemaining(value -> {
         if (predicate.test(value)) {
            action.accept(value);
         }
      });
   }

   public static ByteSpliterator fromTo(byte from, byte to) {
      return new ByteSpliterators.IntervalSpliterator(from, to);
   }

   public static ByteSpliterator concat(ByteSpliterator... a) {
      return concat(a, 0, a.length);
   }

   public static ByteSpliterator concat(ByteSpliterator[] a, int offset, int length) {
      return new ByteSpliterators.SpliteratorConcatenator(a, offset, length);
   }

   public static ByteSpliterator asSpliterator(ByteIterator iter, long size, int additionalCharacterisitcs) {
      return new ByteSpliterators.SpliteratorFromIterator(iter, size, additionalCharacterisitcs);
   }

   public static ByteSpliterator asSpliteratorFromSorted(ByteIterator iter, long size, int additionalCharacterisitcs, ByteComparator comparator) {
      return new ByteSpliterators.SpliteratorFromIteratorWithComparator(iter, size, additionalCharacterisitcs, comparator);
   }

   public static ByteSpliterator asSpliteratorUnknownSize(ByteIterator iter, int characterisitcs) {
      return new ByteSpliterators.SpliteratorFromIterator(iter, characterisitcs);
   }

   public static ByteSpliterator asSpliteratorFromSortedUnknownSize(ByteIterator iter, int additionalCharacterisitcs, ByteComparator comparator) {
      return new ByteSpliterators.SpliteratorFromIteratorWithComparator(iter, additionalCharacterisitcs, comparator);
   }

   public static ByteIterator asIterator(ByteSpliterator spliterator) {
      return new ByteSpliterators.IteratorFromSpliterator(spliterator);
   }

   public abstract static class AbstractIndexBasedSpliterator extends AbstractByteSpliterator {
      protected int pos;

      protected AbstractIndexBasedSpliterator(int initialPos) {
         this.pos = initialPos;
      }

      protected abstract byte get(int var1);

      protected abstract int getMaxPos();

      protected abstract ByteSpliterator makeForSplit(int var1, int var2);

      protected int computeSplitPoint() {
         return this.pos + (this.getMaxPos() - this.pos) / 2;
      }

      private void splitPointCheck(int splitPoint, int observedMax) {
         if (splitPoint < this.pos || splitPoint > observedMax) {
            throw new IndexOutOfBoundsException(
               "splitPoint " + splitPoint + " outside of range of current position " + this.pos + " and range end " + observedMax
            );
         }
      }

      @Override
      public int characteristics() {
         return 16720;
      }

      @Override
      public long estimateSize() {
         return (long)this.getMaxPos() - this.pos;
      }

      public boolean tryAdvance(ByteConsumer action) {
         if (this.pos >= this.getMaxPos()) {
            return false;
         } else {
            action.accept(this.get(this.pos++));
            return true;
         }
      }

      public void forEachRemaining(ByteConsumer action) {
         for (int max = this.getMaxPos(); this.pos < max; this.pos++) {
            action.accept(this.get(this.pos));
         }
      }

      @Override
      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            int max = this.getMaxPos();
            if (this.pos >= max) {
               return 0L;
            } else {
               int remaining = max - this.pos;
               if (n < remaining) {
                  this.pos = SafeMath.safeLongToInt(this.pos + n);
                  return n;
               } else {
                  n = remaining;
                  this.pos = max;
                  return n;
               }
            }
         }
      }

      @Override
      public ByteSpliterator trySplit() {
         int max = this.getMaxPos();
         int splitPoint = this.computeSplitPoint();
         if (splitPoint != this.pos && splitPoint != max) {
            this.splitPointCheck(splitPoint, max);
            int oldPos = this.pos;
            ByteSpliterator maybeSplit = this.makeForSplit(oldPos, splitPoint);
            if (maybeSplit != null) {
               this.pos = splitPoint;
            }

            return maybeSplit;
         } else {
            return null;
         }
      }
   }

   private static class ArraySpliterator implements ByteSpliterator {
      private static final int BASE_CHARACTERISTICS = 16720;
      final byte[] array;
      private final int offset;
      private int length;
      private int curr;
      final int characteristics;

      public ArraySpliterator(byte[] array, int offset, int length, int additionalCharacteristics) {
         this.array = array;
         this.offset = offset;
         this.length = length;
         this.characteristics = 16720 | additionalCharacteristics;
      }

      public boolean tryAdvance(ByteConsumer action) {
         if (this.curr >= this.length) {
            return false;
         } else {
            Objects.requireNonNull(action);
            action.accept(this.array[this.offset + this.curr++]);
            return true;
         }
      }

      @Override
      public long estimateSize() {
         return this.length - this.curr;
      }

      @Override
      public int characteristics() {
         return this.characteristics;
      }

      protected ByteSpliterators.ArraySpliterator makeForSplit(int newOffset, int newLength) {
         return new ByteSpliterators.ArraySpliterator(this.array, newOffset, newLength, this.characteristics);
      }

      @Override
      public ByteSpliterator trySplit() {
         int retLength = this.length - this.curr >> 1;
         if (retLength <= 1) {
            return null;
         } else {
            int myNewCurr = this.curr + retLength;
            int retOffset = this.offset + this.curr;
            this.curr = myNewCurr;
            return this.makeForSplit(retOffset, retLength);
         }
      }

      public void forEachRemaining(ByteConsumer action) {
         Objects.requireNonNull(action);

         while (this.curr < this.length) {
            action.accept(this.array[this.offset + this.curr]);
            this.curr++;
         }
      }

      @Override
      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (this.curr >= this.length) {
            return 0L;
         } else {
            int remaining = this.length - this.curr;
            if (n < remaining) {
               this.curr = SafeMath.safeLongToInt(this.curr + n);
               return n;
            } else {
               n = remaining;
               this.curr = this.length;
               return n;
            }
         }
      }
   }

   private static class ArraySpliteratorWithComparator extends ByteSpliterators.ArraySpliterator {
      private final ByteComparator comparator;

      public ArraySpliteratorWithComparator(byte[] array, int offset, int length, int additionalCharacteristics, ByteComparator comparator) {
         super(array, offset, length, additionalCharacteristics | 20);
         this.comparator = comparator;
      }

      protected ByteSpliterators.ArraySpliteratorWithComparator makeForSplit(int newOffset, int newLength) {
         return new ByteSpliterators.ArraySpliteratorWithComparator(this.array, newOffset, newLength, this.characteristics, this.comparator);
      }

      @Override
      public ByteComparator getComparator() {
         return this.comparator;
      }
   }

   public abstract static class EarlyBindingSizeIndexBasedSpliterator extends ByteSpliterators.AbstractIndexBasedSpliterator {
      protected final int maxPos;

      protected EarlyBindingSizeIndexBasedSpliterator(int initialPos, int maxPos) {
         super(initialPos);
         this.maxPos = maxPos;
      }

      @Override
      protected final int getMaxPos() {
         return this.maxPos;
      }
   }

   public static class EmptySpliterator implements ByteSpliterator, Serializable, Cloneable {
      private static final long serialVersionUID = 8379247926738230492L;
      private static final int CHARACTERISTICS = 16448;

      protected EmptySpliterator() {
      }

      public boolean tryAdvance(ByteConsumer action) {
         return false;
      }

      @Deprecated
      @Override
      public boolean tryAdvance(Consumer<? super Byte> action) {
         return false;
      }

      @Override
      public ByteSpliterator trySplit() {
         return null;
      }

      @Override
      public long estimateSize() {
         return 0L;
      }

      @Override
      public int characteristics() {
         return 16448;
      }

      public void forEachRemaining(ByteConsumer action) {
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Byte> action) {
      }

      @Override
      public Object clone() {
         return ByteSpliterators.EMPTY_SPLITERATOR;
      }

      private Object readResolve() {
         return ByteSpliterators.EMPTY_SPLITERATOR;
      }
   }

   private static class IntervalSpliterator implements ByteSpliterator {
      private static final int DONT_SPLIT_THRESHOLD = 2;
      private static final int CHARACTERISTICS = 17749;
      private byte curr;
      private byte to;

      public IntervalSpliterator(byte from, byte to) {
         this.curr = from;
         this.to = to;
      }

      public boolean tryAdvance(ByteConsumer action) {
         if (this.curr >= this.to) {
            return false;
         } else {
            action.accept(this.curr++);
            return true;
         }
      }

      public void forEachRemaining(ByteConsumer action) {
         Objects.requireNonNull(action);

         while (this.curr < this.to) {
            action.accept(this.curr);
            this.curr++;
         }
      }

      @Override
      public long estimateSize() {
         return this.to - this.curr;
      }

      @Override
      public int characteristics() {
         return 17749;
      }

      @Override
      public ByteComparator getComparator() {
         return null;
      }

      @Override
      public ByteSpliterator trySplit() {
         int remaining = this.to - this.curr;
         byte mid = (byte)(this.curr + (remaining >> 1));
         if (remaining >= 0 && remaining <= 2) {
            return null;
         } else {
            byte old_curr = this.curr;
            this.curr = mid;
            return new ByteSpliterators.IntervalSpliterator(old_curr, mid);
         }
      }

      @Override
      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (this.curr >= this.to) {
            return 0L;
         } else {
            long newCurr = this.curr + n;
            if (newCurr <= this.to && newCurr >= this.curr) {
               this.curr = SafeMath.safeLongToByte(newCurr);
               return n;
            } else {
               n = this.to - this.curr;
               this.curr = this.to;
               return n;
            }
         }
      }
   }

   private static final class IteratorFromSpliterator implements ByteIterator, ByteConsumer {
      private final ByteSpliterator spliterator;
      private byte holder = 0;
      private boolean hasPeeked = false;

      IteratorFromSpliterator(ByteSpliterator spliterator) {
         this.spliterator = spliterator;
      }

      @Override
      public void accept(byte item) {
         this.holder = item;
      }

      @Override
      public boolean hasNext() {
         if (this.hasPeeked) {
            return true;
         } else {
            boolean hadElement = this.spliterator.tryAdvance(this);
            if (!hadElement) {
               return false;
            } else {
               this.hasPeeked = true;
               return true;
            }
         }
      }

      @Override
      public byte nextByte() {
         if (this.hasPeeked) {
            this.hasPeeked = false;
            return this.holder;
         } else {
            boolean hadElement = this.spliterator.tryAdvance(this);
            if (!hadElement) {
               throw new NoSuchElementException();
            } else {
               return this.holder;
            }
         }
      }

      @Override
      public void forEachRemaining(ByteConsumer action) {
         if (this.hasPeeked) {
            this.hasPeeked = false;
            action.accept(this.holder);
         }

         this.spliterator.forEachRemaining(action);
      }

      @Override
      public int skip(int n) {
         if (n < 0) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            int skipped = 0;
            if (this.hasPeeked) {
               this.hasPeeked = false;
               this.spliterator.skip(1L);
               skipped++;
               n--;
            }

            if (n > 0) {
               skipped += SafeMath.safeLongToInt(this.spliterator.skip(n));
            }

            return skipped;
         }
      }
   }

   public abstract static class LateBindingSizeIndexBasedSpliterator extends ByteSpliterators.AbstractIndexBasedSpliterator {
      protected int maxPos = -1;
      private boolean maxPosFixed;

      protected LateBindingSizeIndexBasedSpliterator(int initialPos) {
         super(initialPos);
         this.maxPosFixed = false;
      }

      protected LateBindingSizeIndexBasedSpliterator(int initialPos, int fixedMaxPos) {
         super(initialPos);
         this.maxPos = fixedMaxPos;
         this.maxPosFixed = true;
      }

      protected abstract int getMaxPosFromBackingStore();

      @Override
      protected final int getMaxPos() {
         return this.maxPosFixed ? this.maxPos : this.getMaxPosFromBackingStore();
      }

      @Override
      public ByteSpliterator trySplit() {
         ByteSpliterator maybeSplit = super.trySplit();
         if (!this.maxPosFixed && maybeSplit != null) {
            this.maxPos = this.getMaxPosFromBackingStore();
            this.maxPosFixed = true;
         }

         return maybeSplit;
      }
   }

   private static class PrimitiveSpliteratorWrapper implements ByteSpliterator {
      final OfInt i;

      public PrimitiveSpliteratorWrapper(OfInt i) {
         this.i = i;
      }

      public boolean tryAdvance(ByteConsumer action) {
         return this.i.tryAdvance(action);
      }

      public void forEachRemaining(ByteConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Override
      public long estimateSize() {
         return this.i.estimateSize();
      }

      @Override
      public int characteristics() {
         return this.i.characteristics();
      }

      @Override
      public ByteComparator getComparator() {
         Comparator<? super Integer> comp = this.i.getComparator();
         return (left, right) -> comp.compare(Integer.valueOf(left), Integer.valueOf(right));
      }

      @Override
      public ByteSpliterator trySplit() {
         OfInt innerSplit = this.i.trySplit();
         return innerSplit == null ? null : new ByteSpliterators.PrimitiveSpliteratorWrapper(innerSplit);
      }
   }

   private static class PrimitiveSpliteratorWrapperWithComparator extends ByteSpliterators.PrimitiveSpliteratorWrapper {
      final ByteComparator comparator;

      public PrimitiveSpliteratorWrapperWithComparator(OfInt i, ByteComparator comparator) {
         super(i);
         this.comparator = comparator;
      }

      @Override
      public ByteComparator getComparator() {
         return this.comparator;
      }

      @Override
      public ByteSpliterator trySplit() {
         OfInt innerSplit = this.i.trySplit();
         return innerSplit == null ? null : new ByteSpliterators.PrimitiveSpliteratorWrapperWithComparator(innerSplit, this.comparator);
      }
   }

   private static class SingletonSpliterator implements ByteSpliterator {
      private final byte element;
      private final ByteComparator comparator;
      private boolean consumed = false;
      private static final int CHARACTERISTICS = 17749;

      public SingletonSpliterator(byte element) {
         this(element, null);
      }

      public SingletonSpliterator(byte element, ByteComparator comparator) {
         this.element = element;
         this.comparator = comparator;
      }

      public boolean tryAdvance(ByteConsumer action) {
         Objects.requireNonNull(action);
         if (this.consumed) {
            return false;
         } else {
            this.consumed = true;
            action.accept(this.element);
            return true;
         }
      }

      @Override
      public ByteSpliterator trySplit() {
         return null;
      }

      @Override
      public long estimateSize() {
         return this.consumed ? 0L : 1L;
      }

      @Override
      public int characteristics() {
         return 17749;
      }

      public void forEachRemaining(ByteConsumer action) {
         Objects.requireNonNull(action);
         if (!this.consumed) {
            this.consumed = true;
            action.accept(this.element);
         }
      }

      @Override
      public ByteComparator getComparator() {
         return this.comparator;
      }

      @Override
      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (n != 0L && !this.consumed) {
            this.consumed = true;
            return 1L;
         } else {
            return 0L;
         }
      }
   }

   private static class SpliteratorConcatenator implements ByteSpliterator {
      private static final int EMPTY_CHARACTERISTICS = 16448;
      private static final int CHARACTERISTICS_NOT_SUPPORTED_WHILE_MULTIPLE = 5;
      final ByteSpliterator[] a;
      int offset;
      int length;
      long remainingEstimatedExceptCurrent = Long.MAX_VALUE;
      int characteristics = 0;

      public SpliteratorConcatenator(ByteSpliterator[] a, int offset, int length) {
         this.a = a;
         this.offset = offset;
         this.length = length;
         this.remainingEstimatedExceptCurrent = this.recomputeRemaining();
         this.characteristics = this.computeCharacteristics();
      }

      private long recomputeRemaining() {
         int curLength = this.length - 1;
         int curOffset = this.offset + 1;
         long result = 0L;

         while (curLength > 0) {
            long cur = this.a[curOffset++].estimateSize();
            curLength--;
            if (cur == Long.MAX_VALUE) {
               return Long.MAX_VALUE;
            }

            result += cur;
            if (result == Long.MAX_VALUE || result < 0L) {
               return Long.MAX_VALUE;
            }
         }

         return result;
      }

      private int computeCharacteristics() {
         if (this.length <= 0) {
            return 16448;
         } else {
            int current = -1;
            int curLength = this.length;
            int curOffset = this.offset;
            if (curLength > 1) {
               current &= -6;
            }

            while (curLength > 0) {
               current &= this.a[curOffset++].characteristics();
               curLength--;
            }

            return current;
         }
      }

      private void advanceNextSpliterator() {
         if (this.length <= 0) {
            throw new AssertionError("advanceNextSpliterator() called with none remaining");
         } else {
            this.offset++;
            this.length--;
            this.remainingEstimatedExceptCurrent = this.recomputeRemaining();
         }
      }

      public boolean tryAdvance(ByteConsumer action) {
         boolean any = false;

         while (this.length > 0) {
            if (this.a[this.offset].tryAdvance(action)) {
               any = true;
               break;
            }

            this.advanceNextSpliterator();
         }

         return any;
      }

      public void forEachRemaining(ByteConsumer action) {
         while (this.length > 0) {
            this.a[this.offset].forEachRemaining(action);
            this.advanceNextSpliterator();
         }
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Byte> action) {
         while (this.length > 0) {
            this.a[this.offset].forEachRemaining(action);
            this.advanceNextSpliterator();
         }
      }

      @Override
      public long estimateSize() {
         if (this.length <= 0) {
            return 0L;
         } else {
            long est = this.a[this.offset].estimateSize() + this.remainingEstimatedExceptCurrent;
            return est < 0L ? Long.MAX_VALUE : est;
         }
      }

      @Override
      public int characteristics() {
         return this.characteristics;
      }

      @Override
      public ByteComparator getComparator() {
         if (this.length == 1 && (this.characteristics & 4) != 0) {
            return this.a[this.offset].getComparator();
         } else {
            throw new IllegalStateException();
         }
      }

      @Override
      public ByteSpliterator trySplit() {
         switch (this.length) {
            case 0:
               return null;
            case 1: {
               ByteSpliterator split = this.a[this.offset].trySplit();
               this.characteristics = this.a[this.offset].characteristics();
               return split;
            }
            case 2: {
               ByteSpliterator split = this.a[this.offset++];
               this.length--;
               this.characteristics = this.a[this.offset].characteristics();
               this.remainingEstimatedExceptCurrent = 0L;
               return split;
            }
            default:
               int mid = this.length >> 1;
               int ret_offset = this.offset;
               int new_offset = this.offset + mid;
               int new_length = this.length - mid;
               this.offset = new_offset;
               this.length = new_length;
               this.remainingEstimatedExceptCurrent = this.recomputeRemaining();
               this.characteristics = this.computeCharacteristics();
               return new ByteSpliterators.SpliteratorConcatenator(this.a, ret_offset, mid);
         }
      }

      @Override
      public long skip(long n) {
         long skipped = 0L;
         if (this.length <= 0) {
            return 0L;
         } else {
            while (skipped < n && this.length >= 0) {
               long curSkipped = this.a[this.offset].skip(n - skipped);
               skipped += curSkipped;
               if (skipped < n) {
                  this.advanceNextSpliterator();
               }
            }

            return skipped;
         }
      }
   }

   private static class SpliteratorFromIterator implements ByteSpliterator {
      private static final int BATCH_INCREMENT_SIZE = 1024;
      private static final int BATCH_MAX_SIZE = 33554432;
      private final ByteIterator iter;
      final int characteristics;
      private final boolean knownSize;
      private long size = Long.MAX_VALUE;
      private int nextBatchSize = 1024;
      private ByteSpliterator delegate = null;

      SpliteratorFromIterator(ByteIterator iter, int characteristics) {
         this.iter = iter;
         this.characteristics = 256 | characteristics;
         this.knownSize = false;
      }

      SpliteratorFromIterator(ByteIterator iter, long size, int additionalCharacteristics) {
         this.iter = iter;
         this.knownSize = true;
         this.size = size;
         if ((additionalCharacteristics & 4096) != 0) {
            this.characteristics = 256 | additionalCharacteristics;
         } else {
            this.characteristics = 16704 | additionalCharacteristics;
         }
      }

      public boolean tryAdvance(ByteConsumer action) {
         if (this.delegate != null) {
            boolean hadRemaining = this.delegate.tryAdvance(action);
            if (!hadRemaining) {
               this.delegate = null;
            }

            return hadRemaining;
         } else if (!this.iter.hasNext()) {
            return false;
         } else {
            this.size--;
            action.accept(this.iter.nextByte());
            return true;
         }
      }

      public void forEachRemaining(ByteConsumer action) {
         if (this.delegate != null) {
            this.delegate.forEachRemaining(action);
            this.delegate = null;
         }

         this.iter.forEachRemaining(action);
         this.size = 0L;
      }

      @Override
      public long estimateSize() {
         if (this.delegate != null) {
            return this.delegate.estimateSize();
         } else if (!this.iter.hasNext()) {
            return 0L;
         } else {
            return this.knownSize && this.size >= 0L ? this.size : Long.MAX_VALUE;
         }
      }

      @Override
      public int characteristics() {
         return this.characteristics;
      }

      protected ByteSpliterator makeForSplit(byte[] batch, int len) {
         return ByteSpliterators.wrap(batch, 0, len, this.characteristics);
      }

      @Override
      public ByteSpliterator trySplit() {
         if (!this.iter.hasNext()) {
            return null;
         } else {
            int batchSizeEst = this.knownSize && this.size > 0L ? (int)Math.min((long)this.nextBatchSize, this.size) : this.nextBatchSize;
            byte[] batch = new byte[batchSizeEst];

            int actualSeen;
            for (actualSeen = 0; actualSeen < batchSizeEst && this.iter.hasNext(); this.size--) {
               batch[actualSeen++] = this.iter.nextByte();
            }

            if (batchSizeEst < this.nextBatchSize && this.iter.hasNext()) {
               for (batch = Arrays.copyOf(batch, this.nextBatchSize); this.iter.hasNext() && actualSeen < this.nextBatchSize; this.size--) {
                  batch[actualSeen++] = this.iter.nextByte();
               }
            }

            this.nextBatchSize = Math.min(33554432, this.nextBatchSize + 1024);
            ByteSpliterator split = this.makeForSplit(batch, actualSeen);
            if (!this.iter.hasNext()) {
               this.delegate = split;
               return split.trySplit();
            } else {
               return split;
            }
         }
      }

      @Override
      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else if (this.iter instanceof ByteBigListIterator) {
            long skipped = ((ByteBigListIterator)this.iter).skip(n);
            this.size -= skipped;
            return skipped;
         } else {
            long skippedSoFar = 0L;

            while (skippedSoFar < n && this.iter.hasNext()) {
               int skipped = this.iter.skip(SafeMath.safeLongToInt(Math.min(n, 2147483647L)));
               this.size -= skipped;
               skippedSoFar += skipped;
            }

            return skippedSoFar;
         }
      }
   }

   private static class SpliteratorFromIteratorWithComparator extends ByteSpliterators.SpliteratorFromIterator {
      private final ByteComparator comparator;

      SpliteratorFromIteratorWithComparator(ByteIterator iter, int additionalCharacteristics, ByteComparator comparator) {
         super(iter, additionalCharacteristics | 20);
         this.comparator = comparator;
      }

      SpliteratorFromIteratorWithComparator(ByteIterator iter, long size, int additionalCharacteristics, ByteComparator comparator) {
         super(iter, size, additionalCharacteristics | 20);
         this.comparator = comparator;
      }

      @Override
      public ByteComparator getComparator() {
         return this.comparator;
      }

      @Override
      protected ByteSpliterator makeForSplit(byte[] array, int len) {
         return ByteSpliterators.wrapPreSorted(array, 0, len, this.characteristics, this.comparator);
      }
   }

   private static class SpliteratorWrapper implements ByteSpliterator {
      final Spliterator<Byte> i;

      public SpliteratorWrapper(Spliterator<Byte> i) {
         this.i = i;
      }

      public boolean tryAdvance(ByteConsumer action) {
         return this.i.tryAdvance(action);
      }

      @Deprecated
      @Override
      public boolean tryAdvance(Consumer<? super Byte> action) {
         return this.i.tryAdvance(action);
      }

      public void forEachRemaining(ByteConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Byte> action) {
         this.i.forEachRemaining(action);
      }

      @Override
      public long estimateSize() {
         return this.i.estimateSize();
      }

      @Override
      public int characteristics() {
         return this.i.characteristics();
      }

      @Override
      public ByteComparator getComparator() {
         return ByteComparators.asByteComparator(this.i.getComparator());
      }

      @Override
      public ByteSpliterator trySplit() {
         Spliterator<Byte> innerSplit = this.i.trySplit();
         return innerSplit == null ? null : new ByteSpliterators.SpliteratorWrapper(innerSplit);
      }
   }

   private static class SpliteratorWrapperWithComparator extends ByteSpliterators.SpliteratorWrapper {
      final ByteComparator comparator;

      public SpliteratorWrapperWithComparator(Spliterator<Byte> i, ByteComparator comparator) {
         super(i);
         this.comparator = comparator;
      }

      @Override
      public ByteComparator getComparator() {
         return this.comparator;
      }

      @Override
      public ByteSpliterator trySplit() {
         Spliterator<Byte> innerSplit = this.i.trySplit();
         return innerSplit == null ? null : new ByteSpliterators.SpliteratorWrapperWithComparator(innerSplit, this.comparator);
      }
   }
}
