package org.bouncycastle.asn1;

import java.io.IOException;

public class DEROctetString extends ASN1OctetString {
   public DEROctetString(byte[] var1) {
      super(var1);
   }

   public DEROctetString(ASN1Encodable var1) throws IOException {
      super(var1.toASN1Primitive().getEncoded("DER"));
   }

   @Override
   boolean encodeConstructed() {
      return false;
   }

   @Override
   int encodedLength(boolean var1) {
      return ASN1OutputStream.getLengthOfEncodingDL(var1, this.string.length);
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeEncodingDL(var2, 4, this.string);
   }

   @Override
   ASN1Primitive toDERObject() {
      return this;
   }

   @Override
   ASN1Primitive toDLObject() {
      return this;
   }

   static void encode(ASN1OutputStream var0, boolean var1, byte[] var2, int var3, int var4) throws IOException {
      var0.writeEncodingDL(var1, 4, var2, var3, var4);
   }

   static int encodedLength(boolean var0, int var1) {
      return ASN1OutputStream.getLengthOfEncodingDL(var0, var1);
   }
}
