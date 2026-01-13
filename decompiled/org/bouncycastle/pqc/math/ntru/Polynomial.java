package org.bouncycastle.pqc.math.ntru;

import org.bouncycastle.pqc.math.ntru.parameters.NTRUParameterSet;

public abstract class Polynomial {
   public short[] coeffs;
   protected NTRUParameterSet params;

   public Polynomial(NTRUParameterSet var1) {
      this.coeffs = new short[var1.n()];
      this.params = var1;
   }

   static short bothNegativeMask(short var0, short var1) {
      return (short)((var0 & var1) >>> 15);
   }

   static short mod3(short var0) {
      return (short)((var0 & '\uffff') % 3);
   }

   static byte mod3(byte var0) {
      return (byte)((var0 & 255) % 3);
   }

   static int modQ(int var0, int var1) {
      return var0 % var1;
   }

   public void mod3PhiN() {
      int var1 = this.params.n();

      for (int var2 = 0; var2 < var1; var2++) {
         this.coeffs[var2] = mod3((short)(this.coeffs[var2] + 2 * this.coeffs[var1 - 1]));
      }
   }

   public void modQPhiN() {
      int var1 = this.params.n();

      for (int var2 = 0; var2 < var1; var2++) {
         this.coeffs[var2] = (short)(this.coeffs[var2] - this.coeffs[var1 - 1]);
      }
   }

   public abstract byte[] sqToBytes(int var1);

   public abstract void sqFromBytes(byte[] var1);

   public byte[] rqSumZeroToBytes(int var1) {
      return this.sqToBytes(var1);
   }

   public void rqSumZeroFromBytes(byte[] var1) {
      int var2 = this.coeffs.length;
      this.sqFromBytes(var1);
      this.coeffs[var2 - 1] = 0;

      for (int var3 = 0; var3 < this.params.packDegree(); var3++) {
         this.coeffs[var2 - 1] = (short)(this.coeffs[var2 - 1] - this.coeffs[var3]);
      }
   }

   public byte[] s3ToBytes(int var1) {
      byte[] var2 = new byte[var1];
      this.s3ToBytes(var2, 0);
      return var2;
   }

   public void s3ToBytes(byte[] var1, int var2) {
      int var3 = this.params.packDegree();
      int var4 = var3 - 5;

      byte var5;
      for (var5 = 0; var5 <= var4; var5 += 5) {
         int var6 = this.coeffs[var5 + 0] & 255;
         int var7 = (this.coeffs[var5 + 1] & 255) * 3;
         int var8 = (this.coeffs[var5 + 2] & 255) * 9;
         int var9 = (this.coeffs[var5 + 3] & 255) * 27;
         int var10 = (this.coeffs[var5 + 4] & 255) * 81;
         var1[var2++] = (byte)(var6 + var7 + var8 + var9 + var10);
      }

      if (var5 < var3) {
         int var12 = var3 - 1;
         int var13 = this.coeffs[var12] & 255;

         while (--var12 >= var5) {
            var13 *= 3;
            var13 += this.coeffs[var12] & 255;
         }

         var1[var2++] = (byte)var13;
      }
   }

   public void s3FromBytes(byte[] var1) {
      int var2 = this.coeffs.length;

      for (int var4 = 0; var4 < this.params.packDegree() / 5; var4++) {
         byte var3 = var1[var4];
         this.coeffs[5 * var4 + 0] = var3;
         this.coeffs[5 * var4 + 1] = (short)((var3 & 255) * 171 >>> 9);
         this.coeffs[5 * var4 + 2] = (short)((var3 & 255) * 57 >>> 9);
         this.coeffs[5 * var4 + 3] = (short)((var3 & 255) * 19 >>> 9);
         this.coeffs[5 * var4 + 4] = (short)((var3 & 255) * 203 >>> 14);
      }

      if (this.params.packDegree() > this.params.packDegree() / 5 * 5) {
         int var7 = this.params.packDegree() / 5;
         byte var6 = var1[var7];

         for (int var5 = 0; 5 * var7 + var5 < this.params.packDegree(); var5++) {
            this.coeffs[5 * var7 + var5] = var6;
            var6 = (byte)((var6 & 255) * 171 >> 9);
         }
      }

      this.coeffs[var2 - 1] = 0;
      this.mod3PhiN();
   }

   public void sqMul(Polynomial var1, Polynomial var2) {
      this.rqMul(var1, var2);
      this.modQPhiN();
   }

