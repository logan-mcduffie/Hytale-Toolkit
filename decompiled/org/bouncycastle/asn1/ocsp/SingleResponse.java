package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.X509Extensions;

public class SingleResponse extends ASN1Object {
   private CertID certID;
   private CertStatus certStatus;
   private ASN1GeneralizedTime thisUpdate;
   private ASN1GeneralizedTime nextUpdate;
   private Extensions singleExtensions;

   /** @deprecated */
   public SingleResponse(CertID var1, CertStatus var2, ASN1GeneralizedTime var3, ASN1GeneralizedTime var4, X509Extensions var5) {
      this(var1, var2, var3, var4, Extensions.getInstance(var5));
   }

   public SingleResponse(CertID var1, CertStatus var2, ASN1GeneralizedTime var3, ASN1GeneralizedTime var4, Extensions var5) {
      this.certID = var1;
      this.certStatus = var2;
      this.thisUpdate = var3;
      this.nextUpdate = var4;
      this.singleExtensions = var5;
   }

   private SingleResponse(ASN1Sequence var1) {
      this.certID = CertID.getInstance(var1.getObjectAt(0));
      this.certStatus = CertStatus.getInstance(var1.getObjectAt(1));
      this.thisUpdate = ASN1GeneralizedTime.getInstance(var1.getObjectAt(2));
      if (var1.size() > 4) {
         this.nextUpdate = ASN1GeneralizedTime.getInstance((ASN1TaggedObject)var1.getObjectAt(3), true);
         this.singleExtensions = Extensions.getInstance((ASN1TaggedObject)var1.getObjectAt(4), true);
      } else if (var1.size() > 3) {
         ASN1TaggedObject var2 = (ASN1TaggedObject)var1.getObjectAt(3);
         if (var2.getTagNo() == 0) {
            this.nextUpdate = ASN1GeneralizedTime.getInstance(var2, true);
         } else {
            this.singleExtensions = Extensions.getInstance(var2, true);
         }
      }
   }

   public static SingleResponse getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static SingleResponse getInstance(Object var0) {
      if (var0 instanceof SingleResponse) {
         return (SingleResponse)var0;
      } else {
         return var0 != null ? new SingleResponse(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public CertID getCertID() {
      return this.certID;
   }

   public CertStatus getCertStatus() {
      return this.certStatus;
   }

   public ASN1GeneralizedTime getThisUpdate() {
      return this.thisUpdate;
   }

   public ASN1GeneralizedTime getNextUpdate() {
      return this.nextUpdate;
   }

   public Extensions getSingleExtensions() {
      return this.singleExtensions;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(5);
      var1.add(this.certID);
      var1.add(this.certStatus);
      var1.add(this.thisUpdate);
      if (this.nextUpdate != null) {
         var1.add(new DERTaggedObject(true, 0, this.nextUpdate));
      }

      if (this.singleExtensions != null) {
         var1.add(new DERTaggedObject(true, 1, this.singleExtensions));
      }

      return new DERSequence(var1);
   }
}
