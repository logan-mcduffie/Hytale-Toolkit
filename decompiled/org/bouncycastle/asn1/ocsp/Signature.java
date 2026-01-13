package org.bouncycastle.asn1.ocsp;

import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERBitString;
import org.bouncycastle.asn1.DERSequence;
import org.bouncycastle.asn1.DERTaggedObject;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

public class Signature extends ASN1Object {
   AlgorithmIdentifier signatureAlgorithm;
   DERBitString signature;
   ASN1Sequence certs;

   public Signature(AlgorithmIdentifier var1, DERBitString var2) {
      this.signatureAlgorithm = var1;
      this.signature = var2;
   }

   public Signature(AlgorithmIdentifier var1, DERBitString var2, ASN1Sequence var3) {
      this.signatureAlgorithm = var1;
      this.signature = var2;
      this.certs = var3;
   }

   private Signature(ASN1Sequence var1) {
      this.signatureAlgorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
      this.signature = (DERBitString)var1.getObjectAt(1);
      if (var1.size() == 3) {
         this.certs = ASN1Sequence.getInstance((ASN1TaggedObject)var1.getObjectAt(2), true);
      }
   }

   public static Signature getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static Signature getInstance(Object var0) {
      if (var0 instanceof Signature) {
         return (Signature)var0;
      } else {
         return var0 != null ? new Signature(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public AlgorithmIdentifier getSignatureAlgorithm() {
      return this.signatureAlgorithm;
   }

   public DERBitString getSignature() {
      return this.signature;
   }

   public ASN1Sequence getCerts() {
      return this.certs;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(3);
      var1.add(this.signatureAlgorithm);
      var1.add(this.signature);
      if (this.certs != null) {
         var1.add(new DERTaggedObject(true, 0, this.certs));
      }

      return new DERSequence(var1);
   }
}
