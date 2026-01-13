package org.bouncycastle.util;

public class StoreException extends RuntimeException {
   private Throwable _e;

   public StoreException(String var1, Throwable var2) {
      super(var1);
      this._e = var2;
   }

   @Override
   public Throwable getCause() {
      return this._e;
   }
}
