package org.bouncycastle.pqc.crypto.mayo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Bytes;
import org.bouncycastle.util.GF16;
import org.bouncycastle.util.Longs;
import org.bouncycastle.util.Pack;

public class MayoSigner implements MessageSigner {
   private SecureRandom random;
   private MayoParameters params;
   private MayoPublicKeyParameters pubKey;
   private MayoPrivateKeyParameters privKey;
   private static final int F_TAIL_LEN = 4;
   private static final long EVEN_BYTES = 71777214294589695L;
   private static final long EVEN_2BYTES = 281470681808895L;

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var1) {
         this.pubKey = null;
         if (var2 instanceof ParametersWithRandom) {
            ParametersWithRandom var3 = (ParametersWithRandom)var2;
            this.privKey = (MayoPrivateKeyParameters)var3.getParameters();
            this.random = var3.getRandom();
         } else {
            this.privKey = (MayoPrivateKeyParameters)var2;
            this.random = CryptoServicesRegistrar.getSecureRandom();
         }

         this.params = this.privKey.getParameters();
      } else {
         this.pubKey = (MayoPublicKeyParameters)var2;
         this.params = this.pubKey.getParameters();
         this.privKey = null;
         this.random = null;
      }
   }

   @Override
   public byte[] generateSignature(byte[] var1) {
      int var2 = this.params.getK();
      int var3 = this.params.getV();
      int var4 = this.params.getO();
      int var5 = this.params.getN();
      int var6 = this.params.getM();
      int var7 = this.params.getVBytes();
      int var8 = this.params.getOBytes();
      int var9 = this.params.getSaltBytes();
      int var10 = this.params.getMVecLimbs();
      int var11 = this.params.getP1Limbs();
      int var12 = this.params.getPkSeedBytes();
      int var13 = this.params.getDigestBytes();
      int var14 = this.params.getSkSeedBytes();
      byte[] var15 = new byte[this.params.getMBytes()];
      byte[] var16 = new byte[var6];
      byte[] var17 = new byte[var6];
      byte[] var18 = new byte[var9];
      byte[] var19 = new byte[var2 * var7 + this.params.getRBytes()];
      byte[] var20 = new byte[var3 * var2];
      int var21 = var2 * var4;
      int var22 = var2 * var5;
      byte[] var23 = new byte[(var6 + 7) / 8 * 8 * (var21 + 1)];
      byte[] var24 = new byte[var22];
      byte[] var25 = new byte[var21 + 1];
      byte[] var26 = new byte[var22];
      byte[] var27 = new byte[var13 + var9 + var14 + 1];
      byte[] var28 = new byte[this.params.getSigBytes()];
      long[] var29 = new long[var11 + this.params.getP2Limbs()];
      byte[] var30 = new byte[var3 * var4];
      long[] var31 = new long[var21 * var10];
      long[] var32 = new long[var2 * var2 * var10];
      SHAKEDigest var33 = new SHAKEDigest(256);

      byte[] var55;
      try {
         byte[] var34 = this.privKey.getSeedSk();
         int var35 = var12 + var8;
         byte[] var36 = new byte[var35];
         var33.update(var34, 0, var34.length);
         var33.doFinal(var36, 0, var35);
         GF16.decode(var36, var12, var30, 0, var30.length);
         Utils.expandP1P2(this.params, var29, var36);
         int var37 = 0;
         int var38 = var4 * var10;
         int var39 = 0;
         int var40 = 0;

         for (int var41 = 0; var39 < var3; var41 += var38) {
            int var42 = var39;
            int var43 = var40;

            for (int var44 = var41; var42 < var3; var44 += var38) {
               if (var42 == var39) {
                  var37 += var10;
               } else {
                  int var45 = 0;

                  for (int var46 = var11; var45 < var4; var46 += var10) {
                     GF16Utils.mVecMulAdd(var10, var29, var37, var30[var43 + var45], var29, var41 + var46);
                     GF16Utils.mVecMulAdd(var10, var29, var37, var30[var40 + var45], var29, var44 + var46);
                     var45++;
                  }

                  var37 += var10;
               }

               var42++;
               var43 += var4;
            }

            var39++;
            var40 += var4;
         }

         Arrays.fill(var36, (byte)0);
         var33.update(var1, 0, var1.length);
         var33.doFinal(var27, 0, var13);
         this.random.nextBytes(var18);
         System.arraycopy(var18, 0, var27, var13, var18.length);
         System.arraycopy(var34, 0, var27, var13 + var9, var14);
         var33.update(var27, 0, var13 + var9 + var14);
         var33.doFinal(var18, 0, var9);
         System.arraycopy(var18, 0, var27, var13, var9);
         var33.update(var27, 0, var13 + var9);
         var33.doFinal(var15, 0, this.params.getMBytes());
         GF16.decode(var15, var16, var6);
         var39 = var3 * var2 * var10;
         long[] var51 = new long[var39];
         byte[] var52 = new byte[var3];

         for (int var53 = 0; var53 <= 255; var53++) {
            var27[var27.length - 1] = (byte)var53;
            var33.update(var27, 0, var27.length);
            var33.doFinal(var19, 0, var19.length);

            for (int var56 = 0; var56 < var2; var56++) {
               GF16.decode(var19, var56 * var7, var20, var56 * var3, var3);
            }

            GF16Utils.mulAddMatXMMat(var10, var20, var29, var11, var31, var2, var3, var4);
            GF16Utils.mulAddMUpperTriangularMatXMatTrans(var10, var29, var20, var51, var3, var2);
            GF16Utils.mulAddMatXMMat(var10, var20, var51, var32, var2, var3);
            this.computeRHS(var32, var16, var17);
            this.computeA(var31, var23);
            GF16.decode(var19, var2 * var7, var25, 0, var21);
            if (this.sampleSolution(var23, var17, var25, var24)) {
               break;
            }

            Arrays.fill(var31, 0L);
            Arrays.fill(var32, 0L);
         }

         int var54 = 0;
         int var57 = 0;
         int var58 = 0;

         for (int var59 = 0; var54 < var2; var59 += var3) {
            GF16Utils.matMul(var30, var24, var57, var52, var4, var3);
            Bytes.xor(var3, var20, var59, var52, var26, var58);
            System.arraycopy(var24, var57, var26, var58 + var3, var4);
            var54++;
            var57 += var4;
            var58 += var5;
         }

         GF16.encode(var26, var28, var22);
         System.arraycopy(var18, 0, var28, var28.length - var9, var9);
         var55 = Arrays.concatenate(var28, var1);
      } finally {
         Arrays.fill(var15, (byte)0);
         Arrays.fill(var16, (byte)0);
         Arrays.fill(var17, (byte)0);
         Arrays.fill(var18, (byte)0);
         Arrays.fill(var19, (byte)0);
         Arrays.fill(var20, (byte)0);
         Arrays.fill(var23, (byte)0);
         Arrays.fill(var24, (byte)0);
         Arrays.fill(var25, (byte)0);
         Arrays.fill(var26, (byte)0);
         Arrays.fill(var27, (byte)0);
      }

      return var55;
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      int var3 = this.params.getM();
      int var4 = this.params.getN();
      int var5 = this.params.getK();
      int var6 = var5 * var4;
      int var7 = this.params.getP1Limbs();
      int var8 = this.params.getP2Limbs();
      int var9 = this.params.getP3Limbs();
      int var10 = this.params.getMBytes();
      int var11 = this.params.getSigBytes();
      int var12 = this.params.getDigestBytes();
      int var13 = this.params.getSaltBytes();
      int var14 = this.params.getMVecLimbs();
      byte[] var15 = new byte[var10];
      byte[] var16 = new byte[var3];
      byte[] var17 = new byte[var3 << 1];
      byte[] var18 = new byte[var6];
      long[] var19 = new long[var7 + var8 + var9];
      byte[] var20 = new byte[var12 + var13];
      byte[] var21 = this.pubKey.getEncoded();
      Utils.expandP1P2(this.params, var19, var21);
      Utils.unpackMVecs(var21, this.params.getPkSeedBytes(), var19, var7 + var8, var9 / var14, var3);
      SHAKEDigest var22 = new SHAKEDigest(256);
      var22.update(var1, 0, var1.length);
      var22.doFinal(var20, 0, var12);
      var22.update(var20, 0, var12);
      var22.update(var2, var11 - var13, var13);
      var22.doFinal(var15, 0, var10);
      GF16.decode(var15, var16, var3);
      GF16.decode(var2, var18, var6);
      long[] var23 = new long[var5 * var5 * var14];
      long[] var24 = new long[var6 * var14];
      mayoGenericMCalculatePS(this.params, var19, var7, var7 + var8, var18, this.params.getV(), this.params.getO(), var5, var24);
      mayoGenericMCalculateSPS(var24, var18, var14, var5, var4, var23);
      byte[] var25 = new byte[var3];
      this.computeRHS(var23, var25, var17);
      return Arrays.constantTimeAreEqual(var3, var17, 0, var16, 0);
   }

   void computeRHS(long[] var1, byte[] var2, byte[] var3) {
      int var4 = this.params.getM();
      int var5 = this.params.getMVecLimbs();
      int var6 = this.params.getK();
      int[] var7 = this.params.getFTail();
      int var8 = (var4 - 1 & 15) << 2;
      if ((var4 & 15) != 0) {
         long var9 = (1L << ((var4 & 15) << 2)) - 1L;
         int var11 = var6 * var6;
         int var12 = 0;

         for (int var13 = var5 - 1; var12 < var11; var13 += var5) {
            var1[var13] &= var9;
            var12++;
         }
      }

      long[] var25 = new long[var5];
      byte[] var10 = new byte[var5 << 3];
      int var26 = var6 * var5;
      int var27 = var6 - 1;
      int var29 = var27 * var5;

      for (int var14 = var29 * var6; var27 >= 0; var14 -= var26) {
         int var15 = var27;
         int var16 = var29;

         for (int var17 = var14; var15 < var6; var17 += var26) {
            int var18 = (int)(var25[var5 - 1] >>> var8 & 15L);
            var25[var5 - 1] = var25[var5 - 1] << 4;

            for (int var19 = var5 - 2; var19 >= 0; var19--) {
               var25[var19 + 1] = var25[var19 + 1] ^ var25[var19] >>> 60;
               var25[var19] <<= 4;
            }

            Pack.longToLittleEndian(var25, var10, 0);

            for (int var31 = 0; var31 < 4; var31++) {
               int var20 = var7[var31];
               if (var20 != 0) {
                  long var21 = GF16.mul(var18, var20);
                  if ((var31 & 1) == 0) {
                     var10[var31 >> 1] = (byte)(var10[var31 >> 1] ^ (byte)(var21 & 15L));
                  } else {
                     var10[var31 >> 1] = (byte)(var10[var31 >> 1] ^ (byte)((var21 & 15L) << 4));
                  }
               }
            }

            Pack.littleEndianToLong(var10, 0, var25);
            int var32 = var14 + var16;
            int var33 = var17 + var29;
            boolean var34 = var27 == var15;

            for (int var22 = 0; var22 < var5; var22++) {
               long var23 = var1[var32 + var22];
               if (!var34) {
                  var23 ^= var1[var33 + var22];
               }

               var25[var22] ^= var23;
            }

            var15++;
            var16 += var5;
         }

         var27--;
         var29 -= var5;
      }

      Pack.longToLittleEndian(var25, var10, 0);

      for (byte var28 = 0; var28 < var4; var28 += 2) {
         var29 = var28 >> 1;
         var3[var28] = (byte)(var2[var28] ^ var10[var29] & 15);
         var3[var28 + 1] = (byte)(var2[var28 + 1] ^ var10[var29] >>> 4 & 15);
      }
   }

   void computeA(long[] var1, byte[] var2) {
      int var3 = this.params.getK();
      int var4 = this.params.getO();
      int var5 = this.params.getM();
      int var6 = this.params.getMVecLimbs();
      int var7 = this.params.getACols();
      int[] var8 = this.params.getFTail();
      byte var9 = 0;
      int var10 = 0;
      int var11 = var5 + 7 >>> 3;
      int var12 = var4 * var3;
      int var13 = var4 * var6;
      int var14 = var12 + 15 >> 4 << 4;
      long[] var15 = new long[var14 * var11 << 4];
      if ((var5 & 15) != 0) {
         long var16 = 1L << ((var5 & 15) << 2);
         var16--;
         int var18 = 0;

         for (int var19 = var6 - 1; var18 < var12; var19 += var6) {
            var1[var19] &= var16;
            var18++;
         }
      }

      int var33 = 0;
      int var17 = 0;

      for (int var39 = 0; var33 < var3; var39 += var13) {
         int var46 = var3 - 1;
         int var20 = var46 * var13;

         for (int var21 = var46 * var4; var46 >= var33; var21 -= var4) {
            int var22 = 0;

            for (int var23 = 0; var22 < var4; var23 += var6) {
               int var24 = 0;

               for (int var25 = 0; var24 < var6; var25 += var14) {
                  long var26 = var1[var20 + var24 + var23];
                  int var28 = var17 + var22 + var10 + var25;
                  var15[var28] ^= var26 << var9;
                  if (var9 > 0) {
                     var15[var28 + var14] = var15[var28 + var14] ^ var26 >>> 64 - var9;
                  }

                  var24++;
               }

               var22++;
            }

            if (var33 != var46) {
               var22 = 0;

               for (int var54 = 0; var22 < var4; var54 += var6) {
                  int var55 = 0;

                  for (int var57 = 0; var55 < var6; var57 += var14) {
                     long var58 = var1[var39 + var55 + var54];
                     int var60 = var21 + var22 + var10 + var57;
                     var15[var60] ^= var58 << var9;
                     if (var9 > 0) {
                        var15[var60 + var14] = var15[var60 + var14] ^ var58 >>> 64 - var9;
                     }

                     var55++;
                  }

                  var22++;
               }
            }

            var9 += 4;
            if (var9 == 64) {
               var10 += var14;
               var9 = 0;
            }

            var46--;
            var20 -= var13;
         }

         var33++;
         var17 += var4;
      }

      for (byte var34 = 0; var34 < var14 * (var5 + ((var3 + 1) * var3 >> 1) + 15 >>> 4); var34 += 16) {
         transpose16x16Nibbles(var15, var34);
      }

      byte[] var35 = new byte[16];
      var17 = 0;

      for (int var40 = 0; var17 < 4; var17++) {
         int var47 = var8[var17];
         var35[var40++] = (byte)GF16.mul(var47, 1);
         var35[var40++] = (byte)GF16.mul(var47, 2);
         var35[var40++] = (byte)GF16.mul(var47, 4);
         var35[var40++] = (byte)GF16.mul(var47, 8);
      }

      for (byte var37 = 0; var37 < var14; var37 += 16) {
         for (int var44 = var5; var44 < var5 + ((var3 + 1) * var3 >>> 1); var44++) {
            int var48 = (var44 >>> 4) * var14 + var37 + (var44 & 15);
            long var50 = var15[var48] & 1229782938247303441L;
            long var53 = var15[var48] >>> 1 & 1229782938247303441L;
            long var56 = var15[var48] >>> 2 & 1229782938247303441L;
            long var59 = var15[var48] >>> 3 & 1229782938247303441L;
            int var61 = 0;

            for (byte var29 = 0; var61 < 4; var29 += 4) {
               int var30 = var44 + var61 - var5;
               int var31 = (var30 >> 4) * var14 + var37 + (var30 & 15);
               var15[var31] ^= var50 * var35[var29] ^ var53 * var35[var29 + 1] ^ var56 * var35[var29 + 2] ^ var59 * var35[var29 + 3];
               var61++;
            }
         }
      }

      byte[] var38 = Pack.longToLittleEndian(var15);

      for (byte var45 = 0; var45 < var5; var45 += 16) {
         for (byte var49 = 0; var49 < var7 - 1; var49 += 16) {
            for (int var51 = 0; var51 + var45 < var5; var51++) {
               GF16.decode(var38, (var45 * var14 >> 4) + var49 + var51 << 3, var2, (var45 + var51) * var7 + var49, Math.min(16, var7 - 1 - var49));
            }
         }
      }
   }

   private static void transpose16x16Nibbles(long[] var0, int var1) {
      for (byte var2 = 0; var2 < 16; var2 += 2) {
         int var3 = var1 + var2;
         int var4 = var3 + 1;
         long var5 = (var0[var3] >>> 4 ^ var0[var4]) & 1085102592571150095L;
         var0[var3] ^= var5 << 4;
         var0[var4] ^= var5;
      }

      byte var8 = 0;

      for (int var11 = var1; var8 < 16; var8 += 4) {
         long var17 = (var0[var11] >>> 8 ^ var0[var11 + 2]) & 71777214294589695L;
         long var6 = (var0[var11 + 1] >>> 8 ^ var0[var11 + 3]) & 71777214294589695L;
         var0[var11++] ^= var17 << 8;
         var0[var11++] ^= var6 << 8;
         var0[var11++] ^= var17;
         var0[var11++] ^= var6;
      }

      for (int var9 = 0; var9 < 4; var9++) {
         int var15 = var1 + var9;
         long var18 = (var0[var15] >>> 16 ^ var0[var15 + 4]) & 281470681808895L;
         long var20 = (var0[var15 + 8] >>> 16 ^ var0[var15 + 12]) & 281470681808895L;
         var0[var15] ^= var18 << 16;
         var0[var15 + 8] = var0[var15 + 8] ^ var20 << 16;
         var0[var15 + 4] = var0[var15 + 4] ^ var18;
         var0[var15 + 12] = var0[var15 + 12] ^ var20;
      }

      for (int var10 = 0; var10 < 8; var10++) {
         int var16 = var1 + var10;
         long var19 = (var0[var16] >>> 32 ^ var0[var16 + 8]) & 4294967295L;
         var0[var16] ^= var19 << 32;
         var0[var16 + 8] = var0[var16 + 8] ^ var19;
      }
   }

   boolean sampleSolution(byte[] var1, byte[] var2, byte[] var3, byte[] var4) {
      int var5 = this.params.getK();
      int var6 = this.params.getO();
      int var7 = this.params.getM();
      int var8 = this.params.getACols();
      int var9 = var5 * var6;
      System.arraycopy(var3, 0, var4, 0, var9);
      byte[] var10 = new byte[var7];
      GF16Utils.matMul(var1, var3, 0, var10, var9 + 1, var7);
      int var11 = 0;

      for (int var12 = var9; var11 < var7; var12 += var9 + 1) {
         var1[var12] = (byte)(var2[var11] ^ var10[var11]);
         var11++;
      }

      this.ef(var1, var7, var8);
      boolean var26 = false;
      int var27 = 0;

      for (int var13 = (var7 - 1) * var8; var27 < var8 - 1; var13++) {
         var26 |= var1[var13] != 0;
         var27++;
      }

      if (!var26) {
         return false;
      } else {
         var27 = var7 - 1;

         for (int var29 = var27 * var8; var27 >= 0; var29 -= var8) {
            byte var14 = 0;
            int var15 = Math.min(var27 + 32 / (var7 - var27), var9);

            for (int var16 = var27; var16 <= var15; var16++) {
               byte var17 = (byte)(-(var1[var29 + var16] & 255) >> 31);
               byte var18 = (byte)(var17 & ~var14 & var1[var29 + var8 - 1]);
               var4[var16] ^= var18;
               byte var19 = 0;
               int var20 = var16;

               for (int var21 = var8 - 1; var19 < var27; var21 += var8 << 3) {
                  long var22 = 0L;
                  int var24 = 0;

                  for (int var25 = 0; var24 < 8; var25 += var8) {
                     var22 ^= (long)(var1[var20 + var25] & 255) << (var24 << 3);
                     var24++;
                  }

                  var22 = GF16Utils.mulFx8(var18, var22);
                  var24 = 0;

                  for (int var32 = 0; var24 < 8; var32 += var8) {
                     var1[var21 + var32] = (byte)(var1[var21 + var32] ^ (byte)(var22 >> (var24 << 3) & 15L));
                     var24++;
                  }

                  var19 += 8;
                  var20 += var8 << 3;
               }

               var14 |= var17;
            }

            var27--;
         }

         return true;
      }
   }

   void ef(byte[] var1, int var2, int var3) {
      int var4 = var3 + 15 >> 4;
      long[] var5 = new long[var4];
      long[] var6 = new long[var4];
      long[] var7 = new long[var2 * var4];
      int var8 = this.params.getO() * this.params.getK() + 16;
      byte[] var9 = new byte[var8 >> 1];
      int var10 = var8 >> 4;
      int var11 = 0;
      int var12 = 0;

      for (int var13 = 0; var11 < var2; var13 += var4) {
         for (int var14 = 0; var14 < var4; var14++) {
            long var15 = 0L;

            for (int var17 = 0; var17 < 16; var17++) {
               int var18 = (var14 << 4) + var17;
               if (var18 < var3) {
                  var15 |= (var1[var12 + var18] & 15L) << (var17 << 2);
               }
            }

            var7[var14 + var13] = var15;
         }

         var11++;
         var12 += var3;
      }

      var11 = 0;

      for (int var28 = 0; var28 < var3; var28++) {
         int var30 = Math.max(0, var28 + var2 - var3);
         int var32 = Math.min(var2 - 1, var28);
         Arrays.clear(var5);
         Arrays.clear(var6);
         int var34 = 0;
         long var16 = -1L;
         int var35 = Math.min(var2 - 1, var32 + 32);
         int var19 = var30;

         for (int var20 = var30 * var4; var19 <= var35; var20 += var4) {
            long var21 = ~ctCompare64(var19, var11);
            long var23 = (long)var11 - var19 >> 63;

            for (int var25 = 0; var25 < var4; var25++) {
               var5[var25] ^= (var21 | var23 & var16) & var7[var20 + var25];
            }

            var34 = (int)(var5[var28 >>> 4] >>> ((var28 & 15) << 2) & 15L);
            var16 = ~(-var34 >> 63);
            var19++;
         }

         vecMulAddU64(var4, var5, GF16.inv((byte)var34), var6);
         var19 = var30;

         for (int var38 = var30 * var4; var19 <= var32; var38 += var4) {
            long var40 = ~ctCompare64(var19, var11) & ~var16;
            long var42 = ~var40;
            int var43 = 0;

            for (int var26 = var38; var43 < var4; var26++) {
               var7[var26] = var42 & var7[var26] | var40 & var6[var43];
               var43++;
            }

            var19++;
         }

         var19 = var30;

         for (int var39 = var30 * var4; var19 < var2; var39 += var4) {
            int var41 = var19 > var11 ? -1 : 0;
            int var22 = (int)(var7[var39 + (var28 >>> 4)] >>> ((var28 & 15) << 2) & 15L);
            vecMulAddU64(var4, var6, (byte)(var41 & var22), var7, var39);
            var19++;
         }

         if (var34 != 0) {
            var11++;
         }
      }

      var12 = 0;
      int var31 = 0;

      for (int var33 = 0; var31 < var2; var33 += var4) {
         Pack.longToLittleEndian(var7, var33, var10, var9, 0);
         GF16.decode(var9, 0, var1, var12, var3);
         var12 += var3;
         var31++;
      }
   }

   private static long ctCompare64(int var0, int var1) {
      return -(var0 ^ var1) >> 63;
   }

   private static void vecMulAddU64(int var0, long[] var1, byte var2, long[] var3) {
      int var4 = mulTable(var2 & 255);

      for (int var5 = 0; var5 < var0; var5++) {
         long var6 = (var1[var5] & 1229782938247303441L) * (var4 & 0xFF)
            ^ (var1[var5] >>> 1 & 1229782938247303441L) * (var4 >>> 8 & 15)
            ^ (var1[var5] >>> 2 & 1229782938247303441L) * (var4 >>> 16 & 15)
            ^ (var1[var5] >>> 3 & 1229782938247303441L) * (var4 >>> 24 & 15);
         var3[var5] ^= var6;
      }
   }

   private static void vecMulAddU64(int var0, long[] var1, byte var2, long[] var3, int var4) {
      int var5 = mulTable(var2 & 255);

      for (int var6 = 0; var6 < var0; var6++) {
         long var7 = (var1[var6] & 1229782938247303441L) * (var5 & 0xFF)
            ^ (var1[var6] >>> 1 & 1229782938247303441L) * (var5 >>> 8 & 15)
            ^ (var1[var6] >>> 2 & 1229782938247303441L) * (var5 >>> 16 & 15)
            ^ (var1[var6] >>> 3 & 1229782938247303441L) * (var5 >>> 24 & 15);
         var3[var4 + var6] = var3[var4 + var6] ^ var7;
      }
   }

   private static int mulTable(int var0) {
      int var1 = var0 * 134480385;
      int var2 = var1 & -252645136;
      return var1 ^ var2 >>> 4 ^ var2 >>> 3;
   }

   private static void mayoGenericMCalculatePS(MayoParameters var0, long[] var1, int var2, int var3, byte[] var4, int var5, int var6, int var7, long[] var8) {
      int var9 = var6 + var5;
      int var10 = var0.getMVecLimbs();
      long[] var11 = new long[var10 * var0.getK() * var0.getN() * var10 << 4];
      int var12 = var6 * var10;
      int var13 = 0;
      int var14 = 0;
      int var15 = 0;

      for (int var16 = 0; var14 < var5; var16 += var12) {
         for (int var17 = var14; var17 < var5; var17++) {
            int var18 = 0;

            for (int var19 = 0; var18 < var7; var19 += var9) {
               Longs.xorTo(var10, var1, var13, var11, ((var15 + var18 << 4) + (var4[var19 + var17] & 255)) * var10);
               var18++;
            }

            var13 += var10;
         }

         int var25 = 0;

         for (int var27 = var16; var25 < var6; var27 += var10) {
            int var29 = 0;

            for (int var20 = 0; var29 < var7; var20 += var9) {
               Longs.xorTo(var10, var1, var2 + var27, var11, ((var15 + var29 << 4) + (var4[var20 + var25 + var5] & 255)) * var10);
               var29++;
            }

            var25++;
         }

         var14++;
         var15 += var7;
      }

      var13 = 0;
      var14 = var5;

      for (int var23 = var5 * var7; var14 < var9; var23 += var7) {
         for (int var24 = var14; var24 < var9; var24++) {
            int var26 = 0;

            for (int var28 = 0; var26 < var7; var28 += var9) {
               Longs.xorTo(var10, var1, var3 + var13, var11, ((var23 + var26 << 4) + (var4[var28 + var24] & 255)) * var10);
               var26++;
            }

            var13 += var10;
         }

         var14++;
      }

      mVecMultiplyBins(var10, var9 * var7, var11, var8);
   }

   private static void mayoGenericMCalculateSPS(long[] var0, byte[] var1, int var2, int var3, int var4, long[] var5) {
      int var6 = var3 * var3;
      int var7 = var2 * var6 << 4;
      long[] var8 = new long[var7];
      int var9 = var3 * var2;
      int var10 = 0;
      int var11 = 0;

      for (int var12 = 0; var10 < var3; var12 += var9 << 4) {
         int var13 = 0;

         for (int var14 = 0; var13 < var4; var14 += var9) {
            int var15 = (var1[var11 + var13] & 255) * var2 + var12;
            int var16 = 0;

            for (int var17 = 0; var16 < var3; var17 += var2) {
               Longs.xorTo(var2, var0, var14 + var17, var8, var15 + (var17 << 4));
               var16++;
            }

            var13++;
         }

         var10++;
         var11 += var4;
      }

      mVecMultiplyBins(var2, var6, var8, var5);
   }

   private static void mVecMultiplyBins(int var0, int var1, long[] var2, long[] var3) {
      int var10 = var0 + var0;
      int var11 = var10 + var0;
      int var12 = var11 + var0;
      int var13 = var12 + var0;
      int var14 = var13 + var0;
      int var15 = var14 + var0;
      int var16 = var15 + var0;
      int var17 = var16 + var0;
      int var18 = var17 + var0;
      int var19 = var18 + var0;
      int var20 = var19 + var0;
      int var21 = var20 + var0;
      int var22 = var21 + var0;
      int var23 = var22 + var0;
      int var24 = 0;

      for (int var25 = 0; var24 < var1; var25 += var0 << 4) {
         int var26 = 0;

         for (int var27 = var25; var26 < var0; var27++) {
            long var6 = var2[var27 + var13];
            long var8 = var6 & 1229782938247303441L;
            var6 = var2[var27 + var18] ^ (var6 & -1229782938247303442L) >>> 1 ^ (var8 << 3) + var8;
            long var4 = var2[var27 + var19];
            var8 = (var4 & -8608480567731124088L) >>> 3;
            var4 = var2[var27 + var20] ^ (var4 & 8608480567731124087L) << 1 ^ (var8 << 1) + var8;
            var8 = var6 & 1229782938247303441L;
            var6 = var2[var27 + var15] ^ (var6 & -1229782938247303442L) >>> 1 ^ (var8 << 3) + var8;
            var8 = (var4 & -8608480567731124088L) >>> 3;
            var4 = var2[var27 + var14] ^ (var4 & 8608480567731124087L) << 1 ^ (var8 << 1) + var8;
            var8 = var6 & 1229782938247303441L;
            var6 = var2[var27 + var22] ^ (var6 & -1229782938247303442L) >>> 1 ^ (var8 << 3) + var8;
            var8 = (var4 & -8608480567731124088L) >>> 3;
            var4 = var2[var27 + var11] ^ (var4 & 8608480567731124087L) << 1 ^ (var8 << 1) + var8;
            var8 = var6 & 1229782938247303441L;
            var6 = var2[var27 + var23] ^ (var6 & -1229782938247303442L) >>> 1 ^ (var8 << 3) + var8;
            var8 = (var4 & -8608480567731124088L) >>> 3;
            var4 = var2[var27 + var16] ^ (var4 & 8608480567731124087L) << 1 ^ (var8 << 1) + var8;
            var8 = var6 & 1229782938247303441L;
            var6 = var2[var27 + var21] ^ (var6 & -1229782938247303442L) >>> 1 ^ (var8 << 3) + var8;
            var8 = (var4 & -8608480567731124088L) >>> 3;
            var4 = var2[var27 + var12] ^ (var4 & 8608480567731124087L) << 1 ^ (var8 << 1) + var8;
            var8 = var6 & 1229782938247303441L;
            var6 = var2[var27 + var17] ^ (var6 & -1229782938247303442L) >>> 1 ^ (var8 << 3) + var8;
            var8 = (var4 & -8608480567731124088L) >>> 3;
            var4 = var2[var27 + var10] ^ (var4 & 8608480567731124087L) << 1 ^ (var8 << 1) + var8;
            var8 = var6 & 1229782938247303441L;
            var6 = var2[var27 + var0] ^ (var6 & -1229782938247303442L) >>> 1 ^ (var8 << 3) + var8;
            var8 = (var4 & -8608480567731124088L) >>> 3;
            var3[(var25 >> 4) + var26] = var6 ^ (var4 & 8608480567731124087L) << 1 ^ (var8 << 1) + var8;
            var26++;
         }

         var24++;
      }
   }
}
