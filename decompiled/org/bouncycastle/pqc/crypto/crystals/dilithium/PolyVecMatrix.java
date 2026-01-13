package org.bouncycastle.pqc.crypto.crystals.dilithium;

class PolyVecMatrix {
   private final int dilithiumK;
   private final int dilithiumL;
   private final PolyVecL[] mat;

   public PolyVecMatrix(DilithiumEngine var1) {
      this.dilithiumK = var1.getDilithiumK();
      this.dilithiumL = var1.getDilithiumL();
      this.mat = new PolyVecL[this.dilithiumK];

      for (int var2 = 0; var2 < this.dilithiumK; var2++) {
         this.mat[var2] = new PolyVecL(var1);
      }
   }

   public void pointwiseMontgomery(PolyVecK var1, PolyVecL var2) {
      for (int var3 = 0; var3 < this.dilithiumK; var3++) {
         var1.getVectorIndex(var3).pointwiseAccountMontgomery(this.mat[var3], var2);
      }
   }

   public void expandMatrix(byte[] var1) {
      for (int var2 = 0; var2 < this.dilithiumK; var2++) {
         for (int var3 = 0; var3 < this.dilithiumL; var3++) {
            this.mat[var2].getVectorIndex(var3).uniformBlocks(var1, (short)((var2 << 8) + var3));
         }
      }
   }

   private String addString() {
      String var1 = "[";

      for (int var2 = 0; var2 < this.dilithiumK; var2++) {
         var1 = var1 + "Outer Matrix " + var2 + " [";
         var1 = var1 + this.mat[var2].toString();
         if (var2 == this.dilithiumK - 1) {
            var1 = var1 + "]\n";
         } else {
            var1 = var1 + "],\n";
         }
      }

      return var1 + "]\n";
   }

   public String toString(String var1) {
      return var1.concat(": \n" + this.addString());
   }
}
