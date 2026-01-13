package org.bouncycastle.pqc.crypto.sphincs;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.pqc.crypto.MessageSigner;
import org.bouncycastle.util.Pack;

public class SPHINCS256Signer implements MessageSigner {
   private final HashFunctions hashFunctions;
   private byte[] keyData;

   public SPHINCS256Signer(Digest var1, Digest var2) {
      if (var1.getDigestSize() != 32) {
         throw new IllegalArgumentException("n-digest needs to produce 32 bytes of output");
      } else if (var2.getDigestSize() != 64) {
         throw new IllegalArgumentException("2n-digest needs to produce 64 bytes of output");
      } else {
         this.hashFunctions = new HashFunctions(var1, var2);
      }
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      if (var1) {
         if (var2 instanceof ParametersWithRandom) {
            this.keyData = ((SPHINCSPrivateKeyParameters)((ParametersWithRandom)var2).getParameters()).getKeyData();
         } else {
            this.keyData = ((SPHINCSPrivateKeyParameters)var2).getKeyData();
         }
      } else {
         this.keyData = ((SPHINCSPublicKeyParameters)var2).getKeyData();
      }
   }

   @Override
   public byte[] generateSignature(byte[] var1) {
      return this.crypto_sign(this.hashFunctions, var1, this.keyData);
   }

   @Override
   public boolean verifySignature(byte[] var1, byte[] var2) {
      return this.verify(this.hashFunctions, var1, var2, this.keyData);
   }

   static void validate_authpath(HashFunctions var0, byte[] var1, byte[] var2, int var3, byte[] var4, int var5, byte[] var6, int var7) {
      byte[] var10 = new byte[64];
      if ((var3 & 1) != 0) {
         for (int var9 = 0; var9 < 32; var9++) {
            var10[32 + var9] = var2[var9];
         }

         for (int var12 = 0; var12 < 32; var12++) {
            var10[var12] = var4[var5 + var12];
         }
      } else {
         for (int var13 = 0; var13 < 32; var13++) {
            var10[var13] = var2[var13];
         }

         for (int var14 = 0; var14 < 32; var14++) {
            var10[32 + var14] = var4[var5 + var14];
         }
      }

      int var11 = var5 + 32;

      for (int var8 = 0; var8 < var7 - 1; var8++) {
         var3 >>>= 1;
         if ((var3 & 1) != 0) {
            var0.hash_2n_n_mask(var10, 32, var10, 0, var6, 2 * (7 + var8) * 32);

            for (int var16 = 0; var16 < 32; var16++) {
               var10[var16] = var4[var11 + var16];
            }
         } else {
            var0.hash_2n_n_mask(var10, 0, var10, 0, var6, 2 * (7 + var8) * 32);

            for (int var15 = 0; var15 < 32; var15++) {
               var10[var15 + 32] = var4[var11 + var15];
            }
         }

         var11 += 32;
      }

      var0.hash_2n_n_mask(var1, 0, var10, 0, var6, 2 * (7 + var7 - 1) * 32);
   }

   static void compute_authpath_wots(HashFunctions var0, byte[] var1, byte[] var2, int var3, Tree.leafaddr var4, byte[] var5, byte[] var6, int var7) {
      Tree.leafaddr var11 = new Tree.leafaddr(var4);
      byte[] var12 = new byte[2048];
      byte[] var13 = new byte[1024];
      byte[] var14 = new byte[68608];

      for (var11.subleaf = 0L; var11.subleaf < 32L; var11.subleaf++) {
         Seed.get_seed(var0, var13, (int)(var11.subleaf * 32L), var5, var11);
      }

      Wots var15 = new Wots();

      for (var11.subleaf = 0L; var11.subleaf < 32L; var11.subleaf++) {
         var15.wots_pkgen(var0, var14, (int)(var11.subleaf * 67L * 32L), var13, (int)(var11.subleaf * 32L), var6, 0);
      }

      for (var11.subleaf = 0L; var11.subleaf < 32L; var11.subleaf++) {
         Tree.l_tree(var0, var12, (int)(1024L + var11.subleaf * 32L), var14, (int)(var11.subleaf * 67L * 32L), var6, 0);
      }

      int var16 = 0;

      for (byte var8 = 32; var8 > 0; var8 >>>= 1) {
         for (byte var10 = 0; var10 < var8; var10 += 2) {
            var0.hash_2n_n_mask(var12, (var8 >>> 1) * 32 + (var10 >>> 1) * 32, var12, var8 * 32 + var10 * 32, var6, 2 * (7 + var16) * 32);
         }

         var16++;
      }

      int var9 = (int)var4.subleaf;

      for (int var17 = 0; var17 < var7; var17++) {
         System.arraycopy(var12, (32 >>> var17) * 32 + (var9 >>> var17 ^ 1) * 32, var2, var3 + var17 * 32, 32);
      }

      System.arraycopy(var12, 32, var1, 0, 32);
   }

