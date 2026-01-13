package org.bouncycastle.asn1;

import java.io.IOException;

public class ASN1Exception extends IOException {
   private Throwable cause;

   ASN1Exception(String var1) {
      super(var1);
   }

   ASN1Exception(String var1, Throwable var2) {
      super(var1);
      this.cause = var2;
   }

   @Override
   public Throwable getCause() {
      return this.cause;
   }
}
