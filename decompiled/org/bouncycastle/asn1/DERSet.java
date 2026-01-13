package org.bouncycastle.asn1;

import java.io.IOException;

public class DERSet extends ASN1Set {
   private int contentsLength = -1;

   public static DERSet convert(ASN1Set var0) {
      return (DERSet)var0.toDERObject();
   }

   public DERSet() {
   }

   public DERSet(ASN1Encodable var1) {
      super(var1);
   }

   public DERSet(ASN1EncodableVector var1) {
      super(var1, true);
   }

   public DERSet(ASN1Encodable[] var1) {
      super(var1, true);
   }

   DERSet(boolean var1, ASN1Encodable[] var2) {
      super(checkSorted(var1), var2);
   }

   private int getContentsLength() throws IOException {
      if (this.contentsLength < 0) {
         int var1 = this.elements.length;
         int var2 = 0;

         for (int var3 = 0; var3 < var1; var3++) {
            ASN1Primitive var4 = this.elements[var3].toASN1Primitive().toDERObject();
            var2 += var4.encodedLength(true);
         }

         this.contentsLength = var2;
      }

      return this.contentsLength;
   }

   @Override
   int encodedLength(boolean var1) throws IOException {
      return ASN1OutputStream.getLengthOfEncodingDL(var1, this.getContentsLength());
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeIdentifier(var2, 49);
      DEROutputStream var3 = var1.getDERSubStream();
      int var4 = this.elements.length;
      if (this.contentsLength < 0 && var4 <= 16) {
         int var9 = 0;
         ASN1Primitive[] var10 = new ASN1Primitive[var4];

         for (int var7 = 0; var7 < var4; var7++) {
            ASN1Primitive var8 = this.elements[var7].toASN1Primitive().toDERObject();
            var10[var7] = var8;
            var9 += var8.encodedLength(true);
         }

         this.contentsLength = var9;
         var1.writeDL(var9);

         for (int var11 = 0; var11 < var4; var11++) {
            var10[var11].encode(var3, true);
         }
      } else {
         var1.writeDL(this.getContentsLength());

         for (int var5 = 0; var5 < var4; var5++) {
            ASN1Primitive var6 = this.elements[var5].toASN1Primitive().toDERObject();
            var6.encode(var3, true);
         }
      }
   }

   @Override
   ASN1Primitive toDERObject() {
      return (ASN1Primitive)(this.sortedElements != null ? this : super.toDERObject());
   }

   @Override
   ASN1Primitive toDLObject() {
      return this;
   }

   private static boolean checkSorted(boolean var0) {
      if (!var0) {
         throw new IllegalStateException("DERSet elements should always be in sorted order");
      } else {
         return var0;
      }
   }
}
