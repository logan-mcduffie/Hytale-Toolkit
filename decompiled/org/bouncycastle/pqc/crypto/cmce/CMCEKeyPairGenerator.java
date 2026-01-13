package org.bouncycastle.pqc.crypto.cmce;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class CMCEKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private CMCEKeyGenerationParameters cmceParams;
   private SecureRandom random;

   private void initialize(KeyGenerationParameters var1) {
      this.cmceParams = (CMCEKeyGenerationParameters)var1;
      this.random = var1.getRandom();
   }

   private AsymmetricCipherKeyPair genKeyPair() {
      CMCEEngine var1 = this.cmceParams.getParameters().getEngine();
      byte[] var2 = new byte[var1.getPrivateKeySize()];
      byte[] var3 = new byte[var1.getPublicKeySize()];
      var1.kem_keypair(var3, var2, this.random);
      CMCEPublicKeyParameters var4 = new CMCEPublicKeyParameters(this.cmceParams.getParameters(), var3);
      CMCEPrivateKeyParameters var5 = new CMCEPrivateKeyParameters(this.cmceParams.getParameters(), var2);
      return new AsymmetricCipherKeyPair((AsymmetricKeyParameter)var4, (AsymmetricKeyParameter)var5);
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
