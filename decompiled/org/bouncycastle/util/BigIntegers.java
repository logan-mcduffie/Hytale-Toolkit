package org.bouncycastle.util;

import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Map;
import java.util.WeakHashMap;
import org.bouncycastle.math.raw.Mod;
import org.bouncycastle.math.raw.Nat;

public final class BigIntegers {
   public static final BigInteger ZERO = BigInteger.valueOf(0L);
   public static final BigInteger ONE = BigInteger.valueOf(1L);
   public static final BigInteger TWO = BigInteger.valueOf(2L);
   private static final BigInteger THREE = BigInteger.valueOf(3L);
   private static final int MAX_ITERATIONS = 1000;
   private static final BigInteger SMALL_PRIMES_PRODUCT = new BigInteger(
      "8138e8a0fcf3a4e84a771d40fd305d7f4aa59306d7251de54d98af8fe95729a1f73d893fa424cd2edc8636a6c3285e022b0e3866a565ae8108eed8591cd4fe8d2ce86165a978d719ebf647f362d33fca29cd179fb42401cbaf3df0c614056f9c8f3cfd51e474afb6bc6974f78db8aba8e9e517fded658591ab7502bd41849462f",
      16
   );
   private static final int MAX_SMALL = BigInteger.valueOf(743L).bitLength();

   public static byte[] asUnsignedByteArray(BigInteger var0) {
      byte[] var1 = var0.toByteArray();
      if (var1[0] == 0 && var1.length != 1) {
         byte[] var2 = new byte[var1.length - 1];
         System.arraycopy(var1, 1, var2, 0, var2.length);
         return var2;
      } else {
         return var1;
      }
   }

   public static byte[] asUnsignedByteArray(int var0, BigInteger var1) {
      byte[] var2 = var1.toByteArray();
      if (var2.length == var0) {
         return var2;
      } else {
         int var3 = var2[0] == 0 && var2.length != 1 ? 1 : 0;
         int var4 = var2.length - var3;
         if (var4 > var0) {
            throw new IllegalArgumentException("standard length exceeded for value");
         } else {
            byte[] var5 = new byte[var0];
            System.arraycopy(var2, var3, var5, var5.length - var4, var4);
            return var5;
         }
      }
   }

   public static void asUnsignedByteArray(BigInteger var0, byte[] var1, int var2, int var3) {
      byte[] var4 = var0.toByteArray();
      if (var4.length == var3) {
         System.arraycopy(var4, 0, var1, var2, var3);
      } else {
         int var5 = var4[0] == 0 && var4.length != 1 ? 1 : 0;
         int var6 = var4.length - var5;
         if (var6 > var3) {
            throw new IllegalArgumentException("standard length exceeded for value");
         } else {
            int var7 = var3 - var6;
            Arrays.fill(var1, var2, var2 + var7, (byte)0);
            System.arraycopy(var4, var5, var1, var2 + var7, var6);
         }
      }
   }

   public static BigInteger createRandomInRange(BigInteger var0, BigInteger var1, SecureRandom var2) {
      int var3 = var0.compareTo(var1);
      if (var3 >= 0) {
         if (var3 > 0) {
            throw new IllegalArgumentException("'min' may not be greater than 'max'");
         } else {
            return var0;
         }
      } else if (var0.bitLength() > var1.bitLength() / 2) {
         return createRandomInRange(ZERO, var1.subtract(var0), var2).add(var0);
      } else {
         for (int var4 = 0; var4 < 1000; var4++) {
            BigInteger var5 = createRandomBigInteger(var1.bitLength(), var2);
            if (var5.compareTo(var0) >= 0 && var5.compareTo(var1) <= 0) {
               return var5;
            }
         }

         return createRandomBigInteger(var1.subtract(var0).bitLength() - 1, var2).add(var0);
      }
   }

   public static BigInteger fromUnsignedByteArray(byte[] var0) {
      return new BigInteger(1, var0);
   }

   public static BigInteger fromUnsignedByteArray(byte[] var0, int var1, int var2) {
      byte[] var3 = var0;
      if (var1 != 0 || var2 != var0.length) {
         var3 = new byte[var2];
         System.arraycopy(var0, var1, var3, 0, var2);
      }

      return new BigInteger(1, var3);
   }

   public static byte byteValueExact(BigInteger var0) {
      if (var0.bitLength() > 7) {
         throw new ArithmeticException("BigInteger out of int range");
      } else {
         return var0.byteValue();
      }
   }

   public static short shortValueExact(BigInteger var0) {
      if (var0.bitLength() > 15) {
         throw new ArithmeticException("BigInteger out of int range");
      } else {
         return var0.shortValue();
      }
   }

   public static int intValueExact(BigInteger var0) {
      if (var0.bitLength() > 31) {
         throw new ArithmeticException("BigInteger out of int range");
      } else {
         return var0.intValue();
      }
   }

   public static long longValueExact(BigInteger var0) {
      if (var0.bitLength() > 63) {
         throw new ArithmeticException("BigInteger out of long range");
      } else {
         return var0.longValue();
      }
   }

   public static BigInteger modOddInverse(BigInteger var0, BigInteger var1) {
      if (!var0.testBit(0)) {
         throw new IllegalArgumentException("'M' must be odd");
      } else if (var0.signum() != 1) {
         throw new ArithmeticException("BigInteger: modulus not positive");
      } else {
         if (var1.signum() < 0 || var1.bitLength() > var0.bitLength()) {
            var1 = var1.mod(var0);
         }

         int var2 = var0.bitLength();
         int[] var3 = Nat.fromBigInteger(var2, var0);
         int[] var4 = Nat.fromBigInteger(var2, var1);
         int var5 = var3.length;
         int[] var6 = Nat.create(var5);
         if (0 == Mod.modOddInverse(var3, var4, var6)) {
            throw new ArithmeticException("BigInteger not invertible.");
         } else {
            return Nat.toBigInteger(var5, var6);
         }
      }
   }

