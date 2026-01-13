package org.bouncycastle.pqc.math.ntru;

import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPSParameterSet;

public class HPSPolynomial extends Polynomial {
   public HPSPolynomial(NTRUHPSParameterSet var1) {
      super(var1);
   }

   @Override
   public byte[] sqToBytes(int var1) {
      byte[] var2 = new byte[var1];
      short[] var5 = new short[8];

      int var3;
      for (var3 = 0; var3 < this.params.packDegree() / 8; var3++) {
         for (int var4 = 0; var4 < 8; var4++) {
            var5[var4] = (short)modQ(this.coeffs[8 * var3 + var4] & '\uffff', this.params.q());
         }

         var2[11 * var3 + 0] = (byte)(var5[0] & 255);
         var2[11 * var3 + 1] = (byte)(var5[0] >>> 8 | (var5[1] & 31) << 3);
         var2[11 * var3 + 2] = (byte)(var5[1] >>> 5 | (var5[2] & 3) << 6);
         var2[11 * var3 + 3] = (byte)(var5[2] >>> 2 & 0xFF);
         var2[11 * var3 + 4] = (byte)(var5[2] >>> 10 | (var5[3] & 127) << 1);
         var2[11 * var3 + 5] = (byte)(var5[3] >>> 7 | (var5[4] & 15) << 4);
         var2[11 * var3 + 6] = (byte)(var5[4] >>> 4 | (var5[5] & 1) << 7);
         var2[11 * var3 + 7] = (byte)(var5[5] >>> 1 & 0xFF);
         var2[11 * var3 + 8] = (byte)(var5[5] >>> 9 | (var5[6] & 63) << 2);
         var2[11 * var3 + 9] = (byte)(var5[6] >>> 6 | (var5[7] & 7) << 5);
         var2[11 * var3 + 10] = (byte)(var5[7] >>> 3);
      }

      int var6;
      for (var6 = 0; var6 < this.params.packDegree() - 8 * var3; var6++) {
         var5[var6] = (short)modQ(this.coeffs[8 * var3 + var6] & '\uffff', this.params.q());
      }

      while (var6 < 8) {
         var5[var6] = 0;
         var6++;
      }

      switch (this.params.packDegree() & 7) {
         case 2:
            var2[11 * var3 + 0] = (byte)(var5[0] & 255);
            var2[11 * var3 + 1] = (byte)(var5[0] >>> 8 | (var5[1] & 31) << 3);
            var2[11 * var3 + 2] = (byte)(var5[1] >>> 5 | (var5[2] & 3) << 6);
            break;
         case 4:
            var2[11 * var3 + 0] = (byte)(var5[0] & 255);
            var2[11 * var3 + 1] = (byte)(var5[0] >>> 8 | (var5[1] & 31) << 3);
            var2[11 * var3 + 2] = (byte)(var5[1] >>> 5 | (var5[2] & 3) << 6);
            var2[11 * var3 + 3] = (byte)(var5[2] >>> 2 & 0xFF);
            var2[11 * var3 + 4] = (byte)(var5[2] >>> 10 | (var5[3] & 127) << 1);
            var2[11 * var3 + 5] = (byte)(var5[3] >>> 7 | (var5[4] & 15) << 4);
      }

      return var2;
   }

   @Override
   public void sqFromBytes(byte[] var1) {
      int var2 = this.coeffs.length;

      int var3;
      for (var3 = 0; var3 < this.params.packDegree() / 8; var3++) {
         this.coeffs[8 * var3 + 0] = (short)((var1[11 * var3 + 0] & 255) >>> 0 | ((short)(var1[11 * var3 + 1] & 255) & 7) << 8);
         this.coeffs[8 * var3 + 1] = (short)((var1[11 * var3 + 1] & 255) >>> 3 | ((short)(var1[11 * var3 + 2] & 255) & 63) << 5);
         this.coeffs[8 * var3 + 2] = (short)(
            (var1[11 * var3 + 2] & 255) >>> 6 | ((short)(var1[11 * var3 + 3] & 255) & 255) << 2 | ((short)(var1[11 * var3 + 4] & 255) & 1) << 10
         );
         this.coeffs[8 * var3 + 3] = (short)((var1[11 * var3 + 4] & 255) >>> 1 | ((short)(var1[11 * var3 + 5] & 255) & 15) << 7);
         this.coeffs[8 * var3 + 4] = (short)((var1[11 * var3 + 5] & 255) >>> 4 | ((short)(var1[11 * var3 + 6] & 255) & 127) << 4);
         this.coeffs[8 * var3 + 5] = (short)(
            (var1[11 * var3 + 6] & 255) >>> 7 | ((short)(var1[11 * var3 + 7] & 255) & 255) << 1 | ((short)(var1[11 * var3 + 8] & 255) & 3) << 9
         );
         this.coeffs[8 * var3 + 6] = (short)((var1[11 * var3 + 8] & 255) >>> 2 | ((short)(var1[11 * var3 + 9] & 255) & 31) << 6);
         this.coeffs[8 * var3 + 7] = (short)((var1[11 * var3 + 9] & 255) >>> 5 | ((short)(var1[11 * var3 + 10] & 255) & 255) << 3);
      }

      switch (this.params.packDegree() & 7) {
         case 2:
            this.coeffs[8 * var3 + 0] = (short)((var1[11 * var3 + 0] & 255) >>> 0 | ((short)(var1[11 * var3 + 1] & 255) & 7) << 8);
            this.coeffs[8 * var3 + 1] = (short)((var1[11 * var3 + 1] & 255) >>> 3 | ((short)(var1[11 * var3 + 2] & 255) & 63) << 5);
            break;
         case 4:
            this.coeffs[8 * var3 + 0] = (short)((var1[11 * var3 + 0] & 255) >>> 0 | ((short)(var1[11 * var3 + 1] & 255) & 7) << 8);
            this.coeffs[8 * var3 + 1] = (short)((var1[11 * var3 + 1] & 255) >>> 3 | ((short)(var1[11 * var3 + 2] & 255) & 63) << 5);
            this.coeffs[8 * var3 + 2] = (short)(
               (var1[11 * var3 + 2] & 255) >>> 6 | ((short)(var1[11 * var3 + 3] & 255) & 255) << 2 | ((short)(var1[11 * var3 + 4] & 255) & 1) << 10
            );
            this.coeffs[8 * var3 + 3] = (short)((var1[11 * var3 + 4] & 255) >>> 1 | ((short)(var1[11 * var3 + 5] & 255) & 15) << 7);
      }

      this.coeffs[var2 - 1] = 0;
   }

   @Override
   public void lift(Polynomial var1) {
      int var2 = this.coeffs.length;
      System.arraycopy(var1.coeffs, 0, this.coeffs, 0, var2);
      this.z3ToZq();
   }
}
