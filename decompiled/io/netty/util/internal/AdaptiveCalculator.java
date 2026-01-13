package io.netty.util.internal;

import java.util.ArrayList;
import java.util.List;

public final class AdaptiveCalculator {
   private static final int INDEX_INCREMENT = 4;
   private static final int INDEX_DECREMENT = 1;
   private static final int[] SIZE_TABLE;
   private final int minIndex;
   private final int maxIndex;
   private final int minCapacity;
   private final int maxCapacity;
   private int index;
   private int nextSize;
   private boolean decreaseNow;

   private static int getSizeTableIndex(int size) {
      int low = 0;
      int high = SIZE_TABLE.length - 1;

      while (high >= low) {
         if (high == low) {
            return high;
         }

         int mid = low + high >>> 1;
         int a = SIZE_TABLE[mid];
         int b = SIZE_TABLE[mid + 1];
         if (size > b) {
            low = mid + 1;
         } else {
            if (size >= a) {
               if (size == a) {
                  return mid;
               }

               return mid + 1;
            }

            high = mid - 1;
         }
      }

      return low;
   }

   public AdaptiveCalculator(int minimum, int initial, int maximum) {
      ObjectUtil.checkPositive(minimum, "minimum");
      if (initial < minimum) {
         throw new IllegalArgumentException("initial: " + initial);
      } else if (maximum < initial) {
         throw new IllegalArgumentException("maximum: " + maximum);
      } else {
         int minIndex = getSizeTableIndex(minimum);
         if (SIZE_TABLE[minIndex] < minimum) {
            this.minIndex = minIndex + 1;
         } else {
            this.minIndex = minIndex;
         }

         int maxIndex = getSizeTableIndex(maximum);
         if (SIZE_TABLE[maxIndex] > maximum) {
            this.maxIndex = maxIndex - 1;
         } else {
            this.maxIndex = maxIndex;
         }

         int initialIndex = getSizeTableIndex(initial);
         if (SIZE_TABLE[initialIndex] > initial) {
            this.index = initialIndex - 1;
         } else {
            this.index = initialIndex;
         }

         this.minCapacity = minimum;
         this.maxCapacity = maximum;
         this.nextSize = Math.max(SIZE_TABLE[this.index], this.minCapacity);
      }
   }

   public void record(int size) {
      if (size <= SIZE_TABLE[Math.max(0, this.index - 1)]) {
         if (this.decreaseNow) {
            this.index = Math.max(this.index - 1, this.minIndex);
            this.nextSize = Math.max(SIZE_TABLE[this.index], this.minCapacity);
            this.decreaseNow = false;
         } else {
            this.decreaseNow = true;
         }
      } else if (size >= this.nextSize) {
         this.index = Math.min(this.index + 4, this.maxIndex);
         this.nextSize = Math.min(SIZE_TABLE[this.index], this.maxCapacity);
         this.decreaseNow = false;
      }
   }

   public int nextSize() {
      return this.nextSize;
   }

   static {
      List<Integer> sizeTable = new ArrayList<>();

      for (int i = 16; i < 512; i += 16) {
         sizeTable.add(i);
      }

      for (int i = 512; i > 0; i <<= 1) {
         sizeTable.add(i);
      }

      SIZE_TABLE = new int[sizeTable.size()];

      for (int i = 0; i < SIZE_TABLE.length; i++) {
         SIZE_TABLE[i] = sizeTable.get(i);
      }
   }
}
