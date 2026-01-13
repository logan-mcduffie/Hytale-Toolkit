package org.bouncycastle.asn1.cmp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.crmf.CertId;
import org.bouncycastle.asn1.x509.Extensions;

public class RevAnnContent extends ASN1Object {
   private final PKIStatus status;
   private final CertId certId;
   private final ASN1GeneralizedTime willBeRevokedAt;
   private final ASN1GeneralizedTime badSinceDate;
   private Extensions crlDetails;

   public RevAnnContent(PKIStatus var1, CertId var2, ASN1GeneralizedTime var3, ASN1GeneralizedTime var4) {
      this(var1, var2, var3, var4, null);
   }

   public RevAnnContent(PKIStatus var1, CertId var2, ASN1GeneralizedTime var3, ASN1GeneralizedTime var4, Extensions var5) {
      this.status = var1;
      this.certId = var2;
      this.willBeRevokedAt = var3;
      this.badSinceDate = var4;
      this.crlDetails = var5;
   }

   private RevAnnContent(ASN1Sequence var1) {
      this.status = PKIStatus.getInstance(var1.getObjectAt(0));
      this.certId = CertId.getInstance(var1.getObjectAt(1));
      this.willBeRevokedAt = ASN1GeneralizedTime.getInstance(var1.getObjectAt(2));
      this.badSinceDate = ASN1GeneralizedTime.getInstance(var1.getObjectAt(3));
      if (var1.size() > 4) {
         this.crlDetails = Extensions.getInstance(var1.getObjectAt(4));
      }
   }

   public static RevAnnContent getInstance(Object var0) {
      if (var0 instanceof RevAnnContent) {
         return (RevAnnContent)var0;
      } else {
         return var0 != null ? new RevAnnContent(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public PKIStatus getStatus() {
      return this.status;
   }

   public CertId getCertId() {
      return this.certId;
   }

   public ASN1GeneralizedTime getWillBeRevokedAt() {
      return this.willBeRevokedAt;
   }

   public ASN1GeneralizedTime getBadSinceDate() {
      return this.badSinceDate;
   }

   public Extensions getCrlDetails() {
      return this.crlDetails;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(5);
      var1.add(this.status);
      var1.add(this.certId);
      var1.add(this.willBeRevokedAt);
      var1.add(this.badSinceDate);
      if (this.crlDetails != null) {
         var1.add(this.crlDetails);
      }

      return new DERSequence(var1);
   }
}
