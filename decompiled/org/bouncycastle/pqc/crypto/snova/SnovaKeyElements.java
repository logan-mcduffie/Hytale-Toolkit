package org.bouncycastle.pqc.crypto.snova;

class SnovaKeyElements {
   public final MapGroup1 map1;
   public final byte[][][] T12;
   public final MapGroup2 map2;

   public SnovaKeyElements(SnovaParameters var1) {
      int var2 = var1.getO();
      int var3 = var1.getV();
      int var4 = var1.getLsq();
      this.map1 = new MapGroup1(var1);
      this.T12 = new byte[var3][var2][var4];
      this.map2 = new MapGroup2(var1);
   }

   static int copy3d(byte[][][] var0, byte[] var1, int var2) {
      for (int var3 = 0; var3 < var0.length; var3++) {
         for (int var4 = 0; var4 < var0[var3].length; var4++) {
            System.arraycopy(var0[var3][var4], 0, var1, var2, var0[var3][var4].length);
            var2 += var0[var3][var4].length;
         }
      }

      return var2;
   }

   static int copy4d(byte[][][][] var0, byte[] var1, int var2) {
      for (int var3 = 0; var3 < var0.length; var3++) {
         var2 = copy3d(var0[var3], var1, var2);
      }

      return var2;
   }

   static int copy3d(byte[] var0, int var1, byte[][][] var2) {
      for (int var3 = 0; var3 < var2.length; var3++) {
         for (int var4 = 0; var4 < var2[var3].length; var4++) {
            System.arraycopy(var0, var1, var2[var3][var4], 0, var2[var3][var4].length);
            var1 += var2[var3][var4].length;
         }
      }

      return var1;
   }

   static int copy4d(byte[] var0, int var1, byte[][][][] var2) {
      for (int var3 = 0; var3 < var2.length; var3++) {
         for (int var4 = 0; var4 < var2[var3].length; var4++) {
            for (int var5 = 0; var5 < var2[var3][var4].length; var5++) {
               System.arraycopy(var0, var1, var2[var3][var4][var5], 0, var2[var3][var4][var5].length);
               var1 += var2[var3][var4][var5].length;
            }
         }
      }

      return var1;
   }
}
