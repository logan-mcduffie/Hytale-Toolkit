package org.bouncycastle.pqc.crypto.saber;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class SABERKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private SABERKeyGenerationParameters saberParams;
   private int l;
   private SecureRandom random;

   private void initialize(KeyGenerationParameters var1) {
      this.saberParams = (SABERKeyGenerationParameters)var1;
      this.random = var1.getRandom();
      this.l = this.saberParams.getParameters().getL();
   }

   private AsymmetricCipherKeyPair genKeyPair() {
      SABEREngine var1 = this.saberParams.getParameters().getEngine();
      byte[] var2 = new byte[var1.getPrivateKeySize()];
      byte[] var3 = new byte[var1.getPublicKeySize()];
      var1.crypto_kem_keypair(var3, var2, this.random);
      SABERPublicKeyParameters var4 = new SABERPublicKeyParameters(this.saberParams.getParameters(), var3);
      SABERPrivateKeyParameters var5 = new SABERPrivateKeyParameters(this.saberParams.getParameters(), var2);
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
