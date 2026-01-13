package org.bouncycastle.asn1;

import java.io.IOException;

class BERTaggedObjectParser implements ASN1TaggedObjectParser {
   final int _tagClass;
   final int _tagNo;
   final ASN1StreamParser _parser;

   BERTaggedObjectParser(int var1, int var2, ASN1StreamParser var3) {
      this._tagClass = var1;
      this._tagNo = var2;
      this._parser = var3;
   }

   @Override
   public int getTagClass() {
      return this._tagClass;
   }

   @Override
   public int getTagNo() {
      return this._tagNo;
   }

   @Override
   public boolean hasContextTag() {
      return this._tagClass == 128;
   }

   @Override
   public boolean hasContextTag(int var1) {
      return this._tagClass == 128 && this._tagNo == var1;
   }

   @Override
   public boolean hasTag(int var1, int var2) {
      return this._tagClass == var1 && this._tagNo == var2;
   }

   @Override
   public boolean hasTagClass(int var1) {
      return this._tagClass == var1;
   }

   @Override
   public ASN1Primitive getLoadedObject() throws IOException {
      return this._parser.loadTaggedIL(this._tagClass, this._tagNo);
   }

   @Override
   public ASN1Encodable parseBaseUniversal(boolean var1, int var2) throws IOException {
      return var1 ? this._parser.parseObject(var2) : this._parser.parseImplicitConstructedIL(var2);
   }

   @Override
   public ASN1Encodable parseExplicitBaseObject() throws IOException {
      return this._parser.readObject();
   }

   @Override
   public ASN1TaggedObjectParser parseExplicitBaseTagged() throws IOException {
      return this._parser.parseTaggedObject();
   }

   @Override
   public ASN1TaggedObjectParser parseImplicitBaseTagged(int var1, int var2) throws IOException {
      return new BERTaggedObjectParser(var1, var2, this._parser);
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      try {
         return this.getLoadedObject();
      } catch (IOException var2) {
         throw new ASN1ParsingException(var2.getMessage());
      }
   }
}
