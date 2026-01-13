package org.bouncycastle.asn1;

import java.io.IOException;

public class DERNull extends ASN1Null {
   public static final DERNull INSTANCE = new DERNull();
   private static final byte[] zeroBytes = new byte[0];

   private DERNull() {
   }

   @Override
   boolean encodeConstructed() {
      return false;
   }

   @Override
   int encodedLength(boolean var1) {
      return ASN1OutputStream.getLengthOfEncodingDL(var1, 0);
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeEncodingDL(var2, 5, zeroBytes);
   }
}
