package org.bouncycastle.pqc.crypto.mlkem;

class Reduce {
   public static short montgomeryReduce(int var0) {
      short var2 = (short)(var0 * 62209);
      int var1 = var2 * 3329;
      var1 = var0 - var1;
      var1 >>= 16;
      return (short)var1;
   }

   public static short barretReduce(short var0) {
      long var2 = 67108864L;
      short var4 = (short)((var2 + 1664L) / 3329L);
      short var1 = (short)(var4 * var0 >> 26);
      var1 = (short)(var1 * 3329);
      return (short)(var0 - var1);
   }

   public static short conditionalSubQ(short var0) {
      var0 = (short)(var0 - 3329);
      return (short)(var0 + (var0 >> 15 & 3329));
   }

   static int checkModulus(short var0) {
      return var0 - 3329;
   }
}
