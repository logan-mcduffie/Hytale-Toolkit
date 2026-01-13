package org.bouncycastle.crypto.threshold;

abstract class Polynomial {
   public static Polynomial newInstance(ShamirSecretSplitter.Algorithm var0, ShamirSecretSplitter.Mode var1) {
      return (Polynomial)(var1 == ShamirSecretSplitter.Mode.Native ? new PolynomialNative(var0) : new PolynomialTable(var0));
   }

   protected abstract byte gfMul(int var1, int var2);

   protected abstract byte gfDiv(int var1, int var2);

   protected byte gfPow(int var1, byte var2) {
      byte var3 = 1;

      for (int var4 = 0; var4 < 8; var4++) {
         if ((var2 & 1 << var4) != 0) {
            var3 = this.gfMul(var3 & 255, var1 & 0xFF);
         }

         var1 = this.gfMul(var1 & 0xFF, var1 & 0xFF);
      }

      return (byte)var3;
   }

   public byte[] gfVecMul(byte[] var1, byte[][] var2) {
      byte[] var3 = new byte[var2[0].length];

      for (int var5 = 0; var5 < var2[0].length; var5++) {
         byte var4 = 0;

         for (int var6 = 0; var6 < var1.length; var6++) {
            var4 ^= this.gfMul(var1[var6] & 255, var2[var6][var5] & 255);
         }

         var3[var5] = (byte)var4;
      }

      return var3;
   }
}
