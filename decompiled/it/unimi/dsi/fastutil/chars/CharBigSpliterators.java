package it.unimi.dsi.fastutil.chars;

public final class CharBigSpliterators {
   public abstract static class AbstractIndexBasedSpliterator extends AbstractCharSpliterator {
      protected long pos;

      protected AbstractIndexBasedSpliterator(long initialPos) {
         this.pos = initialPos;
      }

      protected abstract char get(long var1);

      protected abstract long getMaxPos();

      protected abstract CharSpliterator makeForSplit(long var1, long var3);

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

      public boolean tryAdvance(CharConsumer action) {
         if (this.pos >= this.getMaxPos()) {
            return false;
         } else {
            action.accept(this.get(this.pos++));
            return true;
         }
      }

      public void forEachRemaining(CharConsumer action) {
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
      public CharSpliterator trySplit() {
         long max = this.getMaxPos();
         long splitPoint = this.computeSplitPoint();
         if (splitPoint != this.pos && splitPoint != max) {
            this.splitPointCheck(splitPoint, max);
            long oldPos = this.pos;
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

   public abstract static class EarlyBindingSizeIndexBasedSpliterator extends CharBigSpliterators.AbstractIndexBasedSpliterator {
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

   public abstract static class LateBindingSizeIndexBasedSpliterator extends CharBigSpliterators.AbstractIndexBasedSpliterator {
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
      public CharSpliterator trySplit() {
         CharSpliterator maybeSplit = super.trySplit();
         if (!this.maxPosFixed && maybeSplit != null) {
            this.maxPos = this.getMaxPosFromBackingStore();
            this.maxPosFixed = true;
         }

         return maybeSplit;
      }
   }
}
