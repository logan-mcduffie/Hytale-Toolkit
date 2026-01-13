package org.bouncycastle.asn1.x509;

import org.bouncycastle.asn1.ASN1BitString;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.ASN1TaggedObject;
import org.bouncycastle.asn1.DERSequence;

public class SubjectAltPublicKeyInfo extends ASN1Object {
   private AlgorithmIdentifier algorithm;
   private ASN1BitString subjectAltPublicKey;

   public static SubjectAltPublicKeyInfo getInstance(ASN1TaggedObject var0, boolean var1) {
      return getInstance(ASN1Sequence.getInstance(var0, var1));
   }

   public static SubjectAltPublicKeyInfo getInstance(Object var0) {
      if (var0 instanceof SubjectAltPublicKeyInfo) {
         return (SubjectAltPublicKeyInfo)var0;
      } else {
         return var0 != null ? new SubjectAltPublicKeyInfo(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public static SubjectAltPublicKeyInfo fromExtensions(Extensions var0) {
      return getInstance(Extensions.getExtensionParsedValue(var0, Extension.subjectAltPublicKeyInfo));
   }

   private SubjectAltPublicKeyInfo(ASN1Sequence var1) {
      if (var1.size() != 2) {
         throw new IllegalArgumentException("extension should contain only 2 elements");
      } else {
         this.algorithm = AlgorithmIdentifier.getInstance(var1.getObjectAt(0));
         this.subjectAltPublicKey = ASN1BitString.getInstance(var1.getObjectAt(1));
      }
   }

   public SubjectAltPublicKeyInfo(AlgorithmIdentifier var1, ASN1BitString var2) {
      this.algorithm = var1;
      this.subjectAltPublicKey = var2;
   }

   public SubjectAltPublicKeyInfo(SubjectPublicKeyInfo var1) {
      this.algorithm = var1.getAlgorithm();
      this.subjectAltPublicKey = var1.getPublicKeyData();
   }

   public AlgorithmIdentifier getAlgorithm() {
      return this.algorithm;
   }

   public ASN1BitString getSubjectAltPublicKey() {
      return this.subjectAltPublicKey;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      return new DERSequence(this.algorithm, this.subjectAltPublicKey);
   }
}
