package org.bouncycastle.pqc.crypto.newhope;

import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Pack;

class Poly {
   static void add(short[] var0, short[] var1, short[] var2) {
      for (int var3 = 0; var3 < 1024; var3++) {
         var2[var3] = Reduce.barrett((short)(var0[var3] + var1[var3]));
      }
   }

   static void fromBytes(short[] var0, byte[] var1) {
      for (int var2 = 0; var2 < 256; var2++) {
         int var3 = 7 * var2;
         int var4 = var1[var3 + 0] & 255;
         int var5 = var1[var3 + 1] & 255;
         int var6 = var1[var3 + 2] & 255;
         int var7 = var1[var3 + 3] & 255;
         int var8 = var1[var3 + 4] & 255;
         int var9 = var1[var3 + 5] & 255;
         int var10 = var1[var3 + 6] & 255;
         int var11 = 4 * var2;
         var0[var11 + 0] = (short)(var4 | (var5 & 63) << 8);
         var0[var11 + 1] = (short)(var5 >>> 6 | var6 << 2 | (var7 & 15) << 10);
         var0[var11 + 2] = (short)(var7 >>> 4 | var8 << 4 | (var9 & 3) << 12);
         var0[var11 + 3] = (short)(var9 >>> 2 | var10 << 6);
      }
   }

   static void fromNTT(short[] var0) {
      NTT.bitReverse(var0);
      NTT.core(var0, Precomp.OMEGAS_INV_MONTGOMERY);
      NTT.mulCoefficients(var0, Precomp.PSIS_INV_MONTGOMERY);
   }

   static void getNoise(short[] var0, byte[] var1, byte var2) {
      byte[] var3 = new byte[8];
      var3[0] = var2;
      byte[] var4 = new byte[4096];
      ChaCha20.process(var1, var3, var4, 0, var4.length);

      for (int var5 = 0; var5 < 1024; var5++) {
         int var6 = Pack.bigEndianToInt(var4, var5 * 4);
         int var7 = 0;

         for (int var8 = 0; var8 < 8; var8++) {
            var7 += var6 >> var8 & 16843009;
         }

         int var10 = (var7 >>> 24) + (var7 >>> 0) & 0xFF;
         int var9 = (var7 >>> 16) + (var7 >>> 8) & 0xFF;
         var0[var5] = (short)(var10 + 12289 - var9);
      }
   }

   static void pointWise(short[] var0, short[] var1, short[] var2) {
      for (int var3 = 0; var3 < 1024; var3++) {
         int var4 = var0[var3] & '\uffff';
         int var5 = var1[var3] & '\uffff';
         short var6 = Reduce.montgomery(3186 * var5);
         var2[var3] = Reduce.montgomery(var4 * (var6 & '\uffff'));
      }
   }

   static void toBytes(byte[] var0, short[] var1) {
      for (int var2 = 0; var2 < 256; var2++) {
         int var3 = 4 * var2;
         short var4 = normalize(var1[var3 + 0]);
         short var5 = normalize(var1[var3 + 1]);
         short var6 = normalize(var1[var3 + 2]);
         short var7 = normalize(var1[var3 + 3]);
         int var8 = 7 * var2;
         var0[var8 + 0] = (byte)var4;
         var0[var8 + 1] = (byte)(var4 >> 8 | var5 << 6);
         var0[var8 + 2] = (byte)(var5 >> 2);
         var0[var8 + 3] = (byte)(var5 >> 10 | var6 << 4);
         var0[var8 + 4] = (byte)(var6 >> 4);
         var0[var8 + 5] = (byte)(var6 >> 12 | var7 << 2);
         var0[var8 + 6] = (byte)(var7 >> 6);
      }
   }

   static void toNTT(short[] var0) {
      NTT.mulCoefficients(var0, Precomp.PSIS_BITREV_MONTGOMERY);
      NTT.core(var0, Precomp.OMEGAS_MONTGOMERY);
   }

   static void uniform(short[] var0, byte[] var1) {
      SHAKEDigest var2 = new SHAKEDigest(128);
      var2.update(var1, 0, var1.length);
      int var3 = 0;

      while (true) {
         byte[] var4 = new byte[256];
         var2.doOutput(var4, 0, var4.length);

         for (byte var5 = 0; var5 < var4.length; var5 += 2) {
            int var6 = var4[var5] & 255 | (var4[var5 + 1] & 255) << 8;
            if (var6 < 61445) {
               var0[var3++] = (short)var6;
               if (var3 == 1024) {
                  return;
               }
            }
         }
      }
   }

   private static short normalize(short var0) {
      int var1 = Reduce.barrett(var0);
      int var2 = var1 - 12289;
      int var3 = var2 >> 31;
      var1 = var2 ^ (var1 ^ var2) & var3;
      return (short)var1;
   }
}
