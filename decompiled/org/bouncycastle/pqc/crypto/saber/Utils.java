package org.bouncycastle.pqc.crypto.saber;

class Utils {
   private final int SABER_N;
   private final int SABER_L;
   private final int SABER_ET;
   private final int SABER_POLYBYTES;
   private final int SABER_EP;
   private final int SABER_KEYBYTES;
   private final boolean usingEffectiveMasking;

   public Utils(SABEREngine var1) {
      this.SABER_N = var1.getSABER_N();
      this.SABER_L = var1.getSABER_L();
      this.SABER_ET = var1.getSABER_ET();
      this.SABER_POLYBYTES = var1.getSABER_POLYBYTES();
      this.SABER_EP = var1.getSABER_EP();
      this.SABER_KEYBYTES = var1.getSABER_KEYBYTES();
      this.usingEffectiveMasking = var1.usingEffectiveMasking;
   }

   public void POLT2BS(byte[] var1, int var2, short[] var3) {
      if (this.SABER_ET == 3) {
         for (short var4 = 0; var4 < this.SABER_N / 8; var4++) {
            short var5 = (short)(3 * var4);
            short var6 = (short)(8 * var4);
            var1[var2 + var5 + 0] = (byte)(var3[var6 + 0] & 7 | (var3[var6 + 1] & 7) << 3 | (var3[var6 + 2] & 3) << 6);
            var1[var2 + var5 + 1] = (byte)(var3[var6 + 2] >> 2 & 1 | (var3[var6 + 3] & 7) << 1 | (var3[var6 + 4] & 7) << 4 | (var3[var6 + 5] & 1) << 7);
            var1[var2 + var5 + 2] = (byte)(var3[var6 + 5] >> 1 & 3 | (var3[var6 + 6] & 7) << 2 | (var3[var6 + 7] & 7) << 5);
         }
      } else if (this.SABER_ET == 4) {
         for (short var7 = 0; var7 < this.SABER_N / 2; var7++) {
            short var10 = (short)(2 * var7);
            var1[var2 + var7] = (byte)(var3[var10] & 15 | (var3[var10 + 1] & 15) << 4);
         }
      } else if (this.SABER_ET == 6) {
         for (short var8 = 0; var8 < this.SABER_N / 4; var8++) {
            short var9 = (short)(3 * var8);
            short var11 = (short)(4 * var8);
            var1[var2 + var9 + 0] = (byte)(var3[var11 + 0] & 63 | (var3[var11 + 1] & 3) << 6);
            var1[var2 + var9 + 1] = (byte)(var3[var11 + 1] >> 2 & 15 | (var3[var11 + 2] & 15) << 4);
            var1[var2 + var9 + 2] = (byte)(var3[var11 + 2] >> 4 & 3 | (var3[var11 + 3] & 63) << 2);
         }
      }
   }

   public void BS2POLT(byte[] var1, int var2, short[] var3) {
      if (this.SABER_ET == 3) {
         for (short var4 = 0; var4 < this.SABER_N / 8; var4++) {
            short var5 = (short)(3 * var4);
            short var6 = (short)(8 * var4);
            var3[var6 + 0] = (short)(var1[var2 + var5 + 0] & 7);
            var3[var6 + 1] = (short)(var1[var2 + var5 + 0] >> 3 & 7);
            var3[var6 + 2] = (short)(var1[var2 + var5 + 0] >> 6 & 3 | (var1[var2 + var5 + 1] & 1) << 2);
            var3[var6 + 3] = (short)(var1[var2 + var5 + 1] >> 1 & 7);
            var3[var6 + 4] = (short)(var1[var2 + var5 + 1] >> 4 & 7);
            var3[var6 + 5] = (short)(var1[var2 + var5 + 1] >> 7 & 1 | (var1[var2 + var5 + 2] & 3) << 1);
            var3[var6 + 6] = (short)(var1[var2 + var5 + 2] >> 2 & 7);
            var3[var6 + 7] = (short)(var1[var2 + var5 + 2] >> 5 & 7);
         }
      } else if (this.SABER_ET == 4) {
         for (short var7 = 0; var7 < this.SABER_N / 2; var7++) {
            short var10 = (short)(2 * var7);
            var3[var10] = (short)(var1[var2 + var7] & 15);
            var3[var10 + 1] = (short)(var1[var2 + var7] >> 4 & 15);
         }
      } else if (this.SABER_ET == 6) {
         for (short var8 = 0; var8 < this.SABER_N / 4; var8++) {
            short var9 = (short)(3 * var8);
            short var11 = (short)(4 * var8);
            var3[var11 + 0] = (short)(var1[var2 + var9 + 0] & 63);
            var3[var11 + 1] = (short)(var1[var2 + var9 + 0] >> 6 & 3 | (var1[var2 + var9 + 1] & 15) << 2);
            var3[var11 + 2] = (short)((var1[var2 + var9 + 1] & 255) >> 4 | (var1[var2 + var9 + 2] & 3) << 4);
            var3[var11 + 3] = (short)((var1[var2 + var9 + 2] & 255) >> 2);
         }
      }
   }

