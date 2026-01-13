package org.bouncycastle.crypto.generators;

import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.Digest;
import org.bouncycastle.crypto.ExtendedDigest;
import org.bouncycastle.crypto.PBEParametersGenerator;
import org.bouncycastle.crypto.params.KeyParameter;
import org.bouncycastle.crypto.params.ParametersWithIV;

public class PKCS12ParametersGenerator extends PBEParametersGenerator {
   public static final int KEY_MATERIAL = 1;
   public static final int IV_MATERIAL = 2;
   public static final int MAC_MATERIAL = 3;
   private Digest digest;
   private int u;
   private int v;

   public PKCS12ParametersGenerator(Digest var1) {
      this.digest = var1;
      if (var1 instanceof ExtendedDigest) {
         this.u = var1.getDigestSize();
         this.v = ((ExtendedDigest)var1).getByteLength();
      } else {
         throw new IllegalArgumentException("Digest " + var1.getAlgorithmName() + " unsupported");
      }
   }

   private void adjust(byte[] var1, int var2, byte[] var3) {
      int var4 = (var3[var3.length - 1] & 255) + (var1[var2 + var3.length - 1] & 255) + 1;
      var1[var2 + var3.length - 1] = (byte)var4;
      var4 >>>= 8;

      for (int var5 = var3.length - 2; var5 >= 0; var5--) {
         var4 += (var3[var5] & 255) + (var1[var2 + var5] & 255);
         var1[var2 + var5] = (byte)var4;
         var4 >>>= 8;
      }
   }

   private byte[] generateDerivedKey(int var1, int var2) {
      byte[] var3 = new byte[this.v];
      byte[] var4 = new byte[var2];

      for (int var5 = 0; var5 != var3.length; var5++) {
         var3[var5] = (byte)var1;
      }

      byte[] var13;
      if (this.salt != null && this.salt.length != 0) {
         var13 = new byte[this.v * ((this.salt.length + this.v - 1) / this.v)];

         for (int var6 = 0; var6 != var13.length; var6++) {
            var13[var6] = this.salt[var6 % this.salt.length];
         }
      } else {
         var13 = new byte[0];
      }

      byte[] var14;
      if (this.password != null && this.password.length != 0) {
         var14 = new byte[this.v * ((this.password.length + this.v - 1) / this.v)];

         for (int var7 = 0; var7 != var14.length; var7++) {
            var14[var7] = this.password[var7 % this.password.length];
         }
      } else {
         var14 = new byte[0];
      }

      byte[] var15 = new byte[var13.length + var14.length];
      System.arraycopy(var13, 0, var15, 0, var13.length);
      System.arraycopy(var14, 0, var15, var13.length, var14.length);
      byte[] var8 = new byte[this.v];
      int var9 = (var2 + this.u - 1) / this.u;
      byte[] var10 = new byte[this.u];

      for (int var11 = 1; var11 <= var9; var11++) {
         this.digest.update(var3, 0, var3.length);
         this.digest.update(var15, 0, var15.length);
         this.digest.doFinal(var10, 0);

         for (int var12 = 1; var12 < this.iterationCount; var12++) {
            this.digest.update(var10, 0, var10.length);
            this.digest.doFinal(var10, 0);
         }

         for (int var16 = 0; var16 != var8.length; var16++) {
            var8[var16] = var10[var16 % var10.length];
         }

         for (int var17 = 0; var17 != var15.length / this.v; var17++) {
            this.adjust(var15, var17 * this.v, var8);
         }

         if (var11 == var9) {
            System.arraycopy(var10, 0, var4, (var11 - 1) * this.u, var4.length - (var11 - 1) * this.u);
         } else {
            System.arraycopy(var10, 0, var4, (var11 - 1) * this.u, var10.length);
         }
      }

      return var4;
   }

   @Override
   public CipherParameters generateDerivedParameters(int var1) {
      var1 /= 8;
      byte[] var2 = this.generateDerivedKey(1, var1);
      return new KeyParameter(var2, 0, var1);
   }

   @Override
   public CipherParameters generateDerivedParameters(int var1, int var2) {
      var1 /= 8;
      var2 /= 8;
      byte[] var3 = this.generateDerivedKey(1, var1);
      byte[] var4 = this.generateDerivedKey(2, var2);
      return new ParametersWithIV(new KeyParameter(var3, 0, var1), var4, 0, var2);
   }

   @Override
   public CipherParameters generateDerivedMacParameters(int var1) {
      var1 /= 8;
      byte[] var2 = this.generateDerivedKey(3, var1);
      return new KeyParameter(var2, 0, var1);
   }
}
