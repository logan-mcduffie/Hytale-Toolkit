package org.bouncycastle.pqc.crypto.sphincsplus;

class SIG {
   private final byte[] r;
   private final SIG_FORS[] sig_fors;
   private final SIG_XMSS[] sig_ht;

   public SIG(int var1, int var2, int var3, int var4, int var5, int var6, byte[] var7) {
      this.r = new byte[var1];
      System.arraycopy(var7, 0, this.r, 0, var1);
      this.sig_fors = new SIG_FORS[var2];
      int var8 = var1;

      for (int var9 = 0; var9 != var2; var9++) {
         byte[] var10 = new byte[var1];
         System.arraycopy(var7, var8, var10, 0, var1);
         var8 += var1;
         byte[][] var11 = new byte[var3][];

         for (int var12 = 0; var12 != var3; var12++) {
            var11[var12] = new byte[var1];
            System.arraycopy(var7, var8, var11[var12], 0, var1);
            var8 += var1;
         }

         this.sig_fors[var9] = new SIG_FORS(var10, var11);
      }

      this.sig_ht = new SIG_XMSS[var4];

      for (int var13 = 0; var13 != var4; var13++) {
         byte[] var14 = new byte[var6 * var1];
         System.arraycopy(var7, var8, var14, 0, var14.length);
         var8 += var14.length;
         byte[][] var15 = new byte[var5][];

         for (int var16 = 0; var16 != var5; var16++) {
            var15[var16] = new byte[var1];
            System.arraycopy(var7, var8, var15[var16], 0, var1);
            var8 += var1;
         }

         this.sig_ht[var13] = new SIG_XMSS(var14, var15);
      }

      if (var8 != var7.length) {
         throw new IllegalArgumentException("signature wrong length");
      }
   }

   public byte[] getR() {
      return this.r;
   }

   public SIG_FORS[] getSIG_FORS() {
      return this.sig_fors;
   }

   public SIG_XMSS[] getSIG_HT() {
      return this.sig_ht;
   }
}
