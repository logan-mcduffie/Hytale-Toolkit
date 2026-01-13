package org.bouncycastle.asn1;

import java.io.IOException;

public class BERSequence extends ASN1Sequence {
   public BERSequence() {
   }

   public BERSequence(ASN1Encodable var1) {
      super(var1);
   }

   public BERSequence(ASN1Encodable var1, ASN1Encodable var2) {
      super(var1, var2);
   }

   public BERSequence(ASN1EncodableVector var1) {
      super(var1);
   }

   public BERSequence(ASN1Encodable[] var1) {
      super(var1);
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
      var1.writeEncodingIL(var2, 48, this.elements);
   }

   @Override
   ASN1BitString toASN1BitString() {
      return new BERBitString(this.getConstructedBitStrings());
   }

   @Override
   ASN1External toASN1External() {
      return ((ASN1Sequence)this.toDLObject()).toASN1External();
   }

   @Override
   ASN1OctetString toASN1OctetString() {
      return new BEROctetString(this.getConstructedOctetStrings());
   }

   @Override
   ASN1Set toASN1Set() {
      return new BERSet(false, this.toArrayInternal());
   }
}
