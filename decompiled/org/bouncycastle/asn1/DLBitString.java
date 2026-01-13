package org.bouncycastle.asn1;

import java.io.IOException;

public class DLBitString extends ASN1BitString {
   public DLBitString(byte[] var1) {
      this(var1, 0);
   }

   public DLBitString(byte var1, int var2) {
      super(var1, var2);
   }

   public DLBitString(byte[] var1, int var2) {
      super(var1, var2);
   }

   public DLBitString(int var1) {
      super(getBytes(var1), getPadBits(var1));
   }

   public DLBitString(ASN1Encodable var1) throws IOException {
      super(var1.toASN1Primitive().getEncoded("DER"), 0);
   }

   DLBitString(byte[] var1, boolean var2) {
      super(var1, var2);
   }

   @Override
   boolean encodeConstructed() {
      return false;
   }

   @Override
   int encodedLength(boolean var1) {
      return ASN1OutputStream.getLengthOfEncodingDL(var1, this.contents.length);
   }

   @Override
   void encode(ASN1OutputStream var1, boolean var2) throws IOException {
      var1.writeEncodingDL(var2, 3, this.contents);
   }

   @Override
   ASN1Primitive toDLObject() {
      return this;
   }

   static int encodedLength(boolean var0, int var1) {
      return ASN1OutputStream.getLengthOfEncodingDL(var0, var1);
   }

   static void encode(ASN1OutputStream var0, boolean var1, byte[] var2, int var3, int var4) throws IOException {
      var0.writeEncodingDL(var1, 3, var2, var3, var4);
   }

   static void encode(ASN1OutputStream var0, boolean var1, byte var2, byte[] var3, int var4, int var5) throws IOException {
      var0.writeEncodingDL(var1, 3, var2, var3, var4, var5);
   }
}
