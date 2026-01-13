package org.bouncycastle.crypto.io;

import java.io.IOException;

public class CipherIOException extends IOException {
   private static final long serialVersionUID = 1L;
   private final Throwable cause;

   public CipherIOException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
