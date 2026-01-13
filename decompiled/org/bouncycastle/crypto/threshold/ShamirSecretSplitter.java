package org.bouncycastle.crypto.threshold;

import java.io.IOException;
import java.security.SecureRandom;
import org.bouncycastle.util.Arrays;

public class ShamirSecretSplitter implements SecretSplitter {
   private final Polynomial poly;
   protected int l;
   protected SecureRandom random;

   public ShamirSecretSplitter(ShamirSecretSplitter.Algorithm var1, ShamirSecretSplitter.Mode var2, int var3, SecureRandom var4) {
      if (var3 >= 0 && var3 <= 65534) {
         this.poly = Polynomial.newInstance(var1, var2);
         this.l = var3;
         this.random = var4;
      } else {
         throw new IllegalArgumentException("Invalid input: l ranges from 0 to 65534 (2^16-2) bytes.");
      }
   }

   public ShamirSplitSecret split(int var1, int var2) {
      byte[][] var3 = this.initP(var1, var2);
      byte[][] var4 = new byte[var1][this.l];
      ShamirSplitSecretShare[] var5 = new ShamirSplitSecretShare[this.l];

      for (int var6 = 0; var6 < var1; var6++) {
         this.random.nextBytes(var4[var6]);
      }

      for (int var7 = 0; var7 < var3.length; var7++) {
         var5[var7] = new ShamirSplitSecretShare(this.poly.gfVecMul(var3[var7], var4), var7 + 1);
      }

      return new ShamirSplitSecret(this.poly, var5);
   }

   public ShamirSplitSecret splitAround(SecretShare var1, int var2, int var3) throws IOException {
      byte[][] var4 = this.initP(var2, var3);
      byte[][] var5 = new byte[var2][this.l];
      ShamirSplitSecretShare[] var6 = new ShamirSplitSecretShare[this.l];
      byte[] var7 = var1.getEncoded();
      var6[0] = new ShamirSplitSecretShare(var7, 1);

      for (int var8 = 0; var8 < var2; var8++) {
         this.random.nextBytes(var5[var8]);
      }

      for (int var11 = 0; var11 < this.l; var11++) {
         byte var10 = var5[1][var11];

         for (int var9 = 2; var9 < var2; var9++) {
            var10 ^= var5[var9][var11];
         }

         var5[0][var11] = (byte)(var10 ^ var7[var11]);
      }

      for (int var12 = 1; var12 < var4.length; var12++) {
         var6[var12] = new ShamirSplitSecretShare(this.poly.gfVecMul(var4[var12], var5), var12 + 1);
      }

      return new ShamirSplitSecret(this.poly, var6);
   }

   public ShamirSplitSecret resplit(byte[] var1, int var2, int var3) {
      byte[][] var4 = this.initP(var2, var3);
      byte[][] var5 = new byte[var2][this.l];
      ShamirSplitSecretShare[] var6 = new ShamirSplitSecretShare[this.l];
      var5[0] = Arrays.clone(var1);

      for (int var7 = 1; var7 < var2; var7++) {
         this.random.nextBytes(var5[var7]);
      }

      for (int var8 = 0; var8 < var4.length; var8++) {
         var6[var8] = new ShamirSplitSecretShare(this.poly.gfVecMul(var4[var8], var5), var8 + 1);
      }

      return new ShamirSplitSecret(this.poly, var6);
   }

   private byte[][] initP(int var1, int var2) {
      if (var1 >= 1 && var1 <= 255) {
         if (var2 >= var1 && var2 <= 255) {
            byte[][] var3 = new byte[var2][var1];

            for (int var4 = 0; var4 < var2; var4++) {
               for (int var5 = 0; var5 < var1; var5++) {
                  var3[var4][var5] = this.poly.gfPow((byte)(var4 + 1), (byte)var5);
               }
            }

            return var3;
         } else {
            throw new IllegalArgumentException("Invalid input: n must be less than 256 and greater than or equal to n.");
         }
      } else {
         throw new IllegalArgumentException("Invalid input: m must be less than 256 and positive.");
      }
   }

   public static enum Algorithm {
      AES,
      RSA;
   }

   public static enum Mode {
      Native,
      Table;
   }
}
