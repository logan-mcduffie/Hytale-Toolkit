package org.bouncycastle.pqc.crypto.frodo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Xof;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Pack;

class FrodoEngine {
   static final int nbar = 8;
   private static final int mbar = 8;
   private static final int len_seedA = 128;
   private static final int len_z = 128;
   private static final int len_chi = 16;
   private static final int len_seedA_bytes = 16;
   private static final int len_z_bytes = 16;
   private static final int len_chi_bytes = 2;
   private final int D;
   private final int q;
   private final int n;
   private final int B;
   private final int len_sk_bytes;
   private final int len_pk_bytes;
   private final int len_ct_bytes;
   private final short[] T_chi;
   private final int len_mu;
   private final int len_seedSE;
   private final int len_s;
   private final int len_k;
   private final int len_pkh;
   private final int len_ss;
   private final int len_mu_bytes;
   private final int len_seedSE_bytes;
   private final int len_s_bytes;
   private final int len_k_bytes;
   private final int len_pkh_bytes;
   private final int len_ss_bytes;
   private final Xof digest;
   private final FrodoMatrixGenerator gen;

   public int getCipherTextSize() {
      return this.len_ct_bytes;
   }

   public int getSessionKeySize() {
      return this.len_ss_bytes;
   }

   public int getPrivateKeySize() {
      return this.len_sk_bytes;
   }

   public int getPublicKeySize() {
      return this.len_pk_bytes;
   }

   public FrodoEngine(int var1, int var2, int var3, short[] var4, Xof var5, FrodoMatrixGenerator var6) {
      this.n = var1;
      this.D = var2;
      this.q = 1 << var2;
      this.B = var3;
      this.len_mu = var3 * 8 * 8;
      this.len_seedSE = this.len_mu;
      this.len_s = this.len_mu;
      this.len_k = this.len_mu;
      this.len_pkh = this.len_mu;
      this.len_ss = this.len_mu;
      this.len_mu_bytes = this.len_mu / 8;
      this.len_seedSE_bytes = this.len_seedSE / 8;
      this.len_s_bytes = this.len_s / 8;
      this.len_k_bytes = this.len_k / 8;
      this.len_pkh_bytes = this.len_pkh / 8;
      this.len_ss_bytes = this.len_ss / 8;
      this.len_ct_bytes = var2 * var1 * 8 / 8 + var2 * 8 * 8 / 8;
      this.len_pk_bytes = 16 + var2 * var1 * 8 / 8;
      this.len_sk_bytes = this.len_s_bytes + this.len_pk_bytes + 2 * var1 * 8 + this.len_pkh_bytes;
      this.T_chi = var4;
      this.digest = var5;
      this.gen = var6;
   }

   private short sample(short var1) {
      short var2 = (short)((var1 & '\uffff') >>> 1);
      short var3 = 0;

      for (int var4 = 0; var4 < this.T_chi.length; var4++) {
         if (var2 > this.T_chi[var4]) {
            var3++;
         }
      }

      if ((var1 & '\uffff') % 2 == 1) {
         var3 = (short)(var3 * -1 & 65535);
      }

      return var3;
   }

   private short[] sample_matrix(short[] var1, int var2, int var3, int var4) {
      short[] var5 = new short[var3 * var4];

      for (int var6 = 0; var6 < var3; var6++) {
         for (int var7 = 0; var7 < var4; var7++) {
            var5[var6 * var4 + var7] = this.sample(var1[var6 * var4 + var7 + var2]);
         }
      }

      return var5;
   }

   private short[] matrix_transpose(short[] var1, int var2, int var3) {
      short[] var4 = new short[var2 * var3];

      for (int var5 = 0; var5 < var3; var5++) {
         for (int var6 = 0; var6 < var2; var6++) {
            var4[var5 * var2 + var6] = var1[var6 * var3 + var5];
         }
      }

      return var4;
   }

