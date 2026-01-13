package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class MLKEM {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.mlkem.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.ML-KEM", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyFactorySpi");
         var1.addAlgorithm("Alg.Alias.KeyFactory.MLKEM", "ML-KEM");
         this.addKeyFactoryAlgorithm(
            var1,
            "ML-KEM-512",
            "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyFactorySpi$MLKEM512",
            NISTObjectIdentifiers.id_alg_ml_kem_512,
            new MLKEMKeyFactorySpi.MLKEM512()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "ML-KEM-768",
            "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyFactorySpi$MLKEM768",
            NISTObjectIdentifiers.id_alg_ml_kem_768,
            new MLKEMKeyFactorySpi.MLKEM768()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "ML-KEM-1024",
            "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyFactorySpi$MLKEM1024",
            NISTObjectIdentifiers.id_alg_ml_kem_1024,
            new MLKEMKeyFactorySpi.MLKEM1024()
         );
         var1.addAlgorithm("KeyPairGenerator.ML-KEM", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyPairGeneratorSpi");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.MLKEM", "ML-KEM");
         this.addKeyPairGeneratorAlgorithm(
            var1, "ML-KEM-512", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyPairGeneratorSpi$MLKEM512", NISTObjectIdentifiers.id_alg_ml_kem_512
         );
         this.addKeyPairGeneratorAlgorithm(
            var1, "ML-KEM-768", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyPairGeneratorSpi$MLKEM768", NISTObjectIdentifiers.id_alg_ml_kem_768
         );
         this.addKeyPairGeneratorAlgorithm(
            var1,
            "ML-KEM-1024",
            "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyPairGeneratorSpi$MLKEM1024",
            NISTObjectIdentifiers.id_alg_ml_kem_1024
         );
         var1.addAlgorithm("KeyGenerator.ML-KEM", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyGeneratorSpi");
         this.addKeyGeneratorAlgorithm(
            var1, "ML-KEM-512", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyGeneratorSpi$MLKEM512", NISTObjectIdentifiers.id_alg_ml_kem_512
         );
         this.addKeyGeneratorAlgorithm(
            var1, "ML-KEM-768", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyGeneratorSpi$MLKEM768", NISTObjectIdentifiers.id_alg_ml_kem_768
         );
         this.addKeyGeneratorAlgorithm(
            var1, "ML-KEM-1024", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMKeyGeneratorSpi$MLKEM1024", NISTObjectIdentifiers.id_alg_ml_kem_1024
         );
         MLKEMKeyFactorySpi var2 = new MLKEMKeyFactorySpi();
         var1.addAlgorithm("Cipher.ML-KEM", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMCipherSpi$Base");
         var1.addAlgorithm("Alg.Alias.Cipher.MLKEM", "ML-KEM");
         this.addCipherAlgorithm(
            var1, "ML-KEM-512", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMCipherSpi$MLKEM512", NISTObjectIdentifiers.id_alg_ml_kem_512
         );
         this.addCipherAlgorithm(
            var1, "ML-KEM-768", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMCipherSpi$MLKEM768", NISTObjectIdentifiers.id_alg_ml_kem_768
         );
         this.addCipherAlgorithm(
            var1, "ML-KEM-1024", "org.bouncycastle.jcajce.provider.asymmetric.mlkem.MLKEMCipherSpi$MLKEM1024", NISTObjectIdentifiers.id_alg_ml_kem_1024
         );
         var1.addKeyInfoConverter(NISTObjectIdentifiers.id_alg_ml_kem_512, var2);
         var1.addKeyInfoConverter(NISTObjectIdentifiers.id_alg_ml_kem_768, var2);
         var1.addKeyInfoConverter(NISTObjectIdentifiers.id_alg_ml_kem_1024, var2);
      }
   }
}
