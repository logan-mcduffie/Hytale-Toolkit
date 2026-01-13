package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.gost.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class GOST {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.gost.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyPairGenerator.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.KeyPairGeneratorSpi");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.GOST-3410", "GOST3410");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.GOST-3410-94", "GOST3410");
         var1.addAlgorithm("KeyFactory.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.KeyFactorySpi");
         var1.addAlgorithm("Alg.Alias.KeyFactory.GOST-3410", "GOST3410");
         var1.addAlgorithm("Alg.Alias.KeyFactory.GOST-3410-94", "GOST3410");
         var1.addAlgorithm("AlgorithmParameters.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.AlgorithmParametersSpi");
         var1.addAlgorithm("AlgorithmParameterGenerator.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.AlgorithmParameterGeneratorSpi");
         this.registerOid(var1, CryptoProObjectIdentifiers.gostR3410_94, "GOST3410", new KeyFactorySpi());
         this.registerOidAlgorithmParameterGenerator(var1, CryptoProObjectIdentifiers.gostR3410_94, "GOST3410");
         var1.addAlgorithm("Signature.GOST3410", "org.bouncycastle.jcajce.provider.asymmetric.gost.SignatureSpi");
         var1.addAlgorithm("Alg.Alias.Signature.GOST-3410", "GOST3410");
         var1.addAlgorithm("Alg.Alias.Signature.GOST-3410-94", "GOST3410");
         var1.addAlgorithm("Alg.Alias.Signature.GOST3411withGOST3410", "GOST3410");
         var1.addAlgorithm("Alg.Alias.Signature.GOST3411WITHGOST3410", "GOST3410");
         var1.addAlgorithm("Alg.Alias.Signature.GOST3411WithGOST3410", "GOST3410");
         var1.addAlgorithm("Alg.Alias.Signature.GOST3411/GOST3410", "GOST3410");
         var1.addAlgorithm("Alg.Alias.Signature." + CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_94, "GOST3410");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameterGenerator.GOST-3410", "GOST3410");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.GOST-3410", "GOST3410");
      }
   }
}
