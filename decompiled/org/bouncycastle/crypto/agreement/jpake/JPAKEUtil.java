package org.bouncycastle.crypto.agreement.jpake;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Strings;

public class JPAKEUtil {
   static final BigInteger ZERO = BigInteger.valueOf(0L);
   static final BigInteger ONE = BigInteger.valueOf(1L);

   public static BigInteger generateX1(BigInteger var0, SecureRandom var1) {
      BigInteger var2 = ZERO;
      BigInteger var3 = var0.subtract(ONE);
      return BigIntegers.createRandomInRange(var2, var3, var1);
   }

   public static BigInteger generateX2(BigInteger var0, SecureRandom var1) {
      BigInteger var2 = ONE;
      BigInteger var3 = var0.subtract(ONE);
      return BigIntegers.createRandomInRange(var2, var3, var1);
   }

   /** @deprecated */
   public static BigInteger calculateS(char[] var0) {
      return new BigInteger(1, Strings.toUTF8ByteArray(var0));
   }

   public static BigInteger calculateS(BigInteger var0, byte[] var1) throws CryptoException {
      BigInteger var2 = new BigInteger(1, var1).mod(var0);
      if (var2.signum() == 0) {
         throw new CryptoException("MUST ensure s is not equal to 0 modulo q");
      } else {
         return var2;
      }
   }

   public static BigInteger calculateS(BigInteger var0, char[] var1) throws CryptoException {
      return calculateS(var0, Strings.toUTF8ByteArray(var1));
   }

   public static BigInteger calculateGx(BigInteger var0, BigInteger var1, BigInteger var2) {
      return var1.modPow(var2, var0);
   }

   public static BigInteger calculateGA(BigInteger var0, BigInteger var1, BigInteger var2, BigInteger var3) {
      return var1.multiply(var2).multiply(var3).mod(var0);
   }

   public static BigInteger calculateX2s(BigInteger var0, BigInteger var1, BigInteger var2) {
      return var1.multiply(var2).mod(var0);
   }

   public static BigInteger calculateA(BigInteger var0, BigInteger var1, BigInteger var2, BigInteger var3) {
      return var2.modPow(var3, var0);
   }

   public static BigInteger[] calculateZeroKnowledgeProof(
      BigInteger var0, BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, String var5, Digest var6, SecureRandom var7
   ) {
      BigInteger[] var8 = new BigInteger[2];
      BigInteger var9 = ZERO;
      BigInteger var10 = var1.subtract(ONE);
      BigInteger var11 = BigIntegers.createRandomInRange(var9, var10, var7);
      BigInteger var12 = var2.modPow(var11, var0);
      BigInteger var13 = calculateHashForZeroKnowledgeProof(var2, var12, var3, var5, var6);
      var8[0] = var12;
      var8[1] = var11.subtract(var4.multiply(var13)).mod(var1);
      return var8;
   }

   private static BigInteger calculateHashForZeroKnowledgeProof(BigInteger var0, BigInteger var1, BigInteger var2, String var3, Digest var4) {
      var4.reset();
      updateDigestIncludingSize(var4, var0);
      updateDigestIncludingSize(var4, var1);
      updateDigestIncludingSize(var4, var2);
      updateDigestIncludingSize(var4, var3);
      byte[] var5 = new byte[var4.getDigestSize()];
      var4.doFinal(var5, 0);
      return new BigInteger(var5);
   }

   public static void validateGx4(BigInteger var0) throws CryptoException {
      if (var0.equals(ONE)) {
         throw new CryptoException("g^x validation failed.  g^x should not be 1.");
      }
   }

   public static void validateGa(BigInteger var0) throws CryptoException {
      if (var0.equals(ONE)) {
         throw new CryptoException("ga is equal to 1.  It should not be.  The chances of this happening are on the order of 2^160 for a 160-bit q.  Try again.");
      }
   }

   public static void validateZeroKnowledgeProof(
      BigInteger var0, BigInteger var1, BigInteger var2, BigInteger var3, BigInteger[] var4, String var5, Digest var6
   ) throws CryptoException {
      BigInteger var7 = var4[0];
      BigInteger var8 = var4[1];
      BigInteger var9 = calculateHashForZeroKnowledgeProof(var2, var7, var3, var5, var6);
      if (var3.signum() <= 0
         || var3.compareTo(var0) >= 0
         || !var3.modPow(var1, var0).equals(ONE)
         || !var2.modPow(var8, var0).multiply(var3.modPow(var9, var0)).mod(var0).equals(var7)) {
         throw new CryptoException("Zero-knowledge proof validation failed");
      }
   }

