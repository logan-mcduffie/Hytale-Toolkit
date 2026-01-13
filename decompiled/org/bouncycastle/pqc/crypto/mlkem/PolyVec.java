package org.bouncycastle.pqc.crypto.mlkem;

import org.bouncycastle.util.Arrays;

class PolyVec {
   Poly[] vec;
   private MLKEMEngine engine;
   private int kyberK;
   private int polyVecBytes;

   public PolyVec(MLKEMEngine var1) {
      this.engine = var1;
      this.kyberK = var1.getKyberK();
      this.polyVecBytes = var1.getKyberPolyVecBytes();
      this.vec = new Poly[this.kyberK];

      for (int var2 = 0; var2 < this.kyberK; var2++) {
         this.vec[var2] = new Poly(var1);
      }
   }

   public PolyVec() throws Exception {
      throw new Exception("Requires Parameter");
   }

   public Poly getVectorIndex(int var1) {
      return this.vec[var1];
   }

   public void polyVecNtt() {
      for (int var1 = 0; var1 < this.kyberK; var1++) {
         this.getVectorIndex(var1).polyNtt();
      }
   }

   public void polyVecInverseNttToMont() {
      for (int var1 = 0; var1 < this.kyberK; var1++) {
         this.getVectorIndex(var1).polyInverseNttToMont();
      }
   }

   public byte[] compressPolyVec() {
      this.conditionalSubQ();
      byte[] var5 = new byte[this.engine.getKyberPolyVecCompressedBytes()];
      byte var6 = 0;
      if (this.engine.getKyberPolyVecCompressedBytes() == this.kyberK * 320) {
         short[] var4 = new short[4];

         for (int var1 = 0; var1 < this.kyberK; var1++) {
            for (int var2 = 0; var2 < 64; var2++) {
               for (int var3 = 0; var3 < 4; var3++) {
                  long var7 = this.getVectorIndex(var1).getCoeffIndex(4 * var2 + var3);
                  var7 <<= 10;
                  var7 += 1665L;
                  var7 *= 1290167L;
                  var7 >>= 32;
                  var7 &= 1023L;
                  var4[var3] = (short)var7;
               }

               var5[var6 + 0] = (byte)(var4[0] >> 0);
               var5[var6 + 1] = (byte)(var4[0] >> 8 | var4[1] << 2);
               var5[var6 + 2] = (byte)(var4[1] >> 6 | var4[2] << 4);
               var5[var6 + 3] = (byte)(var4[2] >> 4 | var4[3] << 6);
               var5[var6 + 4] = (byte)(var4[3] >> 2);
               var6 += 5;
            }
         }
      } else {
         if (this.engine.getKyberPolyVecCompressedBytes() != this.kyberK * 352) {
            throw new RuntimeException("Kyber PolyVecCompressedBytes neither 320 * KyberK or 352 * KyberK!");
         }

         short[] var12 = new short[8];

         for (int var9 = 0; var9 < this.kyberK; var9++) {
            for (int var10 = 0; var10 < 32; var10++) {
               for (int var11 = 0; var11 < 8; var11++) {
                  long var18 = this.getVectorIndex(var9).getCoeffIndex(8 * var10 + var11);
                  var18 <<= 11;
                  var18 += 1664L;
                  var18 *= 645084L;
                  var18 >>= 31;
                  var18 &= 2047L;
                  var12[var11] = (short)var18;
               }

               var5[var6 + 0] = (byte)(var12[0] >> 0);
               var5[var6 + 1] = (byte)(var12[0] >> 8 | var12[1] << 3);
               var5[var6 + 2] = (byte)(var12[1] >> 5 | var12[2] << 6);
               var5[var6 + 3] = (byte)(var12[2] >> 2);
               var5[var6 + 4] = (byte)(var12[2] >> 10 | var12[3] << 1);
               var5[var6 + 5] = (byte)(var12[3] >> 7 | var12[4] << 4);
               var5[var6 + 6] = (byte)(var12[4] >> 4 | var12[5] << 7);
               var5[var6 + 7] = (byte)(var12[5] >> 1);
               var5[var6 + 8] = (byte)(var12[5] >> 9 | var12[6] << 2);
               var5[var6 + 9] = (byte)(var12[6] >> 6 | var12[7] << 5);
               var5[var6 + 10] = (byte)(var12[7] >> 3);
               var6 += 11;
            }
         }
      }

      return var5;
   }

