package org.bouncycastle.pqc.crypto.saber;

import java.security.SecureRandom;
import org.bouncycastle.util.Arrays;

class SABEREngine {
   public static final int SABER_EP = 10;
   public static final int SABER_N = 256;
   private static final int SABER_SEEDBYTES = 32;
   private static final int SABER_NOISE_SEEDBYTES = 32;
   private static final int SABER_KEYBYTES = 32;
   private static final int SABER_HASHBYTES = 32;
   private final int SABER_L;
   private final int SABER_MU;
   private final int SABER_ET;
   private final int SABER_POLYCOINBYTES;
   private final int SABER_EQ;
   private final int SABER_POLYBYTES;
   private final int SABER_POLYVECBYTES;
   private final int SABER_POLYCOMPRESSEDBYTES;
   private final int SABER_POLYVECCOMPRESSEDBYTES;
   private final int SABER_SCALEBYTES_KEM;
   private final int SABER_INDCPA_PUBLICKEYBYTES;
   private final int SABER_INDCPA_SECRETKEYBYTES;
   private final int SABER_PUBLICKEYBYTES;
   private final int SABER_SECRETKEYBYTES;
   private final int SABER_BYTES_CCA_DEC;
   private final int defaultKeySize;
   private final int h1;
   private final int h2;
   private final Utils utils;
   private final Poly poly;
   private final boolean usingAES;
   protected final boolean usingEffectiveMasking;
   protected final Symmetric symmetric;

   public int getSABER_N() {
      return 256;
   }

   public int getSABER_EP() {
      return 10;
   }

   public int getSABER_KEYBYTES() {
      return 32;
   }

   public int getSABER_L() {
      return this.SABER_L;
   }

   public int getSABER_ET() {
      return this.SABER_ET;
   }

   public int getSABER_POLYBYTES() {
      return this.SABER_POLYBYTES;
   }

   public int getSABER_POLYVECBYTES() {
      return this.SABER_POLYVECBYTES;
   }

   public int getSABER_SEEDBYTES() {
      return 32;
   }

   public int getSABER_POLYCOINBYTES() {
      return this.SABER_POLYCOINBYTES;
   }

   public int getSABER_NOISE_SEEDBYTES() {
      return 32;
   }

   public int getSABER_MU() {
      return this.SABER_MU;
   }

   public Utils getUtils() {
      return this.utils;
   }

   public int getSessionKeySize() {
      return this.defaultKeySize / 8;
   }

   public int getCipherTextSize() {
      return this.SABER_BYTES_CCA_DEC;
   }

   public int getPublicKeySize() {
      return this.SABER_PUBLICKEYBYTES;
   }

   public int getPrivateKeySize() {
      return this.SABER_SECRETKEYBYTES;
   }

   public SABEREngine(int var1, int var2, boolean var3, boolean var4) {
      this.defaultKeySize = var2;
      this.usingAES = var3;
      this.usingEffectiveMasking = var4;
      this.SABER_L = var1;
      if (var1 == 2) {
         this.SABER_MU = 10;
         this.SABER_ET = 3;
      } else if (var1 == 3) {
         this.SABER_MU = 8;
         this.SABER_ET = 4;
      } else {
         this.SABER_MU = 6;
         this.SABER_ET = 6;
      }

      if (var3) {
         this.symmetric = new Symmetric.AesSymmetric();
      } else {
         this.symmetric = new Symmetric.ShakeSymmetric();
      }

      if (var4) {
         this.SABER_EQ = 12;
         this.SABER_POLYCOINBYTES = 64;
      } else {
         this.SABER_EQ = 13;
         this.SABER_POLYCOINBYTES = this.SABER_MU * 256 / 8;
      }

      this.SABER_POLYBYTES = this.SABER_EQ * 256 / 8;
      this.SABER_POLYVECBYTES = this.SABER_L * this.SABER_POLYBYTES;
      this.SABER_POLYCOMPRESSEDBYTES = 320;
      this.SABER_POLYVECCOMPRESSEDBYTES = this.SABER_L * this.SABER_POLYCOMPRESSEDBYTES;
      this.SABER_SCALEBYTES_KEM = this.SABER_ET * 256 / 8;
      this.SABER_INDCPA_PUBLICKEYBYTES = this.SABER_POLYVECCOMPRESSEDBYTES + 32;
      this.SABER_INDCPA_SECRETKEYBYTES = this.SABER_POLYVECBYTES;
      this.SABER_PUBLICKEYBYTES = this.SABER_INDCPA_PUBLICKEYBYTES;
      this.SABER_SECRETKEYBYTES = this.SABER_INDCPA_SECRETKEYBYTES + this.SABER_INDCPA_PUBLICKEYBYTES + 32 + 32;
      this.SABER_BYTES_CCA_DEC = this.SABER_POLYVECCOMPRESSEDBYTES + this.SABER_SCALEBYTES_KEM;
      this.h1 = 1 << this.SABER_EQ - 10 - 1;
      this.h2 = 256 - (1 << 10 - this.SABER_ET - 1) + (1 << this.SABER_EQ - 10 - 1);
      this.utils = new Utils(this);
      this.poly = new Poly(this);
   }

