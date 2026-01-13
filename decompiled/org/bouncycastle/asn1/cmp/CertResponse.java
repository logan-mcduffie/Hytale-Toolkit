package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CertResponse extends ASN1Object {
   private final ASN1Integer certReqId;
   private final PKIStatusInfo status;
   private CertifiedKeyPair certifiedKeyPair;
   private ASN1OctetString rspInfo;

   private CertResponse(ASN1Sequence var1) {
      this.certReqId = ASN1Integer.getInstance(var1.getObjectAt(0));
      this.status = PKIStatusInfo.getInstance(var1.getObjectAt(1));
      if (var1.size() >= 3) {
         if (var1.size() == 3) {
            ASN1Encodable var2 = var1.getObjectAt(2);
            if (var2 instanceof ASN1OctetString) {
               this.rspInfo = ASN1OctetString.getInstance(var2);
            } else {
               this.certifiedKeyPair = CertifiedKeyPair.getInstance(var2);
            }
         } else {
            this.certifiedKeyPair = CertifiedKeyPair.getInstance(var1.getObjectAt(2));
            this.rspInfo = ASN1OctetString.getInstance(var1.getObjectAt(3));
         }
      }
   }

   public CertResponse(ASN1Integer var1, PKIStatusInfo var2) {
      this(var1, var2, null, null);
   }

   public CertResponse(ASN1Integer var1, PKIStatusInfo var2, CertifiedKeyPair var3, ASN1OctetString var4) {
      if (var1 == null) {
         throw new IllegalArgumentException("'certReqId' cannot be null");
      } else if (var2 == null) {
         throw new IllegalArgumentException("'status' cannot be null");
      } else {
         this.certReqId = var1;
         this.status = var2;
         this.certifiedKeyPair = var3;
         this.rspInfo = var4;
      }
   }

   public static CertResponse getInstance(Object var0) {
      if (var0 instanceof CertResponse) {
         return (CertResponse)var0;
      } else {
         return var0 != null ? new CertResponse(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1Integer getCertReqId() {
      return this.certReqId;
   }

   public PKIStatusInfo getStatus() {
      return this.status;
   }

   public CertifiedKeyPair getCertifiedKeyPair() {
      return this.certifiedKeyPair;
   }

   public ASN1OctetString getRspInfo() {
      return this.rspInfo;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(4);
      var1.add(this.certReqId);
      var1.add(this.status);
      if (this.certifiedKeyPair != null) {
         var1.add(this.certifiedKeyPair);
      }

      if (this.rspInfo != null) {
         var1.add(this.rspInfo);
      }

      return new DERSequence(var1);
   }
}
