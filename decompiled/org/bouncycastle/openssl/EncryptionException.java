package org.bouncycastle.openssl;

public class EncryptionException extends PEMException {
   private Throwable cause;

   public EncryptionException(String var1) {
      super(var1);
   }

   public EncryptionException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