   private void indcpa_kem_keypair(byte[] var1, byte[] var2, SecureRandom var3) {
      short[][][] var4 = new short[this.SABER_L][this.SABER_L][256];
      short[][] var5 = new short[this.SABER_L][256];
      short[][] var6 = new short[this.SABER_L][256];
      byte[] var7 = new byte[32];
      byte[] var8 = new byte[32];
      var3.nextBytes(var7);
      this.symmetric.prf(var7, var7, 32, 32);
      var3.nextBytes(var8);
      this.poly.GenMatrix(var4, var7);
      this.poly.GenSecret(var5, var8);
      this.poly.MatrixVectorMul(var4, var5, var6, 1);

      for (int var9 = 0; var9 < this.SABER_L; var9++) {
         for (int var10 = 0; var10 < 256; var10++) {
            var6[var9][var10] = (short)((var6[var9][var10] + this.h1 & 65535) >>> this.SABER_EQ - 10);
         }
      }

      this.utils.POLVECq2BS(var2, var5);
      this.utils.POLVECp2BS(var1, var6);
      System.arraycopy(var7, 0, var1, this.SABER_POLYVECCOMPRESSEDBYTES, var7.length);
   }

   public int crypto_kem_keypair(byte[] var1, byte[] var2, SecureRandom var3) {
      this.indcpa_kem_keypair(var1, var2, var3);

      for (int var4 = 0; var4 < this.SABER_INDCPA_PUBLICKEYBYTES; var4++) {
         var2[var4 + this.SABER_INDCPA_SECRETKEYBYTES] = var1[var4];
      }

      this.symmetric.hash_h(var2, var1, this.SABER_SECRETKEYBYTES - 64);
      byte[] var5 = new byte[32];
      var3.nextBytes(var5);
      System.arraycopy(var5, 0, var2, this.SABER_SECRETKEYBYTES - 32, var5.length);
      return 0;
   }

   private void indcpa_kem_enc(byte[] var1, byte[] var2, byte[] var3, byte[] var4) {
      short[][][] var5 = new short[this.SABER_L][this.SABER_L][256];
      short[][] var6 = new short[this.SABER_L][256];
      short[][] var7 = new short[this.SABER_L][256];
      short[][] var8 = new short[this.SABER_L][256];
      short[] var9 = new short[256];
      short[] var10 = new short[256];
      byte[] var13 = Arrays.copyOfRange(var3, this.SABER_POLYVECCOMPRESSEDBYTES, var3.length);
      this.poly.GenMatrix(var5, var13);
      this.poly.GenSecret(var6, var2);
      this.poly.MatrixVectorMul(var5, var6, var7, 0);

      for (int var11 = 0; var11 < this.SABER_L; var11++) {
         for (int var12 = 0; var12 < 256; var12++) {
            var7[var11][var12] = (short)((var7[var11][var12] + this.h1 & 65535) >>> this.SABER_EQ - 10);
         }
      }

      this.utils.POLVECp2BS(var4, var7);
      this.utils.BS2POLVECp(var3, var8);
      this.poly.InnerProd(var8, var6, var10);
      this.utils.BS2POLmsg(var1, var9);

      for (int var14 = 0; var14 < 256; var14++) {
         var10[var14] = (short)((var10[var14] - (var9[var14] << 9) + this.h1 & 65535) >>> 10 - this.SABER_ET);
      }

      this.utils.POLT2BS(var4, this.SABER_POLYVECCOMPRESSEDBYTES, var10);
   }

