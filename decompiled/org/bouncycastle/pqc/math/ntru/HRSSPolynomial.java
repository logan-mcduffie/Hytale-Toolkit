package org.bouncycastle.pqc.math.ntru;

import org.bouncycastle.pqc.math.ntru.parameters.NTRUHRSSParameterSet;

public class HRSSPolynomial extends Polynomial {
   public HRSSPolynomial(NTRUHRSSParameterSet var1) {
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

         var2[13 * var3 + 0] = (byte)(var5[0] & 255);
         var2[13 * var3 + 1] = (byte)(var5[0] >>> 8 | (var5[1] & 7) << 5);
         var2[13 * var3 + 2] = (byte)(var5[1] >>> 3 & 0xFF);
         var2[13 * var3 + 3] = (byte)(var5[1] >>> 11 | (var5[2] & 63) << 2);
         var2[13 * var3 + 4] = (byte)(var5[2] >>> 6 | (var5[3] & 1) << 7);
         var2[13 * var3 + 5] = (byte)(var5[3] >>> 1 & 0xFF);
         var2[13 * var3 + 6] = (byte)(var5[3] >>> 9 | (var5[4] & 15) << 4);
         var2[13 * var3 + 7] = (byte)(var5[4] >>> 4 & 0xFF);
         var2[13 * var3 + 8] = (byte)(var5[4] >>> 12 | (var5[5] & 127) << 1);
         var2[13 * var3 + 9] = (byte)(var5[5] >>> 7 | (var5[6] & 3) << 6);
         var2[13 * var3 + 10] = (byte)(var5[6] >>> 2 & 0xFF);
         var2[13 * var3 + 11] = (byte)(var5[6] >>> 10 | (var5[7] & 31) << 3);
         var2[13 * var3 + 12] = (byte)(var5[7] >>> 5);
      }

      int var6;
      for (var6 = 0; var6 < this.params.packDegree() - 8 * var3; var6++) {
         var5[var6] = (short)modQ(this.coeffs[8 * var3 + var6] & '\uffff', this.params.q());
      }

      while (var6 < 8) {
         var5[var6] = 0;
         var6++;
      }