   public void rqMul(Polynomial var1, Polynomial var2) {
      int var3 = this.coeffs.length;

      for (int var4 = 0; var4 < var3; var4++) {
         this.coeffs[var4] = 0;

         for (int var5 = 1; var5 < var3 - var4; var5++) {
            this.coeffs[var4] = (short)(this.coeffs[var4] + var1.coeffs[var4 + var5] * var2.coeffs[var3 - var5]);
         }

         for (int var6 = 0; var6 < var4 + 1; var6++) {
            this.coeffs[var4] = (short)(this.coeffs[var4] + var1.coeffs[var4 - var6] * var2.coeffs[var6]);
         }
      }
   }

   public void s3Mul(Polynomial var1, Polynomial var2) {
      this.rqMul(var1, var2);
      this.mod3PhiN();
   }

   public abstract void lift(Polynomial var1);

   public void rqToS3(Polynomial var1) {
      int var2 = this.coeffs.length;

      for (int var4 = 0; var4 < var2; var4++) {
         this.coeffs[var4] = (short)modQ(var1.coeffs[var4] & '\uffff', this.params.q());
         short var3 = (short)(this.coeffs[var4] >>> this.params.logQ() - 1);
         this.coeffs[var4] = (short)(this.coeffs[var4] + (var3 << 1 - (this.params.logQ() & 1)));
      }

      this.mod3PhiN();
   }

   public void r2Inv(Polynomial var1) {
      Polynomial var2 = this.params.createPolynomial();
      Polynomial var3 = this.params.createPolynomial();
      Polynomial var4 = this.params.createPolynomial();
      Polynomial var5 = this.params.createPolynomial();
      this.r2Inv(var1, var2, var3, var4, var5);
   }

   public void rqInv(Polynomial var1) {
      Polynomial var2 = this.params.createPolynomial();
      Polynomial var3 = this.params.createPolynomial();
      Polynomial var4 = this.params.createPolynomial();
      Polynomial var5 = this.params.createPolynomial();
      this.rqInv(var1, var2, var3, var4, var5);
   }

   public void s3Inv(Polynomial var1) {
      Polynomial var2 = this.params.createPolynomial();
      Polynomial var3 = this.params.createPolynomial();
      Polynomial var4 = this.params.createPolynomial();
      Polynomial var5 = this.params.createPolynomial();
      this.s3Inv(var1, var2, var3, var4, var5);
   }

   void r2Inv(Polynomial var1, Polynomial var2, Polynomial var3, Polynomial var4, Polynomial var5) {
      int var6 = this.coeffs.length;
      var5.coeffs[0] = 1;

      for (int var7 = 0; var7 < var6; var7++) {
         var2.coeffs[var7] = 1;
      }

      for (int var13 = 0; var13 < var6 - 1; var13++) {
         var3.coeffs[var6 - 2 - var13] = (short)((var1.coeffs[var13] ^ var1.coeffs[var6 - 1]) & 1);
      }

      var3.coeffs[var6 - 1] = 0;
      short var20 = 1;

      for (int var8 = 0; var8 < 2 * (var6 - 1) - 1; var8++) {
         for (int var14 = var6 - 1; var14 > 0; var14--) {
            var4.coeffs[var14] = var4.coeffs[var14 - 1];
         }

         var4.coeffs[0] = 0;
         short var10 = (short)(var3.coeffs[0] & var2.coeffs[0]);
         short var11 = bothNegativeMask((short)(-var20), (short)(-var3.coeffs[0]));
         var20 = (short)(var20 ^ var11 & (var20 ^ -var20));
         var20++;

         for (int var15 = 0; var15 < var6; var15++) {
            short var12 = (short)(var11 & (var2.coeffs[var15] ^ var3.coeffs[var15]));
            var2.coeffs[var15] = (short)(var2.coeffs[var15] ^ var12);
            var3.coeffs[var15] = (short)(var3.coeffs[var15] ^ var12);
            var12 = (short)(var11 & (var4.coeffs[var15] ^ var5.coeffs[var15]));
            var4.coeffs[var15] = (short)(var4.coeffs[var15] ^ var12);
            var5.coeffs[var15] = (short)(var5.coeffs[var15] ^ var12);
         }

         for (int var16 = 0; var16 < var6; var16++) {
            var3.coeffs[var16] = (short)(var3.coeffs[var16] ^ var10 & var2.coeffs[var16]);
         }

         for (int var17 = 0; var17 < var6; var17++) {
            var5.coeffs[var17] = (short)(var5.coeffs[var17] ^ var10 & var4.coeffs[var17]);
         }

         for (int var18 = 0; var18 < var6 - 1; var18++) {
            var3.coeffs[var18] = var3.coeffs[var18 + 1];
         }

         var3.coeffs[var6 - 1] = 0;
      }

      for (int var19 = 0; var19 < var6 - 1; var19++) {
         this.coeffs[var19] = var4.coeffs[var6 - 2 - var19];
      }

      this.coeffs[var6 - 1] = 0;
   }

