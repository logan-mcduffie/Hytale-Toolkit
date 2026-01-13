package org.bouncycastle.asn1;

import java.io.IOException;

@Deprecated
public class BERSequenceParser implements ASN1SequenceParser {
   private ASN1StreamParser _parser;

   BERSequenceParser(ASN1StreamParser var1) {
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
         throw new IllegalStateException(var2.getMessage());
      }
   }

   static BERSequence parse(ASN1StreamParser var0) throws IOException {
      return new BERSequence(var0.readVector());
   }
}
