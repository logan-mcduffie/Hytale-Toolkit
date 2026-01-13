package org.bouncycastle.tsp;

public class TSPValidationException extends TSPException {
   private int failureCode = -1;

   public TSPValidationException(String var1) {
      super(var1);
   }

   public TSPValidationException(String var1, int var2) {
      super(var1);
      this.failureCode = var2;
   }

   public int getFailureCode() {
      return this.failureCode;
   }
}
