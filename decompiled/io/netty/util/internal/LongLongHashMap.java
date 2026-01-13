package io.netty.util.internal;

import java.util.Arrays;

public final class LongLongHashMap {
   private static final int MASK_TEMPLATE = -2;
   private int mask;
   private long[] array;
   private int maxProbe;
   private long zeroVal;
   private final long emptyVal;

   public LongLongHashMap(long emptyVal) {
      this.emptyVal = emptyVal;
      this.zeroVal = emptyVal;
      int initialSize = 32;
      this.array = new long[initialSize];
      this.mask = initialSize - 1;
      this.computeMaskAndProbe();
   }

   public LongLongHashMap(LongLongHashMap other) {
      this.mask = other.mask;
      this.array = Arrays.copyOf(other.array, other.array.length);
      this.maxProbe = other.maxProbe;
      this.zeroVal = other.zeroVal;
      this.emptyVal = other.emptyVal;
   }

   public long put(long key, long value) {
      if (key == 0L) {
         long prev = this.zeroVal;
         this.zeroVal = value;
         return prev;
      } else {
         while (true) {
            int index = this.index(key);

            for (int i = 0; i < this.maxProbe; i++) {
               long existing = this.array[index];
               if (existing == key || existing == 0L) {
                  long prev = existing == 0L ? this.emptyVal : this.array[index + 1];
                  this.array[index] = key;

                  for (this.array[index + 1] = value; i < this.maxProbe; i++) {
                     index = index + 2 & this.mask;
                     if (this.array[index] == key) {
                        this.array[index] = 0L;
                        prev = this.array[index + 1];
                        break;
                     }
                  }

                  return prev;
               }

               index = index + 2 & this.mask;
            }

            this.expand();
         }
      }
   }

   public void remove(long key) {
      if (key == 0L) {
         this.zeroVal = this.emptyVal;
      } else {
         int index = this.index(key);

         for (int i = 0; i < this.maxProbe; i++) {
            long existing = this.array[index];
            if (existing == key) {
               this.array[index] = 0L;
               break;
            }

            index = index + 2 & this.mask;
         }
      }
   }

   public long get(long key) {
      if (key == 0L) {
         return this.zeroVal;
      } else {
         int index = this.index(key);

         for (int i = 0; i < this.maxProbe; i++) {
            long existing = this.array[index];
            if (existing == key) {
               return this.array[index + 1];
            }

            index = index + 2 & this.mask;
         }

         return this.emptyVal;
      }
   }

   private int index(long key) {
      key ^= key >>> 33;
      key *= -49064778989728563L;
      key ^= key >>> 33;
      key *= -4265267296055464877L;
      key ^= key >>> 33;
      return (int)key & this.mask;
   }

   private void expand() {
      long[] prev = this.array;
      this.array = new long[prev.length * 2];
      this.computeMaskAndProbe();

      for (int i = 0; i < prev.length; i += 2) {
         long key = prev[i];
         if (key != 0L) {
            long val = prev[i + 1];
            this.put(key, val);
         }
      }
   }

   private void computeMaskAndProbe() {
      int length = this.array.length;
      this.mask = length - 1 & -2;
      this.maxProbe = (int)Math.log(length);
   }
}
