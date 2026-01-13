package io.netty.handler.ssl.util;

import java.math.BigInteger;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.Date;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.cert.X509CertificateHolder;
import org.bouncycastle.cert.X509v3CertificateBuilder;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;
import org.bouncycastle.cert.jcajce.JcaX509v3CertificateBuilder;
import org.bouncycastle.operator.ContentSigner;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;

final class BouncyCastleSelfSignedCertGenerator {
   static String[] generate(String fqdn, KeyPair keypair, SecureRandom random, Date notBefore, Date notAfter, String algorithm) throws Exception {
      PrivateKey key = keypair.getPrivate();
      X500Name owner = new X500Name("CN=" + fqdn);
      X509v3CertificateBuilder builder = new JcaX509v3CertificateBuilder(owner, new BigInteger(64, random), notBefore, notAfter, owner, keypair.getPublic());
      ContentSigner signer = new JcaContentSignerBuilder(algorithm.equalsIgnoreCase("EC") ? "SHA256withECDSA" : "SHA256WithRSAEncryption").build(key);
      X509CertificateHolder certHolder = builder.build(signer);
      X509Certificate cert = new JcaX509CertificateConverter().setProvider(BouncyCastleUtil.getBcProviderJce()).getCertificate(certHolder);
      cert.verify(keypair.getPublic());
      return SelfSignedCertificate.newSelfSignedCertificate(fqdn, key, cert);
   }

   private BouncyCastleSelfSignedCertGenerator() {
   }
}
