package org.bouncycastle.pqc.crypto.falcon;

import java.security.SecureRandom;
import org.bouncycastle.crypto.digests.SHAKEDigest;
import org.bouncycastle.util.Arrays;

class FalconNIST {
   final int NONCELEN;
   final int LOGN;
   private final int N;
   private final SecureRandom rand;
   private final int CRYPTO_SECRETKEYBYTES;
   private final int CRYPTO_PUBLICKEYBYTES;
   final int CRYPTO_BYTES;

   FalconNIST(int var1, int var2, SecureRandom var3) {
      this.rand = var3;
      this.LOGN = var1;
      this.NONCELEN = var2;
      this.N = 1 << var1;
      this.CRYPTO_PUBLICKEYBYTES = 1 + 14 * this.N / 8;
      if (var1 == 10) {
         this.CRYPTO_SECRETKEYBYTES = 2305;
         this.CRYPTO_BYTES = 1330;
      } else if (var1 == 9 || var1 == 8) {
         this.CRYPTO_SECRETKEYBYTES = 1 + 6 * this.N * 2 / 8 + this.N;
         this.CRYPTO_BYTES = 690;
      } else if (var1 != 7 && var1 != 6) {
         this.CRYPTO_SECRETKEYBYTES = 1 + this.N * 2 + this.N;
         this.CRYPTO_BYTES = 690;
      } else {
         this.CRYPTO_SECRETKEYBYTES = 1 + 7 * this.N * 2 / 8 + this.N;
         this.CRYPTO_BYTES = 690;
      }
   }

   byte[][] crypto_sign_keypair(byte[] var1, byte[] var2) {
      byte[] var3 = new byte[this.N];
      byte[] var4 = new byte[this.N];
      byte[] var5 = new byte[this.N];
      short[] var6 = new short[this.N];
      byte[] var7 = new byte[48];
      SHAKEDigest var8 = new SHAKEDigest(256);
      this.rand.nextBytes(var7);
      var8.update(var7, 0, var7.length);
      FalconKeyGen.keygen(var8, var3, var4, var5, var6, this.LOGN);
      var2[0] = (byte)(80 + this.LOGN);
      int var9 = 1;
      int var10 = FalconCodec.trim_i8_encode(var2, var9, this.CRYPTO_SECRETKEYBYTES - var9, var3, this.LOGN, FalconCodec.max_fg_bits[this.LOGN]);
      if (var10 == 0) {
         throw new IllegalStateException("f encode failed");
      } else {
         byte[] var11 = Arrays.copyOfRange(var2, var9, var9 + var10);
         var9 += var10;
         var10 = FalconCodec.trim_i8_encode(var2, var9, this.CRYPTO_SECRETKEYBYTES - var9, var4, this.LOGN, FalconCodec.max_fg_bits[this.LOGN]);
         if (var10 == 0) {
            throw new IllegalStateException("g encode failed");
         } else {
            byte[] var12 = Arrays.copyOfRange(var2, var9, var9 + var10);
            var9 += var10;
            var10 = FalconCodec.trim_i8_encode(var2, var9, this.CRYPTO_SECRETKEYBYTES - var9, var5, this.LOGN, FalconCodec.max_FG_bits[this.LOGN]);
            if (var10 == 0) {
               throw new IllegalStateException("F encode failed");
            } else {
               byte[] var13 = Arrays.copyOfRange(var2, var9, var9 + var10);
               var9 += var10;
               if (var9 != this.CRYPTO_SECRETKEYBYTES) {
                  throw new IllegalStateException("secret key encoding failed");
               } else {
                  var1[0] = (byte)this.LOGN;
                  var10 = FalconCodec.modq_encode(var1, this.CRYPTO_PUBLICKEYBYTES - 1, var6, this.LOGN);
                  if (var10 != this.CRYPTO_PUBLICKEYBYTES - 1) {
                     throw new IllegalStateException("public key encoding failed");
                  } else {
                     return new byte[][]{Arrays.copyOfRange(var1, 1, var1.length), var11, var12, var13};
                  }
               }
            }
         }
      }
   }

