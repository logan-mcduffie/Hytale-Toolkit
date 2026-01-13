package org.bouncycastle.math.ec;

public class SimpleLookupTable extends AbstractECLookupTable {
   private final ECPoint[] points;

   private static ECPoint[] copy(ECPoint[] var0, int var1, int var2) {
      ECPoint[] var3 = new ECPoint[var2];

      for (int var4 = 0; var4 < var2; var4++) {
         var3[var4] = var0[var1 + var4];
      }

      return var3;
   }

   public SimpleLookupTable(ECPoint[] var1, int var2, int var3) {
      this.points = copy(var1, var2, var3);
   }

   @Override
   public int getSize() {
      return this.points.length;
   }

   @Override
   public ECPoint lookup(int var1) {
      throw new UnsupportedOperationException("Constant-time lookup not supported");
   }

   @Override
   public ECPoint lookupVar(int var1) {
      return this.points[var1];
   }
}