   public static BigInteger modOddInverseVar(BigInteger var0, BigInteger var1) {
      if (!var0.testBit(0)) {
         throw new IllegalArgumentException("'M' must be odd");
      } else if (var0.signum() != 1) {
         throw new ArithmeticException("BigInteger: modulus not positive");
      } else if (var0.equals(ONE)) {
         return ZERO;
      } else {
         if (var1.signum() < 0 || var1.bitLength() > var0.bitLength()) {
            var1 = var1.mod(var0);
         }

         if (var1.equals(ONE)) {
            return ONE;
         } else {
            int var2 = var0.bitLength();
            int[] var3 = Nat.fromBigInteger(var2, var0);
            int[] var4 = Nat.fromBigInteger(var2, var1);
            int var5 = var3.length;
            int[] var6 = Nat.create(var5);
            if (!Mod.modOddInverseVar(var3, var4, var6)) {
               throw new ArithmeticException("BigInteger not invertible.");
            } else {
               return Nat.toBigInteger(var5, var6);
            }
         }
      }
   }

   public static boolean modOddIsCoprime(BigInteger var0, BigInteger var1) {
      if (!var0.testBit(0)) {
         throw new IllegalArgumentException("'M' must be odd");
      } else if (var0.signum() != 1) {
         throw new ArithmeticException("BigInteger: modulus not positive");
      } else {
         if (var1.signum() < 0 || var1.bitLength() > var0.bitLength()) {
            var1 = var1.mod(var0);
         }

         int var2 = var0.bitLength();
         int[] var3 = Nat.fromBigInteger(var2, var0);
         int[] var4 = Nat.fromBigInteger(var2, var1);
         return 0 != Mod.modOddIsCoprime(var3, var4);
      }
   }

   public static boolean modOddIsCoprimeVar(BigInteger var0, BigInteger var1) {
      if (!var0.testBit(0)) {
         throw new IllegalArgumentException("'M' must be odd");
      } else if (var0.signum() != 1) {
         throw new ArithmeticException("BigInteger: modulus not positive");
      } else {
         if (var1.signum() < 0 || var1.bitLength() > var0.bitLength()) {
            var1 = var1.mod(var0);
         }

         if (var1.equals(ONE)) {
            return true;
         } else {
            int var2 = var0.bitLength();
            int[] var3 = Nat.fromBigInteger(var2, var0);
            int[] var4 = Nat.fromBigInteger(var2, var1);
            return Mod.modOddIsCoprimeVar(var3, var4);
         }
      }
   }

   public static int getUnsignedByteLength(BigInteger var0) {
      return var0.equals(ZERO) ? 1 : (var0.bitLength() + 7) / 8;
   }

   public static BigInteger createRandomBigInteger(int var0, SecureRandom var1) {
      return new BigInteger(1, createRandom(var0, var1));
   }

   public static BigInteger createRandomPrime(int var0, int var1, SecureRandom var2) {
      if (var0 < 2) {
         throw new IllegalArgumentException("bitLength < 2");
      } else if (var0 == 2) {
         return var2.nextInt() < 0 ? TWO : THREE;
      } else {
         BigInteger var3;
         do {
            byte[] var4 = createRandom(var0, var2);
            int var5 = 8 * var4.length - var0;
            byte var6 = (byte)(1 << 7 - var5);
            var4[0] |= var6;
            var4[var4.length - 1] = (byte)(var4[var4.length - 1] | 1);
            var3 = new BigInteger(1, var4);
            if (var0 > MAX_SMALL) {
               while (!var3.gcd(SMALL_PRIMES_PRODUCT).equals(ONE)) {
                  var3 = var3.add(TWO);
               }
            }
         } while (!var3.isProbablePrime(var1));

         return var3;
      }
   }

   public static void writeUnsignedByteArray(OutputStream var0, BigInteger var1) throws IOException {
      byte[] var2 = var1.toByteArray();
      int var3 = var2[0] == 0 ? 1 : 0;
      int var4 = var2.length - var3;
      var0.write(var2, var3, var4);
   }

   private static byte[] createRandom(int var0, SecureRandom var1) throws IllegalArgumentException {
      if (var0 < 1) {
         throw new IllegalArgumentException("bitLength must be at least 1");
      } else {
         int var2 = (var0 + 7) / 8;
         byte[] var3 = new byte[var2];
         var1.nextBytes(var3);
         int var4 = 8 * var2 - var0;
         var3[0] &= (byte)(255 >>> var4);
         return var3;
      }
   }

   public static class Cache {
      private final Map<BigInteger, Boolean> values = new WeakHashMap<>();
      private final BigInteger[] preserve = new BigInteger[8];
      private int preserveCounter = 0;

      public synchronized void add(BigInteger var1) {
         this.values.put(var1, Boolean.TRUE);
         this.preserve[this.preserveCounter] = var1;
         this.preserveCounter = (this.preserveCounter + 1) % this.preserve.length;
      }

      public synchronized boolean contains(BigInteger var1) {
         return this.values.containsKey(var1);
      }

      public synchronized int size() {
         return this.values.size();
      }

      public synchronized void clear() {
         this.values.clear();

         for (int var1 = 0; var1 != this.preserve.length; var1++) {
            this.preserve[var1] = null;
         }
      }
   }
}