   private short[] matrix_mul(short[] var1, int var2, int var3, short[] var4, int var5, int var6) {
      int var7 = this.q - 1;
      short[] var8 = new short[var2 * var6];

      for (int var9 = 0; var9 < var2; var9++) {
         for (int var10 = 0; var10 < var6; var10++) {
            int var11 = 0;

            for (int var12 = 0; var12 < var3; var12++) {
               var11 += var1[var9 * var3 + var12] * var4[var12 * var6 + var10];
            }

            var8[var9 * var6 + var10] = (short)(var11 & var7);
         }
      }

      return var8;
   }

   private short[] matrix_add(short[] var1, short[] var2, int var3, int var4) {
      int var5 = this.q - 1;
      short[] var6 = new short[var3 * var4];

      for (int var7 = 0; var7 < var3; var7++) {
         for (int var8 = 0; var8 < var4; var8++) {
            var6[var7 * var4 + var8] = (short)(var1[var7 * var4 + var8] + var2[var7 * var4 + var8] & var5);
         }
      }

      return var6;
   }

   private byte[] pack(short[] var1) {
      int var2 = var1.length;
      byte[] var3 = new byte[this.D * var2 / 8];
      short var4 = 0;
      short var5 = 0;
      short var6 = 0;
      byte var7 = 0;

      while (var4 < var3.length && (var5 < var2 || var5 == var2 && var7 > 0)) {
         byte var8 = 0;

         while (true) {
            if (var8 < 8) {
               int var9 = Math.min(8 - var8, var7);
               short var10 = (short)((1 << var9) - 1);
               byte var11 = (byte)(var6 >> var7 - var9 & var10);
               var3[var4] = (byte)(var3[var4] + (var11 << 8 - var8 - var9));
               var8 = (byte)(var8 + var9);
               var7 = (byte)(var7 - var9);
               if (var7 != 0) {
                  continue;
               }

               if (var5 < var2) {
                  var6 = var1[var5];
                  var7 = (byte)this.D;
                  var5++;
                  continue;
               }
            }

            if (var8 == 8) {
               var4++;
            }
            break;
         }
      }

      return var3;
   }

   public void kem_keypair(byte[] var1, byte[] var2, SecureRandom var3) {
      byte[] var4 = new byte[this.len_s_bytes + this.len_seedSE_bytes + 16];
      var3.nextBytes(var4);
      byte[] var5 = Arrays.copyOfRange(var4, 0, this.len_s_bytes);
      byte[] var6 = Arrays.copyOfRange(var4, this.len_s_bytes, this.len_s_bytes + this.len_seedSE_bytes);
      byte[] var7 = Arrays.copyOfRange(var4, this.len_s_bytes + this.len_seedSE_bytes, this.len_s_bytes + this.len_seedSE_bytes + 16);
      byte[] var8 = new byte[16];
      this.digest.update(var7, 0, var7.length);
      this.digest.doFinal(var8, 0, var8.length);
      short[] var9 = this.gen.genMatrix(var8);
      byte[] var10 = new byte[2 * this.n * 8 * 2];
      this.digest.update((byte)95);
      this.digest.update(var6, 0, var6.length);
      this.digest.doFinal(var10, 0, var10.length);
      short[] var11 = new short[2 * this.n * 8];

      for (int var12 = 0; var12 < var11.length; var12++) {
         var11[var12] = Pack.littleEndianToShort(var10, var12 * 2);
      }

      short[] var20 = this.sample_matrix(var11, 0, 8, this.n);
      short[] var13 = this.matrix_transpose(var20, 8, this.n);
      short[] var14 = this.sample_matrix(var11, this.n * 8, this.n, 8);
      short[] var15 = this.matrix_add(this.matrix_mul(var9, this.n, this.n, var13, this.n, 8), var14, this.n, 8);
      byte[] var16 = this.pack(var15);
      System.arraycopy(Arrays.concatenate(var8, var16), 0, var1, 0, this.len_pk_bytes);
      byte[] var17 = new byte[this.len_pkh_bytes];
      this.digest.update(var1, 0, var1.length);
      this.digest.doFinal(var17, 0, var17.length);
      System.arraycopy(Arrays.concatenate(var5, var1), 0, var2, 0, this.len_s_bytes + this.len_pk_bytes);

      for (int var18 = 0; var18 < 8; var18++) {
         for (int var19 = 0; var19 < this.n; var19++) {
            System.arraycopy(
               Pack.shortToLittleEndian(var20[var18 * this.n + var19]), 0, var2, this.len_s_bytes + this.len_pk_bytes + var18 * this.n * 2 + var19 * 2, 2
            );
         }
      }

      System.arraycopy(var17, 0, var2, this.len_sk_bytes - this.len_pkh_bytes, this.len_pkh_bytes);
   }

