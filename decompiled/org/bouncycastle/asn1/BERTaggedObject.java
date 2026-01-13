package org.bouncycastle.asn1;

import java.io.IOException;

public class BERTaggedObject extends ASN1TaggedObject {
   public BERTaggedObject(int var1, ASN1Encodable var2) {
      super(true, var1, var2);
   }

   public BERTaggedObject(int var1, int var2, ASN1Encodable var3) {
      super(true, var1, var2, var3);
   }

   public BERTaggedObject(boolean var1, int var2, ASN1Encodable var3) {
      super(var1, var2, var3);
   }

   public BERTaggedObject(boolean var1, int var2, int var3, ASN1Encodable var4) {
      super(var1, var2, var3, var4);
   }

   BERTaggedObject(int var1, int var2, int var3, ASN1Encodable var4) {
      super(var1, var2, var3, var4);
   }

   @Override
   boolean encodeConstructed() {
      return this.isExplicit() || this.obj.toASN1Primitive().encodeConstructed();
   }

   @Override
   int encodedLength(boolean var1) throws IOException {
      ASN1Primitive var2 = this.obj.toASN1Primitive();
      boolean var3 = this.isExplicit();
      int var4 = var2.encodedLength(var3);
      if (var3) {
         var4 += 3;
      }

      return var4 + (var1 ? ASN1OutputStream.getLengthOfIdentifier(this.tagNo) : 0);
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      ASN1Primitive var3 = this.obj.toASN1Primitive();
      boolean var4 = this.isExplicit();
      if (var2) {
         int var5 = this.tagClass;
         if (var4 || var3.encodeConstructed()) {
            var5 |= 32;
         }

         var1.writeIdentifier(true, var5, this.tagNo);
      }

      if (var4) {
         var1.write(128);
         var3.encode(var1, true);
         var1.write(0);
         var1.write(0);
      } else {
         var3.encode(var1, false);
      }
   }

   @Override
   ASN1Sequence rebuildConstructed(ASN1Primitive var1) {
      return new BERSequence(var1);
   }

   @Override
   ASN1TaggedObject replaceTag(int var1, int var2) {
      return new BERTaggedObject(this.explicitness, var1, var2, this.obj);
   }
}
