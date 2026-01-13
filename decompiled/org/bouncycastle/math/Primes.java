package org.bouncycastle.math;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;

public abstract class Primes {
   public static final int SMALL_FACTOR_LIMIT = 211;
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private static final BigInteger TWO = BigInteger.valueOf(2L);
   private static final BigInteger THREE = BigInteger.valueOf(3L);

   public static Primes.STOutput generateSTRandomPrime(Digest var0, int var1, byte[] var2) {
      if (var0 == null) {
         throw new IllegalArgumentException("'hash' cannot be null");
      } else if (var1 < 2) {
         throw new IllegalArgumentException("'length' must be >= 2");
      } else if (var2 != null && var2.length != 0) {
         return implSTRandomPrime(var0, var1, Arrays.clone(var2));
      } else {
         throw new IllegalArgumentException("'inputSeed' cannot be null or empty");
      }
   }

   public static Primes.MROutput enhancedMRProbablePrimeTest(BigInteger var0, SecureRandom var1, int var2) {
      checkCandidate(var0, "candidate");
      if (var1 == null) {
         throw new IllegalArgumentException("'random' cannot be null");
      } else if (var2 < 1) {
         throw new IllegalArgumentException("'iterations' must be > 0");
      } else if (var0.bitLength() == 2) {
         return Primes.MROutput.probablyPrime();
      } else if (!var0.testBit(0)) {
         return Primes.MROutput.provablyCompositeWithFactor(TWO);
      } else {
         BigInteger var3 = var0;
         BigInteger var4 = var0.subtract(ONE);
         BigInteger var5 = var0.subtract(TWO);
         int var6 = var4.getLowestSetBit();
         BigInteger var7 = var4.shiftRight(var6);

         for (int var8 = 0; var8 < var2; var8++) {
            BigInteger var9 = BigIntegers.createRandomInRange(TWO, var5, var1);
            BigInteger var10 = var9.gcd(var3);
            if (var10.compareTo(ONE) > 0) {
               return Primes.MROutput.provablyCompositeWithFactor(var10);
            }

            BigInteger var11 = var9.modPow(var7, var3);
            if (!var11.equals(ONE) && !var11.equals(var4)) {
               boolean var12 = false;
               BigInteger var13 = var11;

               for (int var14 = 1; var14 < var6; var14++) {
                  var11 = var11.modPow(TWO, var3);
                  if (var11.equals(var4)) {
                     var12 = true;
                     break;
                  }

                  if (var11.equals(ONE)) {
                     break;
                  }

                  var13 = var11;
               }

               if (!var12) {
                  if (!var11.equals(ONE)) {
                     var13 = var11;
                     var11 = var11.modPow(TWO, var3);
                     if (!var11.equals(ONE)) {
                        var13 = var11;
                     }
                  }

                  var10 = var13.subtract(ONE).gcd(var3);
                  if (var10.compareTo(ONE) > 0) {
                     return Primes.MROutput.provablyCompositeWithFactor(var10);
                  }

                  return Primes.MROutput.provablyCompositeNotPrimePower();
               }
            }
         }

         return Primes.MROutput.probablyPrime();
      }
   }

   public static boolean hasAnySmallFactors(BigInteger var0) {
      checkCandidate(var0, "candidate");
      return implHasAnySmallFactors(var0);
   }

   public static boolean isMRProbablePrime(BigInteger var0, SecureRandom var1, int var2) {
      checkCandidate(var0, "candidate");
      if (var1 == null) {
         throw new IllegalArgumentException("'random' cannot be null");
      } else if (var2 < 1) {
         throw new IllegalArgumentException("'iterations' must be > 0");
      } else if (var0.bitLength() == 2) {
         return true;
      } else if (!var0.testBit(0)) {
         return false;
      } else {
         BigInteger var3 = var0;
         BigInteger var4 = var0.subtract(ONE);
         BigInteger var5 = var0.subtract(TWO);
         int var6 = var4.getLowestSetBit();
         BigInteger var7 = var4.shiftRight(var6);

         for (int var8 = 0; var8 < var2; var8++) {
            BigInteger var9 = BigIntegers.createRandomInRange(TWO, var5, var1);
            if (!implMRProbablePrimeToBase(var3, var4, var7, var6, var9)) {
               return false;
            }
         }

         return true;
      }
   }

   public static boolean isMRProbablePrimeToBase(BigInteger var0, BigInteger var1) {
      checkCandidate(var0, "candidate");
      checkCandidate(var1, "base");
      if (var1.compareTo(var0.subtract(ONE)) >= 0) {
         throw new IllegalArgumentException("'base' must be < ('candidate' - 1)");
      } else if (var0.bitLength() == 2) {
         return true;
      } else {
         BigInteger var3 = var0.subtract(ONE);
         int var4 = var3.getLowestSetBit();
         BigInteger var5 = var3.shiftRight(var4);
         return implMRProbablePrimeToBase(var0, var3, var5, var4, var1);
      }
   }

