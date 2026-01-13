package org.bouncycastle.pqc.crypto.ntru;

import org.bouncycastle.pqc.math.ntru.HPSPolynomial;
import org.bouncycastle.pqc.math.ntru.Polynomial;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHPSParameterSet;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUHRSSParameterSet;
import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;
import org.bouncycastle.util.Arrays;

class NTRUOWCPA {
   private final NTRUParameterSet params;
   private final NTRUSampling sampling;

   public NTRUOWCPA(NTRUParameterSet var1) {
      this.params = var1;
      this.sampling = new NTRUSampling(var1);
   }

   public OWCPAKeyPair keypair(byte[] var1) {
      byte[] var3 = new byte[this.params.owcpaSecretKeyBytes()];
      int var4 = this.params.n();
      int var5 = this.params.q();
      Polynomial var8 = this.params.createPolynomial();
      Polynomial var9 = this.params.createPolynomial();
      Polynomial var10 = this.params.createPolynomial();
      PolynomialPair var7 = this.sampling.sampleFg(var1);
      Polynomial var11 = var7.f();
      Polynomial var12 = var7.g();
      var8.s3Inv(var11);
      var11.s3ToBytes(var3, 0);
      var8.s3ToBytes(var3, this.params.packTrinaryBytes());
      var11.z3ToZq();
      var12.z3ToZq();
      if (this.params instanceof NTRUHRSSParameterSet) {
         for (int var6 = var4 - 1; var6 > 0; var6--) {
            var12.coeffs[var6] = (short)(3 * (var12.coeffs[var6 - 1] - var12.coeffs[var6]));
         }

         var12.coeffs[0] = (short)(-(3 * var12.coeffs[0]));
      } else {
         for (int var20 = 0; var20 < var4; var20++) {
            var12.coeffs[var20] = (short)(3 * var12.coeffs[var20]);
         }
      }

      var8.rqMul(var12, var11);
      var9.rqInv(var8);
      var10.rqMul(var9, var11);
      var8.sqMul(var10, var11);
      byte[] var19 = var8.sqToBytes(var3.length - 2 * this.params.packTrinaryBytes());
      System.arraycopy(var19, 0, var3, 2 * this.params.packTrinaryBytes(), var19.length);
      var10.rqMul(var9, var12);
      var8.rqMul(var10, var12);
      byte[] var2 = var8.rqSumZeroToBytes(this.params.owcpaPublicKeyBytes());
      return new OWCPAKeyPair(var2, var3);
   }

   public byte[] encrypt(Polynomial var1, Polynomial var2, byte[] var3) {
      Polynomial var5 = this.params.createPolynomial();
      Polynomial var6 = this.params.createPolynomial();
      Polynomial var8 = var5;
      Polynomial var9 = var6;
      var5.rqSumZeroFromBytes(var3);
      var6.rqMul(var1, var5);
      var5.lift(var2);

      for (int var4 = 0; var4 < this.params.n(); var4++) {
         var9.coeffs[var4] = (short)(var9.coeffs[var4] + var8.coeffs[var4]);
      }

      return var9.rqSumZeroToBytes(this.params.ntruCiphertextBytes());
   }

   public OWCPADecryptResult decrypt(byte[] var1, byte[] var2) {
      byte[] var4 = new byte[this.params.owcpaMsgBytes()];
      Polynomial var7 = this.params.createPolynomial();
      Polynomial var8 = this.params.createPolynomial();
      Polynomial var9 = this.params.createPolynomial();
      Polynomial var10 = this.params.createPolynomial();
      Polynomial var11 = var7;
      Polynomial var17 = var8;
      Polynomial var20 = var7;
      var7.rqSumZeroFromBytes(var1);
      var8.s3FromBytes(var2);
      var8.z3ToZq();
      var9.rqMul(var7, var8);
      var8.rqToS3(var9);
      var9.s3FromBytes(Arrays.copyOfRange(var2, this.params.packTrinaryBytes(), var2.length));
      var10.s3Mul(var8, var9);
      var10.s3ToBytes(var4, this.params.packTrinaryBytes());
      int var6 = 0;
      var6 |= this.checkCiphertext(var1);
      if (this.params instanceof NTRUHPSParameterSet) {
         var6 |= this.checkM((HPSPolynomial)var10);
      }

      var8.lift(var10);

      for (int var5 = 0; var5 < this.params.n(); var5++) {
         var20.coeffs[var5] = (short)(var11.coeffs[var5] - var17.coeffs[var5]);
      }

      var9.sqFromBytes(Arrays.copyOfRange(var2, 2 * this.params.packTrinaryBytes(), var2.length));
      var10.sqMul(var20, var9);
      var6 |= this.checkR(var10);
      var10.trinaryZqToZ3();
      var10.s3ToBytes(var4, 0);
      return new OWCPADecryptResult(var4, var6);
   }

   private int checkCiphertext(byte[] var1) {
      short var2 = var1[this.params.ntruCiphertextBytes() - 1];
      var2 = (short)(var2 & 255 << 8 - (7 & this.params.logQ() * this.params.packDegree()));
      return 1 & ~var2 + 1 >>> 15;
   }

   private int checkR(Polynomial var1) {
      int var3 = 0;

      for (int var2 = 0; var2 < this.params.n() - 1; var2++) {
         short var4 = var1.coeffs[var2];
         var3 |= var4 + 1 & this.params.q() - 4;
         var3 |= var4 + 2 & 4;
      }

      var3 |= var1.coeffs[this.params.n() - 1];
      return 1 & ~var3 + 1 >>> 31;
   }

   private int checkM(HPSPolynomial var1) {
      int var3 = 0;
      short var4 = 0;
      short var5 = 0;

      for (int var2 = 0; var2 < this.params.n() - 1; var2++) {
         var4 = (short)(var4 + (var1.coeffs[var2] & 1));
         var5 = (short)(var5 + (var1.coeffs[var2] & 2));
      }

      var3 |= var4 ^ var5 >>> 1;
      var3 |= var5 ^ ((NTRUHPSParameterSet)this.params).weight();
      return 1 & ~var3 + 1 >>> 31;
   }
}
