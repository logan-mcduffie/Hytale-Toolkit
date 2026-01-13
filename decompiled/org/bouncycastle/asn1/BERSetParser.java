package org.bouncycastle.asn1;

import java.io.IOException;

@Deprecated
public class BERSetParser implements ASN1SetParser {
   private ASN1StreamParser _parser;

   BERSetParser(ASN1StreamParser var1) {
      this._parser = var1;
   }

   @Override
   public ASN1Encodable readObject() throws IOException {
      return this._parser.readObject();
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
         throw new ASN1ParsingException(var2.getMessage(), var2);
      }
   }

   static BERSet parse(ASN1StreamParser var0) throws IOException {
      return new BERSet(var0.readVector());
   }
}
