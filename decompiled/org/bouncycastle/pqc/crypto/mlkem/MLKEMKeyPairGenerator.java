package org.bouncycastle.pqc.crypto.mlkem;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class MLKEMKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private MLKEMParameters mlkemParams;
   private SecureRandom random;

   private void initialize(KeyGenerationParameters var1) {
      this.mlkemParams = ((MLKEMKeyGenerationParameters)var1).getParameters();
      this.random = var1.getRandom();
   }

   private AsymmetricCipherKeyPair genKeyPair() {
      MLKEMEngine var1 = this.mlkemParams.getEngine();
      var1.init(this.random);
      byte[][] var2 = var1.generateKemKeyPair();
      MLKEMPublicKeyParameters var3 = new MLKEMPublicKeyParameters(this.mlkemParams, var2[0], var2[1]);
      MLKEMPrivateKeyParameters var4 = new MLKEMPrivateKeyParameters(this.mlkemParams, var2[2], var2[3], var2[4], var2[0], var2[1], var2[5]);
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var3, (AsymmetricKeyParameter)var4);
   }

   @Override
   public void init(KeyGenerationParameters var1) {
      this.initialize(var1);
   }

   @Override
   public AsymmetricCipherKeyPair generateKeyPair() {
      return this.genKeyPair();
   }
}
