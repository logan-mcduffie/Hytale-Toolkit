package org.bouncycastle.pqc.crypto.picnic;

class KMatrices {
   private int nmatrices;
   private int rows;
   private int columns;
   private int[] data;

   public KMatrices(int var1, int var2, int var3, int[] var4) {
      this.nmatrices = var1;
      this.rows = var2;
      this.columns = var3;
      this.data = var4;
   }

   public int getNmatrices() {
      return this.nmatrices;
   }

   public int getSize() {
      return this.rows * this.columns;
   }

   public int getRows() {
      return this.rows;
   }

   public int getColumns() {
      return this.columns;
   }

   public int[] getData() {
      return this.data;
   }
}
