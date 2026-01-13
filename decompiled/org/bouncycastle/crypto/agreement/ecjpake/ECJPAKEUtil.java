package org.bouncycastle.crypto.agreement.ecjpake;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoException;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.Mac;
import org.bouncycastle.crypto.macs.HMac;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.math.ec.ECCurve;
import org.bouncycastle.math.ec.ECPoint;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Strings;

public class ECJPAKEUtil {
   static final BigInteger ZERO = BigInteger.valueOf(0L);
   static final BigInteger ONE = BigInteger.valueOf(1L);

   public static BigInteger generateX1(BigInteger var0, SecureRandom var1) {
      BigInteger var2 = ONE;
      BigInteger var3 = var0.subtract(ONE);
      return BigIntegers.createRandomInRange(var2, var3, var1);
   }

   public static BigInteger calculateS(BigInteger var0, byte[] var1) throws CryptoException {
      BigInteger var2 = new BigInteger(1, var1).mod(var0);
      if (var2.signum() == 0) {
         throw new CryptoException("MUST ensure s is not equal to 0 modulo n");
      } else {
         return var2;
      }
   }

   public static BigInteger calculateS(BigInteger var0, char[] var1) throws CryptoException {
      return calculateS(var0, Strings.toUTF8ByteArray(var1));
   }

   public static ECPoint calculateGx(ECPoint var0, BigInteger var1) {
      return var0.multiply(var1);
   }

   public static ECPoint calculateGA(ECPoint var0, ECPoint var1, ECPoint var2) {
      return var0.add(var1).add(var2);
   }

   public static BigInteger calculateX2s(BigInteger var0, BigInteger var1, BigInteger var2) {
      return var1.multiply(var2).mod(var0);
   }

   public static ECPoint calculateA(ECPoint var0, BigInteger var1) {
      return var0.multiply(var1);
   }

   public static ECSchnorrZKP calculateZeroKnowledgeProof(
      ECPoint var0, BigInteger var1, BigInteger var2, ECPoint var3, Digest var4, String var5, SecureRandom var6
   ) {
      BigInteger var7 = BigIntegers.createRandomInRange(BigInteger.ONE, var1.subtract(BigInteger.ONE), var6);
      ECPoint var8 = var0.multiply(var7);
      BigInteger var9 = calculateHashForZeroKnowledgeProof(var0, var8, var3, var5, var4);
      return new ECSchnorrZKP(var8, var7.subtract(var2.multiply(var9)).mod(var1));
   }

   private static BigInteger calculateHashForZeroKnowledgeProof(ECPoint var0, ECPoint var1, ECPoint var2, String var3, Digest var4) {
      var4.reset();
      updateDigestIncludingSize(var4, var0);
      updateDigestIncludingSize(var4, var1);
      updateDigestIncludingSize(var4, var2);
      updateDigestIncludingSize(var4, var3);
      byte[] var5 = new byte[var4.getDigestSize()];
      var4.doFinal(var5, 0);
      return new BigInteger(var5);
   }

   private static void updateDigestIncludingSize(Digest var0, ECPoint var1) {
      byte[] var2 = var1.getEncoded(true);
      var0.update(intToByteArray(var2.length), 0, 4);
      var0.update(var2, 0, var2.length);
      Arrays.fill(var2, (byte)0);
   }

   private static void updateDigestIncludingSize(Digest var0, String var1) {
      byte[] var2 = Strings.toUTF8ByteArray(var1);
      var0.update(intToByteArray(var2.length), 0, 4);
      var0.update(var2, 0, var2.length);
      Arrays.fill(var2, (byte)0);
   }

