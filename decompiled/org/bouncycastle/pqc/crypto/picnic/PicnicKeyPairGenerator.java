package org.bouncycastle.pqc.crypto.picnic;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class PicnicKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private SecureRandom random;
   private PicnicParameters parameters;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.random = var1.getRandom();
      this.parameters = ((PicnicKeyGenerationParameters)var1).getParameters();
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      PicnicEngine var1 = this.parameters.getEngine();
      byte[] var2 = new byte[var1.getSecretKeySize()];
      byte[] var3 = new byte[var1.getPublicKeySize()];
      var1.crypto_sign_keypair(var3, var2, this.random);
      PicnicPublicKeyParameters var4 = new PicnicPublicKeyParameters(this.parameters, var3);
      PicnicPrivateKeyParameters var5 = new PicnicPrivateKeyParameters(this.parameters, var2);
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var4, (AsymmetricKeyParameter)var5);
   }
}
