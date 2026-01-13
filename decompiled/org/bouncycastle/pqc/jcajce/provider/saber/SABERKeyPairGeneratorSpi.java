package org.bouncycastle.pqc.jcajce.provider.saber;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.pqc.crypto.saber.SABERKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.saber.SABERKeyPairGenerator;
import org.bouncycastle.pqc.crypto.saber.SABERParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.saber.SABERPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.SABERParameterSpec;
import org.bouncycastle.util.Strings;

public class SABERKeyPairGeneratorSpi extends KeyPairGenerator {
   private static Map parameters = new HashMap();
   SABERKeyGenerationParameters param;
   SABERKeyPairGenerator engine = new SABERKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public SABERKeyPairGeneratorSpi() {
      super("SABER");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null) {
         this.param = new SABERKeyGenerationParameters(var2, (SABERParameters)parameters.get(var3));
         this.engine.init(this.param);
         this.initialised = true;
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof SABERParameterSpec) {
         SABERParameterSpec var1 = (SABERParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toLowerCase(SpecUtil.getNameFrom(var0));
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.param = new SABERKeyGenerationParameters(this.random, SABERParameters.firesaberkem256r3);
         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      SABERPublicKeyParameters var2 = (SABERPublicKeyParameters)var1.getPublic();
      SABERPrivateKeyParameters var3 = (SABERPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCSABERPublicKey(var2), new BCSABERPrivateKey(var3));
   }

   static {
      parameters.put(SABERParameterSpec.lightsaberkem128r3.getName(), SABERParameters.lightsaberkem128r3);
      parameters.put(SABERParameterSpec.saberkem128r3.getName(), SABERParameters.saberkem128r3);
      parameters.put(SABERParameterSpec.firesaberkem128r3.getName(), SABERParameters.firesaberkem128r3);
      parameters.put(SABERParameterSpec.lightsaberkem192r3.getName(), SABERParameters.lightsaberkem192r3);
      parameters.put(SABERParameterSpec.saberkem192r3.getName(), SABERParameters.saberkem192r3);
      parameters.put(SABERParameterSpec.firesaberkem192r3.getName(), SABERParameters.firesaberkem192r3);
      parameters.put(SABERParameterSpec.lightsaberkem256r3.getName(), SABERParameters.lightsaberkem256r3);
      parameters.put(SABERParameterSpec.saberkem256r3.getName(), SABERParameters.saberkem256r3);
      parameters.put(SABERParameterSpec.firesaberkem256r3.getName(), SABERParameters.firesaberkem256r3);
   }
}
