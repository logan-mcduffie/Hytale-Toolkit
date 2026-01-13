package org.bouncycastle.pqc.crypto.crystals.dilithium;

import org.bouncycastle.crypto.digests.SHAKEDigest;

class Poly {
   private final int polyUniformNBlocks;
   private int[] coeffs;
   private final DilithiumEngine engine;
   private final int dilithiumN = 256;
   private final Symmetric symmetric;

   public Poly(DilithiumEngine var1) {
      this.coeffs = new int[this.dilithiumN];
      this.engine = var1;
      this.symmetric = var1.GetSymmetric();
      this.polyUniformNBlocks = (768 + this.symmetric.stream128BlockBytes - 1) / this.symmetric.stream128BlockBytes;
   }

   public int getCoeffIndex(int var1) {
      return this.coeffs[var1];
   }

   public int[] getCoeffs() {
      return this.coeffs;
   }

   public void setCoeffIndex(int var1, int var2) {
      this.coeffs[var1] = var2;
   }

   public void setCoeffs(int[] var1) {
      this.coeffs = var1;
   }

   public void uniformBlocks(byte[] var1, short var2) {
      int var6 = this.polyUniformNBlocks * this.symmetric.stream128BlockBytes;
      byte[] var7 = new byte[var6 + 2];
      this.symmetric.stream128init(var1, var2);
      this.symmetric.stream128squeezeBlocks(var7, 0, var6);

      for (int var4 = rejectUniform(this, 0, this.dilithiumN, var7, var6);
         var4 < this.dilithiumN;
         var4 += rejectUniform(this, var4, this.dilithiumN - var4, var7, var6)
      ) {
         int var5 = var6 % 3;

         for (int var3 = 0; var3 < var5; var3++) {
            var7[var3] = var7[var6 - var5 + var3];
         }

         this.symmetric.stream128squeezeBlocks(var7, var5, this.symmetric.stream128BlockBytes);
         var6 = this.symmetric.stream128BlockBytes + var5;
      }
   }

   private static int rejectUniform(Poly var0, int var1, int var2, byte[] var3, int var4) {
      int var6 = 0;
      int var5 = 0;

      while (var5 < var2 && var6 + 3 <= var4) {
         int var7 = var3[var6++] & 255;
         var7 |= (var3[var6++] & 255) << 8;
         var7 |= (var3[var6++] & 255) << 16;
         var7 &= 8388607;
         if (var7 < 8380417) {
            var0.setCoeffIndex(var1 + var5, var7);
            var5++;
         }
      }

      return var5;
   }

   public void uniformEta(byte[] var1, short var2) {
      int var5 = this.engine.getDilithiumEta();
      int var4;
      if (this.engine.getDilithiumEta() == 2) {
         var4 = (136 + this.symmetric.stream256BlockBytes - 1) / this.symmetric.stream256BlockBytes;
      } else {
         if (this.engine.getDilithiumEta() != 4) {
            throw new RuntimeException("Wrong Dilithium Eta!");
         }

         var4 = (227 + this.symmetric.stream256BlockBytes - 1) / this.symmetric.stream256BlockBytes;
      }

      int var6 = var4 * this.symmetric.stream256BlockBytes;
      byte[] var7 = new byte[var6];
      this.symmetric.stream256init(var1, var2);
      this.symmetric.stream256squeezeBlocks(var7, 0, var6);

      for (int var3 = rejectEta(this, 0, this.dilithiumN, var7, var6, var5);
         var3 < 256;
         var3 += rejectEta(this, var3, this.dilithiumN - var3, var7, this.symmetric.stream256BlockBytes, var5)
      ) {
         this.symmetric.stream256squeezeBlocks(var7, 0, this.symmetric.stream256BlockBytes);
      }
   }

