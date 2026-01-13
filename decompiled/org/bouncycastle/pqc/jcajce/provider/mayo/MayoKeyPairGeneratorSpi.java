package org.bouncycastle.pqc.jcajce.provider.mayo;

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
import org.bouncycastle.pqc.crypto.mayo.MayoKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mayo.MayoParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mayo.MayoPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.MayoParameterSpec;
import org.bouncycastle.util.Strings;

public class MayoKeyPairGeneratorSpi extends KeyPairGenerator {
   private static Map parameters = new HashMap();
   MayoKeyGenerationParameters param;
   private MayoParameters mayoParameters;
   MayoKeyPairGenerator engine = new MayoKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public MayoKeyPairGeneratorSpi() {
      super("Mayo");
   }

   protected MayoKeyPairGeneratorSpi(MayoParameters var1) {
      super(var1.getName());
      this.mayoParameters = var1;
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null) {
         this.param = new MayoKeyGenerationParameters(var2, (MayoParameters)parameters.get(var3));
         this.engine.init(this.param);
         this.initialised = true;
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof MayoParameterSpec) {
         MayoParameterSpec var1 = (MayoParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toLowerCase(SpecUtil.getNameFrom(var0));
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.param = new MayoKeyGenerationParameters(this.random, MayoParameters.mayo1);
         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      MayoPublicKeyParameters var2 = (MayoPublicKeyParameters)var1.getPublic();
      MayoPrivateKeyParameters var3 = (MayoPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCMayoPublicKey(var2), new BCMayoPrivateKey(var3));
   }

   static {
      parameters.put("MAYO_1", MayoParameters.mayo1);
      parameters.put("MAYO_2", MayoParameters.mayo2);
      parameters.put("MAYO_3", MayoParameters.mayo3);
      parameters.put("MAYO_5", MayoParameters.mayo5);
      parameters.put(MayoParameterSpec.mayo1.getName(), MayoParameters.mayo1);
      parameters.put(MayoParameterSpec.mayo2.getName(), MayoParameters.mayo2);
      parameters.put(MayoParameterSpec.mayo3.getName(), MayoParameters.mayo3);
      parameters.put(MayoParameterSpec.mayo5.getName(), MayoParameters.mayo5);
   }

   public static class Mayo1 extends MayoKeyPairGeneratorSpi {
      public Mayo1() {
         super(MayoParameters.mayo1);
      }
   }

   public static class Mayo2 extends MayoKeyPairGeneratorSpi {
      public Mayo2() {
         super(MayoParameters.mayo2);
      }
   }

   public static class Mayo3 extends MayoKeyPairGeneratorSpi {
      public Mayo3() {
         super(MayoParameters.mayo3);
      }
   }

   public static class Mayo5 extends MayoKeyPairGeneratorSpi {
      public Mayo5() {
         super(MayoParameters.mayo5);
      }
   }
}
