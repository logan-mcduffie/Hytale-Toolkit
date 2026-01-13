package org.bouncycastle.crypto.generators;

import java.math.BigInteger;
import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.digests.SHA1Digest;
import org.bouncycastle.crypto.params.DSAParameterGenerationParameters;
import org.bouncycastle.crypto.params.DSAParameters;
import org.bouncycastle.crypto.params.DSAValidationParameters;
import org.bouncycastle.crypto.util.DigestFactory;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.encoders.Hex;

public class DSAParametersGenerator {
   private static final BigInteger ZERO = BigInteger.valueOf(0L);
   private static final BigInteger ONE = BigInteger.valueOf(1L);
   private static final BigInteger TWO = BigInteger.valueOf(2L);
   private Digest digest;
   private int L;
   private int N;
   private int certainty;
   private int iterations;
   private SecureRandom random;
   private boolean use186_3;
   private int usageIndex;

   public DSAParametersGenerator() {
      this(DigestFactory.createSHA1());
   }

   public DSAParametersGenerator(Digest var1) {
      this.digest = var1;
   }

   public void init(int var1, int var2, SecureRandom var3) {
      this.L = var1;
      this.N = getDefaultN(var1);
      this.certainty = var2;
      this.iterations = Math.max(getMinimumIterations(this.L), (var2 + 1) / 2);
      this.random = var3;
      this.use186_3 = false;
      this.usageIndex = -1;
   }

   public void init(DSAParameterGenerationParameters var1) {
      int var2 = var1.getL();
      int var3 = var1.getN();
      if (var2 < 1024 || var2 > 3072 || var2 % 1024 != 0) {
         throw new IllegalArgumentException("L values must be between 1024 and 3072 and a multiple of 1024");
      } else if (var2 == 1024 && var3 != 160) {
         throw new IllegalArgumentException("N must be 160 for L = 1024");
      } else if (var2 == 2048 && var3 != 224 && var3 != 256) {
         throw new IllegalArgumentException("N must be 224 or 256 for L = 2048");
      } else if (var2 == 3072 && var3 != 256) {
         throw new IllegalArgumentException("N must be 256 for L = 3072");
      } else if (this.digest.getDigestSize() * 8 < var3) {
         throw new IllegalStateException("Digest output size too small for value of N");
      } else {
         this.L = var2;
         this.N = var3;
         this.certainty = var1.getCertainty();
         this.iterations = Math.max(getMinimumIterations(var2), (this.certainty + 1) / 2);
         this.random = var1.getRandom();
         this.use186_3 = true;
         this.usageIndex = var1.getUsageIndex();
      }
   }

   public DSAParameters generateParameters() {
      return this.use186_3 ? this.generateParameters_FIPS186_3() : this.generateParameters_FIPS186_2();
   }

   private DSAParameters generateParameters_FIPS186_2() {
      byte[] var1 = new byte[20];
      byte[] var2 = new byte[20];
      byte[] var3 = new byte[20];
      byte[] var4 = new byte[20];
      int var5 = (this.L - 1) / 160;
      byte[] var6 = new byte[this.L / 8];
      if (!(this.digest instanceof SHA1Digest)) {
         throw new IllegalStateException("can only use SHA-1 for generating FIPS 186-2 parameters");
      } else {
         while (true) {
            this.random.nextBytes(var1);
            hash(this.digest, var1, var2, 0);
            System.arraycopy(var1, 0, var3, 0, var1.length);
            inc(var3);
            hash(this.digest, var3, var3, 0);

            for (int var7 = 0; var7 != var4.length; var7++) {
               var4[var7] = (byte)(var2[var7] ^ var3[var7]);
            }

            var4[0] |= -128;
            var4[19] = (byte)(var4[19] | 1);
            BigInteger var14 = new BigInteger(1, var4);
            if (this.isProbablePrime(var14)) {
               byte[] var8 = Arrays.clone(var1);
               inc(var8);

               for (int var9 = 0; var9 < 4096; var9++) {
                  for (int var10 = 1; var10 <= var5; var10++) {
                     inc(var8);
                     hash(this.digest, var8, var6, var6.length - var10 * var2.length);
                  }

                  int var15 = var6.length - var5 * var2.length;
                  inc(var8);
                  hash(this.digest, var8, var2, 0);
                  System.arraycopy(var2, var2.length - var15, var6, 0, var15);
                  var6[0] |= -128;
                  BigInteger var16 = new BigInteger(1, var6);
                  BigInteger var11 = var16.mod(var14.shiftLeft(1));
                  BigInteger var12 = var16.subtract(var11.subtract(ONE));
                  if (var12.bitLength() == this.L && this.isProbablePrime(var12)) {
                     BigInteger var13 = calculateGenerator_FIPS186_2(var12, var14, this.random);
                     return new DSAParameters(var12, var14, var13, new DSAValidationParameters(var1, var9));
                  }
               }
            }
         }
      }
   }

