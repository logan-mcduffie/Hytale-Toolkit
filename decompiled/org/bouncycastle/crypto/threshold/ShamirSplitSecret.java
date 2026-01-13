package org.bouncycastle.crypto.threshold;

import java.io.IOException;

public class ShamirSplitSecret implements SplitSecret {
   private final ShamirSplitSecretShare[] secretShares;
   private final Polynomial poly;

   public ShamirSplitSecret(ShamirSecretSplitter.Algorithm var1, ShamirSecretSplitter.Mode var2, ShamirSplitSecretShare[] var3) {
      this.secretShares = var3;
      this.poly = Polynomial.newInstance(var1, var2);
   }

   ShamirSplitSecret(Polynomial var1, ShamirSplitSecretShare[] var2) {
      this.secretShares = var2;
      this.poly = var1;
   }

   public ShamirSplitSecretShare[] getSecretShares() {
      return this.secretShares;
   }

   public ShamirSplitSecret multiple(int var1) throws IOException {
      for (int var3 = 0; var3 < this.secretShares.length; var3++) {
         byte[] var2 = this.secretShares[var3].getEncoded();

         for (int var4 = 0; var4 < var2.length; var4++) {
            var2[var4] = this.poly.gfMul(var2[var4] & 255, var1);
         }

         this.secretShares[var3] = new ShamirSplitSecretShare(var2, var3 + 1);
      }

      return this;
   }

   public ShamirSplitSecret divide(int var1) throws IOException {
      for (int var3 = 0; var3 < this.secretShares.length; var3++) {
         byte[] var2 = this.secretShares[var3].getEncoded();

         for (int var4 = 0; var4 < var2.length; var4++) {
            var2[var4] = this.poly.gfDiv(var2[var4] & 255, var1);
         }

         this.secretShares[var3] = new ShamirSplitSecretShare(var2, var3 + 1);
      }

      return this;
   }

   @Override
   public byte[] getSecret() throws IOException {
      int var1 = this.secretShares.length;
      byte[] var2 = new byte[var1];
      byte[] var4 = new byte[var1 - 1];
      byte[][] var5 = new byte[var1][this.secretShares[0].getEncoded().length];

      for (int var6 = 0; var6 < var1; var6++) {
         var5[var6] = this.secretShares[var6].getEncoded();
         byte var3 = 0;

         for (int var7 = 0; var7 < var1; var7++) {
            if (var7 != var6) {
               var4[var3++] = this.poly.gfDiv(this.secretShares[var7].r, this.secretShares[var6].r ^ this.secretShares[var7].r);
            }
         }

         var3 = 1;

         for (int var9 = 0; var9 != var4.length; var9++) {
            var3 = this.poly.gfMul(var3 & 255, var4[var9] & 255);
         }

         var2[var6] = var3;
      }

      return this.poly.gfVecMul(var2, var5);
   }
}
