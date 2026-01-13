package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;

public class CertPolicyId extends ASN1Object {
   private ASN1ObjectIdentifier id;

   private CertPolicyId(ASN1ObjectIdentifier var1) {
      this.id = var1;
   }

   public static CertPolicyId getInstance(Object var0) {
      if (var0 instanceof CertPolicyId) {
         return (CertPolicyId)var0;
      } else {
         return var0 != null ? new CertPolicyId(ASN1ObjectIdentifier.getInstance(var0)) : null;
      }
   }

   public String getId() {
      return this.id.getId();
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.id;
   }
}