   private static int rejectEta(Poly var0, int var1, int var2, byte[] var3, int var4, int var5) {
      int var7 = 0;
      int var6 = 0;

      while (var6 < var2 && var7 < var4) {
         int var8 = var3[var7] & 255 & 15;
         int var9 = (var3[var7++] & 255) >> 4;
         if (var5 == 2) {
            if (var8 < 15) {
               var8 -= (205 * var8 >> 10) * 5;
               var0.setCoeffIndex(var1 + var6, 2 - var8);
               var6++;
            }

            if (var9 < 15 && var6 < var2) {
               var9 -= (205 * var9 >> 10) * 5;
               var0.setCoeffIndex(var1 + var6, 2 - var9);
               var6++;
            }
         } else if (var5 == 4) {
            if (var8 < 9) {
               var0.setCoeffIndex(var1 + var6, 4 - var8);
               var6++;
            }

            if (var9 < 9 && var6 < var2) {
               var0.setCoeffIndex(var1 + var6, 4 - var9);
               var6++;
            }
         }
      }

      return var6;
   }

   public void polyNtt() {
      this.setCoeffs(Ntt.ntt(this.coeffs));
   }

   public void pointwiseMontgomery(Poly var1, Poly var2) {
      for (int var3 = 0; var3 < this.dilithiumN; var3++) {
         this.setCoeffIndex(var3, Reduce.montgomeryReduce((long)var1.getCoeffIndex(var3) * var2.getCoeffIndex(var3)));
      }
   }

   public void pointwiseAccountMontgomery(PolyVecL var1, PolyVecL var2) {
      Poly var4 = new Poly(this.engine);
      this.pointwiseMontgomery(var1.getVectorIndex(0), var2.getVectorIndex(0));

      for (int var3 = 1; var3 < this.engine.getDilithiumL(); var3++) {
         var4.pointwiseMontgomery(var1.getVectorIndex(var3), var2.getVectorIndex(var3));
         this.addPoly(var4);
      }
   }

   public void addPoly(Poly var1) {
      for (int var2 = 0; var2 < this.dilithiumN; var2++) {
         this.setCoeffIndex(var2, this.getCoeffIndex(var2) + var1.getCoeffIndex(var2));
      }
   }

   public void reduce() {
      for (int var1 = 0; var1 < this.dilithiumN; var1++) {
         this.setCoeffIndex(var1, Reduce.reduce32(this.getCoeffIndex(var1)));
      }
   }

   public void invNttToMont() {
      this.setCoeffs(Ntt.invNttToMont(this.getCoeffs()));
   }

   public void conditionalAddQ() {
      for (int var1 = 0; var1 < this.dilithiumN; var1++) {
         this.setCoeffIndex(var1, Reduce.conditionalAddQ(this.getCoeffIndex(var1)));
      }
   }

   public void power2Round(Poly var1) {
      for (int var2 = 0; var2 < this.dilithiumN; var2++) {
         int[] var3 = Rounding.power2Round(this.getCoeffIndex(var2));
         this.setCoeffIndex(var2, var3[0]);
         var1.setCoeffIndex(var2, var3[1]);
      }
   }

   public byte[] polyt1Pack() {
      byte[] var1 = new byte[320];

      for (int var2 = 0; var2 < this.dilithiumN / 4; var2++) {
         var1[5 * var2 + 0] = (byte)(this.coeffs[4 * var2 + 0] >> 0);
         var1[5 * var2 + 1] = (byte)(this.coeffs[4 * var2 + 0] >> 8 | this.coeffs[4 * var2 + 1] << 2);
         var1[5 * var2 + 2] = (byte)(this.coeffs[4 * var2 + 1] >> 6 | this.coeffs[4 * var2 + 2] << 4);
         var1[5 * var2 + 3] = (byte)(this.coeffs[4 * var2 + 2] >> 4 | this.coeffs[4 * var2 + 3] << 6);
         var1[5 * var2 + 4] = (byte)(this.coeffs[4 * var2 + 3] >> 2);
      }

      return var1;
   }

