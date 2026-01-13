package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.InputStream;
import org.bouncycastle.util.io.Streams;

@Deprecated
public class BEROctetStringParser implements ASN1OctetStringParser {
   private ASN1StreamParser _parser;

   BEROctetStringParser(ASN1StreamParser var1) {
      this._parser = var1;
   }

   @Override
   public InputStream getOctetStream() {
      return new ConstructedOctetStream(this._parser);
   }

   @Override
   public ASN1Primitive getLoadedObject() throws IOException {
      return parse(this._parser);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      try {
         return this.getLoadedObject();
      } catch (IOException var2) {
         throw new ASN1ParsingException("IOException converting stream to byte array: " + var2.getMessage(), var2);
      }
   }

   static BEROctetString parse(ASN1StreamParser var0) throws IOException {
      return new BEROctetString(Streams.readAll(new ConstructedOctetStream(var0)));
   }
}
