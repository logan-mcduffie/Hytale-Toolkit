package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1UTCTime;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;

public class V3TBSCertificateGenerator {
   private static final DERTaggedObject VERSION = new DERTaggedObject(true, 0, new ASN1Integer(2L));
   ASN1Integer serialNumber;
   AlgorithmIdentifier signature;
   X500Name issuer;
   Validity validity;
   Time startDate;
   Time endDate;
   X500Name subject;
   SubjectPublicKeyInfo subjectPublicKeyInfo;
   Extensions extensions;
   private boolean altNamePresentAndCritical;
   private DERBitString issuerUniqueID;
   private DERBitString subjectUniqueID;

   public void setSerialNumber(ASN1Integer var1) {
      this.serialNumber = var1;
   }

   public void setSignature(AlgorithmIdentifier var1) {
      this.signature = var1;
   }

   /** @deprecated */
   public void setIssuer(X509Name var1) {
      this.issuer = X500Name.getInstance(var1);
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

   public void setIssuerUniqueID(DERBitString var1) {
      this.issuerUniqueID = var1;
   }

   public void setSubjectUniqueID(DERBitString var1) {
      this.subjectUniqueID = var1;
   }

   public void setSubjectPublicKeyInfo(SubjectPublicKeyInfo var1) {
      this.subjectPublicKeyInfo = var1;
   }

   /** @deprecated */
   public void setExtensions(X509Extensions var1) {
      this.setExtensions(Extensions.getInstance(var1));
   }

   public void setExtensions(Extensions var1) {
      this.extensions = var1;
      if (var1 != null) {
         Extension var2 = var1.getExtension(Extension.subjectAlternativeName);
         if (var2 != null && var2.isCritical()) {
            this.altNamePresentAndCritical = true;
         }
      }
   }

   public ASN1Sequence generatePreTBSCertificate() {
      if (this.signature != null) {
         throw new IllegalStateException("signature field should not be set in PreTBSCertificate");
      } else if (this.serialNumber != null
         && this.issuer != null
         && (this.validity != null || this.startDate != null && this.endDate != null)
         && (this.subject != null || this.altNamePresentAndCritical)
         && this.subjectPublicKeyInfo != null) {
         ASN1EncodableVector var1 = new ASN1EncodableVector(9);
         var1.add(VERSION);
         var1.add(this.serialNumber);
         var1.add(this.issuer);
         var1.add(this.validity != null ? this.validity : new Validity(this.startDate, this.endDate));
         var1.add(this.subject != null ? this.subject : X500Name.getInstance(new DERSequence()));
         var1.add(this.subjectPublicKeyInfo);
         if (this.issuerUniqueID != null) {
            var1.add(new DERTaggedObject(false, 1, this.issuerUniqueID));
         }

         if (this.subjectUniqueID != null) {
            var1.add(new DERTaggedObject(false, 2, this.subjectUniqueID));
         }

         if (this.extensions != null) {
            var1.add(new DERTaggedObject(true, 3, this.extensions));
         }

         return new DERSequence(var1);
      } else {
         throw new IllegalStateException("not all mandatory fields set in V3 TBScertificate generator");
      }
   }

   public TBSCertificate generateTBSCertificate() {
      if (this.serialNumber != null
         && this.signature != null
         && this.issuer != null
         && (this.validity != null || this.startDate != null && this.endDate != null)
         && (this.subject != null || this.altNamePresentAndCritical)
         && this.subjectPublicKeyInfo != null) {
         return new TBSCertificate(
            new ASN1Integer(2L),
            this.serialNumber,
            this.signature,
            this.issuer,
            this.validity != null ? this.validity : new Validity(this.startDate, this.endDate),
            this.subject != null ? this.subject : X500Name.getInstance(new DERSequence()),
            this.subjectPublicKeyInfo,
            this.issuerUniqueID,
            this.subjectUniqueID,
            this.extensions
         );
      } else {
         throw new IllegalStateException("not all mandatory fields set in V3 TBScertificate generator");
      }
   }
}
