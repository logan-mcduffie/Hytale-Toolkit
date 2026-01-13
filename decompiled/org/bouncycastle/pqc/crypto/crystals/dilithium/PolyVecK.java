package org.bouncycastle.pqc.crypto.crystals.dilithium;

class PolyVecK {
   Poly[] vec;
   private DilithiumEngine engine;
   private int mode;
   private int polyVecBytes;
   private int dilithiumK;
   private int dilithiumL;

   public PolyVecK(DilithiumEngine var1) {
      this.engine = var1;
      this.mode = var1.getDilithiumMode();
      this.dilithiumK = var1.getDilithiumK();
      this.dilithiumL = var1.getDilithiumL();
      this.vec = new Poly[this.dilithiumK];

      for (int var2 = 0; var2 < this.dilithiumK; var2++) {
         this.vec[var2] = new Poly(var1);
      }
   }

   public PolyVecK() throws Exception {
      throw new Exception("Requires Parameter");
   }

   public Poly getVectorIndex(int var1) {
      return this.vec[var1];
   }

   public void setVectorIndex(int var1, Poly var2) {
      this.vec[var1] = var2;
   }

   public void uniformEta(byte[] var1, short var2) {
      short var4 = var2;

      for (int var3 = 0; var3 < this.dilithiumK; var3++) {
         this.getVectorIndex(var3).uniformEta(var1, var4++);
      }
   }

   public void reduce() {
      for (int var1 = 0; var1 < this.dilithiumK; var1++) {
         this.getVectorIndex(var1).reduce();
      }
   }

   public void invNttToMont() {
      for (int var1 = 0; var1 < this.dilithiumK; var1++) {
         this.getVectorIndex(var1).invNttToMont();
      }
   }

   public void addPolyVecK(PolyVecK var1) {
      for (int var2 = 0; var2 < this.dilithiumK; var2++) {
         this.getVectorIndex(var2).addPoly(var1.getVectorIndex(var2));
      }
   }

   public void conditionalAddQ() {
      for (int var1 = 0; var1 < this.dilithiumK; var1++) {
         this.getVectorIndex(var1).conditionalAddQ();
      }
   }

   public void power2Round(PolyVecK var1) {
      for (int var2 = 0; var2 < this.dilithiumK; var2++) {
         this.getVectorIndex(var2).power2Round(var1.getVectorIndex(var2));
      }
   }

   public void polyVecNtt() {
      for (int var1 = 0; var1 < this.dilithiumK; var1++) {
         this.vec[var1].polyNtt();
      }
   }

   public void decompose(PolyVecK var1) {
      for (int var2 = 0; var2 < this.dilithiumK; var2++) {
         this.getVectorIndex(var2).decompose(var1.getVectorIndex(var2));
      }
   }

   public byte[] packW1() {
      byte[] var1 = new byte[this.dilithiumK * this.engine.getDilithiumPolyW1PackedBytes()];

      for (int var2 = 0; var2 < this.dilithiumK; var2++) {
         System.arraycopy(
            this.getVectorIndex(var2).w1Pack(), 0, var1, var2 * this.engine.getDilithiumPolyW1PackedBytes(), this.engine.getDilithiumPolyW1PackedBytes()
         );
      }

      return var1;
   }

   public void pointwisePolyMontgomery(Poly var1, PolyVecK var2) {
      for (int var3 = 0; var3 < this.dilithiumK; var3++) {
         this.getVectorIndex(var3).pointwiseMontgomery(var1, var2.getVectorIndex(var3));
      }
   }

   public void subtract(PolyVecK var1) {
      for (int var2 = 0; var2 < this.dilithiumK; var2++) {
         this.getVectorIndex(var2).subtract(var1.getVectorIndex(var2));
      }
   }

   public boolean checkNorm(int var1) {
      for (int var2 = 0; var2 < this.dilithiumK; var2++) {
         if (this.getVectorIndex(var2).checkNorm(var1)) {
            return true;
         }
      }

      return false;
   }

   public int makeHint(PolyVecK var1, PolyVecK var2) {
      int var4 = 0;

      for (int var3 = 0; var3 < this.dilithiumK; var3++) {
         var4 += this.getVectorIndex(var3).polyMakeHint(var1.getVectorIndex(var3), var2.getVectorIndex(var3));
      }

      return var4;
   }

   public void useHint(PolyVecK var1, PolyVecK var2) {
      for (int var3 = 0; var3 < this.dilithiumK; var3++) {
         this.getVectorIndex(var3).polyUseHint(var1.getVectorIndex(var3), var2.getVectorIndex(var3));
      }
   }

   public void shiftLeft() {
      for (int var1 = 0; var1 < this.dilithiumK; var1++) {
         this.getVectorIndex(var1).shiftLeft();
      }
   }

   @Override
   public String toString() {
      String var1 = "[";

      for (int var2 = 0; var2 < this.dilithiumK; var2++) {
         var1 = var1 + var2 + " " + this.getVectorIndex(var2).toString();
         if (var2 != this.dilithiumK - 1) {
            var1 = var1 + ",\n";
         }
      }

      return var1 + "]";
   }

   public String toString(String var1) {
      return var1 + ": " + this.toString();
   }
}
