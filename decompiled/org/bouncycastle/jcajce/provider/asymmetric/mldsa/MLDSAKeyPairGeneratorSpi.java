package org.bouncycastle.jcajce.provider.asymmetric.mldsa;

import java.security.InvalidAlgorithmParameterException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import org.bouncycastle.crypto.AsymmetricCipherKeyPair;
import org.bouncycastle.crypto.CryptoServicesRegistrar;
import org.bouncycastle.jcajce.spec.MLDSAParameterSpec;
import org.bouncycastle.jcajce.util.BCJcaJceHelper;
import org.bouncycastle.jcajce.util.SpecUtil;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyGenerationParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAKeyPairGenerator;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPrivateKeyParameters;
import org.bouncycastle.pqc.crypto.mldsa.MLDSAPublicKeyParameters;
import org.bouncycastle.util.Strings;

public class MLDSAKeyPairGeneratorSpi extends KeyPairGenerator {
   private final MLDSAParameters mldsaParameters;
   MLDSAKeyGenerationParameters param;
   MLDSAKeyPairGenerator engine = new MLDSAKeyPairGenerator();
   SecureRandom random = CryptoServicesRegistrar.getSecureRandom();
   boolean initialised = false;

   public MLDSAKeyPairGeneratorSpi(String var1) {
      super(var1);
      this.mldsaParameters = null;
   }

   protected MLDSAKeyPairGeneratorSpi(MLDSAParameterSpec var1) {
      super(Strings.toUpperCase(var1.getName()));
      this.mldsaParameters = Utils.getParameters(var1.getName());
      if (this.param == null) {
         this.param = new MLDSAKeyGenerationParameters(this.random, this.mldsaParameters);
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
         MLDSAParameters var4 = Utils.getParameters(var3);
         if (var4 == null) {
            throw new InvalidAlgorithmParameterException("unknown parameter set name: " + var3);
         } else {
            this.param = new MLDSAKeyGenerationParameters(var2, var4);
            if (this.mldsaParameters != null && !var4.getName().equals(this.mldsaParameters.getName())) {
               throw new InvalidAlgorithmParameterException(
                  "key pair generator locked to " + MLDSAParameterSpec.fromName(this.mldsaParameters.getName()).getName()
               );
            } else {
               this.engine.init(this.param);
               this.initialised = true;
            }
         }
      } else {
         throw new InvalidAlgorithmParameterException("invalid ParameterSpec: " + var1);
      }
   }

   @Override
   public KeyPair generateKeyPair() {
      if (!this.initialised) {
         if (this.getAlgorithm().startsWith("HASH")) {
            this.param = new MLDSAKeyGenerationParameters(this.random, MLDSAParameters.ml_dsa_87_with_sha512);
         } else {
            this.param = new MLDSAKeyGenerationParameters(this.random, MLDSAParameters.ml_dsa_87);
         }

         this.engine.init(this.param);
         this.initialised = true;
      }

      AsymmetricCipherKeyPair var1 = this.engine.generateKeyPair();
      MLDSAPublicKeyParameters var2 = (MLDSAPublicKeyParameters)var1.getPublic();
      MLDSAPrivateKeyParameters var3 = (MLDSAPrivateKeyParameters)var1.getPrivate();
      return new KeyPair(new BCMLDSAPublicKey(var2), new BCMLDSAPrivateKey(var3));
   }

   private static String getNameFromParams(AlgorithmParameterSpec var0) {
      if (var0 instanceof MLDSAParameterSpec) {
         MLDSAParameterSpec var1 = (MLDSAParameterSpec)var0;
         return var1.getName();
      } else {
         return Strings.toUpperCase(SpecUtil.getNameFrom(var0));
      }
   }

   public static class Hash extends MLDSAKeyPairGeneratorSpi {
      public Hash() throws NoSuchAlgorithmException {
         super("HASH-ML-DSA");
      }
   }

   public static class MLDSA44 extends MLDSAKeyPairGeneratorSpi {
      public MLDSA44() throws NoSuchAlgorithmException {
         super(MLDSAParameterSpec.ml_dsa_44);
      }
   }

   public static class MLDSA44withSHA512 extends MLDSAKeyPairGeneratorSpi {
      public MLDSA44withSHA512() throws NoSuchAlgorithmException {
         super(MLDSAParameterSpec.ml_dsa_44_with_sha512);
      }
   }

   public static class MLDSA65 extends MLDSAKeyPairGeneratorSpi {
      public MLDSA65() throws NoSuchAlgorithmException {
         super(MLDSAParameterSpec.ml_dsa_65);
      }
   }

   public static class MLDSA65withSHA512 extends MLDSAKeyPairGeneratorSpi {
      public MLDSA65withSHA512() throws NoSuchAlgorithmException {
         super(MLDSAParameterSpec.ml_dsa_65_with_sha512);
      }
   }

   public static class MLDSA87 extends MLDSAKeyPairGeneratorSpi {
      public MLDSA87() throws NoSuchAlgorithmException {
         super(MLDSAParameterSpec.ml_dsa_87);
      }
   }

   public static class MLDSA87withSHA512 extends MLDSAKeyPairGeneratorSpi {
      public MLDSA87withSHA512() throws NoSuchAlgorithmException {
         super(MLDSAParameterSpec.ml_dsa_87_with_sha512);
      }
   }

   public static class Pure extends MLDSAKeyPairGeneratorSpi {
      public Pure() throws NoSuchAlgorithmException {
         super("ML-DSA");
      }
   }
}
