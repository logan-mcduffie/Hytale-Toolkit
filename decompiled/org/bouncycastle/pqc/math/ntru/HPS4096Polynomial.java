package org.bouncycastle.pqc.math.ntru;

import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPSParameterSet;

public class HPS4096Polynomial extends HPSPolynomial {
   public HPS4096Polynomial(NTRUHPSParameterSet var1) {
      super(var1);
   }

   @Override
   public byte[] sqToBytes(int var1) {
      byte[] var2 = new byte[var1];
      int var3 = this.params.q();

      for (int var4 = 0; var4 < this.params.packDegree() / 2; var4++) {
         var2[3 * var4 + 0] = (byte)(modQ(this.coeffs[2 * var4 + 0] & '\uffff', var3) & 0xFF);
         var2[3 * var4 + 1] = (byte)(modQ(this.coeffs[2 * var4 + 0] & '\uffff', var3) >>> 8 | (modQ(this.coeffs[2 * var4 + 1] & '\uffff', var3) & 15) << 4);
         var2[3 * var4 + 2] = (byte)(modQ(this.coeffs[2 * var4 + 1] & '\uffff', var3) >>> 4);
      }

      return var2;
   }

   @Override
   public void sqFromBytes(byte[] var1) {
      for (int var2 = 0; var2 < this.params.packDegree() / 2; var2++) {
         this.coeffs[2 * var2 + 0] = (short)((var1[3 * var2 + 0] & 255) >>> 0 | ((short)(var1[3 * var2 + 1] & 255) & 15) << 8);
         this.coeffs[2 * var2 + 1] = (short)((var1[3 * var2 + 1] & 255) >>> 4 | ((short)(var1[3 * var2 + 2] & 255) & 255) << 4);
      }

      this.coeffs[this.params.n() - 1] = 0;
   }
}
