package org.bouncycastle.pqc.jcajce.provider.kyber;

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
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.pqc.jcajce.spec.KyberParameterSpec;
import org.bouncycastle.util.Strings;

public class KyberKeyPairGeneratorSpi extends KeyPairGenerator {
   private static Map parameters = new HashMap();
   MLKEMKeyGenerationParameters param;
   MLKEMKeyPairGenerator engine = new MLKEMKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;
   private MLKEMParameters kyberParameters;

   public KyberKeyPairGeneratorSpi() {
      super("KYBER");
      this.kyberParameters = null;
   }

   protected KyberKeyPairGeneratorSpi(MLKEMParameters var1) {
      super(Strings.toUpperCase(var1.getName()));
      this.kyberParameters = var1;
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null && parameters.containsKey(var3)) {
         MLKEMParameters var4 = (MLKEMParameters)parameters.get(var3);
         this.param = new MLKEMKeyGenerationParameters(var2, var4);
         if (this.kyberParameters != null && !var4.getName().equals(this.kyberParameters.getName())) {
            throw new InvalidAlgorithmParameterException("key pair generator locked to " + Strings.toUpperCase(this.kyberParameters.getName()));
         } else {
            this.engine.init(this.param);
            this.initialised = true;
         }
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof KyberParameterSpec) {
         KyberParameterSpec var1 = (KyberParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toLowerCase(SpecUtil.getNameFrom(var0));
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         if (this.kyberParameters != null) {
            this.param = new MLKEMKeyGenerationParameters(this.random, this.kyberParameters);
         } else {
            this.param = new MLKEMKeyGenerationParameters(this.random, MLKEMParameters.ml_kem_1024);
         }

         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      MLKEMPublicKeyParameters var2 = (MLKEMPublicKeyParameters)var1.getPublic();
      MLKEMPrivateKeyParameters var3 = (MLKEMPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCKyberPublicKey(var2), new BCKyberPrivateKey(var3));
   }

   static {
      parameters.put(KyberParameterSpec.kyber512.getName(), MLKEMParameters.ml_kem_512);
      parameters.put(KyberParameterSpec.kyber768.getName(), MLKEMParameters.ml_kem_768);
      parameters.put(KyberParameterSpec.kyber1024.getName(), MLKEMParameters.ml_kem_1024);
   }

   public static class Kyber1024 extends KyberKeyPairGeneratorSpi {
      public Kyber1024() {
         super(MLKEMParameters.ml_kem_1024);
      }
   }

   public static class Kyber512 extends KyberKeyPairGeneratorSpi {
      public Kyber512() {
         super(MLKEMParameters.ml_kem_512);
      }
   }

   public static class Kyber768 extends KyberKeyPairGeneratorSpi {
      public Kyber768() {
         super(MLKEMParameters.ml_kem_768);
      }
   }
}
