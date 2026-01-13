package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class AttributeCertificate extends ASN1Object {
   AttributeCertificateInfo acinfo;
   AlgorithmIdentifier signatureAlgorithm;
   ASN1BitString signatureValue;

   public static AttributeCertificate getInstance(Object var0) {
      if (var0 instanceof AttributeCertificate) {
         return (AttributeCertificate)var0;
      } else {
         return var0 != null ? new AttributeCertificate(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AttributeCertificate(AttributeCertificateInfo var1, AlgorithmIdentifier var2, ASN1BitString var3) {
      this.acinfo = var1;
      this.signatureAlgorithm = var2;
      this.signatureValue = var3;
   }

   private AttributeCertificate(ASN1Sequence var1) {
      if (var1.size() != 3) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         this.acinfo = AttributeCertificateInfo.getInstance(var1.getObjectAt(0));
         this.signatureAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(1));
         this.signatureValue = ASN1BitString.getInstance(var1.getObjectAt(2));
      }
   }

   public AttributeCertificateInfo getAcinfo() {
      return this.acinfo;
   }

   public AlgorithmIdentifier getSignatureAlgorithm() {
      return this.signatureAlgorithm;
   }

   public ASN1BitString getSignatureValue() {
      return this.signatureValue;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.acinfo);
      var1.add(this.signatureAlgorithm);
      var1.add(this.signatureValue);
      return new DERSequence(var1);
   }
}
