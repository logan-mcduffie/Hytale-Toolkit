package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.ua.UAObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.dstu.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class DSTU4145 {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.dstu.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.DSTU4145", "org.bouncycastle.jcajce.provider.asymmetric.dstu.KeyFactorySpi");
         var1.addAlgorithm("Alg.Alias.KeyFactory.DSTU-4145-2002", "DSTU4145");
         var1.addAlgorithm("Alg.Alias.KeyFactory.DSTU4145-3410", "DSTU4145");
         this.registerOid(var1, UAObjectIdentifiers.dstu4145le, "DSTU4145", new KeyFactorySpi());
         this.registerOidAlgorithmParameters(var1, UAObjectIdentifiers.dstu4145le, "DSTU4145");
         this.registerOid(var1, UAObjectIdentifiers.dstu4145be, "DSTU4145", new KeyFactorySpi());
         this.registerOidAlgorithmParameters(var1, UAObjectIdentifiers.dstu4145be, "DSTU4145");
         var1.addAlgorithm("KeyPairGenerator.DSTU4145", "org.bouncycastle.jcajce.provider.asymmetric.dstu.KeyPairGeneratorSpi");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.DSTU-4145", "DSTU4145");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.DSTU-4145-2002", "DSTU4145");
         var1.addAlgorithm("Signature.DSTU4145", "org.bouncycastle.jcajce.provider.asymmetric.dstu.SignatureSpi");
         var1.addAlgorithm("Alg.Alias.Signature.DSTU-4145", "DSTU4145");
         var1.addAlgorithm("Alg.Alias.Signature.DSTU-4145-2002", "DSTU4145");
         this.addSignatureAlgorithm(
            var1, "GOST3411", "DSTU4145LE", "org.bouncycastle.jcajce.provider.asymmetric.dstu.SignatureSpiLe", UAObjectIdentifiers.dstu4145le
         );
         this.addSignatureAlgorithm(
            var1, "GOST3411", "DSTU4145", "org.bouncycastle.jcajce.provider.asymmetric.dstu.SignatureSpi", UAObjectIdentifiers.dstu4145be
         );
      }
   }
}
