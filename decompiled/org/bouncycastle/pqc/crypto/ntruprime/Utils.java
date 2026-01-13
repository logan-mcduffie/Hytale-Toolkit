package org.bouncycastle.pqc.crypto.ntruprime;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHA512Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CTRModeCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

class Utils {
   protected static int getRandomUnsignedInteger(SecureRandom var0) {
      byte[] var1 = new byte[4];
      var0.nextBytes(var1);
      return bToUnsignedInt(var1[0]) + (bToUnsignedInt(var1[1]) << 8) + (bToUnsignedInt(var1[2]) << 16) + (bToUnsignedInt(var1[3]) << 24);
   }

   protected static void getRandomSmallPolynomial(SecureRandom var0, byte[] var1) {
      for (int var2 = 0; var2 < var1.length; var2++) {
         var1[var2] = (byte)(((getRandomUnsignedInteger(var0) & 1073741823) * 3 >>> 30) - 1);
      }
   }

   protected static int getModFreeze(int var0, int var1) {
      return getSignedDivMod(var0 + (var1 - 1) / 2, var1)[1] - (var1 - 1) / 2;
   }

   protected static boolean isInvertiblePolynomialInR3(byte[] var0, byte[] var1, int var2) {
      byte[] var3 = new byte[var2 + 1];
      byte[] var4 = new byte[var2 + 1];
      byte[] var5 = new byte[var2 + 1];
      byte[] var6 = new byte[var2 + 1];
      var5[0] = 1;
      var3[0] = 1;
      var3[var2 - 1] = -1;
      var3[var2] = -1;

      for (int var12 = 0; var12 < var2; var12++) {
         var4[var2 - 1 - var12] = var0[var12];
      }

      var4[var2] = 0;
      int var13 = 1;

      for (int var7 = 0; var7 < 2 * var2 - 1; var7++) {
         System.arraycopy(var6, 0, var6, 1, var2);
         var6[0] = 0;
         int var9 = -var4[0] * var3[0];
         int var10 = checkLessThanZero(-var13) & checkNotEqualToZero(var4[0]);
         var13 ^= var10 & (var13 ^ -var13);
         var13++;

         for (int var16 = 0; var16 < var2 + 1; var16++) {
            int var11 = var10 & (var3[var16] ^ var4[var16]);
            var3[var16] = (byte)(var3[var16] ^ var11);
            var4[var16] = (byte)(var4[var16] ^ var11);
            var11 = var10 & (var6[var16] ^ var5[var16]);
            var6[var16] = (byte)(var6[var16] ^ var11);
            var5[var16] = (byte)(var5[var16] ^ var11);
         }

         for (int var17 = 0; var17 < var2 + 1; var17++) {
            var4[var17] = (byte)getModFreeze(var4[var17] + var9 * var3[var17], 3);
         }

         for (int var18 = 0; var18 < var2 + 1; var18++) {
            var5[var18] = (byte)getModFreeze(var5[var18] + var9 * var6[var18], 3);
         }

         for (int var19 = 0; var19 < var2; var19++) {
            var4[var19] = var4[var19 + 1];
         }

         var4[var2] = 0;
      }

      byte var14 = var3[0];

      for (int var20 = 0; var20 < var2; var20++) {
         var1[var20] = (byte)(var14 * var6[var2 - 1 - var20]);
      }

      return var13 == 0;
   }

   protected static void minmax(int[] var0, int var1, int var2) {
      int var3 = var0[var1];
      int var4 = var0[var2];
      int var5 = var3 ^ var4;
      int var6 = var4 - var3;
      var6 ^= var5 & (var6 ^ var4 ^ Integer.MIN_VALUE);
      var6 >>>= 31;
      var6 = -var6;
      var6 &= var5;
      var0[var1] = var3 ^ var6;
      var0[var2] = var4 ^ var6;
   }

   protected static void cryptoSort(int[] var0, int var1) {
      if (var1 >= 2) {
         byte var2 = 1;

         while (var2 < var1 - var2) {
            var2 += var2;
         }

         for (byte var3 = var2; var3 > 0; var3 >>>= 1) {
            for (int var5 = 0; var5 < var1 - var3; var5++) {
               if ((var5 & var3) == 0) {
                  minmax(var0, var5, var5 + var3);
               }
            }

            for (byte var4 = var2; var4 > var3; var4 >>>= 1) {
               for (int var6 = 0; var6 < var1 - var4; var6++) {
                  if ((var6 & var3) == 0) {
                     minmax(var0, var6 + var3, var6 + var4);
                  }
               }
            }
         }
      }
   }