   private short[] unpack(byte[] var1, int var2, int var3) {
      short[] var4 = new short[var2 * var3];
      short var5 = 0;
      short var6 = 0;
      byte var7 = 0;
      byte var8 = 0;

      while (var5 < var4.length && (var6 < var1.length || var6 == var1.length && var8 > 0)) {
         byte var9 = 0;

         while (true) {
            if (var9 < this.D) {
               int var10 = Math.min(this.D - var9, var8);
               short var11 = (short)((1 << var10) - 1 & 65535);
               byte var12 = (byte)((var7 & 255) >>> (var8 & 255) - var10 & var11 & '\uffff' & 0xFF);
               var4[var5] = (short)((var4[var5] & '\uffff') + ((var12 & 255) << this.D - (var9 & 255) - var10) & 65535);
               var9 = (byte)(var9 + var10);
               var8 = (byte)(var8 - var10);
               var7 = (byte)(var7 & ~(var11 << var8));
               if (var8 != 0) {
                  continue;
               }

               if (var6 < var1.length) {
                  var7 = var1[var6];
                  var8 = 8;
                  var6++;
                  continue;
               }
            }

            if (var9 == this.D) {
               var5++;
            }
            break;
         }
      }

      return var4;
   }

   private short[] encode(byte[] var1) {
      int var3 = 0;
      int var4 = 0;
      short[] var5 = new short[64];

      for (int var7 = 0; var7 < 8; var7++) {
         for (int var8 = 0; var8 < 8; var8++) {
            int var6 = 0;

            for (int var2 = 0; var2 < this.B; var2++) {
               var6 += (var1[var3] >>> var4 & 1) << var2;
               var3 += ++var4 >>> 3;
               var4 &= 7;
            }

            var5[var7 * 8 + var8] = (short)(var6 * (this.q / (1 << this.B)));
         }
      }

      return var5;
   }