   public void polyt1Unpack(byte[] var1) {
      for (int var2 = 0; var2 < this.dilithiumN / 4; var2++) {
         this.setCoeffIndex(4 * var2 + 0, ((var1[5 * var2 + 0] & 255) >> 0 | (var1[5 * var2 + 1] & 255) << 8) & 1023);
         this.setCoeffIndex(4 * var2 + 1, ((var1[5 * var2 + 1] & 255) >> 2 | (var1[5 * var2 + 2] & 255) << 6) & 1023);
         this.setCoeffIndex(4 * var2 + 2, ((var1[5 * var2 + 2] & 255) >> 4 | (var1[5 * var2 + 3] & 255) << 4) & 1023);
         this.setCoeffIndex(4 * var2 + 3, ((var1[5 * var2 + 3] & 255) >> 6 | (var1[5 * var2 + 4] & 255) << 2) & 1023);
      }
   }

   public byte[] polyEtaPack(byte[] var1, int var2) {
      byte[] var4 = new byte[8];
      if (this.engine.getDilithiumEta() == 2) {
         for (int var3 = 0; var3 < this.dilithiumN / 8; var3++) {
            var4[0] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * var3 + 0));
            var4[1] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * var3 + 1));
            var4[2] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * var3 + 2));
            var4[3] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * var3 + 3));
            var4[4] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * var3 + 4));
            var4[5] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * var3 + 5));
            var4[6] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * var3 + 6));
            var4[7] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(8 * var3 + 7));
            var1[var2 + 3 * var3 + 0] = (byte)(var4[0] >> 0 | var4[1] << 3 | var4[2] << 6);
            var1[var2 + 3 * var3 + 1] = (byte)(var4[2] >> 2 | var4[3] << 1 | var4[4] << 4 | var4[5] << 7);
            var1[var2 + 3 * var3 + 2] = (byte)(var4[5] >> 1 | var4[6] << 2 | var4[7] << 5);
         }
      } else {
         if (this.engine.getDilithiumEta() != 4) {
            throw new RuntimeException("Eta needs to be 2 or 4!");
         }

         for (int var5 = 0; var5 < this.dilithiumN / 2; var5++) {
            var4[0] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(2 * var5 + 0));
            var4[1] = (byte)(this.engine.getDilithiumEta() - this.getCoeffIndex(2 * var5 + 1));
            var1[var2 + var5] = (byte)(var4[0] | var4[1] << 4);
         }
      }

      return var1;
   }

   public void polyEtaUnpack(byte[] var1, int var2) {
      int var4 = this.engine.getDilithiumEta();
      if (this.engine.getDilithiumEta() == 2) {
         for (int var3 = 0; var3 < this.dilithiumN / 8; var3++) {
            int var5 = var2 + 3 * var3;
            this.setCoeffIndex(8 * var3 + 0, (var1[var5 + 0] & 255) >> 0 & 7);
            this.setCoeffIndex(8 * var3 + 1, (var1[var5 + 0] & 255) >> 3 & 7);
            this.setCoeffIndex(8 * var3 + 2, (var1[var5 + 0] & 255) >> 6 | (var1[var5 + 1] & 255) << 2 & 7);
            this.setCoeffIndex(8 * var3 + 3, (var1[var5 + 1] & 255) >> 1 & 7);
            this.setCoeffIndex(8 * var3 + 4, (var1[var5 + 1] & 255) >> 4 & 7);
            this.setCoeffIndex(8 * var3 + 5, (var1[var5 + 1] & 255) >> 7 | (var1[var5 + 2] & 255) << 1 & 7);
            this.setCoeffIndex(8 * var3 + 6, (var1[var5 + 2] & 255) >> 2 & 7);
            this.setCoeffIndex(8 * var3 + 7, (var1[var5 + 2] & 255) >> 5 & 7);
            this.setCoeffIndex(8 * var3 + 0, var4 - this.getCoeffIndex(8 * var3 + 0));
            this.setCoeffIndex(8 * var3 + 1, var4 - this.getCoeffIndex(8 * var3 + 1));
            this.setCoeffIndex(8 * var3 + 2, var4 - this.getCoeffIndex(8 * var3 + 2));
            this.setCoeffIndex(8 * var3 + 3, var4 - this.getCoeffIndex(8 * var3 + 3));
            this.setCoeffIndex(8 * var3 + 4, var4 - this.getCoeffIndex(8 * var3 + 4));
            this.setCoeffIndex(8 * var3 + 5, var4 - this.getCoeffIndex(8 * var3 + 5));
            this.setCoeffIndex(8 * var3 + 6, var4 - this.getCoeffIndex(8 * var3 + 6));
            this.setCoeffIndex(8 * var3 + 7, var4 - this.getCoeffIndex(8 * var3 + 7));
         }
      } else if (this.engine.getDilithiumEta() == 4) {
         for (int var6 = 0; var6 < this.dilithiumN / 2; var6++) {
            this.setCoeffIndex(2 * var6 + 0, var1[var2 + var6] & 15);
            this.setCoeffIndex(2 * var6 + 1, (var1[var2 + var6] & 255) >> 4);
            this.setCoeffIndex(2 * var6 + 0, var4 - this.getCoeffIndex(2 * var6 + 0));
            this.setCoeffIndex(2 * var6 + 1, var4 - this.getCoeffIndex(2 * var6 + 1));
         }
      }
   }

   public byte[] polyt0Pack(byte[] var1, int var2) {
      int[] var4 = new int[8];

      for (int var3 = 0; var3 < this.dilithiumN / 8; var3++) {
         var4[0] = 4096 - this.getCoeffIndex(8 * var3 + 0);
         var4[1] = 4096 - this.getCoeffIndex(8 * var3 + 1);
         var4[2] = 4096 - this.getCoeffIndex(8 * var3 + 2);
         var4[3] = 4096 - this.getCoeffIndex(8 * var3 + 3);
         var4[4] = 4096 - this.getCoeffIndex(8 * var3 + 4);
         var4[5] = 4096 - this.getCoeffIndex(8 * var3 + 5);
         var4[6] = 4096 - this.getCoeffIndex(8 * var3 + 6);
         var4[7] = 4096 - this.getCoeffIndex(8 * var3 + 7);
         int var5 = var2 + 13 * var3;
         var1[var5 + 0] = (byte)var4[0];
         var1[var5 + 1] = (byte)(var4[0] >> 8);
         var1[var5 + 1] = (byte)(var1[var5 + 1] | (byte)(var4[1] << 5));
         var1[var5 + 2] = (byte)(var4[1] >> 3);
         var1[var5 + 3] = (byte)(var4[1] >> 11);
         var1[var5 + 3] = (byte)(var1[var5 + 3] | (byte)(var4[2] << 2));
         var1[var5 + 4] = (byte)(var4[2] >> 6);
         var1[var5 + 4] = (byte)(var1[var5 + 4] | (byte)(var4[3] << 7));
         var1[var5 + 5] = (byte)(var4[3] >> 1);
         var1[var5 + 6] = (byte)(var4[3] >> 9);
         var1[var5 + 6] = (byte)(var1[var5 + 6] | (byte)(var4[4] << 4));
         var1[var5 + 7] = (byte)(var4[4] >> 4);
         var1[var5 + 8] = (byte)(var4[4] >> 12);
         var1[var5 + 8] = (byte)(var1[var5 + 8] | (byte)(var4[5] << 1));
         var1[var5 + 9] = (byte)(var4[5] >> 7);
         var1[var5 + 9] = (byte)(var1[var5 + 9] | (byte)(var4[6] << 6));
         var1[var5 + 10] = (byte)(var4[6] >> 2);
         var1[var5 + 11] = (byte)(var4[6] >> 10);
         var1[var5 + 11] = (byte)(var1[var5 + 11] | (byte)(var4[7] << 3));
         var1[var5 + 12] = (byte)(var4[7] >> 5);
      }

      return var1;
   }

   public void polyt0Unpack(byte[] var1, int var2) {
      for (int var3 = 0; var3 < this.dilithiumN / 8; var3++) {
         int var4 = var2 + 13 * var3;
         this.setCoeffIndex(8 * var3 + 0, (var1[var4 + 0] & 255 | (var1[var4 + 1] & 255) << 8) & 8191);
         this.setCoeffIndex(8 * var3 + 1, ((var1[var4 + 1] & 255) >> 5 | (var1[var4 + 2] & 255) << 3 | (var1[var4 + 3] & 255) << 11) & 8191);
         this.setCoeffIndex(8 * var3 + 2, ((var1[var4 + 3] & 255) >> 2 | (var1[var4 + 4] & 255) << 6) & 8191);
         this.setCoeffIndex(8 * var3 + 3, ((var1[var4 + 4] & 255) >> 7 | (var1[var4 + 5] & 255) << 1 | (var1[var4 + 6] & 255) << 9) & 8191);
         this.setCoeffIndex(8 * var3 + 4, ((var1[var4 + 6] & 255) >> 4 | (var1[var4 + 7] & 255) << 4 | (var1[var4 + 8] & 255) << 12) & 8191);
         this.setCoeffIndex(8 * var3 + 5, ((var1[var4 + 8] & 255) >> 1 | (var1[var4 + 9] & 255) << 7) & 8191);
         this.setCoeffIndex(8 * var3 + 6, ((var1[var4 + 9] & 255) >> 6 | (var1[var4 + 10] & 255) << 2 | (var1[var4 + 11] & 255) << 10) & 8191);
         this.setCoeffIndex(8 * var3 + 7, ((var1[var4 + 11] & 255) >> 3 | (var1[var4 + 12] & 255) << 5) & 8191);
         this.setCoeffIndex(8 * var3 + 0, 4096 - this.getCoeffIndex(8 * var3 + 0));
         this.setCoeffIndex(8 * var3 + 1, 4096 - this.getCoeffIndex(8 * var3 + 1));
         this.setCoeffIndex(8 * var3 + 2, 4096 - this.getCoeffIndex(8 * var3 + 2));
         this.setCoeffIndex(8 * var3 + 3, 4096 - this.getCoeffIndex(8 * var3 + 3));
         this.setCoeffIndex(8 * var3 + 4, 4096 - this.getCoeffIndex(8 * var3 + 4));
         this.setCoeffIndex(8 * var3 + 5, 4096 - this.getCoeffIndex(8 * var3 + 5));
         this.setCoeffIndex(8 * var3 + 6, 4096 - this.getCoeffIndex(8 * var3 + 6));
         this.setCoeffIndex(8 * var3 + 7, 4096 - this.getCoeffIndex(8 * var3 + 7));
      }
   }

   public void uniformGamma1(byte[] var1, short var2) {
      byte[] var3 = new byte[this.engine.getPolyUniformGamma1NBlocks() * this.symmetric.stream256BlockBytes];
      this.symmetric.stream256init(var1, var2);
      this.symmetric.stream256squeezeBlocks(var3, 0, this.engine.getPolyUniformGamma1NBlocks() * this.symmetric.stream256BlockBytes);
      this.unpackZ(var3);
   }

   private void unpackZ(byte[] var1) {
      if (this.engine.getDilithiumGamma1() == 131072) {
         for (int var2 = 0; var2 < this.dilithiumN / 4; var2++) {
            this.setCoeffIndex(4 * var2 + 0, (var1[9 * var2 + 0] & 255 | (var1[9 * var2 + 1] & 255) << 8 | (var1[9 * var2 + 2] & 255) << 16) & 262143);
            this.setCoeffIndex(4 * var2 + 1, ((var1[9 * var2 + 2] & 255) >> 2 | (var1[9 * var2 + 3] & 255) << 6 | (var1[9 * var2 + 4] & 255) << 14) & 262143);
            this.setCoeffIndex(4 * var2 + 2, ((var1[9 * var2 + 4] & 255) >> 4 | (var1[9 * var2 + 5] & 255) << 4 | (var1[9 * var2 + 6] & 255) << 12) & 262143);
            this.setCoeffIndex(4 * var2 + 3, ((var1[9 * var2 + 6] & 255) >> 6 | (var1[9 * var2 + 7] & 255) << 2 | (var1[9 * var2 + 8] & 255) << 10) & 262143);
            this.setCoeffIndex(4 * var2 + 0, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * var2 + 0));
            this.setCoeffIndex(4 * var2 + 1, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * var2 + 1));
            this.setCoeffIndex(4 * var2 + 2, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * var2 + 2));
            this.setCoeffIndex(4 * var2 + 3, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * var2 + 3));
         }
      } else {
         if (this.engine.getDilithiumGamma1() != 524288) {
            throw new RuntimeException("Wrong Dilithiumn Gamma1!");
         }

         for (int var3 = 0; var3 < this.dilithiumN / 2; var3++) {
            this.setCoeffIndex(2 * var3 + 0, (var1[5 * var3 + 0] & 255 | (var1[5 * var3 + 1] & 255) << 8 | (var1[5 * var3 + 2] & 255) << 16) & 1048575);
            this.setCoeffIndex(2 * var3 + 1, ((var1[5 * var3 + 2] & 255) >> 4 | (var1[5 * var3 + 3] & 255) << 4 | (var1[5 * var3 + 4] & 255) << 12) & 1048575);
            this.setCoeffIndex(2 * var3 + 0, this.engine.getDilithiumGamma1() - this.getCoeffIndex(2 * var3 + 0));
            this.setCoeffIndex(2 * var3 + 1, this.engine.getDilithiumGamma1() - this.getCoeffIndex(2 * var3 + 1));
         }
      }
   }

   public void decompose(Poly var1) {
      for (int var2 = 0; var2 < this.dilithiumN; var2++) {
         int[] var3 = Rounding.decompose(this.getCoeffIndex(var2), this.engine.getDilithiumGamma2());
         this.setCoeffIndex(var2, var3[1]);
         var1.setCoeffIndex(var2, var3[0]);
      }
   }

   public byte[] w1Pack() {
      byte[] var2 = new byte[this.engine.getDilithiumPolyW1PackedBytes()];
      if (this.engine.getDilithiumGamma2() == 95232) {
         for (int var1 = 0; var1 < this.dilithiumN / 4; var1++) {
            var2[3 * var1 + 0] = (byte)((byte)this.getCoeffIndex(4 * var1 + 0) | this.getCoeffIndex(4 * var1 + 1) << 6);
            var2[3 * var1 + 1] = (byte)((byte)(this.getCoeffIndex(4 * var1 + 1) >> 2) | this.getCoeffIndex(4 * var1 + 2) << 4);
            var2[3 * var1 + 2] = (byte)((byte)(this.getCoeffIndex(4 * var1 + 2) >> 4) | this.getCoeffIndex(4 * var1 + 3) << 2);
         }
      } else if (this.engine.getDilithiumGamma2() == 261888) {
         for (int var3 = 0; var3 < this.dilithiumN / 2; var3++) {
            var2[var3] = (byte)(this.getCoeffIndex(2 * var3 + 0) | this.getCoeffIndex(2 * var3 + 1) << 4);
         }
      }

      return var2;
   }

   public void challenge(byte[] var1) {
      int var3 = 0;
      byte[] var7 = new byte[this.symmetric.stream256BlockBytes];
      SHAKEDigest var8 = new SHAKEDigest(256);
      var8.update(var1, 0, this.engine.getDilithiumCTilde());
      var8.doOutput(var7, 0, this.symmetric.stream256BlockBytes);
      long var5 = 0L;

      for (int var2 = 0; var2 < 8; var2++) {
         var5 |= (long)(var7[var2] & 255) << 8 * var2;
      }

      int var4 = 8;

      for (int var9 = 0; var9 < this.dilithiumN; var9++) {
         this.setCoeffIndex(var9, 0);
      }

      for (int var10 = this.dilithiumN - this.engine.getDilithiumTau(); var10 < this.dilithiumN; var10++) {
         do {
            if (var4 >= this.symmetric.stream256BlockBytes) {
               var8.doOutput(var7, 0, this.symmetric.stream256BlockBytes);
               var4 = 0;
            }

            var3 = var7[var4++] & 255;
         } while (var3 > var10);

         this.setCoeffIndex(var10, this.getCoeffIndex(var3));
         this.setCoeffIndex(var3, (int)(1L - 2L * (var5 & 1L)));
         var5 >>= 1;
      }
   }

   public boolean checkNorm(int var1) {
      if (var1 > 1047552) {
         return true;
      } else {
         for (int var2 = 0; var2 < this.dilithiumN; var2++) {
            int var3 = this.getCoeffIndex(var2) >> 31;
            var3 = this.getCoeffIndex(var2) - (var3 & 2 * this.getCoeffIndex(var2));
            if (var3 >= var1) {
               return true;
            }
         }

         return false;
      }
   }

   public void subtract(Poly var1) {
      for (int var2 = 0; var2 < this.dilithiumN; var2++) {
         this.setCoeffIndex(var2, this.getCoeffIndex(var2) - var1.getCoeffIndex(var2));
      }
   }

   public int polyMakeHint(Poly var1, Poly var2) {
      int var4 = 0;

      for (int var3 = 0; var3 < this.dilithiumN; var3++) {
         this.setCoeffIndex(var3, Rounding.makeHint(var1.getCoeffIndex(var3), var2.getCoeffIndex(var3), this.engine));
         var4 += this.getCoeffIndex(var3);
      }

      return var4;
   }

   public void polyUseHint(Poly var1, Poly var2) {
      for (int var3 = 0; var3 < this.dilithiumN; var3++) {
         this.setCoeffIndex(var3, Rounding.useHint(var1.getCoeffIndex(var3), var2.getCoeffIndex(var3), this.engine.getDilithiumGamma2()));
      }
   }

   public byte[] zPack() {
      byte[] var1 = new byte[this.engine.getDilithiumPolyZPackedBytes()];
      int[] var3 = new int[4];
      if (this.engine.getDilithiumGamma1() == 131072) {
         for (int var2 = 0; var2 < this.dilithiumN / 4; var2++) {
            var3[0] = this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * var2 + 0);
            var3[1] = this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * var2 + 1);
            var3[2] = this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * var2 + 2);
            var3[3] = this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * var2 + 3);
            var1[9 * var2 + 0] = (byte)var3[0];
            var1[9 * var2 + 1] = (byte)(var3[0] >> 8);
            var1[9 * var2 + 2] = (byte)((byte)(var3[0] >> 16) | var3[1] << 2);
            var1[9 * var2 + 3] = (byte)(var3[1] >> 6);
            var1[9 * var2 + 4] = (byte)((byte)(var3[1] >> 14) | var3[2] << 4);
            var1[9 * var2 + 5] = (byte)(var3[2] >> 4);
            var1[9 * var2 + 6] = (byte)((byte)(var3[2] >> 12) | var3[3] << 6);
            var1[9 * var2 + 7] = (byte)(var3[3] >> 2);
            var1[9 * var2 + 8] = (byte)(var3[3] >> 10);
         }
      } else {
         if (this.engine.getDilithiumGamma1() != 524288) {
            throw new RuntimeException("Wrong Dilithium Gamma1!");
         }

         for (int var4 = 0; var4 < this.dilithiumN / 2; var4++) {
            var3[0] = this.engine.getDilithiumGamma1() - this.getCoeffIndex(2 * var4 + 0);
            var3[1] = this.engine.getDilithiumGamma1() - this.getCoeffIndex(2 * var4 + 1);
            var1[5 * var4 + 0] = (byte)var3[0];
            var1[5 * var4 + 1] = (byte)(var3[0] >> 8);
            var1[5 * var4 + 2] = (byte)((byte)(var3[0] >> 16) | var3[1] << 4);
            var1[5 * var4 + 3] = (byte)(var3[1] >> 4);
            var1[5 * var4 + 4] = (byte)(var3[1] >> 12);
         }
      }

      return var1;
   }

   void zUnpack(byte[] var1) {
      if (this.engine.getDilithiumGamma1() == 131072) {
         for (int var2 = 0; var2 < this.dilithiumN / 4; var2++) {
            this.setCoeffIndex(4 * var2 + 0, (var1[9 * var2 + 0] & 255 | (var1[9 * var2 + 1] & 255) << 8 | (var1[9 * var2 + 2] & 255) << 16) & 262143);
            this.setCoeffIndex(4 * var2 + 1, ((var1[9 * var2 + 2] & 255) >>> 2 | (var1[9 * var2 + 3] & 255) << 6 | (var1[9 * var2 + 4] & 255) << 14) & 262143);
            this.setCoeffIndex(4 * var2 + 2, ((var1[9 * var2 + 4] & 255) >>> 4 | (var1[9 * var2 + 5] & 255) << 4 | (var1[9 * var2 + 6] & 255) << 12) & 262143);
            this.setCoeffIndex(4 * var2 + 3, ((var1[9 * var2 + 6] & 255) >>> 6 | (var1[9 * var2 + 7] & 255) << 2 | (var1[9 * var2 + 8] & 255) << 10) & 262143);
            this.setCoeffIndex(4 * var2 + 0, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * var2 + 0));
            this.setCoeffIndex(4 * var2 + 1, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * var2 + 1));
            this.setCoeffIndex(4 * var2 + 2, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * var2 + 2));
            this.setCoeffIndex(4 * var2 + 3, this.engine.getDilithiumGamma1() - this.getCoeffIndex(4 * var2 + 3));
         }
      } else {
         if (this.engine.getDilithiumGamma1() != 524288) {
            throw new RuntimeException("Wrong Dilithium Gamma1!");
         }

         for (int var3 = 0; var3 < this.dilithiumN / 2; var3++) {
            this.setCoeffIndex(2 * var3 + 0, (var1[5 * var3 + 0] & 255 | (var1[5 * var3 + 1] & 255) << 8 | (var1[5 * var3 + 2] & 255) << 16) & 1048575);
            this.setCoeffIndex(2 * var3 + 1, ((var1[5 * var3 + 2] & 255) >>> 4 | (var1[5 * var3 + 3] & 255) << 4 | (var1[5 * var3 + 4] & 255) << 12) & 1048575);
            this.setCoeffIndex(2 * var3 + 0, this.engine.getDilithiumGamma1() - this.getCoeffIndex(2 * var3 + 0));
            this.setCoeffIndex(2 * var3 + 1, this.engine.getDilithiumGamma1() - this.getCoeffIndex(2 * var3 + 1));
         }
      }
   }

   public void shiftLeft() {
      for (int var1 = 0; var1 < this.dilithiumN; var1++) {
         this.setCoeffIndex(var1, this.getCoeffIndex(var1) << 13);
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("[");

      for (int var2 = 0; var2 < this.coeffs.length; var2++) {
         var1.append(this.coeffs[var2]);
         if (var2 != this.coeffs.length - 1) {
            var1.append(", ");
         }
      }

      var1.append("]");
      return var1.toString();
   }
}
