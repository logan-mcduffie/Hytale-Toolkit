package org.bouncycastle.pqc.crypto.frodo;

import java.security.SecureRandom;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.AsymmetricCipherKeyPairGenerator;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.crypto.params.AsymmetricKeyParameter;

public class FrodoKeyPairGenerator implements AsymmetricCipherKeyPairGenerator {
   private FrodoKeyGenerationParameters frodoParams;
   private int n;
   private int D;
   private int B;
   private SecureRandom random;

   private void initialize(KeyGenerationParameters var1) {
      this.frodoParams = (FrodoKeyGenerationParameters)var1;
      this.random = var1.getRandom();
      this.n = this.frodoParams.getParameters().getN();
      this.D = this.frodoParams.getParameters().getD();
      this.B = this.frodoParams.getParameters().getB();
   }

   private AsymmetricCipherKeyPair genKeyPair() {
      FrodoEngine var1 = this.frodoParams.getParameters().getEngine();
      byte[] var2 = new byte[var1.getPrivateKeySize()];
      byte[] var3 = new byte[var1.getPublicKeySize()];
      var1.kem_keypair(var3, var2, this.random);
      FrodoPublicKeyParameters var4 = new FrodoPublicKeyParameters(this.frodoParams.getParameters(), var3);
      FrodoPrivateKeyParameters var5 = new FrodoPrivateKeyParameters(this.frodoParams.getParameters(), var2);
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
