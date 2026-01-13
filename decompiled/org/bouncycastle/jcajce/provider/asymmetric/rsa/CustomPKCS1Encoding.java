package org.bouncycastle.jcajce.provider.asymmetric.rsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricBlockCipher;
import org.bouncycastle.crypto.CipherParameters;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.InvalidCipherTextException;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;
import org.bouncycastle.crypto.params.ParametersWithRandom;
import org.bouncycastle.util.Arrays;
import org.bouncycastle.util.Properties;

class CustomPKCS1Encoding implements AsymmetricBlockCipher {
   private static final int HEADER_LENGTH = 10;
   private SecureRandom random;
   private AsymmetricBlockCipher engine;
   private boolean forEncryption;
   private boolean forPrivateKey;
   private boolean useStrictLength;
   private byte[] blockBuffer;

   CustomPKCS1Encoding(AsymmetricBlockCipher var1) {
      this.engine = var1;
      this.useStrictLength = this.useStrict();
   }

   private boolean useStrict() {
      return Properties.isOverrideSetTo("org.bouncycastle.pkcs1.not_strict", true)
         ? false
         : !Properties.isOverrideSetTo("org.bouncycastle.pkcs1.strict", false);
   }

   public AsymmetricBlockCipher getUnderlyingCipher() {
      return this.engine;
   }

   @Override
   public void init(boolean var1, CipherParameters var2) {
      AsymmetricKeyParameter var3;
      if (var2 instanceof ParametersWithRandom) {
         ParametersWithRandom var4 = (ParametersWithRandom)var2;
         this.random = var4.getRandom();
         var3 = (AsymmetricKeyParameter)var4.getParameters();
      } else {
         var3 = (AsymmetricKeyParameter)var2;
         if (!var3.isPrivate() && var1) {
            this.random = CryptoServicesRegistrar.getSecureRandom();
         }
      }

      this.engine.init(var1, var2);
      this.forPrivateKey = var3.isPrivate();
      this.forEncryption = var1;
      this.blockBuffer = new byte[this.engine.getOutputBlockSize()];
   }

   @Override
   public int getInputBlockSize() {
      int var1 = this.engine.getInputBlockSize();
      return this.forEncryption ? var1 - 10 : var1;
   }

   @Override
   public int getOutputBlockSize() {
      int var1 = this.engine.getOutputBlockSize();
      return this.forEncryption ? var1 : var1 - 10;
   }

   @Override
   public byte[] processBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      return this.forEncryption ? this.encodeBlock(var1, var2, var3) : this.decodeBlock(var1, var2, var3);
   }

   private byte[] encodeBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      if (var3 > this.getInputBlockSize()) {
         throw new IllegalArgumentException("input data too large");
      } else {
         byte[] var4 = new byte[this.engine.getInputBlockSize()];
         if (this.forPrivateKey) {
            var4[0] = 1;

            for (int var5 = 1; var5 != var4.length - var3 - 1; var5++) {
               var4[var5] = -1;
            }
         } else {
            this.random.nextBytes(var4);
            var4[0] = 2;

            for (int var6 = 1; var6 != var4.length - var3 - 1; var6++) {
               while (var4[var6] == 0) {
                  var4[var6] = (byte)this.random.nextInt();
               }
            }
         }

         var4[var4.length - var3 - 1] = 0;
         System.arraycopy(var1, var2, var4, var4.length - var3, var3);
         return this.engine.processBlock(var4, 0, var4.length);
      }
   }

   private static int checkPkcs1Encoding1(byte[] var0) {
      int var1 = 0;
      int var2 = 0;
      int var3 = -(var0[0] & 255 ^ 1);

      for (int var4 = 1; var4 < var0.length; var4++) {
         int var5 = var0[var4] & 255;
         int var6 = (var5 ^ 0) - 1 >> 31;
         int var7 = (var5 ^ 255) - 1 >> 31;
         var2 ^= var4 & ~var1 & var6;
         var1 |= var6;
         var3 |= ~(var1 | var7);
      }

      var3 |= var2 - 9;
      int var9 = var0.length - 1 - var2;
      return var9 | var3 >> 31;
   }

   private static int checkPkcs1Encoding2(byte[] var0) {
      int var1 = 0;
      int var2 = 0;
      int var3 = -(var0[0] & 255 ^ 2);

      for (int var4 = 1; var4 < var0.length; var4++) {
         int var5 = var0[var4] & 255;
         int var6 = (var5 ^ 0) - 1 >> 31;
         var2 ^= var4 & ~var1 & var6;
         var1 |= var6;
      }

      var3 |= var2 - 9;
      int var8 = var0.length - 1 - var2;
      return var8 | var3 >> 31;
   }

   private byte[] decodeBlock(byte[] var1, int var2, int var3) throws InvalidCipherTextException {
      int var4 = this.engine.getOutputBlockSize();
      byte[] var5 = this.engine.processBlock(var1, var2, var3);
      boolean var6 = this.useStrictLength & var5.length != var4;
      byte[] var7 = var5;
      if (var5.length < var4) {
         var7 = this.blockBuffer;
      }

      int var8 = this.forPrivateKey ? checkPkcs1Encoding2(var7) : checkPkcs1Encoding1(var7);

      B var9;
      try {
         if (!(var8 < 0 | var6)) {
            var9 = new byte[var8];
            System.arraycopy(var7, var7.length - var8, var9, 0, var8);
            return (byte[])var9;
         }

         var9 = null;
      } finally {
         Arrays.fill(var5, (byte)0);
         Arrays.fill(this.blockBuffer, 0, Math.max(0, this.blockBuffer.length - var5.length), (byte)0);
      }

      return (byte[])var9;
   }
}
