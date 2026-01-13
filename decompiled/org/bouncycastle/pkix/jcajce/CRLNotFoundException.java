package org.bouncycastle.pkix.jcajce;

import java.security.cert.CertPathValidatorException;

class CRLNotFoundException extends CertPathValidatorException {
   CRLNotFoundException(String var1) {
      super(var1);
   }

   public CRLNotFoundException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
