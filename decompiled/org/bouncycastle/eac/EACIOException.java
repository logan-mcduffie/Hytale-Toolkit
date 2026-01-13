package org.bouncycastle.eac;

import java.io.IOException;

public class EACIOException extends IOException {
   private Throwable cause;

   public EACIOException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   public EACIOException(String var1) {
      super(var1);
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
