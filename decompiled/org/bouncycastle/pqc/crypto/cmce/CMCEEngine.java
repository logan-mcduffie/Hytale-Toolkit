package org.bouncycastle.pqc.crypto.cmce;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Arrays;

class CMCEEngine {
   private int SYS_N;
   private int SYS_T;
   private int GFBITS;
   private int IRR_BYTES;
   private int COND_BYTES;
   private int PK_NROWS;
   private int PK_NCOLS;
   private int PK_ROW_BYTES;
   private int SYND_BYTES;
   private int GFMASK;
   private int[] poly;
   private final int defaultKeySize;
   private GF gf;
   private BENES benes;
   private boolean usePadding;
   private boolean countErrorIndices;
   private boolean usePivots;

   public int getIrrBytes() {
      return this.IRR_BYTES;
   }

   public int getCondBytes() {
      return this.COND_BYTES;
   }

   public int getPrivateKeySize() {
      return this.COND_BYTES + this.IRR_BYTES + this.SYS_N / 8 + 40;
   }

   public int getPublicKeySize() {
      return this.usePadding ? this.PK_NROWS * (this.SYS_N / 8 - (this.PK_NROWS - 1) / 8) : this.PK_NROWS * this.PK_NCOLS / 8;
   }

   public int getCipherTextSize() {
      return this.SYND_BYTES;
   }

   public CMCEEngine(int var1, int var2, int var3, int[] var4, boolean var5, int var6) {
      this.usePivots = var5;
      this.SYS_N = var2;
      this.SYS_T = var3;
      this.GFBITS = var1;
      this.poly = var4;
      this.defaultKeySize = var6;
      this.IRR_BYTES = this.SYS_T * 2;
      this.COND_BYTES = (1 << this.GFBITS - 4) * (2 * this.GFBITS - 1);
      this.PK_NROWS = this.SYS_T * this.GFBITS;
      this.PK_NCOLS = this.SYS_N - this.PK_NROWS;
      this.PK_ROW_BYTES = (this.PK_NCOLS + 7) / 8;
      this.SYND_BYTES = (this.PK_NROWS + 7) / 8;
      this.GFMASK = (1 << this.GFBITS) - 1;
      if (this.GFBITS == 12) {
         this.gf = new GF12();
         this.benes = new BENES12(this.SYS_N, this.SYS_T, this.GFBITS);
      } else {
         this.gf = new GF13();
         this.benes = new BENES13(this.SYS_N, this.SYS_T, this.GFBITS);
      }

      this.usePadding = this.SYS_T % 8 != 0;
      this.countErrorIndices = 1 << this.GFBITS > this.SYS_N;
   }

   public byte[] generate_public_key_from_private_key(byte[] var1) {
      byte[] var2 = new byte[this.getPublicKeySize()];
      short[] var3 = new short[1 << this.GFBITS];
      long[] var4 = new long[]{0L};
      int[] var5 = new int[1 << this.GFBITS];
      byte[] var6 = new byte[this.SYS_N / 8 + (1 << this.GFBITS) * 4];
      int var7 = var6.length - 32 - this.IRR_BYTES - (1 << this.GFBITS) * 4;
      SHAKEDigest var8 = new SHAKEDigest(256);
      var8.update((byte)64);
      var8.update(var1, 0, 32);
      var8.doFinal(var6, 0, var6.length);

      for (int var9 = 0; var9 < 1 << this.GFBITS; var9++) {
         var5[var9] = Utils.load4(var6, var7 + var9 * 4);
      }

      this.pk_gen(var2, var1, var5, var3, var4);
      return var2;
   }

   public byte[] decompress_private_key(byte[] var1) {
      byte[] var2 = new byte[this.getPrivateKeySize()];
      System.arraycopy(var1, 0, var2, 0, var1.length);
      byte[] var3 = new byte[this.SYS_N / 8 + (1 << this.GFBITS) * 4 + this.IRR_BYTES + 32];
      int var4 = 0;
      SHAKEDigest var5 = new SHAKEDigest(256);
      var5.update((byte)64);
      var5.update(var1, 0, 32);
      var5.doFinal(var3, 0, var3.length);
      if (var1.length <= 40) {
         short[] var6 = new short[this.SYS_T];
         byte[] var7 = new byte[this.IRR_BYTES];
         var4 = var3.length - 32 - this.IRR_BYTES;

         for (int var8 = 0; var8 < this.SYS_T; var8++) {
            var6[var8] = Utils.load_gf(var3, var4 + var8 * 2, this.GFMASK);
         }

         this.generate_irr_poly(var6);

         for (int var14 = 0; var14 < this.SYS_T; var14++) {
            Utils.store_gf(var7, var14 * 2, var6[var14]);
         }

         System.arraycopy(var7, 0, var2, 40, this.IRR_BYTES);
      }

      if (var1.length <= 40 + this.IRR_BYTES) {
         int[] var12 = new int[1 << this.GFBITS];
         short[] var13 = new short[1 << this.GFBITS];
         var4 = var3.length - 32 - this.IRR_BYTES - (1 << this.GFBITS) * 4;

         for (int var15 = 0; var15 < 1 << this.GFBITS; var15++) {
            var12[var15] = Utils.load4(var3, var4 + var15 * 4);
         }

         if (this.usePivots) {
            long[] var17 = new long[]{0L};
            this.pk_gen(null, var2, var12, var13, var17);
         } else {
            long[] var16 = new long[1 << this.GFBITS];

            for (int var9 = 0; var9 < 1 << this.GFBITS; var9++) {
               var16[var9] = var12[var9];
               var16[var9] <<= 31;
               var16[var9] |= var9;
               var16[var9] &= Long.MAX_VALUE;
            }

            sort64(var16, 0, var16.length);

            for (int var19 = 0; var19 < 1 << this.GFBITS; var19++) {
               var13[var19] = (short)(var16[var19] & this.GFMASK);
            }
         }

         byte[] var18 = new byte[this.COND_BYTES];
         controlbitsfrompermutation(var18, var13, this.GFBITS, 1 << this.GFBITS);
         System.arraycopy(var18, 0, var2, this.IRR_BYTES + 40, var18.length);
      }

      System.arraycopy(var3, 0, var2, this.getPrivateKeySize() - this.SYS_N / 8, this.SYS_N / 8);
      return var2;
   }

