package org.bouncycastle.jcajce.provider.asymmetric;

import java.util.HashMap;
import java.util.Map;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.nist.NISTObjectIdentifiers;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.teletrust.TeleTrusTObjectIdentifiers;
import org.bouncycastle.asn1.x509.X509ObjectIdentifiers;
import org.bouncycastle.internal.asn1.cms.CMSObjectIdentifiers;
import org.bouncycastle.internal.asn1.oiw.OIWObjectIdentifiers;
import org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyFactorySpi;
import org.bouncycastle.jcajce.provider.config.ConfigurableProvider;
import org.bouncycastle.jcajce.provider.util.AsymmetricAlgorithmProvider;

public class RSA {
   private static final String PREFIX = "org.bouncycastle.jcajce.provider.asymmetric.rsa.";
   private static final Map<String, String> generalRsaAttributes = new HashMap<>();

   static {
      generalRsaAttributes.put("SupportedKeyClasses", "java.security.interfaces.RSAPublicKey|java.security.interfaces.RSAPrivateKey");
      generalRsaAttributes.put("SupportedKeyFormats", "PKCS#8|X.509");
   }

   public static class Mappings extends AsymmetricAlgorithmProvider {
      @Override
      public void configure(ConfigurableProvider var1) {
         var1.addAlgorithm("AlgorithmParameters.OAEP", "org.bouncycastle.jcajce.provider.asymmetric.rsa.AlgorithmParametersSpi$OAEP");
         var1.addAlgorithm("AlgorithmParameters.PSS", "org.bouncycastle.jcajce.provider.asymmetric.rsa.AlgorithmParametersSpi$PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.RSAPSS", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.RSASSA-PSS", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA224withRSA/PSS", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA256withRSA/PSS", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA384withRSA/PSS", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA512withRSA/PSS", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA224WITHRSAANDMGF1", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA256WITHRSAANDMGF1", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA384WITHRSAANDMGF1", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA512WITHRSAANDMGF1", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-224WITHRSAANDMGF1", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-256WITHRSAANDMGF1", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-384WITHRSAANDMGF1", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA3-512WITHRSAANDMGF1", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.RAWRSAPSS", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.NONEWITHRSAPSS", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.NONEWITHRSASSA-PSS", "PSS");
         var1.addAlgorithm("Alg.Alias.AlgorithmParameters.NONEWITHRSAANDMGF1", "PSS");
         var1.addAttributes("Cipher.RSA", RSA.generalRsaAttributes);
         var1.addAlgorithm("Cipher.RSA", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$NoPadding");
         var1.addAlgorithm("Cipher.RSA/RAW", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$NoPadding");
         var1.addAlgorithm("Cipher.RSA/PKCS1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding");
         var1.addAlgorithm(
            "Cipher",
            PKCSObjectIdentifiers.rsaEncryption,
            "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding",
            RSA.generalRsaAttributes
         );
         var1.addAlgorithm(
            "Cipher", X509ObjectIdentifiers.id_ea_rsa, "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding", RSA.generalRsaAttributes
         );
         var1.addAlgorithm("Cipher.RSA/1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding_PrivateOnly");
         var1.addAlgorithm("Cipher.RSA/2", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$PKCS1v1_5Padding_PublicOnly");
         var1.addAlgorithm("Cipher.RSA/OAEP", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$OAEPPadding", RSA.generalRsaAttributes);
         var1.addAlgorithm(
            "Cipher", PKCSObjectIdentifiers.id_RSAES_OAEP, "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$OAEPPadding", RSA.generalRsaAttributes
         );
         var1.addAlgorithm("Cipher.RSA/ISO9796-1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.CipherSpi$ISO9796d1Padding", RSA.generalRsaAttributes);
         var1.addAlgorithm("Alg.Alias.Cipher.RSA//RAW", "RSA");
         var1.addAlgorithm("Alg.Alias.Cipher.RSA//NOPADDING", "RSA");
         var1.addAlgorithm("Alg.Alias.Cipher.RSA//PKCS1PADDING", "RSA/PKCS1");
         var1.addAlgorithm("Alg.Alias.Cipher.RSA//OAEPPADDING", "RSA/OAEP");
         var1.addAlgorithm("Alg.Alias.Cipher.RSA//ISO9796-1PADDING", "RSA/ISO9796-1");
         var1.addAlgorithm("KeyFactory.RSA", "org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyFactorySpi");
         var1.addAlgorithm("KeyPairGenerator.RSA", "org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyPairGeneratorSpi");
         var1.addAlgorithm("KeyFactory.RSASSA-PSS", "org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyFactorySpi");
         var1.addAlgorithm("KeyPairGenerator.RSASSA-PSS", "org.bouncycastle.jcajce.provider.asymmetric.rsa.KeyPairGeneratorSpi$PSS");
         KeyFactorySpi var2 = new KeyFactorySpi();
         this.registerOid(var1, PKCSObjectIdentifiers.rsaEncryption, "RSA", var2);
         this.registerOid(var1, X509ObjectIdentifiers.id_ea_rsa, "RSA", var2);
         this.registerOid(var1, PKCSObjectIdentifiers.id_RSAES_OAEP, "RSA", var2);
         this.registerOid(var1, PKCSObjectIdentifiers.id_RSASSA_PSS, "RSA", var2);
         this.registerOidAlgorithmParameters(var1, PKCSObjectIdentifiers.rsaEncryption, "RSA");
         this.registerOidAlgorithmParameters(var1, X509ObjectIdentifiers.id_ea_rsa, "RSA");
         this.registerOidAlgorithmParameters(var1, PKCSObjectIdentifiers.id_RSAES_OAEP, "OAEP");
         this.registerOidAlgorithmParameters(var1, PKCSObjectIdentifiers.id_RSASSA_PSS, "PSS");
         var1.addAlgorithm("Signature.RSASSA-PSS", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$PSSwithRSA", RSA.generalRsaAttributes);
         var1.addAlgorithm("Alg.Alias.Signature." + PKCSObjectIdentifiers.id_RSASSA_PSS, "RSASSA-PSS");
         var1.addAlgorithm("Alg.Alias.Signature.OID." + PKCSObjectIdentifiers.id_RSASSA_PSS, "RSASSA-PSS");
         var1.addAlgorithm("Signature.RSA", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$noneRSA", RSA.generalRsaAttributes);
         var1.addAlgorithm("Signature.RAWRSASSA-PSS", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$nonePSS", RSA.generalRsaAttributes);
         var1.addAlgorithm("Alg.Alias.Signature.RAWRSA", "RSA");
         var1.addAlgorithm("Alg.Alias.Signature.NONEWITHRSA", "RSA");
         var1.addAlgorithm("Alg.Alias.Signature.RAWRSAPSS", "RAWRSASSA-PSS");
         var1.addAlgorithm("Alg.Alias.Signature.NONEWITHRSAPSS", "RAWRSASSA-PSS");
         var1.addAlgorithm("Alg.Alias.Signature.NONEWITHRSASSA-PSS", "RAWRSASSA-PSS");
         var1.addAlgorithm("Alg.Alias.Signature.NONEWITHRSAANDMGF1", "RAWRSASSA-PSS");
         var1.addAlgorithm("Alg.Alias.Signature.RSAPSS", "RSASSA-PSS");
         this.addPSSSignature(var1, "SHA224", "MGF1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA224withRSA");
         this.addPSSSignature(var1, "SHA256", "MGF1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA256withRSA");
         this.addPSSSignature(var1, "SHA384", "MGF1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA384withRSA");
         this.addPSSSignature(var1, "SHA512", "MGF1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512withRSA");
         this.addPSSSignature(var1, "SHA512(224)", "MGF1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512_224withRSA");
         this.addPSSSignature(var1, "SHA512(256)", "MGF1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512_256withRSA");
         this.addPSSSignature(var1, "SHA3-224", "MGF1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_224withRSA");
         this.addPSSSignature(var1, "SHA3-256", "MGF1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_256withRSA");
         this.addPSSSignature(var1, "SHA3-384", "MGF1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_384withRSA");
         this.addPSSSignature(var1, "SHA3-512", "MGF1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_512withRSA");
         this.addPSSSignature(
            var1, "SHAKE128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHAKE128WithRSAPSS", CMSObjectIdentifiers.id_RSASSA_PSS_SHAKE128
         );
         this.addPSSSignature(
            var1, "SHAKE256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHAKE256WithRSAPSS", CMSObjectIdentifiers.id_RSASSA_PSS_SHAKE256
         );
         this.addPSSSignature(var1, "SHA224", "SHAKE128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA224withRSAandSHAKE128");
         this.addPSSSignature(var1, "SHA256", "SHAKE128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA256withRSAandSHAKE128");
         this.addPSSSignature(var1, "SHA384", "SHAKE128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA384withRSAandSHAKE128");
         this.addPSSSignature(var1, "SHA512", "SHAKE128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512withRSAandSHAKE128");
         this.addPSSSignature(var1, "SHA512(224)", "SHAKE128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512_224withRSAandSHAKE128");
         this.addPSSSignature(var1, "SHA512(256)", "SHAKE128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512_256withRSAandSHAKE128");
         this.addPSSSignature(var1, "SHA224", "SHAKE256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA224withRSAandSHAKE256");
         this.addPSSSignature(var1, "SHA256", "SHAKE256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA256withRSAandSHAKE256");
         this.addPSSSignature(var1, "SHA384", "SHAKE256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA384withRSAandSHAKE256");
         this.addPSSSignature(var1, "SHA512", "SHAKE256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512withRSAandSHAKE256");
         this.addPSSSignature(var1, "SHA512(224)", "SHAKE256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512_224withRSAandSHAKE256");
         this.addPSSSignature(var1, "SHA512(256)", "SHAKE256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA512_256withRSAandSHAKE256");
         this.addPSSSignature(var1, "SHA3-224", "SHAKE128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_224withRSAandSHAKE128");
         this.addPSSSignature(var1, "SHA3-256", "SHAKE128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_256withRSAandSHAKE128");
         this.addPSSSignature(var1, "SHA3-384", "SHAKE128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_384withRSAandSHAKE128");
         this.addPSSSignature(var1, "SHA3-512", "SHAKE128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_512withRSAandSHAKE128");
         this.addPSSSignature(var1, "SHA3-224", "SHAKE256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_224withRSAandSHAKE256");
         this.addPSSSignature(var1, "SHA3-256", "SHAKE256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_256withRSAandSHAKE256");
         this.addPSSSignature(var1, "SHA3-384", "SHAKE256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_384withRSAandSHAKE256");
         this.addPSSSignature(var1, "SHA3-512", "SHAKE256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA3_512withRSAandSHAKE256");
         if (var1.hasAlgorithm("MessageDigest", "MD2")) {
            this.addDigestSignature(
               var1, "MD2", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$MD2", PKCSObjectIdentifiers.md2WithRSAEncryption
            );
         }

         if (var1.hasAlgorithm("MessageDigest", "MD4")) {
            this.addDigestSignature(
               var1, "MD4", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$MD4", PKCSObjectIdentifiers.md4WithRSAEncryption
            );
         }

         if (var1.hasAlgorithm("MessageDigest", "MD5")) {
            this.addDigestSignature(
               var1, "MD5", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$MD5", PKCSObjectIdentifiers.md5WithRSAEncryption
            );
            this.addISO9796Signature(var1, "MD5", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$MD5WithRSAEncryption");
         }

         if (var1.hasAlgorithm("MessageDigest", "SHA1")) {
            var1.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA1withRSA/PSS", "PSS");
            var1.addAlgorithm("Alg.Alias.AlgorithmParameters.SHA1WITHRSAANDMGF1", "PSS");
            this.addPSSSignature(var1, "SHA1", "MGF1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA1withRSA");
            this.addPSSSignature(var1, "SHA1", "SHAKE128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA1withRSAandSHAKE128");
            this.addPSSSignature(var1, "SHA1", "SHAKE256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.PSSSignatureSpi$SHA1withRSAandSHAKE256");
            this.addDigestSignature(
               var1, "SHA1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA1", PKCSObjectIdentifiers.sha1WithRSAEncryption
            );
            this.addISO9796Signature(var1, "SHA1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA1WithRSAEncryption");
            var1.addAlgorithm("Alg.Alias.Signature." + OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
            var1.addAlgorithm("Alg.Alias.Signature.OID." + OIWObjectIdentifiers.sha1WithRSA, "SHA1WITHRSA");
            this.addX931Signature(var1, "SHA1", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA1WithRSAEncryption");
         }

         this.addDigestSignature(
            var1, "SHA224", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA224", PKCSObjectIdentifiers.sha224WithRSAEncryption
         );
         this.addDigestSignature(
            var1, "SHA256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA256", PKCSObjectIdentifiers.sha256WithRSAEncryption
         );
         this.addDigestSignature(
            var1, "SHA384", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA384", PKCSObjectIdentifiers.sha384WithRSAEncryption
         );
         this.addDigestSignature(
            var1, "SHA512", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA512", PKCSObjectIdentifiers.sha512WithRSAEncryption
         );
         this.addDigestSignature(
            var1,
            "SHA512(224)",
            "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA512_224",
            PKCSObjectIdentifiers.sha512_224WithRSAEncryption
         );
         this.addDigestSignature(
            var1,
            "SHA512(256)",
            "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA512_256",
            PKCSObjectIdentifiers.sha512_256WithRSAEncryption
         );
         this.addDigestSignature(
            var1,
            "SHA3-224",
            "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_224",
            NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_224
         );
         this.addDigestSignature(
            var1,
            "SHA3-256",
            "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_256",
            NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_256
         );
         this.addDigestSignature(
            var1,
            "SHA3-384",
            "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_384",
            NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_384
         );
         this.addDigestSignature(
            var1,
            "SHA3-512",
            "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$SHA3_512",
            NISTObjectIdentifiers.id_rsassa_pkcs1_v1_5_with_sha3_512
         );
         this.addISO9796Signature(var1, "SHA224", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA224WithRSAEncryption");
         this.addISO9796Signature(var1, "SHA256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA256WithRSAEncryption");
         this.addISO9796Signature(var1, "SHA384", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA384WithRSAEncryption");
         this.addISO9796Signature(var1, "SHA512", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA512WithRSAEncryption");
         this.addISO9796Signature(var1, "SHA512(224)", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA512_224WithRSAEncryption");
         this.addISO9796Signature(var1, "SHA512(256)", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$SHA512_256WithRSAEncryption");
         this.addX931Signature(var1, "SHA224", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA224WithRSAEncryption");
         this.addX931Signature(var1, "SHA256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA256WithRSAEncryption");
         this.addX931Signature(var1, "SHA384", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA384WithRSAEncryption");
         this.addX931Signature(var1, "SHA512", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA512WithRSAEncryption");
         this.addX931Signature(var1, "SHA512(224)", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA512_224WithRSAEncryption");
         this.addX931Signature(var1, "SHA512(256)", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$SHA512_256WithRSAEncryption");
         if (var1.hasAlgorithm("MessageDigest", "RIPEMD128")) {
            this.addDigestSignature(
               var1,
               "RIPEMD128",
               "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD128",
               TeleTrusTObjectIdentifiers.rsaSignatureWithripemd128
            );
            this.addDigestSignature(var1, "RMD128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD128", null);
            this.addX931Signature(var1, "RMD128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD128WithRSAEncryption");
            this.addX931Signature(var1, "RIPEMD128", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD128WithRSAEncryption");
         }

         if (var1.hasAlgorithm("MessageDigest", "RIPEMD160")) {
            this.addDigestSignature(
               var1,
               "RIPEMD160",
               "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD160",
               TeleTrusTObjectIdentifiers.rsaSignatureWithripemd160
            );
            this.addDigestSignature(var1, "RMD160", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD160", null);
            var1.addAlgorithm("Alg.Alias.Signature.RIPEMD160WithRSA/ISO9796-2", "RIPEMD160withRSA/ISO9796-2");
            var1.addAlgorithm(
               "Signature.RIPEMD160withRSA/ISO9796-2", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$RIPEMD160WithRSAEncryption"
            );
            this.addX931Signature(var1, "RMD160", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD160WithRSAEncryption");
            this.addX931Signature(var1, "RIPEMD160", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$RIPEMD160WithRSAEncryption");
         }

         if (var1.hasAlgorithm("MessageDigest", "RIPEMD256")) {
            this.addDigestSignature(
               var1,
               "RIPEMD256",
               "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD256",
               TeleTrusTObjectIdentifiers.rsaSignatureWithripemd256
            );
            this.addDigestSignature(var1, "RMD256", "org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi$RIPEMD256", null);
         }

         if (var1.hasAlgorithm("MessageDigest", "WHIRLPOOL")) {
            this.addISO9796Signature(var1, "Whirlpool", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$WhirlpoolWithRSAEncryption");
            this.addISO9796Signature(var1, "WHIRLPOOL", "org.bouncycastle.jcajce.provider.asymmetric.rsa.ISOSignatureSpi$WhirlpoolWithRSAEncryption");
            this.addX931Signature(var1, "Whirlpool", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$WhirlpoolWithRSAEncryption");
            this.addX931Signature(var1, "WHIRLPOOL", "org.bouncycastle.jcajce.provider.asymmetric.rsa.X931SignatureSpi$WhirlpoolWithRSAEncryption");
         }
      }

      private void addDigestSignature(ConfigurableProvider var1, String var2, String var3, ASN1ObjectIdentifier var4) {
         String var5 = var2 + "WITHRSA";
         String var6 = var2 + "withRSA";
         String var7 = var2 + "WithRSA";
         String var8 = var2 + "/RSA";
         String var9 = var2 + "WITHRSAENCRYPTION";
         String var10 = var2 + "withRSAEncryption";
         String var11 = var2 + "WithRSAEncryption";
         var1.addAlgorithm("Signature." + var5, var3);
         var1.addAlgorithm("Alg.Alias.Signature." + var6, var5);
         var1.addAlgorithm("Alg.Alias.Signature." + var7, var5);
         var1.addAlgorithm("Alg.Alias.Signature." + var9, var5);
         var1.addAlgorithm("Alg.Alias.Signature." + var10, var5);
         var1.addAlgorithm("Alg.Alias.Signature." + var11, var5);
         var1.addAlgorithm("Alg.Alias.Signature." + var8, var5);
         if (var4 != null) {
            var1.addAlgorithm("Alg.Alias.Signature." + var4, var5);
            var1.addAlgorithm("Alg.Alias.Signature.OID." + var4, var5);
         }

         var1.addAttributes("Signature." + var5, RSA.generalRsaAttributes);
      }

      private void addISO9796Signature(ConfigurableProvider var1, String var2, String var3) {
         var1.addAlgorithm("Alg.Alias.Signature." + var2 + "withRSA/ISO9796-2", var2 + "WITHRSA/ISO9796-2");
         var1.addAlgorithm("Alg.Alias.Signature." + var2 + "WithRSA/ISO9796-2", var2 + "WITHRSA/ISO9796-2");
         var1.addAlgorithm("Signature." + var2 + "WITHRSA/ISO9796-2", var3);
         var1.addAttributes("Signature." + var2 + "WITHRSA/ISO9796-2", RSA.generalRsaAttributes);
      }

      private void addPSSSignature(ConfigurableProvider var1, String var2, String var3, String var4) {
         String var5 = "WITHRSAAND" + var3;
         if (var3.equals("MGF1")) {
            var1.addAlgorithm("Alg.Alias.Signature." + var2 + "withRSA/PSS", var2 + var5);
            var1.addAlgorithm("Alg.Alias.Signature." + var2 + "WithRSA/PSS", var2 + var5);
            var1.addAlgorithm("Alg.Alias.Signature." + var2 + "WITHRSA/PSS", var2 + var5);
            var1.addAlgorithm("Alg.Alias.Signature." + var2 + "withRSASSA-PSS", var2 + var5);
            var1.addAlgorithm("Alg.Alias.Signature." + var2 + "WithRSASSA-PSS", var2 + var5);
            var1.addAlgorithm("Alg.Alias.Signature." + var2 + "WITHRSASSA-PSS", var2 + var5);
         }

         var1.addAlgorithm("Alg.Alias.Signature." + var2 + "withRSAand" + var3, var2 + var5);
         var1.addAlgorithm("Alg.Alias.Signature." + var2 + "WithRSAAnd" + var3, var2 + var5);
         var1.addAlgorithm("Signature." + var2 + "WITHRSAAND" + var3, var4);
         var1.addAttributes("Signature." + var2 + "WITHRSAAND" + var3, RSA.generalRsaAttributes);
      }

      private void addPSSSignature(ConfigurableProvider var1, String var2, String var3, ASN1ObjectIdentifier var4) {
         var1.addAlgorithm("Alg.Alias.Signature." + var2 + "withRSA/PSS", var2 + "WITHRSAPSS");
         var1.addAlgorithm("Alg.Alias.Signature." + var2 + "WithRSA/PSS", var2 + "WITHRSAPSS");
         var1.addAlgorithm("Alg.Alias.Signature." + var2 + "withRSASSA-PSS", var2 + "WITHRSAPSS");
         var1.addAlgorithm("Alg.Alias.Signature." + var2 + "WithRSASSA-PSS", var2 + "WITHRSAPSS");
         var1.addAlgorithm("Alg.Alias.Signature." + var2 + "WITHRSASSA-PSS", var2 + "WITHRSAPSS");
         var1.addAlgorithm("Signature", var4, var3);
         var1.addAlgorithm("Signature." + var2 + "WITHRSAPSS", var3);
         var1.addAttributes("Signature." + var2 + "WITHRSAPSS", RSA.generalRsaAttributes);
      }

      private void addX931Signature(ConfigurableProvider var1, String var2, String var3) {
         var1.addAlgorithm("Alg.Alias.Signature." + var2 + "withRSA/X9.31", var2 + "WITHRSA/X9.31");
         var1.addAlgorithm("Alg.Alias.Signature." + var2 + "WithRSA/X9.31", var2 + "WITHRSA/X9.31");
         var1.addAlgorithm("Signature." + var2 + "WITHRSA/X9.31", var3);
         var1.addAttributes("Signature." + var2 + "WITHRSA/X9.31", RSA.generalRsaAttributes);
      }
   }
}
