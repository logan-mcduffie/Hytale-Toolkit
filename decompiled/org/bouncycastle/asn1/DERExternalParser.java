package org.bouncycastle.asn1;

import java.io.IOException;

public class DERExternalParser implements ASN1ExternalParser {
   private ASN1StreamParser _parser;

   public DERExternalParser(ASN1StreamParser var1) {
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
         throw new ASN1ParsingException("unable to get DER object", var2);
      } catch (IllegalArgumentException var3) {
         throw new ASN1ParsingException("unable to get DER object", var3);
      }
   }

   static DLExternal parse(ASN1StreamParser var0) throws IOException {
      try {
         return new DLExternal(new DLSequence(var0.readVector()));
      } catch (IllegalArgumentException var2) {
         throw new ASN1Exception(var2.getMessage(), var2);
      }
   }
}
