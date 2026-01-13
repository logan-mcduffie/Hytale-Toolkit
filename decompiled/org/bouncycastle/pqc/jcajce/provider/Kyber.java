package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyFactorySpi;

public class Kyber {
   private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.kyber.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.KYBER", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyFactorySpi");
         this.addKeyFactoryAlgorithm(
            var1,
            "ML-KEM-512",
            "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyFactorySpi$Kyber512",
            NISTObjectIdentifiers.id_alg_ml_kem_512,
            new KyberKeyFactorySpi.Kyber512()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "ML-KEM-768",
            "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyFactorySpi$Kyber768",
            NISTObjectIdentifiers.id_alg_ml_kem_768,
            new KyberKeyFactorySpi.Kyber768()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "ML-KEM-1024",
            "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyFactorySpi$Kyber1024",
            NISTObjectIdentifiers.id_alg_ml_kem_1024,
            new KyberKeyFactorySpi.Kyber1024()
         );
         var1.addAlgorithm("Alg.Alias.KeyFactory.KYBER512", "ML-KEM-512");
         var1.addAlgorithm("Alg.Alias.KeyFactory.KYBER768", "ML-KEM-768");
         var1.addAlgorithm("Alg.Alias.KeyFactory.KYBER1024", "ML-KEM-1024");
         var1.addAlgorithm("KeyPairGenerator.ML-KEM", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyPairGeneratorSpi");
         var1.addAlgorithm("KeyPairGenerator.ML-KEM-512", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyPairGeneratorSpi$Kyber512");
         var1.addAlgorithm("KeyPairGenerator.ML-KEM-768", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyPairGeneratorSpi$Kyber768");
         var1.addAlgorithm("KeyPairGenerator.ML-KEM-1024", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyPairGeneratorSpi$Kyber1024");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.KYBER", "ML-KEM");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.KYBER512", "ML-KEM-512");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.KYBER768", "ML-KEM-768");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.KYBER1024", "ML-KEM-1024");
         var1.addAlgorithm("KeyGenerator.KYBER", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyGeneratorSpi");
         this.addKeyGeneratorAlgorithm(
            var1, "ML-KEM-512", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyGeneratorSpi$Kyber512", NISTObjectIdentifiers.id_alg_ml_kem_512
         );
         this.addKeyGeneratorAlgorithm(
            var1, "ML-KEM-768", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyGeneratorSpi$Kyber768", NISTObjectIdentifiers.id_alg_ml_kem_768
         );
         this.addKeyGeneratorAlgorithm(
            var1, "ML-KEM-1024", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberKeyGeneratorSpi$Kyber1024", NISTObjectIdentifiers.id_alg_ml_kem_1024
         );
         var1.addAlgorithm("Alg.Alias.KeyGenerator.KYBER512", "ML-KEM-512");
         var1.addAlgorithm("Alg.Alias.KeyGenerator.KYBER768", "ML-KEM-768");
         var1.addAlgorithm("Alg.Alias.KeyGenerator.KYBER1024", "ML-KEM-1024");
         KyberKeyFactorySpi var2 = new KyberKeyFactorySpi();
         var1.addAlgorithm("Cipher.KYBER", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberCipherSpi$Base");
         var1.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_kyber, "KYBER");
         this.addCipherAlgorithm(
            var1, "ML-KEM-512", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberCipherSpi$Kyber512", NISTObjectIdentifiers.id_alg_ml_kem_512
         );
         this.addCipherAlgorithm(
            var1, "ML-KEM-768", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberCipherSpi$Kyber768", NISTObjectIdentifiers.id_alg_ml_kem_768
         );
         this.addCipherAlgorithm(
            var1, "ML-KEM-1024", "org.bouncycastle.pqc.jcajce.provider.kyber.KyberCipherSpi$Kyber1024", NISTObjectIdentifiers.id_alg_ml_kem_1024
         );
         var1.addAlgorithm("Alg.Alias.Cipher.KYBER512", "ML-KEM-512");
         var1.addAlgorithm("Alg.Alias.Cipher.KYBER768", "ML-KEM-768");
         var1.addAlgorithm("Alg.Alias.Cipher.KYBER1024", "ML-KEM-1024");
         this.registerOid(var1, BCObjectIdentifiers.pqc_kem_kyber, "KYBER", var2);
      }
   }
}