   protected static void sortGenerateShortPolynomial(byte[] var0, int[] var1, int var2, int var3) {
      for (int var4 = 0; var4 < var3; var4++) {
         var1[var4] &= -2;
      }

      for (int var5 = var3; var5 < var2; var5++) {
         var1[var5] = var1[var5] & -3 | 1;
      }

      cryptoSort(var1, var2);

      for (int var6 = 0; var6 < var2; var6++) {
         var0[var6] = (byte)((var1[var6] & 3) - 1);
      }
   }

   protected static void getRandomShortPolynomial(SecureRandom var0, byte[] var1, int var2, int var3) {
      int[] var4 = new int[var2];

      for (int var5 = 0; var5 < var2; var5++) {
         var4[var5] = getRandomUnsignedInteger(var0);
      }

      sortGenerateShortPolynomial(var1, var4, var2, var3);
   }

   protected static int getInverseInRQ(int var0, int var1) {
      int var2 = var0;

      for (int var3 = 1; var3 < var1 - 2; var3++) {
         var2 = getModFreeze(var0 * var2, var1);
      }

      return var2;
   }

   protected static void getOneThirdInverseInRQ(short[] var0, byte[] var1, int var2, int var3) {
      short[] var4 = new short[var2 + 1];
      short[] var5 = new short[var2 + 1];
      short[] var6 = new short[var2 + 1];
      short[] var7 = new short[var2 + 1];
      var6[0] = (short)getInverseInRQ(3, var3);
      var4[0] = 1;
      var4[var2 - 1] = -1;
      var4[var2] = -1;

      for (int var15 = 0; var15 < var2; var15++) {
         var5[var2 - 1 - var15] = var1[var15];
      }

      var5[var2] = 0;
      int var16 = 1;

      for (int var8 = 0; var8 < 2 * var2 - 1; var8++) {
         System.arraycopy(var7, 0, var7, 1, var2);
         var7[0] = 0;
         int var11 = checkLessThanZero(-var16) & checkNotEqualToZero(var5[0]);
         var16 ^= var11 & (var16 ^ -var16);
         var16++;

         for (int var18 = 0; var18 < var2 + 1; var18++) {
            int var14 = var11 & (var4[var18] ^ var5[var18]);
            var4[var18] = (short)(var4[var18] ^ var14);
            var5[var18] = (short)(var5[var18] ^ var14);
            var14 = var11 & (var7[var18] ^ var6[var18]);
            var7[var18] = (short)(var7[var18] ^ var14);
            var6[var18] = (short)(var6[var18] ^ var14);
         }

         short var12 = var4[0];
         short var13 = var5[0];

         for (int var19 = 0; var19 < var2 + 1; var19++) {
            var5[var19] = (short)getModFreeze(var12 * var5[var19] - var13 * var4[var19], var3);
         }

         for (int var20 = 0; var20 < var2 + 1; var20++) {
            var6[var20] = (short)getModFreeze(var12 * var6[var20] - var13 * var7[var20], var3);
         }

         for (int var21 = 0; var21 < var2; var21++) {
            var5[var21] = var5[var21 + 1];
         }

         var5[var2] = 0;
      }

      int var10 = getInverseInRQ(var4[0], var3);

      for (int var22 = 0; var22 < var2; var22++) {
         var0[var22] = (short)getModFreeze(var10 * var7[var2 - 1 - var22], var3);
      }
   }

