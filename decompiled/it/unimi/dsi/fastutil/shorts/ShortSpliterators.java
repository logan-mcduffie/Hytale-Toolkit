package it.unimi.dsi.fastutil.shorts;

import it.unimi.dsi.fastutil.SafeMath;
import it.unimi.dsi.fastutil.bytes.ByteSpliterator;
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

public final class ShortSpliterators {
   static final int BASE_SPLITERATOR_CHARACTERISTICS = 256;
   public static final int COLLECTION_SPLITERATOR_CHARACTERISTICS = 320;
   public static final int LIST_SPLITERATOR_CHARACTERISTICS = 16720;
   public static final int SET_SPLITERATOR_CHARACTERISTICS = 321;
   private static final int SORTED_CHARACTERISTICS = 20;
   public static final int SORTED_SET_SPLITERATOR_CHARACTERISTICS = 341;
   public static final ShortSpliterators.EmptySpliterator EMPTY_SPLITERATOR = new ShortSpliterators.EmptySpliterator();

   private ShortSpliterators() {
   }

   public static ShortSpliterator singleton(short element) {
      return new ShortSpliterators.SingletonSpliterator(element);
   }

   public static ShortSpliterator singleton(short element, ShortComparator comparator) {
      return new ShortSpliterators.SingletonSpliterator(element, comparator);
   }

   public static ShortSpliterator wrap(short[] array, int offset, int length) {
      ShortArrays.ensureOffsetLength(array, offset, length);
      return new ShortSpliterators.ArraySpliterator(array, offset, length, 0);
   }

   public static ShortSpliterator wrap(short[] array) {
      return new ShortSpliterators.ArraySpliterator(array, 0, array.length, 0);
   }

   public static ShortSpliterator wrap(short[] array, int offset, int length, int additionalCharacteristics) {
      ShortArrays.ensureOffsetLength(array, offset, length);
      return new ShortSpliterators.ArraySpliterator(array, offset, length, additionalCharacteristics);
   }

   public static ShortSpliterator wrapPreSorted(short[] array, int offset, int length, int additionalCharacteristics, ShortComparator comparator) {
      ShortArrays.ensureOffsetLength(array, offset, length);
      return new ShortSpliterators.ArraySpliteratorWithComparator(array, offset, length, additionalCharacteristics, comparator);
   }

   public static ShortSpliterator wrapPreSorted(short[] array, int offset, int length, ShortComparator comparator) {
      return wrapPreSorted(array, offset, length, 0, comparator);
   }

   public static ShortSpliterator wrapPreSorted(short[] array, ShortComparator comparator) {
      return wrapPreSorted(array, 0, array.length, comparator);
   }

   public static ShortSpliterator asShortSpliterator(Spliterator i) {
      return (ShortSpliterator)(i instanceof ShortSpliterator ? (ShortSpliterator)i : new ShortSpliterators.SpliteratorWrapper(i));
   }

   public static ShortSpliterator asShortSpliterator(Spliterator i, ShortComparator comparatorOverride) {
      if (i instanceof ShortSpliterator) {
         throw new IllegalArgumentException("Cannot override comparator on instance that is already a " + ShortSpliterator.class.getSimpleName());
      } else {
         return (ShortSpliterator)(i instanceof OfInt
            ? new ShortSpliterators.PrimitiveSpliteratorWrapperWithComparator((OfInt)i, comparatorOverride)
            : new ShortSpliterators.SpliteratorWrapperWithComparator(i, comparatorOverride));
      }
   }

   public static ShortSpliterator narrow(OfInt i) {
      return new ShortSpliterators.PrimitiveSpliteratorWrapper(i);
   }

   public static IntSpliterator widen(ShortSpliterator i) {
      return IntSpliterators.wrap(i);
   }

   public static void onEachMatching(ShortSpliterator spliterator, ShortPredicate predicate, ShortConsumer action) {
      Objects.requireNonNull(predicate);
      Objects.requireNonNull(action);
      spliterator.forEachRemaining(value -> {
         if (predicate.test(value)) {
            action.accept(value);
         }
      });
   }

   public static void onEachMatching(ShortSpliterator spliterator, IntPredicate predicate, IntConsumer action) {
      Objects.requireNonNull(predicate);
      Objects.requireNonNull(action);
      spliterator.forEachRemaining(value -> {
         if (predicate.test(value)) {
            action.accept(value);
         }
      });
   }

