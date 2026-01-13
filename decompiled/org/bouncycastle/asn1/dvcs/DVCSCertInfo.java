package org.bouncycastle.asn1.dvcs;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1Set;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.cmp.PKIStatusInfo;
import org.bouncycastle.asn1.x509.DigestInfo;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.PolicyInformation;

public class DVCSCertInfo extends ASN1Object {
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

   public DVCSCertInfo(DVCSRequestInformation var1, DigestInfo var2, ASN1Integer var3, DVCSTime var4) {
      this.dvReqInfo = var1;
      this.messageImprint = var2;
      this.serialNumber = var3;
      this.responseTime = var4;
   }

   private DVCSCertInfo(ASN1Sequence var1) {
      int var2 = 0;
      ASN1Encodable var3 = var1.getObjectAt(var2++);

      try {
         ASN1Integer var4 = ASN1Integer.getInstance(var3);
         this.version = var4.intValueExact();
         var3 = var1.getObjectAt(var2++);
      } catch (IllegalArgumentException var7) {
      }

      this.dvReqInfo = DVCSRequestInformation.getInstance(var3);
      var3 = var1.getObjectAt(var2++);
      this.messageImprint = DigestInfo.getInstance(var3);
      var3 = var1.getObjectAt(var2++);
      this.serialNumber = ASN1Integer.getInstance(var3);
      var3 = var1.getObjectAt(var2++);
      this.responseTime = DVCSTime.getInstance(var3);

      while (var2 < var1.size()) {
         var3 = var1.getObjectAt(var2++);
         if (var3 instanceof ASN1TaggedObject) {
            ASN1TaggedObject var16 = ASN1TaggedObject.getInstance(var3);
            int var5 = var16.getTagNo();
            switch (var5) {
               case 0:
                  this.dvStatus = PKIStatusInfo.getInstance(var16, false);
                  break;
               case 1:
                  this.policy = PolicyInformation.getInstance(ASN1Sequence.getInstance(var16, false));
                  break;
               case 2:
                  this.reqSignature = ASN1Set.getInstance(var16, false);
                  break;
               case 3:
                  this.certs = ASN1Sequence.getInstance(var16, false);
                  break;
               default:
                  throw new IllegalArgumentException("Unknown tag encountered: " + var5);
            }
         } else {
            try {
               this.extensions = Extensions.getInstance(var3);
            } catch (IllegalArgumentException var6) {
            }
         }
      }
   }

   public static DVCSCertInfo getInstance(Object var0) {
      if (var0 instanceof DVCSCertInfo) {
         return (DVCSCertInfo)var0;
      } else {
         return var0 != null ? new DVCSCertInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static DVCSCertInfo getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
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

      return new DERSequence(var1);
   }

   @Override
   public String toString() {
      StringBuilder var1 = new StringBuilder();
      var1.append("DVCSCertInfo {\n");
      if (this.version != 1) {
         var1.append("version: " + this.version + "\n");
      }

      var1.append("dvReqInfo: " + this.dvReqInfo + "\n");
      var1.append("messageImprint: " + this.messageImprint + "\n");
      var1.append("serialNumber: " + this.serialNumber + "\n");
      var1.append("responseTime: " + this.responseTime + "\n");
      if (this.dvStatus != null) {
         var1.append("dvStatus: " + this.dvStatus + "\n");
      }

      if (this.policy != null) {
         var1.append("policy: " + this.policy + "\n");
      }

      if (this.reqSignature != null) {
         var1.append("reqSignature: " + this.reqSignature + "\n");
      }

      if (this.certs != null) {
         var1.append("certs: " + this.certs + "\n");
      }

      if (this.extensions != null) {
         var1.append("extensions: " + this.extensions + "\n");
      }

      var1.append("}\n");
      return var1.toString();
   }

   public int getVersion() {
      return this.version;
   }

   private void setVersion(int var1) {
      this.version = var1;
   }

   public DVCSRequestInformation getDvReqInfo() {
      return this.dvReqInfo;
   }

   private void setDvReqInfo(DVCSRequestInformation var1) {
      this.dvReqInfo = var1;
   }

   public DigestInfo getMessageImprint() {
      return this.messageImprint;
   }

   private void setMessageImprint(DigestInfo var1) {
      this.messageImprint = var1;
   }

   public ASN1Integer getSerialNumber() {
      return this.serialNumber;
   }

   public DVCSTime getResponseTime() {
      return this.responseTime;
   }

   public PKIStatusInfo getDvStatus() {
      return this.dvStatus;
   }

   public PolicyInformation getPolicy() {
      return this.policy;
   }

   public ASN1Set getReqSignature() {
      return this.reqSignature;
   }

   public TargetEtcChain[] getCerts() {
      return this.certs != null ? TargetEtcChain.arrayFromSequence(this.certs) : null;
   }

   public Extensions getExtensions() {
      return this.extensions;
   }
}
