package org.bouncycastle.pqc.crypto.rainbow;

import java.security.SecureRandom;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.engines.AESEngine;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.util.Arrays;

class RainbowDRBG extends SecureRandom {
   private byte[] seed;
   private byte[] key;
   private byte[] v;
   private Digest hashAlgo;

   public RainbowDRBG(byte[] var1, Digest var2) {
      this.seed = var1;
      this.hashAlgo = var2;
      this.init(256);
   }

   private void init(int var1) {
      if (this.seed.length >= 48) {
         this.randombytes_init(this.seed, var1);
      } else {
         byte[] var2 = RainbowUtil.hash(this.hashAlgo, this.seed, 48 - this.seed.length);
         this.randombytes_init(Arrays.concatenate(this.seed, var2), var1);
      }
   }

   @Override
   public void nextBytes(byte[] var1) {
      byte[] var2 = new byte[16];
      byte var3 = 0;
      int var4 = var1.length;

      while (var4 > 0) {
         for (int var5 = 15; var5 >= 0; var5--) {
            if ((this.v[var5] & 255) != 255) {
               this.v[var5]++;
               break;
            }

            this.v[var5] = 0;
         }

         this.AES256_ECB(this.key, this.v, var2, 0);
         if (var4 > 15) {
            System.arraycopy(var2, 0, var1, var3, var2.length);
            var3 += 16;
            var4 -= 16;
         } else {
            System.arraycopy(var2, 0, var1, var3, var4);
            var4 = 0;
         }
      }

      this.AES256_CTR_DRBG_Update(null, this.key, this.v);
   }

   private void AES256_ECB(byte[] var1, byte[] var2, byte[] var3, int var4) {
      try {
         AESEngine var5 = new AESEngine();
         var5.init(true, new KeyParameter(var1));

         for (byte var6 = 0; var6 != var2.length; var6 += 16) {
            var5.processBlock(var2, var6, var3, var4 + var6);
         }
      } catch (Throwable var7) {
         throw new IllegalStateException("drbg failure: " + var7.getMessage(), var7);
      }
   }

   private void AES256_CTR_DRBG_Update(byte[] var1, byte[] var2, byte[] var3) {
      byte[] var4 = new byte[48];

      for (int var5 = 0; var5 < 3; var5++) {
         for (int var6 = 15; var6 >= 0; var6--) {
            if ((var3[var6] & 255) != 255) {
               var3[var6]++;
               break;
            }

            var3[var6] = 0;
         }

         this.AES256_ECB(var2, var3, var4, 16 * var5);
      }

      if (var1 != null) {
         for (int var7 = 0; var7 < 48; var7++) {
            var4[var7] ^= var1[var7];
         }
      }

      System.arraycopy(var4, 0, var2, 0, var2.length);
      System.arraycopy(var4, 32, var3, 0, var3.length);
   }

   private void randombytes_init(byte[] var1, int var2) {
      byte[] var3 = new byte[48];
      System.arraycopy(var1, 0, var3, 0, var3.length);
      this.key = new byte[32];
      this.v = new byte[16];
      this.AES256_CTR_DRBG_Update(var3, this.key, this.v);
   }
}
