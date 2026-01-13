package org.bouncycastle.asn1;

import java.io.IOException;
import java.io.OutputStream;

class DLOutputStream extends ASN1OutputStream {
   DLOutputStream(OutputStream var1) {
      super(var1);
   }

   @Override
   DLOutputStream getDLSubStream() {
      return this;
   }

   @Override
   void writeElements(ASN1Encodable[] var1) throws IOException {
      int var2 = 0;

      for (int var3 = var1.length; var2 < var3; var2++) {
         var1[var2].toASN1Primitive().toDLObject().encode(this, true);
      }
   }

   @Override
   void writePrimitive(ASN1Primitive var1, boolean var2) throws IOException {
      var1.toDLObject().encode(this, var2);
   }

   @Override
   void writePrimitives(ASN1Primitive[] var1) throws IOException {
      int var2 = var1.length;

      for (int var3 = 0; var3 < var2; var3++) {
         var1[var3].toDLObject().encode(this, true);
      }
   }
}