   void rqInv(Polynomial var1, Polynomial var2, Polynomial var3, Polynomial var4, Polynomial var5) {
      var2.r2Inv(var1);
      this.r2InvToRqInv(var2, var1, var3, var4, var5);
   }

   private void r2InvToRqInv(Polynomial var1, Polynomial var2, Polynomial var3, Polynomial var4, Polynomial var5) {
      int var6 = this.coeffs.length;

      for (int var7 = 0; var7 < var6; var7++) {
         var3.coeffs[var7] = (short)(-var2.coeffs[var7]);
      }

      for (int var8 = 0; var8 < var6; var8++) {
         this.coeffs[var8] = var1.coeffs[var8];
      }

      var4.rqMul(this, var3);
      var4.coeffs[0] = (short)(var4.coeffs[0] + 2);
      var5.rqMul(var4, this);
      var4.rqMul(var5, var3);
      var4.coeffs[0] = (short)(var4.coeffs[0] + 2);
      this.rqMul(var4, var5);
      var4.rqMul(this, var3);
      var4.coeffs[0] = (short)(var4.coeffs[0] + 2);
      var5.rqMul(var4, this);
      var4.rqMul(var5, var3);
      var4.coeffs[0] = (short)(var4.coeffs[0] + 2);
      this.rqMul(var4, var5);
   }

   void s3Inv(Polynomial var1, Polynomial var2, Polynomial var3, Polynomial var4, Polynomial var5) {
      int var6 = this.coeffs.length;
      var5.coeffs[0] = 1;

      for (int var7 = 0; var7 < var6; var7++) {
         var2.coeffs[var7] = 1;
      }

      for (int var13 = 0; var13 < var6 - 1; var13++) {
         var3.coeffs[var6 - 2 - var13] = mod3((short)((var1.coeffs[var13] & 3) + 2 * (var1.coeffs[var6 - 1] & 3)));
      }

      var3.coeffs[var6 - 1] = 0;
      short var20 = 1;

      for (int var8 = 0; var8 < 2 * (var6 - 1) - 1; var8++) {
         for (int var14 = var6 - 1; var14 > 0; var14--) {
            var4.coeffs[var14] = var4.coeffs[var14 - 1];
         }

         var4.coeffs[0] = 0;
         short var10 = mod3((byte)(2 * var3.coeffs[0] * var2.coeffs[0]));
         short var11 = bothNegativeMask((short)(-var20), (short)(-var3.coeffs[0]));
         var20 = (short)(var20 ^ var11 & (var20 ^ -var20));
         var20++;

         for (int var15 = 0; var15 < var6; var15++) {
            short var12 = (short)(var11 & (var2.coeffs[var15] ^ var3.coeffs[var15]));
            var2.coeffs[var15] = (short)(var2.coeffs[var15] ^ var12);
            var3.coeffs[var15] = (short)(var3.coeffs[var15] ^ var12);
            var12 = (short)(var11 & (var4.coeffs[var15] ^ var5.coeffs[var15]));
            var4.coeffs[var15] = (short)(var4.coeffs[var15] ^ var12);
            var5.coeffs[var15] = (short)(var5.coeffs[var15] ^ var12);
         }

         for (int var16 = 0; var16 < var6; var16++) {
            var3.coeffs[var16] = mod3((byte)(var3.coeffs[var16] + var10 * var2.coeffs[var16]));
         }

         for (int var17 = 0; var17 < var6; var17++) {
            var5.coeffs[var17] = mod3((byte)(var5.coeffs[var17] + var10 * var4.coeffs[var17]));
         }

         for (int var18 = 0; var18 < var6 - 1; var18++) {
            var3.coeffs[var18] = var3.coeffs[var18 + 1];
         }

         var3.coeffs[var6 - 1] = 0;
      }

      short var21 = var2.coeffs[0];

      for (int var19 = 0; var19 < var6 - 1; var19++) {
         this.coeffs[var19] = mod3((byte)(var21 * var4.coeffs[var6 - 2 - var19]));
      }

      this.coeffs[var6 - 1] = 0;
   }

   public void z3ToZq() {
      int var1 = this.coeffs.length;

      for (int var2 = 0; var2 < var1; var2++) {
         this.coeffs[var2] = (short)(this.coeffs[var2] | -(this.coeffs[var2] >>> 1) & this.params.q() - 1);
      }
   }

   public void trinaryZqToZ3() {
      int var1 = this.coeffs.length;

      for (int var2 = 0; var2 < var1; var2++) {
         this.coeffs[var2] = (short)modQ(this.coeffs[var2] & '\uffff', this.params.q());
         this.coeffs[var2] = (short)(3 & (this.coeffs[var2] ^ this.coeffs[var2] >>> this.params.logQ() - 1));
      }
   }
}
