package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.pqc.math.ntru.HPSPolynomial;
import org.bouncycastle.pqc.math.ntru.HRSSPolynomial;
import org.bouncycastle.pqc.math.ntru.Polynomial;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPSParameterSet;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHRSSParameterSet;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;
import org.bouncycastle.util.Arrays;

class NTRUSampling {
   private final NTRUParameterSet params;

   public NTRUSampling(NTRUParameterSet var1) {
      this.params = var1;
   }

   public PolynomialPair sampleFg(byte[] var1) {
      if (this.params instanceof NTRUHRSSParameterSet) {
         HRSSPolynomial var4 = this.sampleIidPlus(Arrays.copyOfRange(var1, 0, this.params.sampleIidBytes()));
         HRSSPolynomial var5 = this.sampleIidPlus(Arrays.copyOfRange(var1, this.params.sampleIidBytes(), var1.length));
         return new PolynomialPair(var4, var5);
      } else if (this.params instanceof NTRUHPSParameterSet) {
         HPSPolynomial var2 = (HPSPolynomial)this.sampleIid(Arrays.copyOfRange(var1, 0, this.params.sampleIidBytes()));
         HPSPolynomial var3 = this.sampleFixedType(Arrays.copyOfRange(var1, this.params.sampleIidBytes(), var1.length));
         return new PolynomialPair(var2, var3);
      } else {
         throw new IllegalArgumentException("Invalid polynomial type");
      }
   }

   public PolynomialPair sampleRm(byte[] var1) {
      if (this.params instanceof NTRUHRSSParameterSet) {
         HRSSPolynomial var4 = (HRSSPolynomial)this.sampleIid(Arrays.copyOfRange(var1, 0, this.params.sampleIidBytes()));
         HRSSPolynomial var5 = (HRSSPolynomial)this.sampleIid(Arrays.copyOfRange(var1, this.params.sampleIidBytes(), var1.length));
         return new PolynomialPair(var4, var5);
      } else if (this.params instanceof NTRUHPSParameterSet) {
         HPSPolynomial var2 = (HPSPolynomial)this.sampleIid(Arrays.copyOfRange(var1, 0, this.params.sampleIidBytes()));
         HPSPolynomial var3 = this.sampleFixedType(Arrays.copyOfRange(var1, this.params.sampleIidBytes(), var1.length));
         return new PolynomialPair(var2, var3);
      } else {
         throw new IllegalArgumentException("Invalid polynomial type");
      }
   }

   public Polynomial sampleIid(byte[] var1) {
      Polynomial var2 = this.params.createPolynomial();

      for (int var3 = 0; var3 < this.params.n() - 1; var3++) {
         var2.coeffs[var3] = (short)mod3(var1[var3] & 255);
      }

      var2.coeffs[this.params.n() - 1] = 0;
      return var2;
   }

   public HPSPolynomial sampleFixedType(byte[] var1) {
      int var2 = this.params.n();
      int var3 = ((NTRUHPSParameterSet)this.params).weight();
      HPSPolynomial var4 = new HPSPolynomial((NTRUHPSParameterSet)this.params);
      int[] var5 = new int[var2 - 1];

      for (int var6 = 0; var6 < (var2 - 1) / 4; var6++) {
         var5[4 * var6 + 0] = ((var1[15 * var6 + 0] & 255) << 2)
            + ((var1[15 * var6 + 1] & 255) << 10)
            + ((var1[15 * var6 + 2] & 255) << 18)
            + ((var1[15 * var6 + 3] & 255) << 26);
         var5[4 * var6 + 1] = ((var1[15 + var6 * 3] & 255 & 192) >> 4)
            + ((var1[15 * var6 + 4] & 255) << 4)
            + ((var1[15 * var6 + 5] & 255) << 12)
            + ((var1[15 * var6 + 6] & 255) << 20)
            + ((var1[15 * var6 + 7] & 255) << 28);
         var5[4 * var6 + 2] = ((var1[15 + var6 * 7] & 255 & 240) >> 2)
            + ((var1[15 * var6 + 8] & 255) << 6)
            + ((var1[15 * var6 + 9] & 255) << 14)
            + ((var1[15 * var6 + 10] & 255) << 22)
            + ((var1[15 * var6 + 11] & 255) << 30);
         var5[4 * var6 + 3] = (var1[15 * var6 + 11] & 255 & 252)
            + ((var1[15 * var6 + 12] & 255) << 8)
            + ((var1[15 * var6 + 13] & 255) << 16)
            + ((var1[15 * var6 + 14] & 255) << 24);
      }

      if (var2 - 1 > (var2 - 1) / 4 * 4) {
         int var7 = (var2 - 1) / 4;
         var5[4 * var7 + 0] = ((var1[15 * var7 + 0] & 255) << 2)
            + ((var1[15 * var7 + 1] & 255) << 10)
            + ((var1[15 * var7 + 2] & 255) << 18)
            + ((var1[15 * var7 + 3] & 255) << 26);
         var5[4 * var7 + 1] = ((var1[15 + var7 * 3] & 255 & 192) >> 4)
            + ((var1[15 * var7 + 4] & 255) << 4)
            + ((var1[15 * var7 + 5] & 255) << 12)
            + ((var1[15 * var7 + 6] & 255) << 20)
            + ((var1[15 * var7 + 7] & 255) << 28);
      }

      for (int var8 = 0; var8 < var3 / 2; var8++) {
         var5[var8] |= 1;
      }

      for (int var9 = var3 / 2; var9 < var3; var9++) {
         var5[var9] |= 2;
      }

      java.util.Arrays.sort(var5);

      for (int var10 = 0; var10 < var2 - 1; var10++) {
         var4.coeffs[var10] = (short)(var5[var10] & 3);
      }

      var4.coeffs[var2 - 1] = 0;
      return var4;
   }

   public HRSSPolynomial sampleIidPlus(byte[] var1) {
      int var2 = this.params.n();
      short var4 = 0;
      HRSSPolynomial var5 = (HRSSPolynomial)this.sampleIid(var1);

      for (int var3 = 0; var3 < var2 - 1; var3++) {
         var5.coeffs[var3] = (short)(var5.coeffs[var3] | -(var5.coeffs[var3] >>> 1));
      }

      for (int var6 = 0; var6 < var2 - 1; var6++) {
         var4 += (short)(var5.coeffs[var6 + 1] * var5.coeffs[var6]);
      }

      var4 = (short)(1 | -((var4 & '\uffff') >>> 15));

      for (byte var7 = 0; var7 < var2 - 1; var7 += 2) {
         var5.coeffs[var7] = (short)(var4 * var5.coeffs[var7]);
      }

      for (int var8 = 0; var8 < var2 - 1; var8++) {
         var5.coeffs[var8] = (short)(3 & (var5.coeffs[var8] & '\uffff' ^ (var5.coeffs[var8] & '\uffff') >>> 15));
      }

      return var5;
   }

   private static int mod3(int var0) {
      return var0 % 3;
   }
}