   public void kem_enc(byte[] var1, byte[] var2, byte[] var3, SecureRandom var4) {
      byte[] var5 = Arrays.copyOfRange(var3, 0, 16);
      byte[] var6 = Arrays.copyOfRange(var3, 16, this.len_pk_bytes);
      byte[] var7 = new byte[this.len_mu_bytes];
      var4.nextBytes(var7);
      byte[] var8 = new byte[this.len_pkh_bytes];
      this.digest.update(var3, 0, this.len_pk_bytes);
      this.digest.doFinal(var8, 0, this.len_pkh_bytes);
      byte[] var9 = new byte[this.len_seedSE + this.len_k];
      this.digest.update(var8, 0, this.len_pkh_bytes);
      this.digest.update(var7, 0, this.len_mu_bytes);
      this.digest.doFinal(var9, 0, this.len_seedSE_bytes + this.len_k_bytes);
      byte[] var10 = Arrays.copyOfRange(var9, 0, this.len_seedSE_bytes);
      byte[] var11 = Arrays.copyOfRange(var9, this.len_seedSE_bytes, this.len_seedSE_bytes + this.len_k_bytes);
      byte[] var12 = new byte[(16 * this.n + 64) * 2];
      this.digest.update((byte)-106);
      this.digest.update(var10, 0, var10.length);
      this.digest.doFinal(var12, 0, var12.length);
      short[] var13 = new short[var12.length / 2];

      for (int var14 = 0; var14 < var13.length; var14++) {
         var13[var14] = Pack.littleEndianToShort(var12, var14 * 2);
      }

      short[] var25 = this.sample_matrix(var13, 0, 8, this.n);
      short[] var15 = this.sample_matrix(var13, 8 * this.n, 8, this.n);
      short[] var16 = this.gen.genMatrix(var5);
      short[] var17 = this.matrix_add(this.matrix_mul(var25, 8, this.n, var16, this.n, this.n), var15, 8, this.n);
      byte[] var18 = this.pack(var17);
      short[] var19 = this.sample_matrix(var13, 16 * this.n, 8, 8);
      short[] var20 = this.unpack(var6, this.n, 8);
      short[] var21 = this.matrix_add(this.matrix_mul(var25, 8, this.n, var20, this.n, 8), var19, 8, 8);
      short[] var22 = this.encode(var7);
      short[] var23 = this.matrix_add(var21, var22, 8, 8);
      byte[] var24 = this.pack(var23);
      System.arraycopy(Arrays.concatenate(var18, var24), 0, var1, 0, this.len_ct_bytes);
      this.digest.update(var18, 0, var18.length);
      this.digest.update(var24, 0, var24.length);
      this.digest.update(var11, 0, this.len_k_bytes);
      this.digest.doFinal(var2, 0, this.len_s_bytes);
   }

   private short[] matrix_sub(short[] var1, short[] var2, int var3, int var4) {
      int var5 = this.q - 1;
      short[] var6 = new short[var3 * var4];

      for (int var7 = 0; var7 < var3; var7++) {
         for (int var8 = 0; var8 < var4; var8++) {
            var6[var7 * var4 + var8] = (short)(var1[var7 * var4 + var8] - var2[var7 * var4 + var8] & var5);
         }
      }

      return var6;
   }

   private byte[] decode(short[] var1) {
      int var4 = 0;
      byte var5 = 8;
      byte var6 = 8;
      short var8 = (short)((1 << this.B) - 1);
      short var9 = (short)((1 << this.D) - 1);
      byte[] var10 = new byte[var5 * this.B];

      for (int var2 = 0; var2 < var6; var2++) {
         long var11 = 0L;

         for (int var3 = 0; var3 < var5; var3++) {
            short var7 = (short)((var1[var4] & var9) + (1 << this.D - this.B - 1) >> this.D - this.B);
            var11 |= (long)(var7 & var8) << this.B * var3;
            var4++;
         }

         for (int var13 = 0; var13 < this.B; var13++) {
            var10[var2 * this.B + var13] = (byte)(var11 >> 8 * var13 & 255L);
         }
      }

      return var10;
   }

   private short ctverify(short[] var1, short[] var2, short[] var3, short[] var4) {
      short var5 = 0;

      for (short var6 = 0; var6 < var1.length; var6++) {
         var5 = (short)(var5 | var1[var6] ^ var3[var6]);
      }

      for (short var7 = 0; var7 < var2.length; var7++) {
         var5 = (short)(var5 | var2[var7] ^ var4[var7]);
      }

      return (short)(var5 == 0 ? 0 : -1);
   }

   private byte[] ctselect(byte[] var1, byte[] var2, short var3) {
      byte[] var4 = new byte[var1.length];

      for (int var5 = 0; var5 < var1.length; var5++) {
         var4[var5] = (byte)(~var3 & var1[var5] & 0xFF | var3 & var2[var5] & 0xFF);
      }

      return var4;
   }

