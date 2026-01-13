package org.bouncycastle.pqc.jcajce.provider;

import org.bouncycastle.asn1.bc.BCObjectIdentifiers;
import org.bouncycastle.internal.asn1.isara.IsaraObjectIdentifiers;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;
import org.bouncycastle.pqc.asn1.PQCObjectIdentifiers;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyFactorySpi;
import org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyFactorySpi;

public class XMSS {
   private static final String PREFIX = "org.bouncycastle.pqc.jcajce.provider.xmss.";

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("KeyFactory.XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyFactorySpi");
         var1.addAlgorithm("KeyPairGenerator.XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSKeyPairGeneratorSpi");
         var1.addAlgorithm("Signature.XMSS", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$generic");
         var1.addAlgorithm("Alg.Alias.Signature." + IsaraObjectIdentifiers.id_alg_xmss, "XMSS");
         var1.addAlgorithm("Alg.Alias.Signature.OID." + IsaraObjectIdentifiers.id_alg_xmss, "XMSS");
         this.addSignatureAlgorithm(
            var1, "XMSS-SHA256", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withSha256", BCObjectIdentifiers.xmss_SHA256
         );
         this.addSignatureAlgorithm(
            var1, "XMSS-SHAKE128", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withShake128", BCObjectIdentifiers.xmss_SHAKE128
         );
         this.addSignatureAlgorithm(
            var1, "XMSS-SHA512", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withSha512", BCObjectIdentifiers.xmss_SHA512
         );
         this.addSignatureAlgorithm(
            var1, "XMSS-SHAKE256", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withShake256", BCObjectIdentifiers.xmss_SHAKE256
         );
         this.addSignatureAlgorithm(
            var1, "SHA256", "XMSS-SHA256", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withSha256andPrehash", BCObjectIdentifiers.xmss_SHA256ph
         );
         this.addSignatureAlgorithm(
            var1,
            "SHAKE128",
            "XMSS-SHAKE128",
            "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withShake128andPrehash",
            BCObjectIdentifiers.xmss_SHAKE128ph
         );
         this.addSignatureAlgorithm(
            var1,
            "SHAKE128(512)",
            "XMSS-SHAKE128",
            "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withShake128_512andPrehash",
            BCObjectIdentifiers.xmss_SHAKE128_512ph
         );
         this.addSignatureAlgorithm(
            var1, "SHA512", "XMSS-SHA512", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withSha512andPrehash", BCObjectIdentifiers.xmss_SHA512ph
         );
         this.addSignatureAlgorithm(
            var1,
            "SHAKE256",
            "XMSS-SHAKE256",
            "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withShake256andPrehash",
            BCObjectIdentifiers.xmss_SHAKE256ph
         );
         this.addSignatureAlgorithm(
            var1,
            "SHAKE256(1024)",
            "XMSS-SHAKE256",
            "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSSignatureSpi$withShake256_1024andPrehash",
            BCObjectIdentifiers.xmss_SHAKE256_1024ph
         );
         var1.addAlgorithm("Alg.Alias.Signature.SHA256WITHXMSS", "SHA256WITHXMSS-SHA256");
         var1.addAlgorithm("Alg.Alias.Signature.SHAKE128WITHXMSS", "SHAKE128WITHXMSS-SHAKE128");
         var1.addAlgorithm("Alg.Alias.Signature.SHAKE128(512)WITHXMSS", "SHAKE128(512)WITHXMSS-SHAKE128");
         var1.addAlgorithm("Alg.Alias.Signature.SHA512WITHXMSS", "SHA512WITHXMSS-SHA512");
         var1.addAlgorithm("Alg.Alias.Signature.SHAKE256WITHXMSS", "SHAKE256WITHXMSS-SHAKE256");
         var1.addAlgorithm("Alg.Alias.Signature.SHAKE256(1024)WITHXMSS", "SHAKE256(1024)WITHXMSS-SHAKE256");
         var1.addAlgorithm("KeyFactory.XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyFactorySpi");
         var1.addAlgorithm("KeyPairGenerator.XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTKeyPairGeneratorSpi");
         var1.addAlgorithm("Signature.XMSSMT", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$generic");
         var1.addAlgorithm("Alg.Alias.Signature." + IsaraObjectIdentifiers.id_alg_xmssmt, "XMSSMT");
         var1.addAlgorithm("Alg.Alias.Signature.OID." + IsaraObjectIdentifiers.id_alg_xmssmt, "XMSSMT");
         this.addSignatureAlgorithm(
            var1, "XMSSMT-SHA256", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withSha256", BCObjectIdentifiers.xmss_mt_SHA256
         );
         this.addSignatureAlgorithm(
            var1, "XMSSMT-SHAKE128", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withShake128", BCObjectIdentifiers.xmss_mt_SHAKE128
         );
         this.addSignatureAlgorithm(
            var1, "XMSSMT-SHA512", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withSha512", BCObjectIdentifiers.xmss_mt_SHA512
         );
         this.addSignatureAlgorithm(
            var1, "XMSSMT-SHAKE256", "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withShake256", BCObjectIdentifiers.xmss_mt_SHAKE256
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA256",
            "XMSSMT-SHA256",
            "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withSha256andPrehash",
            BCObjectIdentifiers.xmss_mt_SHA256ph
         );
         this.addSignatureAlgorithm(
            var1,
            "SHAKE128",
            "XMSSMT-SHAKE128",
            "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withShake128andPrehash",
            BCObjectIdentifiers.xmss_mt_SHAKE128ph
         );
         this.addSignatureAlgorithm(
            var1,
            "SHAKE128(512)",
            "XMSSMT-SHAKE128",
            "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withShake128_512andPrehash",
            BCObjectIdentifiers.xmss_mt_SHAKE128_512ph
         );
         this.addSignatureAlgorithm(
            var1,
            "SHA512",
            "XMSSMT-SHA512",
            "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withSha512andPrehash",
            BCObjectIdentifiers.xmss_mt_SHA512ph
         );
         this.addSignatureAlgorithm(
            var1,
            "SHAKE256",
            "XMSSMT-SHAKE256",
            "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withShake256andPrehash",
            BCObjectIdentifiers.xmss_mt_SHAKE256ph
         );
         this.addSignatureAlgorithm(
            var1,
            "SHAKE256(1024)",
            "XMSSMT-SHAKE256",
            "org.bouncycastle.pqc.jcajce.provider.xmss.XMSSMTSignatureSpi$withShake256_1024andPrehash",
            BCObjectIdentifiers.xmss_mt_SHAKE256_1024ph
         );
         var1.addAlgorithm("Alg.Alias.Signature.SHA256WITHXMSSMT", "SHA256WITHXMSSMT-SHA256");
         var1.addAlgorithm("Alg.Alias.Signature.SHAKE128WITHXMSSMT", "SHAKE128WITHXMSSMT-SHAKE128");
         var1.addAlgorithm("Alg.Alias.Signature.SHAKE128(512)WITHXMSSMT", "SHAKE128(512)WITHXMSSMT-SHAKE128");
         var1.addAlgorithm("Alg.Alias.Signature.SHA512WITHXMSSMT", "SHA512WITHXMSSMT-SHA512");
         var1.addAlgorithm("Alg.Alias.Signature.SHAKE256WITHXMSSMT", "SHAKE256WITHXMSSMT-SHAKE256");
         var1.addAlgorithm("Alg.Alias.Signature.SHAKE256(1024)WITHXMSSMT", "SHAKE256(1024)WITHXMSSMT-SHAKE256");
         this.registerOid(var1, PQCObjectIdentifiers.xmss, "XMSS", new XMSSKeyFactorySpi());
         this.registerOid(var1, IsaraObjectIdentifiers.id_alg_xmss, "XMSS", new XMSSKeyFactorySpi());
         this.registerOid(var1, PQCObjectIdentifiers.xmss_mt, "XMSSMT", new XMSSMTKeyFactorySpi());
         this.registerOid(var1, IsaraObjectIdentifiers.id_alg_xmssmt, "XMSSMT", new XMSSMTKeyFactorySpi());
      }
   }
}
