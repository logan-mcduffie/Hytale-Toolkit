package org.bouncycastle.pqc.crypto.mlkem;

class Poly {
   private short[] coeffs = new short[256];
   private MLKEMEngine engine;
   private int polyCompressedBytes;
   private int eta1;
   private int eta2;
   private Symmetric symmetric;

   public Poly(MLKEMEngine var1) {
      this.engine = var1;
      this.polyCompressedBytes = var1.getKyberPolyCompressedBytes();
      this.eta1 = var1.getKyberEta1();
      this.eta2 = MLKEMEngine.getKyberEta2();
      this.symmetric = var1.getSymmetric();
   }

   public short getCoeffIndex(int var1) {
      return this.coeffs[var1];
   }

   public short[] getCoeffs() {
      return this.coeffs;
   }

   public void setCoeffIndex(int var1, short var2) {
      this.coeffs[var1] = var2;
   }

   public void setCoeffs(short[] var1) {
      this.coeffs = var1;
   }

   public void polyNtt() {
      this.setCoeffs(Ntt.ntt(this.getCoeffs()));
      this.reduce();
   }

   public void polyInverseNttToMont() {
      this.setCoeffs(Ntt.invNtt(this.getCoeffs()));
   }

   public void reduce() {
      for (int var1 = 0; var1 < 256; var1++) {
         this.setCoeffIndex(var1, Reduce.barretReduce(this.getCoeffIndex(var1)));
      }
   }

   public static void baseMultMontgomery(Poly var0, Poly var1, Poly var2) {
      for (int var3 = 0; var3 < 64; var3++) {
         Ntt.baseMult(
            var0,
            4 * var3,
            var1.getCoeffIndex(4 * var3),
            var1.getCoeffIndex(4 * var3 + 1),
            var2.getCoeffIndex(4 * var3),
            var2.getCoeffIndex(4 * var3 + 1),
            Ntt.nttZetas[64 + var3]
         );
         Ntt.baseMult(
            var0,
            4 * var3 + 2,
            var1.getCoeffIndex(4 * var3 + 2),
            var1.getCoeffIndex(4 * var3 + 3),
            var2.getCoeffIndex(4 * var3 + 2),
            var2.getCoeffIndex(4 * var3 + 3),
            (short)(-1 * Ntt.nttZetas[64 + var3])
         );
      }
   }

   public void addCoeffs(Poly var1) {
      for (int var2 = 0; var2 < 256; var2++) {
         this.setCoeffIndex(var2, (short)(this.getCoeffIndex(var2) + var1.getCoeffIndex(var2)));
      }
   }

   public void convertToMont() {
      for (int var1 = 0; var1 < 256; var1++) {
         this.setCoeffIndex(var1, Reduce.montgomeryReduce(this.getCoeffIndex(var1) * 1353));
      }
   }

   public byte[] compressPoly() {
      byte[] var3 = new byte[8];
      byte[] var4 = new byte[this.polyCompressedBytes];
      byte var5 = 0;
      this.conditionalSubQ();
      if (this.polyCompressedBytes == 128) {
         for (int var1 = 0; var1 < 32; var1++) {
            for (int var2 = 0; var2 < 8; var2++) {
               int var6 = this.getCoeffIndex(8 * var1 + var2);
               var6 <<= 4;
               var6 += 1665;
               var6 *= 80635;
               var6 >>= 28;
               var6 &= 15;
               var3[var2] = (byte)var6;
            }

            var4[var5 + 0] = (byte)(var3[0] | var3[1] << 4);
            var4[var5 + 1] = (byte)(var3[2] | var3[3] << 4);
            var4[var5 + 2] = (byte)(var3[4] | var3[5] << 4);
            var4[var5 + 3] = (byte)(var3[6] | var3[7] << 4);
            var5 += 4;
         }
      } else {
         if (this.polyCompressedBytes != 160) {
            throw new RuntimeException("PolyCompressedBytes is neither 128 or 160!");
         }

         for (int var7 = 0; var7 < 32; var7++) {
            for (int var8 = 0; var8 < 8; var8++) {
               int var14 = this.getCoeffIndex(8 * var7 + var8);
               var14 <<= 5;
               var14 += 1664;
               var14 *= 40318;
               var14 >>= 27;
               var14 &= 31;
               var3[var8] = (byte)var14;
            }

            var4[var5 + 0] = (byte)(var3[0] >> 0 | var3[1] << 5);
            var4[var5 + 1] = (byte)(var3[1] >> 3 | var3[2] << 2 | var3[3] << 7);
            var4[var5 + 2] = (byte)(var3[3] >> 1 | var3[4] << 4);
            var4[var5 + 3] = (byte)(var3[4] >> 4 | var3[5] << 1 | var3[6] << 6);
            var4[var5 + 4] = (byte)(var3[6] >> 2 | var3[7] << 3);
            var5 += 5;
         }
      }

      return var4;
   }