   public static void validateZeroKnowledgeProof(
      ECPoint var0, ECPoint var1, ECSchnorrZKP var2, BigInteger var3, BigInteger var4, ECCurve var5, BigInteger var6, String var7, Digest var8
   ) throws CryptoException {
      ECPoint var9 = var2.getV();
      BigInteger var10 = var2.getr();
      BigInteger var11 = calculateHashForZeroKnowledgeProof(var0, var9, var1, var7, var8);
      if (var1.isInfinity()) {
         throw new CryptoException("Zero-knowledge proof validation failed: X cannot equal infinity");
      } else {
         ECPoint var12 = var1.normalize();
         if (var12.getAffineXCoord().toBigInteger().signum() >= 0
            && var12.getAffineXCoord().toBigInteger().compareTo(var3) < 0
            && var12.getAffineYCoord().toBigInteger().signum() >= 0
            && var12.getAffineYCoord().toBigInteger().compareTo(var3) < 0) {
            try {
               var5.decodePoint(var1.getEncoded(true));
            } catch (Exception var14) {
               throw new CryptoException("Zero-knowledge proof validation failed: x does not lie on the curve", var14);
            }

            if (var1.multiply(var6).isInfinity()) {
               throw new CryptoException("Zero-knowledge proof validation failed: Nx cannot be infinity");
            } else if (!var9.equals(var0.multiply(var10).add(var1.multiply(var11.mod(var4))))) {
               throw new CryptoException("Zero-knowledge proof validation failed: V must be a point on the curve");
            }
         } else {
            throw new CryptoException("Zero-knowledge proof validation failed: x and y are not in the field");
         }
      }
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

   public static BigInteger calculateKeyingMaterial(BigInteger var0, ECPoint var1, BigInteger var2, BigInteger var3, ECPoint var4) {
      ECPoint var5 = var4.subtract(var1.multiply(var2.multiply(var3).mod(var0))).multiply(var2);
      var5 = var5.normalize();
      return var5.getAffineXCoord().toBigInteger();
   }

   public static BigInteger calculateMacTag(String var0, String var1, ECPoint var2, ECPoint var3, ECPoint var4, ECPoint var5, BigInteger var6, Digest var7) {
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
      updateDigest(var1, "ECJPAKE_KC");
      byte[] var2 = new byte[var1.getDigestSize()];
      var1.doFinal(var2, 0);
      return var2;
   }

   public static void validateMacTag(
      String var0, String var1, ECPoint var2, ECPoint var3, ECPoint var4, ECPoint var5, BigInteger var6, Digest var7, BigInteger var8
   ) throws CryptoException {
      BigInteger var9 = calculateMacTag(var1, var0, var4, var5, var2, var3, var6, var7);
      if (!var9.equals(var8)) {
         throw new CryptoException("Partner MacTag validation failed. Therefore, the password, MAC, or digest algorithm of each participant does not match.");
      }
   }

   private static void updateMac(Mac var0, ECPoint var1) {
      byte[] var2 = var1.getEncoded(true);
      var0.update(var2, 0, var2.length);
      Arrays.fill(var2, (byte)0);
   }

   private static void updateMac(Mac var0, String var1) {
      byte[] var2 = Strings.toUTF8ByteArray(var1);
      var0.update(var2, 0, var2.length);
      Arrays.fill(var2, (byte)0);
   }

   private static void updateDigest(Digest var0, ECPoint var1) {
      byte[] var2 = var1.getEncoded(true);
      var0.update(var2, 0, var2.length);
      Arrays.fill(var2, (byte)0);
   }

   private static void updateDigest(Digest var0, String var1) {
      byte[] var2 = Strings.toUTF8ByteArray(var1);
      var0.update(var2, 0, var2.length);
      Arrays.fill(var2, (byte)0);
   }

   private static void updateDigest(Digest var0, BigInteger var1) {
      byte[] var2 = BigIntegers.asUnsignedByteArray(var1);
      var0.update(var2, 0, var2.length);
      Arrays.fill(var2, (byte)0);
   }

   private static byte[] intToByteArray(int var0) {
      return new byte[]{(byte)(var0 >>> 24), (byte)(var0 >>> 16), (byte)(var0 >>> 8), (byte)var0};
   }
}