   public void kem_keypair(byte[] var1, byte[] var2, SecureRandom var3) {
      byte[] var4 = new byte[1];
      byte[] var5 = new byte[32];
      var4[0] = 64;
      var3.nextBytes(var5);
      byte[] var6 = new byte[this.SYS_N / 8 + (1 << this.GFBITS) * 4 + this.SYS_T * 2 + 32];
      byte var8 = 0;
      byte[] var9 = var5;
      long[] var10 = new long[]{0L};
      SHAKEDigest var11 = new SHAKEDigest(256);

      while (true) {
         var11.update(var4, 0, var4.length);
         var11.update(var5, 0, var5.length);
         var11.doFinal(var6, 0, var6.length);
         int var7 = var6.length - 32;
         var5 = Arrays.copyOfRange(var6, var7, var7 + 32);
         System.arraycopy(var9, 0, var2, 0, 32);
         var9 = Arrays.copyOfRange(var5, 0, 32);
         short[] var12 = new short[this.SYS_T];
         int var13 = var6.length - 32 - 2 * this.SYS_T;

         for (int var14 = 0; var14 < this.SYS_T; var14++) {
            var12[var14] = Utils.load_gf(var6, var13 + var14 * 2, this.GFMASK);
         }

         if (this.generate_irr_poly(var12) != -1) {
            var8 = 40;

            for (int var20 = 0; var20 < this.SYS_T; var20++) {
               Utils.store_gf(var2, var8 + var20 * 2, var12[var20]);
            }

            int[] var21 = new int[1 << this.GFBITS];
            var7 = var13 - (1 << this.GFBITS) * 4;

            for (int var15 = 0; var15 < 1 << this.GFBITS; var15++) {
               var21[var15] = Utils.load4(var6, var7 + var15 * 4);
            }

            short[] var22 = new short[1 << this.GFBITS];
            if (this.pk_gen(var1, var2, var21, var22, var10) != -1) {
               byte[] var16 = new byte[this.COND_BYTES];
               controlbitsfrompermutation(var16, var22, this.GFBITS, 1 << this.GFBITS);
               System.arraycopy(var16, 0, var2, this.IRR_BYTES + 40, var16.length);
               var7 -= this.SYS_N / 8;
               System.arraycopy(var6, var7, var2, var2.length - this.SYS_N / 8, this.SYS_N / 8);
               if (!this.usePivots) {
                  Utils.store8(var2, 32, 4294967295L);
               } else {
                  Utils.store8(var2, 32, var10[0]);
               }

               return;
            }
         }
      }
   }

   private void syndrome(byte[] var1, byte[] var2, byte[] var3) {
      short[] var4 = new short[this.SYS_N / 8];
      int var7 = 0;
      int var9 = this.PK_NROWS % 8;

      for (int var5 = 0; var5 < this.SYND_BYTES; var5++) {
         var1[var5] = 0;
      }

      for (int var10 = 0; var10 < this.PK_NROWS; var10++) {
         for (int var6 = 0; var6 < this.SYS_N / 8; var6++) {
            var4[var6] = 0;
         }

         for (int var11 = 0; var11 < this.PK_ROW_BYTES; var11++) {
            var4[this.SYS_N / 8 - this.PK_ROW_BYTES + var11] = var2[var7 + var11];
         }

         if (this.usePadding) {
            for (int var12 = this.SYS_N / 8 - 1; var12 >= this.SYS_N / 8 - this.PK_ROW_BYTES; var12--) {
               var4[var12] = (short)(((var4[var12] & 255) << var9 | (var4[var12 - 1] & 255) >>> 8 - var9) & 0xFF);
            }
         }

         var4[var10 / 8] = (short)(var4[var10 / 8] | 1 << var10 % 8);
         byte var8 = 0;

         for (int var13 = 0; var13 < this.SYS_N / 8; var13++) {
            var8 = (byte)(var8 ^ var4[var13] & var3[var13]);
         }

         var8 = (byte)(var8 ^ var8 >>> 4);
         var8 = (byte)(var8 ^ var8 >>> 2);
         var8 = (byte)(var8 ^ var8 >>> 1);
         var8 = (byte)(var8 & 1);
         var1[var10 / 8] = (byte)(var1[var10 / 8] | var8 << var10 % 8);
         var7 += this.PK_ROW_BYTES;
      }
   }

   private void generate_error_vector(byte[] var1, SecureRandom var2) {
      short[] var4 = new short[this.SYS_T * 2];
      short[] var5 = new short[this.SYS_T];
      byte[] var6 = new byte[this.SYS_T];

      int var13;
      do {
         do {
            if (!this.countErrorIndices) {
               byte[] var10 = new byte[this.SYS_T * 2];
               var2.nextBytes(var10);

               for (int var12 = 0; var12 < this.SYS_T; var12++) {
                  var5[var12] = Utils.load_gf(var10, var12 * 2, this.GFMASK);
               }
               break;
            }

            byte[] var3 = new byte[this.SYS_T * 4];
            var2.nextBytes(var3);

            for (int var7 = 0; var7 < this.SYS_T * 2; var7++) {
               var4[var7] = Utils.load_gf(var3, var7 * 2, this.GFMASK);
            }

            var13 = 0;

            for (int var8 = 0; var8 < this.SYS_T * 2 && var13 < this.SYS_T; var8++) {
               if (var4[var8] < this.SYS_N) {
                  var5[var13++] = var4[var8];
               }
            }
         } while (var13 < this.SYS_T);

         var13 = 0;

         for (int var16 = 1; var16 < this.SYS_T && !var13; var16++) {
            for (int var9 = 0; var9 < var16; var9++) {
               if (var5[var16] == var5[var9]) {
                  var13 = 1;
                  break;
               }
            }
         }
      } while (var13);

      for (int var14 = 0; var14 < this.SYS_T; var14++) {
         var6[var14] = (byte)(1 << (var5[var14] & 7));
      }

      for (short var15 = 0; var15 < this.SYS_N / 8; var15++) {
         var1[var15] = 0;

         for (int var17 = 0; var17 < this.SYS_T; var17++) {
            short var18 = same_mask32(var15, (short)(var5[var17] >> 3));
            var18 = (short)(var18 & 255);
            var1[var15] = (byte)(var1[var15] | var6[var17] & var18);
         }
      }
   }