   byte[] crypto_sign(HashFunctions var1, byte[] var2, byte[] var3) {
      byte[] var4 = new byte[41000];
      byte[] var8 = new byte[32];
      byte[] var9 = new byte[64];
      long[] var10 = new long[8];
      byte[] var11 = new byte[32];
      byte[] var12 = new byte[32];
      byte[] var13 = new byte[1024];
      byte[] var15 = new byte[1088];

      for (int var5 = 0; var5 < 1088; var5++) {
         var15[var5] = var3[var5];
      }

      char var16 = 'ꀈ';
      System.arraycopy(var15, 1056, var4, var16, 32);
      Digest var17 = var1.getMessageHash();
      byte[] var18 = new byte[var17.getDigestSize()];
      var17.update(var4, var16, 32);
      var17.update(var2, 0, var2.length);
      var17.doFinal(var18, 0);
      this.zerobytes(var4, var16, 32);

      for (int var19 = 0; var19 != var10.length; var19++) {
         var10[var19] = Pack.littleEndianToLong(var18, var19 * 8);
      }

      long var6 = var10[0] & 1152921504606846975L;
      System.arraycopy(var18, 16, var8, 0, 32);
      var16 = '鯨';
      System.arraycopy(var8, 0, var4, var16, 32);
      Tree.leafaddr var32 = new Tree.leafaddr();
      var32.level = 11;
      var32.subtree = 0L;
      var32.subleaf = 0L;
      int var14 = var16 + ' ';
      System.arraycopy(var15, 32, var4, var14, 1024);
      Tree.treehash(var1, var4, var14 + 1024, 5, var15, var32, var4, var14);
      var17 = var1.getMessageHash();
      var17.update(var4, var16, 1088);
      var17.update(var2, 0, var2.length);
      var17.doFinal(var9, 0);
      Tree.leafaddr var25 = new Tree.leafaddr();
      var25.level = 12;
      var25.subleaf = (int)(var6 & 31L);
      var25.subtree = var6 >>> 5;

      for (int var21 = 0; var21 < 32; var21++) {
         var4[var21] = var8[var21];
      }

      byte var27 = 32;
      System.arraycopy(var15, 32, var13, 0, 1024);

      for (int var22 = 0; var22 < 8; var22++) {
         var4[var27 + var22] = (byte)(var6 >>> 8 * var22 & 255L);
      }

      int var28 = var27 + 8;
      Seed.get_seed(var1, var12, 0, var15, var25);
      Horst var31 = new Horst();
      int var33 = Horst.horst_sign(var1, var4, var28, var11, var12, var13, var9);
      int var29 = var28 + var33;
      Wots var20 = new Wots();

      for (int var23 = 0; var23 < 12; var23++) {
         var25.level = var23;
         Seed.get_seed(var1, var12, 0, var15, var25);
         var20.wots_sign(var1, var4, var29, var11, var12, var13);
         int var30 = var29 + 2144;
         compute_authpath_wots(var1, var11, var4, var30, var25, var15, var13, 5);
         var29 = var30 + 160;
         var25.subleaf = (int)(var25.subtree & 31L);
         var25.subtree >>>= 5;
      }

      this.zerobytes(var15, 0, 1088);
      return var4;
   }

   private void zerobytes(byte[] var1, int var2, int var3) {
      for (int var4 = 0; var4 != var3; var4++) {
         var1[var2 + var4] = 0;
      }
   }

   boolean verify(HashFunctions var1, byte[] var2, byte[] var3, byte[] var4) {
      int var6 = var3.length;
      long var7 = 0L;
      byte[] var9 = new byte[2144];
      byte[] var10 = new byte[32];
      byte[] var11 = new byte[32];
      byte[] var12 = new byte[41000];
      byte[] var14 = new byte[1056];
      if (var6 != 41000) {
         throw new IllegalArgumentException("signature wrong size");
      } else {
         byte[] var15 = new byte[64];

         for (int var5 = 0; var5 < 1056; var5++) {
            var14[var5] = var4[var5];
         }

         byte[] var16 = new byte[32];

         for (int var18 = 0; var18 < 32; var18++) {
            var16[var18] = var3[var18];
         }

         System.arraycopy(var3, 0, var12, 0, 41000);
         Digest var17 = var1.getMessageHash();
         var17.update(var16, 0, 32);
         var17.update(var14, 0, 1056);
         var17.update(var2, 0, var2.length);
         var17.doFinal(var15, 0);
         int var13 = 0;
         var13 += 32;
         var6 -= 32;

         for (int var19 = 0; var19 < 8; var19++) {
            var7 ^= (long)(var12[var13 + var19] & 255) << 8 * var19;
         }

         new Horst();
         Horst.horst_verify(var1, var11, var12, var13 + 8, var14, var15);
         var13 += 8;
         var6 -= 8;
         var13 += 13312;
         var6 -= 13312;
         Wots var30 = new Wots();

         for (int var20 = 0; var20 < 12; var20++) {
            var30.wots_verify(var1, var9, var12, var13, var11, var14);
            var13 += 2144;
            var6 -= 2144;
            Tree.l_tree(var1, var10, 0, var9, 0, var14, 0);
            validate_authpath(var1, var11, var10, (int)(var7 & 31L), var12, var13, var14, 5);
            var7 >>= 5;
            var13 += 160;
            var6 -= 160;
         }

         boolean var31 = true;

         for (int var21 = 0; var21 < 32; var21++) {
            var31 &= var11[var21] == var14[var21 + 1024];
         }

         return var31;
      }
   }
}