   private void POLq2BS(byte[] var1, int var2, short[] var3) {
      if (!this.usingEffectiveMasking) {
         for (short var4 = 0; var4 < this.SABER_N / 8; var4++) {
            short var5 = (short)(13 * var4);
            short var6 = (short)(8 * var4);
            var1[var2 + var5 + 0] = (byte)(var3[var6 + 0] & 255);
            var1[var2 + var5 + 1] = (byte)(var3[var6 + 0] >> 8 & 31 | (var3[var6 + 1] & 7) << 5);
            var1[var2 + var5 + 2] = (byte)(var3[var6 + 1] >> 3 & 0xFF);
            var1[var2 + var5 + 3] = (byte)(var3[var6 + 1] >> 11 & 3 | (var3[var6 + 2] & 63) << 2);
            var1[var2 + var5 + 4] = (byte)(var3[var6 + 2] >> 6 & 127 | (var3[var6 + 3] & 1) << 7);
            var1[var2 + var5 + 5] = (byte)(var3[var6 + 3] >> 1 & 0xFF);
            var1[var2 + var5 + 6] = (byte)(var3[var6 + 3] >> 9 & 15 | (var3[var6 + 4] & 15) << 4);
            var1[var2 + var5 + 7] = (byte)(var3[var6 + 4] >> 4 & 0xFF);
            var1[var2 + var5 + 8] = (byte)(var3[var6 + 4] >> 12 & 1 | (var3[var6 + 5] & 127) << 1);
            var1[var2 + var5 + 9] = (byte)(var3[var6 + 5] >> 7 & 63 | (var3[var6 + 6] & 3) << 6);
            var1[var2 + var5 + 10] = (byte)(var3[var6 + 6] >> 2 & 0xFF);
            var1[var2 + var5 + 11] = (byte)(var3[var6 + 6] >> 10 & 7 | (var3[var6 + 7] & 31) << 3);
            var1[var2 + var5 + 12] = (byte)(var3[var6 + 7] >> 5 & 0xFF);
         }
      } else {
         for (short var7 = 0; var7 < this.SABER_N / 2; var7++) {
            short var8 = (short)(3 * var7);
            short var9 = (short)(2 * var7);
            var1[var2 + var8 + 0] = (byte)(var3[var9 + 0] & 255);
            var1[var2 + var8 + 1] = (byte)(var3[var9 + 0] >> 8 & 15 | (var3[var9 + 1] & 15) << 4);
            var1[var2 + var8 + 2] = (byte)(var3[var9 + 1] >> 4 & 0xFF);
         }
      }
   }

   private void BS2POLq(byte[] var1, int var2, short[] var3) {
      if (!this.usingEffectiveMasking) {
         for (short var4 = 0; var4 < this.SABER_N / 8; var4++) {
            short var5 = (short)(13 * var4);
            short var6 = (short)(8 * var4);
            var3[var6 + 0] = (short)(var1[var2 + var5 + 0] & 255 | (var1[var2 + var5 + 1] & 31) << 8);
            var3[var6 + 1] = (short)(var1[var2 + var5 + 1] >> 5 & 7 | (var1[var2 + var5 + 2] & 255) << 3 | (var1[var2 + var5 + 3] & 3) << 11);
            var3[var6 + 2] = (short)(var1[var2 + var5 + 3] >> 2 & 63 | (var1[var2 + var5 + 4] & 127) << 6);
            var3[var6 + 3] = (short)(var1[var2 + var5 + 4] >> 7 & 1 | (var1[var2 + var5 + 5] & 255) << 1 | (var1[var2 + var5 + 6] & 15) << 9);
            var3[var6 + 4] = (short)(var1[var2 + var5 + 6] >> 4 & 15 | (var1[var2 + var5 + 7] & 255) << 4 | (var1[var2 + var5 + 8] & 1) << 12);
            var3[var6 + 5] = (short)(var1[var2 + var5 + 8] >> 1 & 127 | (var1[var2 + var5 + 9] & 63) << 7);
            var3[var6 + 6] = (short)(var1[var2 + var5 + 9] >> 6 & 3 | (var1[var2 + var5 + 10] & 255) << 2 | (var1[var2 + var5 + 11] & 7) << 10);
            var3[var6 + 7] = (short)(var1[var2 + var5 + 11] >> 3 & 31 | (var1[var2 + var5 + 12] & 255) << 5);
         }
      } else {
         for (short var7 = 0; var7 < this.SABER_N / 2; var7++) {
            short var8 = (short)(3 * var7);
            short var9 = (short)(2 * var7);
            var3[var9 + 0] = (short)(var1[var2 + var8 + 0] & 255 | (var1[var2 + var8 + 1] & 15) << 8);
            var3[var9 + 1] = (short)(var1[var2 + var8 + 1] >> 4 & 15 | (var1[var2 + var8 + 2] & 255) << 4);
         }
      }
   }

