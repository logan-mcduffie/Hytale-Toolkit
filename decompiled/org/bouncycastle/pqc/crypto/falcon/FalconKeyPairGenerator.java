package org.bouncycastle.pqc.crypto.falcon;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class FalconKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private FalconKeyGenerationParameters params;
   private FalconNIST nist;
   private int pk_size;
   private int sk_size;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.params = (FalconKeyGenerationParameters)var1;
      SecureRandom var2 = var1.getRandom();
      int var3 = ((FalconKeyGenerationParameters)var1).getParameters().getLogN();
      int var4 = ((FalconKeyGenerationParameters)var1).getParameters().getNonceLength();
      this.nist = new FalconNIST(var3, var4, var2);
      int var5 = 1 << var3;
      byte var6 = 8;
      if (var5 == 1024) {
         var6 = 5;
      } else if (var5 == 256 || var5 == 512) {
         var6 = 6;
      } else if (var5 == 64 || var5 == 128) {
         var6 = 7;
      }

      this.pk_size = 1 + 14 * var5 / 8;
      this.sk_size = 1 + 2 * var6 * var5 / 8 + var5;
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      byte[] var1 = new byte[this.pk_size];
      byte[] var2 = new byte[this.sk_size];
      byte[][] var3 = this.nist.crypto_sign_keypair(var1, var2);
      FalconParameters var4 = this.params.getParameters();
      FalconPrivateKeyParameters var5 = new FalconPrivateKeyParameters(var4, var3[1], var3[2], var3[3], var3[0]);
      FalconPublicKeyParameters var6 = new FalconPublicKeyParameters(var4, var3[0]);
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var6, (AsymmetricKeyParameter)var5);
   }
}
