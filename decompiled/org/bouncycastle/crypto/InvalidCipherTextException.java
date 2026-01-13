package org.bouncycastle.crypto;

public class InvalidCipherTextException extends CryptoException {
   public InvalidCipherTextException() {
   }

   public InvalidCipherTextException(String var1) {
      super(var1);
   }

   public InvalidCipherTextException(String var1, Throwable var2) {
      super(var1, var2);
   }
}
