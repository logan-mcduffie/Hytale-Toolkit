package org.bouncycastle.crypto.fpe;

import java.math.BigInteger;
import org.bouncycastle.crypto.BlockCipher;
import org.bouncycastle.crypto.util.RadixConverter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.BigIntegers;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

class SP80038G {
   static final String FPE_DISABLED = "org.bouncycastle.fpe.disable";
   static final String FF1_DISABLED = "org.bouncycastle.fpe.disable_ff1";
   protected static final int BLOCK_SIZE = 16;
   protected static final double LOG2 = Math.log(2.0);
   protected static final double TWO_TO_96 = Math.pow(2.0, 96.0);

   static byte[] decryptFF1(BlockCipher var0, RadixConverter var1, byte[] var2, byte[] var3, int var4, int var5) {
      checkArgs(var0, true, var1.getRadix(), var3, var4, var5);
      int var7 = var5 / 2;
      int var8 = var5 - var7;
      short[] var9 = toShort(var3, var4, var7);
      short[] var10 = toShort(var3, var4 + var7, var8);
      short[] var11 = decFF1(var0, var1, var2, var5, var7, var8, var9, var10);
      return toByte(var11);
   }

   static short[] decryptFF1w(BlockCipher var0, RadixConverter var1, byte[] var2, short[] var3, int var4, int var5) {
      checkArgs(var0, true, var1.getRadix(), var3, var4, var5);
      int var7 = var5 / 2;
      int var8 = var5 - var7;
      short[] var9 = new short[var7];
      short[] var10 = new short[var8];
      System.arraycopy(var3, var4, var9, 0, var7);
      System.arraycopy(var3, var4 + var7, var10, 0, var8);
      return decFF1(var0, var1, var2, var5, var7, var8, var9, var10);
   }

   static short[] decFF1(BlockCipher var0, RadixConverter var1, byte[] var2, int var3, int var4, int var5, short[] var6, short[] var7) {
      int var8 = var1.getRadix();
      int var9 = var2.length;
      int var10 = calculateB_FF1(var8, var5);
      int var11 = var10 + 7 & -4;
      byte[] var12 = calculateP_FF1(var8, (byte)var4, var3, var9);
      BigInteger var13 = BigInteger.valueOf(var8);
      BigInteger[] var14 = calculateModUV(var13, var4, var5);
      int var15 = var4;

      for (int var16 = 9; var16 >= 0; var16--) {
         BigInteger var17 = calculateY_FF1(var0, var2, var10, var11, var16, var12, var6, var1);
         var15 = var3 - var15;
         BigInteger var18 = var14[var16 & 1];
         BigInteger var19 = var1.fromEncoding(var7).subtract(var17).mod(var18);
         short[] var20 = var7;
         var7 = var6;
         var6 = var20;
         var1.toEncoding(var19, var15, var20);
      }

      return Arrays.concatenate(var6, var7);
   }

   static byte[] decryptFF3(BlockCipher var0, RadixConverter var1, byte[] var2, byte[] var3, int var4, int var5) {
      checkArgs(var0, false, var1.getRadix(), var3, var4, var5);
      if (var2.length != 8) {
         throw new IllegalArgumentException();
      } else {
         return implDecryptFF3(var0, var1, var2, var3, var4, var5);
      }
   }

   static byte[] decryptFF3_1(BlockCipher var0, RadixConverter var1, byte[] var2, byte[] var3, int var4, int var5) {
      checkArgs(var0, false, var1.getRadix(), var3, var4, var5);
      if (var2.length != 7) {
         throw new IllegalArgumentException("tweak should be 56 bits");
      } else {
         byte[] var6 = calculateTweak64_FF3_1(var2);
         return implDecryptFF3(var0, var1, var6, var3, var4, var5);
      }
   }

   static short[] decryptFF3_1w(BlockCipher var0, RadixConverter var1, byte[] var2, short[] var3, int var4, int var5) {
      checkArgs(var0, false, var1.getRadix(), var3, var4, var5);
      if (var2.length != 7) {
         throw new IllegalArgumentException("tweak should be 56 bits");
      } else {
         byte[] var6 = calculateTweak64_FF3_1(var2);
         return implDecryptFF3w(var0, var1, var6, var3, var4, var5);
      }
   }

   static byte[] encryptFF1(BlockCipher var0, RadixConverter var1, byte[] var2, byte[] var3, int var4, int var5) {
      checkArgs(var0, true, var1.getRadix(), var3, var4, var5);
      int var7 = var5 / 2;
      int var8 = var5 - var7;
      short[] var9 = toShort(var3, var4, var7);
      short[] var10 = toShort(var3, var4 + var7, var8);
      return toByte(encFF1(var0, var1, var2, var5, var7, var8, var9, var10));
   }

