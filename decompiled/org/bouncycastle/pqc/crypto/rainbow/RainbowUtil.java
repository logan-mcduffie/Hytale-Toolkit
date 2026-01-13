package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.util.Arrays;

class RainbowUtil {
   public static short[] convertArray(byte[] var0) {
      short[] var1 = new short[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = (short)(var0[var2] & 255);
      }

      return var1;
   }

   public static byte[] convertArray(short[] var0) {
      byte[] var1 = new byte[var0.length];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = (byte)var0[var2];
      }

      return var1;
   }

   public static boolean equals(short[] var0, short[] var1) {
      if (var0.length != var1.length) {
         return false;
      } else {
         boolean var2 = true;

         for (int var3 = var0.length - 1; var3 >= 0; var3--) {
            var2 &= var0[var3] == var1[var3];
         }

         return var2;
      }
   }

   public static boolean equals(short[][] var0, short[][] var1) {
      if (var0.length != var1.length) {
         return false;
      } else {
         boolean var2 = true;

         for (int var3 = var0.length - 1; var3 >= 0; var3--) {
            var2 &= equals(var0[var3], var1[var3]);
         }

         return var2;
      }
   }

   public static boolean equals(short[][][] var0, short[][][] var1) {
      if (var0.length != var1.length) {
         return false;
      } else {
         boolean var2 = true;

         for (int var3 = var0.length - 1; var3 >= 0; var3--) {
            var2 &= equals(var0[var3], var1[var3]);
         }

         return var2;
      }
   }

   public static short[][] cloneArray(short[][] var0) {
      short[][] var1 = new short[var0.length][];

      for (int var2 = 0; var2 < var0.length; var2++) {
         var1[var2] = Arrays.clone(var0[var2]);
      }

      return var1;
   }

   public static short[][][] cloneArray(short[][][] var0) {
      short[][][] var1 = new short[var0.length][var0[0].length][];

      for (int var2 = 0; var2 < var0.length; var2++) {
         for (int var3 = 0; var3 < var0[0].length; var3++) {
            var1[var2][var3] = Arrays.clone(var0[var2][var3]);
         }
      }

      return var1;
   }

   public static byte[] hash(Digest var0, byte[] var1, byte[] var2, byte[] var3) {
      int var4 = var0.getDigestSize();
      var0.update(var1, 0, var1.length);
      var0.update(var2, 0, var2.length);
      if (var3.length == var4) {
         var0.doFinal(var3, 0);
         return var3;
      } else {
         byte[] var6 = new byte[var4];
         var0.doFinal(var6, 0);
         if (var3.length < var4) {
            System.arraycopy(var6, 0, var3, 0, var3.length);
            return var3;
         } else {
            System.arraycopy(var6, 0, var3, 0, var6.length);
            int var7 = var3.length - var4;

            int var8;
            for (var8 = var4; var7 >= var6.length; var8 += var6.length) {
               var0.update(var6, 0, var6.length);
               var0.doFinal(var6, 0);
               System.arraycopy(var6, 0, var3, var8, var6.length);
               var7 -= var6.length;
            }

            if (var7 > 0) {
               var0.update(var6, 0, var6.length);
               var0.doFinal(var6, 0);
               System.arraycopy(var6, 0, var3, var8, var7);
            }

            return var3;
         }
      }
   }

   public static byte[] hash(Digest var0, byte[] var1, int var2) {
      int var3 = var0.getDigestSize();
      var0.update(var1, 0, var1.length);
      byte[] var5 = new byte[var3];
      var0.doFinal(var5, 0);
      if (var2 == var3) {
         return var5;
      } else if (var2 < var3) {
         return Arrays.copyOf(var5, var2);
      } else {
         byte[] var4 = Arrays.copyOf(var5, var3);

         int var6;
         for (var6 = var2 - var3; var6 >= var3; var6 -= var3) {
            var0.update(var5, 0, var3);
            var5 = new byte[var3];
            var0.doFinal(var5, 0);
            var4 = Arrays.concatenate(var4, var5);
         }

         if (var6 > 0) {
            var0.update(var5, 0, var3);
            var5 = new byte[var3];
            var0.doFinal(var5, 0);
            int var7 = var4.length;
            var4 = Arrays.copyOf(var4, var7 + var6);
            System.arraycopy(var5, 0, var4, var7, var6);
         }

         return var4;
      }
   }

   public static short[][] generate_random_2d(SecureRandom var0, int var1, int var2) {
      byte[] var3 = new byte[var1 * var2];
      var0.nextBytes(var3);
      short[][] var4 = new short[var1][var2];

      for (int var5 = 0; var5 < var2; var5++) {
         for (int var6 = 0; var6 < var1; var6++) {
            var4[var6][var5] = (short)(var3[var5 * var1 + var6] & 255);
         }
      }

      return var4;
   }

   public static short[][][] generate_random(SecureRandom var0, int var1, int var2, int var3, boolean var4) {
      int var5;
      if (var4) {
         var5 = var1 * (var2 * (var2 + 1) / 2);
      } else {
         var5 = var1 * var2 * var3;
      }

      byte[] var6 = new byte[var5];
      var0.nextBytes(var6);
      int var7 = 0;
      short[][][] var8 = new short[var1][var2][var3];

      for (int var9 = 0; var9 < var2; var9++) {
         for (int var10 = 0; var10 < var3; var10++) {
            for (int var11 = 0; var11 < var1; var11++) {
               if (!var4 || var9 <= var10) {
                  var8[var11][var9][var10] = (short)(var6[var7++] & 255);
               }
            }
         }
      }

      return var8;
   }

   public static byte[] getEncoded(short[][] var0) {
      int var1 = var0.length;
      int var2 = var0[0].length;
      byte[] var3 = new byte[var1 * var2];

      for (int var4 = 0; var4 < var2; var4++) {
         for (int var5 = 0; var5 < var1; var5++) {
            var3[var4 * var1 + var5] = (byte)var0[var5][var4];
         }
      }

      return var3;
   }

   public static byte[] getEncoded(short[][][] var0, boolean var1) {
      int var2 = var0.length;
      int var3 = var0[0].length;
      int var4 = var0[0][0].length;
      int var5;
      if (var1) {
         var5 = var2 * (var3 * (var3 + 1) / 2);
      } else {
         var5 = var2 * var3 * var4;
      }

      byte[] var6 = new byte[var5];
      int var7 = 0;

      for (int var8 = 0; var8 < var3; var8++) {
         for (int var9 = 0; var9 < var4; var9++) {
            for (int var10 = 0; var10 < var2; var10++) {
               if (!var1 || var8 <= var9) {
                  var6[var7] = (byte)var0[var10][var8][var9];
                  var7++;
               }
            }
         }
      }

      return var6;
   }

   public static int loadEncoded(short[][] var0, byte[] var1, int var2) {
      int var3 = var0.length;
      int var4 = var0[0].length;

      for (int var5 = 0; var5 < var4; var5++) {
         for (int var6 = 0; var6 < var3; var6++) {
            var0[var6][var5] = (short)(var1[var2 + var5 * var3 + var6] & 255);
         }
      }

      return var3 * var4;
   }

   public static int loadEncoded(short[][][] var0, byte[] var1, int var2, boolean var3) {
      int var4 = var0.length;
      int var5 = var0[0].length;
      int var6 = var0[0][0].length;
      int var7 = 0;

      for (int var8 = 0; var8 < var5; var8++) {
         for (int var9 = 0; var9 < var6; var9++) {
            for (int var10 = 0; var10 < var4; var10++) {
               if (!var3 || var8 <= var9) {
                  var0[var10][var8][var9] = (short)(var1[var2 + var7++] & 255);
               }
            }
         }
      }

      return var7;
   }
}