   public int crypto_kem_enc(byte[] var1, byte[] var2, byte[] var3, SecureRandom var4) {
      byte[] var5 = new byte[64];
      byte[] var6 = new byte[64];
      byte[] var7 = new byte[32];
      var4.nextBytes(var7);
      this.symmetric.hash_h(var7, var7, 0);
      System.arraycopy(var7, 0, var6, 0, 32);
      this.symmetric.hash_h(var6, var3, 32);
      this.symmetric.hash_g(var5, var6);
      this.indcpa_kem_enc(var6, Arrays.copyOfRange(var5, 32, var5.length), var3, var1);
      this.symmetric.hash_h(var5, var1, 32);
      byte[] var8 = new byte[32];
      this.symmetric.hash_h(var8, var5, 0);
      System.arraycopy(var8, 0, var2, 0, this.defaultKeySize / 8);
      return 0;
   }

   private void indcpa_kem_dec(byte[] var1, byte[] var2, byte[] var3) {
      short[][] var4 = new short[this.SABER_L][256];
      short[][] var5 = new short[this.SABER_L][256];
      short[] var6 = new short[256];
      short[] var7 = new short[256];
      this.utils.BS2POLVECq(var1, 0, var4);
      this.utils.BS2POLVECp(var2, var5);
      this.poly.InnerProd(var5, var4, var6);
      this.utils.BS2POLT(var2, this.SABER_POLYVECCOMPRESSEDBYTES, var7);

      for (int var8 = 0; var8 < 256; var8++) {
         var6[var8] = (short)((var6[var8] + this.h2 - (var7[var8] << 10 - this.SABER_ET) & 65535) >> 9);
      }

      this.utils.POLmsg2BS(var3, var6);
   }

   public int crypto_kem_dec(byte[] var1, byte[] var2, byte[] var3) {
      byte[] var6 = new byte[this.SABER_BYTES_CCA_DEC];
      byte[] var7 = new byte[64];
      byte[] var8 = new byte[64];
      byte[] var9 = Arrays.copyOfRange(var3, this.SABER_INDCPA_SECRETKEYBYTES, var3.length);
      this.indcpa_kem_dec(var3, var2, var7);

      for (int var4 = 0; var4 < 32; var4++) {
         var7[32 + var4] = var3[this.SABER_SECRETKEYBYTES - 64 + var4];
      }

      this.symmetric.hash_g(var8, var7);
      this.indcpa_kem_enc(var7, Arrays.copyOfRange(var8, 32, var8.length), var9, var6);
      int var5 = verify(var2, var6, this.SABER_BYTES_CCA_DEC);
      this.symmetric.hash_h(var8, var2, 32);
      cmov(var8, var3, this.SABER_SECRETKEYBYTES - 32, 32, (byte)var5);
      byte[] var10 = new byte[32];
      this.symmetric.hash_h(var10, var8, 0);
      System.arraycopy(var10, 0, var1, 0, this.defaultKeySize / 8);
      return 0;
   }

   static int verify(byte[] var0, byte[] var1, int var2) {
      long var3 = 0L;

      for (int var5 = 0; var5 < var2; var5++) {
         var3 |= var0[var5] ^ var1[var5];
      }

      var3 = -var3 >>> 63;
      return (int)var3;
   }

   static void cmov(byte[] var0, byte[] var1, int var2, int var3, byte var4) {
      var4 = (byte)(-var4);

      for (int var5 = 0; var5 < var3; var5++) {
         var0[var5] = (byte)(var0[var5] ^ var4 & (var1[var5 + var2] ^ var0[var5]));
      }
   }
}
