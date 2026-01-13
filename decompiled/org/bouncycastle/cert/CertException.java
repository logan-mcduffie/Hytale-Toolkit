package org.bouncycastle.cert;

public class CertException extends Exception {
   private Throwable cause;

   public CertException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   public CertException(String var1) {
      super(var1);
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
