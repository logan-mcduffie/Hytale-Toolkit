package org.bouncycastle.pqc.crypto.snova;

class MapGroup2 {
   public final byte[][][][] f11;
   public final byte[][][][] f12;
   public final byte[][][][] f21;

   public MapGroup2(SnovaParameters var1) {
      int var2 = var1.getM();
      int var3 = var1.getV();
      int var4 = var1.getO();
      int var5 = var1.getLsq();
      this.f11 = new byte[var2][var3][var3][var5];
      this.f12 = new byte[var2][var3][var4][var5];
      this.f21 = new byte[var2][var4][var3][var5];
   }
}
