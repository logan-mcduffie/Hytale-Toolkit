package org.bouncycastle.asn1;

class DLFactory {
   static final DLSequence EMPTY_SEQUENCE = new DLSequence();
   static final DLSet EMPTY_SET = new DLSet();

   static DLSequence createSequence(ASN1EncodableVector var0) {
      return var0.size() < 1 ? EMPTY_SEQUENCE : new DLSequence(var0);
   }

   static DLSet createSet(ASN1EncodableVector var0) {
      return var0.size() < 1 ? EMPTY_SET : new DLSet(var0);
   }
}
