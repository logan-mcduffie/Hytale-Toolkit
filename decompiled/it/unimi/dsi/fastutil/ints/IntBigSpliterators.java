package it.unimi.dsi.fastutil.ints;

public final class IntBigSpliterators {
   public abstract static class AbstractIndexBasedSpliterator extends AbstractIntSpliterator {
      protected long pos;

      protected AbstractIndexBasedSpliterator(long initialPos) {
         this.pos = initialPos;
      }

      protected abstract int get(long var1);

      protected abstract long getMaxPos();

      protected abstract IntSpliterator makeForSplit(long var1, long var3);

      protected long computeSplitPoint() {
         return this.pos + (this.getMaxPos() - this.pos) / 2L;
      }

      private void splitPointCheck(long splitPoint, long observedMax) {
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
         return this.getMaxPos() - this.pos;
      }

      @Override
      public boolean tryAdvance(java.util.function.IntConsumer action) {
         if (this.pos >= this.getMaxPos()) {
            return false;
         } else {
            action.accept(this.get(this.pos++));
            return true;
         }
      }

      @Override
      public void forEachRemaining(java.util.function.IntConsumer action) {
         for (long max = this.getMaxPos(); this.pos < max; this.pos++) {
            action.accept(this.get(this.pos));
         }
      }

      @Override
      public long skip(long n) {
         if (n < 0L) {
            throw new IllegalArgumentException("Argument must be nonnegative: " + n);
         } else {
            long max = this.getMaxPos();
            if (this.pos >= max) {
               return 0L;
            } else {
               long remaining = max - this.pos;
               if (n < remaining) {
                  this.pos += n;
                  return n;
               } else {
                  this.pos = max;
                  return remaining;
               }
            }
         }
      }

      @Override
      public IntSpliterator trySplit() {
         long max = this.getMaxPos();
         long splitPoint = this.computeSplitPoint();
         if (splitPoint != this.pos && splitPoint != max) {
            this.splitPointCheck(splitPoint, max);
            long oldPos = this.pos;
            IntSpliterator maybeSplit = this.makeForSplit(oldPos, splitPoint);
            if (maybeSplit != null) {
               this.pos = splitPoint;
            }

            return maybeSplit;
         } else {
            return null;
         }
      }
   }

   public abstract static class EarlyBindingSizeIndexBasedSpliterator extends IntBigSpliterators.AbstractIndexBasedSpliterator {
      protected final long maxPos;

      protected EarlyBindingSizeIndexBasedSpliterator(long initialPos, long maxPos) {
         super(initialPos);
         this.maxPos = maxPos;
      }

      @Override
      protected final long getMaxPos() {
         return this.maxPos;
      }
   }

   public abstract static class LateBindingSizeIndexBasedSpliterator extends IntBigSpliterators.AbstractIndexBasedSpliterator {
      protected long maxPos = -1L;
      private boolean maxPosFixed;

      protected LateBindingSizeIndexBasedSpliterator(long initialPos) {
         super(initialPos);
         this.maxPosFixed = false;
      }

      protected LateBindingSizeIndexBasedSpliterator(long initialPos, long fixedMaxPos) {
         super(initialPos);
         this.maxPos = fixedMaxPos;
         this.maxPosFixed = true;
      }

      protected abstract long getMaxPosFromBackingStore();

      @Override
      protected final long getMaxPos() {
         return this.maxPosFixed ? this.maxPos : this.getMaxPosFromBackingStore();
      }

      @Override
      public IntSpliterator trySplit() {
         IntSpliterator maybeSplit = super.trySplit();
         if (!this.maxPosFixed && maybeSplit != null) {
            this.maxPos = this.getMaxPosFromBackingStore();
            this.maxPosFixed = true;
         }

         return maybeSplit;
      }
   }
}
