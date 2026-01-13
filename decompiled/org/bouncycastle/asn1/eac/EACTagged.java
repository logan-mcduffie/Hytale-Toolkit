package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.DERTaggedObject;

class EACTagged {
   static ASN1TaggedObject create(int var0, ASN1Sequence var1) {
      return new DERTaggedObject(false, 64, var0, var1);
   }

   static ASN1TaggedObject create(int var0, PublicKeyDataObject var1) {
      return new DERTaggedObject(false, 64, var0, var1);
   }

   static ASN1TaggedObject create(int var0, byte[] var1) {
      return new DERTaggedObject(false, 64, var0, new DEROctetString(var1));
   }
}
