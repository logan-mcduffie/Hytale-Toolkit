package org.bouncycastle.pqc.crypto.saber;

class Poly {
   private static final int KARATSUBA_N = 64;
   private static int SCHB_N = 16;
   private final int N_RES;
   private final int N_SB;
   private final int N_SB_RES;
   private final int SABER_N;
   private final int SABER_L;
   private final SABEREngine engine;
   private final Utils utils;

   public Poly(SABEREngine var1) {
      this.engine = var1;
      this.SABER_L = var1.getSABER_L();
      this.SABER_N = var1.getSABER_N();
      this.N_RES = this.SABER_N << 1;
      this.N_SB = this.SABER_N >> 2;
      this.N_SB_RES = 2 * this.N_SB - 1;
      this.utils = var1.getUtils();
   }

   public void GenMatrix(short[][][] var1, byte[] var2) {
      byte[] var3 = new byte[this.SABER_L * this.engine.getSABER_POLYVECBYTES()];
      this.engine.symmetric.prf(var3, var2, this.engine.getSABER_SEEDBYTES(), var3.length);

      for (int var4 = 0; var4 < this.SABER_L; var4++) {
         this.utils.BS2POLVECq(var3, var4 * this.engine.getSABER_POLYVECBYTES(), var1[var4]);
      }
   }

   public void GenSecret(short[][] var1, byte[] var2) {
      byte[] var3 = new byte[this.SABER_L * this.engine.getSABER_POLYCOINBYTES()];
      this.engine.symmetric.prf(var3, var2, this.engine.getSABER_NOISE_SEEDBYTES(), var3.length);

      for (int var4 = 0; var4 < this.SABER_L; var4++) {
         if (!this.engine.usingEffectiveMasking) {
            this.cbd(var1[var4], var3, var4 * this.engine.getSABER_POLYCOINBYTES());
         } else {
            for (int var5 = 0; var5 < this.SABER_N / 4; var5++) {
               var1[var4][4 * var5] = (short)((var3[var5 + var4 * this.engine.getSABER_POLYCOINBYTES()] & 3 ^ 2) - 2);
               var1[var4][4 * var5 + 1] = (short)((var3[var5 + var4 * this.engine.getSABER_POLYCOINBYTES()] >>> 2 & 3 ^ 2) - 2);
               var1[var4][4 * var5 + 2] = (short)((var3[var5 + var4 * this.engine.getSABER_POLYCOINBYTES()] >>> 4 & 3 ^ 2) - 2);
               var1[var4][4 * var5 + 3] = (short)((var3[var5 + var4 * this.engine.getSABER_POLYCOINBYTES()] >>> 6 & 3 ^ 2) - 2);
            }
         }
      }
   }

   private long load_littleendian(byte[] var1, int var2, int var3) {
      long var5 = var1[var2 + 0] & 255;

      for (int var4 = 1; var4 < var3; var4++) {
         var5 |= (long)(var1[var2 + var4] & 255) << 8 * var4;
      }

      return var5;
   }