   private void encrypt(byte[] var1, byte[] var2, byte[] var3, SecureRandom var4) {
      this.generate_error_vector(var3, var4);
      this.syndrome(var1, var2, var3);
   }

   public int kem_enc(byte[] var1, byte[] var2, byte[] var3, SecureRandom var4) {
      byte[] var5 = new byte[this.SYS_N / 8];
      int var8 = 0;
      if (this.usePadding) {
         var8 = this.check_pk_padding(var3);
      }

      this.encrypt(var1, var3, var5, var4);
      SHAKEDigest var9 = new SHAKEDigest(256);
      var9.update((byte)1);
      var9.update(var5, 0, var5.length);
      var9.update(var1, 0, var1.length);
      var9.doFinal(var2, 0, var2.length);
      if (!this.usePadding) {
         return 0;
      } else {
         byte var6 = (byte)var8;
         var6 = (byte)(var6 ^ 255);

         for (int var7 = 0; var7 < this.SYND_BYTES; var7++) {
            var1[var7] &= var6;
         }

         for (int var11 = 0; var11 < 32; var11++) {
            var2[var11] &= var6;
         }

         return var8;
      }
   }

   public int kem_dec(byte[] var1, byte[] var2, byte[] var3) {
      byte[] var4 = new byte[this.SYS_N / 8];
      byte[] var5 = new byte[1 + this.SYS_N / 8 + this.SYND_BYTES];
      int var7 = 0;
      if (this.usePadding) {
         var7 = this.check_c_padding(var2);
      }

      byte var9 = (byte)this.decrypt(var4, var3, var2);
      short var10 = var9;
      var10 = (short)(--var10 >> 8);
      var10 = (short)(var10 & 255);
      var5[0] = (byte)(var10 & 1);

      for (int var6 = 0; var6 < this.SYS_N / 8; var6++) {
         var5[1 + var6] = (byte)(~var10 & var3[var6 + 40 + this.IRR_BYTES + this.COND_BYTES] | var10 & var4[var6]);
      }

      for (int var12 = 0; var12 < this.SYND_BYTES; var12++) {
         var5[1 + this.SYS_N / 8 + var12] = var2[var12];
      }

      SHAKEDigest var11 = new SHAKEDigest(256);
      var11.update(var5, 0, var5.length);
      var11.doFinal(var1, 0, var1.length);
      if (!this.usePadding) {
         return 0;
      } else {
         byte var8 = (byte)var7;

         for (int var13 = 0; var13 < var1.length; var13++) {
            var1[var13] |= var8;
         }

         return var7;
      }
   }

   private int decrypt(byte[] var1, byte[] var2, byte[] var3) {
      short[] var4 = new short[this.SYS_T + 1];
      short[] var5 = new short[this.SYS_N];
      short[] var6 = new short[this.SYS_T * 2];
      short[] var7 = new short[this.SYS_T * 2];
      short[] var8 = new short[this.SYS_T + 1];
      short[] var9 = new short[this.SYS_N];
      byte[] var11 = new byte[this.SYS_N / 8];

      for (int var12 = 0; var12 < this.SYND_BYTES; var12++) {
         var11[var12] = var3[var12];
      }

      for (int var15 = this.SYND_BYTES; var15 < this.SYS_N / 8; var15++) {
         var11[var15] = 0;
      }

      for (int var16 = 0; var16 < this.SYS_T; var16++) {
         var4[var16] = Utils.load_gf(var2, 40 + var16 * 2, this.GFMASK);
      }

      var4[this.SYS_T] = 1;
      this.benes.support_gen(var5, var2);
      this.synd(var6, var4, var5, var11);
      this.bm(var8, var6);
      this.root(var9, var8, var5);

      for (int var17 = 0; var17 < this.SYS_N / 8; var17++) {
         var1[var17] = 0;
      }

      short var18 = 0;

      for (int var13 = 0; var13 < this.SYS_N; var13++) {
         short var10 = (short)(this.gf.gf_iszero(var9[var13]) & 1);
         var1[var13 / 8] = (byte)(var1[var13 / 8] | var10 << var13 % 8);
         var18 += var10;
      }

      this.synd(var7, var4, var5, var1);
      int var19 = var18 ^ this.SYS_T;

      for (int var14 = 0; var14 < this.SYS_T * 2; var14++) {
         var19 |= var6[var14] ^ var7[var14];
      }

      var19 = --var19 >> 15;
      var19 &= 1;
      if ((var19 ^ 1) != 0) {
      }

      return var19 ^ 1;
   }

   private static int min(short var0, int var1) {
      return var0 < var1 ? var0 : var1;
   }