   public static BigInteger calculateKeyingMaterial(BigInteger var0, BigInteger var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5) {
      return var2.modPow(var3.multiply(var4).negate().mod(var1), var0).multiply(var5).modPow(var3, var0);
   }

   public static void validateParticipantIdsDiffer(String var0, String var1) throws CryptoException {
      if (var0.equals(var1)) {
         throw new CryptoException(
            "Both participants are using the same participantId (" + var0 + "). This is not allowed. Each participant must use a unique participantId."
         );
      }
   }

   public static void validateParticipantIdsEqual(String var0, String var1) throws CryptoException {
      if (!var0.equals(var1)) {
         throw new CryptoException("Received payload from incorrect partner (" + var1 + "). Expected to receive payload from " + var0 + ".");
      }
   }

   public static void validateNotNull(Object var0, String var1) {
      if (var0 == null) {
         throw new NullPointerException(var1 + " must not be null");
      }
   }

   public static BigInteger calculateMacTag(
      String var0, String var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5, BigInteger var6, Digest var7
   ) {
      byte[] var8 = calculateMacKey(var6, var7);
      HMac var9 = new HMac(var7);
      byte[] var10 = new byte[var9.getMacSize()];
      var9.init(new KeyParameter(var8));
      updateMac(var9, "KC_1_U");
      updateMac(var9, var0);
      updateMac(var9, var1);
      updateMac(var9, var2);
      updateMac(var9, var3);
      updateMac(var9, var4);
      updateMac(var9, var5);
      var9.doFinal(var10, 0);
      Arrays.fill(var8, (byte)0);
      return new BigInteger(var10);
   }

   private static byte[] calculateMacKey(BigInteger var0, Digest var1) {
      var1.reset();
      updateDigest(var1, var0);
      updateDigest(var1, "JPAKE_KC");
      byte[] var2 = new byte[var1.getDigestSize()];
      var1.doFinal(var2, 0);
      return var2;
   }

   public static void validateMacTag(
      String var0, String var1, BigInteger var2, BigInteger var3, BigInteger var4, BigInteger var5, BigInteger var6, Digest var7, BigInteger var8
   ) throws CryptoException {
      BigInteger var9 = calculateMacTag(var1, var0, var4, var5, var2, var3, var6, var7);
      if (!var9.equals(var8)) {
         throw new CryptoException("Partner MacTag validation failed. Therefore, the password, MAC, or digest algorithm of each participant does not match.");
      }
   }

   private static void updateDigest(Digest var0, BigInteger var1) {
      byte[] var2 = BigIntegers.asUnsignedByteArray(var1);
      var0.update(var2, 0, var2.length);
      Arrays.fill(var2, (byte)0);
   }

   private static void updateDigestIncludingSize(Digest var0, BigInteger var1) {
      byte[] var2 = BigIntegers.asUnsignedByteArray(var1);
      var0.update(intToByteArray(var2.length), 0, 4);
      var0.update(var2, 0, var2.length);
      Arrays.fill(var2, (byte)0);
   }

   private static void updateDigest(Digest var0, String var1) {
      byte[] var2 = Strings.toUTF8ByteArray(var1);
      var0.update(var2, 0, var2.length);
      Arrays.fill(var2, (byte)0);
   }

   private static void updateDigestIncludingSize(Digest var0, String var1) {
      byte[] var2 = Strings.toUTF8ByteArray(var1);
      var0.update(intToByteArray(var2.length), 0, 4);
      var0.update(var2, 0, var2.length);
      Arrays.fill(var2, (byte)0);
   }

   private static void updateMac(Mac var0, BigInteger var1) {
      byte[] var2 = BigIntegers.asUnsignedByteArray(var1);
      var0.update(var2, 0, var2.length);
      Arrays.fill(var2, (byte)0);
   }

   private static void updateMac(Mac var0, String var1) {
      byte[] var2 = Strings.toUTF8ByteArray(var1);
      var0.update(var2, 0, var2.length);
      Arrays.fill(var2, (byte)0);
   }

   private static byte[] intToByteArray(int var0) {
      return new byte[]{(byte)(var0 >>> 24), (byte)(var0 >>> 16), (byte)(var0 >>> 8), (byte)var0};
   }
}
