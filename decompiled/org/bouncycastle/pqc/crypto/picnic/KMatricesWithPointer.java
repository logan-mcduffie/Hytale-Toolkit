package org.bouncycastle.pqc.crypto.picnic;

class KMatricesWithPointer extends KMatrices {
   private int matrixPointer = 0;

   public KMatricesWithPointer(KMatrices var1) {
      super(var1.getNmatrices(), var1.getRows(), var1.getColumns(), var1.getData());
   }

   public int getMatrixPointer() {
      return this.matrixPointer;
   }

   public void setMatrixPointer(int var1) {
      this.matrixPointer = var1;
   }
}
