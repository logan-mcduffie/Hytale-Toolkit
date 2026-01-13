package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class DVCSCertInfoBuilder {
   private int version = 1;
   private DVCSRequestInformation dvReqInfo;
   private DigestInfo messageImprint;
   private ASN1Integer serialNumber;
   private DVCSTime responseTime;
   private PKIStatusInfo dvStatus;
   private PolicyInformation policy;
   private ASN1Set reqSignature;
   private ASN1Sequence certs;
   private Extensions extensions;
   private static final int DEFAULT_VERSION = 1;
   private static final int TAG_DV_STATUS = 0;
   private static final int TAG_POLICY = 1;
   private static final int TAG_REQ_SIGNATURE = 2;
   private static final int TAG_CERTS = 3;

   public DVCSCertInfoBuilder(DVCSRequestInformation var1, DigestInfo var2, ASN1Integer var3, DVCSTime var4) {
      this.dvReqInfo = var1;
      this.messageImprint = var2;
      this.serialNumber = var3;
      this.responseTime = var4;
   }

   public DVCSCertInfo build() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(10);
      if (this.version != 1) {
         var1.add(new ASN1Integer(this.version));
      }

      var1.add(this.dvReqInfo);
      var1.add(this.messageImprint);
      var1.add(this.serialNumber);
      var1.add(this.responseTime);
      if (this.dvStatus != null) {
         var1.add(new DERTaggedObject(false, 0, this.dvStatus));
      }

      if (this.policy != null) {
         var1.add(new DERTaggedObject(false, 1, this.policy));
      }

      if (this.reqSignature != null) {
         var1.add(new DERTaggedObject(false, 2, this.reqSignature));
      }

      if (this.certs != null) {
         var1.add(new DERTaggedObject(false, 3, this.certs));
      }

      if (this.extensions != null) {
         var1.add(this.extensions);
      }

      return DVCSCertInfo.getInstance(new DERSequence(var1));
   }

   public void setVersion(int var1) {
      this.version = var1;
   }

   public void setDvReqInfo(DVCSRequestInformation var1) {
      this.dvReqInfo = var1;
   }

   public void setMessageImprint(DigestInfo var1) {
      this.messageImprint = var1;
   }

   public void setSerialNumber(ASN1Integer var1) {
      this.serialNumber = var1;
   }

   public void setResponseTime(DVCSTime var1) {
      this.responseTime = var1;
   }

   public void setDvStatus(PKIStatusInfo var1) {
      this.dvStatus = var1;
   }

   public void setPolicy(PolicyInformation var1) {
      this.policy = var1;
   }

   public void setReqSignature(ASN1Set var1) {
      this.reqSignature = var1;
   }

   public void setCerts(TargetEtcChain[] var1) {
      this.certs = new DERSequence(var1);
   }

   public void setExtensions(Extensions var1) {
      this.extensions = var1;
   }
}
