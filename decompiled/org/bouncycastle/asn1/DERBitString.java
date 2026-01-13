package org.bouncycastle.asn1;

import java.io.IOException;

public class DERBitString extends ASN1BitString {
   public static DERBitString convert(ASN1BitString var0) {
      return (DERBitString)var0.toDERObject();
   }

   public DERBitString(byte[] var1) {
      this(var1, 0);
   }

   public DERBitString(byte var1, int var2) {
      super(var1, var2);
   }

   public DERBitString(byte[] var1, int var2) {
      super(var1, var2);
   }

   public DERBitString(int var1) {
      super(getBytes(var1), getPadBits(var1));
   }

   public DERBitString(ASN1Encodable var1) throws IOException {
      super(var1.toASN1Primitive().getEncoded("DER"), 0);
   }

   DERBitString(byte[] var1, boolean var2) {
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
      int var3 = this.contents[0] & 255;
      int var4 = this.contents.length;
      int var5 = var4 - 1;
      byte var6 = this.contents[var5];
      byte var7 = (byte)(this.contents[var5] & 255 << var3);
      if (var6 == var7) {
         var1.writeEncodingDL(var2, 3, this.contents);
      } else {
         var1.writeEncodingDL(var2, 3, this.contents, 0, var5, var7);
      }
   }

   @Override
   ASN1Primitive toDERObject() {
      return this;
   }

   @Override
   ASN1Primitive toDLObject() {
      return this;
   }

   static DERBitString fromOctetString(ASN1OctetString var0) {
      return new DERBitString(var0.getOctets(), true);
   }
}
