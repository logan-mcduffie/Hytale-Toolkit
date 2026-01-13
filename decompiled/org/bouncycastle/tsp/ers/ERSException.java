package org.bouncycastle.tsp.ers;

public class ERSException extends Exception {
   private final Throwable cause;

   public ERSException(String var1) {
      this(var1, null);
   }

   public ERSException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
