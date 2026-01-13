package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyFactorySpi;

public class Dilithium {
   private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.dilithium.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.DILITHIUM", "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyFactorySpi");
         this.addKeyFactoryAlgorithm(
            var1,
            "DILITHIUM2",
            "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyFactorySpi$Base2",
            BCObjectIdentifiers.dilithium2,
            new DilithiumKeyFactorySpi.Base2()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "DILITHIUM3",
            "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyFactorySpi$Base3",
            BCObjectIdentifiers.dilithium3,
            new DilithiumKeyFactorySpi.Base3()
         );
         this.addKeyFactoryAlgorithm(
            var1,
            "DILITHIUM5",
            "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyFactorySpi$Base5",
            BCObjectIdentifiers.dilithium5,
            new DilithiumKeyFactorySpi.Base5()
         );
         var1.addAlgorithm("KeyPairGenerator.DILITHIUM", "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyPairGeneratorSpi");
         this.addKeyPairGeneratorAlgorithm(
            var1, "DILITHIUM2", "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyPairGeneratorSpi$Base2", BCObjectIdentifiers.dilithium2
         );
         this.addKeyPairGeneratorAlgorithm(
            var1, "DILITHIUM3", "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyPairGeneratorSpi$Base3", BCObjectIdentifiers.dilithium3
         );
         this.addKeyPairGeneratorAlgorithm(
            var1, "DILITHIUM5", "org.bouncycastle.pqc.jcajce.provider.dilithium.DilithiumKeyPairGeneratorSpi$Base5", BCObjectIdentifiers.dilithium5
         );
         this.addSignatureAlgorithm(var1, "DILITHIUM", "org.bouncycastle.pqc.jcajce.provider.dilithium.SignatureSpi$Base", BCObjectIdentifiers.dilithium);
         this.addSignatureAlgorithm(var1, "DILITHIUM2", "org.bouncycastle.pqc.jcajce.provider.dilithium.SignatureSpi$Base2", BCObjectIdentifiers.dilithium2);
         this.addSignatureAlgorithm(var1, "DILITHIUM3", "org.bouncycastle.pqc.jcajce.provider.dilithium.SignatureSpi$Base3", BCObjectIdentifiers.dilithium3);
         this.addSignatureAlgorithm(var1, "DILITHIUM5", "org.bouncycastle.pqc.jcajce.provider.dilithium.SignatureSpi$Base5", BCObjectIdentifiers.dilithium5);
      }
   }
}