   private void POLp2BS(byte[] var1, int var2, short[] var3) {
      for (short var4 = 0; var4 < this.SABER_N / 4; var4++) {
         short var5 = (short)(5 * var4);
         short var6 = (short)(4 * var4);
         var1[var2 + var5 + 0] = (byte)(var3[var6 + 0] & 255);
         var1[var2 + var5 + 1] = (byte)(var3[var6 + 0] >> 8 & 3 | (var3[var6 + 1] & 63) << 2);
         var1[var2 + var5 + 2] = (byte)(var3[var6 + 1] >> 6 & 15 | (var3[var6 + 2] & 15) << 4);
         var1[var2 + var5 + 3] = (byte)(var3[var6 + 2] >> 4 & 63 | (var3[var6 + 3] & 3) << 6);
         var1[var2 + var5 + 4] = (byte)(var3[var6 + 3] >> 2 & 0xFF);
      }
   }

   public void BS2POLp(byte[] var1, int var2, short[] var3) {
      for (short var4 = 0; var4 < this.SABER_N / 4; var4++) {
         short var5 = (short)(5 * var4);
         short var6 = (short)(4 * var4);
         var3[var6 + 0] = (short)(var1[var2 + var5 + 0] & 255 | (var1[var2 + var5 + 1] & 3) << 8);
         var3[var6 + 1] = (short)(var1[var2 + var5 + 1] >> 2 & 63 | (var1[var2 + var5 + 2] & 15) << 6);
         var3[var6 + 2] = (short)(var1[var2 + var5 + 2] >> 4 & 15 | (var1[var2 + var5 + 3] & 63) << 4);
         var3[var6 + 3] = (short)(var1[var2 + var5 + 3] >> 6 & 3 | (var1[var2 + var5 + 4] & 255) << 2);
      }
   }

   public void POLVECq2BS(byte[] var1, short[][] var2) {
      for (byte var3 = 0; var3 < this.SABER_L; var3++) {
         this.POLq2BS(var1, var3 * this.SABER_POLYBYTES, var2[var3]);
      }
   }

   public void BS2POLVECq(byte[] var1, int var2, short[][] var3) {
      for (byte var4 = 0; var4 < this.SABER_L; var4++) {
         this.BS2POLq(var1, var2 + var4 * this.SABER_POLYBYTES, var3[var4]);
      }
   }

   public void POLVECp2BS(byte[] var1, short[][] var2) {
      for (byte var3 = 0; var3 < this.SABER_L; var3++) {
         this.POLp2BS(var1, var3 * (this.SABER_EP * this.SABER_N / 8), var2[var3]);
      }
   }

   public void BS2POLVECp(byte[] var1, short[][] var2) {
      for (byte var3 = 0; var3 < this.SABER_L; var3++) {
         this.BS2POLp(var1, var3 * (this.SABER_EP * this.SABER_N / 8), var2[var3]);
      }
   }

   public void BS2POLmsg(byte[] var1, short[] var2) {
      for (byte var4 = 0; var4 < this.SABER_KEYBYTES; var4++) {
         for (byte var3 = 0; var3 < 8; var3++) {
            var2[var4 * 8 + var3] = (short)(var1[var4] >> var3 & 1);
         }
      }
   }

   public void POLmsg2BS(byte[] var1, short[] var2) {
      for (byte var4 = 0; var4 < this.SABER_KEYBYTES; var4++) {
         for (byte var3 = 0; var3 < 8; var3++) {
            var1[var4] = (byte)(var1[var4] | (var2[var4 * 8 + var3] & 1) << var3);
         }
      }
   }
}
