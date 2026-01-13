package org.bouncycastle.util;

public class StreamParsingException extends Exception {
   Throwable _e;

   public StreamParsingException(String var1, Throwable var2) {
      super(var1);
      this._e = var2;
   }

   @Override
   public Throwable getCause() {
      return this._e;
   }
}
