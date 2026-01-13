package org.bouncycastle.crypto.tls;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.CryptoServicePurpose;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.DataLengthException;
import org.bouncycastle.crypto.constraints.ConstraintUtils;
import org.bouncycastle.crypto.constraints.DefaultServiceProperties;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.params.RSAPrivateCrtKeyParameters;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Pack;

public abstract class TlsRsaKeyExchange {
   public static final int PRE_MASTER_SECRET_LENGTH = 48;
   private static final BigInteger ONE = BigInteger.valueOf(1L);

   private TlsRsaKeyExchange() {
   }

   public static byte[] decryptPreMasterSecret(byte[] var0, int var1, int var2, RSAKeyParameters var3, int var4, SecureRandom var5) {
      if (var0 == null || var2 < 1 || var2 > getInputLimit(var3) || var1 < 0 || var1 > var0.length - var2) {
         throw new IllegalArgumentException("input not a valid EncryptedPreMasterSecret");
      } else if (!var3.isPrivate()) {
         throw new IllegalArgumentException("'privateKey' must be an RSA private key");
      } else {
         BigInteger var6 = var3.getModulus();
         int var7 = var6.bitLength();
         if (var7 < 512) {
            throw new IllegalArgumentException("'privateKey' must be at least 512 bits");
         } else {
            int var8 = ConstraintUtils.bitsOfSecurityFor(var6);
            CryptoServicesRegistrar.checkConstraints(new DefaultServiceProperties("RSA", var8, var3, CryptoServicePurpose.DECRYPTION));
            if ((var4 & 65535) != var4) {
               throw new IllegalArgumentException("'protocolVersion' must be a 16 bit value");
            } else {
               var5 = CryptoServicesRegistrar.getSecureRandom(var5);
               byte[] var9 = new byte[48];
               var5.nextBytes(var9);

               try {
                  BigInteger var10 = convertInput(var6, var0, var1, var2);
                  byte[] var11 = rsaBlinded(var3, var10, var5);
                  int var12 = (var7 - 1) / 8;
                  int var13 = var11.length - 48;
                  int var14 = checkPkcs1Encoding2(var11, var12, 48);
                  int var15 = -((Pack.bigEndianToShort(var11, var13) ^ var4) & 65535) >> 31;
                  int var16 = var14 | var15;

                  for (int var17 = 0; var17 < 48; var17++) {
                     var9[var17] = (byte)(var9[var17] & var16 | var11[var13 + var17] & ~var16);
                  }

                  Arrays.fill(var11, (byte)0);
               } catch (Exception var18) {
               }

               return var9;
            }
         }
      }
   }

   public static int getInputLimit(RSAKeyParameters var0) {
      return (var0.getModulus().bitLength() + 7) / 8;
   }

   private static int caddTo(int var0, int var1, byte[] var2, byte[] var3) {
      int var4 = var1 & 0xFF;
      int var5 = 0;

      for (int var6 = var0 - 1; var6 >= 0; var6--) {
         var5 += (var3[var6] & 255) + (var2[var6] & var4);
         var3[var6] = (byte)var5;
         var5 >>>= 8;
      }

      return var5;
   }

   private static int checkPkcs1Encoding2(byte[] var0, int var1, int var2) {
      int var3 = var1 - var2 - 10;
      int var4 = var0.length - var1;
      int var5 = var0.length - 1 - var2;

      for (int var6 = 0; var6 < var4; var6++) {
         var3 |= -(var0[var6] & 255);
      }

      var3 |= -(var0[var4] & 255 ^ 2);

      for (int var9 = var4 + 1; var9 < var5; var9++) {
         var3 |= (var0[var9] & 255) - 1;
      }

      var3 |= -(var0[var5] & 255);
      return var3 >> 31;
   }

   private static BigInteger convertInput(BigInteger var0, byte[] var1, int var2, int var3) {
      BigInteger var4 = BigIntegers.fromUnsignedByteArray(var1, var2, var3);
      if (var4.compareTo(var0) < 0) {
         return var4;
      } else {
         throw new DataLengthException("input too large for RSA cipher.");
      }
   }

   private static BigInteger rsa(RSAKeyParameters var0, BigInteger var1) {
      return var1.modPow(var0.getExponent(), var0.getModulus());
   }

   private static byte[] rsaBlinded(RSAKeyParameters var0, BigInteger var1, SecureRandom var2) {
      BigInteger var3 = var0.getModulus();
      int var4 = var3.bitLength() / 8 + 1;
      if (var0 instanceof RSAPrivateCrtKeyParameters) {
         RSAPrivateCrtKeyParameters var5 = (RSAPrivateCrtKeyParameters)var0;
         BigInteger var6 = var5.getPublicExponent();
         if (var6 != null) {
            BigInteger var7 = BigIntegers.createRandomInRange(ONE, var3.subtract(ONE), var2);
            BigInteger var8 = var7.modPow(var6, var3);
            BigInteger var9 = BigIntegers.modOddInverse(var3, var7);
            BigInteger var10 = var8.multiply(var1).mod(var3);
            BigInteger var11 = rsaCrt(var5, var10);
            BigInteger var12 = var9.add(ONE).multiply(var11).mod(var3);
            byte[] var13 = toBytes(var11, var4);
            byte[] var14 = toBytes(var3, var4);
            byte[] var15 = toBytes(var12, var4);
            int var16 = subFrom(var4, var13, var15);
            caddTo(var4, var16, var14, var15);
            return var15;
         }
      }

      return toBytes(rsa(var0, var1), var4);
   }

   private static BigInteger rsaCrt(RSAPrivateCrtKeyParameters var0, BigInteger var1) {
      BigInteger var2 = var0.getPublicExponent();
      BigInteger var3 = var0.getP();
      BigInteger var4 = var0.getQ();
      BigInteger var5 = var0.getDP();
      BigInteger var6 = var0.getDQ();
      BigInteger var7 = var0.getQInv();
      BigInteger var8 = var1.remainder(var3).modPow(var5, var3);
      BigInteger var9 = var1.remainder(var4).modPow(var6, var4);
      BigInteger var10 = var8.subtract(var9);
      var10 = var10.multiply(var7);
      var10 = var10.mod(var3);
      BigInteger var11 = var10.multiply(var4).add(var9);
      BigInteger var12 = var11.modPow(var2, var0.getModulus());
      if (!var12.equals(var1)) {
         throw new IllegalStateException("RSA engine faulty decryption/signing detected");
      } else {
         return var11;
      }
   }

   private static int subFrom(int var0, byte[] var1, byte[] var2) {
      int var3 = 0;

      for (int var4 = var0 - 1; var4 >= 0; var4--) {
         var3 += (var2[var4] & 255) - (var1[var4] & 255);
         var2[var4] = (byte)var3;
         var3 >>= 8;
      }

      return var3;
   }

   private static byte[] toBytes(BigInteger var0, int var1) {
      byte[] var2 = var0.toByteArray();
      byte[] var3 = new byte[var1];
      System.arraycopy(var2, 0, var3, var3.length - var2.length, var2.length);
      return var3;
   }
}
