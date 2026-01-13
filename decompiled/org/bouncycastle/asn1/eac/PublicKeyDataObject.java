package org.bouncycastle.asn1.eac;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Sequence;

public abstract class PublicKeyDataObject extends ASN1Object {
   public static PublicKeyDataObject getInstance(Object var0) {
      if (var0 instanceof PublicKeyDataObject) {
         return (PublicKeyDataObject)var0;
      } else if (var0 != null) {
         ASN1Sequence var1 = ASN1Sequence.getInstance(var0);
         ASN1ObjectIdentifier var2 = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
         return (PublicKeyDataObject)(var2.on(EACObjectIdentifiers.id_TA_ECDSA) ? new ECDSAPublicKey(var1) : new RSAPublicKey(var1));
      } else {
         return null;
      }
   }

   public abstract ASN1ObjectIdentifier getUsage();
}
