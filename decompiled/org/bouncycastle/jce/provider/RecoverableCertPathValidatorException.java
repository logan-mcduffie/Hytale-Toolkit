package org.bouncycastle.jce.provider;

import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;

class RecoverableCertPathValidatorException extends CertPathValidatorException {
   public RecoverableCertPathValidatorException(String var1, Throwable var2, CertPath var3, int var4) {
      super(var1, var2, var3, var4);
   }
}
