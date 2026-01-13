package org.bouncycastle.pqc.crypto.mldsa;

class PolyVecK {
   private final Poly[] vec;

   PolyVecK(MLDSAEngine var1) {
      int var2 = var1.getDilithiumK();
      this.vec = new Poly[var2];

      for (int var3 = 0; var3 < var2; var3++) {
         this.vec[var3] = new Poly(var1);
      }
   }

   Poly getVectorIndex(int var1) {
      return this.vec[var1];
   }

   void setVectorIndex(int var1, Poly var2) {
      this.vec[var1] = var2;
   }

   public void uniformEta(byte[] var1, short var2) {
      short var3 = var2;

      for (int var4 = 0; var4 < this.vec.length; var4++) {
         this.vec[var4].uniformEta(var1, var3++);
      }
   }

   public void reduce() {
      for (int var1 = 0; var1 < this.vec.length; var1++) {
         this.getVectorIndex(var1).reduce();
      }
   }

   public void invNttToMont() {
      for (int var1 = 0; var1 < this.vec.length; var1++) {
         this.getVectorIndex(var1).invNttToMont();
      }
   }

   public void addPolyVecK(PolyVecK var1) {
      for (int var2 = 0; var2 < this.vec.length; var2++) {
         this.getVectorIndex(var2).addPoly(var1.getVectorIndex(var2));
      }
   }

   public void conditionalAddQ() {
      for (int var1 = 0; var1 < this.vec.length; var1++) {
         this.getVectorIndex(var1).conditionalAddQ();
      }
   }

   public void power2Round(PolyVecK var1) {
      for (int var2 = 0; var2 < this.vec.length; var2++) {
         this.getVectorIndex(var2).power2Round(var1.getVectorIndex(var2));
      }
   }

   public void polyVecNtt() {
      for (int var1 = 0; var1 < this.vec.length; var1++) {
         this.vec[var1].polyNtt();
      }
   }

   public void decompose(PolyVecK var1) {
      for (int var2 = 0; var2 < this.vec.length; var2++) {
         this.getVectorIndex(var2).decompose(var1.getVectorIndex(var2));
      }
   }

   public void packW1(MLDSAEngine var1, byte[] var2, int var3) {
      for (int var4 = 0; var4 < this.vec.length; var4++) {
         this.getVectorIndex(var4).packW1(var2, var3 + var4 * var1.getDilithiumPolyW1PackedBytes());
      }
   }

   public void pointwisePolyMontgomery(Poly var1, PolyVecK var2) {
      for (int var3 = 0; var3 < this.vec.length; var3++) {
         this.getVectorIndex(var3).pointwiseMontgomery(var1, var2.getVectorIndex(var3));
      }
   }

   public void subtract(PolyVecK var1) {
      for (int var2 = 0; var2 < this.vec.length; var2++) {
         this.getVectorIndex(var2).subtract(var1.getVectorIndex(var2));
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

   public int makeHint(PolyVecK var1, PolyVecK var2) {
      int var3 = 0;

      for (int var4 = 0; var4 < this.vec.length; var4++) {
         var3 += this.getVectorIndex(var4).polyMakeHint(var1.getVectorIndex(var4), var2.getVectorIndex(var4));
      }

      return var3;
   }

   public void useHint(PolyVecK var1, PolyVecK var2) {
      for (int var3 = 0; var3 < this.vec.length; var3++) {
         this.getVectorIndex(var3).polyUseHint(var1.getVectorIndex(var3), var2.getVectorIndex(var3));
      }
   }

   public void shiftLeft() {
      for (int var1 = 0; var1 < this.vec.length; var1++) {
         this.getVectorIndex(var1).shiftLeft();
      }
   }

   @Override
   public String toString() {
      String var1 = "[";

      for (int var2 = 0; var2 < this.vec.length; var2++) {
         var1 = var1 + var2 + " " + this.getVectorIndex(var2).toString();
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