   protected static void multiplicationInRQ(short[] var0, short[] var1, byte[] var2, int var3, int var4) {
      short[] var5 = new short[var3 + var3 - 1];

      for (int var7 = 0; var7 < var3; var7++) {
         short var6 = 0;

         for (int var8 = 0; var8 <= var7; var8++) {
            var6 = (short)getModFreeze(var6 + var1[var8] * var2[var7 - var8], var4);
         }

         var5[var7] = var6;
      }

      for (int var10 = var3; var10 < var3 + var3 - 1; var10++) {
         short var9 = 0;

         for (int var13 = var10 - var3 + 1; var13 < var3; var13++) {
            var9 = (short)getModFreeze(var9 + var1[var13] * var2[var10 - var13], var4);
         }

         var5[var10] = var9;
      }

      for (int var11 = var3 + var3 - 2; var11 >= var3; var11--) {
         var5[var11 - var3] = (short)getModFreeze(var5[var11 - var3] + var5[var11], var4);
         var5[var11 - var3 + 1] = (short)getModFreeze(var5[var11 - var3 + 1] + var5[var11], var4);
      }

      for (int var12 = 0; var12 < var3; var12++) {
         var0[var12] = var5[var12];
      }
   }

   private static void encode(byte[] var0, short[] var1, short[] var2, int var3, int var4) {
      if (var3 == 1) {
         short var5 = var1[0];

         for (short var6 = var2[0]; var6 > 1; var6 = (short)(var6 + 255 >>> 8)) {
            var0[var4++] = (byte)var5;
            var5 = (short)(var5 >>> 8);
         }
      }

      if (var3 > 1) {
         short[] var11 = new short[(var3 + 1) / 2];
         short[] var12 = new short[(var3 + 1) / 2];

         byte var7;
         for (var7 = 0; var7 < var3 - 1; var7 += 2) {
            short var8 = var2[var7];
            int var9 = var1[var7] + var1[var7 + 1] * var8;

            int var10;
            for (var10 = var2[var7 + 1] * var8; var10 >= 16384; var10 = var10 + 255 >>> 8) {
               var0[var4++] = (byte)var9;
               var9 >>>= 8;
            }

            var11[var7 / 2] = (short)var9;
            var12[var7 / 2] = (short)var10;
         }

         if (var7 < var3) {
            var11[var7 / 2] = var1[var7];
            var12[var7 / 2] = var2[var7];
         }

         encode(var0, var11, var12, (var3 + 1) / 2, var4);
      }
   }

   protected static void getEncodedPolynomial(byte[] var0, short[] var1, int var2, int var3) {
      short[] var4 = new short[var2];
      short[] var5 = new short[var2];

      for (int var6 = 0; var6 < var2; var6++) {
         var4[var6] = (short)(var1[var6] + (var3 - 1) / 2);
      }

      for (int var7 = 0; var7 < var2; var7++) {
         var5[var7] = (short)var3;
      }

      encode(var0, var4, var5, var2, 0);
   }

   protected static void getEncodedSmallPolynomial(byte[] var0, byte[] var1, int var2) {
      int var4 = 0;
      int var5 = 0;

      for (int var6 = 0; var6 < var2 / 4; var6++) {
         byte var3 = (byte)(var1[var4++] + 1);
         var3 = (byte)(var3 + ((byte)(var1[var4++] + 1) << 2));
         var3 = (byte)(var3 + ((byte)(var1[var4++] + 1) << 4));
         var3 = (byte)(var3 + ((byte)(var1[var4++] + 1) << 6));
         var0[var5++] = var3;
      }

      var0[var5] = (byte)(var1[var4] + 1);
   }

   private static void generateAES256CTRStream(byte[] var0, byte[] var1, byte[] var2, byte[] var3) {
      CTRModeCipher var4 = SICBlockCipher.newInstance(AESEngine.newInstance());
      var4.init(true, new ParametersWithIV(new KeyParameter(var3), var2));
      var4.processBytes(var0, 0, var1.length, var1, 0);
   }

   protected static void expand(int[] var0, byte[] var1) {
      byte[] var2 = new byte[var0.length * 4];
      byte[] var3 = new byte[var0.length * 4];
      byte[] var4 = new byte[16];
      generateAES256CTRStream(var2, var3, var4, var1);

      for (int var5 = 0; var5 < var0.length; var5++) {
         var0[var5] = bToUnsignedInt(var3[var5 * 4])
            + (bToUnsignedInt(var3[var5 * 4 + 1]) << 8)
            + (bToUnsignedInt(var3[var5 * 4 + 2]) << 16)
            + (bToUnsignedInt(var3[var5 * 4 + 3]) << 24);
      }
   }