   private void bm(short[] var1, short[] var2) {
      boolean var3 = false;
      short var4 = 0;
      short[] var7 = new short[this.SYS_T + 1];
      short[] var8 = new short[this.SYS_T + 1];
      short[] var9 = new short[this.SYS_T + 1];
      short var10 = 1;

      for (int var13 = 0; var13 < this.SYS_T + 1; var13++) {
         var8[var13] = var9[var13] = 0;
      }

      var9[1] = var8[0] = 1;

      for (short var15 = 0; var15 < 2 * this.SYS_T; var15++) {
         int var23 = 0;

         for (int var14 = 0; var14 <= min(var15, this.SYS_T); var14++) {
            var23 ^= this.gf.gf_mul_ext(var8[var14], var2[var15 - var14]);
         }

         short var11 = this.gf.gf_reduce(var23);
         short var6 = (short)(var11 - 1);
         var6 = (short)(var6 >> 15);
         var6 = (short)(var6 & 1);
         var6--;
         short var5 = (short)(var15 - 2 * var4);
         var5 = (short)(var5 >> 15);
         var5 = (short)(var5 & 1);
         var5 = (short)(--var5 & var6);

         for (int var25 = 0; var25 <= this.SYS_T; var25++) {
            var7[var25] = var8[var25];
         }

         short var12 = this.gf.gf_frac(var10, var11);

         for (int var26 = 0; var26 <= this.SYS_T; var26++) {
            var8[var26] = (short)(var8[var26] ^ this.gf.gf_mul(var12, var9[var26]) & var6);
         }

         var4 = (short)(var4 & ~var5 | var15 + 1 - var4 & var5);

         for (int var27 = this.SYS_T - 1; var27 >= 0; var27--) {
            var9[var27 + 1] = (short)(var9[var27] & ~var5 | var7[var27] & var5);
         }

         var9[0] = 0;
         var10 = (short)(var10 & ~var5 | var11 & var5);
      }

      for (int var24 = 0; var24 <= this.SYS_T; var24++) {
         var1[var24] = var8[this.SYS_T - var24];
      }
   }

   private void synd(short[] var1, short[] var2, short[] var3, byte[] var4) {
      short var5 = (short)(var4[0] & 1);
      short var6 = var3[0];
      short var7 = this.eval(var2, var6);
      short var8 = this.gf.gf_inv(this.gf.gf_sq(var7));
      short var9 = (short)(var8 & -var5);
      var1[0] = var9;

      for (int var10 = 1; var10 < 2 * this.SYS_T; var10++) {
         var9 = this.gf.gf_mul(var9, var6);
         var1[var10] = var9;
      }

      for (int var12 = 1; var12 < this.SYS_N; var12++) {
         var6 = (short)(var4[var12 / 8] >> var12 % 8 & 1);
         var7 = var3[var12];
         var8 = this.eval(var2, var7);
         var9 = this.gf.gf_inv(this.gf.gf_sq(var8));
         short var17 = this.gf.gf_mul(var9, var6);
         var1[0] ^= var17;

         for (int var11 = 1; var11 < 2 * this.SYS_T; var11++) {
            var17 = this.gf.gf_mul(var17, var7);
            var1[var11] ^= var17;
         }
      }
   }

   private int mov_columns(byte[][] var1, short[] var2, long[] var3) {
      long[] var11 = new long[64];
      long[] var12 = new long[32];
      long var19 = 1L;
      byte[] var21 = new byte[9];
      int var9 = this.PK_NROWS - 32;
      int var8 = var9 / 8;
      int var10 = var9 % 8;
      if (this.usePadding) {
         for (int var4 = 0; var4 < 32; var4++) {
            for (int var5 = 0; var5 < 9; var5++) {
               var21[var5] = var1[var9 + var4][var8 + var5];
            }

            for (int var25 = 0; var25 < 8; var25++) {
               var21[var25] = (byte)((var21[var25] & 255) >> var10 | var21[var25 + 1] << 8 - var10);
            }

            var11[var4] = Utils.load8(var21, 0);
         }
      } else {
         for (int var22 = 0; var22 < 32; var22++) {
            var11[var22] = Utils.load8(var1[var9 + var22], var8);
         }
      }

      var3[0] = 0L;

      for (int var23 = 0; var23 < 32; var23++) {
         long var13 = var11[var23];

         for (int var26 = var23 + 1; var26 < 32; var26++) {
            var13 |= var11[var26];
         }

         if (var13 == 0L) {
            return -1;
         }

         int var7;
         var12[var23] = var7 = ctz(var13);
         var3[0] |= var19 << (int)var12[var23];

         for (int var27 = var23 + 1; var27 < 32; var27++) {
            long var17 = var11[var23] >> var7 & 1L;
            var11[var23] ^= var11[var27] & --var17;
         }

         for (int var28 = var23 + 1; var28 < 32; var28++) {
            long var41 = var11[var28] >> var7 & 1L;
            var41 = -var41;
            var11[var28] ^= var11[var23] & var41;
         }
      }

      for (int var29 = 0; var29 < 32; var29++) {
         for (int var6 = var29 + 1; var6 < 64; var6++) {
            long var15 = var2[var9 + var29] ^ var2[var9 + var6];
            var15 &= same_mask64((short)var6, (short)var12[var29]);
            var2[var9 + var29] = (short)(var2[var9 + var29] ^ var15);
            var2[var9 + var6] = (short)(var2[var9 + var6] ^ var15);
         }
      }

      for (int var24 = 0; var24 < this.PK_NROWS; var24++) {
         long var34;
         if (!this.usePadding) {
            var34 = Utils.load8(var1[var24], var8);
         } else {
            for (int var31 = 0; var31 < 9; var31++) {
               var21[var31] = var1[var24][var8 + var31];
            }

            for (int var32 = 0; var32 < 8; var32++) {
               var21[var32] = (byte)((var21[var32] & 255) >> var10 | var21[var32 + 1] << 8 - var10);
            }

            var34 = Utils.load8(var21, 0);
         }

         for (int var30 = 0; var30 < 32; var30++) {
            long var37 = var34 >> var30;
            var37 ^= var34 >> (int)var12[var30];
            var37 &= 1L;
            var34 ^= var37 << (int)var12[var30];
            var34 ^= var37 << var30;
         }

         if (this.usePadding) {
            Utils.store8(var21, 0, var34);
            var1[var24][var8 + 8] = (byte)((var1[var24][var8 + 8] & 255) >>> var10 << var10 | (var21[7] & 255) >>> 8 - var10);
            var1[var24][var8 + 0] = (byte)((var21[0] & 255) << var10 | (var1[var24][var8] & 255) << 8 - var10 >>> 8 - var10);

            for (int var33 = 7; var33 >= 1; var33--) {
               var1[var24][var8 + var33] = (byte)((var21[var33] & 255) << var10 | (var21[var33 - 1] & 255) >>> 8 - var10);
            }
         } else {
            Utils.store8(var1[var24], var8, var34);
         }
      }

      return 0;
   }