   private void cbd(short[] var1, byte[] var2, int var3) {
      int[] var4 = new int[4];
      int[] var5 = new int[4];
      if (this.engine.getSABER_MU() == 6) {
         for (int var6 = 0; var6 < this.SABER_N / 4; var6++) {
            int var8 = (int)this.load_littleendian(var2, var3 + 3 * var6, 3);
            int var9 = 0;

            for (int var7 = 0; var7 < 3; var7++) {
               var9 += var8 >> var7 & 2396745;
            }

            var4[0] = var9 & 7;
            var5[0] = var9 >>> 3 & 7;
            var4[1] = var9 >>> 6 & 7;
            var5[1] = var9 >>> 9 & 7;
            var4[2] = var9 >>> 12 & 7;
            var5[2] = var9 >>> 15 & 7;
            var4[3] = var9 >>> 18 & 7;
            var5[3] = var9 >>> 21;
            var1[4 * var6 + 0] = (short)(var4[0] - var5[0]);
            var1[4 * var6 + 1] = (short)(var4[1] - var5[1]);
            var1[4 * var6 + 2] = (short)(var4[2] - var5[2]);
            var1[4 * var6 + 3] = (short)(var4[3] - var5[3]);
         }
      } else if (this.engine.getSABER_MU() == 8) {
         for (int var12 = 0; var12 < this.SABER_N / 4; var12++) {
            int var16 = (int)this.load_littleendian(var2, var3 + 4 * var12, 4);
            int var18 = 0;

            for (int var14 = 0; var14 < 4; var14++) {
               var18 += var16 >>> var14 & 286331153;
            }

            var4[0] = var18 & 15;
            var5[0] = var18 >>> 4 & 15;
            var4[1] = var18 >>> 8 & 15;
            var5[1] = var18 >>> 12 & 15;
            var4[2] = var18 >>> 16 & 15;
            var5[2] = var18 >>> 20 & 15;
            var4[3] = var18 >>> 24 & 15;
            var5[3] = var18 >>> 28;
            var1[4 * var12 + 0] = (short)(var4[0] - var5[0]);
            var1[4 * var12 + 1] = (short)(var4[1] - var5[1]);
            var1[4 * var12 + 2] = (short)(var4[2] - var5[2]);
            var1[4 * var12 + 3] = (short)(var4[3] - var5[3]);
         }
      } else if (this.engine.getSABER_MU() == 10) {
         for (int var13 = 0; var13 < this.SABER_N / 4; var13++) {
            long var17 = this.load_littleendian(var2, var3 + 5 * var13, 5);
            long var10 = 0L;

            for (int var15 = 0; var15 < 5; var15++) {
               var10 += var17 >>> var15 & 35468117025L;
            }

            var4[0] = (int)(var10 & 31L);
            var5[0] = (int)(var10 >>> 5 & 31L);
            var4[1] = (int)(var10 >>> 10 & 31L);
            var5[1] = (int)(var10 >>> 15 & 31L);
            var4[2] = (int)(var10 >>> 20 & 31L);
            var5[2] = (int)(var10 >>> 25 & 31L);
            var4[3] = (int)(var10 >>> 30 & 31L);
            var5[3] = (int)(var10 >>> 35);
            var1[4 * var13 + 0] = (short)(var4[0] - var5[0]);
            var1[4 * var13 + 1] = (short)(var4[1] - var5[1]);
            var1[4 * var13 + 2] = (short)(var4[2] - var5[2]);
            var1[4 * var13 + 3] = (short)(var4[3] - var5[3]);
         }
      }
   }

   private short OVERFLOWING_MUL(int var1, int var2) {
      return (short)(var1 * var2);
   }

