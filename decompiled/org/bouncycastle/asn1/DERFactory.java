package org.bouncycastle.asn1;

class DERFactory {
   static final DERSequence EMPTY_SEQUENCE = new DERSequence();
   static final DERSet EMPTY_SET = new DERSet();

   static DERSequence createSequence(ASN1EncodableVector var0) {
      return var0.size() < 1 ? EMPTY_SEQUENCE : new DERSequence(var0);
   }

   static DERSet createSet(ASN1EncodableVector var0) {
      return var0.size() < 1 ? EMPTY_SET : new DERSet(var0);
   }
}