   private static int ctz(long var0) {
      long var2 = 72340172838076673L;
      long var4 = 0L;
      long var6 = ~var0;

      for (int var8 = 0; var8 < 8; var8++) {
         var2 &= var6 >>> var8;
         var4 += var2;
      }

      long var14 = var4 & 578721382704613384L;
      var14 |= var14 >>> 1;
      var14 |= var14 >>> 2;
      long var13 = var4 >>> 8;
      long var17 = var4 + (var13 & var14);

      for (int var12 = 2; var12 < 8; var12++) {
         var14 &= var14 >>> 8;
         var13 >>>= 8;
         var17 += var13 & var14;
      }

      return (int)var17 & 0xFF;
   }

   private static long same_mask64(short var0, short var1) {
      long var2 = var0 ^ var1;
      var2 = --var2 >>> 63;
      return -var2;
   }

   private static byte same_mask32(short var0, short var1) {
      int var2 = var0 ^ var1;
      var2 = --var2 >>> 31;
      var2 = -var2;
      return (byte)(var2 & 0xFF);
   }

   private static void layer(short[] var0, byte[] var1, int var2, int var3, int var4) {
      int var7 = 1 << var3;
      int var8 = 0;

      for (int var5 = 0; var5 < var4; var5 += var7 * 2) {
         for (int var6 = 0; var6 < var7; var6++) {
            int var9 = var0[var5 + var6] ^ var0[var5 + var6 + var7];
            int var10 = var1[var2 + (var8 >> 3)] >> (var8 & 7) & 1;
            var10 = -var10;
            var9 &= var10;
            var0[var5 + var6] = (short)(var0[var5 + var6] ^ var9);
            var0[var5 + var6 + var7] = (short)(var0[var5 + var6 + var7] ^ var9);
            var8++;
         }
      }
   }

   private static void controlbitsfrompermutation(byte[] var0, short[] var1, long var2, long var4) {
      int[] var6 = new int[(int)(2L * var4)];
      short[] var7 = new short[(int)var4];

      short var8;
      do {
         for (int var9 = 0; var9 < ((2L * var2 - 1L) * var4 / 2L + 7L) / 8L; var9++) {
            var0[var9] = 0;
         }

         cbrecursion(var0, 0L, 1L, var1, 0, var2, var4, var6);

         for (int var11 = 0; var11 < var4; var11++) {
            var7[var11] = (short)var11;
         }

         int var10 = 0;

         for (int var12 = 0; var12 < var2; var12++) {
            layer(var7, var0, var10, var12, (int)var4);
            var10 = (int)(var10 + (var4 >> 4));
         }

         for (int var13 = (int)(var2 - 2L); var13 >= 0; var13--) {
            layer(var7, var0, var10, var13, (int)var4);
            var10 = (int)(var10 + (var4 >> 4));
         }

         var8 = 0;

         for (int var14 = 0; var14 < var4; var14++) {
            var8 = (short)(var8 | var1[var14] ^ var7[var14]);
         }
      } while (var8 != 0);
   }

   static short get_q_short(int[] var0, int var1) {
      int var2 = var1 / 2;
      return var1 % 2 == 0 ? (short)var0[var2] : (short)((var0[var2] & -65536) >> 16);
   }

