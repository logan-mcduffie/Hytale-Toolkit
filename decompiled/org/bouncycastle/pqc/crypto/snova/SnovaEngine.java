package org.bouncycastle.pqc.crypto.snova;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.modes.CTRModeCipher;
import org.bouncycastle.crypto.modes.SICBlockCipher;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.GF16;
import org.bouncycastle.util.Integers;
import org.bouncycastle.util.Pack;

class SnovaEngine {
   private static final Map<Integer, byte[]> fixedAbqSet = new HashMap<>();
   private static final Map<Integer, byte[][]> sSet = new HashMap<>();
   private static final Map<Integer, int[][]> xSSet = new HashMap<>();
   private final SnovaParameters params;
   private final int l;
   private final int lsq;
   private final int m;
   private final int v;
   private final int o;
   private final int alpha;
   private final int n;
   final byte[][] S;
   final int[][] xS;

   public SnovaEngine(SnovaParameters var1) {
      this.params = var1;
      this.l = var1.getL();
      this.lsq = var1.getLsq();
      this.m = var1.getM();
      this.v = var1.getV();
      this.o = var1.getO();
      this.alpha = var1.getAlpha();
      this.n = var1.getN();
      if (!xSSet.containsKey(Integers.valueOf(this.l))) {
         byte[][] var2 = new byte[this.l][this.lsq];
         int[][] var3 = new int[this.l][this.lsq];
         this.be_aI(var2[0], 0, (byte)1);
         this.beTheS(var2[1]);

         for (int var4 = 2; var4 < this.l; var4++) {
            GF16Utils.gf16mMul(var2[var4 - 1], var2[1], var2[var4], this.l);
         }

         for (int var19 = 0; var19 < this.l; var19++) {
            for (int var5 = 0; var5 < this.lsq; var5++) {
               var3[var19][var5] = GF16Utils.gf16FromNibble(var2[var19][var5]);
            }
         }

         sSet.put(Integers.valueOf(this.l), var2);
         xSSet.put(Integers.valueOf(this.l), var3);
      }

      this.S = sSet.get(Integers.valueOf(this.l));
      this.xS = xSSet.get(Integers.valueOf(this.l));
      if (this.l < 4 && !fixedAbqSet.containsKey(Integers.valueOf(this.o))) {
         int var17 = this.alpha * this.l;
         int var18 = var17 * this.l;
         int var20 = this.o * var17;
         int var21 = this.o * var18;
         byte[] var6 = new byte[var21 << 2];
         byte[] var7 = new byte[var21 + var20];
         byte[] var8 = new byte[var20 << 2];
         byte[] var9 = "SNOVA_ABQ".getBytes();
         SHAKEDigest var10 = new SHAKEDigest(256);
         var10.update(var9, 0, var9.length);
         var10.doFinal(var7, 0, var7.length);
         GF16.decode(var7, var6, var21 << 1);
         GF16.decode(var7, var18, var8, 0, var20 << 1);
         int var11 = 0;
         int var12 = 0;

         for (int var13 = 0; var11 < this.o; var13 += var17) {
            int var14 = 0;
            int var15 = var13;

            for (int var16 = var12; var14 < this.alpha; var16 += this.lsq) {
               this.makeInvertibleByAddingAS(var6, var16);
               this.makeInvertibleByAddingAS(var6, var21 + var16);
               this.genAFqS(var8, var15, var6, (var21 << 1) + var16);
               this.genAFqS(var8, var20 + var15, var6, (var21 << 1) + var21 + var16);
               var14++;
               var15 += this.l;
            }

            var11++;
            var12 += var18;
         }

         fixedAbqSet.put(Integers.valueOf(this.o), var6);
      }
   }

   private void beTheS(byte[] var1) {
      int var2 = 0;
      int var3 = 0;

      while (var2 < this.l) {
         for (int var4 = 0; var4 < this.l; var4++) {
            int var5 = 8 - (var2 + var4);
            var1[var3 + var4] = (byte)(var5 & 15);
         }

         var2++;
         var3 += this.l;
      }

      if (this.l == 5) {
         var1[24] = 9;
      }
   }

   private void be_aI(byte[] var1, int var2, byte var3) {
      int var4 = this.l + 1;
      int var5 = 0;

      while (var5 < this.l) {
         var1[var2] = var3;
         var5++;
         var2 += var4;
      }
   }