   byte[] crypto_sign(byte[] var1, byte[] var2, int var3, byte[] var4) {
      byte[] var5 = new byte[this.N];
      byte[] var6 = new byte[this.N];
      byte[] var7 = new byte[this.N];
      byte[] var8 = new byte[this.N];
      short[] var9 = new short[this.N];
      short[] var10 = new short[this.N];
      byte[] var11 = new byte[48];
      byte[] var12 = new byte[this.NONCELEN];
      SHAKEDigest var13 = new SHAKEDigest(256);
      FalconSign var17 = new FalconSign();
      int var14 = 0;
      int var15 = FalconCodec.trim_i8_decode(var5, this.LOGN, FalconCodec.max_fg_bits[this.LOGN], var4, 0, this.CRYPTO_SECRETKEYBYTES - var14);
      if (var15 == 0) {
         throw new IllegalStateException("f decode failed");
      } else {
         var14 += var15;
         var15 = FalconCodec.trim_i8_decode(var6, this.LOGN, FalconCodec.max_fg_bits[this.LOGN], var4, var14, this.CRYPTO_SECRETKEYBYTES - var14);
         if (var15 == 0) {
            throw new IllegalStateException("g decode failed");
         } else {
            var14 += var15;
            var15 = FalconCodec.trim_i8_decode(var7, this.LOGN, FalconCodec.max_FG_bits[this.LOGN], var4, var14, this.CRYPTO_SECRETKEYBYTES - var14);
            if (var15 == 0) {
               throw new IllegalArgumentException("F decode failed");
            } else {
               var14 += var15;
               if (var14 != this.CRYPTO_SECRETKEYBYTES - 1) {
                  throw new IllegalStateException("full key not used");
               } else if (!FalconVrfy.complete_private(var8, var5, var6, var7, this.LOGN, new short[2 * this.N])) {
                  throw new IllegalStateException("complete_private failed");
               } else {
                  this.rand.nextBytes(var12);
                  var13.update(var12, 0, this.NONCELEN);
                  var13.update(var2, 0, var3);
                  FalconCommon.hash_to_point_vartime(var13, var10, this.LOGN);
                  this.rand.nextBytes(var11);
                  var13.reset();
                  var13.update(var11, 0, var11.length);
                  var17.sign_dyn(var9, var13, var5, var6, var7, var8, var10, this.LOGN, new double[10 * this.N]);
                  byte[] var18 = new byte[this.CRYPTO_BYTES - 2 - this.NONCELEN];
                  int var16 = FalconCodec.comp_encode(var18, var18.length, var9, this.LOGN);
                  if (var16 == 0) {
                     throw new IllegalStateException("signature failed to generate");
                  } else {
                     var1[0] = (byte)(48 + this.LOGN);
                     System.arraycopy(var12, 0, var1, 1, this.NONCELEN);
                     System.arraycopy(var18, 0, var1, 1 + this.NONCELEN, var16);
                     return Arrays.copyOfRange(var1, 0, 1 + this.NONCELEN + var16);
                  }
               }
            }
         }
      }
   }

   int crypto_sign_open(byte[] var1, byte[] var2, byte[] var3, byte[] var4) {
      short[] var5 = new short[this.N];
      short[] var6 = new short[this.N];
      short[] var7 = new short[this.N];
      SHAKEDigest var8 = new SHAKEDigest(256);
      if (FalconCodec.modq_decode(var5, this.LOGN, var4, this.CRYPTO_PUBLICKEYBYTES - 1) != this.CRYPTO_PUBLICKEYBYTES - 1) {
         return -1;
      } else {
         FalconVrfy.to_ntt_monty(var5, this.LOGN);
         int var9 = var1.length;
         int var10 = var3.length;
         if (var9 >= 1 && FalconCodec.comp_decode(var7, this.LOGN, var1, var9) == var9) {
            var8.update(var2, 0, this.NONCELEN);
            var8.update(var3, 0, var10);
            FalconCommon.hash_to_point_vartime(var8, var6, this.LOGN);
            return FalconVrfy.verify_raw(var6, var7, var5, this.LOGN, new short[this.N]) == 0 ? -1 : 0;
         } else {
            return -1;
         }
      }
   }
}
