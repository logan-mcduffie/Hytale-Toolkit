package org.bouncycastle.pqc.math.ntru;

import org.bouncycastle.pqc.math.ntru.parameters.NTRUHRSSParameterSet;

public class HRSS1373Polynomial extends HRSSPolynomial {
   private static final int L = 1376;
   private static final int M = 344;
   private static final int K = 86;

   public HRSS1373Polynomial(NTRUHRSSParameterSet var1) {
      super(var1);
   }

   @Override
   public byte[] sqToBytes(int var1) {
      byte[] var2 = new byte[var1];
      short[] var5 = new short[4];

      int var3;
      for (var3 = 0; var3 < this.params.packDegree() / 4; var3++) {
         for (int var4 = 0; var4 < 4; var4++) {
            var5[var4] = (short)modQ(this.coeffs[4 * var3 + var4] & '\uffff', this.params.q());
         }

         var2[7 * var3 + 0] = (byte)(var5[0] & 255);
         var2[7 * var3 + 1] = (byte)(var5[0] >>> 8 | (var5[1] & 3) << 6);
         var2[7 * var3 + 2] = (byte)(var5[1] >>> 2 & 0xFF);
         var2[7 * var3 + 3] = (byte)(var5[1] >>> 10 | (var5[2] & 15) << 4);
         var2[7 * var3 + 4] = (byte)(var5[2] >>> 4 & 0xFF);
         var2[7 * var3 + 5] = (byte)(var5[2] >>> 12 | (var5[3] & 63) << 2);
         var2[7 * var3 + 6] = (byte)(var5[3] >>> 6);
      }

      if (this.params.packDegree() % 4 == 2) {
         var5[0] = (short)modQ(this.coeffs[this.params.packDegree() - 2] & '\uffff', this.params.q());
         var5[1] = (short)modQ(this.coeffs[this.params.packDegree() - 1] & '\uffff', this.params.q());
         var2[7 * var3 + 0] = (byte)(var5[0] & 255);
         var2[7 * var3 + 1] = (byte)(var5[0] >>> 8 | (var5[1] & 3) << 6);
         var2[7 * var3 + 2] = (byte)(var5[1] >>> 2 & 0xFF);
         var2[7 * var3 + 3] = (byte)(var5[1] >>> 10);
      }

      return var2;
   }

   @Override
   public void sqFromBytes(byte[] var1) {
      int var2;
      for (var2 = 0; var2 < this.params.packDegree() / 4; var2++) {
         this.coeffs[4 * var2 + 0] = (short)(var1[7 * var2 + 0] & 255 | ((short)(var1[7 * var2 + 1] & 255) & 63) << 8);
         this.coeffs[4 * var2 + 1] = (short)((var1[7 * var2 + 1] & 255) >>> 6 | (short)(var1[7 * var2 + 2] & 255) << 2 | (short)(var1[7 * var2 + 3] & 15) << 10);
         this.coeffs[4 * var2 + 2] = (short)(
            (var1[7 * var2 + 3] & 255) >>> 4 | ((short)(var1[7 * var2 + 4] & 255) & 255) << 4 | (short)(var1[7 * var2 + 5] & 3) << 12
         );
         this.coeffs[4 * var2 + 3] = (short)((var1[7 * var2 + 5] & 255) >>> 2 | (short)(var1[7 * var2 + 6] & 255) << 6);
      }

      if (this.params.packDegree() % 4 == 2) {
         this.coeffs[4 * var2 + 0] = (short)(var1[7 * var2 + 0] | (var1[7 * var2 + 1] & 63) << 8);
         this.coeffs[4 * var2 + 1] = (short)(var1[7 * var2 + 1] >>> 6 | (short)var1[7 * var2 + 2] << 2 | (var1[7 * var2 + 3] & 15) << 10);
      }

      this.coeffs[this.params.n() - 1] = 0;
   }
}
