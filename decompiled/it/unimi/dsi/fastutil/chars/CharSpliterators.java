package it.unimi.dsi.fastutil.chars;

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

public final class CharSpliterators {
   static final int BASE_SPLITERATOR_CHARACTERISTICS = 256;
   public static final int COLLECTION_SPLITERATOR_CHARACTERISTICS = 320;
   public static final int LIST_SPLITERATOR_CHARACTERISTICS = 16720;
   public static final int SET_SPLITERATOR_CHARACTERISTICS = 321;
   private static final int SORTED_CHARACTERISTICS = 20;
   public static final int SORTED_SET_SPLITERATOR_CHARACTERISTICS = 341;
   public static final CharSpliterators.EmptySpliterator EMPTY_SPLITERATOR = new CharSpliterators.EmptySpliterator();

   private CharSpliterators() {
   }

   public static CharSpliterator singleton(char element) {
      return new CharSpliterators.SingletonSpliterator(element);
   }

   public static CharSpliterator singleton(char element, CharComparator comparator) {
      return new CharSpliterators.SingletonSpliterator(element, comparator);
   }

   public static CharSpliterator wrap(char[] array, int offset, int length) {
      CharArrays.ensureOffsetLength(array, offset, length);
      return new CharSpliterators.ArraySpliterator(array, offset, length, 0);
   }

   public static CharSpliterator wrap(char[] array) {
      return new CharSpliterators.ArraySpliterator(array, 0, array.length, 0);
   }

   public static CharSpliterator wrap(char[] array, int offset, int length, int additionalCharacteristics) {
      CharArrays.ensureOffsetLength(array, offset, length);
      return new CharSpliterators.ArraySpliterator(array, offset, length, additionalCharacteristics);
   }

   public static CharSpliterator wrapPreSorted(char[] array, int offset, int length, int additionalCharacteristics, CharComparator comparator) {
      CharArrays.ensureOffsetLength(array, offset, length);
      return new CharSpliterators.ArraySpliteratorWithComparator(array, offset, length, additionalCharacteristics, comparator);
   }

   public static CharSpliterator wrapPreSorted(char[] array, int offset, int length, CharComparator comparator) {
      return wrapPreSorted(array, offset, length, 0, comparator);
   }

   public static CharSpliterator wrapPreSorted(char[] array, CharComparator comparator) {
      return wrapPreSorted(array, 0, array.length, comparator);
   }

   public static CharSpliterator asCharSpliterator(Spliterator i) {
      return (CharSpliterator)(i instanceof CharSpliterator ? (CharSpliterator)i : new CharSpliterators.SpliteratorWrapper(i));
   }

   public static CharSpliterator asCharSpliterator(Spliterator i, CharComparator comparatorOverride) {
      if (i instanceof CharSpliterator) {
         throw new IllegalArgumentException("Cannot override comparator on instance that is already a " + CharSpliterator.class.getSimpleName());
      } else {
         return (CharSpliterator)(i instanceof OfInt
            ? new CharSpliterators.PrimitiveSpliteratorWrapperWithComparator((OfInt)i, comparatorOverride)
            : new CharSpliterators.SpliteratorWrapperWithComparator(i, comparatorOverride));
      }
   }

   public static CharSpliterator narrow(OfInt i) {
      return new CharSpliterators.PrimitiveSpliteratorWrapper(i);
   }

   public static IntSpliterator widen(CharSpliterator i) {
      return IntSpliterators.wrap(i);
   }

   public static void onEachMatching(CharSpliterator spliterator, CharPredicate predicate, CharConsumer action) {
      Objects.requireNonNull(predicate);
      Objects.requireNonNull(action);
      spliterator.forEachRemaining(value -> {
         if (predicate.test(value)) {
            action.accept(value);
         }
      });
   }

   public static void onEachMatching(CharSpliterator spliterator, IntPredicate predicate, IntConsumer action) {
      Objects.requireNonNull(predicate);
      Objects.requireNonNull(action);
      spliterator.forEachRemaining(value -> {
         if (predicate.test(value)) {
            action.accept(value);
         }
      });
   }