   private static void checkCandidate(BigInteger var0, String var1) {
      if (var0 == null || var0.signum() < 1 || var0.bitLength() < 2) {
         throw new IllegalArgumentException("'" + var1 + "' must be non-null and >= 2");
      }
   }

   private static boolean implHasAnySmallFactors(BigInteger var0) {
      int var1 = 223092870;
      int var2 = var0.mod(BigInteger.valueOf(var1)).intValue();
      if (var2 % 2 != 0
         && var2 % 3 != 0
         && var2 % 5 != 0
         && var2 % 7 != 0
         && var2 % 11 != 0
         && var2 % 13 != 0
         && var2 % 17 != 0
         && var2 % 19 != 0
         && var2 % 23 != 0) {
         var1 = 58642669;
         var2 = var0.mod(BigInteger.valueOf(var1)).intValue();
         if (var2 % 29 != 0 && var2 % 31 != 0 && var2 % 37 != 0 && var2 % 41 != 0 && var2 % 43 != 0) {
            var1 = 600662303;
            var2 = var0.mod(BigInteger.valueOf(var1)).intValue();
            if (var2 % 47 != 0 && var2 % 53 != 0 && var2 % 59 != 0 && var2 % 61 != 0 && var2 % 67 != 0) {
               var1 = 33984931;
               var2 = var0.mod(BigInteger.valueOf(var1)).intValue();
               if (var2 % 71 != 0 && var2 % 73 != 0 && var2 % 79 != 0 && var2 % 83 != 0) {
                  var1 = 89809099;
                  var2 = var0.mod(BigInteger.valueOf(var1)).intValue();
                  if (var2 % 89 != 0 && var2 % 97 != 0 && var2 % 101 != 0 && var2 % 103 != 0) {
                     var1 = 167375713;
                     var2 = var0.mod(BigInteger.valueOf(var1)).intValue();
                     if (var2 % 107 != 0 && var2 % 109 != 0 && var2 % 113 != 0 && var2 % 127 != 0) {
                        var1 = 371700317;
                        var2 = var0.mod(BigInteger.valueOf(var1)).intValue();
                        if (var2 % 131 != 0 && var2 % 137 != 0 && var2 % 139 != 0 && var2 % 149 != 0) {
                           var1 = 645328247;
                           var2 = var0.mod(BigInteger.valueOf(var1)).intValue();
                           if (var2 % 151 != 0 && var2 % 157 != 0 && var2 % 163 != 0 && var2 % 167 != 0) {
                              var1 = 1070560157;
                              var2 = var0.mod(BigInteger.valueOf(var1)).intValue();
                              if (var2 % 173 != 0 && var2 % 179 != 0 && var2 % 181 != 0 && var2 % 191 != 0) {
                                 var1 = 1596463769;
                                 var2 = var0.mod(BigInteger.valueOf(var1)).intValue();
                                 return var2 % 193 == 0 || var2 % 197 == 0 || var2 % 199 == 0 || var2 % 211 == 0;
                              } else {
                                 return true;
                              }
                           } else {
                              return true;
                           }
                        } else {
                           return true;
                        }
                     } else {
                        return true;
                     }
                  } else {
                     return true;
                  }
               } else {
                  return true;
               }
            } else {
               return true;
            }
         } else {
            return true;
         }
      } else {
         return true;
      }
   }

   private static boolean implMRProbablePrimeToBase(BigInteger var0, BigInteger var1, BigInteger var2, int var3, BigInteger var4) {
      BigInteger var5 = var4.modPow(var2, var0);
      if (!var5.equals(ONE) && !var5.equals(var1)) {
         boolean var6 = false;

         for (int var7 = 1; var7 < var3; var7++) {
            var5 = var5.modPow(TWO, var0);
            if (var5.equals(var1)) {
               var6 = true;
               break;
            }

            if (var5.equals(ONE)) {
               return false;
            }
         }

         return var6;
      } else {
         return true;
      }
   }