   private void genAFqSCT(byte[] var1, int var2, byte[] var3) {
      int[] var4 = new int[this.lsq];
      int var5 = this.l + 1;
      int var6 = GF16Utils.gf16FromNibble(var1[var2]);
      int var7 = 0;

      for (int var8 = 0; var7 < this.l; var8 += var5) {
         var4[var8] = var6;
         var7++;
      }

      for (int var12 = 1; var12 < this.l - 1; var12++) {
         var6 = GF16Utils.gf16FromNibble(var1[var2 + var12]);

         for (int var14 = 0; var14 < this.lsq; var14++) {
            var4[var14] ^= var6 * this.xS[var12][var14];
         }
      }

      var7 = GF16Utils.ctGF16IsNotZero(var1[var2 + this.l - 1]);
      int var15 = var7 * var1[var2 + this.l - 1] + (1 - var7) * (15 + GF16Utils.ctGF16IsNotZero(var1[var2]) - var1[var2]);
      var6 = GF16Utils.gf16FromNibble((byte)var15);

      for (int var9 = 0; var9 < this.lsq; var9++) {
         var4[var9] ^= var6 * this.xS[this.l - 1][var9];
         var3[var9] = GF16Utils.gf16ToNibble(var4[var9]);
      }

      Arrays.fill(var4, 0);
   }

   private void makeInvertibleByAddingAS(byte[] var1, int var2) {
      if (this.gf16Determinant(var1, var2) == 0) {
         for (int var3 = 1; var3 < 16; var3++) {
            this.generateASMatrixTo(var1, var2, (byte)var3);
            if (this.gf16Determinant(var1, var2) != 0) {
               return;
            }
         }
      }
   }

   private byte gf16Determinant(byte[] var1, int var2) {
      switch (this.l) {
         case 2:
            return this.determinant2x2(var1, var2);
         case 3:
            return this.determinant3x3(var1, var2);
         case 4:
            return this.determinant4x4(var1, var2);
         case 5:
            return this.determinant5x5(var1, var2);
         default:
            throw new IllegalStateException();
      }
   }

   private byte determinant2x2(byte[] var1, int var2) {
      return (byte)(GF16.mul(var1[var2], var1[var2 + 3]) ^ GF16.mul(var1[var2 + 1], var1[var2 + 2]));
   }

   private byte determinant3x3(byte[] var1, int var2) {
      byte var3 = var1[var2++];
      byte var4 = var1[var2++];
      byte var5 = var1[var2++];
      byte var6 = var1[var2++];
      byte var7 = var1[var2++];
      byte var8 = var1[var2++];
      byte var9 = var1[var2++];
      byte var10 = var1[var2++];
      byte var11 = var1[var2];
      return (byte)(
         GF16.mul(var3, GF16.mul(var7, var11) ^ GF16.mul(var8, var10))
            ^ GF16.mul(var4, GF16.mul(var6, var11) ^ GF16.mul(var8, var9))
            ^ GF16.mul(var5, GF16.mul(var6, var10) ^ GF16.mul(var7, var9))
      );
   }

   private byte determinant4x4(byte[] var1, int var2) {
      byte var3 = var1[var2++];
      byte var4 = var1[var2++];
      byte var5 = var1[var2++];
      byte var6 = var1[var2++];
      byte var7 = var1[var2++];
      byte var8 = var1[var2++];
      byte var9 = var1[var2++];
      byte var10 = var1[var2++];
      byte var11 = var1[var2++];
      byte var12 = var1[var2++];
      byte var13 = var1[var2++];
      byte var14 = var1[var2++];
      byte var15 = var1[var2++];
      byte var16 = var1[var2++];
      byte var17 = var1[var2++];
      byte var18 = var1[var2];
      byte var19 = (byte)(GF16.mul(var13, var18) ^ GF16.mul(var14, var17));
      byte var20 = (byte)(GF16.mul(var12, var18) ^ GF16.mul(var14, var16));
      byte var21 = (byte)(GF16.mul(var12, var17) ^ GF16.mul(var13, var16));
      byte var22 = (byte)(GF16.mul(var11, var18) ^ GF16.mul(var14, var15));
      byte var23 = (byte)(GF16.mul(var11, var17) ^ GF16.mul(var13, var15));
      byte var24 = (byte)(GF16.mul(var11, var16) ^ GF16.mul(var12, var15));
      return (byte)(
         GF16.mul(var3, GF16.mul(var8, var19) ^ GF16.mul(var9, var20) ^ GF16.mul(var10, var21))
            ^ GF16.mul(var4, GF16.mul(var7, var19) ^ GF16.mul(var9, var22) ^ GF16.mul(var10, var23))
            ^ GF16.mul(var5, GF16.mul(var7, var20) ^ GF16.mul(var8, var22) ^ GF16.mul(var10, var24))
            ^ GF16.mul(var6, GF16.mul(var7, var21) ^ GF16.mul(var8, var23) ^ GF16.mul(var9, var24))
      );
   }