   public static ShortSpliterator fromTo(short from, short to) {
      return new ShortSpliterators.IntervalSpliterator(from, to);
   }

   public static ShortSpliterator concat(ShortSpliterator... a) {
      return concat(a, 0, a.length);
   }

   public static ShortSpliterator concat(ShortSpliterator[] a, int offset, int length) {
      return new ShortSpliterators.SpliteratorConcatenator(a, offset, length);
   }

   public static ShortSpliterator asSpliterator(ShortIterator iter, long size, int additionalCharacterisitcs) {
      return new ShortSpliterators.SpliteratorFromIterator(iter, size, additionalCharacterisitcs);
   }

   public static ShortSpliterator asSpliteratorFromSorted(ShortIterator iter, long size, int additionalCharacterisitcs, ShortComparator comparator) {
      return new ShortSpliterators.SpliteratorFromIteratorWithComparator(iter, size, additionalCharacterisitcs, comparator);
   }

   public static ShortSpliterator asSpliteratorUnknownSize(ShortIterator iter, int characterisitcs) {
      return new ShortSpliterators.SpliteratorFromIterator(iter, characterisitcs);
   }

   public static ShortSpliterator asSpliteratorFromSortedUnknownSize(ShortIterator iter, int additionalCharacterisitcs, ShortComparator comparator) {
      return new ShortSpliterators.SpliteratorFromIteratorWithComparator(iter, additionalCharacterisitcs, comparator);
   }

   public static ShortIterator asIterator(ShortSpliterator spliterator) {
      return new ShortSpliterators.IteratorFromSpliterator(spliterator);
   }

   public static ShortSpliterator wrap(ByteSpliterator spliterator) {
      return new ShortSpliterators.ByteSpliteratorWrapper(spliterator);
   }

   public abstract static class AbstractIndexBasedSpliterator extends AbstractShortSpliterator {
      protected int pos;

      protected AbstractIndexBasedSpliterator(int initialPos) {
         this.pos = initialPos;
      }

      protected abstract short get(int var1);

      protected abstract int getMaxPos();

      protected abstract ShortSpliterator makeForSplit(int var1, int var2);

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

      public boolean tryAdvance(ShortConsumer action) {
         if (this.pos >= this.getMaxPos()) {
            return false;
         } else {
            action.accept(this.get(this.pos++));
            return true;
         }
      }

      public void forEachRemaining(ShortConsumer action) {
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
      public ShortSpliterator trySplit() {
         int max = this.getMaxPos();
         int splitPoint = this.computeSplitPoint();
         if (splitPoint != this.pos && splitPoint != max) {
            this.splitPointCheck(splitPoint, max);
            int oldPos = this.pos;
            ShortSpliterator maybeSplit = this.makeForSplit(oldPos, splitPoint);
            if (maybeSplit != null) {
               this.pos = splitPoint;
            }

            return maybeSplit;
         } else {
            return null;
         }
      }
   }

   private static class ArraySpliterator implements ShortSpliterator {
      private static final int BASE_CHARACTERISTICS = 16720;
      final short[] array;
      private final int offset;
      private int length;
      private int curr;
      final int characteristics;

      public ArraySpliterator(short[] array, int offset, int length, int additionalCharacteristics) {
         this.array = array;
         this.offset = offset;
         this.length = length;
         this.characteristics = 16720 | additionalCharacteristics;
      }