   static void cbrecursion(byte[] var0, long var1, long var3, short[] var5, int var6, long var7, long var9, int[] var11) {
      if (var7 == 1L) {
         var0[(int)(var1 >> 3)] = (byte)(var0[(int)(var1 >> 3)] ^ get_q_short(var11, var6) << (int)(var1 & 7L));
      } else {
         if (var5 != null) {
            for (long var12 = 0L; var12 < var9; var12++) {
               var11[(int)var12] = (var5[(int)var12] ^ 1) << 16 | var5[(int)(var12 ^ 1L)];
            }
         } else {
            for (long var27 = 0L; var27 < var9; var27++) {
               var11[(int)var27] = (get_q_short(var11, (int)(var6 + var27)) ^ 1) << 16 | get_q_short(var11, (int)(var6 + (var27 ^ 1L)));
            }
         }

         sort32(var11, 0, (int)var9);

         for (long var28 = 0L; var28 < var9; var28++) {
            int var20 = var11[(int)var28];
            int var21 = var20 & 65535;
            int var22 = var21;
            if (var28 < var21) {
               var22 = (int)var28;
            }

            var11[(int)(var9 + var28)] = var21 << 16 | var22;
         }

         for (long var29 = 0L; var29 < var9; var29++) {
            var11[(int)var29] = (int)(var11[(int)var29] << 16 | var29);
         }

         sort32(var11, 0, (int)var9);

         for (long var30 = 0L; var30 < var9; var30++) {
            var11[(int)var30] = (var11[(int)var30] << 16) + (var11[(int)(var9 + var30)] >> 16);
         }

         sort32(var11, 0, (int)var9);
         if (var7 <= 10L) {
            for (long var31 = 0L; var31 < var9; var31++) {
               var11[(int)(var9 + var31)] = (var11[(int)var31] & 65535) << 10 | var11[(int)(var9 + var31)] & 1023;
            }

            for (long var14 = 1L; var14 < var7 - 1L; var14++) {
               for (long var32 = 0L; var32 < var9; var32++) {
                  var11[(int)var32] = (int)((var11[(int)(var9 + var32)] & -1024) << 6 | var32);
               }

               sort32(var11, 0, (int)var9);

               for (long var33 = 0L; var33 < var9; var33++) {
                  var11[(int)var33] = var11[(int)var33] << 20 | var11[(int)(var9 + var33)];
               }

               sort32(var11, 0, (int)var9);

               for (long var34 = 0L; var34 < var9; var34++) {
                  int var49 = var11[(int)var34] & 1048575;
                  int var54 = var11[(int)var34] & 1047552 | var11[(int)(var9 + var34)] & 1023;
                  if (var49 < var54) {
                     var54 = var49;
                  }

                  var11[(int)(var9 + var34)] = var54;
               }
            }

            for (long var35 = 0L; var35 < var9; var35++) {
               var11[(int)(var9 + var35)] = var11[(int)(var9 + var35)] & 1023;
            }
         } else {
            for (long var36 = 0L; var36 < var9; var36++) {
               var11[(int)(var9 + var36)] = var11[(int)var36] << 16 | var11[(int)(var9 + var36)] & 65535;
            }

            for (long var45 = 1L; var45 < var7 - 1L; var45++) {
               for (long var37 = 0L; var37 < var9; var37++) {
                  var11[(int)var37] = (int)(var11[(int)(var9 + var37)] & -65536 | var37);
               }

               sort32(var11, 0, (int)var9);

               for (long var38 = 0L; var38 < var9; var38++) {
                  var11[(int)var38] = var11[(int)var38] << 16 | var11[(int)(var9 + var38)] & 65535;
               }

               if (var45 < var7 - 2L) {
                  for (long var39 = 0L; var39 < var9; var39++) {
                     var11[(int)(var9 + var39)] = var11[(int)var39] & -65536 | var11[(int)(var9 + var39)] >> 16;
                  }

                  sort32(var11, (int)var9, (int)(var9 * 2L));

                  for (long var40 = 0L; var40 < var9; var40++) {
                     var11[(int)(var9 + var40)] = var11[(int)(var9 + var40)] << 16 | var11[(int)var40] & 65535;
                  }
               }

               sort32(var11, 0, (int)var9);

               for (long var41 = 0L; var41 < var9; var41++) {
                  int var50 = var11[(int)(var9 + var41)] & -65536 | var11[(int)var41] & 65535;
                  if (var50 < var11[(int)(var9 + var41)]) {
                     var11[(int)(var9 + var41)] = var50;
                  }
               }
            }

            for (long var42 = 0L; var42 < var9; var42++) {
               var11[(int)(var9 + var42)] = var11[(int)(var9 + var42)] & 65535;
            }
         }

         if (var5 != null) {
            for (long var43 = 0L; var43 < var9; var43++) {
               var11[(int)var43] = (int)((var5[(int)var43] << 16) + var43);
            }
         } else {
            for (long var44 = 0L; var44 < var9; var44++) {
               var11[(int)var44] = (int)((get_q_short(var11, (int)(var6 + var44)) << 16) + var44);
            }
         }

         sort32(var11, 0, (int)var9);

         for (long var16 = 0L; var16 < var9 / 2L; var16++) {
            long var51 = 2L * var16;
            int var55 = var11[(int)(var9 + var51)] & 1;
            int var23 = (int)(var51 + var55);
            int var24 = var23 ^ 1;
            var0[(int)(var1 >> 3)] = (byte)(var0[(int)(var1 >> 3)] ^ var55 << (int)(var1 & 7L));
            var1 += var3;
            var11[(int)(var9 + var51)] = var11[(int)var51] << 16 | var23;
            var11[(int)(var9 + var51 + 1L)] = var11[(int)(var51 + 1L)] << 16 | var24;
         }

         sort32(var11, (int)var9, (int)(var9 * 2L));
         var1 += (2L * var7 - 3L) * var3 * (var9 / 2L);

         for (long var18 = 0L; var18 < var9 / 2L; var18++) {
            long var52 = 2L * var18;
            int var56 = var11[(int)(var9 + var52)] & 1;
            int var57 = (int)(var52 + var56);
            int var58 = var57 ^ 1;
            var0[(int)(var1 >> 3)] = (byte)(var0[(int)(var1 >> 3)] ^ var56 << (int)(var1 & 7L));
            var1 += var3;
            var11[(int)var52] = var57 << 16 | var11[(int)(var9 + var52)] & 65535;
            var11[(int)(var52 + 1L)] = var58 << 16 | var11[(int)(var9 + var52 + 1L)] & 65535;
         }

         sort32(var11, 0, (int)var9);
         var1 -= (2L * var7 - 2L) * var3 * (var9 / 2L);
         short[] var53 = new short[(int)var9 * 4];

         for (long var46 = 0L; var46 < var9 * 2L; var46++) {
            var53[(int)(var46 * 2L + 0L)] = (short)var11[(int)var46];
            var53[(int)(var46 * 2L + 1L)] = (short)((var11[(int)var46] & -65536) >> 16);
         }

         for (long var48 = 0L; var48 < var9 / 2L; var48++) {
            var53[(int)var48] = (short)((var11[(int)(2L * var48)] & 65535) >>> 1);
            var53[(int)(var48 + var9 / 2L)] = (short)((var11[(int)(2L * var48 + 1L)] & 65535) >>> 1);
         }

         for (long var47 = 0L; var47 < var9 / 2L; var47++) {
            var11[(int)(var9 + var9 / 4L + var47)] = var53[(int)(var47 * 2L + 1L)] << 16 | var53[(int)(var47 * 2L)];
         }

         cbrecursion(var0, var1, var3 * 2L, null, (int)(var9 + var9 / 4L) * 2, var7 - 1L, var9 / 2L, var11);
         cbrecursion(var0, var1 + var3, var3 * 2L, null, (int)((var9 + var9 / 4L) * 2L + var9 / 2L), var7 - 1L, var9 / 2L, var11);
      }
   }

