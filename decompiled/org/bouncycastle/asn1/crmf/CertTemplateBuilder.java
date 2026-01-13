package org.bouncycastle.asn1.crmf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Integer;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Extensions;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.asn1.x509.X509Extensions;

public class CertTemplateBuilder {
   private ASN1Integer version;
   private ASN1Integer serialNumber;
   private AlgorithmIdentifier signingAlg;
   private X500Name issuer;
   private OptionalValidity validity;
   private X500Name subject;
   private SubjectPublicKeyInfo publicKey;
   private DERBitString issuerUID;
   private DERBitString subjectUID;
   private Extensions extensions;

   public CertTemplateBuilder setVersion(int var1) {
      this.version = new ASN1Integer(var1);
      return this;
   }

   public CertTemplateBuilder setSerialNumber(ASN1Integer var1) {
      this.serialNumber = var1;
      return this;
   }

   public CertTemplateBuilder setSigningAlg(AlgorithmIdentifier var1) {
      this.signingAlg = var1;
      return this;
   }

   public CertTemplateBuilder setIssuer(X500Name var1) {
      this.issuer = var1;
      return this;
   }

   public CertTemplateBuilder setValidity(OptionalValidity var1) {
      this.validity = var1;
      return this;
   }

   public CertTemplateBuilder setSubject(X500Name var1) {
      this.subject = var1;
      return this;
   }

   public CertTemplateBuilder setPublicKey(SubjectPublicKeyInfo var1) {
      this.publicKey = var1;
      return this;
   }

   public CertTemplateBuilder setIssuerUID(DERBitString var1) {
      this.issuerUID = var1;
      return this;
   }

   public CertTemplateBuilder setSubjectUID(DERBitString var1) {
      this.subjectUID = var1;
      return this;
   }

   /** @deprecated */
   public CertTemplateBuilder setExtensions(X509Extensions var1) {
      return this.setExtensions(Extensions.getInstance(var1));
   }

   public CertTemplateBuilder setExtensions(Extensions var1) {
      this.extensions = var1;
      return this;
   }

   public CertTemplate build() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(10);
      this.addOptional(var1, 0, false, this.version);
      this.addOptional(var1, 1, false, this.serialNumber);
      this.addOptional(var1, 2, false, this.signingAlg);
      this.addOptional(var1, 3, true, this.issuer);
      this.addOptional(var1, 4, false, this.validity);
      this.addOptional(var1, 5, true, this.subject);
      this.addOptional(var1, 6, false, this.publicKey);
      this.addOptional(var1, 7, false, this.issuerUID);
      this.addOptional(var1, 8, false, this.subjectUID);
      this.addOptional(var1, 9, false, this.extensions);
      return CertTemplate.getInstance(new DERSequence(var1));
   }

   private void addOptional(ASN1EncodableVector var1, int var2, boolean var3, ASN1Encodable var4) {
      if (var4 != null) {
         var1.add(new DERTaggedObject(var3, var2, var4));
      }
   }
}
