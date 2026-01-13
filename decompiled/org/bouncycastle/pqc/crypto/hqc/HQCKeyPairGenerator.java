package org.bouncycastle.pqc.crypto.hqc;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class HQCKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private HQCKeyGenerationParameters hqcKeyGenerationParameters;
   private SecureRandom random;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.hqcKeyGenerationParameters = (HQCKeyGenerationParameters)var1;
      this.random = var1.getRandom();
   }

   private AsymmetricCipherKeyPair genKeyPair() {
      HQCEngine var1 = this.hqcKeyGenerationParameters.getParameters().getEngine();
      byte[] var2 = new byte[this.hqcKeyGenerationParameters.getParameters().getPublicKeyBytes()];
      byte[] var3 = new byte[this.hqcKeyGenerationParameters.getParameters().getSecretKeyBytes()];
      var1.genKeyPair(var2, var3, this.random);
      HQCPublicKeyParameters var4 = new HQCPublicKeyParameters(this.hqcKeyGenerationParameters.getParameters(), var2);
      HQCPrivateKeyParameters var5 = new HQCPrivateKeyParameters(this.hqcKeyGenerationParameters.getParameters(), var3);
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var4, (AsymmetricKeyParameter)var5);
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      return this.genKeyPair();
   }
}
