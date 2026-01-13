package org.bouncycastle.mime;

import java.io.IOException;

public class MimeIOException extends IOException {
   private Throwable cause;

   public MimeIOException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   public MimeIOException(String var1) {
      super(var1);
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
