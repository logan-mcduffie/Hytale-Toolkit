package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.mayo.MayoKeyFactorySpi;

public class Mayo {
   private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.mayo.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.Mayo", "org.bouncycastle.pqc.jcajce.provider.mayo.MayoKeyFactorySpi");
         this.addKeyFactoryAlgorithm(
            var1, "MAYO_1", "org.bouncycastle.pqc.jcajce.provider.mayo.MayoKeyFactorySpi$Mayo1", BCObjectIdentifiers.mayo1, new MayoKeyFactorySpi.Mayo1()
         );
         this.addKeyFactoryAlgorithm(
            var1, "MAYO_2", "org.bouncycastle.pqc.jcajce.provider.mayo.MayoKeyFactorySpi$Mayo2", BCObjectIdentifiers.mayo2, new MayoKeyFactorySpi.Mayo2()
         );
         this.addKeyFactoryAlgorithm(
            var1, "MAYO_3", "org.bouncycastle.pqc.jcajce.provider.mayo.MayoKeyFactorySpi$Mayo3", BCObjectIdentifiers.mayo3, new MayoKeyFactorySpi.Mayo3()
         );
         this.addKeyFactoryAlgorithm(
            var1, "MAYO_5", "org.bouncycastle.pqc.jcajce.provider.mayo.MayoKeyFactorySpi$Mayo5", BCObjectIdentifiers.mayo5, new MayoKeyFactorySpi.Mayo5()
         );
         var1.addAlgorithm("KeyPairGenerator.Mayo", "org.bouncycastle.pqc.jcajce.provider.mayo.MayoKeyPairGeneratorSpi");
         this.addKeyPairGeneratorAlgorithm(var1, "MAYO_1", "org.bouncycastle.pqc.jcajce.provider.mayo.MayoKeyPairGeneratorSpi$Mayo1", BCObjectIdentifiers.mayo1);
         this.addKeyPairGeneratorAlgorithm(var1, "MAYO_2", "org.bouncycastle.pqc.jcajce.provider.mayo.MayoKeyPairGeneratorSpi$Mayo2", BCObjectIdentifiers.mayo2);
         this.addKeyPairGeneratorAlgorithm(var1, "MAYO_3", "org.bouncycastle.pqc.jcajce.provider.mayo.MayoKeyPairGeneratorSpi$Mayo3", BCObjectIdentifiers.mayo3);
         this.addKeyPairGeneratorAlgorithm(var1, "MAYO_5", "org.bouncycastle.pqc.jcajce.provider.mayo.MayoKeyPairGeneratorSpi$Mayo5", BCObjectIdentifiers.mayo5);
         this.addSignatureAlgorithm(var1, "Mayo", "org.bouncycastle.pqc.jcajce.provider.mayo.SignatureSpi$Base", BCObjectIdentifiers.mayo);
         this.addSignatureAlgorithm(var1, "MAYO_1", "org.bouncycastle.pqc.jcajce.provider.mayo.SignatureSpi$Mayo1", BCObjectIdentifiers.mayo1);
         this.addSignatureAlgorithm(var1, "MAYO_2", "org.bouncycastle.pqc.jcajce.provider.mayo.SignatureSpi$Mayo2", BCObjectIdentifiers.mayo2);
         this.addSignatureAlgorithm(var1, "MAYO_3", "org.bouncycastle.pqc.jcajce.provider.mayo.SignatureSpi$Mayo3", BCObjectIdentifiers.mayo3);
         this.addSignatureAlgorithm(var1, "MAYO_5", "org.bouncycastle.pqc.jcajce.provider.mayo.SignatureSpi$Mayo5", BCObjectIdentifiers.mayo5);
      }
   }
}