   private byte determinant5x5(byte[] var1, int var2) {
      byte var3 = var1[var2++];
      byte var4 = var1[var2++];
      byte var5 = var1[var2++];
      byte var6 = var1[var2++];
      byte var7 = var1[var2++];
      byte var8 = var1[var2++];
      byte var9 = var1[var2++];
      byte var10 = var1[var2++];
      byte var11 = var1[var2++];
      byte var12 = var1[var2++];
      byte var13 = var1[var2++];
      byte var14 = var1[var2++];
      byte var15 = var1[var2++];
      byte var16 = var1[var2++];
      byte var17 = var1[var2++];
      byte var18 = var1[var2++];
      byte var19 = var1[var2++];
      byte var20 = var1[var2++];
      byte var21 = var1[var2++];
      byte var22 = var1[var2++];
      byte var23 = var1[var2++];
      byte var24 = var1[var2++];
      byte var25 = var1[var2++];
      byte var26 = var1[var2++];
      byte var27 = var1[var2];
      byte var28 = (byte)(GF16.mul(var8, var14) ^ GF16.mul(var9, var13));
      byte var29 = (byte)(GF16.mul(var8, var15) ^ GF16.mul(var10, var13));
      byte var30 = (byte)(GF16.mul(var8, var16) ^ GF16.mul(var11, var13));
      byte var31 = (byte)(GF16.mul(var8, var17) ^ GF16.mul(var12, var13));
      byte var32 = (byte)(GF16.mul(var9, var15) ^ GF16.mul(var10, var14));
      byte var33 = (byte)(GF16.mul(var9, var16) ^ GF16.mul(var11, var14));
      byte var34 = (byte)(GF16.mul(var9, var17) ^ GF16.mul(var12, var14));
      byte var35 = (byte)(GF16.mul(var10, var16) ^ GF16.mul(var11, var15));
      byte var36 = (byte)(GF16.mul(var10, var17) ^ GF16.mul(var12, var15));
      byte var37 = (byte)(GF16.mul(var11, var17) ^ GF16.mul(var12, var16));
      byte var38 = (byte)GF16.mul(GF16.mul(var3, var32) ^ GF16.mul(var4, var29) ^ GF16.mul(var5, var28), GF16.mul(var21, var27) ^ GF16.mul(var22, var26));
      var38 = (byte)(var38 ^ GF16.mul(GF16.mul(var3, var33) ^ GF16.mul(var4, var30) ^ GF16.mul(var6, var28), GF16.mul(var20, var27) ^ GF16.mul(var22, var25)));
      var38 = (byte)(var38 ^ GF16.mul(GF16.mul(var3, var34) ^ GF16.mul(var4, var31) ^ GF16.mul(var7, var28), GF16.mul(var20, var26) ^ GF16.mul(var21, var25)));
      var38 = (byte)(var38 ^ GF16.mul(GF16.mul(var3, var35) ^ GF16.mul(var5, var30) ^ GF16.mul(var6, var29), GF16.mul(var19, var27) ^ GF16.mul(var22, var24)));
      var38 = (byte)(var38 ^ GF16.mul(GF16.mul(var3, var36) ^ GF16.mul(var5, var31) ^ GF16.mul(var7, var29), GF16.mul(var19, var26) ^ GF16.mul(var21, var24)));
      var38 = (byte)(var38 ^ GF16.mul(GF16.mul(var3, var37) ^ GF16.mul(var6, var31) ^ GF16.mul(var7, var30), GF16.mul(var19, var25) ^ GF16.mul(var20, var24)));
      var38 = (byte)(var38 ^ GF16.mul(GF16.mul(var4, var35) ^ GF16.mul(var5, var33) ^ GF16.mul(var6, var32), GF16.mul(var18, var27) ^ GF16.mul(var22, var23)));
      var38 = (byte)(var38 ^ GF16.mul(GF16.mul(var4, var36) ^ GF16.mul(var5, var34) ^ GF16.mul(var7, var32), GF16.mul(var18, var26) ^ GF16.mul(var21, var23)));
      var38 = (byte)(var38 ^ GF16.mul(GF16.mul(var4, var37) ^ GF16.mul(var6, var34) ^ GF16.mul(var7, var33), GF16.mul(var18, var25) ^ GF16.mul(var20, var23)));
      return (byte)(var38 ^ GF16.mul(GF16.mul(var5, var37) ^ GF16.mul(var6, var36) ^ GF16.mul(var7, var35), GF16.mul(var18, var24) ^ GF16.mul(var19, var23)));
   }