   public static CharSpliterator fromTo(char from, char to) {
      return new CharSpliterators.IntervalSpliterator(from, to);
   }

   public static CharSpliterator concat(CharSpliterator... a) {
      return concat(a, 0, a.length);
   }

   public static CharSpliterator concat(CharSpliterator[] a, int offset, int length) {
      return new CharSpliterators.SpliteratorConcatenator(a, offset, length);
   }

   public static CharSpliterator asSpliterator(CharIterator iter, long size, int additionalCharacterisitcs) {
      return new CharSpliterators.SpliteratorFromIterator(iter, size, additionalCharacterisitcs);
   }

   public static CharSpliterator asSpliteratorFromSorted(CharIterator iter, long size, int additionalCharacterisitcs, CharComparator comparator) {
      return new CharSpliterators.SpliteratorFromIteratorWithComparator(iter, size, additionalCharacterisitcs, comparator);
   }

   public static CharSpliterator asSpliteratorUnknownSize(CharIterator iter, int characterisitcs) {
      return new CharSpliterators.SpliteratorFromIterator(iter, characterisitcs);
   }

   public static CharSpliterator asSpliteratorFromSortedUnknownSize(CharIterator iter, int additionalCharacterisitcs, CharComparator comparator) {
      return new CharSpliterators.SpliteratorFromIteratorWithComparator(iter, additionalCharacterisitcs, comparator);
   }

   public static CharIterator asIterator(CharSpliterator spliterator) {
      return new CharSpliterators.IteratorFromSpliterator(spliterator);
   }

   public abstract static class AbstractIndexBasedSpliterator extends AbstractCharSpliterator {
      protected int pos;

      protected AbstractIndexBasedSpliterator(int initialPos) {
         this.pos = initialPos;
      }

      protected abstract char get(int var1);

      protected abstract int getMaxPos();

      protected abstract CharSpliterator makeForSplit(int var1, int var2);

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

      public boolean tryAdvance(CharConsumer action) {
         if (this.pos >= this.getMaxPos()) {
            return false;
         } else {
            action.accept(this.get(this.pos++));
            return true;
         }
      }

      public void forEachRemaining(CharConsumer action) {
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
      public CharSpliterator trySplit() {
         int max = this.getMaxPos();
         int splitPoint = this.computeSplitPoint();
         if (splitPoint != this.pos && splitPoint != max) {
            this.splitPointCheck(splitPoint, max);
            int oldPos = this.pos;
            CharSpliterator maybeSplit = this.makeForSplit(oldPos, splitPoint);
            if (maybeSplit != null) {
               this.pos = splitPoint;
            }

            return maybeSplit;
         } else {
            return null;
         }
      }
   }

   private static class ArraySpliterator implements CharSpliterator {
      private static final int BASE_CHARACTERISTICS = 16720;
      final char[] array;
      private final int offset;
      private int length;
      private int curr;
      final int characteristics;

      public ArraySpliterator(char[] array, int offset, int length, int additionalCharacteristics) {
         this.array = array;
         this.offset = offset;
         this.length = length;
         this.characteristics = 16720 | additionalCharacteristics;
      }

