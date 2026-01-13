package org.bouncycastle.operator;

public class RuntimeOperatorException extends RuntimeException {
   private Throwable cause;

   public RuntimeOperatorException(String var1) {
      super(var1);
   }

   public RuntimeOperatorException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
