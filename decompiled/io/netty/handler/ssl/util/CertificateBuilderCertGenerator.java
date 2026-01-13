package io.netty.handler.ssl.util;

import io.netty.pkitesting.CertificateBuilder;
import io.netty.pkitesting.X509Bundle;
import io.netty.pkitesting.CertificateBuilder.Algorithm;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Date;

final class CertificateBuilderCertGenerator {
   private CertificateBuilderCertGenerator() {
   }

   static boolean isAvailable() {
      try {
         new CertificateBuilder();
         return true;
      } catch (Throwable var1) {
         return false;
      }
   }

   static void generate(SelfSignedCertificate.Builder config) throws Exception {
      String fqdn = config.fqdn;
      Date notBefore = config.notBefore;
      Date notAfter = config.notAfter;
      String algorithm = config.algorithm;
      SecureRandom random = config.random;
      int bits = config.bits;
      CertificateBuilder builder = new CertificateBuilder();
      builder.setIsCertificateAuthority(true);
      if (fqdn.contains("=")) {
         builder.subject(fqdn);
      } else {
         builder.subject("CN=" + fqdn);
      }

      builder.notBefore(Instant.ofEpochMilli(notBefore.getTime()));
      builder.notAfter(Instant.ofEpochMilli(notAfter.getTime()));
      if (random != null) {
         builder.secureRandom(random);
      }

      if ("RSA".equals(algorithm)) {
         Algorithm alg;
         switch (bits) {
            case 2048:
               alg = Algorithm.rsa2048;
               break;
            case 3072:
               alg = Algorithm.rsa3072;
               break;
            case 4096:
               alg = Algorithm.rsa4096;
               break;
            case 8192:
               alg = Algorithm.rsa8192;
               break;
            default:
               throw new IllegalArgumentException("Unsupported RSA bit-width: " + bits);
         }

         builder.algorithm(alg);
      } else if ("EC".equals(algorithm)) {
         if (bits == 256) {
            builder.algorithm(Algorithm.ecp256);
         } else {
            if (bits != 384) {
               throw new IllegalArgumentException("Unsupported EC-P bit-width: " + bits);
            }

            builder.algorithm(Algorithm.ecp384);
         }
      }

      X509Bundle bundle = builder.buildSelfSigned();
      config.paths = SelfSignedCertificate.newSelfSignedCertificate(fqdn, bundle.getKeyPair().getPrivate(), bundle.getCertificate());
      config.keypair = bundle.getKeyPair();
      config.privateKey = bundle.getKeyPair().getPrivate();
   }
}
