package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.digests.SHA256Digest;
import org.bouncycastle.crypto.engines.Salsa20Engine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

public class SCrypt {
   private SCrypt() {
   }

   public static byte[] generate(byte[] var0, byte[] var1, int var2, int var3, int var4, int var5) {
      if (var0 == null) {
         throw new IllegalArgumentException("Passphrase P must be provided.");
      } else if (var1 == null) {
         throw new IllegalArgumentException("Salt S must be provided.");
      } else if (var2 <= 1 || !isPowerOf2(var2)) {
         throw new IllegalArgumentException("Cost parameter N must be > 1 and a power of 2");
      } else if (var3 == 1 && var2 >= 65536) {
         throw new IllegalArgumentException("Cost parameter N must be > 1 and < 65536.");
      } else if (var3 < 1) {
         throw new IllegalArgumentException("Block size r must be >= 1.");
      } else {
         int var6 = Integer.MAX_VALUE / (128 * var3 * 8);
         if (var4 < 1 || var4 > var6) {
            throw new IllegalArgumentException("Parallelisation parameter p must be >= 1 and <= " + var6 + " (based on block size r of " + var3 + ")");
         } else if (var5 < 1) {
            throw new IllegalArgumentException("Generated key length dkLen must be >= 1.");
         } else {
            return MFcrypt(var0, var1, var2, var3, var4, var5);
         }
      }
   }

   private static byte[] MFcrypt(byte[] var0, byte[] var1, int var2, int var3, int var4, int var5) {
      int var6 = var3 * 128;
      byte[] var7 = SingleIterationPBKDF2(var0, var1, var4 * var6);
      int[] var8 = null;

      byte[] var17;
      try {
         int var9 = var7.length >>> 2;
         var8 = new int[var9];
         Pack.littleEndianToInt(var7, 0, var8);
         int var10 = 0;

         for (int var11 = var2 * var3; var2 - var10 > 2 && var11 > 1024; var11 >>>= 1) {
            var10++;
         }

         int var12 = var6 >>> 2;

         for (int var13 = 0; var13 < var9; var13 += var12) {
            SMix(var8, var13, var2, var10, var3);
         }

         Pack.intToLittleEndian(var8, var7, 0);
         var17 = SingleIterationPBKDF2(var0, var7, var5);
      } finally {
         Clear(var7);
         Clear(var8);
      }

      return var17;
   }

   private static byte[] SingleIterationPBKDF2(byte[] var0, byte[] var1, int var2) {
      PKCS5S2ParametersGenerator var3 = new PKCS5S2ParametersGenerator(SHA256Digest.newInstance());
      var3.init(var0, var1, 1);
      KeyParameter var4 = (KeyParameter)var3.generateDerivedMacParameters(var2 * 8);
      return var4.getKey();
   }

   private static void SMix(int[] var0, int var1, int var2, int var3, int var4) {
      int var5 = Integers.numberOfTrailingZeros(var2);
      int var6 = var2 >>> var3;
      int var7 = 1 << var3;
      int var8 = var6 - 1;
      int var9 = var5 - var3;
      int var10 = var4 * 32;
      int[] var11 = new int[16];
      int[] var12 = new int[16];
      int[] var13 = new int[var10];
      int[] var14 = new int[var10];
      int[][] var15 = new int[var7][];

      try {
         System.arraycopy(var0, var1, var14, 0, var10);

         for (int var16 = 0; var16 < var7; var16++) {
            int[] var17 = new int[var6 * var10];
            var15[var16] = var17;
            int var18 = 0;

            for (byte var19 = 0; var19 < var6; var19 += 2) {
               System.arraycopy(var14, 0, var17, var18, var10);
               var18 += var10;
               BlockMix(var14, var11, var12, var13, var4);
               System.arraycopy(var13, 0, var17, var18, var10);
               var18 += var10;
               BlockMix(var13, var11, var12, var14, var4);
            }
         }

         int var24 = var2 - 1;

         for (int var25 = 0; var25 < var2; var25++) {
            int var27 = var14[var10 - 16] & var24;
            int[] var28 = var15[var27 >>> var9];
            int var20 = (var27 & var8) * var10;
            System.arraycopy(var28, var20, var13, 0, var10);
            Xor(var13, var14, 0, var13);
            BlockMix(var13, var11, var12, var14, var4);
         }

         System.arraycopy(var14, 0, var0, var1, var10);
      } finally {
         ClearAll(var15);
         ClearAll(new int[][]{var14, var11, var12, var13});
      }
   }

   private static void BlockMix(int[] var0, int[] var1, int[] var2, int[] var3, int var4) {
      System.arraycopy(var0, var0.length - 16, var1, 0, 16);
      byte var5 = 0;
      int var6 = 0;
      int var7 = var0.length >>> 1;

      for (int var8 = 2 * var4; var8 > 0; var8--) {
         Xor(var1, var0, var5, var2);
         Salsa20Engine.salsaCore(8, var2, var1);
         System.arraycopy(var1, 0, var3, var6, 16);
         var6 = var7 + var5 - var6;
         var5 += 16;
      }
   }

   private static void Xor(int[] var0, int[] var1, int var2, int[] var3) {
      for (int var4 = var3.length - 1; var4 >= 0; var4--) {
         var3[var4] = var0[var4] ^ var1[var2 + var4];
      }
   }

   private static void Clear(byte[] var0) {
      if (var0 != null) {
         Arrays.fill(var0, (byte)0);
      }
   }

   private static void Clear(int[] var0) {
      if (var0 != null) {
         Arrays.fill(var0, 0);
      }
   }

   private static void ClearAll(int[][] var0) {
      for (int var1 = 0; var1 < var0.length; var1++) {
         Clear(var0[var1]);
      }
   }

   private static boolean isPowerOf2(int var0) {
      return (var0 & var0 - 1) == 0;
   }
}