   private int pk_gen(byte[] var1, byte[] var2, int[] var3, short[] var4, long[] var5) {
      short[] var6 = new short[this.SYS_T + 1];
      var6[this.SYS_T] = 1;

      for (int var7 = 0; var7 < this.SYS_T; var7++) {
         var6[var7] = Utils.load_gf(var2, 40 + var7 * 2, this.GFMASK);
      }

      long[] var10 = new long[1 << this.GFBITS];

      for (int var20 = 0; var20 < 1 << this.GFBITS; var20++) {
         var10[var20] = var3[var20];
         var10[var20] <<= 31;
         var10[var20] |= var20;
         var10[var20] &= Long.MAX_VALUE;
      }

      sort64(var10, 0, var10.length);

      for (int var21 = 1; var21 < 1 << this.GFBITS; var21++) {
         if (var10[var21 - 1] >> 31 == var10[var21] >> 31) {
            return -1;
         }
      }

      short[] var11 = new short[this.SYS_N];

      for (int var22 = 0; var22 < 1 << this.GFBITS; var22++) {
         var4[var22] = (short)(var10[var22] & this.GFMASK);
      }

      for (int var23 = 0; var23 < this.SYS_N; var23++) {
         var11[var23] = Utils.bitrev(var4[var23], this.GFBITS);
      }

      short[] var12 = new short[this.SYS_N];
      this.root(var12, var6, var11);

      for (int var24 = 0; var24 < this.SYS_N; var24++) {
         var12[var24] = this.gf.gf_inv(var12[var24]);
      }

      byte[][] var13 = new byte[this.PK_NROWS][this.SYS_N / 8];

      for (int var25 = 0; var25 < this.PK_NROWS; var25++) {
         for (int var8 = 0; var8 < this.SYS_N / 8; var8++) {
            var13[var25][var8] = 0;
         }
      }

      int var26;
      for (var26 = 0; var26 < this.SYS_T; var26++) {
         for (byte var29 = 0; var29 < this.SYS_N; var29 += 8) {
            for (int var9 = 0; var9 < this.GFBITS; var9++) {
               byte var14 = (byte)(var12[var29 + 7] >>> var9 & 1);
               var14 = (byte)(var14 << 1);
               var14 = (byte)(var14 | var12[var29 + 6] >>> var9 & 1);
               var14 = (byte)(var14 << 1);
               var14 = (byte)(var14 | var12[var29 + 5] >>> var9 & 1);
               var14 = (byte)(var14 << 1);
               var14 = (byte)(var14 | var12[var29 + 4] >>> var9 & 1);
               var14 = (byte)(var14 << 1);
               var14 = (byte)(var14 | var12[var29 + 3] >>> var9 & 1);
               var14 = (byte)(var14 << 1);
               var14 = (byte)(var14 | var12[var29 + 2] >>> var9 & 1);
               var14 = (byte)(var14 << 1);
               var14 = (byte)(var14 | var12[var29 + 1] >>> var9 & 1);
               var14 = (byte)(var14 << 1);
               var14 = (byte)(var14 | var12[var29 + 0] >>> var9 & 1);
               var13[var26 * this.GFBITS + var9][var29 / 8] = var14;
            }
         }

         for (int var30 = 0; var30 < this.SYS_N; var30++) {
            var12[var30] = this.gf.gf_mul(var12[var30], var11[var30]);
         }
      }

      for (int var15 = 0; var15 < this.PK_NROWS; var15++) {
         var26 = var15 >>> 3;
         int var31 = var15 & 7;
         if (this.usePivots && var15 == this.PK_NROWS - 32 && this.mov_columns(var13, var4, var5) != 0) {
            return -1;
         }

         for (int var33 = var15 + 1; var33 < this.PK_NROWS; var33++) {
            byte var17 = (byte)(var13[var15][var26] ^ var13[var33][var26]);
            var17 = (byte)(var17 >> var31);
            var17 = (byte)(var17 & 1);
            var17 = (byte)(-var17);

            for (int var16 = 0; var16 < this.SYS_N / 8; var16++) {
               var13[var15][var16] = (byte)(var13[var15][var16] ^ var13[var33][var16] & var17);
            }
         }

         if ((var13[var15][var26] >> var31 & 1) == 0) {
            return -1;
         }

         for (int var34 = 0; var34 < this.PK_NROWS; var34++) {
            if (var34 != var15) {
               byte var53 = (byte)(var13[var34][var26] >> var31);
               var53 = (byte)(var53 & 1);
               var53 = (byte)(-var53);

               for (int var49 = 0; var49 < this.SYS_N / 8; var49++) {
                  var13[var34][var49] = (byte)(var13[var34][var49] ^ var13[var15][var49] & var53);
               }
            }
         }
      }

      if (var1 != null) {
         if (this.usePadding) {
            int var18 = 0;
            int var19 = this.PK_NROWS % 8;
            if (var19 == 0) {
               System.arraycopy(var13[var26], (this.PK_NROWS - 1) / 8, var1, var18, this.SYS_N / 8);
               var18 += this.SYS_N / 8;
            } else {
               for (int var27 = 0; var27 < this.PK_NROWS; var27++) {
                  int var32;
                  for (var32 = (this.PK_NROWS - 1) / 8; var32 < this.SYS_N / 8 - 1; var32++) {
                     var1[var18++] = (byte)((var13[var27][var32] & 255) >>> var19 | var13[var27][var32 + 1] << 8 - var19);
                  }

                  var1[var18++] = (byte)((var13[var27][var32] & 255) >>> var19);
               }
            }
         } else {
            int var57 = (this.SYS_N - this.PK_NROWS + 7) / 8;

            for (int var28 = 0; var28 < this.PK_NROWS; var28++) {
               System.arraycopy(var13[var28], this.PK_NROWS / 8, var1, var57 * var28, var57);
            }
         }
      }

      return 0;
   }

   private short eval(short[] var1, short var2) {
      short var3 = var1[this.SYS_T];

      for (int var4 = this.SYS_T - 1; var4 >= 0; var4--) {
         var3 = (short)(this.gf.gf_mul(var3, var2) ^ var1[var4]);
      }

      return var3;
   }

   private void root(short[] var1, short[] var2, short[] var3) {
      for (int var4 = 0; var4 < this.SYS_N; var4++) {
         var1[var4] = this.eval(var2, var3[var4]);
      }
   }

