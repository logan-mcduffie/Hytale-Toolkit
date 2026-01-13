package org.bouncycastle.crypto.engines;

import org.bouncycastle.util.Pack;

public class XSalsa20Engine extends Salsa20Engine {
   @Override
   public String getAlgorithmName() {
      return "XSalsa20";
   }

   @Override
   protected int getNonceSize() {
      return 24;
   }

   @Override
   protected void setKey(byte[] var1, byte[] var2) {
      if (var1 == null) {
         throw new IllegalArgumentException(this.getAlgorithmName() + " doesn't support re-init with null key");
      } else if (var1.length != 32) {
         throw new IllegalArgumentException(this.getAlgorithmName() + " requires a 256 bit key");
      } else {
         super.setKey(var1, var2);
         Pack.littleEndianToInt(var2, 8, this.engineState, 8, 2);
         int[] var3 = new int[this.engineState.length];
         salsaCore(20, this.engineState, var3);
         this.engineState[1] = var3[0] - this.engineState[0];
         this.engineState[2] = var3[5] - this.engineState[5];
         this.engineState[3] = var3[10] - this.engineState[10];
         this.engineState[4] = var3[15] - this.engineState[15];
         this.engineState[11] = var3[6] - this.engineState[6];
         this.engineState[12] = var3[7] - this.engineState[7];
         this.engineState[13] = var3[8] - this.engineState[8];
         this.engineState[14] = var3[9] - this.engineState[9];
         Pack.littleEndianToInt(var2, 16, this.engineState, 6, 2);
      }
   }
}
