package org.bouncycastle.asn1.bc;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;
import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.util.Arrays;

public class SignatureCheck extends ASN1Object {
   private final AlgorithmIdentifier signatureAlgorithm;
   private final ASN1Sequence certificates;
   private final ASN1BitString signatureValue;

   public SignatureCheck(AlgorithmIdentifier var1, byte[] var2) {
      this.signatureAlgorithm = var1;
      this.certificates = null;
      this.signatureValue = new DERBitString(Arrays.clone(var2));
   }

   public SignatureCheck(AlgorithmIdentifier var1, Certificate[] var2, byte[] var3) {
      this.signatureAlgorithm = var1;
      this.certificates = new DERSequence(var2);
      this.signatureValue = new DERBitString(Arrays.clone(var3));
   }

   private SignatureCheck(ASN1Sequence var1) {
      this.signatureAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
      int var2 = 1;
      if (var1.getObjectAt(1) instanceof ASN1TaggedObject) {
         this.certificates = ASN1Sequence.getInstance(ASN1TaggedObject.getInstance(var1.getObjectAt(var2++)).getBaseUniversal(true, 16));
      } else {
         this.certificates = null;
      }

      this.signatureValue = ASN1BitString.getInstance(var1.getObjectAt(var2));
   }

   public static SignatureCheck getInstance(Object var0) {
      if (var0 instanceof SignatureCheck) {
         return (SignatureCheck)var0;
      } else {
         return var0 != null ? new SignatureCheck(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1BitString getSignature() {
      return new DERBitString(this.signatureValue.getBytes(), this.signatureValue.getPadBits());
   }

   public AlgorithmIdentifier getSignatureAlgorithm() {
      return this.signatureAlgorithm;
   }

   public Certificate[] getCertificates() {
      if (this.certificates == null) {
         return null;
      } else {
         Certificate[] var1 = new Certificate[this.certificates.size()];

         for (int var2 = 0; var2 != var1.length; var2++) {
            var1[var2] = Certificate.getInstance(this.certificates.getObjectAt(var2));
         }

         return var1;
      }
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.signatureAlgorithm);
      if (this.certificates != null) {
         var1.add(new DERTaggedObject(0, this.certificates));
      }

      var1.add(this.signatureValue);
      return new DERSequence(var1);
   }
}
