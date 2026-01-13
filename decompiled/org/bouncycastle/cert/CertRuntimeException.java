package org.bouncycastle.cert;

public class CertRuntimeException extends RuntimeException {
   private Throwable cause;

   public CertRuntimeException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
