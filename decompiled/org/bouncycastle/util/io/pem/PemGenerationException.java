package org.bouncycastle.util.io.pem;

import java.io.IOException;

public class PemGenerationException extends IOException {
   private Throwable cause;

   public PemGenerationException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   public PemGenerationException(String var1) {
      super(var1);
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
