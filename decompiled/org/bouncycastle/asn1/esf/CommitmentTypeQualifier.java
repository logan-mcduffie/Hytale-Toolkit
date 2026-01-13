package org.bouncycastle.asn1.esf;

import org.bouncycastle.asn1.ASN1Encodable;
import org.bouncycastle.asn1.ASN1EncodableVector;
import org.bouncycastle.asn1.ASN1Object;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.ASN1Primitive;
import org.bouncycastle.asn1.ASN1Sequence;
import org.bouncycastle.asn1.DERSequence;

public class CommitmentTypeQualifier extends ASN1Object {
   private ASN1ObjectIdentifier commitmentTypeIdentifier;
   private ASN1Encodable qualifier;

   public CommitmentTypeQualifier(ASN1ObjectIdentifier var1) {
      this(var1, null);
   }

   public CommitmentTypeQualifier(ASN1ObjectIdentifier var1, ASN1Encodable var2) {
      this.commitmentTypeIdentifier = var1;
      this.qualifier = var2;
   }

   private CommitmentTypeQualifier(ASN1Sequence var1) {
      this.commitmentTypeIdentifier = (ASN1ObjectIdentifier)var1.getObjectAt(0);
      if (var1.size() > 1) {
         this.qualifier = var1.getObjectAt(1);
      }
   }

   public static CommitmentTypeQualifier getInstance(Object var0) {
      if (var0 instanceof CommitmentTypeQualifier) {
         return (CommitmentTypeQualifier)var0;
      } else {
         return var0 != null ? new CommitmentTypeQualifier(ASN1Sequence.getInstance(var0)) : null;
      }
   }

   public ASN1ObjectIdentifier getCommitmentTypeIdentifier() {
      return this.commitmentTypeIdentifier;
   }

   public ASN1Encodable getQualifier() {
      return this.qualifier;
   }

   @Override
   public ASN1Primitive toASN1Primitive() {
      ASN1EncodableVector var1 = new ASN1EncodableVector(2);
      var1.add(this.commitmentTypeIdentifier);
      if (this.qualifier != null) {
         var1.add(this.qualifier);
      }

      return new DERSequence(var1);
   }
}
