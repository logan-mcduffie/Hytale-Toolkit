package org.bouncycastle.asn1.pkcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class CRLBag extends ASN1Object {
   private ASN1ObjectIdentifier crlId;
   private ASN1Encodable crlValue;

   private CRLBag(ASN1Sequence var1) {
      this.crlId = ASN1ObjectIdentifier.getInstance(var1.getObjectAt(0));
      this.crlValue = ASN1TaggedObject.getInstance(var1.getObjectAt(1)).getExplicitBaseObject();
   }

   public static CRLBag getInstance(Object var0) {
      if (var0 instanceof CRLBag) {
         return (CRLBag)var0;
      } else {
         return var0 != null ? new CRLBag(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CRLBag(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.crlId = var1;
      this.crlValue = var2;
   }

   public ASN1ObjectIdentifier getCrlId() {
      return this.crlId;
   }

   public ASN1Encodable getCrlValue() {
      return this.crlValue;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.crlId, new DERTaggedObject(0, this.crlValue));
   }
}
