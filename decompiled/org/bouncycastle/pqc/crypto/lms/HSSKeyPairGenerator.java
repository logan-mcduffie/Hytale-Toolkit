package org.bouncycastle.pqc.crypto.lms;

import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class HSSKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   HSSKeyGenerationParameters param;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.param = (HSSKeyGenerationParameters)var1;
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      HSSPrivateKeyParameters var1 = HSS.generateHSSKeyPair(this.param);
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var1.getPublicKey(), (AsymmetricKeyParameter)var1);
   }
}
