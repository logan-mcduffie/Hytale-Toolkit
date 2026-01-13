package org.bouncycastle.cert.jcajce;

import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

class DefaultCertHelper extends CertHelper {
   @Override
   protected CertificateFactory createCertificateFactory(String var1) throws CertificateException {
      return CertificateFactory.getInstance(var1);
   }
}
