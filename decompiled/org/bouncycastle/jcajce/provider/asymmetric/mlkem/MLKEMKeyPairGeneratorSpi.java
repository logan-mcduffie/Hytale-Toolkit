package org.bouncycastle.jcajce.provider.asymmetric.mlkem;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.spec.MLKEMParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mlkem.MLKEMPublicKeyParameters;
import org.bouncycastle.util.Strings;

public class MLKEMKeyPairGeneratorSpi extends KeyPairGenerator {
   MLKEMKeyGenerationParameters param;
   MLKEMKeyPairGenerator engine = new MLKEMKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;
   private MLKEMParameters mlkemParameters;

   public MLKEMKeyPairGeneratorSpi() {
      super("ML-KEM");
   }

   protected MLKEMKeyPairGeneratorSpi(MLKEMParameterSpec var1) {
      super(Strings.toUpperCase(var1.getName()));
      this.mlkemParameters = Utils.getParameters(var1.getName());
      if (this.param == null) {
         this.param = new MLKEMKeyGenerationParameters(this.random, this.mlkemParameters);
      }

      this.engine.init(this.param);
      this.initialised = true;
   }

   @Override
   public void initialize(int var1, SecureRandom var2) {
      throw new IllegalArgumentException("use AlgorithmParameterSpec");
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1) throws InvalidAlgorithmParameterException {
      try {
         this.initialize(var1, new BCJcaJceHelper().createSecureRandom("DEFAULT"));
      } catch (NoSuchAlgorithmException var3) {
         throw new IllegalStateException("unable to find DEFAULT DRBG");
      }
   }

   @Override
   public void initialize(AlgorithmParameterSpec var1, SecureRandom var2) throws InvalidAlgorithmParameterException {
      String var3 = getNameFromParams(var1);
      if (var3 != null) {
         MLKEMParameters var4 = Utils.getParameters(var3);
         if (var4 == null) {
            throw new InvalidAlgorithmParameterException("unknown parameter set name: " + var3);
         } else if (this.mlkemParameters != null && !var4.getName().equals(this.mlkemParameters.getName())) {
            throw new InvalidAlgorithmParameterException("key pair generator locked to " + this.getAlgorithm());
         } else {
            this.param = new MLKEMKeyGenerationParameters(var2, var4);
            this.engine.init(this.param);
            this.initialised = true;
         }
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         this.param = new MLKEMKeyGenerationParameters(this.random, MLKEMParameters.ml_kem_768);
         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      MLKEMPublicKeyParameters var2 = (MLKEMPublicKeyParameters)var1.getPublic();
      MLKEMPrivateKeyParameters var3 = (MLKEMPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCMLKEMPublicKey(var2), new BCMLKEMPrivateKey(var3));
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof MLKEMParameterSpec) {
         MLKEMParameterSpec var1 = (MLKEMParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toUpperCase(SpecUtil.getNameFrom(var0));
      }
   }

   public static class MLKEM1024 extends MLKEMKeyPairGeneratorSpi {
      public MLKEM1024() {
         super(MLKEMParameterSpec.ml_kem_1024);
      }
   }

   public static class MLKEM512 extends MLKEMKeyPairGeneratorSpi {
      public MLKEM512() {
         super(MLKEMParameterSpec.ml_kem_512);
      }
   }

   public static class MLKEM768 extends MLKEMKeyPairGeneratorSpi {
      public MLKEM768() {
         super(MLKEMParameterSpec.ml_kem_768);
      }
   }
}