   private static int getUnsignedMod(int var0, int var1) {
      return getUnsignedDivMod(var0, var1)[1];
   }

   protected static void generatePolynomialInRQFromSeed(short[] var0, byte[] var1, int var2, int var3) {
      int[] var4 = new int[var2];
      expand(var4, var1);

      for (int var5 = 0; var5 < var2; var5++) {
         var0[var5] = (short)(getUnsignedMod(var4[var5], var3) - (var3 - 1) / 2);
      }
   }

   protected static void roundPolynomial(short[] var0, short[] var1) {
      for (int var2 = 0; var2 < var0.length; var2++) {
         var0[var2] = (short)(var1[var2] - getModFreeze(var1[var2], 3));
      }
   }

   protected static void getRoundedEncodedPolynomial(byte[] var0, short[] var1, int var2, int var3) {
      short[] var4 = new short[var2];
      short[] var5 = new short[var2];

      for (int var6 = 0; var6 < var2; var6++) {
         var4[var6] = (short)((var1[var6] + (var3 - 1) / 2) * 10923 >>> 15);
         var5[var6] = (short)((var3 + 2) / 3);
      }

      encode(var0, var4, var5, var2, 0);
   }

   protected static byte[] getHashWithPrefix(byte[] var0, byte[] var1) {
      byte[] var2 = new byte[64];
      byte[] var3 = new byte[var0.length + var1.length];
      System.arraycopy(var0, 0, var3, 0, var0.length);
      System.arraycopy(var1, 0, var3, var0.length, var1.length);
      SHA512Digest var4 = new SHA512Digest();
      var4.update(var3, 0, var3.length);
      var4.doFinal(var2, 0);
      return var2;
   }

   private static void decode(short[] var0, byte[] var1, short[] var2, int var3, int var4, int var5) {
      if (var3 == 1) {
         if (var2[0] == 1) {
            var0[var4] = 0;
         } else if (var2[0] <= 256) {
            var0[var4] = (short)getUnsignedMod(bToUnsignedInt(var1[var5]), var2[0]);
         } else {
            var0[var4] = (short)getUnsignedMod(bToUnsignedInt(var1[var5]) + (var1[var5 + 1] << 8), var2[0]);
         }
      }

      if (var3 > 1) {
         short[] var6 = new short[(var3 + 1) / 2];
         short[] var7 = new short[(var3 + 1) / 2];
         short[] var8 = new short[var3 / 2];
         int[] var9 = new int[var3 / 2];

         byte var10;
         for (var10 = 0; var10 < var3 - 1; var10 += 2) {
            int var11 = var2[var10] * var2[var10 + 1];
            if (var11 > 4194048) {
               var9[var10 / 2] = 65536;
               var8[var10 / 2] = (short)(bToUnsignedInt(var1[var5]) + 256 * bToUnsignedInt(var1[var5 + 1]));
               var5 += 2;
               var7[var10 / 2] = (short)((var11 + 255 >>> 8) + 255 >>> 8);
            } else if (var11 >= 16384) {
               var9[var10 / 2] = 256;
               var8[var10 / 2] = (short)bToUnsignedInt(var1[var5]);
               var5++;
               var7[var10 / 2] = (short)(var11 + 255 >>> 8);
            } else {
               var9[var10 / 2] = 1;
               var8[var10 / 2] = 0;
               var7[var10 / 2] = (short)var11;
            }
         }

         if (var10 < var3) {
            var7[var10 / 2] = var2[var10];
         }

         decode(var6, var1, var7, (var3 + 1) / 2, var4, var5);

         for (var10 = 0; var10 < var3 - 1; var10 += 2) {
            int var15 = sToUnsignedInt(var8[var10 / 2]);
            var15 += var9[var10 / 2] * sToUnsignedInt(var6[var10 / 2]);
            int[] var12 = getUnsignedDivMod(var15, var2[var10]);
            var0[var4++] = (short)var12[1];
            var0[var4++] = (short)getUnsignedMod(var12[0], var2[var10 + 1]);
         }

         if (var10 < var3) {
            var0[var4] = var6[var10 / 2];
         }
      }
   }