   private static Primes.STOutput implSTRandomPrime(Digest var0, int var1, byte[] var2) {
      int var3 = var0.getDigestSize();
      if (var1 >= 33) {
         Primes.STOutput var18 = implSTRandomPrime(var0, (var1 + 3) / 2, var2);
         BigInteger var19 = var18.getPrime();
         var2 = var18.getPrimeSeed();
         int var20 = var18.getPrimeGenCounter();
         int var23 = 8 * var3;
         int var24 = (var1 - 1) / var23;
         int var9 = var20;
         BigInteger var10 = hashGen(var0, var2, var24 + 1);
         var10 = var10.mod(ONE.shiftLeft(var1 - 1)).setBit(var1 - 1);
         BigInteger var11 = var19.shiftLeft(1);
         BigInteger var12 = var10.subtract(ONE).divide(var11).add(ONE).shiftLeft(1);
         byte var13 = 0;
         BigInteger var14 = var12.multiply(var19).add(ONE);

         while (true) {
            if (var14.bitLength() > var1) {
               var12 = ONE.shiftLeft(var1 - 1).subtract(ONE).divide(var11).add(ONE).shiftLeft(1);
               var14 = var12.multiply(var19).add(ONE);
            }

            var20++;
            if (!implHasAnySmallFactors(var14)) {
               BigInteger var15 = hashGen(var0, var2, var24 + 1);
               var15 = var15.mod(var14.subtract(THREE)).add(TWO);
               var12 = var12.add(BigInteger.valueOf(var13));
               var13 = 0;
               BigInteger var16 = var15.modPow(var12, var14);
               if (var14.gcd(var16.subtract(ONE)).equals(ONE) && var16.modPow(var19, var14).equals(ONE)) {
                  return new Primes.STOutput(var14, var2, var20);
               }
            } else {
               inc(var2, var24 + 1);
            }

            if (var20 >= 4 * var1 + var9) {
               throw new IllegalStateException("Too many iterations in Shawe-Taylor Random_Prime Routine");
            }

            var13 += 2;
            var14 = var14.add(var11);
         }
      } else {
         int var4 = 0;
         byte[] var5 = new byte[var3];
         byte[] var6 = new byte[var3];

         do {
            hash(var0, var2, var5, 0);
            inc(var2, 1);
            hash(var0, var2, var6, 0);
            inc(var2, 1);
            int var7 = extract32(var5) ^ extract32(var6);
            var7 &= -1 >>> 32 - var1;
            var7 |= 1 << var1 - 1 | 1;
            var4++;
            long var8 = var7 & 4294967295L;
            if (isPrime32(var8)) {
               return new Primes.STOutput(BigInteger.valueOf(var8), var2, var4);
            }
         } while (var4 <= 4 * var1);

         throw new IllegalStateException("Too many iterations in Shawe-Taylor Random_Prime Routine");
      }
   }

   private static int extract32(byte[] var0) {
      int var1 = 0;
      int var2 = Math.min(4, var0.length);

      for (int var3 = 0; var3 < var2; var3++) {
         int var4 = var0[var0.length - (var3 + 1)] & 255;
         var1 |= var4 << 8 * var3;
      }

      return var1;
   }

   private static void hash(Digest var0, byte[] var1, byte[] var2, int var3) {
      var0.update(var1, 0, var1.length);
      var0.doFinal(var2, var3);
   }

   private static BigInteger hashGen(Digest var0, byte[] var1, int var2) {
      int var3 = var0.getDigestSize();
      int var4 = var2 * var3;
      byte[] var5 = new byte[var4];

      for (int var6 = 0; var6 < var2; var6++) {
         var4 -= var3;
         hash(var0, var1, var5, var4);
         inc(var1, 1);
      }

      return new BigInteger(1, var5);
   }

   private static void inc(byte[] var0, int var1) {
      int var2 = var0.length;

      while (var1 > 0) {
         if (--var2 < 0) {
            break;
         }

         var1 += var0[var2] & 255;
         var0[var2] = (byte)var1;
         var1 >>>= 8;
      }
   }

   private static boolean isPrime32(long var0) {
      if (var0 >>> 32 != 0L) {
         throw new IllegalArgumentException("Size limit exceeded");
      } else if (var0 <= 5L) {
         return var0 == 2L || var0 == 3L || var0 == 5L;
      } else if ((var0 & 1L) != 0L && var0 % 3L != 0L && var0 % 5L != 0L) {
         long[] var2 = new long[]{1L, 7L, 11L, 13L, 17L, 19L, 23L, 29L};
         long var3 = 0L;
         int var5 = 1;

         while (true) {
            while (var5 >= var2.length) {
               var3 += 30L;
               if (var3 * var3 >= var0) {
                  return true;
               }

               var5 = 0;
            }

            long var6 = var3 + var2[var5];
            if (var0 % var6 == 0L) {
               return var0 < 30L;
            }

            var5++;
         }
      } else {
         return false;
      }
   }

   public static class MROutput {
      private boolean provablyComposite;
      private BigInteger factor;

      private static Primes.MROutput probablyPrime() {
         return new Primes.MROutput(false, null);
      }

      private static Primes.MROutput provablyCompositeWithFactor(BigInteger var0) {
         return new Primes.MROutput(true, var0);
      }

      private static Primes.MROutput provablyCompositeNotPrimePower() {
         return new Primes.MROutput(true, null);
      }

      private MROutput(boolean var1, BigInteger var2) {
         this.provablyComposite = var1;
         this.factor = var2;
      }

      public BigInteger getFactor() {
         return this.factor;
      }

      public boolean isProvablyComposite() {
         return this.provablyComposite;
      }

      public boolean isNotPrimePower() {
         return this.provablyComposite && this.factor == null;
      }
   }

   public static class STOutput {
      private BigInteger prime;
      private byte[] primeSeed;
      private int primeGenCounter;

      private STOutput(BigInteger var1, byte[] var2, int var3) {
         this.prime = var1;
         this.primeSeed = var2;
         this.primeGenCounter = var3;
      }

      public BigInteger getPrime() {
         return this.prime;
      }

      public byte[] getPrimeSeed() {
         return this.primeSeed;
      }

      public int getPrimeGenCounter() {
         return this.primeGenCounter;
      }
   }
}
