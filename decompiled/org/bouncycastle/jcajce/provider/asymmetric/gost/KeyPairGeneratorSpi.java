package org.bouncycastle.jcajce.provider.asymmetric.gost;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.crypto.generators.GOST3410KeyPairGenerator;
import org.bouncycastle.crypto.params.GOST3410KeyGenerationParameters;
import org.bouncycastle.crypto.params.GOST3410Parameters;
import org.bouncycastle.crypto.params.GOST3410PrivateKeyParameters;
import org.bouncycastle.crypto.params.GOST3410PublicKeyParameters;
import org.bouncycastle.jce.spec.GOST3410ParameterSpec;
import org.bouncycastle.jce.spec.GOST3410PublicKeyParameterSetSpec;

public class KeyPairGeneratorSpi extends KeyPairGenerator {
   GOST3410KeyGenerationParameters param;
   GOST3410KeyPairGenerator engine = new GOST3410KeyPairGenerator();
   GOST3410ParameterSpec gost3410Params;
   int strength = 1024;
   SecureRandom random = null;
   boolean initialised = false;

   public KeyPairGeneratorSpi() {
      super("GOST3410");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      this.strength = var1;
      this.random = var2;
   }

   private void init(GOST3410ParameterSpec var1, SecureRandom var2) {
      GOST3410PublicKeyParameterSetSpec var3 = var1.getPublicKeyParameters();
      this.param = new GOST3410KeyGenerationParameters(var2, new GOST3410Parameters(var3.getP(), var3.getQ(), var3.getA()));
      this.engine.init(this.param);
      this.initialised = true;
      this.gost3410Params = var1;
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      if (!(var1 instanceof GOST3410ParameterSpec)) {
         throw new InvalidAlgorithmParameterException("parameter object not a GOST3410ParameterSpec");
      } else {
         this.init((GOST3410ParameterSpec)var1, var2);
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.init(new GOST3410ParameterSpec(CryptoProObjectIdentifiers.gostR3410_94_CryptoPro_A.getId()), CryptoServicesRegistrar.getSecureRandom());
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      GOST3410PublicKeyParameters var2 = (GOST3410PublicKeyParameters)var1.getPublic();
      GOST3410PrivateKeyParameters var3 = (GOST3410PrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCGOST3410PublicKey(var2, this.gost3410Params), new BCGOST3410PrivateKey(var3, this.gost3410Params));
   }
}
