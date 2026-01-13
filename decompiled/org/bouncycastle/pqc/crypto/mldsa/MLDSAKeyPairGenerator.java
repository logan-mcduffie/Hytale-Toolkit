package org.bouncycastle.pqc.crypto.mldsa;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class MLDSAKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private MLDSAParameters parameters;
   private SecureRandom random;

   @Override
   public void init(KeyGenerationParameters var1) {
      this.parameters = ((MLDSAKeyGenerationParameters)var1).getParameters();
      this.random = var1.getRandom();
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      MLDSAEngine var1 = this.parameters.getEngine(this.random);
      byte[][] var2 = var1.generateKeyPair();
      MLDSAPublicKeyParameters var3 = new MLDSAPublicKeyParameters(this.parameters, var2[0], var2[6]);
      MLDSAPrivateKeyParameters var4 = new MLDSAPrivateKeyParameters(this.parameters, var2[0], var2[1], var2[2], var2[3], var2[4], var2[5], var2[6], var2[7]);
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var3, (AsymmetricKeyParameter)var4);
   }
}
