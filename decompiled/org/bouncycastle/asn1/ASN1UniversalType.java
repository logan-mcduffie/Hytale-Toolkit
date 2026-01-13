package org.bouncycastle.asn1;

import java.io.IOException;

abstract class ASN1UniversalType extends ASN1Type {
   final ASN1Tag tag;

   ASN1UniversalType(Class var1, int var2) {
      super(var1);
      this.tag = ASN1Tag.create(0, var2);
   }

   final ASN1Primitive checkedCast(ASN1Primitive var1) {
      if (this.javaClass.isInstance(var1)) {
         return var1;
      } else {
         throw new IllegalStateException("unexpected object: " + var1.getClass().getName());
      }
   }

   ASN1Primitive fromImplicitPrimitive(DEROctetString var1) {
      throw new IllegalStateException("unexpected implicit primitive encoding");
   }

   ASN1Primitive fromImplicitConstructed(ASN1Sequence var1) {
      throw new IllegalStateException("unexpected implicit constructed encoding");
   }

   final ASN1Primitive fromByteArray(byte[] var1) throws IOException {
      return this.checkedCast(ASN1Primitive.fromByteArray(var1));
   }

   final ASN1Primitive getContextTagged(ASN1TaggedObject var1, boolean var2) {
      return this.checkedCast(ASN1Util.checkContextTagClass(var1).getBaseUniversal(var2, this));
   }

   final ASN1Primitive getTagged(ASN1TaggedObject var1, boolean var2) {
      return this.checkedCast(var1.getBaseUniversal(var2, this));
   }

   final ASN1Tag getTag() {
      return this.tag;
   }
}
