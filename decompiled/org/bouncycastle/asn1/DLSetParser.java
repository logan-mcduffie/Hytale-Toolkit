package org.bouncycastle.asn1;

import java.io.IOException;

@Deprecated
public class DLSetParser implements ASN1SetParser {
   private ASN1StreamParser _parser;

   DLSetParser(ASN1StreamParser var1) {
      this._parser = var1;
   }

   @Override
   public ASN1Encodable readObject() throws IOException {
      return this._parser.readObject();
   }

   @Override
   public ASN1Primitive getLoadedObject() throws IOException {
      return DLFactory.createSet(this._parser.readVector());
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      try {
         return this.getLoadedObject();
      } catch (IOException var2) {
         throw new ASN1ParsingException(var2.getMessage(), var2);
      }
   }
}
