package org.bouncycastle.eac;

public class EACException extends Exception {
   private Throwable cause;

   public EACException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   public EACException(String var1) {
      super(var1);
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
