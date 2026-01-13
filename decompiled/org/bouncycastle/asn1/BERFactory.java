package org.bouncycastle.asn1;

class BERFactory {
   static final BERSequence EMPTY_SEQUENCE = new BERSequence();
   static final BERSet EMPTY_SET = new BERSet();

   static BERSequence createSequence(ASN1EncodableVector var0) {
      return var0.size() < 1 ? EMPTY_SEQUENCE : new BERSequence(var0);
   }

   static BERSet createSet(ASN1EncodableVector var0) {
      return var0.size() < 1 ? EMPTY_SET : new BERSet(var0);
   }
}
