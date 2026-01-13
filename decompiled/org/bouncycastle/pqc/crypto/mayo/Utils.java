package org.bouncycastle.pqc.crypto.mayo;

import org.bouncycastle.crypto.MultiBlockCipher;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CTRModeCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

class Utils {
   public static void unpackMVecs(byte[] var0, int var1, long[] var2, int var3, int var4, int var5) {
      int var6 = var5 + 15 >> 4;
      int var7 = var5 >> 1;
      int var8 = 8 - (var6 << 3) + var7;
      int var9 = var4 - 1;
      var3 += var9 * var6;

      for (int var11 = var1 + var9 * var7; var9 >= 0; var11 -= var7) {
         int var10;
         for (var10 = 0; var10 < var6 - 1; var10++) {
            var2[var3 + var10] = Pack.littleEndianToLong(var0, var11 + (var10 << 3));
         }

         var2[var3 + var10] = Pack.littleEndianToLong(var0, var11 + (var10 << 3), var8);
         var9--;
         var3 -= var6;
      }
   }

   public static void packMVecs(long[] var0, byte[] var1, int var2, int var3, int var4) {
      int var5 = var4 + 15 >> 4;
      int var6 = var4 >> 1;
      int var7 = 8 - (var5 << 3) + var6;
      int var9 = 0;

      for (int var10 = 0; var9 < var3; var10 += var5) {
         int var8;
         for (var8 = 0; var8 < var5 - 1; var8++) {
            Pack.longToLittleEndian(var0[var10 + var8], var1, var2 + (var8 << 3));
         }

         Pack.longToLittleEndian(var0[var10 + var8], var1, var2 + (var8 << 3), var7);
         var9++;
         var2 += var6;
      }
   }

   public static void expandP1P2(MayoParameters var0, long[] var1, byte[] var2) {
      int var3 = var0.getP1Bytes() + var0.getP2Bytes();
      byte[] var4 = new byte[var3];
      byte[] var5 = new byte[16];
      MultiBlockCipher var6 = AESEngine.newInstance();
      CTRModeCipher var7 = SICBlockCipher.newInstance(var6);
      ParametersWithIV var8 = new ParametersWithIV(new KeyParameter(Arrays.copyOf(var2, var0.getPkSeedBytes())), var5);
      var7.init(true, var8);
      int var9 = var7.getBlockSize();
      byte[] var10 = new byte[var9];
      byte[] var11 = new byte[var9];

      int var12;
      for (var12 = 0; var12 + var9 <= var3; var12 += var9) {
         var7.processBlock(var10, 0, var11, 0);
         System.arraycopy(var11, 0, var4, var12, var9);
      }

      if (var12 < var3) {
         var7.processBlock(var10, 0, var11, 0);
         int var13 = var3 - var12;
         System.arraycopy(var11, 0, var4, var12, var13);
      }

      int var14 = (var0.getP1Limbs() + var0.getP2Limbs()) / var0.getMVecLimbs();
      unpackMVecs(var4, 0, var1, 0, var14, var0.getM());
   }
}
