package org.bouncycastle.operator;

import java.io.IOException;

public class OperatorStreamException extends IOException {
   private Throwable cause;

   public OperatorStreamException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
