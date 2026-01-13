package org.bouncycastle.cert.ocsp;

public class OCSPException extends Exception {
   private Throwable cause;

   public OCSPException(String var1) {
      super(var1);
   }

   public OCSPException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
