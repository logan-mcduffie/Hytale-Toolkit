package org.bouncycastle.openssl;

import java.io.IOException;

public class PEMException extends IOException {
   Exception underlying;

   public PEMException(String var1) {
      super(var1);
   }

   public PEMException(String var1, Exception var2) {
      super(var1);
      this.underlying = var2;
   }

   public Exception getUnderlyingException() {
      return this.underlying;
   }

   @Override
   public Throwable getCause() {
      return this.underlying;
   }
}
