package org.bouncycastle.pqc.crypto.snova;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.GF16;

public class SnovaSigner implements MessageSigner {
   private SnovaParameters params;
   private SnovaEngine engine;
   private SecureRandom random;
   private final SHAKEDigest shake = new SHAKEDigest(256);
   private SnovaPublicKeyParameters pubKey;
   private SnovaPrivateKeyParameters privKey;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var1) {
         this.pubKey = null;
         if (var2 instanceof ParametersWithRandom) {
            ParametersWithRandom var3 = (ParametersWithRandom)var2;
            this.privKey = (SnovaPrivateKeyParameters)var3.getParameters();
            this.random = var3.getRandom();
         } else {
            this.privKey = (SnovaPrivateKeyParameters)var2;
            this.random = CryptoServicesRegistrar.getSecureRandom();
         }

         this.params = this.privKey.getParameters();
      } else {
         this.pubKey = (SnovaPublicKeyParameters)var2;
         this.params = this.pubKey.getParameters();
         this.privKey = null;
         this.random = null;
      }

      this.engine = new SnovaEngine(this.params);
   }

   @Override
   public byte[] generateSignature(byte[] var1) {
      byte[] var2 = this.getMessageHash(var1);
      byte[] var3 = new byte[this.params.getSaltLength()];
      this.random.nextBytes(var3);
      byte[] var4 = new byte[(this.params.getN() * this.params.getLsq() + 1 >>> 1) + this.params.getSaltLength()];
      SnovaKeyElements var5 = new SnovaKeyElements(this.params);
      byte[] var6;
      byte[] var7;
      if (this.params.isSkIsSeed()) {
         byte[] var8 = this.privKey.getPrivateKey();
         var6 = Arrays.copyOfRange(var8, 0, 16);
         var7 = Arrays.copyOfRange(var8, 16, var8.length);
         this.engine.genMap1T12Map2(var5, var6, var7);
      } else {
         byte[] var11 = this.privKey.getPrivateKey();
         byte[] var9 = new byte[var11.length - 16 - 32 << 1];
         GF16Utils.decodeMergeInHalf(var11, var9, var9.length);
         int var10 = 0;
         var10 = SnovaKeyElements.copy3d(var9, var10, var5.map1.aAlpha);
         var10 = SnovaKeyElements.copy3d(var9, var10, var5.map1.bAlpha);
         var10 = SnovaKeyElements.copy3d(var9, var10, var5.map1.qAlpha1);
         var10 = SnovaKeyElements.copy3d(var9, var10, var5.map1.qAlpha2);
         var10 = SnovaKeyElements.copy3d(var9, var10, var5.T12);
         var10 = SnovaKeyElements.copy4d(var9, var10, var5.map2.f11);
         var10 = SnovaKeyElements.copy4d(var9, var10, var5.map2.f12);
         SnovaKeyElements.copy4d(var9, var10, var5.map2.f21);
         var6 = Arrays.copyOfRange(var11, var11.length - 16 - 32, var11.length - 32);
         var7 = Arrays.copyOfRange(var11, var11.length - 32, var11.length);
      }

      this.signDigestCore(
         var4,
         var2,
         var3,
         var5.map1.aAlpha,
         var5.map1.bAlpha,
         var5.map1.qAlpha1,
         var5.map1.qAlpha2,
         var5.T12,
         var5.map2.f11,
         var5.map2.f12,
         var5.map2.f21,
         var6,
         var7
      );
      return Arrays.concatenate(var4, var1);
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      byte[] var3 = this.getMessageHash(var1);
      MapGroup1 var4 = new MapGroup1(this.params);
      byte[] var5 = this.pubKey.getEncoded();
      byte[] var6 = Arrays.copyOf(var5, 16);
      byte[] var7 = Arrays.copyOfRange(var5, 16, var5.length);
      this.engine.genABQP(var4, var6);
      byte[][][][] var8 = new byte[this.params.getM()][this.params.getO()][this.params.getO()][this.params.getLsq()];
      if ((this.params.getLsq() & 1) == 0) {
         MapGroup1.decodeP(var7, 0, var8, var7.length << 1);
      } else {
         byte[] var9 = new byte[var7.length << 1];
         GF16.decode(var7, var9, var9.length);
         MapGroup1.fillP(var9, 0, var8, var9.length);
      }

      return this.verifySignatureCore(var3, var2, var6, var4, var8);
   }

   void createSignedHash(byte[] var1, int var2, byte[] var3, int var4, byte[] var5, int var6, int var7, byte[] var8, int var9) {
      this.shake.update(var1, 0, var2);
      this.shake.update(var3, 0, var4);
      this.shake.update(var5, var6, var7);
      this.shake.doFinal(var8, 0, var9);
   }

   void signDigestCore(
      byte[] var1,
      byte[] var2,
      byte[] var3,
      byte[][][] var4,
      byte[][][] var5,
      byte[][][] var6,
      byte[][][] var7,
      byte[][][] var8,
      byte[][][][] var9,
      byte[][][][] var10,
      byte[][][][] var11,
      byte[] var12,
      byte[] var13
   ) {
      int var14 = this.params.getM();
      int var15 = this.params.getL();
      int var16 = this.params.getLsq();
      int var17 = this.params.getAlpha();
      int var18 = this.params.getV();
      int var19 = this.params.getO();
      int var20 = this.params.getN();
      int var21 = var14 * var16;
      int var22 = var19 * var16;
      int var23 = var18 * var16;
      int var24 = var22 + 1 >>> 1;
      byte[][] var25 = new byte[var21][var21 + 1];
      byte[][] var26 = new byte[var16][var16];
      byte[] var27 = new byte[var21];
      byte[][][] var28 = new byte[var17][var18][var16];
      byte[][][] var29 = new byte[var17][var18][var16];
      byte[] var30 = new byte[var16];
      byte[] var31 = new byte[var16];
      byte[] var32 = new byte[var16];
      byte[] var33 = new byte[var21];
      byte[] var34 = new byte[var20 * var16];
      byte[] var35 = new byte[var24];
      byte[] var36 = new byte[var23 + 1 >>> 1];
      byte[] var37 = new byte[var15];
      byte var39 = 0;
      this.createSignedHash(var12, var12.length, var2, var2.length, var3, 0, var3.length, var35, var24);
      GF16.decode(var35, 0, var33, 0, var33.length);

      int var38;
      do {
         for (int var44 = 0; var44 < var25.length; var44++) {
            Arrays.fill(var25[var44], (byte)0);
         }

         var39++;

         for (int var59 = 0; var59 < var21; var59++) {
            var25[var59][var21] = var33[var59];
         }

         this.shake.update(var13, 0, var13.length);
         this.shake.update(var2, 0, var2.length);
         this.shake.update(var3, 0, var3.length);
         this.shake.update(var39);
         this.shake.doFinal(var36, 0, var36.length);
         GF16.decode(var36, var34, var36.length << 1);
         int var60 = 0;

         for (int var45 = 0; var60 < var14; var45 += var16) {
            Arrays.fill(var32, (byte)0);
            int var46 = 0;

            for (int var47 = var60; var46 < var17; var47++) {
               if (var47 >= var19) {
                  var47 -= var19;
               }

               int var48 = 0;

               for (int var49 = 0; var48 < var18; var49 += var16) {
                  GF16Utils.gf16mTranMulMul(
                     var34,
                     var49,
                     var4[var60][var46],
                     var5[var60][var46],
                     var6[var60][var46],
                     var7[var60][var46],
                     var37,
                     var28[var46][var48],
                     var29[var46][var48],
                     var15
                  );
                  var48++;
               }

               for (int var69 = 0; var69 < var18; var69++) {
                  for (int var72 = 0; var72 < var18; var72++) {
                     GF16Utils.gf16mMulMulTo(var28[var46][var69], var9[var47][var69][var72], var29[var46][var72], var37, var32, var15);
                  }
               }

               var46++;
            }

            var46 = 0;

            for (int var66 = 0; var46 < var15; var46++) {
               for (int var70 = 0; var70 < var15; var70++) {
                  var25[var45 + var66][var21] = (byte)(var25[var45 + var66][var21] ^ var32[var66++]);
               }
            }

            var46 = 0;

            for (int var67 = 0; var46 < var19; var67 += var16) {
               int var71 = 0;

               for (int var73 = var60; var71 < var17; var73++) {
                  if (var73 >= var19) {
                     var73 -= var19;
                  }

                  for (int var50 = 0; var50 < var16; var50++) {
                     Arrays.fill(var26[var50], (byte)0);
                  }

                  for (int var74 = 0; var74 < var18; var74++) {
                     GF16Utils.gf16mMulMul(var28[var71][var74], var10[var73][var74][var46], var7[var60][var71], var37, var30, var15);
                     GF16Utils.gf16mMulMul(var6[var60][var71], var11[var73][var46][var74], var29[var71][var74], var37, var31, var15);
                     int var51 = 0;
                     int var52 = 0;

                     for (int var53 = 0; var51 < var16; var52++) {
                        if (var52 == var15) {
                           var52 = 0;
                           var53 += var15;
                        }

                        byte var40 = var30[var53];
                        byte var43 = var31[var52];
                        int var54 = 0;
                        int var55 = 0;
                        int var56 = 0;
                        int var57 = 0;

                        for (int var58 = 0; var54 < var16; var58 += var15) {
                           if (var55 == var15) {
                              var55 = 0;
                              var58 = 0;
                              var56++;
                              var57 += var15;
                              var40 = var30[var53 + var56];
                              var43 = var31[var57 + var52];
                           }

                           byte var41 = var5[var60][var71][var58 + var52];
                           byte var42 = var4[var60][var71][var53 + var55];
                           var26[var51][var54] = (byte)(var26[var51][var54] ^ GF16.mul(var40, var41) ^ GF16.mul(var42, var43));
                           var54++;
                           var55++;
                        }

                        var51++;
                     }
                  }

                  for (int var75 = 0; var75 < var16; var75++) {
                     for (int var76 = 0; var76 < var16; var76++) {
                        var25[var45 + var75][var67 + var76] = (byte)(var25[var45 + var75][var67 + var76] ^ var26[var75][var76]);
                     }
                  }

                  var71++;
               }

               var46++;
            }

            var60++;
         }

         var38 = this.performGaussianElimination(var25, var27, var21);
      } while (var38 != 0);

      int var61 = 0;

      for (int var62 = 0; var61 < var18; var62 += var16) {
         int var65 = 0;

         for (int var68 = 0; var65 < var19; var68 += var16) {
            GF16Utils.gf16mMulTo(var8[var61][var65], var27, var68, var34, var62, var15);
            var65++;
         }

         var61++;
      }

      System.arraycopy(var27, 0, var34, var23, var22);
      GF16.encode(var34, var1, var34.length);
      System.arraycopy(var3, 0, var1, var1.length - 16, 16);
   }

   boolean verifySignatureCore(byte[] var1, byte[] var2, byte[] var3, MapGroup1 var4, byte[][][][] var5) {
      int var6 = this.params.getLsq();
      int var7 = this.params.getO();
      int var8 = var7 * var6;
      int var9 = var8 + 1 >>> 1;
      int var10 = this.params.getSaltLength();
      int var11 = this.params.getM();
      int var12 = this.params.getN();
      int var13 = var12 * var6;
      int var14 = var13 + 1 >>> 1;
      byte[] var15 = new byte[var9];
      this.createSignedHash(var3, var3.length, var1, var1.length, var2, var14, var10, var15, var9);
      if ((var8 & 1) != 0) {
         var15[var9 - 1] = (byte)(var15[var9 - 1] & 15);
      }

      byte[] var16 = new byte[var13];
      GF16.decode(var2, 0, var16, 0, var16.length);
      byte[] var17 = new byte[var11 * var6];
      this.evaluation(var17, var4, var5, var16);
      byte[] var18 = new byte[var9];
      GF16.encode(var17, var18, var17.length);
      return Arrays.areEqual(var15, var18);
   }

   private void evaluation(byte[] var1, MapGroup1 var2, byte[][][][] var3, byte[] var4) {
      int var5 = this.params.getM();
      int var6 = this.params.getAlpha();
      int var7 = this.params.getN();
      int var8 = this.params.getL();
      int var9 = this.params.getLsq();
      int var10 = this.params.getO();
      byte[][][] var11 = new byte[var6][var7][var9];
      byte[][][] var12 = new byte[var6][var7][var9];
      byte[] var13 = new byte[var9];
      int var14 = 0;

      for (int var15 = 0; var14 < var5; var15 += var9) {
         int var16 = 0;

         for (int var17 = 0; var16 < var7; var17 += var9) {
            for (int var18 = 0; var18 < var6; var18++) {
               GF16Utils.gf16mTranMulMul(
                  var4,
                  var17,
                  var2.aAlpha[var14][var18],
                  var2.bAlpha[var14][var18],
                  var2.qAlpha1[var14][var18],
                  var2.qAlpha2[var14][var18],
                  var13,
                  var11[var18][var16],
                  var12[var18][var16],
                  var8
               );
            }

            var16++;
         }

         var16 = 0;

         for (int var22 = var14; var16 < var6; var22++) {
            if (var22 >= var10) {
               var22 -= var10;
            }

            for (int var23 = 0; var23 < var7; var23++) {
               byte[] var19 = this.getPMatrix(var2, var3, var22, var23, 0);
               GF16Utils.gf16mMul(var19, var12[var16][0], var13, var8);

               for (int var20 = 1; var20 < var7; var20++) {
                  var19 = this.getPMatrix(var2, var3, var22, var23, var20);
                  GF16Utils.gf16mMulTo(var19, var12[var16][var20], var13, var8);
               }

               GF16Utils.gf16mMulTo(var11[var16][var23], var13, var1, var15, var8);
            }

            var16++;
         }

         var14++;
      }
   }

   private byte[] getPMatrix(MapGroup1 var1, byte[][][][] var2, int var3, int var4, int var5) {
      int var6 = this.params.getV();
      if (var4 < var6) {
         return var5 < var6 ? var1.p11[var3][var4][var5] : var1.p12[var3][var4][var5 - var6];
      } else {
         return var5 < var6 ? var1.p21[var3][var4 - var6][var5] : var2[var3][var4 - var6][var5 - var6];
      }
   }

   private int performGaussianElimination(byte[][] var1, byte[] var2, int var3) {
      int var4 = var3 + 1;

      for (int var5 = 0; var5 < var3; var5++) {
         int var6 = var5;

         while (var6 < var3 && var1[var6][var5] == 0) {
            var6++;
         }

         if (var6 >= var3) {
            return 1;
         }

         if (var6 != var5) {
            byte[] var7 = var1[var5];
            var1[var5] = var1[var6];
            var1[var6] = var7;
         }

         byte var13 = GF16.inv(var1[var5][var5]);

         for (int var8 = var5; var8 < var4; var8++) {
            var1[var5][var8] = GF16.mul(var1[var5][var8], var13);
         }

         for (int var15 = var5 + 1; var15 < var3; var15++) {
            byte var9 = var1[var15][var5];
            if (var9 != 0) {
               for (int var10 = var5; var10 < var4; var10++) {
                  var1[var15][var10] = (byte)(var1[var15][var10] ^ GF16.mul(var1[var5][var10], var9));
               }
            }
         }
      }

      for (int var11 = var3 - 1; var11 >= 0; var11--) {
         byte var12 = var1[var11][var3];

         for (int var14 = var11 + 1; var14 < var3; var14++) {
            var12 ^= GF16.mul(var1[var11][var14], var2[var14]);
         }

         var2[var11] = var12;
      }

      return 0;
   }

   private byte[] getMessageHash(byte[] var1) {
      byte[] var2 = new byte[this.shake.getDigestSize()];
      this.shake.update(var1, 0, var1.length);
      this.shake.doFinal(var2, 0);
      return var2;
   }
}
