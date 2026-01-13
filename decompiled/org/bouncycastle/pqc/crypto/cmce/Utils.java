package org.bouncycastle.pqc.crypto.cmce;

import org.bouncycastle.util.Pack;

class Utils {
   static void store_gf(byte[] var0, int var1, short var2) {
      var0[var1 + 0] = (byte)(var2 & 255);
      var0[var1 + 1] = (byte)(var2 >> 8);
   }

   static short load_gf(byte[] var0, int var1, int var2) {
      return (short)(Pack.littleEndianToShort(var0, var1) & var2);
   }

   static int load4(byte[] var0, int var1) {
      return Pack.littleEndianToInt(var0, var1);
   }

   static void store8(byte[] var0, int var1, long var2) {
      var0[var1 + 0] = (byte)(var2 >> 0 & 255L);
      var0[var1 + 1] = (byte)(var2 >> 8 & 255L);
      var0[var1 + 2] = (byte)(var2 >> 16 & 255L);
      var0[var1 + 3] = (byte)(var2 >> 24 & 255L);
      var0[var1 + 4] = (byte)(var2 >> 32 & 255L);
      var0[var1 + 5] = (byte)(var2 >> 40 & 255L);
      var0[var1 + 6] = (byte)(var2 >> 48 & 255L);
      var0[var1 + 7] = (byte)(var2 >> 56 & 255L);
   }

   static long load8(byte[] var0, int var1) {
      return Pack.littleEndianToLong(var0, var1);
   }

   static short bitrev(short var0, int var1) {
      var0 = (short)((var0 & 255) << 8 | (var0 & '\uff00') >> 8);
      var0 = (short)((var0 & 3855) << 4 | (var0 & '\uf0f0') >> 4);
      var0 = (short)((var0 & 13107) << 2 | (var0 & '쳌') >> 2);
      var0 = (short)((var0 & 21845) << 1 | (var0 & 'ꪪ') >> 1);
      return var1 == 12 ? (short)(var0 >> 4) : (short)(var0 >> 3);
   }
}
