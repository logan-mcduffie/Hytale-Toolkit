package org.bouncycastle.crypto.util;

import java.math.BigInteger;
import org.bouncycastle.util.BigIntegers;

public class RadixConverter {
   private static final double LOG_LONG_MAX_VALUE = Math.log(9.223372E18F);
   private static final int DEFAULT_POWERS_TO_CACHE = 10;
   private final int digitsGroupLength;
   private final BigInteger digitsGroupSpaceSize;
   private final int radix;
   private final BigInteger[] digitsGroupSpacePowers;

   public RadixConverter(int var1, int var2) {
      this.radix = var1;
      this.digitsGroupLength = (int)Math.floor(LOG_LONG_MAX_VALUE / Math.log(var1));
      this.digitsGroupSpaceSize = BigInteger.valueOf(var1).pow(this.digitsGroupLength);
      this.digitsGroupSpacePowers = this.precomputeDigitsGroupPowers(var2, this.digitsGroupSpaceSize);
   }

   public RadixConverter(int var1) {
      this(var1, 10);
   }

   public int getRadix() {
      return this.radix;
   }

   public void toEncoding(BigInteger var1, int var2, short[] var3) {
      if (var1.signum() < 0) {
         throw new IllegalArgumentException();
      } else {
         int var4 = var2 - 1;

         do {
            if (var1.equals(BigInteger.ZERO)) {
               var3[var4--] = 0;
            } else {
               BigInteger[] var5 = var1.divideAndRemainder(this.digitsGroupSpaceSize);
               var1 = var5[0];
               var4 = this.toEncoding(var5[1].longValue(), var4, var3);
            }
         } while (var4 >= 0);

         if (var1.signum() != 0) {
            throw new IllegalArgumentException();
         }
      }
   }

   private int toEncoding(long var1, int var3, short[] var4) {
      for (int var5 = 0; var5 < this.digitsGroupLength && var3 >= 0; var5++) {
         if (var1 == 0L) {
            var4[var3--] = 0;
         } else {
            var4[var3--] = (short)(var1 % this.radix);
            var1 /= this.radix;
         }
      }

      if (var1 != 0L) {
         throw new IllegalStateException("Failed to convert decimal number");
      } else {
         return var3;
      }
   }

   public BigInteger fromEncoding(short[] var1) {
      BigInteger var2 = BigIntegers.ONE;
      BigInteger var3 = null;
      int var4 = 0;
      int var5 = var1.length;

      for (int var6 = var5 - this.digitsGroupLength; var6 > -this.digitsGroupLength; var6 -= this.digitsGroupLength) {
         int var7 = this.digitsGroupLength;
         if (var6 < 0) {
            var7 = this.digitsGroupLength + var6;
            var6 = 0;
         }

         int var8 = Math.min(var6 + var7, var5);
         long var9 = this.fromEncoding(var6, var8, var1);
         BigInteger var11 = BigInteger.valueOf(var9);
         if (var4 == 0) {
            var3 = var11;
         } else {
            var2 = var4 <= this.digitsGroupSpacePowers.length ? this.digitsGroupSpacePowers[var4 - 1] : var2.multiply(this.digitsGroupSpaceSize);
            var3 = var3.add(var11.multiply(var2));
         }

         var4++;
      }

      return var3;
   }

   public int getDigitsGroupLength() {
      return this.digitsGroupLength;
   }

   private long fromEncoding(int var1, int var2, short[] var3) {
      long var4 = 0L;

      for (int var6 = var1; var6 < var2; var6++) {
         var4 = var4 * this.radix + (var3[var6] & '\uffff');
      }

      return var4;
   }

   private BigInteger[] precomputeDigitsGroupPowers(int var1, BigInteger var2) {
      BigInteger[] var3 = new BigInteger[var1];
      BigInteger var4 = var2;

      for (int var5 = 0; var5 < var1; var5++) {
         var3[var5] = var4;
         var4 = var4.multiply(var2);
      }

      return var3;
   }
}
