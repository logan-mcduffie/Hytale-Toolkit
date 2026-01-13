package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.jcajce.provider.saber.SABERKeyFactorySpi;

public class SABER {
   private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.saber.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.SABER", "org.bouncycastle.pqc.jcajce.provider.saber.SABERKeyFactorySpi");
         var1.addAlgorithm("KeyPairGenerator.SABER", "org.bouncycastle.pqc.jcajce.provider.saber.SABERKeyPairGeneratorSpi");
         var1.addAlgorithm("KeyGenerator.SABER", "org.bouncycastle.pqc.jcajce.provider.saber.SABERKeyGeneratorSpi");
         SABERKeyFactorySpi var2 = new SABERKeyFactorySpi();
         var1.addAlgorithm("Cipher.SABER", "org.bouncycastle.pqc.jcajce.provider.saber.SABERCipherSpi$Base");
         var1.addAlgorithm("Alg.Alias.Cipher." + BCObjectIdentifiers.pqc_kem_saber, "SABER");
         this.registerOid(var1, BCObjectIdentifiers.pqc_kem_saber, "SABER", var2);
         this.registerOidAlgorithmParameters(var1, BCObjectIdentifiers.pqc_kem_saber, "SABER");
      }
   }
}
