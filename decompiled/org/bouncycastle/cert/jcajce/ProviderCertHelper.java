package org.bouncycastle.cert.jcajce;

import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

class ProviderCertHelper extends CertHelper {
   private final Provider provider;

   ProviderCertHelper(Provider var1) {
      this.provider = var1;
   }

   @Override
   protected CertificateFactory createCertificateFactory(String var1) throws CertificateException {
      return CertificateFactory.getInstance(var1, this.provider);
   }
}
