package org.bouncycastle.jcajce.spec;

import java.security.spec.EncodedKeySpec;

public class OpenSSHPrivateKeySpec extends EncodedKeySpec {
   private final String format;

   public OpenSSHPrivateKeySpec(byte[] var1) {
      super(var1);
      if (var1[0] == 48) {
         this.format = "ASN.1";
      } else {
         if (var1[0] != 111) {
            throw new IllegalArgumentException("unknown byte encoding");
         }

         this.format = "OpenSSH";
      }
   }

   @Override
   public String getFormat() {
      return this.format;
   }
}