   private int generate_irr_poly(short[] var1) {
      short[][] var2 = new short[this.SYS_T + 1][this.SYS_T];
      var2[0][0] = 1;
      System.arraycopy(var1, 0, var2[1], 0, this.SYS_T);
      int[] var3 = new int[this.SYS_T * 2 - 1];

      byte var4;
      for (var4 = 2; var4 < this.SYS_T; var4 += 2) {
         this.gf.gf_sqr_poly(this.SYS_T, this.poly, var2[var4], var2[var4 >>> 1], var3);
         this.gf.gf_mul_poly(this.SYS_T, this.poly, var2[var4 + 1], var2[var4], var1, var3);
      }

      if (var4 == this.SYS_T) {
         this.gf.gf_sqr_poly(this.SYS_T, this.poly, var2[var4], var2[var4 >>> 1], var3);
      }

      for (int var8 = 0; var8 < this.SYS_T; var8++) {
         for (int var9 = var8 + 1; var9 < this.SYS_T; var9++) {
            short var5 = this.gf.gf_iszero(var2[var8][var8]);

            for (int var6 = var8; var6 < this.SYS_T + 1; var6++) {
               var2[var6][var8] = (short)(var2[var6][var8] ^ (short)(var2[var6][var9] & var5));
            }
         }

         if (var2[var8][var8] == 0) {
            return -1;
         }

         var4 = this.gf.gf_inv(var2[var8][var8]);

         for (int var11 = var8; var11 < this.SYS_T + 1; var11++) {
            var2[var11][var8] = this.gf.gf_mul(var2[var11][var8], var4);
         }

         for (int var12 = 0; var12 < this.SYS_T; var12++) {
            if (var12 != var8) {
               short var13 = var2[var8][var12];

               for (int var7 = var8; var7 <= this.SYS_T; var7++) {
                  var2[var7][var12] = (short)(var2[var7][var12] ^ this.gf.gf_mul(var2[var7][var8], var13));
               }
            }
         }
      }

      System.arraycopy(var2[this.SYS_T], 0, var1, 0, this.SYS_T);
      return 0;
   }

   int check_pk_padding(byte[] var1) {
      byte var2 = 0;

      for (int var3 = 0; var3 < this.PK_NROWS; var3++) {
         var2 |= var1[var3 * this.PK_ROW_BYTES + this.PK_ROW_BYTES - 1];
      }

      var2 = (byte)((var2 & 255) >>> this.PK_NCOLS % 8);
      var2 = (byte)((--var2 & 255) >>> 7);
      return var2 - 1;
   }

   int check_c_padding(byte[] var1) {
      byte var2 = (byte)((var1[this.SYND_BYTES - 1] & 255) >>> this.PK_NROWS % 8);
      var2 = (byte)((--var2 & 255) >>> 7);
      return var2 - 1;
   }

   public int getDefaultSessionKeySize() {
      return this.defaultKeySize;
   }

   private static void sort32(int[] var0, int var1, int var2) {
      int var8 = var2 - var1;
      if (var8 >= 2) {
         byte var3 = 1;

         while (var3 < var8 - var3) {
            var3 += var3;
         }

         for (byte var4 = var3; var4 > 0; var4 >>>= 1) {
            for (int var7 = 0; var7 < var8 - var4; var7++) {
               if ((var7 & var4) == 0) {
                  int var9 = var0[var1 + var7 + var4] ^ var0[var1 + var7];
                  int var10 = var0[var1 + var7 + var4] - var0[var1 + var7];
                  var10 ^= var9 & (var10 ^ var0[var1 + var7 + var4]);
                  var10 >>= 31;
                  var10 &= var9;
                  var0[var1 + var7] = var0[var1 + var7] ^ var10;
                  var0[var1 + var7 + var4] = var0[var1 + var7 + var4] ^ var10;
               }
            }

            int var12 = 0;

            for (byte var5 = var3; var5 > var4; var5 >>>= 1) {
               for (; var12 < var8 - var5; var12++) {
                  if ((var12 & var4) == 0) {
                     int var13 = var0[var1 + var12 + var4];

                     for (byte var6 = var5; var6 > var4; var6 >>>= 1) {
                        int var17 = var0[var1 + var12 + var6] ^ var13;
                        int var11 = var0[var1 + var12 + var6] - var13;
                        var11 ^= var17 & (var11 ^ var0[var1 + var12 + var6]);
                        var11 >>= 31;
                        var11 &= var17;
                        var13 ^= var11;
                        var0[var1 + var12 + var6] = var0[var1 + var12 + var6] ^ var11;
                     }

                     var0[var1 + var12 + var4] = var13;
                  }
               }
            }
         }
      }
   }

   private static void sort64(long[] var0, int var1, int var2) {
      int var8 = var2 - var1;
      if (var8 >= 2) {
         byte var3 = 1;

         while (var3 < var8 - var3) {
            var3 += var3;
         }

         for (byte var4 = var3; var4 > 0; var4 >>>= 1) {
            for (int var7 = 0; var7 < var8 - var4; var7++) {
               if ((var7 & var4) == 0) {
                  long var9 = var0[var1 + var7 + var4] - var0[var1 + var7];
                  var9 >>>= 63;
                  var9 = -var9;
                  var9 &= var0[var1 + var7] ^ var0[var1 + var7 + var4];
                  var0[var1 + var7] = var0[var1 + var7] ^ var9;
                  var0[var1 + var7 + var4] = var0[var1 + var7 + var4] ^ var9;
               }
            }

            int var13 = 0;

            for (byte var5 = var3; var5 > var4; var5 >>>= 1) {
               for (; var13 < var8 - var5; var13++) {
                  if ((var13 & var4) == 0) {
                     long var17 = var0[var1 + var13 + var4];

                     for (byte var6 = var5; var6 > var4; var6 >>>= 1) {
                        long var11 = var0[var1 + var13 + var6] - var17;
                        var11 >>>= 63;
                        var11 = -var11;
                        var11 &= var17 ^ var0[var1 + var13 + var6];
                        var17 ^= var11;
                        var0[var1 + var13 + var6] = var0[var1 + var13 + var6] ^ var11;
                     }

                     var0[var1 + var13 + var4] = var17;
                  }
               }
            }
         }
      }
   }
}
