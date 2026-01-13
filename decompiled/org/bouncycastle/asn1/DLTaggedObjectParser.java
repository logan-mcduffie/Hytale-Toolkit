package org.bouncycastle.asn1;

import java.io.IOException;

class DLTaggedObjectParser extends BERTaggedObjectParser {
   private final boolean _constructed;

   DLTaggedObjectParser(int var1, int var2, boolean var3, ASN1StreamParser var4) {
      super(var1, var2, var4);
      this._constructed = var3;
   }

   @Override
   public ASN1Primitive getLoadedObject() throws IOException {
      return this._parser.loadTaggedDL(this._tagClass, this._tagNo, this._constructed);
   }

   @Override
   public ASN1Encodable parseBaseUniversal(boolean var1, int var2) throws IOException {
      if (var1) {
         return this.checkConstructed().parseObject(var2);
      } else {
         return this._constructed ? this._parser.parseImplicitConstructedDL(var2) : this._parser.parseImplicitPrimitive(var2);
      }
   }

   @Override
   public ASN1Encodable parseExplicitBaseObject() throws IOException {
      return this.checkConstructed().readObject();
   }

   @Override
   public ASN1TaggedObjectParser parseExplicitBaseTagged() throws IOException {
      return this.checkConstructed().parseTaggedObject();
   }

   @Override
   public ASN1TaggedObjectParser parseImplicitBaseTagged(int var1, int var2) throws IOException {
      return new DLTaggedObjectParser(var1, var2, this._constructed, this._parser);
   }

   private ASN1StreamParser checkConstructed() throws IOException {
      if (!this._constructed) {
         throw new IOException("Explicit tags must be constructed (see X.690 8.14.2)");
      } else {
         return this._parser;
      }
   }
}
