package org.bouncycastle.cert.jcajce;

import java.security.NoSuchProviderException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

abstract class CertHelper {
   public CertificateFactory getCertificateFactory(String var1) throws NoSuchProviderException, CertificateException {
      return this.createCertificateFactory(var1);
   }

   protected abstract CertificateFactory createCertificateFactory(String var1) throws CertificateException, NoSuchProviderException;
}