   static short[] encryptFF1w(BlockCipher var0, RadixConverter var1, byte[] var2, short[] var3, int var4, int var5) {
      checkArgs(var0, true, var1.getRadix(), var3, var4, var5);
      int var7 = var5 / 2;
      int var8 = var5 - var7;
      short[] var9 = new short[var7];
      short[] var10 = new short[var8];
      System.arraycopy(var3, var4, var9, 0, var7);
      System.arraycopy(var3, var4 + var7, var10, 0, var8);
      return encFF1(var0, var1, var2, var5, var7, var8, var9, var10);
   }

   private static short[] encFF1(BlockCipher var0, RadixConverter var1, byte[] var2, int var3, int var4, int var5, short[] var6, short[] var7) {
      int var8 = var1.getRadix();
      int var9 = var2.length;
      int var10 = calculateB_FF1(var8, var5);
      int var11 = var10 + 7 & -4;
      byte[] var12 = calculateP_FF1(var8, (byte)var4, var3, var9);
      BigInteger var13 = BigInteger.valueOf(var8);
      BigInteger[] var14 = calculateModUV(var13, var4, var5);
      int var15 = var5;

      for (int var16 = 0; var16 < 10; var16++) {
         BigInteger var17 = calculateY_FF1(var0, var2, var10, var11, var16, var12, var7, var1);
         var15 = var3 - var15;
         BigInteger var18 = var14[var16 & 1];
         BigInteger var19 = var1.fromEncoding(var6);
         BigInteger var20 = var19.add(var17).mod(var18);
         short[] var21 = var6;
         var6 = var7;
         var7 = var21;
         var1.toEncoding(var20, var15, var21);
      }

      return Arrays.concatenate(var6, var7);
   }

   static byte[] encryptFF3(BlockCipher var0, RadixConverter var1, byte[] var2, byte[] var3, int var4, int var5) {
      checkArgs(var0, false, var1.getRadix(), var3, var4, var5);
      if (var2.length != 8) {
         throw new IllegalArgumentException();
      } else {
         return implEncryptFF3(var0, var1, var2, var3, var4, var5);
      }
   }

   static short[] encryptFF3w(BlockCipher var0, RadixConverter var1, byte[] var2, short[] var3, int var4, int var5) {
      checkArgs(var0, false, var1.getRadix(), var3, var4, var5);
      if (var2.length != 8) {
         throw new IllegalArgumentException();
      } else {
         return implEncryptFF3w(var0, var1, var2, var3, var4, var5);
      }
   }

   static short[] encryptFF3_1w(BlockCipher var0, RadixConverter var1, byte[] var2, short[] var3, int var4, int var5) {
      checkArgs(var0, false, var1.getRadix(), var3, var4, var5);
      if (var2.length != 7) {
         throw new IllegalArgumentException("tweak should be 56 bits");
      } else {
         byte[] var6 = calculateTweak64_FF3_1(var2);
         return encryptFF3w(var0, var1, var6, var3, var4, var5);
      }
   }

   static byte[] encryptFF3_1(BlockCipher var0, RadixConverter var1, byte[] var2, byte[] var3, int var4, int var5) {
      checkArgs(var0, false, var1.getRadix(), var3, var4, var5);
      if (var2.length != 7) {
         throw new IllegalArgumentException("tweak should be 56 bits");
      } else {
         byte[] var6 = calculateTweak64_FF3_1(var2);
         return encryptFF3(var0, var1, var6, var3, var4, var5);
      }
   }

   protected static int calculateB_FF1(int var0, int var1) {
      int var2 = Integers.numberOfTrailingZeros(var0);
      int var3 = var2 * var1;
      int var4 = var0 >>> var2;
      if (var4 != 1) {
         var3 += BigInteger.valueOf(var4).pow(var1).bitLength();
      }

      return (var3 + 7) / 8;
   }

   protected static BigInteger[] calculateModUV(BigInteger var0, int var1, int var2) {
      BigInteger[] var3 = new BigInteger[]{var0.pow(var1), null};
      var3[1] = var3[0];
      if (var2 != var1) {
         var3[1] = var3[1].multiply(var0);
      }

      return var3;
   }

   protected static byte[] calculateP_FF1(int var0, byte var1, int var2, int var3) {
      byte[] var4 = new byte[16];
      var4[0] = 1;
      var4[1] = 2;
      var4[2] = 1;
      var4[3] = 0;
      var4[4] = (byte)(var0 >> 8);
      var4[5] = (byte)var0;
      var4[6] = 10;
      var4[7] = var1;
      Pack.intToBigEndian(var2, var4, 8);
      Pack.intToBigEndian(var3, var4, 12);
      return var4;
   }