      switch (this.params.packDegree() - 8 * (this.params.packDegree() / 8)) {
         case 4:
            var2[13 * var3 + 0] = (byte)(var5[0] & 255);
            var2[13 * var3 + 1] = (byte)(var5[0] >>> 8 | (var5[1] & 7) << 5);
            var2[13 * var3 + 2] = (byte)(var5[1] >>> 3 & 0xFF);
            var2[13 * var3 + 3] = (byte)(var5[1] >>> 11 | (var5[2] & 63) << 2);
            var2[13 * var3 + 4] = (byte)(var5[2] >>> 6 | (var5[3] & 1) << 7);
            var2[13 * var3 + 5] = (byte)(var5[3] >>> 1 & 0xFF);
            var2[13 * var3 + 6] = (byte)(var5[3] >>> 9 | (var5[4] & 15) << 4);
         case 2:
            var2[13 * var3 + 0] = (byte)(var5[0] & 255);
            var2[13 * var3 + 1] = (byte)(var5[0] >>> 8 | (var5[1] & 7) << 5);
            var2[13 * var3 + 2] = (byte)(var5[1] >>> 3 & 0xFF);
            var2[13 * var3 + 3] = (byte)(var5[1] >>> 11 | (var5[2] & 63) << 2);
         default:
            return var2;
      }
   }

   @Override
   public void sqFromBytes(byte[] var1) {
      int var2;
      for (var2 = 0; var2 < this.params.packDegree() / 8; var2++) {
         this.coeffs[8 * var2 + 0] = (short)(var1[13 * var2 + 0] & 255 | ((short)(var1[13 * var2 + 1] & 255) & 31) << 8);
         this.coeffs[8 * var2 + 1] = (short)(
            (var1[13 * var2 + 1] & 255) >>> 5 | (short)(var1[13 * var2 + 2] & 255) << 3 | ((short)(var1[13 * var2 + 3] & 255) & 3) << 11
         );
         this.coeffs[8 * var2 + 2] = (short)((var1[13 * var2 + 3] & 255) >>> 2 | ((short)(var1[13 * var2 + 4] & 255) & 127) << 6);
         this.coeffs[8 * var2 + 3] = (short)(
            (var1[13 * var2 + 4] & 255) >>> 7 | (short)(var1[13 * var2 + 5] & 255) << 1 | ((short)(var1[13 * var2 + 6] & 255) & 15) << 9
         );
         this.coeffs[8 * var2 + 4] = (short)(
            (var1[13 * var2 + 6] & 255) >>> 4 | (short)(var1[13 * var2 + 7] & 255) << 4 | ((short)(var1[13 * var2 + 8] & 255) & 1) << 12
         );
         this.coeffs[8 * var2 + 5] = (short)((var1[13 * var2 + 8] & 255) >>> 1 | ((short)(var1[13 * var2 + 9] & 255) & 63) << 7);
         this.coeffs[8 * var2 + 6] = (short)(
            (var1[13 * var2 + 9] & 255) >>> 6 | (short)(var1[13 * var2 + 10] & 255) << 2 | ((short)(var1[13 * var2 + 11] & 255) & 7) << 10
         );
         this.coeffs[8 * var2 + 7] = (short)((var1[13 * var2 + 11] & 255) >>> 3 | (short)(var1[13 * var2 + 12] & 255) << 5);
      }

      switch (this.params.packDegree() & 7) {
         case 2:
            this.coeffs[8 * var2 + 0] = (short)(var1[13 * var2 + 0] & 255 | ((short)(var1[13 * var2 + 1] & 255) & 31) << 8);
            this.coeffs[8 * var2 + 1] = (short)(
               (var1[13 * var2 + 1] & 255) >>> 5 | (short)(var1[13 * var2 + 2] & 255) << 3 | ((short)(var1[13 * var2 + 3] & 255) & 3) << 11
            );
            break;
         case 4:
            this.coeffs[8 * var2 + 0] = (short)(var1[13 * var2 + 0] & 255 | ((short)(var1[13 * var2 + 1] & 255) & 31) << 8);
            this.coeffs[8 * var2 + 1] = (short)(
               (var1[13 * var2 + 1] & 255) >>> 5 | (short)(var1[13 * var2 + 2] & 255) << 3 | ((short)(var1[13 * var2 + 3] & 255) & 3) << 11
            );
            this.coeffs[8 * var2 + 2] = (short)((var1[13 * var2 + 3] & 255) >>> 2 | ((short)(var1[13 * var2 + 4] & 255) & 127) << 6);
            this.coeffs[8 * var2 + 3] = (short)(
               (var1[13 * var2 + 4] & 255) >>> 7 | (short)(var1[13 * var2 + 5] & 255) << 1 | ((short)(var1[13 * var2 + 6] & 255) & 15) << 9
            );
      }

      this.coeffs[this.params.n() - 1] = 0;
   }

   @Override
   public void lift(Polynomial var1) {
      int var2 = this.coeffs.length;
      Polynomial var4 = this.params.createPolynomial();
      short var5 = (short)(3 - var2 % 3);
      var4.coeffs[0] = (short)(var1.coeffs[0] * (2 - var5) + var1.coeffs[1] * 0 + var1.coeffs[2] * var5);
      var4.coeffs[1] = (short)(var1.coeffs[1] * (2 - var5) + var1.coeffs[2] * 0);
      var4.coeffs[2] = (short)(var1.coeffs[2] * (2 - var5));
      short var6 = 0;

      for (int var3 = 3; var3 < var2; var3++) {
         var4.coeffs[0] = (short)(var4.coeffs[0] + var1.coeffs[var3] * (var6 + 2 * var5));
         var4.coeffs[1] = (short)(var4.coeffs[1] + var1.coeffs[var3] * (var6 + var5));
         var4.coeffs[2] = (short)(var4.coeffs[2] + var1.coeffs[var3] * var6);
         var6 = (short)((var6 + var5) % 3);
      }

      var4.coeffs[1] = (short)(var4.coeffs[1] + var1.coeffs[0] * (var6 + var5));
      var4.coeffs[2] = (short)(var4.coeffs[2] + var1.coeffs[0] * var6);
      var4.coeffs[2] = (short)(var4.coeffs[2] + var1.coeffs[1] * (var6 + var5));

      for (int var7 = 3; var7 < var2; var7++) {
         var4.coeffs[var7] = (short)(var4.coeffs[var7 - 3] + 2 * (var1.coeffs[var7] + var1.coeffs[var7 - 1] + var1.coeffs[var7 - 2]));
      }

      var4.mod3PhiN();
      var4.z3ToZq();
      this.coeffs[0] = (short)(-var4.coeffs[0]);

      for (int var8 = 0; var8 < var2 - 1; var8++) {
         this.coeffs[var8 + 1] = (short)(var4.coeffs[var8] - var4.coeffs[var8 + 1]);
      }
   }
}