   public void decompressPoly(byte[] var1) {
      int var3 = 0;
      if (this.engine.getKyberPolyCompressedBytes() == 128) {
         for (int var2 = 0; var2 < 128; var2++) {
            this.setCoeffIndex(2 * var2 + 0, (short)((short)(var1[var3] & 255 & 15) * 3329 + 8 >> 4));
            this.setCoeffIndex(2 * var2 + 1, (short)((short)((var1[var3] & 255) >> 4) * 3329 + 8 >> 4));
            var3++;
         }
      } else {
         if (this.engine.getKyberPolyCompressedBytes() != 160) {
            throw new RuntimeException("PolyCompressedBytes is neither 128 or 160!");
         }

         byte[] var5 = new byte[8];

         for (int var6 = 0; var6 < 32; var6++) {
            var5[0] = (byte)((var1[var3 + 0] & 255) >> 0);
            var5[1] = (byte)((var1[var3 + 0] & 255) >> 5 | (var1[var3 + 1] & 255) << 3);
            var5[2] = (byte)((var1[var3 + 1] & 255) >> 2);
            var5[3] = (byte)((var1[var3 + 1] & 255) >> 7 | (var1[var3 + 2] & 255) << 1);
            var5[4] = (byte)((var1[var3 + 2] & 255) >> 4 | (var1[var3 + 3] & 255) << 4);
            var5[5] = (byte)((var1[var3 + 3] & 255) >> 1);
            var5[6] = (byte)((var1[var3 + 3] & 255) >> 6 | (var1[var3 + 4] & 255) << 2);
            var5[7] = (byte)((var1[var3 + 4] & 255) >> 3);
            var3 += 5;

            for (int var4 = 0; var4 < 8; var4++) {
               this.setCoeffIndex(8 * var6 + var4, (short)((var5[var4] & 31) * 3329 + 16 >> 5));
            }
         }
      }
   }

   public byte[] toBytes() {
      this.conditionalSubQ();
      byte[] var1 = new byte[384];

      for (int var2 = 0; var2 < 128; var2++) {
         short var3 = this.coeffs[2 * var2 + 0];
         short var4 = this.coeffs[2 * var2 + 1];
         var1[3 * var2 + 0] = (byte)(var3 >> 0);
         var1[3 * var2 + 1] = (byte)(var3 >> 8 | var4 << 4);
         var1[3 * var2 + 2] = (byte)(var4 >> 4);
      }

      return var1;
   }

   public void fromBytes(byte[] var1) {
      for (int var2 = 0; var2 < 128; var2++) {
         int var3 = var1[3 * var2 + 0] & 255;
         int var4 = var1[3 * var2 + 1] & 255;
         int var5 = var1[3 * var2 + 2] & 255;
         this.coeffs[2 * var2 + 0] = (short)((var3 >> 0 | var4 << 8) & 4095);
         this.coeffs[2 * var2 + 1] = (short)((var4 >> 4 | var5 << 4) & 4095);
      }
   }

   public byte[] toMsg() {
      short var1 = 832;
      int var2 = 3329 - var1;
      byte[] var3 = new byte[MLKEMEngine.getKyberIndCpaMsgBytes()];
      this.conditionalSubQ();

      for (int var4 = 0; var4 < 32; var4++) {
         var3[var4] = 0;

         for (int var5 = 0; var5 < 8; var5++) {
            short var6 = this.getCoeffIndex(8 * var4 + var5);
            int var7 = (var1 - var6 & var6 - var2) >>> 31;
            var3[var4] |= (byte)(var7 << var5);
         }
      }

      return var3;
   }

   public void fromMsg(byte[] var1) {
      if (var1.length != 32) {
         throw new RuntimeException("KYBER_INDCPA_MSGBYTES must be equal to KYBER_N/8 bytes!");
      } else {
         for (int var2 = 0; var2 < 32; var2++) {
            for (int var3 = 0; var3 < 8; var3++) {
               short var4 = (short)(-1 * (short)((var1[var2] & 255) >> var3 & 1));
               this.setCoeffIndex(8 * var2 + var3, (short)(var4 & 1665));
            }
         }
      }
   }

   public void conditionalSubQ() {
      for (int var1 = 0; var1 < 256; var1++) {
         this.setCoeffIndex(var1, Reduce.conditionalSubQ(this.getCoeffIndex(var1)));
      }
   }

   public void getEta1Noise(byte[] var1, byte var2) {
      byte[] var3 = new byte[256 * this.eta1 / 4];
      this.symmetric.prf(var3, var1, var2);
      CBD.mlkemCBD(this, var3, this.eta1);
   }

   public void getEta2Noise(byte[] var1, byte var2) {
      byte[] var3 = new byte[256 * this.eta2 / 4];
      this.symmetric.prf(var3, var1, var2);
      CBD.mlkemCBD(this, var3, this.eta2);
   }

   public void polySubtract(Poly var1) {
      for (int var2 = 0; var2 < 256; var2++) {
         this.setCoeffIndex(var2, (short)(var1.getCoeffIndex(var2) - this.getCoeffIndex(var2)));
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

   static int checkModulus(byte[] var0, int var1) {
      int var2 = -1;

      for (int var3 = 0; var3 < 128; var3++) {
         int var4 = var0[var1 + 3 * var3 + 0] & 255;
         int var5 = var0[var1 + 3 * var3 + 1] & 255;
         int var6 = var0[var1 + 3 * var3 + 2] & 255;
         short var7 = (short)((var4 >> 0 | var5 << 8) & 4095);
         short var8 = (short)((var5 >> 4 | var6 << 4) & 4095);
         var2 &= Reduce.checkModulus(var7);
         var2 &= Reduce.checkModulus(var8);
      }

      return var2;
   }
}