   private void generateASMatrixTo(byte[] var1, int var2, byte var3) {
      int var4 = 0;
      int var5 = var2;

      while (var4 < this.l) {
         for (int var6 = 0; var6 < this.l; var6++) {
            byte var7 = (byte)(8 - (var4 + var6));
            if (this.l == 5 && var4 == 4 && var6 == 4) {
               var7 = 9;
            }

            var1[var5 + var6] = (byte)(var1[var5 + var6] ^ GF16.mul(var7, var3));
         }

         var4++;
         var5 += this.l;
      }
   }

   private void genAFqS(byte[] var1, int var2, byte[] var3, int var4) {
      this.be_aI(var3, var4, var1[var2]);

      for (int var5 = 1; var5 < this.l - 1; var5++) {
         this.gf16mScaleTo(this.S[var5], var1[var2 + var5], var3, var4);
      }

      byte var6 = (byte)(var1[var2 + this.l - 1] != 0 ? var1[var2 + this.l - 1] : 16 - (var1[var2] + (var1[var2] == 0 ? 1 : 0)));
      this.gf16mScaleTo(this.S[this.l - 1], var6, var3, var4);
   }

   private void gf16mScaleTo(byte[] var1, byte var2, byte[] var3, int var4) {
      int var5 = 0;
      int var6 = 0;

      while (var5 < this.l) {
         for (int var7 = 0; var7 < this.l; var7++) {
            var3[var6 + var7 + var4] = (byte)(var3[var6 + var7 + var4] ^ GF16.mul(var1[var6 + var7], var2));
         }

         var5++;
         var6 += this.l;
      }
   }

   private void genF(MapGroup2 var1, MapGroup1 var2, byte[][][] var3) {
      copy4DMatrix(var2.p11, var1.f11, this.m, this.v, this.v, this.lsq);
      copy4DMatrix(var2.p12, var1.f12, this.m, this.v, this.o, this.lsq);
      copy4DMatrix(var2.p21, var1.f21, this.m, this.o, this.v, this.lsq);

      for (int var4 = 0; var4 < this.m; var4++) {
         for (int var5 = 0; var5 < this.v; var5++) {
            for (int var6 = 0; var6 < this.o; var6++) {
               for (int var7 = 0; var7 < this.v; var7++) {
                  GF16Utils.gf16mMulToTo(
                     var2.p11[var4][var5][var7], var3[var7][var6], var2.p11[var4][var7][var5], var1.f12[var4][var5][var6], var1.f21[var4][var6][var5], this.l
                  );
               }
            }
         }
      }
   }

   private static void copy4DMatrix(byte[][][][] var0, byte[][][][] var1, int var2, int var3, int var4, int var5) {
      for (int var6 = 0; var6 < var2; var6++) {
         for (int var7 = 0; var7 < var3; var7++) {
            for (int var8 = 0; var8 < var4; var8++) {
               System.arraycopy(var0[var6][var7][var8], 0, var1[var6][var7][var8], 0, var5);
            }
         }
      }
   }

   public void genP22(byte[] var1, int var2, byte[][][] var3, byte[][][][] var4, byte[][][][] var5) {
      int var6 = this.o * this.lsq;
      int var7 = var6 * this.o;
      byte[] var8 = new byte[this.m * var7];
      int var9 = 0;

      for (int var10 = 0; var9 < this.m; var10 += var7) {
         int var11 = 0;

         for (int var12 = var10; var11 < this.o; var12 += var6) {
            int var13 = 0;

            for (int var14 = var12; var13 < this.o; var14 += this.lsq) {
               for (int var15 = 0; var15 < this.v; var15++) {
                  GF16Utils.gf16mMulTo(var3[var15][var11], var5[var9][var15][var13], var4[var9][var11][var15], var3[var15][var13], var8, var14, this.l);
               }

               var13++;
            }

            var11++;
         }

         var9++;
      }

      GF16.encode(var8, var1, var2, var8.length);
   }

