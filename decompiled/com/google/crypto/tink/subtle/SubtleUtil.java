package com.google.crypto.tink.subtle;

import com.google.crypto.tink.internal.BigIntegerEncoding;
import com.google.crypto.tink.internal.Util;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;

public final class SubtleUtil {
   public static String toEcdsaAlgo(Enums.HashType hash) throws GeneralSecurityException {
      Validators.validateSignatureHash(hash);
      return hash + "withECDSA";
   }

   public static String toRsaSsaPkcs1Algo(Enums.HashType hash) throws GeneralSecurityException {
      Validators.validateSignatureHash(hash);
      return hash + "withRSA";
   }

   public static String toDigestAlgo(Enums.HashType hash) throws GeneralSecurityException {
      switch (hash) {
         case SHA1:
            return "SHA-1";
         case SHA224:
            return "SHA-224";
         case SHA256:
            return "SHA-256";
         case SHA384:
            return "SHA-384";
         case SHA512:
            return "SHA-512";
         default:
            throw new GeneralSecurityException("Unsupported hash " + hash);
      }
   }

   public static boolean isAndroid() {
      return "The Android Project".equals(System.getProperty("java.vendor"));
   }

   @Deprecated
   public static int androidApiLevel() {
      Integer androidApiLevel = Util.getAndroidApiLevel();
      return androidApiLevel != null ? androidApiLevel : -1;
   }

   public static BigInteger bytes2Integer(byte[] bs) {
      return BigIntegerEncoding.fromUnsignedBigEndianBytes(bs);
   }

   public static byte[] integer2Bytes(BigInteger num, int intendedLength) throws GeneralSecurityException {
      return BigIntegerEncoding.toBigEndianBytesOfFixedLength(num, intendedLength);
   }

   public static byte[] mgf1(byte[] mgfSeed, int maskLen, Enums.HashType mgfHash) throws GeneralSecurityException {
      MessageDigest digest = EngineFactory.MESSAGE_DIGEST.getInstance(toDigestAlgo(mgfHash));
      int hLen = digest.getDigestLength();
      byte[] t = new byte[maskLen];
      int tPos = 0;

      for (int counter = 0; counter <= (maskLen - 1) / hLen; counter++) {
         digest.reset();
         digest.update(mgfSeed);
         digest.update(integer2Bytes(BigInteger.valueOf(counter), 4));
         byte[] c = digest.digest();
         System.arraycopy(c, 0, t, tPos, Math.min(c.length, t.length - tPos));
         tPos += c.length;
      }

      return t;
   }

   public static void putAsUnsigedInt(ByteBuffer buffer, long value) throws GeneralSecurityException {
      if (0L <= value && value < 4294967296L) {
         buffer.putInt((int)value);
      } else {
         throw new GeneralSecurityException("Index out of range");
      }
   }

   private SubtleUtil() {
   }
}