   protected static byte[] calculateTweak64_FF3_1(byte[] var0) {
      return new byte[]{var0[0], var0[1], var0[2], (byte)(var0[3] & 240), var0[4], var0[5], var0[6], (byte)(var0[3] << 4)};
   }

   protected static BigInteger calculateY_FF1(BlockCipher var0, byte[] var1, int var2, int var3, int var4, byte[] var5, short[] var6, RadixConverter var7) {
      int var8 = var1.length;
      BigInteger var9 = var7.fromEncoding(var6);
      byte[] var10 = BigIntegers.asUnsignedByteArray(var9);
      int var11 = -(var8 + var2 + 1) & 15;
      byte[] var12 = new byte[var8 + var11 + 1 + var2];
      System.arraycopy(var1, 0, var12, 0, var8);
      var12[var8 + var11] = (byte)var4;
      System.arraycopy(var10, 0, var12, var12.length - var10.length, var10.length);
      byte[] var13 = prf(var0, Arrays.concatenate(var5, var12));
      byte[] var14 = var13;
      if (var3 > 16) {
         int var15 = (var3 + 16 - 1) / 16;
         var14 = new byte[var15 * 16];
         int var16 = Pack.bigEndianToInt(var13, 12);
         System.arraycopy(var13, 0, var14, 0, 16);

         for (int var17 = 1; var17 < var15; var17++) {
            int var18 = var17 * 16;
            System.arraycopy(var13, 0, var14, var18, 12);
            Pack.intToBigEndian(var16 ^ var17, var14, var18 + 16 - 4);
            var0.processBlock(var14, var18, var14, var18);
         }
      }

      return num(var14, 0, var3);
   }

   protected static BigInteger calculateY_FF3(BlockCipher var0, byte[] var1, int var2, int var3, short[] var4, RadixConverter var5) {
      byte[] var6 = new byte[16];
      Pack.intToBigEndian(Pack.bigEndianToInt(var1, var2) ^ var3, var6, 0);
      BigInteger var7 = var5.fromEncoding(var4);
      BigIntegers.asUnsignedByteArray(var7, var6, 4, 12);
      Arrays.reverseInPlace(var6);
      var0.processBlock(var6, 0, var6, 0);
      Arrays.reverseInPlace(var6);
      return num(var6, 0, var6.length);
   }

