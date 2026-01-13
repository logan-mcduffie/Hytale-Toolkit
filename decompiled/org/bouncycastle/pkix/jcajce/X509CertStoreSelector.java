package org.bouncycastle.pkix.jcajce;

import java.io.IOException;
import java.security.cert.Certificate;
import java.security.cert.X509CertSelector;
import java.security.cert.X509Certificate;
import org.bouncycastle.util.Selector;

class X509CertStoreSelector extends X509CertSelector implements Selector {
   @Override
   public boolean match(Object var1) {
      if (!(var1 instanceof X509Certificate)) {
         return false;
      } else {
         X509Certificate var2 = (X509Certificate)var1;
         return super.match(var2);
      }
   }

   @Override
   public boolean match(Certificate var1) {
      return this.match((Object)var1);
   }

   @Override
   public Object clone() {
      return (X509CertStoreSelector)super.clone();
   }

   public static X509CertStoreSelector getInstance(X509CertSelector var0) {
      if (var0 == null) {
         throw new IllegalArgumentException("cannot create from null selector");
      } else {
         X509CertStoreSelector var1 = new X509CertStoreSelector();
         var1.setAuthorityKeyIdentifier(var0.getAuthorityKeyIdentifier());
         var1.setBasicConstraints(var0.getBasicConstraints());
         var1.setCertificate(var0.getCertificate());
         var1.setCertificateValid(var0.getCertificateValid());
         var1.setMatchAllSubjectAltNames(var0.getMatchAllSubjectAltNames());

         try {
            var1.setPathToNames(var0.getPathToNames());
            var1.setExtendedKeyUsage(var0.getExtendedKeyUsage());
            var1.setNameConstraints(var0.getNameConstraints());
            var1.setPolicy(var0.getPolicy());
            var1.setSubjectPublicKeyAlgID(var0.getSubjectPublicKeyAlgID());
            var1.setSubjectAlternativeNames(var0.getSubjectAlternativeNames());
         } catch (IOException var3) {
            throw new IllegalArgumentException("error in passed in selector: " + var3);
         }

         var1.setIssuer(var0.getIssuer());
         var1.setKeyUsage(var0.getKeyUsage());
         var1.setPrivateKeyValid(var0.getPrivateKeyValid());
         var1.setSerialNumber(var0.getSerialNumber());
         var1.setSubject(var0.getSubject());
         var1.setSubjectKeyIdentifier(var0.getSubjectKeyIdentifier());
         var1.setSubjectPublicKey(var0.getSubjectPublicKey());
         return var1;
      }
   }
}
