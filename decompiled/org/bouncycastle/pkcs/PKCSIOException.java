package org.bouncycastle.pkcs;

import java.io.IOException;

public class PKCSIOException extends IOException {
   private Throwable cause;

   public PKCSIOException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   public PKCSIOException(String var1) {
      super(var1);
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
