package org.bouncycastle.asn1.cms;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class OriginatorInfo extends ASN1Object {
   private ASN1Set certs;
   private ASN1Set crls;

   public OriginatorInfo(ASN1Set var1, ASN1Set var2) {
      this.certs = var1;
      this.crls = var2;
   }

   private OriginatorInfo(ASN1Sequence var1) {
      switch (var1.size()) {
         case 0:
            break;
         case 1:
            ASN1TaggedObject var2 = (ASN1TaggedObject)var1.getObjectAt(0);
            switch (var2.getTagNo()) {
               case 0:
                  this.certs = ASN1Set.getInstance(var2, false);
                  return;
               case 1:
                  this.crls = ASN1Set.getInstance(var2, false);
                  return;
               default:
                  throw new IllegalArgumentException("Bad tag in OriginatorInfo: " + var2.getTagNo());
            }
         case 2:
            this.certs = ASN1Set.getInstance((ASN1TaggedObject)var1.getObjectAt(0), false);
            this.crls = ASN1Set.getInstance((ASN1TaggedObject)var1.getObjectAt(1), false);
            break;
         default:
            throw new IllegalArgumentException("OriginatorInfo too big");
      }
   }

   public static OriginatorInfo getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static OriginatorInfo getInstance(Object var0) {
      if (var0 instanceof OriginatorInfo) {
         return (OriginatorInfo)var0;
      } else {
         return var0 != null ? new OriginatorInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1Set getCertificates() {
      return this.certs;
   }

   public ASN1Set getCRLs() {
      return this.crls;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      if (this.certs != null) {
         var1.add(new DERTaggedObject(false, 0, this.certs));
      }

      if (this.crls != null) {
         var1.add(new DERTaggedObject(false, 1, this.crls));
      }

      return new DERSequence(var1);
   }
}