   private void karatsuba_simple(int[] var1, int[] var2, int[] var3) {
      int[] var4 = new int[31];
      int[] var5 = new int[31];
      int[] var6 = new int[31];
      int[] var7 = new int[63];

      for (int var8 = 0; var8 < 16; var8++) {
         int var10 = var1[var8];
         int var11 = var1[var8 + 16];
         int var12 = var1[var8 + 32];
         int var13 = var1[var8 + 48];

         for (int var9 = 0; var9 < 16; var9++) {
            int var14 = var2[var9];
            int var15 = var2[var9 + 16];
            var3[var8 + var9 + 0] = var3[var8 + var9 + 0] + this.OVERFLOWING_MUL(var10, var14);
            var3[var8 + var9 + 32] = var3[var8 + var9 + 32] + this.OVERFLOWING_MUL(var11, var15);
            int var16 = var14 + var15;
            int var17 = var10 + var11;
            var4[var8 + var9] = (int)(var4[var8 + var9] + (long)var16 * var17);
            var16 = var2[var9 + 32];
            var17 = var2[var9 + 48];
            var3[var8 + var9 + 64] = var3[var8 + var9 + 64] + this.OVERFLOWING_MUL(var16, var12);
            var3[var8 + var9 + 96] = var3[var8 + var9 + 96] + this.OVERFLOWING_MUL(var17, var13);
            int var18 = var12 + var13;
            int var19 = var16 + var17;
            var6[var8 + var9] = var6[var8 + var9] + this.OVERFLOWING_MUL(var18, var19);
            var14 += var16;
            var16 = var10 + var12;
            var7[var8 + var9 + 0] = var7[var8 + var9 + 0] + this.OVERFLOWING_MUL(var14, var16);
            var15 += var17;
            var17 = var11 + var13;
            var7[var8 + var9 + 32] = var7[var8 + var9 + 32] + this.OVERFLOWING_MUL(var15, var17);
            var14 += var15;
            var16 += var17;
            var5[var8 + var9] = var5[var8 + var9] + this.OVERFLOWING_MUL(var14, var16);
         }
      }

      for (int var20 = 0; var20 < 31; var20++) {
         var5[var20] = var5[var20] - var7[var20 + 0] - var7[var20 + 32];
         var4[var20] = var4[var20] - var3[var20 + 0] - var3[var20 + 32];
         var6[var20] = var6[var20] - var3[var20 + 64] - var3[var20 + 96];
      }

      for (int var21 = 0; var21 < 31; var21++) {
         var7[var21 + 16] = var7[var21 + 16] + var5[var21];
         var3[var21 + 16] = var3[var21 + 16] + var4[var21];
         var3[var21 + 80] = var3[var21 + 80] + var6[var21];
      }

      for (int var22 = 0; var22 < 63; var22++) {
         var7[var22] = var7[var22] - var3[var22] - var3[var22 + 64];
      }

      for (int var23 = 0; var23 < 63; var23++) {
         var3[var23 + 32] = var3[var23 + 32] + var7[var23];
      }
   }