      public boolean tryAdvance(ShortConsumer action) {
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

      protected ShortSpliterators.ArraySpliterator makeForSplit(int newOffset, int newLength) {
         return new ShortSpliterators.ArraySpliterator(this.array, newOffset, newLength, this.characteristics);
      }

      @Override
      public ShortSpliterator trySplit() {
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

      public void forEachRemaining(ShortConsumer action) {
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

   private static class ArraySpliteratorWithComparator extends ShortSpliterators.ArraySpliterator {
      private final ShortComparator comparator;

      public ArraySpliteratorWithComparator(short[] array, int offset, int length, int additionalCharacteristics, ShortComparator comparator) {
         super(array, offset, length, additionalCharacteristics | 20);
         this.comparator = comparator;
      }

      protected ShortSpliterators.ArraySpliteratorWithComparator makeForSplit(int newOffset, int newLength) {
         return new ShortSpliterators.ArraySpliteratorWithComparator(this.array, newOffset, newLength, this.characteristics, this.comparator);
      }

      @Override
      public ShortComparator getComparator() {
         return this.comparator;
      }
   }

   private static final class ByteSpliteratorWrapper implements ShortSpliterator {
      final ByteSpliterator spliterator;

      public ByteSpliteratorWrapper(ByteSpliterator spliterator) {
         this.spliterator = spliterator;
      }

      public boolean tryAdvance(ShortConsumer action) {
         return this.spliterator.tryAdvance(action::accept);
      }

      public void forEachRemaining(ShortConsumer action) {
         this.spliterator.forEachRemaining(action::accept);
      }

      @Override
      public long estimateSize() {
         return this.spliterator.estimateSize();
      }

      @Override
      public int characteristics() {
         return this.spliterator.characteristics();
      }

      @Override
      public long skip(long n) {
         return this.spliterator.skip(n);
      }

      @Override
      public ShortSpliterator trySplit() {
         ByteSpliterator possibleSplit = this.spliterator.trySplit();
         return possibleSplit == null ? null : new ShortSpliterators.ByteSpliteratorWrapper(possibleSplit);
      }
   }

   public abstract static class EarlyBindingSizeIndexBasedSpliterator extends ShortSpliterators.AbstractIndexBasedSpliterator {
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

   public static class EmptySpliterator implements ShortSpliterator, Serializable, Cloneable {
      private static final long serialVersionUID = 8379247926738230492L;
      private static final int CHARACTERISTICS = 16448;

      protected EmptySpliterator() {
      }

      public boolean tryAdvance(ShortConsumer action) {
         return false;
      }

      @Deprecated
      @Override
      public boolean tryAdvance(Consumer<? super Short> action) {
         return false;
      }

      @Override
      public ShortSpliterator trySplit() {
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

      public void forEachRemaining(ShortConsumer action) {
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Short> action) {
      }

      @Override
      public Object clone() {
         return ShortSpliterators.EMPTY_SPLITERATOR;
      }

      private Object readResolve() {
         return ShortSpliterators.EMPTY_SPLITERATOR;
      }
   }

   private static class IntervalSpliterator implements ShortSpliterator {
      private static final int DONT_SPLIT_THRESHOLD = 2;
      private static final int CHARACTERISTICS = 17749;
      private short curr;
      private short to;

      public IntervalSpliterator(short from, short to) {
         this.curr = from;
         this.to = to;
      }

      public boolean tryAdvance(ShortConsumer action) {
         if (this.curr >= this.to) {
            return false;
         } else {
            action.accept(this.curr++);
            return true;
         }
      }

      public void forEachRemaining(ShortConsumer action) {
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
      public ShortComparator getComparator() {
         return null;
      }

      @Override
      public ShortSpliterator trySplit() {
         int remaining = this.to - this.curr;
         short mid = (short)(this.curr + (remaining >> 1));
         if (remaining >= 0 && remaining <= 2) {
            return null;
         } else {
            short old_curr = this.curr;
            this.curr = mid;
            return new ShortSpliterators.IntervalSpliterator(old_curr, mid);
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
               this.curr = SafeMath.safeLongToShort(newCurr);
               return n;
            } else {
               n = this.to - this.curr;
               this.curr = this.to;
               return n;
            }
         }
      }
   }

   private static final class IteratorFromSpliterator implements ShortIterator, ShortConsumer {
      private final ShortSpliterator spliterator;
      private short holder = 0;
      private boolean hasPeeked = false;

      IteratorFromSpliterator(ShortSpliterator spliterator) {
         this.spliterator = spliterator;
      }

      @Override
      public void accept(short item) {
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
      public short nextShort() {
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
      public void forEachRemaining(ShortConsumer action) {
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

   public abstract static class LateBindingSizeIndexBasedSpliterator extends ShortSpliterators.AbstractIndexBasedSpliterator {
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
      public ShortSpliterator trySplit() {
         ShortSpliterator maybeSplit = super.trySplit();
         if (!this.maxPosFixed && maybeSplit != null) {
            this.maxPos = this.getMaxPosFromBackingStore();
            this.maxPosFixed = true;
         }

         return maybeSplit;
      }
   }

   private static class PrimitiveSpliteratorWrapper implements ShortSpliterator {
      final OfInt i;

      public PrimitiveSpliteratorWrapper(OfInt i) {
         this.i = i;
      }

      public boolean tryAdvance(ShortConsumer action) {
         return this.i.tryAdvance(action);
      }

      public void forEachRemaining(ShortConsumer action) {
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
      public ShortComparator getComparator() {
         Comparator<? super Integer> comp = this.i.getComparator();
         return (left, right) -> comp.compare(Integer.valueOf(left), Integer.valueOf(right));
      }

      @Override
      public ShortSpliterator trySplit() {
         OfInt innerSplit = this.i.trySplit();
         return innerSplit == null ? null : new ShortSpliterators.PrimitiveSpliteratorWrapper(innerSplit);
      }
   }

   private static class PrimitiveSpliteratorWrapperWithComparator extends ShortSpliterators.PrimitiveSpliteratorWrapper {
      final ShortComparator comparator;

      public PrimitiveSpliteratorWrapperWithComparator(OfInt i, ShortComparator comparator) {
         super(i);
         this.comparator = comparator;
      }

      @Override
      public ShortComparator getComparator() {
         return this.comparator;
      }

      @Override
      public ShortSpliterator trySplit() {
         OfInt innerSplit = this.i.trySplit();
         return innerSplit == null ? null : new ShortSpliterators.PrimitiveSpliteratorWrapperWithComparator(innerSplit, this.comparator);
      }
   }

   private static class SingletonSpliterator implements ShortSpliterator {
      private final short element;
      private final ShortComparator comparator;
      private boolean consumed = false;
      private static final int CHARACTERISTICS = 17749;

      public SingletonSpliterator(short element) {
         this(element, null);
      }

      public SingletonSpliterator(short element, ShortComparator comparator) {
         this.element = element;
         this.comparator = comparator;
      }

      public boolean tryAdvance(ShortConsumer action) {
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
      public ShortSpliterator trySplit() {
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

      public void forEachRemaining(ShortConsumer action) {
         Objects.requireNonNull(action);
         if (!this.consumed) {
            this.consumed = true;
            action.accept(this.element);
         }
      }

      @Override
      public ShortComparator getComparator() {
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

   private static class SpliteratorConcatenator implements ShortSpliterator {
      private static final int EMPTY_CHARACTERISTICS = 16448;
      private static final int CHARACTERISTICS_NOT_SUPPORTED_WHILE_MULTIPLE = 5;
      final ShortSpliterator[] a;
      int offset;
      int length;
      long remainingEstimatedExceptCurrent = Long.MAX_VALUE;
      int characteristics = 0;

      public SpliteratorConcatenator(ShortSpliterator[] a, int offset, int length) {
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

      public boolean tryAdvance(ShortConsumer action) {
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

      public void forEachRemaining(ShortConsumer action) {
         while (this.length > 0) {
            this.a[this.offset].forEachRemaining(action);
            this.advanceNextSpliterator();
         }
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Short> action) {
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
      public ShortComparator getComparator() {
         if (this.length == 1 && (this.characteristics & 4) != 0) {
            return this.a[this.offset].getComparator();
         } else {
            throw new IllegalStateException();
         }
      }

      @Override
      public ShortSpliterator trySplit() {
         switch (this.length) {
            case 0:
               return null;
            case 1: {
               ShortSpliterator split = this.a[this.offset].trySplit();
               this.characteristics = this.a[this.offset].characteristics();
               return split;
            }
            case 2: {
               ShortSpliterator split = this.a[this.offset++];
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
               return new ShortSpliterators.SpliteratorConcatenator(this.a, ret_offset, mid);
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

   private static class SpliteratorFromIterator implements ShortSpliterator {
      private static final int BATCH_INCREMENT_SIZE = 1024;
      private static final int BATCH_MAX_SIZE = 33554432;
      private final ShortIterator iter;
      final int characteristics;
      private final boolean knownSize;
      private long size = Long.MAX_VALUE;
      private int nextBatchSize = 1024;
      private ShortSpliterator delegate = null;

      SpliteratorFromIterator(ShortIterator iter, int characteristics) {
         this.iter = iter;
         this.characteristics = 256 | characteristics;
         this.knownSize = false;
      }

      SpliteratorFromIterator(ShortIterator iter, long size, int additionalCharacteristics) {
         this.iter = iter;
         this.knownSize = true;
         this.size = size;
         if ((additionalCharacteristics & 4096) != 0) {
            this.characteristics = 256 | additionalCharacteristics;
         } else {
            this.characteristics = 16704 | additionalCharacteristics;
         }
      }

      public boolean tryAdvance(ShortConsumer action) {
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
            action.accept(this.iter.nextShort());
            return true;
         }
      }

      public void forEachRemaining(ShortConsumer action) {
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

      protected ShortSpliterator makeForSplit(short[] batch, int len) {
         return ShortSpliterators.wrap(batch, 0, len, this.characteristics);
      }

      @Override
      public ShortSpliterator trySplit() {
         if (!this.iter.hasNext()) {
            return null;
         } else {
            int batchSizeEst = this.knownSize && this.size > 0L ? (int)Math.min((long)this.nextBatchSize, this.size) : this.nextBatchSize;
            short[] batch = new short[batchSizeEst];

            int actualSeen;
            for (actualSeen = 0; actualSeen < batchSizeEst && this.iter.hasNext(); this.size--) {
               batch[actualSeen++] = this.iter.nextShort();
            }

            if (batchSizeEst < this.nextBatchSize && this.iter.hasNext()) {
               for (batch = Arrays.copyOf(batch, this.nextBatchSize); this.iter.hasNext() && actualSeen < this.nextBatchSize; this.size--) {
                  batch[actualSeen++] = this.iter.nextShort();
               }
            }

            this.nextBatchSize = Math.min(33554432, this.nextBatchSize + 1024);
            ShortSpliterator split = this.makeForSplit(batch, actualSeen);
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
         } else if (this.iter instanceof ShortBigListIterator) {
            long skipped = ((ShortBigListIterator)this.iter).skip(n);
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

   private static class SpliteratorFromIteratorWithComparator extends ShortSpliterators.SpliteratorFromIterator {
      private final ShortComparator comparator;

      SpliteratorFromIteratorWithComparator(ShortIterator iter, int additionalCharacteristics, ShortComparator comparator) {
         super(iter, additionalCharacteristics | 20);
         this.comparator = comparator;
      }

      SpliteratorFromIteratorWithComparator(ShortIterator iter, long size, int additionalCharacteristics, ShortComparator comparator) {
         super(iter, size, additionalCharacteristics | 20);
         this.comparator = comparator;
      }

      @Override
      public ShortComparator getComparator() {
         return this.comparator;
      }

      @Override
      protected ShortSpliterator makeForSplit(short[] array, int len) {
         return ShortSpliterators.wrapPreSorted(array, 0, len, this.characteristics, this.comparator);
      }
   }

   private static class SpliteratorWrapper implements ShortSpliterator {
      final Spliterator<Short> i;

      public SpliteratorWrapper(Spliterator<Short> i) {
         this.i = i;
      }

      public boolean tryAdvance(ShortConsumer action) {
         return this.i.tryAdvance(action);
      }

      @Deprecated
      @Override
      public boolean tryAdvance(Consumer<? super Short> action) {
         return this.i.tryAdvance(action);
      }

      public void forEachRemaining(ShortConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Short> action) {
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
      public ShortComparator getComparator() {
         return ShortComparators.asShortComparator(this.i.getComparator());
      }

      @Override
      public ShortSpliterator trySplit() {
         Spliterator<Short> innerSplit = this.i.trySplit();
         return innerSplit == null ? null : new ShortSpliterators.SpliteratorWrapper(innerSplit);
      }
   }

   private static class SpliteratorWrapperWithComparator extends ShortSpliterators.SpliteratorWrapper {
      final ShortComparator comparator;

      public SpliteratorWrapperWithComparator(Spliterator<Short> i, ShortComparator comparator) {
         super(i);
         this.comparator = comparator;
      }

      @Override
      public ShortComparator getComparator() {
         return this.comparator;
      }

      @Override
      public ShortSpliterator trySplit() {
         Spliterator<Short> innerSplit = this.i.trySplit();
         return innerSplit == null ? null : new ShortSpliterators.SpliteratorWrapperWithComparator(innerSplit, this.comparator);
      }
   }
}
