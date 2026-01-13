package org.bouncycastle.asn1;

final class ASN1Tag {
   private final int tagClass;
   private final int tagNumber;

   static ASN1Tag create(int var0, int var1) {
      return new ASN1Tag(var0, var1);
   }

   private ASN1Tag(int var1, int var2) {
      this.tagClass = var1;
      this.tagNumber = var2;
   }

   int getTagClass() {
      return this.tagClass;
   }

   int getTagNumber() {
      return this.tagNumber;
   }
}