   private void toom_cook_4way(short[] var1, short[] var2, short[] var3) {
      char var4 = 'ꪫ';
      char var5 = '踹';
      char var6 = '\ueeef';
      int[] var7 = new int[this.N_SB];
      int[] var8 = new int[this.N_SB];
      int[] var9 = new int[this.N_SB];
      int[] var10 = new int[this.N_SB];
      int[] var11 = new int[this.N_SB];
      int[] var12 = new int[this.N_SB];
      int[] var13 = new int[this.N_SB];
      int[] var14 = new int[this.N_SB];
      int[] var15 = new int[this.N_SB];
      int[] var16 = new int[this.N_SB];
      int[] var17 = new int[this.N_SB];
      int[] var18 = new int[this.N_SB];
      int[] var19 = new int[this.N_SB];
      int[] var20 = new int[this.N_SB];
      int[] var21 = new int[this.N_SB_RES];
      int[] var22 = new int[this.N_SB_RES];
      int[] var23 = new int[this.N_SB_RES];
      int[] var24 = new int[this.N_SB_RES];
      int[] var25 = new int[this.N_SB_RES];
      int[] var26 = new int[this.N_SB_RES];
      int[] var27 = new int[this.N_SB_RES];
      short[] var36 = var3;

      for (int var38 = 0; var38 < this.N_SB; var38++) {
         short var28 = var1[var38];
         short var29 = var1[var38 + this.N_SB];
         short var30 = var1[var38 + this.N_SB * 2];
         short var31 = var1[var38 + this.N_SB * 3];
         short var32 = (short)(var28 + var30);
         short var33 = (short)(var29 + var31);
         short var34 = (short)(var32 + var33);
         short var35 = (short)(var32 - var33);
         var9[var38] = var34;
         var10[var38] = var35;
         var32 = (short)((var28 << 2) + var30 << 1);
         var33 = (short)((var29 << 2) + var31);
         var34 = (short)(var32 + var33);
         var35 = (short)(var32 - var33);
         var11[var38] = var34;
         var12[var38] = var35;
         var32 = (short)((var31 << 3) + (var30 << 2) + (var29 << 1) + var28);
         var8[var38] = var32;
         var13[var38] = var28;
         var7[var38] = var31;
      }

      for (int var82 = 0; var82 < this.N_SB; var82++) {
         short var39 = var2[var82];
         short var41 = var2[var82 + this.N_SB];
         short var48 = var2[var82 + this.N_SB * 2];
         short var54 = var2[var82 + this.N_SB * 3];
         int var60 = var39 + var48;
         int var69 = var41 + var54;
         int var76 = var60 + var69;
         int var80 = var60 - var69;
         var16[var82] = var76;
         var17[var82] = var80;
         var60 = (var39 << 2) + var48 << 1;
         var69 = (var41 << 2) + var54;
         var76 = var60 + var69;
         var80 = var60 - var69;
         var18[var82] = var76;
         var19[var82] = var80;
         var60 = (var54 << 3) + (var48 << 2) + (var41 << 1) + var39;
         var15[var82] = var60;
         var20[var82] = var39;
         var14[var82] = var54;
      }

      this.karatsuba_simple(var7, var14, var21);
      this.karatsuba_simple(var8, var15, var22);
      this.karatsuba_simple(var9, var16, var23);
      this.karatsuba_simple(var10, var17, var24);
      this.karatsuba_simple(var11, var18, var25);
      this.karatsuba_simple(var12, var19, var26);
      this.karatsuba_simple(var13, var20, var27);

      for (int var37 = 0; var37 < this.N_SB_RES; var37++) {
         int var40 = var21[var37];
         int var42 = var22[var37];
         int var49 = var23[var37];
         int var55 = var24[var37];
         int var63 = var25[var37];
         int var71 = var26[var37];
         int var78 = var27[var37];
         var42 += var63;
         var71 -= var63;
         var55 = (var55 & 65535) - (var49 & 65535) >>> 1;
         var63 -= var40;
         var63 -= var78 << 6;
         var63 = (var63 << 1) + var71;
         var49 += var55;
         var42 = var42 - (var49 << 6) - var49;
         var49 -= var78;
         var49 -= var40;
         var42 += 45 * var49;
         var63 = ((var63 & 65535) - (var49 << 3)) * var4 >> 3;
         var71 += var42;
         var42 = ((var42 & 65535) + ((var55 & 65535) << 4)) * var5 >> 1;
         var55 = -(var55 + var42);
         var71 = (30 * (var42 & 65535) - (var71 & 65535)) * var6 >> 2;
         var49 -= var63;
         var42 -= var71;
         var36[var37] = (short)(var36[var37] + (var78 & 65535));
         var36[var37 + 64] = (short)(var36[var37 + 64] + (var71 & 65535));
         var36[var37 + 128] = (short)(var36[var37 + 128] + (var63 & 65535));
         var36[var37 + 192] = (short)(var36[var37 + 192] + (var55 & 65535));
         var36[var37 + 256] = (short)(var36[var37 + 256] + (var49 & 65535));
         var36[var37 + 320] = (short)(var36[var37 + 320] + (var42 & 65535));
         var36[var37 + 384] = (short)(var36[var37 + 384] + (var40 & 65535));
      }
   }

   private void poly_mul_acc(short[] var1, short[] var2, short[] var3) {
      short[] var5 = new short[2 * this.SABER_N];
      this.toom_cook_4way(var1, var2, var5);

      for (int var4 = this.SABER_N; var4 < 2 * this.SABER_N; var4++) {
         var3[var4 - this.SABER_N] = (short)(var3[var4 - this.SABER_N] + (var5[var4 - this.SABER_N] - var5[var4]));
      }
   }

   public void MatrixVectorMul(short[][][] var1, short[][] var2, short[][] var3, int var4) {
      for (int var5 = 0; var5 < this.SABER_L; var5++) {
         for (int var6 = 0; var6 < this.SABER_L; var6++) {
            if (var4 == 1) {
               this.poly_mul_acc(var1[var6][var5], var2[var6], var3[var5]);
            } else {
               this.poly_mul_acc(var1[var5][var6], var2[var6], var3[var5]);
            }
         }
      }
   }

   public void InnerProd(short[][] var1, short[][] var2, short[] var3) {
      for (int var4 = 0; var4 < this.SABER_L; var4++) {
         this.poly_mul_acc(var1[var4], var2[var4], var3);
      }
   }
}
