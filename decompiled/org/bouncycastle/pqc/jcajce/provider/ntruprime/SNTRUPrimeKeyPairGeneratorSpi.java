package org.bouncycastle.pqc.jcajce.provider.ntruprime;

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
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeKeyPairGenerator;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.SNTRUPrimePublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.SNTRUPrimeParameterSpec;
import org.bouncycastle.util.Strings;

public class SNTRUPrimeKeyPairGeneratorSpi extends KeyPairGenerator {
   private static Map parameters = new HashMap();
   SNTRUPrimeKeyGenerationParameters param;
   SNTRUPrimeKeyPairGenerator engine = new SNTRUPrimeKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public SNTRUPrimeKeyPairGeneratorSpi() {
      super("SNTRUPrime");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null) {
         this.param = new SNTRUPrimeKeyGenerationParameters(var2, (SNTRUPrimeParameters)parameters.get(var3));
         this.engine.init(this.param);
         this.initialised = true;
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof SNTRUPrimeParameterSpec) {
         SNTRUPrimeParameterSpec var1 = (SNTRUPrimeParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toLowerCase(SpecUtil.getNameFrom(var0));
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.param = new SNTRUPrimeKeyGenerationParameters(this.random, SNTRUPrimeParameters.sntrup953);
         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      SNTRUPrimePublicKeyParameters var2 = (SNTRUPrimePublicKeyParameters)var1.getPublic();
      SNTRUPrimePrivateKeyParameters var3 = (SNTRUPrimePrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCSNTRUPrimePublicKey(var2), new BCSNTRUPrimePrivateKey(var3));
   }

   static {
      parameters.put(SNTRUPrimeParameterSpec.sntrup653.getName(), SNTRUPrimeParameters.sntrup653);
      parameters.put(SNTRUPrimeParameterSpec.sntrup761.getName(), SNTRUPrimeParameters.sntrup761);
      parameters.put(SNTRUPrimeParameterSpec.sntrup857.getName(), SNTRUPrimeParameters.sntrup857);
      parameters.put(SNTRUPrimeParameterSpec.sntrup953.getName(), SNTRUPrimeParameters.sntrup953);
      parameters.put(SNTRUPrimeParameterSpec.sntrup1013.getName(), SNTRUPrimeParameters.sntrup1013);
      parameters.put(SNTRUPrimeParameterSpec.sntrup1277.getName(), SNTRUPrimeParameters.sntrup1277);
   }
}
