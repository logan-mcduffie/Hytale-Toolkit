package org.bouncycastle.cert.path;

public class CertPathValidationException extends Exception {
   private final Exception cause;

   public CertPathValidationException(String var1) {
      this(var1, null);
   }

   public CertPathValidationException(String var1, Exception var2) {
      super(var1);
      this.cause = var2;
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
