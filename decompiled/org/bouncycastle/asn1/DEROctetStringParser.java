package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;

@Deprecated
public class DEROctetStringParser implements ASN1OctetStringParser {
   private DefiniteLengthInputStream stream;

   DEROctetStringParser(DefiniteLengthInputStream var1) {
      this.stream = var1;
   }

   @Override
   public InputStream getOctetStream() {
      return this.stream;
   }

   @Override
   public ASN1Primitive getLoadedObject() throws IOException {
      return new DEROctetString(this.stream.toByteArray());
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      try {
         return this.getLoadedObject();
      } catch (IOException var2) {
         throw new ASN1ParsingException("IOException converting stream to byte array: " + var2.getMessage(), var2);
      }
   }
}
