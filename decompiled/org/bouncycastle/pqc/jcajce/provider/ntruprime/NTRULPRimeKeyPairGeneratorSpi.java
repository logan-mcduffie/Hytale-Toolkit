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
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeKeyPairGenerator;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimeParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePrivateKeyParameters;
import org.bouncycastle.pqc.crypto.ntruprime.NTRULPRimePublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.NTRULPRimeParameterSpec;
import org.bouncycastle.util.Strings;

public class NTRULPRimeKeyPairGeneratorSpi extends KeyPairGenerator {
   private static Map parameters = new HashMap();
   NTRULPRimeKeyGenerationParameters param;
   NTRULPRimeKeyPairGenerator engine = new NTRULPRimeKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public NTRULPRimeKeyPairGeneratorSpi() {
      super("NTRULPRime");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null) {
         this.param = new NTRULPRimeKeyGenerationParameters(var2, (NTRULPRimeParameters)parameters.get(var3));
         this.engine.init(this.param);
         this.initialised = true;
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof NTRULPRimeParameterSpec) {
         NTRULPRimeParameterSpec var1 = (NTRULPRimeParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toLowerCase(SpecUtil.getNameFrom(var0));
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.param = new NTRULPRimeKeyGenerationParameters(this.random, NTRULPRimeParameters.ntrulpr953);
         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      NTRULPRimePublicKeyParameters var2 = (NTRULPRimePublicKeyParameters)var1.getPublic();
      NTRULPRimePrivateKeyParameters var3 = (NTRULPRimePrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCNTRULPRimePublicKey(var2), new BCNTRULPRimePrivateKey(var3));
   }

   static {
      parameters.put(NTRULPRimeParameterSpec.ntrulpr653.getName(), NTRULPRimeParameters.ntrulpr653);
      parameters.put(NTRULPRimeParameterSpec.ntrulpr761.getName(), NTRULPRimeParameters.ntrulpr761);
      parameters.put(NTRULPRimeParameterSpec.ntrulpr857.getName(), NTRULPRimeParameters.ntrulpr857);
      parameters.put(NTRULPRimeParameterSpec.ntrulpr953.getName(), NTRULPRimeParameters.ntrulpr953);
      parameters.put(NTRULPRimeParameterSpec.ntrulpr1013.getName(), NTRULPRimeParameters.ntrulpr1013);
      parameters.put(NTRULPRimeParameterSpec.ntrulpr1277.getName(), NTRULPRimeParameters.ntrulpr1277);
   }
}