   public void decompressPolyVec(byte[] var1) {
      byte var5 = 0;
      if (this.engine.getKyberPolyVecCompressedBytes() == this.kyberK * 320) {
         short[] var6 = new short[4];

         for (int var2 = 0; var2 < this.kyberK; var2++) {
            for (int var3 = 0; var3 < 64; var3++) {
               var6[0] = (short)((var1[var5] & 255) >> 0 | (short)((var1[var5 + 1] & 255) << 8));
               var6[1] = (short)((var1[var5 + 1] & 255) >> 2 | (short)((var1[var5 + 2] & 255) << 6));
               var6[2] = (short)((var1[var5 + 2] & 255) >> 4 | (short)((var1[var5 + 3] & 255) << 4));
               var6[3] = (short)((var1[var5 + 3] & 255) >> 6 | (short)((var1[var5 + 4] & 255) << 2));
               var5 += 5;

               for (int var4 = 0; var4 < 4; var4++) {
                  this.vec[var2].setCoeffIndex(4 * var3 + var4, (short)((var6[var4] & 1023) * 3329 + 512 >> 10));
               }
            }
         }
      } else {
         if (this.engine.getKyberPolyVecCompressedBytes() != this.kyberK * 352) {
            throw new RuntimeException("Kyber PolyVecCompressedBytes neither 320 * KyberK or 352 * KyberK!");
         }

         short[] var10 = new short[8];

         for (int var7 = 0; var7 < this.kyberK; var7++) {
            for (int var8 = 0; var8 < 32; var8++) {
               var10[0] = (short)((var1[var5] & 255) >> 0 | (short)(var1[var5 + 1] & 255) << 8);
               var10[1] = (short)((var1[var5 + 1] & 255) >> 3 | (short)(var1[var5 + 2] & 255) << 5);
               var10[2] = (short)((var1[var5 + 2] & 255) >> 6 | (short)(var1[var5 + 3] & 255) << 2 | (short)((var1[var5 + 4] & 255) << 10));
               var10[3] = (short)((var1[var5 + 4] & 255) >> 1 | (short)(var1[var5 + 5] & 255) << 7);
               var10[4] = (short)((var1[var5 + 5] & 255) >> 4 | (short)(var1[var5 + 6] & 255) << 4);
               var10[5] = (short)((var1[var5 + 6] & 255) >> 7 | (short)(var1[var5 + 7] & 255) << 1 | (short)((var1[var5 + 8] & 255) << 9));
               var10[6] = (short)((var1[var5 + 8] & 255) >> 2 | (short)(var1[var5 + 9] & 255) << 6);
               var10[7] = (short)((var1[var5 + 9] & 255) >> 5 | (short)(var1[var5 + 10] & 255) << 3);
               var5 += 11;

               for (int var9 = 0; var9 < 8; var9++) {
                  this.vec[var7].setCoeffIndex(8 * var8 + var9, (short)((var10[var9] & 2047) * 3329 + 1024 >> 11));
               }
            }
         }
      }
   }

   public static void pointwiseAccountMontgomery(Poly var0, PolyVec var1, PolyVec var2, MLKEMEngine var3) {
      Poly var5 = new Poly(var3);
      Poly.baseMultMontgomery(var0, var1.getVectorIndex(0), var2.getVectorIndex(0));

      for (int var4 = 1; var4 < var3.getKyberK(); var4++) {
         Poly.baseMultMontgomery(var5, var1.getVectorIndex(var4), var2.getVectorIndex(var4));
         var0.addCoeffs(var5);
      }

      var0.reduce();
   }

   public void reducePoly() {
      for (int var1 = 0; var1 < this.kyberK; var1++) {
         this.getVectorIndex(var1).reduce();
      }
   }

   public void addPoly(PolyVec var1) {
      for (int var2 = 0; var2 < this.kyberK; var2++) {
         this.getVectorIndex(var2).addCoeffs(var1.getVectorIndex(var2));
      }
   }

   public byte[] toBytes() {
      byte[] var1 = new byte[this.polyVecBytes];

      for (int var2 = 0; var2 < this.kyberK; var2++) {
         System.arraycopy(this.vec[var2].toBytes(), 0, var1, var2 * 384, 384);
      }

      return var1;
   }

   public void fromBytes(byte[] var1) {
      for (int var2 = 0; var2 < this.kyberK; var2++) {
         this.getVectorIndex(var2).fromBytes(Arrays.copyOfRange(var1, var2 * 384, (var2 + 1) * 384));
      }
   }

   public void conditionalSubQ() {
      for (int var1 = 0; var1 < this.kyberK; var1++) {
         this.getVectorIndex(var1).conditionalSubQ();
      }
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("[");

      for (int var2 = 0; var2 < this.kyberK; var2++) {
         var1.append(this.vec[var2].toString());
         if (var2 != this.kyberK - 1) {
            var1.append(", ");
         }
      }

      var1.append("]");
      return var1.toString();
   }

   static int checkModulus(MLKEMEngine var0, byte[] var1) {
      int var2 = -1;
      int var3 = 0;

      for (int var4 = var0.getKyberK(); var3 < var4; var3++) {
         var2 &= Poly.checkModulus(var1, var3 * 384);
      }

      return var2;
   }
}
