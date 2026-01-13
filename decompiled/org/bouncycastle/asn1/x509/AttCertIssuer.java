package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Choice;
import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERTaggedObject;

public class AttCertIssuer extends ASN1Object implements ASN1Choice {
   ASN1Encodable obj;
   ASN1Primitive choiceObj;

   public static AttCertIssuer getInstance(Object var0) {
      if (var0 == null || var0 instanceof AttCertIssuer) {
         return (AttCertIssuer)var0;
      } else if (var0 instanceof V2Form) {
         return new AttCertIssuer(V2Form.getInstance(var0));
      } else if (var0 instanceof GeneralNames) {
         return new AttCertIssuer((GeneralNames)var0);
      } else if (var0 instanceof ASN1TaggedObject) {
         return new AttCertIssuer(V2Form.getInstance((ASN1TaggedObject)var0, false));
      } else if (var0 instanceof ASN1Sequence) {
         return new AttCertIssuer(GeneralNames.getInstance(var0));
      } else {
         throw new IllegalArgumentException("unknown object in factory: " + var0.getClass().getName());
      }
   }

   public static AttCertIssuer getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(var0.getExplicitBaseObject());
   }

   public AttCertIssuer(GeneralNames var1) {
      this.obj = var1;
      this.choiceObj = this.obj.toASN1Primitive();
   }

   public AttCertIssuer(V2Form var1) {
      this.obj = var1;
      this.choiceObj = new DERTaggedObject(false, 0, this.obj);
   }

   public ASN1Encodable getIssuer() {
      return this.obj;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return this.choiceObj;
   }
}
