package org.bouncycastle.tsp;

import java.io.IOException;

public class TSPIOException extends IOException {
   Throwable underlyingException;

   public TSPIOException(String var1) {
      super(var1);
   }

   public TSPIOException(String var1, Throwable var2) {
      super(var1);
      this.underlyingException = var2;
   }

   public Exception getUnderlyingException() {
      return (Exception)this.underlyingException;
   }

   @Override
   public Throwable getCause() {
      return this.underlyingException;
   }
}
