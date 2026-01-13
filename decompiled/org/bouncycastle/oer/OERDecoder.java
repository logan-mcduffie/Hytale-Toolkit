package org.bouncycastle.oer;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.asn1.ASN1Encodable;

public class OERDecoder {
   public static ASN1Encodable decode(byte[] var0, Element var1) throws IOException {
      return decode(new ByteArrayInputStream(var0), var1);
   }

   public static ASN1Encodable decode(InputStream var0, Element var1) throws IOException {
      OERInputStream var2 = new OERInputStream(var0);
      return var2.parse(var1);
   }
}
