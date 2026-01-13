package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyFactorySpi;

public class SPHINCS {
   private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.sphincs.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.SPHINCS256", "org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyFactorySpi");
         var1.addAlgorithm("KeyPairGenerator.SPHINCS256", "org.bouncycastle.pqc.jcajce.provider.sphincs.Sphincs256KeyPairGeneratorSpi");
         this.addSignatureAlgorithm(
            var1, "SHA512", "SPHINCS256", "org.bouncycastle.pqc.jcajce.provider.sphincs.SignatureSpi$withSha512", PQCObjectIdentifiers.sphincs256_with_SHA512
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA3-512",
            "SPHINCS256",
            "org.bouncycastle.pqc.jcajce.provider.sphincs.SignatureSpi$withSha3_512",
            PQCObjectIdentifiers.sphincs256_with_SHA3_512
         );
         Sphincs256KeyFactorySpi var2 = new Sphincs256KeyFactorySpi();
         this.registerOid(var1, PQCObjectIdentifiers.sphincs256, "SPHINCS256", var2);
         this.registerOidAlgorithmParameters(var1, PQCObjectIdentifiers.sphincs256, "SPHINCS256");
      }
   }
}
