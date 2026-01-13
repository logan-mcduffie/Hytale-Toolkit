package org.bouncycastle.pqc.crypto.mldsa;

class PolyVecL {
   private final Poly[] vec;

   PolyVecL(MLDSAEngine var1) {
      int var2 = var1.getDilithiumL();
      this.vec = new Poly[var2];

      for (int var3 = 0; var3 < var2; var3++) {
         this.vec[var3] = new Poly(var1);
      }
   }

   public PolyVecL() throws Exception {
      throw new Exception("Requires Parameter");
   }

   public Poly getVectorIndex(int var1) {
      return this.vec[var1];
   }

   void uniformBlocks(byte[] var1, int var2) {
      for (int var3 = 0; var3 < this.vec.length; var3++) {
         this.vec[var3].uniformBlocks(var1, (short)(var2 + var3));
      }
   }

   public void uniformEta(byte[] var1, short var2) {
      short var4 = var2;

      for (int var3 = 0; var3 < this.vec.length; var3++) {
         this.getVectorIndex(var3).uniformEta(var1, var4++);
      }
   }

   void copyTo(PolyVecL var1) {
      for (int var2 = 0; var2 < this.vec.length; var2++) {
         this.vec[var2].copyTo(var1.vec[var2]);
      }
   }

   public void polyVecNtt() {
      for (int var1 = 0; var1 < this.vec.length; var1++) {
         this.vec[var1].polyNtt();
      }
   }

   public void uniformGamma1(byte[] var1, short var2) {
      for (int var3 = 0; var3 < this.vec.length; var3++) {
         this.getVectorIndex(var3).uniformGamma1(var1, (short)(this.vec.length * var2 + var3));
      }
   }

   public void pointwisePolyMontgomery(Poly var1, PolyVecL var2) {
      for (int var3 = 0; var3 < this.vec.length; var3++) {
         this.getVectorIndex(var3).pointwiseMontgomery(var1, var2.getVectorIndex(var3));
      }
   }

   public void invNttToMont() {
      for (int var1 = 0; var1 < this.vec.length; var1++) {
         this.getVectorIndex(var1).invNttToMont();
      }
   }

   public void addPolyVecL(PolyVecL var1) {
      for (int var2 = 0; var2 < this.vec.length; var2++) {
         this.getVectorIndex(var2).addPoly(var1.getVectorIndex(var2));
      }
   }

   public void reduce() {
      for (int var1 = 0; var1 < this.vec.length; var1++) {
         this.getVectorIndex(var1).reduce();
      }
   }

   public boolean checkNorm(int var1) {
      for (int var2 = 0; var2 < this.vec.length; var2++) {
         if (this.getVectorIndex(var2).checkNorm(var1)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public String toString() {
      String var1 = "\n[";

      for (int var2 = 0; var2 < this.vec.length; var2++) {
         var1 = var1 + "Inner Matrix " + var2 + " " + this.getVectorIndex(var2).toString();
         if (var2 != this.vec.length - 1) {
            var1 = var1 + ",\n";
         }
      }

      return var1 + "]";
   }

   public String toString(String var1) {
      return var1 + ": " + this.toString();
   }
}
