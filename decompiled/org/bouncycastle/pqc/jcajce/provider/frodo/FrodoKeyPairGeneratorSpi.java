package org.bouncycastle.pqc.jcajce.provider.frodo;

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
import org.bouncycastle.pqc.crypto.frodo.FrodoKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoKeyPairGenerator;
import org.bouncycastle.pqc.crypto.frodo.FrodoParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.frodo.FrodoPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.FrodoParameterSpec;
import org.bouncycastle.util.Strings;

public class FrodoKeyPairGeneratorSpi extends KeyPairGenerator {
   private static Map parameters = new HashMap();
   FrodoKeyGenerationParameters param;
   FrodoKeyPairGenerator engine = new FrodoKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public FrodoKeyPairGeneratorSpi() {
      super("Frodo");
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null) {
         this.param = new FrodoKeyGenerationParameters(var2, (FrodoParameters)parameters.get(var3));
         this.engine.init(this.param);
         this.initialised = true;
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof FrodoParameterSpec) {
         FrodoParameterSpec var1 = (FrodoParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toLowerCase(SpecUtil.getNameFrom(var0));
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.param = new FrodoKeyGenerationParameters(this.random, FrodoParameters.frodokem1344shake);
         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      FrodoPublicKeyParameters var2 = (FrodoPublicKeyParameters)var1.getPublic();
      FrodoPrivateKeyParameters var3 = (FrodoPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCFrodoPublicKey(var2), new BCFrodoPrivateKey(var3));
   }

   static {
      parameters.put("frodokem19888r3", FrodoParameters.frodokem640aes);
      parameters.put("frodokem19888shaker3", FrodoParameters.frodokem640shake);
      parameters.put("frodokem31296r3", FrodoParameters.frodokem976aes);
      parameters.put("frodokem31296shaker3", FrodoParameters.frodokem976shake);
      parameters.put("frodokem43088r3", FrodoParameters.frodokem1344aes);
      parameters.put("frodokem43088shaker3", FrodoParameters.frodokem1344shake);
      parameters.put(FrodoParameterSpec.frodokem640aes.getName(), FrodoParameters.frodokem640aes);
      parameters.put(FrodoParameterSpec.frodokem640shake.getName(), FrodoParameters.frodokem640shake);
      parameters.put(FrodoParameterSpec.frodokem976aes.getName(), FrodoParameters.frodokem976aes);
      parameters.put(FrodoParameterSpec.frodokem976shake.getName(), FrodoParameters.frodokem976shake);
      parameters.put(FrodoParameterSpec.frodokem1344aes.getName(), FrodoParameters.frodokem1344aes);
      parameters.put(FrodoParameterSpec.frodokem1344shake.getName(), FrodoParameters.frodokem1344shake);
   }
}
