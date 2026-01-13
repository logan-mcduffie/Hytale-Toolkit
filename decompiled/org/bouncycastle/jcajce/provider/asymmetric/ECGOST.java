package org.bouncycastle.jcajce.provider.asymmetric;

import org.bouncycastle.asn1.cryptopro.CryptoProObjectIdentifiers;
import org.bouncycastle.internal.asn1.rosstandart.RosstandartObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.ecgost.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class ECGOST {
   private static final String PREFIX_COMMON = "org.bouncycastle.jcajce.provider.asymmetric.ec.";
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.ecgost.";
   private static final String PREFIX_GOST_2012 = "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.KeyFactorySpi");
         var1.addAlgorithm("Alg.Alias.KeyFactory.GOST-3410-2001", "ECGOST3410");
         var1.addAlgorithm("Alg.Alias.KeyFactory.ECGOST-3410", "ECGOST3410");
         this.registerOid(var1, CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410", new KeyFactorySpi());
         this.registerOid(var1, CryptoProObjectIdentifiers.gostR3410_2001DH, "ECGOST3410", new KeyFactorySpi());
         this.registerOidAlgorithmParameters(var1, CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
         var1.addAlgorithm("KeyPairGenerator.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.KeyPairGeneratorSpi");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.ECGOST-3410", "ECGOST3410");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.GOST-3410-2001", "ECGOST3410");
         var1.addAlgorithm("Signature.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.SignatureSpi");
         var1.addAlgorithm("Alg.Alias.Signature.ECGOST-3410", "ECGOST3410");
         var1.addAlgorithm("Alg.Alias.Signature.GOST-3410-2001", "ECGOST3410");
         var1.addAlgorithm("KeyAgreement.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ecgost.KeyAgreementSpi$ECVKO");
         var1.addAlgorithm("Alg.Alias.KeyAgreement." + CryptoProObjectIdentifiers.gostR3410_2001, "ECGOST3410");
         var1.addAlgorithm("Alg.Alias.KeyAgreement.GOST-3410-2001", "ECGOST3410");
         var1.addAlgorithm("Alg.Alias.KeyAgreement." + CryptoProObjectIdentifiers.gostR3410_2001_CryptoPro_ESDH, "ECGOST3410");
         var1.addAlgorithm("AlgorithmParameters.ECGOST3410", "org.bouncycastle.jcajce.provider.asymmetric.ec.AlgorithmParametersSpi");
         var1.addAlgorithm("AlgorithmParameters.ECGOST3410-2012", "org.bouncycastle.jcajce.provider.asymmetric.ec.AlgorithmParametersSpi");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.GOST-3410-2001", "ECGOST3410");
         this.addSignatureAlgorithm(
            var1,
            "GOST3411",
            "ECGOST3410",
            "org.bouncycastle.jcajce.provider.asymmetric.ecgost.SignatureSpi",
            CryptoProObjectIdentifiers.gostR3411_94_with_gostR3410_2001
         );
         var1.addAlgorithm("KeyFactory.ECGOST3410-2012", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi");
         var1.addAlgorithm("Alg.Alias.KeyFactory.GOST-3410-2012", "ECGOST3410-2012");
         var1.addAlgorithm("Alg.Alias.KeyFactory.ECGOST-3410-2012", "ECGOST3410-2012");
         this.registerOid(
            var1,
            RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256,
            "ECGOST3410-2012",
            new org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi()
         );
         this.registerOid(
            var1,
            RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256,
            "ECGOST3410-2012",
            new org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi()
         );
         this.registerOidAlgorithmParameters(var1, RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012");
         this.registerOid(
            var1,
            RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512,
            "ECGOST3410-2012",
            new org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi()
         );
         this.registerOid(
            var1,
            RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512,
            "ECGOST3410-2012",
            new org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyFactorySpi()
         );
         this.registerOidAlgorithmParameters(var1, RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012");
         var1.addAlgorithm("KeyPairGenerator.ECGOST3410-2012", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyPairGeneratorSpi");
         var1.addAlgorithm("Alg.Alias.KeyPairGenerator.GOST-3410-2012", "ECGOST3410-2012");
         var1.addAlgorithm("Signature.ECGOST3410-2012-256", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi256");
         var1.addAlgorithm("Alg.Alias.Signature.GOST-3410-2012-256", "ECGOST3410-2012-256");
         var1.addAlgorithm("Alg.Alias.Signature.GOST3411WITHECGOST3410-2012-256", "ECGOST3410-2012-256");
         this.addSignatureAlgorithm(
            var1,
            "GOST3411-2012-256",
            "ECGOST3410-2012-256",
            "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi256",
            RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_256
         );
         var1.addAlgorithm("Signature.ECGOST3410-2012-512", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi512");
         var1.addAlgorithm("Alg.Alias.Signature.ECGOST3410-2012-512", "ECGOST3410-2012-512");
         var1.addAlgorithm("Alg.Alias.Signature.GOST-3410-2012-512", "ECGOST3410-2012-512");
         var1.addAlgorithm("Alg.Alias.Signature.GOST3411WITHECGOST3410-2012-512", "ECGOST3410-2012-512");
         this.addSignatureAlgorithm(
            var1,
            "GOST3411-2012-512",
            "ECGOST3410-2012-512",
            "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.ECGOST2012SignatureSpi512",
            RosstandartObjectIdentifiers.id_tc26_signwithdigest_gost_3410_12_512
         );
         var1.addAlgorithm("KeyAgreement.ECGOST3410-2012-256", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyAgreementSpi$ECVKO256");
         var1.addAlgorithm("KeyAgreement.ECGOST3410-2012-512", "org.bouncycastle.jcajce.provider.asymmetric.ecgost12.KeyAgreementSpi$ECVKO512");
         var1.addAlgorithm("Alg.Alias.KeyAgreement." + RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_256, "ECGOST3410-2012-256");
         var1.addAlgorithm("Alg.Alias.KeyAgreement." + RosstandartObjectIdentifiers.id_tc26_agreement_gost_3410_12_512, "ECGOST3410-2012-512");
         var1.addAlgorithm("Alg.Alias.KeyAgreement." + RosstandartObjectIdentifiers.id_tc26_gost_3410_12_256, "ECGOST3410-2012-256");
         var1.addAlgorithm("Alg.Alias.KeyAgreement." + RosstandartObjectIdentifiers.id_tc26_gost_3410_12_512, "ECGOST3410-2012-512");
      }
   }
}
