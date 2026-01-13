package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1OctetString;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class OtherHashAlgAndValue extends ASN1Object {
   private AlgorithmIdentifier hashAlgorithm;
   private ASN1OctetString hashValue;

   public static OtherHashAlgAndValue getInstance(Object var0) {
      if (var0 instanceof OtherHashAlgAndValue) {
         return (OtherHashAlgAndValue)var0;
      } else {
         return var0 != null ? new OtherHashAlgAndValue(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   private OtherHashAlgAndValue(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("Bad sequence size: " + var1.size());
      } else {
         this.hashAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
         this.hashValue = ASN1OctetString.getInstance(var1.getObjectAt(1));
      }
   }

   public OtherHashAlgAndValue(AlgorithmIdentifier var1, ASN1OctetString var2) {
      this.hashAlgorithm = var1;
      this.hashValue = var2;
   }

   public AlgorithmIdentifier getHashAlgorithm() {
      return this.hashAlgorithm;
   }

   public ASN1OctetString getHashValue() {
      return this.hashValue;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.hashAlgorithm);
      var1.add(this.hashValue);
      return new DERSequence(var1);
   }
}
