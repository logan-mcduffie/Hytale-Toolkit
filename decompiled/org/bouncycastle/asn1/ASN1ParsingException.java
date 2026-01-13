package org.bouncycastle.asn1;

public class ASN1ParsingException extends IllegalStateException {
   private Throwable cause;

   public ASN1ParsingException(String var1) {
      super(var1);
   }

   public ASN1ParsingException(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
