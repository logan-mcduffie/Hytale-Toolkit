package org.bouncycastle.pqc.jcajce.provider.newhope;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.KeyGenerationParameters;
import org.bouncycastle.pqc.crypto.newhope.NHKeyPairGenerator;
import org.bouncycastle.pqc.crypto.newhope.NHPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.newhope.NHPublicKeyParameters;

public class NHKeyPairGeneratorSpi extends KeyPairGenerator {
   NHKeyPairGenerator engine = new NHKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public NHKeyPairGeneratorSpi() {
      super("NH");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      if (var1 != 1024) {
         throw new IllegalArgumentException("strength must be 1024 bits");
      } else {
         this.engine.init(new KeyGenerationParameters(var2, 1024));
         this.initialised = true;
      }
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      throw new InvalidAlgorithmParameterException("parameter object not recognised");
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.engine.init(new KeyGenerationParameters(this.random, 1024));
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      NHPublicKeyParameters var2 = (NHPublicKeyParameters)var1.getPublic();
      NHPrivateKeyParameters var3 = (NHPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCNHPublicKey(var2), new BCNHPrivateKey(var3));
   }
}