   protected static void getDecodedPolynomial(short[] var0, byte[] var1, int var2, int var3) {
      short[] var4 = new short[var2];
      short[] var5 = new short[var2];

      for (int var6 = 0; var6 < var2; var6++) {
         var5[var6] = (short)var3;
      }

      decode(var4, var1, var5, var2, 0, 0);

      for (int var7 = 0; var7 < var2; var7++) {
         var0[var7] = (short)(var4[var7] - (var3 - 1) / 2);
      }
   }

   protected static void getRandomInputs(SecureRandom var0, byte[] var1) {
      byte[] var2 = new byte[var1.length / 8];
      var0.nextBytes(var2);

      for (int var3 = 0; var3 < var1.length; var3++) {
         var1[var3] = (byte)(1 & var2[var3 >>> 3] >>> (var3 & 7));
      }
   }

   protected static void getEncodedInputs(byte[] var0, byte[] var1) {
      for (int var2 = 0; var2 < var1.length; var2++) {
         var0[var2 >>> 3] = (byte)(var0[var2 >>> 3] | var1[var2] << (var2 & 7));
      }
   }

   protected static void getRoundedDecodedPolynomial(short[] var0, byte[] var1, int var2, int var3) {
      short[] var4 = new short[var2];
      short[] var5 = new short[var2];

      for (int var6 = 0; var6 < var2; var6++) {
         var5[var6] = (short)((var3 + 2) / 3);
      }

      decode(var4, var1, var5, var2, 0, 0);

      for (int var7 = 0; var7 < var2; var7++) {
         var0[var7] = (short)(var4[var7] * 3 - (var3 - 1) / 2);
      }
   }

   protected static void top(byte[] var0, short[] var1, byte[] var2, int var3, int var4, int var5) {
      for (int var6 = 0; var6 < var0.length; var6++) {
         var0[var6] = (byte)(var5 * (getModFreeze(var1[var6] + var2[var6] * ((var3 - 1) / 2), var3) + var4) + 16384 >>> 15);
      }
   }

   protected static void getTopEncodedPolynomial(byte[] var0, byte[] var1) {
      for (int var2 = 0; var2 < var0.length; var2++) {
         var0[var2] = (byte)(var1[2 * var2] + (var1[2 * var2 + 1] << 4));
      }
   }

   protected static void getDecodedSmallPolynomial(byte[] var0, byte[] var1, int var2) {
      int var4 = 0;
      int var5 = 0;

      for (int var6 = 0; var6 < var2 / 4; var6++) {
         byte var3 = var1[var5++];
         var0[var4++] = (byte)((bToUnsignedInt(var3) & 3) - 1);
         var3 = (byte)(var3 >>> 2);
         var0[var4++] = (byte)((bToUnsignedInt(var3) & 3) - 1);
         var3 = (byte)(var3 >>> 2);
         var0[var4++] = (byte)((bToUnsignedInt(var3) & 3) - 1);
         var3 = (byte)(var3 >>> 2);
         var0[var4++] = (byte)((bToUnsignedInt(var3) & 3) - 1);
      }

      byte var10 = var1[var5];
      var0[var4] = (byte)((bToUnsignedInt(var10) & 3) - 1);
   }

   protected static void scalarMultiplicationInRQ(short[] var0, short[] var1, int var2, int var3) {
      for (int var4 = 0; var4 < var1.length; var4++) {
         var0[var4] = (short)getModFreeze(var2 * var1[var4], var3);
      }
   }

   protected static void transformRQToR3(byte[] var0, short[] var1) {
      for (int var2 = 0; var2 < var1.length; var2++) {
         var0[var2] = (byte)getModFreeze(var1[var2], 3);
      }
   }

   protected static void multiplicationInR3(byte[] var0, byte[] var1, byte[] var2, int var3) {
      byte[] var4 = new byte[var3 + var3 - 1];

      for (int var6 = 0; var6 < var3; var6++) {
         byte var5 = 0;

         for (int var7 = 0; var7 <= var6; var7++) {
            var5 = (byte)getModFreeze(var5 + var1[var7] * var2[var6 - var7], 3);
         }

         var4[var6] = var5;
      }

      for (int var9 = var3; var9 < var3 + var3 - 1; var9++) {
         byte var8 = 0;

         for (int var12 = var9 - var3 + 1; var12 < var3; var12++) {
            var8 = (byte)getModFreeze(var8 + var1[var12] * var2[var9 - var12], 3);
         }

         var4[var9] = var8;
      }

      for (int var10 = var3 + var3 - 2; var10 >= var3; var10--) {
         var4[var10 - var3] = (byte)getModFreeze(var4[var10 - var3] + var4[var10], 3);
         var4[var10 - var3 + 1] = (byte)getModFreeze(var4[var10 - var3 + 1] + var4[var10], 3);
      }

      for (int var11 = 0; var11 < var3; var11++) {
         var0[var11] = var4[var11];
      }
   }