      public boolean tryAdvance(CharConsumer action) {
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

      protected CharSpliterators.ArraySpliterator makeForSplit(int newOffset, int newLength) {
         return new CharSpliterators.ArraySpliterator(this.array, newOffset, newLength, this.characteristics);
      }

      @Override
      public CharSpliterator trySplit() {
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

      public void forEachRemaining(CharConsumer action) {
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

   private static class ArraySpliteratorWithComparator extends CharSpliterators.ArraySpliterator {
      private final CharComparator comparator;

      public ArraySpliteratorWithComparator(char[] array, int offset, int length, int additionalCharacteristics, CharComparator comparator) {
         super(array, offset, length, additionalCharacteristics | 20);
         this.comparator = comparator;
      }

      protected CharSpliterators.ArraySpliteratorWithComparator makeForSplit(int newOffset, int newLength) {
         return new CharSpliterators.ArraySpliteratorWithComparator(this.array, newOffset, newLength, this.characteristics, this.comparator);
      }

      @Override
      public CharComparator getComparator() {
         return this.comparator;
      }
   }

   public abstract static class EarlyBindingSizeIndexBasedSpliterator extends CharSpliterators.AbstractIndexBasedSpliterator {
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

   public static class EmptySpliterator implements CharSpliterator, Serializable, Cloneable {
      private static final long serialVersionUID = 8379247926738230492L;
      private static final int CHARACTERISTICS = 16448;

      protected EmptySpliterator() {
      }

      public boolean tryAdvance(CharConsumer action) {
         return false;
      }

      @Deprecated
      @Override
      public boolean tryAdvance(Consumer<? super Character> action) {
         return false;
      }

      @Override
      public CharSpliterator trySplit() {
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

      public void forEachRemaining(CharConsumer action) {
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Character> action) {
      }

      @Override
      public Object clone() {
         return CharSpliterators.EMPTY_SPLITERATOR;
      }

      private Object readResolve() {
         return CharSpliterators.EMPTY_SPLITERATOR;
      }
   }

   private static class IntervalSpliterator implements CharSpliterator {
      private static final int DONT_SPLIT_THRESHOLD = 2;
      private static final int CHARACTERISTICS = 17749;
      private char curr;
      private char to;

      public IntervalSpliterator(char from, char to) {
         this.curr = from;
         this.to = to;
      }

      public boolean tryAdvance(CharConsumer action) {
         if (this.curr >= this.to) {
            return false;
         } else {
            action.accept(this.curr++);
            return true;
         }
      }

      public void forEachRemaining(CharConsumer action) {
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
      public CharComparator getComparator() {
         return null;
      }

      @Override
      public CharSpliterator trySplit() {
         int remaining = this.to - this.curr;
         char mid = (char)(this.curr + (remaining >> 1));
         if (remaining >= 0 && remaining <= 2) {
            return null;
         } else {
            char old_curr = this.curr;
            this.curr = mid;
            return new CharSpliterators.IntervalSpliterator(old_curr, mid);
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
               this.curr = SafeMath.safeLongToChar(newCurr);
               return n;
            } else {
               n = this.to - this.curr;
               this.curr = this.to;
               return n;
            }
         }
      }
   }

   private static final class IteratorFromSpliterator implements CharIterator, CharConsumer {
      private final CharSpliterator spliterator;
      private char holder = 0;
      private boolean hasPeeked = false;

      IteratorFromSpliterator(CharSpliterator spliterator) {
         this.spliterator = spliterator;
      }

      @Override
      public void accept(char item) {
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
      public char nextChar() {
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
      public void forEachRemaining(CharConsumer action) {
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

   public abstract static class LateBindingSizeIndexBasedSpliterator extends CharSpliterators.AbstractIndexBasedSpliterator {
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
      public CharSpliterator trySplit() {
         CharSpliterator maybeSplit = super.trySplit();
         if (!this.maxPosFixed && maybeSplit != null) {
            this.maxPos = this.getMaxPosFromBackingStore();
            this.maxPosFixed = true;
         }

         return maybeSplit;
      }
   }

   private static class PrimitiveSpliteratorWrapper implements CharSpliterator {
      final OfInt i;

      public PrimitiveSpliteratorWrapper(OfInt i) {
         this.i = i;
      }

      public boolean tryAdvance(CharConsumer action) {
         return this.i.tryAdvance(action);
      }

      public void forEachRemaining(CharConsumer action) {
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
      public CharComparator getComparator() {
         Comparator<? super Integer> comp = this.i.getComparator();
         return (left, right) -> comp.compare(Integer.valueOf(left), Integer.valueOf(right));
      }

      @Override
      public CharSpliterator trySplit() {
         OfInt innerSplit = this.i.trySplit();
         return innerSplit == null ? null : new CharSpliterators.PrimitiveSpliteratorWrapper(innerSplit);
      }
   }

   private static class PrimitiveSpliteratorWrapperWithComparator extends CharSpliterators.PrimitiveSpliteratorWrapper {
      final CharComparator comparator;

      public PrimitiveSpliteratorWrapperWithComparator(OfInt i, CharComparator comparator) {
         super(i);
         this.comparator = comparator;
      }

      @Override
      public CharComparator getComparator() {
         return this.comparator;
      }

      @Override
      public CharSpliterator trySplit() {
         OfInt innerSplit = this.i.trySplit();
         return innerSplit == null ? null : new CharSpliterators.PrimitiveSpliteratorWrapperWithComparator(innerSplit, this.comparator);
      }
   }

   private static class SingletonSpliterator implements CharSpliterator {
      private final char element;
      private final CharComparator comparator;
      private boolean consumed = false;
      private static final int CHARACTERISTICS = 17749;

      public SingletonSpliterator(char element) {
         this(element, null);
      }

      public SingletonSpliterator(char element, CharComparator comparator) {
         this.element = element;
         this.comparator = comparator;
      }

      public boolean tryAdvance(CharConsumer action) {
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
      public CharSpliterator trySplit() {
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

      public void forEachRemaining(CharConsumer action) {
         Objects.requireNonNull(action);
         if (!this.consumed) {
            this.consumed = true;
            action.accept(this.element);
         }
      }

      @Override
      public CharComparator getComparator() {
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

   private static class SpliteratorConcatenator implements CharSpliterator {
      private static final int EMPTY_CHARACTERISTICS = 16448;
      private static final int CHARACTERISTICS_NOT_SUPPORTED_WHILE_MULTIPLE = 5;
      final CharSpliterator[] a;
      int offset;
      int length;
      long remainingEstimatedExceptCurrent = Long.MAX_VALUE;
      int characteristics = 0;

      public SpliteratorConcatenator(CharSpliterator[] a, int offset, int length) {
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

      public boolean tryAdvance(CharConsumer action) {
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

      public void forEachRemaining(CharConsumer action) {
         while (this.length > 0) {
            this.a[this.offset].forEachRemaining(action);
            this.advanceNextSpliterator();
         }
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Character> action) {
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
      public CharComparator getComparator() {
         if (this.length == 1 && (this.characteristics & 4) != 0) {
            return this.a[this.offset].getComparator();
         } else {
            throw new IllegalStateException();
         }
      }

      @Override
      public CharSpliterator trySplit() {
         switch (this.length) {
            case 0:
               return null;
            case 1: {
               CharSpliterator split = this.a[this.offset].trySplit();
               this.characteristics = this.a[this.offset].characteristics();
               return split;
            }
            case 2: {
               CharSpliterator split = this.a[this.offset++];
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
               return new CharSpliterators.SpliteratorConcatenator(this.a, ret_offset, mid);
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

   private static class SpliteratorFromIterator implements CharSpliterator {
      private static final int BATCH_INCREMENT_SIZE = 1024;
      private static final int BATCH_MAX_SIZE = 33554432;
      private final CharIterator iter;
      final int characteristics;
      private final boolean knownSize;
      private long size = Long.MAX_VALUE;
      private int nextBatchSize = 1024;
      private CharSpliterator delegate = null;

      SpliteratorFromIterator(CharIterator iter, int characteristics) {
         this.iter = iter;
         this.characteristics = 256 | characteristics;
         this.knownSize = false;
      }

      SpliteratorFromIterator(CharIterator iter, long size, int additionalCharacteristics) {
         this.iter = iter;
         this.knownSize = true;
         this.size = size;
         if ((additionalCharacteristics & 4096) != 0) {
            this.characteristics = 256 | additionalCharacteristics;
         } else {
            this.characteristics = 16704 | additionalCharacteristics;
         }
      }

      public boolean tryAdvance(CharConsumer action) {
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
            action.accept(this.iter.nextChar());
            return true;
         }
      }

      public void forEachRemaining(CharConsumer action) {
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

      protected CharSpliterator makeForSplit(char[] batch, int len) {
         return CharSpliterators.wrap(batch, 0, len, this.characteristics);
      }

      @Override
      public CharSpliterator trySplit() {
         if (!this.iter.hasNext()) {
            return null;
         } else {
            int batchSizeEst = this.knownSize && this.size > 0L ? (int)Math.min((long)this.nextBatchSize, this.size) : this.nextBatchSize;
            char[] batch = new char[batchSizeEst];

            int actualSeen;
            for (actualSeen = 0; actualSeen < batchSizeEst && this.iter.hasNext(); this.size--) {
               batch[actualSeen++] = this.iter.nextChar();
            }

            if (batchSizeEst < this.nextBatchSize && this.iter.hasNext()) {
               for (batch = Arrays.copyOf(batch, this.nextBatchSize); this.iter.hasNext() && actualSeen < this.nextBatchSize; this.size--) {
                  batch[actualSeen++] = this.iter.nextChar();
               }
            }

            this.nextBatchSize = Math.min(33554432, this.nextBatchSize + 1024);
            CharSpliterator split = this.makeForSplit(batch, actualSeen);
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
         } else if (this.iter instanceof CharBigListIterator) {
            long skipped = ((CharBigListIterator)this.iter).skip(n);
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

   private static class SpliteratorFromIteratorWithComparator extends CharSpliterators.SpliteratorFromIterator {
      private final CharComparator comparator;

      SpliteratorFromIteratorWithComparator(CharIterator iter, int additionalCharacteristics, CharComparator comparator) {
         super(iter, additionalCharacteristics | 20);
         this.comparator = comparator;
      }

      SpliteratorFromIteratorWithComparator(CharIterator iter, long size, int additionalCharacteristics, CharComparator comparator) {
         super(iter, size, additionalCharacteristics | 20);
         this.comparator = comparator;
      }

      @Override
      public CharComparator getComparator() {
         return this.comparator;
      }

      @Override
      protected CharSpliterator makeForSplit(char[] array, int len) {
         return CharSpliterators.wrapPreSorted(array, 0, len, this.characteristics, this.comparator);
      }
   }

   private static class SpliteratorWrapper implements CharSpliterator {
      final Spliterator<Character> i;

      public SpliteratorWrapper(Spliterator<Character> i) {
         this.i = i;
      }

      public boolean tryAdvance(CharConsumer action) {
         return this.i.tryAdvance(action);
      }

      @Deprecated
      @Override
      public boolean tryAdvance(Consumer<? super Character> action) {
         return this.i.tryAdvance(action);
      }

      public void forEachRemaining(CharConsumer action) {
         this.i.forEachRemaining(action);
      }

      @Deprecated
      @Override
      public void forEachRemaining(Consumer<? super Character> action) {
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
      public CharComparator getComparator() {
         return CharComparators.asCharComparator(this.i.getComparator());
      }

      @Override
      public CharSpliterator trySplit() {
         Spliterator<Character> innerSplit = this.i.trySplit();
         return innerSplit == null ? null : new CharSpliterators.SpliteratorWrapper(innerSplit);
      }
   }

   private static class SpliteratorWrapperWithComparator extends CharSpliterators.SpliteratorWrapper {
      final CharComparator comparator;

      public SpliteratorWrapperWithComparator(Spliterator<Character> i, CharComparator comparator) {
         super(i);
         this.comparator = comparator;
      }

      @Override
      public CharComparator getComparator() {
         return this.comparator;
      }

      @Override
      public CharSpliterator trySplit() {
         Spliterator<Character> innerSplit = this.i.trySplit();
         return innerSplit == null ? null : new CharSpliterators.SpliteratorWrapperWithComparator(innerSplit, this.comparator);
      }
   }
}