   protected static void checkArgs(BlockCipher var0, boolean var1, int var2, short[] var3, int var4, int var5) {
      checkCipher(var0);
      if (var2 >= 2 && var2 <= 65536) {
         checkData(var1, var2, var3, var4, var5);
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected static void checkArgs(BlockCipher var0, boolean var1, int var2, byte[] var3, int var4, int var5) {
      checkCipher(var0);
      if (var2 >= 2 && var2 <= 256) {
         checkData(var1, var2, var3, var4, var5);
      } else {
         throw new IllegalArgumentException();
      }
   }

   protected static void checkCipher(BlockCipher var0) {
      if (16 != var0.getBlockSize()) {
         throw new IllegalArgumentException();
      }
   }

   protected static void checkData(boolean var0, int var1, short[] var2, int var3, int var4) {
      checkLength(var0, var1, var4);

      for (int var5 = 0; var5 < var4; var5++) {
         int var6 = var2[var3 + var5] & '\uffff';
         if (var6 >= var1) {
            throw new IllegalArgumentException("input data outside of radix");
         }
      }
   }

   protected static void checkData(boolean var0, int var1, byte[] var2, int var3, int var4) {
      checkLength(var0, var1, var4);

      for (int var5 = 0; var5 < var4; var5++) {
         int var6 = var2[var3 + var5] & 255;
         if (var6 >= var1) {
            throw new IllegalArgumentException("input data outside of radix");
         }
      }
   }

   private static void checkLength(boolean var0, int var1, int var2) {
      if (var2 >= 2 && !(Math.pow(var1, var2) < 1000000.0)) {
         if (!var0) {
            int var3 = 2 * (int)Math.floor(Math.log(TWO_TO_96) / Math.log(var1));
            if (var2 > var3) {
               throw new IllegalArgumentException("maximum input length is " + var3);
            }
         }
      } else {
         throw new IllegalArgumentException("input too short");
      }
   }

   protected static byte[] implDecryptFF3(BlockCipher var0, RadixConverter var1, byte[] var2, byte[] var3, int var4, int var5) {
      int var8 = var5 / 2;
      int var9 = var5 - var8;
      short[] var10 = toShort(var3, var4, var9);
      short[] var11 = toShort(var3, var4 + var9, var8);
      short[] var12 = decFF3_1(var0, var1, var2, var5, var8, var9, var10, var11);
      return toByte(var12);
   }

   protected static short[] implDecryptFF3w(BlockCipher var0, RadixConverter var1, byte[] var2, short[] var3, int var4, int var5) {
      int var8 = var5 / 2;
      int var9 = var5 - var8;
      short[] var10 = new short[var9];
      short[] var11 = new short[var8];
      System.arraycopy(var3, var4, var10, 0, var9);
      System.arraycopy(var3, var4 + var9, var11, 0, var8);
      return decFF3_1(var0, var1, var2, var5, var8, var9, var10, var11);
   }

   private static short[] decFF3_1(BlockCipher var0, RadixConverter var1, byte[] var2, int var3, int var4, int var5, short[] var6, short[] var7) {
      BigInteger var8 = BigInteger.valueOf(var1.getRadix());
      BigInteger[] var9 = calculateModUV(var8, var4, var5);
      int var10 = var5;
      Arrays.reverseInPlace(var6);
      Arrays.reverseInPlace(var7);

      for (int var11 = 7; var11 >= 0; var11--) {
         var10 = var3 - var10;
         BigInteger var12 = var9[1 - (var11 & 1)];
         int var13 = 4 - (var11 & 1) * 4;
         BigInteger var14 = calculateY_FF3(var0, var2, var13, var11, var6, var1);
         BigInteger var15 = var1.fromEncoding(var7).subtract(var14).mod(var12);
         short[] var16 = var7;
         var7 = var6;
         var6 = var16;
         var1.toEncoding(var15, var10, var16);
      }

      Arrays.reverseInPlace(var6);
      Arrays.reverseInPlace(var7);
      return Arrays.concatenate(var6, var7);
   }

   protected static byte[] implEncryptFF3(BlockCipher var0, RadixConverter var1, byte[] var2, byte[] var3, int var4, int var5) {
      int var8 = var5 / 2;
      int var9 = var5 - var8;
      short[] var10 = toShort(var3, var4, var9);
      short[] var11 = toShort(var3, var4 + var9, var8);
      short[] var12 = encFF3_1(var0, var1, var2, var5, var8, var9, var10, var11);
      return toByte(var12);
   }

   protected static short[] implEncryptFF3w(BlockCipher var0, RadixConverter var1, byte[] var2, short[] var3, int var4, int var5) {
      int var8 = var5 / 2;
      int var9 = var5 - var8;
      short[] var10 = new short[var9];
      short[] var11 = new short[var8];
      System.arraycopy(var3, var4, var10, 0, var9);
      System.arraycopy(var3, var4 + var9, var11, 0, var8);
      return encFF3_1(var0, var1, var2, var5, var8, var9, var10, var11);
   }

   private static short[] encFF3_1(BlockCipher var0, RadixConverter var1, byte[] var2, int var3, int var4, int var5, short[] var6, short[] var7) {
      BigInteger var8 = BigInteger.valueOf(var1.getRadix());
      BigInteger[] var9 = calculateModUV(var8, var4, var5);
      int var10 = var4;
      Arrays.reverseInPlace(var6);
      Arrays.reverseInPlace(var7);

      for (int var11 = 0; var11 < 8; var11++) {
         var10 = var3 - var10;
         BigInteger var12 = var9[1 - (var11 & 1)];
         int var13 = 4 - (var11 & 1) * 4;
         BigInteger var14 = calculateY_FF3(var0, var2, var13, var11, var7, var1);
         BigInteger var15 = var1.fromEncoding(var6).add(var14).mod(var12);
         short[] var16 = var6;
         var6 = var7;
         var7 = var16;
         var1.toEncoding(var15, var10, var16);
      }

      Arrays.reverseInPlace(var6);
      Arrays.reverseInPlace(var7);
      return Arrays.concatenate(var6, var7);
   }

   protected static BigInteger num(byte[] var0, int var1, int var2) {
      return new BigInteger(1, Arrays.copyOfRange(var0, var1, var1 + var2));
   }

   protected static byte[] prf(BlockCipher var0, byte[] var1) {
      if (var1.length % 16 != 0) {
         throw new IllegalArgumentException();
      } else {
         int var2 = var1.length / 16;
         byte[] var3 = new byte[16];

         for (int var4 = 0; var4 < var2; var4++) {
            Bytes.xorTo(16, var1, var4 * 16, var3, 0);
            var0.processBlock(var3, 0, var3, 0);
         }

         return var3;
      }
   }

   private static byte[] toByte(short[] var0) {
      byte[] var1 = new byte[var0.length];

      for (int var2 = 0; var2 != var1.length; var2++) {
         var1[var2] = (byte)var0[var2];
      }

      return var1;
   }

   private static short[] toShort(byte[] var0, int var1, int var2) {
      short[] var3 = new short[var2];

      for (int var4 = 0; var4 != var3.length; var4++) {
         var3[var4] = (short)(var0[var1 + var4] & 255);
      }

      return var3;
   }
}
