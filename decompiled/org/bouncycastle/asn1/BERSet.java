package org.bouncycastle.asn1;

import java.io.IOException;

public class BERSet extends ASN1Set {
   public BERSet() {
   }

   public BERSet(ASN1Encodable var1) {
      super(var1);
   }

   public BERSet(ASN1EncodableVector var1) {
      super(var1, false);
   }

   public BERSet(ASN1Encodable[] var1) {
      super(var1, false);
   }

   BERSet(boolean var1, ASN1Encodable[] var2) {
      super(var1, var2);
   }

   @Override
   int encodedLength(boolean var1) throws IOException {
      int var2 = var1 ? 4 : 3;
      int var3 = 0;

      for (int var4 = this.elements.length; var3 < var4; var3++) {
         ASN1Primitive var5 = this.elements[var3].toASN1Primitive();
         var2 += var5.encodedLength(true);
      }

      return var2;
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeEncodingIL(var2, 49, this.elements);
   }
}
