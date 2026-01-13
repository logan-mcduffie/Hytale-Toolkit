package org.bouncycastle.crypto.agreement.srp;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.BigIntegers;

public class SRP6Util {
   private static BigInteger ZERO = BigInteger.valueOf(0L);
   private static BigInteger ONE = BigInteger.valueOf(1L);

   public static BigInteger calculateK(Digest var0, BigInteger var1, BigInteger var2) {
      return hashPaddedPair(var0, var1, var1, var2);
   }

   public static BigInteger calculateU(Digest var0, BigInteger var1, BigInteger var2, BigInteger var3) {
      return hashPaddedPair(var0, var1, var2, var3);
   }

   public static BigInteger calculateX(Digest var0, BigInteger var1, byte[] var2, byte[] var3, byte[] var4) {
      byte[] var5 = new byte[var0.getDigestSize()];
      var0.update(var3, 0, var3.length);
      var0.update((byte)58);
      var0.update(var4, 0, var4.length);
      var0.doFinal(var5, 0);
      var0.update(var2, 0, var2.length);
      var0.update(var5, 0, var5.length);
      var0.doFinal(var5, 0);
      return new BigInteger(1, var5);
   }

   public static BigInteger generatePrivateValue(Digest var0, BigInteger var1, BigInteger var2, SecureRandom var3) {
      int var4 = Math.min(256, var1.bitLength() / 2);
      BigInteger var5 = ONE.shiftLeft(var4 - 1);
      BigInteger var6 = var1.subtract(ONE);
      return BigIntegers.createRandomInRange(var5, var6, var3);
   }

   public static BigInteger validatePublicValue(BigInteger var0, BigInteger var1) throws CryptoException {
      var1 = var1.mod(var0);
      if (var1.equals(ZERO)) {
         throw new CryptoException("Invalid public value: 0");
      } else {
         return var1;
      }
   }

   public static BigInteger calculateM1(Digest var0, BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4) {
      return hashPaddedTriplet(var0, var1, var2, var3, var4);
   }

   public static BigInteger calculateM2(Digest var0, BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4) {
      return hashPaddedTriplet(var0, var1, var2, var3, var4);
   }

   public static BigInteger calculateKey(Digest var0, BigInteger var1, BigInteger var2) {
      int var3 = (var1.bitLength() + 7) / 8;
      byte[] var4 = getPadded(var2, var3);
      var0.update(var4, 0, var4.length);
      byte[] var5 = new byte[var0.getDigestSize()];
      var0.doFinal(var5, 0);
      return new BigInteger(1, var5);
   }

   private static BigInteger hashPaddedTriplet(Digest var0, BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4) {
      int var5 = (var1.bitLength() + 7) / 8;
      byte[] var6 = getPadded(var2, var5);
      byte[] var7 = getPadded(var3, var5);
      byte[] var8 = getPadded(var4, var5);
      var0.update(var6, 0, var6.length);
      var0.update(var7, 0, var7.length);
      var0.update(var8, 0, var8.length);
      byte[] var9 = new byte[var0.getDigestSize()];
      var0.doFinal(var9, 0);
      return new BigInteger(1, var9);
   }

   private static BigInteger hashPaddedPair(Digest var0, BigInteger var1, BigInteger var2, BigInteger var3) {
      int var4 = (var1.bitLength() + 7) / 8;
      byte[] var5 = getPadded(var2, var4);
      byte[] var6 = getPadded(var3, var4);
      var0.update(var5, 0, var5.length);
      var0.update(var6, 0, var6.length);
      byte[] var7 = new byte[var0.getDigestSize()];
      var0.doFinal(var7, 0);
      return new BigInteger(1, var7);
   }

   private static byte[] getPadded(BigInteger var0, int var1) {
      byte[] var2 = BigIntegers.asUnsignedByteArray(var0);
      if (var2.length < var1) {
         byte[] var3 = new byte[var1];
         System.arraycopy(var2, 0, var3, var1 - var2.length, var2.length);
         var2 = var3;
      }

      return var2;
   }
}
