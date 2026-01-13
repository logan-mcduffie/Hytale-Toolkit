package org.bouncycastle.jce.exception;

import java.security.cert.CertPath;
import java.security.cert.CertPathValidatorException;

public class ExtCertPathValidatorException extends CertPathValidatorException implements ExtException {
   private Throwable cause;

   public ExtCertPathValidatorException(String var1) {
      super(var1);
   }

   public ExtCertPathValidatorException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   public ExtCertPathValidatorException(String var1, Throwable var2, CertPath var3, int var4) {
      super(var1, var2, var3, var4);
      this.cause = var2;
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
