package org.bouncycastle.math.ec;

public class FixedPointPreCompInfo implements PreCompInfo {
   protected ECPoint offset = null;
   protected ECLookupTable lookupTable = null;
   protected int width = -1;

   public ECLookupTable getLookupTable() {
      return this.lookupTable;
   }

   public void setLookupTable(ECLookupTable var1) {
      this.lookupTable = var1;
   }

   public ECPoint getOffset() {
      return this.offset;
   }

   public void setOffset(ECPoint var1) {
      this.offset = var1;
   }

   public int getWidth() {
      return this.width;
   }

   public void setWidth(int var1) {
      this.width = var1;
   }
}
