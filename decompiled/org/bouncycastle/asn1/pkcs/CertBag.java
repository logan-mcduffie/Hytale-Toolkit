package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class CertBag extends ASN1Object {
   private ASN1ObjectIdentifier certId;
   private ASN1Encodable certValue;

   private CertBag(ASN1Sequence var1) {
      this.certId = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
      this.certValue = ASN1TaggedObject.getInstance(var1.getObjectAt(1)).getExplicitBaseObject();
   }

   public static CertBag getInstance(Object var0) {
      if (var0 instanceof CertBag) {
         return (CertBag)var0;
      } else {
         return var0 != null ? new CertBag(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CertBag(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.certId = var1;
      this.certValue = var2;
   }

   public ASN1ObjectIdentifier getCertId() {
      return this.certId;
   }

   public ASN1Encodable getCertValue() {
      return this.certValue;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.certId, new DERTaggedObject(0, this.certValue));
   }
}