   private static BigInteger calculateGenerator_FIPS186_2(BigInteger var0, BigInteger var1, SecureRandom var2) {
      BigInteger var3 = var0.subtract(ONE).divide(var1);
      BigInteger var4 = var0.subtract(TWO);

      BigInteger var6;
      do {
         BigInteger var5 = BigIntegers.createRandomInRange(TWO, var4, var2);
         var6 = var5.modPow(var3, var0);
      } while (var6.bitLength() <= 1);

      return var6;
   }

   private DSAParameters generateParameters_FIPS186_3() {
      Digest var1 = this.digest;
      int var2 = var1.getDigestSize() * 8;
      int var3 = this.N;
      byte[] var4 = new byte[var3 / 8];
      int var5 = (this.L - 1) / var2;
      int var6 = (this.L - 1) % var2;
      byte[] var7 = new byte[this.L / 8];
      byte[] var8 = new byte[var1.getDigestSize()];

      while (true) {
         this.random.nextBytes(var4);
         hash(var1, var4, var8, 0);
         BigInteger var9 = new BigInteger(1, var8).mod(ONE.shiftLeft(this.N - 1));
         BigInteger var10 = var9.setBit(0).setBit(this.N - 1);
         if (this.isProbablePrime(var10)) {
            byte[] var11 = Arrays.clone(var4);
            int var12 = 4 * this.L;

            for (int var13 = 0; var13 < var12; var13++) {
               for (int var14 = 1; var14 <= var5; var14++) {
                  inc(var11);
                  hash(var1, var11, var7, var7.length - var14 * var8.length);
               }

               int var18 = var7.length - var5 * var8.length;
               inc(var11);
               hash(var1, var11, var8, 0);
               System.arraycopy(var8, var8.length - var18, var7, 0, var18);
               var7[0] |= -128;
               BigInteger var19 = new BigInteger(1, var7);
               BigInteger var15 = var19.mod(var10.shiftLeft(1));
               BigInteger var16 = var19.subtract(var15.subtract(ONE));
               if (var16.bitLength() == this.L && this.isProbablePrime(var16)) {
                  if (this.usageIndex >= 0) {
                     BigInteger var17 = calculateGenerator_FIPS186_3_Verifiable(var1, var16, var10, var4, this.usageIndex);
                     if (var17 != null) {
                        return new DSAParameters(var16, var10, var17, new DSAValidationParameters(var4, var13, this.usageIndex));
                     }
                  }

                  BigInteger var20 = calculateGenerator_FIPS186_3_Unverifiable(var16, var10, this.random);
                  return new DSAParameters(var16, var10, var20, new DSAValidationParameters(var4, var13));
               }
            }
         }
      }
   }

   private boolean isProbablePrime(BigInteger var1) {
      return var1.isProbablePrime(this.certainty);
   }

   private static BigInteger calculateGenerator_FIPS186_3_Unverifiable(BigInteger var0, BigInteger var1, SecureRandom var2) {
      return calculateGenerator_FIPS186_2(var0, var1, var2);
   }

   private static BigInteger calculateGenerator_FIPS186_3_Verifiable(Digest var0, BigInteger var1, BigInteger var2, byte[] var3, int var4) {
      BigInteger var5 = var1.subtract(ONE).divide(var2);
      byte[] var6 = Hex.decodeStrict("6767656E");
      byte[] var7 = new byte[var3.length + var6.length + 1 + 2];
      System.arraycopy(var3, 0, var7, 0, var3.length);
      System.arraycopy(var6, 0, var7, var3.length, var6.length);
      var7[var7.length - 3] = (byte)var4;
      byte[] var8 = new byte[var0.getDigestSize()];

      for (int var9 = 1; var9 < 65536; var9++) {
         inc(var7);
         hash(var0, var7, var8, 0);
         BigInteger var10 = new BigInteger(1, var8);
         BigInteger var11 = var10.modPow(var5, var1);
         if (var11.compareTo(TWO) >= 0) {
            return var11;
         }
      }

      return null;
   }

   private static void hash(Digest var0, byte[] var1, byte[] var2, int var3) {
      var0.update(var1, 0, var1.length);
      var0.doFinal(var2, var3);
   }

   private static int getDefaultN(int var0) {
      return var0 > 1024 ? 256 : 160;
   }

   private static int getMinimumIterations(int var0) {
      return var0 <= 1024 ? 40 : 48 + 8 * ((var0 - 1) / 1024);
   }

   private static void inc(byte[] var0) {
      for (int var1 = var0.length - 1; var1 >= 0; var1--) {
         byte var2 = (byte)(var0[var1] + 1 & 0xFF);
         var0[var1] = var2;
         if (var2 != 0) {
            break;
         }
      }
   }
}