   public void kem_dec(byte[] var1, byte[] var2, byte[] var3) {
      int var4 = 0;
      int var5 = 8 * this.n * this.D / 8;
      byte[] var6 = Arrays.copyOfRange(var2, var4, var4 + var5);
      var4 += var5;
      var5 = 64 * this.D / 8;
      byte[] var7 = Arrays.copyOfRange(var2, var4, var4 + var5);
      int var35 = 0;
      var5 = this.len_s_bytes;
      byte[] var8 = Arrays.copyOfRange(var3, var35, var35 + var5);
      var35 += var5;
      int var42 = 16;
      byte[] var9 = Arrays.copyOfRange(var3, var35, var35 + var42);
      var35 += var42;
      var42 = this.D * this.n * 8 / 8;
      byte[] var10 = Arrays.copyOfRange(var3, var35, var35 + var42);
      var35 += var42;
      var42 = this.n * 8 * 16 / 8;
      byte[] var11 = Arrays.copyOfRange(var3, var35, var35 + var42);
      short[] var12 = new short[8 * this.n];

      for (int var13 = 0; var13 < 8; var13++) {
         for (int var14 = 0; var14 < this.n; var14++) {
            var12[var13 * this.n + var14] = Pack.littleEndianToShort(var11, var13 * this.n * 2 + var14 * 2);
         }
      }

      short[] var46 = this.matrix_transpose(var12, 8, this.n);
      var35 += var42;
      var42 = this.len_pkh_bytes;
      byte[] var47 = Arrays.copyOfRange(var3, var35, var35 + var42);
      short[] var15 = this.unpack(var6, 8, this.n);
      short[] var16 = this.unpack(var7, 8, 8);
      short[] var17 = this.matrix_mul(var15, 8, this.n, var46, this.n, 8);
      short[] var18 = this.matrix_sub(var16, var17, 8, 8);
      byte[] var19 = this.decode(var18);
      byte[] var20 = new byte[this.len_seedSE_bytes + this.len_k_bytes];
      this.digest.update(var47, 0, this.len_pkh_bytes);
      this.digest.update(var19, 0, this.len_mu_bytes);
      this.digest.doFinal(var20, 0, this.len_seedSE_bytes + this.len_k_bytes);
      byte[] var21 = Arrays.copyOfRange(var20, this.len_seedSE_bytes, this.len_seedSE_bytes + this.len_k_bytes);
      byte[] var22 = new byte[(16 * this.n + 64) * 2];
      this.digest.update((byte)-106);
      this.digest.update(var20, 0, this.len_seedSE_bytes);
      this.digest.doFinal(var22, 0, var22.length);
      short[] var23 = new short[16 * this.n + 64];

      for (int var24 = 0; var24 < var23.length; var24++) {
         var23[var24] = Pack.littleEndianToShort(var22, var24 * 2);
      }

      short[] var48 = this.sample_matrix(var23, 0, 8, this.n);
      short[] var25 = this.sample_matrix(var23, 8 * this.n, 8, this.n);
      short[] var26 = this.gen.genMatrix(var9);
      short[] var27 = this.matrix_add(this.matrix_mul(var48, 8, this.n, var26, this.n, this.n), var25, 8, this.n);
      short[] var28 = this.sample_matrix(var23, 16 * this.n, 8, 8);
      short[] var29 = this.unpack(var10, this.n, 8);
      short[] var30 = this.matrix_add(this.matrix_mul(var48, 8, this.n, var29, this.n, 8), var28, 8, 8);
      short[] var31 = this.matrix_add(var30, this.encode(var19), 8, 8);
      short var32 = this.ctverify(var15, var16, var27, var31);
      byte[] var33 = this.ctselect(var21, var8, var32);
      this.digest.update(var6, 0, var6.length);
      this.digest.update(var7, 0, var7.length);
      this.digest.update(var33, 0, var33.length);
      this.digest.doFinal(var1, 0, this.len_ss_bytes);
   }
}
