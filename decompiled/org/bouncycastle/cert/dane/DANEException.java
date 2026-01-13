package org.bouncycastle.cert.dane;

public class DANEException extends Exception {
   private Throwable cause;

   public DANEException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   public DANEException(String var1) {
      super(var1);
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
