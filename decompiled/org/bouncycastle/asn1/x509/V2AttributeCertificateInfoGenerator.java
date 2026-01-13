package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1GeneralizedTime;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;

public class V2AttributeCertificateInfoGenerator {
   private ASN1Integer version = new ASN1Integer(1L);
   private Holder holder;
   private AttCertIssuer issuer;
   private AlgorithmIdentifier signature;
   private ASN1Integer serialNumber;
   private ASN1EncodableVector attributes = new ASN1EncodableVector();
   private DERBitString issuerUniqueID;
   private Extensions extensions;
   private ASN1GeneralizedTime startDate;
   private ASN1GeneralizedTime endDate;

   public void setHolder(Holder var1) {
      this.holder = var1;
   }

   public void addAttribute(String var1, ASN1Encodable var2) {
      this.attributes.add(new Attribute(new ASN1ObjectIdentifier(var1), new DERSet(var2)));
   }

   public void addAttribute(Attribute var1) {
      this.attributes.add(var1);
   }

   public void setSerialNumber(ASN1Integer var1) {
      this.serialNumber = var1;
   }

   public void setSignature(AlgorithmIdentifier var1) {
      this.signature = var1;
   }

   public void setIssuer(AttCertIssuer var1) {
      this.issuer = var1;
   }

   public void setStartDate(ASN1GeneralizedTime var1) {
      this.startDate = var1;
   }

   public void setEndDate(ASN1GeneralizedTime var1) {
      this.endDate = var1;
   }

   public void setIssuerUniqueID(DERBitString var1) {
      this.issuerUniqueID = var1;
   }

   /** @deprecated */
   public void setExtensions(X509Extensions var1) {
      this.extensions = Extensions.getInstance(var1.toASN1Primitive());
   }

   public void setExtensions(Extensions var1) {
      this.extensions = var1;
   }

   public AttributeCertificateInfo generateAttributeCertificateInfo() {
      if (this.serialNumber != null
         && this.signature != null
         && this.issuer != null
         && this.startDate != null
         && this.endDate != null
         && this.holder != null
         && this.attributes != null) {
         ASN1EncodableVector var1 = new ASN1EncodableVector(9);
         var1.add(this.version);
         var1.add(this.holder);
         var1.add(this.issuer);
         var1.add(this.signature);
         var1.add(this.serialNumber);
         AttCertValidityPeriod var2 = new AttCertValidityPeriod(this.startDate, this.endDate);
         var1.add(var2);
         var1.add(new DERSequence(this.attributes));
         if (this.issuerUniqueID != null) {
            var1.add(this.issuerUniqueID);
         }

         if (this.extensions != null) {
            var1.add(this.extensions);
         }

         return AttributeCertificateInfo.getInstance(new DERSequence(var1));
      } else {
         throw new IllegalStateException("not all mandatory fields set in V2 AttributeCertificateInfo generator");
      }
   }
}
