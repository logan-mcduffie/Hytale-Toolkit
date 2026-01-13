package org.bouncycastle.pqc.crypto.mldsa;

class PolyVecMatrix {
   private final PolyVecL[] matrix;

   PolyVecMatrix(MLDSAEngine var1) {
      int var2 = var1.getDilithiumK();
      this.matrix = new PolyVecL[var2];

      for (int var3 = 0; var3 < var2; var3++) {
         this.matrix[var3] = new PolyVecL(var1);
      }
   }

   public void pointwiseMontgomery(PolyVecK var1, PolyVecL var2) {
      for (int var3 = 0; var3 < this.matrix.length; var3++) {
         var1.getVectorIndex(var3).pointwiseAccountMontgomery(this.matrix[var3], var2);
      }
   }

   public void expandMatrix(byte[] var1) {
      for (int var2 = 0; var2 < this.matrix.length; var2++) {
         this.matrix[var2].uniformBlocks(var1, var2 << 8);
      }
   }

   private String addString() {
      String var1 = "[";

      for (int var2 = 0; var2 < this.matrix.length; var2++) {
         var1 = var1 + "Outer Matrix " + var2 + " [";
         var1 = var1 + this.matrix[var2].toString();
         if (var2 == this.matrix.length - 1) {
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
