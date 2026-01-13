package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;

public class V1TBSCertificateGenerator {
   DERTaggedObject version = new DERTaggedObject(true, 0, new ASN1Integer(0L));
   ASN1Integer serialNumber;
   AlgorithmIdentifier signature;
   X500Name issuer;
   Validity validity;
   Time startDate;
   Time endDate;
   X500Name subject;
   SubjectPublicKeyInfo subjectPublicKeyInfo;

   public void setSerialNumber(ASN1Integer var1) {
      this.serialNumber = var1;
   }

   public void setSignature(AlgorithmIdentifier var1) {
      this.signature = var1;
   }

   /** @deprecated */
   public void setIssuer(X509Name var1) {
      this.issuer = X500Name.getInstance(var1.toASN1Primitive());
   }

   public void setIssuer(X500Name var1) {
      this.issuer = var1;
   }

   public void setValidity(Validity var1) {
      this.validity = var1;
      this.startDate = null;
      this.endDate = null;
   }

   public void setStartDate(Time var1) {
      this.validity = null;
      this.startDate = var1;
   }

   public void setStartDate(ASN1UTCTime var1) {
      this.setStartDate(new Time(var1));
   }

   public void setEndDate(Time var1) {
      this.validity = null;
      this.endDate = var1;
   }

   public void setEndDate(ASN1UTCTime var1) {
      this.setEndDate(new Time(var1));
   }

   /** @deprecated */
   public void setSubject(X509Name var1) {
      this.subject = X500Name.getInstance(var1.toASN1Primitive());
   }

   public void setSubject(X500Name var1) {
      this.subject = var1;
   }

   public void setSubjectPublicKeyInfo(SubjectPublicKeyInfo var1) {
      this.subjectPublicKeyInfo = var1;
   }

   public TBSCertificate generateTBSCertificate() {
      if (this.serialNumber != null
         && this.signature != null
         && this.issuer != null
         && (this.validity != null || this.startDate != null && this.endDate != null)
         && this.subject != null
         && this.subjectPublicKeyInfo != null) {
         return new TBSCertificate(
            new ASN1Integer(0L),
            this.serialNumber,
            this.signature,
            this.issuer,
            this.validity != null ? this.validity : new Validity(this.startDate, this.endDate),
            this.subject,
            this.subjectPublicKeyInfo,
            null,
            null,
            null
         );
      } else {
         throw new IllegalStateException("not all mandatory fields set in V1 TBScertificate generator");
      }
   }
}