   private void genSeedsAndT12(byte[][][] var1, byte[] var2) {
      int var3 = this.v * this.o * this.l;
      int var4 = var3 + 1 >>> 1;
      byte[] var5 = new byte[var4];
      SHAKEDigest var6 = new SHAKEDigest(256);
      var6.update(var2, 0, var2.length);
      var6.doFinal(var5, 0, var5.length);
      byte[] var7 = new byte[var3];
      GF16.decode(var5, var7, var3);
      int var8 = 0;

      for (int var9 = 0; var9 < this.v; var9++) {
         for (int var10 = 0; var10 < this.o; var10++) {
            this.genAFqSCT(var7, var8, var1[var9][var10]);
            var8 += this.l;
         }
      }
   }

   public void genABQP(MapGroup1 var1, byte[] var2) {
      int var3 = this.lsq * (2 * this.m * this.alpha + this.m * (this.n * this.n - this.m * this.m)) + this.l * 2 * this.m * this.alpha;
      byte[] var4 = new byte[this.m * this.alpha * this.l << 1];
      byte[] var5 = new byte[var3 + 1 >> 1];
      if (this.params.isPkExpandShake()) {
         long var6 = 0L;
         int var8 = 0;
         int var9 = var5.length;
         byte[] var10 = new byte[8];

         for (SHAKEDigest var11 = new SHAKEDigest(128); var9 > 0; var6++) {
            var11.update(var2, 0, var2.length);
            Pack.longToLittleEndian(var6, var10, 0);
            var11.update(var10, 0, 8);
            int var12 = Math.min(var9, 168);
            var11.doFinal(var5, var8, var12);
            var8 += var12;
            var9 -= var12;
         }
      } else {
         byte[] var13 = new byte[16];
         CTRModeCipher var7 = SICBlockCipher.newInstance(AESEngine.newInstance());
         var7.init(true, new ParametersWithIV(new KeyParameter(var2), var13));
         int var19 = var7.getBlockSize();
         byte[] var21 = new byte[var19];

         int var23;
         for (var23 = 0; var23 + var19 <= var5.length; var23 += var19) {
            var7.processBlock(var21, 0, var5, var23);
         }

         if (var23 < var5.length) {
            var7.processBlock(var21, 0, var21, 0);
            int var24 = var5.length - var23;
            System.arraycopy(var21, 0, var5, var23, var24);
         }
      }

      if ((this.lsq & 1) == 0) {
         var1.decode(var5, var3 - var4.length >> 1, this.l >= 4);
      } else {
         byte[] var14 = new byte[var3 - var4.length];
         GF16.decode(var5, var14, var14.length);
         var1.fill(var14, this.l >= 4);
      }

      if (this.l >= 4) {
         GF16.decode(var5, var3 - var4.length >> 1, var4, 0, var4.length);
         int var15 = 0;
         int var17 = this.m * this.alpha * this.l;

         for (int var20 = 0; var20 < this.m; var20++) {
            for (int var22 = 0; var22 < this.alpha; var22++) {
               this.makeInvertibleByAddingAS(var1.aAlpha[var20][var22], 0);
               this.makeInvertibleByAddingAS(var1.bAlpha[var20][var22], 0);
               this.genAFqS(var4, var15, var1.qAlpha1[var20][var22], 0);
               this.genAFqS(var4, var17, var1.qAlpha2[var20][var22], 0);
               var15 += this.l;
               var17 += this.l;
            }
         }
      } else {
         int var16 = this.o * this.alpha * this.lsq;
         byte[] var18 = fixedAbqSet.get(Integers.valueOf(this.o));
         MapGroup1.fillAlpha(var18, 0, var1.aAlpha, this.m * var16);
         MapGroup1.fillAlpha(var18, var16, var1.bAlpha, (this.m - 1) * var16);
         MapGroup1.fillAlpha(var18, var16 * 2, var1.qAlpha1, (this.m - 2) * var16);
         MapGroup1.fillAlpha(var18, var16 * 3, var1.qAlpha2, (this.m - 3) * var16);
      }
   }

   public void genMap1T12Map2(SnovaKeyElements var1, byte[] var2, byte[] var3) {
      this.genSeedsAndT12(var1.T12, var3);
      this.genABQP(var1.map1, var2);
      this.genF(var1.map2, var1.map1, var1.T12);
   }
}
