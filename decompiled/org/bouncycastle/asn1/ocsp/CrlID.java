package org.bouncycastle.asn1.ocsp;

import java.util.Enumeration;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1IA5String;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;

public class CrlID extends ASN1Object {
   private ASN1IA5String crlUrl;
   private ASN1Integer crlNum;
   private ASN1GeneralizedTime crlTime;

   private CrlID(ASN1Sequence var1) {
      Enumeration var2 = var1.getObjects();

      while (var2.hasMoreElements()) {
         ASN1TaggedObject var3 = (ASN1TaggedObject)var2.nextElement();
         switch (var3.getTagNo()) {
            case 0:
               this.crlUrl = ASN1IA5String.getInstance(var3, true);
               break;
            case 1:
               this.crlNum = ASN1Integer.getInstance(var3, true);
               break;
            case 2:
               this.crlTime = ASN1GeneralizedTime.getInstance(var3, true);
               break;
            default:
               throw new IllegalArgumentException("unknown tag number: " + var3.getTagNo());
         }
      }
   }

   public static CrlID getInstance(Object var0) {
      if (var0 instanceof CrlID) {
         return (CrlID)var0;
      } else {
         return var0 != null ? new CrlID(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   /** @deprecated */
   public DERIA5String getCrlUrl() {
      return null != this.crlUrl && !(this.crlUrl instanceof DERIA5String) ? new DERIA5String(this.crlUrl.getString(), false) : (DERIA5String)this.crlUrl;
   }

   public ASN1IA5String getCrlUrlIA5() {
      return this.crlUrl;
   }

   public ASN1Integer getCrlNum() {
      return this.crlNum;
   }

   public ASN1GeneralizedTime getCrlTime() {
      return this.crlTime;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      if (this.crlUrl != null) {
         var1.add(new DERTaggedObject(true, 0, this.crlUrl));
      }

      if (this.crlNum != null) {
         var1.add(new DERTaggedObject(true, 1, this.crlNum));
      }

      if (this.crlTime != null) {
         var1.add(new DERTaggedObject(true, 2, this.crlTime));
      }

      return new DERSequence(var1);
   }
}