   protected static void checkForSmallPolynomial(byte[] var0, byte[] var1, int var2, int var3) {
      int var4 = 0;

      for (int var5 = 0; var5 != var1.length; var5++) {
         var4 += var1[var5] & 1;
      }

      int var7 = checkNotEqualToZero(var4 - var3);

      for (int var6 = 0; var6 < var3; var6++) {
         var0[var6] = (byte)((var1[var6] ^ 1) & ~var7 ^ 1);
      }

      for (int var8 = var3; var8 < var2; var8++) {
         var0[var8] = (byte)(var1[var8] & ~var7);
      }
   }

   protected static void updateDiffMask(byte[] var0, byte[] var1, int var2) {
      for (int var3 = 0; var3 < var0.length; var3++) {
         var0[var3] = (byte)(var0[var3] ^ var2 & (var0[var3] ^ var1[var3]));
      }
   }

   protected static void getTopDecodedPolynomial(byte[] var0, byte[] var1) {
      for (int var2 = 0; var2 < var1.length; var2++) {
         var0[2 * var2] = (byte)(var1[var2] & 15);
         var0[2 * var2 + 1] = (byte)(var1[var2] >>> 4);
      }
   }

   protected static void right(byte[] var0, short[] var1, byte[] var2, int var3, int var4, int var5, int var6) {
      for (int var7 = 0; var7 < var0.length; var7++) {
         var0[var7] = (byte)(-checkLessThanZero(getModFreeze(getModFreeze(var6 * var2[var7] - var5, var3) - var1[var7] + 4 * var4 + 1, var3)));
      }
   }

   private static int[] getUnsignedDivMod(int var0, int var1) {
      long var2 = iToUnsignedLong(var0);
      long var4 = iToUnsignedLong(Integer.MIN_VALUE);
      var4 /= var1;
      long var6 = 0L;
      long var8 = var2 * var4 >>> 31;
      var2 -= var8 * var1;
      var6 += var8;
      var8 = var2 * var4 >>> 31;
      var2 -= var8 * var1;
      var6 += var8;
      var2 -= var1;
      var6++;
      long var10 = -(var2 >>> 63);
      var2 += var10 & var1;
      var6 += var10;
      return new int[]{toIntExact(var6), toIntExact(var2)};
   }

   private static int[] getSignedDivMod(int var0, int var1) {
      int[] var5 = getUnsignedDivMod(toIntExact(-2147483648L + iToUnsignedLong(var0)), var1);
      int[] var6 = getUnsignedDivMod(Integer.MIN_VALUE, var1);
      int var2 = toIntExact(iToUnsignedLong(var5[0]) - iToUnsignedLong(var6[0]));
      int var3 = toIntExact(iToUnsignedLong(var5[1]) - iToUnsignedLong(var6[1]));
      int var4 = -(var3 >>> 31);
      var3 += var4 & var1;
      var2 += var4;
      return new int[]{var2, var3};
   }

   private static int checkLessThanZero(int var0) {
      return -(var0 >>> 31);
   }

   private static int checkNotEqualToZero(int var0) {
      long var1 = iToUnsignedLong(var0);
      var1 = -var1;
      return -((int)(var1 >>> 63));
   }

   static int bToUnsignedInt(byte var0) {
      return var0 & 0xFF;
   }

   static int sToUnsignedInt(short var0) {
      return var0 & 65535;
   }

   static long iToUnsignedLong(int var0) {
      return var0 & 4294967295L;
   }

   static int toIntExact(long var0) {
      int var2 = (int)var0;
      if (var2 != var0) {
         throw new IllegalStateException("value out of integer range");
      } else {
         return var2;
      }
   }
}
