package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyFactorySpi;

public class HQC {
   private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.hqc.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.HQC", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyFactorySpi");
         var1.addAlgorithm("Alg.Alias.KeyFactory.HQC", "HQC");
         this.addKeyFactoryAlgorithm(
            var1, "HQC128", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyFactorySpi$HQC128", BCObjectIdentifiers.hqc128, new HQCKeyFactorySpi.HQC128()
         );
         this.addKeyFactoryAlgorithm(
            var1, "HQC192", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyFactorySpi$HQC192", BCObjectIdentifiers.hqc192, new HQCKeyFactorySpi.HQC192()
         );
         this.addKeyFactoryAlgorithm(
            var1, "HQC256", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyFactorySpi$HQC256", BCObjectIdentifiers.hqc256, new HQCKeyFactorySpi.HQC256()
         );
         var1.addAlgorithm("KeyPairGenerator.HQC", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyPairGeneratorSpi");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.HQC", "HQC");
         this.addKeyPairGeneratorAlgorithm(var1, "HQC128", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyPairGeneratorSpi$HQC128", BCObjectIdentifiers.hqc128);
         this.addKeyPairGeneratorAlgorithm(var1, "HQC192", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyPairGeneratorSpi$HQC192", BCObjectIdentifiers.hqc192);
         this.addKeyPairGeneratorAlgorithm(var1, "HQC256", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyPairGeneratorSpi$HQC256", BCObjectIdentifiers.hqc256);
         var1.addAlgorithm("KeyGenerator.HQC", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyGeneratorSpi");
         this.addKeyGeneratorAlgorithm(var1, "HQC128", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyGeneratorSpi$HQC128", BCObjectIdentifiers.hqc128);
         this.addKeyGeneratorAlgorithm(var1, "HQC192", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyGeneratorSpi$HQC192", BCObjectIdentifiers.hqc192);
         this.addKeyGeneratorAlgorithm(var1, "HQC256", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCKeyGeneratorSpi$HQC256", BCObjectIdentifiers.hqc256);
         HQCKeyFactorySpi var2 = new HQCKeyFactorySpi();
         var1.addAlgorithm("Cipher.HQC", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCCipherSpi$Base");
         var1.addAlgorithm("Alg.Alias.Cipher.HQC", "HQC");
         var1.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_hqc, "HQC");
         this.addCipherAlgorithm(var1, "HQC128", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCCipherSpi$HQC128", BCObjectIdentifiers.hqc128);
         this.addCipherAlgorithm(var1, "HQC192", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCCipherSpi$HQC192", BCObjectIdentifiers.hqc192);
         this.addCipherAlgorithm(var1, "HQC256", "org.bouncycastle.pqc.jcajce.provider.hqc.HQCCipherSpi$HQC256", BCObjectIdentifiers.hqc256);
         this.registerOid(var1, BCObjectIdentifiers.pqc_kem_hqc, "HQC", var2);
         var1.addKeyInfoConverter(BCObjectIdentifiers.hqc128, var2);
         var1.addKeyInfoConverter(BCObjectIdentifiers.hqc192, var2);
         var1.addKeyInfoConverter(BCObjectIdentifiers.hqc256, var2);
      }
   }
}
