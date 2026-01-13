package org.bouncycastle.pkcs;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERSet;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.bouncycastle.asn1.x500.X500Name;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class DeltaCertificateRequestAttributeValueBuilder {
   private final SubjectPublicKeyInfo subjectPublicKey;
   private AlgorithmIdentifier signatureAlgorithm;
   private X500Name subject;

   public DeltaCertificateRequestAttributeValueBuilder(SubjectPublicKeyInfo var1) {
      this.subjectPublicKey = var1;
   }

   public DeltaCertificateRequestAttributeValueBuilder setSignatureAlgorithm(AlgorithmIdentifier var1) {
      this.signatureAlgorithm = var1;
      return this;
   }

   public DeltaCertificateRequestAttributeValueBuilder setSubject(X500Name var1) {
      this.subject = var1;
      return this;
   }

   public DeltaCertificateRequestAttributeValue build() {
      ASN1EncodableVector var1 = new ASN1EncodableVector();
      if (this.subject != null) {
         var1.add(new DERTaggedObject(true, 0, this.subject));
      }

      var1.add(this.subjectPublicKey);
      if (this.signatureAlgorithm != null) {
         var1.add(new DERTaggedObject(true, 2, this.signatureAlgorithm));
      }

      return new DeltaCertificateRequestAttributeValue(new Attribute(new ASN1ObjectIdentifier("2.16.840.1.114027.80.6.2"), new DERSet(new DERSequence(var1))));
   }
}
